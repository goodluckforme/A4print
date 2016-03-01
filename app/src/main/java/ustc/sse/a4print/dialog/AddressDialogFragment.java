package ustc.sse.a4print.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.User;
import ustc.sse.a4print.fragment.MyInfoFragment;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

/**
 * Created by Administrator on 2015/10/29.
 */
public class AddressDialogFragment extends DialogFragment {

    private EditText etAddAddress;
    private  EditText etAddPhone;
    private  EditText etCustomerName;
    private RadioButton rbSetDefault;
    private  String addressString;
    private  String phoneString;
    private String customerNameString;
    private  int type;
    private MyInfoFragment parentFrag;
    public static AddressDialogFragment instance;
    private String addressId;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public AddressDialogFragment(String customerNameString,String s, String s1,MyInfoFragment fragment) {
        super();
        this.customerNameString=customerNameString;
        this.addressString=s;
        this.phoneString=s1;
        this.instance=this;
        this.parentFrag=  fragment;
    }

    public AddressDialogFragment(MyInfoFragment fragment) {
        super();
        this.instance=this;
        this.parentFrag=  fragment;
    }

    public interface InfoInputListener
    {
        void InputComplete(String address, String phone);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.address_dialog, null);

        etAddAddress= (EditText) view.findViewById(R.id.et_add_address);
        etAddPhone= (EditText) view.findViewById(R.id.et_add_phone);
        etCustomerName= (EditText) view.findViewById(R.id.et_add_customerName);
        rbSetDefault= (RadioButton) view.findViewById(R.id.dialog_setDefault);
        rbSetDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        if (addressString!=null&&phoneString!=null&&customerNameString!=null){
            etAddAddress.setText(addressString);
            etAddPhone.setText(phoneString);
            etCustomerName.setText(customerNameString);
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // InfoInputListener infoInputListener= Fragment3.fragment3;
                                //infoInputListener.InputComplete(etAddAddress.getText().toString(),etAddPhone.getText().toString());
                                if (instance.getType() == 1) {
                                    //addAddress
                                    addAddress(etCustomerName.getText().toString().trim(),etAddAddress.getText().toString().trim(), etAddPhone.getText().toString().trim());

                                } else if (instance.getType() == 2) {
                                    //upDateAddress
                                    updateAddress(etCustomerName.getText().toString().trim(),etAddAddress.getText().toString(), etAddPhone.getText().toString(), instance.getAddressId());
                                }
                                if (rbSetDefault.isChecked()) {
                                    User user= (User) getActivity().getApplication();
                                    user.setDefaultAddress(etAddAddress.getText().toString().trim());
                                    user.setDefaultPhone(etAddPhone.getText().toString().trim());
                                }

                            }
                        }).setNegativeButton("取消", null);
        return builder.create();
    }

    private void updateAddress(String customerName,String address, String phone,String addressId) {
        RequestParams params=new RequestParams();
        params.put("address", address);
        params.put("telenumber", phone);
        params.put("addressId", addressId);
        params.put("userName",customerName);
        params.put("cityAreaId","1");
        params.put("addressType", "1");
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+ HostIp.ip+"/A4print/updateAddress.do",params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                System.out.println("s:"+new String(responseBody));
                //parentFrag.refreshAddressList();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(getActivity(),responseBody.toString(),Toast.LENGTH_SHORT).show();
                System.out.println("f:"+new String(responseBody));
            }
        });
    }

    private void addAddress(String customerName,String address, String phone) {
        RequestParams params=new RequestParams();
        params.put("address", address);
        params.put("telenumber", phone);
        params.put("userName",customerName);
        params.put("cityAreaId","1");
        params.put("addressType", "1");
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+HostIp.ip+"/A4print/saveAddress.do",params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                System.out.println("s:"+new String(responseBody));
                //parentFrag.refreshAddressList();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(getActivity(),responseBody.toString(),Toast.LENGTH_SHORT).show();
                System.out.println("f:"+new String(responseBody));
            }
        });
    }

}
