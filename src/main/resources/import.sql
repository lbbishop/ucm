-- load ucm seed data

-- setup admin user
insert into authuser (id, identifier, user_name, password, name, email, modified_by, modified_date, object_version) values (1, 1, 'admin', 'admin', 'Administrator','admin@gmail.com','ucm','2016-10-01',0);
insert into authrole (id, user_name, role_name, role_group, modified_by, modified_date, object_version, authuser_id) values (1, 'admin', 'Admin', 'Admins','ucm','2016-10-01',0, 1);
