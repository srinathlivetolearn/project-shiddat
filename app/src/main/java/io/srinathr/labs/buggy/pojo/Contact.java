package io.srinathr.labs.buggy.pojo;

import java.util.Objects;

public class Contact {

    private String contactId;
    private String displayName;
    private String number;
    private String email;

    public Contact() {

    }

    public Contact(String contactId, String displayName, String number, String email) {
        this.contactId = contactId;
        this.displayName = displayName;
        this.number = number;
        this.email = email;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(contactId, contact.contactId) &&
                Objects.equals(displayName, contact.displayName) &&
                Objects.equals(number, contact.number) &&
                Objects.equals(email, contact.email);
    }

    @Override
    public int hashCode() {

        return Objects.hash(contactId, displayName, number, email);
    }

    public void setEmail(String email) {

        this.email = email;
    }


}
