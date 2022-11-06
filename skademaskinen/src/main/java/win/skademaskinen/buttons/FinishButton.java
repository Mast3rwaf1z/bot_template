package win.skademaskinen.buttons;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class FinishButton implements Button {
    private boolean successTag = false;
    private String id = "SpawnMessageCommand::"+this.getClass().getSimpleName();
    private Member author;
    private Guild guild;
    private ButtonInteractionEvent event;

    public FinishButton(ButtonInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
        this.event = event;
    }

    @Override
    public String build() {
        // TODO Auto-generated method stub
        return log("buttonId: "+id+" author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public String run() {
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            event.getMessage().editMessageComponents().queue();
            successTag = true;
            return "Successfully edited message!";
        }
        else{
            successTag = false;
            return permissionDenied();
        }
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
