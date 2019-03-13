package net.vpnsdk.wanve.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.vpnsdk.wanve.utils.LoadDialog;

import butterknife.ButterKnife;

/**
 * Created by zhou
 * on 2018/8/2.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public abstract int getLayout();

    public abstract void init();

    protected LoadDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        dialog = new LoadDialog(this,false,"加载中");
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}