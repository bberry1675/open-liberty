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

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_EJB;
import com.ibm.ws.anno.test.utils.TestLocalization;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public class DT7_DT_jar_Data {
    public static final String EAR_NAME = "dt7.ear";
    public static final String EAR_SIMPLE_NAME = "dt7";
    
    public static final String EJBJAR_NAME = "daytrader-ee7-ejb.jar";
    public static final String EJBJAR_SIMPLE_NAME = "daytrader";

    public static ClassSource_Specification_Direct_EJB createClassSourceSpecification(ClassSource_Factory factory) {

        ClassSource_Specification_Direct_EJB ejbSpecification =
            factory.newEJBDirectSpecification(EAR_SIMPLE_NAME, EJBJAR_SIMPLE_NAME);
        
        String earPath = TestLocalization.putIntoData(EAR_NAME) + '/';

        String ejbJarPath = TestLocalization.putInto(earPath, EJBJAR_NAME);
        ejbSpecification.setModulePath(ejbJarPath);

        ejbSpecification.setRootClassLoader( DT7_DT_jar_Data.class.getClassLoader() );

        return ejbSpecification;
    }
}
