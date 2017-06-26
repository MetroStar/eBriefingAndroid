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

package com.metrostarsystems.ebriefing.BookPage.Notes;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Search.SearchObject;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesListAdapter extends ArrayAdapter<Note> {
	
	private MainApplication mApp;
	private Context 		mContext;
	private Book 			mBook;
	
	public NotesListAdapter(Context context, Book book, String pageId) {
		super(context, R.layout.activity_page_notes_list_item);
		
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		addAll(mApp.data().database().notesDatabase().notesByPage(pageId));
		
		mBook = book;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		// If convertview is null create the layout
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_page_notes_list_item, parent, false);
			
			// Create the view holder
			holder = new ViewHolder();
			holder.mContentTextView = (TextView) convertView.findViewById(R.id.textView_content);
			holder.mDateTextView = (TextView) convertView.findViewById(R.id.textView_date);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		// Otherwise recycle the layout
		
		Note note = getItem(position);
		
		holder.mContentTextView.setText(note.content());
		holder.mDateTextView.setText(note.dateModifiedFormat());
		

		return convertView;
	}
	
	
	
	public void refresh(Book book, String pageId) {
		clear();
		mBook = book;
		addAll(mApp.data().database().notesDatabase().notesByPage(pageId));
	}
	
	
	private static class ViewHolder {
		public TextView 		mContentTextView;
		public TextView 		mDateTextView;
	}
}
