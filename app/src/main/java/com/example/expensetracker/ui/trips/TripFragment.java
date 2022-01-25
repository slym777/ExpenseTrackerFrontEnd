package com.example.expensetracker.ui.trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TripFragment extends Fragment implements TripAdapter.OnClickTripListener {

    private TripViewModel tripViewModel;
    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripRecyclerView = view.findViewById(R.id.ft_trips_recycler_view);
        tripRecyclerView.setHasFixedSize(true);
        tripRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        tripAdapter = new TripAdapter(new ArrayList<>(), this);
        tripRecyclerView.setAdapter(tripAdapter);

        FloatingActionButton createGroup = view.findViewById(R.id.ft_add_trip_button);
        createGroup.setOnClickListener(v -> {
            showAddHubDialog(view);
        });

        tripViewModel.tripLiveList.observe(getViewLifecycleOwner(), trips -> {
            tripAdapter.updateRecyclerView(trips);
        });

        tripViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Error")
                    .setMessage(str)
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        tripViewModel.getAllTrips();
    }


//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.main_filter_menu, menu);
//    }


    private void showAddHubDialog(View view) {
//        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_add_hub, null);
//
//        final EditText hubNameEditText = dialogView.findViewById(R.id.dah_title_text);
//        final EditText descEditText = dialogView.findViewById(R.id.dah_desc_text);
//        final TextView dahCurrencyText = dialogView.findViewById(R.id.dah_currency_text);
//        ConstraintLayout chooseCurrency = dialogView.findViewById(R.id.dah_currency);
//        Button submitButton = dialogView.findViewById(R.id.button_submit);
//        Button cancelButton = dialogView.findViewById(R.id.button_cancel);
//
//        dahCurrencyText.setText(SharedPreferencesUtils.retrieveMainCurrencyFromSharedPref().name);
//
//        chooseCurrency.setOnClickListener(v -> {
//            List<Currency> somethingList =
//                    new ArrayList<Currency>(EnumSet.allOf(Currency.class));
//            String oldCurrency = dahCurrencyText.getText().toString();
//            ArrayAdapter arrayAdapter = new ArrayAdapter<Currency>(getContext(), android.R.layout.select_dialog_singlechoice, somethingList);
//
//            new MaterialAlertDialogBuilder(getContext())
//                    .setTitle("Choose your main currency")
//                    .setNeutralButton("Cancel", (dialog, which) -> {
//                        dahCurrencyText.setText(oldCurrency);
//                        dialog.dismiss();
//                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            })
//                    .setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Currency currency = (Currency) arrayAdapter.getItem(which);
//                            dahCurrencyText.setText(currency.name);
//                        }
//                    }).show();
//        });
//
//        submitButton.setOnClickListener(v -> {
//            String hubName = hubNameEditText.getText().toString();
//            String hubDescription = descEditText.getText().toString();
//            String currency = dahCurrencyText.getText().toString();
//            if (!TextUtils.isEmpty(hubName) && !TextUtils.isEmpty(hubDescription)) {
//                hubViewModel.createHub(hubName, hubDescription, currency);
//                dialogBuilder.dismiss();
//            } else {
//                if (TextUtils.isEmpty(hubName))
//                    hubNameEditText.setError("Provide hub name");
//                if (TextUtils.isEmpty(hubDescription))
//                    descEditText.setError("Provide desc description");
//            }
//        });
//
//        cancelButton.setOnClickListener(v -> {
//            dialogBuilder.dismiss();
//        });
//
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.show();
    }


    @Override
    public void onHubClick(Long hubId, String hubName) {
//        Intent intent = new Intent(getActivity(), HubInfoActivity.class);
//        intent.putExtra(HUB_ID_EXTRA, hubId);
//        intent.putExtra(HUB_NAME_EXTRA, hubName);
//        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        tripViewModel.getAllTrips();
    }

}