package com.pipit.waffle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kyle on 11/18/2014.
 */
public class LoginActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade());
        getWindow().setEnterTransition(new Fade());
        setContentView(R.layout.splash_screen);



        ImageView login = (ImageView) findViewById(R.id.fb_test_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ToolbarActivity.class);
                startActivity(intent);
            }
        });
    }
}
