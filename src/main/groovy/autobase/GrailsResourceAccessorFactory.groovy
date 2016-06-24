package autobase

import liquibase.*
import liquibase.resource.ResourceAccessor;

import grails.util.Holders

class GrailsResourceAccessorFactory {

	static ResourceAccessor getResourceAccessor() {
		if (Holders.findApplication().isWarDeployed()) {
    	return new GrailsClassLoaderResourceAccessor()
    } else {
    	return new GrailsFileSystemResourceAccessor()
    }		
	}

	
}

