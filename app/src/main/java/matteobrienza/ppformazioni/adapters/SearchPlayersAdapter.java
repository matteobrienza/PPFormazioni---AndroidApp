package matteobrienza.ppformazioni.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.interfaces.INotificationsAdapterEmpty;
import matteobrienza.ppformazioni.models.Notification;
import matteobrienza.ppformazioni.models.Player;

/**
 * Created by Matteo on 07/01/2017.
 */

public class SearchPlayersAdapter extends RecyclerView.Adapter<SearchPlayersAdapter.SearchPlayerViewHolder> {

    private List<Player> players;
    private List<Player> all_players;

    public class SearchPlayerViewHolder extends RecyclerView.ViewHolder {

        private TextView Player_Name;
        private CheckBox Player_State;
        private  LinearLayout Player_Layout;

        public SearchPlayerViewHolder(View v) {
            super(v);
            Player_Name = (TextView) v.findViewById(R.id.item_search_player_name);
            Player_State = (CheckBox) v.findViewById(R.id.item_search_player_state);
            Player_Layout = (LinearLayout) v.findViewById(R.id.item_search_layout);
        }
    }

    public SearchPlayersAdapter(List<Player> p1, List<Player> p2) {
        players = p1;
        all_players = p2;
    }

    public void applyFilter(String name){
        players = new LinkedList<>();
        int size = all_players.size();
        for(int i = 0; i < size; i++){
            Player p = all_players.get(i);
            if( Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(p.getName()).find()){
                players.add(p);
            }
        }
        notifyDataSetChanged();
    }

    public void updatePlayer(Player pToUpdate){
        int size = all_players.size();
        for(int i = 0; i < size; i++){
            Player p = all_players.get(i);
            if( p.getId() == pToUpdate.getId()){
                all_players.set(i,pToUpdate);
            }
        }
    }

    @Override
    public SearchPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_searchable, parent, false);
        return new SearchPlayerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SearchPlayerViewHolder holder, final int position) {
        holder.Player_Name.setText(players.get(position).getName());
        holder.Player_State.setChecked(players.get(position).isSelected());
        holder.Player_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player p = players.get(position);
                p.setSelected(holder.Player_State.isChecked());
                updatePlayer(p);
            }
        });
        holder.Player_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.Player_State.toggle();
                Player p = players.get(position);
                p.setSelected(holder.Player_State.isChecked());
                updatePlayer(p);
            }
        });
    }

    public List<Player> getPlayersSelected(){
        List<Player> list = new LinkedList<>();
        int size = all_players.size();
        for(int i = 0; i < size; i++){
            Player p = all_players.get(i);
            if( p.isSelected())list.add(p);
        }
        return list;
    }


    @Override
    public int getItemCount() {
        return players.size();
    }
}