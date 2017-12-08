package ustc.sse.a4print.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.model.User;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;


public class DeliveryAddressActivity extends ListActivity implements ModifyAddressActivity.addDeliveryAddressListener {

    private List<Map<String, String>> mAddressData;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private DeliveryAddressAdapter adapter;
    public static Context context;
    private LinearLayout addAddressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        context=this;
        mAddressData=list;
        refreshAddressList();
        initView();
        adapter=new DeliveryAddressAdapter(context);
        setListAdapter(adapter);
    }

    private void initView() {
        addAddressLayout= (LinearLayout) findViewById(R.id.delivery_address_plus_layout);
        addAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ModifyAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void shouldRefreshData() {
        refreshAddressList();
    }

    public final class ViewHolder{
        public TextView name;
        public TextView phone;
        public TextView address;
        public LinearLayout itemLayout;
    }

    public class DeliveryAddressAdapter extends BaseAdapter{

        private LayoutInflater mInflater;
        public DeliveryAddressAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return mAddressData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
             if (convertView == null) {

                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_delivery_address, null);
                 holder.name= (TextView) convertView.findViewById(R.id.delivery_address_name);
                 holder.phone= (TextView) convertView.findViewById(R.id.delivery_address_phone);
                 holder.address= (TextView) convertView.findViewById(R.id.delivery_address_address);
                 holder.itemLayout= (LinearLayout) convertView.findViewById(R.id.delivery_address_item_layout);
                 convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.name.setText(mAddressData.get(position).get("name"));
            holder.phone.setText(mAddressData.get(position).get("phone"));
            holder.address.setText(mAddressData.get(position).get("address"));
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }
    }

    public  void refreshAddressList() {
        list.clear();
        RequestParams params=new RequestParams();
        User userdate= (User) getApplication();
        params.put("id", userdate.getId());
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(context);
        client.post("http://"+ HostIp.ip+"/A4print/getUserAddress.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                try {
                    JSONArray array = object.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = (JSONObject) array.get(i);
                        String address = (String) item.get("address");
                        String phone = (String) item.get("teleNumber");
                        String name=item.getString("userName");
                        String addressId= (String) item.get("id");

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("address", address);
                        map.put("phone", phone);
                        map.put("addressId",addressId);
                        map.put("name",name);
                        list.add(map);
                    }
                    mAddressData = list;
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println("error" + responseString);
            }
        });
    }
}
