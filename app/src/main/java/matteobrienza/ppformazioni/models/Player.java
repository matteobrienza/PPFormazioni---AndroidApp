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

    //FOR SEARCH ACTIVITY
    private boolean Selected;

    //FOR MYPLAYERS AREA;
    private int Cds_Status;
    private int Gds_Status;
    private int Ss_Status;

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

    public Player(int i, String n, boolean selected){
        Id = i;
        Name = n;
        Selected = selected;
    }

    public Player(String n, int cds, int gds, int ss){
        Name = n;
        Cds_Status = cds;
        Gds_Status = gds;
        Ss_Status = ss;
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

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    public int getCds_Status() {
        return Cds_Status;
    }

    public int getGds_Status() {
        return Gds_Status;
    }

    public int getSs_Status() {
        return Ss_Status;
    }
}
