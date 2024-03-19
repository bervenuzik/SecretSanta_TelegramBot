# Используем базовый образ Java
FROM amazoncorretto:17.0.8

# Установка рабочей директории
WORKDIR /app

# Копируем собранный JAR-файл в контейнер
COPY target/SecretSanta-0.0.1-SNAPSHOT.jar /app/SecretSanta.jar

# Команда для запуска приложения
CMD ["java", "-jar", "SecretSanta.jar"]