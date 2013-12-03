CREATE TABLE action_type
(
id INTEGER NOT NULL PRIMARY KEY,
action_name TEXT
);

CREATE TABLE eclipse_action
(
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
last_action INTEGER,
time_since_last INTEGER,
send_status INTEGER,
action INTEGER,
context_change BOOLEAN,
last_actions TEXT,
recent_same_actions_count INTEGER,
time_stamp DATETIME DEFAULT CURRENT_TIMESTAMP,
package_distance INTEGER,
recent_lines_added INTEGER,
recent_lines_changed INTEGER,
recent_lines_deleted INTEGER,
average_lines_added INTEGER,
average_lines_changed INTEGER,
average_lines_deleted INTEGER,
average_package_diff DOUBLE,
average_package_action_diff DOUBLE,
max_package_diff INTEGER,
min_package_diff INTEGER,
average_duration_diff INTEGER,
average_duration_action_diff INTEGER,
max_duration_diff INTEGER,
min_duration_diff INTEGER,
context_same_actions INTEGER,
same_actions_ratio DOUBLE,
time_since_last_same INTEGER,
same_transitions_count INTEGER,
same_transitions_ratio DOUBLE,
FOREIGN KEY (last_action) REFERENCES action_type(id),
FOREIGN KEY (action) REFERENCES action_type(id)
);

INSERT INTO action_type(id, action_name) values (1, 'ADD_FILE');
INSERT INTO action_type(id, action_name) values (2, 'ADD_PACKAGE');
INSERT INTO action_type(id, action_name) values (3, 'ADD_PROJECT');
INSERT INTO action_type(id, action_name) values (4, 'CLOSE_FILE');
INSERT INTO action_type(id, action_name) values (5, 'DELETE_FILE');
INSERT INTO action_type(id, action_name) values (6, 'DELETE_PACKAGE');
INSERT INTO action_type(id, action_name) values (7, 'OPEN_FILE');
INSERT INTO action_type(id, action_name) values (8, 'REFATOR_FILE');
INSERT INTO action_type(id, action_name) values (9, 'REFACTOR_PACKAGE');
INSERT INTO action_type(id, action_name) values (10, 'SWITCH_FILE');

CREATE TABLE add_file
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
same_type boolean,
added_file TEXT,
previous_file TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE add_package
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
added_package TEXT,
previous_file TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE add_project
(
action_id INTEGER NOT NULL,
name TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE close_file
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
same_type boolean,
closed_file TEXT,
previous_file TEXT,
deleted_lines INTEGER,
added_lines INTEGER,
changed_lines INTEGER,
work_time INTEGER,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE delete_file
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
same_type boolean,
deleted_file TEXT,
previous_file TEXT,
deleted_lines INTEGER,
added_lines INTEGER,
changed_lines INTEGER,
work_time INTEGER,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE delete_package
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
deleted_package TEXT,
previous_file TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE open_file
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
same_type boolean,
opened_file TEXT,
previous_file TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE refactor_file
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
refactor_type INTEGER,
refactored_file TEXT,
previous_file TEXT,
old_file_path TEXT,
new_file_path TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE refactor_package
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
refactor_type INTEGER,
refactored_package TEXT,
previous_file TEXT,
old_package_path TEXT,
new_package_path TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);

CREATE TABLE switch_file
(
action_id INTEGER NOT NULL,
same_package boolean,
same_project boolean,
same_type boolean,
switched_file TEXT,
previous_file TEXT,
FOREIGN KEY (action_id) REFERENCES eclipse_action(id)
);



