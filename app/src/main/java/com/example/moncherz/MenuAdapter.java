package com.example.moncherz;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MenuAdapter extends FragmentStatePagerAdapter {
    private int place;
    public MenuAdapter (FragmentManager fragmentManager, int mPlace) {
        super(fragmentManager);
        this.place = mPlace;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MenuFragment.init(place, Utilities.Breakfast);
            case 1:
                return MenuFragment.init(place, Utilities.Lunch);
            case 2:
                return MenuFragment.init(place, Utilities.Dinner);
            default:
                return MenuFragment.init(place, Utilities.Dinner);
        }

    }
}
