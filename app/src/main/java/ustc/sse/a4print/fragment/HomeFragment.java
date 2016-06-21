package ustc.sse.a4print.fragment;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ustc.sse.a4print.R;
import ustc.sse.a4print.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ImageView printType1;
    private ImageView printType2;
    private ImageView printType3;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        printType1= (ImageView) v.findViewById(R.id.home_iv_printtype1);
        printType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mFragmentTabHost.setCurrentTabByTag("打印");
            }
        });
        printType2= (ImageView) v.findViewById(R.id.home_iv_printtype2);
        printType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mFragmentTabHost.setCurrentTabByTag("打印");
            }
        });
        printType3= (ImageView) v.findViewById(R.id.home_iv_printtype3);
        printType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mFragmentTabHost.setCurrentTabByTag("打印");
            }
        });
    }


}
