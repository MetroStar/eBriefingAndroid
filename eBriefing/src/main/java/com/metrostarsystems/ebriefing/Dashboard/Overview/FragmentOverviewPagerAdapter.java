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

package com.metrostarsystems.ebriefing.Dashboard.Overview;

import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class FragmentOverviewPagerAdapter extends FragmentPagerAdapter {

	private Book mBook;
	
	private FragmentOverviewDescription mOverviewDescription;
	private FragmentOverviewChapters mOverviewChapters;
	
	public FragmentOverviewPagerAdapter(FragmentManager fm, Book book) {
		super(fm);

		mBook = book;
		
		mOverviewDescription = FragmentOverviewDescription.newInstance(mBook.id());
		mOverviewChapters = FragmentOverviewChapters.newInstance(mBook.id());
	}



	@Override
	public Fragment getItem(int index) {
		switch(index) {
			case 0:	return mOverviewDescription;
			case 1:	return mOverviewChapters;
		}
		
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Description";
            case 1:
                return "Chapters";
        }
        
        return null;
    }
}
