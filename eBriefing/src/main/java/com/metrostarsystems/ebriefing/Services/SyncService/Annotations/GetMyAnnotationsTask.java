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

package com.metrostarsystems.ebriefing.Services.SyncService.Annotations;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.SyncRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class GetMyAnnotationsTask extends AbstractSyncTask<Void, Void, GetMyAnnotationsObject> {
	
	private static final String TAG = GetMyAnnotationsTask.class.getSimpleName();
	
	private GetMyAnnotationsTaskListener	mListener;
	private Book							mBook;
	
	public GetMyAnnotationsTask(SyncManager manager, ServerConnection connection, Book book) {
		super(GetMyAnnotationsTask.class, SyncTaskType.TYPE_GET_MY_ANNOTATIONS, connection);

		mListener = (GetMyAnnotationsTaskListener) manager;
		mBook = book;
	}
	
	@Override
	protected GetMyAnnotationsObject doInBackground(Void... params) {
		
		if(serverConnection() == null || mBook == null) {
			return new GetMyAnnotationsObject(false);
		}
		
		SyncRequest request = new SyncRequest(serverConnection(), 
				  							  ServerConnectionRequest.REQUEST_SYNC_GET_MY_ANNOTATIONS);
		
		request.addPropertyString(Tags.SYNC_GET_MY_ANNOTATIONS_REQUEST_BOOK_ID, mBook.id());
		request.addPropertyString(Tags.SYNC_GET_MY_ANNOTATIONS_REQUEST_PLATFORM, ServerConnection.PLATFORM_ANDROID);
		
		return executeRequest(request);
	}

	@Override
	protected void onPostExecute(GetMyAnnotationsObject result) {
		super.onPostExecute(result);
		
		mListener.onGetMyAnnotationsTaskFinishedListener(result, mBook.id());
	}

	@Override
	protected GetMyAnnotationsObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyAnnotationsObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			if(Settings.DEBUG) { Log.i(TAG, "Start " + TAG + " response"); }
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(GetMyAnnotationsTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(Settings.DEBUG) { Log.i(TAG, "Done " + TAG + " response"); }
			
			if(response == null) {
				return new GetMyAnnotationsObject(false);
			}
			
			return (GetMyAnnotationsObject) new GetMyAnnotationsObject.Builder().generate(serverConnection(), response).build();
		}
		
		return new GetMyAnnotationsObject(false);
	}
	
	public static interface GetMyAnnotationsTaskListener {
		public abstract void onGetMyAnnotationsTaskFinishedListener(GetMyAnnotationsObject result, String bookId);
	}

}
