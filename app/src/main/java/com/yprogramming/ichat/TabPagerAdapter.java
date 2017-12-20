package com.yprogramming.ichat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by yourthor on 14/11/2560.
 */

class TabPagerAdapter extends FragmentPagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PostFragment postFragment = new PostFragment();
                return postFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FriendFragment friendFragment = new FriendFragment();
                return friendFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "ບັນເທີງ";
            case 1:
                return "ສົນທະນາ";
            case 2:
                return "ຫາເພື່ອນ";
            default:
                return null;
        }
    }
}
