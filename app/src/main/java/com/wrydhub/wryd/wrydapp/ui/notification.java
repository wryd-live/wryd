package com.wrydhub.wryd.wrydapp.ui;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrydhub.wryd.wrydapp.adapters.NotificationListAdapter;
import com.wrydhub.wryd.wrydapp.R;
import com.wrydhub.wryd.wrydapp.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link notification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class notification extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ArrayList<User> userArrayList = new ArrayList<>();
    NotificationListAdapter listAdapter;
    ProgressDialog progress;


    public notification() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment notification.
     */
    // TODO: Rename and change types and number of parameters
    public static notification newInstance(String param1, String param2) {
        notification fragment = new notification();
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


        int[] imageId = {R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar,
                R.drawable.facebook_avatar};

        String[] name = {"Christopher","Craig","Sergio","Mubariz","Mike","Michael","Toa","Ivana","Alex"};
        String[] lastMessage = {"Heye","Supp","Let's Catchup","Dinner tonight?","Gotta go",
                "i'm in meeting","Gotcha","Let's Go","any Weekend Plans?"};
        String[] lastmsgTime = {"8:45 pm","9:00 am","7:34 pm","6:32 am","5:76 am",
                "5:00 am","7:34 pm","2:32 am","7:76 am"};
        String[] phoneNo = {"7656610000","9999043232","7834354323","9876543211","5434432343",
                "9439043232","7534354323","6545543211","7654432343"};
        String[] country = {"United States","Russia","India","Israel","Germany","Thailand","Canada","France","Switzerland"};


        String[] notificationType = {
                "requested",
                "accepted",
                "accepted",
                "requested",
                "accepted",
                "requested",
                "requested",
                "accepted",
                "other",
        };

        long notification_time = 12345678;
        for(int i = 0;i< imageId.length;i++){

            User user = new User(
                    name[i],
                    lastMessage[i],
                    lastmsgTime[i],
                    notification_time,
                    phoneNo[i],
                    country[i],
                    imageId[i]);

            user.setImageUrl("https://api.multiavatar.com/"+ name[i] +".png");
            user.setNotificationType(notificationType[i]);
            userArrayList.add(user);
        }

        listAdapter = new NotificationListAdapter(root.getContext(),userArrayList);


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

                Log.d(TAG, "onItemClick: Notification Item Clicked");

            }
        });


        return root;
    }
}