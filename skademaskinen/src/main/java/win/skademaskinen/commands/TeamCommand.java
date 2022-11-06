package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import win.skademaskinen.WorldOfWarcraft.RaidTeamManager;

public class TeamCommand implements Command{
    private boolean successTag = false;
    private boolean shouldEphemeral = false;
    private Member author;
    private SlashCommandInteractionEvent event;
    private Guild guild;
    private List<ActionRow> actionRows = new ArrayList<>();

    public TeamCommand(SlashCommandInteractionEvent event){
        this.event = event;
        author = event.getMember();
        guild = event.getGuild();
    }


    @Override
    public String build() {
        return log(null, successTag);
    }

    @Override
    public Object run() {
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            switch(event.getSubcommandName()){
                case "add":
                    RaidTeamManager.addRaider(event.getOption("name").getAsString(), 
                        event.getOption("server").getAsString(), 
                        event.getOption("role").getAsString(), 
                        event.getOption("raider").getAsMember().getId(), 
                        guild);
                    shouldEphemeral = true;
                    return "Successfully added raider to the team!";
                case "remove":
                    Member member = event.getOption("raider").getAsMember();
                    RaidTeamManager.removeRaider(member);
                    shouldEphemeral = true;
                    return "Successfully removed raider from the raid team!";
                case "form":
                    MessageEmbed embed = new EmbedBuilder()
                        .setTitle("Apply to The Nut Hut raid team!")
                        .setDescription("Hi, here you can apply to the raid team!\nYou will receive a pop-up form to add your character's details.")
                        .setImage("https://cdn.discordapp.com/attachments/642853163774509116/922532262459867196/The_nut_hut.gif")
                        .build();
                    actionRows.add(ActionRow.of(Button.primary("apply_button", "Apply here!")));
                    return embed;
                case "update":
                    RaidTeamManager.update(guild);
                    return "Updated raid team";
                default:
                    return "Error: failed to parse subcommand!";
            }
        }
        else{
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
