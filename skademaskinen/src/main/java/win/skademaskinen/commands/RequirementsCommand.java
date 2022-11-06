package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import win.skademaskinen.WorldOfWarcraft.RaidTeamManager;
import win.skademaskinen.utils.Config;

public class RequirementsCommand implements Command{
    private boolean successTag = false;
    private boolean shouldEphemeral = false;
    private List<ActionRow> actionRows = new ArrayList<>();
    private SlashCommandInteractionEvent event;
    private Member author;
    private Guild guild;

    public RequirementsCommand(SlashCommandInteractionEvent event){
        this.event = event;
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Object run() {
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            EmbedBuilder builder = new EmbedBuilder();
            switch(event.getSubcommandName()){
                case "add":
                    RaidTeamManager.addRequirement(event.getOption("type").getAsString(), event.getOption("value").getAsString());
                    shouldEphemeral = true;
                    successTag = true;
                    return "Successfully added requirement!";
                case "remove":
                    RaidTeamManager.removeRequirement(event.getOption("type").getAsString(), event.getOption("value").getAsString());
                    shouldEphemeral = true;
                    successTag = true;
                    return "Successfully removed requirement!";
                case "list":
                    builder.setTitle("Raid team requirements!");
                    JSONObject raidForm = Config.getFile("files/team_requirements.json").getJSONObject("raid_form");
                    String filled = "";
                    for(Object role : raidForm.getJSONArray("filled_roles")){
                        filled+=role.toString()+"\n";
                    }
                    builder.addField("filled roles:", filled, false);
                    String preferred = "";
                    for(Object role : raidForm.getJSONArray("preferred_roles")){
                        preferred+=role.toString()+"\n";
                    }
                    builder.addField("Preferred roles:", preferred, false);
                    String needed = "";
                    for(Object _class : raidForm.getJSONArray("needed_classes")){
                        needed+=_class.toString()+"\n";
                    }
                    builder.addField("Needed classes:", needed, false);
                    builder.setDescription("Minimum item level: " + raidForm.get("minimum_ilvl"));

                    shouldEphemeral = true;
                    successTag = true;
                    return builder.build();
                case "setilvl":
                    RaidTeamManager.setIlvlRequirement(event.getOption("ilvl").getAsInt());
                    shouldEphemeral = true;
                    successTag = true;
                    return "Successfully set ilvl!";
                case "form":
                    JSONObject raidForm1 = Config.getFile("files/team_requirements.json").getJSONObject("raid_form");
                    String filled1 = "";
                    for(Object role :  raidForm1.getJSONArray("filled_roles")){
                        filled1+=role.toString()+", ";
                    }
                    String preferred1 = "";
                    for(Object role : raidForm1.getJSONArray("preferred_roles")){
                        preferred1+=role.toString()+", ";
                    }
                    String needed1 = "";
                    for(Object _class : raidForm1.getJSONArray("needed_classes")){
                        needed1+=_class.toString()+", ";
                    }
                    String minimum_ilvl = String.valueOf(raidForm1.get("minimum_ilvl"));
                
                    TextInput.Builder filled_field = TextInput.create("filled_roles", "Specify filled roles", TextInputStyle.PARAGRAPH)
                        .setRequired(false)
                        .setPlaceholder("Specify filled roles in this format: [role1, role2, role3]");
                    if(filled1.length() > 0){
                        filled1 = filled1.substring(0, filled1.length()-2);
                        filled_field.setValue(filled1);
                    }
                
                    TextInput.Builder preferred_field = TextInput.create("preferred_roles", "Specify preferred roles", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Specify preferred roles in this format [role1, role2, role3]")
                        .setRequired(false);
                    if(preferred1.length() > 0){
                        preferred1 = preferred1.substring(0, preferred1.length()-2);
                        preferred_field.setValue(preferred1);
                    }
                
                    TextInput.Builder needed_field = TextInput.create("needed_classes", "Specify needed classes", TextInputStyle.PARAGRAPH)
                        .setRequired(false)
                        .setPlaceholder("Specify needed classes in this format [class1, class2, class3]");
                        if(needed1.length() > 0){
                            needed1 = needed1.substring(0, needed1.length()-2);
                            needed_field.setValue(needed1);
                        }
                    
                    TextInput.Builder ilvl_field = TextInput.create("minimum_ilvl", "Specify minimum item level", TextInputStyle.SHORT)
                        .setValue(minimum_ilvl)
                        .setPlaceholder("Specify item level");
                    
                    Modal modal = Modal.create("requirements_modal", "Set requirements")
                        .addActionRows(ActionRow.of(filled_field.build()), ActionRow.of(preferred_field.build()), ActionRow.of(needed_field.build()), ActionRow.of(ilvl_field.build()))
                        .build();
                    successTag = true;
                    return modal;
                default:
                    successTag = false;
                    return "Error: invalid subcommand!";
                    
            }

        }
        else{
            successTag = false;
            return permissionDenied();
        }
    }

    @Override
    public boolean shouldEphemeral() {
        return shouldEphemeral;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return actionRows;
    }
    
}
