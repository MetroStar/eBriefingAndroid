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

package com.metrostarsystems.ebriefing.Data.Framework.Annotations;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkPaint;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkPath;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark.Builder;
import com.metrostarsystems.ebriefing.Data.Framework.Database.DatabaseHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.Requests.GetHttpRequest;

import android.database.Cursor;

public class Annotation {
	
	private String 		mId;
	private String		mBookId;
	private int			mBookVersion;
	private String		mChapterId;
	private String 		mPageId;
	private int 		mPageNumber;
	private String 		mPlatform;
	private String 		mTextDataUrl;
	private String 		mImageDataUrl;
	private String 		mDateAdded;
	private String		mDateModified;
	private boolean		mRemoved;
	private boolean		mSynced;
	private boolean		mNew;
	private String		mTextData;
	
	private ArrayList<InkAnnotation>	mInkAnnotation	= new ArrayList<InkAnnotation>();
	private float						mWidth 			= 0.0f;
	private float 						mHeight 		= 0.0f;

	
	public String id() { return mId; }
	public String bookId() { return mBookId; }
	public int bookVersion() { return mBookVersion; }
	public String chapterId() { return mChapterId; }
	public String pageId() { return mPageId; }
	public int pageNumber() { return mPageNumber; }
	public String platform() { return mPlatform; }
	public String textDataUrl() { return mTextDataUrl; }
	public String imageDataUrl() { return mImageDataUrl; }
	public String dateAdded() { return mDateAdded; }
	public String dateModified() { return mDateModified; }
	public boolean isRemoved() { return mRemoved; }
	public boolean isSynced() { return mSynced; }
	public boolean isNew() { return mNew; }
	public String textData() { return mTextData; }
	
	public ArrayList<InkAnnotation> inkAnnotation() { return mInkAnnotation; }
	public float width() { return mWidth; }
	public float height() { return mHeight; }
	
	public boolean isNewer(String dateModified) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		
		SimpleDateFormat format = Book.SERVER_DATE_FORMAT;
		
		try {
			calendar1.setTime(format.parse(dateModified));
			calendar2.setTime(format.parse(dateModified()));
			
			if(calendar2.before(calendar1)) { return false; }
			if(calendar2.after(calendar1)) { return true; }
				
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
			
		return false;
	}
	
	public void setWidth(float width) {
		mWidth = width;
	}
	
	public void setHeight(float height) {
		mHeight = height;
	}
	
	public static String toXml(float width, float height, ArrayList<InkAnnotation> annotations) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<annotation>");
		sb.append("<width>" + String.valueOf(width) + "</width>");
		sb.append("<height>" + String.valueOf(height) + "</height>");
		
		for(int index = 0; index < annotations.size(); index++) {
			sb.append(annotations.get(index).toString());
		}
		
		sb.append("</annotation>");
		
		return sb.toString();
	}
	
	public String toXml() {
		return Annotation.toXml(mWidth, mHeight, mInkAnnotation);
	}
	
	private Annotation(Builder build) {
		mId				= build.mId;
		mBookId			= build.mBookId;
		mBookVersion	= build.mBookVersion;
		mChapterId		= build.mChapterId;
		mPageId			= build.mPageId;
		mPageNumber		= build.mPageNumber;
		mPlatform		= build.mPlatform;
		mTextDataUrl	= build.mTextDataUrl;
		mImageDataUrl	= build.mImageDataUrl;
		mDateAdded		= build.mDateAdded;
		mDateModified	= build.mDateModified;
		mRemoved		= build.mRemoved;
		mSynced			= build.mSynced;
		mNew			= build.mNew;
		mTextData		= build.mTextData;
		mInkAnnotation	= build.mInkAnnotation;
		mWidth			= build.mWidth;
		mHeight			= build.mHeight;
	}
	
	
	public static class Builder {
		private String 		mId;
		private String		mBookId;
		private int			mBookVersion;
		private String		mChapterId;
		private String 		mPageId;
		private int 		mPageNumber;
		private String 		mPlatform;
		private String 		mTextDataUrl	= "";
		private String 		mImageDataUrl	= "";
		private String 		mDateAdded		= Book.dateNow();
		private String		mDateModified	= Book.dateNow();
		private boolean		mRemoved		= false;
		private boolean		mSynced			= false;
		private boolean		mNew			= false;
		private String		mTextData		= "";
		
		private ArrayList<InkAnnotation>	mInkAnnotation	= new ArrayList<InkAnnotation>();
		private float						mWidth 			= 0.0f;
		private float 						mHeight 		= 0.0f;
		
		public Builder id() { mId = UUID.randomUUID().toString(); return this; }
		public Builder bookId(String id) { mBookId = id; return this; }
		public Builder bookVersion(int version) { mBookVersion = version; return this; }
		public Builder chapterId(String id) { mChapterId = id; return this; }
		public Builder pageId(String id) { mPageId = id; return this; }
		public Builder pageNumber(int number) { mPageNumber = number; return this; }
		public Builder platform(String platform) { mPlatform = platform; return this; }
		public Builder textDataUrl(String url) { mTextDataUrl = url; return this; }
		public Builder imageDataUrl(String url) { mImageDataUrl = url; return this; }
		public Builder dateAdded(String date) {
			if(date.isEmpty()) {
				mDateAdded = Book.dateNow();
			} else {
				mDateAdded = date;
			}

			return this;
		}
		public Builder dateModified(String date) {
			if(date.isEmpty()) {
				mDateModified = Book.dateNow();
			} else {
				mDateModified = date;
			}

			return this;
		}
		public Builder isRemoved(boolean isRemoved) { mRemoved = isRemoved; return this; }
		public Builder isSynced(boolean isSynced) { mSynced = isSynced; return this; }
		public Builder isNew(boolean isNew) { mNew = isNew; return this; }
		
		public Builder textData(String text) { 
			mTextData = text; 
			
			parse(text);
			
			return this; 
		}
		
		public Builder inkAnnotation(float width, float height, ArrayList<InkAnnotation> annotation) {
			mInkAnnotation = annotation;
			
			mWidth 		= width;
			mHeight 	= height;
			mTextData 	= Annotation.toXml(width, height, annotation);
			
			return this;
		}
		
		public Builder inkAnnotation(String text) {
			mTextData = text;
			
			parse(text);
			
			return this;
		}

		public Builder fromAnnotation(Annotation annotation) {
			if(annotation == null) {
				return this;
			}
			
			mId				= annotation.mId;
			mBookId			= annotation.mBookId;
			mBookVersion	= annotation.mBookVersion;
			mChapterId		= annotation.mChapterId;
			mPageId			= annotation.mPageId;
			mPageNumber		= annotation.mPageNumber;
			mPlatform		= annotation.mPlatform;
			mTextDataUrl	= annotation.mTextDataUrl;
			mImageDataUrl	= annotation.mImageDataUrl;
			mDateAdded		= annotation.mDateAdded;
			mDateModified	= Book.dateNow();
			mRemoved		= false;
			mSynced			= false;
			mNew			= false;
			
			return this;
		}
		
		public Builder fromCursor(Cursor cursor) {
			mId				= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_ID));
			mBookId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_ID));
			mBookVersion	= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_BOOK_VERSION));
			mChapterId		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_CHAPTER_ID));
			mPageId			= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_ID));
			mPageNumber		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_PAGE_NUMBER));
			mPlatform		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_PLATFORM));
			mTextDataUrl	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_TEXT_DATA_URL));
			mImageDataUrl	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_IMAGE_DATA_URL));
			mDateAdded		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_DATE_ADDED));
			mDateModified	= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_DATE_MODIFIED));
			mRemoved		= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_REMOVED)) == 1 ? true : false;
			mSynced			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_SYNCED)) == 1 ? true : false;
			mNew			= cursor.getInt(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_NEW)) == 1 ? true : false;
			mTextData		= cursor.getString(cursor.getColumnIndex(DatabaseHandle.ANNOTATIONS_COLUMN_TEXT_DATA));
			
			parse(mTextData);
			
			return this;
		}
		
		public Builder fromSoap(ServerConnection connection, SoapObject object) {
			String book_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_BOOK_ID).toString();
			
			String page_id = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_PAGE_ID).toString();
			
			String platform = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_PLATFORM).toString();
			
			String image_data_url = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_IMAGE_URL).toString();
			
			String text_data_url = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_TEXT_URL).toString();
			
			String date_modified = 
					object.getPrimitiveProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_MODIFIED_DATE).toString();

			int book_version = 0;
			boolean removed = false;
			
			try {
				book_version = Integer.valueOf(
					object.getProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_BOOK_VERSION).toString());

				removed = Boolean.valueOf(
					object.getProperty(Tags.SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_REMOVED).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			HttpResponse url_response = new GetHttpRequest(connection).execute(text_data_url);
			
			String text_data = "";
			
			if(url_response != null) {
				try {
					text_data = new BasicResponseHandler().handleResponse(url_response);
				} catch (HttpResponseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			mId 			= UUID.randomUUID().toString();
			mBookId 		= book_id;
			mBookVersion 	= book_version;
			mPageId 		= page_id;
			mPageNumber 	= connection.app().data().database().pagesDatabase().pageNumber(page_id);
			mChapterId		= connection.app().data().database().pagesDatabase().chapterIdByPage(page_id);
			mPlatform		= platform;
			mImageDataUrl	= image_data_url;
			mTextDataUrl	= text_data_url;
			mDateAdded		= Book.dateNow();
			mDateModified 	= date_modified;
			mRemoved 		= removed;
			mSynced			= true;
			mNew			= false;
			mTextData		= text_data;

			parse(text_data);
			
			return this;
		}
		
		private void parse(String xml) {
			XmlPullParserFactory factory = null;
	        XmlPullParser parser = null;
	        
	        ArrayList<InkAnnotation> ink_annotations = new ArrayList<InkAnnotation>();
	        
	        String text = "";
	        
	        InkAnnotation.Builder	ink_annotation = null;
	        InkPaint.Builder 		ink_paint = null;
	        
	        InkPath.Builder			ink_path = null;
	        String action = "";
	        float x1 = 0;
	        float y1 = 0;
	        float x2 = 0;
	        float y2 = 0;
	        
	        
	        try {
	            factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            parser = factory.newPullParser();
	 
	            parser.setInput(new StringReader (xml));
	 
	            int eventType = parser.getEventType();
	            
	            while (eventType != XmlPullParser.END_DOCUMENT) {
	            	
	                String tagname = parser.getName();
	                
	                switch (eventType) {
		                case XmlPullParser.START_TAG:
		                    if (tagname.equalsIgnoreCase("annotation")) {
		                        
		                    } else if(tagname.equalsIgnoreCase("inkannotation")) {
		                    	ink_annotation = new InkAnnotation.Builder();
		                    } else if(tagname.equalsIgnoreCase("inkpaint")) {
		                    	ink_paint = new InkPaint.Builder();
		                    } else if(tagname.equalsIgnoreCase("inkpath")) {
		                    	ink_path = new InkPath.Builder();
		                    }
		                    break;
		 
		                case XmlPullParser.TEXT:
		                    text = parser.getText();
		                    break;
		 
		                case XmlPullParser.END_TAG:
		                    if(tagname.equalsIgnoreCase("annotation")) {
		                    	mInkAnnotation = ink_annotations;
		                    } else if(tagname.equalsIgnoreCase("width")) {	
		                    	mWidth = Float.parseFloat(text);
		                    } else if(tagname.equalsIgnoreCase("height")) {	
		                    	mHeight = Float.parseFloat(text);
		                    	
		                    // InkPaint Object ---------------------------------------------------------------
		                    } else if(tagname.equalsIgnoreCase("color")) {
		                    	ink_paint.color(Integer.parseInt(text));
		                    } else if(tagname.equalsIgnoreCase("strokewidth")) {
		                    	ink_paint.strokeWidth(Float.parseFloat(text));
		                    } else if(tagname.equalsIgnoreCase("mode")) {
		                    	ink_paint.mode(text);
		                    } else if(tagname.equalsIgnoreCase("inkpaint")) {
		                    	ink_annotation.paint(ink_paint.build());
		                    // End InkPaint Object -----------------------------------------------------------
		                    
		                    // InkPath Object ----------------------------------------------------------------
		                    } else if(tagname.equalsIgnoreCase("action")) {
			                    action = text;
		                    } else if(tagname.equalsIgnoreCase("x1")) {
			                    x1 = Float.parseFloat(text);
		                    } else if(tagname.equalsIgnoreCase("y1")) {
		                    	y1 = Float.parseFloat(text);
		                    } else if(tagname.equalsIgnoreCase("x2")) {
		                    	x2 = Float.parseFloat(text);
		                    } else if(tagname.equalsIgnoreCase("y2")) {
		                    	y2 = Float.parseFloat(text);
		                    } else if(tagname.equalsIgnoreCase("moveto")) {
		                    	ink_path.addAction(action, x1, y1);
		                    } else if(tagname.equalsIgnoreCase("lineto")) {
		                    	ink_path.addAction(action, x1, y1);
		                    } else if(tagname.equalsIgnoreCase("quadto")) {
		                    	ink_path.addAction(action, x1, y1, x2, y2);
		                    } else if(tagname.equalsIgnoreCase("inkpath")) {	
		                    	ink_annotation.path(ink_path.build());
		                    // End InkPath Object ------------------------------------------------------------
		                    	
		                    // InkAnnotation Object ----------------------------------------------------------
		                    } else if(tagname.equalsIgnoreCase("inkannotation")) {
		                    	ink_annotations.add(ink_annotation.build());
		                    }
		                    break;
		 
		                default:
		                    break;
	                }
	                eventType = parser.next();
	            }
	 
	        } catch (XmlPullParserException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (NumberFormatException e) {
	        	e.printStackTrace();
	        }
	        
	        mInkAnnotation = ink_annotations;     
		}
		
		public Annotation build() {
			return new Annotation(this);
		}
	}
}
