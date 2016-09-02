package com.shane.smartbanking.domain;

import java.io.Serializable;
import java.util.List;

public class Client implements Serializable {
    private List<Account> accounts;
    private Integer age;
    private String firstName;
    private Integer id;
    private String lastName;

    public static class Builder {
        private List<Account> accounts;
        private Integer age;
        private String firstName;
        private Integer id;
        private String lastName;

        public Builder(String lastName) {
            this.lastName = lastName;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public Builder accounts(List<Account> accounts) {
            this.accounts = accounts;
            return this;
        }

        public Builder copy(Client client) {
            this.id = client.getId();
            this.lastName = client.getLastName();
            this.firstName = client.getFirstName();
            this.accounts = client.getAccounts();
            this.age = client.getAge();
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }

    public Client(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.accounts = builder.accounts;
        this.age = builder.age;
        this.id = builder.id;
    }

    public Integer getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Integer getAge() {
        return this.age;
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
        Client client = (Client) o;
        if (this.id.equals(client.id)) {
            return this.lastName.equals(client.lastName);
        }
        return false;
    }

    public int hashCode() {
        return (this.id.hashCode() * 31) + this.lastName.hashCode();
    }

    public String toString() {
        return "Client{id=" + this.id + ", firstName='" + this.firstName + '\'' + ", lastName='" + this.lastName + '\'' + ", age=" + this.age + ", accounts=" + this.accounts + '}';
    }
}
