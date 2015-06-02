#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
<<<<<<< HEAD
cd ..
cd ..
cd ..
psql -p 1234 final < $DIR/../src/create_tables.sql
psql -p 1234 final < $DIR/../src/create_indexes.sql
psql -p 1234 final < $DIR/../src/load_data.sql
=======
psql -p $PGPORT $DB_NAME < $DIR/../src/create_tables.sql
psql -p $PGPORT $DB_NAME < $DIR/../src/create_indexes.sql
psql -p $PGPORT $DB_NAME < $DIR/../src/load_data.sql
>>>>>>> 0be0416766edcbfc850dc114abd379d1c254298b
