package com.push.notification.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.push.notification.fragment.NotificationFragment;
import com.push.notification.fragment.ProfileFragment;
import com.push.notification.fragment.UsersFragment;

/**
 * Created by Himanshu Sharma on 09/07/19.
 */

public class PagerViewAdapter extends FragmentPagerAdapter {

    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ProfileFragment();

            case 1:
                return new UsersFragment();

            case 2:
                return new NotificationFragment();

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

}
