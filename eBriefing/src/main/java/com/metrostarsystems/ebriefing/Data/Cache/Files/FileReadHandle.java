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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import com.metrostarsystems.ebriefing.Data.Cache.Cache;

import android.content.Context;

public class FileReadHandle extends AbstractFileHandle {

	private FileReadType		mType			= FileReadType.DATA;
	private FileLocation 		mLocation		= FileLocation.NONE;
	private String				mDirectory		= "";
	protected long				mExpireTime		= 0;
	
	public enum FileReadType {
		DATA,
		TEXT_ARRAY,
		TEXT_STRING;
	}
	
	// Methods --------------------------------------------------------------------------------------
	public FileReadType type() { return mType; }
	
	/**
	 * Return the location the data will be read
	 * @return the location the data will be read
	 */
	public FileLocation location() { return mLocation; }
	public String directory() { return mDirectory; }
	
	public String path() { 
		StringBuilder sb = new StringBuilder();
		
		sb.append(path(mLocation));
		sb.append(mDirectory);
		sb.append(File.separator);
		
		return sb.toString(); 
	}
	
	public long expireTime() { return mExpireTime; }
	public void setType(FileReadType type) { mType = type; }
	public void setLocation(FileLocation location) { mLocation = location; }
	public void setDirectory(String directory) { mDirectory = directory; }
	
	public void setData(Object data, FileReadType type) { 
		setData(data);
		
		mType = type;
	}
	
	public void setExpireTime(long time) { mExpireTime = time; }
	// ----------------------------------------------------------------------------------------------
	
	
	public boolean valid() {
		return !mFileName.isEmpty() && mLocation != FileLocation.NONE;
	}
	
	public boolean validExpire() {
		return valid() && mExpireTime != 0;
	}
	
	private File file(String fileName) {
		return new File(path(), fileName);
	}

	/**
	 * Returns a file where the data will be read
	 * @return the file
	 */
	public File file() { 
		return !mFileName.isEmpty() ? file(mFileName) : file(mLocation, mDirectory); 
	}
	
	public boolean doesFileExist() {
		if(mFileName.isEmpty()) {
			return false;
		}
		
		return doesFileExist(file());
	}
	
	public boolean delete() {
		if(mLocation != FileLocation.NONE && doesFileExist()) {
			return super.delete(file());
		}
		
		return false;
	}
	
	public boolean isExpired() {
		if(validExpire()) {
			return (Calendar.getInstance().getTimeInMillis() - file().lastModified() > expireTime()) ? true : false;
		} else {
			return false;
		}	
	}
	
	public FileInputStream inputStream() throws FileNotFoundException {
		return new FileInputStream(file());
	}
	
	public InputStreamReader inputStreamReader() throws FileNotFoundException {
		return new InputStreamReader(inputStream());
	}
	
	/**
	 * Reads the data and returns
	 * @return the data
	 * @throws Exception
	 */
	public Object read() throws Exception {
		switch(mType) {
			case DATA: return Cache.readFile(this);
			case TEXT_ARRAY: return Cache.readText(this, new ArrayList<String>());
			case TEXT_STRING: return Cache.readText(this, new String());
			default: return Cache.readFile(this);
		}
	}
	
	// Constructors ----------------------------------------------------------------------------------
	
	protected FileReadHandle(Builder build) {
		super(build);
		
		mType 		= build.mType;
		mLocation 	= build.mLocation;
		mDirectory 	= build.mDirectory;
	}
	// ------------------------------------------------------------------------------------------------
	
	// Builder ----------------------------------------------------------------------------------------
	public static class Builder extends AbstractFileHandle.Builder {
		
		private FileReadType		mType			= FileReadType.DATA;
		private FileLocation 		mLocation		= FileLocation.NONE;
		private String				mDirectory		= "";
		
		public Builder(Context context) {
			super(context);
		}
		
		public Builder type(FileReadType type) { mType = type; return this; }
		public Builder directory(String directory) { mDirectory = directory; return this; }

		@Override
		public Builder location(FileLocation location) { mLocation = location; return this; }
		
		@Override
		public FileReadHandle build() {
			return new FileReadHandle(this);
		}
	}
}
