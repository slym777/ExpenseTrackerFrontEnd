package com.example.expensetracker.ui.viewtrip.addexpense;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.databinding.DialogAddExpenseBinding;
import com.example.expensetracker.model.ExpenseType;
import com.example.expensetracker.model.Trip;

import org.json.JSONException;

import java.util.ArrayList;

public class AddExpenseDialog extends DialogFragment {
    private Trip trip;
    DialogAddExpenseBinding binding;
    AddExpenseViewModel addExpenseViewModel;
    private boolean comesFromTripView;

    public AddExpenseDialog(Trip trip) {
        super();
        this.trip = trip;
        comesFromTripView = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addExpenseViewModel = new ViewModelProvider(requireActivity()).get(AddExpenseViewModel.class);
        if (comesFromTripView) {
            addExpenseViewModel.resetValues();
            comesFromTripView = false;
        }
        binding = DialogAddExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addExpenseViewModel.tripId = trip.getId();

        binding.isGroupCheckbox.setOnClickListener(l -> {
            if (!binding.isGroupCheckbox.isChecked()) {
                binding.members.setVisibility(View.GONE);
                addExpenseViewModel.isGroupExpense = false;
            } else {
                binding.members.setVisibility(View.VISIBLE);
                addExpenseViewModel.isGroupExpense = true;
            }
        });

        addExpenseViewModel.selectedUserList.observe(getViewLifecycleOwner(), users -> {
            binding.nrParticipants.setText(String.format("%d", users.size()));
        });

        binding.expenseTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch ((checkedId - 1) % 4 + 1) {
                case 1: {
                    addExpenseViewModel.expenseType = ExpenseType.STAY;
                    break;
                }
                case 2: {
                    addExpenseViewModel.expenseType = ExpenseType.TRANSPORT;
                    break;
                }
                case 3: {
                    addExpenseViewModel.expenseType = ExpenseType.MEAL;
                    break;
                }
                default: {
                    addExpenseViewModel.expenseType = ExpenseType.OTHER;
                    break;
                }
            }
        });

        binding.members.setOnClickListener(l -> showAddMembersDialog());

        binding.buttonAddExpense.setOnClickListener(l -> {
            String expense = TextUtils.isEmpty(binding.addExpenseEditText.getText())
                    ? "0"
                    : binding.addExpenseEditText.getText().toString();
            double amount = Double.parseDouble(expense);
            String description = binding.expenseDescriptionEditText.getText().toString();
            try {
                addExpenseViewModel.createExpenseInTrip(
                        description,
                        addExpenseViewModel.expenseType,
                        amount,
                        addExpenseViewModel.selectedUserList.getValue() == null
                                ? new ArrayList<>()
                                : addExpenseViewModel.selectedUserList.getValue()
                        , addExpenseViewModel.isGroupExpense);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dismiss();
        });

        binding.buttonCancelExpense.setOnClickListener(l -> {
            // TODO after dismiss trigger load data in tripview
            dismiss();
        });
    }

    private void showAddMembersDialog() {
        ChooseMembersExpenseDialog dialog = new ChooseMembersExpenseDialog(trip);
        dialog.show(getChildFragmentManager(), "ChooseMembersExpenseDialog");
    }
}
