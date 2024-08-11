@echo off

REM Compile the project
echo Compiling the project...
mvn clean compile

REM Run the project
echo Running the project...
mvn exec:java -Dexec.mainClass="edu.rpi.cs.csci4963.u24.wangn4.hw03.battleship.network.BattleShipApplication"

REM Generate Javadocs
echo Generating Javadocs...
mvn javadoc:javadoc

REM Move Javadocs to docs folder
echo Moving Javadocs to docs folder...
if not exist docs mkdir docs
xcopy /s /e /q /y target\site\apidocs docs

echo Done!
pause
