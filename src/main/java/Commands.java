import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;




import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class Commands extends ListenerAdapter {

    /**
     * Modtager beskeder og returnere de nødvendige metodekald
     * @param event discord event, tjekker når noget sker
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {


        String[] args = event.getMessage().getContentRaw().split(" ", 4);
        if (event.getChannel().getId().equals("Channel_ID")){ //make sure to copy channel ID
            switch (args[0].toLowerCase(Locale.ROOT)){
                case "!help":
                    helpMetode(event);
                    break;
                case "!klandre":
                    klandreMetode(args, event);
                    break;
                case "!liste":
                    listeMetode(event);
                    break;
                case "!beggrundelser":
                    listeBegrundelser(event);
                    break;
                case "!anti":
                    antiKlandring(args, event);
                    break;
                case "!antiliste":
                    antiKlandringListe(event);
                    break;
                case "!betaltklandring":
                    betaltKlandring(args, event);
                    break;
                case "!betaltanti":
                    betaltAntiKlandring(args, event);
                    break;
                case "!executeorder66":
                    order66(event);
                    break;
            }
        }
    }

    /**
     * Commando til at slette alt fra alle tables
     * @param event det modtaget discord event
     */
    private void order66(GuildMessageReceivedEvent event) {

        SQL_Connection_Setup.SQLconnect();

        Queries.truncateAall();

        event.getChannel().sendMessage("Order 66").queue();

        SQL_Connection_Setup.SQLDisconnect();

    }

    /**
     * Metoden sletter personen efter !betaltanti fra AntiKlandring listen
     * @param args personen som har betalt
     * @param event det modtaget discord event
     */
    private void  betaltAntiKlandring(String[] args, GuildMessageReceivedEvent event) {

        SQL_Connection_Setup.SQLconnect();

        String personSomSlettes = args[1];

        Queries.betaltAntiKlandring(personSomSlettes);

        event.getChannel().sendMessage(personSomSlettes + " slettet fra AntiKlandring listen").queue();

        SQL_Connection_Setup.SQLDisconnect();

    }

    /**
     * Metoden sletter navnet efter !betaltklandring fra table 1 og 2 og sender en bekræftelse
     * @param args Navnet der blir slettet fra table 1 og 2
     * @param event det modtaget discord event
     */
    private void betaltKlandring(String[] args, GuildMessageReceivedEvent event) {

        SQL_Connection_Setup.SQLconnect();

        String personSomSlettes = args[1];

        Queries.betalKlandring(personSomSlettes);

        event.getChannel().sendMessage(personSomSlettes + " slettet fra Klandring listen").queue();

        SQL_Connection_Setup.SQLDisconnect();
    }

    /**
     * Metoden printer en embed med liste over hvad folk skylder fra antiKlandringer
     * @param event det modtaget discord event
     */
    private void antiKlandringListe(GuildMessageReceivedEvent event) {
        SQL_Connection_Setup.SQLconnect();
        try {

            EmbedBuilder antiEmbed = new EmbedBuilder();

            ResultSet antiListe = Queries.antiListe();

            antiEmbed.setTitle("Liste over hvor meget man skylder for antiKlandringer:");

            while (antiListe.next()){
                antiEmbed.addField(antiListe.getString(1), antiListe.getString(2) + "kr", false);
            }


            event.getChannel().sendMessage(antiEmbed.build()).queue();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        SQL_Connection_Setup.SQLDisconnect();
    }

    /**
     * Metoden tilføjer en person til antiklandrings listen og returnere en bekræftelse
     * @param args Stringen efter !anto
     * @param event det modtaget discord event
     */
    private void antiKlandring(String[] args, GuildMessageReceivedEvent event) {

        SQL_Connection_Setup.SQLconnect();

        Queries.antiKlandring(args[1].trim());

        event.getChannel().sendMessage(args[1].trim() + " tilfoejet/opdateret paa antiKlandring listen").queue();

        SQL_Connection_Setup.SQLDisconnect();
    }

    /**
     * printer en embed med liste over kommandoer. Jeg ved heller ikke hvorfor addBlankField skal have en parameter.
     * @param event det modtaget discord event
     */
    private void helpMetode(GuildMessageReceivedEvent event) {


        EmbedBuilder helpEmbed = new EmbedBuilder();

        helpEmbed.setTitle("En liste over Kommandoer");
        helpEmbed.addField("!klandre",
                "Klandre en person, foerste ord efter !Klandre er fra, andet er " +
                        "til, og alt efter er beggrundelsen", false);
        helpEmbed.addBlankField(false);
        helpEmbed.addField("!liste",
                "Returnere en liste over hvem der skylder hvad", false);
        helpEmbed.addBlankField(true);
        helpEmbed.addField("!beggrundelser",
                "Returnere en lang liste over alle klandringer indtil " +
                        "videre, nyeste foest", false);
        helpEmbed.addBlankField(true);
        helpEmbed.addField("!anti",
                "navnet efter \" anti \" deltager i antiklandring", false);
        helpEmbed.addBlankField(false);
        helpEmbed.addField("!antiliste",
                "Returnere en liste af hvad " +
                        "folk skylder for deltagelse i antiklandring", false);
        helpEmbed.addBlankField(true);
        helpEmbed.setAuthor("For more help press" +
                " see; ", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        helpEmbed.addField("!betaltklandring",
                "navnet efter !betaltklandring blir slaettet fra klandre listen" +
                        "og klandre beskrivelses listen (alle beskrivelserne hvor han er blevet klandret", false);
        helpEmbed.addBlankField(false);
        helpEmbed.addField("!betaltanti",
                "navnet efter !betaltanti blir slettet fra antiKlandring listen", false);
        event.getMessage().reply(helpEmbed.build()).queue();
    }

    /**
     * metode henter SQL tabel 2 og modtager data, printer ud i discord Embeds
     * @param event det modtaget discord event
     */
    private void listeBegrundelser(GuildMessageReceivedEvent event) {

        SQL_Connection_Setup.SQLconnect();

        try {

            ResultSet BeskrevetListe = Queries.KlandreBeskrivelser();

            EmbedBuilder KlandreBeskrevet = new EmbedBuilder();
            KlandreBeskrevet.setTitle("Beskrivelser for givet klandringer");

            while (BeskrevetListe.next()){
                KlandreBeskrevet.addField(
                        BeskrevetListe.getString(1) + " har klandret " +
                                BeskrevetListe.getString(2) + " beggrundelse:",
                        BeskrevetListe.getString(3), false);
            }

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(KlandreBeskrevet.build()).queue();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        SQL_Connection_Setup.SQLDisconnect();
    }

    /**
     * Henter tabel 1 fra SQL og laver en ny Embed. Itererer igennem listen og returnere et nyt
     * embed felt med navn og hvad de skylder i kr.
     * @param event det modtaget discord event
     */
    private void listeMetode(GuildMessageReceivedEvent event) {
        SQL_Connection_Setup.SQLconnect();
        try {
            ResultSet Listen = Queries.KlandreListe();

            EmbedBuilder KlandreListe = new EmbedBuilder();
            KlandreListe.setTitle("Liste over hvem skylder hvad:");

            while (Listen.next()) {
                KlandreListe.addField(Listen.getString(1), Listen.getString(3) + "kr", false);
            }

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(KlandreListe.build()).queue();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        SQL_Connection_Setup.SQLDisconnect();
    }

    /**
     * Tilføjer ny person til tabel 1 i SQL, hvis personen allerede eksistere incrementer den
     * antal med +1 og total +5. Til sidst returnere den en bekræftelse.
     * @param args besked modtaget omdannet til liste hvor mellemrum er fjernet.
     * @param event det modtaget discord event
     */
    private void klandreMetode(String[] args, GuildMessageReceivedEvent event) {
        SQL_Connection_Setup.SQLconnect();
        String fromPerson = args[1];
        String toPerson = args[2];
        String begrundelse = args[3];

        Queries.Klandre(toPerson);
        Queries.KlandreBeskrevet(toPerson, fromPerson, begrundelse);


        event.getChannel().sendMessage(fromPerson + " Klandre " + toPerson + " for " + begrundelse).queue();
        SQL_Connection_Setup.SQLDisconnect();
    }
}
