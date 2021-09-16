import java.sql.*;

public class Queries {

    /**
     * Laver de nødvendige tables, hvis de allerede eksistere så skipper den over.
     */
    public static void createTables() {
        SQL_Connection_Setup.SQLconnect();
        try {

            String table1;
            table1 = " create table IF NOT EXISTS Klandringer( \n" +
                    " Navn VARCHAR(60) PRIMARY KEY NOT NULL, \n" +
                    " Antal INT NOT NULL, \n" +
                    " Total INT NOT NULL, \n" +
                    " UNIQUE(Navn) \n" +
                    ");";
            PreparedStatement create = SQL_Connection_Setup.connection.prepareStatement(table1);
            create.executeUpdate();

            String table2 = " create table IF NOT EXISTS Klandring_Beskrivelser( \n Navn_fra VARCHAR(60), \n Navn_til VARCHAR(60), \n Beskrivelse VARCHAR(1000) \n);";
            PreparedStatement create2 = SQL_Connection_Setup.connection.prepareStatement(table2);
            create2.executeUpdate();

            String table3;
            table3 = "create table IF NOT EXISTS Anti_Klandringer( \n" +
                    " Navn VARCHAR(60) primary KEY NOT NULL, \n" +
                    "  Skylder INT NOT NULL);";

            PreparedStatement create3 = SQL_Connection_Setup.connection.prepareStatement(table3);
            create3.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            System.out.println("Tables created successfully");
        }
        SQL_Connection_Setup.SQLDisconnect();
    }

    /**
     * Metode der prøver at tilføje en ny person til klandring tabellen, hvis personen allerede eksistere
     * så tilføjer den +1 antal og +5 total, hvis intet virker kaster den en error.
     * @param to    Personen som blir klandret
     */
    public static void Klandre(String to){
        try {
            PreparedStatement Klandring = SQL_Connection_Setup.connection.prepareStatement(
                    "INSERT INTO Klandringer VALUES ('" + to + "', 1, 5);"
            );
            Klandring.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException throwables) {
            System.out.println("Cought: " + throwables);
            try {
                PreparedStatement KlandretAlready = SQL_Connection_Setup.connection.prepareStatement(
                        "UPDATE Klandringer SET Antal = " +
                                "(SELECT Antal WHERE Navn = '" + to + "') + 1 WHERE Navn = '" + to + "';"
                );
                KlandretAlready.executeUpdate();
                PreparedStatement KlandretAlready2 = SQL_Connection_Setup.connection.prepareStatement(
                        "UPDATE Klandringer SET Total = " +
                                "(SELECT Total WHERE Navn = '" + to + "') + 5 WHERE Navn = '" + to + "';"
                );
                KlandretAlready2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * metode som opdatere anden tabel med selvre klandringen fra/til og hele beskedden.
     * @param to    personen som blir klandret
     * @param from  personen som klandre
     * @param begrundelse   Baggrund for klandring
     */
    public static void KlandreBeskrevet(String to, String from, String begrundelse){
        try {
            PreparedStatement KlandringMedInfo = SQL_Connection_Setup.connection.prepareStatement(
                    "INSERT INTO Klandring_Beskrivelser " +
                            "VALUES('" + from + "', '" + to + "', '"+ begrundelse + "');"
            );
            KlandringMedInfo.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    /**
     *
     * @return returnere en ResultSet bestående af alt i vores første tabel
     * @throws SQLException if shit hits the fan
     */
    public static ResultSet KlandreListe() throws SQLException {

        PreparedStatement liste = SQL_Connection_Setup.connection.prepareStatement(
                "SELECT * FROM Klandringer;"
        );
        return liste.executeQuery();
    }

    /**
     * Metoden returnere Klandre_beskrivelser tabellen fra SQL
     * @return returnere Klandring_Beskrivelser tabellen fra SQL
     */
    public static ResultSet KlandreBeskrivelser() throws SQLException {

        PreparedStatement listeAfBeskrivelser = SQL_Connection_Setup.connection.prepareStatement(
                "SELECT * FROM Klandring_Beskrivelser"
        );
        return listeAfBeskrivelser.executeQuery();
    }

    /**
     * Metoden tilføjer personen til antiklandringslisten, hvis personen allerede er der tilføjer den 5kr til personen.
     * @param person person som deltager i antiklandring
     */
    public static void antiKlandring(String person){

        try {
            PreparedStatement navnTilAnti = SQL_Connection_Setup.connection.prepareStatement(
                    "INSERT INTO Anti_Klandringer VALUES ('" + person + "', 5);"
            );
            navnTilAnti.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException throwables) {
            System.out.println("Caught: " + throwables);
            try {
                PreparedStatement navnTilAnti2 = SQL_Connection_Setup.connection.prepareStatement(
                        "UPDATE Anti_Klandringer SET Skylder = " +
                                "(SELECT Skylder WHERE Navn = '" + person + "') + 5 WHERE Navn = '" + person + "';"
                );
                navnTilAnti2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Metoden returnere anti klandrings tabellen fra SQL
     * @return antiklandring tabellen
     */
    public static ResultSet antiListe() throws SQLException {

        PreparedStatement antiListe = SQL_Connection_Setup.connection.prepareStatement(
                "SELECT * FROM Anti_Klandringer;"
        );
        return antiListe.executeQuery();
    }

    /**
     * Sletter personen som har betalt fra Tabel 1 og 2 i SQL
     * @param betalt personen som har betalt
     */
    public static void betalKlandring(String betalt) {

        try {
            PreparedStatement harBetalt = SQL_Connection_Setup.connection.prepareStatement(
                    "DELETE FROM Klandringer WHERE Navn = '" + betalt + "';"
            );

            harBetalt.executeUpdate();

            PreparedStatement harBetalt2 = SQL_Connection_Setup.connection.prepareStatement(
                    "DELETE FROM Klandring_Beskrivelser WHERE Navn_til = '"+ betalt +"';"
            );

            harBetalt2.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Sletter personen fra Anti_Klandring listen.
     * @param personSomSlettes Personen som har betalt
     */
    public static void betaltAntiKlandring(String personSomSlettes) {
        try {
            PreparedStatement harBetalt = SQL_Connection_Setup.connection.prepareStatement(
                    "DELETE FROM Anti_Klandringer WHERE Navn = '"+ personSomSlettes + "';"
            );
            harBetalt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * deletes everything from every table
     */
    public static void truncateAall() {
        try {

            ResultSet del1 = SQL_Connection_Setup.connection.prepareStatement(
                    "SELECT * FROM Klandringer"
            ).executeQuery();

            while (del1.next()){
                SQL_Connection_Setup.connection.prepareStatement(
                        "DELETE FROM Klandringer WHERE Navn = '" + del1.getString(1) + "';"
                ).executeUpdate();
            }

            ResultSet del2 = SQL_Connection_Setup.connection.prepareStatement(
                    "SELECT * FROM Klandring_Beskrivelser;"
            ).executeQuery();

            while(del2.next()){
                SQL_Connection_Setup.connection.prepareStatement(
                        "DELETE FROM Klandring_Beskrivelser WHERE Navn_til = '"+ del2.getString(2) +"';"
                ).executeUpdate();
            }

            ResultSet del3 = SQL_Connection_Setup.connection.prepareStatement(
                    "SELECT * FROM Anti_Klandringer;"
            ).executeQuery();

            while(del3.next()){
                SQL_Connection_Setup.connection.prepareStatement(
                        "DELETE FROM Anti_Klandringer WHERE Navn = '"+ del3.getString(1) + "';"
                ).executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
