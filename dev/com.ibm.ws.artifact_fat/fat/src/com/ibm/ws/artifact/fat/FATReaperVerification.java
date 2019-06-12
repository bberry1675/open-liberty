/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.artifact.fat;

import java.beans.Transient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;


@RunWith(FATRunner.class)
public class FATReaperVerification{

    private LibertyServer server = null;
    private final String serverName = "com.ibm.ws.artifact.zipReaper";

    private static void logInfo(String methodName, String outputString){
        FATLogging.info(FATReaperVerification.class, methodName, outputString);
    }

    @Before
    public void setUp() throws Exception{
        String methodName = "setUp";
        logInfo(methodName, "Entering: " + methodName);

        if(server == null){
            server = LibertyServerFactory.getLibertyServer(serverName);
        }
        
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

        logInfo(methodName, "Exiting: " + methodName);
    }

    @Test
    public void testServerDumpIncludesZipReader() throws Exception{
        String methodName = "testServerDumpIncludesZipReader";
        logInfo(methodName, "Entering: " + methodName);

        server.executeServerScript("dump",null);


        logInfo(methodName, "Exiting: " + methodName);
    }

    @Test
    public void testZipReaperDumpContainsAllZips() throws Exception{
        String methodName = "testZipReaperDumpContainsAllZips";
        logInfo(methodName, "Entering: " + methodName);

        logInfo(methodName, "Exiting: " + methodName);
    }

    
}