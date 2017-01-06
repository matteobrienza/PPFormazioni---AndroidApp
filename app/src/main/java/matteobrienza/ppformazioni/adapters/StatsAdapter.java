package matteobrienza.ppformazioni.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.TeamStats;
import okhttp3.OkHttpClient;

/**
 * Created by Matteo on 03/01/2017.
 */

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatViewHolder> {

    private List<TeamStats> stats;
    private Context context;

    public class StatViewHolder extends RecyclerView.ViewHolder {

        private ImageView Team_avatar;
        private TextView Team_name;
        private TextView Team_points;
        private TextView Team_games;
        private TextView Team_wins;
        private TextView Team_draws;
        private TextView Team_losts;

        public StatViewHolder(View v) {
            super(v);
            Team_avatar = (ImageView) v.findViewById(R.id.stats_team_avatar);
            Team_name = (TextView) v.findViewById(R.id.stats_team_name);
            Team_points = (TextView) v.findViewById(R.id.stats_team_points);
            Team_games = (TextView) v.findViewById(R.id.stats_team_games);
            Team_wins = (TextView) v.findViewById(R.id.stats_team_wins);
            Team_draws = (TextView) v.findViewById(R.id.stats_team_draws);
            Team_losts = (TextView) v.findViewById(R.id.stats_team_losts);
        }
    }

    public StatsAdapter(List<TeamStats> s, Context c) {
        stats = s;
        context = c;
    }


    @Override
    public StatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statdata, parent, false);
        return new StatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatViewHolder holder, int position) {
        holder.Team_name.setText(stats.get(position).getTeamName());
        holder.Team_games.setText(stats.get(position).getMatchGames());
        holder.Team_points.setText(stats.get(position).getPoints());
        holder.Team_wins.setText(stats.get(position).getMatchWins());
        holder.Team_draws.setText(stats.get(position).getMatchDraws());
        holder.Team_losts.setText(stats.get(position).getMatchLosts());

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });
        OkHttpClient client = new OkHttpClient();
        builder.downloader(new OkHttp3Downloader(client));

        builder.build().load(stats.get(position).getTeamAvatar()).error(R.drawable.ic_football).into(holder.Team_avatar);
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }
}