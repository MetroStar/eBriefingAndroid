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

import java.util.LinkedList;
import java.util.Queue;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksTask;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksTask.GetBooksTaskListener;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.GetChaptersObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.GetChaptersTask;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.GetChaptersTask.GetChaptersTaskListener;

public class CoreManager implements GetBooksTaskListener,
									GetChaptersTaskListener {
	
	private static final String TAG = CoreManager.class.getSimpleName();
	
	private MainApplication 					mApp;
	private CoreService							mService;
	private CoreManagerFinishedListener			mManagerFinishedListener;
	
	private CoreManagerStatus					mStatus;
	
	private Queue<AbstractCoreTask<?,?,?>> 		mQueue;
	
	private AbstractCoreTask<?,?,?>				mCurrentTask;
	
	
	private GetBooksObject						mGetBooksResults;
	private GetChaptersObject					mGetChaptersResults;
	
	public CoreManager(MainApplication main, CoreService service) {
		mApp = (MainApplication) main;
		mService = service;
		mManagerFinishedListener = (CoreManagerFinishedListener) service;
		
		mQueue = new LinkedList<AbstractCoreTask<?,?,?>>();
	}
	
	public CoreManagerStatus status() { return mStatus; }
	
	public void start(Intent intent) {
		mStatus = CoreManagerStatus.RUNNING;
		
		add(intent);

	}
	
	/**
	 * Add a task by an SyncServiceReciever intent
	 * @param intent the intent
	 */
	public void add(Intent intent) {
		if(intent == null || !intent.hasExtra("response")) {
			return;
		}
		
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		// Process the response code
		switch(intent.getIntExtra("response", -1)) {

			case CoreServiceReceiver.MSG_CORE_GET_BOOKS:				addGetBooksTask(); 				break;
			case CoreServiceReceiver.MSG_CORE_GET_CHAPTERS:				addGetChaptersTask(intent);		break;
		}
		
		if(mCurrentTask == null || mCurrentTask.isFinished()) {
			process();
		}
	}
	
	/**
	 * Adds a task to the queue unless its already in the queue
	 * @param task the task to be added
	 */
	private void addTask(AbstractCoreTask<?,?,?> task) {
		
		if(Settings.DEBUG_SOAP_MESSAGES) { Log.i(TAG, "Added task: " + task.getClass().getSimpleName()); }
		mQueue.add(task);

	}
	
// Books Tasks -----------------------------------------------------------------------------------------------
	
	
// RefreshAvailable Task -------------------------------------------------------------------------------------
	private void addGetBooksTask() {
		if(CoreService.allowAutoRefresh()) {
			GetBooksTask task = new GetBooksTask((GetBooksTaskListener) this, mApp.serverConnection());
			
			if(!mQueue.contains(task)) {
				addTask(task);
			}
		}
	}
	
	public GetBooksObject getGetBooksResults() {
		return mGetBooksResults;
	}
	
// -----------------------------------------------------------------------------------------------------------
	
	private void addGetChaptersTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String bookId = intent.getStringExtra("bookid");
			addGetChaptersTask(bookId);
		}
	}
	
	private void addGetChaptersTask(String bookId) {
		if(bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
		
		if(book == null) {
			return;
		}
		
		GetChaptersTask task = new GetChaptersTask(this, mApp.serverConnection(), book);
		
		addTask(task);
	}
	
	public GetChaptersObject getGetChaptersResults() {
		return mGetChaptersResults;
	}

	
	public void process() {

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Queue: " + String.valueOf(mQueue.size())); }
        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Processing"); }
		
		if(mCurrentTask != null && !mCurrentTask.isFinished()) {
			mCurrentTask = null;
		}

		if(!mQueue.isEmpty()) {
			mCurrentTask = mQueue.remove();
			mCurrentTask.initialize(this);

			if(Settings.DEBUG_MESSAGES ) { Log.i(TAG, "Executing " + mCurrentTask.getClass().getSimpleName()); }

			switch(mCurrentTask.type()) {

				case TYPE_GET_BOOKS:				((GetBooksTask) 	mCurrentTask).execute();	break;
				case TYPE_GET_CHAPTERS:				((GetChaptersTask)	mCurrentTask).execute();	break;
				default: break;
			}
		}
	
		if(mQueue.isEmpty()) {
			stop();
		}
	}
		
	public void stop() {
		mStatus = CoreManagerStatus.COMPLETED;
		
		if(!mQueue.isEmpty()) {
			mQueue.clear();
		}

		
		mManagerFinishedListener.OnCoreManagerFinished(this);
	}
	
// Finished Listeners ----------------------------------------------------------------------------------------
	@Override
	public void OnGetBooksTaskFinishedListener(GetBooksObject result) {
		
		if(result == null || !result.isValid()) {
			addGetBooksTask();
			
			process();
			
			return;
		}
		
		mGetBooksResults = result;
		broadcastGetBooksFinished();
		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mCurrentTask.getClass().getSimpleName() + " done"); }
		
		process();
	}
	
	private void broadcastGetBooksFinished() {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(CoreServiceReceiver.PROCESS_CORE_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", CoreServiceReceiver.MSG_CORE_GET_BOOKS);
        mService.sendBroadcast(broadcastIntent);
	}
	
	@Override
	public void OnGetChaptersTaskFinishedListener(String bookId, GetChaptersObject result) {
		
		if(result == null || !result.isValid()) {
			addGetChaptersTask(bookId);
			
			process();
			
			return;
		}
		
		mGetChaptersResults = result;
		broadcastGetChaptersFinished(bookId);
		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mCurrentTask.getClass().getSimpleName() + " done"); }
		
		process();
	}
	
	private void broadcastGetChaptersFinished(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(CoreServiceReceiver.PROCESS_CORE_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", CoreServiceReceiver.MSG_CORE_GET_CHAPTERS);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}

// Books Listeners -------------------------------------------------------------------------------------------
	
// -----------------------------------------------------------------------------------------------------------
	
// -----------------------------------------------------------------------------------------------------------
	
	public static enum CoreManagerStatus {
		RUNNING,
		IDLE,
		COMPLETED;
	}
	
	public interface CoreManagerFinishedListener {
		public abstract void OnCoreManagerFinished(CoreManager manager);
	}

	

	

	

	
	

	

}
