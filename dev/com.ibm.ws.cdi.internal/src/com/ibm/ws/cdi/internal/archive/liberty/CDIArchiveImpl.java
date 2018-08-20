/*******************************************************************************
 * Copyright (c) 2015, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.cdi.internal.archive.liberty;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.internal.archive.AbstractCDIArchive;
import com.ibm.ws.cdi.internal.interfaces.Application;
import com.ibm.ws.cdi.internal.interfaces.ArchiveType;
import com.ibm.ws.cdi.internal.interfaces.CDIArchive;
import com.ibm.ws.cdi.internal.interfaces.CDIUtils;
import com.ibm.ws.cdi.internal.interfaces.Resource;
import com.ibm.ws.cdi.internal.interfaces.ResourceInjectionBag;
import com.ibm.ws.container.service.annotations.ContainerAnnotations;
import com.ibm.ws.container.service.app.deploy.ClientModuleInfo;
import com.ibm.ws.container.service.app.deploy.ContainerInfo;
import com.ibm.ws.container.service.app.deploy.ContainerInfo.Type;
import com.ibm.ws.container.service.app.deploy.InjectionClassList;
import com.ibm.ws.container.service.app.deploy.ModuleClassesContainerInfo;
import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.container.service.app.deploy.extended.ModuleContainerInfo;
import com.ibm.ws.injectionengine.osgi.util.OSGiJNDIEnvironmentRefBindingHelper;
import com.ibm.ws.javaee.dd.client.ApplicationClient;
import com.ibm.ws.javaee.dd.managedbean.ManagedBean;
import com.ibm.ws.javaee.dd.managedbean.ManagedBeanBnd;
import com.ibm.ws.resource.ResourceRefConfigFactory;
import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.NonPersistentCache;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public class CDIArchiveImpl extends AbstractCDIArchive implements CDIArchive {
    // CDIArchives are created ...
    //
    // As module archives of an application:
    //
    // com.ibm.ws.cdi.internal.archive.liberty.ApplicationImpl.initModuleArchives()
    //   -- 'application' is the enclosing application
    //   -- 'containerInfo' is module container information
    //
    // As library archives of an application:
    //
    // com.ibm.ws.cdi.internal.archive.liberty.ApplicationImpl.initLibraryArchives()
    //
    // As extension archives:
    //
    // com.ibm.ws.cdi.internal.archive.liberty.ExtensionArchiveImpl.ExtensionArchiveImpl()
    //   -- 'application' is null
    //   -- 'containerInfo' is extension container information
    //
    // As module library archives:
    //
    // com.ibm.ws.cdi.internal.archive.liberty.CDIArchiveImpl.initModuleLibraryArchives()
    
    public CDIArchiveImpl(
    	ApplicationImpl application,
    	ContainerInfo containerInfo,
    	ArchiveType archiveType,
    	ClassLoader classLoader,
    	RuntimeFactory factory) {

    	super( containerInfo.getName(), factory.getServices() );

    	this.factory = factory;

    	this.application = application; // Null for an externsion archive.

    	this.containerInfo = containerInfo;
    	this.type = archiveType;

    	this.classLoader = classLoader;

    	this.moduleLibraryArchives = initModuleLibraryArchives();
    }

    //

    private final RuntimeFactory factory;

    public RuntimeFactory getFactory() {
        return factory;
    }

    //

    private final ApplicationImpl application;

    @Override
    public Application getApplication() {
        return application;
    }

    //

    // Could be static ... but leave as instance so to keep the
    // trace associated with an instance.

    @Trivial
    private Container getContainer(Entry entry) {
        try {
            return entry.adapt(Container.class); // throws UnableToAdaptException
        } catch ( Throwable th ) {
        	return null; // FFDC
        }
    }

    private Container getContainer(Container container, String path) {
        Entry startEntry = container.getEntry(path);
        if ( startEntry == null ) {
        	return null;
        } else {
            return getContainer(startEntry);
        }
    }

    //

    private final ContainerInfo containerInfo;
    private String path; // The path to the root-of-roots.

    public ContainerInfo getContainerInfo() {
        return containerInfo;
    }

    public Container getContainer() {
        return containerInfo.getContainer();
    }

    @Override
    public String getPath() throws CDIException {
    	if ( path == null ) {
    		path = getPath( getContainer() );
    	}
    	return path;
    }

    private String getPath(Container useContainer) throws CDIException {
    	StringBuilder pathBuilder = new StringBuilder();

    	Entry entry;
    	try {
    		entry = useContainer.adapt(Entry.class);
    	} catch ( UnableToAdaptException e ) {
    		throw new CDIException(e);
    	}

    	while ( entry != null ) {
    		pathBuilder.insert(0,  entry.getPath() );

    		try {
    			entry = entry.getRoot().adapt(Entry.class);
    		} catch ( UnableToAdaptException e ) {
    			throw new CDIException(e);
    		}
    	}

    	return pathBuilder.toString();
    }

    @Override
    public Resource getResource(String path) {
        Entry entry = getContainer().getEntry(path);
        return ((entry == null) ? null : new ResourceImpl(entry));
    }

    @Override
    public boolean isModule() {
        return ( containerInfo instanceof ModuleContainerInfo );
    }

    //

    private final ArchiveType type;

    @Override
    public ArchiveType getType() {
        return type;
    }

    //

    private final ClassLoader classLoader;

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    //

    private final Collection<CDIArchive> moduleLibraryArchives;

    @Override
    public Collection<CDIArchive> getModuleLibraryArchives() {
        return moduleLibraryArchives;
    }

    private Collection<CDIArchive> initModuleLibraryArchives() {
        Map<ContainerInfo, CDIArchive> moduleLibraryArchives = new HashMap<ContainerInfo, CDIArchive>();

        if ( containerInfo instanceof ModuleClassesContainerInfo ) {
            ModuleClassesContainerInfo moduleClassesContainerInfo = (ModuleClassesContainerInfo) containerInfo;
            List<ContainerInfo> containerInfos = moduleClassesContainerInfo.getClassesContainerInfo();
            for ( ContainerInfo containerInfo : containerInfos ) {
                Type containerType = containerInfo.getType();

                if ( (containerType == Type.WEB_INF_LIB) ||
                     (containerType == Type.MANIFEST_CLASSPATH) ||
                     (containerType == Type.JAR_MODULE) ) {
                    ArchiveType archiveType = ContainerInfoTypeUtils.getType(containerType);
                    CDIArchive childArchive = factory.newArchive(application, containerInfo, archiveType, classLoader);
                    moduleLibraryArchives.put(containerInfo, childArchive);
                }
            }
        }

        return moduleLibraryArchives.values();
    }

    //

    private ExtendedModuleInfo moduleInfo;
    private String appCallbackHandlerName;

    public ExtendedModuleInfo getModuleInfo() throws CDIException {
        if ( moduleInfo == null ) {
            if ( containerInfo instanceof ModuleContainerInfo ) {
                NonPersistentCache cache;

                ModuleContainerInfo moduleContainerInfo = (ModuleContainerInfo) containerInfo;
            	try {
                    cache = moduleContainerInfo.getContainer().adapt(NonPersistentCache.class);
                } catch ( UnableToAdaptException e ) {
                    throw new CDIException(e);
                }

                moduleInfo = (ExtendedModuleInfo) cache.getFromCache(ModuleInfo.class);
            }
        }

        return moduleInfo;
    }

    public ModuleMetaData getModuleMetaData() throws CDIException {
        if ( !isModule() ) {
        	return null;
        }

        ExtendedModuleInfo moduleInfo = getModuleInfo();
        if ( moduleInfo == null ) {
        	return null;
        }

        return moduleInfo.getMetaData();
    }

    @Override
    public MetaData getMetaData() throws CDIException {
        if ( isModule() ) {
            return getModuleMetaData();
        } else {
            return application.getApplicationMetaData();
        }
    }

    @Override
    public J2EEName getJ2EEName() throws CDIException {
        // TODO Can we come up with a J2EEName for libraries as well as modules?

        ModuleMetaData moduleMetaData = getModuleMetaData();
        if ( moduleMetaData == null ) {
        	return null;
        } else {
            return moduleMetaData.getJ2EEName();
        }
    }

    @Override
    public String getClientModuleMainClass() throws CDIException {
        ModuleInfo moduleInfo = getModuleInfo();
        if ( (moduleInfo == null) || !(moduleInfo instanceof ClientModuleInfo) ) {
        	return null;
        }

        ClientModuleInfo clientModuleInfo = (ClientModuleInfo) moduleInfo;
        return clientModuleInfo.getMainClassName();
    }

    @Override
    public String getClientAppCallbackHandlerName() throws CDIException {
        if ( appCallbackHandlerName == null ) {
            ModuleInfo moduleInfo = getModuleInfo();
            if ( (moduleInfo != null) && (moduleInfo instanceof ClientModuleInfo) ) {
                ApplicationClient appClientXml;
                try {
                    appClientXml = getContainer().adapt(ApplicationClient.class);
                } catch ( UnableToAdaptException e ) {
                    // This should never happen unless there's a parse error
                	// in the application-client.xml in which case the container
                	// should catch it first.
                    throw new CDIException(e);
                }
                if ( appClientXml != null ) {
                    appCallbackHandlerName = appClientXml.getCallbackHandler();
                }
            }
        }
        return appCallbackHandlerName;
    }

    //

    private Set<String> classNames;

    @Override
    public Set<String> getClassNames() {
        if ( classNames == null ) {
            Set<String> storage = new TreeSet<String>();

            Container container = containerInfo.getContainer();
            if ( type == ArchiveType.WEB_MODULE ) {
                container = getContainer(container, CDIUtils.WEB_INF_CLASSES);
            }
            if ( container != null ) {
            	collectClassNames(container, null, storage);
            }

            classNames = storage;
        }

        return classNames;
    }

    @Trivial
    private void collectClassNames(Container container, String packageName, Set<String> storage) {
        for ( Entry entry : container ) {
        	String entryName = entry.getName();

        	if ( !entryName.endsWith(CDIUtils.CLASS_EXT) ) {
        		// TODO: Is this correct for RAR files?
        		//       A RAR can have nested JARs, which will be picked up
        		//       by this loop.
        		//       Conversion to a *local* container might be correct.

        		Container entryContainer = getContainer(entry);
        		if ( entryContainer != null ) {
        			collectClassNames(entryContainer, entryName, storage);
        		}

        	} else {
        		int classNameLength = entryName.length() - CDIUtils.CLASS_EXT_LENGTH;
        		String className = entryName.substring(0, classNameLength);

        		String qualifiedClassName;
        		if ( packageName == null ) {
        			qualifiedClassName = className;
        		} else {
        			qualifiedClassName = packageName + CDIUtils.DOT + className;
        		}

        		storage.add(qualifiedClassName);
        	}
        }
    }

    //
    
    private List<String> jeeComponentClassNames;

    @Override
    public List<String> getInjectionClassList() throws CDIException {
        if ( jeeComponentClassNames == null ) {
            InjectionClassList injectionClassList;
            try {
                injectionClassList = getContainer().adapt(InjectionClassList.class);
            } catch ( UnableToAdaptException e ) {
                throw new CDIException(e);
            }
            jeeComponentClassNames = injectionClassList.getClassNames();
        }
        return jeeComponentClassNames;
    }

    //

    // TODO: Should an 'isSetAllBindings' flag be used?
    //       If no ManagedBeanBnd instance is available, 'allBindings' is
    //       left null, in which case subsequent calls will re-attempt to
    //       initialize the bindings.

    private ResourceInjectionBag allBindings;

    @Override
    public ResourceInjectionBag getAllBindings() throws CDIException {
        if ( allBindings == null ) {
        	ManagedBeanBnd managedBeanBnd;
        	try {
        		managedBeanBnd = getContainer().adapt(ManagedBeanBnd.class);
        	} catch (UnableToAdaptException e) {
        		throw new CDIException(e);
        	}
            
        	if ( managedBeanBnd != null ) {
        		ResourceRefConfigFactory resourceRefConfigFactory = factory.getServices().getResourceRefConfigFactory();

        		allBindings = new ResourceInjectionBag(resourceRefConfigFactory.createResourceRefConfigList());

        		List<ManagedBean> managedBeans = managedBeanBnd.getManagedBeans();
        		if ( managedBeans != null ) {
        			for ( ManagedBean managedBean : managedBeans ) {
        				OSGiJNDIEnvironmentRefBindingHelper.processBndAndExt(
        					allBindings.allBindings,
        					allBindings.envEntryValues,
        					allBindings.resourceRefConfigList,
        					managedBean,
        					null);
        			}
        		}
        	}
        }
        return allBindings;
    }

    //

    @Override
    public Set<String> getAnnotatedClasses(Set<String> annotationClassNames) throws CDIException {
    	// Archive.Type:
        //   MANIFEST_CLASSPATH
        //   EAR_LIB
        //   WEB_INF_LIB
    	//
        //   JAR_MODULE
        //   WEB_MODULE
        //   EJB_MODULE
        //   CLIENT_MODULE
        //   RAR_MODULE
    	//
        //   SHARED_LIB
        //   ON_DEMAND_LIB
        //   RUNTIME_EXTENSION

    	Container archiveContainer = getContainer();
        Container classesContainer;
        if ( type == ArchiveType.WEB_MODULE ) {
            classesContainer = getContainer(archiveContainer, CDIUtils.WEB_INF_CLASSES);
        } else {
        	classesContainer = archiveContainer;
        }

        if ( classesContainer == null ) {
        	// This happens, for example, when a WAR has no WEB-INF/classes folder.
        	// This should not happen in any other cases.
        	return Collections.emptySet();
        }

        ContainerAnnotations containerAnnotations;
        try {
            containerAnnotations = classesContainer.adapt(ContainerAnnotations.class);
        } catch ( UnableToAdaptException e ) {
            throw new CDIException(e);
        }

        // Supply persistence information, but only when the archive is associated
        // with an application (currently, only when the archive is an extension archive).
        //
        // Use the path as the module name, even for cases when the archive is not a module
        // type archive.
        //
        // Three general cases are generated?
        //
        // The archive is a module archive;
        // The archive is a non-module archive of the application;
        // The archive is a library archive of a web module.
        //
        // For all cases, the container path (from the root-of-roots, which should
        // be the application root) is the second half of the key to the results.
        // The first half of the key is the "CDI" category name, which is necessary
        // because CDI obtains module results somewhat differently than it obtains
        // JavaEE obtains module results.

        if ( application != null ) {
            boolean useJandex = application.getUseJandex();
            containerAnnotations.setUseJandex(useJandex);

        	containerAnnotations.setAppName( application.getName() );
    		containerAnnotations.setModName( getPath() );
    		containerAnnotations.setModCategoryName(ClassSource_Factory.CDI_CATEGORY_NAME);
        }

        return containerAnnotations.getClassesWithSpecifiedInheritedAnnotations(annotationClassNames);
    }
}
