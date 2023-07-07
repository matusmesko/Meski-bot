package sk.meski;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import sk.meski.antibot.AntiBotVerification;
import sk.meski.tickets.TicketListeners;
import sk.meski.verify.VerifyListener;

import java.util.Arrays;

public class Main {

    private static JDA jda;


    public static void main(String[] args) throws InterruptedException{
        String botToken = "MTEyNTkzMDc3OTE5MzY1NTUwNg.GQQvc-.Z6mRzDAgG7KPdwHljUeuKWO01fqSHGBOaTtvLw";
        jda = JDABuilder.createDefault(botToken)
                .setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("v1.0"))
                .build().awaitReady();

        jda.addEventListener(new TicketListeners());
        jda.addEventListener(new VerifyListener());
        jda.addEventListener(new AntiBotVerification());

    }

    public static JDA getJda() {
        return jda;
    }
}
