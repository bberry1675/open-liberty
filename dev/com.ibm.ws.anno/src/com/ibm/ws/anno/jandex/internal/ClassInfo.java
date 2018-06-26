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
import java.util.LinkedList;



public final class ClassInfo{

    private final DotName name;
    private short flags;
    private DotName[] interfaces;
    private DotName superClass;
    private final List<DotName> fields;
    private final List<DotName> methods;
    private final List<LimitedAnnotation> fieldAnnotations;
    private final List<LimitedAnnotation> methodAnnotations;
    //have a method to add and remove inside this class
    private List<DotName> classAnnotations;


    //add in functions to add to list inside the class info class
    //addField() etc.
    //addAll etc.
    

    ClassInfo(DotName name, DotName superClass, short flags, DotName[] interfaces){
        this.name = name;
        this.superClass = superClass;
        this.flags = flags;
        this.interfaces = interfaces;
        this.classAnnotations = new LinkedList<DotName>();
        this.fields = new LinkedList<DotName>();
        this.methods = new LinkedList<DotName>();
        this.fieldAnnotations = new LinkedList<LimitedAnnotation>();
        this.methodAnnotations = new LinkedList<LimitedAnnotation>();
    }
  
    public String toString() {
        return name.toString();
    }

    //pull out the final from the method signatures


    /**
     * Returns the name of the class
     *
     * @return the name of the class
     */
    public final DotName name() {
        return name;
    }

    /**
     * Returns the access flags for this class. The standard {@link java.lang.reflect.Modifier}
     * can be used to decode the value.
     */
    public final short flags() {
        return flags;
    }

    public final DotName superName() {
        return superClass;
    }



    /**
     * Returns a list of all annotations directly declared on this class.
     *
     * @return the list of annotations declared on this class
     */
    public final List<DotName> classAnnotations() {
        return classAnnotations;
    }


    public final List<DotName> methods(){
        return methods;
    }

    public final List<LimitedAnnotation> methodAnnotations(){
        return methodAnnotations;
    }
    public final List<DotName> fields(){
        return fields;
    }


    public final List<LimitedAnnotation> fieldAnnotations(){
        return fieldAnnotations;
    }

    public final DotName[] interfaceNames(){
        return interfaces;
    }

}
