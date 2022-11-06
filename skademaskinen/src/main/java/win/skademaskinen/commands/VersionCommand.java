package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.interactions.components.ActionRow;

public class VersionCommand implements Command{
    private boolean successTag = false;
    private String msg = "**Changelog**\n```\ngeneral cleanup and restructuring of code\n```";

    @Override
    public String build() {
        return log(null, successTag);
    }

    @Override
    public String run() {
        return msg;
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
