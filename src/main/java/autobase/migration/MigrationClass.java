package autobase.migration;
import groovy.lang.Closure;
import grails.core.GrailsClass;

public interface MigrationClass extends GrailsClass{

    public void setMigration();
    public Closure getMigration();
    
}
