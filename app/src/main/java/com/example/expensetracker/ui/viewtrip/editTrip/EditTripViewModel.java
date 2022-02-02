package com.example.expensetracker.ui.viewtrip.editTrip;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.model.UpdateTripRequest;
import com.example.expensetracker.model.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class EditTripViewModel extends ViewModel {
    Long tripId;
    public List<User> allUserList = new ArrayList<>();
    public String name, description, location, avatarUri;

    public MutableLiveData<Trip> tripLive = new MutableLiveData<>();
    public MutableLiveData<List<User>> selectedUserList = new MutableLiveData<>();
    public MutableLiveData<List<User>> searchUserLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    public void getAllUsers(){
        disposableLinkedList.add(UserApi.getListOfUsers().subscribe(list -> {
            allUserList.clear();
            allUserList.addAll(list);

            for (User user: list) {
                if (selectedUserList.getValue() == null || selectedUserList.getValue().isEmpty()) {
                    break;
                }

                for (User selectedUser: selectedUserList.getValue()) {
                    if (user.getId().equals(selectedUser.getId())) {
                        user.setSelected(true);
                    }
                }
            }

            searchUserLiveList.setValue(list);
        }, error -> {
            Timber.e(error.getMessage());
            errorLiveMsg.postValue(error.getLocalizedMessage());
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setSelectedUsers(List<User> users) {
        selectedUserList.postValue(users);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addMembers() {
        List<User> selectedUsers = allUserList.stream()
                .filter(f -> f.isSelected())
                .collect(Collectors.toList());
        selectedUserList.postValue(selectedUsers);
    }

    public void getTripById(){
        disposableLinkedList.add(TripApi.getTripByTripId(tripId).subscribe(trip -> {
            tripLive.postValue(trip);
        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getLocalizedMessage());
        }));
    }

    public BehaviorSubject<Trip> updateTrip(String name, String description, String avatarUri, String location) throws JSONException {
        UpdateTripRequest updateTripRequest = new UpdateTripRequest(name, description, avatarUri, location, selectedUserList.getValue());
        return TripApi.updateTrip(tripId, updateTripRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
