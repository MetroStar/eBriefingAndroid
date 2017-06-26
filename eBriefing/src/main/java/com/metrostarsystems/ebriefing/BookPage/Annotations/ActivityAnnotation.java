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

package com.metrostarsystems.ebriefing.BookPage.Annotations;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.Page.PageReader;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Matrix;
import com.radaee.reader.PDFReader;
import com.radaee.reader.PDFReader.PDFReaderListener;
import com.radaee.util.PDFFileStream;
import com.radaee.view.PDFVPage;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityAnnotation extends FragmentActivity implements PDFReaderListener {

	private static final String TAG = ActivityAnnotation.class.getSimpleName();
	
	private MainApplication				mApp;
	private Book						mBook;
	private Page						mPage;
	private Annotation					mAnnotation;
	
	private PageReader 					mPDF;
	private Document 					mPDFDocument;
	private PDFFileStream				mPDFStream;
	
	private AnnotationSurface			mAnnotationSurface;
	private TextView					mPageNumberTextView;
	
	private String						mFilePath = "";
	private String 						mCache = "";
	private boolean						mSetCache = false;
	
	private ActivityAnnotationToolBar	mToolBar;
	
	private LinearLayout				mAnnotationLayout;
	
//	private int							mPagePositionX = 0;
//	private int							mPagePositionY = 0;
//	
//	private int							mPageWidth = 0;
//	private int							mPageHeight = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_annotation);
		
		mAnnotationLayout = (LinearLayout) findViewById(R.id.linearLayout_annotation);
		
		mApp = (MainApplication) getApplicationContext();
		
		processIntent(getIntent());
		
		mAnnotationSurface = (AnnotationSurface) findViewById(R.id.surface_annotation);
		mPageNumberTextView = (TextView) findViewById(R.id.textView_page_number);
		
		mPDF = (PageReader) findViewById(R.id.reader_pdf);

		openDocument();
		
		openAnnotation();
		
		mToolBar.activateAnnotationMode();
	}
	
	private void processIntent(Intent intent) {
		 if(intent.hasExtra("bookid") && intent.hasExtra("pagenumber")) {
			String book_id = intent.getStringExtra("bookid");
			int page_number = intent.getIntExtra("pagenumber", 1);
			
			mBook = mApp.data().database().booksDatabase().book(book_id);
			mPage = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), page_number);
	
			
			mToolBar = new ActivityAnnotationToolBar(this);
			
//			if(intent.hasExtra("pagepositionx") && intent.hasExtra("pagepositiony") &&
//					 intent.hasExtra("pagewidth") && intent.hasExtra("pageheight")) {
//				 mPagePositionX = intent.getIntExtra("pagepositionx", 0);
//				 mPagePositionY = intent.getIntExtra("pagepositiony", 0);
//				 mPageWidth = intent.getIntExtra("pagewidth", 0);
//				 mPageHeight = intent.getIntExtra("pageheight", 0);
//			 }
		} else {
			finish();
		}
		 
		 
	}
	
	private void openDocument() {
		mPDFDocument = new Document();
		
	    mPDFDocument.Close();

	    mPDFStream = new PDFFileStream();
	    
	    mFilePath = mApp.readHandle().path() + mPage.filePath();
	    
	    if(!mFilePath.isEmpty()) {
		    
	        mPDFStream.open(mFilePath);
	        
	        int ret = mPDFDocument.OpenStream(mPDFStream, null);
	        
	        mCache = mBook.id() + String.valueOf(mPage.pageNumber()) + "temp.dat";
	        
	        mSetCache = mPDFDocument.SetCache(Global.tmp_path + "/" + mCache);
	        
	        if(Settings.DEBUG && !mSetCache) {
		    	Utilities.displayToast(mApp, "SetCache: failed page " + mPage.pageNumber());
		    }
	        
	        switch( ret ) {
	            case -1://need input password
	            	finish();
	                break;
	            case -2://unknown encryption
	            	finish();
	                break;
	            case -3://damaged or invalid format
	            	finish();
	                break;
	            case -10://access denied or invalid file path
	            	finish();
	                break;
	            case 0://succeeded, and continue
	                break;
	            default://unknown error
	            	finish();
	                break;
	        }
	    }
	    
	   
	    mPDF.PDFOpen(mPDFDocument, false, this); 
	    
	    if(configurationOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
	    	mPDF.PDFSetView(3);
	    }
	}
	
	public PDFReader reader() { return mPDF; }
	
	public Document document() { return mPDFDocument; }
	
	public int pagePositionX() {
		
		PDFVPage vpage = mPDF.PDFGetView().vGetPage(0);
		
		return vpage.GetX();
		//return mPagePositionX;
	}
	
	public int pagePositionY() {
		
		PDFVPage vpage = mPDF.PDFGetView().vGetPage(0);
		
		return vpage.GetY();
		//return mPagePositionY;
	}
	
	public int pageWidth() {
		
		PDFVPage vpage = mPDF.PDFGetView().vGetPage(0);
		
		return (int) vpage.GetWidth();
//		return mPageWidth;
	}
	
	public int pageHeight() {
		
		PDFVPage vpage = mPDF.PDFGetView().vGetPage(0);
		
		return (int) vpage.GetHeight();
//		return mPageHeight;
	}
	
	public PageReader pdfReader() {
		return mPDF;
	}
	
	public com.radaee.pdf.Page page() { return mPDFDocument.GetPage(0); }
	
	public void setScale(float scale, float fx, float fy) {
		
		
		
		mPDF.PDFGetView().vSetScale(scale, fx, fy);
		
//		Log.i(TAG, "scale: " + String.valueOf(scale) + "pdf scale: " + mPDF.PDFGetView().vGetScale());
	}
	
	public Book book() {
		return mBook;
	}
	
	public LinearLayout annotationLayout() {
		return mAnnotationLayout;
	}
	
	public void openAnnotation() {
//		page().ObjsStart();
//		Bitmap bitmap = null;
		
//		if(page().GetAnnotCount() > 0 && page().GetAnnot(0).RenderToBmp(bitmap)) {
//			mAnnotationSurface.setSurface(bitmap);
//		}
		
		
		
		if(mApp.data().database().annotationsDatabase().has(mPage.bookId(), mPage.pageNumber())) {
			
			mAnnotation = mApp.data().database().annotationsDatabase().annotation(mPage.bookId(), mPage.pageNumber());
			
			mAnnotationSurface.setInkAnnotations(mAnnotation);
		} else {
			mAnnotationSurface.setInkAnnotations(null);
		}
		
		mAnnotationSurface.setParent(this);
		
		clearAnnotations();
		
	}
	
	public void refresh() {
		page().Close();

		mPDF.PDFSave();
		mPDF.PDFClose();
		openDocument();
	}
	
	public void save() {
		if(mPDF != null) {
			
			if(!mAnnotationSurface.inkAnnotations().isEmpty() && 
					mAnnotationSurface.getWidth() > 0 &&
					mAnnotationSurface.getHeight() > 0) {
				float[] rect = new float[4];
				rect[0] = mAnnotationSurface.getWidth(); 
				rect[1] = 0;
				rect[2] = 0;
				rect[3] = mAnnotationSurface.getHeight();
				
				if(configurationOrientation() == Configuration.ORIENTATION_PORTRAIT) {
					PDFVPage vpage = mPDF.PDFGetView().vGetPage(0);
					Matrix mat = vpage.CreateInvertMatrix(mPDF.PDFGetView().vGetX(), mPDF.PDFGetView().vGetY());
					mat.TransformRect(rect);
				}
				
				//com.radaee.pdf.Page page = vpage.GetPage();
				com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
				page.ObjsStart();
				
				int width = mAnnotationSurface.getWidth();
				int height = mAnnotationSurface.getHeight();
				
				Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				
//				Bitmap saveBitmap = Bitmap.createBitmap(map);
                Canvas c = new Canvas(bitmap);
                c.drawColor(Color.TRANSPARENT);
                
                for(InkAnnotation ink : mAnnotationSurface.inkAnnotations()) {
                	ink.draw(c, ink.paint());
                }
                
                c.drawBitmap(bitmap,0,0,null);
				
				if(page.AddAnnotBitmap(bitmap, true, rect)) {
					
					if(mAnnotation == null) {
						Annotation new_annotation = new Annotation.Builder()
															.id()
															.bookId(mBook.id())
															.bookVersion(mBook.bookVersion())
															.chapterId(mPage.chapterId())
															.pageId(mPage.id())
															.pageNumber(mPage.pageNumber())
															.platform(ServerConnection.PLATFORM_ANDROID)
															.inkAnnotation(width, height, mAnnotationSurface.inkAnnotations())
															.isSynced(false)
															.isNew(true)
															.build();
					
						mApp.data().database().annotationsDatabase().insert(new_annotation);
					} else {
						Annotation update_annotation = new Annotation.Builder()
															.fromAnnotation(mAnnotation)
															.inkAnnotation(width, height, mAnnotationSurface.inkAnnotations())
															.isSynced(false)
															.isNew(false)
															.build();
									
						mApp.data().database().annotationsDatabase().update(update_annotation);
					}

					bitmap.recycle();
					
					refresh();
				}
			} else {

                if(mAnnotation != null) {
                    Annotation remove_annotation = new Annotation.Builder()
                                                    .fromAnnotation(mAnnotation)
                                                    .inkAnnotation(mAnnotation.width(), mAnnotation.height(), mAnnotation.inkAnnotation())
                                                    .isRemoved(true)
                                                    .isSynced(false)
                                                    .isNew(false)
                                                    .build();

                    // Remove the annotation from the PDF
    //				removeInkAnnotationsFromPDF();
                    // Remove the annotation from the database
                    mApp.data().database().annotationsDatabase().update(remove_annotation);
                }

				refresh();
			}
		}
	}
	
	public void cancel() {
		
//		mBook.showInkAnnotationsInPDF(mPageNumber);
		
		//refresh();
		
		if(mPDF != null) {

			
			if(mApp.data().database().annotationsDatabase().has(mPage.bookId(), mPage.pageNumber())) {
				
				Annotation annotation = mApp.data().database().annotationsDatabase().annotation(mPage.bookId(), mPage.pageNumber());
				
				com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
				
				float width = mPDFDocument.GetPageWidth(0);
				float height = mPDFDocument.GetPageHeight(0);
				
				float[] rect = new float[4];
				rect[0] = width; 
				rect[1] = 0;
				rect[2] = 0;
				rect[3] = height;
				
				page.ObjsStart();
				
				Bitmap annotation_bitmap = Bitmap.createBitmap((int) annotation.width(), (int) annotation.height(), Bitmap.Config.ARGB_8888);
	        	
	        	Canvas c = new Canvas(annotation_bitmap);
	            c.drawColor(Color.TRANSPARENT);
	        
		        for(InkAnnotation ink : annotation.inkAnnotation()) {
		        	ink.draw(c, ink.paint());
		        }
		        
		        c.drawBitmap(annotation_bitmap,0,0,null);
		        
		        annotation_bitmap = Bitmap.createScaledBitmap(annotation_bitmap, 
		        											(int) width, 
		        											(int) height, false);
		        
				if(page.AddAnnotBitmap(annotation_bitmap, true, rect)) {
					mApp.data().imageManager().clearThumbnails();
					annotation_bitmap.recycle();
					
					refresh();
					
				}
			}
		}
	}
	
	public void updateUndoRedoTool() {
		mToolBar.updateUndoRedoTool();
	}
	
	public void undo() {
		mAnnotationSurface.undo();
	}
	
	public void redo() {
		mAnnotationSurface.redo();
	}
	
	public boolean canUndo() {
		return mAnnotationSurface.canUndo();
	}
	
	public boolean canRedo() {
		return mAnnotationSurface.canRedo();
	}
	
	public void clear() {
		mAnnotationSurface.clear();
	}
	
	
	
	

	public void clearAnnotations() {
		//save();

		//hideInkAnnotationsInPDF();
		removeInkAnnotationsFromPDF();
		
		refresh();
	}
	
	public int totalAnnotations() {
		com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
		
		page.ObjsStart();
		
		return page.GetAnnotCount();
	}
	
//	public void hideInkAnnotationsInPDF() {
//		com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
//
//		page.ObjsStart();
//
//		int count = page.GetAnnotCount();
//		for(int index = 0; index < count; index++) {
//			com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(0);
//			annotation.SetHide(true);
//		}
//	}
//	
//	public void showInkAnnotationsInPDF() {
//		com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
//
//		page.ObjsStart();
//
//		int count = page.GetAnnotCount();
//		for(int index = 0; index < count; index++) {
//			com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(0);
//			annotation.SetHide(false);
//		}
//	}
	
	public void removeInkAnnotationsFromPDF() {
		com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
		
		page.ObjsStart();
		
		int count = page.GetAnnotCount();
		for(int index = 0; index < count; index++) {
			com.radaee.pdf.Page.Annotation annotation = page.GetAnnot(0);
			annotation.RemoveFromPage();
		}
	}
	
	public int configurationOrientation() { 
		return getResources().getConfiguration().orientation;
	}

	@Override public void OnPageModified(int pageno) { }
	@Override public void OnPageChanged(int pageno) { }
	@Override public void OnAnnotClicked(PDFVPage vpage, com.radaee.pdf.Page.Annotation annot) { }
	@Override public void OnSelectEnd(String text) { }
	@Override public void OnOpenURI(String uri) { }
	@Override public void OnOpenMovie(String path) { }
	@Override public void OnOpenSound(int[] paras, String path) { }
	@Override public void OnOpenAttachment(String path) { }
	@Override public void OnOpen3D(String path) { }
	@Override public void OnOpenJS(String js) { }
}
