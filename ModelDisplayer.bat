@echo off
REM simple command line to run ModelDisplayer with the name of a .XSI model

set WURM_PACKS=C:\$user\Wurm\packs
echo %WURM_PACKS%

set CP=./bin
set CP=%CP%;.
set CP=%CP%;./src
set CP=%CP%;./sample-models
set CP=%CP%;lib\jogl.jar
set CP=%CP%;%WURM_PACKS%\base.jar
set CP=%CP%;%WURM_PACKS%\optional1.jar;

java -classpath %CP% -Djava.library.path=./lib/jogl-natives-windows-i586 com.mojang.joxsi.demo.ModelDisplayer %1

pause