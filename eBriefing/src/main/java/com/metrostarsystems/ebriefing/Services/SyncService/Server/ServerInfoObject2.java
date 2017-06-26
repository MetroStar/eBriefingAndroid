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

import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerInfo;
import com.metrostarsystems.ebriefing.Services.SyncService.AbstractSyncObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject.ServerInfoType;

public class ServerInfoObject2 extends AbstractSyncObject {

	public ServerInfo				mServerInfo;
	public ArrayList<ServerFeature> mFeatures = new ArrayList<ServerFeature>();
	
	public ServerInfoObject2(boolean valid) {
		super(valid);
	}
	
	public ServerInfoObject2(SoapObject response) {
		super(true);
		 
		String xml = response.getProperty(0).toString();

		
		xml = xml.replaceAll("\\s","");
		
		parse(xml);
	}
	
	public ServerInfo serverInfo() { return mServerInfo; }
	
	public ArrayList<ServerFeature> features() { return mFeatures; }
	
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
            
            ServerInfoType type = null;
            
            ServerInfo.Builder server_builder = null;
            ServerFeature.Builder feature_builder = null;
            
            while(eventType != XmlPullParser.END_DOCUMENT) {
            	
                switch(eventType) {
	                case XmlPullParser.START_TAG:
	                	
	                	tag_name = parser.getName();
	                	tag_value = "";
	                	tag_end = "";
	                	
	                	if(tag_name.equalsIgnoreCase("ServerInfo")) {
	                		server_builder = new ServerInfo.Builder();
	                		type = ServerInfoType.INFO_SERVER;
	                	} else if(tag_name.equalsIgnoreCase("Feature")) {
	                		feature_builder = new ServerFeature.Builder();
	                		type = ServerInfoType.INFO_FEATURE;
	                	}
	                	
	                    break;
	 
	                case XmlPullParser.TEXT:
	                	
	                    tag_value = parser.getText();
	                    break;
	 
	                case XmlPullParser.END_TAG:
	                	
	                	tag_end = parser.getName();
	                	
	                	if(!tag_value.isEmpty()) {
	                		
		                	if(tag_end.equalsIgnoreCase("Name")) {
		                		feature_builder.name(tag_value);
		                	} else if(tag_end.equalsIgnoreCase("Release")) {
		                		try {
		                			server_builder.release(Integer.parseInt(tag_value));
		                		} catch(Exception e) {
		                			server_builder.release(1);
		                		}
		                	} else if(tag_end.equalsIgnoreCase("Version") && type == ServerInfoType.INFO_SERVER) {
		                		try {
		                			server_builder.version(Float.parseFloat(tag_value));
		                		} catch(Exception e) {
		                			server_builder.version(1.0f);
		                		}
		                	}else if(tag_end.equalsIgnoreCase("Version") && type == ServerInfoType.INFO_FEATURE) {
		                		try {
		                			feature_builder.version(Integer.parseInt(tag_value));
		                		} catch(Exception e) {
		                			feature_builder.version(1);
		                		}
		                	} else if(tag_end.equalsIgnoreCase("RelativePath")) {
		                		feature_builder.url(tag_value);
		                	}
		                	
		                	tag_value = "";
	                	}
	                	
	                	if(tag_end.equalsIgnoreCase("ServerInfo")) {
	                		mServerInfo = server_builder.build();
	                	} else if(tag_end.equalsIgnoreCase("Feature")) {
	                		mFeatures.add(feature_builder.build());
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
		INFO_SERVER,
		INFO_FEATURE,
		INFO_PLATFORM;
	}
}
