package matteobrienza.ppformazioni.models;

/**
 * Created by Matteo on 03/01/2017.
 */

public class MatchOtherInfo {

    public String InfoType;
    public String InfoText;

    public MatchOtherInfo(String ity, String ite){
        InfoType = ity;
        InfoText = ite;
    }

    public String getInfoText() {
        return InfoText;
    }

    public String getInfoType() {
        return InfoType;
    }
}
