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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;

import org.jboss.shrinkwrap.api.ShrinkWrap;

import componenttest.annotation.Server;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

import com.ibm.websphere.simplicity.ShrinkHelper;

@RunWith(FATRunner.class)
public class FATReaperVerification{

    @Server(FATReaperVerification.serverName)
    public static LibertyServer server;
    public static final String serverName = "com.ibm.ws.artifact.zipReaper";
    private static final String[] appNames = {
        "jarneeder.war"
    };
    private static final int SECONDS_WAITING_FOR_DUMP = 30;
    private static final int ATTEMPTS_TO_RETRY  = 2;
    private DumpArchive dump = null;

    private static void logInfo(String methodName, String outputString){
        FATLogging.info(FATReaperVerification.class, methodName, outputString);
    }

    private DumpArchive getRecentServerDump() throws Exception{
        String methodName = "getRecentServerDump";
        logInfo(methodName, "Entering: " + methodName);

        DumpArchive dumpRef = DumpArchive.getMostRecentDumpArchive(server);
        int retryCount = 0;

        //retry logic if the server dump takes a long time
        while(dumpRef == null && retryCount < ATTEMPTS_TO_RETRY){
            logInfo(methodName, "Failed to get the server dump (retry #" + ++retryCount + "). Going to sleep.");
            Thread.sleep(1000 *SECONDS_WAITING_FOR_DUMP);

            dumpRef = DumpArchive.getMostRecentDumpArchive(server);
        }

        //if the retry logic still fails then fail the test
        Assert.assertNotNull("Server dump not found in directory: " + server.getServerRoot(), dumpRef);

        logInfo(methodName, "Exiting: " + methodName);

        return dumpRef;
    }


    @Before
    public void setUp() throws Exception{
        String methodName = "setUp";
        logInfo(methodName, "Entering: " + methodName);

        Assert.assertNotNull("Server reference is null in test class", server);

        ShrinkHelper.defaultApp(server, "jarneeder.war", "com.ibm.ws.artifact.fat.servlet");
        

        if(!server.isStarted()){
            server.startServer();
        }

        logInfo(methodName, "Exiting: " + methodName);
    }

    @After
    public void tearDown() throws Exception{
        String methodName = "tearDown";
        logInfo(methodName, "Entering: " + methodName);

        if(server != null && server.isStarted()){
            server.stopServer();
        }

        if(dump != null){
            dump.close();
        }

        logInfo(methodName, "Exiting: " + methodName);
    }

    @Test
    public void testServerDumpIncludesZipReader() throws Exception{
        String methodName = "testServerDumpIncludesZipReader";
        logInfo(methodName, "Entering: " + methodName);

        

        //send the dump action to the server
        server.executeServerScript("dump",null);
        //wait 30 seconds for the dump to be created in the server directory
        Thread.sleep(1000 * SECONDS_WAITING_FOR_DUMP);

        //get a ZipFile object to interact with the dump archive
        dump = getRecentServerDump();

        Assert.assertTrue(DumpArchive.ZIP_CACHING_INTROSPECTOR_FILE_NAME + " was not found in archive: " + dump.getName(), dump.doesZipCachingIntrospectorDumpExist());

        logInfo(methodName, "Exiting: " + methodName);
    }

    @Test
    public void testZipReaperDumpContainsAllZips() throws Exception{
        String methodName = "testZipReaperDumpContainsAllZips";
        logInfo(methodName, "Entering: " + methodName);

        logInfo(methodName, "Exiting: " + methodName);
    }

    
}