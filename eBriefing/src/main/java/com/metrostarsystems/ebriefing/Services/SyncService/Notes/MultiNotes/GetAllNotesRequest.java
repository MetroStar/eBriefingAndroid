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

package com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes;

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Tags;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class GetAllNotesRequest extends AbstractSoapRequest {


	public GetAllNotesRequest(ServerConnection connection, String bookId, String startOffset, int pageSize) {
		super(connection, ServerConnectionRequest.REQUEST_MULTINOTES_GET_ALL_NOTES);

		addPropertyString(Tags.MULTINOTES_GET_ALL_NOTES_REQUEST_BOOK_ID, bookId);

        if(startOffset != null) {
            addPropertyString(Tags.MULTINOTES_GET_ALL_NOTES_REQUEST_START_OFFSET, startOffset);
        }

        addPropertyInt(Tags.MULTINOTES_GET_ALL_NOTES_REQUEST_PAGE_SIZE, pageSize);

	}
	
	@Override
	public void initialize(SoapSerializationEnvelope envelope) { }
	
}
