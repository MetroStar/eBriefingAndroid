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

package com.metrostarsystems.ebriefing.Activity.About;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.metrostarsystems.ebriefing.R;

public class ActivityAboutPagerAdapter extends FragmentStatePagerAdapter {

	private Map<Integer, FragmentAboutPage> mPageReferenceMap = new HashMap<Integer, FragmentAboutPage>();
	
	
	public ActivityAboutPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		int resourceid = 0;
		
		switch(position) {
			case 0: resourceid = R.drawable.about_1; break;
			case 1: resourceid = R.drawable.about_2; break;
			case 2: resourceid = R.drawable.about_3; break;
			case 3: resourceid = R.drawable.about_4; break;
		}
		
		FragmentAboutPage page = FragmentAboutPage.newInstance(resourceid);
		
		mPageReferenceMap.put(position, page);
		
		return page; 
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    super.destroyItem(container, position, object);
	    
	    mPageReferenceMap.remove(position);
	}

	@Override
	public int getCount() {
		return 4;
	}
	
	public FragmentAboutPage getFragment(int key) {
		return mPageReferenceMap.get(key);
	}
	

}
