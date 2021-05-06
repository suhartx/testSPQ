launch mysql

mvn clean compile

Create Datanucleus schema:
  mvn datanucleus:schema-create

Launch server:
  mvn jetty:run
  
Launch client:
  mvn exec:java -Pclient
  
  
To generate documentation write: 
  mvn doxygen:report
  

Notice that the workflow configured for GitHub Actions takes quite a long time since:
- It installs Java, mysqld and doxygen
- It also runs unit tests
- You should update it if you want to also enable Performance Tests to be executed
- The VERY GOOD THING about this GitHub Action workflow is that is able to test client/server communication, i.e. the RESTful API
