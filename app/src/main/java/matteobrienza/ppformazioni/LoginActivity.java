package matteobrienza.ppformazioni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.Fabric;
import matteobrienza.ppformazioni.networking.CustomRequest;

public class LoginActivity extends AppCompatActivity {

    private static final String TWITTER_KEY = "DX5drsHV1XmCSyYDwGNx8yGly";
    private static final String TWITTER_SECRET = "qcZwmZfepCn3qZA1y3Aj09qDWFDfogQRQzb8RR4YhwjSUXLzZn";

    private static final float BACKOFF_MULT = 1.0f;
    private static final int TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 0;

    private Context context;
    public static SharedPreferences sp;
    public static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Build.VERSION.SDK_INT >= 21) {
            dialog = new ProgressDialog(this,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        }else  dialog = new ProgressDialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        context = this;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().withTheme(R.style.AppTheme).build());

        Digits.clearActiveSession();

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
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


                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                i.putExtra("phone_number", digitsClient.getString("phoneNumber"));
                                i.putExtra("user_name", digitsClient.getString("username"));
                                i.putExtra("user_id", digitsClient.getString("id"));
                                i.putExtra("auth_token", digitsClient.getString("authToken"));

                                LoginActivity.this.finish();
                                startActivity(i);
                                Toast.makeText(getApplicationContext(), "Authentication successful for " + phoneNumber, Toast.LENGTH_LONG).show();
                                dialog.dismiss();


                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                                finish();
                                System.exit(0);
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //editor_account_information.clear().commit();
                            dialog.dismiss();
                            System.out.println(error.toString());
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            finish();
                            System.exit(0);
                        }
                    });

                    //PERFORM REQUEST
                    authRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
                    queue.add(authRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                    finish();
                    System.exit(0);
                }
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });

    }
}
