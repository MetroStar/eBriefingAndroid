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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.metrostarsystems.ebriefing.Data.Cache.Files.AbstractFileHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileWriteHandle.FileWriteType;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public abstract class AbstractPreferencesHandle extends AbstractFileHandle {
	
	private HashMap<String, String> mData;
	
	private int 			mMode = Context.MODE_PRIVATE;
	private FileWriteType	mType = FileWriteType.PREFERENCE;
	private FileLocation	mLocation = FileLocation.SHARED_PREFERENCES;
	
	public abstract void write();
	
	public abstract void read();
	
	public int mode() { return mMode; }
	
	public void add(String key, String value) {
		if(mData == null) {
			mData = new HashMap<String, String>();
		}
		
		mData.put(key, value);
		
		write();
	}
	
	public void set(String key, Object value) {
		if(mData == null) {
			mData = new HashMap<String, String>();
		}
		
		mData.remove(key);

		mData.put(key, String.valueOf(value));
		
		write();
	}
	
	public Object get(String key, Object defaultValue) {
		if(key.isEmpty() || defaultValue == null) {
			return defaultValue;
		}
		
		Object object = mData.get(key);
		
		if(object == null) {
			return defaultValue;
		}
		
		return object;
	}
	
    public boolean contains(String key) {
    	if(key == null || key.isEmpty()) {
    		return false;
    	}
    	
        return mData.containsKey(key);
    }
	
	public Object remove(String key) {
		if(key == null || key.isEmpty()) {
			return null;
		}
		
		if(mData == null) {
			return null;
		}
		
		Object object = mData.remove(key);
		
		write();
		
		return object;
	}
	
	protected void writePreferences(AbstractPreferencesHandle handle) {
		SharedPreferences prefs = handle.context().getApplicationContext().getSharedPreferences(handle.fileName(), handle.mode());
		
		Editor editor = prefs.edit();
		
		editor.clear();
	
		Iterator<Entry<String, String>> iter = handle.data().entrySet().iterator();
		
		while(iter.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter.next();
			
			editor.putString(pairs.getKey(), pairs.getValue());
		}
		
		editor.commit();
	}
	
	protected void readPreferences(AbstractPreferencesHandle handle) {
		SharedPreferences prefs = handle.context().getApplicationContext().getSharedPreferences(handle.fileName(), handle.mode());
		
		Map<String, String> keys = (Map<String, String>) prefs.getAll();
		
		for(Map.Entry<String, String> entry : keys.entrySet()) {
			handle.add(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public HashMap<String, String> data() {
		return mData;
	}
	
	protected AbstractPreferencesHandle(Builder build) {
		super(build.mContext);
		
		mData = new HashMap<String, String>();
	}
	
	public abstract static class Builder  {
		
		protected Context mContext;
		protected String mFileName;
		
		public Builder(Context context, String filename) {
			mContext = context;
			mFileName = filename;
		}
		
		public abstract AbstractPreferencesHandle build();
		
		
	}

}
