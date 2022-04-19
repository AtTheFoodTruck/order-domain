FROM openjdk:11
ENV APP_HOME=/user/app
WORKDIR $APP_HOME
COPY ./build/libs/foodtruck-order-0.0.1-SNAPSHOT.jar OrderService.jar

CMD ["java", "-jar", "OrderService.jar"]
