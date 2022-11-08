package win.skademaskinen.listeners;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import win.skademaskinen.musicbot.MusicBot;
import win.skademaskinen.utils.Colors;
import win.skademaskinen.utils.Config;

public class SelectMenuListener extends ListenerAdapter{
	
	@SuppressWarnings("unchecked")
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event){
		String menu_id = event.getComponentId();
		List<String> values = event.getValues();
		Guild guild = event.getGuild();
		EmbedBuilder builder = new EmbedBuilder();
		HashMap<String, String> types = new HashMap<String, String>();
		types.put("pve", "776204665524191232");
		types.put("pvp", "776207123515441162");
		HashMap<String, String> roles = new HashMap<String, String>();
		roles.put("tank", "776184488077950977");
		roles.put("healer", "776184278832381962");
		roles.put("dps", "776184402132336661");
		HashMap<String, String> other_games = new HashMap<String, String>();
		other_games.put("amongus", "777502866377015326");
		other_games.put("minecraft", "777503152605364248");
		other_games.put("terraria", "777502989304725505");
		other_games.put("leagueoflegends", "777539954195562497");
		other_games.put("fromsoftgames", "777623985851465808");
		other_games.put("rockstargames", "847212557404340224");
		other_games.put("blizzardgames", "854792211563413515");
		other_games.put("eagames", "854791884332335185");
		other_games.put("ubisoftgames", "854795088008708136");
		other_games.put("squareenixgames", "867696118498721842");
		other_games.put("nintendogames", "916271612498690049");
		HashMap<String, String> misc = new HashMap<String, String>();
		misc.put("mountwhore", "776187232918568980");
		misc.put("memedealer", "776726601874014218");
		misc.put("artist", "785636073040642048");
		misc.put("nsfw", "970614921354174474");
		ArrayList<String> added_roles = new ArrayList<String>();
		switch(menu_id){
		case "type_menu":
			for(String role : types.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(types.get(role))).queue();
					added_roles.add(guild.getRoleById(types.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(types.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
			
		case "role_menu":
			for(String role : roles.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(roles.get(role))).queue();
					added_roles.add(guild.getRoleById(roles.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(roles.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
			
		case "other_games_menu":
			for(String role : other_games.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(other_games.get(role))).queue();
					added_roles.add(guild.getRoleById(other_games.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(other_games.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;
		case "misc_menu":
			for(String role : misc.keySet()){
				if(values.contains(role)){
					guild.addRoleToMember(event.getMember(), guild.getRoleById(misc.get(role))).queue();
					added_roles.add(guild.getRoleById(misc.get(role)).getName());
				}
				else{
					guild.removeRoleFromMember(event.getMember(), guild.getRoleById(misc.get(role))).queue();
				}
			}
			builder.setTitle("Confirmation message: Roles in category");
			for(String role : added_roles){
				builder.appendDescription(role + "\n");
			}
			event.replyEmbeds(builder.build()).setEphemeral(true).queue();
			break;

		case "poll_menu":
			String pollId = event.getMessageId();
			String key = event.getMember().getId();
			ArrayList<String> options = new ArrayList<String>();
			for(SelectOption option : event.getSelectedOptions()){
				options.add(option.getValue());
			}
				try {
					JSONObject polls = Config.readJSON("polls.json");
					if(!polls.has(pollId)){
						polls.put(pollId, new HashMap<>());
					}
					HashMap<String, ArrayList<String>> poll = (HashMap<String, ArrayList<String>>) polls.get(pollId);
					poll.put(key, options);
					polls.put(pollId, poll);
					try(FileWriter writer = new FileWriter("polls.json")){
						writer.write(polls.toString(4));
						writer.close();
					}
					event.deferEdit().queue();

				} catch (IOException e) {
					Colors.exceptionHandler(e);
				}
			
			break;
			case "playlist":
				event.deferReply().queue();
				if(!MusicBot.getBots().containsKey(guild)){
					MusicBot.getBots().put(guild, new MusicBot(event.getMember().getVoiceState().getChannel().asVoiceChannel()));
				}
				MusicBot.getBots().get(guild).play(event.getValues().get(0), event.getHook());
				break;
		}
	}
}
