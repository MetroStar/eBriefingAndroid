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

package com.metrostarsystems.ebriefing.Services.SyncService;

import com.metrostarsystems.ebriefing.MainApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SyncServiceReceiver extends BroadcastReceiver {
	
	// Service Messages
	public static final int MSG_SYNC_STARTED				= 0;
	public static final int MSG_SYNC_COMPLETE 				= 1;
//	public static final int MSG_SYNC_REFRESH				= 2;
	public static final int MSG_SYNC_SETMYBOOKS				= 3;
	public static final int MSG_SYNC_GETMYBOOKS				= 4;
	public static final int MSG_SYNC_DELETEMYSTUFF			= 5;
//	public static final int MSG_SYNC_SAVECACHE				= 6;
	public static final int MSG_SYNC_SETMYNOTE				= 7;
	public static final int MSG_SYNC_GETMYNOTES				= 8;
	public static final int MSG_SYNC_REMOVEMYNOTE			= 9;
	public static final int MSG_SYNC_SETMYBOOKMARKS			= 10;
	public static final int MSG_SYNC_GETMYBOOKMARKS			= 11;
	public static final int MSG_SYNC_SETMYANNOTATION 		= 12;
	public static final int MSG_SYNC_GETMYANNOTATIONS 		= 13;
	public static final int MSG_SYNC_REMOVEMYANNOTATION 	= 14;
	public static final int MSG_SYNC_DELETEBOOK 			= 15;
	public static final int MSG_SYNC_REMOVEMYBOOK 			= 16;
//	public static final int MSG_SYNC_SETMYBOOK 				= 17;
	public static final int MSG_MULTINOTES_GETALLNOTES		= 18;
    public static final int MSG_MULTINOTES_GETNOTESUPDATES  = 19;
    public static final int MSG_MULTINOTES_SAVENOTES        = 20;
	
	public static final int	MSG_SYNC_BOOK					= 21;
    public static final int MSG_SYNC_BOOK_AFTER_DOWNLOAD    = 22;
	
	public static final String PROCESS_SYNC_RESPONSE = "com.metrostarsystems.intent.action.PROCESS_SYNC_RESPONSE";
	 
	private SyncService			mService;
	private MainApplication 	mApp;
	
	public SyncServiceReceiver(SyncService service) {
		mService = service;
		mApp = service.app();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int response = intent.getIntExtra("response", -1);
		
		if(mApp != null && mService != null) {
			
			switch(response) {
				case MSG_SYNC_GETMYBOOKS:	
						SyncService.processBookTaskResponses().processGetMyBooksResponse(mService.getMyBooksResults());	 
						break;
					
				case MSG_SYNC_SETMYBOOKS:
						SyncService.processBookTaskResponses().processSetMyBooksResponse(mService.setMyBooksResults());
						break;

				case MSG_SYNC_GETMYNOTES:	{
						String bookid = intent.getStringExtra("bookid");
						SyncService.processSyncNoteTaskResponses().processGetMyNotesResponse(bookid, mService.getMyNotesResults());
					}
					break;
					
//				case MSG_SYNC_REFRESH:		
//						SyncService.processAvailableTaskResponses().processRefreshAvailableResponse(mApp.syncService().getRefreshAvailableResults()); 
//						break;
			
				case MSG_SYNC_SETMYNOTE:	{
						boolean result = intent.getBooleanExtra("result", false);
						String bookid = intent.getStringExtra("bookid");
						int pagenumber = intent.getIntExtra("pagenumber", -1);
						
						SyncService.processSyncNoteTaskResponses().processSetMyNoteResponse(result, bookid, pagenumber);
					}
				
					break;
				
				case MSG_SYNC_REMOVEMYNOTE:	{
						boolean result = intent.getBooleanExtra("result", false);
						String bookid = intent.getStringExtra("bookid");
						int pagenumber = intent.getIntExtra("pagenumber", -1);
						
						SyncService.processSyncNoteTaskResponses().processRemoveMyNoteResponse(result, bookid, pagenumber);
					}
					
					break;
				
				case MSG_SYNC_GETMYBOOKMARKS:	{
						String bookid = intent.getStringExtra("bookid");
						SyncService.processBookmarkTaskResponses().processGetMyBookmarksResponse(bookid, mService.getMyBookmarksResults()); 
					}
					break;
					
				case MSG_SYNC_SETMYBOOKMARKS: {
						boolean result = intent.getBooleanExtra("result", false);
			
						SyncService.processBookmarkTaskResponses().processSetMyBookmarksResponse(result);
					}
				
					break;
					
				case MSG_SYNC_SETMYANNOTATION:	{
					boolean result = intent.getBooleanExtra("result", false);
					String bookid = intent.getStringExtra("bookid");
					int pagenumber = intent.getIntExtra("pagenumber", -1);
					
					SyncService.processAnnotationTaskResponses().processSetMyAnnotationResponse(result, bookid, pagenumber);
				}
			
				break;
				
				case MSG_SYNC_GETMYANNOTATIONS:	{
						String bookid = intent.getStringExtra("bookid");
						SyncService.processAnnotationTaskResponses().processGetMyAnnotationsResponse(bookid, mService.getMyAnnotationsResults()); 
					}
					break;
					
				case MSG_SYNC_REMOVEMYANNOTATION:	{
						boolean result = intent.getBooleanExtra("result", false);
						String bookid = intent.getStringExtra("bookid");
						int pagenumber = intent.getIntExtra("pagenumber", -1);
						
						SyncService.processAnnotationTaskResponses().processRemoveMyAnnotationResponse(result, bookid, pagenumber);
					}
				
					break;
//				case MSG_SYNC_DELETEBOOK:	{
//						String bookid = intent.getStringExtra("bookid");
//						
//						SyncService.processBookTaskResponses().processDeleteBookResponse(bookid);
//					}
//				
//					break;
					
//				case MSG_MULTINOTES_GETALLNOTES:	{
//						String book_id = intent.getStringExtra("bookid");
//
//						SyncService.processMultiNoteTaskResponses().processGetAllNotesResponse(book_id, mService.getGetAllNotesResults());
//					}
//
//					break;
                case MSG_MULTINOTES_GETNOTESUPDATES:	{
                    String book_id = intent.getStringExtra("bookid");

                    SyncService.processMultiNoteTaskResponses().processGetNotesUpdatesResponse(book_id, mService.getGetNotesUpdatesResults());
                }

                break;

                case MSG_MULTINOTES_SAVENOTES: {
                    String book_id = intent.getStringExtra("bookid");

                    SyncService.processMultiNoteTaskResponses().processSaveNotesResponse(book_id, mService.getSaveNotesResults());
                }
			}
		}
	}

}