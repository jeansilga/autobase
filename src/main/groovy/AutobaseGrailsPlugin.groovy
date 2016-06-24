

import grails.plugins.*
import autobase.Autobase
import autobase.change.GroovyScriptChange
import grails.util.GrailsUtil
import java.util.logging.Handler
import liquibase.parser.factory.OpenChangeFactory
import liquibase.logging.LogFactory
import org.apache.log4j.*
import org.slf4j.bridge.*
import autobase.migration.*;
import autobase.migration.MigrationArtefactHandler

class AutobaseGrailsPlugin extends Plugin {
	private static final Logger log = Logger.getLogger(AutobaseGrailsPlugin);

	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "3.1.8 > *"
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		"grails-app/**",
		"grails-app/assets/**"
	]

	// TODO Fill in these fields
	def title = "Autobase" // Headline display name of the plugin
	def author = "Robert Fischer, Jun Chen, Antoine roux, Jean Silga"
	def authorEmail = ""
	def description = '''\
This plugin marries the established Liquibase core with Grails development processes in order to to minimze the amount of database code you have to think about.

The approach to this plugin is to leave the database update mode ("hbm2ddl.auto" value) as "update", and to manage alterations to the database schema through checking in changesets to a folder.  The changesets are made up of Liquibase's many "refactorings": http://www.liquibase.org/manual/home#available_database_refactorings
'''
	def profiles = ['web']
	def dependsOn = [:]

	def loadBefore = ["hibernate"]

	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/autobase"

	// Extra (optional) plugin metadata

	// License: one of 'APACHE', 'GPL2', 'GPL3'
	def license = "APACHE"

	// Details of company behind the plugin (if there is one)
	//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

	// Any additional developers beyond the author specified above.
	//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

	// Location of the plugin's issue tracker.
	//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

	// Online location of the plugin's browseable source code.
	//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

	def artefacts = [new MigrationArtefactHandler()]

	def watchedResources = [
		"file:./grails-app/migrations/*Migration.groovy",
	]

	//TODO: Formalize how we want to register change/precondition extensions conventionally
	private static final Closure doRegisterExtensions = {
		try {
			OpenChangeFactory.instance.registerChange(GroovyScriptChange.TAG_NAME, GroovyScriptChange.class)
		} catch (Throwable e) {
			GrailsUtil.deepSanitize(e)
			log.error("Error registering extensions, message: ${e.getMessage()}", e)
		}
	}

	private static final Closure doMigrate = {application, appCtx ->

		def runOnCreateDrop = application.config.autobase.runOnCreateDrop
		if (runOnCreateDrop == false && application.config.dataSource.dbCreate == 'create-drop') {
			log.info("Skipping Autobase migration due to create-drop (set 'autobase.runOnCreateDrop' to 'true' in Config.groovy to run anyway)")
		} else {
			log.info("---- Starting Autobase migrations  ----")
			Autobase.migrate(appCtx,application)
			log.info("---- Autobase migrations completed ----")
		}
	}

	Closure doWithSpring() { {->
			// TODO Implement runtime spring config (optional)
		}
	}

	void doWithDynamicMethods() {
		// TODO Implement registering dynamic methods to classes (optional)
	}

	void doWithApplicationContext() {
		doRegisterExtensions()
		doMigrate(grailsApplication, applicationContext)
	}

	void onChange(Map<String, Object> event) {
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
		if (grailsApplication.isArtefactOfType(MigrationArtefactHandler.TYPE, event.source)) {
			log.debug("Migration ${event.source} changed. Reloading .. }")
			grailsApplication.addArtefact(MigrationArtefactHandler.TYPE, event.source)
			doMigrate(grailsApplication, applicationContext)
		}
	}

	void onConfigChange(Map<String, Object> event) {
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}

	void onShutdown(Map<String, Object> event) {
		// TODO Implement code that is executed when the application shuts down (optional)
	}
}
