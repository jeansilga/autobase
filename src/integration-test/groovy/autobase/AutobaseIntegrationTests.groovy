package autobase;

import grails.test.*
import grails.test.mixin.integration.Integration
import liquibase.database.Database

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import spock.lang.Specification

@Integration
class AutobaseIntegrationTests extends Specification implements ApplicationContextAware {
	
    ApplicationContext applicationContext

    void testGetDatabaseShouldReturnAProvisionedDatabaseInstance() {
        Autobase.appCtxHolder.set(applicationContext)
        
		when:""
        def database = Autobase.getDatabase()
		
		then:""
        database != null
        database instanceof Database
    }
}
