package sk.meski.tickets;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class TicketListeners extends ListenerAdapter {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        TextChannel textChannel = event.getGuild().getTextChannelById("1125929851422003252");
        if (args[0].equalsIgnoreCase( "+setup")){
            String roles = String.valueOf(event.getMember().getRoles());
            if (roles.contains("Developer")) {
                if (textChannel != null) {
                    if (event.getMessage().getChannel() == textChannel) {
                        List<Button> buttons = new ArrayList<>();

                        buttons.add(Button.secondary("meskixray","Meski Xray"));
                        buttons.add(Button.secondary("meskixraylite", "Meski Xray Lite"));
                        textChannel.sendMessageEmbeds(embedMessageCategory(event.getMember()).build()).setActionRow(buttons).queue();
                    }
                }
            }
        }
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        Guild guild = event.getGuild();
        int min = 1000;
        int max = 99999;
        int random = (int) Math.floor(Math.random() * (max - min + 1) + min);
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.danger("remove","Delete Channel"));
        if (event.getButton().getId().equals("meskixray")) {
            if (hasRole(event.getMember(), "Meski Xray")) {
                guild.createTextChannel("MeskiXray-" + event.getUser().getName() + "-" + random, guild.getCategoryById("1125952267443503246"))
                        .addPermissionOverride(event.getMember(), EnumSet. of (Permission.VIEW_CHANNEL),null)
                        .addPermissionOverride(guild.getPublicRole(), null, EnumSet. of (Permission. VIEW_CHANNEL))
                        .complete()
                        .sendMessageEmbeds(embedTicketWelcome("Meski Xray Support", event.getMember()).build())
                        .setActionRow(buttons)
                        .queue();
            }else {
                try {
                    sendMessage(event.getUser(), "Sorry but you are not the customer of Meski Xray plugin!");
                }catch (IllegalStateException ex) {

                }
            }
        }else if (event.getButton().getId().equals("remove")) {
            guild.getTextChannelById(event.getChannel().getId()).delete().queue();
        } else if (event.getButton().getId().equals("meskixraylite")) {
            guild.createTextChannel("MeskiXrayLite-" + event.getUser().getName() + "-" + random, guild.getCategoryById("1126132602152173688"))
                    .addPermissionOverride(event.getMember(), EnumSet. of (Permission.VIEW_CHANNEL),null)
                    .addPermissionOverride(guild.getPublicRole(), null, EnumSet. of (Permission. VIEW_CHANNEL))
                    .complete()
                    .sendMessageEmbeds(embedTicketWelcome("Meski Xray Lite Support", event.getMember()).build())
                    .setActionRow(buttons)
                    .queue();
        }
    }

    private EmbedBuilder embedTicketWelcome (String text, Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor (Color.BLUE);
        embedBuilder.setTitle ("Ticket System");
        embedBuilder.setDescription("We will help you soon <@"+member.getId()+">");
        embedBuilder.addField( "Category", text,  true);
        return embedBuilder;
    }

    public EmbedBuilder embedMessageCategory (Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor (Color.BLUE);
        embedBuilder.setTitle ("Ticket System");
        embedBuilder.setDescription("Create Ticket");

        return embedBuilder;
    }

    public boolean hasRole(Member member, String name) {
        return member.getRoles().stream().map(Role::getName).anyMatch(name::equalsIgnoreCase);
    }

    public void sendMessage(User user, String content) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }
}

