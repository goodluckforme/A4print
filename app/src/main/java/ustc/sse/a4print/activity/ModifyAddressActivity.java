package ustc.sse.a4print.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.PublicKey;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.Tools.T;
import ustc.sse.a4print.dialog.AddressDialogFragment;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

public class ModifyAddressActivity extends AppCompatActivity {

    private ImageView cancel;
    private LinearLayout saveLayout;
    private TextView name;
    private TextView phone;
    private TextView address;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_address);
        context=this;
        initView();
    }

    private void initView() {
        cancel= (ImageView) findViewById(R.id.modify_address_cancel);
        saveLayout= (LinearLayout) findViewById(R.id.modify_address_save_layout);
        name= (TextView) findViewById(R.id.modify_address_name);
        phone= (TextView) findViewById(R.id.modify_address_phone);
        address= (TextView) findViewById(R.id.modify_address_addressInfo);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDeliveryAddress(name.getText().toString(),phone.getText().toString(),address.getText().toString());
            }
        });
    }

    private void addDeliveryAddress(String name,String phone,String address) {
        RequestParams params=new RequestParams();
        params.put("address", address);
        params.put("telenumber", phone);
        params.put("userName",name);
        params.put("cityAreaId","1");
        params.put("addressType", "1");
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(context);
        client.post("http://"+ HostIp.ip+"/A4print/saveAddress.do",params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                addDeliveryAddressListener listener= (addDeliveryAddressListener) DeliveryAddressActivity.context;
                listener.shouldRefreshData();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                T.showShort(context,"新增地址失败了！");
            }
        });
    }
    public interface addDeliveryAddressListener{
        void shouldRefreshData();
    }
}
