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

package com.metrostarsystems.ebriefing.Data.Cache.Preferences;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

public class ObscuredPreferencesHandle extends AbstractPreferencesHandle {
	
	private static final String TAG = ObscuredPreferencesHandle.class.getSimpleName();
	
	protected static final String UTF8 = "UTF-8";
	private static char[] SEKRIT = null;
	
	@Override
	public Object get(String key, Object defaultValue) {
		String object = (String) data().get(key);
		
		if(object == null || object.isEmpty()) {
			return defaultValue;
		}
		
		try {
			if(defaultValue instanceof String) {
				return object;
			} else if(defaultValue instanceof Integer) {
				return Integer.parseInt(object);
			} else if(defaultValue instanceof Long) {
				return Long.parseLong(object);
			} else if(defaultValue instanceof Boolean) {
				return Boolean.parseBoolean(object);
			} else if(defaultValue instanceof Float) {
				return Float.parseFloat(object);
			}
		} catch(NumberFormatException e) {
			Log.e(TAG, "Warning, possible incorrect key." + e.getMessage());
		}
		
		return defaultValue;
	}
	
	/**
	 * Writes the preferences
	 */
	public void write() {
		if(data().isEmpty()) {
			return;
		}
		
		writePreferences(this);
	}
	
	/**
	 * Reads the preferences
	 */
	public void read() {
		readPreferences(this);
	}
	
	@Override
	protected void writePreferences(AbstractPreferencesHandle handle) {
		SharedPreferences prefs = handle.context().getApplicationContext().getSharedPreferences(handle.fileName(), handle.mode());
		
		Editor editor = prefs.edit();
		
		editor.clear();
	
		Iterator<Entry<String, String>> iter = handle.data().entrySet().iterator();
		
		while(iter.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter.next();
			
			editor.putString(pairs.getKey(), encrypt(pairs.getValue()));
		}
		
		editor.commit();
	}
	
	@Override
	protected void readPreferences(AbstractPreferencesHandle handle) {
		SharedPreferences prefs = handle.context().getApplicationContext().getSharedPreferences(handle.fileName(), handle.mode());
		
		Map<String, ?> keys = prefs.getAll();
		
		for(Map.Entry<String, ?> entry : keys.entrySet()) {
			handle.add(entry.getKey(), decrypt((String) entry.getValue()));
		}
	}
	
	/**
	 * Encrypts the string value
	 * @param value the value to be encrypted
	 * @return the encrypted value
	 */
	private String encrypt(String value) {
        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Secure.getString(context().getContentResolver(), 
            															  Secure.ANDROID_ID).getBytes(UTF8), 
            															  20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP), UTF8);

        } catch (Exception e ) {
            throw new RuntimeException(e);
        }

    }

	/**
	 * Decrypts the string value
	 * @param value the value to be decrypted
	 * @return the decrypted value
	 */
    private String decrypt(String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value, Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Secure.getString(context().getContentResolver(), 
            															  Secure.ANDROID_ID).getBytes(UTF8), 
            															  20));
            return new String(pbeCipher.doFinal(bytes), UTF8);

        } catch (Exception e) {
            return value;
        }
    }
	
    /**
     * Private builder constructor
     * @param build the builder
     */
	private ObscuredPreferencesHandle(Builder build) {
		super(build);
		
		SEKRIT = Secure.ANDROID_ID.toCharArray();
	}
	
	public static class Builder extends AbstractPreferencesHandle.Builder {

		public Builder(Context context, String filename) {
			super(context, filename);
		}

		@Override
		public ObscuredPreferencesHandle build() {
			return new ObscuredPreferencesHandle(this);
		}
	}
	
}
