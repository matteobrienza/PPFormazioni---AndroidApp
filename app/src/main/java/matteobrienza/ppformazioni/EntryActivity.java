package matteobrienza.ppformazioni;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by Matteo on 05/01/2017.
 */

public class EntryActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent launchIntent = null;
        String userName = state.getString("user_name", null);
        System.out.println(userName);
        String activity = null;
        if (userName != null) {
            launchIntent = new Intent(getApplicationContext(),MainActivity.class);
        }else{
            launchIntent = new Intent(getApplicationContext(),LoginActivity.class);
        }
        startActivity(launchIntent);
        finish();
    }


}