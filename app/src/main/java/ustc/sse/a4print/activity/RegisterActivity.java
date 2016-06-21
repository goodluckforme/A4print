package ustc.sse.a4print.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.Tools.T;
import ustc.sse.a4print.Tools.UserInfoValidator;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUserName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfrimPwd;
    private Button btnRegister;
    private TextView tvCancel;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context=this;
        initview();
    }

    private void initview() {
        etUserName= (EditText) findViewById(R.id.register_username);
        etPhone= (EditText) findViewById(R.id.register_phone);
        etEmail= (EditText) findViewById(R.id.register_email);
        etPassword= (EditText) findViewById(R.id.register_password);
        etConfrimPwd= (EditText) findViewById(R.id.register_confirm_pwd);
        tvCancel= (TextView) findViewById(R.id.toobar_register_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegister= (Button) findViewById(R.id.register_btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getText().toString().equals(etConfrimPwd.getText().toString())) {
                    if (UserInfoValidator.validateUserName(etUserName.getText().toString())
                            && UserInfoValidator.validatePassword(etPassword.getText().toString())
                            && UserInfoValidator.validateTeleNumber(etPhone.getText().toString())
                            && UserInfoValidator.validateEmail(etEmail.getText().toString())) {
                        toRegister(etUserName.getText().toString(),etPhone.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString());
                    }
                    else{
                        T.showShort(context, "请输入完整信息");
                    }
                } else {
                    T.showShort(context, "两次输入密码不同");
                }
            }
        });
    }

    private void toRegister(String userName,String phone,String email,String password){
        RequestParams params=new RequestParams();
        params.put("email",email);
        params.put("password", password);
        params.put("username", userName);
        params.put("telenumber", phone);
        AsyncHttpClient client= AsyncHttpCilentUtil.getInstance(context);
        client.post("http://"+ HostIp.ip+"/A4print/androidRegister.do",params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                String strings=new String(responseBody);
                if(strings.equals("success")) {

                    T.showShort(context,"注册成功！");
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    T.showShort(context,"注册失败！");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                T.showShort(context, "注册失败！");
            }
        });
    }
}
