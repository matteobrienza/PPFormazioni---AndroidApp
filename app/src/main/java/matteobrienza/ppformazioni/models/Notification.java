package matteobrienza.ppformazioni.models;

/**
 * Created by Matteo on 06/01/2017.
 */

public class Notification {

    private int Id;
    private String Title;
    private String Body;
    private String Date;

    public Notification(int i,String t, String b, String d){
        Id = i;
        Title = t;
        Body = b;
        Date = d;
    }

    public int getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public String getBody() {
        return Body;
    }

    public String getDate() {
        return Date;
    }
}
