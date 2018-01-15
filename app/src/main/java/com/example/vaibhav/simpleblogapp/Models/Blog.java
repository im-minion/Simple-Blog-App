package com.example.vaibhav.simpleblogapp.Models;

/**
 * Created by vaibhav on 7/1/17.
 */

public class Blog {
    private String Title;
    private String DESCRIPTION;
    private String IMAGE;
    private String username;

    public Blog(String title, String DESCRIPTION, String IMAGE, String username) {
        this.Title = title;
        this.DESCRIPTION = DESCRIPTION;
        this.IMAGE = IMAGE;
        this.username = username;
    }

    public Blog() {

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
