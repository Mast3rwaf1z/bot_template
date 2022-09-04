package win.skademaskinen;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SelectMenuListener extends ListenerAdapter{
	final private String[] colors = {"blue", "green", "gray", "yellow", "white", "orange", "red", "purple", "pink", "darkgreen"};
	final private String[] years = {"2019", "2020", "2021", "2022"};

	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		switch(event.getComponentId()){
			case "color_menu":
				Role role = guild.getRolesByName(event.getValues().get(0), true).get(0);
				for(String color : colors){
					if(member.getRoles().contains(guild.getRolesByName(color, true).get(0))){
						guild.removeRoleFromMember(member, guild.getRolesByName(color, true).get(0)).queue();
					}
				}
				guild.addRoleToMember(member, role).queue();
				break;

			case "year_menu":
				Role role1 = guild.getRolesByName("comtek-"+event.getValues().get(0), true).get(0);
				for(String year : years){
					if(member.getRoles().contains(guild.getRolesByName("comtek-"+year, true).get(0))){
						guild.removeRoleFromMember(member, guild.getRolesByName("comtek-"+year, true).get(0)).queue();
					}
				}
				guild.addRoleToMember(member, role1).queue();
				break;
			case "games_menu":
				guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1015709321113378917")).queue();
				guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1015709678338060349")).queue();
				guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1015964640171270206")).queue();
				for(String value : event.getValues()){
					if(value.equals("0")){
						guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1015709321113378917")).queue();
						guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1015709678338060349")).queue();
						guild.removeRoleFromMember(event.getMember(), guild.getRoleById("1015964640171270206")).queue();
						event.reply("Successfully cleared games roles").setEphemeral(true).queue();
						return;
					}
					else{
						guild.addRoleToMember(event.getMember(), guild.getRoleById(value)).queue();
					}
				}
				break;
		}
		event.deferEdit().queue();
	}
}
