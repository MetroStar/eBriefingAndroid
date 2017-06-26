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

package com.metrostarsystems.ebriefing.BookChapter;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.TypedValue;

public class ActivityChapterPagerAdapter extends FragmentPagerAdapter {
	
	private MainApplication mApp;
	private ActivityChapter mParent;
	private TypedValue mTypedValue = new TypedValue();

	public ActivityChapterPagerAdapter(FragmentManager fm, ActivityChapter parent) {
		super(fm);
		
		mApp = (MainApplication) parent.getApplicationContext();
		mParent = parent;
	}

	@Override
	public Fragment getItem(int index) {
		return mApp.chapterTabs().processTab(index);
	}

	@Override
	public int getCount() {
		return mApp.chapterTabs().total();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return mApp.chapterTabs().tabTitle(position);
	}
	
	@Override 
    public float getPageWidth(int position) {
	
		mParent.getResources().getValue(R.dimen.view_pager_offset, mTypedValue, true);
		float offset = mTypedValue.getFloat();
		
		if(position == 0) {
			return offset; 
		} else {
			return 1f;
		}
		
    }

}
