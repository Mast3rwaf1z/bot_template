package win.skademaskinen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class CommandListener extends ListenerAdapter {
    private HashMap<Guild, MusicBot> bots = new HashMap<>();
    Runtime runtime = Runtime.getRuntime();

    public CommandListener() throws ClassNotFoundException, SQLException, IOException, ParseException{

    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        System.out.println("Command:                " + event.getCommandString());
        System.out.println();
        //event.deferReply().queue();
        Guild guild = event.getGuild();
        Member author = event.getMember();
        EmbedBuilder builder = new EmbedBuilder();
        switch (event.getName().toLowerCase()) {
            case "ping":
                event.reply("Pong").queue();
                break;

            //music
            case "play":
                if (author.getVoiceState().inAudioChannel() && bots.containsKey(guild)) {
                    MusicBot bot = bots.get(guild);
                    if(!guild.getSelfMember().getVoiceState().inAudioChannel()){
                        bot.connectToVoiceChannel(author.getVoiceState().getChannel());
                    }
                    bot.play(event.getOption("url").getAsString().strip(), event);
                }
                else if(event.getOptions().size() == 0){
                    event.reply("Please specify a track").setEphemeral(true).queue();
                }
                else{
                    bots.put(guild, new MusicBot(event.getMember().getVoiceState().getChannel(), event));
                    MusicBot bot = bots.get(guild);
                    bot.play(event.getOption("url").getAsString().strip(), event);
                }
                break;
            case "skip":
                if(author.getVoiceState().inAudioChannel() && bots.containsKey(guild)){
                    bots.get(guild).skip(event);
                }
                break;
            case "queue":
                if(author.getVoiceState().inAudioChannel() && bots.containsKey(guild)){
                    int page = 0;
                    for(OptionMapping option : event.getOptions()){
                        page = option.getAsInt()-1;
                    }
                    System.out.println(bots.get(guild).getQueue().size());
                    List<AudioTrack> tracks;
                    if(bots.get(guild).getQueue().size()< 15){
                        tracks = bots.get(guild).getQueue();
                    }
                    else{
                        tracks = bots.get(guild).getQueue().subList(page*15, (page*15)+15);
                    }
                    builder.setTitle("Track queue");
                    for(AudioTrack track : tracks){
                        builder.addField("", "["+track.getInfo().title+"]("+track.getInfo().uri+")\n Duration: "+getTime(track.getDuration()), false);
                    }
                    int totalTime = 0;
                    for(AudioTrack track : tracks){
                        totalTime += track.getDuration();
                    }
                    builder.setFooter("Total time remaining: " + getTime(totalTime-bots.get(guild).getCurrentTrack().getDuration()) + " | Total tracks in queue: " + bots.get(guild).getQueue().size());
                    event.replyEmbeds(builder.build()).queue();
                }
                break;
            case "nowplaying":
                if(author.getVoiceState().inAudioChannel() && bots.containsKey(guild)){
                    AudioTrack track = bots.get(guild).getCurrentTrack();
                    event.reply("Currently playing track: " + "["+track.getInfo().title+"]("+track.getInfo().uri+")").queue();
                }
                break;
            case "disconnect":
                if(author.getVoiceState().inAudioChannel() && bots.containsKey(guild)){
                    bots.get(guild).disconnect();
                }
                event.reply("Successfully disconnected bot").queue();
                break;
            case "pause":
                if(author.getVoiceState().inAudioChannel() && bots.containsKey(guild)){
                    if(bots.get(guild).pause()){
                        event.reply("Bot paused!").queue();
                    }
                    else{
                        event.reply("Bot unpaused!").queue();
                    }
                }
                break;
            case "clear":
                if(author.getVoiceState().inAudioChannel() && bots.containsKey(guild)){
                    bots.get(guild).clear();
                    event.reply("Queue cleared!").queue();
                }
                break;
            case "roll":
                ArrayList<String> entries = new ArrayList<>();
                for(OptionMapping option : event.getOptions()){
                    entries.add(option.getAsString());
                }
                HashMap<String, Integer> results = new HashMap<String, Integer>();
                for(String entry : entries){
                    int roll = (int) (Math.random()*100);
                    results.put(entry, roll);
                    builder.appendDescription("**"+entry+"**: "+roll+"\n");
                }
                int winnerValue = Collections.max(results.values());
                String winner = "";
                for(String key : results.keySet()){
                    if(results.get(key).equals(winnerValue)){
                        winner = key;
                    }
                }
                builder.addField("", "**"+winner+"** has won the roll", false);
                builder.setColor(Color.blue);
                builder.setThumbnail("https://cdn.discordapp.com/attachments/692410386657574955/889818089066221578/dice.png");
                builder.setTitle("Rolls");
                event.replyEmbeds(builder.build()).queue();
                break;
		    case "welcomemessage":
				if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

					builder.setTitle("Welcome to The Nut Hut");
					builder.setDescription(
							"The World of Warcraft guild The Nut Hut - <Argent Dawn> welcomes you to our discord server!\nYou can find our rules in "
									+ guild.getTextChannelById("642853163774509116").getAsMention()
									+ "\nBelow you can choose the roles you need in this discord server!");
					builder.setImage("https://cdn.discordapp.com/attachments/642853163774509116/922532262459867196/The_nut_hut.gif");
						
					SelectMenu type_menu = SelectMenu.create("type_menu")
						.setPlaceholder("PvE and/or PvP")
						.setMinValues(0)
						.setMaxValues(2)
						.addOption("PvE", "pve")
						.addOption("PvP", "pvp")
						.build();

					SelectMenu role_menu = SelectMenu.create("role_menu")
						.setPlaceholder("Choose your role(s)")
						.setMinValues(0)
						.setMaxValues(3)
						.addOption("Tank", "tank", Emoji.fromCustom("Tank", 869171302307610695L, false))
						.addOption("Healer", "healer", Emoji.fromCustom("Healer", 869171419458707506L, false))
						.addOption("DPS", "dps", Emoji.fromCustom("Dps", 869171471992360990L, false))
						.build();

					SelectMenu other_games_menu = SelectMenu.create("other_games_menu")
						.setPlaceholder("Choose accces to other games channels")
						.setMaxValues(11)
						.setMinValues(0)
						//.addOption("Among Us", "amongus", Emoji.fromCustom("amongus", 777507568251043880L, false))
						.addOption("Minecraft", "minecraft", Emoji.fromCustom("minecraft", 777508556429459477L, false))
						//.addOption("Terraria", "terraria", Emoji.fromCustom("terraria", 777509181481549844L, false))
						.addOption("League of Legends", "leagueoflegends", Emoji.fromCustom("league", 852537658252984330L, false))
						.addOption("From Software Games", "fromsoftgames", Emoji.fromCustom("fromsoftware", 777624293948915773L, false))
						.addOption("Rockstar Games", "rockstargames", Emoji.fromCustom("Rockstar", 847213407681773578L, false))
						.addOption("Blizzard Games", "blizzardgames", Emoji.fromCustom("blizzard", 854794855968145409L, false))
						.addOption("EA Games", "eagames", Emoji.fromCustom("EA", 854794890218569738L, false))
						//.addOption("Ubisoft Games", "ubisoftgames", Emoji.fromCustom("Ubisoft", 854794796962676736L, false))
						.addOption("Square Enix Games", "squareenixgames", Emoji.fromCustom("SE", 867691313191977009L, false))
						.addOption("Nintendo Games", "nintendogames", Emoji.fromCustom("Nintendo", 916271940870762506L, false))
						.build();

					SelectMenu misc_menu = SelectMenu.create("misc_menu")
						.setPlaceholder("Choose misc roles")
						.setMaxValues(4)
						.addOption("Mount Whore", "mountwhore", Emoji.fromCustom("Panties", 652562519470374933L, false))
						.addOption("Meme Dealer", "memedealer", Emoji.fromCustom("Unicorndab", 645342104557584394L, false))
						.addOption("Artist", "artist")
						.addOption("NSFW", "nsfw", Emoji.fromCustom("lewd", 656973114793525258L, false))
						.build();
					
					event.replyEmbeds(builder.build()).addActionRow(type_menu).addActionRow(role_menu).addActionRow(other_games_menu).addActionRow(misc_menu).queue();
				}
				break;
		    case "version":
				String msg = "**Changelog**\n```\nRedesigned application form```";
				event.reply(msg).setEphemeral(true).queue();
				break;
		    case "applicationform":
                if(author.hasPermission(Permission.ADMINISTRATOR)){
                    MessageEmbed embed = new EmbedBuilder()
                        .setTitle("Apply to The Nut Hut raid team!")
                        .setDescription("Hi, here you can apply to the raid team!\nYou will receive a pop-up form to add your character's details.")
                        .setImage("https://cdn.discordapp.com/attachments/642853163774509116/922532262459867196/The_nut_hut.gif")
                        .build();
                    event.replyEmbeds(embed).addActionRow(Button.primary("apply_button", "Apply here!")).queue();
                }
                else{
                    event.reply("You are not an administrator!").setEphemeral(true).queue();
                }
                break;
            case "removeraider":
                if(author.hasPermission(Permission.ADMINISTRATOR)){

                    Member member = event.getOption("raider").getAsMember();
                    RaidTeamManager.removeRaider(member);
                    event.reply("Successfully removed raider from the raid team!").setEphemeral(true).queue();
                }
                else{
                    event.reply("You are not an administrator!").setEphemeral(true).queue();
                }
                break;
            case "addraider":
            event.deferReply(true).queue();
                if(author.hasPermission(Permission.ADMINISTRATOR)){
                    RaidTeamManager.addRaiderOption(event.getOptions(), event.getOption("raider").getAsMember().getId(), guild);
                    event.getHook().editOriginal("Successfully added raider to the team!").queue();
                }
                else{
                    event.getHook().editOriginal("You are not an administrator!").queue();
                }
                break;
            case "updateteam":
                if(author.hasPermission(Permission.ADMINISTRATOR)){
                    event.deferReply(true).queue();
                    RaidTeamManager.update(event.getGuild());
                    event.getHook().editOriginal("updated raid team").queue();
                }
                else{
                    event.getHook().editOriginal("You are not an administrator!").queue();
                }
                break;
            case "spawnmessage":
                if(author.hasPermission(Permission.ADMINISTRATOR)){
                    MessageEmbed embed = new EmbedBuilder().setTitle("empty embed").build();
                    event.replyEmbeds(embed).addActionRow(Button.success("finish_button", "Finish"), 
                    Button.secondary("set_title", "Set Title"),
                    Button.secondary("set_description", "Set Description"),
                    Button.secondary("add_field", "Add Field"),
                    Button.secondary("add_image", "Add Image"))
                    .addActionRow(Button.danger("clear_embed", "Clear")
                    ).queue();
                }
                else{
                    event.reply("You are not an administrator").setEphemeral(true).queue();
                }
                break;
            case "editmessage":
                if(author.hasPermission(Permission.ADMINISTRATOR)){
                    boolean inline = false;
                    String fieldname = "";
                    Message message = guild.getTextChannelById(event.getMessageChannel().getId()).getHistoryAround(event.getOption("messageid").getAsString(), 1).complete().getMessageById(event.getOption("messageid").getAsString());
                    if(event.getOption("inline") != null){
                        inline = event.getOption("inline").getAsBoolean();
                    }
                    if(event.getOption("fieldname") != null){
                        fieldname = event.getOption("fieldname").getAsString();
                    }
                    MessageEmbed embed = message.getEmbeds().get(0);
                    builder.setTitle(embed.getTitle());
                    builder.setDescription(embed.getDescription());
                    for(Field field : embed.getFields()){
                        builder.addField(field);
                    }
                    for(OptionMapping option : event.getOptions()){
                        switch(option.getName()){
                            case "description":
                                builder.setDescription(option.getAsString());
                                break;
                            case "title":
                                builder.setTitle(option.getAsString());
                                break;
                            case "field":
                                builder.addField(fieldname, option.getAsString(), inline);
                                break;
                            case "imageurl":
                                builder.setImage(option.getAsString());
                                break;
                            }
                        }
                    message.editMessageEmbeds(builder.build()).queue();
                    event.reply("Successfully edited message").setEphemeral(true).queue();
                }
                else{
                    event.reply("You are not an administrator!").setEphemeral(true).queue();
                }
                break;
            case "message":
                if(author.hasPermission(Permission.ADMINISTRATOR)){
                    Message announcement;
                    if(event.getOption("channel_id") == null){
                        announcement = event.getMessageChannel()
                            .getHistoryAround(event.getOption("message_id")
                            .getAsString(), 1)
                            .complete()
                            .getMessageById(event.getOption("message_id")
                            .getAsString());
                    }
                    else{
                        announcement = guild.getTextChannelById(event.getOption("channel_id").getAsString())
                            .getHistoryAround(event.getOption("message_id")
                            .getAsString(), 1)
                            .complete()
                            .getMessageById(event.getOption("message_id")
                            .getAsString());
                    }
                    event.reply(announcement).queue();
                }
        }
    }
    
    public void onMessageReceived(MessageReceivedEvent event){
        System.out.println("Server:                 " + event.getGuild().getName());
        System.out.println("Channel:                " + event.getChannel().getName());
        System.out.println("Author:                 " + event.getAuthor().getName());
        System.out.println("Message:                " + event.getMessage().getContentDisplay());
        System.out.println("Number of attachments:  " + event.getMessage().getAttachments().size());
        for(Attachment url : event.getMessage().getAttachments()){
            System.out.println("Attachment:             " + url.getUrl());
        }
        System.out.println();
    }

    private String getTime(long duration) {
        String minutes = String.valueOf((duration/1000)/60);
        if(Integer.parseInt(minutes) < 10){
            minutes = "0" + minutes;
        }
        String seconds = String.valueOf((duration/1000)%60);
        if(Integer.parseInt(seconds) < 10){
            seconds = "0"+seconds;
        }
        return minutes + ":" + seconds;
    }

	public Role findRole(Member member, String name) {
        List<Role> roles = member.getRoles();
        return roles.stream()
            .filter(role -> role.getName().equals(name)) // filter by role name
            .findFirst() // take first result
            .orElse(null); // else return null
	}

}
