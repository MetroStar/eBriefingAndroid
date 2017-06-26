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

package com.metrostarsystems.ebriefing.BookPage.Print.Preview;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Print.PageDocument;
import com.metrostarsystems.ebriefing.Data.Framework.Print.PrintDocument;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

public class PrintPreviewPagerAdapter extends FragmentStatePagerAdapter {
	
	private static final String TAG = PrintPreviewPagerAdapter.class.getSimpleName();
	
	private SparseArray<FragmentPrintPreview> mPageReferenceMap;
	
	private Book mBook;
	
	private PrintDocument mDocument;

	public PrintPreviewPagerAdapter(FragmentManager fm, Book book, PrintDocument document) {
		super(fm);

		mBook = book;
		mPageReferenceMap = new SparseArray<FragmentPrintPreview>();
        mDocument = document;
	}

	@Override
	public Fragment getItem(int position) {
		FragmentPrintPreview page;
		
		page = mPageReferenceMap.get(position);
		
		if(page == null) {
			page = FragmentPrintPreview.newInstance(mBook.id(), mDocument.page(position));
		}

		return page; 
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Object key = super.instantiateItem(container, position);

		mPageReferenceMap.put(position, (FragmentPrintPreview) key);
		
		return key;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    super.destroyItem(container, position, object);
	    
	    mPageReferenceMap.remove(position);
	}

    @Override
	public int getCount() {
		return mDocument.size();
	}
	
	public FragmentPrintPreview getFragment(int key) {
		return mPageReferenceMap.get(key);
	}
	
	public void refresh(PrintDocument document) {
        Log.i(TAG, "Refreshed");
		mDocument = document;

        for(int index = 0; index < document.size(); index++) {
            PageDocument page = document.page(index);

            if(mPageReferenceMap.get(index) != null) {
                FragmentPrintPreview preview = mPageReferenceMap.get(index);
                preview.refresh(page);
            }
        }

        notifyDataSetChanged();
	}
}
