#!/bin/sh
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "CreateTables.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetIDFromName.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetLatestTourneyDate.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetNameFromAlias.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetPlayerID.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertAlias.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertPlayer.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertTournament.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertPlayerName.sql"