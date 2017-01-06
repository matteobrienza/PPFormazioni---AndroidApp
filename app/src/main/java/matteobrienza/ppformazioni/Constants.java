package matteobrienza.ppformazioni;

/**
 * Created by Matteo on 03/01/2017.
 */

public class Constants {

    //public static String BASE_URL = "http://ppformazioni.azurewebsites.net/api";
    public static String BASE_URL = "http://localhost:8081/api";
    public static String USERS_URL = BASE_URL + "/users";
    public static String CHAMPIONSHIPS_URL = BASE_URL + "/championships/1";
    public static String STATS_URL = CHAMPIONSHIPS_URL + "/standings";
    public static String DAYS_URL = CHAMPIONSHIPS_URL + "/days";
    public static String NEWSPAPERS_URL = CHAMPIONSHIPS_URL + "/newspapers";
    public static String MATCH_URL = "/matches";
    public static String PLAYERS_URL = CHAMPIONSHIPS_URL + "/players";

}
