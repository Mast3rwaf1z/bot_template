package win.skademaskinen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandListener extends ListenerAdapter {
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
        EmbedBuilder builder = new EmbedBuilder();
        switch (event.getName().toLowerCase()) {
            case "ping":
                event.reply("Pong").queue();
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
                String characterName = event.getOption("name").getAsString();
                String characterServer = event.getOption("server").getAsString();
                String characterClass = event.getOption("class").getAsString();
                String characterRole = event.getOption("role").getAsString();
                String characterItemLevel = event.getOption("ilvl").getAsString();
                boolean raidtimes = event.getOption("raidtimes").getAsBoolean();
                builder.setTitle("Raid team application");
                builder.setDescription("Raid team application for " +"["+characterName+"](https://worldofwarcraft.com/en-gb/character/eu/"+characterServer+"/"+characterName+")");
                builder.addField("Class/Role", characterClass+"/"+characterRole, false);
                builder.addField("Item Level", characterItemLevel, false);
                if(raidtimes){
                    builder.addField("Will you be able to raid on Wednesdays and Sundays at 19:30 - 22:30 server time?", "Yes", false);
                }
                else{
                    builder.addField("Will you be able to raid on Wednesdays and Sundays at 19:30 - 22:30 server time?", "No", false);
                }

                //automated response
                int score = 0;
                try {
                    JSONObject applicationData = (JSONObject) Config.getConfig().get("raid_application_form");
                    long requiredIlvl = (long) applicationData.get("minimum_ilvl");
                    JSONArray filledRoles = (JSONArray) applicationData.get("filled_roles");
                    JSONArray preferredRoles = (JSONArray) applicationData.get("preferred_roles");
                    JSONArray neededClasses = (JSONArray) applicationData.get("needed_classes");
                    ArrayList<Field> fields = new ArrayList<Field>();
                    if(!filledRoles.contains(characterRole.toLowerCase())){

                        if(Integer.parseInt(characterItemLevel)>=requiredIlvl){
                            score++;
                        }
                        else{
                            fields.add(new Field("", "Your item level is too low, we have a requirement of 252 (you need"+(requiredIlvl-Long.parseLong(characterItemLevel))+")", false));
                        }
                        if(preferredRoles.contains(characterRole.toLowerCase())){
                            score++;
                        }
                        else{
                            fields.add(new Field("", "We are not actively looking for " + characterRole + "s", true));
                        }
                        if(neededClasses.contains(characterClass.toLowerCase())){
                            score++;
                        }
                        else{
                            fields.add(new Field("", "We are not actively looking for " + characterClass + "s", true));
                        }
                        if(raidtimes){
                            score++;
                        }

                    }
                    else{
                        fields.add(new Field("", "we do not need any more " + characterRole + "s", true));
                    }
                    String color = "";
                    if(score >= 3){
                        builder.setColor(Color.green);
                        color = "green";
                    }
                    else if(score == 2){
                        builder.setColor(Color.yellow);
                        color = "yellow";
                    }
                    else if(score <= 1){
                        builder.setColor(Color.red);
                        color = "red";
                    }
                    if(fields.size() >= 1){
                        builder.addField("Your application is " + color + " because:", "", false);
                        for(Field field : fields){
                            builder.addField(field);
                        }
                    }
                    if(color == "green"){
                        event.getChannel().sendMessage(guild.getMemberById("214752462769356802").getAsMention()).queue();
                    }
                } catch (IOException | ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //finally lets do some interesting profile picture getting
                try {
                    Runtime runtime = Runtime.getRuntime();
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
                    command = "curl https://eu.api.blizzard.com/profile/wow/character/"+characterServer.toLowerCase()+"/"+characterName.toLowerCase()+"/character-media?namespace=profile-eu&locale=en_GB&access_token="+token;
                    Process p2 = runtime.exec(command);
                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    p2.waitFor();
                    JSONObject characterMedia = (JSONObject) parser.parse(reader2);
                    JSONArray assets = (JSONArray) characterMedia.get("assets");
                    JSONObject avatarObject = (JSONObject) assets.get(0);
                    String avatarUrl = (String) avatarObject.get("value");
                    System.out.println(avatarUrl);
                    builder.setThumbnail(avatarUrl);
                } catch (IOException | ParseException | InterruptedException e) {
                    e.printStackTrace();
                }

                event.replyEmbeds(builder.build()).queue();
                break;
            case "announcement":
                break;

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

}
