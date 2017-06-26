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

package com.metrostarsystems.ebriefing.BookPage.Page;

import java.io.UnsupportedEncodingException;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.BookPage.Bookmarks.BookmarkImageView;
import com.metrostarsystems.ebriefing.Data.Framework.BookUtilities;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.reader.PDFReader;
import com.radaee.reader.PDFReader.PDFReaderListener;
import com.radaee.util.PDFFileStream;
import com.radaee.view.PDFVPage;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentPage extends Fragment implements PDFReaderListener {
	
	private static final String TAG = FragmentPage.class.getSimpleName();
	
	private MainApplication				mApp;	
	private ActivityPage				mParent;
	private FragmentPage				mPage;
	
	private PageReader 					mPDF;
	private Document 					mPDFDocument;
	private PDFFileStream				mPDFFileStream;
	private PDFObscuredStream			mPDFObscuredStream;

	private BookmarkImageView 			mBookmarkImageView;
	private TextView					mPageNumberTextView;
	
	private Book						mBook;
	private Annotation				    mAnnotation;
	private String						mBookId;
	private int							mPageNumber = 0;
	private String						mFilePath = "";
	
	private String 						mCache = "";
	private boolean						mSetCache = false;
	
	public static FragmentPage newInstance(String bookId, int pageNumber) {
		FragmentPage fragment = new FragmentPage();
        Bundle args = new Bundle();
        args.putInt(Tags.PAGE_NUMBER_TAG, pageNumber);
        args.putString(Tags.BOOK_ID_TAG, bookId);
        fragment.setArguments(args);
        
        return fragment;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mPage = this;
		
		mParent = (ActivityPage) getActivity();
		
		mBookId = getArguments().getString(Tags.BOOK_ID_TAG);
        mPageNumber = getArguments().getInt(Tags.PAGE_NUMBER_TAG, 0);

        Log.i(TAG, "Page Number: " + String.valueOf(mPageNumber));
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		if(!Settings.ENABLE_ENCRYPT_BOOK_DATA) {
			mPDFFileStream = new PDFFileStream();
			mPDFObscuredStream = null;
		} else {
			mPDFFileStream = null;
			mPDFObscuredStream = new PDFObscuredStream(mApp);
		}
		
		
		mBook = mApp.data().database().booksDatabase().book(mBookId);
		
		BookUtilities.removeInkAnnotationsFromPDF(mApp, mBook, mPageNumber);
			
		View view = inflater.inflate(R.layout.fragment_page_reader, null);

		mBookmarkImageView = (BookmarkImageView) view.findViewById(R.id.imageView_bookmark);
        Log.i(TAG, "Page Number: " + String.valueOf(mPageNumber));
		mBookmarkImageView.initialize(mParent, mPageNumber);
		
		mPageNumberTextView = (TextView) view.findViewById(R.id.textView_page_number);
		
		mPDF = (PageReader) view.findViewById(R.id.reader_pdf);
		mPDF.setParent(this);
		
		openDocument();
		
		if(mSetCache && mApp.data().database().annotationsDatabase().has(mBookId, mPageNumber)) {
			
			mAnnotation = mApp.data().database().annotationsDatabase().annotation(mBookId, mPageNumber);
    
			com.radaee.pdf.Page page = mPDFDocument.GetPage(0);
		
			float width = mPDFDocument.GetPageWidth(0);
			float height = mPDFDocument.GetPageHeight(0);

			
			float[] rect = new float[4];
			rect[0] = width; 
			rect[1] = 0;
			rect[2] = 0;
			rect[3] = height;
			
			page.ObjsStart();
			
			Bitmap annotation_bitmap = Bitmap.createBitmap((int) mAnnotation.width(), (int) mAnnotation.height(), Bitmap.Config.ARGB_8888);
			
			Canvas annotation_canvas = new Canvas(annotation_bitmap);
			annotation_canvas.drawColor(Color.TRANSPARENT);
        
	        for(InkAnnotation ink : mAnnotation.inkAnnotation()) {
	        	ink.draw(annotation_canvas, ink.paint());
	        }
	        
	        annotation_canvas.drawBitmap(annotation_bitmap, 0, 0, null);
			
			if(page.AddAnnotBitmap(annotation_bitmap, true, rect)) {
				mApp.data().imageManager().clearThumbnails();
				annotation_bitmap.recycle();
				
				refresh();
				
			}
			
			
		}
		
	    mPageNumberTextView.setText(String.valueOf(mPageNumber));

		return view;
	}
	
	public BookmarkImageView bookmarkImageView() {
		return mBookmarkImageView;
	}

	public void openDocument() {
		mPDFDocument = new Document();
		
	    mPDFDocument.Close();
	    
	    
	   
	    Page page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), mPageNumber);
	    
	    StringBuilder sb = new StringBuilder();
	    
	    sb.append(mApp.serverConnection().fileReadHandle().path());
	    sb.append(page.filePath());
	    
	    mFilePath = sb.toString();
	    
	    if(!mFilePath.isEmpty()) {
		    
			int ret = -3;
			
	    	if(mPDFFileStream != null) {
	    		mPDFFileStream.open(mFilePath);
	        
	    		ret = mPDFDocument.OpenStream(mPDFFileStream, null);
	    	} else {
	    		try {
					mPDFObscuredStream.open(mFilePath, Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID).getBytes("UTF-8"));
	    		} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
	    		
	    		ret = mPDFDocument.OpenStream(mPDFObscuredStream, null);
	    	}
	    	
	    	
    		
    		switch( ret ) {
	            case -1://need input password
	            	getActivity().finish();
	                break;
	            case -2://unknown encryption
	            	getActivity().finish();
	                break;
	            case -3://damaged or invalid format
	            	getActivity().finish();
	                break;
	            case -10://access denied or invalid file path
	            	getActivity().finish();
	                break;
	            case 0://succeeded, and continue
	                break;
	            default://unknown error
	            	getActivity().finish();
	                break;
    		}
	        
	        mCache = mBook.id() + String.valueOf(mPageNumber) + "temp.dat";
	        
	        mSetCache = mPDFDocument.SetCache(Global.tmp_path + "/" + mCache);
	        
	        if(Settings.DEBUG && !mSetCache) {
		    	Utilities.displayToast(mApp, "SetCache: failed page " + mPageNumber);
		    }
	        
	        
	    }
	    
	   
	    mPDF.PDFOpen(mPDFDocument, false, this);
	    
	    if(mParent.configurationOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
	    	mPDF.PDFSetView(3);	
	    }

	}
	
	public int orientationConfiguration() {
		return mParent.configurationOrientation();
	}
	
	public Book book() {
		return mBook;
	}
	
	public int pageNumber() {
		return mPageNumber;
	}
	
	public Annotation annotation() {
		return mAnnotation;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		if(mPDF != null) {
			mPDF.PDFSave();
		} else {
			openDocument();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		openDocument();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onStop() {
		super.onStop();
		
		if(mPDF != null) {
			mPDF.PDFSave();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		close();
	}

	public PDFReader reader() { return mPDF; }

	public Document document() { return mPDFDocument; }
	
	public com.radaee.pdf.Page page() { return mPDFDocument.GetPage(0); }
	
	private void close() {
		mPDF.PDFSave();
		mPDF.PDFClose();
		
		if(mPDFDocument != null) {
			mPDFDocument.Close();
			mPDFDocument = null;
		}
		
		
		if(mPDFFileStream != null) {
			mPDFFileStream.close();
			mPDFFileStream = null;
		}
		
		if(mPDFObscuredStream != null) {
			mPDFObscuredStream.close();
			mPDFObscuredStream = null;
		}
	}
	
	public void refresh() {
		page().Close();

		mPDF.PDFSave();
		mPDF.PDFClose();
		openDocument();
	}
	
	public void showBookmark() {
		mBookmarkImageView.setVisibility(View.VISIBLE);
	}

	public void hideBookmark() {
		mBookmarkImageView.setVisibility(View.GONE);
	}



    public void updateBookmarkImage() {

        mBookmarkImageView.updateImage();
    }

	@Override public void OnPageModified(int pageno) { }
	@Override public void OnPageChanged(int pageno) { }

	@Override
	public void OnAnnotClicked(PDFVPage vpage, com.radaee.pdf.Page.Annotation annot) {

	}

	@Override 
	public void OnSelectEnd(String text) { 

	}
	@Override public void OnOpenURI(String uri) { }
	@Override public void OnOpenMovie(String path) { }
	@Override public void OnOpenSound(int[] paras, String path) { }
	@Override public void OnOpenAttachment(String path) { }
	@Override public void OnOpen3D(String path) { }
	@Override public void OnOpenJS(String js) { }
}
