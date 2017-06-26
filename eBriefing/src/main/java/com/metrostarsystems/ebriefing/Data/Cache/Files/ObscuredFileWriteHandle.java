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

package com.metrostarsystems.ebriefing.Data.Cache.Files;

import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.metrostarsystems.ebriefing.Data.Cache.Cache;
import android.content.Context;
import android.provider.Settings.Secure;

public class ObscuredFileWriteHandle extends FileWriteHandle {
	
//	private SealedObject mSealedObject;
	
	protected static final String UTF8 = "UTF-8";
	private static char[] SEKRIT = null;
	

	
	
	@Override
	public void write() throws Exception {
		Object data = encrypt(mData);
		
		Cache.writeFile(this, data);
	}

	private Object encrypt(Object data) {
		Object encrypted_data = null;
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Secure.getString(context().getContentResolver(), 
            															  Secure.ANDROID_ID).getBytes(UTF8), 
            															  20));
 
            encrypted_data = new SealedObject((Serializable) data, pbeCipher);
            

        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
        
        return encrypted_data;
    }
	
	// Constructors ----------------------------------------------------------------------------------
		
	private ObscuredFileWriteHandle(Builder build) {
		super(build);
		
		SEKRIT = Secure.ANDROID_ID.toCharArray();
	}
	// ------------------------------------------------------------------------------------------------

	
	public static class Builder extends FileWriteHandle.Builder {
		
		public Builder(Context context) {
			super(context);
		}
		
		@Override
		public ObscuredFileWriteHandle build() {
			return new ObscuredFileWriteHandle(this);
		}
	}
}
