package matteobrienza.ppformazioni;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;
import matteobrienza.ppformazioni.FCM.MyFirebaseMessagingService;
import matteobrienza.ppformazioni.fragments.HomeFragment;
import matteobrienza.ppformazioni.fragments.LoginFragment;
import matteobrienza.ppformazioni.fragments.MyFantaFootballFragment;
import matteobrienza.ppformazioni.fragments.NotificationFragment;
import matteobrienza.ppformazioni.fragments.StatsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar Toolbar_top;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/lineups_update");
        System.out.println("FCM-TOKEN:  " + token);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.list_view_drawer);
        Toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(Toolbar_top);
        Toolbar_top.setTitleTextColor(Color.WHITE);
        Toolbar_top.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        navView.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, drawer, Toolbar_top, R.string.app_name, R.string.app_name);
        mToggle.setDrawerIndicatorEnabled(false);
        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.parseColor("#00000000"));
                }

            }
        });
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        final SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent launchIntent = null;
        String userName = state.getString("user_name", null);
        System.out.println(userName);
        String activity = null;
        if (userName != null) {
            FrameLayout fl = (FrameLayout)findViewById(R.id.fragment_container_login);
            fl.setVisibility(View.GONE);
            HomeFragment fragment_home = new HomeFragment();
            fragmentTransaction.replace(R.id.fragment_container, fragment_home);
            fragmentTransaction.commit();
        }else{
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.replace(R.id.fragment_container_login, loginFragment);
            fragmentTransaction.commit();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_home:
                        HomeFragment fragment_home = new HomeFragment();
                        fragmentTransaction.replace(R.id.fragment_container, fragment_home);
                        break;
                    case R.id.action_stats:
                        StatsFragment fragment_stats = new StatsFragment();
                        fragmentTransaction.replace(R.id.fragment_container, fragment_stats);
                        break;
                    case R.id.action_myfanta:
                        MyFantaFootballFragment fragment_myfanta = new MyFantaFootballFragment();
                        fragmentTransaction.replace(R.id.fragment_container, fragment_myfanta);
                        break;
                    case R.id.action_notifications:
                        NotificationFragment fragment_notifications = new NotificationFragment();
                        fragmentTransaction.replace(R.id.fragment_container, fragment_notifications);
                }
                fragmentTransaction.commit();
                return true;
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("lineups-update"));
    }

    // handler for received Intents for the "lineups-update" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Notification Received: lineups-updated!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
