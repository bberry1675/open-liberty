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
package com.ibm.ws.anno.util.internal;

import java.util.concurrent.TimeUnit;

/**
 * <p>Pool parameters for targets multi-threaded operations.</p>
 */
public class UtilImpl_PoolParameters {
    /**
     * Pool defaults.  These are set for a small, infrequently used pool.
     */
    public static final int CORE_SIZE_DEFAULT = 0;
    public static final int MAXIMUM_SIZE_DEFAULT = 8;
    public static final long KEEP_ALIVE_TIME_DEFAULT = 1L;
    public static final TimeUnit KEEP_ALIVE_UNIT_DEFAULT = TimeUnit.SECONDS;

    // * @param corePoolSize the number of threads to keep in the pool, even
    // *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
    // * @param maximumPoolSize the maximum number of threads to allow in the
    // *        pool
    // * @param keepAliveTime when the number of threads is greater than
    // *        the core, this is the maximum time that excess idle threads
    // *        will wait for new tasks before terminating.
    // * @param unit the time unit for the {@code keepAliveTime} argument

    public static UtilImpl_PoolParameters createDefaultParameters() {
        return new UtilImpl_PoolParameters(
            CORE_SIZE_DEFAULT,
            MAXIMUM_SIZE_DEFAULT,
            KEEP_ALIVE_TIME_DEFAULT,
            KEEP_ALIVE_UNIT_DEFAULT);
    }

    public final int coreSize;
    public final int maxSize;
    public final long keepAliveTime;
    public final TimeUnit keepAliveUnit;

    public UtilImpl_PoolParameters(int corePoolSize,
                                   int maxPoolSize,
                                   long keepAliveTime,
                                   TimeUnit keepAliveUnit) {
        this.coreSize = corePoolSize;
        this.maxSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.keepAliveUnit = keepAliveUnit;
    }
}
