package win.skademaskinen.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import win.skademaskinen.musicbot.MusicBot;

public class VoiceChannelListener extends ListenerAdapter{

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) return;
		if(event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1 && MusicBot.getBots().containsKey(event.getGuild())){
			MusicBot.getBots().get(event.getGuild()).disconnect();
		}
	}
}
