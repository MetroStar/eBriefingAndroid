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

package com.metrostarsystems.ebriefing.Data.Framework.Server;

import java.io.File;
import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Cache.Cache;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileReadHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileWriteHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Preferences.AbstractPreferencesHandle;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerFeature;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject2;

public class ServerConnection {
	
	private static final String TAG = ServerConnection.class.getSimpleName();
	
	public static final String DEMO_LIBRARY_URL 	= "https://libraries.e-briefing.com/sites/demo";

	public static final String DEMO_ID				= "ebriefingdemoacct";
	public static final String DEMO_PASSWORD		= "5t@Wa*7uT%c#A!";
	public static final String DEMO_DOMAIN			= "ebriefingprod";
	
	public static final String NAMESPACE 			= "eBriefing";
	
	public static final String PLATFORM_ANDROID		= "android";
		
	
	public static final String CORE					= "Core";
	public static final String SAVEMYSTUFF			= "SaveMyStuff";
	public static final String MULTINOTES			= "MultiNotes";
	// -------------------------------------------------------------------------------------------------------
	
	private MainApplication mApp;
	
	private ServerConnectionType mType = ServerConnectionType.TYPE_DEMO;

	private String mLibraryURL = "";
	private String mUserID = "";
	private String mPassword = "";
	private String mDomain = "";
	
	private String mDataFilename = "";
	
	private FileReadHandle 	mReadHandle; 		
	private FileWriteHandle	mWriteHandle;
	
	private File			mDataLocation;
	
	public boolean isMultiNotes() {
		return mApp.data().database().serverDatabase().hasFeature(ServerConnection.MULTINOTES);
	}
	
	public MainApplication app() { return mApp; }
	public String userId() { return mUserID; }
	public String password() { return mPassword; }
	public String domain() { return mDomain; }
	
	public String database() { return mDataFilename; }
	
	public void setConnectionType(ServerConnectionType type) {
		mType = type;
	}
	
	public ServerConnectionType type() {
		return mType;
	}
	
	public String libraryURL() { 
		if(mApp.data() == null) {
			return mLibraryURL;
		}
		
		return mLibraryURL + mApp.data().database().serverDatabase().featureUrl(ServerConnection.CORE); 
	}
	
	public String syncURL() {
		if(mApp.data() == null) {
			return mLibraryURL;
		}
		
		return mLibraryURL + mApp.data().database().serverDatabase().featureUrl(ServerConnection.SAVEMYSTUFF); 
	}
	
	public String multiNotesURL() {
		if(mApp.data() == null) {
			return mLibraryURL;
		}
		
		return mLibraryURL + mApp.data().database().serverDatabase().featureUrl(ServerConnection.MULTINOTES); 
	}
	
	public String serverInfo2010Url() {
		return mLibraryURL + "_layouts/eBriefing/ServerInfo.asmx";
	}
	
	public String serverInfo2013Url() {
		return mLibraryURL + "_layouts/15/eBriefing/ServerInfo.asmx";
	}
	
	public String nameSpace() { return NAMESPACE; }
	
	public void saveConnectionPreferences(AbstractPreferencesHandle handle) {
		handle.set("libraryurl", 	mLibraryURL);
		handle.set("userid", 		mUserID);
		handle.set("password", 		mPassword);
		handle.set("domain", 		mDomain);
	}
	
	public void clearConnectionPreferences(AbstractPreferencesHandle handle) {
		handle.remove("libraryurl");
		handle.remove("userid");
		handle.remove("password");
		handle.remove("domain");
	}
	
	public FileReadHandle fileReadHandle() { return mReadHandle; }
	public FileWriteHandle fileWriteHandle() { return mWriteHandle; }
	
	// Static Connections ------------------------------------------------------------------------------------
	public static ServerConnection demo(MainApplication main) {
		return new ServerConnection.Builder(main)
									.type(ServerConnectionType.TYPE_DEMO)
									.libraryURL(DEMO_LIBRARY_URL)
									.userId(DEMO_ID)
									.password(DEMO_PASSWORD)
									.domain(DEMO_DOMAIN)
									.dataFilename(Utilities.MD5(DEMO_LIBRARY_URL + DEMO_ID) + ".db")
									.sync(false)
									.build();
	}
	
	public static ServerConnection enterprise(MainApplication main, String libraryURL, String id, String password, String domain) {
		return new ServerConnection.Builder(main)
									.type(ServerConnectionType.TYPE_ENTERPRISE)
									.libraryURL(libraryURL)
									.userId(id)
									.password(password)
									.domain(domain)
									.dataFilename(Utilities.MD5(libraryURL + id) + ".db")
									.sync(true)
									.build();
	}
	// -------------------------------------------------------------------------------------------------------
	
	// Constructors ------------------------------------------------------------------------------------------
	private ServerConnection(MainApplication main) {
		mApp = main;
	}
	
	private ServerConnection(Builder build) {
		this(build.mServer.mApp);
		
		mType 			= build.mServer.mType;
		
		mLibraryURL 	= build.mServer.mLibraryURL;
		mUserID 		= build.mServer.mUserID;
		mPassword 		= build.mServer.mPassword;
		mDomain 		= build.mServer.mDomain;
		
		mDataFilename 	= build.mServer.mDataFilename;
		
		if(build.mSync) {
			SyncService.setSync((Boolean) mApp.preferences().get(Tags.SYNC_PREFERENCE, Settings.SYNC_ON_INSTALL));
		} else {
			SyncService.setSync(Settings.SYNC_ON_INSTALL);
		}
		
		mReadHandle = Cache.loadExternalDataHandle(mApp.getApplicationContext(), mDataFilename);
		mWriteHandle = Cache.saveExternalDataHandle(mApp.getApplicationContext(), mDataFilename);
		
	}
	// -------------------------------------------------------------------------------------------------------
	
	// Builder -----------------------------------------------------------------------------------------------
	private static class Builder {
		
		private ServerConnection mServer;
		private boolean mSync;
		
		public Builder(MainApplication main) {
			mServer = new ServerConnection(main);
		}
		
		public Builder libraryURL(String url) { 
			
			if(url.endsWith("/")) {
				mServer.mLibraryURL = url; 
			} else {
				mServer.mLibraryURL = url + "/"; 
			}
			
			return this; 
			
		}
		
		public Builder userId(String id) { mServer.mUserID = id; return this; }
		public Builder password(String password) { mServer.mPassword = password; return this; }
		public Builder domain(String domain) { mServer.mDomain = domain; return this; }
		public Builder dataFilename(String filename) { mServer.mDataFilename = filename; return this; }
		public Builder sync(boolean sync) { mSync = sync; return this; }
		public Builder type(ServerConnectionType type) { mServer.mType = type; return this; }
	
		public ServerConnection build() {
			return new ServerConnection(this);
		}
	}
	
	public static enum ServerConnectionType {
		TYPE_DEMO,
		TYPE_ENTERPRISE;
	}
	
	public static enum ServerConnectionRequest {

        // Server
		REQUEST_GET_SERVER_INFO("GetServerInfo"),

        // Core
		REQUEST_CORE_GET_BOOKS("GetBooks"),
		REQUEST_CORE_GET_CHAPTERS("GetChaptersInBook"),
		REQUEST_CORE_GET_PAGES("GetPagesInBook"),

        // Sync
		REQUEST_SYNC_SET_MY_BOOKS("SetMyBooks"),
		REQUEST_SYNC_GET_MY_BOOKS("GetMyBooks"),
		REQUEST_SYNC_SET_MY_BOOKMARKS("SetMyBookmarks"),
		REQUEST_SYNC_GET_MY_BOOKMARKS("GetMyBookmarks"),
		REQUEST_SYNC_SET_MY_ANNOTATION("SetMyPenAnnotation"),
		REQUEST_SYNC_GET_MY_ANNOTATIONS("GetMyPenAnnotations"),
		REQUEST_SYNC_REMOVE_MY_ANNOTATION("RemoveMyPenAnnotation"),
		REQUEST_SYNC_GET_MY_NOTES("GetMyTextAnnotations"),
		REQUEST_SYNC_SET_MY_NOTE("SetMyTextAnnotation"),
		REQUEST_SYNC_REMOVE_MY_NOTE("RemoveMyTextAnnotation"),
		REQUEST_SYNC_DELETE_MY_STUFF("DeleteMyStuff"),

        // MultiNotes
		REQUEST_MULTINOTES_GET_ALL_NOTES("GetAllNotes"),
        REQUEST_MULTINOTES_GET_NOTES_UPDATES("GetNotesUpdates"),
        REQUEST_MULTINOTES_SAVE_NOTES("SaveNotes"),

		
		REQUEST_DELETE_BOOK("DeleteDeviceBook");
		
		private String mMethod = "";
		
		private ServerConnectionRequest(String method) {
			mMethod = method;
		}
		
		public String method() {
			return mMethod;
		}
	}
}
