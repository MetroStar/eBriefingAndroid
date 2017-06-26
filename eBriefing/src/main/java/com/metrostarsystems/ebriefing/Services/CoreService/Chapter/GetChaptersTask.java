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

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.CoreService.AbstractCoreTask;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreManager;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreService;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.CoreRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetCoreRequest;
import com.metrostarsystems.ebriefing.Services.Requests.SyncRequest;

public class GetChaptersTask extends AbstractCoreTask<Void, Void, GetChaptersObject> {
	
	private static final String TAG = GetChaptersTask.class.getSimpleName();
	
	private Book mBook;
	private GetChaptersTaskListener mListener;

	public GetChaptersTask(CoreManager manager, ServerConnection connection, Book book) {
		super(GetChaptersTask.class, CoreTaskType.TYPE_GET_CHAPTERS, connection);
		
		mBook = book;
		mListener = (GetChaptersTaskListener) manager;
	}
	
	@Override
	protected GetChaptersObject doInBackground(Void... params) {
		
		if(serverConnection() == null || mBook == null) {
			return new GetChaptersObject(false);
		}
		
		CoreRequest request = new CoreRequest(serverConnection(), ServerConnectionRequest.REQUEST_CORE_GET_CHAPTERS);
		
		request.addPropertyString(Tags.CORE_GET_CHAPTERS_REQUEST_ID, mBook.id());
		
		return executeRequest(request);
	}
	
	@Override
	protected void onPostExecute(GetChaptersObject result) {
		super.onPostExecute(result);
		
		mListener.OnGetChaptersTaskFinishedListener(mBook.id(), result);
	}

	@Override
	protected GetChaptersObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetChaptersObject(false);
		}
		
		SoapObject response = null;
		
		if(CoreService.isNetworkAvailable()) {
			if(Settings.DEBUG) { Log.i(TAG, "Start " + TAG + " response"); }
			
			try {
				response = new GetCoreRequest(serverConnection()).execute(GetChaptersTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(Settings.DEBUG) { Log.i(TAG, "Done " + TAG + " response"); }
			
			if(response == null) {
				return new GetChaptersObject(false);
			}
			
			return (GetChaptersObject) new GetChaptersObject.Builder().generate(serverConnection(), response).build();
		}
		
		return new GetChaptersObject(false);
	}

	public static interface GetChaptersTaskListener {
		public abstract void OnGetChaptersTaskFinishedListener(String bookId, GetChaptersObject result);
	}
}
