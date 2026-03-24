@echo off

set CATALINA_HOME=C:\Users\Admin\Desktop\Diploma Favkes\apache-tomcat-10.1.52

for /r src %%i in (*.java) do (
    javac -sourcepath ./src -cp ./lib/* -d ./artefact/WEB-INF/classes "%%i"
)

if errorlevel 1 (
    echo compiling fail
    exit
)

mkdir artefact\WEB-INF\lib
copy lib\postgresql-42.7.8.jar artefact\WEB-INF\lib

set APP_NAME=hotelsystem

jar cf %APP_NAME%.war -C ./artefact . -C ./resources .

copy %APP_NAME%.war "%CATALINA_HOME%\webapps"

del %APP_NAME%.war

rmdir /s /q artefact

"%CATALINA_HOME%\bin\startup.bat"