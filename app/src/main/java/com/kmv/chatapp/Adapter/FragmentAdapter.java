package com.kmv.chatapp.Adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kmv.chatapp.MainActivity;
import com.kmv.chatapp.fragment.ChatFragment;
import com.kmv.chatapp.fragment.SettingsFragment;
import com.kmv.chatapp.fragment.UserFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    ArrayList arrayList;
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList arrayList) {
        super(fragmentManager, lifecycle);
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 1:
                return new UserFragment(arrayList);
//            case 2 :
//                return new SettingsFragment();
        }

        return new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
