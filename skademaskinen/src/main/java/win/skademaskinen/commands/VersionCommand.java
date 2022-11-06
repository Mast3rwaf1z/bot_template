package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class VersionCommand implements Command{
    private boolean successTag = false;
    private Member author;
    private Guild guild;
    private String[] additions = {
        "Added success tags",
        "Added args to logging",
        "Made version command easier to update"
    };

    public VersionCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public String run() {
        String msg = "**Changelog**\n```\n";
        for(String addition : additions){
            msg+=addition+"\n";
        }
        successTag = true;
        return msg+"```";
    }

    @Override
    public boolean shouldEphemeral() {
        return true;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return new ArrayList<>();
    }
    
}
