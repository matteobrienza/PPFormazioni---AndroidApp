package matteobrienza.ppformazioni.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import matteobrienza.ppformazioni.Constants;
import matteobrienza.ppformazioni.R;
import matteobrienza.ppformazioni.networking.CustomRequest;

public class LoginFragment extends Fragment {

    private static final String TWITTER_KEY = "DX5drsHV1XmCSyYDwGNx8yGly";
    private static final String TWITTER_SECRET = "qcZwmZfepCn3qZA1y3Aj09qDWFDfogQRQzb8RR4YhwjSUXLzZn";

    private static final float BACKOFF_MULT = 1.0f;
    private static final int TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 0;

    private Context context;
    public static SharedPreferences sp;
    public static ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_login, container, false);

        if(Build.VERSION.SDK_INT >= 21) {
            dialog = new ProgressDialog(getActivity(),android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        }else  dialog = new ProgressDialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        context = getActivity();
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final Fragment fragment = this;

        final String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println(token);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new TwitterCore(authConfig), new Digits.Builder().withTheme(R.style.AppTheme).build());

        Digits.clearActiveSession();

        DigitsAuthButton digitsButton = (DigitsAuthButton)v.findViewById(R.id.auth_button);
        digitsButton.setAuthTheme(R.style.AppTheme);
        digitsButton.setText("Login with Phone Number");
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, final String phoneNumber) {

                dialog.show();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.setContentView(R.layout.dialog_progress);

                try {

                    // Add OAuth Echo headers to request
                    TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
                    TwitterAuthToken authToken = session.getAuthToken();
                    DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
                    Map<String, String> headers = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();
                    headers.put("Content-Type","application/json");

                    RequestQueue queue = Volley.newRequestQueue(context);
                    System.out.println(Constants.USERS_URL);
                    CustomRequest authRequest = new CustomRequest(Request.Method.POST, Constants.USERS_URL, headers, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {

                            System.out.println("RISPOSTA AUTH RICEVUTA");

                            try {

                                final String jsonString;

                                jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                                System.out.println("AUTH RESPONSE: " + jsonString);

                                JSONObject digitsClient = new JSONObject(jsonString);

                                SharedPreferences.Editor editor_account_information = sp.edit();

                                editor_account_information.putString("user_id", digitsClient.getString("id"));
                                editor_account_information.putString("user_name", digitsClient.getString("username"));
                                editor_account_information.putString("auth_token", digitsClient.getString("authToken"));
                                editor_account_information.putString("phone_number", digitsClient.getString("phoneNumber"));
                                editor_account_information.commit();

                                FrameLayout fl = (FrameLayout)getActivity().findViewById(R.id.fragment_container_login);
                                fl.setVisibility(View.GONE);

                                HomeFragment fragment_home = new HomeFragment();
                                fragmentTransaction.replace(R.id.fragment_container, fragment_home);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();

                                Toast.makeText(getActivity(), "Authentication successful for " + phoneNumber, Toast.LENGTH_LONG).show();
                                dialog.dismiss();


                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_LONG).show();
                                System.exit(0);
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //editor_account_information.clear().commit();
                            dialog.dismiss();
                            System.out.println(error.toString());
                            Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            System.exit(0);
                        }
                    });

                    //PERFORM REQUEST
                    authRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
                    queue.add(authRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_LONG).show();
                    //finish();
                    System.exit(0);
                }
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });


        return v;
    }

}
