package liquibase.dsl.parser.groovy

import autobase.migration.DefaultMigrationClass;

import grails.test.*

class GroovyChangeLogParserTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    /**
     * The method should return sorted migration classes, using the runAfter
     * values.
     */
    void testGetSortedMigrationClasses() {
        def parser = new GroovyChangeLogParser()
        def migrationClasses = [FourMigration, OneMigration, FiveMigration, 
                                   ThreeMigration, OneBisMigration, TwoMigration ]
        
        parser.migrationClasses = migrationClasses.collect { new DefaultMigrationClass(it) }
        
        def result = parser.getSortedMigrations()

        println result.name
        assertTrue result.originalClass.indexOf(OneMigration) < result.originalClass.indexOf(TwoMigration)
        assertTrue result.originalClass.indexOf(OneBisMigration) < result.originalClass.indexOf(TwoMigration)
        assertTrue result.originalClass.indexOf(OneBisMigration) < result.originalClass.indexOf(ThreeMigration)
        assertTrue result.originalClass.indexOf(OneMigration) < result.originalClass.indexOf(TwoMigration)
        assertTrue result.originalClass.indexOf(OneMigration) < result.originalClass.indexOf(FourMigration)
        assertTrue result.originalClass.indexOf(TwoMigration) < result.originalClass.indexOf(FourMigration)
        assertTrue result.originalClass.indexOf(ThreeMigration) < result.originalClass.indexOf(FiveMigration)
    }
    
    void testGetSortedMigrationClassesWithDependentMigrations() {
        def parser = new GroovyChangeLogParser()
        def migrationClasses = [OneMigration, UMigration, LPSMigration, 
                                LGMigration, LCMigration,
                                TwoMigration, ThreeMigration ]
        
        parser.migrationClasses = migrationClasses.collect { new DefaultMigrationClass(it) }
        
        def result = parser.getSortedMigrations()

        println result.name
        assertTrue result.originalClass.indexOf(LCMigration) < result.originalClass.indexOf(UMigration)
        assertTrue result.originalClass.indexOf(UMigration) < result.originalClass.indexOf(LGMigration)
        assertTrue result.originalClass.indexOf(LPSMigration) < result.originalClass.indexOf(LGMigration)
    }
    
}

class OneMigration {
    
}

class OneBisMigration {
    static runAfter = []
}

class TwoMigration {
    static runAfter = [OneMigration]
}

class ThreeMigration {
    static runAfter = [OneMigration]
}

class FourMigration {
    static runAfter = [OneMigration, TwoMigration]
}

class FiveMigration {
    static runAfter = [ThreeMigration]
}

class UMigration {
    static  runAfter = [LCMigration]
}

class LCMigration {
    static runAfter = [OneMigration, TwoMigration, ThreeMigration]
}

class LPSMigration {
    static runAfter = [LCMigration, UMigration]
}

class LGMigration {
    static runAfter = [LPSMigration, LCMigration]
}
