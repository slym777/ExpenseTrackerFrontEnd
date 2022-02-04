package com.example.expensetracker.ui.statistics;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.ExpenseApi;
import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class StatisticsViewModel extends ViewModel {

    public MutableLiveData<Trip> tripLive = new MutableLiveData<>();
    public List<Expense> expenseList = new ArrayList<>();
    public List<Expense> filteredList = new ArrayList<>();
    public MutableLiveData<List<Expense>> allExpenseLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private LinkedList<Disposable> disposableLinkedList = new LinkedList<>();
    public Date min, max;
    public boolean isGroup;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadExpensesPieChart(){
        disposableLinkedList.add(ExpenseApi.getCreditorExpensesByUserId(SharedPreferencesUtils.getUserId()).subscribe(list -> {

            allExpenseLiveList.postValue(list);
            expenseList = list;
            filterByDate();
            filterByIsGroup();

        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getMessage());
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadExpenseFromTrip(Long tripId) {
        disposableLinkedList.add(TripApi.getTripByTripId(tripId).subscribe(trip -> {

            tripLive.postValue(trip);
            expenseList = trip.getExpenses();
            filterByDate();
            filterByIsGroup();

        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getMessage());
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterByDate() {
        expenseList = expenseList.stream()
                .filter(e -> !min.after(e.getCreatedDate()) && !max.before(e.getCreatedDate()))
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterByIsGroup() {
        filteredList = expenseList.stream()
                .filter(e -> e.getIsGroupExpense() == isGroup)
                .collect(Collectors.toList());
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