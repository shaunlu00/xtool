package org.crudboy.toolbar.mail;

public class MailUser {

    private String email;

    private String name;

    public MailUser(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
