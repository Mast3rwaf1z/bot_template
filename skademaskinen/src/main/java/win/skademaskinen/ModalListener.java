package win.skademaskinen;

import java.net.MalformedURLException;
import java.net.URL;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalListener extends ListenerAdapter{
    public void onModalInteraction(ModalInteractionEvent event){
        Member author = event.getMember();
        Guild guild = event.getGuild();
        

        switch(event.getModalId()){
            case "add more":
                event.deferReply().queue();
                if (author.getVoiceState().inAudioChannel() && CommandListener.bots.containsKey(guild)) {
                    MusicBot bot = CommandListener.bots.get(guild);
                    if(!guild.getSelfMember().getVoiceState().inAudioChannel()){
                        bot.connectToVoiceChannel(author.getVoiceState().getChannel());
                    }
                    try{
                        new URL(event.getValue("url").getAsString());
                        bot.play(event.getValue("url").getAsString().strip(), event.getHook());
                    }
                    catch(MalformedURLException e){
                        bot.play("ytsearch:"+event.getValue("url").getAsString(), event.getHook());
                    }
                }
                else{
                    CommandListener.bots.put(guild, new MusicBot(event.getMember().getVoiceState().getChannel()));
                    MusicBot bot = CommandListener.bots.get(guild);
                    try{
                        new URL(event.getValue("url").getAsString());
                        bot.play(event.getValue("url").getAsString().strip(), event.getHook());
                    }
                    catch(MalformedURLException e){
                        bot.play("ytsearch:"+event.getValue("url").getAsString(), event.getHook());
                    }
                }
                break;
        }
    }
    
}
