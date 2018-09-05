/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2014, 2018
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.anno.targets.delta.internal;

import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.anno.service.internal.AnnotationServiceImpl_Logging;
import com.ibm.ws.anno.targets.delta.TargetsDelta;
import com.ibm.ws.anno.targets.internal.AnnotationTargetsImpl_Factory;
import com.ibm.ws.anno.targets.internal.AnnotationTargetsImpl_Targets;
import com.ibm.ws.anno.util.delta.internal.UtilImpl_PrintLogger;
import com.ibm.wsspi.anno.classsource.ClassSource_Aggregate.ScanPolicy;
import com.ibm.wsspi.anno.util.Util_PrintLogger;

public class TargetsDeltaImpl implements TargetsDelta {

    protected static final Logger logger = AnnotationServiceImpl_Logging.ANNO_LOGGER;

    public static final String CLASS_NAME = TargetsDeltaImpl.class.getSimpleName();

    //

    protected final String hashText;

    @Override
    public String getHashText() {
        return hashText;
    }

    //

    public TargetsDeltaImpl(
        AnnotationTargetsImpl_Factory factory,
        String appName, String modName,
        AnnotationTargetsImpl_Targets finalTargets,
        AnnotationTargetsImpl_Targets initialTargets) {

        String methodName = "<init>";

        this.hashText =
            getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
            "(" + appName + "." + modName + ")";

        this.factory = factory;

        this.appName = appName;
        this.modName = modName;

        this.seedAnnotationsDelta = new TargetsDeltaImpl_Targets(factory,
            ScanPolicy.SEED, finalTargets.getSeedData(), initialTargets.getSeedData());
        this.partialAnnotationsDelta = new TargetsDeltaImpl_Targets(factory,
            ScanPolicy.PARTIAL, finalTargets.getPartialData(), initialTargets.getPartialData());
        this.excludedAnnotationsDelta = new TargetsDeltaImpl_Targets(factory,
            ScanPolicy.EXCLUDED, finalTargets.getExcludedData(), initialTargets.getExcludedData());
        this.externalAnnotationsDelta = new TargetsDeltaImpl_Targets(factory,
            ScanPolicy.EXTERNAL, finalTargets.getExternalData(), initialTargets.getExternalData());

        if ( logger.isLoggable(Level.FINER) ) {
            logger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] isNull [ {1} ]",
                new Object[] { this.hashText, Boolean.valueOf(this.isNull()) });
            logger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Seed isNull [ {1} ]",
                new Object[] { this.hashText, Boolean.valueOf(this.seedAnnotationsDelta.isNull()) });
            logger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Partial isNull [ {1} ]",
                new Object[] { this.hashText, Boolean.valueOf(this.partialAnnotationsDelta.isNull()) });
            logger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] Excluded isNull [ {1} ]",
                new Object[] { this.hashText, Boolean.valueOf(this.excludedAnnotationsDelta.isNull()) });
            logger.logp(Level.FINER, CLASS_NAME, methodName, "[ {0} ] External isNull [ {1} ]",
                new Object[] { this.hashText, Boolean.valueOf(this.externalAnnotationsDelta.isNull()) });
        }
    }

    //

    protected final AnnotationTargetsImpl_Factory factory;

    @Override
    public AnnotationTargetsImpl_Factory getFactory() {
        return factory;
    }

    //
    
    private final String appName;

    @Override
    public String getAppName() {
        return appName;
    }

    private final String modName;

    @Override
    public String getModName() {
        return modName;
    }

    //

    private final TargetsDeltaImpl_Targets seedAnnotationsDelta;
    private final TargetsDeltaImpl_Targets partialAnnotationsDelta;
    private final TargetsDeltaImpl_Targets excludedAnnotationsDelta;
    private final TargetsDeltaImpl_Targets externalAnnotationsDelta;


    @Override
    public TargetsDeltaImpl_Targets getSeedDelta() {
        return seedAnnotationsDelta;
    }

    @Override
    public TargetsDeltaImpl_Targets getPartialDelta() {
        return partialAnnotationsDelta;
    }

    @Override
    public TargetsDeltaImpl_Targets getExcludedDelta() {
        return excludedAnnotationsDelta;
    }

    @Override
    public TargetsDeltaImpl_Targets getExternalDelta() {
        return externalAnnotationsDelta;
    }

    //

    @Override
    public boolean isNull() {
        return ( getSeedDelta().isNull() &&
                 getPartialDelta().isNull() &&
                 getExcludedDelta().isNull() &&
                 getExternalDelta().isNull() );
    }

    @Override
    public boolean isNull(boolean ignoreRemovedPackages, boolean ignoreRemovedInterfaces) {
        return ( getSeedDelta().isNull(ignoreRemovedPackages, ignoreRemovedInterfaces) &&
                 getPartialDelta().isNull(ignoreRemovedPackages, ignoreRemovedInterfaces) &&
                 getExcludedDelta().isNull(ignoreRemovedPackages, ignoreRemovedInterfaces) &&
                 getExternalDelta().isNull(ignoreRemovedPackages, ignoreRemovedInterfaces) );
    }

    @Override
    public void describe(String prefix, List<String> nonNull) {
        getSeedDelta().describe(prefix + ": Seed", nonNull);
        getPartialDelta().describe(prefix + ": Partial", nonNull);
        getExcludedDelta().describe(prefix + ": Excluded", nonNull);
        getExternalDelta().describe(prefix + ": External", nonNull);
    }
    //

    @Override
    public void log(Logger useLogger) {
        if ( useLogger.isLoggable(Level.FINER) ) {
            log( new UtilImpl_PrintLogger(useLogger) );
        }
    }

    @Override
    public void log(PrintWriter writer) {
        log( new UtilImpl_PrintLogger(writer) );
    }

    @Override
    public void log(Util_PrintLogger useLogger) {
        String methodName = "log";

        useLogger.logp(Level.FINER, CLASS_NAME, methodName,
            "Annotation Targets Delta [ {0} ] BEGIN", getHashText());

        getSeedDelta().log(useLogger);
        getPartialDelta().log(useLogger);
        getExcludedDelta().log(useLogger);
        getExternalDelta().log(useLogger);
        
        useLogger.logp(Level.FINER, CLASS_NAME, methodName,
            "Annotation Targets Delta [ {0} ] END", getHashText());
    }
}