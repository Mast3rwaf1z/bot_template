package win.skademaskinen;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildEventListener extends ListenerAdapter {
	
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		RaidTeamManager.removeRaider(event.getUser(), event.getGuild());
	}
}
