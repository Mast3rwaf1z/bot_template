package win.skademaskinen;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandListener extends ListenerAdapter {
    final private String[] colors = {"blue", "green", "gray", "yellow", "orange", "red", "white", "purple", "pink", "darkgreen"};
    private HashMap<Guild, MusicBot> bots = new HashMap<>();
    private DatabaseHandler databaseHandler;


    public CommandListener() throws ClassNotFoundException, SQLException, IOException, ParseException{
        databaseHandler = new DatabaseHandler();

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        System.out.println("Command:                " + event.getCommandString());
        System.out.println();
        Guild guild = event.getGuild();
        Member author = event.getMember();
        switch (event.getName().toLowerCase()) {
            case "ping":
                event.reply("Pong").queue();
                break;
        
            case "jail":
                Thread thread = new Thread(){
                    public void run(){
                        jail(author, event, guild);
                    }
                };
                thread.start();
                break;

            case "color":
                for(String color : colors){
                    if(author.getRoles().contains(guild.getRolesByName(color, true).get(0))){
                        System.out.println("Removing role" + color);
                        guild.removeRoleFromMember(author, guild.getRolesByName(color, true).get(0)).queue();
                    }
                }
                guild.addRoleToMember(author, guild.getRolesByName(event.getOption("color").getAsString(), true).get(0)).queue();
                event.reply("Set color to " + event.getOption("color").getAsString()).queue();
                break;

            //music
            case "play":
                if (author.getVoiceState().inAudioChannel() && bots.containsKey(guild)) {
                    MusicBot bot = bots.get(guild);
                    bot.play(event.getOption("url").getAsString().strip(), event);
                }
                else if(event.getOptions().size() == 0){
                    event.reply("Please specify a track").queue();
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
                    EmbedBuilder builder = new EmbedBuilder();
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
            case "poop":
                int poops = 0;
                try {
                    poops = databaseHandler.getPoopsForMember(author);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                event.reply("You currently have "+poops+" poops").queue();
                break;
            case "leaderboard":
                try {
                    ResultSet resultSet = databaseHandler.getPoopsForGuild(guild);
                    HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
                    EmbedBuilder builder2 = new EmbedBuilder();
                    builder2.setTitle("Poop leaderboard");
                    builder2.setThumbnail("https://cdn.discordapp.com/attachments/692410386657574955/809730484640284682/lort.png");

                    while(resultSet.next()){
                        resultMap.put(resultSet.getString("id"), resultSet.getInt("count"));
                    }

                    Map<String, Integer> finalResult = resultMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                    for(int i = finalResult.size(); i > 0; i--){
                        String id = finalResult.keySet().toArray()[i - 1].toString();
                        builder2.addField(guild.retrieveMemberById(id).complete().getEffectiveName(), finalResult.get(id).toString() + " Poop", false);
                    }
                    event.replyEmbeds(builder2.build()).queue();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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
        
        Message message = event.getMessage();
        if(!message.getGuild().getId().equalsIgnoreCase("642852517197250560")){
            double roll = Math.random() * 100;
            if(roll > 99 - (Math.exp(message.getContentRaw().length()/175))){
                message.addReaction("\uD83D\uDCA9").queue();
                try {
                    databaseHandler.addPoopToMember(event.getMember());
                } catch (SQLException e) {
                    try {
                        databaseHandler.createPoopTable(event.getGuild());
                        databaseHandler.addPoopToMember(event.getMember());
                    } catch (SQLException e1) {
                    }
                }
            }
        }
    }

    public void onGuildJoined(GuildJoinEvent event) throws SQLException{
        databaseHandler.createPoopTable(event.getGuild());

    }

    private void jail(Member author, SlashCommandInteractionEvent event, Guild guild){

        Member who = author;
        int time = -1;
        String measurement = "seconds";

        if(!author.hasPermission(Permission.ADMINISTRATOR)){
            event.reply("You are not an administrator").queue();
            return;
        }

        else if (guild.getTextChannelsByName("jail", true).size() == 0) {
            event.reply("This server does not have a #jail channel").queue();
            return;
        }
        else if(guild.getRolesByName("jailed", true).size() == 0){
            event.reply("This server does not have an @jailed role").queue();
            return;
        }

        for(OptionMapping option : event.getOptions()){
            switch (option.getName()) {
                case "who":
                    who = option.getAsMember();
                    break;
            
                case "time":
                    time = option.getAsInt();
                    break;

                case "measurement":
                    measurement = option.getAsString();
                    break;
            }
        }
        if(who.equals(event.getMember())){
            event.reply("Please specify user to be jailed").queue();
            return;
        }
        else if(time == -1){
            event.reply("Please specify time to be jailed").queue();
            return;
        }
        //everything went as planned
        Role jailed = guild.getRolesByName("jailed", true).get(0);
        TextChannel jail = guild.getTextChannelsByName("jail", true).get(0);
        List<Role> previous_roles = who.getRoles();
        for(Role role : previous_roles){
            if(!role.getName().equalsIgnoreCase("Server Booster")){
                guild.removeRoleFromMember(who, role).queue();
            }
        }
        guild.addRoleToMember(who, jailed).queue();
        event.reply("Successfully jailed " + who.getAsMention() + " for " + time + " " + measurement).queue();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(who.getEffectiveName() + " has been jailed!");
        builder.setDescription(who.getAsMention() + " has been jailed for " + time + " " + measurement);
        builder.setThumbnail(who.getAvatarUrl());
        jail.sendMessageEmbeds(builder.build()).queue();

        System.out.println("sleeping thread");
        switch(measurement.toLowerCase()){
            case "seconds":
                System.out.println("Sleeping seconds");
                try {
                    Thread.sleep(time*1000);
                } catch (InterruptedException e) {
                    event.reply("Failed to sleep thread").queue();
                    e.printStackTrace();
                }
                break;
            case "minutes":
                System.out.println("Sleeping minutes");
                try {
                    Thread.sleep((long)(time*6e4));
                } catch (InterruptedException e) {
                    event.reply("Failed to sleep thread").queue();
                    e.printStackTrace();
                }
                break;
            case "hours":
                System.out.println("Sleeping hours");
                try {
                    Thread.sleep((long)(time*36e5));
                } catch (InterruptedException e) {
                    event.reply("Failed to sleep thread").queue();
                    e.printStackTrace();
                }
                break;
            case "days":
                System.out.println("Sleeping days");
                try {
                    Thread.sleep((long)(time*864e5));
                } catch (InterruptedException e) {
                    event.reply("Failed to sleep thread").queue();
                    e.printStackTrace();
                }
                break;
        }

        //read roles
        guild.removeRoleFromMember(who, jailed).queue();
        for(Role role : previous_roles){
            if(!role.getName().equalsIgnoreCase("Server Booster")){
                guild.addRoleToMember(who, role).queue();
            }
        }
        System.out.println("Released " + who.getEffectiveName() + " From jail");
    }

}
