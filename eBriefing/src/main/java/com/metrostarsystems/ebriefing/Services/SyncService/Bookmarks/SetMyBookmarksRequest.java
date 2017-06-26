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

package com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;

public class SetMyBookmarksRequest extends AbstractSoapRequest {
	
	private SoapObject mBookmarks;
	
	public SetMyBookmarksRequest(ServerConnection connection) {
		super(connection, ServerConnectionRequest.REQUEST_SYNC_SET_MY_BOOKMARKS);
		
		mBookmarks = new SoapObject(nameSpace(), Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_BOOKMARKS);
		
		addSoapObject(mBookmarks);
	}
	
	public void addData(String bookId, int version, String pageId, String value, String dateModified, boolean removed) {
		SoapObject bookObject = new SoapObject(nameSpace(), Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_BOOKMARK_OBJECT);
		
		addPropertyString(bookObject, Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_BOOK_ID, bookId);
		addPropertyInt(bookObject, Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_BOOK_VERSION, version);
		addPropertyString(bookObject, Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_PAGE_ID, pageId);
		addPropertyString(bookObject, Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_VALUE, value);
		addPropertyString(bookObject, Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_MODIFIED_DATE, dateModified);
		addPropertyBoolean(bookObject, Tags.SYNC_SET_MY_BOOKMARKS_REQUEST_REMOVED, removed);
		
		mBookmarks.addSoapObject(bookObject);
	}

	@Override
	public void initialize(SoapSerializationEnvelope envelope) {
		
	}

}
