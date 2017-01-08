package matteobrienza.ppformazioni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import matteobrienza.ppformazioni.adapters.NotificationsAdapter;
import matteobrienza.ppformazioni.adapters.SearchPlayersAdapter;
import matteobrienza.ppformazioni.models.Notification;
import matteobrienza.ppformazioni.models.Player;

public class SearchPlayersActivity extends AppCompatActivity {

    private RecyclerView SearchPlayers_rv;
    private SearchPlayersAdapter SearchPlayers_adapter;
    private RecyclerView.LayoutManager SearchPlayers_layoutManager;

    private static final float BACKOFF_MULT = 1.0f;
    private static final int TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 0;

    private EditText SearchText;
    private ImageView SearchButton;
    private Button SaveButton;

    private List<Player> players;
    private List<Player> playersToFilter;

    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_players);

        if(Build.VERSION.SDK_INT >= 21) {
            dialog = new ProgressDialog(this,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        }else  dialog = new ProgressDialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        SearchText = (EditText) findViewById(R.id.search_player_name);
        SearchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SearchPlayers_adapter.applyFilter(SearchText.getText().toString());
                return false;
            }
        });
        SearchButton = (ImageView) findViewById(R.id.search_button);
        SaveButton = (Button) findViewById(R.id.search_button_save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Player> playersToSave = SearchPlayers_adapter.getPlayersSelected();
                JSONObject Lineup = new JSONObject();
                try {
                    Lineup.put("players",new Gson().toJson(playersToSave));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String postString = new Gson().toJson(playersToSave);

                System.out.println(postString);

                dialog.show();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_progress);

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //NOTIFY THE DELETE TO THE SERVER
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.USERS_URL + "/" + state.getString("user_id", "1") + Constants.USERS_LINEUP_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        SearchPlayersActivity.this.finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                        dialog.dismiss();
                        SearchPlayersActivity.this.finish();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return postString == null ? null : postString.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", postString, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        players = new LinkedList<>();

        playersToFilter = new LinkedList<>();

        SearchPlayers_rv = (RecyclerView) findViewById(R.id.search_players_list);

        SearchPlayers_rv.setHasFixedSize(true);

        SearchPlayers_layoutManager = new LinearLayoutManager(this);

        SearchPlayers_rv.setLayoutManager(SearchPlayers_layoutManager);

        SearchPlayers_adapter = new SearchPlayersAdapter(players, playersToFilter);

        SearchPlayers_rv.setAdapter(SearchPlayers_adapter);

        GetPlayers(Constants.PLAYERS_URL, this);

    }

    public void GetPlayers(String URL, Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jo = response.getJSONObject(i);

                                Player ts = new Player(
                                        jo.getInt("id"),
                                        jo.getString("name").substring(jo.getString("name").lastIndexOf("\n") + 1),
                                        false
                                );

                                players.add(ts);
                                playersToFilter.add(ts);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SearchPlayers_adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsObjRequest);
    }

}
