grails.app.context="/"


// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// log4j configuration
log4j = {
	appenders {
		/*rollingFile name:'nvconnect', file:"./var/log/nvconnect.log".toString(), maxFileSize:'5000KB'
		 console name: 'stdout', layout: pattern(conversionPattern:'%d [%t] %-5p %c{2} %x - %m%n')
		 file name: 'stackTraceLog', layout: pattern(conversionPattern:'%d [%t] %-5p %c{2} %x - %m%n'), file: 'stacktrace.log'*/
		file name:'file', file:'./var/log/mylog.log'
	}
	
	root {
		warn 'stdout','file'
		additivity = true
		//warn 'stdout'    
		//additivity = true
	}
	
	// Grails default logging
	error 'org.springframework', 
			'org.hibernate',
			'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'org.springframework',
			'org.hibernate',
			'net.sf.ehcache.hibernate'
			
	
}
// The following properties have been added by the Upgrade process...
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
