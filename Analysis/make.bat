SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_19

mkdir build
cd build
xcopy /E /I ..\bin\classes\org org
xcopy /E /I ..\bin\classes\dic dic

"%JAVA_HOME%\bin\jar" -cvfm ..\org.snu.ids.ha.3.0.jar ..\Manifest.txt org dic
cd ..
rmdir /S /Q build
copy org.snu.ids.ha.3.0.jar ..\org.snu.ids.sejong\web\WEB-INF\lib\org.snu.ids.ha.3.0.jar
