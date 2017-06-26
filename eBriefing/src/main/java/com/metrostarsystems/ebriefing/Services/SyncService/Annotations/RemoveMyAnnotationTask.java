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

package com.metrostarsystems.ebriefing.Services.SyncService.Annotations;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetSyncRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncTask;

public class RemoveMyAnnotationTask extends AbstractSyncTask<Void, Void, GetMyAnnotationsObject> {

	private static final String TAG = RemoveMyAnnotationTask.class.getSimpleName();
	
	private RemoveMyAnnotationTaskListener			mListener;
	private Book									mBook;
	private Annotation							mAnnotation;

	public RemoveMyAnnotationTask(SyncManager manager, ServerConnection connection, Book book, Annotation annotation) {
		super(RemoveMyAnnotationTask.class, SyncTaskType.TYPE_REMOVE_MY_ANNOTATION, connection);
		
		mListener = (RemoveMyAnnotationTaskListener) manager;
		mBook = book;
		mAnnotation = annotation;
	}
	
	@Override
	protected GetMyAnnotationsObject doInBackground(Void... params) {
		
		if(serverConnection() == null || mBook == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyAnnotationsObject(false);
		}

		RemoveMyAnnotationRequest request = new RemoveMyAnnotationRequest(serverConnection());
		
		request.addPropertyData(	mAnnotation.bookId(), 
									mAnnotation.pageId(), 
									mAnnotation.dateModified());	
		
		return executeRequest(request);
	}
	
	

	@Override
	protected void onPostExecute(GetMyAnnotationsObject result) {
		super.onPostExecute(result);
		
		mListener.onRemoveMyAnnotationTaskFinishedListener(result, mBook.id(), mAnnotation.pageNumber());
	}

	@Override
	protected GetMyAnnotationsObject executeRequest(AbstractSoapRequest request) {
		if(serverConnection() == null || request == null) {
			Log.i(TAG, TAG + " failed");
			return new GetMyAnnotationsObject(false);
		}
		
		if(SyncService.isNetworkAvailable()) {
			Log.i(TAG, "Start " + TAG + " response");
			
			SoapObject response = null;
			
			try {
				response = new GetSyncRequest(serverConnection()).execute(RemoveMyAnnotationTask.class, request);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			Log.i(TAG, "Done " + TAG + " response");
			
			if(response == null) {
				return new GetMyAnnotationsObject(false);
			}
			
			return new GetMyAnnotationsObject(true);
		}
		
		return new GetMyAnnotationsObject(false);
	}
	
	public static interface RemoveMyAnnotationTaskListener {
		public abstract void onRemoveMyAnnotationTaskFinishedListener(GetMyAnnotationsObject result, String bookId, int pageNumber);
	}
}
