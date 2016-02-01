package com.hardcopy.btchat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class Login extends Activity {
    ImageView ima1;
    EditText ed1;
    CheckBox chkbtn1;
    Button logbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ima1=(ImageView) findViewById(R.id.ima1);
        ed1=(EditText)findViewById(R.id.ed1);
        chkbtn1=(CheckBox)findViewById(R.id.chkbtn1);
        logbtn=(Button)findViewById(R.id.logBtn);

        logbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainPage.class);
                startActivity(intent);
            }
        });

    }

}
