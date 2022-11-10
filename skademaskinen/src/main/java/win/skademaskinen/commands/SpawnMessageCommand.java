package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class SpawnMessageCommand implements Command{
    private boolean successTag = false;
    private List<ActionRow> actionRows = new ArrayList<>();
    private Member author;
    private Guild guild;

    public SpawnMessageCommand(SlashCommandInteractionEvent event){
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
            actionRows.add(ActionRow.of(Button.success(buttonIdBuilder("FinishButton", ""), "Finish"), 
            Button.secondary(buttonIdBuilder("SetTitlebutton", ""), "Set Title"),
            Button.secondary(buttonIdBuilder("SetDescriptionButton", ""), "Set Description"),
            Button.secondary(buttonIdBuilder("AddFieldButton", ""), "Add Field"),
            Button.secondary(buttonIdBuilder("AddImageButton", ""), "Add Image")));
            actionRows.add(ActionRow.of(Button.danger(buttonIdBuilder("clear_embed", ""), "Clear")));
            successTag = true;
            return new EmbedBuilder().setTitle("empty embed").build();
        }
        else{
            successTag = false;
            return permissionDenied();
        }
    }

    @Override
    public boolean shouldEphemeral() {
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return actionRows;
    }

    public static Object buttonHandler(ButtonInteractionEvent event){
        TextInput messageId = TextInput.create("message_id", "Id of the message being edited, DO NOT EDIT", TextInputStyle.SHORT).setValue(event.getMessageId()).build();
        String id = event.getButton().getId().split("::")[1].split(" ")[0];
        if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            switch(id){
                case "FinishButton":
                    event.getMessage().editMessageComponents().queue();
                    return "Successfully edited embed";
                case "SetTitleButton":
                    TextInput titleInput = TextInput.create("title_input", "New Title", TextInputStyle.SHORT).build();
                    Modal titleModal = Modal.create("title_modal", "Set title").addActionRows(ActionRow.of(titleInput), ActionRow.of(messageId)).build();
                    return titleModal;
                case "SetDescriptionButton":
                    TextInput descriptionInput = TextInput.create("description_input", "New description", TextInputStyle.PARAGRAPH).build();
                    Modal descriptionModal = Modal.create("description_modal", "Set description").addActionRows(ActionRow.of(descriptionInput), ActionRow.of(messageId)).build();
                    return descriptionModal;
                case "AddFieldButton":
                    TextInput inline = TextInput.create("inline_input", "inline?", TextInputStyle.SHORT).setValue("yes").build();
                    TextInput title = TextInput.create("field_title", "Field title", TextInputStyle.SHORT).build();
                    TextInput body = TextInput.create("field_body", "Field Body", TextInputStyle.PARAGRAPH).build();
                    Modal fieldModal = Modal.create("field_modal", "Add field").addActionRows(ActionRow.of(inline), ActionRow.of(title), ActionRow.of(body), ActionRow.of(messageId)).build();
                    return fieldModal;
                case "AddImageButton":
                    TextInput url = TextInput.create("url_input", "Image url", TextInputStyle.SHORT).build();
                    Modal imageModal = Modal.create("image_modal", "Set image").addActionRows(ActionRow.of(url), ActionRow.of(messageId)).build();
                    return imageModal;
                case "ClearButton":
                    event.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("Empty embed").build()).queue();
                    return "Cleared embed";
                default:
                    return "Error: invalid button id!";
            }

        }
        else{
            return "Permission denied! you are not an administrator!";
        }
    }
    
}
