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

package com.metrostarsystems.ebriefing.Services.SyncService.Server;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.CoreRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetServerRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetServerRequest.ServerType;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Settings;

public class ServerInfo2013Task extends AbstractSyncTask<Void, Void, ServerInfoObject2> {
	
	private static final String TAG = ServerInfo2013Task.class.getSimpleName();
	
	private ServerInfo2013TaskListener		mListener;
	
	public ServerInfo2013Task(ServerInfo2013TaskListener listener, ServerConnection connection) {
		super(ServerInfo2013Task.class, SyncTaskType.TYPE_SERVER_INFO, connection);
		
		mListener = listener;
	}
	
	@Override
	protected ServerInfoObject2 doInBackground(Void... params) {
		
		return executeRequest(new CoreRequest(serverConnection(), 
											  ServerConnectionRequest.REQUEST_GET_SERVER_INFO));
	}
	
	

	@Override
	protected void onPostExecute(ServerInfoObject2 result) {
		super.onPostExecute(result);
		
		mListener.onServerInfo2013TaskFinishedListener(result);
	}

	@Override
	protected ServerInfoObject2 executeRequest(AbstractSoapRequest request) {
		SoapObject response = null;
		
		if(request == null) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, TAG + " failed"); }
			return new ServerInfoObject2(false);
		}
		
		if(SyncService.isNetworkAvailable()) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Start " + TAG + " response"); }
			
			try {
				response = new GetServerRequest(serverConnection(), ServerType.TYPE_SERVER_2013).execute(ServerInfo2013Task.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Done " + TAG + " response"); }
			
			if(response == null) {
				return new ServerInfoObject2(false);
			}
			
			if(response != null) {
				return new ServerInfoObject2(response);
			}
		}
		
		return new ServerInfoObject2(false);
	}
	
	public static interface ServerInfo2013TaskListener {
		public abstract void onServerInfo2013TaskFinishedListener(ServerInfoObject2 result);
	}
}
