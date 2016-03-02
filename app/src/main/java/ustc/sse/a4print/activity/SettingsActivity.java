package ustc.sse.a4print.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import ustc.sse.a4print.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout dropLoginInfo;
    private RelativeLayout exitLayout;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        context=this;
        initView();
    }

    private void initView() {
        dropLoginInfo= (RelativeLayout) findViewById(R.id.settings_drop_login_info);
        dropLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences=getSharedPreferences("myinfo",MODE_PRIVATE);
                editor=preferences.edit();
                editor.putString("email","");
                editor.putString("password","");
                editor.commit();
                Intent intent=new Intent(context,LoginActivity.class);
                startActivity(intent);;
            }
        });
        exitLayout= (RelativeLayout) findViewById(R.id.settings_exitLayout);
        exitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                manager.restartPackage(getPackageName());
            }
        });
    }

}
