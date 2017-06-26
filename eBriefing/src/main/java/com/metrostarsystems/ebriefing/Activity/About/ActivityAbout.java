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

import com.metrostarsystems.ebriefing.R;
import com.viewpagerindicator.CirclePageIndicator;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityAbout extends FragmentActivity {

	private ActionBar 						mActionBar;
	
	private ViewPager 						mViewPager;
    private ActivityAboutPagerAdapter		mViewPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		mActionBar = getActionBar();
		mActionBar.hide();
		
		mViewPager = (ViewPager) findViewById(R.id.viewPager_about);
		mViewPagerAdapter = new ActivityAboutPagerAdapter(getSupportFragmentManager());
		
		mViewPager.setAdapter(mViewPagerAdapter);
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setFillColor(Color.parseColor("#FFFFFFFF"));
		indicator.setViewPager(mViewPager);
		
		Button getStartedButton = (Button) findViewById(R.id.button_started);
		getStartedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		SharedPreferences.Editor editor = getSharedPreferences("mainapplication", Context.MODE_PRIVATE).edit();
		editor.putBoolean("showabout", false);
		editor.commit();
		
	}
}
