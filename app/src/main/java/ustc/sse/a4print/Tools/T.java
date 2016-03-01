package ustc.sse.a4print.Tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/4.
 * Toast 統一管理類
 */
public class T {

    public static boolean isShow = true;
    private static Toast toast=null;


    public static void showShort(Context context, String message)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }


    public static void showLong(Context context, String message)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }


    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,duration);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,duration);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }
}
