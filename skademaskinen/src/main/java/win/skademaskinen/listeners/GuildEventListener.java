package win.skademaskinen.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import win.skademaskinen.WorldOfWarcraft.RaidTeamManager;

public class GuildEventListener extends ListenerAdapter {
	
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		RaidTeamManager.removeRaider(event.getUser(), event.getGuild());
	}
}
