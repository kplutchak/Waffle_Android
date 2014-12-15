package com.pipit.waffle.Objects;

/**
 * Created by Eric on 12/13/2014.
 */
public class User {
    private String uID;
    private String name;

    public User (String uID){
        setuID(uID);
    }

    /**
     * @return the uID
     */
    public String getuID() {
        return uID;
    }

    /**
     * @param uID the uID to set
     */
    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
