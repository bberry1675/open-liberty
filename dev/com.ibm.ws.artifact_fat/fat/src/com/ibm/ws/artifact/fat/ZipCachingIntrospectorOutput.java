/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.artifact.fat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

public class ZipCachingIntrospectorOutput{
    
    String introspectorDescription;
    Map<String, PropertyIntrospection> entryCacheSettings;
    Map<String, PropertyIntrospection> zipReaperSettings;
    Map<String, String> ActiveAndCachedZipFileHandles;

    class PropertyIntrospection{

        private final String propertyDescription, propertyName, propertyValue, propertyDefault;

        public PropertyIntrospection(String propDesc, String propName, String propValue, String propDefault){
            this.propertyDescription = propDesc;
            this.propertyName = propName;
            this.propertyValue = propValue;
            this.propertyDefault = propDefault;
        }

        public String getPropertyDescription(){
            return this.propertyDescription;
        }
        public String getPropertyName(){
            return this.propertyName;
        }
        public String getPropertyValue(){
            return this.propertyValue;
        }
        public String getPropertyDefault(){
            return this.propertyDefault;
        }

    }


    private PropertyIntrospection parsePropertyLine(String description, String line){
        String[] splitLine = line.split("\\s+[\\[\\]\\s]+\\s*");
        return new PropertyIntrospection(description, splitLine[1], splitLine[2], splitLine[3]);
    }

    public ZipCachingIntrospectorOutput(InputStream in) throws IOException{
        BufferedReader zipCachingReader = new BufferedReader(new InputStreamReader(in));
        String currentLine;

        
        currentLine = zipCachingReader.readLine();//= "The description of this introspector:"

        if(currentLine == null){
            throw new IOException("Could not read line from Zip Caching Introspector output file");
        }

        currentLine = zipCachingReader.readLine();//= "Liberty zip file caching diagnostics"
        introspectorDescription = currentLine;

        currentLine = zipCachingReader.readLine();//= EMPTY

        currentLine = zipCachingReader.readLine();//= "Zip Caching Service:"

        currentLine = zipCachingReader.readLine();//= EMPTY

        currentLine = zipCachingReader.readLine();//= "Entry Cache Settings:"
        
        entryCacheSettings = new HashMap<String,PropertyIntrospection>();
        PropertyIntrospection tempHolder;

        currentLine = zipCachingReader.readLine();//= First setting description or blank line
        while(!currentLine.equals("")){
            String propdes = currentLine;
            currentLine = zipCachingReader.readLine();//= "[ prop_name ] [ prop_value ] [[ prop_default ]]"
            tempHolder = parsePropertyLine(propdes, currentLine);
            entryCacheSettings.put(tempHolder.getPropertyName(),tempHolder);
            currentLine = zipCachingReader.readLine();//= next setting description or EMPTY
        }

        currentLine = zipCachingReader.readLine();//= "Zip Reaper Settings:"

        zipReaperSettings = new HashMap<String, PropertyIntrospection>();

        currentLine = zipCachingReader.readLine();//= First reaper setting description or EMPTY
        while(!currentLine.equals("")){
            String propdes = currentLine;
            currentLine = zipCachingReader.readLine();//="[ prop_name ] [ prop_value ] [[ prop_default ]]"
            tempHolder = parsePropertyLine(propdes, currentLine);
            zipReaperSettings.put(tempHolder.getPropertyName(),tempHolder);
            currentLine = zipCachingReader.readLine();//= next setting description or EMPTY
        }

        currentLine = zipCachingReader.readLine();//= "The entry cache is a cache of small zip file entries."
        currentLine = zipCachingReader.readLine();//= "The entry cache is disabled if either setting is 0."
        currentLine = zipCachingReader.readLine();//= EMPTY
        currentLine = zipCachingReader.readLine();//= "The zip reaper is a service which delays closes of zip files."
        currentLine = zipCachingReader.readLine();//= "The zip reaper is disabled if the maximum pending closes setting is 0."
        currentLine = zipCachingReader.readLine();//= EMPTY
        
        currentLine = zipCachingReader.readLine();//= "Active and Cached ZipFile Handles:"

        ActiveAndCachedZipFileHandles = new HashMap<String,String>();

        currentLine = zipCachingReader.readLine();//= "  ** NONE **" or "ZipFileHandle@x0000000 (LOCATION,#)"
        while(!currentLine.equals("") && !currentLine.equals("  ** NONE **")){
            //can be the handle introspect or the toString() of ZipFileHandle

            currentLine = zipCachingReader.readLine();//= "ZipFileHandle@x0000000 (LOCATION,#)" or EMPTY
        }


        zipCachingReader.close();
    }



}