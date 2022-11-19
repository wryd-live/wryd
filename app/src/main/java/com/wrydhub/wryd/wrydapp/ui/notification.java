package com.wrydhub.wryd.wrydapp.ui;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.wrydhub.wryd.wrydapp.adapters.NotificationListAdapter;
import com.wrydhub.wryd.wrydapp.R;
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
 * Use the {@link notification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class notification extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userid";
    private static final String ARG_PARAM2 = "orgUsername";
    private static final String ARG_PARAM3 = "token";

    // TODO: Rename and change types of parameters
    private String savedUserid;
    private String savedOrgUsername;
    private String savedUserToken;


    ArrayList<User> userArrayList = new ArrayList<>();
    NotificationListAdapter listAdapter;
    ProgressDialog progress;
    ShimmerFrameLayout shimmer;


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
            savedUserid = getArguments().getString(ARG_PARAM1);
            savedOrgUsername = getArguments().getString(ARG_PARAM2);
            savedUserToken = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_notification,container,false);


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
        String[] lastMessage = {
                "sent you a friend request",
                "accepted your friend request",
                "accepted your friend request",
                "sent you a friend request",
                "accepted your friend request",
                "sent you a friend request",
                "sent you a friend request",
                "accepted your friend request",
                "other",
        };
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


        /*
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
         */
        userArrayList.clear();
        listAdapter = new NotificationListAdapter(root.getContext(),userArrayList);

        shimmer = root.findViewById(R.id.shimmer_view_container_notification);

        shimmer.startShimmer();


//        progress = new ProgressDialog(getActivity());
//        progress.setTitle("Loading");
//        progress.setMessage("Fetching notifications from server....");
//        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//        progress.show();

        fetchAndUpdateData();


        ListView lv = (ListView) root.findViewById(R.id.listview_notification);

        lv.setAdapter(listAdapter);
        lv.setClickable(true);
        lv.setLongClickable(true);
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
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);

                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Notification")
                        .setMessage("Are you sure you want to delete this Notification?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();




                return true;
            }
        });


        return root;
    }

    private void fetchAndUpdateData()
    {
        OkHttpClient client = new OkHttpClient();
        String url = keysConfig.wrydServerURL + "/api/notification" ;
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
//                    progress.dismiss();
                    stopShimmer();

                    Toast.makeText(getContext(), "Unable To Fetch Data", Toast.LENGTH_SHORT).show();
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
                            String devId = dev.getString("personid");
                            String sqlTime = dev.getString("time");
                            String notificationType = dev.getString("type");
                            String sensTime = "1668367250140";
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Instant  instTime = Instant.parse(sqlTime);
                                long epoTime = instTime.toEpochMilli();
                                sensTime = Long.toString(epoTime);
                            }
                            String lst_seen = lastSeen.func(sensTime);
                            long lastSeenTime = Long.parseLong(sensTime);

                            String notificationMessage;
                            if(notificationType.equals("accepted"))
                            {
                                notificationMessage = "accepted your friend request";
                            }
                            else if(notificationType.equals("requested"))
                            {
                                notificationMessage = "sent you a friend request";
                            }
                            else
                            {
                                notificationMessage = "other";
                            }

                            User user = new User(
                                    devName,
                                    notificationMessage,
                                    lst_seen,
                                    lastSeenTime,
                                    "8433076726",
                                    "india",
                                    R.drawable.facebook_avatar);

                            user.setImageUrl("https://api.multiavatar.com/"+ devName +".png");
                            user.setNotificationType(notificationType);
                            userArrayList.add(user);
                        }

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            userArrayList.sort((o1, o2) -> {
//                                if (o1.getLastSeenTime() > o2.getLastSeenTime())
//                                    return -1;
//                                else if (o1.getLastSeenTime() < o2.getLastSeenTime())
//                                    return 1;
//                                else
//                                    return 0;
//                            });
//                        }


                        if(getActivity() == null)
                        {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
//                                progress.dismiss();
                                stopShimmer();

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();

                        getActivity().runOnUiThread(() -> {
//                            progress.dismiss();
                            stopShimmer();

                            Toast.makeText(getContext(), "Error Parsing Data", Toast.LENGTH_SHORT).show();
                        });
                    }

                }
                else
                {
                    getActivity().runOnUiThread(() -> {
//                        progress.dismiss();
                        stopShimmer();
                        try {
                            Toast.makeText(getContext(), "Error Fetching Data" + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


    void stopShimmer()
    {
        shimmer.hideShimmer();
        shimmer.setVisibility(View.INVISIBLE);
    }
}