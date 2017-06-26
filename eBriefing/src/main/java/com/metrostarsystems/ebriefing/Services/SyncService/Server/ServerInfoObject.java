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

package com.metrostarsystems.ebriefing.Services.SyncService.Server;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkAnnotation;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkPaint;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Ink.InkPath;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ServerInfoObject extends AbstractSyncObject {
	
	private static final String TAG = ServerInfoObject.class.getSimpleName();
	
	public ArrayList<ServerInfoTag> mServerInfo = new ArrayList<ServerInfoTag>();
	public ArrayList<ServerInfoTag> mFeatures = new ArrayList<ServerInfoTag>();
	public ArrayList<ServerInfoTag> mPlatforms = new ArrayList<ServerInfoTag>();
	
	public ServerInfoObject(boolean valid) {
		super(valid);
	}
	
	
	public String feature(String name, String tag) {
		for(int index = 0; index < mFeatures.size(); index++) {
			ServerInfoTag feature = mFeatures.get(index);
					
			if(feature.name().equalsIgnoreCase(name)) {
				return feature.value(tag);
			}
		}
				
		return "";
	}
	
	
	public ServerInfoObject(SoapObject response) {
		super(true);
		 
		String xml = response.getProperty(0).toString();

		
		xml = xml.replaceAll("\\s","");
		
		parse(xml);
	}

	private void parse(String xml) {
		XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            
            StringReader reader = new StringReader(xml);
 
            parser.setInput(reader);
 
            String tag_name 	= "";
        	String tag_value 	= "";
        	String tag_end 		= "";
        	
            int eventType = parser.getEventType();
            
            ServerInfoType type = ServerInfoType.INFO_SERVER_INFO;
            
            ServerInfoTag.Builder server_tag_builder = null;
            ServerInfoTag.Builder tag_builder = null;
            
            while(eventType != XmlPullParser.END_DOCUMENT) {
            	
                switch(eventType) {
	                case XmlPullParser.START_TAG:
	                	
	                	tag_name = parser.getName();
	                	tag_value = "";
	                	tag_end = "";
	                	
	                	if(tag_name.equalsIgnoreCase("ServerInfo")) {
	                		server_tag_builder = new ServerInfoTag.Builder();
	                		type = ServerInfoType.INFO_SERVER_INFO;
	                	} else if(tag_name.equalsIgnoreCase("Feature")) {
	                		tag_builder = new ServerInfoTag.Builder();
	                		type = ServerInfoType.INFO_FEATURE;
	                	} else if(tag_name.equalsIgnoreCase("Platform")) {
	                		tag_builder = new ServerInfoTag.Builder();
	                		type = ServerInfoType.INFO_PLATFORM;
	                	}
	                	
	                    break;
	 
	                case XmlPullParser.TEXT:
	                	
	                    tag_value = parser.getText();
	                    break;
	 
	                case XmlPullParser.END_TAG:
	                	
	                	tag_end = parser.getName();
	                	
	                	if(!tag_value.isEmpty()) {
	                		
		                	if(type == ServerInfoType.INFO_SERVER_INFO) {
		                		
		                		if(tag_end.equalsIgnoreCase("Name")) {
		                			server_tag_builder.name(tag_value);
		                		} else {
		                			server_tag_builder.addTag(tag_name, tag_value);
		                		}
		                	} else {
		                		if(tag_end.equalsIgnoreCase("Name")) {
		                			tag_builder.name(tag_value);
		                		} else {
		                			tag_builder.addTag(tag_name, tag_value);
		                		}
		                	}
		                	
		                	tag_value = "";
	                	}
	                	
	                	if(tag_end.equalsIgnoreCase("ServerInfo")) {
	                		mServerInfo.add(server_tag_builder.build());
	                	} else if(tag_end.equalsIgnoreCase("Feature")) {
	                		mFeatures.add(tag_builder.build());
	                	} else if(tag_end.equalsIgnoreCase("Platform")) {
	                		mPlatforms.add(tag_builder.build());
	                	}
	                	
	                    break;
	 
	                default:
	                    break;
                }
                eventType = parser.next();
            }
 
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            setValid(false);
        } catch (IOException e) {
            e.printStackTrace();
            setValid(false);
        } catch (NumberFormatException e) {
        	e.printStackTrace();
        	setValid(false);
        }
        
//        Log.i(TAG, "Show");
       
	}
	
	public static enum ServerInfoType {
		INFO_SERVER_INFO,
		INFO_FEATURE,
		INFO_PLATFORM;
	}
}
