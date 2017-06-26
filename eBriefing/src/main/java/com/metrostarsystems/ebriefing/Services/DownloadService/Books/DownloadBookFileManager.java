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

package com.metrostarsystems.ebriefing.Services.DownloadService.Books;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadManager;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile.DownloadBookFileListener;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile.DownloadType;

public class DownloadBookFileManager implements DownloadBookFileListener {
	
	public static final String 					TAG = DownloadBookFileManager.class.getSimpleName();
	
	

	private MainApplication							mApp;
	private DownloadService							mService;
	private DownloadManager							mManager;
	private Book									mBook;
	
	private transient DownloadBookFileTaskManager	mDownloadingTasks;
	private Stack<DownloadBookFile> 				mDownloadFiles = new Stack<DownloadBookFile>();
	private ArrayList<DownloadBookFile>				mDownloadFilesCompleted = new ArrayList<DownloadBookFile>();
	public static final int							MAX_DOWNLOADS = 5;
	
	private transient DownloadBookFinishedListener 	mListener;
	
	private transient DownloadBookStatus			mStatus;
	
	
	private long									mDownloadSize = 0;
	
	private int										mMaxProgress = 0;
	
	private Date									mStartTime;
	
	/**
	 * Download book constructor
	 * @param main the main application instance
	 * @param service the download service
	 * @param manager the download manager
	 * @param book the book instance to download
	 */
	public DownloadBookFileManager(MainApplication main, 
										DownloadService service, 
										DownloadManager manager, 
										Book book) {
		mApp 		= main;
		mService 	= service;
		mManager 	= manager;
		mListener 	= (DownloadBookFinishedListener) manager;
		mBook 		= book;
		
		mDownloadingTasks  = new DownloadBookFileTaskManager();
		
		
		// Add the download files 
		// ---------------------------------------------------------------------------------------------------	
		// Book Images
		DownloadBookFile bookSmallImage = new DownloadBookFile(mBook, DownloadType.TYPE_BOOK_SMALL_IMAGE);
		mDownloadFiles.push(bookSmallImage);
		
		DownloadBookFile bookLargeImage = new DownloadBookFile(mBook, DownloadType.TYPE_BOOK_LARGE_IMAGE);
		mDownloadFiles.push(bookLargeImage);
	
		// Pages
		ArrayList<Page> pages = mApp.data().database().pagesDatabase().pagesByBook(mBook.id());
		for(int index = 0; index < pages.size(); index++) {
			DownloadBookFile page = new DownloadBookFile(pages.get(index), DownloadType.TYPE_PAGE);
			mDownloadFiles.push(page);
		}
		// Chapter Images
		ArrayList<Chapter> chapters = mApp.data().database().chaptersDatabase().chaptersByBook(mBook.id());
		for(int index = 0; index < chapters.size(); index++) {
			DownloadBookFile chapterSmall = new DownloadBookFile(chapters.get(index), DownloadType.TYPE_CHAPTER_SMALL_IMAGE);
			mDownloadFiles.push(chapterSmall);
			
			DownloadBookFile chapterLarge = new DownloadBookFile(chapters.get(index), DownloadType.TYPE_CHAPTER_LARGE_IMAGE);
			mDownloadFiles.push(chapterLarge);
		}
		// ---------------------------------------------------------------------------------------------------
		
		mMaxProgress = mDownloadFiles.size();
	}

	public Book book() { return mBook; }
	
	/**
	 * Start the download of the book
	 */
	public void execute() {
		mStartTime = new Date();
		mManager.broadcastDownloadingActive(mBook.id());
		
		mStatus = DownloadBookStatus.RUNNING;
		DownloadService.displayNotificationDownloadStart(mBook.title().hashCode(), mBook.title(), mMaxProgress);
		process();
	}
	
	/**
	 * Resume the download of the book
	 */
	public void resume() {
		mManager.broadcastDownloadingActive(mBook.id());
		
		mStatus = DownloadBookStatus.RUNNING;
		DownloadService.displayNotificationDownloadResumed(mBook.title().hashCode(), mBook.title(), mMaxProgress, mDownloadFilesCompleted.size());
		process();
	}
	
	
	/**
	 * Cancel the download of the book
	 */
	public void cancel() {
		mStatus = DownloadBookStatus.CANCELLED;
		process();
	}
	
	/**
	 * Pause the download of the book
	 */
	public void pause() {
		mManager.broadcastDownloadingPending(mBook.id());
		
		mDownloadingTasks.pause(mDownloadFiles);
		
		mStatus = DownloadBookStatus.PAUSED;
		process();
	}
	
	/**
	 * Process status changes
	 */
	private void process() {
		switch(mStatus) {
			case CANCELLED:	onDownloadCancelled(); break;
			case PAUSED: onDownloadPaused(); break;
			case RUNNING: onDownloadRunning(); break;
			default: onDownloadRunning(); break;
		}
	}
	
	private void onDownloadComplete() {
			
			mStatus = DownloadBookStatus.COMPLETED;
			DownloadService.displayNotificationDownloadComplete(mBook.title().hashCode(), mBook.title());

			// Notify Application that book is complete
			if(!mBook.isUpdated()) {
				mManager.broadcastDownloadingComplete(mBook.id());
			} else {
				mManager.broadcastDownloadingUpdateComplete(mBook.id());
			}
	        // Notify Download Manager that book is complete
	        mListener.onDownloadBookFinished(this);
	}

	private void onDownloadCancelled() {

		mDownloadingTasks.cancel();
		
		mDownloadFiles.clear();
		DownloadService.displayNotificationDownloadCancelled(mBook.title().hashCode(), mBook.title());
        mManager.broadcastDownloadingCancelled(mBook.id());
	}
	
	private void onDownloadPaused() {
		DownloadService.displayNotificationDownloadPaused(mBook.title().hashCode(), mBook.title());
		mManager.broadcastDownloadingPaused(mBook.id());
	}
	
	private void onDownloadRunning() {
		if(!mDownloadFiles.isEmpty()) {
			
			if(mDownloadingTasks == null) {
				mDownloadingTasks  = new DownloadBookFileTaskManager();
			}
			
			while(mDownloadingTasks.hasAvailableTasks()) {
				
				if(mDownloadFiles.isEmpty()) {
					break;
				}
				
				if(!Settings.ENABLE_ENCRYPT_BOOK_DATA) {
					mDownloadingTasks.add(new DownloadBookFileTask(mApp, this, mDownloadFiles.pop()));
				}
				
			}
		} else {
			if(mDownloadingTasks.isFinished()) {
				onDownloadComplete();
			}
		}
	}
	
	@Override
	public void onDownloadBookFileFinished(boolean finished,
			AbstractDownloadBookFileTask task, DownloadBookFile download) {

		// There was an error push the url back to the stack and continue
		if(!finished) {
			Log.i(TAG, "Download failed, adding page " + download.toString() + " to queue");
			mDownloadFiles.push(download);
			
			mDownloadingTasks.remove(task);

		} else {
			
			mDownloadSize += task.fileSize();
			
			Date end_time = new Date();
			
			long totalTime = end_time.getTime() - mStartTime.getTime();

			double rate = (((mDownloadSize / 1024) / ((double) (totalTime) / 1000)) * 8);
	        rate = Math.round( rate * 100.0 ) / 100.0;
	        
	        String rate_value = "";
	      
	        if(rate > 1000) {
	        	rate_value = " at " + DownloadBookFileTask.RATE_FORMAT.format(rate / 1024) + " Mbps";
	        } else {
	        	rate_value = " at " + DownloadBookFileTask.RATE_FORMAT.format(rate) + " Kbps"; 
	        }

			mDownloadingTasks.remove(task);
			mDownloadFilesCompleted.add(download);
			DownloadService.updateNotificationDownloadInProgress(mBook.title().hashCode(), 
														  mBook.title(), 
														  mMaxProgress, 
														  mDownloadFilesCompleted.size(),
														  rate_value);
		}
		
		process();
		
	}

	public static enum DownloadBookStatus {
		PAUSED,
		CANCELLED,
		RUNNING,
		COMPLETED,
		STOPPED;
	}
	
	public static interface DownloadBookFinishedListener {
		public abstract void onDownloadBookFinished(DownloadBookFileManager book);
	}


	
}
