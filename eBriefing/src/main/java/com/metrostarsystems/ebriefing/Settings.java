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

import com.metrostarsystems.ebriefing.Data.Cache.Cache;

public class Settings {
	
	// Cache Settings ----------------------------------------------------------------------------------------------
	public static final long					SECOND										= 1000;
	public static final long					MINUTE										= SECOND * 60;
	public static final long					HOUR										= MINUTE * 60;
	public static final long					DAY											= HOUR * 24;
	public static final long					WEEK										= DAY * 7;
	public static final long					MONTH										= DAY * 30;
	public static final long					YEAR										= DAY * 365;

//	public static final long					AVAILABLE_BOOKLIST_CACHE_DURATION			= 6 * HOUR;
	public static final long					IMAGE_CACHE_DURATION						= 24 * HOUR;

	// Testing Flags
	public static boolean						DEBUG 										= false;
	public static boolean						DEBUG_MESSAGES 								= false;
	public static boolean						DEBUG_SOAP_MESSAGES 						= false;
	
	
//	public static boolean						ALLOW_MULTINOTES_UNIVERSAL					= false;
		
	// Delete Settings
	public static boolean						DELETE_MY_STUFF_ON_STARTUP					= false;
	public static boolean						DELETE_DATABASE_ON_STARTUP					= false;
    public static boolean                       EXPORT_DATABASE                             = false;
	
	// Auto Refresh Settings
	public static boolean						AUTO_REFRESH								= true;
	public static long							AUTO_REFRESH_PERIOD							= MINUTE;
	
	// Auto Sync Settings
	public static boolean						AUTO_SYNC									= true;
	public static long							AUTO_SYNC_PERIOD							= SECOND * 30;
	public static boolean						DISPLAY_SYNC_MESSAGE						= false;
	
	// Sync Settings
    public static boolean                       SYNC_ON_INSTALL                             = true;
	public static boolean						SYNC_ON_START								= true;
	public static boolean						SYNC_ON_TURN_ON_SYNC						= true;
	public static boolean						SYNC_ON_DOWNLOAD							= true;
	
	
	
	
	public static boolean						ENABLE_ENCRYPT_LIBRARY_DATA					= false;
	public static boolean						ENABLE_ENCRYPT_BOOK_DATA					= false;
	
	public static boolean						ENABLE_PRINTING								= true;
	
	public static int							MAX_NOTE_LENGTH								= 500;
	
	
	public static enum CacheType {
		TYPE_EXTERNAL_DATA,
		TYPE_EXTERNAL_TEXT,
		TYPE_INTERNAL_DATA,
		TYPE_INTERNAL_TEXT;
	}
}
