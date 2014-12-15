package com.pipit.waffle.Objects;

/**
 * Created by Eric on 12/14/2014.
 */
public class Self {

    private static User user;
    /**
     * @return the myuser
     */
    public static User getUser() {
        if (user==null){
            user = new User("NullUID");
        }
        return user;
    }

    /**
     * @param myuser the myuser to set
     */
    public static void setUser(User myuser) {
        Self.user = myuser;
    }

    public static void updateSelf(String UID){
        //Check in saved preferences for an ID
        //	else generate an ID
        if (user==null){
            user = new User("NullUID");
        }
        Self.user.setuID(UID);
    }
}
