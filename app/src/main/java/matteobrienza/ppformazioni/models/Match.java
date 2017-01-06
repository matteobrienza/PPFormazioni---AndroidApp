package matteobrienza.ppformazioni.models;

import java.util.List;

/**
 * Created by Matteo on 03/01/2017.
 */

public class Match {

    private int Id;
    private int HomeTeam_Id;
    private String HomeTeam_Name;
    private String HomeTeam_Avatar;
    private int AwayTeam_Id;
    private String AwayTeam_Name;
    private String AwayTeam_Avatar;
    private List<Player> HomePlayers;
    private List<Player> AwayPlayers;

    private String MatchDate;

    public Match(int id, int ht_id, String ht_name, String ht_avatar, int at_id, String at_name, String at_avatar, List<Player> hp, List<Player> ap, String md){
        Id = id;
        HomeTeam_Id = ht_id;
        HomeTeam_Name = ht_name;
        HomeTeam_Avatar = ht_avatar;
        AwayTeam_Id = at_id;
        AwayTeam_Name = at_name;
        AwayTeam_Avatar = at_avatar;
        HomePlayers = hp;
        AwayPlayers = ap;
        MatchDate = md;
    }

    public int getId() {
        return Id;
    }

    public int getHomeTeam_Id() {
        return HomeTeam_Id;
    }

    public String getHomeTeam_Name() {
        return HomeTeam_Name;
    }

    public String getHomeTeam_Avatar() {
        return HomeTeam_Avatar;
    }

    public List<Player> getHomePlayers() {
        return HomePlayers;
    }

    public int getAwayTeam_Id() {
        return AwayTeam_Id;
    }

    public String getAwayTeam_Name() {
        return AwayTeam_Name;
    }

    public String getAwayTeam_Avatar() {
        return AwayTeam_Avatar;
    }

    public List<Player> getAwayPlayers() {
        return AwayPlayers;
    }

    public String getMatchDate() {
        return MatchDate;
    }
}
