package com.example.expensetracker.ui.statistics;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.databinding.DialogChooseTripBinding;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.ui.trips.TripViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ChooseTripDialog extends DialogFragment implements ChooseTripAdapter.OnClickTripListener {

    private DialogChooseTripBinding binding;
    private TripViewModel tripViewModel;
    private ChooseTripAdapter chooseTripAdapter;
    private OnSelectTripListener onSelectTripListener;

    public ChooseTripDialog (OnSelectTripListener onSelectTripListener) {
        this.onSelectTripListener = onSelectTripListener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tripViewModel = new ViewModelProvider(getActivity()).get(TripViewModel.class);
        binding = DialogChooseTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.famRecyclerForSelection.setHasFixedSize(true);
        binding.famRecyclerForSelection.setLayoutManager(new LinearLayoutManager(getContext()));

        chooseTripAdapter = new ChooseTripAdapter(new ArrayList<>(), this);
        binding.famRecyclerForSelection.setAdapter(chooseTripAdapter);

        binding.famButtonSubmit.setOnClickListener(v -> {
            Trip trip = chooseTripAdapter.getSelectedTrip();
            if (trip != null) {
                onSelectTripListener.onSelectTrip(trip);
            }
            dismiss();
        });

        tripViewModel.tripLiveList.observe(getViewLifecycleOwner(), list -> {
            chooseTripAdapter.updateRecyclerView(list.stream()
                    .map(t -> new ChooseTripRow(t.getId(), t.getName(), t.getDescription(), t.getAvatarUri(), t.getLocation(), t.getGroupSize(), t.getUsers(), t.getExpenses(), false))
                    .collect(Collectors.toList()));
        });

        tripViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Something went wrong")
                    .setMessage(str)
                    .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        tripViewModel.getAllTrips();
    }

    @Override
    public void onTripClick(Long hubId, String hubName) {

    }
}
