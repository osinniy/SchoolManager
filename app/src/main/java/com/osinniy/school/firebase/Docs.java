package com.osinniy.school.firebase;

public final class Docs {

//    collections
    public static final String COL_IMPORTANT = "Important";
    public static final String COL_DZ        = "DZ";
    public static final String COL_USERS     = "Users";
    public static final String COL_GROUPS    = "Groups";

//    documents
    public static final String DOC_GROUP_USERS = "Users";
    public static final String DOC_TIMETABLE   = "Timetable";
    public static final String DOC_DATA        = "Data";
    public static final String DOC_METADATA    = "Metadata";

//    references
    public static final String REF_GROUP_CODES = "/Groups/GroupCodes";

//    info about user
    public static final String UID = "uid";

//    info about dz obj
    public static final String HOMEWORK    = "homework";
    public static final String TARGET_DATE = "target_date";

//    info about bindable classes
    public static final String IS_CHANGED = "changed";

//    group metadata
    public static final String CODE            = "code";
    public static final String NUM_OF_MEMBERS  = "number_of_members";
    public static final String ADMIN_ID        = "admin_id";
    public static final String TIMETABLE_ADDED = "timetable_added";

//    log tags
    public static final String TAG_FIRESTORE_READ  = "Firestore Read";
    public static final String TAG_FIRESTORE_WRITE = "Firestore Write";

//    fields
    public static final String USERS_ARRAY       = "users";
    public static final String GROUP_CODES_ARRAY = "codes";
    public static final String DESCRIPTION       = "description";

//    other
    public static final String NAME          = "name";
    public static final String TEXT          = "text";
    public static final String CREATION_DATE = "creation_date";
    public static final String EDIT_DATE     = "edit_date";
    public static final String ID            = "id";

}
