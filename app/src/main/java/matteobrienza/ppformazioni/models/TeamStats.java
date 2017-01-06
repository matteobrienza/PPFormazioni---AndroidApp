package matteobrienza.ppformazioni.models;

/**
 * Created by Matteo on 03/01/2017.
 */

public class TeamStats {

    public int TeamId;
    public String TeamName;
    public String TeamAvatar;
    public String Points;
    public String MatchGames;
    public String MatchWins;
    public String MatchDraws;
    public String MatchLosts;

    public TeamStats(int ti, String tn, String ta, String p, String mg, String mw, String md, String ml){
        TeamId = ti;
        TeamName = tn;
        TeamAvatar = ta;
        Points = p;
        MatchGames = mg;
        MatchWins = mw;
        MatchDraws = md;
        MatchLosts = ml;
    }


    public String getTeamName() {
        return TeamName;
    }

    public String getTeamAvatar() {
        return TeamAvatar;
    }

    public String getPoints() {
        return Points;
    }

    public String getMatchWins() {
        return MatchWins;
    }

    public String getMatchGames() {
        return MatchGames;
    }

    public String getMatchDraws() {
        return MatchDraws;
    }

    public String getMatchLosts() {
        return MatchLosts;
    }
}
