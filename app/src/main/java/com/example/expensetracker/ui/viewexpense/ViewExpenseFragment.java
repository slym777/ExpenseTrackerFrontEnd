package com.example.expensetracker.ui.viewexpense;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentExpenseViewBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.utils.BaseApp;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import timber.log.Timber;

public class ViewExpenseFragment extends Fragment {

    private FragmentExpenseViewBinding binding;
    private ExpenseViewModel expenseViewModel;

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

    }

    private void setGroupExpBinding(Expense expense) {

        binding.eachAmountText.setVisibility(View.VISIBLE);
        binding.eachAmountValue.setVisibility(View.VISIBLE);
        binding.view.setVisibility(View.VISIBLE);

        binding.debtorAvatar.setVisibility(View.VISIBLE);
        binding.debtorName.setVisibility(View.VISIBLE);
        binding.debtorText.setVisibility(View.VISIBLE);

        binding.ftiMemberList.setVisibility(View.VISIBLE);

        binding.totalAmountValue.setText(expense.getAmount() + "$");
        Double perEachAmount = expense.getAmount() / (expense.getCreditors().size() == 0 ? 1d : (double) expense.getCreditors().size());
        binding.eachAmountValue.setText(perEachAmount.toString() + "$");
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
            binding.nrContributors.setText(expense.getCreditors().size());
        } else {
            binding.nrContributors.setText("0");
        }

    }

    private void setPersonalExpBinding(Expense expense) {

        binding.eachAmountText.setVisibility(View.GONE);
        binding.eachAmountValue.setVisibility(View.GONE);
        binding.view.setVisibility(View.GONE);

        binding.debtorAvatar.setVisibility(View.GONE);
        binding.debtorName.setVisibility(View.GONE);
        binding.debtorText.setVisibility(View.GONE);

        binding.ftiMemberList.setVisibility(View.GONE);

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
