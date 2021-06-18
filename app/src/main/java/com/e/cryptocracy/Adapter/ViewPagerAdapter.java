package com.e.cryptocracy.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments1=new ArrayList<>(  );
    private final List<String> Title=new ArrayList<>(  );
    public ViewPagerAdapter(FragmentManager fm) {
        super( fm );
    }

    @Override
    public Fragment getItem(int position) {
        return fragments1.get( position );
    }

    @Override
    public int getCount() {
        return Title.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Title.get( position );
    }

    public void Addfragment(Fragment fragment, String string){
        fragments1.add( fragment );
        Title.add( string);
    }
}
