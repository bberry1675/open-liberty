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
package com.ibm.ws.anno.targets.internal;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;

import com.ibm.ws.anno.service.internal.AnnotationServiceImpl_Logging;
import com.ibm.wsspi.anno.targets.AnnotationTargets_Targets.AnnotationCategory;
import com.ibm.wsspi.anno.util.Util_InternMap;

/**
 * Utility for populating annotation targets from a JANDEX index file.
 *
 * While not a visitor, the convert is placed with the visitor classes
 * because the converter is to populate the targets data, using the jandex
 * information, exactly the same as when the class visitor populates the
 * targets data from collected class data.  (See {@link TargetsVisitorClassImpl#ClassData}.)
 */
public class TargetsVisitorJandexConverterImpl {
    // Logging ...

    protected static final Logger jandexLogger = AnnotationServiceImpl_Logging.ANNO_JANDEX_LOGGER;

    public static final String CLASS_NAME = TargetsVisitorJandexConverterImpl.class.getSimpleName();

    //

    /** Name of package classes.*/
    public static final String PACKAGE_INFO_CLASS_NAME = "package-info";

    /**
     * <p>Tell if a name is a package name.</p>
     * 
     * @param name The name which is to be tested. 
     */
    public static boolean isPackageName(String name) {
        return ( name.endsWith(PACKAGE_INFO_CLASS_NAME) );
    }

    /**
     * <p>Strip the package name from a qualified class name.</p>
     * 
     * @param className The qualified class name from which to remove
     *     the package name.
     * 
     * @return The simple class name.
     */
    public static String stripPackageNameFromClassName(String className) {
        return (className.substring(0, className.length() - (PACKAGE_INFO_CLASS_NAME.length() + 1)));
    }

    //
    
    protected final String hashText;
    
    public String getHashText() {
        return hashText;
    }

    public TargetsVisitorJandexConverterImpl(TargetsTableImpl targetsTable) {
        this.targetsTable = targetsTable;
        this.classesTable = targetsTable.getClassTable();
        this.annotationsTable = targetsTable.getTargetTable();
        // this.detailsTable = targetsTable.getDetailsTable();

        this.hashText = CLASS_NAME + "@" + Integer.toHexString(hashCode());
    }

    //
     
    protected final TargetsTableImpl targetsTable;
    protected final TargetsTableClassesImpl classesTable;
    protected final TargetsTableAnnotationsImpl annotationsTable;
    
    public TargetsTableImpl getTargetsTable() {
        return targetsTable;
    }

    public TargetsTableClassesImpl getClassesTable() {
        return classesTable;
    }
    
    public TargetsTableAnnotationsImpl getAnnotationsTable() {
        return annotationsTable;
    }    
    
    protected String internClassName(String className) {
        return getTargetsTable().internClassName(className,  Util_InternMap.DO_FORCE);
    }

    protected String internClassName(DotName name) {
        return internClassName( name.toString() ); 
    }

    protected String[] internClassNames(List<DotName> dotNames) {
        int numNames = dotNames.size();
        String[] i_names = new String[numNames];

        for ( int nameNo = 0; nameNo < numNames; nameNo++ ) {
            i_names[nameNo] = internClassName( dotNames.get(nameNo) );
        }

        return i_names;
    }

    //

    public boolean convertClassInfo(String classSourceName, Object classInfoObj) {
        String methodName = "convertClassInfo";

        ClassInfo classInfo = (ClassInfo) classInfoObj;

        DotName classDotName = classInfo.name();
        String className = classDotName.toString();

        Object[] logParms =
            ( jandexLogger.isLoggable(Level.FINER)
                ? new String[] { getHashText(), className, null, null }
                : null );

        // TODO: Enable this?  Not sure if Jandex records packages.

        if ( isPackageName(className) ) {
            if ( logParms != null ) {
                jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Package [ {1} ]", logParms); 
            }
            return false;
        }

        int modifiers = classInfo.flags();

        DotName superClassDotName = classInfo.superName();
        List<DotName> interfaceDotNames = classInfo.interfaceNames();

        if ( logParms != null ) {
            logParms[2] = Integer.valueOf(modifiers);
            jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Class [ {1} ] Modifiers [ {2} ]", logParms);
            logParms[2] = superClassDotName;
            jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Class [ {1} ] Superclass [ {1} ]", logParms);
            logParms[2] = interfaceDotNames;
            jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Class [ {1} ] Interfaces [ {1} ]", logParms);
        }

        String i_className = internClassName(className);

        if ( !classesTable.jandex_i_addClass(i_className) ) {
            // TODO: Find or create a message for this.
            jandexLogger.logp(Level.WARNING, CLASS_NAME, methodName, "[ {0} ] Duplicate class [ {1} ]", logParms);
            return false;
        }

        classesTable.jandex_i_setModifiers(i_className, modifiers);

        if ( superClassDotName != null ) {
            String superClassName = superClassDotName.toString();
            String i_superclassName = internClassName(superClassName);
            classesTable.jandex_i_setSuperclassName(i_className, i_superclassName);            
        }

        if ( (interfaceDotNames != null) && (interfaceDotNames.size() > 0) ) {
            String[] i_interfaceNames = internClassNames(interfaceDotNames);
            classesTable.jandex_i_setInterfaceNames(i_className, i_interfaceNames);
        }

        for ( AnnotationInstance jandexClassAnnotation : classInfo.classAnnotations() ) {
            String i_annotationClassName = internClassName(jandexClassAnnotation.name());
            annotationsTable.jandex_i_recordAnnotation(
                i_className, AnnotationCategory.CLASS, i_annotationClassName);
            if ( logParms != null ) {
                logParms[2] = i_annotationClassName;
                jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Class annotation [ {1} ]", logParms);
            }
        }

        for ( FieldInfo jandexFieldInfo : classInfo.fields() ) {
            for ( AnnotationInstance jandexFieldAnnotation : jandexFieldInfo.annotations() ) {
                String i_annotationClassName = internClassName(jandexFieldAnnotation.name());
                annotationsTable.jandex_i_recordAnnotation(
                    i_className, AnnotationCategory.FIELD, i_annotationClassName);
                if ( logParms != null ) {
                    logParms[2] = jandexFieldInfo.name();
                    logParms[3] = i_annotationClassName;
                    jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Field annotation [ {1}.{2} ] [ {3} ]", logParms);
                }
            }
        }

        for ( MethodInfo methodInfo : classInfo.methods() ) {
            for ( AnnotationInstance methodAnnotation : methodInfo.annotations() ) {
                if ( methodAnnotation.target().kind() != Kind.METHOD ) {
                    // Kind.TYPE, Kind.CLASS, Kind.FIELD, and Kind.METHOD_PARAMETER also
                    // exist, although, only TYPE, METHOD, and METHOD_PARAMETER are possible
                    // for method annotation.

                    // The annotation targets framework only handles method annotations.
                    // Ignore the other kinds of annotations.
                    continue;
                }

                String i_annotationClassName = internClassName(methodAnnotation.name());
                annotationsTable.jandex_i_recordAnnotation(i_className, AnnotationCategory.METHOD, i_annotationClassName);
                if ( logParms != null ) {
                    logParms[2] = methodInfo.name();
                    logParms[3] = i_annotationClassName;
                    jandexLogger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Method annotation [ {1}.{2} ] [ {3} ]", logParms);
                }
            }
        }

        return true;
    }
}
