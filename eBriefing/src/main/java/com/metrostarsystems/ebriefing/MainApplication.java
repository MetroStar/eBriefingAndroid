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

package com.metrostarsystems.ebriefing;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.metrostarsystems.ebriefing.BookChapter.Tab.ChapterTabs;
import com.metrostarsystems.ebriefing.BookPage.Contents.Tab.ContentTabs;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Dashboard.Tab.DashboardTabs;
import com.metrostarsystems.ebriefing.Data.Data;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileReadHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileWriteHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Preferences.ObscuredPreferencesHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerInfo;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreHandler;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreService;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreService.CoreBinder;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksObject;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksTask;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.GetBooksTask.GetBooksTaskListener;
import com.metrostarsystems.ebriefing.Services.CoreService.Book.ProcessGetBooksTaskResponses;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService.DownloadBinder;
import com.metrostarsystems.ebriefing.Services.DownloadService.Images.DownloadImage;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncHandler;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.Books.GetMyBooksObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteMyStuffTask;
import com.metrostarsystems.ebriefing.Services.SyncService.Delete.DeleteMyStuffTask.DeleteMyStuffTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerFeature;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject2;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService.SyncBinder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.radaee.pdf.Global;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class MainApplication extends Application implements GetBooksTaskListener,
															DeleteMyStuffTaskListener {
	
	private static final String TAG = MainApplication.class.getSimpleName();
	
	private Data 						    mData;
	private DownloadImage				    mDownloadImage;
	private ServerConnection			    mServerConnection;
	private ObscuredPreferencesHandle       mPreferences;
	
	private DashboardTabs 		            mDashboardTabs;
	private ChapterTabs			            mChapterTabs;
	private ContentTabs			            mContentTabs;
	
	private ActivityDashboard	            mDashboard = null;
	private DownloadService		            mDownloadService;
	private boolean				            mDownloadServiceBound = false;
	
	private ProcessGetBooksTaskResponses    mProcessGetBooksTaskResponses;
	
	private ServiceConnection 	            mDownloadConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			DownloadBinder binder = (DownloadBinder) service;
			mDownloadService = binder.getService();
			mDownloadServiceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mDownloadServiceBound = false;
			mDownloadService = null;
		}
		
	};
	
	private SyncService			mSyncService;
	private boolean				mSyncServiceBound = false;
	
	private ServiceConnection 	mSyncConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			SyncBinder binder = (SyncBinder) service;
			mSyncService = binder.getService();
			mSyncServiceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSyncServiceBound = false;
			mSyncService = null;
		}
		
	};
	
	private CoreService			mCoreService;
	private boolean				mCoreServiceBound = false;
	
	private ServiceConnection 	mCoreConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			CoreBinder binder = (CoreBinder) service;
			mCoreService = binder.getService();
			mCoreServiceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mCoreServiceBound = false;
			mCoreService = null;
		}
		
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mDashboardTabs 		= new DashboardTabs(this);
		mChapterTabs 		= new ChapterTabs(this);
		mContentTabs 		= new ContentTabs(this);
		
		mPreferences 		= new ObscuredPreferencesHandle.Builder(this, "mainapplication").build();
		
		mPreferences.read();
		
		mProcessGetBooksTaskResponses = new ProcessGetBooksTaskResponses(this);
		
		
		mDownloadImage 		= new DownloadImage(this);
		
		mDownloadService 	= new DownloadService();
        mDownloadService.setApplicationContext(this);

		mSyncService 		= new SyncService();
		mSyncService.setApplicationContext(this);
		
		mCoreService 		= new CoreService();
		mCoreService.setApplicationContext(this);
		
		Global.Init(getApplicationContext());


	}
	
	public void onDestroy() {
		
		// Stop the download service
		if(mDownloadServiceBound) {
			unbindService(mDownloadConnection);
			mDownloadServiceBound = false;
		}
		
		// Stop the sync service
		if(mSyncServiceBound) {
			unbindService(mSyncConnection);
			mSyncServiceBound = false;
		}
		
		// Stop the core service
		if(mCoreServiceBound) {
			unbindService(mCoreConnection);
			mCoreServiceBound = false;
		}
	}
	

	public void setDashboard(ActivityDashboard dashboard) {
		mDashboard = dashboard;
	}
	
	public ActivityDashboard dashBoard() {
		return mDashboard;
	}
	
	public void terminateDashboard() {
		if(mDashboard != null) {
			mDashboard.finish();
			mDashboard = null;
		}
	}

	
	// -------------------------------------------------------------------------------------------------------
	
	public void initialize(ServerConnection connection, ServerInfoObject2 serverInfo) {
		mServerConnection = connection;
		
		mDownloadImage.initialize();
		
		// Close the database
		if(mData != null && mData.database() != null) {
			mData.database().close();
		}
		
		mData = new Data(this);
		
		if(serverInfo != null && serverInfo.isValid()) {
			// Update server info
			
			// Check for existing server info
			ServerInfo info = data().database().serverDatabase().serverInfo();
			
			// Server Info exists
			if(info != null) {
                if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Updating server info..."); }
				ServerInfo new_server_info = serverInfo.serverInfo();
				
				if(new_server_info.release() > info.release()) {
					// Update server info
					new_server_info.setId(info.id());
					data().database().serverDatabase().updateServer(new_server_info);
				}
				
				ArrayList<ServerFeature> features = serverInfo.features();
				
				for(ServerFeature feature : features) {
					data().database().serverDatabase().updateFeature(feature);
				}
				
			// Server Info doesn't exist
			} else {
                if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Inserting server info..."); }
				data().database().serverDatabase().insertServer(serverInfo.serverInfo());
				
				ArrayList<ServerFeature> features = serverInfo.features();
				
				for(ServerFeature feature : features) {
					data().database().serverDatabase().insertFeature(feature);
				}
			}
		}
		
		setupServices();
		
		if(Settings.DELETE_MY_STUFF_ON_STARTUP) {
			new DeleteMyStuffTask(this, mServerConnection).execute();
		} else {
			new GetBooksTask(this, mServerConnection).execute();
		}

        //Check for uncompleted downloads
        ArrayList<Book> books = data().database().booksDatabase().getMyBooksNotDownloaded();

        if(books != null && books.size() > 0) {
            for(Book book : books) {
                DownloadService.downloadDeviceBookData(book);
            }
        }
		
		data().database().exportDB(this);
	}
	
	private void setupServices() {
		// Download Service -------------------------------------------------------------------------------
		if(mDownloadServiceBound) {
			unbindService(mDownloadConnection);
			mDownloadServiceBound = false;
		}

		// Kills the service if the service is still active from previous run
		stopService(new Intent(this, DownloadService.class));

		Intent download_service_intent = new Intent(this, DownloadService.class);
		bindService(download_service_intent, mDownloadConnection, Context.BIND_AUTO_CREATE);
		// End Download Service ---------------------------------------------------------------------------

		// Sync Service -----------------------------------------------------------------------------------
		if(mSyncServiceBound) {
			unbindService(mSyncConnection);
			mSyncServiceBound = false;
		}

		// Kills the service if the service is still active from previous run
		stopService(new Intent(this, SyncService.class));

		Intent sync_service_intent = new Intent(this, SyncService.class);
		bindService(sync_service_intent, mSyncConnection, Context.BIND_AUTO_CREATE);

		initializeSyncService();
		// End Sync Service -------------------------------------------------------------------------------

		// Core Service -----------------------------------------------------------------------------------
		if(mCoreServiceBound) {
			unbindService(mCoreConnection);
			mCoreServiceBound = false;
		}

		// Kills the service if the service is still active from previous run
		stopService(new Intent(this, CoreService.class));

		Intent core_service_intent = new Intent(this, CoreService.class);
		bindService(core_service_intent, mCoreConnection, Context.BIND_AUTO_CREATE);

		initializeCoreService();
		// End Core Service -------------------------------------------------------------------------------
	}
	
// Core Service Methods --------------------------------------------------------------------------------------
	
	private CoreHandler mCoreHandler;
	private Timer mRefreshTimer;


	private void initializeCoreService() {
		mCoreHandler = new CoreHandler(this);

		if(Settings.AUTO_REFRESH) {
			TimerTask refresher;
			mRefreshTimer = new Timer();

			refresher = new TimerTask() {
				public void run() {
					mCoreHandler.sendEmptyMessage(CoreHandler.HANDLER_AUTO_REFRESH);
				}
			};

			mRefreshTimer.scheduleAtFixedRate(refresher, Settings.AUTO_REFRESH_PERIOD, 
														 Settings.AUTO_REFRESH_PERIOD);
		} else {
			Utilities.displayToast(getApplicationContext(), "Auto Refresh Disabled...");
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "AUTO REFRESH_DISABLED"); }
		}
	}
	
	public CoreService coreService() {
		return mCoreService;
	}
	
// Sync Service Methods --------------------------------------------------------------------------------------
	private SyncHandler mSyncHandler;
	private Timer mSyncTimer;
	
	
	private void initializeSyncService() {
		mSyncHandler = new SyncHandler(this);
        
        if(Settings.AUTO_SYNC) {
	        TimerTask syncer;
	        mSyncTimer = new Timer();
	        
	        syncer = new TimerTask() {
	        	public void run() {
	        		mSyncHandler.sendEmptyMessage(SyncHandler.HANDLER_AUTO_SYNC);
	        	}
	        };
	        
	        mSyncTimer.scheduleAtFixedRate(syncer, Settings.AUTO_SYNC_PERIOD, 
	        									   Settings.AUTO_SYNC_PERIOD);
        } else {
        	Utilities.displayToast(getApplicationContext(), "Auto Sync Disabled...");
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "AUTO SYNC DISABLED"); }
        }
	}
	
	
	public SyncService syncService() {
		return mSyncService;
	}
	
// Download Service Methods -------------------------------------------------------------------------------------------------
	public DownloadService downloadService() {
		return mDownloadService;
	}
	
	/**
	 * Returns the data
	 * @return the data
	 */
	public Data data() {
		return mData; 
	}
		
	public ObscuredPreferencesHandle preferences() { return mPreferences; }

	
	/**
	 * Returns the current application locale
	 * @return the current application locale
	 */
	public Locale locale() { return getResources().getConfiguration().locale; }

	
	public ImageLoader imageLoader() { return mDownloadImage.imageLoader(); }
	public DisplayImageOptions imageOptions() { return mDownloadImage.imageOptions(); }

	// Connection Methods ------------------------------------------------------------------------------------
	public ServerConnection serverConnection() { return mServerConnection; }
	public void setServerConnection(ServerConnection connection) { mServerConnection = connection; }

	public FileReadHandle readHandle() {
		return mServerConnection.fileReadHandle();
	}

	public FileWriteHandle writeHandle() {
		return mServerConnection.fileWriteHandle();
	}
	// -------------------------------------------------------------------------------------------------------
	
	public DashboardTabs dashboardTabs() {
		return mDashboardTabs;
	}
	
	public ChapterTabs chapterTabs() {
		return mChapterTabs;
	}
	
	public ContentTabs contentTabs() {
		return mContentTabs;
	}

	
	public void setCurrentBook(Book book) {
		mData.setCurrentBook(book);
	}
	
	public Book currentBook() {
		return mData.currentBook();
	}

	public float dpi() {
		return getResources().getDisplayMetrics().density;
	}

	@Override
	public void OnGetBooksTaskFinishedListener(GetBooksObject result) {
		if(result == null || !result.isValid()) {
			new GetBooksTask(this, mServerConnection).execute();
		}
		
		mProcessGetBooksTaskResponses.processGetBooksResponse(result);
		
		if(Settings.SYNC_ON_START) {
			SyncService.sync();
		}
	}

	@Override
	public void OnDeleteMyStuffTaskFinishedListener(GetMyBooksObject result) {
		new GetBooksTask(this, mServerConnection).execute();
	}
}
