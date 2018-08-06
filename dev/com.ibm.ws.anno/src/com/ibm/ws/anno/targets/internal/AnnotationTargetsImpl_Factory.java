/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2018
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.targets.internal;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.anno.classsource.internal.ClassSourceImpl_Factory;
import com.ibm.ws.anno.service.internal.AnnotationServiceImpl_Logging;
import com.ibm.ws.anno.service.internal.AnnotationServiceImpl_Service;
import com.ibm.ws.anno.targets.cache.internal.TargetCacheImpl_DataApps;
import com.ibm.ws.anno.targets.cache.internal.TargetCacheImpl_Factory;
import com.ibm.ws.anno.util.internal.UtilImpl_Factory;
import com.ibm.ws.anno.util.internal.UtilImpl_InternMap;
import com.ibm.wsspi.anno.classsource.ClassSource_Aggregate;
import com.ibm.wsspi.anno.targets.AnnotationTargets_Exception;
import com.ibm.wsspi.anno.targets.AnnotationTargets_Factory;
import com.ibm.wsspi.anno.util.Util_InternMap;

public class AnnotationTargetsImpl_Factory implements AnnotationTargets_Factory {
    protected static final Logger logger = AnnotationServiceImpl_Logging.ANNO_LOGGER;

    public static final String CLASS_NAME = AnnotationTargetsImpl_Factory.class.getSimpleName();

    //

    protected final String hashText;

    @Override
    public String getHashText() {
        return hashText;
    }

    //

    public AnnotationTargetsImpl_Factory(
        AnnotationServiceImpl_Service annoService,
        UtilImpl_Factory utilFactory,
        ClassSourceImpl_Factory classSourceFactory,
        TargetCacheImpl_Factory annoCacheFactory) {

        super();

        String methodName = "<init>";

        this.hashText = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());

        this.annoService = annoService;
        this.utilFactory = utilFactory;
        this.cacheFactory = annoCacheFactory;

        if (logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, methodName,
                "[ {0} ] using [ {1} ]",
                new Object[] { this.hashText, this.utilFactory.getHashText() });
        }
    }

    //

    protected final AnnotationServiceImpl_Service annoService;

    public AnnotationServiceImpl_Service getAnnotationService() {
        return annoService;
    }

    //

    protected final UtilImpl_Factory utilFactory;

    @Override
    public UtilImpl_Factory getUtilFactory() {
        return utilFactory;
    }

    //

    protected final TargetCacheImpl_Factory cacheFactory;

    public TargetCacheImpl_Factory getCacheFactory() {
        return cacheFactory;
    }

    public TargetCacheImpl_DataApps getCache() {
        return getCacheFactory().getCache();
    }

    //

    @Override
    public AnnotationTargets_Exception newAnnotationTargetsException(Logger useLogger, String message) {
        String methodName = "newAnnotationTargetsException";

        AnnotationTargets_Exception exception = new AnnotationTargets_Exception(message);

        if (useLogger.isLoggable(Level.FINER)) {
            useLogger.logp(Level.FINER, CLASS_NAME, methodName, "Created [ {0} ]", message);
        }

        return exception;
    }

    @Override
    public AnnotationTargets_Exception wrapIntoAnnotationTargetsException(Logger useLogger,
                                                                          String callingClassName,
                                                                          String callingMethodName,
                                                                          String message, Throwable th) {
        String methodName = "wrapIntoAnnotationTargetsException";

        AnnotationTargets_Exception wrappedException = new AnnotationTargets_Exception(message, th);

        if (useLogger.isLoggable(Level.FINER)) {
            useLogger.logp(Level.FINER, CLASS_NAME, methodName,
                        "[ {0} ] [ {1} ] Wrap [ {2} ] as [ {3} ]",
                        new Object[] { callingClassName,
                                       callingMethodName,
                                       th.getClass().getName(),
                                       wrappedException.getClass().getName() });

            useLogger.logp(Level.FINER, CLASS_NAME, methodName,
                        "Wrapped [ {0} ] [ {1} ]",
                        new Object[] { th.getMessage(), th.getClass().getName() });
        }

        return wrappedException;
    }

    // Global scan APIs ...

    @Override
    public AnnotationTargetsImpl_Targets createTargets()
        throws AnnotationTargets_Exception {

        return new AnnotationTargetsImpl_Targets( this,
                                                  getCache(),
                                                  createClassNameInternMap(),
                                                  createFieldNameInternMap(),
                                                  createMethodSignatureInternMap() );
    }

    // These are needed for concurrent scanning.

    protected UtilImpl_InternMap createClassNameInternMap() {
        return getUtilFactory().createInternMap(Util_InternMap.ValueType.VT_CLASS_NAME, "classes and package names");
    }

    protected UtilImpl_InternMap createFieldNameInternMap() {
        return getUtilFactory().createInternMap(Util_InternMap.ValueType.VT_FIELD_NAME, "field names");
    }

    protected UtilImpl_InternMap createMethodSignatureInternMap() {
        return getUtilFactory().createInternMap(Util_InternMap.ValueType.VT_OTHER, "method signatures");
    }

    //

    @Override
    public AnnotationTargetsImpl_Fault createFault(String unresolvedText) {
        return new AnnotationTargetsImpl_Fault(unresolvedText);
    }

    @Override
    public AnnotationTargetsImpl_Fault createFault(String unresolvedText, String parameter) {
        return new AnnotationTargetsImpl_Fault(unresolvedText, new String[] { parameter });
    }

    @Override
    public AnnotationTargetsImpl_Fault createFault(String unresolvedText, String... parameters) {
        return new AnnotationTargetsImpl_Fault(unresolvedText, parameters);
    }

    //

    @Override
    public AnnotationTargetsImpl_Targets createTargets(ClassSource_Aggregate classSource)
                    throws AnnotationTargets_Exception {

        AnnotationTargetsImpl_Targets moduleTargets = createTargets(); // throws AnnotationTargets_Exception

        moduleTargets.scan(classSource);

        return moduleTargets;
    }

    // Limited scan APIs ...

    @Override
    public AnnotationTargetsImpl_Targets createTargets(ClassSource_Aggregate classSource,
                                                       Set<String> specificClassNames,
                                                       Set<String> specificAnnotationClassNames)
        throws AnnotationTargets_Exception {

        AnnotationTargetsImpl_Targets specificTargets = createTargets();
        // throws AnnotationTargets_Exception

        specificTargets.scan(classSource, specificClassNames, specificAnnotationClassNames);
        // throws AnnotationTargets_Exception

        return specificTargets;
    }

    @Override
    public AnnotationTargetsImpl_Targets createTargets(ClassSource_Aggregate classSource,
                                                       Set<String> specificClassNames)
        throws AnnotationTargets_Exception {

        AnnotationTargetsImpl_Targets specificTargets = createTargets();
        // throws AnnotationTargets_Exception

        specificTargets.scan(classSource, specificClassNames, TargetsVisitorClassImpl.SELECT_ALL_ANNOTATIONS);
        // throws AnnotationTargets_Exception

        return specificTargets;
    }
}
