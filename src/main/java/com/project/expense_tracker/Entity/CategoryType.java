package com.project.expense_tracker.Entity;

import lombok.Getter;

@Getter
public enum CategoryType {
    INCOME("Income"),
    EXPENSE("Expense");

    private String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }
}
