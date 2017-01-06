package matteobrienza.ppformazioni.models;

import java.util.List;

/**
 * Created by Matteo on 03/01/2017.
 */

public class Team {

    public int Id;
    public String Name;
    public String FullName;
    public String Avatar;
    public String MarketValue;
    public List<Player> Players;

    public Team(int i, String n, String fn, String a, String mv, List<Player> p){
        Id = i;
        Name = n;
        FullName = fn;
        Avatar = a;
        MarketValue = mv;
        Players = p;
    }

    public int getId() {
        return Id;
    }

    public String getAvatar() {
        return Avatar;
    }

    public String getName() {
        return Name;
    }

    public String getFullName() {
        return FullName;
    }

    public String getMarketValue() {
        return MarketValue;
    }

    public List<Player> getPlayers() {
        return Players;
    }
}
