package win.skademaskinen.listeners;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.interactions.component.ModalImpl;
import win.skademaskinen.commands.*;
import win.skademaskinen.utils.Colors;
import win.skademaskinen.utils.Shell;

public class CommandListener extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        Shell.printer(Colors.yellow("Received slash command:"));
        Shell.printer(Colors.green("Command:        ") + event.getName());
        if(event.getSubcommandName() != null){
            Shell.printer(Colors.green("Subcommand:     ") + event.getSubcommandName());
        }
        Shell.printer(Colors.blue("Option count:   ") + event.getOptions().size());
        for(OptionMapping option : event.getOptions()){
            String message = option.getName() + ":";
            while(message.length() < 16){
                message+=" ";
            }
            Shell.printer(Colors.green(message) + option.getAsString());
        }
        Shell.prompt();
        Command command = null;
        switch (event.getName().toLowerCase()) {
            case "ping":
                event.reply("Pong").queue();
                return;
            case "play":
                command = new PlayCommand(event);
                break;
            case "skip":
                command = new SkipCommand(event);
                break;
            case "queue":
                command = new QueueCommand(event);
                break;
            case "nowplaying":
                command = new NowPlayingCommand(event);
                break;
            case "leave":
            case "disconnect":
                command = new DisconnectCommand(event);
                break;
            case "pause":
                command = new PauseCommand(event);
                break;
            case "clear":
                command = new ClearCommand(event);
                break;
            case "roll":
                command = new RollCommand(event);
                break;
		    case "rolepicker":
                command = new RolePickerCommand(event);
				break;
		    case "version":
                command = new VersionCommand(event);
				break;
            case "team":
                command = new TeamCommand(event);
                break;
            case "spawnmessage":
                command = new SpawnMessageCommand(event);
                break;
            case "editmessage":
                command = new EditMessageCommand(event);
                break;
            case "message":
                command = new MessageCommand(event);
                break;
            case "requirements":
                command = new RequirementsCommand(event);
                break;
            case "poll":
                command = new PollCommand(event);
                break;
            case "featurerequest":
                command = new FeatureRequestCommand(event);
                break;
        }
        if(command != null){
            Object result = command.execute();
            if(result.getClass().equals(ModalImpl.class)){
                event.replyModal((Modal)result).queue();
            }
            else if(result.getClass().equals(MessageEmbed.class)){
                ReplyCallbackAction callback = event.replyEmbeds((MessageEmbed)result).setEphemeral(command.shouldEphemeral());
                for(ActionRow actionRow : command.getActionRows()){
                    callback.addActionRow(actionRow.getComponents());
                }
                callback.queue();
            }
            else if(result.getClass().equals(String.class)){
                ReplyCallbackAction callback = event.reply((String)result).setEphemeral(command.shouldEphemeral());
                for(ActionRow actionRow : command.getActionRows()){
                    callback.addActionRow(actionRow.getComponents());
                }
                callback.queue();
            }
            else if(result.getClass().equals(MessageCreateData.class)){
                ReplyCallbackAction callback = event.reply((MessageCreateData)result).setEphemeral(command.shouldEphemeral());
                for(ActionRow actionRow : command.getActionRows()){
                    callback.addActionRow(actionRow.getComponents());
                }
                callback.queue();
            }
        }
        else{
            event.reply("invalid command!").setEphemeral(true).queue();
        }
    }

}
