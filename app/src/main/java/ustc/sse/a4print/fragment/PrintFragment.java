package ustc.sse.a4print.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import ustc.sse.a4print.R;
import ustc.sse.a4print.Tools.T;
import ustc.sse.a4print.User;
import ustc.sse.a4print.activity.AlipayActivity;
import ustc.sse.a4print.activity.BaiduMapActivity;
import ustc.sse.a4print.activity.MainActivity;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrintFragment extends ListFragment implements BaiduMapActivity.PrinterSelectListener,MainActivity.AutoUploadFileListener,DocumentFragment.DocumentPageToPrintListener {


    private View v;
    private TextView tvSelectedPrinter;
    private ImageView ivDocsAdd;
    private Button btnSubmit;
    private LinearLayout btnRecommendList;
    private  LinearLayout btnSearchAround;
    private TextView tvMyAds;
    private RadioButton rbtnIsDelivery;
    private LinearLayout layoutMyAds;
    private RelativeLayout selectPrinterLayout;
    private LinearLayout selectPrinterOptionsLayout;

    private boolean isFold=true;

    public static PrintFragment frg1context;
    private boolean isFirstIn=true;
    private boolean isSuccess;
    private AlertDialog processDialog;
    private AlertDialog submitProcessDialog;

    public static  MyDocAdapter myDocAdapter;
    public static List<Map<String, Object>> mDocData;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private ArrayList<DialogMenuItem> testItems = new ArrayList<>();
    private ArrayList<DialogMenuItem> myAddressItems = new ArrayList<>();
    private ArrayList<DialogMenuItem> printTypeList;

    private Map<String,String> printAddMap=new HashMap<String,String>();
    private Map<String,String> printAddToIdMap=new HashMap<String,String>();
    private Map<String,String> myAddMap=new HashMap<String,String>();

    private Map<String,String> printType1=new HashMap<String,String>();//doc
    private Map<String,String> printType2=new HashMap<String,String>();//ppt
    private Map<String,String> printType3=new HashMap<String,String>();//pdf
    private Map<String,String> printType4=new HashMap<String,String>();//picture

    public static int currentPostion=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         v=inflater.inflate(R.layout.fragment_print,container,false);
        this.frg1context=this;
        initTab1();
        if (isFirstIn) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("docName", "");
            map.put("docPages", "");
            map.put("docPages_old","");
            map.put("docCopies", "1");
            map.put("docPath", "docPath");
            map.put("printType","打印类型");
            list.add(map);
            isFirstIn=false;
        }
        mDocData = list;
        myDocAdapter = new MyDocAdapter(getActivity());
        setListAdapter(myDocAdapter);
        loadPrinterAddress();
        loadMyAddress();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDocData.size()>0) {
            reSetListViewHeight();
//            for (Map<String, Object> item : mDocData) {
//
//
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("docName", item.get("docName").toString());
//                map.put("docPages", item.get("docPages").toString());
//                map.put("docPages_old", item.get("docPages_old".toString()));
//                map.put("docCopies", "1");
//                map.put("docPath", item.get("docPath").toString());
//                map.put("printType", "打印类型");
//                list.add(map);
//                mDocData = list;
//                myDocAdapter.notifyDataSetChanged();
//                reSetListViewHeight();
//            }
        }
    }

    private void loadPrinterAddress() {
        testItems.clear();
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+HostIp.ip+"/A4print/getPrinterAddresses.do", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                //JSONObject message=object.getJSONObject("message");
                JSONArray result = null;
                try {
                    result = object.getJSONArray("result");
                    if (result.length() == 0) {

                    } else {
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject address = result.getJSONObject(i);
                            String addressStr = address.getString("address");
                            String addressId = address.getString("id");
                            String printerId = address.getString("userId");
                            printAddMap.put(addressStr, addressId);
                            printAddToIdMap.put(addressStr, printerId);
                            testItems.add(new DialogMenuItem(addressStr, R.mipmap.ic_winstyle_album));
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
    private void loadMyAddress() {
        myAddressItems.clear();
        RequestParams params=new RequestParams();
        User userdate= (User) getActivity().getApplication();
        params.put("id", userdate.getId());
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+ HostIp.ip+"/A4print/getUserAddress.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject object = response;
                try {
                    JSONArray array = object.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = (JSONObject) array.get(i);
                        String address = (String) item.get("address");
                        String addressId= (String) item.get("id");
                        myAddMap.put(address.trim(),addressId);
                        myAddressItems.add(new DialogMenuItem(address,R.mipmap.ic_winstyle_album));
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

    private void initTab1() {


        btnSubmit= (Button) v.findViewById(R.id.btn_submitOrder);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user= (User) getActivity().getApplication();
                if (user.getDefaultAddressId()!=null) {
                    String printerAddressId=printAddMap.get(tvSelectedPrinter.getText().toString().trim());
                    if (printerAddressId!=null) {
                        if (orderDataValidate()){
                            submitProcessDialog = new SpotsDialog(getActivity(), "提交订单");
                            submitProcessDialog.show();
                            submitOrders(user.getDefaultAddressId(), printerAddressId);
                        }else{
                            T.showShort(getActivity(),"请设置完整的打印信息");
                        }
                    }else{
                        T.showShort(getActivity(),"未选择打印店！");
                    }
                }
            }
        });
        tvSelectedPrinter= (TextView) v.findViewById(R.id.tv_selected_printer);
        ivDocsAdd= (ImageView) v.findViewById(R.id.documents_add);
        ivDocsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("docName", "");
                map.put("docPages", "");
                map.put("docPages_old", "");
                map.put("docCopies", "1");
                map.put("docPath", "docPath");
                map.put("printType","打印类型");
                list.add(map);
                mDocData = list;
                myDocAdapter.notifyDataSetChanged();
                reSetListViewHeight();
            }
        });
        btnRecommendList= (LinearLayout) v.findViewById(R.id.recommend_list);
        btnRecommendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinterListDialog();
            }
        });
        btnSearchAround= (LinearLayout) v.findViewById(R.id.search_around);
        btnSearchAround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BaiduMapActivity.class);
                startActivity(i);
            }
        });
        layoutMyAds= (LinearLayout) v.findViewById(R.id.layout_myAddress);
        layoutMyAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAddressListDialog();
            }
        });
        rbtnIsDelivery= (RadioButton) v.findViewById(R.id.is_delivery);
        rbtnIsDelivery.setChecked(false);
        rbtnIsDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutMyAds.getLayoutParams();
                    params.setMargins(0, 1, 0, 0);
                    layoutMyAds.setVisibility(View.VISIBLE);
                    layoutMyAds.setLayoutParams(params);
                } else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutMyAds.getLayoutParams();
                    params.setMargins(0, -100, 0, 0);
                    layoutMyAds.setVisibility(View.INVISIBLE);
                    layoutMyAds.setLayoutParams(params);
                }
            }
        });
        tvMyAds= (TextView) v.findViewById(R.id.tv_myAddress);
        User user= (User) getActivity().getApplication();
        tvMyAds.setText(user.getDefaultAddress());
        selectPrinterOptionsLayout= (LinearLayout) v.findViewById(R.id.print_select_printer_options_layout);
        selectPrinterLayout= (RelativeLayout) v.findViewById(R.id.print_select_printer_layout);
        selectPrinterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFold) {
                    isFold=false;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) selectPrinterOptionsLayout.getLayoutParams();
                    params.setMargins(0, 1, 0, 0);
                    selectPrinterOptionsLayout.setVisibility(View.VISIBLE);
                    selectPrinterOptionsLayout.setLayoutParams(params);
                }else{
                    isFold=true;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) selectPrinterOptionsLayout.getLayoutParams();
                    params.setMargins(0, -100, 0, 0);
                    selectPrinterOptionsLayout.setVisibility(View.INVISIBLE);
                    selectPrinterOptionsLayout.setLayoutParams(params);
                }
            }
        });
    }

    private boolean orderDataValidate() {
        for(int i=0;i<mDocData.size();i++){
            if(mDocData.get(i).get("docName").toString().trim().equals("")
                    ||mDocData.get(i).get("printType").toString().trim().equals("打印类型"))
            {
                return false;
            }

        }
        return true;
    }

    private boolean submitOrders(String userAddressId,String printerAddressId) {
        isSuccess=false;
        ArrayList fileIdArray = new ArrayList();
        ArrayList fileNameArray = new ArrayList();
        ArrayList perFilePagesArray = new ArrayList();
        ArrayList perFileCopiesArray = new ArrayList();
        ArrayList perTotalPriceArray = new ArrayList();
        ArrayList perPrintTypeArray = new ArrayList();
        double totalPrice=0;
        for(int i=0;i<mDocData.size();i++){
            String fileNameStr=mDocData.get(i).get("docName").toString().trim();
            fileIdArray.add(mDocData.get(i).get("id").toString());
            fileNameArray.add(fileNameStr);
            perFilePagesArray.add(mDocData.get(i).get("docPages").toString());
            perFileCopiesArray.add(mDocData.get(i).get("docCopies").toString());
            String fileType=fileNameStr.substring(fileNameStr.lastIndexOf(".")+1,fileNameStr.length());
            double perPrice=0;
            if (fileType.equals("doc")||fileType.equals("docx"))//doc docx
            {
                perPrice=Double.parseDouble(printType1.get(mDocData.get(i).get("printType").toString()));
            }else if (fileType.equals("ppt")||fileType.equals("pptx")){//ppt pptx
                perPrice=Double.parseDouble(printType2.get(mDocData.get(i).get("printType").toString()));
            }else if(fileType.equals("pdf")){//pdf
                perPrice=Double.parseDouble(printType3.get(mDocData.get(i).get("printType").toString()));
            }else if (fileType.equals("png")||fileType.equals("gif")||fileType.equals("jpg")){//picture
                perPrice=Double.parseDouble(printType4.get(mDocData.get(i).get("printType").toString()));
            }else{//other
            }
            double item=Integer.parseInt(mDocData.get(i).get("docPages").toString())*Integer.parseInt(mDocData.get(i).get("docCopies").toString())*perPrice;
            totalPrice+=item;
            perTotalPriceArray.add(item+"");
            perPrintTypeArray.add(perPrice);
        }


        RequestParams params=new RequestParams();
        User userdate= (User) getActivity().getApplication();
        params.put("userAddressId", rbtnIsDelivery.isChecked()?myAddMap.get(tvMyAds.getText().toString().trim()):userAddressId);
        params.put("printerAddressId", printerAddressId);
        params.put("deliveryWay", rbtnIsDelivery.isChecked()?"2":"1");
        params.put("payWay", "1");
        if (rbtnIsDelivery.isChecked()){
            totalPrice+=5.0;//配送价格
        }
        params.put("totalPrice", totalPrice+"");
        params.put("fileIdArray",JSON.toJSONString(fileIdArray) );
        params.put("fileNameArray", JSON.toJSONString(fileNameArray));
        params.put("perFilePagesArray", JSON.toJSONString(perFilePagesArray));
        params.put("perFileCopiesArray", JSON.toJSONString(perFileCopiesArray));
        params.put("perTotalPriceArray", JSON.toJSONString(perTotalPriceArray));
        params.put("perPrintTypeArray", JSON.toJSONString(perPrintTypeArray));

        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+HostIp.ip+"/A4print/saveOrder.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                submitProcessDialog.dismiss();
                JSONObject object = response;
                    isSuccess=true;
                Intent intent=new Intent(getActivity(),AlipayActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                submitProcessDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                submitProcessDialog.dismiss();
                System.out.println("error" + responseString);
            }
        });
        return isSuccess;
    }

    public  void reSetListViewHeight() {
        if (myDocAdapter == null) {
            return;
        };
        int totalHeight = 0;
        for (int i = 0, len = myDocAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = myDocAdapter.getView(i, null, getListView());
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = getListView().getLayoutParams();
        params.height = totalHeight + (getListView().getDividerHeight() * (myDocAdapter.getCount() - 1));
        getListView().setLayoutParams(params);
    }

    @Override
    public void documentPageToPrint(int count) {
        for(int i=0;i<count;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("docName", "");
            map.put("docPages", "");
            map.put("docPages_old", "");
            map.put("docCopies", "1");
            map.put("docPath", "docPath");
            map.put("printType", "打印类型");
            list.add(map);
        }
        mDocData = list;
        myDocAdapter.notifyDataSetChanged();
        reSetListViewHeight();
    }

    @Override
    public void reSetListHeight() {
        reSetListViewHeight();
    }


    public final class ViewHolder{
    public ImageView docImage;
    public LinearLayout intoSelectFile;
    public TextView printTip;
    //public  ImageView docUpload;
    public TextView docName;
    public TextView docPages;
    public TextView docCopies;
    public ImageView docCopiesAdd;
    public ImageView docCopiesMinus;
        private LinearLayout layoutDocSetting;
        private TextView deliveryInfo;
        private LinearLayout printTypeLayout;
        private TextView printTypeText;

}

    public class MyDocAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyDocAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDocData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_order, null);
                holder.docImage= (ImageView) convertView.findViewById(R.id.document_add);
                holder.printTip= (TextView) convertView.findViewById(R.id.print_tip);
                holder.intoSelectFile= (LinearLayout) convertView.findViewById(R.id.print_into_select);
                holder.docName= (TextView) convertView.findViewById(R.id.document_name);
                holder.docPages= (TextView) convertView.findViewById(R.id.document_pages);
                //holder.docUpload= (ImageView) convertView.findViewById(R.id.document_upload);
                holder.docCopiesAdd= (ImageView) convertView.findViewById(R.id.document_add_copies);
                holder.docCopiesMinus= (ImageView) convertView.findViewById(R.id.document_minus_copies);
                holder.docCopies= (TextView) convertView.findViewById(R.id.document_copies);
                holder.layoutDocSetting= (LinearLayout) convertView.findViewById(R.id.document_setting);
                holder.deliveryInfo= (TextView) convertView.findViewById(R.id.tv_item_delivery_info);
                holder.printTypeLayout= (LinearLayout) convertView.findViewById(R.id.print_item_printType_layout);
                holder.printTypeText= (TextView) convertView.findViewById(R.id.print_printType_tv);
                convertView.setTag(holder);

            }else {

                holder = (ViewHolder)convertView.getTag();
            }

            holder.docName.setText(mDocData.get(position).get("docName").toString());
            holder.docPages.setText(mDocData.get(position).get("docPages").toString());
            holder.docCopies.setText(mDocData.get(position).get("docCopies").toString());
            holder.printTypeText.setText(mDocData.get(position).get("printType").toString());
            if (!mDocData.get(position).get("docPages").toString().equals("")) {
                holder.docPages.setText(mDocData.get(position).get("docPages").toString() + " 页");
                holder.layoutDocSetting.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutDocSetting.getLayoutParams();
                params.setMargins(0,1,0,0);
                holder.layoutDocSetting.setLayoutParams(params);

                holder.printTip.setText("");
            }
            holder.intoSelectFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPostion = position;
                    getActivity().showDialog(MainActivity.openfileDialogId);
                }
            });
            String tpye=mDocData.get(position).get("docName").toString().substring(mDocData.get(position).get("docName").toString().indexOf(".")+1,mDocData.get(position).get("docName").toString().length());
            if(tpye.equals("doc")){
                holder.docImage.setImageResource(R.drawable.doc);
            }else if (tpye.equals("docx")){
                holder.docImage.setImageResource(R.drawable.docx);
            }else if (tpye.equals("jpg")){
                holder.docImage.setImageResource(R.drawable.jpg);
            }else if (tpye.equals("png")){
                holder.docImage.setImageResource(R.drawable.png);
            }
            else if (tpye.equals("jpeg")){
                holder.docImage.setImageResource(R.drawable.jpeg);
            }else if (tpye.equals("ppt")){
                holder.docImage.setImageResource(R.drawable.ppt);
            }else if (tpye.equals("pptx")){
                holder.docImage.setImageResource(R.drawable.pptx);
            }
            else if (tpye.equals("pdf")){
                holder.docImage.setImageResource(R.drawable.pdf);
            }else{
                //holder.docImage.setImageResource(R.drawable.marquee_add);
            }
            if (mDocData.get(position).get("docName").toString().equals("docName")){
                //holder.docUpload.setEnabled(false);
            }else {
                //holder.docUpload.setEnabled(true);
            }
            holder.docCopiesAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     int copies=Integer.parseInt(mDocData.get(position).get("docCopies").toString())+1;
                    mDocData.get(position).put("docCopies",copies);
                    myDocAdapter.notifyDataSetChanged();
                }
            });
            holder.docCopiesMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int copies = Integer.parseInt(mDocData.get(position).get("docCopies").toString());
                    if (copies >= 2) {
                        mDocData.get(position).put("docCopies", --copies);
                        myDocAdapter.notifyDataSetChanged();
                    }
                }
            });
            holder.printTypeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileNameStr=mDocData.get(position).get("docName").toString();
                    printTypeDialog(fileNameStr.substring(fileNameStr.lastIndexOf(".")+1,fileNameStr.length()),position);
                }
            });
            return convertView;
        }

    }

    public  void upLoadFile(final String filePath) {


        RequestParams params=new RequestParams();
        User userdate= (User) getActivity().getApplication();
        params.put("id", userdate.getId());
        try {
            params.put("file",new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+HostIp.ip+"/A4print/addUploadFiles.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                try {
                    JSONArray array=object.getJSONArray("result");
                    JSONObject fileInfo=array.getJSONObject(0);
                    String id =fileInfo.getString("id");
                    String filePages=fileInfo.getString("filePages");
                    mDocData.get(currentPostion).put("id",id);
                    mDocData.get(currentPostion).put("docPages",filePages);
                    myDocAdapter.notifyDataSetChanged();
                    reSetListViewHeight();
                    processDialog.dismiss();
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

    private void loadPrice(String printerId) {
        RequestParams params=new RequestParams();
        params.put("printerId",printerId);
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+HostIp.ip+"/A4print/getPricesByPrinterId.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                //JSONObject message=object.getJSONObject("message");
                JSONArray result = null;
                try {
                    result = object.getJSONArray("result");
                    if (result.length() == 0) {

                    } else {
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject obj = result.getJSONObject(i);
                            String fileType = obj.getString("fileType");
                            String price=obj.getString("price");
                            String priceType=obj.getString("priceType");
                            if (fileType.equals("1"))//doc docx
                            {
                                printType1.put(priceType,price);
                            }else if (fileType.equals("2")){//ppt pptx
                                printType2.put(priceType,price);
                            }else if(fileType.equals("3")){//pdf
                                printType3.put(priceType,price);
                            }else if (fileType.equals("4")){//picture
                                printType4.put(priceType,price);
                            }else{//other

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

    private void PinterListDialog() {
        final NormalListDialog dialog = new NormalListDialog(getActivity(), testItems);
        BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
        BaseAnimatorSet bas_out = new FadeExit();
        dialog.title("请选择")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                T.showShort(getActivity(), testItems.get(position).operName);
                tvSelectedPrinter.setText(testItems.get(position).operName);
                loadPrice(printAddToIdMap.get(tvSelectedPrinter.getText().toString().trim()));
                dialog.dismiss();
            }
        });
    }

    private void MyAddressListDialog() {
        final NormalListDialog dialog = new NormalListDialog(getActivity(), myAddressItems);
        BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
        BaseAnimatorSet bas_out = new FadeExit();
        dialog.title("请选择")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                T.showShort(getActivity(), myAddressItems.get(position).operName);
                tvMyAds.setText(myAddressItems.get(position).operName);
                dialog.dismiss();
            }
        });
    }

    private void printTypeDialog(String fileType, final int location) {
        printTypeList=new ArrayList<>();
        if (fileType.equals("doc")||fileType.equals("docx"))//doc docx
        {
            if (printType1.size()==0){
                T.showShort(getActivity(),"请先选择打印店！");
                return;
            }
            for (String item:printType1.keySet()) {
                printTypeList.add(new DialogMenuItem(item,R.mipmap.ic_winstyle_album));
            }
        }else if (fileType.equals("ppt")||fileType.equals("pptx")){//ppt pptx
            if (printType2.size()==0){
                T.showShort(getActivity(),"请先选择打印店！");
                return;
            }
            for (String item:printType2.keySet()) {
                printTypeList.add(new DialogMenuItem(item,R.mipmap.ic_winstyle_album));
            }
        }else if(fileType.equals("pdf")){//pdf
            if (printType3.size()==0){
                T.showShort(getActivity(),"请先选择打印店！");
                return;
            }
            for (String item:printType3.keySet()) {
                printTypeList.add(new DialogMenuItem(item,R.mipmap.ic_winstyle_album));
            }
        }else if (fileType.equals("png")||fileType.equals("gif")||fileType.equals("jpg")){//picture
            if (printType4.size()==0){
                T.showShort(getActivity(),"请先选择打印店！");
                return;
            }
            for (String item:printType4.keySet()) {
                printTypeList.add(new DialogMenuItem(item,R.mipmap.ic_winstyle_album));
            }
        }else{//other

        }
        final NormalListDialog dialog = new NormalListDialog(getActivity(), printTypeList);
        BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
        BaseAnimatorSet bas_out = new FadeExit();
        dialog.title("打印类型")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                T.showShort(getActivity(), printTypeList.get(position).operName);
                mDocData.get(location).put("printType", printTypeList.get(position).operName);
                myDocAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void printerSelected(String printerId) {

        tvSelectedPrinter.setText(printerId);
        loadPrice(printAddToIdMap.get(tvSelectedPrinter.getText().toString().trim()));
    }

    @Override
    public void toUploadFile(int position) {

//                    if (!mDocData.get(position).get("docPages").toString().equals(mDocData.get(position).get("docPages_old").toString())) {
//                        mDocData.get(position).put("docPages_old",mDocData.get(position).get("docPages").toString());
                        String filePath=mDocData.get(position).get("docPath").toString();
                        if (!filePath.equals("docPath")&&!filePath.equals("")&&filePath!=null) {
                            processDialog = new SpotsDialog(getActivity(),"文件上传中");
                            processDialog.show();
                            upLoadFile(filePath);
                        }else
                        {
                            T.showShort(getActivity(), "未选择文件！");
                        }
                  //  }
    }
}

