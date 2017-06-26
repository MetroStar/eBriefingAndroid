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
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetMultiNotesRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

/**
 * Created by jhyde on 2/18/2015.
 */
public class GetAllNotesTask extends AbstractSyncTask<Void, Void, GetAllNotesObject> {

    private static final String TAG = GetAllNotesTask.class.getSimpleName();

    private GetAllNotesTaskListener mListener;
    private Book mBook;

    public GetAllNotesTask(SyncManager manager, ServerConnection connection, Book book) {
        super(GetAllNotesTask.class, SyncTaskType.TYPE_MULTINOTES_GET_ALL_NOTES, connection);
        mListener = (GetAllNotesTaskListener) manager;


        mBook = book;
    }

    @Override
    protected GetAllNotesObject doInBackground(Void... params) {

        if(mBook == null) {
            Log.i(TAG, TAG + " failed");
            return null;
        }

        GetAllNotesRequest request = new GetAllNotesRequest(serverConnection(),
                mBook.id(),
                "0",
                10);




        return executeRequest(request);
    }


    @Override
    protected void onPostExecute(GetAllNotesObject result) {
        super.onPostExecute(result);

        mListener.onGetAllNotesTaskFinishedListener(result, mBook.id());
    }

    @Override
    protected GetAllNotesObject executeRequest(AbstractSoapRequest request) {
        if(serverConnection() == null || request == null) {
            Log.i(TAG, TAG + " failed");
            return new GetAllNotesObject(false);
        }

        if(SyncService.isNetworkAvailable()) {
            Log.i(TAG, "Start " + TAG + " response");

            SoapObject response = null;

            try {
                response = new GetMultiNotesRequest(serverConnection()).execute(GetAllNotesTask.class, request);
            } catch(Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Done " + TAG + " response");

            if(response == null) {
                new GetAllNotesObject(false);
            }

            return new GetAllNotesObject.Builder().generate(serverConnection(), response).build();
        }

        return new GetAllNotesObject(false);
    }

    public static interface GetAllNotesTaskListener {
        public abstract void onGetAllNotesTaskFinishedListener(GetAllNotesObject results, String bookId);
    }

}