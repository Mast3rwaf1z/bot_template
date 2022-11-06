package win.skademaskinen.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class RollCommand implements Command{
    private boolean successTag = false;
    private SlashCommandInteractionEvent event;

    public RollCommand(SlashCommandInteractionEvent event){
        this.event = event;
    }

    @Override
    public String build() {
        return log(null, successTag);
    }

    @Override
    public MessageEmbed run() {
        EmbedBuilder builder = new EmbedBuilder();
        ArrayList<String> entries = new ArrayList<>();
        for(OptionMapping option : event.getOptions()){
            entries.add(option.getAsString());
        }
        HashMap<String, Integer> results = new HashMap<String, Integer>();
        for(String entry : entries){
            int roll = (int) (Math.random()*100);
            results.put(entry, roll);
            builder.appendDescription("**"+entry+"**: "+roll+"\n");
        }
        int winnerValue = Collections.max(results.values());
        String winner = "";
        for(String key : results.keySet()){
            if(results.get(key).equals(winnerValue)){
                winner = key;
            }
        }
        builder.addField("", "**"+winner+"** has won the roll", false);
        builder.setColor(Color.blue);
        builder.setThumbnail("https://cdn.discordapp.com/attachments/692410386657574955/889818089066221578/dice.png");
        builder.setTitle("Rolls");
        return builder.build();

    }

    @Override
    public boolean shouldEphemeral() {
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return new ArrayList<>();
    }
    
}
