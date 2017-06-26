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

package com.metrostarsystems.ebriefing.Dashboard.Policy;

import com.metrostarsystems.ebriefing.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;

public class FragmentPrivacyPolicy extends DialogFragment {

	public static final FragmentPrivacyPolicy newInstance() {
		FragmentPrivacyPolicy fragment = new FragmentPrivacyPolicy();
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		View view = inflater.inflate(R.layout.fragment_privacy_policy, null);
		
		WebView htmlWebView = (WebView) view.findViewById(R.id.webView_html);
		
		htmlWebView.loadUrl("file:///android_asset/privacy_policy.html");
		
		return view;
	}
}
