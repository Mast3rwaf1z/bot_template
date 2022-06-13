package win.skademaskinen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class CommandListener extends ListenerAdapter {
    private HashMap<Guild, MusicBot> bots = new HashMap<>();
    private DatabaseHandler databaseHandler;
    Runtime runtime = Runtime.getRuntime();


    public CommandListener() throws ClassNotFoundException, SQLException, IOException, ParseException{
        System.out.println("Creating database handler");
        databaseHandler = new DatabaseHandler();

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        System.out.println("Command:                " + event.getCommandString());
        System.out.println();
        event.deferReply();
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
                        builder.setDescription("Raid team application for " +"["+characterName+"](https://worldofwarcraft.com/en-gb/character/eu/"+characterServer+"/"+characterName+")");
                        builder.setFooter(characterName);
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
                        ArrayList<Field> fields = new ArrayList<Field>();
                        if(!filledRoles.contains(characterRole.toLowerCase())){
                            if(Integer.parseInt(characterItemLevel)>=requiredIlvl){
                                score++;
                            }
                            else{
                                fields.add(new Field("", "Your item level is too low, we have a requirement of 252 (you need to be "+(requiredIlvl-Long.parseLong(characterItemLevel))+" higher)", true));
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
                            //event.getChannel().sendMessage(event.getJDA().getRoleById("756106691946217542").getAsMention()).queue();
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

        }
    }

    public void onButtonInteraction(ButtonInteractionEvent event){
        switch (event.getButton().getId()) {
            case "approve_button":
                if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                    event.reply(event.getMember().getNickname()+ " "+ event.getButton().getLabel() + "d " + event.getMessage().getEmbeds().get(0).getFooter().getText()+"s application\nThe next step would be to have a DPS/Healing/Tanking check").queue();
                }
                else{
                    event.deferEdit().queue();
                }
                break;
        
            case "decline_button":
                if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                    event.reply(event.getMember().getNickname()+ " "+ event.getButton().getLabel() + "d " + event.getMessage().getEmbeds().get(0).getFooter().getText()+"s application\nPlease refer to your application for an explaination").queue();
                }
                else{
                    event.deferEdit().queue();
                }
                break;
        }
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event){
        switch(event.getName()){
            case "apply":
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

}
