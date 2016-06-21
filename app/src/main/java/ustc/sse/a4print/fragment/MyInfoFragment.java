package ustc.sse.a4print.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ustc.sse.a4print.R;
import ustc.sse.a4print.model.User;
import ustc.sse.a4print.activity.BaiduMapActivity;
import ustc.sse.a4print.activity.DeliveryAddressActivity;
import ustc.sse.a4print.activity.PersonalInfoActivity;
import ustc.sse.a4print.activity.SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInfoFragment extends Fragment {

    private TextView tvMyName;
    private View view;
    private Activity mActivity;
    private Context mContext;
    private View mToolBar;
    private RelativeLayout personalInfo;
    private RelativeLayout deliveryAddressLayout;
    private RelativeLayout searchPrinterByMap;
    private RelativeLayout settingsLayout;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.profilefragment_layout, container, false);
        initView();
        //initToolBar();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mToolBar.setVisibility(View.VISIBLE);
    }

    private void initView() {
        tvMyName= (TextView) view.findViewById(R.id.profile_myname);
        User user= (User) getActivity().getApplication();
        tvMyName.setText(user.getUserName());
        personalInfo= (RelativeLayout) view.findViewById(R.id.myInfo_personal_info_layout);
        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
        deliveryAddressLayout= (RelativeLayout) view.findViewById(R.id.myInfo_delivery_address_layout);
        deliveryAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), DeliveryAddressActivity.class);
                startActivity(intent);
            }
        });
        searchPrinterByMap= (RelativeLayout) view.findViewById(R.id.myInfo_search_printer_by_map);
        searchPrinterByMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), BaiduMapActivity.class);
                startActivity(intent);
            }
        });
        settingsLayout= (RelativeLayout) view.findViewById(R.id.myInfo_settings_layout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


}
