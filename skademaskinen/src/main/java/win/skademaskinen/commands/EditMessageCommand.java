package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class EditMessageCommand implements Command{
    private boolean successTag = false;
    private Member author;
    private Guild guild;
    private SlashCommandInteractionEvent event;
    private boolean shouldEphemeral;

    public EditMessageCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
        this.event = event;
    }

    @Override
    public String build() {
        return log("author: "+author.getUser().getAsTag()+" server: "+guild.getName(), successTag);
    }

    @Override
    public Object run() {
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            EmbedBuilder builder = new EmbedBuilder();
            boolean inline = false;
            String fieldname = "";
            Message message = guild.getTextChannelById(event.getMessageChannel().getId()).getHistoryAround(event.getOption("messageid").getAsString(), 1).complete().getMessageById(event.getOption("messageid").getAsString());
            if(event.getOption("inline") != null){
                inline = event.getOption("inline").getAsBoolean();
            }
            if(event.getOption("fieldname") != null){
                fieldname = event.getOption("fieldname").getAsString();
            }
            MessageEmbed embed = message.getEmbeds().get(0);
            builder.setTitle(embed.getTitle());
            builder.setDescription(embed.getDescription());
            for(Field field : embed.getFields()){
                builder.addField(field);
            }
            for(OptionMapping option : event.getOptions()){
                switch(option.getName()){
                    case "description":
                        builder.setDescription(option.getAsString());
                        break;
                    case "title":
                        builder.setTitle(option.getAsString());
                        break;
                    case "field":
                        builder.addField(fieldname, option.getAsString(), inline);
                        break;
                    case "imageurl":
                        builder.setImage(option.getAsString());
                        break;
                    }
                }
            message.editMessageEmbeds(builder.build()).queue();
            shouldEphemeral = true;
            successTag = true;
            return "Successfully edited message";

        }
        else{
            successTag = false;
            return permissionDenied();
        }
    }

    @Override
    public boolean shouldEphemeral() {
        return shouldEphemeral;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return new ArrayList<>();
    }
    
}
