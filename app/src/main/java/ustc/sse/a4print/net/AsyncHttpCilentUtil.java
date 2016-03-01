package ustc.sse.a4print.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

/**
 * Created by Administrator on 2015/10/28.
 */
public class AsyncHttpCilentUtil {
    private static AsyncHttpClient client;

    public static AsyncHttpClient getInstance(Context paramContext) {
        if (client == null) {
            client = new AsyncHttpClient();
            PersistentCookieStore myCookieStore = new PersistentCookieStore(paramContext);
            client.setCookieStore(myCookieStore);
        }
        return client;
    }
}
