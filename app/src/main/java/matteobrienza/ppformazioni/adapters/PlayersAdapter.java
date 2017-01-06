package matteobrienza.ppformazioni.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import matteobrienza.ppformazioni.MatchDetailsActivity;
import matteobrienza.ppformazioni.PlayerDetailsActivity;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.Match;
import matteobrienza.ppformazioni.models.MatchOtherInfo;
import matteobrienza.ppformazioni.models.Player;

/**
 * Created by Matteo on 03/01/2017.
 */

public class PlayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Player> playersHome;
    private List<MatchOtherInfo> infosHome;
    private List<Player> playersAway;
    private List<MatchOtherInfo> infosAway;


    private Context context;

    private static final int PLAYER_TYPE = 0;
    private static final int OTHERINFO_TYPE = 1;

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout HomeTeam_Player;
        public LinearLayout AwayTeam_Player;
        public TextView HomeTeam_Player_Name;
        public TextView HomeTeam_Player_Number;
        public TextView AwayTeam_Player_Name;
        public TextView AwayTeam_Player_Number;

        public PlayerViewHolder(View v) {
            super(v);
            HomeTeam_Player = (LinearLayout)v.findViewById(R.id.homeTeam_player);
            AwayTeam_Player = (LinearLayout)v.findViewById(R.id.awayTeam_player);
            HomeTeam_Player_Name = (TextView)v.findViewById(R.id.homeTeam_player_name);
            HomeTeam_Player_Number = (TextView) v.findViewById(R.id.homeTeam_player_number);
            AwayTeam_Player_Name = (TextView)v.findViewById(R.id.awayTeam_player_name);
            AwayTeam_Player_Number = (TextView) v.findViewById(R.id.awayTeam_player_number);
        }
    }

    public class OtherInfoViewHolder extends RecyclerView.ViewHolder {
        public TextView HomeTeam_InfoType;
        public TextView HomeTeam_InfoText;
        public TextView AwayTeam_InfoType;
        public TextView AwayTeam_InfoText;

        public OtherInfoViewHolder(View v) {
            super(v);
            HomeTeam_InfoType = (TextView)v.findViewById(R.id.homeTeam_info_type);
            HomeTeam_InfoText = (TextView) v.findViewById(R.id.homeTeam_info_text);
            AwayTeam_InfoType = (TextView)v.findViewById(R.id.awayTeam_info_type);
            AwayTeam_InfoText = (TextView) v.findViewById(R.id.awayTeam_info_text);
        }
    }

    public PlayersAdapter(List<Player> playersHome, List<Player> playersAway, List<MatchOtherInfo> infosHome, List<MatchOtherInfo> infosAway, Context context) {
        this.playersHome = playersHome;
        this.playersAway = playersAway;
        this.infosHome = infosHome;
        this.infosAway = infosAway;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < 11) ? PLAYER_TYPE : OTHERINFO_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PLAYER_TYPE:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_players, parent, false);
                return new PlayerViewHolder(v1);
            case OTHERINFO_TYPE:
            default:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_otherinfos, parent, false);
                return new OtherInfoViewHolder(v2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case PLAYER_TYPE:
                PlayerViewHolder playerViewHolder = (PlayerViewHolder)holder;
                playerViewHolder.HomeTeam_Player.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PlayerDetailsActivity.class);
                        intent.putExtra("PLAYER_ID", playersHome.get(position).getId());
                        context.startActivity(intent);
                    }
                });
                playerViewHolder.AwayTeam_Player.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PlayerDetailsActivity.class);
                        intent.putExtra("PLAYER_ID", playersAway.get(position).getId());
                        context.startActivity(intent);
                    }
                });
                playerViewHolder.HomeTeam_Player_Name.setText(playersHome.get(position).getName());
                playerViewHolder.HomeTeam_Player_Number.setText(playersHome.get(position).getNumber());
                playerViewHolder.AwayTeam_Player_Name.setText(playersAway.get(position).getName());
                playerViewHolder.AwayTeam_Player_Number.setText(playersAway.get(position).getNumber());
                break;

            case OTHERINFO_TYPE:
                System.out.println(position);
                int offset = position-playersHome.size();
                if(offset >= 0){
                    OtherInfoViewHolder otherInfoViewHolder = (OtherInfoViewHolder)holder;
                    otherInfoViewHolder.HomeTeam_InfoType.setText(infosHome.get(position-playersHome.size()).InfoType);
                    otherInfoViewHolder.HomeTeam_InfoText.setText(infosHome.get(position-playersHome.size()).InfoText);
                    otherInfoViewHolder.AwayTeam_InfoType.setText(infosAway.get(position-playersAway.size()).InfoType);
                    otherInfoViewHolder.AwayTeam_InfoText.setText(infosAway.get(position-playersAway.size()).InfoText);
                    break;
                }

        }
    }

    @Override
    public int getItemCount() {
        return playersHome.size() + infosHome.size(); //home has same size of away, 11
    }
}