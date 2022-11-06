package win.skademaskinen.listeners;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import win.skademaskinen.utils.Colors;
import win.skademaskinen.utils.Shell;

public class MessageListener extends ListenerAdapter{
    
    public void onMessageReceived(MessageReceivedEvent event){
        Shell.printer(Colors.yellow("Message received:"));
        if(event.isFromGuild()){
            Shell.printer(Colors.green("Server:                 ") + event.getGuild().getName());
        }
        Shell.printer(Colors.green("Channel:                ") + event.getChannel().getName());
        Shell.printer(Colors.green("Author:                 ") + event.getAuthor().getName());
        Shell.printer(Colors.green("Message:                ") + event.getMessage().getContentDisplay());
        Shell.printer(Colors.green("Number of attachments:  ") + event.getMessage().getAttachments().size());
        for(Attachment url : event.getMessage().getAttachments()){
            Shell.printer(Colors.green("Attachment:             ") + url.getUrl());
        }
        Shell.prompt();
    }
    
}
