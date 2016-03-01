package ustc.sse.a4print.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;

/**
 * Created by Administrator on 2015/10/28.
 */
public class ManageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        User user= (User) getApplication();
//        if (user.getEmail()!=null&&user.getPassword()!=null){
//            loginMethod(user.getEmail(),user.getPassword(),ManageActivity.this);
//        }
//        else
//        {
//            Intent intent=new Intent(ManageActivity.this,LoginActivity.class);
//            startActivity(intent);
//        }
//       finish();
    }


}
