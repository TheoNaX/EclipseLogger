package eclipselogger.events.actions;

public enum ActionType {
	ADD_FILE(1),
	ADD_PACKAGE(2),
	ADD_PROJECT(3),
	CLOSE_FILE(4),
	DELETE_FILE(5),
	DELETE_PACKAGE(6),
	OPEN_FILE(7),
	REFACTOR_FILE(8),
	REFACTOR_PACKAGE(9),
	SWITCH_FILE(10);
	
	private final int value;
    private ActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
