## MAVEN RUN
```
mvn -Dspring.profiles.active=dev
```



## DOCKER RUN MYSQL
```
docker run -d --name mysql \
   -e MYSQL_ROOT_PASSWORD=dodol123 \
   -e MYSQL_DATABASE=ngrest \
   -e MYSQL_USER=ngrest \
   -e MYSQL_PASSWORD=dodol123 \
   mysql
```

