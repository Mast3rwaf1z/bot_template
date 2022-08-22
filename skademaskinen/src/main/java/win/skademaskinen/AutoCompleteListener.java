package win.skademaskinen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

public class AutoCompleteListener extends ListenerAdapter{
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event){
        switch(event.getName()){
            case "apply":
            case "addraider":
                if(event.getFocusedOption().getName().equalsIgnoreCase("role")){
                    String[] choices = {"Tank", "Healer", "Ranged Damage", "Melee Damage"};
                    List<Command.Choice> options = Stream.of(choices)
                        .filter(choice -> choice.startsWith(event.getFocusedOption().getValue()))
                        .map(choice -> new Command.Choice(choice, choice))
                        .collect(Collectors.toList());
                    event.replyChoices(options).queue();
                }
                else if(event.getFocusedOption().getName().equalsIgnoreCase("server")){
                    String[] choices = {"argent-dawn"};
                    List<Command.Choice> options = Stream.of(choices)
                        .filter(choice -> choice.startsWith(event.getFocusedOption().getValue()))
                        .map(choice -> new Command.Choice(choice, choice))
                        .collect(Collectors.toList());
                    event.replyChoices(options).queue();
                }

                break;
        }
    }
}
