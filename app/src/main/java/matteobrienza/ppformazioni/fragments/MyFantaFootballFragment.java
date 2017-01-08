package matteobrienza.ppformazioni.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
import matteobrienza.ppformazioni.SearchPlayersActivity;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.adapters.NotificationsAdapter;
import matteobrienza.ppformazioni.adapters.PlayerWithStatusAdapter;
import matteobrienza.ppformazioni.models.Notification;
import matteobrienza.ppformazioni.models.Player;


public class MyFantaFootballFragment extends Fragment {


    private FloatingActionButton AddPlayersButton;
    private RecyclerView Players_List;

    private RecyclerView.Adapter Players_adapter;
    private RecyclerView.LayoutManager Players_layoutManager;

    private ImageView NoLineup_Image;
    private TextView NoLineup_Message;

    private List<Player> players;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_myfantafootball, container, false);

        SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getContext());

        AddPlayersButton = (FloatingActionButton)v.findViewById(R.id.mylineup_addPlayers);
        AddPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchPlayersActivity.class);
                startActivity(i);
            }
        });


        Players_List = (RecyclerView)v.findViewById(R.id.myPlayers_rv);

        NoLineup_Image = (ImageView) v.findViewById(R.id.no_lineup_image);
        NoLineup_Message = (TextView) v.findViewById(R.id.no_lineup_text);

        players = new LinkedList<Player>();

        Players_List.setHasFixedSize(true);

        Players_List.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && AddPlayersButton.isShown())
                {
                    AddPlayersButton.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    AddPlayersButton.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        Players_layoutManager = new LinearLayoutManager(container.getContext());

        Players_List.setLayoutManager(Players_layoutManager);

        Players_adapter = new PlayerWithStatusAdapter(players,getContext());

        Players_List.setAdapter(Players_adapter);

        GetPlayers(Constants.USERS_URL + "/" + state.getString("user_id", "1") + Constants.USERS_LINEUP_URL,getContext());


        return v;
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of HomeFragment");
        super.onResume();
        SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getContext());
        GetPlayers(Constants.USERS_URL + "/" + state.getString("user_id", "1") + Constants.USERS_LINEUP_URL,getContext());
    }

    public void GetPlayers(String URL, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject jo = response.getJSONObject(i);

                                Player ts = new Player(
                                        jo.getString("name"),
                                        jo.getInt("cdS_Status"),
                                        jo.getInt("gdS_Status"),
                                        jo.getInt("sS_Status")
                                );

                                players.add(ts);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(players.size() == 0){
                            NoLineup_Image.setVisibility(View.VISIBLE);
                            NoLineup_Message.setVisibility(View.VISIBLE);
                        }else
                        {
                            Players_adapter.notifyDataSetChanged();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NoLineup_Image.setVisibility(View.VISIBLE);
                        NoLineup_Message.setVisibility(View.VISIBLE);
                        System.out.println(error);
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsObjRequest);
    }



}
