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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.ntlmtransport.NtlmTransport;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;

import android.util.Log;


public class GetSoapRequest {
	
	private static final String TAG = GetSoapRequest.class.getSimpleName();

	
	private NtlmTransport mNTLMTransport;
	private ServerConnection mConnection;

	public GetSoapRequest(ServerConnection connection) {
		mConnection = connection;
	}
	
	
	
	public SoapObject execute(Class callingClass, AbstractSoapRequest requestObject) {
		SoapObject request = requestObject.object();
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		
		mNTLMTransport = new NtlmTransport();
		
		mNTLMTransport.setCredentials(		mConnection.libraryURL(), 
											mConnection.userId(), 
											mConnection.password(), 
											mConnection.domain(), null);

		return doCall(callingClass, requestObject, envelope);
	}
	
	public SoapObject execute(Class callingClass, AbstractSoapRequest requestObject, boolean sync) {
		
		SoapObject request = requestObject.object();
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		
		mNTLMTransport = new NtlmTransport();
		
		if(sync) {
			mNTLMTransport.setCredentials(		mConnection.syncURL(), 
												mConnection.userId(), 
												mConnection.password(), 
												mConnection.domain(), null);
			
		} else {
			mNTLMTransport.setCredentials(		mConnection.libraryURL(), 
												mConnection.userId(), 
												mConnection.password(), 
												mConnection.domain(), null);
		}
		
		if(Settings.DEBUG_SOAP_MESSAGES) { mNTLMTransport.debug = true; }
		
		return doCall(callingClass, requestObject, envelope);
	}
	
	private SoapObject doCall(Class callingClass, AbstractSoapRequest requestObject, SoapSerializationEnvelope envelope) {

		SoapObject response = null;
		
		try {
			
			if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "call from " + callingClass.getSimpleName()); }
		
			mNTLMTransport.call(requestObject.getAction(), envelope);
			
			if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "after call from " + callingClass.getSimpleName()); }


			try{
                response = (SoapObject) envelope.getResponse();
            } catch (ClassCastException e) {
                response = (SoapObject) envelope.bodyIn; 
            }
			
			if(response == null) {
				response = (SoapObject) envelope.bodyIn; 
			}

			if(response == null) {
				return null;
			}
		} catch (NullPointerException e) {
			if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "NullPointerException Occured..."); }
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Unknown Exception Occured..."); }
			e.printStackTrace();
			return null;
		}
		
		return response;
	}
}
