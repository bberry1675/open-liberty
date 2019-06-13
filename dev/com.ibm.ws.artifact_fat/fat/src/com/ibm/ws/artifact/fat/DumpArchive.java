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

import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilenameFilter;

import componenttest.topology.impl.LibertyServer;

import com.ibm.websphere.simplicity.log.Log;

public class DumpArchive extends ZipFile{

    public static String ZIP_CACHING_INTROSPECTOR_FILE_NAME = "ZipCachingIntrospector.txt";

    private static class DumpArchiveFilenameFilter implements FilenameFilter{

        private final String serverName;

        public DumpArchiveFilenameFilter(String serverName){
            this.serverName = serverName;
        }

        @Override
        public boolean accept(File dir, String name){
            return name.contains(serverName + ".dump");
        }
    }

    private static void logInfo(String methodName, String outputString){
        FATLogging.info(DumpArchive.class, methodName, outputString);
    }

    private DumpArchive(File dumpArchive) throws IOException{
        super(dumpArchive);
    }

    public boolean doesZipCachingIntrospectorDumpExist(){
        //file path to introspector outputs: /dump_***/introspections/

        //need to iterate overall entries
        Enumeration<? extends ZipEntry> e = entries();
        ZipEntry currentEntry = e.nextElement();
        boolean retValue = false;

        if(currentEntry == null){
            return false;
        }
        else{
            if(currentEntry.getName().contains(ZIP_CACHING_INTROSPECTOR_FILE_NAME)){
                return true;
            }
            while(e.hasMoreElements()){
                currentEntry = e.nextElement();
                if(currentEntry.getName().contains(ZIP_CACHING_INTROSPECTOR_FILE_NAME))
                    return true;
            }
        }

        return retValue;
    }

    public InputStream getZipCachingDumpStream() throws IOException{
        ZipEntry introspectorDump;
        if((introspectorDump = getEntry(ZIP_CACHING_INTROSPECTOR_FILE_NAME)) != null){
            return getInputStream(introspectorDump);
        }
        else{
            throw new ZipException(String.format("%s missing from Archive [%s]",ZIP_CACHING_INTROSPECTOR_FILE_NAME,this.getName()));
        }
    } 


    public static DumpArchive getMostRecentDumpArchive(LibertyServer server) throws IOException{
        if(server == null)
            return null;

        String serverRoot = server.getServerRoot();
        File serverDirectory = new File(serverRoot);
        File[] dumpArchives;
        int mostRecentIndex;

        if(serverDirectory.isDirectory()){
            dumpArchives = serverDirectory.listFiles(new DumpArchiveFilenameFilter(server.getServerName()));
            if(dumpArchives.length == 1){
                return new DumpArchive(dumpArchives[0]);
            }
            else if(dumpArchives.length == 0){
                return null;
            }
            else{
                mostRecentIndex = 0;
                for(int counter = 1; counter < dumpArchives.length; ++counter){
                    if(dumpArchives[counter].lastModified() > dumpArchives[mostRecentIndex].lastModified()){
                        mostRecentIndex = counter;
                    }
                }

                return new DumpArchive(dumpArchives[mostRecentIndex]);
            }
        }
        else{
            return null;
        }   
    }

}