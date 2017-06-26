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

package com.metrostarsystems.ebriefing.Services.CoreService;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreManager.CoreManagerFinishedListener;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreManager.CoreManagerStatus;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.ProcessGetBooksTaskResponses;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.GetChaptersObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Chapter.ProcessGetChaptersTaskResponses;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class CoreService extends Service implements CoreManagerFinishedListener {
	
	private static final String TAG = CoreService.class.getSimpleName();
	
	public static final int	SERVICE_NOTIFICATION_ID = 87655;
	
	private static MainApplication 					mApp;
	private static CoreService 						mService;
	
	private final IBinder 							mBinder = new CoreBinder();

	
	private static boolean 							mAutoRefresh = false;

	
	private CoreManager 							mCoreManager;
	
	private CoreServiceReceiver						mCoreServiceReceiver;
	
	private static ProcessGetBooksTaskResponses		mProcessGetBooksTaskResponses;
	private static ProcessGetChaptersTaskResponses	mProcessGetChaptersTaskResponses;


	private WakeLock mWakeLock;
	
	public CoreService() {
		mService = this;
	}
	
	public void setApplicationContext(MainApplication main) {
		mApp = main;
	}
	
	public static void setAllowAutoRefresh(boolean refresh) {
		mAutoRefresh = refresh;
	}

	public static boolean allowAutoRefresh() {
		return mAutoRefresh;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mService = this;
		
		
		
		// obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SyncServiceWakeLock");
        
        if(mWakeLock != null && !mWakeLock.isHeld()) {
        	mWakeLock.acquire();
        }
        
        mApp = (MainApplication) getApplicationContext();
        
        mProcessGetBooksTaskResponses = new ProcessGetBooksTaskResponses(mApp);
        

        IntentFilter filterSync = new IntentFilter(CoreServiceReceiver.PROCESS_CORE_RESPONSE);
        filterSync.addCategory(Intent.CATEGORY_DEFAULT);
		mCoreServiceReceiver = new CoreServiceReceiver(this);
		registerReceiver(mCoreServiceReceiver, filterSync);
	}
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(mCoreManager == null) {
			mCoreManager = new CoreManager((MainApplication) getApplicationContext(), this);
		}
	
		handleIntent(intent);
		
		return START_STICKY;
	}
	
	public MainApplication app() {
		return mApp;
	}


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mCoreServiceReceiver);
		
		onComplete();
	}
	
	 
	private void handleIntent(Intent intent) {
		
        if(mCoreManager.status() == CoreManagerStatus.RUNNING) {
        	mCoreManager.add(intent);
        } else {
        	mCoreManager.start(intent);
        }
        
    }
	
	public void onComplete() {
		if(mWakeLock != null) {
			if(mWakeLock.isHeld()) {
				mWakeLock.release();
			}
		}
		
	}
	

// -----------------------------------------------------------------------------------------------------------


//	/**
//	 * Run the delete my stuff task
//	 * @param app the main application instance
//	 */
//	public static void syncServiceDeleteMyStuff() {
//		if(mApp == null) {
//			return;
//		}
//		
//		if(canSync()) {
//			Intent intent = new Intent(mApp, CoreService.class);
//			
//			Bundle bundle = new Bundle();
//			bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_DELETEMYSTUFF);
//			intent.putExtras(bundle);
//			
//			mApp.startService(intent);
//		}
//	}
	
	/**
	 * Run the refresh available books task
	 * @param app the main application instance
	 */
	public static void coreServiceGetBooks() {
		if(mApp == null) {
			return;
		}
		
		Intent intent = new Intent(mApp, CoreService.class);
		
		Bundle bundle = new Bundle();
		bundle.putInt("response", CoreServiceReceiver.MSG_CORE_GET_BOOKS);
		intent.putExtras(bundle);
		
		mApp.startService(intent);
	}
	
	public static ProcessGetBooksTaskResponses processGetBooksTaskResponses() {
		return mProcessGetBooksTaskResponses;
	}
	
	/**
	 * Runs the sync book task
	 */
	public static void coreServiceGetChapters(String bookId) {
		if(mApp == null || mApp.serverConnection() == null) {
			return;
		}
		
		Intent intent = new Intent(mApp, CoreService.class);
	
		Bundle bundle = new Bundle();
		bundle.putInt("response", CoreServiceReceiver.MSG_CORE_GET_CHAPTERS);
		bundle.putString("bookid", bookId);
		intent.putExtras(bundle);
	
		mApp.startService(intent);
	}
	
	public static ProcessGetChaptersTaskResponses processGetChaptersTaskResponses() {
		return mProcessGetChaptersTaskResponses;
	}
	
//	/** 
//	 * Run the save cache task, does not require a network connection
//	 * Note: if the app is killed while save cache is running, this will corrupt
//	 * the cache and require downloading of all the book upon syncing
//	 * @param app the main application instance
//	 */
//	public static void syncServiceSaveCache() {
//		if(mApp == null || mApp.data() == null) {
//			return;
//		}
//		
//		Intent intent = new Intent(mApp, SyncService.class);
//		
//		Bundle bundle = new Bundle();
//		bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_SAVECACHE);
//		intent.putExtras(bundle);
//		
//		mApp.startService(intent);
//	}
//	
//	public static void syncServiceSaveCache(boolean override) {
//		if(mApp == null) {
//			return;
//		}
//		
//		Intent intent = new Intent(mApp, SyncService.class);
//		
//		Bundle bundle = new Bundle();
//		bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_SAVECACHE);
//		bundle.putBoolean("overridesave", override);
//		intent.putExtras(bundle);
//		
//		mApp.startService(intent);
//	}
	
	/**
	 * Runs the delete my book task, does not require a network connection
	 * @param app the main application instance
	 * @param bookId the id of the book to delete
	 */
//	public static void syncServiceDeleteBook(String bookId) {
//		if(mApp == null || bookId.isEmpty()) {
//			return;
//		}
//		
//		Intent intent = new Intent(mApp, CoreService.class);
//		
//		Bundle bundle = new Bundle();
//		bundle.putInt("response", SyncServiceReceiver.MSG_SYNC_DELETEBOOK);
//		bundle.putString("bookid", bookId);
//		intent.putExtras(bundle);
//		
//		mApp.startService(intent);
//	}
	

	
	public GetBooksObject getGetBooksResults() {
		return mCoreManager.getGetBooksResults();
	}
	
	public GetChaptersObject getGetChaptersResults() {
		return mCoreManager.getGetChaptersResults();
	}

	@Override
	public void OnCoreManagerFinished(CoreManager manager) {
		onComplete();
		stopSelf();
	}
	
	public class CoreBinder extends Binder {
		public CoreService getService() {
			return CoreService.this;
		}
	}
	
	public static boolean isNetworkAvailable() {
		if(mApp == null) {
			return false;
		}
		
		boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    
	    return haveConnectedWifi || haveConnectedMobile;
	}
}
