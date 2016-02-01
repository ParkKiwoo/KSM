package com.hardcopy.btchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by user on 2016-01-25.
 */
public class Intro extends Activity {

    ImageView intro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.intro);
        new Thread(new Runnable() {
            public void run() {
                try {
                    intro = (ImageView) findViewById(R.id.logo);
                    Animation alphaAnim = AnimationUtils.loadAnimation(Intro.this, R.anim.alpha);
                    intro.startAnimation(alphaAnim);
                    Thread.sleep(3500);
                    isIntro();
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void isIntro() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
