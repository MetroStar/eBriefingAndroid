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

package com.metrostarsystems.ebriefing.Services.CoreService;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CoreHandler extends Handler {
	
	private static final String TAG = CoreHandler.class.getSimpleName();
	
	public static final int HANDLER_AUTO_REFRESH 		= 0;
	
	private MainApplication mApp;
	
	public CoreHandler(MainApplication main) {
		mApp = main;
	}

	public void handleMessage(Message msg) {

		
		switch (msg.what) {
			case HANDLER_AUTO_REFRESH: 
				
				if(Settings.AUTO_REFRESH && CoreService.allowAutoRefresh()) {
					
					if(mApp != null) {
						CoreService.coreServiceGetBooks();
					}
				}
			default:
				break;
		}
	}
	
	
}
