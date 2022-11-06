package win.skademaskinen.buttons;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class SetTitleButton implements Button {
    private boolean successTag = false;
    private String id = "TeamCommand::"+this.getClass().getSimpleName();
    private Member author;
    private Guild guild;
    private ButtonInteractionEvent event;
    private TextInput messageId;

    public SetTitleButton(ButtonInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
        this.event = event;
        messageId = TextInput.create("message_id", "Id of the message being edited, DO NOT EDIT", TextInputStyle.SHORT).setValue(event.getMessageId()).build();
    }

    @Override
    public String build() {
        return log("buttonId: "+id+" author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Object run() {
        if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            TextInput titleInput = TextInput.create("title_input", "New Title", TextInputStyle.SHORT).build();
            Modal titleModal = Modal.create("title_modal", "Set title").addActionRows(ActionRow.of(titleInput), ActionRow.of(messageId)).build();
            return titleModal;
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
