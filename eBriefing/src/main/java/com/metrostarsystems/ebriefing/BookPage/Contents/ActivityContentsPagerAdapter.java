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

package com.metrostarsystems.ebriefing.BookPage.Contents;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.metrostarsystems.ebriefing.MainApplication;

public class ActivityContentsPagerAdapter extends FragmentPagerAdapter {
	
	private MainApplication mApp;

	public ActivityContentsPagerAdapter(MainApplication app, FragmentManager fm) {
		super(fm);
		
		mApp = app;
	}

	@Override
	public Fragment getItem(int position) {
		return mApp.contentTabs().processTab(position);
	}

	@Override
	public int getCount() {
		return mApp.contentTabs().total();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return mApp.contentTabs().tabTitle(position);
		//return super.getPageTitle(position);
	}

}
