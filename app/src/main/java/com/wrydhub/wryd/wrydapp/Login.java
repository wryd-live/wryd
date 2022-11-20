package com.wrydhub.wryd.wrydapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    boolean isOrganizationSelected = false;
    int selectedOrganizationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences shredpreff = getSharedPreferences("login",MODE_PRIVATE);


        String savedToken = shredpreff.getString("token",null);
        if(savedToken!=null)
        {
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
//            Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
            return;
        }

        /*


        // TODO: Fix This
        if(savedToken!=null)
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Authorization","Bearer "+savedToken)
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




         */
        setContentView(R.layout.activity_login);


        Button organizationButton = findViewById(R.id.selectOrganizationButtonLogin);
        Button loginButton = (Button)findViewById(R.id.loginbuttonLogin);
        TextView organizationTxtView = findViewById(R.id.organizationTextViewLogin);
        TextView registerTxtView = (TextView)findViewById(R.id.registerTextView);


        organizationButton.setOnClickListener(view -> {
            List<String> organizationNames = new ArrayList<>();
            List<Integer> organizationId = new ArrayList<>();

            Thread myt = new Thread(
                    () -> {
                        OkHttpClient client = new OkHttpClient();
                        String orgAllURL = keysConfig.wrydServerURL+"/api/organization/all";
                        Request request = new Request.Builder()
                                .url(orgAllURL)
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            String myResponse = response.body().string();
                            JSONArray Orgdata = new JSONArray(myResponse);

                            for(int i=0;i<Orgdata.length();i++)
                            {
                                JSONObject zv = (JSONObject) Orgdata.get(i);
                                String orgName = zv.getString("name");
                                String orgId = zv.getString("id");
                                organizationNames.add(orgName);
                                organizationId.add(Integer.parseInt(orgId));
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            myt.start();
            try {
                myt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String organizationStringNames[]= organizationNames.toArray(new String[organizationNames.size()]);


            final int[] selectedItem = {-1};

            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose an animal");// add a radio button list
            int checkedItem = 0; // cow
            builder.setSingleChoiceItems(organizationStringNames, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // user checked an item
                    selectedItem[0] = which;
                    Log.d(TAG, "onClick: "+which);
                }
            });// add OK and Cancel buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(selectedItem[0]>=0 && selectedItem[0]<organizationNames.size())
                    {
                        organizationTxtView.setText(organizationNames.get(selectedItem[0]));
                        selectedOrganizationId = organizationId.get(selectedItem[0]);
                        Log.d(TAG, "onClick: Selected OrgId "+selectedOrganizationId);
                    }
                }
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                selectedItem[0] = -1;
            });// create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();


            System.out.println("Organizations Fetched");


        });


        loginButton.setOnClickListener(view -> {

            Log.d(TAG, "onClick: Button Clicked");

            EditText et = findViewById(R.id.personNameSignUp);
            EditText pt = findViewById(R.id.passwordInputSignup);

            String inpEmail = et.getText().toString();
            String inpPass = pt.getText().toString();

            if(inpEmail.equals("") || inpPass.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Empty Email|Password",Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "input: "+inpEmail);

            Log.d(TAG, "pattern: "+validateEmail(inpEmail));

            if(!validateEmail(inpEmail))
            {
                Toast.makeText(getApplicationContext(),"Invalid Email Input",Toast.LENGTH_SHORT).show();
            }
            else {


                try {
                    JSONObject myPostDat = new JSONObject();
                    myPostDat.put("email", inpEmail);
                    myPostDat.put("password", inpPass);

                    String jsonPost = myPostDat.toString();

                    Thread myNetThread = new Thread(() -> {

                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient();
                        String loginURL = keysConfig.wrydServerURL + "/api/login";

                        RequestBody body = RequestBody.create(JSON,jsonPost);
                        Request request = new Request.Builder()
                                .url(loginURL)
                                .post(body)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String responseBodyString = response.body().string();
                            Log.d(TAG, "respone: "+responseBodyString);
                            if(response.code()==200)
                            {
                                JSONObject myResponseJson = new JSONObject(responseBodyString);

                                String userId = myResponseJson.getString("userid");
                                String organizationId = myResponseJson.getString("orgid");
                                String jwtToken = myResponseJson.getString("token");
//
//
                                Log.d(TAG, "onCreate: "+userId);
                                Log.d(TAG, "onCreate: "+organizationId);
                                Log.d(TAG, "onCreate: "+jwtToken);


                                String organizationURL = keysConfig.wrydServerURL + "/api/organization/view/" + organizationId;
                                Request orgRequest = new Request.Builder()
                                        .url(organizationURL)
                                        .build();

                                    Response orgResponse = client.newCall(orgRequest).execute();

                                    String orgResponseString = orgResponse.body().string();
                                Log.d(TAG, "orgResponse: "+orgResponseString);
                                if(response.code()==200)
                                {
                                    JSONObject orgResponseJson = new JSONObject(orgResponseString);

                                    String orgUsername = orgResponseJson.getString("username");
                                    String orgName = orgResponseJson.getString("name");
                                    String orgDomain = orgResponseJson.getString("domain");



                                    SharedPreferences.Editor editor = shredpreff.edit();
                                    editor.putString("userid", userId);
                                    editor.putString("orgid",organizationId);
                                    editor.putString("orgUsername",orgUsername);
                                    editor.putString("orgName",orgName);
                                    editor.putString("orgDomain",orgDomain);
                                    editor.putString("token",jwtToken);
                                    editor.apply();

                                    runOnUiThread(()->{

                                        Intent intent = new Intent(Login.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();

                                    });
                                }
                                else
                                {
                                    runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), "Invalid Email/Password/Org", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                            else
                            {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Invalid Email/Password", Toast.LENGTH_SHORT).show();
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                            runOnUiThread(() -> {
                                Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            });
                        }

                    });
                    myNetThread.start();
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

        });

        registerTxtView.setOnClickListener(view -> {
            System.out.println("Register Text View Clicked");

            Intent intent = new Intent(Login.this,SignUp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });




    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}