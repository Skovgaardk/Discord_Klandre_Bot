import java.sql.*;

public class SQL_Connection_Setup {

    public static Connection connection;
    public static Statement statement;

    /**
     * opretter en connection til ens SQL server, URL, Username og password indslættes i lokale variable
     * opretter et connection og statement object, statement objected blir brugt i forskellige
     * metoder til at sende/modtage data til SQL serveren.
     */
    public static void SQLconnect() {

        String url = "SQL_URL";          //Url for database
        String user = "Username";        //Username for database
        String password = "Password";    //Password for database


        try {

            connection = DriverManager.getConnection(url, user, password);

            statement = connection.createStatement();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Slukker for connection objected (hvis det samme connection object blir brugt for lang tid går det i stykker)
     */
    public static void SQLDisconnect(){

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
