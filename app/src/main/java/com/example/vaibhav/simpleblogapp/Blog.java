package com.example.vaibhav.simpleblogapp;

/**
 * Created by vaibhav on 7/1/17.
 */

public class Blog {

    private String Title;
    private String DESCRIPTION;
    private String IMAGE;
    public Blog(){

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



}
