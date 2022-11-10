package win.skademaskinen.commands;

import java.util.List;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import win.skademaskinen.utils.Log;
import win.skademaskinen.utils.Loggable;

/*
 * Represents a command in the discord bot
 */
public interface Command extends Loggable{

    /*returns an object of type Modal, MessageEmbed or String*/
    default public Object execute(){
        Object result =  this.run();
        Log.appendLog(this);
        return result;
    }

    default public String permissionDenied(){
        return "Error: You are not an administrator! Contact Skademanden for more information";
    }

    public Object run();

    public boolean shouldEphemeral();

    public List<ActionRow> getActionRows();

    default public String buttonIdBuilder(String customId, String metadata){
        return getClass().getSimpleName()+"::"+customId+" "+metadata;
    }
    

}
