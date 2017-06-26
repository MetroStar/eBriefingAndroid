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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.metrostarsystems.ebriefing.Data.Cache.Cache;

import android.content.Context;

public class FileWriteHandle extends AbstractFileHandle {

	private FileWriteType			mType			= FileWriteType.DATA;
	private FileLocation			mLocation		= FileLocation.NONE;
	private String					mDirectory		= "";
	
	public enum FileWriteType {
		DATA,
		DATA_APPEND,
		TEXT,
		TEXT_APPEND,
		PREFERENCE,
		JSON;
	}
	
	// Methods --------------------------------------------------------------------------------------
	public FileWriteType type() { return mType; }
	
	/**
	 * Return the location the data will be written
	 * @return the location the data will be written
	 */
	public FileLocation location() { return mLocation; }
	
	public String directory() { return mDirectory; }
	
	public String path() { return path(mLocation) + mDirectory; }
	
	public void setType(FileWriteType type) { mType = type; }
	public void setLocation(FileLocation location) { mLocation = location; } 
	public void setDirectory(String directory) { mDirectory = directory; }
	
	public void setData(Object data, FileWriteType type) { 
		setData(data);
		
		mType = type;
	}
	// ----------------------------------------------------------------------------------------------
	
	public void makeDirectory() throws IOException {
		if(!path().isEmpty()) {
			File destinationDirectory = new File(path());
			
			if (destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
                throw new IOException("Can't create directory, a file is in the way");
			}
		
			if(!destinationDirectory.exists()) {
				destinationDirectory.mkdirs();
				
				if (!destinationDirectory.isDirectory()) {
                    throw new IOException("Unable to create directory");
				}
			}
		}
	}
	
	public boolean valid() {
		return !mFileName.isEmpty() && mLocation != FileLocation.NONE && mData != null;
	}
	
	private File file(String fileName) {
		return new File(path(), fileName);
	}
	
	/**
	 * Returns a file where the data will be written
	 * @return the file
	 */
	public File file() { 
		return !mFileName.isEmpty() ? file(mFileName) : file(mLocation, mDirectory); 
	}
	
	public boolean doesFileExist() {
		return doesFileExist(file());
	}
	

	
	public boolean delete() {
		if(mLocation != FileLocation.NONE && doesFileExist()) {
			return super.delete(file());
		}
		
		return false;
	}
	
	public FileOutputStream outputStream(boolean makeDirectory) throws IOException, FileNotFoundException {
		if(makeDirectory) {
			makeDirectory();
		}
		
		return outputStream();
	}
	
	public FileOutputStream outputStream() throws FileNotFoundException {
		return new FileOutputStream(file(), false);
	}
	
	public FileOutputStream appendOutputStream(boolean makeDirectory) throws IOException, FileNotFoundException {
		if(makeDirectory) {
			makeDirectory();
		}
		
		return appendOutputStream();
	}
	
	public FileOutputStream appendOutputStream() throws FileNotFoundException {
		return new FileOutputStream(file(), true);
	}
	
	public OutputStreamWriter outputStreamWriter() throws FileNotFoundException {
		return new OutputStreamWriter(outputStream());
	}
	
	/**
	 * Write the data of the file handle to the cache
	 * @throws Exception
	 */
	public void write() throws Exception {
		switch(mType) {
			case DATA: Cache.writeFile(this); break;
			case TEXT: Cache.writeText(this); break;
//			case JSON: Cache.writeJSON(this); break;
			case DATA_APPEND: Cache.appendFile(this); break;
			case TEXT_APPEND: Cache.appendText(this); break;
			default: Cache.writeFile(this); break;
		}
	}
	
	public void writeStream(FileOutputStream stream, Object data) throws Exception {
		Cache.writeTextStream(stream, data);
	}
	
	
	/**
	 * Write the data to the cache
	 * @param data the data to be cached
	 * @throws Exception
	 */
	public void write(Object data) throws Exception {
		mData = data;
		
		switch(mType) {
			case DATA: Cache.writeFile(this); break;
			case TEXT: Cache.writeText(this); break;
//			case JSON: Cache.writeJSON(this); break;
			case DATA_APPEND: Cache.appendFile(this); break;
			case TEXT_APPEND: Cache.appendText(this); break;
			default: Cache.writeFile(this); break;
		}
	}
	
	
	// Constructors ----------------------------------------------------------------------------------
	
	protected FileWriteHandle(Builder build) {
		super(build);
		
		mType 		= build.mType;
		mLocation 	= build.mLocation;
		mDirectory 	= build.mDirectory;
	}
	// ------------------------------------------------------------------------------------------------
	
	// Builder ----------------------------------------------------------------------------------------
	public static class Builder extends AbstractFileHandle.Builder {
		
		private FileWriteType	mType			= FileWriteType.DATA;
		private FileLocation	mLocation		= FileLocation.NONE;
		private String			mDirectory		= "";
		
		public Builder(Context context) {
			super(context);
		}
		
		public Builder type(FileWriteType type) { mType = type; return this; }
		public Builder directory(String directory) { mDirectory = directory; return this; }

		@Override
		public Builder location(FileLocation location) { mLocation = location; return this; }

		@Override
		public FileWriteHandle build() {
			return new FileWriteHandle(this);
		}
	}
}
