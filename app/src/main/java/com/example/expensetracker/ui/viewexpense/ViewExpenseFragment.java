package com.example.expensetracker.ui.viewexpense;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentExpenseViewBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.viewtrip.tripinfo.TripMembersAdapter;
import com.example.expensetracker.utils.BaseApp;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import org.json.JSONException;

import timber.log.Timber;

public class ViewExpenseFragment extends Fragment {
    private FragmentExpenseViewBinding binding;
    private ExpenseViewModel expenseViewModel;
    private ExpenseMembersAdapter expenseMembersAdapter;
    private Boolean isMemberListExpanded;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        if (getArguments() != null) {
            ViewExpenseFragmentArgs args = ViewExpenseFragmentArgs.fromBundle(getArguments());
            expenseViewModel.expenseId = args.getExpenseId();
            Timber.d("Get expenseId=" + expenseViewModel.expenseId + " from args");
        }

        binding = FragmentExpenseViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.membersRecyclerView.setHasFixedSize(true);
        binding.membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseMembersAdapter = new ExpenseMembersAdapter(new ArrayList<>());
        binding.membersRecyclerView.setAdapter(expenseMembersAdapter);

        isMemberListExpanded = false;

        binding.memberListExpand.setOnClickListener(v -> {
            if (isMemberListExpanded) {
                binding.membersRecyclerView.setVisibility(View.VISIBLE);
                binding.imageExpand.setImageDrawable(getResources().getDrawable(R.drawable.expand2_icon));
                isMemberListExpanded = false;
            } else {
                binding.membersRecyclerView.setVisibility(View.GONE);
                binding.imageExpand.setImageDrawable(getResources().getDrawable(R.drawable.expand_icon));
                isMemberListExpanded = true;
            }
        });

        expenseViewModel.expenseLive.observe(getViewLifecycleOwner(), expense -> {

            if (expense.getIsGroupExpense()) {
                setGroupExpBinding(expense);
            } else {
                setPersonalExpBinding(expense);
            }
        });

        expenseViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Something went wrong")
                    .setMessage(str)
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        binding.deleteImageView.setOnClickListener(l -> handleDeleteExpense());
    }

    private void handleDeleteExpense() {
        new AlertDialog.Builder(getContext())
                .setTitle("Deleting expense of amount \"" + expenseViewModel.expenseLive.getValue().getAmount() + "\"")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        expenseViewModel.deleteExpenseById().subscribe(
                            bool -> {
                                if (bool) {
                                    Toast.makeText(getContext(), "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Could not delete expense...", Toast.LENGTH_SHORT).show();
                                }
                                Navigation.findNavController(getView()).navigate(ViewExpenseFragmentDirections.actionNavigationExpenseViewToNavigationTripView());
                            }, err -> Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setGroupExpBinding(Expense expense) {

        binding.eachAmountText.setVisibility(View.VISIBLE);
        binding.eachAmountValue.setVisibility(View.VISIBLE);
        binding.view.setVisibility(View.VISIBLE);

        binding.debtorAvatar.setVisibility(View.VISIBLE);
        binding.debtorName.setVisibility(View.VISIBLE);
        binding.debtorText.setVisibility(View.VISIBLE);

        binding.memberListExpand.setVisibility(View.VISIBLE);

        binding.totalAmountText.setText("Total Amount");
        binding.totalAmountValue.setText(String.format("%.2f $", expense.getAmount()));
        Double perEachAmount = expense.getAmount() / (expense.getCreditors().size() == 0 ? 1d : (double) expense.getCreditors().size());
        binding.eachAmountValue.setText(String.format("%.2f $", perEachAmount));
        binding.typeValue.setText(expense.getType().name());
        binding.descValue.setText(expense.getDescription());

        if (!TextUtils.isEmpty(expense.getDebtor().getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(expense.getDebtor().getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(binding.debtorAvatar);
        } else {
            Glide.with(BaseApp.context).clear(binding.debtorAvatar);
            binding.debtorAvatar.setImageResource(R.drawable.default_user_avatar);
        }

        if (expense.getCreditors() != null && expense.getCreditors().size() != 0) {
            binding.nrContributors.setText(String.format("%d", expense.getCreditors().size()));
        } else {
            binding.nrContributors.setText("0");
        }

        expenseMembersAdapter.updateRecyclerView(expense.getCreditors());

    }

    private void setPersonalExpBinding(Expense expense) {

        binding.eachAmountText.setVisibility(View.GONE);
        binding.eachAmountValue.setVisibility(View.GONE);
        binding.view.setVisibility(View.GONE);

        binding.debtorAvatar.setVisibility(View.GONE);
        binding.debtorName.setVisibility(View.GONE);
        binding.debtorText.setVisibility(View.GONE);

        binding.memberListExpand.setVisibility(View.GONE);

        binding.totalAmountText.setText("You paid");
        binding.totalAmountValue.setText(expense.getAmount() + "$");
        binding.typeValue.setText(expense.getType().name());
        binding.descValue.setText(expense.getDescription());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        expenseViewModel.getExpense();
    }
}
