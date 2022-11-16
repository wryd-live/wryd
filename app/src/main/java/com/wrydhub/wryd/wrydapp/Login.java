package com.wrydhub.wryd.wrydapp;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);


        String token = sp.getString("token",null);


        // TODO: Fix This
        if(token!=null)
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Authorization","Bearer "+token)
                    .url(keysConfig.wrydServerURL+"/api/user/checkToken")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());

                Intent intent = new Intent(Login.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);


            } catch (IOException e) {
                e.printStackTrace();
            }



            return;
        }

        setContentView(R.layout.activity_login);
        Button b = (Button)findViewById(R.id.loginbuttonLogin);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: Button Clicked");

                EditText et = (EditText)findViewById(R.id.emailInputSignUp);
                String inp = et.getText().toString();
                Log.d(TAG, "input: "+inp);

                String rgx = "^[A-Za-z0-9]+$";
                Log.d(TAG, "pattern: "+Pattern.matches(rgx,inp));

                if(!Pattern.matches(rgx,inp))
                {
                    Toast.makeText(getApplicationContext(),"Invalid Name",Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("user",inp);
                    editor.apply();


                    Intent intent = new Intent(Login.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();

                }

            }
        });




    }
}