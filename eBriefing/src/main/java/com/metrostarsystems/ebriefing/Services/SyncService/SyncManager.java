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

package com.metrostarsystems.ebriefing.Services.SyncService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Intent;
import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookAfterDownloadTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.SaveNotesTask;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.RemoveMyAnnotationTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.SetMyAnnotationTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsTask.GetMyAnnotationsTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.RemoveMyAnnotationTask.RemoveMyAnnotationTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.SetMyAnnotationTask.SetMyAnnotationTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookTask.SyncBookObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookTask.SyncBookTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookAfterDownloadTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookAfterDownloadTask.SyncBookAfterDownloadTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Book.SyncBookAfterDownloadTask.SyncBookAfterDownloadObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.SetMyBookmarksTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksTask.GetMyBookmarksTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.SetMyBookmarksTask.SetMyBookmarksTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.RemoveMyBookTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.SetMyBooksTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksTask.GetMyBooksTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.RemoveMyBookTask.RemoveMyBookTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.SetMyBooksTask.SetMyBooksTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteBookObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteDeviceBookTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteDeviceBookTask.DeleteDeviceBookTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteMyStuffTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteMyStuffTask.DeleteMyStuffTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesTask.GetMyNotesTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.RemoveMyNoteTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.RemoveMyNoteTask.RemoveMyNoteTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.SetMyNoteTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.SetMyNoteTask.SetMyNoteTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesTask.GetAllNotesTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesTask.GetNotesUpdatesTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.SaveNotesTask.SaveNotesTaskListener;

public class SyncManager implements SetMyBooksTaskListener, 
									SetMyNoteTaskListener,
									DeleteMyStuffTaskListener, 
									GetMyBooksTaskListener,
									GetMyNotesTaskListener,
									RemoveMyNoteTaskListener,
									SetMyBookmarksTaskListener,
									GetMyBookmarksTaskListener,
									SetMyAnnotationTaskListener,
									GetMyAnnotationsTaskListener,
									RemoveMyAnnotationTaskListener,
									DeleteDeviceBookTaskListener,
									RemoveMyBookTaskListener,
                                    GetNotesUpdatesTaskListener,
                                    SaveNotesTaskListener,
									
									SyncBookTaskListener,
                                    SyncBookAfterDownloadTaskListener {
	
	private static final String TAG = SyncManager.class.getSimpleName();
	
	private MainApplication 					mApp;
	private SyncService							mService;
	private SyncManagerFinishedListener			mSyncManagerFinishedListener;
	
	private SyncManagerStatus					mStatus;
	
	private Queue<AbstractSyncTask<?,?,?>> 	    mQueue;
	
	private AbstractSyncTask<?,?,?>			    mCurrentTask;
	
	private GetMyBooksObject                    mGetMyBooksResults;
	private GetMyBooksObject                    mSetMyBooksResults;
	private GetMyNotesObject                    mGetMyNotesResults;
	private GetMyBookmarksObject                mGetMyBookmarksResults;
	private GetMyAnnotationsObject              mGetMyAnnotationsResults;

    private GetNotesUpdatesObject               mGetNotesUpdatesResults;
	private ArrayList<Note>                     mSaveNotesResults;
	
	public SyncManager(MainApplication main, SyncService service) {
		mApp = (MainApplication) main;
		mService = service;
		mSyncManagerFinishedListener = (SyncManagerFinishedListener) service;
		
		mQueue = new LinkedList<AbstractSyncTask<?,?,?>>();
	}
	
	public SyncManagerStatus status() { return mStatus; }
	
	public void start(Intent intent) {
		mStatus = SyncManagerStatus.RUNNING;
		
		add(intent);
		
//		process();
	}
	
	/**
	 * Add a task by an SyncServiceReciever intent
	 * @param intent the intent
	 */
	public void add(Intent intent) {
		if(intent == null || !intent.hasExtra("response")) {
			return;
		}
		
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		// Process the response code
		switch(intent.getIntExtra("response", -1)) {
			case SyncServiceReceiver.MSG_SYNC_SETMYBOOKS: 			addSetMyBooksTask(); 				break;
			case SyncServiceReceiver.MSG_SYNC_GETMYBOOKS:			addGetMyBooksTask();				break;
			case SyncServiceReceiver.MSG_SYNC_SETMYNOTE:			addSetMyNoteTask(intent);			break;
			case SyncServiceReceiver.MSG_SYNC_GETMYNOTES:			addGetMyNotesTask(intent);			break;
			case SyncServiceReceiver.MSG_SYNC_REMOVEMYNOTE:			addRemoveMyNoteTask(intent);		break;
			case SyncServiceReceiver.MSG_SYNC_SETMYBOOKMARKS:		addSetMyBookmarksTask(intent);		break;
			case SyncServiceReceiver.MSG_SYNC_GETMYBOOKMARKS:		addGetMyBookmarksTask(intent);		break;
			case SyncServiceReceiver.MSG_SYNC_SETMYANNOTATION:		addSetMyAnnotationTask(intent);		break;
			case SyncServiceReceiver.MSG_SYNC_GETMYANNOTATIONS:		addGetMyAnnotationsTask(intent);	break;
			case SyncServiceReceiver.MSG_SYNC_REMOVEMYANNOTATION:	addRemoveMyAnnotationTask(intent);	break;
			case SyncServiceReceiver.MSG_SYNC_DELETEMYSTUFF: 		addDeleteMyStuffTask(); 			break;
			case SyncServiceReceiver.MSG_SYNC_DELETEBOOK:			addDeleteBookTask(intent);			break;
			case SyncServiceReceiver.MSG_SYNC_REMOVEMYBOOK:			addRemoveMyBookTask(intent);		break;
            case SyncServiceReceiver.MSG_MULTINOTES_GETNOTESUPDATES:addGetNotesUpdatesTask(intent);     break;
            case SyncServiceReceiver.MSG_MULTINOTES_SAVENOTES:      addSaveNotesTask(intent);           break;
			
			case SyncServiceReceiver.MSG_SYNC_BOOK:					addSyncBookTask(intent);			break;
            case SyncServiceReceiver.MSG_SYNC_BOOK_AFTER_DOWNLOAD:  addSyncBookAfterDownloadTask(intent); break;
		}
		
		if(mCurrentTask == null || mCurrentTask.isFinished()) {
			process();
		}
	}
	
	/**
	 * Adds a task to the queue unless its already in the queue
	 * @param task the task to be added
	 */
	private void addTask(AbstractSyncTask<?,?,?> task) {

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Added task: " + task.getClass().getSimpleName()); }
		mQueue.add(task);

	}
	
// Books Tasks -----------------------------------------------------------------------------------------------
	private void addSetMyBooksTask() {
		SetMyBooksTask task = new SetMyBooksTask(this, mApp.serverConnection());

		addTask(task);
	}
	
	public GetMyBooksObject setMyBooksResults() {
		return mSetMyBooksResults;
	}
	
	private void addGetMyBooksTask() {
		addTask(new GetMyBooksTask(this, mApp.serverConnection()));
	}
	
	public GetMyBooksObject getMyBooksResults() {
		return mGetMyBooksResults;
	}
	
	private void addRemoveMyBookTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String bookid = intent.getStringExtra("bookid");
	    	
			addRemoveMyBookTask(bookid);
		}
	}
	
	private void addRemoveMyBookTask(String bookId) {
		if(bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
		
		addTask(new RemoveMyBookTask(this, mApp.serverConnection(), book));
	}
	
	private void addSyncBookTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String bookid = intent.getStringExtra("bookid");
	    	
			addSyncBookTask(bookid);
		}
	}

	private void addSyncBookTask(String bookId) {
		if(bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
		
		if(book == null) {
			return;
		}
		
		addTask(new SyncBookTask(this, mApp.serverConnection(), book));
	}

    private void addSyncBookAfterDownloadTask(Intent intent) {
        if(intent.hasExtra("bookid")) {
            String bookid = intent.getStringExtra("bookid");

            addSyncBookTaskAfterDownload(bookid);
        }
    }

    private void addSyncBookTaskAfterDownload(String bookId) {
        if(bookId.isEmpty()) {
            return;
        }

        Book book = mApp.data().database().booksDatabase().book(bookId);

        if(book == null) {
            return;
        }

        addTask(new SyncBookAfterDownloadTask(this, mApp.serverConnection(), book));
    }
// -----------------------------------------------------------------------------------------------------------

// Notes Tasks -----------------------------------------------------------------------------------------------
	private void addSetMyNoteTask(Intent intent) {
		if(intent.hasExtra("bookid") && intent.hasExtra("pagenumber")) {
		
			String bookId = intent.getStringExtra("bookid");
	    	int pageNumber = intent.getIntExtra("pagenumber", -1);
			
			addSetMyNoteTask(bookId, pageNumber);
		}
	}
	
	private void addSetMyNoteTask(String bookId, int pageNumber) {
		Book book = mApp.data().database().booksDatabase().book(bookId);
			
		Note note = mApp.data().database().notesDatabase().noteByNumber(bookId, pageNumber);
			
		addTask(new SetMyNoteTask(this, mApp.serverConnection(), book, note));
	}
	
	private void addGetMyNotesTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String book_id = intent.getStringExtra("bookid");
	    	addGetMyNotesTask(book_id);
		}
	}
	
	private void addGetMyNotesTask(String bookId) {
		if(bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
			
		addTask(new GetMyNotesTask(this, mApp.serverConnection(), book));
	}
	
	public GetMyNotesObject getMyNotesResults() {
		return mGetMyNotesResults;
	}
	
	private void addRemoveMyNoteTask(Intent intent) {
		if(intent.hasExtra("bookid") && intent.hasExtra("pagenumber")) {
			
			String book_id = intent.getStringExtra("bookid");
	    	int page_number = intent.getIntExtra("pagenumber", -1);
	    	
	    	Book book = mApp.data().database().booksDatabase().book(book_id);
	    	
	    	Note note = mApp.data().database().notesDatabase().noteByNumber(book_id, page_number);
    		
    		
    		addTask(new RemoveMyNoteTask(this, mApp.serverConnection(), book, note));
		}
	}

    private void addGetNotesUpdatesTask(Intent intent) {
        if(intent.hasExtra(Tags.BOOK_ID_TAG)) {

            String bookId = intent.getStringExtra(Tags.BOOK_ID_TAG);

            addGetNotesUpdatesTask(bookId);
        }
    }

    private void addGetNotesUpdatesTask(String bookId) {
        if(bookId.isEmpty() || bookId.isEmpty()) {
            return;
        }

        Book book = mApp.data().database().booksDatabase().book(bookId);

        if(book != null) {
            addTask(new GetNotesUpdatesTask(this, mApp.serverConnection(), book));
        }
    }

    public GetNotesUpdatesObject getGetNotesUpdatesResults() {
        return mGetNotesUpdatesResults;
    }

    private void addSaveNotesTask(Intent intent) {
        if(intent.hasExtra(Tags.BOOK_ID_TAG)) {

            String bookId = intent.getStringExtra(Tags.BOOK_ID_TAG);

            addSaveNotesTask(bookId);
        }
    }

    private void addSaveNotesTask(String bookId) {
        if(bookId.isEmpty() || bookId.isEmpty()) {
            return;
        }

        Book book = mApp.data().database().booksDatabase().book(bookId);

        if(book != null) {
            addTask(new SaveNotesTask(this, mApp.serverConnection(), book));
        }
    }

    public ArrayList<Note> getSaveNotesResults() {
        return mSaveNotesResults;
    }
// -----------------------------------------------------------------------------------------------------------

// Bookmark Tasks --------------------------------------------------------------------------------------------
	private void addSetMyBookmarksTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String book_id = intent.getStringExtra("bookid");
			
			addSetMyBookmarksTask(book_id);
		}
	}
	
	private void addSetMyBookmarksTask(String bookId) {
		if(bookId == null || bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
			
		if(book != null) {
			addTask(new SetMyBookmarksTask(this, mApp.serverConnection(), book));
		}
	}
	
	private void addGetMyBookmarksTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String bookId = intent.getStringExtra("bookid");
	    	addGetMyBookmarksTask(bookId);
		}
	}
	
	private void addGetMyBookmarksTask(String bookId) {
		if(bookId == null || bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
			
		if(book != null) {
			addTask(new GetMyBookmarksTask(this, mApp.serverConnection(), book));
		}
	}
	
	public GetMyBookmarksObject getMyBookmarksResults() {
		return mGetMyBookmarksResults;
	}
// -----------------------------------------------------------------------------------------------------------

// Annotation Tasks ------------------------------------------------------------------------------------------
	private void addSetMyAnnotationTask(Intent intent) {
		if(intent.hasExtra("bookid") && intent.hasExtra("pagenumber")) {
		
			String bookId = intent.getStringExtra("bookid");
		    int pageNumber = intent.getIntExtra("pagenumber", -1);
				
			addSetMyAnnotationTask(bookId, pageNumber);
		}
	}
	
	private void addSetMyAnnotationTask(String bookId, int pageNumber) {
		if(bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
		
		Annotation annotation = mApp.data().database().annotationsDatabase().annotation(bookId, pageNumber);
		
		addTask(new SetMyAnnotationTask(this, mApp.serverConnection(), book, annotation));
	}
	
	private void addGetMyAnnotationsTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String bookId = intent.getStringExtra("bookid");
			addGetMyAnnotationsTask(bookId);
		}
	}
	
	private void addGetMyAnnotationsTask(String bookId) {
		if(bookId.isEmpty()) {
			return;
		}
		
		Book book = mApp.data().database().booksDatabase().book(bookId);
			
		addTask(new GetMyAnnotationsTask(this, mApp.serverConnection(), book));
	}
	
	public GetMyAnnotationsObject getMyAnnotationsResults() {
		return mGetMyAnnotationsResults;
	}
	
	private void addRemoveMyAnnotationTask(Intent intent) {
		if(intent.hasExtra("bookid") && intent.hasExtra("pagenumber")) {
			
			String bookId = intent.getStringExtra("bookid");
	    	int pageNumber = intent.getIntExtra("pagenumber", -1);
	    	
	    	Book book = mApp.data().database().booksDatabase().book(bookId);
    		
	    	Annotation annotation = mApp.data().database().annotationsDatabase().annotation(bookId, pageNumber);
    		
    		addTask(new RemoveMyAnnotationTask(this, mApp.serverConnection(), book, annotation));
		}
	}
// -----------------------------------------------------------------------------------------------------------


	private void addDeleteMyStuffTask() {
		addTask(new DeleteMyStuffTask(this, mApp.serverConnection()));
	}

// Delete Book Task ------------------------------------------------------------------------------------------
	private void addDeleteBookTask(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String bookId = intent.getStringExtra("bookid");
			Book book = mApp.data().database().booksDatabase().book(bookId);
		
	    	addTask(new DeleteDeviceBookTask(this, mApp.serverConnection(), book));
		}
	}
// -----------------------------------------------------------------------------------------------------------

	
	private void process() {

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Queue: " + String.valueOf(mQueue.size())); }
        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Processing"); }

		// Current task is not completed, do nothing
		if(mCurrentTask != null && !mCurrentTask.isFinished()) {
			return;
		}
		
		if(!mQueue.isEmpty()) {
					mCurrentTask = mQueue.remove();
					mCurrentTask.initialize(this);
					
					if(Settings.DEBUG_MESSAGES ) { Log.i(TAG, "Executing " + mCurrentTask.getClass().getSimpleName()); }
					
					switch(mCurrentTask.type()) { // AsyncTask.THREAD_POOL_EXECUTOR
						case TYPE_SET_MY_BOOKS:				((SetMyBooksTask) 			mCurrentTask).execute();	break;
						case TYPE_GET_MY_BOOKS:				((GetMyBooksTask) 			mCurrentTask).execute(); 	break;
						case TYPE_SET_MY_NOTE:				((SetMyNoteTask) 			mCurrentTask).execute();	break;
						case TYPE_GET_MY_NOTES:				((GetMyNotesTask) 			mCurrentTask).execute();	break;
						case TYPE_REMOVE_MY_NOTE:			((RemoveMyNoteTask) 		mCurrentTask).execute();	break;
						case TYPE_SET_MY_BOOKMARKS:			((SetMyBookmarksTask) 		mCurrentTask).execute();	break;
						case TYPE_GET_MY_BOOKMARKS:			((GetMyBookmarksTask) 		mCurrentTask).execute();	break;
						case TYPE_SET_MY_ANNOTATION:		((SetMyAnnotationTask)  	mCurrentTask).execute();	break;
						case TYPE_GET_MY_ANNOTATIONS:		((GetMyAnnotationsTask) 	mCurrentTask).execute();	break;
						case TYPE_REMOVE_MY_ANNOTATION:		((RemoveMyAnnotationTask) 	mCurrentTask).execute();	break;
						case TYPE_DELETE_BOOK:				((DeleteDeviceBookTask)		mCurrentTask).execute(); 	break;
						case TYPE_REMOVE_MY_BOOK:			((RemoveMyBookTask)			mCurrentTask).execute(); 	break;
						
						case TYPE_MULTINOTES_GET_ALL_NOTES:	((GetAllNotesTask)			mCurrentTask).execute();	break;
                        case TYPE_MULTINOTES_GET_NOTES_UPDATES:	((GetNotesUpdatesTask)	mCurrentTask).execute();	break;
                        case TYPE_MULTINOTES_SAVE_NOTES:	((SaveNotesTask)	        mCurrentTask).execute();	break;
						
						case TYPE_DELETE_MY_STUFF: 			((DeleteMyStuffTask) 		mCurrentTask).execute(); 	break;
						
						case TYPE_SYNC_BOOK:				((SyncBookTask)          	mCurrentTask).execute();	break;
                        case TYPE_SYNC_BOOK_AFTER_DOWNLOAD: ((SyncBookAfterDownloadTask)mCurrentTask).execute();    break;
						default: break;
					}
		}
		
		if(mQueue.isEmpty()) {
			stop();
		}
	}
		
	public void stop() {
		mStatus = SyncManagerStatus.COMPLETED;
		
		if(!mQueue.isEmpty()) {
			mQueue.clear();
		}

		
		mSyncManagerFinishedListener.onSyncManagerFinished(this);
	}
	
	@Override
	public void OnDeleteMyStuffTaskFinishedListener(GetMyBooksObject result) {
		process();
	}

	
	@Override
	public void onDeleteDeviceBookTaskFinishedListener(DeleteBookObject result, String bookId) {
		if(result == null || !result.isValid()) {
			
			process();
			return;
		}

		process();
	}


// Books Listeners -------------------------------------------------------------------------------------------
	@Override
	public void onSetMyBooksTaskFinishedListener(GetMyBooksObject result) {

		
		mSetMyBooksResults = result;
		broadcastSetMyBooksFinished();
		
		process();
	}
	
	private void broadcastSetMyBooksFinished() {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_SETMYBOOKS);
        mService.sendBroadcast(broadcastIntent);
	}
	
	@Override
	public void onGetMyBooksTaskFinishedListener(GetMyBooksObject result) {
		if(result == null || !result.isValid()) {
			addGetMyBooksTask();
			
			process();
			return;
		}
		
		mGetMyBooksResults = result;
		broadcastGetMyBooksFinished();
		
		process();
	}
	
	private void broadcastGetMyBooksFinished() {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_GETMYBOOKS);
        mService.sendBroadcast(broadcastIntent);
	}
	
	@Override
	public void onRemoveMyBookTaskFinishedListener(GetMyBooksObject result, String bookId) {
		if(result == null || !result.isValid()) {
//			if(Settings.DEBUG ) { Log.i(TAG, mCurrentTask.getClass().getSimpleName() + " failed, retasking"); }
			addRemoveMyBookTask(bookId);
			
			process();
			return;
		}
		
		process();
	}
	
	@Override
	public void onSyncBookTaskFinishedListener(SyncBookTask task, SyncBookObject object) {
		if(object == null) {
			
			process();
			return;
		}
		
		if(!mApp.serverConnection().isMultiNotes()) {
			mGetMyNotesResults = object.getMyNotes();
			broadcastGetMyNotesFinished(object.book().id());
		} else {
			mGetNotesUpdatesResults = object.getNotesUpdates();
			broadcastGetNotesUpdatesFinished(object.book().id());
		}
		mGetMyBookmarksResults = object.bookmarks();
		broadcastGetMyBookmarksFinished(object.book().id());
		
		mGetMyAnnotationsResults = object.annotations();
		broadcastGetMyAnnotationsFinished(object.book().id());
		
		process();
	}

    @Override
    public void onSyncBookAfterDownloadTaskFinishedListener(SyncBookAfterDownloadTask task, SyncBookAfterDownloadObject object) {
        if(object == null) {

            process();
            return;
        }

        if(!mApp.serverConnection().isMultiNotes()) {
            mGetMyNotesResults = object.notesSyncNotes();
            broadcastGetMyNotesFinished(object.book().id());
        } else {
            mGetNotesUpdatesResults = object.notesMultiNotes();
            broadcastGetNotesUpdatesFinished(object.book().id());
        }
        mGetMyBookmarksResults = object.bookmarks();
        broadcastGetMyBookmarksFinished(object.book().id());

        mGetMyAnnotationsResults = object.annotations();
        broadcastGetMyAnnotationsFinished(object.book().id());

        process();
    }

    // -----------------------------------------------------------------------------------------------------------
	
// Notes Listeners -------------------------------------------------------------------------------------------
	@Override
	public void onSetMyNoteTaskFinishedListener(SetMyNoteTask task, GetMyNotesObject result, String bookId, int pageNumber) {
		
		task = null;
		
		if(!result.isValid()) {
			if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mCurrentTask.getClass().getSimpleName() + " failed, retasking"); }
			addSetMyNoteTask(bookId, pageNumber);
			
			process();
			return;
		}
		
		broadcastSetMyNoteFinished(result.isValid(), bookId, pageNumber);
		
		process();

	}
	
	@Override
	public void onGetMyNotesTaskFinishedListener(GetMyNotesTask task, GetMyNotesObject result, String bookId) {
		
		task = null;
		
		if(result == null || !result.isValid()) {
			addGetMyNotesTask(bookId);
			
			process();
			return;
		}
		mGetMyNotesResults = result;
		broadcastGetMyNotesFinished(bookId);
		
		process();
	}
	
	@Override
	public void onRemoveMyNoteTaskFinishedListener(RemoveMyNoteTask task, GetMyNotesObject result, String bookId, int pageNumber) {
		
		task = null;
		
		broadcastRemoveMyNoteFinished(result.isValid(), bookId, pageNumber);
		
		process();
	}
	
	private void broadcastGetMyNotesFinished(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_GETMYNOTES);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	private void broadcastSetMyNoteFinished(boolean result, String bookId, int pageNumber) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_SETMYNOTE);
        broadcastIntent.putExtra("result", result);
        broadcastIntent.putExtra("bookid", bookId);
        broadcastIntent.putExtra("pagenumber", pageNumber);
        mService.sendBroadcast(broadcastIntent);
	}
	
	private void broadcastRemoveMyNoteFinished(boolean result, String bookId, int pageNumber) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_REMOVEMYNOTE);
        broadcastIntent.putExtra("result", result);
        broadcastIntent.putExtra("bookid", bookId);
        broadcastIntent.putExtra("pagenumber", pageNumber);
        mService.sendBroadcast(broadcastIntent);
	}
	
	private void broadcastGetAllNotesFinished(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_MULTINOTES_GETALLNOTES);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}

    @Override
    public void onGetNotesUpdatesTaskFinishedListener(GetNotesUpdatesObject results, String bookId) {
        mGetNotesUpdatesResults = results;

        broadcastGetNotesUpdatesFinished(bookId);

        process();
    }

    private void broadcastGetNotesUpdatesFinished(String bookId) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_MULTINOTES_GETNOTESUPDATES);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
    }

    @Override
    public void onSaveNotesTaskFinishedListener(ArrayList<Note> result, String bookId) {
        if(result != null && result.size() > 0) {
            mSaveNotesResults = result;
            broadcastSaveNotesFinished(bookId);

        }

        process();
    }

    private void broadcastSaveNotesFinished(String bookId) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_MULTINOTES_SAVENOTES);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
    }

// -----------------------------------------------------------------------------------------------------------
	
// Bookmarks Listeners ---------------------------------------------------------------------------------------
	@Override
	public void onSetMyBookmarksTaskFinishedListener(GetMyBookmarksObject result, String bookId) {
		if(!result.isValid()) {
			if(Settings.DEBUG_MESSAGES) { Log.i(TAG, mCurrentTask.getClass().getSimpleName() + " failed, retasking"); }
			addSetMyBookmarksTask(bookId);
			
			process();
			return;
		}
		
		broadcastSetMyBookmarksFinished(result.isValid());
		
		process();
	}
	
	@Override
	public void onGetMyBookmarksTaskFinishedListener(GetMyBookmarksObject result, String bookId) {
		if(result == null || !result.isValid()) {
			addGetMyBookmarksTask(bookId);
			
			process();
			return;
		}
		mGetMyBookmarksResults = result;
		broadcastGetMyBookmarksFinished(bookId);
		
		process();
	}
	
	private void broadcastGetMyBookmarksFinished(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_GETMYBOOKMARKS);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	private void broadcastSetMyBookmarksFinished(boolean result) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_SETMYBOOKMARKS);
        broadcastIntent.putExtra("result", result);
        mService.sendBroadcast(broadcastIntent);
	}
// -----------------------------------------------------------------------------------------------------------
	
// Annotation Listeners --------------------------------------------------------------------------------------
	@Override
	public void onSetMyAnnotationTaskFinishedListener(GetMyAnnotationsObject result, String bookId, int pageNumber) {
		if(!result.isValid()) {
			if(Settings.DEBUG ) { Log.i(TAG, mCurrentTask.getClass().getSimpleName() + " failed, retasking"); }
			addSetMyAnnotationTask(bookId, pageNumber);
			
			process();
			return;
		}
		
		broadcastSetMyAnnotationFinished(result.isValid(), bookId, pageNumber);
		
		process();
	}
	
	@Override
	public void onGetMyAnnotationsTaskFinishedListener(GetMyAnnotationsObject result, String bookId) {
		if(result == null || !result.isValid()) {
			
			addGetMyAnnotationsTask(bookId);
			
			process();
			return;
		}
		
		mGetMyAnnotationsResults = result;
		broadcastGetMyAnnotationsFinished(bookId);
		
		process();
	}
	
	@Override
	public void onRemoveMyAnnotationTaskFinishedListener(GetMyAnnotationsObject result, String bookId, int pageNumber) {
		broadcastRemoveMyAnnotationFinished(result.isValid(), bookId, pageNumber);
		
		process();
	}
	
	private void broadcastSetMyAnnotationFinished(boolean result, String bookId, int pageNumber) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_SETMYANNOTATION);
        broadcastIntent.putExtra("result", result);
        broadcastIntent.putExtra("bookid", bookId);
        broadcastIntent.putExtra("pagenumber", pageNumber);
        mService.sendBroadcast(broadcastIntent);
	}
	
	private void broadcastGetMyAnnotationsFinished(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_GETMYANNOTATIONS);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	private void broadcastRemoveMyAnnotationFinished(boolean result, String bookId, int pageNumber) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", SyncServiceReceiver.MSG_SYNC_REMOVEMYANNOTATION);
        broadcastIntent.putExtra("result", result);
        broadcastIntent.putExtra("bookid", bookId);
        broadcastIntent.putExtra("pagenumber", pageNumber);
        mService.sendBroadcast(broadcastIntent);
	}
	
// -----------------------------------------------------------------------------------------------------------
	
// -----------------------------------------------------------------------------------------------------------
	
	public static enum SyncManagerStatus {
		RUNNING,
		IDLE,
		COMPLETED;
	}
	
	public interface SyncManagerFinishedListener {
		public abstract void onSyncManagerFinished(SyncManager manager);
	}

	

	

	

	

	
	

	

}
