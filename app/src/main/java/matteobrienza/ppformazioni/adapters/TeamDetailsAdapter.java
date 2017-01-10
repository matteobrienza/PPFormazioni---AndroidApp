package matteobrienza.ppformazioni.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import matteobrienza.ppformazioni.PlayerDetailsActivity;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.MatchOtherInfo;
import matteobrienza.ppformazioni.models.Player;
import matteobrienza.ppformazioni.models.Team;

/**
 * Created by Matteo on 10/01/2017.
 */

public class TeamDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Team team;
    private List<Player> players;
    private Context context;

    private static final int TEAM_TYPE = 0;
    private static final int PLAYER_HEADER_TYPE = 1;
    private static final int PLAYER_TYPE = 2;


    public class TeamViewHolder extends RecyclerView.ViewHolder {
        public ImageView Team_Avatar;
        public TextView Team_Name;
        public TextView Team_MarketValue;

        public TeamViewHolder(View v) {
            super(v);
            Team_Avatar = (ImageView)v.findViewById(R.id.team_avatar);
            Team_Name = (TextView)v.findViewById(R.id.team_name);
            Team_MarketValue = (TextView)v.findViewById(R.id.team_marketvalue);
        }
    }

    public class PlayerHeaderViewHolder extends RecyclerView.ViewHolder {

        public PlayerHeaderViewHolder(View v) {
            super(v);

        }
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout Team_Player_Layout;
        public TextView Team_Player_Number;
        public TextView Team_Player_Name;

        public PlayerViewHolder(View v) {
            super(v);
            Team_Player_Layout = (LinearLayout)v.findViewById(R.id.item_team_player_layout);
            Team_Player_Number = (TextView)v.findViewById(R.id.item_team_player_name);
            Team_Player_Name = (TextView)v.findViewById(R.id.item_team_player_number);
        }
    }


    public TeamDetailsAdapter(Team t, Context context) {
        team = t;
        this.context = context;
        players = t.Players;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0) ? TEAM_TYPE : (position==1) ? PLAYER_HEADER_TYPE : PLAYER_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TEAM_TYPE:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
                return new TeamViewHolder(v0);
            case PLAYER_HEADER_TYPE:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_player_header, parent, false);
                return new PlayerHeaderViewHolder(v1);
            case PLAYER_TYPE:
            default:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_player, parent, false);
                return new PlayerViewHolder(v2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TEAM_TYPE:
                TeamViewHolder teamViewHolder = (TeamViewHolder)holder;
                downloadImage(teamViewHolder.Team_Avatar,team.getAvatar());
                teamViewHolder.Team_Name.setText(team.getFullName());
                teamViewHolder.Team_MarketValue.setText("Market Value: \n" +  team.getMarketValue());
                break;

            case PLAYER_HEADER_TYPE:
                PlayerHeaderViewHolder playerHeaderViewHolder = (PlayerHeaderViewHolder)holder;
                //DO NOTHING
                break;

            case PLAYER_TYPE:
                PlayerViewHolder playerViewHolder = (PlayerViewHolder)holder;
                playerViewHolder.Team_Player_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PlayerDetailsActivity.class);
                        intent.putExtra("PLAYER_ID", players.get(position-2).getId());
                        context.startActivity(intent);
                    }
                });
                playerViewHolder.Team_Player_Name.setText(players.get(position-2).getName());
                playerViewHolder.Team_Player_Number.setText(players.get(position-2).getNumber());
                break;

        }
    }



    @Override
    public int getItemCount() {
        return team.Players.size() + 2;
    }

    public void downloadImage(final ImageView im, final String URL){

        RequestCreator request = Picasso.with(im.getContext()).load(URL);

        request.networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.ic_football)
                .placeholder(R.drawable.circle_big)
                .into(im, new Callback() {

                    /*
                    Picasso will keep looking for it offline in cache and fail,
                    the following code looks at the local cache, if not found offline,
                    it goes online and replenishes the cache
                    */

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        RequestCreator request2 = Picasso.with(im.getContext()).load(URL);

                        request2.error(R.drawable.ic_football).placeholder(R.drawable.circle_big).into(im, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                            }
                        });

                    }
                });

    }
}