@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
if not exist bin mkdir bin
javac --release 17 -encoding UTF-8 -d bin src\id3\*.java test\id3\*.java
if errorlevel 1 exit /b 1
if "%~1"=="--test" (
  java -Dfile.encoding=UTF-8 -cp bin id3.Id3Test
) else (
  java -Dfile.encoding=UTF-8 -cp bin id3.Main %*
)
exit /b %errorlevel%
