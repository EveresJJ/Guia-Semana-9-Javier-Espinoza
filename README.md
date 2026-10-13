# Práctica Guiada n.º 9 — Seguridad con Spring Security y JWT (Comercial NicaMarket, S.A.)

**Universidad:** UAM · **Carrera:** Ingeniería de Sistemas  
**Asignatura:** Servicios Web · **Estudiante:** Javier Espinoza

---

## 1. Instrucciones breves de ejecución

1. **Requisitos previos:**
   - Java 17 o superior (configurado con Java 21) y Maven.
   - Base de datos PostgreSQL activa en `localhost:5432` con la base de datos `gestion_productos`.

2. **Configurar la variable de entorno `JWT_SECRET_BASE64`:**
   - Genere una clave aleatoria de 32 bytes codificada en Base64 (algoritmo `HS256`) y configúrela como variable de entorno en su IDE (IntelliJ IDEA: *Run -> Edit Configurations -> Environment variables*) o en su terminal antes de iniciar la aplicación:
     ```powershell
     # Ejemplo en PowerShell (genera una clave aleatoria de 32 bytes en Base64 para la sesión actual)
     $bytes = New-Object byte[] 32
     [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
     $env:JWT_SECRET_BASE64 = [Convert]::ToBase64String($bytes)
     ```
   - *(Por seguridad, el repositorio no incluye ningún secreto JWT ni tokens vigentes).*

3. **Iniciar el servidor:**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. **Usuarios de prueba configurados en memoria:**
   - **Administrador (`ROLE_ADMIN`):** `admin` / `Admin123!`
   - **Operador (`ROLE_USER`):** `operador` / `Operador123!`

---

## 2. Matriz de Roles y Permisos

| Rol | Permisos implementados |
|---|---|
| **Invitado (Público)** | `GET /api/publico/saludo` y `POST /api/auth/login` |
| **USER (`operador`)** | Consultar (`GET`) en `/api/productos/**`, `/api/categorias/**` y consultar perfil propio en `GET /api/auth/perfil` |
| **ADMIN (`admin`)** | Consultar (`GET`) y modificar (`POST`, `PUT`, `DELETE`) en `/api/productos/**` y `/api/categorias/**`, además de `GET /api/auth/perfil` |

---

## 3. Colección de Postman y Evidencias del Reto Final

El archivo [`NicaMarket-JWT.postman_collection.json`](./NicaMarket-JWT.postman_collection.json) incluido en la raíz del proyecto contiene todas las pruebas organizadas y listas para importar en Postman:

| # | Solicitud | Token utilizado | Código HTTP | Explicación |
|---|---|---|---|---|
| **1** | `GET /api/categorias` | Sin token | `401 Unauthorized` | La ruta está protegida; al no presentar un token Bearer válido, Spring Security rechaza la petición antes de llegar al controlador. |
| **2** | `GET /api/categorias` | Token `USER` | `200 OK` | El token es válido y el rol `ROLE_USER` tiene autorización de consulta (`HttpMethod.GET`) sobre `/api/categorias/**`. |
| **3** | `POST /api/categorias` | Token `USER` | `403 Forbidden` | El usuario está autenticado, pero su rol `ROLE_USER` no tiene privilegios de administrador (`ROLE_ADMIN`) para crear categorías. |
| **4** | `POST /api/categorias` | Token `ADMIN` | `201 Created` | El token de `admin` posee `ROLE_ADMIN`, permitiendo registrar la nueva categoría exitosamente. |
| **5** | `PUT /api/categorias/{id}` | Token `ADMIN` | `200 OK` | El rol `ROLE_ADMIN` está autorizado para actualizar (`PUT`) y eliminar (`DELETE`) categorías existentes. |

---

## 4. Diferencia entre Autenticación y Autorización y Preguntas de Cierre

- **Autenticación vs. Autorización:**
  - **Autenticación (*Authentication*):** Proceso de verificar la identidad del usuario (comprobar credenciales en `/api/auth/login` o validar la firma y vigencia del JWT). Su fallo devuelve `401 Unauthorized`.
  - **Autorización (*Authorization*):** Proceso de verificar si el usuario ya autenticado cuenta con los roles o permisos necesarios para acceder a un recurso o ejecutar una acción. Su fallo devuelve `403 Forbidden`.

1. **¿En qué se diferencian HTTP 401 y 403?**
   - `401 Unauthorized` significa que la petición carece de credenciales de autenticación válidas (sin token, token expirado o firma inválida). `403 Forbidden` significa que el usuario sí se autenticó con un token válido, pero su rol no tiene permisos para realizar la operación solicitada.
2. **¿Por qué no basta con leer el contenido de un JWT para confiar en él?**
   - Porque el encabezado y el *payload* de un JWT solo están codificados en Base64Url y cualquiera puede leerlos o alterarlos. Solo al verificar criptográficamente la **firma** con la clave secreta se garantiza que el token fue emitido por el servidor y no fue modificado.
3. **¿Qué función cumple `JwtDecoder`?**
   - Valida criptográficamente la firma HMAC-SHA256 del token entrante, verifica que no haya expirado (`exp`) y convierte el contenido del token en un objeto `Jwt` confiable para el contexto de seguridad.
4. **¿Qué riesgos ocasionaría publicar la clave utilizada para firmar tokens?**
   - Cualquier tercero podría generar y firmar tokens JWT falsos con rol `ADMIN` o suplantar a cualquier usuario sin pasar por el inicio de sesión, comprometiendo por completo la seguridad de la API.
