package net.vpnsdk.wanve.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import net.vpnsdk.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter sa;
    private List<Map<String, Object>> dataList;
    public static final int REQ_CODE_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        initView();
    }

    private void initView() {
        //得到ListView
        mListView = (ListView) findViewById(R.id.listView);
        dataList = new ArrayList<Map<String, Object>>();
        //配置适配置器S
        sa = new SimpleAdapter(this, dataList, android.R.layout.simple_list_item_2,
                new String[]{"names", "message"}, new int[]{android.R.id.text1,
                android.R.id.text2});
        mListView.setAdapter(sa);
    }

    /**
     * 检查申请短信权限
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //未获取到读取短信权限

            //向系统申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, REQ_CODE_CONTACT);
        } else {
            query();
        }
    }

    /**
     * 点击读取短信
     *
     * @param view
     */
    public void readSMS(View view) {
        checkSMSPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断用户是否，同意 获取短信授权
        if (requestCode == REQ_CODE_CONTACT && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //获取到读取短信权限
            query();
        } else {
            Toast.makeText(this, "未获取到短信权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void query() {
        //读取所有短信
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "body", "date", "type"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int _id;
            String address;
            String body;
            String date;
            int type;
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                _id = cursor.getInt(0);
                address = cursor.getString(1);
                body = cursor.getString(2);
                date = cursor.getString(3);
                type = cursor.getInt(4);
                if (address.equals("106350123333777")) {
                    map.put("names", body);
                    map.put("address", address);
                    map.put("date", date);
                }
                Log.i("test", "_id=" + _id + " address=" + address + " body=" + body + " date=" + date + " type=" + type);
                dataList.add(map);
                // 通知适配器发生改变
                sa.notifyDataSetChanged();
            }
        }
        Log.d("获取", "query: " + dataList.toString());
        Log.d("获取", "query: " + dataList.get(0).get("names"));
        String msgBody = (String) dataList.get(0).get("names");
        Log.d("获取", "query: " + msgBody.substring(11, 17));
    }
}
