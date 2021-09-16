import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

public class BotStartup {

    /**
     *
     * @param args nødvendig i Main metoden
     * @throws LoginException   Kaster en LoginException hvis den ikke kan connecte til Discord
     */
    public static void main(String[] args) throws LoginException {

        Queries.createTables();

        JDABuilder jda = JDABuilder.createDefault("Discord_Bot_Token");
        jda.setActivity(Activity.playing("!help for help"));
        jda.setStatus(OnlineStatus.ONLINE); // Sets status to online
        jda.addEventListeners(new Commands()); // Adds event listener from Commands
        jda.build(); // Builds the bot
        jda.setAutoReconnect(true);
    }
}
