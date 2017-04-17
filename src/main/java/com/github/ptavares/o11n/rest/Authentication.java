package com.github.ptavares.o11n.rest;

/**
 * Store authentication for one user
 *
 * @author Patrick Tavares
 */
public class Authentication {
    /**
     * User name
     */
    private final String username;
    /**
     * User password
     */
    private final String password;

    /**
     * Default constructor
     *
     * @param username the user name
     * @param password the user password
     */
    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for the <code>username</code>
     *
     * @return The user name
     */
    public String getUsername() {
        return username;
    }


    /**
     * Getter for the <code>password</code>
     *
     * @return The user password
     */
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
