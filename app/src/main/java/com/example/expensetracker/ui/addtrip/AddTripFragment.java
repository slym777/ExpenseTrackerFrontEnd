package com.example.expensetracker.ui.addtrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentAddTripBinding;

import org.json.JSONException;

import timber.log.Timber;

public class AddTripFragment extends Fragment {

    private FragmentAddTripBinding binding;
    private AddTripViewModel addtripViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
        addtripViewModel = new ViewModelProvider(requireActivity()).get(AddTripViewModel.class);
        binding = FragmentAddTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fatTripNameText.setText(addtripViewModel.name);
        binding.fatTripDescText.setText(addtripViewModel.name);
        binding.fatTripLocationText.setText(addtripViewModel.name);

        binding.buttonAddMembers.setOnClickListener(v -> {
            addtripViewModel.name = binding.fatTripNameText.getText().toString();
            addtripViewModel.description = binding.fatTripDescText.getText().toString();
            addtripViewModel.location = binding.fatTripLocationText.getText().toString();
            addtripViewModel.avatarUri = "";

            Navigation.findNavController(view).navigate(R.id.action_navigation_add_trip_to_navigation_add_members);
        });

        binding.buttonAddTrip.setOnClickListener(v -> {
            try {
                addtripViewModel.createTrip(binding.fatTripNameText.getText().toString(),
                        binding.fatTripDescText.getText().toString(), "",
                        binding.fatTripLocationText.getText().toString())
                        .subscribe(bool -> {
                            Timber.d("Added new friends");
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }, error -> new AlertDialog.Builder(getActivity())
                                .setTitle("Error")
                                .setMessage(error.getLocalizedMessage())
                                .show()
                        );
            } catch (JSONException e) {
                Timber.d(e.getMessage());
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .show();
            }
        });
    }


}
