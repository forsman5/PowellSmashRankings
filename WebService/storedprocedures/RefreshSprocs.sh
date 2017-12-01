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
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertPlacing.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertMatch.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetPlayerName.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetElo.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "SetElo.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetEmptyPlayers.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetAliases.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "DeletePlayer.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "DeletePlacings.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "DeleteMatches.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "GetMatches.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "UpdatePlayerId.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "UpdateAliasReferences.sql"
mysql -h "localhost" -u "root" "-padmin" "powellsmashdb" < "InsertEventLog.sql"
