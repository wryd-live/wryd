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
import android.widget.Toast;

import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileView extends AppCompatActivity {


    String savedToken;

    ProgressDialog progress;


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

        int personId = intent.getIntExtra("personid",-1);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Fetching data from server....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

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
                    progress.dismiss();
//                    stopShimmer();
                    Toast.makeText(getApplicationContext(), "Unable To Delete Notification", Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.d(TAG, "=================================");

                    String result = response.body().string();
                    Log.d(TAG, "onResponse: "+result);




                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                progress.dismiss();
//                                stopShimmer();

                        }
                    });
                }
                else
                {
                    runOnUiThread(() -> {
                        progress.dismiss();
//                        stopShimmer();
                        try {
                            Toast.makeText(getApplicationContext(), "Error Dismissing Notification" + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}