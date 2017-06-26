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

package com.metrostarsystems.ebriefing.Data.Cache;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.metrostarsystems.ebriefing.Data.Cache.Files.FileCopyHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileReadHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileWriteHandle;
import com.metrostarsystems.ebriefing.Data.Cache.Files.AbstractFileHandle.FileLocation;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileReadHandle.FileReadType;
import com.metrostarsystems.ebriefing.Data.Cache.Files.FileWriteHandle.FileWriteType;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class Cache {
	
	public static final long					SECOND										= 1000;
	public static final long					MINUTE										= SECOND * 60;
	public static final long					HOUR										= MINUTE * 60;
	public static final long					DAY											= HOUR * 24;
	public static final long					WEEK										= DAY * 7;
	public static final long					YEAR										= DAY * 365;
	
	public static final long					BYTE										= 1;
	public static final long					K_BYTE										= BYTE * 1024;
	public static final long					M_BYTE										= K_BYTE * 1024;
	public static final long					G_BYTE										= M_BYTE * 1024;
	public static final long					T_BYTE										= G_BYTE * 1024;
	public static final long					P_BYTE										= T_BYTE * 1024;
	
	public static void writeFile(FileWriteHandle handle) {
		writeFile(handle, handle.data());
	}
	
	public static void writeFile(FileWriteHandle handle, Object data) {
		if(handle.valid()) {
			
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			
			try {
				fos = new FileOutputStream(handle.file(), false);
				
				oos = new ObjectOutputStream(fos);
		
				oos.writeObject(data);
				oos.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(oos != null) {
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			
		}
	}
	
	public static Object readFile(FileReadHandle handle, boolean checkExpired) throws IOException, ClassNotFoundException, EOFException {
		Object object = null;
	
		if(handle.valid() && handle.doesFileExist()) {
				
			if(checkExpired) {
				if(handle.isExpired()) {
					return null;	
				}
			}
			
			FileInputStream fis = new FileInputStream(handle.file());
			
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			object = ois.readObject();
			
			if(ois != null) {
				ois.close();
			}
				
			if(fis != null) {
				fis.close();
			}
			
		}
	
		return object;
	}
	
	public static Object readFile(FileReadHandle handle) throws IOException, ClassNotFoundException, EOFException {
		Object object = null;
	
		if(handle.valid() && handle.doesFileExist()) {
				
			FileInputStream fis = new FileInputStream(handle.file());
			
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			object = ois.readObject();
			
			if(ois != null) {
				ois.close();
			}
				
			if(fis != null) {
				fis.close();
			}
		}
	
		return object;
	}
	
	public static void appendFile(FileWriteHandle handle) throws IOException {
		if(handle.valid()) {
			FileOutputStream fos = handle.appendOutputStream();
				
			ObjectOutputStream oos = new ObjectOutputStream(fos);
	
			oos.writeObject(handle.data());
			oos.flush();
			oos.close();
			fos.close();
		}
	}
	
	public static void writeText(FileWriteHandle handle) {
		if(!handle.valid()) {
			return;
		}
		
		try {
			//Now create the file in the directory and write the contents into it
			FileOutputStream fOut = handle.outputStream(true);
			
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write((String) handle.data());
			osw.flush();
			osw.close();
		} catch (Exception e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
	
	public static void writeTextStream(FileOutputStream stream, Object data) {
		if(stream == null) {
			return;
		}
		
		try {
			stream.write(((String) data).getBytes());
			stream.flush();
		} catch (Exception e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
	
	public static void appendText(FileWriteHandle handle) {
		if(!handle.valid()) {
			return;
		}
		
		try {
			//Now create the file in the directory and append the contents into it
			FileOutputStream fOut = handle.appendOutputStream(true);
			
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write((String) handle.data());
			osw.flush();
			osw.close();
		} catch (Exception e) {
			Log.e("Exception", "File append failed: " + e.toString());
		}
	}
	
	public static ArrayList<String> readText(FileReadHandle handle, ArrayList<String> values) {
		//Get the text file
		if(handle.valid()) {
			
			File file = handle.file();
	
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
			    String line;
	
			    while ((line = br.readLine()) != null) {
			       values.add(line);
			    }
			    
			    br.close();
			} catch (IOException e) {
			    //You'll need to add proper error handling here
			}
			
			return values;
		}
		
		return new ArrayList<String>();	
	}
	
	public static String readText(FileReadHandle handle, String values) {
		//Get the text file
		if(handle.valid()) {
		
			File file = handle.file();
	
			//Read text from file
			StringBuilder sb = new StringBuilder();
	
			try {
			    BufferedReader br = new BufferedReader(new FileReader(file));
			    String line;
	
			    while ((line = br.readLine()) != null) {
			       sb.append(line);
			    }
			    
			    br.close();
			} catch (IOException e) {
			    //You'll need to add proper error handling here
			}
			
			return values = sb.toString();
		}
		
		return "";
	}
	
	public static void writeImage(FileWriteHandle handle, int quality) {
		if(handle.valid()) {
			try {
		          FileOutputStream out = handle.outputStream();
		          
		          if(handle.fileName().contains(".png")) {
		        	  ((Bitmap) handle.data()).compress(CompressFormat.PNG, quality, out);
		          } else if(handle.fileName().contains(".jpg") || handle.fileName().contains(".jpeg")) {
		        	  ((Bitmap) handle.data()).compress(CompressFormat.JPEG, quality, out);
		          } else {
		        	  ((Bitmap) handle.data()).compress(CompressFormat.WEBP, quality, out);
		          }
		          out.flush();
		          out.close();
		      } catch(Exception e) {
		    	  e.printStackTrace();
		      }
		}
	}
	
	public static void writeImage(String filePath, Bitmap bitmap, int quality) {
		if(!filePath.isEmpty()) {
			try {
		          FileOutputStream out = new FileOutputStream(filePath);
		          
		          if(filePath.contains(".png")) {
		        	  bitmap.compress(CompressFormat.PNG, quality, out);
		          } else if(filePath.contains(".jpg") || filePath.contains(".jpeg")) {
		        	  bitmap.compress(CompressFormat.JPEG, quality, out);
		          } else {
		        	  bitmap.compress(CompressFormat.WEBP, quality, out);
		          }
		          out.flush();
		          out.close();
		      } catch(Exception e) {
		    	  e.printStackTrace();
		      }
		}
	}
	
	public static void writeImage(FileWriteHandle handle) {
		writeImage(handle, 100);
	}
	
	public static Bitmap readImage(FileReadHandle handle) {
		
		String filepath = handle.path() + File.separator + handle.fileName();
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		
		return bitmap;
	}
	
	// Asset Methods ----------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Copies all files from the assets folder
	 * Usage: FileHandle object = new FileHandle(context);
	 *        object.setLocationTo(FileLocation.*);
	 *        object.setDirTo(directory);
	 * @param storage object that contains information about where to put the assets
	 */
	public static void copyAssets(FileCopyHandle handle) {
	    AssetManager assetManager = handle.context().getAssets();
	    
	    String[] files = null;
	    
	    try {
	    	// Retrieve the list of files from the assetmanager
	        files = assetManager.list("");
	    } catch (Exception e) {
	    	Log.e("Cache", "Unable to retrieve list of files from assetmanager.", e);
	    }
	    
	    // Loop through the files
	    for(String filename : files) {
	    	if (filename.equals("images") || filename.equals("kioskmode") ||
	                filename.equals("sounds") || filename.equals("webkit")) {
	            Log.i("Cache", "Skipping folder " + filename);
	            continue;
	        }
	    	//Log.d("Cache", "Copying asset to " + handle.pathTo() + " file: " + filename);
	    	
	        InputStream in = null;
	        FileOutputStream out = null;
	        
	        try {
	          in = assetManager.open(filename);
	          
	          File outFile = new File(handle.path(), filename);
	          
	          out = new FileOutputStream(outFile);
	          
	          // Copy the file
	          copyFileStream(in, out);
	          
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(Exception e) {
	        	Log.e("Cache", "Failed to copy asset file: " + filename, e);
	        }       
	    }
	}
	// End Asset Methods ------------------------------------------------------------------------------------------------------------------------------------
	
	public static void copyFile(FileCopyHandle handle) {
		if(handle.validCopyFile()) {
			File file = handle.readFile();
			
			if(file.exists()) {
				Log.i("Cache", "Copying file: " + handle.fileName());
				
				try {
					FileInputStream in = new FileInputStream(file);
					
					FileOutputStream out = handle.outputStream(true);
		          
		        	copyFileStream(in, out);
		        	
		        	in.close();
		        	in = null;
		        	out.flush();
		        	out.close();
		        	out = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void moveFile(FileReadHandle readHandle, FileWriteHandle writeHandle) {
		File inFile = readHandle.file();
		
		try {
			File outFile = writeHandle.file();
	          
			inFile.renameTo(outFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
					
	public static void copyFiles(FileReadHandle readHandle, FileWriteHandle writeHandle) {
		File dirs = new File(readHandle.path());
		
		File[] files = null;
		
		if(dirs.exists()) {
		    files = dirs.listFiles();
		    
		    for(File file : files) {
		    	InputStream in = null;
		        FileOutputStream out = null;
		        try {
		          in = new FileInputStream(file);
		          
		          File outFile = new File(writeHandle.path(), file.getName());
		          
		          out = new FileOutputStream(outFile);
		          
		          copyFileStream(in, out);
		          
		          in.close();
		          in = null;
		          out.flush();
		          out.close();
		          out = null;
		        } catch(Exception e) {
		            Log.e("tag", "Failed to copy asset file: " + file.getName(), e);
		        }       
		    }
		}
	}
	

	
	public static void moveFiles(FileReadHandle readHandle, FileWriteHandle writeHandle) {
		File dirs = new File(readHandle.path());
		
		File[] files = null;
		
		if(dirs.exists()) {
		    files = dirs.listFiles();
		    
		    for(File file : files) {
		    	
		    	try {
		    		File outFile = new File(writeHandle.path(), file.getName());
		          
		    		file.renameTo(outFile);
		        } catch(Exception e) {
		            //Log.e("tag", "Failed to copy asset file: " + filename, e);
		        }       
		    }
		}
	}
	

	
	private static void copyFileStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[(int) K_BYTE];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	public static void loadImage(FileReadHandle handle, ImageView view, int placeHolder) {
		if(!handle.hasFileName()) {
			new ImageLoadTask(handle, view, placeHolder).execute(handle.fileName());
		} else {
			view.setBackgroundResource(placeHolder);
		}
		
		//Picasso.with(handle.context()).load(handle.fileFrom()).placeholder(placeHolder).into(view);
	}
	
	public static void setImage(Context context, ImageView view, Uri url, int placeHolder) {
        try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), url);
			
			if(bitmap != null) {
				view.setImageBitmap(bitmap);
			} else {
				view.setBackgroundResource(placeHolder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class ImageLoadTask extends AsyncTask<String, Boolean, Bitmap> {
		
		private ImageView mImage;
		private int mPlaceHolder;
		private FileReadHandle mReadHandle;
		
		public ImageLoadTask(FileReadHandle handle, ImageView image, int placeHolder) {
			mImage = image;
			mPlaceHolder = placeHolder;
			mReadHandle = handle;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			
			mReadHandle.setFileName(params[0]);
	
			return readImage(mReadHandle);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if(mImage != null) {
				mImage.setImageBitmap(result);
			} else {
				mImage.setBackgroundResource(mPlaceHolder);
			}
		}
	}
	
	public static class WriteFileTask extends AsyncTask<FileWriteHandle, Void, Void> {

		private String mMessage;
		private Context mContext;
		
		public WriteFileTask(Context context, String message) {
			mContext = context;
			mMessage = message;
		}
		
		@Override
		protected Void doInBackground(FileWriteHandle... params) {
		
			for(int index = 0; index < params.length; index++) {
				try {
					params[0].write();
				} catch (Exception e) {
					
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			displayToast(mContext, mMessage);
		}
	}
	
	public static FileWriteHandle saveExternalDataHandle(Context context, String fileName) {
		return (FileWriteHandle) new FileWriteHandle.Builder(context)
								.location(FileLocation.EXTERNAL)
								.type(FileWriteType.DATA)
								.fileName(fileName)
								.build();
	}

	public static FileReadHandle loadExternalHandle(Context context, String fileName) {
		return (FileReadHandle) new FileReadHandle.Builder(context)
								.location(FileLocation.EXTERNAL)
								.fileName(fileName)
								.build();
	}

	public static FileReadHandle loadExternalDataHandle(Context context, String fileName) {
		return (FileReadHandle) new FileReadHandle.Builder(context)
								.location(FileLocation.EXTERNAL)
								.type(FileReadType.DATA)
								.fileName(fileName)
								.build();
	}
	
	// External String Data
	
	public static FileReadHandle loadExternalTextHandle(Context context, String fileName) {
		return (FileReadHandle) new FileReadHandle.Builder(context)
								.location(FileLocation.EXTERNAL)
								.type(FileReadType.TEXT_STRING)
								.fileName(fileName)
								.build();
	}
	
	public static FileWriteHandle saveExternalTextHandle(Context context, String fileName) {
		return (FileWriteHandle) new FileWriteHandle.Builder(context)
								.location(FileLocation.EXTERNAL)
								.type(FileWriteType.DATA)
								.fileName(fileName)
								.build();
	}
	
	public final static void displayToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
