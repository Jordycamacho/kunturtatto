package com.example.kunturtatto.common;

public class LogConstants {
    
    // Audit Actions
    public static final String AUDIT_CREATE = "CREATE";
    public static final String AUDIT_UPDATE = "UPDATE";
    public static final String AUDIT_DELETE = "DELETE";
    public static final String AUDIT_READ = "READ";
    
    // Entity Names
    public static final String ENTITY_SUBCATEGORY = "SubCategory";
    public static final String ENTITY_CATEGORY = "Category";
    public static final String ENTITY_DESIGN = "Design";
    
    // Log Messages
    public static final String MSG_ENTITY_NOT_FOUND = "Entity not found";
    public static final String MSG_CREATION_STARTED = "Creation started";
    public static final String MSG_UPDATE_STARTED = "Update started";
    public static final String MSG_DELETE_STARTED = "Delete started";
    
    private LogConstants() {
        // Utility class
    }
}