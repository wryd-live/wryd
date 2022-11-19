package com.wrydhub.wryd.wrydapp.utils;

public class lastSeen {
//    public static void main(String[] args) throws Exception {
//        String ans = lastSeen("1667499412062");
//        System.out.println(ans);
//    }


    /*

    calledFrom = 0 -> Home Page
    calledFrom = 1 -> Notification Page
     */
    public static String func(String givenTime,int calledFrom)
    {
        // givenTime = 1667499412062
        long given = Long.parseLong(givenTime);
        long curr = System.currentTimeMillis();

        long diff = (curr - given)/1000;

        String ans;
        if(diff <= 60)
        {
            ans=" ";
            if(calledFrom==0)
                ans = "online";
            else if(calledFrom==1)
                ans = "just now";
            return ans;
        }
        else if(diff > 60 && diff <= 3600)
        {
            ans = (diff/60) + " min ago";
            return ans;
        }
        else if(diff > 3600 && diff <= 86400)
        {
            ans = (diff/3600) + " hr ago";
            return ans;
        }
        else if(diff > 86400 && diff <= 2629743)
        {
            ans = (diff/86400) + " days ago";
            return ans;
        }
        else
        {
            ans = (diff/2629743) + " months ago";
            return ans;
        }
    }
}
