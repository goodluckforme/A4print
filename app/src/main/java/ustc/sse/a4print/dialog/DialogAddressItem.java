package ustc.sse.a4print.dialog;

/**
 * Created by Administrator on 2016/1/25.
 */
public class DialogAddressItem {
    public String operName;
    public int resId;
    public int addressId;

    public DialogAddressItem(String operName, int resId,int addressId)
    {
        this.operName = operName;
        this.resId = resId;
        this.addressId=addressId;
    }
}
