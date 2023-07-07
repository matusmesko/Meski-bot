package sk.meski.verify;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import sk.meski.Main;

import java.awt.*;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class VerifyListener extends ListenerAdapter {

    // Setup command
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        TextChannel textChannel = event.getGuild().getTextChannelById("1126134963994103828");
        if (args[0].equalsIgnoreCase( "+setup")){
            String roles = String.valueOf(event.getMember().getRoles());
            if (roles.contains("Developer")) {
                if (textChannel != null) {
                    if (event.getMessage().getChannel() == textChannel) {
                        List<Button> buttons = new ArrayList<>();
                        StringSelectMenu menu = StringSelectMenu.create("verifyMenu")
                                .addOptions(SelectOption.of("Meski Xray", "meskixray").withDescription("Meski Xray Premium Plugin"))
                                .addOptions(SelectOption.of("None", "none"))
                                .build();
                        textChannel.sendMessageEmbeds(embedVerify().build())
                                .setActionRow(menu)
                                .queue();
                    }
                }
            }
        }
    }

    //String menu event
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("verifyMenu")) {
            if (event.getValues().get(0).equals("meskixray")) {
                event.replyModal(verifyModal()).queue();
            }else {
                return;
            }
        }
    }

    // Modal object
    public Modal verifyModal() {
        TextInput nick = TextInput.create("nick", "Nickname", TextInputStyle.SHORT)
                .setPlaceholder("Your ingame nickname")
                .setMinLength(1)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build();


        TextInput mail = TextInput.create("mail", "Email", TextInputStyle.SHORT)
                .setPlaceholder("Your email")
                .setMinLength(5)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build();

        TextInput tebexID = TextInput.create("tebex", "Tebex ID", TextInputStyle.SHORT)
                .setPlaceholder("Your tebex purchase ID")
                .setMinLength(20)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build();

        Modal modal = Modal.create("verifysystemMeskiXray", "Verify System")
                .addActionRow(nick)
                .addActionRow(mail)
                .addActionRow(tebexID)
                .build();
        return modal;
    }


    // Modal event
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        TextChannel textChannel = event.getGuild().getTextChannelById("1126244000047833169");
        if (event.getModalId().equals("verifysystemMeskiXray")) {
            String mail = event.getValue("mail").getAsString();
            String tebexid = event.getValue("tebex").getAsString();
            String nick = event.getValue("nick").getAsString();
            User user = event.getUser();
            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("roleRequestMeskiXrayAccept", "Accept request"));
            buttons.add(Button.danger("roleRequestMeskiXrayDecline", "Decline request"));
            textChannel.sendMessageEmbeds(embedRoleRequest(event.getMember(),"Meski Xray", nick, mail, tebexid, user).build()).setActionRow(buttons).queue();
            event.reply("Success! If you are customer you will receive role as soon as possible.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();

        /*
            DECLINE BUTTOM
         */
        if (event.getButton().getId().equals("roleRequestMeskiXrayDecline")) {
            event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                List<MessageEmbed> embeds = message.getEmbeds();
                if (embeds.isEmpty()) {
                    event.reply("ERROR: Embed message is null").setEphemeral(true).queue();
                    return;
                }
                MessageEmbed oldEmbed = embeds.get(0);
                List<MessageEmbed.Field> fields = oldEmbed.getFields();
                /*
                    SENDING PRIVATE MESSAGE
                 */
                Long userID = 0L;
                for (MessageEmbed.Field field: fields) {
                    if (field.getName().equals("User ID")) {
                        userID = Long.valueOf(field.getValue());
                        Main.getJda().retrieveUserById(userID).queue(user -> {
                            sendPrivateEmbedMessage(user, DMDeclineVerify("Meski Xray").build());
                        });
                    }
                }
                /*
                    CHANGING COLOR
                 */
                EmbedBuilder newEmbedBuilder = new EmbedBuilder(oldEmbed);
                newEmbedBuilder.setColor(Color.RED);
                message.editMessageEmbeds(newEmbedBuilder.build()).queue();
            });
            /*
                REMOVING BUTTONS
             */
            Message embedMessage = event.getMessage();
            List<ActionRow> actionRows = embedMessage.getActionRows();
            actionRows.clear();
            embedMessage.editMessageComponents(actionRows).queue();
        }


        /*
            ACCEPT BUTTON
         */

        if (event.getButton().getId().equals("roleRequestMeskiXrayAccept")) {
            event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                List<MessageEmbed> embeds = message.getEmbeds();
                if (embeds.isEmpty()) {
                    event.reply("ERROR: Embed message is null").setEphemeral(true).queue();
                    return;
                }
                MessageEmbed oldEmbed = embeds.get(0);
                List<MessageEmbed.Field> fields = oldEmbed.getFields();
                /*
                    SENDING PRIVATE MESSAGE AND ASSIGN THE ROLE
                 */
                Long userID = 0L;
                for (MessageEmbed.Field field: fields) {
                    if (field.getName().equals("User ID")) {
                        userID = Long.valueOf(field.getValue());
                        Main.getJda().retrieveUserById(userID).queue(user -> {
                            Role role = getRoleById(guild, "1125953421971828756");
                            guild.addRoleToMember(user, role).queue();
                            sendPrivateEmbedMessage(user, DMAccepptVerify("Meski Xray").build());
                        });
                    }
                }
                /*
                    CHANGING COLOR
                 */
                EmbedBuilder newEmbedBuilder = new EmbedBuilder(oldEmbed);
                newEmbedBuilder.setColor(Color.GREEN);
                message.editMessageEmbeds(newEmbedBuilder.build()).queue();
            });
            /*
                REMOVING BUTTONS
             */
            Message embedMessage = event.getMessage();
            List<ActionRow> actionRows = embedMessage.getActionRows();
            actionRows.clear();
            embedMessage.editMessageComponents(actionRows).queue();
        }

    }
    public EmbedBuilder embedRoleRequest(Member member, String pluginName, String nickname, String mail, String tebex, User user) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setTitle("Role Request: " + pluginName);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setThumbnail(member.getEffectiveAvatarUrl());
        embedBuilder.addField("Nickname", nickname, true);
        embedBuilder.addField("Mail", mail, true);
        embedBuilder.addField("Tebex ID", tebex, false);
        embedBuilder.addField("User ID", user.getId(), true);
        embedBuilder.addField("Member ID", member.getId(), true);
        embedBuilder.setAuthor(user.getEffectiveName());
        return embedBuilder;
    }

    public EmbedBuilder embedVerify () {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor (Color.RED);
        embedBuilder.setTitle("Verify system");
        embedBuilder.setDescription("Please choose what plugin and give credentials");

        return embedBuilder;
    }

    public EmbedBuilder DMDeclineVerify (String pluginName) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor (Color.RED);
        embedBuilder.setTitle("Verify system");
        embedBuilder.setThumbnail("https://cdn.discordapp.com/attachments/812446574717173770/1126278655589482596/pngegg_2.png");
        embedBuilder.addField("Plugin", pluginName, true);
        embedBuilder.addField("Status", "Rejected", true);
        embedBuilder.addField("Reason", "Your role application was rejected because we couldn't find any information that you made a payment. If it's an error, contact me in the DM <@698176934294847568>", false);
        embedBuilder.setTimestamp(Instant.now());

        return embedBuilder;
    }

    public EmbedBuilder DMAccepptVerify (String pluginName) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor (Color.GREEN);
        embedBuilder.setTitle("Verify system");
        embedBuilder.setThumbnail("https://cdn.discordapp.com/attachments/812446574717173770/1126279979479597086/pngegg_3.png");
        embedBuilder.addField("Plugin", pluginName, true);
        embedBuilder.addField("Status", "Success", true);
        embedBuilder.addField("Reason", "We have found your payment information and you will be assigned a role", false);
        embedBuilder.setTimestamp(Instant.now());

        return embedBuilder;
    }


    public void sendMessage(User user, String content) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(content))
                .queue();
    }

    public static User getUserByName(Guild guild, String name) {
        // Retrieve members with the specified name
        List<Member> members = guild.getMembersByName(name, true);

        // Check if any members were found
        if (!members.isEmpty()) {
            Member member = members.get(0); // Get the first member with the name
            return member.getUser();
        }

        return null; // No matching users found
    }

    public static void sendPrivateEmbedMessage(User user, MessageEmbed embed) {
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(embed))
                .queue();
    }


    public static Role getRoleById(Guild guild, String roleId) {
        return guild.getRoleById(roleId);
    }
}
