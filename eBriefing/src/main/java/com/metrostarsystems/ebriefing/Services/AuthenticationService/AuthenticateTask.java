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

package com.metrostarsystems.ebriefing.Services.AuthenticationService;

import org.apache.http.HttpResponse;

import android.os.AsyncTask;

import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetHttpRequest;

public class AuthenticateTask extends AsyncTask<ServerConnection, HttpResponse, HttpResponse> {

	private AuthenticateTaskListener mListener;
	
	public AuthenticateTask(AuthenticateTaskListener listener) {
		mListener = listener;
	}

	@Override
	protected HttpResponse doInBackground(ServerConnection... params) {

		HttpResponse response = null;

		ServerConnection connection = params[0];

		if(Utilities.isNetworkAvailable(connection.app())) {
			response = new GetHttpRequest(connection).execute(connection.libraryURL());
		}
			
		return response;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		super.onPostExecute(result);
		
		mListener.onAuthenticateTaskFinished(result);
	}
	
	public static interface AuthenticateTaskListener {
		public abstract void onAuthenticateTaskFinished(HttpResponse result);
	}
	
	public static String responseCode(int code) {
		switch(code) {
			case 401: return "\nPlease check domain, username and/or password!";
			default: return "Unknown";
		}
	}
}
