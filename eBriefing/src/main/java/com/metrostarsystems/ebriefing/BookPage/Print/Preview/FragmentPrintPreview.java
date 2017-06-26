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

import com.metrostarsystems.ebriefing.Data.Framework.Print.PageDocument;
import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Print.DialogFragmentPrint;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GenerateNoteThumbnailTask;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GeneratePageThumbnailTask;
import com.metrostarsystems.ebriefing.Data.Framework.Print.PrintDocument;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentPrintPreview extends Fragment {
	
	private static final String TAG = FragmentPrintPreview.class.getSimpleName();

	private MainApplication 			mApp;
	private View						mRootView;
	
	private PageDocument                mPageDocument;
	private Book						mBook;
	
	private ImageView					mPreviewImageView;
	
	public static FragmentPrintPreview newInstance(String bookId, PageDocument document) {
		FragmentPrintPreview fragment = new FragmentPrintPreview();
        fragment.setDocument(document);
        Bundle args = new Bundle();
        args.putString("bookid", bookId);
        fragment.setArguments(args);
        
        return fragment;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		String book_id = getArguments().getString("bookid");
		
		Log.i(TAG, "Page Number: " + String.valueOf(mPageDocument.pageNumber()));
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		mBook = mApp.data().database().booksDatabase().book(book_id);
		
			
		mRootView = inflater.inflate(R.layout.fragment_print_preview, null);
		
		mPreviewImageView = (ImageView) mRootView.findViewById(R.id.imageView_preview);

        mPreviewImageView.setImageBitmap(mPageDocument.generateBitmap());

		return mRootView;
	}

    private void setDocument(PageDocument document) {
        mPageDocument = document;
    }
	
	public void refresh(PageDocument document) {

        mPageDocument = document;

        if(mPreviewImageView != null) {
            mPreviewImageView.setImageBitmap(mPageDocument.generateBitmap());
        }
	}
}
