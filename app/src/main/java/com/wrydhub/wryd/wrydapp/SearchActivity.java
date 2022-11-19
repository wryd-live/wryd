package com.wrydhub.wryd.wrydapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.wrydhub.wryd.wrydapp.models.User;
import com.wrydhub.wryd.wrydapp.ui.FruitAdapter;
import com.wrydhub.wryd.wrydapp.ui.ModelClass;

import java.util.ArrayList;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        getSupportActionBar().hide();


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
                    for (int i=0;i<arrayList.size();i++){
                        recyclerView.setVisibility(View.VISIBLE);
                        if(arrayList.get(i).getFruitName().toUpperCase().contains(query.toUpperCase())){
                            ModelClass modelClass=new ModelClass();
                            modelClass.setFruitName(arrayList.get(i).getFruitName());
                            modelClass.setFruitNum(arrayList.get(i).getFruitNum());
                            modelClass.setImg(arrayList.get(i).getImg());
                            searchList.add(modelClass);
                        }
                    }

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getApplicationContext(),searchList);
                    recyclerView.setAdapter(fruitAdapter);
                }

                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getApplicationContext(),arrayList);
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
                    for (int i=0;i<arrayList.size();i++){
                        if(arrayList.get(i).getFruitName().toUpperCase().contains(newText.toUpperCase())){
                            ModelClass modelClass=new ModelClass();
                            modelClass.setFruitName(arrayList.get(i).getFruitName());
                            modelClass.setFruitNum(arrayList.get(i).getFruitNum());
                            modelClass.setImg(arrayList.get(i).getImg());
                            searchList.add(modelClass);
                        }
                    }

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getApplicationContext(),searchList);
                    recyclerView.setAdapter(fruitAdapter);
                }

                else{
                    recyclerView.setVisibility(View.VISIBLE);

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getApplicationContext(),arrayList);
                    recyclerView.setAdapter(fruitAdapter);

                }
                return false;
            }
        });
    }
}