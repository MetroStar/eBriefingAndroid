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

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BookmarksDatabase.BookmarksDatabaseChangedListener;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AnnotationsDatabase {
	
	private static final String TAG = AnnotationsDatabase.class.getSimpleName();

	private SQLiteDatabase 	mReadDatabase;
	private SQLiteDatabase	mWriteDatabase;
	private DatabaseHandle	mHelper;
	
	private ArrayList<AnnotationsDatabaseChangedListener> mListeners;
	
	public AnnotationsDatabase(DatabaseHandle helper) {
		mHelper = helper;
	}
	
	public AnnotationsDatabase open() throws SQLException {
		mWriteDatabase = mHelper.getWritableDatabase();
		mReadDatabase = mHelper.getReadableDatabase();
		
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	public void addListener(AnnotationsDatabaseChangedListener listener) {
		if(mListeners == null) {
			mListeners = new ArrayList<AnnotationsDatabaseChangedListener>();
		}
		
		mListeners.add(listener);
	}
	
	public void removeListener(AnnotationsDatabaseChangedListener listener) {
		if(mListeners == null) {
			return;
		}
		
		mListeners.remove(listener);
	}
	
	public void removeAllListeners() {
		if(mListeners == null) {
			return;
		}
		
		mListeners.clear();
	}
	
	private void notifyListeners(Annotation annotation) {
		if(mListeners != null) {
			for(AnnotationsDatabaseChangedListener listener : mListeners) {
				listener.OnAnnotationsDatabaseChangedListener(annotation);
			}
		}
	}
	
	private ContentValues generateContent(Annotation annotation) {
		ContentValues values = new ContentValues();
		
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_ID, annotation.id());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID, annotation.bookId());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_VERSION, annotation.bookVersion());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_CHAPTER_ID, annotation.chapterId());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_ID, annotation.pageId());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER, annotation.pageNumber());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_PLATFORM, annotation.platform());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_TEXT_DATA_URL, annotation.textDataUrl());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_IMAGE_DATA_URL, annotation.imageDataUrl());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_DATE_ADDED, annotation.dateAdded());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_DATE_MODIFIED, annotation.dateModified());
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_REMOVED, annotation.isRemoved() == true ? 1 : 0);
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_SYNCED, annotation.isSynced() == true ? 1 : 0);
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_NEW, annotation.isNew() == true ? 1 : 0);
		values.put(DatabaseHandle.ANNOTATIONS_COLUMN_TEXT_DATA, Annotation.toXml(annotation.width(),
																				     annotation.height(),
																				     annotation.inkAnnotation()));

		return values;
	}
	
	public boolean insert(Annotation annotation) {
		Log.i(TAG, "inserting annotation page: " + String.valueOf(annotation.pageNumber()));
		long rows = mWriteDatabase.insert(DatabaseHandle.ANNOTATIONS_TABLE, null, generateContent(annotation));
		
		if(rows > 0) {
			notifyListeners(annotation);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update(Annotation annotation) {
		Log.i(TAG, "updating annotation page: " + String.valueOf(annotation.pageNumber()));
		if(annotation == null || annotation.width() == 0 || annotation.height() == 0) {
			return false;
		}
		
		long rows = mWriteDatabase.update(	DatabaseHandle.ANNOTATIONS_TABLE, 
											generateContent(annotation), 
											DatabaseHandle.ANNOTATIONS_COLUMN_ID + " = ?",
											//DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER + " = ? and " +
											//DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
											//new String[] { String.valueOf(annotation.pageNumber()),
											//			   annotation.bookId() } 	// SELECTION ARGS
											new String[] { annotation.id() }
											);
		
		if(rows > 0) {
			notifyListeners(annotation);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteByNumber(Annotation annotation) {
		long rows = mWriteDatabase.delete(	DatabaseHandle.ANNOTATIONS_TABLE,
											DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
											new String[] { String.valueOf(annotation.pageNumber()),
														   String.valueOf(annotation.bookId())} 	// SELECTION ARGS
											);
		
		if(rows > 0) {
			notifyListeners(annotation);
			
			return true;
		} else {
			return false;
		}
	}
	
	public void deleteAnnotations(String bookId) {
		mWriteDatabase.delete(	DatabaseHandle.ANNOTATIONS_TABLE,
								DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ?", 	// SELECTIONS
								new String[] { String.valueOf(bookId) } 	// SELECTION ARGS
								);
	}
	
	public boolean has(String bookId, int pageNumber) {
		return countByPage(bookId, pageNumber) > 0;
	}
	
	public int countByPage(String bookId, int pageNumber) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.ANNOTATIONS_TABLE +
											" where " + DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER + " = ? and " +
											DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ? and not " +
											DatabaseHandle.ANNOTATIONS_COLUMN_REMOVED,
											new String[] { String.valueOf(pageNumber),
														   String.valueOf(bookId) }
											);
	}
	
	public int countByChapter(String chapterId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.ANNOTATIONS_TABLE +
											" where " + DatabaseHandle.ANNOTATIONS_COLUMN_CHAPTER_ID + " = ? and " +
											"not " + DatabaseHandle.ANNOTATIONS_COLUMN_REMOVED,
											new String[] { String.valueOf(chapterId) }
											);
	}
	
	public int countByBook(String bookId) {
		return (int) DatabaseUtils.longForQuery(mReadDatabase, 
											"select count(*) from " + DatabaseHandle.ANNOTATIONS_TABLE +
											" where " + DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ? and " +
											"not " + DatabaseHandle.ANNOTATIONS_COLUMN_REMOVED,
											new String[] { String.valueOf(bookId) }
											);
	}
	
	public Annotation annotationById(String annotationId) {
		Annotation annotation = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.ANNOTATIONS_TABLE +
											" where " + DatabaseHandle.ANNOTATIONS_COLUMN_ID + " = ?", 
											new String[] { String.valueOf(annotationId) }
											);
			
			if(res.getCount() > 0) {
				res.moveToFirst();
				annotation = new Annotation.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
			Log.e(TAG, "annotationById, Error: annotation id " + annotationId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return annotation;
	}
	
	public Annotation annotation(String bookId, int pageNumber) {
		Annotation annotation = null;
		
		Cursor res = null;
		
		try {
			res = mReadDatabase.rawQuery("select * from " + DatabaseHandle.ANNOTATIONS_TABLE +
											" where " + DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER + " = ?" +
											" and " + DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ?",
											new String[] { String.valueOf(pageNumber),
														   String.valueOf(bookId) }
											);
			if(res.getCount() > 0) {
				res.moveToFirst();
				annotation = new Annotation.Builder().fromCursor(res).build();
			}
		} catch(Exception e) {
			Log.e(TAG, "annotation, Error: book id " + bookId + ", page number " + String.valueOf(pageNumber) + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}
		
		return annotation;
	}

	public ArrayList<Annotation> annotationsByBook(String bookId) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.ANNOTATIONS_TABLE +
											 " where " + DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ?" +
											 " and " + DatabaseHandle.ANNOTATIONS_COLUMN_REMOVED + " = ?" +
											 " order by " + DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER + " asc",
											 new String[] { String.valueOf(bookId),
															"0" }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					annotations.add(new Annotation.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "annotationByBook, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return annotations;
	}
	
	public ArrayList<Annotation> annotationsNotSynced(String bookId) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		
		Cursor res = null;

		try {
			res =  mReadDatabase.rawQuery("select * from " + DatabaseHandle.ANNOTATIONS_TABLE +
											 " where " + DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID + " = ?" +
											 "and " + DatabaseHandle.ANNOTATIONS_COLUMN_SYNCED + " = ?",
											 new String[] { String.valueOf(bookId),
															"0" }
											 );
			if(res.getCount() > 0) {
				res.moveToFirst();
				while(res.isAfterLast() == false) {
					annotations.add(new Annotation.Builder().fromCursor(res).build());
					res.moveToNext();
				}
			}
		} catch(Exception e) {
			Log.e(TAG, "annotationsNotSynced, Error: book id " + bookId + " " + e.getMessage());
		} finally {
			if(res != null) {
				res.close();
			}
		}

		return annotations;
	}
	
	public static interface AnnotationsDatabaseChangedListener {
		public abstract void OnAnnotationsDatabaseChangedListener(Annotation annotation);
	}
}
