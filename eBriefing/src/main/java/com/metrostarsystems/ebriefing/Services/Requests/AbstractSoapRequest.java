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

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;


public abstract class AbstractSoapRequest {

	private SoapObject 	mObject;
	private String 		mNameSpace = "";
	private String 		mMethod = "";
	
	public AbstractSoapRequest(ServerConnection connection, ServerConnectionRequest request) {
		mNameSpace = connection.nameSpace();
		mMethod = request.method();
		
		mObject = new SoapObject(mNameSpace, mMethod);
	}
	
	public abstract void initialize(SoapSerializationEnvelope envelope);
	
	public SoapObject object() {
		return mObject;
	}
	
	public String nameSpace() {
		return mNameSpace;
	}
	
	public String method() {
		return mMethod;
	}
	
	public String getAction() {
		if(!mMethod.isEmpty()) {
			return mNameSpace + "/" + mMethod;
		} else {
			return mNameSpace;
		}
	}
	
	public void addSoapObject(SoapObject soapObject) {
		mObject.addSoapObject(soapObject);
	}
	
	public void addPropertyString(String name, String value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(String.class);
		
		mObject.addProperty(p);
	}
	
	public void addPropertyString(SoapObject object, String name, String value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(String.class);
		
		object.addProperty(p);
	}
	
	public void addPropertyInt(String name, int value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Integer.class);
		
		mObject.addProperty(p);
	}
	
	public void addPropertyInt(SoapObject object, String name, int value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Integer.class);
		
		object.addProperty(p);
	}
	
	public void addPropertyBoolean(String name, boolean value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Boolean.class);
		
		mObject.addProperty(p);
	}
	
	public void addPropertyBoolean(SoapObject object, String name, boolean value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Boolean.class);
		
		object.addProperty(p);
	}
	
	public void addPropertyLong(String name, long value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Long.class);
		
		mObject.addProperty(p);
	}
	
	public void addPropertyLong(SoapObject object, String name, boolean value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Long.class);
		
		object.addProperty(p);
	}
	
	public void addPropertyEnum(String name, Enum value) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(Enum.class);
		
		mObject.addProperty(p);
	}
	
	public void addPropertyEnum(SoapObject object, String name, Enum value, Class enumClass) {
		PropertyInfo p = new PropertyInfo();
		
		p.setName(name);
		p.setValue(value);
		p.setType(enumClass);
		
		object.addProperty(p);
	}
	
	public static enum SoapRequestType {
		TYPE_CORE,
		TYPE_SYNC,
		TYPE_MULTINOTE;
	}
	
}
