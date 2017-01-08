package matteobrienza.ppformazioni.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.interfaces.INotificationsAdapterEmpty;
import matteobrienza.ppformazioni.models.Notification;
import matteobrienza.ppformazioni.models.TeamStats;
import okhttp3.OkHttpClient;

/**
 * Created by Matteo on 06/01/2017.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private Context context;
    private INotificationsAdapterEmpty InotificationsAdapter;

    public static ProgressDialog dialog;

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView Notification_Title;
        private TextView Notification_Body;
        private TextView Notification_Date;
        private LinearLayout Notification_Remove;

        public NotificationViewHolder(View v) {
            super(v);
            Notification_Title = (TextView) v.findViewById(R.id.notification_title);
            Notification_Body = (TextView) v.findViewById(R.id.notification_body);
            Notification_Date = (TextView) v.findViewById(R.id.notification_date);
            Notification_Remove = (LinearLayout) v.findViewById(R.id.notification_remove);
        }
    }

    public NotificationsAdapter(List<Notification> s, Context c, INotificationsAdapterEmpty ina) {
        notifications = s;
        context = c;
        InotificationsAdapter = ina;

        if(Build.VERSION.SDK_INT >= 21) {
            dialog = new ProgressDialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        }else  dialog = new ProgressDialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }


    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, final int position) {

        holder.Notification_Title.setText(notifications.get(position).getTitle());
        holder.Notification_Body.setText(notifications.get(position).getBody());
        holder.Notification_Date.setText(notifications.get(position).getDate());

        holder.Notification_Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.traslucent)));
                dialog.setContentView(R.layout.dialog_progress);

                //NOTIFY THE DELETE TO THE SERVER
                RequestQueue queue = Volley.newRequestQueue(context);
                SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(context);
                StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, Constants.USERS_URL + "/" + state.getString("user_id", "1") + Constants.NOTIFICATIONS_URL + "/" + notifications.get(position).getId(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                notifications.remove(position);
                                notifyDataSetChanged();
                                if(notifications.size()==0)
                                InotificationsAdapter.setDefaultNotificationsIcons();
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(context, "Error while trying to delete notification. Check your network and try again!", Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(deleteRequest);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}