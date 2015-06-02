#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -p 1234 final < $DIR/../src/create_tables.sql
psql -p 1234 final < $DIR/../src/create_indexes.sql
psql -p 1234 final < $DIR/../src/load_data.sql

psql -p 1234 final < $DIR/../src/sql_files/connection_entry.sql
psql -p 1234 final < $DIR/../src/sql_files/edu_details.sql
psql -p 1234 final < $DIR/../src/sql_files/message_entry.sql
psql -p 1234 final < $DIR/../src/sql_files/user_entry.sql
psql -p 1234 final < $DIR/../src/sql_files/work_expr.sql

