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

import java.io.File;

import net.maxters.android.ntlm.NTLM;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class DownloadImage {

	private MainApplication	mApp;
	private DisplayImageOptions mOptions;
	private ImageLoader mImageLoader;
	
	private File 					mCacheDir;
	
	private HttpParams 				mParams;
	private SchemeRegistry 			mSchemeRegistry;
	private ClientConnectionManager mManager;
	private DefaultHttpClient 		mHttpClient;
	
	public DownloadImage(MainApplication main) {
		mApp = main;
		
		mCacheDir = StorageUtils.getCacheDirectory(mApp.getApplicationContext());
		
		mOptions = new DisplayImageOptions.Builder()
											.cacheInMemory(true)
											//.displayer(new RoundedBitmapDisplayer(25))
											.showImageOnFail(android.R.drawable.ic_menu_gallery)
											.showImageOnLoading(android.R.drawable.ic_menu_gallery)
											/*.displayer(new FadeInBitmapDisplayer(3000))*/
											.cacheOnDisc(true)
											.build();

		mSchemeRegistry = new SchemeRegistry();
		mSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		mSchemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		mParams = new BasicHttpParams();
		
		mManager = new ThreadSafeClientConnManager(mParams, mSchemeRegistry);
		
		
	}
	
	public void initialize() {
		mHttpClient = new DefaultHttpClient(mManager, mParams);
		NTLM.setNTLM(mHttpClient, 	mApp.serverConnection().userId(), 
									mApp.serverConnection().password(), 
									mApp.serverConnection().domain());
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mApp.getApplicationContext())
				.imageDownloader(new HttpClientImageDownloader(mApp.getApplicationContext(), mHttpClient))
				.discCache(new LimitedAgeDiscCache(mCacheDir, Settings.IMAGE_CACHE_DURATION))
				.build();
		
		mImageLoader = ImageLoader.getInstance();
		
		if(mImageLoader.isInited()) {
			mImageLoader.destroy();
		}
		
		mImageLoader.init(config);
	}
	
	public ImageLoader imageLoader() { return mImageLoader; }
	public DisplayImageOptions imageOptions() { return mOptions; }
}
