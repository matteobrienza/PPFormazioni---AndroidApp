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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.BackgroundPriorityRunnable;
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
    private BottomNavigationView BottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("lineups_update");
        System.out.println("FCM-TOKEN:  " + token);

        BottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
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

        BottomNavigation.setItemIconTintList(null);
        BottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
    public boolean onNavigationItemSelected(MenuItem item){

        final SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        switch(item.getItemId()){
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.logout:
                state.edit().clear().commit();
                clearApplicationData();
                MainActivity.this.finish();
                break;
        }

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

        //Setup Username and Number
        final SharedPreferences state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        View header = navView.getHeaderView(0);
        TextView navHeader_phoneNumber = (TextView) header.findViewById(R.id.phone_number);

        String phone_number = state.getString("phone_number", null);

        TextView navHeader_userName = (TextView) header.findViewById(R.id.name);

        String userName = state.getString("user_name", null);

        navHeader_phoneNumber.setText(phone_number);

        navHeader_userName.setText(userName);

        boolean isThereNotifications = state.getBoolean("notifications",false);

        if(isThereNotifications){
            Menu m = BottomNavigation.getMenu();
            m.findItem(R.id.action_notifications).setIcon(R.drawable.ic_notifications_active_24dp);
        }
    }


    // handler for received Intents for the "lineups-update" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Notification Received: lineups-updated!", Toast.LENGTH_LONG).show();
            Menu m = BottomNavigation.getMenu();
            m.findItem(R.id.action_notifications).setIcon(R.drawable.ic_notifications_active_24dp);
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
