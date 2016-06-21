package ustc.sse.a4print.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.Tools.CustomListView;
import ustc.sse.a4print.Tools.QRCodeUtil;
import ustc.sse.a4print.Tools.T;
import ustc.sse.a4print.activity.BaiduMapActivity;
import ustc.sse.a4print.alipay.PayDemoActivity;
import ustc.sse.a4print.dialog.TwoDimensionDialog;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends ListFragment{

    private MyAdapter adapter;
    private List<Map<String, Object>> mOrderData;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private CustomListView customListView;

    private static final int QR_WIDTH =600 ;
    private static final int QR_HEIGHT =600 ;

    private GestureDetector gestureDetector;
    final int UP = 0;
    final int DOWN = 1;

    private  TextView noOrders;
    private OrderFragment mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_order,container,false);
        gestureDetector=new GestureDetector(getActivity(),onGestureListener);
        noOrders= (TextView) v.findViewById(R.id.no_orders);
        adapter = new MyAdapter(getActivity());
        reLoadOrders(false);
        //setListAdapter(adapter);
        mContext=this;
        return v;
    }

    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    float x = e2.getX() - e1.getX();
                    float y = e2.getY() - e1.getY();

                    if (y> 0) {
                        doResult(DOWN);
                    } else if (y< 0) {
                        doResult(UP);
                    }
                    return true;
                }
            };

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void doResult(int action) {

        switch (action) {
            case UP:
                T.showShort(getActivity(),"UP");
                break;

            case DOWN:
               T.showShort(getActivity(),"down");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        customListView= (CustomListView) getListView();
        customListView.setonRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reLoadOrders(true);
            }
        });
    }
    public final class ViewHolder{
        public TextView printerName;
        public TextView orderState;
        public TextView totalPrice;
        //public TextView delectOrder;
        public LinearLayout toPay;
        public ListView  detialListview;
        public LinearLayout orderContainer;
        public List<Map<String, Object>> mDetailData;
        public LinearLayout detailLayout;
        public TextView deliveryInfo;
        public LinearLayout twoDimension;
        public LinearLayout moreLayout;
    }


    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mOrderData.size();
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
           // if (convertView == null) {
            if (true) {

                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_orders, null);
                holder.printerName= (TextView) convertView.findViewById(R.id.tv_item_printerName);
                holder.orderState= (TextView) convertView.findViewById(R.id.tv_item_orderState);
                holder.totalPrice= (TextView) convertView.findViewById(R.id.tv_item_totalPrice);
                holder.toPay= (LinearLayout) convertView.findViewById(R.id.btn_item_toPay);
                holder.orderContainer= (LinearLayout) convertView.findViewById(R.id.order_container);
                holder.mDetailData= (List<Map<String, Object>>) mOrderData.get(position).get("filesInfo");
                holder.deliveryInfo= (TextView) convertView.findViewById(R.id.tv_item_delivery_info);
                holder.twoDimension= (LinearLayout) convertView.findViewById(R.id.two_dimension);
                holder.moreLayout= (LinearLayout) convertView.findViewById(R.id.order_item_more_layout);
                for (int i=0;i<holder.mDetailData.size();i++){
                    ViewGroup rootView= (ViewGroup) mInflater.inflate(R.layout.item_detail,null);
                    TextView fileName= (TextView) rootView.findViewById(R.id.tv_item_fileName);
                    TextView filePages= (TextView) rootView.findViewById(R.id.tv_item_filePages);
                    TextView fileCopies= (TextView) rootView.findViewById(R.id.tv_item_fileCopies);
                    TextView price= (TextView) rootView.findViewById(R.id.tv_item_price);
                    ImageView fileType= (ImageView) rootView.findViewById(R.id.iv_item_fileType);
                    fileName.setText(holder.mDetailData.get(i).get("fileName").toString());
                    filePages.setText("  "+holder.mDetailData.get(i).get("filePages").toString()+" 页");
                    fileCopies.setText("  "+holder.mDetailData.get(i).get("fileCopies").toString()+" 份");
                    price.setText("   小计：" + holder.mDetailData.get(i).get("price").toString() + " 元");
                    String type=holder.mDetailData.get(i).get("fileName").toString().substring(holder.mDetailData.get(i).get("fileName").toString().indexOf(".")+1,holder.mDetailData.get(i).get("fileName").toString().length());
                    if (type.equals("doc")){
                        fileType.setImageResource(R.drawable.doc);
                    }else if (type.equals("docx"))
                    {
                        fileType.setImageResource(R.drawable.docx);
                    }
                    else if (type.equals("pdf"))
                    {
                        fileType.setImageResource(R.drawable.pdf);
                    }else if (type.equals("jpg"))
                    {
                        fileType.setImageResource(R.drawable.jpg);
                    }else if (type.equals("jpeg"))
                    {
                        fileType.setImageResource(R.drawable.jpeg);
                    }else if (type.equals("png"))
                    {
                        fileType.setImageResource(R.drawable.png);
                    }else if (type.equals("ppt"))
                    {
                        fileType.setImageResource(R.drawable.ppt);
                    }else if (type.equals("pptx"))
                    {
                        fileType.setImageResource(R.drawable.pptx);
                    }
                    else{
                        fileType.setImageResource(R.drawable.file);
                    }
                    holder.orderContainer.addView(rootView);
                }
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                holder.orderContainer.setLayoutParams(lp);
                convertView.setTag(holder);

            }else {

                holder = (ViewHolder)convertView.getTag();
                holder.mDetailData.clear();
                holder.mDetailData= (List<Map<String, Object>>) mOrderData.get(position).get("filesInfo");
                //holder.detailAdapter.notifyDataSetChanged();
            }
            holder.printerName.setText(mOrderData.get(position).get("printerName").toString());
            holder.orderState.setText(mOrderData.get(position).get("orderState").toString());
            if (!mOrderData.get(position).get("orderState").toString().equals("未付款")){
                holder.toPay.setVisibility(View.GONE);
            }
            else {
                holder.twoDimension.setVisibility(View.GONE);
            }
            if (mOrderData.get(position).get("deliveryWay").toString().trim().equals("2")){
                holder.deliveryInfo.setText("取件方式：配送    配送费：5.00元");
                holder.totalPrice.setText("订单总额 " +new Double(Double.parseDouble(mOrderData.get(position).get("totalPrice").toString())).toString()+ " 元");
            }else{
                holder.deliveryInfo.setText("取件方式：自取");
                holder.totalPrice.setText("订单总额 " + mOrderData.get(position).get("totalPrice").toString() + " 元");
            }
            holder.toPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PayDemoActivity.class);
                    intent.putExtra("orderNo",mOrderData.get(position).get("orderNo").toString());
                    intent.putExtra("totalPrice",mOrderData.get(position).get("totalPrice").toString());
                    startActivity(intent);
                }
            });
            holder.twoDimension.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getQRCode(mOrderData.get(position).get("orderNo").toString());
                }
            });
            holder.moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initPopWindow(v,mOrderData.get(position).get("orderId").toString());
                }
            });
            return convertView;
        }

    }

    private void showQRImage(String qrCode) {
        Resources res = getActivity().getResources();
        Bitmap logo = BitmapFactory.decodeResource(res, R.drawable.qrcode_logo);
        Bitmap bitmap = QRCodeUtil.createQRImage(qrCode, 600, 600, logo);
        TwoDimensionDialog dialog = new TwoDimensionDialog(bitmap);
        dialog.show(getActivity().getFragmentManager(), "twoDim");
    }

    private void deletedOrder(String orderId) {
        RequestParams params=new RequestParams();
        params.put("orderId", orderId);
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+ HostIp.ip+"/A4print/deleteOrderById.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject object = response;
                try {
                    boolean mes=object.getBoolean("success");
                    if (mes){
                        T.showShort(getActivity(), "删除成功");
                        reLoadOrders(false);
                    }else{
                        T.showShort(getActivity(), "哎哟,没删掉啊");
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

    private void getQRCode(String orderNo) {
        RequestParams params=new RequestParams();
        params.put("orderNo", orderNo);
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+ HostIp.ip+"/A4print/getEncodeOrderInfoByOderNo.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject object = response;
                try {
                    boolean mes=object.getBoolean("success");
                    if (mes){
                        String qrCode=object.getString("result");
                        showQRImage(qrCode);
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

    private void reLoadOrders(final boolean isRefresh){
        list.clear();
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://"+HostIp.ip+"/A4print/getOrdersByUserId.do", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object=response;
                //JSONObject message=object.getJSONObject("message");
                JSONArray result= null;
                try {
                    result = object.getJSONArray("result");
                    if (result.length()==0)
                    {
                    noOrders.setText("没有订单！");
                        mOrderData = list;
                        setListAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        customListView.onRefreshComplete();
                    }else {
                        noOrders.setText("");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject order1 = result.getJSONObject(i);
                            JSONArray files = order1.getJSONArray("orderFileRManageList");
                            JSONObject userInfo = order1.getJSONObject("userAddressRM");
                            int filePages = 0;
                            String filesInfo = "";

                            List<Map<String, Object>> orderInfo = new ArrayList<Map<String, Object>>();
                            for (int j = 0; j < files.length(); j++) {
                                JSONObject file = files.getJSONObject(j);
                                Map<String, Object> map1 = new HashMap<String, Object>();
                                map1.put("fileName", file.getString("fileName"));
                                map1.put("filePages", file.getInt("filePages"));
                                map1.put("fileCopies", file.getInt("perFileCopies"));
                                map1.put("price", file.getString("perFilePrice"));//每页的价格
                                map1.put("perFilePrice", file.getString("perFilePrice"));//当前文档的打印价格
                                orderInfo.add(map1);
                                filePages += file.getInt("filePages") * file.getInt("perFileCopies");
                            }
                            Map<String, Object> map0 = new HashMap<String, Object>();
                            map0.put("printerName", userInfo.get("printShopName").toString());
                            map0.put("printerAddress",userInfo.getString("address"));
                            map0.put("customerName", userInfo.getString("userName"));
                            map0.put("customerPhone", userInfo.getString("teleNumber"));
                            map0.put("orderNo", order1.getString("orderNo"));
                            map0.put("orderId", order1.get("id").toString());
                            map0.put("deliveryWay", order1.getString("deliveryWay"));
                            String oState = order1.get("orderState").toString();
                            if (oState.equals("1")) {
                                oState = "未付款";
                            } else if (oState.equals("2")) {
                                oState = "未打印";
                            } else if (oState.equals("3")) {
                                oState = "已打印";
                            }
                            map0.put("orderState", oState);
                            map0.put("totalPrice", order1.getString("totalPrice"));
                            map0.put("filesInfo", orderInfo);
                            list.add(map0);
                        }

                            mOrderData = list;
                            setListAdapter(adapter);
                            adapter.notifyDataSetChanged();
                           // if (isRefresh) {
                                customListView.onRefreshComplete();
                          // }
                            //adapter.notifyDataSetChanged();
                            //orderNo, userName, filesInfo,totalPages,address,teleNumber, createTime,finishTime,isDelivery,orderState
                            //addRowData(order1.get("orderNo").toString(), userInfo.get("userName").toString(), filesInfo, filePages + "", userInfo.get("address").toString(), userInfo.get("teleNumber").toString(), order1.getTimestamp("createTime").toString(), order1.get("orderState").toString().equals("3") ? order1.getTimestamp("finishTime").toString() : "未完成", order1.get("deliveryWay").toString(), order1.get("orderState").toString());
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

    private void NormalDialogStyleOne(final String orderId) {
        final NormalDialog dialog = new NormalDialog(getActivity());
        BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
        BaseAnimatorSet bas_out = new FadeExit();

        dialog.content("你真的要删除这个订单吗?")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();

            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {

                deletedOrder(orderId);
                dialog.dismiss();
            }
        });
    }
    /*
    two dimension image
     */
    public Bitmap createQRImage(String url)
    {
        try
        {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++)
            {
                for (int x = 0; x < QR_WIDTH; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            return bitmap;
        }
        catch (WriterException e)
        {
            e.printStackTrace();
            return  null;
        }
    }

    private void initPopWindow(View v, final String orderId) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_popup, null, false);
        TextView btn_delete = (TextView) view.findViewById(R.id.btn_delete);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,100,100, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, -66, -20);

        //设置popupWindow里的按钮的事件
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               NormalDialogStyleOne(orderId);
                popWindow.dismiss();
            }
        });
    }



    class  OrderDetail{
        private String fileName;
        private String filePages;
        private  String fileCopies;
        private  String price;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePages() {
            return filePages;
        }

        public void setFilePages(String filePages) {
            this.filePages = filePages;
        }

        public String getFileCopies() {
            return fileCopies;
        }

        public void setFileCopies(String fileCopies) {
            this.fileCopies = fileCopies;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
    class OrderItem{
        private List<OrderDetail> item;

        public OrderItem(){
            if (item==null){
                item=new ArrayList<OrderDetail>();
            }
        }

        public List<OrderDetail> getItem() {
            return item;
        }

        public void setItem(List<OrderDetail> item) {
            this.item = item;
        }

        public void addData(OrderDetail orderDetail){
            item.add(orderDetail);
        }
    }
}
