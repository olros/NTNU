@echo off
IF EXIST C:\Program Files\Java\jdk1.7.0_80\bin SET PATH=%PATH%;C:\Program Files\Java\jdk1.7.0_80\bin
javac %~n1.java
jar cvfm %~n1.jar manifest.txt %~n1.class
REM pause