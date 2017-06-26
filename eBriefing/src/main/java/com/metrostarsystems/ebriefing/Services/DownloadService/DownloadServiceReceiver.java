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

package com.metrostarsystems.ebriefing.Services.DownloadService;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadServiceReceiver extends BroadcastReceiver {
	
	// Service Messages
	public static final int MSG_DOWNLOAD_STARTED	= 0;
	public static final int MSG_DOWNLOAD_COMPLETE 	= 1;
	public static final int MSG_DOWNLOAD_CANCELLED	= 3;
	public static final int MSG_DOWNLOAD_PAUSED		= 4;
	public static final int MSG_DOWNLOAD_RESUMED	= 5;
	public static final int MSG_DOWNLOAD_UPDATED	= 6;
	
	// Manager Messages
	public static final int MSG_BOOK_DOWNLOADING_ACTIVE = 10;
	public static final int MSG_BOOK_DOWNLOADING_PENDING = 11;
	public static final int MSG_BOOK_DOWNLOADING_CANCELLED = 12;
	public static final int MSG_BOOK_DOWNLOADING_PAUSED = 13;
	public static final int MSG_BOOK_DOWNLOADING_COMPLETE = 14;
	public static final int MSG_BOOK_DOWNLOADING_UPDATE_COMPLETE = 15;
	
	
	
	public static final String PROCESS_RESPONSE = "com.metrostarsystems.intent.action.PROCESS_RESPONSE";
	 
	private DownloadService mService;
	private MainApplication mApp;
	
	public DownloadServiceReceiver(DownloadService service) {
		mService = service;
		mApp = service.app();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int response = intent.getIntExtra("response", 0);
		String book_id = intent.getStringExtra("bookid");
		
		Book book = mApp.data().database().booksDatabase().book(book_id);
		
		if(mApp != null && book != null) {
			switch(response) {
				case MSG_BOOK_DOWNLOADING_ACTIVE: 		DownloadService.downloadServiceBookDownloadActive(book); break;
				case MSG_BOOK_DOWNLOADING_PENDING:		DownloadService.downloadServiceBookDownloadPending(book); break;
				case MSG_BOOK_DOWNLOADING_CANCELLED:	DownloadService.downloadServiceBookDownloadCancelled(book); break;
				case MSG_BOOK_DOWNLOADING_PAUSED:		DownloadService.downloadServiceBookDownloadPaused(book); break;
				case MSG_BOOK_DOWNLOADING_COMPLETE: 	DownloadService.downloadServiceBookDownloadComplete(book); break;
				case MSG_BOOK_DOWNLOADING_UPDATE_COMPLETE: 	DownloadService.downloadServiceBookDownloadUpdateComplete(book); break;
			}
		}
	}

}
