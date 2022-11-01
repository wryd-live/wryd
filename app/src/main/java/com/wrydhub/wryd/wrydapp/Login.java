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
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);


        String username = sp.getString("user",null);

        if(username!=null)
        {
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_login);
        Button b = (Button)findViewById(R.id.loginbutton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: Button Clicked");

                EditText et = (EditText)findViewById(R.id.editTextPersonName);
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