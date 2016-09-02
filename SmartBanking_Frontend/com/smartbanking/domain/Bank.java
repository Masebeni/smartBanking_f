package com.shane.smartbanking.domain;

import java.io.Serializable;
import java.util.List;

public class Bank implements Serializable {
    private List<Account> accounts;
    private Integer id;
    private String name;

    public static class Builder {
        private List<Account> accounts;
        private Integer id;
        private String name;

        public Builder(String bankName) {
            this.name = bankName;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder accounts(List<Account> accounts) {
            this.accounts = accounts;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder copy(Bank bank) {
            this.id = bank.getId();
            this.name = bank.getName();
            return this;
        }

        public Bank build() {
            return new Bank(this);
        }
    }

    public Bank(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.accounts = builder.accounts;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.id.equals(((Bank) o).id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "Bank{id=" + this.id + ", name='" + this.name + '\'' + ", accounts=" + this.accounts + '}';
    }
}
