#!/bin/bash

set -e
set -u

function create_user_and_database() {
	local database=$(echo "$1" | cut -d ":" -f 1)
	local password=$(echo "$1" | cut -d ":" -f 2-)

	echo "  Creating user and database '$database'"
	psql -v ON_ERROR_STOP=1 -U "${POSTGRES_USER}" <<-EOSQL
	    CREATE USER "${database}" WITH ENCRYPTED PASSWORD '${password}';
	    CREATE DATABASE "${database}";
	    GRANT ALL PRIVILEGES ON DATABASE "${database}" TO "${database}";
EOSQL
}

if [[ -n "${POSTGRES_DATABASES}" ]]; then
	echo "The following databases will be created: ${POSTGRES_DATABASES}"
	for db in $(echo ${POSTGRES_DATABASES} | tr ',' ' '); do
		create_user_and_database ${db}
	done
	echo "Databases created"
fi
