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

package com.metrostarsystems.ebriefing.Services.DownloadService;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookDataTasks.DownloadBookDataTask;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookDataTasks.DownloadBookUpdateDataTask;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookDataTasks.DownloadBookDataTask.DownloadBookDataTaskListener;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookDataTasks.DownloadBookUpdateDataTask.DownloadBookUpdateDataTaskListener;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadManager.DownloadManagerFinishedListener;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.SparseArray;

@SuppressLint("NewApi")
public class DownloadService extends Service implements DownloadBookDataTaskListener, DownloadBookUpdateDataTaskListener, DownloadManagerFinishedListener {
	
	private static final String TAG = DownloadService.class.getSimpleName();
	
	public static final int	SERVICE_NOTIFICATION_ID = 87654;
	
	private static MainApplication 				mApp;
	
	private static DownloadService 				mService;
	
	private final IBinder 						mBinder = new DownloadBinder();
	
	private DownloadManager 					mDownloadManager;

	private WakeLock 							mWakeLock;
	
	private static NotificationManager 					mNotificationManager;
	
	private static SparseArray<Notification.Builder> 	mNotifications;
	
	private static Bitmap 								mServiceIconBitmap;
	
	private DownloadServiceReceiver				mDownloadServiceReceiver;
	
	public DownloadService() {
        mService = this;
	}
	
	public void setApplicationContext(MainApplication main) {
		if(mApp == null) {
			mApp = main;
		}
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
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DownloadServiceWakeLock");
        
        if(mWakeLock != null && !mWakeLock.isHeld()) {
        	mWakeLock.acquire();
        }
        
        mApp = (MainApplication) getApplicationContext();
        
        IntentFilter filterDownload = new IntentFilter(DownloadServiceReceiver.PROCESS_RESPONSE);
        filterDownload.addCategory(Intent.CATEGORY_DEFAULT);
		mDownloadServiceReceiver = new DownloadServiceReceiver(this);
		registerReceiver(mDownloadServiceReceiver, filterDownload);
	}
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(mDownloadManager == null) {
			mDownloadManager = new DownloadManager((MainApplication) getApplicationContext(), this);
		}
		
		
		displayNotificationServiceStart(SERVICE_NOTIFICATION_ID);
		handleIntent(intent);
		
		return START_STICKY;
	}



	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mDownloadServiceReceiver);
		
		onComplete();

	}
	
	public MainApplication app() {
		return mApp;
	}
	
	 
	private void handleIntent(Intent intent) {
		if(intent == null || mDownloadManager == null) {
			return;
		}
        
        if(intent.getIntExtra("response", -1) == DownloadServiceReceiver.MSG_DOWNLOAD_CANCELLED) {
        	mDownloadManager.cancelBook(intent);
        	return;
        }
        
        if(intent.getIntExtra("response", -1) == DownloadServiceReceiver.MSG_DOWNLOAD_PAUSED) {
        	mDownloadManager.pauseBook(intent);
        	return;
        }
        
        if(intent.getIntExtra("response", -1) == DownloadServiceReceiver.MSG_DOWNLOAD_RESUMED) {
        	mDownloadManager.resumeBook(intent);
        	return;
        }
        
        if(intent.getIntExtra("response", -1) == DownloadServiceReceiver.MSG_DOWNLOAD_STARTED) {
        	if(mDownloadManager.isRunning()) {
            	mDownloadManager.addBook(intent);
            } else {
            	mDownloadManager.start(intent);
            }
        }
        
        if(intent.getIntExtra("response", -1) == DownloadServiceReceiver.MSG_DOWNLOAD_UPDATED) {
        	if(mDownloadManager.isRunning()) {
            	mDownloadManager.addBook(intent);
            } else {
            	mDownloadManager.start(intent);
            }
        }
    }
	
	public void onComplete() {
		
		if(mDownloadManager != null) {
		
			if(mDownloadManager.isRunning()) {
				mDownloadManager.stop();
			}
			
			mNotifications.clear();

			
			cancelNotification(SERVICE_NOTIFICATION_ID);
			
			if(mWakeLock != null) {
				if(mWakeLock.isHeld()) {
					mWakeLock.release();
				}
			}
		}
		
	}
	
	public static void displayNotificationServiceStart(int notificationId) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}
		
		builder.setContentTitle("Book Download Service");
		builder.setContentText("Downloading...");
		builder.setLargeIcon(mServiceIconBitmap);
		builder.setSmallIcon(R.drawable.ic_book_notification);
		builder.setOngoing(true);
		builder.setPriority(1);

		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void displayNotificationServiceUpdate(int notificationId, int queueCount) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}

		builder.setContentText("Downloading...");
		builder.setSmallIcon(R.drawable.ic_book_notification);
		builder.setNumber(queueCount);
		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void displayNotificationServiceIdle(int notificationId) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}

		builder.setContentText("Idle, waiting for downloads...");
		
		mNotificationManager.notify(notificationId, builder.build());
	}
	
	
	// Notification
	public static void displayNotificationDownloadStart(int notificationId, String bookTitle, int max) {
		
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}
		
		builder.setContentTitle("Downloading: " + bookTitle);
		builder.setContentText("Download in progress");
		builder.setTicker("Downloading: " + bookTitle);
		builder.setSmallIcon(R.drawable.ic_book_notification);
		builder.setProgress(max, 0, false);
		
		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void displayNotificationDownloadComplete(int notificationId, String bookTitle) {
		
		cancelNotification(notificationId);
	}
	
	public static void displayNotificationDownloadCancelled(int notificationId, String bookTitle) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}
		
		builder.setContentTitle("Downloading: " + bookTitle);
		builder.setContentText("Download Cancelled");
		builder.setSmallIcon(R.drawable.ic_book_logo);
		builder.setProgress(0, 0, false);
		builder.setOngoing(false);

		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void displayNotificationDownloadPaused(int notificationId, String bookTitle) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}
		
		builder.setContentTitle("Downloading: " + bookTitle);
		builder.setContentText("Download Paused");
		builder.setSmallIcon(R.drawable.ic_book_logo);

		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void displayNotificationDownloadResumed(int notificationId, String bookTitle, int max, int current) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}

		builder.setContentText("Download pending");
		builder.setSmallIcon(R.drawable.ic_book_logo);
		builder.setProgress(max, current, false);

		mNotificationManager.notify(notificationId, builder.build());
	}
	
	public static void cancelNotification(int notificationId) {
		mNotificationManager.cancel(notificationId);
	}

	public static void updateNotificationDownloadInProgress(int notificationId, String bookTitle, int max, int current, String rate) {
		Notification.Builder builder = null;
		
		builder = mNotifications.get(notificationId);

		if(builder == null) {
			builder = new Notification.Builder(mService);
			mNotifications.put(notificationId, builder);
		}
		
		builder.setContentTitle("Downloading: " + bookTitle);
		builder.setContentText("Downloading page: " + String.valueOf(current) + " of " + String.valueOf(max) + rate);
		builder.setSmallIcon(R.drawable.ic_book_logo);
		builder.setProgress(max, current, false);

		mNotificationManager.notify(notificationId, builder.build());
	}



	@Override
	public void onDownloadManagerFinished(DownloadManager manager) {
		onComplete();
		stopSelf();
	}
	
// Download Service Broadcasts ---------------------------------------------------------------------------
	
	/**
	 * Run the download book task
	 * @param bookId the id of the book to download
	 */
	public static void downloadServiceStartBook(MainApplication app, String bookId) {
		if(app == null || bookId.isEmpty()) {
			return;
		}
		
		Intent intent = new Intent(app, DownloadService.class);

		Bundle bundle = new Bundle();
		bundle.putInt("response", DownloadServiceReceiver.MSG_DOWNLOAD_STARTED);
		bundle.putString("bookid", bookId);
		intent.putExtras(bundle);

		app.startService(intent);
	}

	/**
	 * Run the download book task
	 * @param bookId the id of the book to download
	 * @param update true if the book is updated, false if not
	 */
	public static void downloadServiceStartBook(MainApplication app, String bookId, boolean update) {
		if(app == null || bookId.isEmpty()) {
			return;
		}
		
		Intent intent = new Intent(app, DownloadService.class);

		Bundle bundle = new Bundle();
		bundle.putInt("response", DownloadServiceReceiver.MSG_DOWNLOAD_UPDATED);
		bundle.putString("bookid", bookId);
		bundle.putBoolean("updated", update);
		intent.putExtras(bundle);

		app.startService(intent);
	}

	/**
	 * Run the cancel download book task
	 * @param bookId the id of the book to cancel the download
	 */
	public static void downloadServiceCancelBook(String bookId) {
		if(mApp == null || mApp.serverConnection() == null || bookId.isEmpty()) {
			return;
		}

		Intent intent = new Intent(mApp, DownloadService.class);

		Bundle bundle = new Bundle();
		bundle.putInt("response", DownloadServiceReceiver.MSG_DOWNLOAD_CANCELLED);
		bundle.putString("bookid", bookId);
		intent.putExtras(bundle);

		mApp.startService(intent);
	}

	/**
	 * Run the pause download book task
	 * @param bookId the id of the book to pause the download
	 */
	public static void downloadServicePauseBook(String bookId) {
		if(mApp == null || bookId.isEmpty()) {
			return;
		}
		
		Intent intent = new Intent(mApp, DownloadService.class);

		Bundle bundle = new Bundle();
		bundle.putInt("response", DownloadServiceReceiver.MSG_DOWNLOAD_PAUSED);
		bundle.putString("bookid", bookId);
		intent.putExtras(bundle);

		mApp.startService(intent);
	}

	/**
	 * Run the resume download book task
	 * @param bookId the id of the book to resume the download
	 */
	public static void downloadServiceResumeBook(String bookId) {
		if(mApp == null || bookId.isEmpty()) {
			return;
		}
		
		Intent intent = new Intent(mApp, DownloadService.class);

		Bundle bundle = new Bundle();
		bundle.putInt("response", DownloadServiceReceiver.MSG_DOWNLOAD_RESUMED);
		bundle.putString("bookid", bookId);
		intent.putExtras(bundle);

		mApp.startService(intent);
	}
	
	/**
	 * Set the book to resumed
	 * @param book the book to be resumed
	 */
	public static void downloadServiceBookResumed(Book book) {
		if(mApp == null || mApp.data() == null || book == null) {
			return;
		}
		
		Book.setStatusPending(mApp.data().database().booksDatabase(), book);
	}
	
	/**
	 * Set the book to downloading
	 * @param book the book to be downloaded
	 */
	public static void downloadServiceBookDownloadActive(Book book) {
		if(mApp == null || mApp.data() == null || book == null) {
			return;
		}

        Book.setStatusActive(mApp.data().database().booksDatabase(), book);
	}
	
	/**
	 * Set the book to download pending
	 * @param book the book to be download pending
	 */
	public static void downloadServiceBookDownloadPending(Book book) {
		if(mApp == null || mApp.data() == null || book == null) {
			return;
		}
		
		Book.setStatusPending(mApp.data().database().booksDatabase(), book);
	}

	/**
	 * Set the book to download cancelled
	 * @param book the book to be download cancelled
	 */
	public static void downloadServiceBookDownloadCancelled(Book book) {
		if(mApp == null || mApp.data() == null || book == null) {
			return;
		}

        // Download cancelled, set book back to server
        book.setSynced(true);
        Book.setStatusServer(mApp.data().database().booksDatabase(), book);
	}
	
	/**
	 * Set the book to download paused
	 * @param book the book to be download paused
	 */
	public static void downloadServiceBookDownloadPaused(Book book) {
		if(mApp == null || mApp.data() == null || book == null) {
			return;
		}
		
		Book.setStatusPaused(mApp.data().database().booksDatabase(), book);
	}
	
	/**
	 * Download of the book is complete and needs to sync
	 * @param book the book that the download is complete
	 */
	public static void downloadServiceBookDownloadComplete(Book book) {
		if(mApp == null || mApp.serverConnection() == null || mApp.data() == null || book == null) {
			return;
		}
		

	
		if(SyncService.canSync() && Settings.SYNC_ON_DOWNLOAD) {

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Syncing: " + book.isSyncedNotes() + " " +
									 book.isSyncedBookmarks() + " " +
									 book.isSyncedAnnotations()); }
			
			Book.setStatusSyncing(mApp.data().database().booksDatabase(), book);
			
			SyncService.syncServiceSyncBookAfterDownload(book.id());
		} else {
			// Unable to sync book after downloading
			
			book.setSyncedNotes(true);
			book.setSyncedBookmarks(true);
			book.setSyncedAnnotations(true);

            Book.setStatusDevice(mApp.data().database().booksDatabase(), book);

			SyncService.syncServiceBookComplete(book);
		}
		
		
	}
	
	public static void downloadServiceBookDownloadUpdateComplete(Book book) {
		downloadServiceBookDownloadComplete(book);
	}
	
	/**
	 * Downloads device book data from a server book
	 * @param book the server book the device book data will come from
	 */
	public static void downloadDeviceBookData(Book book) {
		if(mApp == null || book == null) {
			return;
		}

		if(Book.setStatusPending(mApp.data().database().booksDatabase(), book)) {
			new DownloadBookDataTask(mApp, mApp.downloadService()).execute(book);
		}
	}
	
	/**
	 * Downloads device book update data from a server book
	 * @param book the server book the device book update data will come from
	 */
	public static void downloadUpdateBookData(Book book) {
		if(mApp == null || book == null) {
			return;
		}

		if(Book.setStatusPending(mApp.data().database().booksDatabase(), book)) {
			new DownloadBookUpdateDataTask(mApp, mApp.downloadService()).execute(book);
		}
	}
	
	/**
	 * Called when the data for the book is downloaded so the book 
	 * pages and images can be downloaded
	 * @param book the device book with the downloaded data
	 */
	private void downloadBookDataFinished(Book book) {
		if(book == null) {
			return;
		}

		if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "DownloadBookDataFinished: " + book.title()); }

		if(!book.isUpdated()) {

            if(Book.setStatusPending(mApp.data().database().booksDatabase(), book)) {
                downloadServiceStartBook(mApp, book.id());
            }
		} else {
            if(Book.setStatusUpdating(mApp.data().database().booksDatabase(), book)) {
                downloadServiceStartBook(mApp, book.id(), true);
            }
		}
		
		if(book.isFavorite()) {
			book.setFavorite(true);
			mApp.data().database().booksDatabase().update(book);
		} else {
			book.setFavorite(false);
			mApp.data().database().booksDatabase().update(book);
		}

	}
	
	@Override
	public void onBookDownloadDataTaskFinished(Book book) {
		if(book == null) {
			return;
		}
		
		downloadBookDataFinished(book);
	}
	
	@Override
	public void onBookUpdateDataTaskFinished(Book book) {
		if(book == null) {
			return;
		}
		
		book.setUpdated(true);
		downloadBookDataFinished(book);
	}
	
	public class DownloadBinder extends Binder {
		public DownloadService getService() {
			return DownloadService.this;
		}
	}
}
