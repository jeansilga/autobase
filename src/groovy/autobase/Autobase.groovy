package autobase;

import liquibase.*
import liquibase.database.*
import liquibase.log.LogFactory;
import liquibase.dsl.command.MigrateCommand
import liquibase.dsl.properties.LbdslProperties as Props
import liquibase.parser.groovy.*;
import org.codehaus.groovy.grails.commons.ConfigurationHolder as Config
import org.codehaus.groovy.grails.commons.ApplicationHolder as App
import org.springframework.web.context.WebApplicationContext as WAC
import grails.util.GrailsUtil
import org.apache.log4j.*;
import org.hibernate.FlushMode
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.util.GrailsUtil
import org.apache.log4j.*;

class Autobase {

  private static final Logger log = Logger.getLogger(Autobase)

  private static final InheritableThreadLocal appCtxHolder = new InheritableThreadLocal()

	static void migrate(appCtx, app) {
    appCtxHolder.set(appCtx)
    boolean attachedSession = false
    try {
      attachedSession = attachHibernateSession()
      assignSystemProperties();
      Database db = getDatabase();
	  
	  new LiquibaseDsl("",null, db, app).update(null)

    } catch(Exception e) {
      GrailsUtil.deepSanitize(e)
      throw e
    } finally {
      if(attachedSession) {
        try {
          detachHibernateSession()
        } catch(Exception e2) {
          GrailsUtil.deepSanitize(e2)
          log.error("Cannot detach the Hibernate session", e2)
        }
      }
      appCtxHolder.set(null)
    }
  }

  private static boolean attachHibernateSession() {
    def sessionFactory = getSessionFactory()
    final Object inStorage = TransactionSynchronizationManager.getResource(sessionFactory);
    if(inStorage != null) {
      ((SessionHolder)inStorage).getSession().flush();
      return false;
    } else {
      Session session = SessionFactoryUtils.getSession(sessionFactory, true);
      session.setFlushMode(FlushMode.AUTO);
      TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
      return true;
    }
  }

  private static void detachHibernateSession() {
    def sessionFactory = getSessionFactory()
    final SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
    if(!FlushMode.MANUAL.equals(sessionHolder.getSession().getFlushMode())) {
      sessionHolder.getSession().flush();
    }
    SessionFactoryUtils.closeSession(sessionHolder.getSession());
  }


  static FileOpener findFileOpener() {
    if(App.application?.isWarDeployed()) {
      return new WarFileOpener()
    } else {
      return new FileSystemFileOpener()
    }
  }

  static void assignSystemProperties() {
    assignSystemProperty("lbdsl.home", new File((String)System.properties["user.home"], (String)".lbdsl").canonicalPath)
  }

  static void assignSystemProperty(String propName, String defValue) {
    System.properties[propName] = System.properties[propName] ?:
                                  Config.config.autobase."$propName" ?:
                                  defValue
  }

  private static SessionFactory getSessionFactory() {
    def ctx = appCtxHolder.get()
    if(!ctx) { throw new IllegalStateException("No web application context found") } 
    return (SessionFactory)ctx.getBean('sessionFactory')
  }

	static Database getDatabase() {
		return DatabaseFactory.instance.findCorrectDatabaseImplementation(getSessionFactory().currentSession.connection())
	} 

}
