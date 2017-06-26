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

package com.metrostarsystems.ebriefing.BookPage;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.BookPage.Page.FragmentPage;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

public class ActivityPageViewPagerAdapter extends FragmentStatePagerAdapter {
	
	private static final String TAG = ActivityPageViewPagerAdapter.class.getSimpleName();
	
	private static SparseArray<FragmentPage> mPageReferenceMap;
	private Book mBook;
	private ActivityPage mParent;

	public ActivityPageViewPagerAdapter(FragmentManager fm, ActivityPage activity, Book book) {
		super(fm);
		
		mBook = book;
		
		mParent = activity;
		
		mPageReferenceMap = new SparseArray<FragmentPage>();
	}

	@Override
	public Fragment getItem(int position) {
		FragmentPage page;
		
		page = mPageReferenceMap.get(position);
		
		if(page == null) {
			page = FragmentPage.newInstance(mBook.id(), position + 1);
		}

		return page; 
	}
	
	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Object key = super.instantiateItem(container, position);


		mPageReferenceMap.put(position, (FragmentPage) key);
		
		return key;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    super.destroyItem(container, position, object);
	    
	    mPageReferenceMap.remove(position);
	}

	@Override
	public int getCount() {
		return mBook.pageCount();
	}
	
	public FragmentPage getFragment(int key) {
		return mPageReferenceMap.get(key);
	}
	
	@Override
	public float getPageWidth(int position) {
		
		if(mParent.mode() == PageMode.MODE_TWO_PAGE && mParent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return 0.5f;
		} else {
			return 1;
		}
	}

}
