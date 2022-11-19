package com.wrydhub.wryd.wrydapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wrydhub.wryd.wrydapp.models.User;
import com.wrydhub.wryd.wrydapp.ui.FruitAdapter;
import com.wrydhub.wryd.wrydapp.ui.ModelClass;
import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {




    SearchView searchVieww;
    RecyclerView recyclerView;

    ArrayList<ModelClass> arrayList=new ArrayList<>();
    ArrayList<ModelClass>searchList;
    String[] fruitList=new String[]{
            "Apple","Banana","Pineapple","Orange","Lychee","Guava","Peach","Melon","Watermelon","Papaya"
    };

    int[] imgList=new int[]{
            R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon,R.drawable.camera_icon
    };

    String[] fruitNum=new String[]{
            "Fruit 1","Fruit 2","Fruit 3","Fruit 4","Fruit 5","Fruit 6","Fruit 7","Fruit 8","Fruit 9","Fruit 10"
    };


    ArrayList<User> userArrayList = new ArrayList<>();


    String savedUserid;
    String savedUserToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        getSupportActionBar().hide();


        Bundle b = getIntent().getExtras();

        if(b != null) {
            savedUserToken = b.getString("savedToken");
            savedUserid = b.getString("savedUserid");
        }


        recyclerView=findViewById(R.id.recyclerView_searchPage);
        searchVieww=findViewById(R.id.searchView);

        recyclerView.setVisibility(View.INVISIBLE);
        for (int i=0;i<fruitList.length;i++){
            ModelClass modelClass=new ModelClass();
            modelClass.setFruitName(fruitList[i]);
            modelClass.setFruitNum(fruitNum[i]);
            modelClass.setImg(imgList[i]);

            arrayList.add(modelClass);
        }

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        FruitAdapter fruitAdapter = new FruitAdapter(this,arrayList);
        recyclerView.setAdapter(fruitAdapter);

        searchVieww.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList=new ArrayList<>();

//                String query;
                if (query.length()>0){

                    searchList = getSearchAPI(query);

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getApplicationContext(),searchList);
                    recyclerView.setAdapter(fruitAdapter);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList=new ArrayList<>();

//                String query;
                if (newText.length()>0){
                    recyclerView.setVisibility(View.VISIBLE);

                    searchList = getSearchAPI(newText);

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getApplicationContext(),searchList);
                    recyclerView.setAdapter(fruitAdapter);
                }

                return false;
            }
        });
    }



    ArrayList<ModelClass> getSearchAPI(String query)
    {
        ArrayList<ModelClass> fetchedUsers = new ArrayList<>();

        try {

        Thread mythr = new Thread(()->{

            try {
                OkHttpClient client = new OkHttpClient();
                String url = keysConfig.wrydServerURL + "/api/search/" + query;
                System.out.println("my url ===== " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + savedUserToken)
                        .build();


                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    Log.d(TAG, "=================================");

                    String result = response.body().string();
                    Log.d(TAG, "onResponse: " + result);

                    JSONArray res = null;
                    try {
                        res = new JSONArray(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < res.length(); i++) {
                        JSONObject myjs = res.getJSONObject(i);
                        String pid = myjs.getString("id");
                        String imgUrl = myjs.getString("imageurl");
                        String person_name = myjs.getString("name");


                        ModelClass modelClass = new ModelClass();
                        modelClass.setFruitName(person_name);
                        modelClass.setFruitNum(pid);
                        modelClass.setImg(R.drawable.camera_icon);

                        fetchedUsers.add(modelClass);
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                                stopLoading();
//                                    stopShimmer();
                        }
                    });


                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        });


        mythr.start();
        mythr.join();
        return fetchedUsers;

        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}