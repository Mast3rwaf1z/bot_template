package win.skademaskinen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoCompleteListener extends ListenerAdapter {
    final private String[] colors = {"blue", "green", "gray", "yellow", "orange", "red", "white", "purple", "pink", "darkgreen"};
	
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event){
        switch(event.getName()){
            case "color":
                String[] choices = colors;
                List<Command.Choice> options = Stream.of(choices)
                    .filter(choice -> choice.startsWith(event.getFocusedOption().getValue()))
                    .map(choice -> new Command.Choice(choice, choice))
                    .collect(Collectors.toList());
                event.replyChoices(options).queue();
                break;
        }
    }
}
