package ustc.sse.a4print.fragment;

import ustc.sse.a4print.R;
public class TabDB {

    public static String[] getTabText() {
        String[] tabs = {"首页", "打印", "订单", "文档", "我"};
        return tabs;
    }

    public static int[] getTabImg() {
        int[] imags = {R.drawable.tabbar_home_auto, R.drawable.tabbar_message_auto, R.drawable.tabbar_post_auto, R.drawable.tabbar_discover_auto, R.drawable.tabbar_profile_auto};
    return imags;
    }


    public static Class[] getFragments() {
        Class[] classess = {HomeFragment.class, PrintFragment.class, OrderFragment.class, DocumentFragment.class, MyInfoFragment.class};
        return classess;
    }


}
