package net.vpnsdk.vpn.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings.SettingNotFoundException;
import android.widget.ImageView;

public class AndroidCompatibleMethods {
	public static void ImageView_setBackGroundDrawable(ImageView image, Drawable drawable) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			image.setBackgroundDrawable(drawable);
		} else {
			image.setBackground(drawable);
		}
	}
	
	public static boolean isDeviceSecured(final Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
	        KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
	        return manager.isDeviceSecure();
	    } else {
	    	int isSecured = 0;
			try {
				isSecured = android.provider.Settings.System.getInt(context.getContentResolver(), 
						android.provider.Settings.System.LOCK_PATTERN_ENABLED);
			} catch (SettingNotFoundException e) {
				isSecured = 0;
			}		
			return isSecured == 1;
	    }
	}
}
