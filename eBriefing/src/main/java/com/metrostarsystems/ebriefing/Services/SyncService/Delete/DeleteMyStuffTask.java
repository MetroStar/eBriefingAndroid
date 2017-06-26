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

package com.metrostarsystems.ebriefing.Services.SyncService.Delete;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksObject;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.SyncRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

public class DeleteMyStuffTask extends AbstractSyncTask<Void, Void, GetMyBooksObject> {
	
	private static final String TAG = DeleteMyStuffTask.class.getSimpleName();
	
	private DeleteMyStuffTaskListener		mListener;

	public DeleteMyStuffTask(DeleteMyStuffTaskListener listener, ServerConnection connection) {
		super(DeleteMyStuffTask.class, SyncTaskType.TYPE_DELETE_MY_STUFF, connection);
		
		mListener = listener;
	}
	
	@Override
	protected GetMyBooksObject doInBackground(Void... params) {
		
		if(serverConnection() == null) {
			return new GetMyBooksObject(false);
		}
		
		return executeRequest(new SyncRequest(serverConnection(), 
											  ServerConnectionRequest.REQUEST_SYNC_DELETE_MY_STUFF));
	}
	
	

	@Override
	protected void onPostExecute(GetMyBooksObject result) {
		super.onPostExecute(result);
		
		mListener.OnDeleteMyStuffTaskFinishedListener(result);
	}

	@Override
	protected GetMyBooksObject executeRequest(AbstractSoapRequest request) {
		
		if(request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyBooksObject(false);
		}
		
		SoapObject response = null;
		
		if(SyncService.isNetworkAvailable()) {
			if(Settings.DEBUG) { Log.i(TAG, "Start " + TAG + " response"); }
			
			try {
				response = new GetSoapRequest(serverConnection()).execute(DeleteMyStuffTask.class, request, true);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(Settings.DEBUG) { Log.i(TAG, "Done " + TAG + " response"); }
			
			if(response == null) {
				return new GetMyBooksObject(false);
			}
			
			return new GetMyBooksObject(true);
		}
		
		return new GetMyBooksObject(false);
	}
	
	public static interface DeleteMyStuffTaskListener {
		public abstract void OnDeleteMyStuffTaskFinishedListener(GetMyBooksObject result);
	}
}
