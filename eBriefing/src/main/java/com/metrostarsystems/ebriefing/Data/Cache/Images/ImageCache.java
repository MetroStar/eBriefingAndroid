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

package com.metrostarsystems.ebriefing.Data.Cache.Images;

import java.io.FileInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

public class ImageCache {
	
	private BitmapCache mBitmapCache;
	
	public ImageCache() {
		 mBitmapCache = new BitmapCache();
	}
	
	/**
	 * Clears the cache
	 */
	public void clear() {
		mBitmapCache.clear();
	}


	/**
	 * Loads a bitmap from a file if the key is not in the cache
	 * @param key the key of the bitmap
	 * @param filepath the filepath of the bitmap
	 * @return the bitmap
	 */
	public Bitmap loadBitmap(String key, String filepath) {
		Bitmap bitmap = mBitmapCache.getBitmap(key);
		
		if(bitmap != null) {
			return bitmap;
		}
		
		// Bitmap is not in cache to load it from the file
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		
		try {			
			FileInputStream input_stream = new FileInputStream(filepath);
			
			bitmap = BitmapFactory.decodeStream(input_stream, null, options);
			
			// Add the bitmap to the cache after the bitmap is loaded
			if(bitmap != null) {
				addToCache(key, bitmap);
			}
			
			input_stream.close();
			input_stream = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	
	
	public Bitmap getBitmap(String id) {
		return mBitmapCache.getBitmap(id);
	}
	
	public void addToCache(String key, Bitmap bitmap) {
		mBitmapCache.addToCache(key, bitmap);
	}
	
	private class BitmapCache {

		private LruCache<String, Bitmap> mCache;
		
		public BitmapCache() {
			init();
		}
		
		public void init() {
			final int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
			
			final int cacheSize = maxMemory / 8;
			
			mCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getByteCount() / 1024;
				}
			};
		}
		
		public void addToCache(String key, Bitmap bitmap) {
			if(key != null && bitmap != null) {
				if(bitmap(key) == null) {
					mCache.put(key, bitmap);
				}
			}
		}
		
		private Bitmap bitmap(String key) {
			return mCache.get(key);
		}
		
		public Bitmap getBitmap(String id) {
			final Bitmap bitmap = bitmap(id);
			
			return bitmap;
		}
		
		public void clear() {
			init();
		}
	}
}
