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

import java.util.ArrayList;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsObject.AnnotationObject;

public class ProcessAnnotationTaskResponses {
	
	private static final String TAG = ProcessAnnotationTaskResponses.class.getSimpleName();
	
	private MainApplication mApp;
	private String			mBookId = "";
	private Book			mBook;
	
	public ProcessAnnotationTaskResponses(MainApplication app) {
		mApp = app;
	}
	
	public void processSetMyAnnotationResponse(boolean result, String bookId, int pageNumber) {
		if(mApp == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		processSetMyAnnotation(result, pageNumber);
	}
	
	public void processGetMyAnnotationsResponse(String bookId, GetMyAnnotationsObject annotations) {
		if(mApp == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		processGetMyAnnotations(annotations);
	}
	
	public void processRemoveMyAnnotationResponse(boolean result, String bookId, int pageNumber) {
		if(mApp == null || bookId.isEmpty()) {
			return;
		}
		
		mBookId = bookId;
		
		processRemoveMyAnnotation(result, pageNumber);
	}

	private void processSetMyAnnotation(boolean result, int pageNumber) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		if(mApp == null || mBook == null) {
			return;
		}
		
		if(result) {
			
			Annotation annotation = mApp.data().database()
											.annotationsDatabase().annotation(mBook.id(), pageNumber);
			
			Annotation update_annotation = new Annotation.Builder()
													.fromAnnotation(annotation)
													.inkAnnotation(annotation.width(), annotation.height(), annotation.inkAnnotation())
													.isSynced(true)
													.isNew(false)
													.build();
			
			mApp.data().database().annotationsDatabase().update(update_annotation);
		}
	}
	
	private void processGetMyAnnotations(GetMyAnnotationsObject annotations_objects) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		// annotations can be empty
		if(mApp == null || annotations_objects == null || !annotations_objects.isValid() ||
				 mBook == null) {
			mBook.setSyncedAnnotations(true);
			mApp.data().database().booksDatabase().update(mBook);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " +
									 mBook.isSyncedBookmarks() + " " +
									 mBook.isSyncedAnnotations()); }
			
			if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(mBook);
			}
			
			return;
		}
		
		ArrayList<AnnotationObject> annotation_objects = annotations_objects.objects();
		
		if(annotation_objects == null || annotation_objects.size() <= 0) {
			mBook.setSyncedAnnotations(true);
			
			mApp.data().database().booksDatabase().update(mBook);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " +
									 mBook.isSyncedBookmarks() + " " +
									 mBook.isSyncedAnnotations()); }
			
			if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
				SyncService.syncServiceBookComplete(mBook);
			}
			
			return;
		}
		
		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mBook.title() + " Found " + String.valueOf(annotation_objects.size()) + " annotations"); }
		
		ArrayList<Annotation> annotations = mApp.data().database().annotationsDatabase().annotationsByBook(mBook.id());
		
		
		
		
		for(AnnotationObject annotation_object : annotation_objects) {
			boolean found = false;
			
			for(Annotation annotation : annotations) {
				
				// Annotation exists
				if(annotation.pageNumber() == annotation_object.mPageNumber) {
					found = true;
					
					if(annotation_object.mRemoved) {
						mApp.data().database().annotationsDatabase().deleteByNumber(annotation);
						
						break;
					}
					
					// Server Annotation is newer so update the local annotation
					if(!annotation.isNewer(annotation_object.mDateModified)) {
						Annotation updated_annotation = new Annotation.Builder()
															.id()
															.bookId(annotation_object.mBookId)
															.bookVersion(annotation_object.mBookVersion)
															.chapterId(annotation_object.mChapterId)
															.pageId(annotation_object.mPageId)
															.pageNumber(annotation_object.mPageNumber)
															.platform(annotation_object.mPlatform)
															.textDataUrl(annotation_object.mTextDataUrl)
															.imageDataUrl(annotation_object.mImageDataUrl)
															.dateModified(annotation_object.mDateModified)
															.isSynced(true)
															.isNew(false)
															.textData(annotation_object.mContent)
															.build();
							
						mApp.data().database().annotationsDatabase().update(updated_annotation);
						
						break;
					}
				}
			}
			
			// If it gets this far then the Annotation doesn't exist
			if(!found && !annotation_object.mRemoved) {

				Annotation new_annotation = new Annotation.Builder()
															.id()
															.bookId(annotation_object.mBookId)
															.bookVersion(annotation_object.mBookVersion)
															.chapterId(annotation_object.mChapterId)
															.pageId(annotation_object.mPageId)
															.pageNumber(annotation_object.mPageNumber)
															.platform(annotation_object.mPlatform)
															.textDataUrl(annotation_object.mTextDataUrl)
															.imageDataUrl(annotation_object.mImageDataUrl)
															.dateModified(annotation_object.mDateModified)
															.isSynced(true)
															.isNew(false)
															.textData(annotation_object.mContent)
															.build();
											
				mApp.data().database().annotationsDatabase().insert(new_annotation);
			}
		}
		
		mBook.setSyncedAnnotations(true);
		mApp.data().database().booksDatabase().update(mBook);

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + mBook.isSyncedNotes() + " " +
								 mBook.isSyncedBookmarks() + " " +
								 mBook.isSyncedAnnotations()); }
		
		if(mBook.isSyncedNotes() && mBook.isSyncedBookmarks() && mBook.isSyncedAnnotations()) {
			SyncService.syncServiceBookComplete(mBook);
		}

	}
	
	private void processRemoveMyAnnotation(boolean result, int pageNumber) {
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		if(mApp == null || mBook == null) {
			return;
		}
		
		if(result) {
			Annotation annotation = mApp.data().database()
											.annotationsDatabase().annotation(mBook.id(), pageNumber);
			
			mApp.data().database().annotationsDatabase().deleteByNumber(annotation);

		}
	}
}
