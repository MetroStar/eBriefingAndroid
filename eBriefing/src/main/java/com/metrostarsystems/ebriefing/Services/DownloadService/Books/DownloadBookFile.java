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

import java.io.Serializable;


public class DownloadBookFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object			mObject = null;
	private DownloadType	mType = DownloadType.TYPE_PAGE;
	
	public DownloadBookFile(Object object, DownloadType type) {
		mObject = object;
		mType = type;
	}

	public Object object() { return mObject; }
	public DownloadType type() { return mType; }
	
	
	
	@Override
	public String toString() {
		return mObject.toString();
	}



	public static enum DownloadType {
		TYPE_BOOK_SMALL_IMAGE,
		TYPE_BOOK_LARGE_IMAGE,
		TYPE_PAGE,
		TYPE_CHAPTER_SMALL_IMAGE,
		TYPE_CHAPTER_LARGE_IMAGE;
	}
	
	public static interface DownloadBookFileListener {
		public void onDownloadBookFileFinished(boolean finished, AbstractDownloadBookFileTask task, DownloadBookFile download);
	}
}
