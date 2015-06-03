#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -p 1245 mydb < $DIR/../src/create_tables.sql
psql -p 1245 mydb < $DIR/../src/create_indexes.sql
psql -p 1245 mydb < $DIR/../src/load_data.sql

psql -p 1245 mydb < $DIR/../src/sql_files/connection_entry.sql
psql -p 1245 mydb < $DIR/../src/sql_files/edu_details.sql
psql -p 1245 mydb < $DIR/../src/sql_files/message_entry.sql
psql -p 1245 mydb < $DIR/../src/sql_files/user_entry.sql
psql -p 1245 mydb < $DIR/../src/sql_files/work_expr.sql

