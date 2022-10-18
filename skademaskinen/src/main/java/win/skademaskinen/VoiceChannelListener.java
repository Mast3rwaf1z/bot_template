package win.skademaskinen;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceChannelListener extends ListenerAdapter{

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) return;
		if(event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().size() == 1 && CommandListener.bots.containsKey(event.getGuild())){
			CommandListener.bots.get(event.getGuild()).disconnect();
		}
	}
}
