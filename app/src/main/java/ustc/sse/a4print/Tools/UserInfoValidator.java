package ustc.sse.a4print.Tools;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016-02-22.
 */
public class UserInfoValidator {

    public static boolean validateUserName(String userName){
        if(userName.length()<2||userName.length()>8||userName==null){
            return false;
        }else{
            final String REGEX_USERNAME = "^([\u4e00-\u9fa50-9a-zA-Z_]+)$";
            return Pattern.matches(REGEX_USERNAME, userName);
        }
    }
    public static boolean validateEmail(String email){
        if(email.length()>50)
            return false;
        else {
            final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            return Pattern.matches(REGEX_EMAIL, email);
        }
    }
    public static boolean validateTeleNumber(String teleNumber){
        final String REGEX_MOBILE = "^[1][3,4,5,8][0-9]{9}$";
        return Pattern.matches(REGEX_MOBILE, teleNumber);
    }
    public static boolean validatePassword(String password){
        if(password.length()<6||password.length()>20)
            return false;
        else {
            return Pattern.matches("^[a-zA-Z0-9_]+$", password);
        }
    }
    public static boolean validateDate(String datetime)
    {
        final String REGEX_DATETIME="^((?:19|20|21)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
        return Pattern.matches(REGEX_DATETIME, datetime);
    }
}
