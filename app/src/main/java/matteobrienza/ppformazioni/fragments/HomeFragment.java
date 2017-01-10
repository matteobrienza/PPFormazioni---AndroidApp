package matteobrienza.ppformazioni.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.adapters.MatchesAdapter;
import matteobrienza.ppformazioni.listeners.HidingScrollListener;
import matteobrienza.ppformazioni.models.Match;
import matteobrienza.ppformazioni.models.Player;
import matteobrienza.ppformazioni.models.TeamStats;
import matteobrienza.ppformazioni.networking.CacheRequest;


public class HomeFragment extends Fragment {

    private RecyclerView MatchList_rv;
    private MatchesAdapter MatchList_adapter;
    private RecyclerView.LayoutManager MatchList_layoutManager;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private List<Match> Matches;
    private String Day;

    private static final float BACKOFF_MULT = 1.0f;
    private static final int TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 0;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar Toolbar_top = (Toolbar)getActivity().findViewById(R.id.toolbar_top);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation);
        Toolbar_top.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);

        mySwipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2, R.color.refresh_progress_3, R.color.refresh_progress_4);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(true);
                        GetMatches(Constants.DAYS_URL, getContext());
                    }
                }
        );

        Matches = new LinkedList<Match>();
        Day = "";
        MatchList_rv = (RecyclerView)root.findViewById(R.id.matches_list);


        MatchList_rv.setHasFixedSize(true);

        MatchList_layoutManager = new LinearLayoutManager(container.getContext());

        //MatchList_layoutManager = new GridLayoutManager(getActivity(), 1);

        MatchList_rv.setLayoutManager(MatchList_layoutManager);

        MatchList_rv.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(3));
            }

            @Override
            public void onShow() {
                bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
            }
        });

        MatchList_rv.setLayoutManager(MatchList_layoutManager);

        MatchList_adapter = new MatchesAdapter(Matches, Day, container.getContext());

        MatchList_rv.setAdapter(MatchList_adapter);

        mySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                System.out.println(Matches);
                mySwipeRefreshLayout.setRefreshing(true);
                GetMatches(Constants.DAYS_URL, getContext());
            }
        });



        return root;
    }

    public void GetMatches(String URL, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        CacheRequest jsObjRequest = new CacheRequest( "", 0, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    final String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    JSONArray base = new JSONArray(jsonString);



                    JSONObject jo = base.getJSONObject(0);

                    Activity activity = getActivity();
                    if(activity != null){
                        MatchList_adapter.setDay(activity.getResources().getString(R.string.day) + " " + jo.getString("number"));
                    }


                    JSONArray matches = jo.getJSONArray("matches");

                    Matches.clear();

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
                    mySwipeRefreshLayout.setRefreshing(false);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
                MatchList_adapter.notifyDataSetChanged();
                mySwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mySwipeRefreshLayout.setRefreshing(false);
                System.out.println(error);
            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

}
