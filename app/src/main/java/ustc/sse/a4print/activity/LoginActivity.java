package ustc.sse.a4print.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import ustc.sse.a4print.R;
import ustc.sse.a4print.User;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

public class LoginActivity extends Activity {

    private EditText mEmail;
    private  EditText mPassword;
    private Button mBtnLogin;
    private  Button mBtnCancel;
    private TextView tvRegister;
    private AlertDialog loginProcessDialog;
    private Context context;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;
        initView();

    }

    private void initView() {
        mEmail= (EditText) findViewById(R.id.login_edtId);
        mPassword= (EditText) findViewById(R.id.login_edtPwd);
        mBtnLogin= (Button) findViewById(R.id.login_btnLogin);
        tvRegister= (TextView) findViewById(R.id.login_tv_register);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
               if(email!=""&&password!=""&&email!=null&&password!=null){
                   loginProcessDialog=new SpotsDialog(context,"登录·····");
                   loginProcessDialog.show();
                   loginMethod(email, password, LoginActivity.this);
               }
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private   void loginMethod(String email, String password, final Context context) {
        RequestParams params=new RequestParams();
        params.put("email",email);
        params.put("password", password);
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(context);
        client.post("http://"+ HostIp.ip+"/A4print/androidlogin.do",params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                String[] strings=new String(responseBody).split(",");
                if(strings[0].equals("success")) {
                    loginProcessDialog.dismiss();
                   User user= (User)getApplication();
                    user.setId(strings[1]);
                    user.setUserName(strings[2]);
                    user.setEmail(strings[3]);
                    user.setPhoneNumber(strings[4]);
                    user.setPassword(strings[5]);
                    preferences=getSharedPreferences("myinfo", MODE_PRIVATE);
                    editor=preferences.edit();
                    editor.putString("email",strings[3]);
                    editor.putString("password",strings[5]);
                    editor.commit();
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    loginProcessDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "邮箱或密码错误！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loginProcessDialog.dismiss();
                Toast.makeText(LoginActivity.this,responseBody.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
