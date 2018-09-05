/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.anno.test.scan.samples;

import org.junit.Test;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_WAR;
import com.ibm.ws.anno.test.scan.TestOptions_SuiteCase;
import com.ibm.ws.anno.test.scan.Test_Base;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public class SecFVT_Servlet30_AnnPure_war_Test extends Test_Base {

    @Override
    public ClassSource_Specification_Direct_WAR createClassSourceSpecification(ClassSource_Factory factory) {
        return SecFVT_Servlet30_Data.createClassSourceSpecification_WAR(
            factory,
            SecFVT_Servlet30_Data.WAR_ANNPURE_SIMPLE_NAME,
            SecFVT_Servlet30_Data.WAR_ANNPURE_NAME);
    }

    //

    @Override
    public String getAppName() {
        return SecFVT_Servlet30_Data.EAR_NAME;
    }

    @Override
    public String getAppSimpleName() {
        return SecFVT_Servlet30_Data.EAR_SIMPLE_NAME;
    }

    @Override
    public String getModName() {
        return SecFVT_Servlet30_Data.WAR_ANNPURE_NAME;
    }

    @Override
    public String getModSimpleName() {
        return SecFVT_Servlet30_Data.WAR_ANNPURE_SIMPLE_NAME;
    }

    //

    @Test
    public void testSecFVT_Servlet30_AnnPure_war_BASE() throws Exception {
        runSuiteTest( getBaseCase() ); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_SINGLE_JANDEX() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_JANDEX); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_SINGLE_JANDEX_FULL() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_JANDEX_FULL); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_MULTI() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_MULTI_JANDEX() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_JANDEX); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_MULTI_JANDEX_FULL() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_JANDEX_FULL); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_SINGLE_WRITE() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_WRITE); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_SINGLE_READ() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_READ); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_MULTI_WRITE() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_WRITE); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_MULTI_READ() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_READ); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_SINGLE_WRITE_ASYNC() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_WRITE_ASYNC); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testSecFVT_Servlet30_AnnPure_MULTI_WRITE_ASYNC() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_WRITE_ASYNC); // 'runSuiteTest' throws Exception
    }
}