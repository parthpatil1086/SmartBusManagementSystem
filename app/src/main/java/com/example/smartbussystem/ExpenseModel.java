package com.example.smartbussystem;

public class ExpenseModel {

    String expenseId;
    String routeName;
    String busNumber;
    String expenseDate;

    int revenue;
    int totalExpense;
    int netProfit;

    public ExpenseModel() {
    }

    public ExpenseModel(
            String expenseId,
            String routeName,
            String busNumber,
            String expenseDate,
            int revenue,
            int totalExpense,
            int netProfit
    ) {

        this.expenseId = expenseId;
        this.routeName = routeName;
        this.busNumber = busNumber;
        this.expenseDate = expenseDate;
        this.revenue = revenue;
        this.totalExpense = totalExpense;
        this.netProfit = netProfit;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public int getRevenue() {
        return revenue;
    }

    public int getTotalExpense() {
        return totalExpense;
    }

    public int getNetProfit() {
        return netProfit;
    }
}