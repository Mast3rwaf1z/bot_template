package win.skademaskinen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;



public class DatabaseHandler {
    Connection connection;
    private static DatabaseHandler activeHandler;
    DatabaseHandler() throws ClassNotFoundException, SQLException, IOException, ParseException{
        JSONObject config = (JSONObject) Config.getConfig().get("database");

        System.out.println("Connecting to mysql...");
        connection = DriverManager.getConnection(config.get("database").toString(), config.get("username").toString(), config.get("password").toString());
        System.out.println("Connected to mysql!");
    }

    void close() throws SQLException{
        connection.close();
    }

    public ResultSet getTable(String table) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from " + table + ";");
        return resultSet;
    } 

    public static void registerHandler() throws ClassNotFoundException, SQLException, IOException, ParseException{
        activeHandler = new DatabaseHandler();
    }
    
    public static DatabaseHandler getHandler(){
        return DatabaseHandler.activeHandler;
    }
}
