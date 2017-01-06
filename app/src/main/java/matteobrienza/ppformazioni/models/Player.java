package matteobrienza.ppformazioni.models;

/**
 * Created by Matteo on 03/01/2017.
 */

public class Player {

    private  int Id;
    private String Name;
    private String Number;
    private String Position;
    private String Nationality;
    private String MarketValue;
    private String DateOfBirth;
    private String ContractUntil;

    public Player(int i, String n, String nb, String p, String nt, String mv, String db, String cu){
        Id = i;
        Name = n;
        Number = nb;
        Position = p;
        Nationality = nt;
        MarketValue = mv;
        DateOfBirth = db;
        ContractUntil = cu;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getNumber() {
        return Number;
    }

    public String getPosition() {
        return Position;
    }

    public String getNationality() {
        return Nationality;
    }

    public String getMarketValue() {
        return MarketValue;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public String getContractUntil() {
        return ContractUntil;
    }
}
