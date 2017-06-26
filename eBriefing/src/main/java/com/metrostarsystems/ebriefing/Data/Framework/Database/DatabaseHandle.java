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

package com.metrostarsystems.ebriefing.Data.Framework.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandle extends SQLiteOpenHelper {
	
	private static final String TAG  = DatabaseHandle.class.getSimpleName();

	public static final int	   DATABASE_VERSION					= 1;
	
	public static final String SERVER_TABLE						= "server";
	
	public static final String SERVER_COLUMN_ID					= "_id";
	public static final String SERVER_COLUMN_RELEASE			= "release";
	public static final String SERVER_COLUMN_VERSION			= "version";
	
	public static final String CREATE_SERVER_TABLE				= 
			"create table " + 	SERVER_TABLE 					+
						" ( " + SERVER_COLUMN_ID 				+ " text primary key, " +
								SERVER_COLUMN_RELEASE			+ " integer, " +
								SERVER_COLUMN_VERSION			+ " real" +
						" );";
	
	public static final String SERVER_FEATURES_TABLE				= "features";
	
	public static final String SERVER_FEATURES_COLUMN_ID			= "_id";
	public static final String SERVER_FEATURES_COLUMN_NAME		= "name";
	public static final String SERVER_FEATURES_COLUMN_VERSION	= "version";
	public static final String SERVER_FEATURES_COLUMN_RELATIVE_URL = "relative_url";
	
	public static final String CREATE_SERVER_FEATURES_TABLE		= 
			"create table " + 	SERVER_FEATURES_TABLE			+
						" ( " + SERVER_FEATURES_COLUMN_ID 		+ " text primary key, " +
								SERVER_FEATURES_COLUMN_NAME		+ " text, " +
								SERVER_FEATURES_COLUMN_VERSION	+ " integer, " +
								SERVER_FEATURES_COLUMN_RELATIVE_URL + " text" +
						" );";
		
	public static final String BOOKS_TABLE 						= "books";
	
	public static final String BOOKS_COLUMN_ID					= "_id";
	public static final String BOOKS_COLUMN_STATUS				= "status";
	public static final String BOOKS_COLUMN_TITLE				= "title";
	public static final String BOOKS_COLUMN_DESCRIPTION 		= "description";
	public static final String BOOKS_COLUMN_CHAPTER_COUNT		= "chapter_count";
	public static final String BOOKS_COLUMN_PAGE_COUNT			= "page_count";
	public static final String BOOKS_COLUMN_SMALL_IMAGE_URL		= "small_image_url";
	public static final String BOOKS_COLUMN_LARGE_IMAGE_URL		= "large_image_url";
	public static final String BOOKS_COLUMN_IMAGE_VERSION		= "image_version";
	public static final String BOOKS_COLUMN_BOOK_VERSION		= "book_version";
	public static final String BOOKS_COLUMN_DATE_ADDED			= "date_added";
	public static final String BOOKS_COLUMN_DATE_MODIFIED		= "date_modified";
	public static final String BOOKS_COLUMN_NEW					= "new";
	public static final String BOOKS_COLUMN_FAVORITE			= "favorite";
	public static final String BOOKS_COLUMN_DOWNLOADED			= "downloaded";
	public static final String BOOKS_COLUMN_CHAPTERS_DOWNLOADED	= "chapters_downloaded";
	public static final String BOOKS_COLUMN_PAGES_DOWNLOADED	= "pages_downloaded";
	public static final String BOOKS_COLUMN_UPDATED				= "updated";
	public static final String BOOKS_COLUMN_USER_ADDED			= "user_added";
	public static final String BOOKS_COLUMN_USER_MODIFIED		= "user_modified";
    public static final String BOOKS_COLUMN_DATE_SYNCED 		= "date_synced";
	public static final String BOOKS_COLUMN_SYNCED				= "synced";
	public static final String BOOKS_COLUMN_SYNCED_NOTES		= "synced_notes";
	public static final String BOOKS_COLUMN_SYNCED_BOOKMARKS	= "synced_bookmarks";
	public static final String BOOKS_COLUMN_SYNCED_ANNOTATIONS	= "synced_annotations";
    public static final String BOOKS_COLUMN_REMOVED             = "removed";
	
	public static final String CREATE_BOOKS_TABLE				= 
			"create table " + 	BOOKS_TABLE 					+
						" ( " + BOOKS_COLUMN_ID 				+ " text primary key, " +
								BOOKS_COLUMN_STATUS				+ " integer, " +
								BOOKS_COLUMN_TITLE 				+ " text not null, " +
								BOOKS_COLUMN_DESCRIPTION		+ " text not null, " +
								BOOKS_COLUMN_CHAPTER_COUNT		+ " integer, " +
								BOOKS_COLUMN_PAGE_COUNT			+ " integer, " +
								BOOKS_COLUMN_SMALL_IMAGE_URL 	+ " text not null, " +
								BOOKS_COLUMN_LARGE_IMAGE_URL 	+ " text not null, " +
								BOOKS_COLUMN_IMAGE_VERSION		+ " integer, " +
								BOOKS_COLUMN_BOOK_VERSION 		+ " integer, " +
								BOOKS_COLUMN_DATE_ADDED			+ " text, " +
								BOOKS_COLUMN_DATE_MODIFIED		+ " text, " +
								BOOKS_COLUMN_NEW				+ " integer, " +
								BOOKS_COLUMN_FAVORITE			+ " integer, " +
								BOOKS_COLUMN_DOWNLOADED			+ " integer, " +
								BOOKS_COLUMN_CHAPTERS_DOWNLOADED+ " integer, " +
								BOOKS_COLUMN_PAGES_DOWNLOADED	+ " integer, " +
								BOOKS_COLUMN_UPDATED			+ " integer, " +
								BOOKS_COLUMN_USER_ADDED			+ " text, " +
								BOOKS_COLUMN_USER_MODIFIED		+ " text, " +
                                BOOKS_COLUMN_DATE_SYNCED		+ " text, " +
								BOOKS_COLUMN_SYNCED				+ " integer, " +
								BOOKS_COLUMN_SYNCED_NOTES		+ " integer, " +
								BOOKS_COLUMN_SYNCED_BOOKMARKS	+ " integer, " +
								BOOKS_COLUMN_SYNCED_ANNOTATIONS	+ " integer," +
                                BOOKS_COLUMN_REMOVED	        + " integer" +
						" );";
	
	public static final String CHAPTERS_TABLE					= "chapters";
	
	public static final String CHAPTERS_COLUMN_ID				= "_id";
	public static final String CHAPTERS_COLUMN_BOOK_ID			= "book_id";
	public static final String CHAPTERS_COLUMN_TITLE			= "title";
	public static final String CHAPTERS_COLUMN_DESCRIPTION 		= "description";
	public static final String CHAPTERS_COLUMN_PAGE_COUNT		= "page_count";
	public static final String CHAPTERS_COLUMN_SMALL_IMAGE_URL	= "small_image_url";
	public static final String CHAPTERS_COLUMN_LARGE_IMAGE_URL	= "large_image_url";
	public static final String CHAPTERS_COLUMN_IMAGE_VERSION	= "image_version";
	public static final String CHAPTERS_COLUMN_FIRST_PAGE_ID	= "first_page_id";
	
	public static final String CREATE_CHAPTERS_TABLE			= 
			"create table " + 	CHAPTERS_TABLE 					+
						" ( " + CHAPTERS_COLUMN_ID 				+ " text primary key, " +
								CHAPTERS_COLUMN_BOOK_ID			+ " text, " +
								CHAPTERS_COLUMN_TITLE 			+ " text not null, " +
								CHAPTERS_COLUMN_DESCRIPTION		+ " text not null, " +
								CHAPTERS_COLUMN_PAGE_COUNT		+ " integer, " +
								CHAPTERS_COLUMN_SMALL_IMAGE_URL + " text not null, " +
								CHAPTERS_COLUMN_LARGE_IMAGE_URL + " text not null, " +
								CHAPTERS_COLUMN_IMAGE_VERSION	+ " integer, " +
								CHAPTERS_COLUMN_FIRST_PAGE_ID	+ " text" +
						" );";
	
	
	public static final String PAGES_TABLE 		  				= "pages";
	
	public static final String PAGES_COLUMN_ID					= "_id";
	public static final String PAGES_COLUMN_CHAPTER_ID			= "chapter_id";
	public static final String PAGES_COLUMN_BOOK_ID				= "book_id";
	public static final String PAGES_COLUMN_URL					= "url";
	public static final String PAGES_COLUMN_PAGE_NUMBER 		= "page_number";
	public static final String PAGES_COLUMN_MD5					= "md5";
	public static final String PAGES_COLUMN_TYPE				= "type";
	public static final String PAGES_COLUMN_VERSION				= "version";
	
	public static final String CREATE_PAGES_TABLE			= 
			"create table " + 	PAGES_TABLE 					+
						" ( " + PAGES_COLUMN_ID 				+ " text primary key, " +
								PAGES_COLUMN_CHAPTER_ID 		+ " text, " +
								PAGES_COLUMN_BOOK_ID			+ " text, " +
								PAGES_COLUMN_URL 				+ " text not null, " +
								PAGES_COLUMN_PAGE_NUMBER		+ " integer, " +
								PAGES_COLUMN_MD5				+ " text, " +
								PAGES_COLUMN_TYPE				+ " text not null, " +
								PAGES_COLUMN_VERSION			+ " integer" +
						" );";
	
	public static final String NOTES_TABLE						= "notes";
	
	public static final String NOTES_COLUMN_ID					= "_id";
	public static final String NOTES_COLUMN_BOOK_ID				= "book_id";
	public static final String NOTES_COLUMN_BOOK_VERSION		= "book_version";
	public static final String NOTES_COLUMN_CHAPTER_ID			= "chapter_id";
	public static final String NOTES_COLUMN_PAGE_ID				= "page_id";
	public static final String NOTES_COLUMN_PAGE_NUMBER			= "page_number";
	public static final String NOTES_COLUMN_VALUE_URL			= "value_url";
	public static final String NOTES_COLUMN_DATE_CREATED		= "date_created";
	public static final String NOTES_COLUMN_DATE_MODIFIED 		= "date_modified";
	public static final String NOTES_COLUMN_REMOVED				= "removed";
	public static final String NOTES_COLUMN_SYNCED		 		= "synced";
	public static final String NOTES_COLUMN_NEW					= "new";
	public static final String NOTES_COLUMN_CONTENT				= "content";
	
	public static final String CREATE_NOTES_TABLE				= 
			"create table " + 	NOTES_TABLE 					+
						" ( " + NOTES_COLUMN_ID 				+ " text primary key, " +
								NOTES_COLUMN_BOOK_ID			+ " text, " +
								NOTES_COLUMN_BOOK_VERSION		+ " integer, " +
								NOTES_COLUMN_CHAPTER_ID			+ " text, " +
								NOTES_COLUMN_PAGE_ID			+ " text, " +
								NOTES_COLUMN_PAGE_NUMBER		+ " integer, " +
								NOTES_COLUMN_VALUE_URL 			+ " text, " +
								NOTES_COLUMN_DATE_CREATED		+ " text, " +
								NOTES_COLUMN_DATE_MODIFIED		+ " text, " +
								NOTES_COLUMN_REMOVED			+ " integer, " +
								NOTES_COLUMN_SYNCED				+ " integer, " +
								NOTES_COLUMN_CONTENT			+ " text" +
								
						" );";
	
	public static final String BOOKMARKS_TABLE					= "bookmarks";
	
	public static final String BOOKMARKS_COLUMN_ID				= "_id";
	public static final String BOOKMARKS_COLUMN_BOOK_ID			= "book_id";
	public static final String BOOKMARKS_COLUMN_BOOK_VERSION	= "book_version";
	public static final String BOOKMARKS_COLUMN_CHAPTER_ID		= "chapter_id";
	public static final String BOOKMARKS_COLUMN_PAGE_ID			= "page_id";
	public static final String BOOKMARKS_COLUMN_PAGE_NUMBER		= "page_number";
	public static final String BOOKMARKS_COLUMN_VALUE			= "value";
	public static final String BOOKMARKS_COLUMN_DATE_ADDED 		= "date_added";
	public static final String BOOKMARKS_COLUMN_DATE_MODIFIED 	= "date_modified";
	public static final String BOOKMARKS_COLUMN_REMOVED			= "removed";
	public static final String BOOKMARKS_COLUMN_SYNCED		 	= "synced";
	public static final String BOOKMARKS_COLUMN_NEW				= "new";
	
	public static final String CREATE_BOOKMARKS_TABLE			= 
			"create table " + 	BOOKMARKS_TABLE 				+
						" ( " + BOOKMARKS_COLUMN_ID 			+ " text primary key, " +
								BOOKMARKS_COLUMN_BOOK_ID		+ " text, " +
								BOOKMARKS_COLUMN_BOOK_VERSION	+ " integer, " +
								BOOKMARKS_COLUMN_CHAPTER_ID		+ " text, " +
								BOOKMARKS_COLUMN_PAGE_ID		+ " text, " +
								BOOKMARKS_COLUMN_PAGE_NUMBER	+ " integer, " +
								BOOKMARKS_COLUMN_VALUE 			+ " text, " +
								BOOKMARKS_COLUMN_DATE_ADDED		+ " text, " +
								BOOKMARKS_COLUMN_DATE_MODIFIED	+ " text, " +
								BOOKMARKS_COLUMN_REMOVED		+ " integer, " +
								BOOKMARKS_COLUMN_SYNCED			+ " integer, " +
								BOOKMARKS_COLUMN_NEW			+ " integer" +
						" );";
	
	public static final String ANNOTATIONS_TABLE				= "annotations";
	
	public static final String ANNOTATIONS_COLUMN_ID			= "_id";
	public static final String ANNOTATIONS_COLUMN_BOOK_ID		= "book_id";
	public static final String ANNOTATIONS_COLUMN_BOOK_VERSION	= "book_version";
	public static final String ANNOTATIONS_COLUMN_CHAPTER_ID	= "chapter_id";
	public static final String ANNOTATIONS_COLUMN_PAGE_ID		= "page_id";
	public static final String ANNOTATIONS_COLUMN_PAGE_NUMBER	= "page_number";
	public static final String ANNOTATIONS_COLUMN_PLATFORM		= "platform";
	public static final String ANNOTATIONS_COLUMN_TEXT_DATA_URL	= "text_data_url";
	public static final String ANNOTATIONS_COLUMN_IMAGE_DATA_URL= "image_data_url";
	public static final String ANNOTATIONS_COLUMN_DATE_ADDED 	= "date_added";
	public static final String ANNOTATIONS_COLUMN_DATE_MODIFIED = "date_modified";
	public static final String ANNOTATIONS_COLUMN_REMOVED		= "removed";
	public static final String ANNOTATIONS_COLUMN_SYNCED		= "synced";
	public static final String ANNOTATIONS_COLUMN_NEW			= "new";
	public static final String ANNOTATIONS_COLUMN_TEXT_DATA 	= "text_data";
	
	public static final String CREATE_ANNOTATIONS_TABLE			= 
			"create table " + 	ANNOTATIONS_TABLE 				+
						" ( " + ANNOTATIONS_COLUMN_ID 			+ " text primary key, " +
								ANNOTATIONS_COLUMN_BOOK_ID		+ " text, " +
								ANNOTATIONS_COLUMN_BOOK_VERSION	+ " integer, " +
								ANNOTATIONS_COLUMN_CHAPTER_ID	+ " text, " +
								ANNOTATIONS_COLUMN_PAGE_ID		+ " text, " +
								ANNOTATIONS_COLUMN_PAGE_NUMBER	+ " integer, " +
								ANNOTATIONS_COLUMN_PLATFORM		+ " text not null, " +
								ANNOTATIONS_COLUMN_TEXT_DATA_URL+ " text, " +
								ANNOTATIONS_COLUMN_IMAGE_DATA_URL + " text, " +
								ANNOTATIONS_COLUMN_DATE_ADDED	+ " text, " +
								ANNOTATIONS_COLUMN_DATE_MODIFIED+ " text, " +
								ANNOTATIONS_COLUMN_REMOVED		+ " integer, " +
								ANNOTATIONS_COLUMN_SYNCED		+ " integer, " +
								ANNOTATIONS_COLUMN_NEW			+ " integer, " +
								ANNOTATIONS_COLUMN_TEXT_DATA	+ " text" +
						" );";
	
	
	
	
	private Context				mContext;
	
	private static DatabaseHandle	mDatabaseHandle;
	
	private ServerDatabase			mServerDatabase;
	private BooksDatabase 			mBooksDatabase;
	private ChaptersDatabase 		mChaptersDatabase;
	private PagesDatabase			mPagesDatabase;
	private NotesDatabase			mNotesDatabase;
	private BookmarksDatabase		mBookmarksDatabase;
	private AnnotationsDatabase		mAnnotationsDatabase;

	// Constructor
	public DatabaseHandle(Context context, String database) {
		super(context, database, null, DATABASE_VERSION);

		mContext = context;
		
		if(Settings.DELETE_DATABASE_ON_STARTUP) {
			dropTables();
			createTables();
		}
//		exportDB();
		
		mServerDatabase			= new ServerDatabase(this);
		mBooksDatabase 			= new BooksDatabase(this);
		mChaptersDatabase 		= new ChaptersDatabase(this);
		mPagesDatabase 			= new PagesDatabase(this);
		mNotesDatabase 			= new NotesDatabase(this);
		mBookmarksDatabase 		= new BookmarksDatabase(this);
		mAnnotationsDatabase 	= new AnnotationsDatabase(this);
	}
	
	public ServerDatabase serverDatabase() {
		if(mServerDatabase == null) {
			mServerDatabase = new ServerDatabase(this);
		}
		
		return mServerDatabase.open();
	}
	
	public BooksDatabase booksDatabase() {
		if(mBooksDatabase == null) {
			mBooksDatabase = new BooksDatabase(this);
		}
		
		return mBooksDatabase.open();
	}
	
	public ChaptersDatabase chaptersDatabase() {
		if(mChaptersDatabase == null) {
			mChaptersDatabase = new ChaptersDatabase(this);
		}
		
		return mChaptersDatabase.open();
	}
	
	public PagesDatabase pagesDatabase() {
		if(mPagesDatabase == null) {
			mPagesDatabase = new PagesDatabase(this);
		}
		
		return mPagesDatabase.open();
	}
	
	public NotesDatabase notesDatabase() {
		if(mNotesDatabase == null) {
			mNotesDatabase = new NotesDatabase(this);
		}
		
		return mNotesDatabase.open();
	}
	
	public BookmarksDatabase bookmarksDatabase() {
		if(mBookmarksDatabase == null) {
			mBookmarksDatabase = new BookmarksDatabase(this);
		}
		
		return mBookmarksDatabase.open();
	}
	
	public AnnotationsDatabase annotationsDatabase() {
		if(mAnnotationsDatabase == null) {
			mAnnotationsDatabase = new AnnotationsDatabase(this);
		}
		
		return mAnnotationsDatabase.open();
	}
	
	public void removeAllListeners() {
		mBooksDatabase.removeAllListeners();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SERVER_TABLE);
		db.execSQL(CREATE_SERVER_FEATURES_TABLE);
		db.execSQL(CREATE_BOOKS_TABLE);
		db.execSQL(CREATE_CHAPTERS_TABLE);
		db.execSQL(CREATE_PAGES_TABLE);
		db.execSQL(CREATE_NOTES_TABLE);
		db.execSQL(CREATE_BOOKMARKS_TABLE);
		db.execSQL(CREATE_ANNOTATIONS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public void dropTables() {
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL("DROP TABLE IF EXISTS " + SERVER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SERVER_FEATURES_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + BOOKS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CHAPTERS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + PAGES_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + BOOKMARKS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ANNOTATIONS_TABLE);
		
		db.close();
	}
	
	public void createTables() {
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL(CREATE_SERVER_TABLE);
		db.execSQL(CREATE_SERVER_FEATURES_TABLE);
		db.execSQL(CREATE_BOOKS_TABLE);
		db.execSQL(CREATE_CHAPTERS_TABLE);
		db.execSQL(CREATE_PAGES_TABLE);
		db.execSQL(CREATE_NOTES_TABLE);
		db.execSQL(CREATE_BOOKMARKS_TABLE);
		db.execSQL(CREATE_ANNOTATIONS_TABLE);
		
		db.close();
	}
	
	public void exportDB(MainApplication app){
        if(Settings.EXPORT_DATABASE) {
            if(app == null || app.serverConnection() == null) {
                return;
            }

            if(Settings.DEBUG_MESSAGES) {
                Log.i(TAG, "Exporting database: " + app.serverConnection().database());
            }

            File sd = Environment.getExternalStorageDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            File currentDB = mContext.getDatabasePath(app.serverConnection().database());
            String backupDBPath = app.serverConnection().database();
            File backupDB = new File(sd, backupDBPath);

            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
	}
}
