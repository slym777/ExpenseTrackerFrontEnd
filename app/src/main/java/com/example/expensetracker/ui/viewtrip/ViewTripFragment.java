package com.example.expensetracker.ui.viewtrip;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.expensetracker.databinding.FragmentTripViewBinding;
import com.example.expensetracker.ui.viewtrip.groupexpense.GroupExpensesFragment;
import com.example.expensetracker.ui.viewtrip.personalexpense.PersonalExpensesFragment;
import com.example.expensetracker.ui.viewtrip.tripinfo.TripInfoFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.expensetracker.utils.ConstantsUtils.TRIP_ID_EXTRA;

public class ViewTripFragment extends Fragment {

    private FragmentTripViewBinding binding;
    private ViewTripPagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Long tripId = getActivity().getIntent().getLongExtra(TRIP_ID_EXTRA, -1);

        pagerAdapter = new ViewTripPagerAdapter(getActivity().getSupportFragmentManager(), tripId);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

        //TODO to set avatar of a trip


    }

    class ViewTripPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public ViewTripPagerAdapter(@NonNull FragmentManager fm, Long tripId) {
            super(fm);
            fragments.add(new GroupExpensesFragment(tripId));
            fragments.add(new PersonalExpensesFragment(tripId));
            fragments.add(new TripInfoFragment(tripId));
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Group Expenses";
                case 1: return "Personal Expenses";
                case 2: return "Trip Info";
                default: return "Group Expenses";
            }
        }
    }

}
