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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import matteobrienza.ppformazioni.MatchDetailsActivity;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.models.Match;
import okhttp3.OkHttpClient;


/**
 * Created by Matteo on 03/01/2017.
 */

public class MatchesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Match> matches;
    private String day;
    private Context context;

    private static final int HEADER_TYPE = 0;
    private static final int MATCHES_TYPE = 1;

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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView Day;

        public HeaderViewHolder(View v) {
            super(v);
            Day = (TextView)v.findViewById(R.id.day);
        }
    }

    public MatchesAdapter(List<Match> matches, String day, Context context) {
        this.day = day;
        this.matches = matches;
        this.context = context;
    }

    public void setDay(String day){
        this.day = day;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_header, parent, false);
                return new MatchesAdapter.HeaderViewHolder(v1);
            case MATCHES_TYPE:
            default:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_header, parent, false);
                return new MatchesAdapter.MatchViewHolder(v2);
        }
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case HEADER_TYPE:
                HeaderViewHolder holder_header = (HeaderViewHolder)holder;
                holder_header.Day.setText(day);
                break;
            case MATCHES_TYPE:
                final Match match = matches.get(position);
                MatchViewHolder holder_match = (MatchViewHolder)holder;
                holder_match.CardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MatchDetailsActivity.class);
                        intent.putExtra("MATCH_ID", Integer.toString(match.getId()));
                        intent.putExtra("MATCH_HEADER", match.getHomeTeam_Name() + " - " + match.getAwayTeam_Name());
                        context.startActivity(intent);
                    }
                });
                holder_match.HomeTeam_Name.setText(match.getHomeTeam_Name());
                holder_match.AwayTeam_Name.setText(match.getAwayTeam_Name());
                holder_match.MatchDate.setText(match.getMatchDate());
                downloadImage(holder_match.HomeTeam_Avatar, match.getHomeTeam_Avatar());
                downloadImage(holder_match.AwayTeam_Avatar, match.getAwayTeam_Avatar());
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? HEADER_TYPE : MATCHES_TYPE;
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public void downloadImage(final ImageView im, final String URL){

        RequestCreator request = Picasso.with(im.getContext()).load(URL);

        request.networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.ic_football)
                .placeholder(R.drawable.circle)
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

                        request2.error(R.drawable.ic_football).placeholder(R.drawable.circle).into(im, new Callback() {
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