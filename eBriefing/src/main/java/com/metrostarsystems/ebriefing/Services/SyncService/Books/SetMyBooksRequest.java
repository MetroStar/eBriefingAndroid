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

package com.metrostarsystems.ebriefing.Services.SyncService.Books;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;

public class SetMyBooksRequest extends AbstractSoapRequest {
	
	private SoapObject mBooks;
	
	public SetMyBooksRequest(ServerConnection connection) {
		super(connection, ServerConnectionRequest.REQUEST_SYNC_SET_MY_BOOKS);
		
		mBooks = new SoapObject(nameSpace(), "books");
		
		addSoapObject(mBooks);
	}
	
	public void addBookData(String id, int version, boolean favorite, String dateModified, boolean removed) {
		SoapObject bookObject = new SoapObject(nameSpace(), "MyBookObj");
		
		addPropertyString(bookObject, "BookId", id);
		addPropertyInt(bookObject, "BookVersion", version);
		addPropertyBoolean(bookObject, "IsFavorite", favorite);
		addPropertyString(bookObject, "ModifiedUtc", dateModified);
		addPropertyBoolean(bookObject, "Removed", removed);
		
		mBooks.addSoapObject(bookObject);
	}

	@Override
	public void initialize(SoapSerializationEnvelope envelope) {
		
	}

}
