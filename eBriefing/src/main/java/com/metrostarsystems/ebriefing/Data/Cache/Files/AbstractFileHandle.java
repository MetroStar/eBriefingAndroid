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

import java.io.File;

import android.content.Context;
import android.os.Environment;

public abstract class AbstractFileHandle {
	
	private Context				mContext			= null;
	protected String			mFileName			= "";
	protected Object			mData				= null;
	
	public enum FileLocation {
		NONE,
		INTERNAL,
		EXTERNAL,
		EXTERNAL_SD_CARD,
		INTERNAL_DATABASE,
		CACHE,
		URL,
		SHARED_PREFERENCES,
		ABSOLUTE;			// path must be set
	}
	
	// Methods --------------------------------------------------------------------------------------
	public Context context() { return mContext; }
	public String fileName() { return mFileName; }
	public Object data() { return mData; }
	public boolean hasFileName() { return !mFileName.isEmpty(); }
	
	//public void setContext(Context context) { mContext = context; }
	public void setFileName(String fileName) { mFileName = fileName; }
	public void setData(Object data) { mData = data; }
	// -----------------------------------------------------------------------------------------------
	
	protected String path(FileLocation location) {
		if(location == null) {
			return "";
		}
		
		return infoLocation(location).getAbsolutePath();
	}
	
	/**
	 * Returns information about files in location
	 * @param location the location
	 * @return the file containing the location information
	 */
	private File infoLocation(FileLocation location) {
		switch(location) {
			case INTERNAL: return mContext.getFilesDir();
			case EXTERNAL: return mContext.getExternalFilesDir(null);
			case EXTERNAL_SD_CARD: {
				if(hasSDCard()) {
					return Environment.getExternalStorageDirectory();
				} else {
					return mContext.getExternalFilesDir(null);
				}
			}
			case INTERNAL_DATABASE: {
				if(!mFileName.isEmpty()) {
					return mContext.getDatabasePath(mFileName);
				} else {
					return mContext.getFilesDir();
				}
			}
			case CACHE: return mContext.getCacheDir();
			default: return mContext.getFilesDir();
		}
	}
	
	public String[] files(FileLocation location) {
		String[] files = infoLocation(location).list();
		
		if(files == null) {
			return new String[] {};
		}
		
		return files;
	}
	
	protected File file(FileLocation location, String directory) {
		return new File(path(location) + directory);
	}
	

	
	private boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	
	
	protected boolean doesFileExist(File file) {
		return file.exists();
	}
	
	protected boolean delete(File file) {
		return file.delete();
	}
	
	// Constructors ----------------------------------------------------------------------------------
	public AbstractFileHandle(Context context) {
		mContext = context;
	}
	
	protected AbstractFileHandle(Builder build) {
		mContext 	= build.mContext;
		mFileName 	= build.mFileName;
		mData 		= build.mData;
	}
	// ------------------------------------------------------------------------------------------------
	
	// Builder ----------------------------------------------------------------------------------------
	public abstract static class Builder {
		
		private Context mContext;
		private String mFileName;
		private Object mData;
		
		public Builder(Context context) {
			mContext = context;
		}
		
		public abstract Builder location(FileLocation location);
		public abstract AbstractFileHandle build();
		
		public Builder fileName(String fileName) { mFileName = fileName; return this; }
		public Builder data(Object data) { mData = data; return this; }
	}
}
