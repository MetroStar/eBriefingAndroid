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

package com.metrostarsystems.ebriefing.Services.SyncService;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SyncHandler extends Handler {
	
	private static final String TAG = SyncHandler.class.getSimpleName();
	
	public static final int HANDLER_AUTO_SYNC 			= 0;
	
	private MainApplication mApp;
	
	public SyncHandler(MainApplication main) {
		mApp = main;
	}

	public void handleMessage(Message msg) {

		
		switch (msg.what) {
			case HANDLER_AUTO_SYNC:	
				if(SyncService.isScreenOn() && Settings.AUTO_SYNC && SyncService.canSync() && SyncService.allowAutoSync()) {
					if(mApp != null) {
						
						// Sync book data to server
                        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Auto Sync"); }
                        SyncService.syncServiceSetMyBooks();
						SyncService.syncServiceSyncBook();
					}
				}
				break;

			default:
				break;
		}
	}
	
	
}
