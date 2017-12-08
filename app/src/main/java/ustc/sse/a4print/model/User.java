package ustc.sse.a4print.model;

import android.app.Application;

/**
 * Created by Administrator on 2015/10/28.
 */
public class User extends Application {
    private String id;
    private  String userName;
    private  String email;
    private  String phoneNumber;
    private  String password;
    private  String defaultAddress;
    private  String defaultPhone;
    private String defaultAddressId;

    public void clear(){
        this.id=null;
        this.userName=null;
        this.email=null;
        this.phoneNumber=null;
        this.password=null;
        this.defaultAddress=null;
        this.defaultPhone=null;
        this.defaultAddressId=null;
    }
    public String getDefaultAddressId() {
        return defaultAddressId;
    }

    public void setDefaultAddressId(String defaultAddressId) {
        this.defaultAddressId = defaultAddressId;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getDefaultPhone() {
        return defaultPhone;
    }

    public void setDefaultPhone(String defaultPhone) {
        this.defaultPhone = defaultPhone;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
