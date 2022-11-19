package com.wrydhub.wryd.wrydapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileView extends AppCompatActivity {


    String savedToken;

    ProgressDialog progress;


    int personId;

    ImageView profileImgView;
    TextView userNameTxtView;
    TextView emailTxtView;
    TextView orgTxtView;


    LinearLayout acc_rej_group;
    LinearLayout profileViewPanel;
    Button accept_req_btn;
    Button reject_req_btn;


    Button send_req_btn ;
    Button cancel_req_btn;
    Button unfriend_button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);

        String savedUsername = sp.getString("userid",null);
        String savedOrganization = sp.getString("orgUsername",null);
        String savedToken = sp.getString("token",null);

        if(savedUsername==null)
        {
            finishAndRemoveTask();
            return;
        }

        this.savedToken = savedToken;





        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();

        personId = intent.getIntExtra("personid",-1);

        profileViewPanel = findViewById(R.id.profileViewPanel);

        profileImgView =  findViewById(R.id.profileImageView);
        userNameTxtView =  findViewById(R.id.userNameTextView);
        emailTxtView = findViewById(R.id.emailTextView);
        orgTxtView = findViewById(R.id.organizationTextView);


        acc_rej_group = findViewById(R.id.acc_rej_group_btn);
        accept_req_btn =  findViewById(R.id.accept_request_button);
        reject_req_btn = findViewById(R.id.reject_request_button);


        send_req_btn = findViewById(R.id.send_request_button);
        cancel_req_btn = findViewById(R.id.cancel_request_button);
        unfriend_button = findViewById(R.id.un_friend_button);



        accept_req_btn.setOnClickListener(view -> {
            String url = keysConfig.wrydServerURL + "/api/profile/request/accept/" + personId;

            requestAPICall(url);
        });

        reject_req_btn.setOnClickListener(view -> {
            String url = keysConfig.wrydServerURL + "/api/profile/request/reject/" + personId;

            requestAPICall(url);

        });

        send_req_btn.setOnClickListener(view -> {
            String url = keysConfig.wrydServerURL + "/api/profile/request/send/" + personId;
            requestAPICall(url);

        });

        cancel_req_btn.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: Clicked Unfriend");

            String url = keysConfig.wrydServerURL + "/api/profile/request/cancel/" + personId;
            requestAPICall(url);

        });

        unfriend_button.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: Clicked Unfriend");

            String url = keysConfig.wrydServerURL + "/api/profile/unfriend/" + personId;
            requestAPICall(url);
        });


        getUserViewDetails(personId);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void getUserViewDetails(int profileId)
    {
        showLoading();
        OkHttpClient client = new OkHttpClient();
        String url = keysConfig.wrydServerURL + "/api/profile/view/" + profileId;
        System.out.println("my url ===== "+url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+savedToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(() -> {
                    stopLoading();
//                    stopShimmer();
                    Toast.makeText(getApplicationContext(), "Unable To Delete Notification", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    if(response.isSuccessful())
                    {
                        Log.d(TAG, "=================================");

                        String result = response.body().string();
                        Log.d(TAG, "onResponse: "+result);

                        JSONObject res = new JSONObject(result);
                        String pName =  res.getString("person_name");
                        String imgUrl = res.getString("imageurl");
                        String person_email = res.getString("person_email");
                        boolean isFriend = Boolean.parseBoolean(res.getString("friend"));
                        boolean isRequest = Boolean.parseBoolean(res.getString("request"));
                        String requestType = res.getString("request-type");


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
//                                    stopShimmer();


                                //Updating Person Profile
                                userNameTxtView.setText(pName);
                                emailTxtView.setText(person_email);
                                orgTxtView.setText("Organization");

                                String myImgUrl = "https://api.multiavatar.com/"+ pName +".png";
                                Glide
                                    .with(getApplicationContext())
                                    .load(myImgUrl)
                                    .into(profileImgView);

                                if(isFriend)
                                {
                                    //Show Unfriend
                                    showRequestButtons(3);
                                }
                                else
                                {
                                    if(isRequest)
                                    {
                                        if(Objects.equals(requestType, "incoming"))
                                        {
                                            //Person Has Sent you a request
                                            showRequestButtons(0);
                                        }
                                        else
                                        {
                                            //Show Cancel Request
                                            showRequestButtons(2);
                                        }
                                    }
                                    else
                                    {
                                        //Show SendRequest
                                        showRequestButtons(1);
                                    }
                                }


                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(() -> {
                            stopLoading();
    //                        stopShimmer();
                            try {
                                Toast.makeText(getApplicationContext(), "Error Retrieving Profile" + response.body().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    runOnUiThread(() -> {
                        stopLoading();
                        //                        stopShimmer();
                        try {
                            Toast.makeText(getApplicationContext(), "Error" + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
    }



    void showRequestButtons(int n)
    {
        //0,1,2,3
        acc_rej_group.setVisibility(View.GONE);
        send_req_btn.setVisibility(View.GONE);
        cancel_req_btn.setVisibility(View.GONE);
        unfriend_button.setVisibility(View.GONE);


        if(n==0)
        {
            acc_rej_group.setVisibility(View.VISIBLE);
        }
        else if(n==1)
        {
            send_req_btn.setVisibility(View.VISIBLE);
        }
        else if(n==2)
        {
            cancel_req_btn.setVisibility(View.VISIBLE);
        }
        else if(n==3)
        {
            unfriend_button.setVisibility(View.VISIBLE);
        }
    }


    void showLoading()
    {
        profileViewPanel.setVisibility(View.INVISIBLE);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Fetching data from server....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    void stopLoading()
    {
        progress.hide();
        profileViewPanel.setVisibility(View.VISIBLE);
    }


    void requestAPICall(String url)
    {
        showLoading();

        OkHttpClient client = new OkHttpClient();
//        String url = keysConfig.wrydServerURL + "/api/profile/accept/" + personId;
        System.out.println("my url ===== "+url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+savedToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(() -> {
                    stopLoading();
//                    stopShimmer();
                    Toast.makeText(getApplicationContext(), "Unable To Accept Request", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    if(response.isSuccessful())
                    {
                        Log.d(TAG, "=================================");

                        String result = response.body().string();
                        Log.d(TAG, "onResponse: "+result);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();

                                getUserViewDetails(personId);
//                                    stopShimmer();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    runOnUiThread(() -> {
                        stopLoading();
                        try {
                            Toast.makeText(getApplicationContext(), "Error" + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });

    }
}