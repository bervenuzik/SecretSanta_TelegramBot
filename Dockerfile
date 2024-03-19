# Используем базовый образ Java
FROM amazoncorretto:17.0.8

# Установка рабочей директории
WORKDIR /app

# Копируем собранный JAR-файл в контейнер
COPY SecretSanta-0.0.1-SNAPSHOT.jar /app/SecretSanta.jar

# Определяем порт, который будет доступен из контейнера
EXPOSE 8080

# Команда для запуска приложения
CMD ["java", "-jar", "SecretSanta.jar"]

