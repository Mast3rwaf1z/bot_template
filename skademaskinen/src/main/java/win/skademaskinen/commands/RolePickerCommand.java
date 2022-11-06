package win.skademaskinen.commands;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public class RolePickerCommand implements Command {
    private boolean successTag = false;
    private Member author;
    private Guild guild;
    private List<ActionRow> actionRows = new ArrayList<>();

    public RolePickerCommand(SlashCommandInteractionEvent event){
        author = event.getMember();
        guild = event.getGuild();
    }

    @Override
    public String build() {
        return log(null, successTag);
    }

    @Override
    public Object run() {
        EmbedBuilder builder = new EmbedBuilder();
        if(author.hasPermission(Permission.ADMINISTRATOR)){
            builder.setTitle("Welcome to The Nut Hut");
            builder.setDescription(
                    "The World of Warcraft guild The Nut Hut - <Argent Dawn> welcomes you to our discord server!\nYou can find our rules in "
                            + guild.getTextChannelById("642853163774509116").getAsMention()
                            + "\nBelow you can choose the roles you need in this discord server!");
            builder.setImage("https://cdn.discordapp.com/attachments/642853163774509116/922532262459867196/The_nut_hut.gif");

            SelectMenu type_menu = SelectMenu.create("type_menu")
                .setPlaceholder("PvE and/or PvP")
                .setMinValues(0)
                .setMaxValues(2)
                .addOption("PvE", "pve")
                .addOption("PvP", "pvp")
                .build();
            actionRows.add(ActionRow.of(type_menu));

            SelectMenu role_menu = SelectMenu.create("role_menu")
                .setPlaceholder("Choose your role(s)")
                .setMinValues(0)
                .setMaxValues(3)
                .addOption("Tank", "tank", Emoji.fromCustom("Tank", 869171302307610695L, false))
                .addOption("Healer", "healer", Emoji.fromCustom("Healer", 869171419458707506L, false))
                .addOption("DPS", "dps", Emoji.fromCustom("Dps", 869171471992360990L, false))
                .build();
            actionRows.add(ActionRow.of(role_menu));

            SelectMenu other_games_menu = SelectMenu.create("other_games_menu")
                .setPlaceholder("Choose accces to other games channels")
                .setMaxValues(11)
                .setMinValues(0)
                //.addOption("Among Us", "amongus", Emoji.fromCustom("amongus", 777507568251043880L, false))
                .addOption("Minecraft", "minecraft", Emoji.fromCustom("minecraft", 777508556429459477L, false))
                //.addOption("Terraria", "terraria", Emoji.fromCustom("terraria", 777509181481549844L, false))
                .addOption("League of Legends", "leagueoflegends", Emoji.fromCustom("league", 852537658252984330L, false))
                .addOption("From Software Games", "fromsoftgames", Emoji.fromCustom("fromsoftware", 777624293948915773L, false))
                .addOption("Rockstar Games", "rockstargames", Emoji.fromCustom("Rockstar", 847213407681773578L, false))
                .addOption("Blizzard Games", "blizzardgames", Emoji.fromCustom("blizzard", 854794855968145409L, false))
                .addOption("EA Games", "eagames", Emoji.fromCustom("EA", 854794890218569738L, false))
                //.addOption("Ubisoft Games", "ubisoftgames", Emoji.fromCustom("Ubisoft", 854794796962676736L, false))
                .addOption("Square Enix Games", "squareenixgames", Emoji.fromCustom("SE", 867691313191977009L, false))
                .addOption("Nintendo Games", "nintendogames", Emoji.fromCustom("Nintendo", 916271940870762506L, false))
                .build();
            actionRows.add(ActionRow.of(other_games_menu));

            SelectMenu misc_menu = SelectMenu.create("misc_menu")
                .setPlaceholder("Choose misc roles")
                .setMaxValues(4)
                .addOption("Mount Whore", "mountwhore", Emoji.fromCustom("Panties", 652562519470374933L, false))
                .addOption("Meme Dealer", "memedealer", Emoji.fromCustom("Unicorndab", 645342104557584394L, false))
                .addOption("Artist", "artist")
                .addOption("NSFW", "nsfw", Emoji.fromCustom("lewd", 656973114793525258L, false))
                .build();
            actionRows.add(ActionRow.of(misc_menu));
            return builder.build();
        }
        else{
            return permissionDenied();
        }
    }

    @Override
    public boolean shouldEphemeral() {
        return false;
    }

    @Override
    public List<ActionRow> getActionRows() {
        return actionRows;
    }
    
}
