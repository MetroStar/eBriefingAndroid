/*
Copyright (C) 2017 MetroStar Systems

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full license text can be found is the included LICENSE file.

You can freely use any of this software which you make publicly
available at no charge.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.metrostarsystems.ebriefing;

import java.security.MessageDigest;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Utilities {

	public static String MD5(String text) {
		try {
	    } catch (Exception e) { }

	   return Hashing.sha256().hashString(text, Charsets.UTF_8).toString();
			   
	}

	
	public static final int hash(String string) {         
	      return string.hashCode();  
	}
	
	public static void toggleSoftKeyboard(Activity activity) {
		if(activity != null) {
			InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
			
			inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}
	}
	
	public static void closeSoftKeyboard(Activity activity) {
		if(activity != null) {
			InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
			View focus = activity.getCurrentFocus();
			
		    //If no view currently has focus, create a new one, just so we can grab a window token from it
		    if(focus == null) {
		        focus = new View(activity);
		    }
	       
	        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}
	
	public static void closeSoftKeyboard(Context c, IBinder windowToken) {
	    InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
	    mgr.hideSoftInputFromWindow(windowToken, 0);
	}
	
	public static void openSoftKeyboard(Context c, EditText textView) {
		InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public static void hideKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    //Find the currently focused view, so we can grab the correct window token from it.
	    View view = activity.getCurrentFocus();
	    //If no view currently has focus, create a new one, just so we can grab a window token from it
	    if(view == null) {
	        view = new View(activity);
	    }
	    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public static void hideKeyboardFrom(Context context, View view) {
	    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public static void showKeyboard(Context context, View view) {
	    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}
	
	public final static void displayToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	public final static void displayToastShort(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static Bitmap roundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
	            .getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pixels;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    
	    bitmap.recycle();

	    return output;
	}
	
	private static Bitmap convertToAlphaMask(Bitmap mask) {
		Bitmap output = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ALPHA_8);
	    Canvas canvas = new Canvas(output);
	    
	    canvas.drawBitmap(mask, 0.0f, 0.0f, null);
	    return output;
	}
	
	public static Bitmap imageMask(Context context, Bitmap image, int maskId) {
		Paint paint = new Paint();
		Canvas canvas = new Canvas(image);
		
		Bitmap mask = convertToAlphaMask(BitmapFactory.decodeResource(context.getResources(), maskId));
		
		Shader shader = new BitmapShader(mask, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		
		paint.setShader(shader);
		
		canvas.drawBitmap(mask, 0.0f, 0.0f, paint);
		
		return image;
	}
	
	public static boolean isNetworkAvailable(Context context) {
		if(context == null) {
			return false;
		}
		
		boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    
	    return haveConnectedWifi || haveConnectedMobile;
	}
}
