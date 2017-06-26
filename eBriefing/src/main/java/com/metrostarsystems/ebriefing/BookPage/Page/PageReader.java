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

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.radaee.pdf.Document;
import com.radaee.reader.PDFReader;
import com.radaee.view.PDFVPage;
import com.radaee.view.PDFView;

public class PageReader extends PDFReader {
	
	private static final String TAG = PageReader.class.getSimpleName();
	
	private FragmentPage mParent;
	
	public static boolean SELECT = false;
	
	public PageReader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mParent != null) {
			if(mParent.orientationConfiguration() == Configuration.ORIENTATION_LANDSCAPE) {
				return super.onTouchEvent(event);
			} else {
				if(mParent.getUserVisibleHint()) {
					return super.onTouchEvent(event);
				}
			}
		}
//		} else {
//			return super.onTouchEvent(event);
//		}
		
		return false;
	}



	@Override
	public void OnPDFLongPressed(float x, float y) {
		super.OnPDFLongPressed(x, y);
		
//		SELECT = !SELECT;
//		PDFSetSelect();
	}
		
	@Override
	public boolean OnPDFSingleTapped(float x, float y) {
		
		//PDFVPage vpage = PDFGetView().vGetPage(0);
		
		//SELECT = false;
		//PDFSetSelect();
		
		//PDFGetView().vClearSel();
		//PDFGetView().vRenderAsync(vpage);

		//return super.OnPDFSingleTapped(x, y);
		
		return false;
	}
	
	
	
	@Override
	public void OnPDFPageDisplayed(Canvas canvas, PDFVPage vpage) {
		super.OnPDFPageDisplayed(canvas, vpage);
		
//		int x = vpage.GetX();
//    	int y = vpage.GetY();
//    	
//    	Log.i(TAG, "PDF Screen Position: x:" + String.valueOf(x) + " y: " + String.valueOf(y));
//    	
//    	int width = vpage.GetWidth();
//    	int height = vpage.GetHeight();
//    	
//    	Log.i(TAG, "PDF Page Size: w:" + String.valueOf(width) + " h: " + String.valueOf(height));
//    	
//    	if(mParent != null) {
//    		mParent.setPagePosition(x, y);
//    		mParent.setPageSize(width, height);
//    	}
	}



	public void setParent(FragmentPage page) {
		mParent = page;
	}

	
	public PDFView PDFGetView() {
		return m_view;
	}
	
	public Document PDFGetDocument() {
		return m_doc;
	}

}
