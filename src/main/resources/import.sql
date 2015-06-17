-- load captor seed data

-- setup admin user
insert into authuser (id, identifier, user_name, password, name, email, modified_by, modified_date, object_version) values (1, 1, 'admin', 'admin', 'Administrator','admin@gmail.com','captor','2012-12-01',0);
insert into authrole (id, user_name, role_name, role_group, modified_by, modified_date, object_version, authuser_id) values (1, 'admin', 'Admin', 'Admins','captor','2012-12-01',0, 1);
insert into preference (id, keyword, strval, modified_by, modified_date, object_version, authuser_id) values (1, 'theme', 'aristo', 'captor','2012-12-01',0, 1);
insert into preference (id, keyword, blnval, modified_by, modified_date, object_version, authuser_id) values (2, 'DisplayTooltips', true, 'captor','2012-12-01',0, 1);