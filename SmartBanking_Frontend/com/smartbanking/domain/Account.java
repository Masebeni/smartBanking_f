package com.shane.smartbanking.domain;

import java.io.Serializable;
import java.util.List;

public class Account implements Serializable {
    private Double balance;
    private Integer id;
    private String number;
    private List<Transaction> transactionList;

    public static class Builder {
        private Double balance;
        private Integer id;
        private String number;
        private List<Transaction> transactionList;

        public Builder(String number) {
            this.number = number;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder balance(Double amt) {
            this.balance = amt;
            return this;
        }

        public Builder transaction(List<Transaction> transactions) {
            this.transactionList = transactions;
            return this;
        }

        public Builder copy(Account account) {
            this.balance = account.getBalance();
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionList = account.getTransactionList();
            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }

    public Account(Builder builder) {
        this.id = builder.id;
        this.balance = builder.balance;
        this.number = builder.number;
        this.transactionList = builder.transactionList;
        processTransactions();
    }

    private void processTransactions() {
        if (this.transactionList != null && !this.transactionList.isEmpty()) {
            for (Transaction currentTransaction : this.transactionList) {
                if (currentTransaction.getTransactionType().equals("DEPOSIT") && currentTransaction.getAmount().doubleValue() > 0.0d) {
                    this.balance = Double.valueOf(this.balance.doubleValue() + currentTransaction.getAmount().doubleValue());
                } else if (currentTransaction.getTransactionType().equals("WITHDRAWAL") && currentTransaction.getAmount().doubleValue() > 0.0d && this.balance.doubleValue() > currentTransaction.getAmount().doubleValue()) {
                    this.balance = Double.valueOf(this.balance.doubleValue() - currentTransaction.getAmount().doubleValue());
                }
            }
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getNumber() {
        return this.number;
    }

    public Double getBalance() {
        return this.balance;
    }

    public List<Transaction> getTransactionList() {
        return this.transactionList;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        if (this.id.equals(account.id)) {
            return this.number.equals(account.number);
        }
        return false;
    }

    public int hashCode() {
        return (this.id.hashCode() * 31) + this.number.hashCode();
    }

    public String toString() {
        return "Account{id=" + this.id + ", number='" + this.number + '\'' + ", balance=" + this.balance + ", transactionList=" + this.transactionList + '}';
    }
}
