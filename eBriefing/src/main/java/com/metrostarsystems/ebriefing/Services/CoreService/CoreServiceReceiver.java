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
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CoreServiceReceiver extends BroadcastReceiver {
	
	// Service Messages

	public static final int MSG_CORE_COMPLETE 				= 0;
	public static final int MSG_CORE_GET_BOOKS				= 1;
	public static final int MSG_CORE_GET_CHAPTERS			= 2;

	
	public static final String PROCESS_CORE_RESPONSE = "com.metrostarsystems.intent.action.PROCESS_CORE_RESPONSE";
	 
	private CoreService			mService;
	private MainApplication 	mApp;
	
	public CoreServiceReceiver(CoreService service) {
		mService = service;
		mApp = service.app();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int response = intent.getIntExtra("response", -1);
		
		if(mApp != null && mService != null) {
			
			switch(response) {
				
				case MSG_CORE_GET_BOOKS:
					CoreService.processGetBooksTaskResponses().processGetBooksResponse(mService.getGetBooksResults());
					break;
				case MSG_CORE_GET_CHAPTERS:
					CoreService.processGetChaptersTaskResponses().processGetChaptersResponse(mService.getGetChaptersResults());
			}
		}
	}

}