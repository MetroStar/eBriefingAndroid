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

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.GetMyAnnotationsObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Annotations.ProcessAnnotationTaskResponses;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.GetMyBookmarksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Bookmarks.ProcessBookmarkTaskResponses;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.ProcessBooksTaskResponses;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.SyncNotesProcessTaskResponses;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.SyncNotes.GetMyNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetAllNotesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.MultiNotesProcessTaskResponses;
import com.metrostarsystems.ebriefing.Services.SyncService.Notes.MultiNotes.GetNotesUpdatesObject;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager.SyncManagerFinishedListener;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncManager.SyncManagerStatus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.SparseArray;

public class SyncService extends Service implements SyncManagerFinishedListener {
	
	private static final String TAG = SyncService.class.getSimpleName();
	
	public static final int	SERVICE_NOTIFICATION_ID = 87655;
	
	private static MainApplication 					mApp;
	private static SyncService 						mService;
	
	private final IBinder 							mBinder = new SyncBinder();
	
	private static Bitmap 								mServiceIconBitmap;
	private static NotificationManager 					mNotificationManager;
	private static SparseArray<Notification.Builder> 	mNotifications;

	
	private static boolean							mAutoSync = false;
	private static boolean 							mSync = false;
	
	private SyncManager 							mSyncManager;
	
	private SyncServiceReceiver						mSyncServiceReceiver;

	
	private static ProcessBooksTaskResponses   mProcessBookTaskResponses;
	private static SyncNotesProcessTaskResponses mProcessNoteTaskResponses;
	private static ProcessBookmarkTaskResponses		mProcessBookmarkTaskResponses;
	private static ProcessAnnotationTaskResponses	mProcessAnnotationTaskResponses;
	
	private static MultiNotesProcessTaskResponses	mMultiNotesProcessTaskResponses;

	private WakeLock mWakeLock;
	
	public SyncService() {
		mService = this;
	}
	
	public void setApplicationContext(MainApplication main) {
		mApp = main;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mService = this;
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotifications = new SparseArray<Notification.Builder>();
		
		mServiceIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_book_logo);
		
		// obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SyncServiceWakeLock");
        
        if(mWakeLock != null && !mWakeLock.isHeld()) {
        	mWakeLock.acquire();
        }
        
        mApp = (MainApplication) getApplicationContext();

        // Core
        mProcessBookTaskResponses = new ProcessBooksTaskResponses(mApp);

        // SyncNotes
        mProcessNoteTaskResponses = new SyncNotesProcessTaskResponses(mApp);
        mProcessBookmarkTaskResponses = new ProcessBookmarkTaskResponses(mApp);
        mProcessAnnotationTaskResponses = new ProcessAnnotationTaskResponses(mApp);

        // MultiNotes
        mMultiNotesProcessTaskResponses = new MultiNotesProcessTaskResponses(mApp);
        
        IntentFilter filterSync = new IntentFilter(SyncServiceReceiver.PROCESS_SYNC_RESPONSE);
        filterSync.addCategory(Intent.CATEGORY_DEFAULT);
		mSyncServiceReceiver = new SyncServiceReceiver(this);
		registerReceiver(mSyncServiceReceiver, filterSync);
	}
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mServiceIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_book_logo);
		
		if(mSyncManager == null) {
			mSyncManager = new SyncManager((MainApplication) getApplicationContext(), this);
		}

		handleIntent(intent);
		
		return START_STICKY;
	}
	
	public MainApplication app() {
		return mApp;
	}
	
	public static void displayNotificationUpdateAvailable(int notificationId, String bookTitle) {
		
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}
		
		builder.setContentTitle("Update available for Book: " + bookTitle);
		builder.setContentText("Update available");
		builder.setTicker("Update available for Book: " + bookTitle);
		builder.setLargeIcon(mServiceIconBitmap);
		builder.setSmallIcon(R.drawable.ic_book_notification);
		builder.setAutoCancel(true);
		
		Intent intent = new Intent(mService, ActivityDashboard.class);
		intent.setAction("opentab");
		intent.putExtra("open_updated", true);
		PendingIntent pending_intent = PendingIntent.getActivity(mService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(pending_intent);
		
		
		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void setSync(boolean sync) {
		mSync = sync;
	}
	
	public static boolean canSync() {
		return mSync;
	}
	
	public static void toggleSync() {
		if(mApp == null) {
			return;
		}
		
		mSync = !mSync;
		mApp.preferences().set(Tags.SYNC_PREFERENCE, mSync);
		
		if(mSync) {
			syncOn();
		}
	}
//	
	private static void syncOn() {
		if(mApp == null) {
			return;
		}
		
		if(Settings.SYNC_ON_TURN_ON_SYNC) {
			sync();
		}
	}
	
	/**
	 * Syncs library
	 */
	public static void sync() {
		if(mApp == null) {
			return;
		}
		
		if(canSync()) {
			if(Settings.DISPLAY_SYNC_MESSAGE) {
				Utilities.displayToast(mApp.getApplicationContext(), "Syncing...");
			}
			
			// Sync books to server
			syncServiceSetMyBooks();
			
			// Sync book data to server
			syncServiceSyncBook();
			
			// Sync device with server
			syncServiceGetMyBooks();
		}
	}
	
	public static void setAllowAutoSync(boolean sync) {
		mAutoSync = sync;
	}

	public static boolean allowAutoSync() {
		return mAutoSync;
	}

	public static void syncServiceSyncBook() {
		
		ArrayList<Book> books = mApp.data().database().booksDatabase().getMyBooks();
		
		for(Book book : books) {
			if(book.status() == BookStatus.STATUS_DEVICE) {

				
				// Sync Notes
				ArrayList<Note> notes = mApp.data().database().notesDatabase().notesNotSynced(book.id());
				
				if(notes != null && notes.size() > 0) {
					if(mApp.serverConnection().isMultiNotes()) {
                        syncServiceSaveNotes(book.id());
					} else {
						for(Note note : notes) {
                            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing Note: " + note.id()); }
							syncServiceSetMyNote(book.id(), note.pageNumber());
						}
					}
				}
				
				// Sync Bookmarks
				ArrayList<Bookmark> bookmarks = mApp.data().database().bookmarksDatabase().bookmarksNotSynced(book.id());
				
				if(bookmarks != null && bookmarks.size() > 0) {
					for(Bookmark bookmark : bookmarks) {
                        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing Bookmark: " + bookmark.id()); }
						syncServiceSetMyBookmarks(book.id());
					}
				}
				
				// Sync Annotations
				ArrayList<Annotation> annotations = mApp.data().database().annotationsDatabase().annotationsNotSynced(book.id());
				
				if(annotations != null && annotations.size() > 0) {
					for(Annotation annotation : annotations) {
                        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing Annotation: " + annotation.id()); }
						
						if(!annotation.isRemoved()) {
							syncServiceSetMyAnnotation(book.id(), annotation.pageNumber());
						} else {
							syncServiceRemoveMyAnnotation(book.id(), annotation.pageNumber());
						}
					}
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mSyncServiceReceiver);
		
		onComplete();
	}
	
	 
	private void handleIntent(Intent intent) {
		
        if(mSyncManager.status() == SyncManagerStatus.RUNNING) {
        	mSyncManager.add(intent);
        } else {
        	mSyncManager.start(intent);
        }
        
    }
	
	public void onComplete() {
		
		if(mWakeLock != null) {
			if(mWakeLock.isHeld()) {
				mWakeLock.release();
			}
		}
		
	}

// Sync Books Methods ----------------------------------------------------------------------------------------
	
	/**
	 * Syncing of the book is complete
	 * @param book the book that syncing is complete
	 */
	public static void syncServiceBookComplete(Book book) {
		if(mApp == null || book == null) {
			return;
		}

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Completed Syncing " + book.title()); }
        Book.setStatusDevice(mApp.data().database().booksDatabase(), book);



		syncServiceSetMyBooks();
	}
	
	/**
	 * Runs the set my books task
	 */
	public static void syncServiceSetMyBooks() {
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);
	
			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_SETMYBOOKS);
			intent.putExtras(bundle);
	
			mApp.startService(intent);
		}
	}

	/**
	 * Runs the get my books task
	 */
	public static void syncServiceGetMyBooks() {
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);
	
			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_GETMYBOOKS);
			intent.putExtras(bundle);
	
			mApp.startService(intent);
		}
	}
	
	/**
	 * Runs the sync book task
	 */
	public static void syncServiceSyncBook(String bookId) {
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);
	
			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_BOOK);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);
	
			mApp.startService(intent);
		}
	}

    public static void syncServiceSyncBookAfterDownload(String bookId) {
        if(mApp == null || mApp.serverConnection() == null) {
            return;
        }

        if(canSync()) {
            Intent intent = new Intent(mApp, SyncService.class);

            Bundle bundle = new Bundle();
            bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_BOOK_AFTER_DOWNLOAD);
            bundle.putString("bookid", bookId);
            intent.putExtras(bundle);

            mApp.startService(intent);
        }
    }
	
	/**
	 * Processing of the book is complete
	 * @param book the book
	 */
	public static void syncServiceGetMyBookComplete(Book book) {
		if(mApp == null || mApp.serverConnection() == null || mApp.data() == null || book == null) {
			return;
		}
		
		
		if(canSync()) {
			
			book.setSyncedNotes(false);
			book.setSyncedBookmarks(false);
			book.setSyncedAnnotations(false);

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + book.title()); }

            Book.setStatusSyncing(mApp.data().database().booksDatabase(), book);
			
			syncServiceSyncBook(book.id());
		} else {
			// Sync cannot be completed, complete syncing anyway
			
			book.setSyncedNotes(true);
			book.setSyncedBookmarks(true);
			book.setSyncedAnnotations(true);
			mApp.data().database().booksDatabase().update(book);
			
			syncServiceBookComplete(book);
		}
		
		
	}

	/**
	 * Runs the remove my book task
	 * @param bookId the id of the book to remove
	 */
	public static void syncServiceRemoveMyBook(String bookId) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_REMOVEMYBOOK);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}
	
	public static ProcessBooksTaskResponses processBookTaskResponses() {
		return mProcessBookTaskResponses;
	}
// -----------------------------------------------------------------------------------------------------------

// Sync Notes Methods ----------------------------------------------------------------------------------------
	
	public static void syncServiceGetAllNotes(String bookId) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_MULTINOTES_GETALLNOTES);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}

    public static void syncServiceGetNotesUpdates(String bookId) {
        if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
            return;
        }

        if(canSync()) {
            Intent intent = new Intent(mApp, SyncService.class);

            Bundle bundle = new Bundle();
            bundle.putInt("response", SyncServiceReceiver.MSG_MULTINOTES_GETNOTESUPDATES);
            bundle.putString("bookid", bookId);
            intent.putExtras(bundle);

            mApp.startService(intent);
        }
    }

    public static void syncServiceSaveNotes(String bookId) {
        if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
            return;
        }

        if(canSync()) {
            Intent intent = new Intent(mApp, SyncService.class);

            Bundle bundle = new Bundle();
            bundle.putInt("response", SyncServiceReceiver.MSG_MULTINOTES_SAVENOTES);
            bundle.putString("bookid", bookId);
            intent.putExtras(bundle);

            mApp.startService(intent);
        }
    }
	
	
	/**
	 * Run the set my note task
	 * @param bookId the id of the book to set the note
	 * @param pageNumber the page number of the note to set
	 */
	public static void syncServiceSetMyNote(String bookId, int pageNumber) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_SETMYNOTE);
			bundle.putString("bookid", bookId);
			bundle.putInt("pagenumber", pageNumber);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}

	/**
	 * Run the get my notes task
	 * @param bookId the id of the book to get the notes
	 */
	public static void syncServiceGetMyNotes(String bookId) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_GETMYNOTES);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}

	/**
	 * Run the remove my note task
	 * @param bookId the id of the book to remove the note
	 * @param pageNumber the page number of the note to remove
	 */
	public static void syncServiceRemoveMyNote(String bookId, int pageNumber) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_REMOVEMYNOTE);
			bundle.putString("bookid", bookId);
			bundle.putInt("pagenumber", pageNumber);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}
	
	public static SyncNotesProcessTaskResponses processSyncNoteTaskResponses() {
		return mProcessNoteTaskResponses;
	}
	
	public static MultiNotesProcessTaskResponses processMultiNoteTaskResponses() {
		return mMultiNotesProcessTaskResponses;
	}
// -----------------------------------------------------------------------------------------------------------

// Sync Bookmarks Methods ------------------------------------------------------------------------------------
	
	/**
	 * Run the set my bookmarks task
	 */
	public static void syncServiceSetMyBookmarks(String bookId) {
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_SETMYBOOKMARKS);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}

	/**
	 * Runs the get my bookmarks task
	 * @param bookId the id of the book to get the bookmarks
	 */
	public static void syncServiceGetMyBookmarks(String bookId) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_GETMYBOOKMARKS);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}	
	
	public static ProcessBookmarkTaskResponses processBookmarkTaskResponses() {
		return mProcessBookmarkTaskResponses;
	}
// -----------------------------------------------------------------------------------------------------------

// Sync Annotations Methods ----------------------------------------------------------------------------------
	
	/**
	 * Run the set my annotation task
	 * @param bookId the id of the book to set the annotation
	 * @param pageNumber the page number of the annotation to set
	 */
	public static void syncServiceSetMyAnnotation(String bookId, int pageNumber) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_SETMYANNOTATION);
			bundle.putString("bookid", bookId);
			bundle.putInt("pagenumber", pageNumber);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}

	/**
	 * Run the get my annotations task
	 * @param bookId the id of the book to get the annotations
	 */
	public static void syncServiceGetMyAnnotations(String bookId) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_GETMYANNOTATIONS);
			bundle.putString("bookid", bookId);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}	

	/**
	 * Run the remove my annotation task
	 * @param bookId the id of the book to remove the annotation
	 * @param pageNumber the page number of the annotation to remove
	 */
	public static void syncServiceRemoveMyAnnotation(String bookId, int pageNumber) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);

			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_REMOVEMYANNOTATION);
			bundle.putString("bookid", bookId);
			bundle.putInt("pagenumber", pageNumber);
			intent.putExtras(bundle);

			mApp.startService(intent);
		}
	}
	
	public static ProcessAnnotationTaskResponses processAnnotationTaskResponses() {
		return mProcessAnnotationTaskResponses;
	}
// -----------------------------------------------------------------------------------------------------------


	/**
	 * Run the delete my stuff task
	 */
	public static void syncServiceDeleteMyStuff() {
		if(mApp == null) {
			return;
		}
		
		if(canSync()) {
			Intent intent = new Intent(mApp, SyncService.class);
			
			Bundle bundle = new Bundle();
			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_DELETEMYSTUFF);
			intent.putExtras(bundle);
			
			mApp.startService(intent);
		}
	}
	
	/**
	 * Runs the delete my book task, does not require a network connection
	 * @param bookId the id of the book to delete
	 */
	public static void syncServiceDeleteBook(String bookId) {
		if(mApp == null || bookId.isEmpty()) {
			return;
		}
		
		Intent intent = new Intent(mApp, SyncService.class);
		
		Bundle bundle = new Bundle();
		bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_DELETEBOOK);
		bundle.putString("bookid", bookId);
		intent.putExtras(bundle);
		
		mApp.startService(intent);
	}
	
	
	
	public GetMyBooksObject getMyBooksResults() {
		return mSyncManager.getMyBooksResults();
	}
	
	public GetMyBooksObject setMyBooksResults() {
		return mSyncManager.setMyBooksResults();
	}

	public GetMyNotesObject getMyNotesResults() {
		return mSyncManager.getMyNotesResults();
	}

    public GetNotesUpdatesObject getGetNotesUpdatesResults() {
        return mSyncManager.getGetNotesUpdatesResults();
    }

    public ArrayList<Note> getSaveNotesResults() {
        return mSyncManager.getSaveNotesResults();
    }
	
	public GetMyBookmarksObject getMyBookmarksResults() {
		return mSyncManager.getMyBookmarksResults();
	}
	
	public GetMyAnnotationsObject getMyAnnotationsResults() {
		return mSyncManager.getMyAnnotationsResults();
	}

	@Override
	public void onSyncManagerFinished(SyncManager manager) {
		onComplete();
		stopSelf();
	}

	public class SyncBinder extends Binder {
		public SyncService getService() {
			return SyncService.this;
		}
	}
	
	public static boolean isNetworkAvailable() {
		if(mApp == null) {
			return false;
		}
		
		boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    
	    return haveConnectedWifi || haveConnectedMobile;
	}
	
	public static boolean isScreenOn() {
		if(mApp == null) {
			return false;
		}
		
		PowerManager pm = (PowerManager) mApp.getSystemService(Context.POWER_SERVICE);
				
		return pm.isScreenOn();
	}
}
