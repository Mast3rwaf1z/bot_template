package win.skademaskinen;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceChannelListener extends ListenerAdapter{

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) return;
		if(event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1 && CommandListener.bots.containsKey(event.getGuild())){
			CommandListener.bots.get(event.getGuild()).disconnect();
		}
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) return;
		if(event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1 && CommandListener.bots.containsKey(event.getGuild())){
			CommandListener.bots.get(event.getGuild()).disconnect();
		}
	}

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) return;
		if(event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1 && CommandListener.bots.containsKey(event.getGuild())){
			CommandListener.bots.get(event.getGuild()).disconnect();
		}
	}
}
