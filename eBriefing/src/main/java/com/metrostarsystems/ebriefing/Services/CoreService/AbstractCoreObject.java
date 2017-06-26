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

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;


public abstract class AbstractCoreObject<T> {

	protected ArrayList<T>	mObjects = null;
	private boolean			mValid = false;
	
	public AbstractCoreObject() {
		
	}
	
	public AbstractCoreObject(boolean valid) {
		mValid = valid;
	}
	
	public ArrayList<T> objects() { return mObjects; }
	
	public void setValid(boolean valid) { mValid = valid; }
	public boolean isValid() { return mObjects != null || mValid; }
	
	public abstract static class Builder<T> {
		protected ArrayList<T>	mObjects;
		
		public Builder() { }
		
		public ArrayList<T> objects() { return mObjects; }
		
		public Builder<T> generate(ServerConnection connection, SoapObject object) {
			if(object == null) {
				return this;
			}
			
			if(mObjects == null) {
				mObjects = new ArrayList<T>();
			}
			
			int count = object.getPropertyCount();
			
			if(count > 0) {
				for(int index = 0; index < count; index++) {
					add(connection, (SoapObject) object.getProperty(index));			 
				}
			}
			
			return this;
		}
		
		protected abstract void add(ServerConnection connection, SoapObject object);
		
		public abstract AbstractCoreObject<T> build();

	}
	
}
