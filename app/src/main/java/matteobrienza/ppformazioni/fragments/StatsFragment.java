package matteobrienza.ppformazioni.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.adapters.MatchesAdapter;
import matteobrienza.ppformazioni.adapters.StatsAdapter;
import matteobrienza.ppformazioni.models.Match;
import matteobrienza.ppformazioni.models.TeamStats;
import matteobrienza.ppformazioni.networking.CacheRequest;


public class StatsFragment extends Fragment {


    private List<TeamStats> Stats;
    private RecyclerView StatList_rv;
    private RecyclerView.Adapter StatList_adapter;
    private RecyclerView.LayoutManager StatList_layoutManager;

    private FrameLayout Stats_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        Stats_layout = (FrameLayout)v.findViewById(R.id.stats_layout);

        Stats = new LinkedList<TeamStats>();

        StatList_rv = (RecyclerView)v.findViewById(R.id.stats_list);

        StatList_rv.setHasFixedSize(true);

        StatList_layoutManager = new LinearLayoutManager(container.getContext());
        StatList_rv.setLayoutManager(StatList_layoutManager);

        StatList_adapter = new StatsAdapter(Stats, container.getContext());
        StatList_rv.setAdapter(StatList_adapter);

        GetStats(Constants.STATS_URL, getContext());

        return v;

    }

    public void GetStats(String URL, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        CacheRequest jsObjRequest = new CacheRequest( "", 0, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    final String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    JSONArray base = new JSONArray(jsonString);

                    for(int i = 0; i < base.length(); i++){

                            JSONObject jo = base.getJSONObject(i);

                            JSONObject team = jo.getJSONObject("team");
                            JSONObject stats = jo.getJSONObject("stats");

                            TeamStats ts = new TeamStats(
                                    team.getInt("id"),
                                    team.getString("name"),
                                    team.getString("logo_URL"),
                                    stats.getString("points"),
                                    stats.getString("playedGames"),
                                    stats.getString("wins"),
                                    stats.getString("draws"),
                                    stats.getString("losses")
                            );

                            Stats.add(ts);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                StatList_adapter.notifyDataSetChanged();
                Stats_layout.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(jsObjRequest);

        /*RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject jo = response.getJSONObject(i);

                                JSONObject team = jo.getJSONObject("team");
                                JSONObject stats = jo.getJSONObject("stats");

                                TeamStats ts = new TeamStats(
                                        team.getInt("id"),
                                        team.getString("name"),
                                        team.getString("logo_URL"),
                                        stats.getString("points"),
                                        stats.getString("playedGames"),
                                        stats.getString("wins"),
                                        stats.getString("draws"),
                                        stats.getString("losses")
                                );

                                Stats.add(ts);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        StatList_adapter.notifyDataSetChanged();
                        Stats_layout.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsObjRequest);*/
    }

}
