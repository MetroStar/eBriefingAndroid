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

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Settings;

import android.os.AsyncTask;
import android.util.Log;

public abstract class AbstractSyncTask<T, R, S> extends AsyncTask<T, R, S> {
	
	private static final String TAG = AbstractSyncTask.class.getSimpleName();
	
	private		ServerConnection						mServerConnection;
	private 	Class<? extends AbstractSyncTask<T, R, S>> 		mExecutingClass;
	
	private boolean mFinished = false;

	protected 	SyncManager								mManager;
	private 	SyncTaskType 							mType;
//	private Timer			mTaskTimer;		
	
	protected long			mTime;
	
	public AbstractSyncTask(Class<? extends AbstractSyncTask<T, R, S>> executingClass, 
						SyncTaskType type, 
						ServerConnection serverConnection) {
		mExecutingClass		= executingClass;
		mType				= type;
		mServerConnection 	= serverConnection;
	}
	
	public void initialize(SyncManager manager) {
		mManager = manager;

	}
	
	public SyncTaskType type() { return mType; }
	public ServerConnection serverConnection() { return mServerConnection; }
	
	public boolean isFinished() { return mFinished || isCancelled() || getStatus() == AsyncTask.Status.FINISHED; }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		mTime = System.currentTimeMillis();
	}

	@Override
	protected S doInBackground(T... params) {
		return null;
	}
	
	
	

	@Override
	protected void onPostExecute(S result) {
		super.onPostExecute(result);
		
		if(mExecutingClass != null) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mExecutingClass.getSimpleName() + " Execute time = " + String.valueOf((System.currentTimeMillis() - mTime) / 1000) + " seconds"); }
		}
		
		mFinished = true;
	}

	protected abstract S executeRequest(AbstractSoapRequest request);


	public static enum SyncTaskType {
		TYPE_REFRESH_AVAILABLE_BOOKS,
		TYPE_SET_MY_BOOKS,
		TYPE_SET_MY_NOTE,
		TYPE_SET_MY_BOOKMARKS,
		TYPE_SET_MY_ANNOTATION,
		TYPE_GET_MY_BOOKS,
		TYPE_GET_MY_NOTES,
		TYPE_GET_MY_BOOKMARKS,
		TYPE_GET_MY_ANNOTATIONS,
		TYPE_REMOVE_MY_NOTE,
		TYPE_REMOVE_MY_ANNOTATION,
		TYPE_DELETE_MY_ANNOTATIONS,
		TYPE_DELETE_MY_STUFF,
		TYPE_SAVE_CACHE,
		TYPE_DELETE_BOOK,
		TYPE_REMOVE_MY_BOOK,
		TYPE_SET_MY_BOOK,
		TYPE_READ_CACHE,
		TYPE_SERVER_INFO,
		TYPE_MULTINOTES_GET_ALL_NOTES,
        TYPE_MULTINOTES_GET_NOTES_UPDATES,
        TYPE_MULTINOTES_SAVE_NOTES,
		
		TYPE_SYNC_BOOK,
        TYPE_SYNC_BOOK_AFTER_DOWNLOAD,
		TYPE_SYNC_SET_BOOK_EXTRAS;
	}
	
	
}
