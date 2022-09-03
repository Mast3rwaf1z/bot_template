package win.skademaskinen;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu.Builder;

import java.awt.Color;
import java.io.IOException;

public class ModalListener extends ListenerAdapter{
	public void onModalInteraction(ModalInteractionEvent event){
        EmbedBuilder builder = new EmbedBuilder();
        switch(event.getModalId()){
            case "Application form":
            String[] validRoles = {"tank", "healer", "melee damage", "ranged damage"};
            String name = event.getValue("name").getAsString();
            String server = event.getValue("server").getAsString();
            String role = event.getValue("role").getAsString();
            boolean raidtimes = event.getValue("raidtimes").getAsString().equalsIgnoreCase("yes") ? true : false;
            boolean isValid = false;
            for(String Role : validRoles){
                if(role.equalsIgnoreCase(Role.toLowerCase())){
                    isValid = true;
                    break;
                }
            }
            if(!isValid){
                event.reply("Invalid role, please select one of the following: Tank, Healer, Ranged Damage, Melee Damage").setEphemeral(true).queue();
                return;
            }
            event.deferReply().queue();
            Shell.printer(Colors.yellow("Application:"));
            Shell.printer(Colors.green("Name:       ") + name);
            Shell.printer(Colors.green("Server:     ") + server);
            Shell.printer(Colors.green("Role:       ") + role);
            Shell.printer(Colors.green("raidtimes:  ") + raidtimes);
            try{
                String _class = RaidTeamManager.get_class(name, server);
                String ilvl = RaidTeamManager.get_ilvl(name, server);
                String avgIlvl = RaidTeamManager.get_avg_ilvl(name, server);
                String spec = RaidTeamManager.get_spec(name, server);
                builder.setTitle("Raid team application")
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
                JSONObject applicationData = (JSONObject) Config.getFile("team_requirements.json").get("raid_form");
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
                        fields.add("Your item level is too low, we have a requirement of "+applicationData.get("minimum_ilvl")+" (you need to be "+(reqIlvl-Long.parseLong(ilvl))+" higher)");
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
                    Config.modals.add(new ModalData(event.getValues(), event.getId(), event.getMember().getId()));
                } catch (IOException | ParseException e) {
                    for(StackTraceElement element : e.getStackTrace()){
                        Shell.printer(Colors.red(element.toString()));
                    }
                }
                break;
            case "title_modal":
                Message message = event.getMessageChannel().getHistoryAround(event.getValue("message_id").getAsString(), 1).complete().getMessageById(event.getValue("message_id").getAsString());
                MessageEmbed embed = message.getEmbeds().get(0);
                builder.setTitle(event.getValue("title_input").getAsString());
                builder.setDescription(embed.getDescription());
                for(Field field : embed.getFields()){
                    builder.addField(field);
                }
                if(embed.getImage() != null){
                    builder.setImage(embed.getImage().getUrl());
                }
                message.editMessageEmbeds(builder.build()).queue();
                event.reply("success").setEphemeral(true).queue();
                break;
            
            case "description_modal":
                Message dmessage = event.getMessageChannel().getHistoryAround(event.getValue("message_id").getAsString(), 1).complete().getMessageById(event.getValue("message_id").getAsString());
                MessageEmbed dembed = dmessage.getEmbeds().get(0);
                builder.setTitle(dembed.getTitle());
                builder.setDescription(event.getValue("description_input").getAsString());
                for(Field field : dembed.getFields()){
                    builder.addField(field);
                }
                if(dembed.getImage() != null){
                    builder.setImage(dembed.getImage().getUrl());
                }
                dmessage.editMessageEmbeds(builder.build()).queue();
                event.reply("success").setEphemeral(true).queue();
                break;

            case "field_modal":
                Message fmessage = event.getMessageChannel().getHistoryAround(event.getValue("message_id").getAsString(), 1).complete().getMessageById(event.getValue("message_id").getAsString());
                MessageEmbed fembed = fmessage.getEmbeds().get(0);
                builder.setTitle(fembed.getTitle());
                builder.setDescription(fembed.getDescription());
                for(Field field : fembed.getFields()){
                    builder.addField(field);
                }
                boolean inline = event.getValue("inline_input").getAsString().equalsIgnoreCase("yes") ? true : false;
                builder.addField(event.getValue("field_title").getAsString(), event.getValue("field_body").getAsString(), inline);
                if(fembed.getImage() != null){
                    builder.setImage(fembed.getImage().getUrl());
                }
                fmessage.editMessageEmbeds(builder.build()).queue();
                event.reply("success").setEphemeral(true).queue();
                break;

            case "image_modal":
                Message imessage = event.getMessageChannel().getHistoryAround(event.getValue("message_id").getAsString(), 1).complete().getMessageById(event.getValue("message_id").getAsString());
                MessageEmbed iembed = imessage.getEmbeds().get(0);
                builder.setTitle(iembed.getTitle());
                builder.setDescription(iembed.getDescription());
                for(Field field : iembed.getFields()){
                    builder.addField(field);
                }
                builder.setImage(event.getValue("url_input").getAsString());
                imessage.editMessageEmbeds(builder.build()).queue();
                event.reply("success").setEphemeral(true).queue();
                break;

            case "poll_modal":
                String description = event.getValue("description").getAsString();
                String[] options = event.getValue("options").getAsString().split(",");
                for(int i = 0; i < options.length; i++){
                    options[i] = options[i].strip();
                }
                for(int i = 0; i < options.length; i++){
                    for(int j = 0; j < options.length; j++){
                        if(options[i].equals(options[j]) && i != j){
                            event.reply("You can't have two identical options").setEphemeral(true).queue();
                            return;
                        }
                    }
                }
                builder.setTitle(event.getMember().getEffectiveName()+"s poll");
                builder.setDescription(description);
                Builder selectMenu = SelectMenu.create("poll_menu");
                for(String option : options){
                    selectMenu.addOption(option, option);
                }
                selectMenu.setMaxValues(options.length);
                builder.setColor(Color.blue);
                builder.setThumbnail("https://cdn-icons-png.flaticon.com/512/1246/1246239.png");
                
                event.replyEmbeds(builder.build()).addActionRow(selectMenu.build()).addActionRow(Button.primary("poll_button", "Show results")).queue();
                break;
                
                
        }
            
            
        }
        
}
