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

package com.metrostarsystems.ebriefing.BookChapter.TabAnnotations;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookChapter.ActivityChapter;
import com.metrostarsystems.ebriefing.BookChapter.Tab.ChapterTabs.ChapterTab;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Database.AnnotationsDatabase.AnnotationsDatabaseChangedListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentAnnotations extends AbstractPagerFragment implements AnnotationsDatabaseChangedListener {

	private MainApplication			mApp;
	private GridView 				mGridView;
	private FragmentAnnotationsGridAdapter 	mGridAdapter;
	
	private Book				mBook;
	
	private int						mSelectedPosition;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		View rootView = inflater.inflate(R.layout.annotation_grid, null);
		
		LinearLayout emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty);
		TextView emptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
		emptyTextView.setText("There are no annotations to display!");
		
		mGridView = (GridView) rootView.findViewById(R.id.gridView_annotations);

		mGridView.setEmptyView(emptyLayout);

		mBook = ((ActivityChapter) getActivity()).book();
		
		mApp.data().database().annotationsDatabase().addListener(this);

		mGridAdapter = new FragmentAnnotationsGridAdapter(rootView.getContext(), ChapterTab.TAB_ANNOTATIONS, mBook);
		((ActivityChapter) getActivity()).update();
		
		mGridView.setAdapter(mGridAdapter);
		
		
		mGridView.setOnItemClickListener(new OnAnnotationClickListener());
		
		registerForContextMenu(mGridView);
		
		
		return rootView;
	}
	
	@Override
	public void OnAnnotationsDatabaseChangedListener(Annotation annotation) {
		if(annotation != null && mGridAdapter != null) {
			mApp.data().imageManager().clearThumbnails();
			mGridAdapter.refresh(mBook);
			mGridAdapter.notifyDataSetChanged();
		}
		((ActivityChapter) getActivity()).update();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().annotationsDatabase().removeListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    mSelectedPosition = info.position;
	    
	    getActivity().getMenuInflater().inflate(R.menu.annotations_context_menu, menu);

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.remove_annotation:
				Annotation annotation = mGridAdapter.getItem(mSelectedPosition);

				
				Annotation remove_annotation = new Annotation.Builder()
																.fromAnnotation(annotation)
																.inkAnnotation(annotation.width(), annotation.height(), annotation.inkAnnotation())
																.isRemoved(true)
																.isSynced(false)
																.isNew(false)
																.build();
				
				mApp.data().database().annotationsDatabase().update(remove_annotation);
				mApp.data().imageManager().clearThumbnails();
				break;
		}

		return super.onContextItemSelected(item);             
	}
	
	
	
	private class OnAnnotationClickListener implements OnItemClickListener {
		
		public OnAnnotationClickListener() {
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Annotation annotation = mGridAdapter.getItem(position);
			
			Book.generateBookPageIntent((Activity) getActivity(), annotation.bookId(), annotation.pageNumber());
		}
		
	}

	@Override
	public void refresh() {
		if(mGridAdapter != null && mBook != null) {
			mGridAdapter.refresh(mBook);
			mGridAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public int count(MainApplication main) {
		if(mApp == null || mBook == null) {
			return 0;
		}
		
		return mApp.data().database().annotationsDatabase().countByBook(mBook.id());
	}

	

}
