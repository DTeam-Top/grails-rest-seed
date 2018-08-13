sudo -u postgres psql <<EOF
create user $1_admin with password 'admin';
create database $1;
alter DATABASE $1 owner to $1_admin;
create database $1_test;
alter DATABASE $1_test owner to $1_admin;
EOF