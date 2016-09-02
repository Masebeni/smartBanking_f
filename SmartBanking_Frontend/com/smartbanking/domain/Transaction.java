package com.shane.smartbanking.domain;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    public static final Integer DEPOSIT;
    public static final Integer WITHDRAWAL;
    private Double amount;
    private Date date;
    private Integer id;
    private String transactionType;

    public static class Builder {
        private Double amount;
        private Date date;
        private Integer id;
        private String transactionType;

        public Builder(Date date) {
            this.date = date;
        }

        public Builder type(Integer transType) {
            if (transType == Transaction.DEPOSIT) {
                this.transactionType = "DEPOSIT";
            } else if (transType == Transaction.WITHDRAWAL) {
                this.transactionType = "WITHDRAWAL";
            } else {
                this.transactionType = "UNKNOWN";
            }
            return this;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder amount(Double amt) {
            this.amount = amt;
            return this;
        }

        public Builder copy(Transaction transaction) {
            this.id = transaction.getId();
            this.amount = transaction.getAmount();
            this.date = transaction.getDate();
            this.transactionType = transaction.getTransactionType();
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    static {
        DEPOSIT = Integer.valueOf(0);
        WITHDRAWAL = Integer.valueOf(1);
    }

    public Transaction(Builder builder) {
        this.amount = builder.amount;
        this.date = builder.date;
        this.id = builder.id;
        this.transactionType = builder.transactionType;
    }

    public static Integer getTransactionType(String type) {
        if (type == null) {
            return Integer.valueOf(2);
        }
        if (type.equals("DEPOSIT")) {
            return Integer.valueOf(0);
        }
        if (type.equals("WITHDRAWAL")) {
            return Integer.valueOf(1);
        }
        return Integer.valueOf(2);
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public Integer getId() {
        return this.id;
    }

    public Double getAmount() {
        return this.amount;
    }

    public Date getDate() {
        return this.date;
    }
}
