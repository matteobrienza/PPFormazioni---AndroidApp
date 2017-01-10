package matteobrienza.ppformazioni;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.adapters.NewspapersAdapter;
import matteobrienza.ppformazioni.adapters.PlayersAdapter;
import matteobrienza.ppformazioni.interfaces.INewspaperAdapterClicks;
import matteobrienza.ppformazioni.models.MatchOtherInfo;
import matteobrienza.ppformazioni.models.Newspaper;
import matteobrienza.ppformazioni.models.Player;

public class MatchDetailsActivity extends AppCompatActivity implements INewspaperAdapterClicks {

    private Toolbar Toolbar_top;
    private RecyclerView Newspapers_rv;
    private RecyclerView.LayoutManager Newspapers_layoutManager;
    private RecyclerView.Adapter Newspapers_adapter;

    private CardView matchInfo;
    private LinearLayout HomeTeam;
    private LinearLayout AwayTeam;
    private TextView HomeTeam_name;
    private ImageView HomeTeam_avatar;
    private TextView AwayTeam_name;
    private ImageView AwayTeam_avatar;
    private TextView MatchDate;

    private RecyclerView Players_rv;
    private RecyclerView.LayoutManager Players_layoutManager;
    private RecyclerView.Adapter Players_adapter;

    private List<Newspaper> Newspapers;
    private List<Player> PlayersHome;
    private List<Player> PlayersAway;
    private List<MatchOtherInfo> HomeInfos;
    private List<MatchOtherInfo> AwayInfos;

    private String MATCH_ID;
    private String MATCH_HEADER;
    private Context context;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        context = this;

        //BINDING UI INFO
        HomeTeam = (LinearLayout)findViewById(R.id.homeTeam);
        AwayTeam = (LinearLayout)findViewById(R.id.awayTeam);
        matchInfo = (CardView)findViewById(R.id.card_view);
        HomeTeam_name = (TextView)findViewById(R.id.homeTeam_name);
        HomeTeam_avatar = (ImageView) findViewById(R.id.homeTeam_avatar);
        AwayTeam_name = (TextView) findViewById(R.id.awayTeam_name);
        AwayTeam_avatar = (ImageView) findViewById(R.id.awayTeam_avatar);
        MatchDate = (TextView) findViewById(R.id.match_date);

        MATCH_ID = getIntent().getStringExtra("MATCH_ID");
        MATCH_HEADER = getIntent().getStringExtra("MATCH_HEADER");

        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2, R.color.refresh_progress_3, R.color.refresh_progress_4);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(true);
                        GetMatch(Constants.NEWSPAPERS_URL + "/1" + Constants.MATCH_URL + "/" + MATCH_ID, context);
                    }
                }
        );

        //TOP TOOLBAR SETUP
        Toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(Toolbar_top);
        Toolbar_top.setTitleTextColor(Color.WHITE);
        Toolbar_top.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        Toolbar_top.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchDetailsActivity.this.finish();
            }
        });
        getSupportActionBar().setTitle(MATCH_HEADER);

        //NESPAPERS
        Newspapers = new LinkedList<Newspaper>();

        Newspapers_rv = (RecyclerView)findViewById(R.id.newspapers_list);

        Newspapers_rv.setHasFixedSize(true);

        Newspapers_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);;

        Newspapers_rv.setLayoutManager(Newspapers_layoutManager);

        Newspapers_adapter = new NewspapersAdapter(Newspapers, this, context);

        Newspapers_rv.setAdapter(Newspapers_adapter);

        GetNewspapers(Constants.NEWSPAPERS_URL, this);

        Players_rv = (RecyclerView)findViewById(R.id.players_list);

        Players_rv.setHasFixedSize(true);

        Players_layoutManager = new LinearLayoutManager(this);;

        Players_rv.setLayoutManager(Players_layoutManager);

        mySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mySwipeRefreshLayout.setRefreshing(true);
                GetMatch(Constants.NEWSPAPERS_URL + "/1" + Constants.MATCH_URL + "/" + MATCH_ID, context);
            }
        });


    }

    public void GetNewspapers(String URL, final Context context){


        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for(int i = 0; i < response.length(); i++) {
                                JSONObject n = response.getJSONObject(i);

                                Newspaper newspaper = new Newspaper(
                                        n.getInt("id"),
                                        n.getString("name")
                                );

                                Newspapers.add(newspaper);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Newspapers_adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });

        queue.add(jsObjRequest);
    }


    public void GetMatch(String URL, final Context context){

        PlayersHome = new LinkedList<Player>();

        PlayersAway = new LinkedList<Player>();

        HomeInfos = new LinkedList<MatchOtherInfo>();

        AwayInfos = new LinkedList<MatchOtherInfo>();

        Players_adapter = new PlayersAdapter(PlayersHome,PlayersAway,HomeInfos,AwayInfos,this);

        Players_rv.setAdapter(Players_adapter);

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject match = response;

                            System.out.println(match);
                            final JSONObject team_home = match.getJSONObject("homeTeam");

                            final JSONObject team_away = match.getJSONObject("awayTeam");

                            HomeTeam_name.setText(team_home.getString("name"));
                            HomeTeam.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, TeamDetailsActivity.class);
                                    try {
                                        intent.putExtra("TEAM_ID", team_home.getInt("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    context.startActivity(intent);
                                }
                            });

                            Picasso.with(context).load(team_home.getString("logo_URL")).error(R.drawable.ic_football).into(HomeTeam_avatar);

                            AwayTeam_name.setText(team_away.getString("name"));
                            AwayTeam.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, TeamDetailsActivity.class);
                                    try {
                                        intent.putExtra("TEAM_ID", team_away.getInt("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    context.startActivity(intent);
                                }
                            });

                            Picasso.with(context).load(team_away.getString("logo_URL")).error(R.drawable.ic_football).into(AwayTeam_avatar);

                            MatchDate.setText(match.getString("matchDate"));

                            JSONArray players = match.getJSONArray("players");

                            String substitutionsHome = "";

                            String substitutionsAway = "";

                            System.out.println(players);

                            for(int i = 0; i < players.length(); i++){

                                JSONObject playerMatch = players.getJSONObject(i);

                                JSONObject player = playerMatch.getJSONObject("player");

                                int player_status = playerMatch.getInt("status");

                                int player_teamId = player.getInt("teamId");

                                if( player_status == 1){

                                    Player p = new Player(
                                            player.getInt("id"),
                                            player.getString("name").substring(player.getString("name").lastIndexOf("\n") + 1),
                                            player.getString("number"),
                                            null,
                                            null,
                                            null,
                                            null,
                                            null
                                    );

                                    if(player_teamId == team_home.getInt("id")){
                                        PlayersHome.add(p);
                                        continue;
                                    }

                                    if(player_teamId == team_away.getInt("id")){
                                        PlayersAway.add(p);
                                        continue;
                                    }

                                }

                                if(player_status == 2){

                                    if(player_teamId == team_home.getInt("id")){
                                        substitutionsHome += player.getString("name").substring(player.getString("name").lastIndexOf("\n") + 1) + ", ";
                                        continue;
                                    }

                                    if(player_teamId == team_away.getInt("id")){
                                        substitutionsAway += player.getString("name").substring(player.getString("name").lastIndexOf("\n") + 1) + ", ";
                                        continue;
                                    }

                                }

                            }

                            if(substitutionsHome.length()>0){
                                MatchOtherInfo msubs_home = new MatchOtherInfo("Substitutions", substitutionsHome.substring(0,substitutionsHome.lastIndexOf(",")));
                                MatchOtherInfo coach_home = new MatchOtherInfo("Coach", team_home.getJSONObject("coach").getString("surname"));
                                HomeInfos.add(coach_home);
                                HomeInfos.add(msubs_home);
                            }

                            if(substitutionsAway.length()>0){
                                MatchOtherInfo msubs_away = new MatchOtherInfo("Substitutions", substitutionsAway.substring(0,substitutionsAway.lastIndexOf(",")));
                                MatchOtherInfo coach_away = new MatchOtherInfo("Coach", team_away.getJSONObject("coach").getString("surname"));
                                AwayInfos.add(coach_away);
                                AwayInfos.add(msubs_away);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mySwipeRefreshLayout.setRefreshing(false);

                        Players_adapter.notifyDataSetChanged();

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

    @Override
    public void onNewspaperItemClick(int newspaperId) {
        mySwipeRefreshLayout.setRefreshing(true);
        GetMatch(Constants.NEWSPAPERS_URL + "/" + newspaperId + Constants.MATCH_URL + "/" + MATCH_ID, context);
    }
}
