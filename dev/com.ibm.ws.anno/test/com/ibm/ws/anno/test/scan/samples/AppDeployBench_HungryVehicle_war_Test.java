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

public class AppDeployBench_HungryVehicle_war_Test extends Test_Base {

    @Override
    public ClassSource_Specification_Direct_WAR createClassSourceSpecification(ClassSource_Factory factory) {
        return AppDeployBench_HungryVehicle_war_Data.createClassSourceSpecification(factory);
    }

    //

    @Override
    public String getAppName() {
        return AppDeployBench_HungryVehicle_war_Data.EAR_NAME;
    }

    @Override
    public String getAppSimpleName() {
        return AppDeployBench_HungryVehicle_war_Data.EAR_SIMPLE_NAME;
    }

    @Override
    public String getModName() {
        return AppDeployBench_HungryVehicle_war_Data.WAR_NAME;
    }

    @Override
    public String getModSimpleName() {
        return AppDeployBench_HungryVehicle_war_Data.WAR_SIMPLE_NAME;
    }

    //

    @Test
    public void testAppDeployBench_HungryVehicle_war_BASE() throws Exception {
        runSuiteTest( getBaseCase() ); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_SINGLE_JANDEX() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_JANDEX); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_SINGLE_JANDEX_FULL() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_JANDEX_FULL); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_MULTI() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_MULTI_JANDEX() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_JANDEX); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_MULTI_JANDEX_FULL() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_JANDEX_FULL); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_SINGLE_WRITE() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_WRITE); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_SINGLE_READ() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_READ); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_MULTI_WRITE() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_WRITE); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_MULTI_READ() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_READ); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_SINGLE_WRITE_ASYNC() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.SINGLE_WRITE_ASYNC); // 'runSuiteTest' throws Exception
    }

    @Test
    public void testAppDeployBench_HungryVehicle_war_MULTI_WRITE_ASYNC() throws Exception {
        runSuiteTest(TestOptions_SuiteCase.MULTI_WRITE_ASYNC); // 'runSuiteTest' throws Exception
    }
}