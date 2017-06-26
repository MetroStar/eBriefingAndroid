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

package com.metrostarsystems.ebriefing.Data.Framework.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase;
import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookChapter.ActivityChapter;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page.Builder;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionRequest;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject.ServerBookObject;
import com.metrostarsystems.ebriefing.Services.Requests.CoreRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetCoreRequest;
import com.metrostarsystems.ebriefing.Services.Requests.GetSoapRequest;
import com.metrostarsystems.ebriefing.Services.Requests.AbstractSoapRequest;


public class Book {
	
	public static final String TAG = Book.class.getSimpleName();
	
	public static SimpleDateFormat SERVER_DATE_FORMAT 	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
	public static SimpleDateFormat BOOK_DATE_FORMAT		= new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	public static SimpleDateFormat OVERVIEW_FORMAT 		= new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);


	private String 				mId;
	private BookStatus			mStatus;
	private String 				mTitle;
	private String 				mDescription;
	private int 				mChapterCount;
	private int					mPageCount;
	private String 				mSmallImageUrl;
	private String				mLargeImageUrl;
	private int					mImageVersion;
	private int					mBookVersion;
	private String				mDateAdded;
	private String				mDateModified;
	private boolean				mNew				= true;
	private boolean				mFavorite 			= false;
	private boolean				mDownloaded			= false;
	
	private boolean				mChaptersDownloaded	= false;
	private boolean				mPagesDownloaded	= false;
	
	private boolean				mUpdated			= false;
	private String				mUserAdded 			= dateNow();
	private String				mUserModified 		= dateNow();
	
	// Sync is true when book has been synced to server (SetMyBooks)
    private String              mDateSynced         = datePast();
	// If a book is deleted or favorites are changed, synced should be set to false
	private boolean				mSynced				= false;
	// SyncedNotes is true when notes have been synced from the server (GetMyNotes)
	private boolean				mSyncedNotes		= false;
	// SyncedBookmarks is true when bookmarks have been synced from the server (GetMyBookmarks)
	private boolean				mSyncedBookmarks	= false;
	// SyncedAnnotations is true when annotations have been synced from the server (GetMyAnnotations)
	private boolean				mSyncedAnnotations	= false;
    private boolean             mRemoved            = false;
	
	public String id() { return mId; }
	public BookStatus status() { return mStatus; }
	public String title() { return mTitle; }
	public String description() { return mDescription; }
	public int chapterCount() { return mChapterCount; }
	public int pageCount() { return mPageCount; }
	public String directory() { return mId + "/"; }
	public String smallImageUrl() { return mSmallImageUrl; }
	public String smallImageFilename() {  return mId + "small.png"; }
	public String smallImageFilePath() { return directory() + smallImageFilename(); }
	public String largeImageUrl() { return mLargeImageUrl; }
	public String largeImageFilename() { return mId + "large.png"; }
	public String largeImageFilePath() { return directory() + largeImageFilename(); }
	public int imageVersion() { return mImageVersion; }
	public int bookVersion() { return mBookVersion; }
	public String dateAdded() { return mDateAdded; }
	public String dateModified() { return mDateModified; }
	public boolean isNew() { return mNew; }
	public boolean isFavorite() { return mFavorite; }
	public boolean isDownloaded() { return mDownloaded; }
	public boolean isChaptersDownloaded() { return mChaptersDownloaded; }
	public boolean isPagesDownloaded() { return mPagesDownloaded; }
	public boolean isUpdated() { return mUpdated; }
	public String userAdded() { return mUserAdded; }
	public String userModified() { return mUserModified; }
    public String dateSynced() { return mDateSynced; }
	public boolean isSynced() { return mSynced; }
	public boolean isSyncedNotes() { return mSyncedNotes; }
	public boolean isSyncedBookmarks() { return mSyncedBookmarks; }
	public boolean isSyncedAnnotations() { return mSyncedAnnotations; }
    public boolean isRemoved() { return mRemoved; }
	
	public void setNew(boolean isNew) {
		mNew = isNew;
	}
	
	public void setFavorite(boolean isFavorite) {
		mFavorite = isFavorite;
	}
	
	public void setSynced(boolean isSynced) {
		if(isSynced) {
            mDateSynced = dateNow();
        }

        mSynced = isSynced;
	}
	
	public void setDownloaded(boolean isDownloaded) {
		mDownloaded = isDownloaded;
	}
	
	public void setChaptersDownloaded(boolean isDownloaded) {
		mChaptersDownloaded = isDownloaded;
	}
	
	public void setPagesDownloaded(boolean isDownloaded) {
		mPagesDownloaded = isDownloaded;
	}
	
	public void setUpdated(boolean isUpdated) {
		mUpdated = isUpdated;
	}
	
	public void setSyncedNotes(boolean isSynced) {
		mSyncedNotes = isSynced;
	}
	
	public void setSyncedBookmarks(boolean isSynced) {
		mSyncedBookmarks = isSynced;
	}
	
	public void setSyncedAnnotations(boolean isSynced) {
		mSyncedAnnotations = isSynced;
	}

    public void setRemoved(boolean isRemoved) {
        mRemoved = isRemoved;
    }
	
	public static boolean setStatusDevice(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_DEVICE);
        book.setUpdated(false);
        book.setDownloaded(true);
        book.setSynced(false);
        book.setRemoved(false);

        return database.update(book);
    }

    public static boolean setStatusServer(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_SERVER);
        book.setUpdated(false);
        book.setDownloaded(false);
        book.setRemoved(true);

        return database.update(book);
    }

    public static boolean setStatusActive(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_DOWNLOADING_ACTIVE);
        book.setDownloaded(false);
        book.setSynced(false);
        book.setRemoved(false);

        return database.update(book);
    }

    public static boolean setStatusPending(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_DOWNLOADING_PENDING);
        book.setDownloaded(false);
        book.setSynced(false);
        book.setRemoved(false);

        return database.update(book);
    }

    public static boolean setStatusPaused(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_DOWNLOADING_PAUSED);

        return database.update(book);
    }

    public static boolean setStatusSyncing(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_SYNCING);
        book.setUpdated(false);
        book.setDownloaded(true);
        book.setSynced(false);
        book.setRemoved(false);

        return database.update(book);
    }

    public static boolean setStatusUpdating(BooksDatabase database, Book book) {

        book.setStatus(BookStatus.STATUS_UPDATING);

        return database.update(book);
    }

    private void setStatus(BookStatus status) {
        mStatus = status;
	}

	public static String dateNow() {
		return SERVER_DATE_FORMAT.format(new Date());
	}

    public static String datePast() {return SERVER_DATE_FORMAT.format(new Date(0L));
    }
	
	public String dateAddedOverviewFormat() { 
		String date = "";
		
		try {
			date = OVERVIEW_FORMAT.format(BOOK_DATE_FORMAT.parse(mDateAdded));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	public String dateModifiedOverviewFormat() { 
		String date = "";
		
		try {
			date = OVERVIEW_FORMAT.format(BOOK_DATE_FORMAT.parse(mDateModified));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

	
	public static void loadChaptersFromService(ServerConnection connection, Book book) throws Exception {
		if(connection == null || book == null) {
			return;
		}
		
		CoreRequest request = new CoreRequest(connection, ServerConnectionRequest.REQUEST_CORE_GET_CHAPTERS);
		request.addPropertyString("bookID", book.id());
		
		Log.i(TAG, book.title());
	
		SoapObject response = new GetCoreRequest(connection).execute(Book.class, request);
		
		if(response != null) {
			for(int index = 0; index < response.getPropertyCount(); index++) {
				Chapter chapter = new Chapter.Builder()
												.bookId(book.id())
												.fromSoap(connection, (SoapObject) response.getProperty(index))
												.build();
				
				connection.app().data().database().chaptersDatabase().insert(chapter);
			}
		}
	}
	
	public static void loadPagesFromService(ServerConnection connection, Book book) throws Exception {
		if(connection == null || book == null) {
			return;
		}
		
		CoreRequest request = new CoreRequest(connection, ServerConnectionRequest.REQUEST_CORE_GET_PAGES);
		request.addPropertyString("bookID", book.id());

        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, book.title()); }
	
		SoapObject response = new GetCoreRequest(connection).execute(Book.class, request);
		
		if(response != null) {
			for(int index = 0; index < response.getPropertyCount(); index++) {
				
				Page page = new Page.Builder()
										.fromSoap(connection, (SoapObject) response.getProperty(index), book)
										.build();
				
				connection.app().data().database().pagesDatabase().insert(page);
			}
		}
	}

	
//	Generators ---------------------------------------------------------------------------------
	public static void generateBookChapterIntent(Activity activity, String bookId) {
		Intent intent = new Intent(activity, ActivityChapter.class);
		
		Bundle extras = new Bundle();
		extras.putString("bookid", bookId);
		intent.putExtras(extras);
		
		activity.startActivity(intent);
	}
	
	public static void generateBookPageIntent(Activity activity, String bookId, String chapterId) {
		Intent intent = new Intent(activity, ActivityPage.class);
		
		Bundle extras = new Bundle();
		extras.putString("bookid", bookId);
		extras.putString("chapterid", chapterId);
		intent.putExtras(extras);
		
		activity.startActivity(intent);
	}
	
	public static void generateBookPageIntent(Activity activity, String bookId, int pageNumber) {
		Intent intent = new Intent(activity, ActivityPage.class);
		
		Bundle extras = new Bundle();
		extras.putString("bookid", bookId);
		extras.putInt("pagenumber", pageNumber);
		intent.putExtras(extras);
		
		activity.startActivity(intent);
	}
	
	public static void generateBookPageEditNoteIntent(Activity activity, Note note) {
		Intent intent = new Intent(activity, ActivityPage.class);
		
		Bundle extras = new Bundle();
		extras.putString("bookid", note.bookId());
		extras.putInt("pagenumber", note.pageNumber());
		extras.putBoolean("editnote", true);
		intent.putExtras(extras);
		
		activity.startActivity(intent);
	}
	
	public static void generateBookPageEditBookmarkIntent(Activity activity, Bookmark bookmark) {
		Intent intent = new Intent(activity, ActivityPage.class);
		
		Bundle extras = new Bundle();
		extras.putString("bookid", bookmark.bookId());
		extras.putInt("pagenumber", bookmark.pageNumber());
		extras.putBoolean("editbookmark", true);
		intent.putExtras(extras);
		
		activity.startActivity(intent);
	}

	
	protected Book(Builder build) {
		mId					= build.mId;
		mStatus				= build.mStatus;
		mTitle				= build.mTitle;
		mDescription		= build.mDescription;
		mChapterCount		= build.mChapterCount;
		mPageCount			= build.mPageCount;
		mSmallImageUrl		= build.mSmallImageUrl;
		mLargeImageUrl		= build.mLargeImageUrl;
		mImageVersion		= build.mImageVersion;
		mBookVersion		= build.mBookVersion;
		mDateAdded			= build.mDateAdded;
		mDateModified		= build.mDateModified;
		mNew				= build.mNew;
		mFavorite 			= build.mFavorite;
		mDownloaded			= build.mDownloaded;
		mChaptersDownloaded	= build.mChaptersDownloaded;
		mPagesDownloaded	= build.mPagesDownloaded;
		mUpdated			= build.mUpdated;
		mUserAdded 			= build.mUserAdded;
		mUserModified 		= build.mUserModified;
        mDateSynced         = build.mDateSynced;
		mSynced				= build.mSynced;
		mSyncedNotes		= build.mSyncedNotes;
		mSyncedBookmarks	= build.mSyncedBookmarks;
		mSyncedAnnotations	= build.mSyncedAnnotations;
        mRemoved            = build.mRemoved;
	}

	public static class Builder {
		private String 				mId;
		private BookStatus			mStatus;
		private String 				mTitle;
		private String 				mDescription;
		private int 				mChapterCount;
		private int					mPageCount;
		private String 				mSmallImageUrl;
		private String				mLargeImageUrl;
		private int					mImageVersion;
		private int					mBookVersion;
		private String				mDateAdded;
		private String				mDateModified;
		private boolean				mNew				= true;
		private boolean				mFavorite 			= false;
		private boolean				mDownloaded			= false;
		private boolean				mChaptersDownloaded	= false;
		private boolean				mPagesDownloaded	= false;
		private boolean				mUpdated			= false;
		private String				mUserAdded 			= dateNow();
		private String				mUserModified 		= dateNow();
        private String              mDateSynced         = datePast();
		private boolean				mSynced				= false;
		private boolean				mSyncedNotes		= false;
		private boolean				mSyncedBookmarks	= false;
		private boolean				mSyncedAnnotations	= false;
        private boolean             mRemoved            = false;
	
		public Builder status(BookStatus status) { mStatus = status; return this; }
		public Builder updated(boolean isUpdated) { mUpdated = isUpdated; return this; }

		public Builder fromCursor(Cursor cursor) {
			
			mId		 			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_ID));
			mStatus				= BookStatus.convert(cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_STATUS)));
			mTitle				= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_TITLE));
			mDescription 		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_DESCRIPTION));
			mChapterCount		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_CHAPTER_COUNT));
			mPageCount			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_PAGE_COUNT));
			mSmallImageUrl		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_SMALL_IMAGE_URL));
			mLargeImageUrl  	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_LARGE_IMAGE_URL));
			mImageVersion  	 	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_IMAGE_VERSION));
			mBookVersion 		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_BOOK_VERSION));
			mDateAdded			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_DATE_ADDED));
			mDateModified		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_DATE_MODIFIED));
			mNew				= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_NEW)) == 1 ? true : false;
			mFavorite 			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_FAVORITE)) == 1 ? true : false;
			mDownloaded			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_DOWNLOADED)) == 1 ? true : false;
			mChaptersDownloaded	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_CHAPTERS_DOWNLOADED)) == 1 ? true : false;
			mPagesDownloaded	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_PAGES_DOWNLOADED)) == 1 ? true : false;
			mUpdated			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_UPDATED)) == 1 ? true : false;
			mUserAdded			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_USER_ADDED));
			mUserModified		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_USER_MODIFIED));
			mDateSynced         = cursor.getString(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_DATE_SYNCED));
            mSynced 			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_SYNCED)) == 1 ? true : false;
			mSyncedNotes		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_SYNCED_NOTES)) == 1 ? true : false;
			mSyncedBookmarks	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_SYNCED_BOOKMARKS)) == 1 ? true : false;
			mSyncedAnnotations	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_SYNCED_ANNOTATIONS)) == 1 ? true : false;
            mRemoved        	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.BOOKS_COLUMN_REMOVED)) == 1 ? true : false;

			return this;
		}
		
		public Builder fromSoap(SoapObject object) {
			if(object == null) { 
				return this; 
			}
			
			String id = 
					object.getPrimitiveProperty(Tags.CORE_GET_BOOKS_RESPONSE_ID).toString();
			String title = 
					object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_TITLE).toString();
			String description = 
					object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_DESCRIPTION).toString();
			
			if(description.contains("anyType{}")) {
				description = "";
			}
			
			int chapter_count = 0;
			int page_count = 0;
			int image_version = 0;
			int version = 0;
			
			try {
				chapter_count = Integer.valueOf(
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_CHAPTER_COUNT).toString());
				page_count = Integer.valueOf(
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_PAGE_COUNT).toString());
				image_version = Integer.valueOf(
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_IMAGE_VERSION).toString());
				version = Integer.valueOf(
						object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_VERSION).toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
			String small_image_url = 
					object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_SMALL_IMAGE_URL).toString();
			String large_image_url = 
					object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_LARGE_IMAGE_URL).toString();
			String date_added = 
					object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_DATE_ADDED).toString();
			String date_modified = 
					object.getProperty(Tags.CORE_GET_BOOKS_RESPONSE_DATE_MODIFIED).toString();
			
			mId		 			= id;
			mStatus				= BookStatus.STATUS_SERVER;
			mTitle				= title;
			mDescription 		= description;
			mChapterCount		= chapter_count;
			mPageCount			= page_count;
			mSmallImageUrl		= small_image_url;
			mLargeImageUrl  	= large_image_url;
			mImageVersion  	 	= image_version;
			mBookVersion 		= version;
			mDateAdded			= date_added;
			mDateModified		= date_modified;
            mDateSynced         = datePast();
			mUpdated 			= true;
			mSynced				= true;
            mRemoved            = false;
			
			return this;
		}
		
		public Builder fromServer(ServerBookObject object) {
			if(object == null) {
				return this;
			}
			
			mId		 			= object.id();
			mTitle				= object.title();
			mDescription 		= object.description();
			mChapterCount		= object.chapterCount();
			mPageCount			= object.pageCount();
			mSmallImageUrl		= object.smallImageUrl();
			mLargeImageUrl  	= object.largeImageUrl();
			mImageVersion  	 	= object.imageVersion();
			mBookVersion 		= object.bookVersion();
			mDateAdded			= object.dateAdded();
			mDateModified		= object.dateModified();
            mDateSynced         = datePast();
			mSynced				= true;
            mRemoved            = false;
			
			return this;
		}
		
		public Book build() {
			return new Book(this);
		}
	}
	
	public static enum BookStatus {
		STATUS_DEVICE,
		STATUS_SERVER,
		STATUS_DOWNLOADING_CANCELLED,
		STATUS_DOWNLOADING_PAUSED,
		STATUS_DOWNLOADING_FAILED,
		STATUS_DOWNLOADING_ACTIVE,
		STATUS_DOWNLOADING_PENDING,
		STATUS_SYNCING,
		STATUS_UPDATING;
		
		private static BookStatus[] mValues = null;
		
		public static BookStatus convert(int status) {
			if(mValues == null) {
				mValues = BookStatus.values();
			}
			
			return mValues[status];
		}
	}

	
}
