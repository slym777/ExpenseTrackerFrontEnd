package com.example.expensetracker.ui.trips;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Trip;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class TripViewModel extends ViewModel {

    public MutableLiveData<List<Trip>> tripLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    public void getAllTrips(){
        disposableLinkedList.add(TripApi.getAllTrips().subscribe(list -> {
            tripLiveList.postValue(list);
        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getLocalizedMessage());
        }));
    }

//    public void createHub(String hubName, String hubDescription, String currency){
//        Hub newHub = new Hub(hubName, hubDescription, currency);
//        disposableLinkedList.add(HubApi.createHub(newHub).subscribe(bool -> {
//            loadData();
//            Timber.d("Hub created successfully");
//        }, error -> {
//            Timber.e(error.getMessage());
//            errorLiveMsg.postValue(error.getLocalizedMessage());
//        }));
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}