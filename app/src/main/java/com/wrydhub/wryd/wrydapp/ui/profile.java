package com.wrydhub.wryd.wrydapp.ui;
//package com.example.bottomsheetlayout;
//
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wrydhub.wryd.wrydapp.MainActivity;
import com.wrydhub.wryd.wrydapp.R;
import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userid";
    private static final String ARG_PARAM2 = "orgUsername";
    private static final String ARG_PARAM3 = "token";

    // TODO: Rename and change types of parameters
    private String savedUserid;
    private String savedOrgUsername;
    private String savedUserToken;


    Button bottomsheet;

    ProgressDialog progress;


    ImageView profileImageView;
    TextView nameTextView;
    TextView emailTextView;


    String picturePath = "";

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;



    public profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            savedUserid = getArguments().getString(ARG_PARAM1);
            savedOrgUsername = getArguments().getString(ARG_PARAM2);
            savedUserToken = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile,container,false);



        profileImageView = root.findViewById(R.id.profileImageView);
        nameTextView = root.findViewById(R.id.userNameTextView);
        emailTextView = root.findViewById(R.id.emailTextView);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        CircleImageView chngImgBtn = root.findViewById(R.id.change_image_button);
        chngImgBtn.setOnClickListener(view -> {

            showDialog();

        });


        Button logoutButton = root.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            SharedPreferences settings = getContext().getSharedPreferences("login", getContext().MODE_PRIVATE);
            settings.edit().clear().commit();
            getActivity().finishAndRemoveTask();
        });
        // Inflate the layout for this fragment



        Button selectImageButton = root.findViewById(R.id.button_select);
        Button uploadImageButton = root.findViewById(R.id.button_upload);

        uploadImageButton.setOnClickListener(view -> {
            uploadFromFile();
        });


        selectImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,100);
        });


        getUserViewDetails();

        return root;
    }


    public void uploadFromFile()
    {
        Uri fileUri = Uri.fromFile(new File(picturePath));
        StorageReference imageRef3 = storageReference.child("images/"+fileUri.getLastPathSegment());

        UploadTask uploadTask2 = imageRef3.putFile(fileUri);

        uploadTask2.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error uploading image.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: uri= "+ uri.toString());

                        stopLoading();
                        uploadPhotoAPI(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to Get Image URL.", Toast.LENGTH_SHORT).show();
                        stopLoading();

                    }
                });

            }
        });
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                Uri selectedImage = data.getData();
                String [] filePathCols = {MediaStore.Images.Media.DATA};
                if(selectedImage!=null)
                {
                    Cursor cursor = getContext().getContentResolver().query(selectedImage,filePathCols,null,null,null);
                    if(cursor!=null)
                    {
                        cursor.moveToFirst();
                        int colIndex = cursor.getColumnIndex(filePathCols[0]);
                        picturePath = cursor.getString(colIndex);
                        profileImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        cursor.close();

                        if(!Objects.equals(picturePath, ""))
                        {
                            showLoading();
                            uploadFromFile();
                        }
                        else
                        {
                            //Toast
                            Toast.makeText(getContext(),"Unable to Upload Image",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }














    private void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);

        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
//                Toast.makeText(getContext(),"Remove is Clicked",Toast.LENGTH_SHORT).show();
                deletePhotoAPI();

            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
//                Toast.makeText(getContext(),"Upload is Clicked",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,100);

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    void showLoading()
    {
        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Fetching profile data from server....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    void stopLoading()
    {
        progress.hide();
    }


    void getUserViewDetails()
    {
        showLoading();
        OkHttpClient client = new OkHttpClient();
        String url = keysConfig.wrydServerURL + "/api/user/view";
        System.out.println("my url ===== "+url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+savedUserToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                getActivity().runOnUiThread(() -> {
                    stopLoading();
//                    stopShimmer();
                    Toast.makeText(getContext(), "Unable To Retrieve Profile", Toast.LENGTH_SHORT).show();
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


                        JSONArray myResultArray = new JSONArray(result);
                        JSONObject res = myResultArray.getJSONObject(0);


                        String myName =  res.getString("name");
                        String imgUrl = res.getString("imageurl");
                        String userEmail = res.getString("email");




//                        String person_email = res.getString("person_email");
//                        boolean isFriend = Boolean.parseBoolean(res.getString("friend"));
//                        boolean isRequest = Boolean.parseBoolean(res.getString("request"));
//                        String requestType = res.getString("request-type");


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
//                                    stopShimmer();

                                String myImgUrl = "https://api.multiavatar.com/"+ myName +".png";
                                nameTextView.setText(myName);
                                emailTextView.setText(userEmail);

                                Glide
                                    .with(getContext())
                                    .load(myImgUrl)
                                    .into(profileImageView);


                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    getActivity().runOnUiThread(() -> {
                        stopLoading();
                        //                        stopShimmer();
                        try {
                            Toast.makeText(getContext(), "Error" + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
    }



    void uploadPhotoAPI(String newImageURL)
    {
        showLoading();

        new Thread(() -> {
            try {

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");

                final OkHttpClient client = new OkHttpClient();

                JSONObject myPostDat = new JSONObject();
                myPostDat.put("imageurl", newImageURL);

                String jsonPost = myPostDat.toString();

                RequestBody body = RequestBody.create(JSON,jsonPost);

                String wryd_url = keysConfig.wrydServerURL + "/api/user/dp";
                Request request = new Request.Builder()
                        .url(wryd_url)
                        .addHeader("Authorization","Bearer "+savedUserToken)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);


                    Log.d(TAG, "uploadPhotoAPI: "+response.body().string());
                    Log.d(TAG, "uploadPhotoAPI: Image Upload Failed");


                    getActivity().runOnUiThread(()-> {

                        Toast.makeText(getContext(), "Profile Image Changed Successfully.", Toast.LENGTH_SHORT).show();

                    });

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d(TAG, "uploadPhotoAPI: Image Upload Failed");

                getActivity().runOnUiThread(() -> {

                    Toast.makeText(getContext(), "Image API Upload Failed.", Toast.LENGTH_SHORT).show();
                });

            }


            getActivity().runOnUiThread(() -> {

                stopLoading();
                getUserViewDetails();

            });

        }).start();

    }


    void deletePhotoAPI()
    {
        showLoading();

        new Thread(() -> {
            try {

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");

                final OkHttpClient client = new OkHttpClient();

                String wryd_url = keysConfig.wrydServerURL + "/api/user/dp";

                Request request = new Request.Builder()
                        .url(wryd_url)
                        .addHeader("Authorization","Bearer "+savedUserToken)
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);


                    Log.d(TAG, "uploadPhotoAPI: "+response.body().string());
                    Log.d(TAG, "uploadPhotoAPI: Image Deletion Failed");


                    getActivity().runOnUiThread(()-> {

                        Toast.makeText(getContext(), "Image Removed Successfully.", Toast.LENGTH_SHORT).show();

                    });

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d(TAG, "uploadPhotoAPI: Image Deletion Failed");

                getActivity().runOnUiThread(() -> {

                    Toast.makeText(getContext(), "Image API Deletion Failed.", Toast.LENGTH_SHORT).show();
                });

            }


            getActivity().runOnUiThread(() -> {

                stopLoading();
                getUserViewDetails();

            });

        }).start();

    }
}