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

import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetMultiNotesRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Settings;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by jhyde on 2/18/2015.
 */
public class GetNotesUpdatesTask extends AbstractSyncTask<Void, Void, GetNotesUpdatesObject> {

    private static final String TAG = GetNotesUpdatesTask.class.getSimpleName();

    private GetNotesUpdatesTaskListener mListener;
    private Book mBook;

    public GetNotesUpdatesTask(SyncManager manager, ServerConnection connection, Book book) {
        super(GetNotesUpdatesTask.class, SyncTaskType.TYPE_MULTINOTES_GET_NOTES_UPDATES, connection);
        mListener = (GetNotesUpdatesTaskListener) manager;
        mBook = book;
    }

    @Override
    protected GetNotesUpdatesObject doInBackground(Void... params) {

        if(mBook == null) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, TAG + " failed"); }
            return null;
        }

        GetNotesUpdatesRequest request = new GetNotesUpdatesRequest(serverConnection(),
                mBook.id(),
                mBook.dateSynced());




        return executeRequest(request);
    }


    @Override
    protected void onPostExecute(GetNotesUpdatesObject result) {
        super.onPostExecute(result);

        mListener.onGetNotesUpdatesTaskFinishedListener(result, mBook.id());
    }

    @Override
    protected GetNotesUpdatesObject executeRequest(AbstractSoapRequest request) {
        if(serverConnection() == null || request == null) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, TAG + " failed"); }
            return new GetNotesUpdatesObject(false);
        }

        if(SyncService.isNetworkAvailable()) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Start " + TAG + " response"); }

            SoapObject response = null;

            try {
                response = new GetMultiNotesRequest(serverConnection()).execute(GetNotesUpdatesTask.class, request);
            } catch(Exception e) {
                e.printStackTrace();
            }

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Done " + TAG + " response"); }

            if(response == null) {
                new GetAllNotesObject(false);
            }

            return new GetNotesUpdatesObject.Builder().generate(serverConnection(), response).build();
        }

        return new GetNotesUpdatesObject(false);
    }

    public static interface GetNotesUpdatesTaskListener {
        public abstract void onGetNotesUpdatesTaskFinishedListener(GetNotesUpdatesObject results, String bookId);
    }

}