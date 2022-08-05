package win.skademaskinen;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

public class ResultGetter {
	void run(SlashCommandInteractionEvent event) throws IOException, ParseException, RateLimitedException{
		Guild guild = event.getGuild();
		String message_id = event.getOption("messageid").getAsString();
		Message message = event.getTextChannel().getHistoryAround(message_id, 1).complete(true).getMessageById(message_id);
		JSONParser parser = new JSONParser();
		FileReader reader = new FileReader("polls.json");
		JSONObject polls = (JSONObject) parser.parse(reader);
		String out = "";
		HashMap<String, ArrayList<String>> result = new HashMap<>();
		JSONObject poll = (JSONObject) polls.get(message_id);
		System.out.println("results for poll #" + message.getId() + " with name: " + message.getEmbeds().get(0).getDescription());
		out += "results for poll #" + message.getId() + " with name: " + message.getEmbeds().get(0).getDescription() + "\n";
		for(Object reply_key : poll.keySet()){
			Member member = guild.retrieveMemberById(reply_key.toString()).complete();
			JSONArray replies = (JSONArray) poll.get(reply_key.toString());
			if (member == null) {
				continue;
			}
			System.out.println(member.getEffectiveName());
			out += member.getEffectiveName() +"\n";
			for(Object reply : replies){
				System.out.println("\t" + reply.toString());
				out += "\t" + reply.toString() + "\n";
				ArrayList<String> list = result.get(reply.toString());
				if(list == null){
					list = new ArrayList<>();
					list.add(member.getEffectiveName());
					result.put(reply.toString(), list);
				}
				else{
					result.get(reply.toString()).add(member.getEffectiveName());
				}
			}
		}
		System.out.println("Formatted results for poll #" + message_id);
		out += "Formatted results for poll #" + message_id + "\n";
		for(String key : result.keySet()){
			System.out.println(key);
			out += key + "\n";
			ArrayList<String> list = result.get(key);
			for(String element : list){
				System.out.println("\t" + element);
				out += "\t" + element + "\n";
			}
		}
		FileWriter writer = new FileWriter("out.txt");
		writer.write(out);
		writer.close();
		reader.close();
		event.getUser().openPrivateChannel().complete().sendFile(new File("out.txt")).queue();
		event.deferReply(true).queue();
	}
}
