package ustc.sse.a4print.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.Tools.ActivityCollector;
import ustc.sse.a4print.Tools.CallbackBundle;
import ustc.sse.a4print.Tools.DensityUtil;
import ustc.sse.a4print.Tools.OpenFileDialog;
import ustc.sse.a4print.model.User;
import ustc.sse.a4print.fragment.DocumentFragment;
import ustc.sse.a4print.fragment.HomeFragment;
import ustc.sse.a4print.fragment.MyInfoFragment;
import ustc.sse.a4print.fragment.PrintFragment;
import ustc.sse.a4print.fragment.OrderFragment;
import ustc.sse.a4print.fragment.FragmentTabHost;
import ustc.sse.a4print.fragment.TabDB;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;


public class MainActivity extends FragmentActivity {
    public static FragmentTabHost mFragmentTabHost;
    private Context mContext;
    private PrintFragment printFragment;
    private OrderFragment orderFragment;
    private HomeFragment homeFragment;
    private DocumentFragment documentFragment;
    private MyInfoFragment myInfoFragment;
    static public int openfileDialogId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.mainactivity_layout);
        mContext = this;
        ActivityCollector.addActivity(this);
        initTab();
        setDefaultAddress();
        setSessionValid();
    }

    private void setDefaultAddress() {
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(mContext);
        client.post("http://"+ HostIp.ip+"/A4print/getUserAddress.do", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject object = response;
                try {
                    JSONArray array = object.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = (JSONObject) array.get(i);
                        String addressType=item.getString("addressType");
                        if (addressType.equals("1")) {
                            String defaultAddress=item.getString("defaultAddress");
                            if (defaultAddress.equals("1")) {
                                String address = (String) item.get("address");
                                String addressId = (String) item.get("id");
                                User user= (User) getApplication();
                                user.setDefaultAddress(address);
                                user.setDefaultAddressId(addressId);
                            }
                        }
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

    private void setSessionValid() {

    }

    private void initTab() {
        mFragmentTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mFragmentTabHost.setup(mContext, getSupportFragmentManager(), R.id.contentLayout);
        mFragmentTabHost.getTabWidget().setDividerDrawable(null);
        TabHost.TabSpec tabSpec;
        String tabs[] = TabDB.getTabText();
        for (int i = 0; i < tabs.length; i++) {
            tabSpec = mFragmentTabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i));
            mFragmentTabHost.addTab(tabSpec, TabDB.getFragments()[i], null);
            mFragmentTabHost.setTag(i);
        }
        printFragment = (PrintFragment) getSupportFragmentManager().findFragmentByTag("打印");
        orderFragment = (OrderFragment) getSupportFragmentManager().findFragmentByTag("订单");
        myInfoFragment = (MyInfoFragment) getSupportFragmentManager().findFragmentByTag("我");
        homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("首页");
        documentFragment = (DocumentFragment) getSupportFragmentManager().findFragmentByTag("文档");
    }

    private View getTabView(int index) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tabitem_havetext, null);
        TextView textView = (TextView) view.findViewById(R.id.itemTextView);
        textView.setText(TabDB.getTabText()[index]);
        Drawable drawable = getResources().getDrawable(TabDB.getTabImg()[index]);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, drawable, null, null);
        return view;
    }

    public void clickTheTab(){
        getTabView(1).callOnClick();
    }

    private View getMiddleTabView(int index) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tabitem_notext, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dp2px(mContext, 60), DensityUtil.dp2px(mContext, 45));
        params.setMargins(0, DensityUtil.dp2px(mContext, 5), 0, DensityUtil.dp2px(mContext, 5));
        view.setLayoutParams(params);
        return view;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag("首页");
        if (mainFragment != null) {
            mainFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==openfileDialogId){
            Map<String, Integer> images = new HashMap<String, Integer>();
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);   // 根目录图标
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);    //返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);   //文件夹图标
            images.put("wav", R.drawable.filedialog_wavfile);   //wav文件图标
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String filepath = bundle.getString("path");
                            setTitle(filepath); // 把文件路径显示在标题上
                            Toast.makeText(MainActivity.this, filepath, Toast.LENGTH_SHORT).show();
                            PrintFragment.mDocData.get(PrintFragment.currentPostion).put("docPath", filepath);
                            String fileName=filepath.substring(filepath.lastIndexOf("/")+1,filepath.length());
                            PrintFragment.mDocData.get(PrintFragment.currentPostion).put("docName",fileName);
                            PrintFragment.frg1context.myDocAdapter.notifyDataSetChanged();

                                    AutoUploadFileListener listener= PrintFragment.frg1context;
                                    listener.toUploadFile(PrintFragment.currentPostion);

                            //TextView text= (TextView) findViewById(R.id.selectedfilename);
                            //text.setText(filepath);
                        }
                    },
                    null,
                    images);
            return dialog;
        }
        return null;
    }
    public interface AutoUploadFileListener{
        void toUploadFile(int position);
    }
}
