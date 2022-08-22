package win.skademaskinen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;


public class CommandListener extends ListenerAdapter {
    private HashMap<Guild, MusicBot> bots = new HashMap<>();
    private ArrayList<ModalInteraction> modals = new ArrayList<ModalInteraction>();
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
                case "apply":
                    try {
                        //first we initialize the wow api
                        JSONObject wowApi = (JSONObject) Config.getConfig().get("wow_api");
                        String clientId = (String) wowApi.get("client_id");
                        String clientSecret = (String) wowApi.get("client_secret");
                        String command = "curl -u "+clientId+":"+clientSecret+" -d grant_type=client_credentials https://eu.battle.net/oauth/token";
                        JSONParser parser = new JSONParser();
                        Process p1 = runtime.exec(command);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                        p1.waitFor();
                        JSONObject token_file = (JSONObject) parser.parse(reader);
                        String token = (String) token_file.get("access_token");

                        //get the first bits of data from the wow API
                        String characterName = event.getOption("name").getAsString();
                        String characterServer = event.getOption("server").getAsString();
                        String characterClass = "invalid";
                        String characterRole = event.getOption("role").getAsString();
                        String characterItemLevel = "invalid";
                        String characterItemLevelAverage = "invalid";
                        boolean raidtimes = event.getOption("raidtimes").getAsBoolean();

                        command = "curl https://eu.api.blizzard.com/profile/wow/character/"+characterServer.toLowerCase()+"/"+characterName.toLowerCase()+"?namespace=profile-eu&locale=en_GB&access_token="+token;
                        Process p2 = runtime.exec(command);
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                        p2.waitFor();
                        JSONObject characterData = (JSONObject) parser.parse(reader2);
                        JSONObject characterClassData = (JSONObject) characterData.get("character_class");
                        characterClass = (String) characterClassData.get("name");
                        characterItemLevel = String.valueOf(characterData.get("equipped_item_level"));
                        characterItemLevelAverage = String.valueOf(characterData.get("average_item_level"));
                        JSONObject characterSpecData = (JSONObject) characterData.get("active_spec");
                        String characterSpec = (String) characterSpecData.get("name");

                        builder.setTitle("Raid team application");
                        builder.setDescription("Raid team application for " +"["+characterName+"](https://worldofwarcraft.com/en-gb/character/eu/"+characterServer+"/"+characterName+") ("+characterServer+")");
                        builder.addField("Class/Role", characterClass+"/"+characterSpec + " ("+characterRole+")", false);
                        builder.addField("Item Level", "Equipped: "+characterItemLevel+ " Average: "+characterItemLevelAverage, false);
                        if(raidtimes){
                            builder.addField("Will you be able to raid on Wednesdays and Sundays at 19:30 - 22:30 server time?", "Yes", false);
                        }
                        else{
                            builder.addField("Will you be able to raid on Wednesdays and Sundays at 19:30 - 22:30 server time?", "No", false);
                        }
                    
                        //automated response
                        int score = 0;
                        JSONObject applicationData = (JSONObject) Config.getConfig().get("raid_form");
                        long requiredIlvl = (long) applicationData.get("minimum_ilvl");
                        JSONArray filledRoles = (JSONArray) applicationData.get("filled_roles");
                        JSONArray preferredRoles = (JSONArray) applicationData.get("preferred_roles");
                        JSONArray neededClasses = (JSONArray) applicationData.get("needed_classes");
                        ArrayList<String> fields = new ArrayList<String>();
                        if(!filledRoles.contains(characterRole.toLowerCase())){
                            if(Integer.parseInt(characterItemLevel)>=requiredIlvl){
                                score++;
                            }
                            else{
                                fields.add("Your item level is too low, we have a requirement of 252 (you need to be "+(requiredIlvl-Long.parseLong(characterItemLevel))+" higher)");
                            }
                            if(preferredRoles.contains(characterRole.toLowerCase())){
                                score++;
                            }
                            else{
                                fields.add("We are not actively looking for " + characterRole + "s");
                            }
                            if(neededClasses.contains(characterClass.toLowerCase())){
                                score++;
                            }
                            else{
                                fields.add("We are not actively looking for " + characterClass + "s");
                            }
                            if(raidtimes){
                                score++;
                            }
                        }
                        else{
                            fields.add("we do not need any more " + characterRole + "s");
                        }
                        String color = "";
                        if(score > 3){
                            builder.setColor(Color.green);
                            color = "green";
                        }
                        else if(score == 3){
                            builder.setColor(Color.yellow);
                            color = "yellow";
                        }
                        else if(score <= 1){
                            builder.setColor(Color.red);
                            color = "red";
                        }
                        if(!color.equalsIgnoreCase("green")){
                            String title ="Your application is " + color + " because:";
                            String final_field = "";
                            for(String field : fields){
                                final_field += field + '\n';
                            }
                            builder.addField(title, final_field, false);
                        }
                    
                        //finally lets do some interesting profile picture getting
                        command = "curl https://eu.api.blizzard.com/profile/wow/character/"+characterServer.toLowerCase()+"/"+characterName.toLowerCase()+"/character-media?namespace=profile-eu&locale=en_GB&access_token="+token;
                        Process p3 = runtime.exec(command);
                        BufferedReader reader3 = new BufferedReader(new InputStreamReader(p3.getInputStream()));
                        p3.waitFor();
                        JSONObject characterMedia = (JSONObject) parser.parse(reader3);
                        JSONArray assets = (JSONArray) characterMedia.get("assets");
                        JSONObject avatarObject = (JSONObject) assets.get(0);
                        String avatarUrl = (String) avatarObject.get("value");
                        System.out.println(avatarUrl);
                        builder.setThumbnail(avatarUrl);
                    } catch (IOException | ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    event.replyEmbeds(builder.build()).addActionRow(Button.primary("approve_button", "Approve"), Button.primary("decline_button", "Decline")).queue();

                break;
            case "announcement":
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
        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event){
        switch (event.getButton().getId()) {
            case "apply_button":
				TextInput name = TextInput.create("name", "Character name", TextInputStyle.SHORT)
                    .setPlaceholder("Your character name")
                    .build();
                TextInput server = TextInput.create("server", "Character server", TextInputStyle.SHORT)
                    .setPlaceholder("Your character server, example: argent-dawn")
                    .build();
                TextInput role = TextInput.create("role", "Your role", TextInputStyle.SHORT)
                    .setPlaceholder("Healer, Tank, Ranged Damage or Melee Damage")
                    .build();
                TextInput raidtimes = TextInput.create("raidtimes", "Can you raid with us? (yes/no)", TextInputStyle.SHORT)
                    .setPlaceholder("Wednesday and Sunday at 19:30 - 22:30 server time?")
                    .build();

                Modal modal = Modal.create("Application form", "application")
                    .addActionRows(ActionRow.of(name), ActionRow.of(server), ActionRow.of(role), ActionRow.of(raidtimes))
                    .build();

                event.replyModal(modal).queue();
                break;
		}
        if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            if (event.getButton().getId().contains("approve_button")) {
                String id = event.getButton().getId().replace("approve_button", "");
                ModalInteraction modal = null;
                for(ModalInteraction m : modals){
                    if (m.getId().equals(id)){
                        modal = m;
                        break;
                    }
                }
                event.reply(event.getMember().getAsMention() + " approved " + modal.getMember().getAsMention() + "s application, you should have a dps/heal/tanking check!").queue();
            }
            else if(event.getButton().getId().contains("decline_button")){
                String id = event.getButton().getId().replace("decline_button", "");
                ModalInteraction modal = null;
                for(ModalInteraction m : modals){
                    if (m.getId().equals(id)){
                        modal = m;
                        break;
                    }
                }
                event.reply(event.getMember().getAsMention() + " declined " + modal.getMember().getAsMention() + "s application").queue();
                event.getMessage().delete().complete();
            }
            else if(event.getButton().getId().contains("add_button")){
                String id = event.getButton().getId().replace("add_button", "");
                ModalInteraction modal = null;
                for(ModalInteraction m : modals){
                    if (m.getId().equals(id)){
                        modal = m;
                        break;
                    }
                }
                RaidTeamManager.addRaider(modal.getValues(), modal.getMember().getId(), event.getGuild());
                event.reply("Successfully added raider to the team and deleted application!").setEphemeral(true).queue();
                event.getMessage().delete().complete();
            }
        }
        else{
            event.reply("You are not an administrator!").setEphemeral(true).queue();
        }
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event){
        switch(event.getName()){
            case "apply":
            case "addraider":
                if(event.getFocusedOption().getName().equalsIgnoreCase("role")){
                    String[] choices = {"Tank", "Healer", "Ranged Damage", "Melee Damage"};
                    List<Command.Choice> options = Stream.of(choices)
                        .filter(choice -> choice.startsWith(event.getFocusedOption().getValue()))
                        .map(choice -> new Command.Choice(choice, choice))
                        .collect(Collectors.toList());
                    event.replyChoices(options).queue();
                }
                else if(event.getFocusedOption().getName().equalsIgnoreCase("server")){
                    String[] choices = {"argent-dawn"};
                    List<Command.Choice> options = Stream.of(choices)
                        .filter(choice -> choice.startsWith(event.getFocusedOption().getValue()))
                        .map(choice -> new Command.Choice(choice, choice))
                        .collect(Collectors.toList());
                    event.replyChoices(options).queue();
                }

                break;
        }
    }
    
    public void onModalInteraction(ModalInteractionEvent event){
        event.deferReply().queue();
        
        String name = event.getValue("name").getAsString();
        String server = event.getValue("server").getAsString();
        String role = event.getValue("role").getAsString();
        boolean raidtimes = event.getValue("raidtimes").getAsString().equalsIgnoreCase("yes") ? true : false;
        System.out.println("Name:       " + name);
        System.out.println("Server:     " + server);
        System.out.println("Role:       " + role);
        System.out.println("raidtimes:  " + raidtimes);
        try{
            String _class = RaidTeamManager.get_class(name, server);
            String ilvl = RaidTeamManager.get_ilvl(name, server);
            String avgIlvl = RaidTeamManager.get_avg_ilvl(name, server);
            String spec = RaidTeamManager.get_spec(name, server);
            EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Raid team application")
                .setDescription("**Application by:** "+event.getMember().getAsMention()+"\nRaid team application for " +"["+name+"](https://worldofwarcraft.com/en-gb/character/eu/"+server+"/"+name+") ("+server+")")
                .addField("Class/Role", _class+"/"+spec + " ("+role+")", false)
                .addField("Item Level", "Equipped: "+ilvl+ " Average: "+avgIlvl, false);
            if(raidtimes){
                builder.addField("Will you be able to raid on Wednesdays and Sundays at 19:30 - 22:30 server time?", "Yes", false);
            }
            else{
                builder.addField("Will you be able to raid on Wednesdays and Sundays at 19:30 - 22:30 server time?", "No", false);
            }

            int score = 0;
            JSONObject applicationData = (JSONObject) Config.getConfig().get("raid_form");
            long reqIlvl = (long) applicationData.get("minimum_ilvl");
            JSONArray filledRoles = (JSONArray) applicationData.get("filled_roles");
            JSONArray preferredRoles = (JSONArray) applicationData.get("preferred_roles");
            JSONArray neededClasses = (JSONArray) applicationData.get("needed_classes");
            ArrayList<String> fields = new ArrayList<String>();
            
            
            if(!filledRoles.contains(role.toLowerCase())){
                if(Integer.parseInt(ilvl)>=reqIlvl){
                    score++;
                }
                else{
                    fields.add("Your item level is too low, we have a requirement of 252 (you need to be "+(reqIlvl-Long.parseLong(ilvl))+" higher)");
                }
                if(preferredRoles.contains(role.toLowerCase())){
                    score++;
                }
                else{
                    fields.add("We are not actively looking for " + role + "s");
                }
                if(neededClasses.contains(_class.toLowerCase())){
                    score++;
                }
                else{
                    fields.add("We are not actively looking for " + _class + "s");
                }
                if(raidtimes){
                    score++;
                }
            }
            else{
                fields.add("we do not need any more " + role + "s");
            }
            String color = "";
            if(score > 3){
                builder.setColor(Color.green);
                color = "green";
            }
            else if(score == 3){
                builder.setColor(Color.yellow);
                color = "yellow";
            }
            else if(score <= 2){
                builder.setColor(Color.red);
                color = "red";
            }
            if(!color.equalsIgnoreCase("green")){
                String title ="Your application is " + color + " because:";
                String final_field = "";
                for(String field : fields){
                    final_field += field + '\n';
                }
                builder.addField(title, final_field, false);
            }
        
            //finally lets do some interesting profile picture getting
            String avatarUrl = RaidTeamManager.get_image(name, server);
            builder.setThumbnail(avatarUrl);

            event.getHook()
                .editOriginalEmbeds(builder.build())
                .setActionRow(
                    Button.success(event.getInteraction().getId()+"approve_button", "Approve"), 
                    Button.danger(event.getInteraction().getId()+"decline_button", "Decline"), 
                    Button.secondary(event.getInteraction().getId()+"add_button", "Add to team"))
                .queue();
            modals.add(event.getInteraction());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


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

	public Role findRole(Member member, String name) {
    List<Role> roles = member.getRoles();
    return roles.stream()
                .filter(role -> role.getName().equals(name)) // filter by role name
                .findFirst() // take first result
                .orElse(null); // else return null
	}

	
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event){
		String menu_id = event.getComponentId();
		List<String> values = event.getValues();
		Guild guild = event.getGuild();
		EmbedBuilder builder = new EmbedBuilder();
		HashMap<String, String> types = new HashMap<String, String>();
		types.put("pve", "776204665524191232");
		types.put("pvp", "776207123515441162");
		HashMap<String, String> roles = new HashMap<String, String>();
		roles.put("tank", "776184488077950977");
		roles.put("healer", "776184278832381962");
		roles.put("dps", "776184402132336661");
		HashMap<String, String> other_games = new HashMap<String, String>();
		other_games.put("amongus", "777502866377015326");
		other_games.put("minecraft", "777503152605364248");
		other_games.put("terraria", "777502989304725505");
		other_games.put("leagueoflegends", "777539954195562497");
		other_games.put("fromsoftgames", "777623985851465808");
		other_games.put("rockstargames", "847212557404340224");
		other_games.put("blizzardgames", "854792211563413515");
		other_games.put("eagames", "854791884332335185");
		other_games.put("ubisoftgames", "854795088008708136");
		other_games.put("squareenixgames", "867696118498721842");
		other_games.put("nintendogames", "916271612498690049");
		HashMap<String, String> misc = new HashMap<String, String>();
		misc.put("mountwhore", "776187232918568980");
		misc.put("memedealer", "776726601874014218");
		misc.put("artist", "785636073040642048");
		misc.put("nsfw", "970614921354174474");
		ArrayList<String> added_roles = new ArrayList<String>();
		switch(menu_id){
		case "type_menu":
			for(String role : types.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(types.get(role))).queue();
					added_roles.add(guild.getRoleById(types.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(types.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
			
		case "role_menu":
			for(String role : roles.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(roles.get(role))).queue();
					added_roles.add(guild.getRoleById(roles.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(roles.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
			
		case "other_games_menu":
			for(String role : other_games.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(other_games.get(role))).queue();
					added_roles.add(guild.getRoleById(other_games.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(other_games.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
		case "misc_menu":
			for(String role : misc.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(misc.get(role))).queue();
					added_roles.add(guild.getRoleById(misc.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(misc.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
		}
	}

}
