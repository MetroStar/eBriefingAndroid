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

package com.metrostarsystems.ebriefing.Services.DownloadService.Images;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.net.ssl.SSLException;

import net.maxters.android.ntlm.NTLM;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.AsyncTask;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.AbstractDownloadBookFileTask;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile.DownloadBookFileListener;
import com.metrostarsystems.ebriefing.Services.DownloadService.Books.DownloadBookFile.DownloadType;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;


public class DownloadImageFileTask extends AsyncTask<Void, Void, Boolean> {
	
	public static final int						BUFFER_SIZE = 1024 * 50;
	public static final NumberFormat 			RATE_FORMAT = new DecimalFormat("0.00");

	private static HttpParams 					mHttpParams;
	private static SchemeRegistry 				mSchemeRegistry;
	private static ClientConnectionManager 		mCCManager;
	private static DefaultHttpClient 			mHttpClient;
	protected static HttpGet 					mHttpget = new HttpGet();
	protected static File 						mDirectoryFile = null;
	
	private MainApplication 					mApp;
	private Context								mContext;
	
	private String 								mFilename;
	protected long								mFileSize = 0;
	
	static {
		mHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mHttpParams, 5000);
		HttpConnectionParams.setSoTimeout(mHttpParams, 5000);
		
		mSchemeRegistry = new SchemeRegistry();
		mSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		mSchemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		mCCManager = new ThreadSafeClientConnManager(mHttpParams, mSchemeRegistry);
	}

	public DownloadImageFileTask(MainApplication app, String filename) {
		mApp = app;
		mFilename = filename;
		
		if(mDirectoryFile == null) {
			mDirectoryFile = mContext.getExternalFilesDir(null);
		}
		
		mHttpClient = new DefaultHttpClient(mCCManager, mHttpParams);
		NTLM.setNTLM(mHttpClient,   mApp.serverConnection().userId(), 
									mApp.serverConnection().password(), 
									mApp.serverConnection().domain());
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		return doResponse(mFilename);
	}
	
	protected boolean doResponse(String fileName) {
		HttpResponse response = null;
        
		try {
			response = executeHttp(mHttpget);
		} catch(ConnectTimeoutException e)    {
		    //System.out.println("ConnectTimeoutException Occurred...");
			e.printStackTrace();
		    return false;
		} catch(SocketTimeoutException e)   {
		    //System.out.println("SocketTimeoutException Occurred...");
			e.printStackTrace();
		    return false;
		    
		    //return doResponse(fileName);
		} catch (SSLException e) {
			//System.out.println("SSLException Occurred...");
			e.printStackTrace();
		    return false;
		} catch (UnknownHostException e) {
			//System.out.println("UnknownHostException Occurred...");
			e.printStackTrace();
		    return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
		
		HttpEntity entity = null;
		
		if(response != null) {
			entity = response.getEntity();
		}
        
        if (entity != null) {
            try {
            	BufferedInputStream bis = new BufferedInputStream(entity.getContent());
            	
            	File file = new File(mDirectoryFile, fileName);
            	
            	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
            	byte[] inByte = new byte[BUFFER_SIZE];
            	int read = 0;
            	while((read = bis.read(inByte, 0, BUFFER_SIZE)) >= 0) {
            		bos.write(inByte, 0, read);
            		mFileSize += read;
            	}
            	
            	if(bos != null) {
            		bos.flush();
            		bos.close();
            	}
            	
            	if(bis != null) {
            		bis.close(); 
            	}
            } catch(ConnectTimeoutException e)    {
			    //System.out.println("ConnectTimeoutException Occurred...");
            	e.printStackTrace();
			    return false;
			} catch(SocketTimeoutException e)   {
			    //System.out.println("SocketTimeoutException Occurred...");
				e.printStackTrace();
			    return false;
			} catch (UnknownHostException e) {
				//System.out.println("UnknownHostException Occurred...");
				e.printStackTrace();
			    return false;
			} catch (SSLException e) {
				//System.out.println("SSLException Occurred...");
				e.printStackTrace();
			    return false;
            } catch (Exception e) {
            	e.printStackTrace();
            	
            	return false;
            }
        }
        
        return true;
	}
	
	protected HttpResponse executeHttp(HttpGet httpget) throws Exception {
		HttpResponse response = null;
        
		response = mHttpClient.execute(httpget);
		
		if(response == null) {
			response = mHttpClient.execute(httpget);
		}
		
		return response;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
	}

}
