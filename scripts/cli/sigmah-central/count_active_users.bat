rem Sigmah batch file to compute number of active users base on the csv files exported from Sigmah Central server

rem Batch configuration
echo off
cls

rem Batch parameters 
set users_lists_folder=ListesUtilisateurs

rem Input management
rem IF %1==withpause SET withpause=true

cd %users_lists_folder%
FOR %%F IN (*.csv) DO call :ProcessFile %%F

rem End batch
cd ..
rem IF %withpause%==true pause
IF %1==withpause pause

:ProcessFile
rem extract organisation sub-domain name from file name
FOR /F "tokens=1 delims=." %%o IN ("%1") DO SET name=%%o

rem count the number of active users
set /A c=0
setlocal ENABLEDELAYEDEXPANSION
FOR /F "skip=1 tokens=5 delims=," %%a IN (%1) DO (
	IF %%a==t SET /A c=c+1
	)
	
rem display result
ECHO %name%	!c!
goto :eof
