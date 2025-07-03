package com.project.expense_tracker.Entity;

import lombok.Getter;

@Getter
public enum CategoryTypes {
    INCOME("Income"),
    EXPENSE("Expense");

    private String displayName;

    CategoryTypes(String displayName) {
        this.displayName = displayName;
    }
}
