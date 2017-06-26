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

import java.util.Stack;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreService;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFileManager;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFileManager.DownloadBookFinishedListener;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.content.Intent;

public class DownloadManager implements DownloadBookFinishedListener {
	
	private static final String TAG = DownloadManager.class.getSimpleName();
	
	private MainApplication 					mApp;
	private DownloadService						mService;
	private DownloadManagerFinishedListener		mDownloadManagerFinishedListener;
	
	private Stack<DownloadBookFileManager>		mQueue;
	private Stack<DownloadBookFileManager>		mPaused;
	private DownloadBookFileManager				mCurrentDownload;
	
	private int									mTotalDownloads = 0;
	
	private DownloadManagerStatus				mStatus = DownloadManagerStatus.IDLE;
	
	public DownloadManager(MainApplication main, DownloadService service) {
		mApp = (MainApplication) main;
		mStatus = DownloadManagerStatus.INITIALIZING;
		mQueue = new Stack<DownloadBookFileManager>();
		mPaused = new Stack<DownloadBookFileManager>();
		
		mService = service;
		mDownloadManagerFinishedListener = (DownloadManagerFinishedListener) service;
	}
	
	public boolean isRunning() {
		return mStatus == DownloadManagerStatus.RUNNING;
	}
	
	/**
	 * Starts the download manager
	 * @param intent
	 */
	public void start(Intent intent) {
		CoreService.setAllowAutoRefresh(false);
		boolean updated = false;
		
		if(intent == null || !intent.hasExtra("bookid")) {
			return;
		}
		
		mStatus = DownloadManagerStatus.RUNNING;
		
		String book_id = intent.getStringExtra("bookid");
		
		if(intent.hasExtra("updated")) {
			updated = intent.getBooleanExtra("updated", false);
		}
		
		Book book = mApp.data().database().booksDatabase().book(book_id);
		
		if(book == null) {
			return;
		}
		
		if(!isInQueue(book.id())) {
			
			mTotalDownloads = 1;
			
			if(!updated) {
				mQueue.add(new DownloadBookFileManager(mApp, mService, this, book));
				
			} 

		}
		
		process();
	}
	
	/**
	 * Process the download manager
	 */
	private void process() {
		if(mCurrentDownload == null && !mQueue.isEmpty()) {
			DownloadService.displayNotificationServiceUpdate(DownloadService.SERVICE_NOTIFICATION_ID, mTotalDownloads);
			mCurrentDownload = mQueue.pop();
			mCurrentDownload.execute();
		}
		
		if(mCurrentDownload == null && mQueue.isEmpty() && mPaused.isEmpty()) {
			stop();
		} else if(mCurrentDownload == null && !mPaused.isEmpty()) {
			mStatus = DownloadManagerStatus.IDLE;
			DownloadService.displayNotificationServiceIdle(DownloadService.SERVICE_NOTIFICATION_ID);
		}
	}
	
	
	private boolean isInQueue(String bookid) {
		if(mCurrentDownload != null) {
			if(mCurrentDownload.book().id().equalsIgnoreCase(bookid)) {
				return true;
			}
		}
		
		for(int index = 0; index < mQueue.size(); index++) {
			DownloadBookFileManager book = mQueue.get(index);
			
			if(book.book().id().equalsIgnoreCase(bookid)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	public void addBook(Intent intent) {
		String book_id = intent.getStringExtra("bookid");
		
		Book book = mApp.data().database().booksDatabase().book(book_id);
		
		boolean updated = intent.getBooleanExtra("updated", false);
		
		broadcastDownloadingPending(book.id());
		
		if(!isInQueue(book.id())) {
			if(!updated) {
				mQueue.add(new DownloadBookFileManager(mApp, mService, this, book));
			} 

			mTotalDownloads++;
			DownloadService.displayNotificationServiceUpdate(DownloadService.SERVICE_NOTIFICATION_ID, mTotalDownloads);
		}
	}
	
	public void pauseBook(Intent intent) {
		String bookid = intent.getStringExtra("bookid");
		
		if(mCurrentDownload.book().id().equalsIgnoreCase(bookid)) {
			mCurrentDownload.pause();
			mPaused.add(mCurrentDownload);
			mCurrentDownload = null;
			process();
		} else {
			for(int index = 0; index < mQueue.size(); index++) {
				DownloadBookFileManager book = mQueue.get(index);
				
				if(book.book().id().equalsIgnoreCase(bookid)) {
					book.pause();
					mPaused.add(mQueue.remove(index));
					mTotalDownloads--;
					DownloadService.displayNotificationServiceUpdate(DownloadService.SERVICE_NOTIFICATION_ID, mTotalDownloads);
				}
			}
		}
	}
	
	public void resumeBook(Intent intent) {
		String bookid = intent.getStringExtra("bookid");
		
		for(int index = 0; index < mPaused.size(); index++) {
			DownloadBookFileManager book = mPaused.get(index);
			
			if(book.book().id().equalsIgnoreCase(bookid)) {
				
				if(mCurrentDownload == null) {
					mCurrentDownload = mPaused.remove(index);
					mStatus = DownloadManagerStatus.RUNNING;
					mCurrentDownload.resume();
					DownloadService.displayNotificationServiceUpdate(DownloadService.SERVICE_NOTIFICATION_ID, mTotalDownloads);
				} else {
					broadcastDownloadingPending(book.book().id());
					mQueue.add(mPaused.remove(index));
					DownloadService.displayNotificationServiceUpdate(DownloadService.SERVICE_NOTIFICATION_ID, mTotalDownloads);
				}
			}
		}
	}
	
	public void cancelBook(Intent intent) {
		String bookid = intent.getStringExtra("bookid");
		
		if(mCurrentDownload == null) {
			return;
		}
		
		if(mCurrentDownload.book().id().equalsIgnoreCase(bookid)) {
			mCurrentDownload.cancel();
			mCurrentDownload = null;
			
			process();
		} else {
			// Cancel pending book
			for(int index = 0; index < mQueue.size(); index++) {
				DownloadBookFileManager book = mQueue.get(index);
				
				if(book.book().id().equalsIgnoreCase(bookid)) {
					book.cancel();
					mQueue.remove(index);
					mTotalDownloads--;
					DownloadService.displayNotificationServiceUpdate(DownloadService.SERVICE_NOTIFICATION_ID, mTotalDownloads);
				}
			}
		}
	}
	
	public void stop() {
		mStatus = DownloadManagerStatus.STOPPED;
		
		if(mCurrentDownload != null) {
			mCurrentDownload.cancel();
		}
		
		if(!mQueue.isEmpty()) {
			mQueue.clear();
		}
		
		CoreService.setAllowAutoRefresh(true);
		mDownloadManagerFinishedListener.onDownloadManagerFinished(this);
	}
	
	public int queueCount() {
		return mQueue.size();
	}
	
	public void broadcastDownloadingActive(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", DownloadServiceReceiver.MSG_BOOK_DOWNLOADING_ACTIVE);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	public void broadcastDownloadingPending(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", DownloadServiceReceiver.MSG_BOOK_DOWNLOADING_PENDING);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	public void broadcastDownloadingCancelled(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", DownloadServiceReceiver.MSG_BOOK_DOWNLOADING_CANCELLED);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	public void broadcastDownloadingPaused(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", DownloadServiceReceiver.MSG_BOOK_DOWNLOADING_PAUSED);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	public void broadcastDownloadingComplete(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", DownloadServiceReceiver.MSG_BOOK_DOWNLOADING_COMPLETE);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}
	
	public void broadcastDownloadingUpdateComplete(String bookId) {
		Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("response", DownloadServiceReceiver.MSG_BOOK_DOWNLOADING_UPDATE_COMPLETE);
        broadcastIntent.putExtra("bookid", bookId);
        mService.sendBroadcast(broadcastIntent);
	}

	@Override
	public void onDownloadBookFinished(DownloadBookFileManager book) {
		mCurrentDownload = null;
		mTotalDownloads--;
		process();
	}
	
	public static enum DownloadManagerStatus {
		INITIALIZING,
		RUNNING,
		IDLE,
		STOPPED,
		COMPLETED;
	}

	public interface DownloadManagerFinishedListener {
		public abstract void onDownloadManagerFinished(DownloadManager manager);
	}
}
