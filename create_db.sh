sudo -u postgres psql <<EOF
create user $1_admin with password 'admin';
create database $1 owner $1_admin;
create database $1_test owner $1_admin;
EOF
