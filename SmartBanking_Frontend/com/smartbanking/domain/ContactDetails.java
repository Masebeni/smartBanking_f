package com.shane.smartbanking.domain;

import java.io.Serializable;
import java.util.List;

public class ContactDetails implements Serializable {
    private String cellNumber;
    private List<Client> client;
    private String emailAddress;
    private Integer id;
    private String password;

    public static class Builder {
        private String cellNumber;
        private List<Client> client;
        private String emailAddress;
        private Integer id;
        private String password;

        public Builder(String newCellNumber) {
            this.cellNumber = newCellNumber;
        }

        public Builder email(String newMailAddress) {
            this.emailAddress = newMailAddress;
            return this;
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder client(List<Client> newClient) {
            this.client = newClient;
            return this;
        }

        public Builder password(String newPassword) {
            this.password = newPassword;
            return this;
        }

        public Builder copy(ContactDetails contactDetails) {
            this.id = contactDetails.getId();
            this.client = contactDetails.getClient();
            this.emailAddress = contactDetails.getEmailAddress();
            this.cellNumber = contactDetails.getCellNumber();
            return this;
        }

        public ContactDetails build() {
            return new ContactDetails(this);
        }
    }

    public ContactDetails(Builder builder) {
        this.id = builder.id;
        this.emailAddress = builder.emailAddress;
        this.cellNumber = builder.cellNumber;
        this.client = builder.client;
        this.password = builder.password;
    }

    public String getCellNumber() {
        return this.cellNumber;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public List<Client> getClient() {
        return this.client;
    }

    public String getPassword() {
        return this.password;
    }

    public Integer getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.id.equals(((ContactDetails) o).id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "ContactDetails{id=" + this.id + ", cellNumber='" + this.cellNumber + '\'' + ", emailAddress='" + this.emailAddress + '\'' + ", password='" + this.password + '\'' + ", client=" + this.client + '}';
    }
}
