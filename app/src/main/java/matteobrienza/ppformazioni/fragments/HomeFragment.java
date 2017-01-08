package matteobrienza.ppformazioni.fragments;


import android.content.Context;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.adapters.MatchesAdapter;
import matteobrienza.ppformazioni.models.Match;
import matteobrienza.ppformazioni.models.Player;
import matteobrienza.ppformazioni.models.TeamStats;


public class HomeFragment extends Fragment {

    private RecyclerView MatchList_rv;
    private MatchesAdapter MatchList_adapter;
    private RecyclerView.LayoutManager MatchList_layoutManager;

    private List<Match> Matches;
    private String Day;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar Toolbar_top = (Toolbar)getActivity().findViewById(R.id.toolbar_top);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation);
        Toolbar_top.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);

        Matches = new LinkedList<Match>();
        Day = "";

        GetMatches(Constants.DAYS_URL, getContext());

        MatchList_rv = (RecyclerView)root.findViewById(R.id.matches_list);

        MatchList_rv.setHasFixedSize(true);

        MatchList_layoutManager = new LinearLayoutManager(container.getContext());

        MatchList_rv.setLayoutManager(MatchList_layoutManager);

        MatchList_adapter = new MatchesAdapter(Matches, Day, container.getContext());

        MatchList_rv.setAdapter(MatchList_adapter);

        return root;
    }

    public void GetMatches(String URL, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject jo = response.getJSONObject(0);

                            MatchList_adapter.setDay(getResources().getString(R.string.day) + " " + jo.getString("number"));

                            JSONArray matches = jo.getJSONArray("matches");

                            for(int i = 0; i < matches.length(); i++){
                                JSONObject match = matches.getJSONObject(i);
                                JSONObject team_home = match.getJSONObject("homeTeam");
                                JSONObject team_away = match.getJSONObject("awayTeam");
                                Match m = new Match(
                                        match.getInt("id"),
                                        team_home.getInt("id"),
                                        team_home.getString("name"),
                                        team_home.getString("logo_URL"),
                                        team_away.getInt("id"),
                                        team_away.getString("name"),
                                        team_away.getString("logo_URL"),
                                        null,
                                        null,
                                        match.getString("matchDate")
                                );
                                Matches.add(m);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        MatchList_adapter.notifyDataSetChanged();
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
