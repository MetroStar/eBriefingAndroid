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

package com.metrostarsystems.ebriefing.Services.PrintService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages;
import com.metrostarsystems.ebriefing.Data.Framework.Print.PageDocument;
import com.metrostarsystems.ebriefing.Data.Framework.Print.PrintDocument;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.print.PrintHelper;


public class PrintService extends PrintDocumentAdapter implements ProcessPDFDocumentFinishedListener {
	
	private static final String TAG = PrintService.class.getSimpleName();
	
	private static final int MILS_IN_INCH = 1000;
	
	private Book mBook;
	private Context mContext;
	private PrintedPdfDocument mPdfDocument;
	private PrintAttributes mPrintAttributes;
	private PrintDocumentInfo mDocumentInfo;
	private Context mPrintContext;
	
	private static int mContentWidth;
	private static int mContentHeight;
	
	private PrintDocument		mDocument;
	
	private ParcelFileDescriptor mDestination;
	private WriteResultCallback mWriteCallback;

	
    @Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onLayout(PrintAttributes oldAttributes,
			PrintAttributes newAttributes,
			CancellationSignal cancellationSignal,
			LayoutResultCallback callback, Bundle extras) {
		
		// Respond to cancellation request
	    if(cancellationSignal.isCanceled() ) {
	        callback.onLayoutCancelled();
	        return;
	    }
	    
	 // Now we determined if the print attributes changed in a way that
        // would change the layout and if so we will do a layout pass.
//        boolean layoutNeeded = false;
//	    
	    final int density = Math.max(newAttributes.getResolution().getHorizontalDpi(),
                newAttributes.getResolution().getVerticalDpi());
//	    
//	 // The content width is equal to the page width minus the margins times
//        // the horizontal printer density. This way we get the maximal number
//        // of pixels the printer can put horizontally.
        final int marginLeft = (int) (density * (float) newAttributes.getMinMargins()
                .getLeftMils() / MILS_IN_INCH);
        final int marginRight = (int) (density * (float) newAttributes.getMinMargins()
                .getRightMils() / MILS_IN_INCH);
        mContentWidth = (int) (density * (float) newAttributes.getMediaSize()
               .getWidthMils() / MILS_IN_INCH) - marginLeft - marginRight;
//        // The content height is equal to the page height minus the margins times
//        // the vertical printer resolution. This way we get the maximal number
//        // of pixels the printer can put vertically.
        final int marginTop = (int) (density * (float) newAttributes.getMinMargins()
                .getTopMils() / MILS_IN_INCH);
        final int marginBottom = (int) (density * (float) newAttributes.getMinMargins()
                .getBottomMils() / MILS_IN_INCH);
        mContentHeight = (int) (density * (float) newAttributes.getMediaSize()
                .getHeightMils() / MILS_IN_INCH) - marginTop - marginBottom;

	    
	    // Stash the attributes as we will need them for rendering.
        mPrintAttributes = newAttributes;
       
	    
	    if(mDocument.size() > 0) {
	    	// Return print information to print framework
	        PrintDocumentInfo info = new PrintDocumentInfo
	                .Builder("print_output.pdf")
	                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
	                .setPageCount(mDocument.size())
	                .build();
	        
	        mDocumentInfo = info;
	        
	        if(mDocumentInfo == null) {
	        	return;
	        }
	        
	        // Content layout reflow is complete
	        callback.onLayoutFinished(mDocumentInfo, false);
	    } else {
	    	// Otherwise report an error to the print framework
	        callback.onLayoutFailed("Page count calculation failed.");
	        return;
	    }
	}

	

	@Override
	public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
			CancellationSignal cancellationSignal, final WriteResultCallback callback) {
		
		// If we are already cancelled, don't do any work.
        if(cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            return;
        }

        mDestination = destination;
        mWriteCallback = callback;
        
        mPdfDocument = new PrintedPdfDocument(mContext, mPrintAttributes);

        new ProcessPDFDocumentTask(mBook, this, mPdfDocument, mDocument).execute();
		
		
	}

	
	private PrintService(Builder build) {
		mContext = build.mContext;
		mDocument = build.mDocument;
		mBook = build.mBook;
	}
	
	
	public static class Builder {

		private Book mBook;
		private Context mContext;
		private PrintDocument mDocument;
		
		public Builder(Context context, Book book) {
			mContext = context;
			mBook = book;
		}
		
		public Builder addDocument(PrintDocument document) {
			mDocument = document;
			
			
			return this;
		}
		
		public PrintService build() {
			return new PrintService(this);
		}
		
	}
	
	private static class ProcessPDFDocumentTask extends AsyncTask<Void, Void, Boolean> {

		private ProcessPDFDocumentFinishedListener mListener;
		private Book		   mBook;
		private PrintedPdfDocument mPDFDocument;
		private PrintDocument		mDocument;
		
		public ProcessPDFDocumentTask(Book book, ProcessPDFDocumentFinishedListener listener, PrintedPdfDocument pdfDocument, PrintDocument document) {
			mListener = listener;
			mBook = book;
			mPDFDocument = pdfDocument;
			mDocument = document;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			for(int index = 0; index < mDocument.size(); index++) {

                PageDocument page_document = mDocument.page(index);
				
				int page_number = page_document.pageNumber();
				
				if(page_number > 0) {
					Bitmap bitmap = page_document.generateBitmap();

					PageInfo page_info = new PageInfo.Builder(bitmap.getWidth(), 
															  bitmap.getHeight(), 
															  page_number).create();
					
					Page page = mPDFDocument.startPage(page_info);
					Canvas page_canvas = page.getCanvas();
					
					page_canvas.drawBitmap(bitmap, 0, 0, PageDocument.mPaint);
					
					
					
					mPDFDocument.finishPage(page);
					bitmap.recycle();
				}
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			mListener.OnProcessPDFDocumentFinished(mPDFDocument);
		}
		
		
		
	}

	@Override
	public void OnProcessPDFDocumentFinished(PrintedPdfDocument document) {
		try {
			document.writeTo(new FileOutputStream(mDestination.getFileDescriptor()));
		} catch(IOException e) {
			mWriteCallback.onWriteFailed(e.toString());
			return;
		} finally {
			document.close();
		}
		
		mWriteCallback.onWriteFinished(new PageRange[] {PageRange.ALL_PAGES});
	}

}
