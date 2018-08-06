/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * All modifications made by IBM from initial source -
 * https://github.com/wildfly/jandex/blob/master/src/main/java/org/jboss/jandex/ClassInfo.java
 * commit - 07cbcd56c0e282bc550c327e1ce28c798f628a21
 */

package com.ibm.ws.anno.jandex.internal;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class SparseClassInfo {
    private final SparseDotName name;
    private final short flags;

    private final SparseDotName[] interfaceNames;
    private final SparseDotName superClassName;

    private List<SparseDotName> fieldNames;
    private List<SparseDotName> methodNames;

    private ArrayList<SparseDotName> classAnnotations;
    private List<SparseDotName> fieldAnnotations;
    private List<SparseDotName> methodAnnotations;

    SparseClassInfo(SparseDotName name, SparseDotName superClassName, short flags, SparseDotName[] interfaceNames) {
        this.name = name;
        this.flags = flags;

        this.superClassName = superClassName;
        this.interfaceNames = interfaceNames;

        this.fieldNames = null;
        this.methodNames = null;

        this.classAnnotations = null;
        this.fieldAnnotations = null;
        this.methodAnnotations = null;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object other) {
        if ( other == null ) {
            return false;
        } else if ( !(other instanceof SparseClassInfo) ) {
            return false;
        } else {
            SparseClassInfo otherClassInfo = (SparseClassInfo) other;
            return ( name().equals(otherClassInfo.name()) );
        }
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    //

    public SparseDotName name() {
        return name;
    }

    public short flags() {
        return flags;
    }

    public SparseDotName superName() {
        return superClassName;
    }

    public SparseDotName[] interfaceNames() {
        return interfaceNames;
    }

    //

    public List<SparseDotName> fields() {
        return ((fieldNames == null) ? Collections.emptyList() : fieldNames);
    }

    // Only used by the V1 reader.

    public void addField(SparseDotName fieldName) {
        if ( fieldNames == null ) {
            fieldNames = new ArrayList<SparseDotName>(1);
        }
        fieldNames.add(fieldName);
    }

    // Only used by the V2 reader.

    public void allocateFields(int numFields) {
        if ( (numFields == 0) || (numFields == 1) ) {
            return;
        }
        fieldNames = new ArrayList<SparseDotName>(numFields);
    }

    // Only used by the V2 reader.

    public void addAllocatedField(SparseDotName fieldName) {
        if ( fieldNames == null ) {
            fieldNames = new Singleton<SparseDotName>(fieldName);
        } else {
            fieldNames.add(fieldName);
        }
    }    

    public List<SparseDotName> methods() {
        return ((methodNames == null) ? Collections.emptyList() : methodNames);
    }

    // Only used by the V1 reader.

    public void addMethod(SparseDotName methodName) {
        if ( methodNames == null ) {
            methodNames = new ArrayList<SparseDotName>(1);
        }
        methodNames.add(methodName);
    }

    // Only used by the V2 reader.

    public void allocateMethods(int numMethods) {
        if ( (numMethods == 0) || (numMethods == 1) ) {
            return;
        }
        methodNames = new ArrayList<SparseDotName>(numMethods);
    }

    // Only used by the V2 reader.

    public void addAllocatedMethod(SparseDotName methodName) {
        if ( methodNames == null ) {
            methodNames = new Singleton<SparseDotName>(methodName);
        } else {
            methodNames.add(methodName);
        }
    }

    //

    public List<SparseDotName> classAnnotations() {
        return ((classAnnotations == null) ? Collections.emptyList() : classAnnotations);
    }

    public void addClassAnnotation(SparseDotName classAnnotation) {
        if ( classAnnotations == null ) {
            classAnnotations = new ArrayList<SparseDotName>(1);
        }
        classAnnotations.add(classAnnotation);
    }

    public void addClassAnnotations(List<SparseDotName> annotationClassNames) {
        if ( annotationClassNames == null ) {
            return;
        }
        int numAnnotations = annotationClassNames.size();
        if ( numAnnotations == 0 ) {
            return;
        }

        if ( classAnnotations == null ) {
            classAnnotations = new ArrayList<SparseDotName>(numAnnotations);
        } else {
            classAnnotations.ensureCapacity(classAnnotations.size() + numAnnotations);
        }
        classAnnotations.addAll(annotationClassNames);
    }

    public List<SparseDotName> fieldAnnotations() {
        return ((fieldAnnotations == null) ? Collections.emptyList() : fieldAnnotations);
    }

    // Only used by the V1 reader.

    public void addFieldAnnotation(SparseDotName fieldAnnotation) {
        if ( fieldAnnotations == null ) {
            fieldAnnotations = new ArrayList<SparseDotName>(1);
        }
        fieldAnnotations.add(fieldAnnotation);
    }

    // Only used by the V2 reader.

    public void allocateFieldAnnotations(int numAnnotations) {
        if ( (numAnnotations == 0) || (numAnnotations == 1) ) {
            return;
        } else {
            fieldAnnotations = new ArrayList<SparseDotName>(numAnnotations);
        }
    }

    // Only used by the V2 reader.

    public void addAllocatedFieldAnnotations(SparseDotName[] annotations) {
        if ( (annotations == null) || (annotations.length == 0) ) {
            return;
        }

        if ( fieldAnnotations == null ) {
            fieldAnnotations = new Singleton<SparseDotName>(annotations[0]);
        } else {
            for ( SparseDotName annotation : annotations ) {
                fieldAnnotations.add(annotation);
            }
        }
    }

    public List<SparseDotName> methodAnnotations() {
        return ((methodAnnotations == null) ? Collections.emptyList() : methodAnnotations);
    }

    // Only used by the V1 reader.

    public void addMethodAnnotation(SparseDotName methodAnnotation) {
        if ( methodAnnotations == null ) {
            methodAnnotations = new ArrayList<SparseDotName>(1);
        }
        methodAnnotations.add(methodAnnotation);
    }

    // Only used by the V2 reader.

    public void allocateMethodAnnotations(int numAnnotations) {
        if ( (numAnnotations == 0) || (numAnnotations == 1) ) {
            return;
        } else {
            methodAnnotations = new ArrayList<SparseDotName>(numAnnotations);
        }
    }

    // Only used by the V2 reader.

    public void addAllocatedMethodAnnotations(SparseDotName[] annotations) {
        if ( (annotations == null) || (annotations.length == 0) ) {
            return;
        }

        if ( methodAnnotations == null ) {
            methodAnnotations = new Singleton<SparseDotName>(annotations[0]);
        } else {
            for ( SparseDotName annotation : annotations ) {
                methodAnnotations.add(annotation);
            }
        }
    }

    //

    // Only used by the V2 reader.

    public void recordFieldEntry(SparseAnnotationHolder fieldAndAnnotations) {
        addAllocatedField( fieldAndAnnotations.getName() );
        addAllocatedFieldAnnotations(fieldAndAnnotations.getAnnotations());
    }

    // Only used by the V2 reader.

    public void recordMethodEntry(SparseAnnotationHolder methodAndAnnotations) {
        addAllocatedMethod( methodAndAnnotations.getName() );
        addAllocatedMethodAnnotations(methodAndAnnotations.getAnnotations());
    }
}
