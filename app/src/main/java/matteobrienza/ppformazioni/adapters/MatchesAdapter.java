package matteobrienza.ppformazioni.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import matteobrienza.ppformazioni.MatchDetailsActivity;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.Match;
import okhttp3.OkHttpClient;


/**
 * Created by Matteo on 03/01/2017.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchViewHolder> {

    private List<Match> matches;
    private Context context;

    public class MatchViewHolder extends RecyclerView.ViewHolder {
        public TextView MatchDate;
        public CardView CardView;
        public TextView HomeTeam_Name;
        public ImageView HomeTeam_Avatar;
        public TextView AwayTeam_Name;
        public ImageView AwayTeam_Avatar;

        public MatchViewHolder(View v) {
            super(v);
            MatchDate = (TextView)v.findViewById(R.id.match_date);
            HomeTeam_Name = (TextView)v.findViewById(R.id.homeTeam_name);
            HomeTeam_Avatar = (ImageView) v.findViewById(R.id.homeTeam_avatar);
            AwayTeam_Name = (TextView)v.findViewById(R.id.awayTeam_name);
            AwayTeam_Avatar = (ImageView) v.findViewById(R.id.awayTeam_avatar);
            CardView = (CardView)v.findViewById(R.id.card_match);
        }
    }

    public MatchesAdapter(List<Match> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_header, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        final Match match = matches.get(position);

        holder.CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MatchDetailsActivity.class);
                intent.putExtra("MATCH_ID", Integer.toString(match.getId()));
                intent.putExtra("MATCH_HEADER", match.getHomeTeam_Name() + " - " + match.getAwayTeam_Name());
                context.startActivity(intent);
            }
        });

        holder.HomeTeam_Name.setText(match.getHomeTeam_Name());
        holder.AwayTeam_Name.setText(match.getAwayTeam_Name());

        holder.MatchDate.setText(match.getMatchDate());

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

        builder.build().load(match.getHomeTeam_Avatar()).error(R.drawable.ic_football).into(holder.HomeTeam_Avatar);
        builder.build().load(match.getAwayTeam_Avatar()).error(R.drawable.ic_football).into(holder.AwayTeam_Avatar);


    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}