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

import android.os.AsyncTask;
import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask.SyncTaskType;

public abstract class AbstractCoreTask<T, R, S> extends AsyncTask<T, R, S> {
	
	private static final String TAG = AbstractCoreTask.class.getSimpleName();

	private		ServerConnection						mServerConnection;
	private 	Class<? extends AbstractCoreTask<T, R, S>> 		mExecutingClass;
	
	private boolean mFinished = false;
	
	protected 	CoreManager								mManager;
	private 	CoreTaskType 							mType;
	
	public AbstractCoreTask(Class<? extends AbstractCoreTask<T, R, S>> executingClass, 
			CoreTaskType type, 
			ServerConnection serverConnection) {
		mExecutingClass		= executingClass;
		mType				= type;
		mServerConnection 	= serverConnection;
	}
	
	public void initialize(CoreManager manager) {
		mManager = manager;
	}
	
	public CoreTaskType type() { return mType; }
	public ServerConnection serverConnection() { return mServerConnection; }
	
	public boolean isFinished() { return mFinished || isCancelled() || getStatus() == AsyncTask.Status.FINISHED; }
	
	@Override
	protected S doInBackground(T... params) {
		return null;
	}
	
	@Override
	protected void onPostExecute(S result) {
		super.onPostExecute(result);
		
		
	}

	protected abstract S executeRequest(AbstractSoapRequest request);
	
	
	public static enum CoreTaskType {
		TYPE_GET_BOOKS,
		TYPE_GET_CHAPTERS,
		TYPE_GET_PAGES,
		TYPE_REFRESH_AVAILABLE_BOOKS;
	}
	
}
