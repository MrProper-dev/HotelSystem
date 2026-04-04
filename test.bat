@echo off

javac -sourcepath "./src;./test" -cp ./lib/* -d ./testBin test/MainTest.java

java -cp "./lib/*;./testBin" MainTest