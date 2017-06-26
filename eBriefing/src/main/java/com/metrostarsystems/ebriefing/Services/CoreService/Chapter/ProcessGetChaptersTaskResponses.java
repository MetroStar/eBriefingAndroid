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

package com.metrostarsystems.ebriefing.Services.CoreService.Chapter;

import java.util.ArrayList;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject.ServerBookObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.GetChaptersObject.ChapterObject;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

public class ProcessGetChaptersTaskResponses {

	private static final String TAG = ProcessGetChaptersTaskResponses.class.getSimpleName();
	
	private MainApplication mApp;
	
	public ProcessGetChaptersTaskResponses(MainApplication app) {
		mApp = app;
	}
	
	public void processGetChaptersResponse(GetChaptersObject object) {
		if(mApp == null || object == null) {
			return;
		}
		
		processGetChapters(object);
	}
	
	private void processGetChapters(GetChaptersObject object) {
		if(mApp == null || object == null || !object.isValid()) {
			return;
		}
		
		ArrayList<ChapterObject> chapter_objects = object.objects();
		
		if(chapter_objects == null) {
			return;
		}
		
		Log.i(TAG, "Processing: " + String.valueOf(chapter_objects.size()) + " chapters...");

	}
}
