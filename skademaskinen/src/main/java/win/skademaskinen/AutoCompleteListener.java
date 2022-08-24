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
            case "team":
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
            case "requirements":
                switch(event.getFocusedOption().getName()){
                    case "type":
                        String[] choices = {"filled roles", "preferred roles", "needed classes"};
                        List<Command.Choice> options = Stream.of(choices)
                            .filter(choice -> choice.startsWith(event.getFocusedOption().getValue()))
                            .map(choice -> new Command.Choice(choice, choice))
                            .collect(Collectors.toList());
                        event.replyChoices(options).queue();
                        break;
                    case "value":
                        if(event.getOption("type").getAsString().equals("needed classes")){
                            String[] vchoices = {"warrior", "paladin", "hunter", "rogue", "priest", "shaman", "mage", "warlock", "monk", "druid", "demon hunter", "death knight"};
                            List<Command.Choice> voptions = Stream.of(vchoices)
                                .filter(vchoice -> vchoice.startsWith(event.getFocusedOption().getValue()))
                                .map(vchoice -> new Command.Choice(vchoice, vchoice))
                                .collect(Collectors.toList());
                            event.replyChoices(voptions).queue();
                        }
                        else{
                            String[] vchoices = {"tank", "healer", "ranged damage", "melee damage"};
                            List<Command.Choice> voptions = Stream.of(vchoices)
                                .filter(vchoice -> vchoice.startsWith(event.getFocusedOption().getValue()))
                                .map(vchoice -> new Command.Choice(vchoice, vchoice))
                                .collect(Collectors.toList());
                            event.replyChoices(voptions).queue();
                        }
                        break;
                }
        }
    }
}
