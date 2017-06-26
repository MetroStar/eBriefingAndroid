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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

import android.os.Environment;
import android.util.Log;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile.DownloadBookFileListener;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile.DownloadType;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;


public class DownloadBookFileTask extends AbstractDownloadBookFileTask {
	
	private static final String TAG = DownloadBookFileTask.class.getSimpleName();
	
	protected DownloadBookFileListener 	mListener;

	public DownloadBookFileTask(MainApplication app,
			DownloadBookFileListener listener, DownloadBookFile object) {
		super(app, object);
		
		mListener = listener;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String filename = "";
		String directory = "";
		
		if(mDownload.type() == DownloadType.TYPE_PAGE) {
			Page page = (Page) mObject;
			mHttpget.setURI(URI.create(page.url()));
			directory = page.directory();
			filename = page.filename();
		} else if(mDownload.type() == DownloadType.TYPE_CHAPTER_SMALL_IMAGE) {
			Chapter chapter = (Chapter) mObject;
			mHttpget.setURI(URI.create(chapter.smallImageUrl()));
			directory = chapter.directory();
			filename = chapter.smallImageFilename();
		} else if(mDownload.type() == DownloadType.TYPE_CHAPTER_LARGE_IMAGE) {
			Chapter chapter = (Chapter) mObject;
			mHttpget.setURI(URI.create(chapter.largeImageUrl()));
			directory = chapter.directory();
			filename = chapter.largeImageFilename();
		} else if(mDownload.type() == DownloadType.TYPE_BOOK_SMALL_IMAGE) {
			Book book = (Book) mObject;
			mHttpget.setURI(URI.create(book.smallImageUrl()));
			directory = book.directory();
			filename = book.smallImageFilename();
		} else if(mDownload.type() == DownloadType.TYPE_BOOK_LARGE_IMAGE) {
			Book book = (Book) mObject;
			mHttpget.setURI(URI.create(book.largeImageUrl()));
			directory = book.directory();
			filename = book.largeImageFilename();
		}
		
		return doResponse(directory, filename);
	}
	
	@Override
	protected boolean doResponse(String directory, String fileName) {
		HttpResponse response = null;
        
		try {
			response = executeHttp(mHttpget);
		} catch(ConnectTimeoutException e)    {
		    System.out.println("ConnectTimeoutException Occurred...");
		    return false;
		} catch(SocketTimeoutException e)   {
		    System.out.println("SocketTimeoutException Occurred...");
		    return false;
		} catch (SSLException e) {
			System.out.println("SSLException Occurred...");
		    return false;
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException Occurred...");
		    return false;
			
		} catch (Exception e) {
			System.out.println("UnknownException Occurred...");
			
			return false;
		}
		
		HttpEntity entity = null;
		
		if(response != null) {
			entity = response.getEntity();
		}
        
        if (entity != null) {
            try {
            	BufferedInputStream bis = new BufferedInputStream(entity.getContent());
            	
            	File storage_directory;
           		storage_directory = mContext.getExternalFilesDir(null);
            	
            	File directory_file = new File(storage_directory + File.separator + directory);
            	directory_file.mkdirs();
            	
            	File file = new File(directory_file, fileName);

            	
            	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
            	byte[] inByte = new byte[BUFFER_SIZE];
            	int read = 0;
            	while((read = bis.read(inByte, 0, BUFFER_SIZE)) >= 0) {
            		bos.write(inByte, 0, read);
            		mFileSize += read;
            		
            		// Flush after 1 MB
            	}
            	
            	if(bos != null) {
            		bos.flush();
            		bos.close();
            	}
            	
            	if(bis != null) {
            		bis.close(); 
            	}
            } catch(ConnectTimeoutException e)    {
			    System.out.println("ConnectTimeoutException Occurred...");
            	//e.printStackTrace();
			    return false;
			} catch(SocketTimeoutException e)   {
			    System.out.println("SocketTimeoutException Occurred...");
				//e.printStackTrace();
			    return false;
			} catch (UnknownHostException e) {
				System.out.println("UnknownHostException Occurred...");
				//e.printStackTrace();
			    return false;
			} catch (SSLException e) {
				System.out.println("SSLException Occurred...");
				//e.printStackTrace();
			    return false;
            } catch (Exception e) {
            	System.out.println("UnknownException Occurred...");
            	//e.printStackTrace();
            	
            	return false;
            }
        }
        
        return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		mListener.onDownloadBookFileFinished(result, this, mDownload);
	}

}
