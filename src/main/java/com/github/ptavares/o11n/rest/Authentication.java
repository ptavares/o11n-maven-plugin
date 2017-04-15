package com.github.ptavares.o11n.rest;

/**
 * Created by Patrick on 14/04/2017.
 */
public class Authentication {
    private final String username;
    private final String password;

    /**
     * Default constructor
     *
     * @param username
     * @param password
     */
    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
