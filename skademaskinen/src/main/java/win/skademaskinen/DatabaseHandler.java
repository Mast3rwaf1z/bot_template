package win.skademaskinen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;


public class DatabaseHandler {
    Connection connection;
    DatabaseHandler() throws ClassNotFoundException, SQLException, IOException, ParseException{
        JSONObject config = (JSONObject) Config.getConfig().get("database");

        connection = DriverManager.getConnection(config.get("database").toString(), config.get("username").toString(), config.get("password").toString());
    }

    public ResultSet getPoopsForGuild(Guild guild) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("select * from server_" + guild.getId() + ";");
        return result;
    }

    public void createPoopTable(Guild guild) throws SQLException{
        Statement statement = connection.createStatement();
        statement.execute("create table server_" + guild.getId() + "(id bigint, count int);");
    }

    void close() throws SQLException{
        connection.close();
    }

    public void addPoopToMember(Member member) throws SQLException {
        String guildId = member.getGuild().getId();
        String memberId = member.getId();
        Statement statement = connection.createStatement();
        int current = getPoopsForMember(member);
        current++;
        if(current == 1){
            statement.execute("insert into server_" + guildId + " values("+memberId+", "+current+");");
        }
        else{
            statement.execute("update server_"+ guildId + " set count="+current+ " where id="+memberId+";");
        }
        System.out.println("Successfully incremented poop value to" + current);
    }

    public int getPoopsForMember(Member member) throws SQLException{
        Statement statement = connection.createStatement();
        try{
            ResultSet result = statement.executeQuery("select count from server_" + member.getGuild().getId() + " where id="+member.getId()+";");
            result.next();
            return result.getInt(1);

        }
        catch(SQLException e){
            return 0;
        }
    }
    
}
