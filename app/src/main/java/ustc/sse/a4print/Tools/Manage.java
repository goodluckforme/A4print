package ustc.sse.a4print.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.activity.LoginActivity;
import ustc.sse.a4print.R;
import ustc.sse.a4print.User;
import ustc.sse.a4print.activity.MainActivity;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

public class Manage extends Activity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_manage);

        preferences=getSharedPreferences("myinfo", MODE_PRIVATE);
        editor=preferences.edit();
        String email=preferences.getString("email","");
        String password=preferences.getString("password","");
        if (!email.equals("")&&!password.equals("")){
            loginMethod(email,password,Manage.this);
        }
        else
        {
            Intent intent=new Intent(Manage.this,LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
    private   void loginMethod(String email, String password, final Context context) {
        RequestParams params=new RequestParams();
        params.put("email", email);
        params.put("password", password);
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(context);
        client.post("http://"+ HostIp.ip+"/A4print/androidlogin.do", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String[] strings = new String(responseBody).split(",");
                if (strings[0].equals("success")) {
                    User user = (User) getApplication();
                    user.setId(strings[1]);
                    user.setUserName(strings[2]);
                    user.setEmail(strings[3]);
                    user.setPhoneNumber(strings[4]);
                    user.setPassword(strings[5]);
                    Intent intent = new Intent(context,MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "邮箱或密码错误！", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, responseBody.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
