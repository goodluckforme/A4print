package ustc.sse.a4print.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.fragment.PrintFragment;
import ustc.sse.a4print.model.PrintShopBean;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

public class PrintShopActivity extends AppCompatActivity {

    public static ArrayList<PrintShopBean> printShopList=new ArrayList<>();
    private FloatingActionButton fab;
    private ListView listView;
    private MyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_shop);
        getData();
        initView();
        //给页面设置工具栏
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置工具栏标题
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("A4print");
    }

    private void initView() {
        fab= (FloatingActionButton) findViewById(R.id.print_shop_floatactionbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BaiduMapActivity.class);
                startActivity(i);
                finish();
            }
        });
        listView= (ListView) findViewById(R.id.print_shop_listview);
        mAdapter=new MyAdapter(getApplicationContext());
        listView.setAdapter(mAdapter);
    }

    public void getData() {
        loadPrintShopInfo();
    }

    private class MyAdapter extends BaseAdapter{

        private LayoutInflater mInflater;
        private ImageLoader imageLoader=ImageLoader.getInstance();

        public MyAdapter(Context context){
            mInflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return printShopList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView==null){
                convertView=mInflater.inflate(R.layout.item_printshop,null);
                holder=new ViewHolder();
                holder.shopImage= (ImageView) convertView.findViewById(R.id.item_print_shop_image);
                holder.shopName= (TextView) convertView.findViewById(R.id.item_print_shop_name);
                holder.shopAddress= (TextView) convertView.findViewById(R.id.item_print_shop_address);
                holder.printInfo= (TextView) convertView.findViewById(R.id.item_print_printinfo);
                holder.zan= (TextView) convertView.findViewById(R.id.item_print_tv_zan);
                holder.selectThisShop= (LinearLayout) convertView.findViewById(R.id.item_print_shop_select_this_shop);

                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.shopName.setText(printShopList.get(position).getPrintShopName());
            holder.shopAddress.setText(printShopList.get(position).getAddress());
            holder.zan.setText(printShopList.get(position).getPraise()+"");
            holder.selectThisShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectThisShopListener psl= PrintFragment.frg1context;
                    psl.selectedThisShop(printShopList.get(position).getPrintShopName());
                    finish();
                }
            });
            imageLoader.displayImage("http://www.a4print.cn/A4print/"+printShopList.get(position).getPrintShopImage(),holder.shopImage);
            return convertView;
        }
        class ViewHolder{
            ImageView shopImage;
            TextView shopName;
            TextView shopAddress;
            TextView printInfo;
            TextView zan;
            LinearLayout selectThisShop;
        }
    }

    public  void reSetListViewHeight() {
        if (mAdapter == null) {
            return;
        };
        int totalHeight = 0;
        for (int i = 0, len = mAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = mAdapter.getView(i, null,listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        totalHeight+=400;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void loadPrintShopInfo() {
        printShopList.clear();
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getApplicationContext());
        client.post("http://"+ HostIp.ip+"/A4print/getPrinterAddresses.do", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                JSONArray result = null;
                try {
                    result = object.getJSONArray("result");
                    if (result.length() == 0) {

                    } else {
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject address = result.getJSONObject(i);
                            PrintShopBean printShopBean=new PrintShopBean();
                            printShopBean.setId(address.getString("id"));
                            printShopBean.setUserId(address.getString("userId"));
                            printShopBean.setPrintShopImage(address.getString("printShopImage"));
                            printShopBean.setPrintShopName(address.getString("printShopName"));;
                            printShopBean.setAddress(address.getString("address"));
                            printShopBean.setUserName(address.getString("userName"));
                            printShopBean.setLatitude(address.getString("latitude"));
                            printShopBean.setLongitude(address.getString("longitude"));
                            printShopBean.setPraise(address.getInt("praise"));
                            printShopBean.setCityAreaId(address.getInt("cityAreaId"));
                            printShopList.add(printShopBean);
                        }
                        mAdapter.notifyDataSetChanged();
                        reSetListViewHeight();
                    }
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
    public interface SelectThisShopListener{
        void selectedThisShop(String printShopName);
    }
}
