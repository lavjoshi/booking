FROM openjdk:17
LABEL maintainer="elements"
ADD target/Booking-0.0.1-SNAPSHOT.jar booking.jar
ENTRYPOINT ["java", "-jar", "booking.jar"]