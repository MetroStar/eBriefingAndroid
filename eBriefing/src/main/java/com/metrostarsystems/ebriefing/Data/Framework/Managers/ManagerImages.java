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

package com.metrostarsystems.ebriefing.Data.Framework.Managers;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Cache.Images.ImageCache;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.util.PDFFileStream;

public class ManagerImages extends AbstractManager {
	
	private static final String TAG = ManagerImages.class.getSimpleName();
	
	private static final int PAGE_MARGIN = 80;
	
	protected static ImageCache mImageCache;
	protected static ImageCache	mThumbnailCache;
	protected static ImageCache	mNoteCache;
	protected static ImageCache	mPrintCache;
	
	static {
		mImageCache 	= new ImageCache();
		mThumbnailCache = new ImageCache();
		mNoteCache 		= new ImageCache();
		mPrintCache 	= new ImageCache();
	}

	public ManagerImages(MainApplication app) {
		super(app);
	}
	
	public void clearThumbnails() {
		mThumbnailCache.clear();
	}
	
	public void clearPrintCache() {
		mThumbnailCache.clear();
		mPrintCache.clear();
		mNoteCache.clear();
	}
	
	// Book
	public boolean hasSmallImageFile(MainApplication app, Book book) { 
		if(book == null) {
			return false;
		}
		
		if(book.smallImageUrl().isEmpty()) {
			return false;
		}
		
		
		
		File file = new File(app.readHandle().path() + book.smallImageFilePath());
		
		return file.exists();
	}
	
	public boolean hasLargeImageFile(MainApplication app, Book book) { 
		if(book == null) {
			return false;
		}
		
		if(book.largeImageUrl().isEmpty()) {
			return false;
		}
		
		File file = new File(app.readHandle().path() + book.largeImageFilePath());
		
		return file.exists();
	}
	
	// Chapter
	public boolean hasSmallImageFile(MainApplication app, Chapter chapter) { 
		if(chapter == null) {
			return false;
		}
		
		if(chapter.smallImageUrl().isEmpty()) {
			return false;
		}
		
		File file = new File(app.readHandle().path() + chapter.smallImageFilePath());
		
		return file.exists();
	}
	
	public boolean hasLargeImageFile(MainApplication app, Chapter chapter) { 
		if(chapter == null) {
			return false;
		}
		
		if(chapter.largeImageUrl().isEmpty()) {
			return false;
		}
		
		File file = new File(app.readHandle().path() + chapter.largeImageFilePath());
		
		return file.exists();
	}
	
	
	public static class LoadBookImageTask extends AsyncTask<Void, Integer, Bitmap> {
		private MainApplication 			mApp;
		private WeakReference<ImageView>	mImageReference;
		private Book 						mBook;
		
		public LoadBookImageTask(MainApplication app, ImageView imageView, Book book) {
			mApp = app;
			mBook = book;
			mImageReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			return mImageCache.loadBitmap(mBook.id() + "largeimage", mApp.readHandle().path() + mBook.largeImageFilePath());
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			
			if(mImageReference != null && bitmap != null) {
				final ImageView image_view = mImageReference.get();
				
				if(image_view != null) {
					image_view.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	public static class LoadChapterImageTask extends AsyncTask<Void, Integer, Bitmap> {
		private MainApplication 			mApp;
		private WeakReference<ImageView>	mImageReference;
		private Chapter 				mChapter;
		
		public LoadChapterImageTask(MainApplication app, ImageView imageView, Chapter chapter) {
			mApp 			= app;
			mChapter 		= chapter;
			mImageReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			
			Bitmap image = mImageCache.loadBitmap(mChapter.id() + "largeimage", 
											mApp.readHandle().path() + mChapter.largeImageFilePath());
			
			if(image == null) {
				// TODO redownload image
			}
			
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			
			if(mImageReference != null && bitmap != null) {
				final ImageView image_view = mImageReference.get();
				
				if(image_view != null) {
					image_view.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	private static Bitmap generateNoteBitmap(Book book, int pageNumber, int width, int height) {	
		Bitmap note_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		if(book == null) {
			return note_bitmap;
		}
		
		Canvas note_canvas = new Canvas(note_bitmap);
		note_canvas.drawColor(Color.WHITE);
		
		ArrayList<Note> notes = app().data().database().notesDatabase().notesByNumber(book.id(), pageNumber);
		
		StringBuilder sb =  new StringBuilder();
		
		for(int index = 0; index < notes.size(); index++) {
			Note note = notes.get(index);
			
			sb.append(note.content() + "\n\n");
		}
		
		TextPaint text_paint = new TextPaint();
		text_paint.setColor(Color.BLACK);
		text_paint.setTextSize(25);
		text_paint.setTextAlign(Align.LEFT);
		text_paint.setAntiAlias(true);
		
		StaticLayout note_layout = new StaticLayout(sb.toString(), 
													text_paint, 
													note_canvas.getWidth(), 
													Layout.Alignment.ALIGN_NORMAL, 
													1, 
													0, 
													false);
		note_canvas.translate(0, 10);
		note_layout.draw(note_canvas);
		
		return note_bitmap;
	}
	
	private static Bitmap generateNotePage(Bitmap pageBitmap, Bitmap noteBitmap, Paint paint) {
		
		Bitmap bitmap = Bitmap.createBitmap((int) pageBitmap.getWidth() +
				 								  noteBitmap.getWidth() + PAGE_MARGIN, 
				 							(int) pageBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmap);
		
		canvas.drawBitmap(pageBitmap, PAGE_MARGIN, 0, paint);
		canvas.drawBitmap(noteBitmap, pageBitmap.getWidth() + PAGE_MARGIN, 0, paint);
		
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
	}
	
	public static Bitmap generateAnnotationBitmap(Annotation annotation, float width, float height) {
		if(annotation == null) {
			return null;
		}
		
		if(annotation.width() == 0 && annotation.height() == 0) {
			Log.i(TAG, "Unable to draw annotation, width and height == 0");
			return null;
		}
		
		Bitmap annotation_bitmap = Bitmap.createBitmap((int) annotation.width(), (int) annotation.height(), Bitmap.Config.ARGB_8888);
		
		Canvas annotation_canvas = new Canvas(annotation_bitmap);
		annotation_canvas.drawColor(Color.TRANSPARENT);
    
        for(InkAnnotation ink : annotation.inkAnnotation()) {
        	ink.draw(annotation_canvas, ink.paint());
        }
        
        annotation_canvas.drawBitmap(annotation_bitmap, 0, 0, null);
        
        Bitmap scaled_bitmap = Bitmap.createScaledBitmap(annotation_bitmap, (int) width, (int) height, false);
        
        return scaled_bitmap;
	}
	
	public static Bitmap generateColorBitmapFromPage(Book book, int pageNumber, float scale, 
															boolean showNotes, 
															boolean showAnnotations) {

		Page page = app().data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		String file_path = app().readHandle().path() + page.filePath();
		
		Bitmap page_bitmap = getBitmapFromPage(file_path, scale, showAnnotations);
	
		Paint paint = null;
		
		if(showNotes && app().data().database().notesDatabase().has(book.id(), pageNumber)) {
			Bitmap note_bitmap = generateNoteBitmap(book, pageNumber, page_bitmap.getWidth(), page_bitmap.getHeight());
			
			return generateNotePage(page_bitmap, note_bitmap, paint);
		}
        
        return page_bitmap;
	}
	
	
	
	public static Bitmap generateBWBitmapFromPage(Book book, int pageNumber, float scale,
															boolean showNotes,
															boolean showAnnotations) {
		Page page = app().data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		String file_path = app().readHandle().path() + page.filePath();
		
		Bitmap page_bitmap = getBitmapFromPage(file_path, scale, showAnnotations);
		
		Bitmap background_bitmap = Bitmap.createBitmap(page_bitmap.getWidth(), page_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas page_canvas = new Canvas(background_bitmap);
        
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		
		page_canvas.drawBitmap(page_bitmap, 0, 0, paint);
		
		if(showNotes && app().data().database().notesDatabase().has(book.id(), pageNumber)) {
			Bitmap note_bitmap = generateNoteBitmap(book, pageNumber, page_bitmap.getWidth(), page_bitmap.getHeight());
			
			return generateNotePage(background_bitmap, note_bitmap, paint);
		}
        
        return background_bitmap;
	}
	
	
	public static Bitmap getBitmapFromPage(String filepath, float scale, boolean showAnnotations) {
		Document document = new Document();
		document.Close();
		
		PDFFileStream stream = new PDFFileStream();
		stream.open(filepath);
		
		int ret = document.OpenStream(stream, null);
		
		switch( ret ) {
	        case -1://need input password
	        	return null;
	        case -2://unknown encryption
	        	return null;
	        case -3://damaged or invalid format
	        	return null;
	        case -10://access denied or invalid file path
	        	return null;
	        case 0://succeeded, and continue
	            break;
	        default://unknown error
	        	return null;
		}
		
		com.radaee.pdf.Page page = document.GetPage(0);
		
		int w = (int) (document.GetPageWidth(0) * scale);
		int h = (int) (document.GetPageHeight(0) * scale);
		
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bm = Bitmap.createBitmap(w, h, conf);
		bm.eraseColor(Color.WHITE);//draw background

		com.radaee.pdf.Matrix mat = new com.radaee.pdf.Matrix(scale, -scale, 0, h/* * scale*/);
		
		page.ObjsStart();
		
		if(!showAnnotations) {
			for(int index = 0; index < page.GetAnnotCount(); index++) {
				com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(index);
				annotation.SetHide(true);
			}
		} else {
			for(int index = 0; index < page.GetAnnotCount(); index++) {
				com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(index);
				annotation.SetHide(false);
			}
		}
		
		int dib = Global.dibGet(0, w, h);

		page.RenderPrePare(dib);
		page.RenderToBmp(bm, mat);
		
		mat.Destroy();
		
		page.Close();
		document.Close();
		stream.close();

		return bm;
	}
	
	private static Bitmap getThumbnailFromPage(String filepath, boolean showAnnotations) {
		Document document = new Document();
		document.Close();
		
		PDFFileStream stream = new PDFFileStream();
		stream.open(filepath);
		
		int ret = document.OpenStream(stream, null);
		
		switch( ret ) {
	        case -1://need input password
	        	return null;
	        case -2://unknown encryption
	        	return null;
	        case -3://damaged or invalid format
	        	return null;
	        case -10://access denied or invalid file path
	        	return null;
	        case 0://succeeded, and continue
	            break;
	        default://unknown error
	        	return null;
		}
		
		com.radaee.pdf.Page page = document.GetPage(0);
		
		float scale = .5f;
		
		int w = (int) (document.GetPageWidth(0) * scale);
		int h = (int) (document.GetPageHeight(0) * scale);
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bm = Bitmap.createBitmap(w, h, conf);
		
		bm.eraseColor(Color.WHITE);//draw background

		com.radaee.pdf.Matrix mat = new com.radaee.pdf.Matrix(scale, -scale, 0, h);
		
		page.ObjsStart();
		

		if(!showAnnotations) {
			for(int index = 0; index < page.GetAnnotCount(); index++) {
				com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(index);
				annotation.SetHide(true);
			}
		} else {
			for(int index = 0; index < page.GetAnnotCount(); index++) {
				com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(index);
				annotation.SetHide(false);
			}
		}

		
		if(page.RenderThumb(bm)) {
		
		} else {
			page.RenderToBmp(bm, mat);
		}
		
		mat.Destroy();
		
		page.Close();
		document.Close();
		stream.close();

		return bm;
	}
	
	private static Bitmap generateNoteThumbnail(Book book, int pageNumber) {
		Bitmap note_bitmap = Bitmap.createBitmap((int) 595, (int) 642, Bitmap.Config.ARGB_8888);
		
		if(book == null) {
			return note_bitmap;
		}

		
		Canvas note_canvas = new Canvas(note_bitmap);
		note_canvas.drawColor(Color.WHITE);
		
		ArrayList<Note> notes = app().data().database().notesDatabase().notesByNumber(book.id(), pageNumber);
		
		StringBuilder sb =  new StringBuilder();

        TextPaint text_paint = new TextPaint();
        text_paint.setColor(Color.BLACK);
        text_paint.setTextSize(25);
        text_paint.setTextAlign(Align.LEFT);
        text_paint.setAntiAlias(true);

        float y_pos = 0;
        int max_height = note_canvas.getHeight();
        int available_height = note_canvas.getHeight();

		for(int index = 0; index < notes.size(); index++) {
            Log.i(TAG, "Available Height: " + String.valueOf(available_height));
			Note note = notes.get(index);

            StaticLayout note_layout = new StaticLayout(note.content(),
                    text_paint,
                    note_canvas.getWidth(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1,
                    0,
                    false);

            int note_height = note_layout.getHeight();
            Log.i(TAG, "Note Height: " + String.valueOf(note_height));
            if(note_height <= available_height) {

                Log.i(TAG, "Printed note: " + String.valueOf(index));

                note_canvas.save();

                note_canvas.translate(0f, y_pos);

                note_layout.draw(note_canvas);
                note_canvas.restore();

                y_pos += note_layout.getHeight();
                available_height -= note_height;
            } else {
                Log.i(TAG, "Unable to print note: " + String.valueOf(index) + " note exceeded available space");
                break;
            }
		}
		
		return note_bitmap;
	}
	
	private static Bitmap generatePrintThumbnail(Bitmap pageBitmap, Bitmap noteBitmap, boolean color) {
		
		Bitmap thumb_bitmap = Bitmap.createBitmap((int) 842, (int) 595, Bitmap.Config.ARGB_8888);
		
		Canvas thumb_canvas = new Canvas(thumb_bitmap);
		thumb_canvas.drawColor(Color.WHITE);
		
		Paint paint = null;
		
		if(color) {
			paint = new Paint();
			Log.i(TAG, "Color");
		} else {
			Log.i(TAG, "Black and White");
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			paint = new Paint();
			paint.setColorFilter(new ColorMatrixColorFilter(cm));
		}
		
		thumb_canvas.drawBitmap(pageBitmap, 0, 0, paint);
		thumb_canvas.drawBitmap(noteBitmap, 842 / 2, 0, paint);
		
		//thumb_canvas.rotate(90);
		
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		
		return Bitmap.createBitmap(thumb_bitmap, 0, 0, thumb_bitmap.getWidth(), thumb_bitmap.getHeight(), matrix, true);
		
	}
	
	private static Bitmap generateColorThumbnail(Book book, int pageNumber, boolean showAnnotations) {
		Page page = app().data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		
		Annotation annotation = app().data().database().annotationsDatabase().annotation(book.id(), pageNumber);
		
		Bitmap page_thumbnail = getThumbnailFromPage(app().readHandle().path() + page.filePath(), false);
		
		if(annotation == null) {
			return page_thumbnail;
		}
		
		if(showAnnotations) {
			Bitmap annotation_thumbnail = generateAnnotationBitmap(annotation, page_thumbnail.getWidth(), page_thumbnail.getHeight());
			
			if(annotation_thumbnail == null) {
				return page_thumbnail;
			}
			
			Bitmap bitmap = Bitmap.createBitmap(page_thumbnail.getWidth(), page_thumbnail.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas page_canvas = new Canvas(bitmap);
			
			page_canvas.drawBitmap(page_thumbnail, 0, 0, null);
			page_canvas.drawBitmap(annotation_thumbnail, 0, 0, null);
			
			
			return bitmap;
		}
		
		return page_thumbnail;
	}
	
	private static Bitmap generateBWThumbnail(Book book, int pageNumber, boolean showAnnotations) {
		Page page = app().data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);

		Annotation annotation = app().data().database().annotationsDatabase().annotation(book.id(), pageNumber);
		  
		Bitmap page_thumbnail = getThumbnailFromPage(app().readHandle().path() + page.filePath(), false);
		
		Bitmap bitmap = Bitmap.createBitmap(page_thumbnail.getWidth(), page_thumbnail.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas page_canvas = new Canvas(bitmap);
        
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		
		page_canvas.drawBitmap(page_thumbnail, 0, 0, paint);
		
		if(annotation == null) {
			return bitmap;
		}
		
		if(showAnnotations) {
			Bitmap annotation_thumbnail = generateAnnotationBitmap(annotation, page_thumbnail.getWidth(), page_thumbnail.getHeight());
			
			page_canvas.drawBitmap(annotation_thumbnail, 0, 0, paint);
			
			return bitmap;
		}
		
		return bitmap;
	}
	
	private static Bitmap generatePagePrintThumbNail(Book book, int pageNumber, boolean showAnnotations) {
		Page page = app().data().database().pagesDatabase().pageByNumber(book.id(), pageNumber);
		    
		return getBitmapFromPage(app().readHandle().path() + page.filePath(), 1, showAnnotations);
	}
	
	public static class GeneratePageThumbnailTask extends AsyncTask<Integer, Integer, Bitmap> {

		private WeakReference<ImageView>	mImageReference;
		private Book 						mBook;
		private boolean						mColor = true;
		private boolean 					mShowAnnotations = false;
		
		public GeneratePageThumbnailTask(ImageView imageView, Book book, boolean showAnnotations) {
			mBook = book;
			mShowAnnotations = showAnnotations;
			mImageReference = new WeakReference<ImageView>(imageView);
		}
		
		public GeneratePageThumbnailTask(ImageView imageView, Book book,  boolean color, boolean showAnnotations) {
			mBook = book;
			mColor = color;
			mShowAnnotations = showAnnotations;
			mImageReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Bitmap doInBackground(Integer... params) {
			Bitmap bitmap = mThumbnailCache.getBitmap(mBook.id() + String.valueOf(params[0]));
			
			if(bitmap != null) {
				return bitmap;
			}
			
			if(mColor) {
				bitmap = generateColorThumbnail(mBook, params[0], mShowAnnotations); 
			} else {
				bitmap = generateBWThumbnail(mBook, params[0], mShowAnnotations); 
			}
				
			mThumbnailCache.addToCache(mBook.id() + String.valueOf(params[0]), bitmap);
			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			
			if(mImageReference != null && bitmap != null) {
				final ImageView image_view = mImageReference.get();
				
				if(image_view != null) {
					image_view.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	public static class GenerateNoteThumbnailTask extends AsyncTask<Integer, Integer, Bitmap> {

		private WeakReference<ImageView>	mImageReference;
		private Book 						mBook;
		private boolean						mColor = true;
		private boolean 					mShowAnnotations = false;
		
		public GenerateNoteThumbnailTask(ImageView imageView, Book book, boolean color, boolean showAnnotations) {
			mBook = book;
			mColor = color;
			mShowAnnotations = showAnnotations;
			
			mImageReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Bitmap doInBackground(Integer... params) {
			Bitmap bitmap = mPrintCache.getBitmap(mBook.id() + String.valueOf(params[0]));
			
			if(bitmap != null) {
				return bitmap;
			}
			
			Bitmap page_bitmap = generatePagePrintThumbNail(mBook, params[0], mShowAnnotations); 
			
			page_bitmap = Bitmap.createScaledBitmap(page_bitmap, 400, 600, false);
			
			Bitmap note_bitmap = mNoteCache.getBitmap(mBook.id() + String.valueOf(params[0]));
			
			if(note_bitmap == null) {
				note_bitmap = generateNoteThumbnail(mBook, params[0]);
			}
			
			bitmap = generatePrintThumbnail(page_bitmap, note_bitmap, mColor);
				
			mPrintCache.addToCache(mBook.id() + String.valueOf(params[0]), bitmap);
			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			
			if(mImageReference != null && bitmap != null) {
				final ImageView image_view = mImageReference.get();
				
				if(image_view != null) {
					image_view.setImageBitmap(bitmap);
				}
			}
		}
	}
}
