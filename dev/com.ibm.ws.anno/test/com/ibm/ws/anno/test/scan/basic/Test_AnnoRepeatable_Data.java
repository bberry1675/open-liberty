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

package com.ibm.ws.anno.test.scan.basic;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_EJB;
import com.ibm.ws.anno.test.scan.Test_Base;
import com.ibm.ws.anno.test.utils.TestLocalization;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

/**
 * Test data for testing @Repeatable annotation cases (added in Java 8).
 * */
public class Test_AnnoRepeatable_Data {
    public static final String EAR_NAME = "Repeatable.ear";
    public static final String EAR_SIMPLE_NAME = "Repeatable";

    public static final String EJBJAR_NAME = "Repeatable.jar";
    public static final String EJBJAR_SIMPLE_NAME = "Repeatable";

    public static ClassSource_Specification_Direct_EJB
        createClassSourceSpecification(ClassSource_Factory classSourceFactory) {

        ClassSource_Specification_Direct_EJB ejbSpecification =
            classSourceFactory.newEJBDirectSpecification(EAR_SIMPLE_NAME, EJBJAR_SIMPLE_NAME, Test_Base.JAVAEE_MOD_CATEGORY_NAME);

        ejbSpecification.setModulePath(TestLocalization.getClassesPath());

        ejbSpecification.setRootClassLoader(Test_AnnoBasic_Data.class.getClassLoader());

        return ejbSpecification;
    }
}
