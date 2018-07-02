 /*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
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
 * https://github.com/wildfly/jandex/blob/master/src/main/java/org/jboss/jandex/IndexReaderV2.java
 * commit - 36c2b049b7858205c6504308a5e162a4e943ff21
 */

package com.ibm.ws.anno.jandex.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

final class LimitedIndexReaderV2 extends IndexReaderImpl {
    static final int MIN_VERSION = 6;
    static final int MAX_VERSION = 6;
    static final int MAX_DATA_VERSION = 4;
    private static final byte NULL_TARGET_TAG = 0;
    private static final byte FIELD_TAG = 1;
    private static final byte METHOD_TAG = 2;
    private static final byte METHOD_PARAMATER_TAG = 3;
    private static final byte CLASS_TAG = 4;
    private static final byte EMPTY_TYPE_TAG = 5;
    private static final byte CLASS_EXTENDS_TYPE_TAG = 6;
    private static final byte TYPE_PARAMETER_TAG = 7;
    private static final byte TYPE_PARAMETER_BOUND_TAG = 8;
    private static final byte METHOD_PARAMETER_TYPE_TAG = 9;
    private static final byte THROWS_TYPE_TAG = 10;
    private static final int AVALUE_BYTE = 1;
    private static final int AVALUE_SHORT = 2;
    private static final int AVALUE_INT = 3;
    private static final int AVALUE_CHAR = 4;
    private static final int AVALUE_FLOAT = 5;
    private static final int AVALUE_DOUBLE = 6;
    private static final int AVALUE_LONG = 7;
    private static final int AVALUE_BOOLEAN = 8;
    private static final int AVALUE_STRING = 9;
    private static final int AVALUE_CLASS = 10;
    private static final int AVALUE_ENUM = 11;
    private static final int AVALUE_ARRAY = 12;
    private static final int AVALUE_NESTED = 13;
    private static final int HAS_ENCLOSING_METHOD = 1;

    private final PackedDataInputStream input;
    private byte[][] byteTable;
    private String[] stringTable;
    private DotName[] nameTable;
    private DotName[] typeTable;
    private DotName[][] typeListTable;
    private LimitedAnnotation[] annotationTable;
    private LimitedAnnotationHolder[] methodTable;
    private LimitedAnnotationHolder[] fieldTable;


    LimitedIndexReaderV2(PackedDataInputStream input) {
        this.input = input;
    }

    LimitedIndex read(int version) throws IOException {
            input.seekPackedU32(); //annotationSize
            input.seekPackedU32(); //implementorSize
            input.seekPackedU32(); //subclassesSize 
            readByteTable();
            readStringTable();
            readNameTable();

            typeTable = new DotName[input.readPackedU32() + 1];
            typeListTable = new DotName[input.readPackedU32() + 1][];
            annotationTable = new LimitedAnnotation[input.readPackedU32() + 1];

            readTypeTable();
            readTypeListTable();
            readMethodTable();
            readFieldTable();
                      
            return readClasses();
    }

    private void readByteTable() throws IOException {
        // Null is the implicit first entry
        int size = input.readPackedU32() + 1;
        byte[][] byteTable = this.byteTable = new byte[size][];

        for (int i = 1; i < size; i++) {
            int len = input.readPackedU32();
            byteTable[i] = new byte[len];
            input.readFully(byteTable[i], 0, len);
        }
    }

    private void readStringTable() throws IOException {
        // Null is the implicit first entry
        int size = input.readPackedU32() + 1;
        String[] stringTable = this.stringTable = new String[size];

        for (int i = 1; i < size; i++) {
            stringTable[i] = input.readUTF();
        }
    }

    private void readNameTable() throws IOException {
        // Null is the implicit first entry
        int entries = input.readPackedU32() + 1;
        int lastDepth = -1;
        DotName curr = null;
        nameTable = new DotName[entries];

        for (int i = 1; i < entries; i++) {
            int depth = input.readPackedU32();
            boolean inner = (depth & 1) == 1;
            String local = stringTable[input.readPackedU32()];

            depth >>= 1;
            if (depth <= lastDepth) {
                while (lastDepth-- >= depth) {
                    assert curr != null;
                    curr = curr.prefix();
                }
            }

            nameTable[i] = curr = new DotName(curr, local, true, inner);
            lastDepth = depth;
        }
    }

    private void readTypeTable() throws IOException {
        // Null is the implicit first entry
        for (int i = 1; i < typeTable.length; i++) {
            typeTable[i] = movePastReadTypeEntry();
        }
    }

    private int findNextNull(Object[] array, int start) {
        while (start < array.length) {
            if (array[start] == null) {
                return start;
            }
            start++;
        }

        return array.length;
    }

    private void readTypeListTable() throws IOException {
        // Null is the implicit first entry
        DotName[][] typeListTable = this.typeListTable;

        // Already emitted entries are omitted as gaps in the table portion
        for (int i = findNextNull(typeListTable, 1); i < typeListTable.length; i = findNextNull(typeListTable, i)) {
            typeListTable[i] = readTypeListEntry();
        }
    }

    private LimitedAnnotation[] readAnnotations( DotName target) throws IOException {
        int numberOfAnnotations = input.readPackedU32();
        
        if (numberOfAnnotations == 0) {
            return LimitedAnnotation.EMPTY_ARRAY;
        }
        
        LimitedAnnotation[] annotations = new LimitedAnnotation[numberOfAnnotations];
        
        for (int i = 0; i < numberOfAnnotations; i++) {
            int reference = input.readPackedU32();

            if (annotationTable[reference] == null) {
                annotationTable[reference] = readAnnotationEntry( target);
            }

            annotations[i] = annotationTable[reference];
        }

        return annotations;
    }

    private void movePastAnnotationValues() throws IOException {
        int numValues = input.readPackedU32();
        
        for (int i = 0; i < numValues; i++) {
            input.seekPackedU32();

            int tag = input.readByte();

            switch (tag) {
                case AVALUE_BYTE:
                    input.readByte();
                    break;
                case AVALUE_SHORT:
                    input.seekPackedU32();
                    break;
                case AVALUE_INT:
                    input.seekPackedU32();
                    break;
                case AVALUE_CHAR:
                    input.seekPackedU32();
                    break;
                case AVALUE_FLOAT:
                    input.readFloat();
                    break;
                case AVALUE_DOUBLE:
                    input.readDouble();
                    break;
                case AVALUE_LONG:
                    input.readLong();
                    break;
                case AVALUE_BOOLEAN:
                    input.readBoolean();
                    break;
                case AVALUE_STRING:
                    input.seekPackedU32();
                    break;
                case AVALUE_CLASS:
                    input.seekPackedU32();
                    break;
                case AVALUE_ENUM:
                    input.seekPackedU32();
                    input.seekPackedU32();
                    break;
                case AVALUE_ARRAY:
                    movePastAnnotationValues();
                    break;
                case AVALUE_NESTED: {
                    int reference = input.readPackedU32();
                    LimitedAnnotation nestedInstance = annotationTable[reference];
                    
                    if (nestedInstance == null) {
                        nestedInstance = annotationTable[reference] = readAnnotationEntry( null);
                    }
                    break;
                }
                default:
                    throw new IllegalStateException("Invalid annotation value tag:" + tag);
            }
        }
    }

    private LimitedAnnotation readAnnotationEntry( DotName caller) throws IOException {
        DotName name = nameTable[input.readPackedU32()];

        movePastAnnotationTarget();
        movePastAnnotationValues();

        return new LimitedAnnotation(name,caller);
    }

    private DotName[] readTypeListReference() throws IOException {
        int reference = input.readPackedU32();
        DotName[] types = typeListTable[reference];
        
        if (types != null) {
            return types;
        }

        return typeListTable[reference] = readTypeListEntry();
    }


    private DotName[] readTypeListEntry() throws IOException {
        int size = input.readPackedU32();
        
        if (size == 0) {
            return DotName.PLACEHOLDER_ARRAY;
        }

        DotName[] types = new DotName[size];
        
        for (int i = 0; i < size; i++) {
            types[i] = typeTable[input.readPackedU32()];
        }

        return types;
    }


    private DotName movePastReadTypeEntry() throws IOException{
        int kind = (int) input.readUnsignedByte();

        switch (kind) {
            case 0: { //class
                DotName name = nameTable[input.readPackedU32()];

                readAnnotations(null);
                return name;
            }
            case 1: { //Array
                input.seekPackedU32();
                input.seekPackedU32();
                readAnnotations( null);
                return DotName.PLACEHOLDER;
            }
            case 2: { //primitive
                input.readUnsignedByte();
                readAnnotations( null);
                return DotName.PLACEHOLDER;
            }
            default:
            case 3: { //void
                readAnnotations( null);
                return DotName.PLACEHOLDER;
            }
            case 4: { //type_variable

                input.seekPackedU32();
                readTypeListReference();
                readAnnotations( null);
                return DotName.PLACEHOLDER;
            }
            case 5: { //unresolved_typevariable
                input.seekPackedU32();
                readAnnotations( null);
                return DotName.PLACEHOLDER;
            }
            case 6: { //wildcard type
                input.seekPackedU32();
                input.seekPackedU32();
                readAnnotations( null);
                return DotName.PLACEHOLDER;
            }
            case 7: { //parametrized type
                DotName name = nameTable[input.readPackedU32()];
                
                input.seekPackedU32();
                readTypeListReference();
                readAnnotations( null);
                return name;
            }
        }
    }


    private void movePastAnnotationTarget() throws IOException {
        byte tag = input.readByte();

        switch(tag){
            case NULL_TARGET_TAG:
                return;
            case CLASS_TAG:
            case FIELD_TAG:
            case METHOD_TAG:
                return;
            case METHOD_PARAMATER_TAG: {
                input.seekPackedU32();
                return;
            }
            case EMPTY_TYPE_TAG: {
                input.seekPackedU32();
                input.seekPackedU32();
                return;
            }
            case CLASS_EXTENDS_TYPE_TAG: {
                input.seekPackedU32();
                input.seekPackedU32();
                return;
            }
            case TYPE_PARAMETER_TAG: {
                input.seekPackedU32();
                input.seekPackedU32();
                return;
            }
            case TYPE_PARAMETER_BOUND_TAG: {
                input.seekPackedU32();
                input.seekPackedU32();
                input.seekPackedU32();
                return;
            }
            case METHOD_PARAMETER_TYPE_TAG: {
                input.seekPackedU32();
                input.seekPackedU32();
                return;
            }
            case THROWS_TYPE_TAG: {
                input.seekPackedU32();
                input.seekPackedU32();
                return;
            }
        }
    }

    private void readMethodTable() throws IOException {
        // Null holds the first slot
        int size = input.readPackedU32() + 1;
        methodTable = new LimitedAnnotationHolder[size];

        for (int i = 1; i < size; i++) {
            methodTable[i] = readMethodEntry();
        }
    }

    private void readFieldTable() throws IOException {
        // Null holds the first slot
        int size = input.readPackedU32() + 1;
        fieldTable = new LimitedAnnotationHolder[size];

        for (int i = 1; i < size; i++) {
            fieldTable[i] = readFieldEntry();
        }
    }

    private LimitedAnnotationHolder readMethodEntry() throws IOException {
        byte[] name = byteTable[input.readPackedU32()];

        input.seekPackedU32();
        input.seekPackedU32();
        input.seekPackedU32();
        input.seekPackedU32();
        input.seekPackedU32();
        input.seekPackedU32();

        LimitedAnnotation[] annotations = readAnnotations( DotName.createSimple(Utils.fromUTF8(name)));

        return new LimitedAnnotationHolder(DotName.createSimple(Utils.fromUTF8(name)), annotations);
    }

    private LimitedAnnotationHolder readFieldEntry() throws IOException {
        byte[] name = byteTable[input.readPackedU32()];

        input.seekPackedU32();
        input.seekPackedU32();

        LimitedAnnotation[] annotations = readAnnotations( DotName.createSimple(Utils.fromUTF8(name)));

        return new LimitedAnnotationHolder(DotName.createSimple(Utils.fromUTF8(name)), annotations);
    }

    private ClassInfo readClassEntry() throws IOException {
        DotName name  = nameTable[input.readPackedU32()];
        short flags = (short) input.readPackedU32();
        DotName superType = typeTable[input.readPackedU32()];
        
        input.seekPackedU32();

        DotName[] interfaceTypes = typeListTable[input.readPackedU32()];
        ClassInfo currentClassInformation = new ClassInfo(name, superType, flags, interfaceTypes);

        input.seekPackedU32();
        input.seekPackedU32();
        readPastEnclosingMethod();

        int numberOfAnnotations = input.readPackedU32();

        readClassFields(currentClassInformation);
        readClassMethods(currentClassInformation);

        for (int currentAnnotation = 0; currentAnnotation < numberOfAnnotations; currentAnnotation++) {
            LimitedAnnotation[] annotationInstances = readAnnotations(currentClassInformation.name());

            if (annotationInstances.length > 0) {
                processClassAnnotations(annotationInstances, currentClassInformation);
            }
        }

        return currentClassInformation;
    }

    //Only adds annotaions from the array that targets the class given by currentClass parameter
    private void processClassAnnotations(LimitedAnnotation[] allAnnotationsInClass, ClassInfo currentClass){
        for(int annotationCounter = 0; annotationCounter < allAnnotationsInClass.length; annotationCounter++){
            DotName targetName = allAnnotationsInClass[annotationCounter].getTargetName();
            
            if(targetName.equals(currentClass.name())){
                currentClass.addClassAnnotation(allAnnotationsInClass[annotationCounter].getName());
            }
        }
    }

    private void readClassFields( ClassInfo currentClass) throws IOException {
        int numOfFields = input.readPackedU32();

        for (int i = 0; i < numOfFields; i++) {
            LimitedAnnotationHolder currentField = fieldTable[input.readPackedU32()];
            
            currentClass.recordFieldEntry(currentField);
        }
    }

    private void readClassMethods( ClassInfo currentClass) throws IOException {
        int len = input.readPackedU32();
        
        for (int i = 0; i < len; i++) {
            LimitedAnnotationHolder currentMethod = methodTable[input.readPackedU32()];

            currentClass.recordMethodEntry(currentMethod);
        }
    }

    private void readPastEnclosingMethod() throws IOException{
        if (input.readUnsignedByte() == HAS_ENCLOSING_METHOD) {
            input.seekPackedU32(); //eName
            input.seekPackedU32(); //eClass
            input.seekPackedU32(); //returnType
            input.seekPackedU32(); //parameters
        }
    }

    private LimitedIndex readClasses() throws IOException {
        int classesSize = input.readPackedU32();
        Map<DotName, ClassInfo> classes = new HashMap<DotName, ClassInfo>(classesSize);

        for (int currentEntry = 0; currentEntry < classesSize; currentEntry++) {
            ClassInfo currentClassEntry = readClassEntry();

            classes.put(currentClassEntry.name(), currentClassEntry);
        }

        return new LimitedIndex(classes);
    }

    int toDataVersion(int version) {

        return MAX_DATA_VERSION;
    }
}
 