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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.metrostarsystems.ebriefing.R;

public class FragmentAboutPage extends Fragment {

	private int							mImageResource;

	public static FragmentAboutPage newInstance(int imageResource) {
		FragmentAboutPage fragment = new FragmentAboutPage();
		Bundle args = new Bundle();
		args.putInt("imageresource", imageResource);
	    fragment.setArguments(args);
        
        return fragment;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        mImageResource = getArguments().getInt("imageresource", 0);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_about_page, null);
		
		ImageView imageImageView = (ImageView) rootView.findViewById(R.id.imageView_image);
		
		imageImageView.setImageResource(mImageResource);
		
		return rootView;
	}

}
