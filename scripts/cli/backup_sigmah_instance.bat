echo off

rem Parameters
set postgresbindir=C:\Program Files (x86)\PostgreSQL\9.5\bin
set sigmahrootfilesdir=C:\Sigmah\files
set dir7zip=C:\Program Files\7-Zip
set sqlscriptsdir=..\sql
set postgresport=5432
set postgresusername=postgres

rem List all available databases
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" -t -A -l

rem Prompt user to give database name to dump
set /p database="Sigmah instance unique Database & Files dir name? "
rem set database=sigmah_2.2-demo3

rem Add check constraints defer/restore functions
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" --dbname %database% -f %sqlscriptsdir%\Sigmah_DeferCheckConstraints.sql

rem Defer check constraints
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" --dbname %database% -t -c "select defer_checkconstraints();"

rem Backup the database
call "%postgresbindir%\pg_dump.exe" --host localhost --port %postgresport% --username "%postgresusername%" --no-password  --format plain --no-owner --inserts --column-inserts --no-privileges --no-tablespaces --verbose --no-unlogged-table-data --file "%database%.sql" "%database%"

rem Add check constraints restore at the end of the backup
echo --Restore check constraints and drop related defer/restore functions  >> %database%.sql
echo SELECT restore_checkconstraints(); >> %database%.sql
echo DROP FUNCTION defer_checkconstraints(); >> %database%.sql
echo DROP FUNCTION restore_checkconstraints(); >> %database%.sql

rem Restore check constraints (and drop those defer/restore functions)
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" --dbname %database% -t -c "select restore_checkconstraints();"
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" --dbname %database% -t -c "DROP FUNCTION defer_checkconstraints();"
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" --dbname %database% -t -c "DROP FUNCTION restore_checkconstraints();"

rem Create zip of sql and attached files
move %database%.sql %sigmahrootfilesdir%
rem call "%dir7zip%\7z.exe" -r -tzip -mm=BZip2 -mx9 a %sigmahrootfilesdir%\%database%--dbANDfiles.zip %sigmahrootfilesdir%\%database% %sigmahrootfilesdir%\%database%.sql
call "%dir7zip%\7z.exe" -r -tzip -mx9 a %database%--dbANDfiles.zip %sigmahrootfilesdir%\%database% %sigmahrootfilesdir%\%database%.sql   
move %sigmahrootfilesdir%\%database%.sql .
