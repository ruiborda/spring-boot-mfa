# Spring Boot MFA Demo

Aplicación de demostración de autenticación de dos factores (MFA) desarrollada con Spring Boot.

## Ejecución de la aplicación

### Opción 1: Ejecución con Docker

1. Asegúrate de tener Docker y Docker Compose instalados
2. Ejecuta el siguiente comando en la raíz del proyecto:
   ```
   docker-compose up
   ```
3. La aplicación estará disponible en [http://localhost:8080](http://localhost:8080)

### Opción 2: Ejecución local

1. Requisitos previos:
   - Java 21
   - PostgreSQL
   - Maven

2. Configura tu base de datos PostgreSQL:
   - Crea una base de datos llamada `database`
   - Usuario: `user`
   - Contraseña: `password`

3. Ejecuta la aplicación:
   ```
   ./mvnw spring-boot:run
   ```

4. Accede a la aplicación en [http://localhost:8080](http://localhost:8080)

## Uso de la aplicación

1. **Registro de usuario**: Usa el endpoint `/api/v1/users` para crear un usuario mediante la API REST.

2. **Inicio de sesión**: 
   - Accede a [http://localhost:8080](http://localhost:8080)
   - Ingresa el correo electrónico y contraseña del usuario registrado

3. **Configuración de MFA**:
   - Una vez iniciada la sesión, accede al dashboard
   - Haz clic en "Enable 2FA"
   - Escanea el código QR con una aplicación de autenticación (Google Authenticator, Authy, etc.)
   - Ingresa el código de 6 dígitos para verificar la configuración

4. **Validación de MFA**:
   - En próximos inicios de sesión, después de ingresar las credenciales, se solicitará el código de verificación
   - Introduce el código de 6 dígitos mostrado en tu aplicación de autenticación

## Documentación API

Accede a la documentación de la API en [http://localhost:8080/api/v1](http://localhost:8080/api/v1)