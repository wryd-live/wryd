package com.wrydhub.wryd.wrydapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrydhub.wryd.wrydapp.R;
import com.wrydhub.wryd.wrydapp.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_search_bar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_search_bar extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;




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



    public fragment_search_bar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_search_bar.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_search_bar newInstance(String param1, String param2) {
        fragment_search_bar fragment = new fragment_search_bar();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_search_bar,container,false);




        recyclerView=root.findViewById(R.id.recyclerView_searchPage);
        searchVieww=root.findViewById(R.id.searchView);

        recyclerView.setVisibility(View.INVISIBLE);
        for (int i=0;i<fruitList.length;i++){
            ModelClass modelClass=new ModelClass();
            modelClass.setFruitName(fruitList[i]);
            modelClass.setFruitNum(fruitNum[i]);
            modelClass.setImg(imgList[i]);

            arrayList.add(modelClass);
        }

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FruitAdapter fruitAdapter = new FruitAdapter(getContext(),arrayList);
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

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getContext(),searchList);
                    recyclerView.setAdapter(fruitAdapter);
                }

                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getContext(),arrayList);
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

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getContext(),searchList);
                    recyclerView.setAdapter(fruitAdapter);
                }

                else{
                    recyclerView.setVisibility(View.VISIBLE);

                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    FruitAdapter fruitAdapter = new FruitAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(fruitAdapter);

                }
                return false;
            }
        });
        return root;
    }
}