package matteobrienza.ppformazioni.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.adapters.NotificationsAdapter;
import matteobrienza.ppformazioni.adapters.StatsAdapter;
import matteobrienza.ppformazioni.interfaces.INotificationsAdapterEmpty;
import matteobrienza.ppformazioni.models.Notification;
import matteobrienza.ppformazioni.models.TeamStats;


public class NotificationFragment extends Fragment implements INotificationsAdapterEmpty{

    private List<Notification> Notifications;
    private RecyclerView Notifications_rv;
    private RecyclerView.Adapter Notifications_adapter;
    private RecyclerView.LayoutManager Notifications_layoutManager;

    private ImageView NoNotification_Image;
    private TextView NoNotification_Message;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    private Activity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getContext());

        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        mainActivity = getActivity();

        mySwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2, R.color.refresh_progress_3, R.color.refresh_progress_4);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(true);
                        GetNotifications(Constants.USERS_URL + "/" + state.getString("user_id", "1") + Constants.NOTIFICATIONS_URL, getContext());
                    }
                }
        );


        NoNotification_Image = (ImageView) v.findViewById(R.id.no_notifications_image);
        NoNotification_Message = (TextView) v.findViewById(R.id.no_notifications_text);

        Notifications = new LinkedList<Notification>();

        Notifications_rv = (RecyclerView)v.findViewById(R.id.notifications_list);

        Notifications_rv.setHasFixedSize(true);

        Notifications_layoutManager = new LinearLayoutManager(container.getContext());
        Notifications_rv.setLayoutManager(Notifications_layoutManager);

        Notifications_adapter = new NotificationsAdapter(Notifications, container.getContext(), this);
        Notifications_rv.setAdapter(Notifications_adapter);

        mySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mySwipeRefreshLayout.setRefreshing(true);
                GetNotifications(Constants.USERS_URL + "/" + state.getString("user_id", "1") + Constants.NOTIFICATIONS_URL, getContext());
            }
        });

        return v;


    }

    public void GetNotifications(String URL, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Notifications.clear();

                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject jo = response.getJSONObject(i);

                                Notification ts = new Notification(
                                        jo.getInt("id"),
                                        jo.getString("title"),
                                        jo.getString("body"),
                                        jo.getString("createdAt")
                                );

                                Notifications.add(ts);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        if(Notifications.size() == 0){
                            NoNotification_Image.setVisibility(View.VISIBLE);
                            NoNotification_Message.setVisibility(View.VISIBLE);
                        }else
                        {
                            NoNotification_Image.setVisibility(View.GONE);
                            NoNotification_Message.setVisibility(View.GONE);
                            Notifications_adapter.notifyDataSetChanged();
                        }

                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NoNotification_Image.setVisibility(View.VISIBLE);
                        NoNotification_Message.setVisibility(View.VISIBLE);
                        mySwipeRefreshLayout.setRefreshing(false);
                        System.out.println(error);
                    }
                });

        queue.add(jsObjRequest);
    }


    @Override
    public void setDefaultNotificationsIcons() {
        NoNotification_Image.setVisibility(View.VISIBLE);
        NoNotification_Message.setVisibility(View.VISIBLE);
        BottomNavigationView BottomNavigation = (BottomNavigationView)mainActivity.findViewById(R.id.bottom_navigation);
        Menu m = BottomNavigation.getMenu();
        m.findItem(R.id.action_notifications).setIcon(R.drawable.ic_notifications_white_24dp);
        final SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getContext());
        state.edit().putBoolean("notifications",false).commit();
    }
}
