package matteobrienza.ppformazioni.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.Player;

/**
 * Created by Matteo on 08/01/2017.
 */

public class PlayerWithStatusAdapter extends RecyclerView.Adapter<PlayerWithStatusAdapter.PlayerWithStatusViewHolder> {

    private List<Player> players;
    private Context context;

    public class PlayerWithStatusViewHolder extends RecyclerView.ViewHolder {

        private TextView Player_Name;
        private TextView Player_Cds_Status;
        private TextView Player_Gds_Status;
        private TextView Player_Ss_Status;

        public PlayerWithStatusViewHolder(View v) {
            super(v);
            Player_Name = (TextView) v.findViewById(R.id.myPlayers_player_name);
            Player_Cds_Status = (TextView) v.findViewById(R.id.cds);
            Player_Gds_Status = (TextView) v.findViewById(R.id.gds);
            Player_Ss_Status = (TextView) v.findViewById(R.id.ss);
        }
    }

    public PlayerWithStatusAdapter(List<Player> p, Context c) {
        players = p;
        context = c;
    }

    @Override
    public PlayerWithStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_with_status, parent, false);
        return new PlayerWithStatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PlayerWithStatusViewHolder holder, final int position) {
        Player p = players.get(position);
        holder.Player_Name.setText(p.getName().substring(p.getName().lastIndexOf("\n") + 1));
       /* holder.Player_Cds_Status.setBackground( p.getCds_Status()==1 ? context.getResources().getDrawable(R.drawable.circle_enabled) : context.getResources().getDrawable(R.drawable.circle_disabled));
        holder.Player_Gds_Status.setBackground(p.getGds_Status()==1 ? context.getResources().getDrawable(R.drawable.circle_enabled) : context.getResources().getDrawable(R.drawable.circle_disabled));
        holder.Player_Ss_Status.setBackground(p.getSs_Status()==1 ? context.getResources().getDrawable(R.drawable.circle_enabled) : context.getResources().getDrawable(R.drawable.circle_disabled));*/

        holder.Player_Cds_Status.setTextColor( p.getCds_Status()==1 ? context.getResources().getColor(R.color.circle_enabled) :  context.getResources().getColor(R.color.circle_disabled));
        holder.Player_Gds_Status.setTextColor(p.getGds_Status()==1 ? context.getResources().getColor(R.color.circle_enabled) :  context.getResources().getColor(R.color.circle_disabled));
        holder.Player_Ss_Status.setTextColor(p.getSs_Status()==1 ? context.getResources().getColor(R.color.circle_enabled) :  context.getResources().getColor(R.color.circle_disabled));
    }


    @Override
    public int getItemCount() {
        return players.size();
    }
}