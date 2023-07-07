package sk.meski.antibot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AntiBotVerification extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        TextChannel textChannel = event.getGuild().getTextChannelById("1126899862856794142");
        if (args[0].equalsIgnoreCase("+setup")) {
            String roles = String.valueOf(event.getMember().getRoles());
            if (roles.contains("Developer")) {
                if (textChannel != null) {
                    if (event.getMessage().getChannel() == textChannel) {
                        textChannel.sendMessageEmbeds(embedVerify().build())
                                .queue(message -> message.addReaction(Emoji.fromUnicode("\u2705")).queue());
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        TextChannel textChannel = event.getGuild().getTextChannelById("1126899862856794142");

        if (event.getChannel().getId().equals(textChannel.getId())) {
            Guild guild = event.getGuild();
            Member member = event.getMember();

            Role role = guild.getRoleById("1126128905229709322");
            guild.addRoleToMember(member, role).queue();
        }
}

    public EmbedBuilder embedVerify () {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor (Color.GREEN);
        embedBuilder.setTitle("Verify yourself!");
        embedBuilder.setDescription("Click to reaction  to verify!");

        return embedBuilder;
    }

}
