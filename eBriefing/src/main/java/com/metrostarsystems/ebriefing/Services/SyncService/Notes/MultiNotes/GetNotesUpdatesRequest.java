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

import org.ksoap2.serialization.SoapSerializationEnvelope;

public class GetNotesUpdatesRequest extends AbstractSoapRequest {


	public GetNotesUpdatesRequest(ServerConnection connection, String bookId, String lastSyncedDate) {
		super(connection, ServerConnectionRequest.REQUEST_MULTINOTES_GET_NOTES_UPDATES);

		addPropertyString(Tags.MULTINOTES_GET_NOTES_UPDATES_REQUEST_BOOK_ID, bookId);
		addPropertyString(Tags.MULTINOTES_GET_NOTES_UPDATES_REQUEST_DATE_LAST_SYNCED, lastSyncedDate);
	}
	
	@Override
	public void initialize(SoapSerializationEnvelope envelope) { }
	
}
