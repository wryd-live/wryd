package com.wrydhub.wryd.wrydapp.ui;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrydhub.wryd.wrydapp.adapters.HomeListAdapter;
import com.wrydhub.wryd.wrydapp.R;
import com.wrydhub.wryd.wrydapp.models.User;
import com.wrydhub.wryd.wrydapp.utils.lastSeen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ArrayList<User> userArrayList = new ArrayList<>();
    HomeListAdapter listAdapter;
    ProgressDialog progress;

    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home,container,false);


//        int[] imageId = {R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar,
//                R.drawable.facebook_avatar};

//        String[] name = {"Christopher","Craig","Sergio","Mubariz","Mike","Michael","Toa","Ivana","Alex"};
//        String[] lastMessage = {"Heye","Supp","Let's Catchup","Dinner tonight?","Gotta go",
//                "i'm in meeting","Gotcha","Let's Go","any Weekend Plans?"};
//        String[] lastmsgTime = {"8:45 pm","9:00 am","7:34 pm","6:32 am","5:76 am",
//                "5:00 am","7:34 pm","2:32 am","7:76 am"};
//        String[] phoneNo = {"7656610000","9999043232","7834354323","9876543211","5434432343",
//                "9439043232","7534354323","6545543211","7654432343"};
//        String[] country = {"United States","Russia","India","Israel","Germany","Thailand","Canada","France","Switzerland"};



//        for(int i = 0;i< imageId.length;i++){
//
//            User user = new User(name[i],lastMessage[i],lastmsgTime[i],phoneNo[i],country[i],imageId[i]);
//            userArrayList.add(user);
//
//        }
        userArrayList.clear();

        listAdapter = new HomeListAdapter(root.getContext(),userArrayList);


        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Fetching data from server....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        fetchAndUpdateData();



        ListView lv = (ListView) root.findViewById(R.id.listview_home);

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

            }
        });





        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // Write your code here to call the method.
//                                fetchData();
                                Log.d(TAG, "run: Fetching Data from WRYD API");
                                fetchAndUpdateData();


                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception here
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        return root;
    }



    void fetchAndUpdateData()
    {
        OkHttpClient client = new OkHttpClient();
        String url = "https://wryd.live/api/v1/locations/iiitdharwad";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.d(TAG, "=================================");

                    String result = response.body().string();
                    Log.d(TAG, "onResponse: "+result);

                    try {
                        JSONObject data = new JSONObject(result);
                        JSONArray deviceData = data.getJSONArray("locations");

                        userArrayList.clear();
                        for(int i=0;i<deviceData.length();i++)
                        {
                            JSONObject dev = deviceData.getJSONObject(i);
                            String devName = dev.getString("device");

                            JSONObject sens = dev.getJSONObject("sensors");
                            String sensTime = sens.getString("t");

                            JSONObject pred = dev.getJSONObject("prediction");
                            String loc = pred.getString("location");
                            String probab = pred.getString("probability");
                            String lst_seen = lastSeen.func(sensTime);
                            long lastSeenTime = Long.parseLong(sensTime);
                            User us = new User(
                                    devName,
                                    loc,
                                    lst_seen,
                                    lastSeenTime,
                                    "8433076726",
                                    "India",
                                    R.drawable.facebook_avatar
                            );
                            userArrayList.add(us);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            userArrayList.sort((o1, o2) -> {
                                if (o1.getLastSeenTime() > o2.getLastSeenTime())
                                    return -1;
                                else if (o1.getLastSeenTime() < o2.getLastSeenTime())
                                    return 1;
                                else
                                    return 0;
                            });
                        }


                        if(getActivity() == null)
                        {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                                progress.dismiss();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}