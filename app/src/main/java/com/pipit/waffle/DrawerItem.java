package com.pipit.waffle;

/**
 * Created by Kyle on 2/10/2015.
 */
public class DrawerItem {
    private String text;
    private int image_id;

    public DrawerItem(String text, int image_id) {
        this.text = text;
        this.image_id = image_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }
}
