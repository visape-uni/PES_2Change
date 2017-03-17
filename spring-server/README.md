#2change backend server

##To compile
1. Make sure you have Java 1.8+ installed.
2. Run `./gradlew build`
3. You will find the compiled `.war` file in `/build/libs/spring-server-version.war`, where `version` is the version name defined in the file `build.gradle`.

##To deploy (Windows)
1. Install [Apache Tomcat](http://tomcat.apache.org/)
2. Copy the `.war` file you got from compilation to `/path/to/your/tomcat/webapps`
3. Open a terminal **(as administrator)** and `cd` to `/path/to/your/tomcat/bin`.
4. Run `./startup.bat` (most likely something like `sudo ./some_startup_script` on Linux)


And that's it. Now you should have a local Tomcat server running at `localhost:8080`. You can chech if the 2change server has been correctly deployed by accessing `http://localhost:8080/manager/html` with your web browser and making sure `spring-server-version` is running.

The 2change server's root will be `localhost:8080/spring-server-version`. You have a simple Hello World example at `http://localhost:8080/spring-server-version/greeting`.