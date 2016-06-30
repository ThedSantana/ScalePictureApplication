package com.marco.scalepictureapplication;

import android.app.Activity;
import android.os.Bundle;

/**
 * User: KdMobiB
 * Date: 2016/6/30
 * Time: 17:21
 */
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ScaleDragImageView(this));
    }
}
