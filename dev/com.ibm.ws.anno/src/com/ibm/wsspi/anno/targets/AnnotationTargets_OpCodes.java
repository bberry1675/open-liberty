/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2018
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.wsspi.anno.targets;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Enum of OpCodes from ASM; see org.objectweb.asm.Opcodes.
 */
public enum AnnotationTargets_OpCodes {
    ACC_PUBLIC(1),
    ACC_PRIVATE(2),
    ACC_PROTECTED(4),
    ACC_STATIC(8),
    ACC_FINAL(16),
    ACC_SYNCHRONIZED(32),
    ACC_VOLATILE(64),
    ACC_VARARGS(128),
    ACC_NATIVE(256),
    ACC_INTERFACE(512),
    ACC_ABSTRACT(1024),
    ACC_STRICT(2048),
    ACC_SYNTHETIC(4096),
    ACC_ANNOTATION(8192),
    ACC_ENUM(16384),
    ACC_MODULE(32768);

    private AnnotationTargets_OpCodes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public final int value;

    //

    public static EnumSet<AnnotationTargets_OpCodes> place(int opCodesValue, EnumSet<AnnotationTargets_OpCodes> opCodes) {
        for ( AnnotationTargets_OpCodes opCode : AnnotationTargets_OpCodes.values() ) {
            if ( (opCodesValue & opCode.getValue()) != 0 ) {
                opCodes.add(opCode);
            }
        }
        return opCodes;
    }

    public static EnumSet<AnnotationTargets_OpCodes> split(int opCodesValue) {
        EnumSet<AnnotationTargets_OpCodes> opCodes =
            EnumSet.noneOf(AnnotationTargets_OpCodes.class);

        for ( AnnotationTargets_OpCodes opCode : AnnotationTargets_OpCodes.values() ) {
            if ( (opCodesValue & opCode.getValue()) != 0 ) {
                opCodes.add(opCode);
            }
        }

        return opCodes;
    }

    public static int combine(EnumSet<AnnotationTargets_OpCodes> opCodes) {
        int result = 0;
        for ( AnnotationTargets_OpCodes opCode : opCodes ) {
            result |= opCode.getValue();
        }
        return result;
    }

    public static int combine(Collection<AnnotationTargets_OpCodes> opCodes) {
        int result = 0;
        for ( AnnotationTargets_OpCodes opCode : opCodes ) {
            result |= opCode.getValue();
        }
        return result;
    }

    public static int combine(AnnotationTargets_OpCodes... opCodes) {
        int result = 0;
        for ( AnnotationTargets_OpCodes opCode : opCodes ) {
            result |= opCode.getValue();
        }
        return result;
    }
}
