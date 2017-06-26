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

package com.metrostarsystems.ebriefing.BookPage.Page;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.radaee.util.PDFAESStream;

public class PDFObscuredStream extends PDFAESStream {

	private MainApplication mApp;
	
	public PDFObscuredStream(MainApplication app) {
		mApp = app;
	}

	
	@Override
	public boolean open(String path, byte[] key) {
		File file = new File(path);
		try
		{
			if( file.exists() && !file.isFile() ) return false;
			if( !file.exists() ) file.createNewFile();
			m_file = new RandomAccessFile( path, "rw" );
			m_enc_len = (int)m_file.length();
			if( m_enc_len > 0 )
			{
				m_file.seek(m_enc_len - 4);
				m_dec_len = m_file.readInt();
			}
			m_writeable = true;
		}
		catch( Exception e )
		{
			Log.e("o error", e.getMessage());
		}
		if( !m_writeable )
		{
			try
			{
				m_file = new RandomAccessFile( path, "r" );
				m_enc_len = (int)m_file.length();
				if( m_enc_len > 0 )
				{
					m_file.seek(m_enc_len - 4);
					m_dec_len = m_file.readInt();
				}
				m_writeable = false;
			}
			catch( Exception e )
			{
				Log.e("o error", e.getMessage());
				return false;
			}
		}
		try
		{
			
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			
			SecretKeySpec skey = new SecretKeySpec(key, "AES");  
			byte[] ivbytes = new byte[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15 };
			IvParameterSpec iv = new IvParameterSpec(ivbytes);//need IV in CBC mode

			m_dec_cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			m_dec_cipher.init(Cipher.DECRYPT_MODE, skey, iv);
			m_enc_cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			m_enc_cipher.init(Cipher.ENCRYPT_MODE, skey, iv);
		}
		catch(Exception e)
		{
			Log.e("o error", e.getMessage());
			return false;
		}
		if( m_enc_len == 0 )
			m_dec_len = 0;
		else
		{
			if( m_enc_len % BLOCK_ENC_SIZE != 4 )
			{
				try
				{
					m_file.close();
				}
				catch(Exception e)
				{
				}
				return false;
			}
			m_dec_pos = 0;
			dec_block();
		}
		return true;
	}

	
}
