1. Update .travis.yml to:

language: java
os: linux
jdk:
    - openjdk8
script:
    - mvn test -DargLine="-Dcontiperf.active=false"

branches:
    only:
        - development_junit
    except:
        - master
        - development

2. Execute the following git commands:		
git branch development_junit
git checkout development_junit
git commit -m ".travis.yml updated"
git push origin development_junit

3. Go to Travis CI dashboard:

https://travis-ci.com/github/

4. Browse through the builds

