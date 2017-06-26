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

public class Tags {

	public static final String BOOK_ID_TAG 			= "bookid";
	public static final String PAGE_NUMBER_TAG 		= "pagenumber";
	public static final String NOTE_ID_TAG 			= "noteid";
	public static final String CHAPTER_ID_TAG		= "chapterid";
	
	public static final String EDIT_NOTE_TAG		= "editnote";
	public static final String EDIT_BOOKMARK_TAG	= "editbookmark";
	
	
	public static final String SYNC_PREFERENCE								= "sync";
	
	public static final String CORE_GET_BOOKS_RESPONSE_ID								= "ID";
	public static final String CORE_GET_BOOKS_RESPONSE_TITLE							= "Title";
	public static final String CORE_GET_BOOKS_RESPONSE_DESCRIPTION						= "Description";
	public static final String CORE_GET_BOOKS_RESPONSE_CHAPTER_COUNT					= "ChapterCount";
	public static final String CORE_GET_BOOKS_RESPONSE_PAGE_COUNT						= "PageCount";
	public static final String CORE_GET_BOOKS_RESPONSE_SMALL_IMAGE_URL					= "SmallImageURL";
	public static final String CORE_GET_BOOKS_RESPONSE_LARGE_IMAGE_URL					= "LargeImageURL";
	public static final String CORE_GET_BOOKS_RESPONSE_IMAGE_VERSION					= "ImageVersion";
	public static final String CORE_GET_BOOKS_RESPONSE_VERSION							= "Version";
	public static final String CORE_GET_BOOKS_RESPONSE_DATE_ADDED						= "DateAdded";
	public static final String CORE_GET_BOOKS_RESPONSE_DATE_MODIFIED					= "DateModified";
	
	public static final String CORE_GET_CHAPTERS_REQUEST_ID								= "bookID";
	
	public static final String CORE_GET_CHAPTERS_RESPONSE_ID							= "ID";
	public static final String CORE_GET_CHAPTERS_RESPONSE_TITLE							= "Title";
	public static final String CORE_GET_CHAPTERS_RESPONSE_DESCRIPTION					= "Description";
	public static final String CORE_GET_CHAPTERS_RESPONSE_PAGE_COUNT					= "PageCount";
	public static final String CORE_GET_CHAPTERS_RESPONSE_SMALL_IMAGE_URL				= "SmallImageURL";
	public static final String CORE_GET_CHAPTERS_RESPONSE_LARGE_IMAGE_URL				= "LargeImageURL";
	public static final String CORE_GET_CHAPTERS_RESPONSE_IMAGE_VERSION					= "ImageVersion";
	public static final String CORE_GET_CHAPTERS_RESPONSE_FIRST_PAGE_ID					= "FirstPageID";
	
	// Syncing Notes ---------------------------------------------------------------------------------------

    // Notes V1 --------------------------------------------------------------------------------------------
	// Get My Notes Request
	public static final String SYNC_GET_MY_NOTES_REQUEST_BOOK_ID						= "bookId";
	
	// Get My Notes Response
	public static final String SYNC_GET_MY_NOTES_RESPONSE_NOTE_BOOK_ID					= "BookId";
	public static final String SYNC_GET_MY_NOTES_RESPONSE_NOTE_BOOK_VERSION				= "BookVersion";
	public static final String SYNC_GET_MY_NOTES_RESPONSE_NOTE_PAGE_ID					= "PageId";
	public static final String SYNC_GET_MY_NOTES_RESPONSE_NOTE_VALUE_URL				= "ValueUrl";
	public static final String SYNC_GET_MY_NOTES_RESPONSE_NOTE_MODIFIED_DATE			= "ModifiedUtc";
	public static final String SYNC_GET_MY_NOTES_RESPONSE_NOTE_REMOVED					= "Removed";
	
	// Set My Note Request
	public static final String SYNC_SET_MY_NOTE_REQUEST_BOOK_ID							= "bookId";
	public static final String SYNC_SET_MY_NOTE_REQUEST_BOOK_VERSION					= "bookVersion";
	public static final String SYNC_SET_MY_NOTE_REQUEST_PAGE_ID							= "pageId";
	public static final String SYNC_SET_MY_NOTE_REQUEST_MODIFIED_DATE					= "modifiedUtc";
	public static final String SYNC_SET_MY_NOTE_REQUEST_CONTENT							= "content";
	
	// Remove My Note Request
	public static final String SYNC_REMOVE_MY_NOTE_REQUEST_BOOK_ID						= "bookId";
	public static final String SYNC_REMOVE_MY_NOTE_REQUEST_PAGE_ID						= "pageId";
	public static final String SYNC_REMOVE_MY_NOTE_REQUEST_MODIFIED_DATE				= "modifiedUtc";


    // MultiNotes
    // Get All Notes Request
    public static final String MULTINOTES_GET_ALL_NOTES_REQUEST_BOOK_ID                 = "bookId";
    public static final String MULTINOTES_GET_ALL_NOTES_REQUEST_START_OFFSET            = "startOffset";
    public static final String MULTINOTES_GET_ALL_NOTES_REQUEST_PAGE_SIZE               = "pageSize";

    // Get All Notes Response
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_NOTE                   = "Note";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_NOTE_ID                = "NoteId";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_BOOK_ID                = "BookId";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_BOOK_VERSION           = "BookVersion";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_PAGE_ID                = "PageId";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_DATE_CREATED           = "Created";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_DATE_MODIFIED          = "Modified";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_NOTE_TEXT              = "NoteText";
    public static final String MULTINOTES_GET_ALL_NOTES_RESPONSE_IS_DELETED             = "IsDeleted";

    // Get Notes Updates
    public static final String MULTINOTES_GET_NOTES_UPDATES_REQUEST_BOOK_ID             = "bookId";
    public static final String MULTINOTES_GET_NOTES_UPDATES_REQUEST_DATE_LAST_SYNCED    = "LastSyncDateTime";

    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_NOTE               = "Note";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_NOTE_ID            = "NoteId";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_BOOK_ID            = "BookId";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_BOOK_VERSION       = "BookVersion";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_PAGE_ID            = "PageId";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_DATE_CREATED       = "Created";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_DATE_MODIFIED      = "Modified";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_NOTE_TEXT          = "NoteText";
    public static final String MULTINOTES_GET_NOTES_UPDATES_RESPONSE_IS_DELETED         = "IsDeleted";

    // Save Notes
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_NOTES                      = "notes";

    public static final String MULTINOTES_SAVE_NOTES_REQUEST_NOTE                       = "Note";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_NOTE_ID                    = "NoteId";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_BOOK_ID                    = "BookId";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_BOOK_VERSION               = "BookVersion";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_PAGE_ID                    = "PageId";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_DATE_CREATED               = "Created";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_DATE_MODIFIED              = "Modified";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_NOTE_TEXT                  = "NoteText";
    public static final String MULTINOTES_SAVE_NOTES_REQUEST_IS_DELETED                 = "IsDeleted";
	
	public static final String SYNC_BOOK_REQUEST_BOOK_ID						        = "BookId";
	public static final String SYNC_BOOK_REQUEST_BOOK_VERSION					        = "BookVersion";
	public static final String SYNC_BOOK_REQUEST_BOOK_FAVORITE					        = "IsFavorite";
	public static final String SYNC_BOOK_REQUEST_BOOK_MODIFIED_DATE				        = "ModifiedUtc";
	public static final String SYNC_BOOK_REQUEST_BOOK_REMOVED					        = "Removed";
	
	public static final String SYNC_GET_MY_ANNOTATIONS_REQUEST_BOOK_ID			        = "bookId";
	public static final String SYNC_GET_MY_ANNOTATIONS_REQUEST_PLATFORM			        = "platform";
	
	public static final String SYNC_REMOVE_MY_ANNOTATION_REQUEST_BOOK_ID		        = "bookId";
	public static final String SYNC_REMOVE_MY_ANNOTATION_REQUEST_PAGE_ID		        = "pageId";
	public static final String SYNC_REMOVE_MY_ANNOTATION_REQUEST_PLATFORM		        = "platform";
	public static final String SYNC_REMOVE_MY_ANNOTATION_REQUEST_MODIFIED_DATE	        = "modifiedUtc";
	
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_BOOK_ID			        = "bookId";
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_BOOK_VERSION		        = "bookVersion";
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_PAGE_ID			        = "pageId";
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_PLATFORM			        = "platform";
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_MODIFIED_DATE		        = "modifiedUtc";
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_TEXT_DATA			        = "textData";
	public static final String SYNC_SET_MY_ANNOTATION_REQUEST_IMAGE_DATA		        = "imageData";
	
	// Get My Annotations Response
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_BOOK_ID		= "BookId";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_BOOK_VERSION	= "BookVersion";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_PAGE_ID		= "PageId";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_PLATFORM		= "Platform";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_IMAGE_URL    = "ImageDataUrl";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_TEXT_URL		= "TextDataUrl";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_MODIFIED_DATE= "ModifiedUtc";
	public static final String SYNC_GET_MY_ANNOTATIONS_RESPONSE_ANNOTATION_REMOVED		= "Removed";
	
	
	
	// Sync Bookmarks
	
	// Get My Bookmarks Request
	public static final String SYNC_GET_MY_BOOKMARKS_REQUEST_BOOK_ID					= "bookId";
	
	// Get My Bookmarks Response
	public static final String SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_BOOK_ID			= "BookId";
	public static final String SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_BOOK_VERSION		= "BookVersion";
	public static final String SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_PAGE_ID			= "PageId";
	public static final String SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_VALUE			= "Value";
	public static final String SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_MODIFIED_DATE	= "ModifiedUtc";
	public static final String SYNC_GET_MY_BOOKMARKS_RESPONSE_BOOKMARK_REMOVED			= "Removed";
	
	// Set My Bookmarks Request
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_BOOKMARKS					= "bookmarks";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_BOOKMARK_OBJECT			= "BookmarkObj";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_BOOK_ID					= "BookId";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_BOOK_VERSION				= "BookVersion";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_PAGE_ID					= "PageId";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_VALUE						= "Value";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_MODIFIED_DATE				= "ModifiedUtc";
	public static final String SYNC_SET_MY_BOOKMARKS_REQUEST_REMOVED					= "Removed";
	
	// Set My Bookmarks Response
	public static final String SYNC_SET_MY_BOOKMARKS_RESPONSE_BOOK_ID					= "BookId";
	public static final String SYNC_SET_MY_BOOKMARKS_RESPONSE_BOOK_VERSION				= "BookVersion";
	public static final String SYNC_SET_MY_BOOKMARKS_RESPONSE_PAGE_ID					= "PageId";
	public static final String SYNC_SET_MY_BOOKMARKS_RESPONSE_VALUE						= "Value";
	public static final String SYNC_SET_MY_BOOKMARKS_RESPONSE_MODIFIED_DATE				= "modifiedUtc";
	public static final String SYNC_SET_MY_BOOKMARKS_RESPONSE_REMOVED					= "Removed";
	
	
}
