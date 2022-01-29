package com.example.expensetracker.ui.viewtrip.groupexpense;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.databinding.FragmentGroupExpenseBinding;
import com.example.expensetracker.ui.viewtrip.OnClickExpenseListener;
import com.example.expensetracker.ui.viewtrip.ViewTripFragmentDirections;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class GroupExpensesFragment extends Fragment implements OnClickExpenseListener {

    private GroupExpenseViewModel groupExpenseViewModel;
    private FragmentGroupExpenseBinding binding;
    private GroupExpensesAdapter groupExpensesAdapter;
    private Long tripId;

    public GroupExpensesFragment(Long tripId) {
        super();
        this.tripId = tripId;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groupExpenseViewModel = new ViewModelProvider(requireActivity()).get(GroupExpenseViewModel.class);
        binding = FragmentGroupExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        groupExpensesAdapter = new GroupExpensesAdapter(new ArrayList<>(), this);
        binding.recycler.setAdapter(groupExpensesAdapter);

        groupExpenseViewModel.expensesLiveList.observe(getViewLifecycleOwner(), expenses -> {
            groupExpensesAdapter.updateRecyclerView(expenses);
        });

        groupExpenseViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
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
        groupExpenseViewModel.getGroupExpenses(tripId);
    }


    @Override
    public void onGroupExpenseClick(Long expenseId) {
        ViewTripFragmentDirections.ActionNavigationTripViewToNavigationExpenseView action =
                ViewTripFragmentDirections.actionNavigationTripViewToNavigationExpenseView();
        action.setExpenseId(expenseId);
        Navigation.findNavController(getView()).navigate(action);
    }

}