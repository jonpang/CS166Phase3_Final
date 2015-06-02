#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -p 1234 final < $DIR/../src/create_tables.sql
psql -p 1234 final < $DIR/../src/create_indexes.sql
psql -p 1234 final < $DIR/../src/load_data.sql
