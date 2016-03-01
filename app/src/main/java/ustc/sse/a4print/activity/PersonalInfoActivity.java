package ustc.sse.a4print.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ustc.sse.a4print.R;
import ustc.sse.a4print.User;

public class PersonalInfoActivity extends AppCompatActivity {

    private ImageView pageCancel;
    private TextView userName;
    private TextView phone;
    private TextView email;
    private TextView defaultAddress;
    private TextView password;
    private RelativeLayout phoneLayout;
    private  RelativeLayout emailLayout;
    private  RelativeLayout defaultAddressLayout;
    private RelativeLayout passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initView();
    }

    private void initView() {
        pageCancel= (ImageView) findViewById(R.id.personal_info_cancel);
        userName= (TextView) findViewById(R.id.personal_info_userName);
        phone= (TextView) findViewById(R.id.personal_info_phone);
        email= (TextView) findViewById(R.id.personal_info_email);
        defaultAddress= (TextView) findViewById(R.id.personal_info_default_address);
        password= (TextView) findViewById(R.id.personal_info_password);
        phoneLayout= (RelativeLayout) findViewById(R.id.personal_info_phone_layout);
        emailLayout= (RelativeLayout) findViewById(R.id.personal_info_email_layout);
        defaultAddressLayout= (RelativeLayout) findViewById(R.id.personal_info_default_address_layout);
        passwordLayout= (RelativeLayout) findViewById(R.id.personal_info_password_layout);

        pageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        User user= (User) getApplication();
        userName.setText(user.getUserName());
        phone.setText(user.getPhoneNumber());
        email.setText(user.getEmail());
        defaultAddress.setText(user.getDefaultAddress());
        password.setText("111111");
    }

}
