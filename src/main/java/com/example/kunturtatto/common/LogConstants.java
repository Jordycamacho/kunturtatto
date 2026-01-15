package com.example.kunturtatto.common;

public class LogConstants {
    
    // Audit Actions
    public static final String AUDIT_CREATE = "CREATE";
    public static final String AUDIT_UPDATE = "UPDATE";
    public static final String AUDIT_DELETE = "DELETE";
    public static final String AUDIT_READ = "READ";
    public static final String AUDIT_CANCEL = "CANCEL";
    public static final String AUDIT_CONFIRM = "CONFIRM";
    public static final String AUDIT_COMPLETE = "COMPLETE";
    
    // Entity Names
    public static final String ENTITY_SUBCATEGORY = "SubCategory";
    public static final String ENTITY_CATEGORY = "Category";
    public static final String ENTITY_DESIGN = "Design";
    public static final String ENTITY_APPOINTMENT = "Appointment";
    public static final String ENTITY_USER = "User";
    
    // Log Messages
    public static final String MSG_ENTITY_NOT_FOUND = "Entity not found";
    public static final String MSG_CREATION_STARTED = "Creation started";
    public static final String MSG_UPDATE_STARTED = "Update started";
    public static final String MSG_DELETE_STARTED = "Delete started";
    public static final String MSG_CANCEL_STARTED = "Cancel started";
    public static final String MSG_CONFIRM_STARTED = "Confirm started";
    public static final String MSG_COMPLETE_STARTED = "Complete started";
    
    // Appointment Specific
    public static final String APPOINTMENT_TIME_VALIDATION = "Appointment time validation";
    public static final String APPOINTMENT_NOTIFICATION_SENT = "Appointment notification sent";
    public static final String APPOINTMENT_STATUS_CHANGED = "Appointment status changed";
    
    // Cache Operations
    public static final String CACHE_HIT = "Cache hit";
    public static final String CACHE_MISS = "Cache miss";
    public static final String CACHE_EVICT = "Cache evict";
    
    private LogConstants() {
    }
}