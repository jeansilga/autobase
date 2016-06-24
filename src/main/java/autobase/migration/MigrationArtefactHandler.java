package autobase.migration;

import grails.core.ArtefactHandlerAdapter;

class MigrationArtefactHandler extends ArtefactHandlerAdapter {
    static public final String TYPE = "Migration";
    static public final String SUFFIX = "Migration";

    public MigrationArtefactHandler() {
        super(TYPE, MigrationClass.class, DefaultMigrationClass.class, SUFFIX);
    }
}