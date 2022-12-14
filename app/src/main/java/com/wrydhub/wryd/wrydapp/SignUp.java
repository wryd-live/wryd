package com.wrydhub.wryd.wrydapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class SignUp extends AppCompatActivity {


    boolean isOrganizationSelected = false;
    int selectedOrganizationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        final SharedPreferences shredpreff = getSharedPreferences("login",MODE_PRIVATE);



        Button organizationButton = findViewById(R.id.selectOrganizationButtonSignUp);
        Button loginButton = (Button)findViewById(R.id.registerbuttonSignUp);
        TextView organizationTxtView = findViewById(R.id.organizationTextViewSignUp);
        TextView loginTxtView = (TextView)findViewById(R.id.alreadyHaveAnAccountSignup);


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
            builder.setTitle("Choose an organization");// add a radio button list
            int checkedItem = -1; // cow
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


        loginTxtView.setOnClickListener(view -> {
            System.out.println("Register Text View Clicked");

            Intent intent = new Intent(this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });



        loginButton.setOnClickListener(view -> {

            if(selectedOrganizationId==-1)
            {
                Toast.makeText(getApplicationContext(),"Please Select Organization before Registering",Toast.LENGTH_SHORT).show();
                return;
            }


            EditText personName = findViewById(R.id.personNameSignUp);
            EditText et = findViewById(R.id.emailInputSigup);
            EditText pt = findViewById(R.id.passwordInputSignup);
            EditText renterpt = findViewById(R.id.reenterPassSignUp);

            String inpEmail = et.getText().toString().trim();
            String inpPass = pt.getText().toString();
            String pName = personName.getText().toString().trim();
            String inpRenterPass = renterpt.getText().toString();

            if(inpEmail.equals("") || inpPass.equals("") || !inpRenterPass.equals(inpPass) || pName.equals(""))
            {
                Toast.makeText(getApplicationContext(),"Empty Email|Password| Passwords didn't match",Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "input: "+inpEmail);

            Log.d(TAG, "pattern: "+validateEmail(inpEmail));

            if(!validateEmail(inpEmail))
            {
                Toast.makeText(getApplicationContext(),"Invalid Email Input",Toast.LENGTH_SHORT).show();
                return;
            }


            try {
                JSONObject myPostDat = new JSONObject();
                myPostDat.put("name",pName);
                myPostDat.put("email", inpEmail);
                myPostDat.put("password", inpPass);
                myPostDat.put("organization_id", selectedOrganizationId);

                String jsonPost = myPostDat.toString();

                Thread myNetThread = new Thread(() -> {

                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");

                    OkHttpClient client = new OkHttpClient();
                    String loginURL = keysConfig.wrydServerURL + "/api/user/create";

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

                            runOnUiThread(()->{

                                Toast.makeText(getApplicationContext(), "User Registered Successfully. Please Verify your mail", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(this,Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            });

                        }
                        else
                        {
                            JSONObject myResponseJson = new JSONObject(responseBodyString);

                            String errCode = myResponseJson.getString("code");
                            String errMsg = myResponseJson.getString("msg");

                            System.out.println(errCode+ " -> "+ errMsg);

                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Invalid Email/Password", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Something Went Wrong! Try Again", Toast.LENGTH_SHORT).show();
                        });
                    }

                });
                myNetThread.start();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}