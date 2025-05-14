@echo off

set VM_USER=server
set VM_HOST=172.31.250.176

set JAR_NAME=xmart-zity-backend-1.0-SNAPSHOT-jar-with-dependencies.jar
set JAR_PATH=%CD%\xmart-city-backend\target\%JAR_NAME%

echo [1] Building backend module...
cd xmart-city-backend
call mvn clean compile assembly:single
cd ..

echo [2] Copying JAR to backend server...
scp "%JAR_PATH%" %VM_USER%@%VM_HOST%:/home/%VM_USER%/

echo [3] Killing any process using port 45065...
ssh %VM_USER%@%VM_HOST% "fuser -k 45065/tcp || echo Nothing running on port 45065"

echo [4] Running backend server...
ssh %VM_USER%@%VM_HOST% "java -jar /home/%VM_USER%/%JAR_NAME%"

echo [✔] Deployment complete.
pause
