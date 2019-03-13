package net.vpnsdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import net.vpnsdk.vpn.Common.VpnError;
import net.vpnsdk.vpn.VPNAccount;
import net.vpnsdk.vpn.VPNManager;
import net.vpnsdk.vpn.VPNManager.AAAMethod;
import net.vpnsdk.vpn.VPNManager.InputField;
import net.vpnsdk.vpn.VPNManager.InputFieldType;

import java.util.ArrayList;

/**
 * This activity is used to input user credentials for both login and device registration.
 */
public class LoginSdkActivity extends Activity implements OnItemSelectedListener {
	
	public final static String LOGIN_PASSWORD_ID = "multi_login_password";
	public final static String DEVICE_REG = "DEVICE_REG";
	public final static String ERROR_CODE = "ERROR_CODE";
	private static final int MAX_DEVICE_LENGTH = 64;
	
	private LinearLayout mLayout;
	private Spinner mSpinner;
	private RelativeLayout mLayoutDevName;
	private RelativeLayout mLayoutSpinner;
	private RelativeLayout mLayoutUserName;
	private TextView mTitle;
	private EditText mEdtDevName;
	private EditText mEdtUserName;
	private ArrayList<EditText> mPwdEdits = new ArrayList<EditText>();
	private boolean mIsDevReg = false;
	private boolean mOnlyOneMethod = false;
	private boolean mErrorShown = false; 
	private AlertDialog mAlertDlg;
	private static VPNAccount mVpnAccount;
	private AAAMethod[] mMethods;
	private int mErrorCode;
	private AAAMethod mSelectedMethod;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Dialog_NoActionBar);
        setContentView(R.layout.login_multi_steps);
        mMethods = MainActivity.getMethods();
        mIsDevReg = getIntent().getExtras().getBoolean(DEVICE_REG);
        mOnlyOneMethod = (1 == mMethods.length);
        mErrorCode = getIntent().getExtras().getInt(ERROR_CODE);
        loadComponents();
        createMethodsDropDownList();
        if (mIsDevReg || mOnlyOneMethod) {
        	mSelectedMethod = mMethods[0];
    		createInputUI(mSelectedMethod);
        }
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!mErrorShown) {
			if ((VpnError.ERR_WRONG_USER_PASS == mErrorCode)) {
				alert(getString(R.string.error), getString(R.string.user_pwd_error));
	        } else if (mErrorCode > 0) {
	        	alert(getString(R.string.error), "internal error " + mErrorCode);
	        }
			mErrorShown = true;
		}
	}
	
	@Override
	public void onBackPressed(){
		onCancel();
	}
	
	@Override
	public void onDestroy() {
		if (mAlertDlg != null) {
			mAlertDlg.dismiss();
		}
		
		super.onDestroy();
	}
	
	/**
	 * Alert a message dialog.
	 * @param title the title of the dialog.
	 * @param msg the message of the dialog.
	 */
	private void alert(String title, String msg) {
		mAlertDlg = new AlertDialog.Builder(this).setTitle(title)
			.setMessage(msg)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setNeutralButton(R.string.ok, null).show();
	}
	
	private void loadComponents() {
		mLayout = (LinearLayout) findViewById(R.id.layout_multi_login_pwd);
		mTitle = findViewById(R.id.txtTitle);
		mLayoutSpinner = (RelativeLayout) findViewById(R.id.layout_spinner);
		mLayoutDevName = (RelativeLayout) findViewById(R.id.layout_device_name);
		mLayoutUserName = (RelativeLayout) findViewById(R.id.layout_username);
		mSpinner = (Spinner) findViewById(R.id.spin_methods);
		mSpinner.setOnItemSelectedListener(this);
		mEdtDevName = (EditText) findViewById(R.id.edtDEvicenameMultiLoginVal);
		mEdtUserName = (EditText) findViewById(R.id.edtUsernameMultiLoginVal);
		
		if (mIsDevReg) {
			mLayoutDevName.setVisibility(View.VISIBLE);
			mTitle.setText(R.string.dev_reg);
		} else {
			mLayoutDevName.setVisibility(View.GONE);
		}
	}

	/**
	 * Create the DropDown list for authentication methods.
	 */
	private void createMethodsDropDownList() {
		final String[] arr_methods = new String[mMethods.length];
		// Only one method exists.
		if (mIsDevReg || mOnlyOneMethod) {
			mLayoutSpinner.setVisibility(View.GONE);
		}
		else {
			for (int i = 0; i < mMethods.length; i++) {
				AAAMethod method = mMethods[i];
				arr_methods[i] = method.getName();
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	                this, R.layout.spinner_method, arr_methods);
	        adapter.setDropDownViewResource(R.layout.spinner_method);
	        mSpinner.setAdapter(adapter);
		}
	}

	/**
	 * Create an EditText component for password input.
	 * @param text the name of a server that is responsible for authenticating this password.
	 * @param id the id of an Android EditText object.
	 */
	private void createPwdInputUI(String text, int id) {
		RelativeLayout layout = new RelativeLayout(this);
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(params);
		mLayout.addView(layout);
		
		TextView txtPassword = new TextView(this);
		txtPassword.setId(id);
		txtPassword.setText(text);
		params = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.multi_step_login_txt_width),
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		txtPassword.setLayoutParams(params);
		txtPassword.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		layout.addView(txtPassword);
		
		EditText edtPassword = new EditText(this);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.RIGHT_OF, txtPassword.getId());
		edtPassword.setLayoutParams(params);
		edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(edtPassword);
		
		mPwdEdits.add(edtPassword);
	}
	
	/**
	 * Create the UI for credentials input according to the given authentication method.
	 * @param method the method that a user chooses for authentication. 
	 */
	private void createInputUI(AAAMethod method) {
		int id = 0, pwdnum = 1;
		String lbltext = "";
		mLayout.removeAllViews();
		mPwdEdits.clear();
		mLayoutUserName.setVisibility(View.GONE);
		mLayoutDevName.setVisibility(View.GONE);
		for (int i = 0; i < method.getInputFieldCount(); i++) {
			InputField field = method.getInputField(i);
			InputFieldType type = field.getType(); 
			switch (type) {
			case USERNAME:
				mLayoutUserName.setVisibility(View.VISIBLE);
				// field.getInputString() is not empty, that means the server sent a user name to the client.
				if ((field.getInputString() != null) && (field.getInputString().length() > 0)) {
					// Show the name to a user 
					mEdtUserName.setText(field.getInputString());
					// This name is not allowed to edit
					mEdtUserName.setEnabled(false);
				}
				break;
			case DEVICENAME:
				mLayoutDevName.setVisibility(View.VISIBLE);
				break;
			case PASSWORD:
				id = getResources().getIdentifier(LOGIN_PASSWORD_ID + pwdnum, "id", getApplication().getPackageName());
				if (1 == pwdnum) {
					lbltext = getString(R.string.lblPassword);
				} else {
					lbltext = getString(R.string.lblPassword) + "(" + field.getDescription() + ")";
				}
				pwdnum++;
				createPwdInputUI(lbltext, id);
				break;
			}
		}
	}
	
	public void clickLogin(View view) {
		if (!isInputValid()) {
			return;
		}
		int editIndex = 0;
		EditText edt;
		for (int i = 0; i < mSelectedMethod.getInputFieldCount(); i++) {
			InputField field = mSelectedMethod.getInputField(i);
			InputFieldType type = field.getType(); 
			switch (type) {
			case USERNAME:
				// input a user name.
				edt = (EditText) findViewById(R.id.edtUsernameMultiLoginVal);
				field.setInputString(edt.getText().toString().trim());
				break;
			case DEVICENAME:
				// input a device name.
				field.setInputString(mEdtDevName.getText().toString().trim());
				break;
			case PASSWORD:
				// input a password.
				edt = mPwdEdits.get(editIndex);
				field.setInputString(edt.getText().toString());
				editIndex++;
				break;
			}
		}
		if (mIsDevReg) {
			VPNManager.getInstance().registerWithMethod(mSelectedMethod);
		} else {
			VPNManager.getInstance().loginWithMethod(mSelectedMethod);
		}
		finish();
	}

	public void clickCancel(View view) {
		onCancel();
	}

	/**
	 * A user want to cancel the login or device registration process.
	 */
	private void onCancel() {
		if (mIsDevReg) {
			VPNManager.getInstance().registerWithMethod(null);
		} else {
			VPNManager.getInstance().loginWithMethod(null);
		}
		finish();
	}
	
	/**
	 * Check whether the user inputs are valid.
	 * @return true if all the user inputs are valid.
	 */
	private boolean isInputValid() {
		if (mLayoutDevName.getVisibility() == View.VISIBLE) {
			if (mEdtDevName.getText().toString().isEmpty()) {
				alert(getString(R.string.warning), getString(R.string.device_empty));
				return false;
			}
		}
		
		if(mLayoutDevName.getVisibility() == View.VISIBLE){
			if(mEdtDevName.getText().toString().trim().length() > MAX_DEVICE_LENGTH){
				alert(getString(R.string.warning), getString(R.string.device_not_valid));
				return false;
			}
		}
		
		if (mLayoutUserName.getVisibility() == View.VISIBLE) {
			if (mEdtUserName.getText().toString().isEmpty()) {
				alert(getString(R.string.warning), getString(R.string.user_empty));
				return false;
			}
		}
		for (int i = 0; i < mPwdEdits.size(); i++) {
			EditText edit = mPwdEdits.get(i);
			if (edit.getText().toString().isEmpty()) {
				alert(getString(R.string.warning), getString(R.string.password_empty));
				return false;
			}
		}
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		mSelectedMethod = mMethods[pos];
		createInputUI(mSelectedMethod);
	}
	
	public static VPNAccount getVpnAccount() {
		return mVpnAccount;
	}
	
	public static void setVpnAccount(VPNAccount account) {
		mVpnAccount = account;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// do nothing
	}
}