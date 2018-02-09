echo off

rem Parameters
set postgresbindir=C:\Program Files (x86)\PostgreSQL\9.5\bin
set sigmahrootfilesdir=C:\Sigmah\files
set dir7zip=C:\Program Files\7-Zip
set sqlscriptsdir=C:\Sigmah\dev\sigmah-lightdev\sigmah\scripts\sql
set postgresport=5433
set postgresusername=postgres

rem List all available databases
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" -t -A -l

rem Prompt user to give database name to dump et files dir to copy
set /p database="Sigmah instance unique Database & Files dir name? "
rem set database=sigmah_2.2-demo3


rem Duplicate the database
call "%postgresbindir%\psql.exe"  --host localhost --port %postgresport% --username "%postgresusername%" --dbname %database% -t -c "CREATE DATABASE \"%database%-copy\" WITH TEMPLATE \"%database%\";"

rem Create the files dir copy folder
mkdir %sigmahrootfilesdir%\%database%-copy

rem Copy the files dir
xcopy /E /C %sigmahrootfilesdir%\%database% %sigmahrootfilesdir%\%database%-copy
