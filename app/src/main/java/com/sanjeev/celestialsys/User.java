package com.sanjeev.celestialsys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * Created by sanjeev-sh on 01/01/16.
 */

public class User implements Serializable {

    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String address;

    private String password;

    private String fbId;

    public User() {
    }

    public User(String name, String email) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(email)) {
            throw new IllegalArgumentException("First Name and Email Address are mandatory fields");
        }

        if (!Constants.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid Email Address");
        }

        String[] strings = name.split("\\s");
        firstName = strings[0];
        if (strings.length > 1) {
            lastName = strings[1];
        }
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getFullName() {
        return firstName + (lastName == null ? "" : " " + lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        User user;
        if (!(object instanceof User)) {
            return false;
        }

        user = (User) object;
        return email.equalsIgnoreCase(user.getEmail());
    }

    private void appendString(StringBuilder sb, String string) {
        if (!StringUtils.isEmpty(string)) {
            sb.append(string);
            sb.append(';');
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getId() > 0) {
            sb.append(getId());
            sb.append(';');
        }

        appendString(sb, getFullName());
        appendString(sb, getEmail());
        appendString(sb, getUsername());
        appendString(sb, getAddress());

        return sb.toString();
    }
}
