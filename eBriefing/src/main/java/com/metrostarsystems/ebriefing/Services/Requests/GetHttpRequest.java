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

package com.metrostarsystems.ebriefing.Services.Requests;

import java.net.URI;

import javax.net.ssl.SSLException;

import net.maxters.android.ntlm.NTLM;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;

public class GetHttpRequest {

	private DefaultHttpClient 	mHttpClient;
	private ServerConnection 	mConnection;
	
	public GetHttpRequest(ServerConnection connection) {
		mConnection = connection;
		
		HttpParams params = new BasicHttpParams();
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
		
		
		
		mHttpClient = new DefaultHttpClient(manager, params);
		
		NTLM.setNTLM(mHttpClient, 	mConnection.userId(), 
									mConnection.password(), 
									mConnection.domain());
	}
	
	public HttpResponse execute(String url) {
		
		URI uri = null;
		
		try {
			uri = new URI(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		HttpGet http_get = new HttpGet(uri);

		HttpResponse response = null;
		
		try {
			response = mHttpClient.execute(http_get);
		} catch (SSLException e) {
			System.out.println("SSLException Occured...");
			e.printStackTrace();
			
			http_get.abort();
		} catch(Exception e) {
			e.printStackTrace();
			
			http_get.abort();
		} 

		return response;
	}
	
	
}
