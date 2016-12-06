echo off

rem Parameters
set postgresbindir=C:\Program Files (x86)\PostgreSQL\9.5\bin
set postgresport=5433
set postgresusername=postgres

rem List all sql files in the dir
dir *.sql

rem Prompt user to give filename to restore
set /p filename="SQL filename to restore? "

call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" -f %filename%