package com.example.expensetracker.ui.viewtrip.tripinfo;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.databinding.FragmentTripInfoBinding;

public class TripInfoFragment extends Fragment {

    private TripInfoViewModel tripInfoViewModel;
    private FragmentTripInfoBinding binding;
    private Long tripId;

    public TripInfoFragment(Long tripId) {
        super();
        this.tripId = tripId;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tripInfoViewModel = new ViewModelProvider(requireActivity()).get(TripInfoViewModel.class);
        binding = FragmentTripInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
