package matteobrienza.ppformazioni;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.adapters.NewspapersAdapter;
import matteobrienza.ppformazioni.adapters.TeamDetailsAdapter;
import matteobrienza.ppformazioni.models.Newspaper;
import matteobrienza.ppformazioni.models.Player;
import matteobrienza.ppformazioni.models.Team;

public class TeamDetailsActivity extends AppCompatActivity {

    private RecyclerView Team_rv;
    private RecyclerView.LayoutManager Team_layoutManager;
    private RecyclerView.Adapter Team_adapter;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    private Toolbar Toolbar_top;
    private Team team;

    private int TEAM_ID;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        context = this;

        Toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(Toolbar_top);
        Toolbar_top.setTitleTextColor(Color.WHITE);
        Toolbar_top.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        Toolbar_top.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamDetailsActivity.this.finish();
            }
        });
        getSupportActionBar().setTitle("");

        TEAM_ID  = getIntent().getIntExtra("TEAM_ID",0);

        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2, R.color.refresh_progress_3, R.color.refresh_progress_4);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(true);
                        GetTeam(Constants.TEAMS_URL + "/" + TEAM_ID,context);
                    }
                }
        );

        Team_rv = (RecyclerView)findViewById(R.id.team_rv);

        Team_rv.setHasFixedSize(true);

        Team_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);;

        Team_rv.setLayoutManager(Team_layoutManager);

        mySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mySwipeRefreshLayout.setRefreshing(true);
                GetTeam(Constants.TEAMS_URL + "/" + TEAM_ID,context);
            }
        });

    }

    public void GetTeam(String URL, final Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject t = response;

                            JSONArray playersArray = t.getJSONArray("players");

                            List<Player> players = new LinkedList<>();

                            for(int i = 0; i < playersArray.length(); i++){
                                JSONObject p = playersArray.getJSONObject(i);

                                String playerName = p.getString("name");

                                int start = 0;
                                int end = playerName.lastIndexOf("\n");

                                String fullPlayerName = playerName.substring(start,end);

                                Player player = new Player(
                                        p.getInt("id"),
                                        p.getString("number"),
                                        fullPlayerName
                                );
                                players.add(player);
                            }

                            team = new Team(
                                    t.getInt("id"),
                                    t.getString("name"),
                                    t.getString("fullName"),
                                    t.getString("logo_URL"),
                                    t.getString("marketValue"),
                                    players
                            );

                        } catch (JSONException e) {
                            mySwipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }

                        Team_adapter = new TeamDetailsAdapter(team, context);

                        Team_rv.setAdapter(Team_adapter);

                        mySwipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mySwipeRefreshLayout.setRefreshing(false);
                        System.out.println(error);
                    }
                });

        queue.add(jsObjRequest);
    }
}
