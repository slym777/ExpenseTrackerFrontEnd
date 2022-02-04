package com.example.expensetracker.ui.statistics;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.ExpenseApi;
import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.viewtrip.tripinfo.TripInfoViewModel;
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
    public Date min, max;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadExpensesPieChart(){
        disposableLinkedList.add(ExpenseApi.getCreditorExpensesByUserId(SharedPreferencesUtils.getUserId()).subscribe(list -> {

            allExpenseLiveList.postValue(list);

            expenseList = filterByDate(list);

        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getMessage());
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadExpenseFromTrip(Long tripId) {
        disposableLinkedList.add(TripApi.getTripByTripId(tripId).subscribe(trip -> {

            allExpenseLiveList.postValue(trip.getExpenses());
//            expenseList = filterByDate(trip.getExpenses());
            expenseList = trip.getExpenses();

        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getMessage());
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Expense> filterByDate(List<Expense> list) {
        return list.stream()
                .filter(e -> !min.after(e.getCreatedDate()) && !max.before(e.getCreatedDate()))
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