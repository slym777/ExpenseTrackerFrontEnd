package com.example.expensetracker.ui.statistics;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.ExpenseApi;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class StatisticsViewModel extends ViewModel {

    public List<Expense> expenseList = new ArrayList<>();
    public MutableLiveData<List<Expense>> allExpenseLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadExpensesPieChart(Date weekStart, Date weekFinish){
        disposableLinkedList.add(ExpenseApi.getCreditorExpensesByUserId(SharedPreferencesUtils.getUserId()).subscribe(list -> {

            allExpenseLiveList.postValue(list);

            expenseList = list.stream()
                    .filter(e -> !weekStart.after(e.getCreatedDate()) && !weekFinish.before(e.getCreatedDate()))
                    .collect(Collectors.toList());

        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getMessage());
        }));
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList.clear();
        this.expenseList.addAll(expenseList);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}