package com.netvitesse.nvconnect.migration

import java.util.NoSuchElementException;

/**
 * this class loads and builds the menu definition from a configuration file 
 * written in groovy
 * @author Antoine Roux
 * @author Jun Chen
 *
 */
class MigrationBuilder {
   // MigrationPosition parsedMigrationPosition

	
	    /** 
     * The map of the Migrations that were parsed. 
     * Key: Migration group. 
     * Value: list of Migration positions 
     */
    def migrations = [:]
    
    /** The list of changeSets that were parsed */
    def changeSets = []
    /** 
     * The list of all changes that were parsed.
     * Key: id of the MigrationItem.
     * Value: value of the MigrationItem. 
     */
    def changes = [:]
    
    /**
     * The name of the closure being currently parsed. "items" or "positions",
     * or "" if nothing is parsed currently.
     */
    def parsedClosure = ""
    
    /** The name of the Migration group being parsed */
    def migrationParsed = ""
    
    /**
     * two variable is useful when we load many files of configuration Migration.
     * at first one part of each file which define the definition of MigrationItem will be loaded
     * one time we finish to load all definition of  Migration item from all files , we start to
     * load MigrationPosition
     */
    boolean blockLoadChangeSets,blockLoadChanges
    /**
     * Parses the Migration
     * 
     * @param Migration The Migration configuration to parse
     * 
     * @author Antoine Roux
     */
    void parse(Closure migration) {
        migration.delegate = this
        migration.call()
    }
	
	
	void changeSet(Closure changeset) {
		if (blockLoadChangeSets) return
			parsedClosure = "changeSet"
		changeset.delegate = this
		changeset.call()
		parsedClosure = ""
	}

 /*
	
	    
     void leftShift(Closure migration) {
        parse migration
    }
    
  
    List<MigrationPosition> get(String migrationId) {
        return migrations."$migrationId"
    }
    
 
    void items(Closure items) {
        if (blockLoadItems) return
            parsedClosure = "items"
        items.delegate = this
        items.call()
        parsedClosure = ""
    }
 
    void positions(Closure positions) {
        if (blockLoadPositions) return 
            parsedClosure = "positions"
        positions.delegate = this
        positions.call()
        parsedClosure = ""
    }
	
	
	
    def methodMissing(String name, args) {
        if (args[0]){
            if (args[0] instanceof Closure) {
                // We are parsing a Migration
                // Name is the group, the closure contains the Migration elements
                migrationParsed = name
                if (migrations["$migrationParsed"] == null) migrations["$migrationParsed"] = []
                def migrationElements = args[0]
                migrationElements.delegate = this
                migrationElements.call()
                migrationParsed = ""
            }
            else if (args[0] instanceof Map) {
                if (parsedClosure == "items") {
                    args[0].id = name
                    migrationItems[name] = new MigrationItem(args[0])
                }
                else if (parsedClosure == "positions") {
                    // We are parsing a Migration position
                    // name is the id of the Migration item the map contains the r
                    if (!args[0].order)
                        args[0].order=0
                    //MigrationItems["$name"] is the item in the subItems of parsedMigrationPosition
                    if( ( migrationItems["$name"]) && parsedMigrationPosition )
                    {
                        args[0].id=name
                        args[0].item=MigrationItems["$name"]
                        parsedMigrationPosition.subItems << new MigrationPosition(args[0])
                    }
                    // if the group of MigrationParsed contains the idMigrationPosition, we add the child for this idMigrationPosition
                    else if (Migrations["$MigrationParsed"].id.contains(name))
                    {
                        def MigrationPositionId=Migrations["$MigrationParsed"].find{ it.id==name }
                        addChildsForMigrationPosition(MigrationPositionId,args[0])
                    }
                    else {
                        args[0].id=name
                        createMigrationPosition(name, args[0])
                    }
                }
                else throw new NoSuchElementException("The configuration element" +
                    "$name with arguments $args is neither in an items nor" +
                    "in a positions element")
            }
            else throw new NoSuchElementException("Your configuration contains" +
                "an unknown element: $name with arguments $args")
        }
        else {
            throw new Exception("the element name ${name} must have a parameter")
        }
    }
    
  
    def createMigrationPosition(mainItemName, Map args) {
        def closure 
        if (args.subItems instanceof Closure)  
            closure = args.subItems.clone()
        def mainItem = MigrationItems["$mainItemName"]
        if (!mainItem) throw new NoSuchElementException("There is no Migration item"+
            " with name $mainItemName")
        def MigrationPositionConfig = args
        MigrationPositionConfig.group = MigrationParsed
        MigrationPositionConfig.item = mainItem
        MigrationPositionConfig.subItems = []
        def MigrationPosition = new MigrationPosition(MigrationPositionConfig)
 
        
        if (closure) {
            parsedMigrationPosition = MigrationPosition
            closure.delegate = this
            closure.call()
            parsedMigrationPosition=null
        }
        else 
            positions << MigrationPosition
        Migrations["$MigrationParsed"] << MigrationPosition
    }
    
    def addChildsForMigrationPosition(MigrationPosition MigrationPosition,Map args){
        def closure 
        if (!MigrationPosition.order && args.order )
            MigrationPosition.order=  args.order
        if (args.subItems instanceof Closure)  
            closure = args.subItems.clone()
        if (closure) {
            parsedMigrationPosition = MigrationPosition
            closure.delegate = this
            closure.call()
            parsedMigrationPosition=null
        }
    }
    
 
    String getMigrationPositionIdForController(String controllerName,String actionName,String group){    
        def MigrationPositionList =Migrations["${group}"]
        def searchedPosition=  MigrationPositionList?.findAll {
            it.subItems.item.controller.contains(controllerName) && it.subItems.item.action.contains(actionName)
        }
        if (searchedPosition?.size()>1) {
            return ""
        }
        
        else if (searchedPosition?.size()==1)
            return searchedPosition[0].id
        
        searchedPosition = MigrationPositionList?.find {
            it.item.controller == controllerName ||
                    it.subItems.item.controller.contains(controllerName)
        }
        return searchedPosition?.id
    }
 
    
    def sort(){
        def MigrationLocationList
        Migrations.each{
            if (it.value instanceof List && it.value.size()> 0 ) {
                MigrationLocationList=it.value
                MigrationLocationList.each {
                    if (it.subItems)
                        (
                                it.subItems.sort{ it.order }
                                )
                }
                MigrationLocationList.sort {it.order }
            }
        }
    }
	
	*/
	
	
    /**
     * load all the definition of Migration from all configuration Migration file 
     *@author  Jun Chen 
     *@param MigrationClassList list of configuration Migration files
     */
    def loadAll(def migrationClassList) {
        migrations = [:]  
        //positions = []
        //migrationItems = [:] 
        parsedClosure = "" 
        migrationParsed = ""
        blockLoadChangeSets=false
        blockLoadChanges=true
        migrationClassList.each {
            if (it.migration)
                parse it.migration
        }
        blockLoadChangeSets=true
        blockLoadChanges=false
        migrationClassList.each {
            if (it.migration)
                parse it.migration
        }
        sort()
    }
}
