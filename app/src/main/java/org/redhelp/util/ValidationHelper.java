package org.redhelp.util;

/**
 * Created by harshis on 8/3/14.
 */
public class ValidationHelper {

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }
    public final static boolean isValidName(String name) {
        if (name == null) {
            return false;
        } else if(name.length() < 1 || name.length() >30){
            return false;
        } else
            return true;
    }

    public final static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        } else if(password.length() < 1 || password.length() >30){
            return false;
        } else
            return true;
    }

    public final static boolean isValidPhonenumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        } else if(phoneNumber.length() < 1 || phoneNumber.length() >12){
            return false;
        } else
            return true;
    }

}
