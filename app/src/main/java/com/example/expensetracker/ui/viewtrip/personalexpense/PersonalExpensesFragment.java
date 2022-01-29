package com.example.expensetracker.ui.viewtrip.personalexpense;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.databinding.FragmentPersonalExpenseBinding;
import com.example.expensetracker.ui.viewtrip.OnClickExpenseListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class PersonalExpensesFragment extends Fragment implements OnClickExpenseListener {

    private PersonalExpenseViewModel personalExpenseViewModel;
    private FragmentPersonalExpenseBinding binding;
    private PersonalExpensesAdapter personalExpensesAdapter;
    private Long tripId;

    public PersonalExpensesFragment(Long tripId) {
        super();
        this.tripId = tripId;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalExpenseViewModel = new ViewModelProvider(requireActivity()).get(PersonalExpenseViewModel.class);
        binding = FragmentPersonalExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        personalExpensesAdapter = new PersonalExpensesAdapter(new ArrayList<>(), this);
        binding.recycler.setAdapter(personalExpensesAdapter);

        personalExpenseViewModel.expensesLiveList.observe(getViewLifecycleOwner(), expenses -> {
            personalExpensesAdapter.updateRecyclerView(expenses);
        });

        personalExpenseViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("No connection")
                    .setMessage(str)
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
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
        personalExpenseViewModel.getPersonalExpenses(tripId);
    }


    @Override
    public void onGroupExpenseClick(Long expenseId) {

    }
}