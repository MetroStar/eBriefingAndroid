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

import java.util.UUID;

public class ServerFeature {

	private String 	mId = "";
	private String 	mName = "";
	private int		mVersion = 0;
	private String	mRelativeUrl = "";
	
	private ServerFeature(Builder build) {
		mId 			= build.mId;
		mName 			= build.mName;
		mVersion 		= build.mVersion;
		mRelativeUrl 	= build.mRelativeUrl;
	}
	
	public String id() { return mId; }
	public String name() { return mName; }
	public int version() { return mVersion; }
	public String url() { return mRelativeUrl; }
	
	public static class Builder {
		private String 	mId = "";
		private String 	mName = "";
		private int		mVersion = 0;
		private String	mRelativeUrl = "";
		
		public Builder() {
			mId = UUID.randomUUID().toString();
		}
		
		public Builder name(String name) { mName = name; return this; }
		public Builder version(int version) { mVersion = version; return this; }
		public Builder url(String url) { mRelativeUrl = url; return this; }
		
		public ServerFeature build() {
			return new ServerFeature(this);
		}
	}
}
