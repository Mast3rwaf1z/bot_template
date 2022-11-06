package win.skademaskinen.buttons;

import java.util.List;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import win.skademaskinen.utils.Log;
import win.skademaskinen.utils.Loggable;

public interface Button extends Loggable {
    //id should be a variable formatted in this way: ParentClass::ButtonClass metadata
    //This is to be able to split the id and the metadata from the id

    default Object execute(){
        Object result = this.run();
        Log.appendLog(this);
        return result;
    }

    public Object run();
    
    public boolean shouldEphemeral();

    public List<ActionRow> getActionRows();
    
    default public String permissionDenied(){
        return "Error: You are not an administrator! Contact Skademanden for more information";
    }
}
