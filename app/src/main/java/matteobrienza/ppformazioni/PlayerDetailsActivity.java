package matteobrienza.ppformazioni;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import matteobrienza.ppformazioni.adapters.PlayersAdapter;
import matteobrienza.ppformazioni.models.MatchOtherInfo;
import matteobrienza.ppformazioni.models.Player;

public class PlayerDetailsActivity extends AppCompatActivity {

    private int PLAYER_ID;

    private Toolbar Toolbar_top;
    private ImageView Player_Avatar;
    private TextView Player_Name;
    private TextView Player_Number;
    private TextView Player_Birthday;
    private TextView Player_Position;
    private TextView Player_Nationality;
    private TextView Player_MarketValue;
    private TextView Player_ContractUntil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);

        PLAYER_ID  = getIntent().getIntExtra("PLAYER_ID",0);

        //TOP TOOLBAR SETUP
        Toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(Toolbar_top);
        Toolbar_top.setTitleTextColor(Color.WHITE);
        Toolbar_top.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        Toolbar_top.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerDetailsActivity.this.finish();
            }
        });
        getSupportActionBar().setTitle("");


        Player_Avatar = (ImageView)findViewById(R.id.player_avatar);
        Player_Name = (TextView)findViewById(R.id.player_name);
        Player_Number = (TextView)findViewById(R.id.player_number);
        Player_Birthday = (TextView)findViewById(R.id.player_birthday);
        Player_Position = (TextView)findViewById(R.id.player_position);
        Player_Nationality = (TextView)findViewById(R.id.player_nationality);
        Player_MarketValue = (TextView)findViewById(R.id.player_marketvalue);
        Player_ContractUntil = (TextView)findViewById(R.id.player_contractuntil);

        GetPlayer(Constants.PLAYERS_URL + "/" + PLAYER_ID, this);

    }


    public void GetPlayer(String URL, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject player = response;

                            Player_Name.setText(player.getString("name").substring(player.getString("name").lastIndexOf("\n") + 1));
                            Player_Number.setText("#" + player.getString("number"));
                            Player_Birthday.setText("Birthday: " + player.getString("dateOfBirth"));
                            Player_Position.setText("Position: " + player.getString("position"));
                            Player_Nationality.setText("Nationality: " + player.getString("nationality"));
                            Player_MarketValue.setText("Market Value: " + player.getString("marketValue"));
                            Player_ContractUntil.setText("Contract Until: " + player.getString("contractUntil"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });

        queue.add(jsObjRequest);
    }
}
