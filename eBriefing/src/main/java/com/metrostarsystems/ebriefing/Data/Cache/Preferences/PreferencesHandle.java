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

import android.content.Context;

public class PreferencesHandle extends AbstractPreferencesHandle {
	
	public void write() {
		if(data().isEmpty()) {
			return;
		}
		
		writePreferences(this);
	}
	
	public void read() {
		readPreferences(this);
	}
	
	private PreferencesHandle(Builder build) {
		super(build);
	}
	
	public static class Builder extends AbstractPreferencesHandle.Builder {

		public Builder(Context context, String filename) {
			super(context, filename);
			
		}

		@Override
		public PreferencesHandle build() {
			return new PreferencesHandle(this);
		}
		
	}
}
