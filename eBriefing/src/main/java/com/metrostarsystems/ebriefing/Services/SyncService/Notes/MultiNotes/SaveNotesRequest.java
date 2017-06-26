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

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class SaveNotesRequest extends AbstractSoapRequest {

    private SoapObject mNotes;

	public SaveNotesRequest(ServerConnection connection) {
		super(connection, ServerConnectionRequest.REQUEST_MULTINOTES_SAVE_NOTES);

        mNotes = new SoapObject(nameSpace(), Tags.MULTINOTES_SAVE_NOTES_REQUEST_NOTES);

        addSoapObject(mNotes);
	}

    public void addData(String noteId, String bookId, int bookVersion,
                            String pageId, String dateCreated, String dateModified,
                            String text, boolean removed) {
        SoapObject note_object = new SoapObject(nameSpace(), Tags.MULTINOTES_SAVE_NOTES_REQUEST_NOTE);

        addPropertyString(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_NOTE_ID, noteId);
        addPropertyString(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_BOOK_ID, bookId);
        addPropertyInt(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_BOOK_VERSION, bookVersion);
        addPropertyString(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_PAGE_ID, pageId);
        addPropertyString(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_DATE_CREATED, dateCreated);
        addPropertyString(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_DATE_MODIFIED, dateModified);
        addPropertyString(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_NOTE_TEXT, text);
        addPropertyBoolean(note_object, Tags.MULTINOTES_SAVE_NOTES_REQUEST_IS_DELETED, removed);

        mNotes.addSoapObject(note_object);
    }
	
	@Override
	public void initialize(SoapSerializationEnvelope envelope) { }
	
}
