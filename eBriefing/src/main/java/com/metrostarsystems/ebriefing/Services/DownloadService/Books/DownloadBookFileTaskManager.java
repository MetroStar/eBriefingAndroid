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

package com.metrostarsystems.ebriefing.Services.DownloadService.Books;

import java.util.ArrayList;
import java.util.Stack;

import android.os.AsyncTask;

public class DownloadBookFileTaskManager {
	
	private static final String TAG = DownloadBookFileTaskManager.class.getSimpleName();

	private ArrayList<AbstractDownloadBookFileTask>	mTasks;
	
	public DownloadBookFileTaskManager() {
		mTasks = new ArrayList<AbstractDownloadBookFileTask>();
	}
	
	/**
	 * Adds the file download task and starts the download
	 * @param context the context
	 * @param parent the parent of
	 * @param downloadObject the downloadObject file
	 */
	public void add(AbstractDownloadBookFileTask task) {
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		mTasks.add(task);
	}
	
	public boolean hasAvailableTasks() {
		if(mTasks == null) {
			return false;
		}
		
		return mTasks.size() < DownloadBookFileManager.MAX_DOWNLOADS;
	}
	
	public boolean isFinished() {
		return isEmpty();
	}
	
	public boolean isEmpty() {
		return mTasks.size() == 0;
	}
	
	public void remove(AbstractDownloadBookFileTask task) {
		mTasks.remove(task);
	}
	
	public void cancel() {
		for(int index = 0; index < mTasks.size(); index++) {
			AbstractDownloadBookFileTask task = mTasks.get(index);
			
			task.cancel(true);
			task = null;
		}
		
		mTasks.clear();
	}
	
	public void pause(Stack<DownloadBookFile> files) {
		for(int index = 0; index < mTasks.size(); index++) {
			AbstractDownloadBookFileTask task = mTasks.get(index);
			
			if(task != null) {
				DownloadBookFile file = task.file();
				files.push(file);
				task.cancel(true);
				task = null;
			}
		}
		
		mTasks.clear();
	}
}
