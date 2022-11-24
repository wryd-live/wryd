package com.wrydhub.wryd.wrydapp.ui;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;

import com.wrydhub.wryd.wrydapp.ProfileView;
import com.wrydhub.wryd.wrydapp.R;
import com.wrydhub.wryd.wrydapp.SearchActivity;
import com.wrydhub.wryd.wrydapp.adapters.HomeListAdapter;
import com.wrydhub.wryd.wrydapp.models.User;
import com.wrydhub.wryd.wrydapp.utils.keysConfig;
import com.wrydhub.wryd.wrydapp.utils.lastSeen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userid";
    private static final String ARG_PARAM2 = "orgUsername";
    private static final String ARG_PARAM3 = "token";

    // TODO: Rename and change types of parameters
    private String savedUserid;
    private String savedOrgUsername;
    private String savedUserToken;

//
    SearchView searchVieww;
    RecyclerView recyclerView;

    ArrayList<ModelClass>arrayList=new ArrayList<>();
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

    HomeListAdapter listAdapter;

    ProgressDialog progress;

    ImageView friendsNotFoundImg;
    TextView friendsNotFoundTxt;

//    ListView myListView;
//
//    ArrayList<String> arrayList;
//
//    ArrayAdapter adapter;

    public search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search.
     */
    // TODO: Rename and change types and number of parameters
    public static search newInstance(String param1, String param2) {
        search fragment = new search();
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_search,container,false);


//        int[] imageId = {R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar};
//
//        String[] name = {"Christopher","Craig","Sergio","Mubariz","Mike","Michael","Toa","Ivana","Alex"};
//        String[] lastMessage = {"Heye","Supp","Let's Catchup","Dinner tonight?","Gotta go",
//                "i'm in meeting","Gotcha","Let's Go","any Weekend Plans?"};
//        String[] lastmsgTime = {"8:45 pm","9:00 am","7:34 pm","6:32 am","5:76 am",
//                "5:00 am","7:34 pm","2:32 am","7:76 am"};
//        String[] phoneNo = {"7656610000","9999043232","7834354323","9876543211","5434432343",
//                "9439043232","7534354323","6545543211","7654432343"};
//        String[] country = {"United States","Russia","India","Israel","Germany","Thailand","Canada","France","Switzerland"};
//
//
//
//        for(int i = 0;i< imageId.length;i++){
//
//            User user = new User(
//                    name[i],
//                    lastMessage[i],
//                    lastmsgTime[i],
//                    123456,
//                    phoneNo[i],
//                    country[i],
//                    imageId[i]
//            );
//
//            userArrayList.add(user);
//
//        }


        friendsNotFoundImg = root.findViewById(R.id.friendsNotFoundImg);
        friendsNotFoundTxt = root.findViewById(R.id.friendsNotFoundTxt);

        listAdapter = new HomeListAdapter(root.getContext(), userArrayList);
        ListView lv = (ListView) root.findViewById(R.id.listview_search);

        lv.setAdapter(listAdapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Intent i = new Intent(MainActivity.this,UserActivity.class);
//                i.putExtra("name",name[position]);
//                i.putExtra("phone",phoneNo[position]);
//                i.putExtra("country",country[position]);
//                i.putExtra("imageid",imageId[position]);
//                startActivity(i);

                Log.d(TAG, "onItemClick: ItemClicked");

                int personId = userArrayList.get(position).personId;

                Intent myIntent = new Intent(getActivity(), ProfileView.class);
                myIntent.putExtra("personid", personId); //Optional parameters
                getActivity().startActivity(myIntent);

            }
        });


        searchVieww=root.findViewById(R.id.searchView);

        ImageView closeBtn = (ImageView) searchVieww.findViewById(R.id.search_button);
        closeBtn.setEnabled(false);


        searchVieww.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: Clicked Search");

//            fragment_search_bar fsb = new fragment_search_bar();
//            replaceFragment(fsb);

            Intent myIntent = new Intent(getActivity(), SearchActivity.class);
            myIntent.putExtra("savedUserid", savedUserid); //Optional parameters
            myIntent.putExtra("savedToken", savedUserToken); //Optional parameters
            getActivity().startActivity(myIntent);

        });



        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Fetching friend list from server....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog


        getFriendsData();

        return root;
    }


    public void getFriendsData()
    {
        userArrayList.clear();
        listAdapter.notifyDataSetChanged();
        progress.show();

        friendsNotFoundImg.setVisibility(View.INVISIBLE);
        friendsNotFoundTxt.setVisibility(View.INVISIBLE);

        OkHttpClient client = new OkHttpClient();
        String url = keysConfig.wrydServerURL + "/api/profile/friends" ;
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
                    progress.dismiss();
//                    stopShimmer();

                    friendsNotFoundImg.setVisibility(View.VISIBLE);
                    friendsNotFoundTxt.setVisibility(View.VISIBLE);

//                    Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.d(TAG, "=================================");

                    String result = response.body().string();
                    Log.d(TAG, "onResponse: "+result);

                    try {
                        JSONArray deviceData = new JSONArray(result);

                        userArrayList.clear();
                        for(int i=0;i<deviceData.length();i++)
                        {
                            JSONObject dev = deviceData.getJSONObject(i);
                            String devName = dev.getString("person_name");
                            int devId = Integer.parseInt(dev.getString("person_id"));
                            String imgUrl = dev.getString("imageurl");
                            String personEmail = dev.getString("person_email");


                            User user = new User(
                                    devName,
                                    personEmail,
                                    "",
                                    123456789,
                                    "8433076726",
                                    "india",
                                    R.drawable.facebook_avatar);

                            if(!imgUrl.equals("null"))
                            {
                                user.setImageUrl(imgUrl);
                            }
                            user.setPersonId(devId);
                            userArrayList.add(user);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                                progress.dismiss();
//                                stopShimmer();

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();

                        getActivity().runOnUiThread(() -> {
                            progress.dismiss();
//                            stopShimmer();

                            friendsNotFoundImg.setVisibility(View.VISIBLE);
                            friendsNotFoundTxt.setVisibility(View.VISIBLE);

//                            Toast.makeText(getContext(), "Error Parsing Data", Toast.LENGTH_SHORT).show();
                        });
                    }

                }
                else
                {
                    getActivity().runOnUiThread(() -> {
                        progress.dismiss();
//                    stopShimmer();

                        friendsNotFoundImg.setVisibility(View.VISIBLE);
                        friendsNotFoundTxt.setVisibility(View.VISIBLE);

//                        Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}