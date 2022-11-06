package win.skademaskinen.buttons;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ApplyButton implements Button{
    private boolean successTag = false;
    private String id = "TeamCommand::"+this.getClass().getSimpleName();
    private Member author;
    private Guild guild;

    public ApplyButton(ButtonInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log("buttonId: "+id+" author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Modal run() {
        TextInput name = TextInput.create("name", "Character name", TextInputStyle.SHORT)
                .setPlaceholder("Your character name")
                .build();
            TextInput server = TextInput.create("server", "Character server", TextInputStyle.SHORT)
                .setPlaceholder("Your character server, example: argent-dawn")
                .setValue("argent-dawn")
                .build();
            TextInput role = TextInput.create("role", "Your role", TextInputStyle.SHORT)
                .setPlaceholder("Healer, Tank, Ranged Damage or Melee Damage")
                .build();
            TextInput raidtimes = TextInput.create("raidtimes", "Wednesday and Sunday 19:30 - 22:30?", TextInputStyle.SHORT)
                .setPlaceholder("Can you raid with us? (yes/no)")
                .setValue("yes")
                .build();

            Modal modal = Modal.create("Application form", "application")
                .addActionRows(ActionRow.of(name), ActionRow.of(role), ActionRow.of(server), ActionRow.of(raidtimes))
                .build();
                successTag = true;
            return modal;
    }

    @Override
    public boolean shouldEphemeral() {
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return null;
    }
}
