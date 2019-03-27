package net.vpnsdk.wanve.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.vpnsdk.demo.R;
import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.bean.VpnSelectBean;
import net.vpnsdk.wanve.utils.CheckPhoneUtil;
import net.vpnsdk.wanve.utils.CountDownTimerUtils;
import net.vpnsdk.wanve.utils.SpUtil;
import net.vpnsdk.wanve.utils.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class VpnLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "VpnLoginActivity";
    private EditText etVpnUser;
    private EditText etVpnPsd;
    private boolean isVpn = false;
    private boolean isYes = false;
    private boolean isNo = false;
    private TextView btCode;
    private PendingIntent deliverPI;
    private PendingIntent sentPI;
    private TextView tvTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn_login);

        initView();
    }

    /**
     * 获取控件id
     */
    private void initView() {

        etVpnUser = findViewById(R.id.editText3);
        etVpnPsd = findViewById(R.id.etPsd);
        btCode = findViewById(R.id.btCode);
        tvTip = findViewById(R.id.tvTip);

        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final RadioButton radioYes = findViewById(R.id.radioYes);
        final RadioButton radioNo = findViewById(R.id.radioNo);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioYes.getId() == checkedId){
                    isVpn = true;
                    isYes = true;
                    isNo = false;
                }
                if (radioNo.getId() == checkedId){
                    isVpn = false;
                    isYes = false;
                    isNo = true;
                }
            }
        });

        findViewById(R.id.ivShow).setOnClickListener(this);
        findViewById(R.id.ivClear).setOnClickListener(this);
        findViewById(R.id.btCode).setOnClickListener(this);

        /**
         * 记住vpn账号密码
         * 这里判断并且显示
         */
        VpnSelectBean vpnSelectBean = (VpnSelectBean) SpUtil.getObject(getApplicationContext(), Constant.vpnAccount,VpnSelectBean.class);
        if (vpnSelectBean!=null){
            etVpnUser.setText(vpnSelectBean.getVpnUser());
            etVpnPsd.setText(vpnSelectBean.getVpnPsd());
            radioYes.setChecked(vpnSelectBean.isYes());
            radioNo.setChecked(vpnSelectBean.isNo());
        }

        findViewById(R.id.btSure).setOnClickListener(this);

        //处理返回的发送状态
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,
                0);
        // register the Broadcast Receivers
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(),
                                "短信发送成功", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));


        //处理返回的接收状态
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        deliverPI = PendingIntent.getBroadcast(this, 0,
                deliverIntent, 0);
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                Toast.makeText(getApplicationContext(),
                        "收信人已经成功接收", Toast.LENGTH_SHORT)
                        .show();
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClear:
                etVpnUser.setText("");
                break;
            case R.id.ivShow:
                if (etVpnPsd.getInputType() == 129) {
                    etVpnPsd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etVpnPsd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etVpnPsd.setSelection(etVpnPsd.getText().toString().length());
                break;
            case R.id.btSure:
                String VpnUser = etVpnUser.getText().toString();
                String VpnPsd = etVpnPsd.getText().toString();

                VpnSelectBean vpnSelectBean = new VpnSelectBean(VpnUser, VpnPsd, isVpn,isYes,isNo);

                if (isVpn) {
                    if (TextUtils.isEmpty(VpnUser)){
                        ToastUtil.show(getApplicationContext(),"vpn账号不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(VpnPsd)){
                        ToastUtil.show(getApplicationContext(),"vpn密码不能为空");
                        return;
                    }
                    tvTip.setText("");
                }
                SpUtil.putObject(this, Constant.vpnAccount,vpnSelectBean);
                finish();
                break;
            case R.id.btCode:
                CountDownTimerUtils timerUtils = new CountDownTimerUtils(btCode, 60000, 1000);
                timerUtils.start();

                getSimState(0);
                getSimState(1);

                break;
        }
    }


    /**
     * sim卡的状态码
     * TelephonyManager.SIM_STATE_READY
     TelephonyManager.IM_STATE_ABSENT
     TelephonyManager.SIM_STATE_NETWORK_LOCKED
     TelephonyManager.SIM_STATE_PIN_REQUIRED
     TelephonyManager.SIM_STATE_READY
     TelephonyManager.SIM_STATE_UNKNOWN

     * @param slotID
     */
    public void getSimState(int slotID) {
        Log.d(TAG, "getSimState: ----------------------------");
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> clz = (Class<TelephonyManager>) mTelephonyManager.getClass();
        int status = 0;
        try {
            Method mtd = clz.getMethod("getSimState", int.class);
            mtd.setAccessible(true);
            status = (Integer) mtd.invoke(mTelephonyManager, slotID);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSimState: "+status);
        if (status==TelephonyManager.SIM_STATE_READY){
            @SuppressLint("MissingPermission")
            String line1Number = mTelephonyManager.getLine1Number();
            Log.d(TAG, "getSimState: "+line1Number);
            //电信
            if (CheckPhoneUtil.isChinaTelecomPhoneNum(line1Number)){
                Log.d(TAG, "getSimState: 电信");
                sendSMS(Constant.PhoneVpnDx, "mm");
            }
            else if (CheckPhoneUtil.isChinaUnicomPhoneNum(line1Number)){
                //联通
                Log.d(TAG, "getSimState: 联通");
                sendSMS(Constant.PhoneVpnLt, "mm");
            }else {
                Log.d(TAG, "getSimState: 移动");
                //移动
                sendSMS(Constant.PhoneVpn, "mm");
            }

        }
    }


    /**
     * 直接调用短信接口发短信
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        Log.d(TAG, "sendSMS: "+phoneNumber+""+message);
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSimState(0);
                    getSimState(1);
                } else {
                    //这里是拒绝给APP发送信息权限，给个提示什么的说明一下都可以。
                    Toast.makeText(this, "请手动打开发送信息权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
