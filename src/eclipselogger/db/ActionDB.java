package eclipselogger.db;

public class ActionDB {
	
	// EclipseAction
	public static final String ECLIPSE_ACTION_ID = "id";
	public static final String LAST_ACTION = "last_action";
	public static final String TIME_SINCE_LAST = "time_since_last";
	public static final String SEND_STATUS = "send_status";
	public static final String ACTION_TYPE = "action";
	public static final String CONTEXT = "context_change";
	public static final String TIMESTAMP = "time_stamp";
	public static final String RECENT_ACTIONS = "last_actions";
	public static final String RECENT_SAME_ACTIONS_COUNT = "recent_same_actions_count";
	public static final String PACKAGE_DISTANCE = "package_distance";
	
	// context
	public static final String TOTAL_PACKAGES = "total_packages";
	public static final String TOTAL_RESOURCES = "total_resources";
	public static final String PACKAGE_WORKED_BEFORE = "package_before";
	public static final String RESOURCE_WORKED_BEFORE = "resource_before";
	
	public static final String RECENT_LINES_ADDED = "recent_lines_added";
	public static final String RECENT_LINES_CHANGED = "recent_lines_changed";
	public static final String RECENT_LINES_DELETED = "recent_lines_deleted";
	public static final String AVERAGE_LINES_ADDED = "average_lines_added";
	public static final String AVERAGE_LINES_CHANGED = "average_lines_changed";
	public static final String AVERAGE_LINES_DELETED = "average_lines_deleted"; 
	
	public static final String AVERAGE_PACKAGE_DIFF = "average_package_diff";
	public static final String AVERAGE_PACKAGE_ACTION_DIFF = "average_package_action_diff";
	public static final String MAX_PACKAGE_DIFF = "max_package_diff";
	public static final String MIN_PACKAGE_DIFF = "min_package_diff"; 
	
	public static final String AVERAGE_DURATION_DIFF = "average_duration_diff";
	public static final String AVERAGE_DURATION_ACTION_DIFF = "average_duration_action_diff";
	public static final String MAX_DURATION_DIFF = "max_duration_diff";
	public static final String MIN_DURATION_DIFF = "min_duration_diff";
	
	public static final String CONTEXT_SAME_ACTIONS = "context_same_actions";
	public static final String SAME_ACTIONS_RATIO = "same_actions_ratio";
	public static final String TIME_SINCE_LAST_SAME = "time_since_last_same";
	public static final String SAME_TRANSITIONS_COUNT = "same_transitions_count";
	public static final String SAME_TRANSITIONS_RATIO = "same_transitions_ratio";
	
	// add file
	public static final String SAME_PACKAGE = "same_package";
	public static final String SAME_PROJECT = "same_project";
	public static final String SAME_TYPE = "same_type";
	public static final String ADDED_FILE = "added_file";
	public static final String PREVIOUS_FILE = "previous_file";
	
	// add package
	public static final String ADDED_PACKAGE = "added_package";
	
	// add project
	public static final String PROJECT_NAME = "name";
	
	// close file
	public static final String CLOSED_FILE = "closed_file";
	public static final String LINES_DELETED = "deleted_lines";
	public static final String LINES_ADDED = "added_lines";
	public static final String LINES_CHANGED = "changed_lines";
	public static final String WORK_TIME = "work_time";
	
	
	// delete file
	public static final String DELETED_FILE = "deleted_file";
	
	// delete package
	public static final String DELETED_PACKAGE = "deleted_package";
	
	// open file
	public static final String OPENED_FILE = "opened_file";
	
	// refactor file
	public static final String REFACTORED_FILE = "refactored_file";
	public static final String REFACTOR_TYPE = "refactor_type";
	public static final String REFACTOR_OLD_FILE = "old_file_path";
	public static final String REFACTOR_NEW_FILE = "new_file_path";
	
	// refactor package
	public static final String REFACTORED_PACKAGE = "refactored_package";
	public static final String REFACTOR_OLD_PACKAGE = "old_package_path";
	public static final String REFACTOR_NEW_PACKAGE = "new_package_path";
	
	// switch file
	public static final String SWITCHED_FILE = "switched_file";
	
	
	// common
	public static final String ACTION_ID = "action_id";
	
	
	
}
