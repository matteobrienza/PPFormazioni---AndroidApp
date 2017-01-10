package matteobrienza.ppformazioni.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matteo on 03/01/2017.
 */

public class Player implements Parcelable{

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

    /*public Player(int i,String n, int cds, int gds, int ss){
        Id = i;
        Name = n;
        Cds_Status = cds;
        Gds_Status = gds;
        Ss_Status = ss;
    }*/

    public Player(int i, String n, String nb){
        Id = i;
        Name = n;
        Number = nb;
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

    @Override
    public String toString() {
        return Id + ", " + Name.substring(Name.lastIndexOf("\n") + 1) + " " + Number;
    }

    public Player(Parcel in) {
        //Id = in.readInt();
        Name = in.readString();
        Cds_Status = in.readInt();
        Gds_Status = in.readInt();
        Ss_Status = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeInt(Cds_Status);
        dest.writeInt(Gds_Status);
        dest.writeInt(Ss_Status);
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>()
    {
        @Override
        public Player createFromParcel(Parcel in)
        {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size)
        {
            return new Player[size];
        }
    };
}
