package com.example.expensetracker.ui.statistics;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.model.User;

import java.util.List;

public class ChooseTripRow extends Trip {
    private Boolean checked;

    public ChooseTripRow(Long id, String name, String description, String avatarUri, String location, Integer groupSize, List<User> users, List<Expense> expenses, Boolean checked) {
        super(id, name, description, avatarUri, location, groupSize, users, expenses);
        this.checked = checked;
    }

    public ChooseTripRow() {
        super();
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
