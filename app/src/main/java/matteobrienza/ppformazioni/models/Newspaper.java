package matteobrienza.ppformazioni.models;

/**
 * Created by Matteo on 03/01/2017.
 */

public class Newspaper {

    private int Id;
    private String Name;

    public Newspaper(int id, String name){
        Id = id;
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }
}
