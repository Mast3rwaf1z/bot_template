package win.skademaskinen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class RaidTeamManager {
	static private String raidTeamMessageId = "987484728724705360";
	public static Map<Member, Character> getTeam(Guild guild) throws SQLException{
		//Name:	[id, main, class, spec, role, status, average ilvl, equipped ilvl]
		HashMap<Member, Character> team = new HashMap<>();
		DatabaseHandler databaseHandler = DatabaseHandler.getHandler();
		ResultSet raid_teamResultSet = databaseHandler.getTable("raid_team");
		while(raid_teamResultSet.next()){
			System.out.println(raid_teamResultSet.getString("id"));
			team.put(guild.retrieveMemberById(raid_teamResultSet.getString("id")).complete(), new Character(
				raid_teamResultSet.getString("main"), 
				raid_teamResultSet.getString("class"), 
				raid_teamResultSet.getString("specialization"), 
				raid_teamResultSet.getInt("role"), 
				raid_teamResultSet.getInt("status"), 
				raid_teamResultSet.getInt("average"), 
				raid_teamResultSet.getInt("equipped")));
		}
		return team;
	}

	static public String raidTeamMessageId(){
		return raidTeamMessageId;
	}
}
