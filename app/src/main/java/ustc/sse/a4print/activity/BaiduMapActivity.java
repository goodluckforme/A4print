package ustc.sse.a4print.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.fragment.PrintFragment;
import ustc.sse.a4print.Info;
import ustc.sse.a4print.MyOrientationListener;
import ustc.sse.a4print.R;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;


public class BaiduMapActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Context context;

    // 定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;
    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mLocationMode;

    // 覆盖物相关
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerLy;

    private  ImageView btnMapTraffic;
    private  ImageView btnMapSwitch;
    private ImageView btnMapLocal;
    private  ImageView btnMapSearch;
    private  ImageView btnMapMode;
    private  Button btnSeletedPrinter;
    private  ImageView ivHeart;

    private boolean isGood;


    private TextView printerName;
    private TextView address;
    private TextView zan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);
        this.context=this;
        initView();
        initLocation();
        initMarker();
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }

        //地图上比例尺
        mMapView.showScaleControl(false);
         // 隐藏缩放控件
        mMapView.showZoomControls(false);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
                Info info = (Info) extraInfo.getSerializable("info");
                ImageView iv = (ImageView) mMarkerLy
                        .findViewById(R.id.id_info_img);
                TextView distance = (TextView) mMarkerLy
                        .findViewById(R.id.id_info_distance);
                address = (TextView) mMarkerLy.findViewById(R.id.id_info_address);
                zan = (TextView) mMarkerLy.findViewById(R.id.id_info_zan);
                ImageView heart = (ImageView) mMarkerLy.findViewById(R.id.id_info_heart);
                printerName = (TextView) mMarkerLy
                        .findViewById(R.id.id_info_name);

                iv.setImageResource(info.getImgId());
                LatLng p1 = new LatLng(mLatitude, mLongitude);
                LatLng p2 = new LatLng(info.getLatitude(), info.getLongitude());
                double distance1 = DistanceUtil.getDistance(p1, p2);
                distance.setText(new Double(distance1).intValue() + "米");
                printerName.setText(info.getName());

                address.setText(info.getAddress());
                zan.setText(info.getZan() + "");

                InfoWindow infoWindow;
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundResource(R.drawable.location_tips);
                tv.setPadding(30, 20, 30, 50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#ffffff"));

                final LatLng latLng = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y -= 47;
                LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

                BitmapDescriptor bmd = BitmapDescriptorFactory.fromView(tv);
                infoWindow = new InfoWindow(bmd, ll, 0, new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        mBaiduMap.hideInfoWindow();
                    }
                });

                mBaiduMap.showInfoWindow(infoWindow);

                mMarkerLy.setVisibility(View.VISIBLE);

                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        btnMapTraffic = (ImageView) findViewById(R.id.iv_map_traffic);
        btnMapTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                }
            }
        });
        btnMapSwitch= (ImageView) findViewById(R.id.iv_map_switch);
        btnMapSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
            }
        });
        btnMapLocal= (ImageView) findViewById(R.id.iv_map_local);
        btnMapLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerToMyLocation();
            }
        });
        btnMapSearch= (ImageView) findViewById(R.id.iv_map_search);
        btnMapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrinterShopByDistance(mLongitude, mLatitude, 50);
                addOverlays(Info.infos);
            }
        });
        btnMapMode= (ImageView) findViewById(R.id.iv_map_mode);
        btnMapMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationMode== MyLocationConfiguration.LocationMode.NORMAL){
                    mLocationMode= MyLocationConfiguration.LocationMode.COMPASS;
                }else if(mLocationMode== MyLocationConfiguration.LocationMode.COMPASS){
                    mLocationMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                }else{
                    mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;
                }
            }
        });
       btnSeletedPrinter= (Button) findViewById(R.id.btn_select_printer);
        btnSeletedPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrinterSelectListener psl= PrintFragment.frg1context;
                psl.printerSelected(address.getText().toString());
                finish();
            }
        });
        ivHeart= (ImageView) findViewById(R.id.id_info_heart);
        ivHeart.setImageResource(R.drawable.white_heart);
        isGood=false;
        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=Integer.parseInt(zan.getText().toString());

                if (isGood)
                {
                    --i;
                    zan.setText(i + "");
                    ivHeart.setImageResource(R.drawable.white_heart);
                    isGood=false;
                }
                else {
                    ++i;
                    zan.setText(i + "");
                    ivHeart.setImageResource(R.drawable.red_heart);
                    isGood=true;
                }
            }
        });
    }

    private void getPrinterShopByDistance(double longitude, double latitude, double distance) {
        RequestParams params=new RequestParams();
        params.put("longitude", longitude+"");
        params.put("latitude", latitude+"");
        params.put("distence",distance+"");
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(context);
        client.post("http://"+ HostIp.ip+"/A4print/getPrinterAddressesByDistence.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                try {
                    JSONArray array = object.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = (JSONObject) array.get(i);
                        String address = (String) item.get("address");
                        String teleNumber=item.getString("teleNumber");
                        String printShopName=item.getString("printShopName");
                        String userId=item.getString("userId");
                        String printAddressId=item.getString("id");
                        String longitude=item.getString("longitude");
                        String latitude=item.getString("latitude");
                        String praise=item.getString("praise");

                        Info.infos.add(new Info(Double.parseDouble(longitude), Double.parseDouble(latitude), R.drawable.a01, printShopName, Integer.parseInt(praise),address,teleNumber,printAddressId ));
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


    private void initMarker()
    {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
        mMarkerLy = (RelativeLayout) findViewById(R.id.id_maker_ly);
    }

    private void initLocation()
    {

        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        // 初始化图标
        mIconLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.navi_map_gps_locked);
        myOrientationListener = new MyOrientationListener(context);

        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mCurrentX = x;
                    }
                });

    }

    private void initView()
    {
        mMapView = (MapView) findViewById(R.id.id_bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // 开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        // 开启方向传感器
        myOrientationListener.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        // 停止方向传感器
        myOrientationListener.stop();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }


    /**
     * 将打印店信息显示到地图，并定位到距离当前最近的打印店
     *
     * @param infos
     */
    private void addOverlays(List<Info> infos)
    {
        mBaiduMap.clear();
        LatLng latLng = null;
        LatLng mLatLng=new LatLng(mLatitude,mLongitude);
        LatLng closerLatLng=null;
        Marker marker = null;
        OverlayOptions options;
        double distance=Double.MAX_VALUE;
        for (Info info : infos)
        {
            // 经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            // 图标
            options = new MarkerOptions().position(latLng).icon(mMarker)
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info);
            marker.setExtraInfo(arg0);
            if (distance>DistanceUtil.getDistance(latLng,mLatLng)){
                distance=DistanceUtil.getDistance(latLng,mLatLng);
                closerLatLng=latLng;
            }
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(closerLatLng);
        mBaiduMap.setMapStatus(msu);
    }

    /**
     * 定位到我的位置
     */
    private void centerToMyLocation()
    {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    private class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {

            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
            mBaiduMap.setMyLocationData(data);
            // 设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

            // 更新经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if (isFirstIn)
            {
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;

                Toast.makeText(context, location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public interface PrinterSelectListener{
        void printerSelected(String printerId);
    }
}


