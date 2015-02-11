package com.pipit.waffle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pipit.waffle.Objects.ClientData;

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


        /*Universal Image Loader*/
        Context context = this;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .build();
        ImageLoader.getInstance().init(config);



        ImageView login = (ImageView) findViewById(R.id.fb_test_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ToolbarActivity.class);
                startActivity(intent);
            }
        });

    }

}
