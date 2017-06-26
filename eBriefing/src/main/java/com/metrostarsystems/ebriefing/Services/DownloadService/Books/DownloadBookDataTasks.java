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

package com.metrostarsystems.ebriefing.Services.DownloadService.Books;

import android.os.AsyncTask;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

public class DownloadBookDataTasks {

	public static class DownloadBookDataTask extends AsyncTask<Book, Integer, Book> {

		private MainApplication 				mApp;
		private DownloadBookDataTaskListener 	mListener;
		
		public DownloadBookDataTask(MainApplication main, DownloadBookDataTaskListener listener) {
			mApp = main;
			mListener = listener;
		}
		
		@Override
		protected Book doInBackground(Book... params) {
			Book book = params[0];

			try {
					Book.loadChaptersFromService(mApp.serverConnection(), book);
				
				Book.loadPagesFromService(mApp.serverConnection(), book);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return book;
		}


		@Override
		protected void onPostExecute(Book result) {
			super.onPostExecute(result);
			
			mListener.onBookDownloadDataTaskFinished(result);
			
		}
		
		public static interface DownloadBookDataTaskListener {
			public void onBookDownloadDataTaskFinished(Book book);
		}

	}
	
	public static class DownloadBookDataChaptersTask extends AsyncTask<Book, Integer, Book> {

		private MainApplication 				mApp;
		private BookDownloadChaptersListener 	mListener;
		
		public DownloadBookDataChaptersTask(MainApplication main, BookDownloadChaptersListener listener) {
			mApp = main;
			mListener = listener;
		}
		
		@Override
		protected Book doInBackground(Book... params) {
			Book book = params[0];
			
			try {
				Book.loadChaptersFromService(mApp.serverConnection(), book);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return book;
		}


		@Override
		protected void onPostExecute(Book result) {
			super.onPostExecute(result);
			
			mListener.onBookDownloadChaptersFinished(result);
			
		}
		
		public static interface BookDownloadChaptersListener {
			public void onBookDownloadChaptersFinished(Book book);
		}

	}

	public static class DownloadBookUpdateDataTask extends AsyncTask<Book, Integer, Book> {

		private MainApplication mApp;
		private DownloadBookUpdateDataTaskListener mListener;
		
		public DownloadBookUpdateDataTask(MainApplication main, DownloadBookUpdateDataTaskListener listener) {
			mApp = main;
			mListener = listener;
		}
		
		@Override
		protected Book doInBackground(Book... params) {
			Book book = params[0];
			
			try {
				Book.loadChaptersFromService(mApp.serverConnection(), book);
				
				Book.loadPagesFromService(mApp.serverConnection(), book);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return book;
		}


		@Override
		protected void onPostExecute(Book result) {
			super.onPostExecute(result);
			
			mListener.onBookUpdateDataTaskFinished(result);
			
		}
		
		public static interface DownloadBookUpdateDataTaskListener/* extends DownloadDeviceBookListener */{
			public void onBookUpdateDataTaskFinished(Book book);
		}

	}
}
