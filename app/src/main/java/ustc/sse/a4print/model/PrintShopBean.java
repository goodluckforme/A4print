package ustc.sse.a4print.model;

/**
 * Created by Administrator on 2016-06-07.
 */
public class PrintShopBean {
    private String id;
    private String userId;
    private String printShopImage;
    private String printShopName;
    private String address;
    private String userName;
    private String teleNumber;
    private String longitude;
    private String latitude;
    private int cityAreaId;
    private int praise;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrintShopImage() {
        return printShopImage;
    }

    public void setPrintShopImage(String printShopImage) {
        this.printShopImage = printShopImage;
    }

    public String getPrintShopName() {
        return printShopName;
    }

    public void setPrintShopName(String printShopName) {
        this.printShopName = printShopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTeleNumber() {
        return teleNumber;
    }

    public void setTeleNumber(String teleNumber) {
        this.teleNumber = teleNumber;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getCityAreaId() {
        return cityAreaId;
    }

    public void setCityAreaId(int cityAreaId) {
        this.cityAreaId = cityAreaId;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }
}
