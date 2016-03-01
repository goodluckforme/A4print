package ustc.sse.a4print;

/**
 * Created by Administrator on 2015/11/2.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Info implements Serializable
{
    private static final long serialVersionUID = -1010711775392052966L;
    private double latitude;
    private double longitude;
    private int imgId;
    private String name;
    private int zan;
    private String address;
    private String phone;
    private String printAddressId;

    public static List<Info> infos = new ArrayList<Info>();

//    static
//    {
//        infos.add(new Info(120.732956,31.282505, R.drawable.a01, "中科大软件学院",
//                "距离209米", 1456));
//        infos.add(new Info(120.739581,31.282988, R.drawable.a02, "中国人大云打印店",
//                "距离897米", 456));
//        infos.add(new Info(120.744486,31.282494, R.drawable.a03, "南大云打印店",
//                "距离249米", 1456));
//        infos.add(new Info(120.755993,31.274809, R.drawable.a04, "文辉人渣市场",
//                "距离679米", 1456));
//    }

    public Info( double longitude,double latitude, int imgId, String name,int zan,String address,String phone,String printAddressId)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.zan = zan;
        this.address=address;
        this.phone=phone;
        this.printAddressId=printAddressId;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public int getImgId()
    {
        return imgId;
    }

    public void setImgId(int imgId)
    {
        this.imgId = imgId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getZan()
    {
        return zan;
    }

    public void setZan(int zan)
    {
        this.zan = zan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrintAddressId() {
        return printAddressId;
    }

    public void setPrintAddressId(String printAddressId) {
        this.printAddressId = printAddressId;
    }
}
