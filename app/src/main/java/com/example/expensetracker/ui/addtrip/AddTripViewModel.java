package com.example.expensetracker.ui.addtrip;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.CreateTripRequest;
import com.example.expensetracker.model.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import javax.xml.parsers.FactoryConfigurationError;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class AddTripViewModel extends ViewModel {
    public List<User> allUserList = new ArrayList<>();
    public String name, description, location, avatarUri;

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
    public void addMembers() {
        List<User> selectedUsers = allUserList.stream()
                .filter(f -> f.isSelected())
                .collect(Collectors.toList());
        selectedUserList.postValue(selectedUsers);
    }

    public BehaviorSubject<Boolean> createTrip(String name, String description, String avatarUri, String location) throws JSONException {
        CreateTripRequest createTripRequest = new CreateTripRequest(name, description, avatarUri, location, selectedUserList.getValue());
        return TripApi.createTrip(createTripRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
