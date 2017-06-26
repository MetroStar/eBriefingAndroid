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

package com.metrostarsystems.ebriefing.Services.SyncService.Server;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject.ServerInfoType;


public class ServerInfoTag {
	
	private String mName;
	private ArrayList<ServerInfoValue> mValues;
	
	public String name() {
		return mName;
	}
	
	public String value(String tag) {
		for(int index = 0; index < mValues.size(); index++) {
			ServerInfoValue value = mValues.get(index);
			
			if(value.name().equalsIgnoreCase(tag)) {
				return value.value();
			}
		}
		
		return "";
	}
	
	private ServerInfoTag() {
		
	}
	
	private ServerInfoTag(Builder build) {
		mName = build.mTag.mName;
		mValues = build.mTag.mValues;
	}
	
	public static class Builder {
		
		private ServerInfoTag mTag;
		
		public Builder() {
			mTag = new ServerInfoTag();
		}
		
		public Builder name(String name) {
			mTag.mName = name;
			
			return this;
		}
		
		public Builder addTag(String name, String value) {
			if(mTag.mValues == null) {
				mTag.mValues = new ArrayList<ServerInfoValue>();
			}
			
			mTag.mValues.add(new ServerInfoValue.Builder().set(name, value).build());
			
			return this;
		}
		
		public ServerInfoTag build() {
			return new ServerInfoTag(this);
		}
	}
}
