# SmartLogix · Order-Service

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)

Microservicio de **gestión de pedidos** de SmartLogix. Permite registrar, consultar y administrar pedidos y su estado, coordinando con Inventory-Service la reserva/liberación de stock mediante una **saga** y aplicando un **circuit breaker** (Resilience4j) sobre las dependencias externas.

## Características

- CRUD de pedidos y cambio de estado (catálogo: 1 Pendiente, 2 Confirmado, 3 Procesando, 4 Completado, 5 Cancelado).
- Patrones **Saga** (`PedidoSaga`), **Facade** (`PedidoFacade`) y **Observer** (`OrdenObserver`).
- **Circuit Breaker** con Resilience4j (instancia `pedidos`); un 404 de negocio no abre el circuito.
- Persistencia en **Oracle Autonomous Database** (schema `SL_PEDIDOS`).
- `GlobalExceptionHandler` que traduce excepciones a HTTP (404 inexistente, 503 circuito abierto).
- Documentación OpenAPI/Swagger.

> El JWT lo valida el **Api-Gateway**; este servicio confía en el tráfico que llega a través del gateway.

## Requisitos previos

- **Java 21** (JDK).
- **Oracle Autonomous Database** accesible + wallet (`TNS_ADMIN`). En tests se usa H2 en memoria.
- Maven Wrapper incluido (`mvnw` / `mvnw.cmd`).

## Configuración

La configuración está en [src/main/resources/application.yml](src/main/resources/application.yml). Se sobreescribe por variables de entorno:

| Variable | Por defecto | Descripción |
|----------|-------------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:oracle:thin:@yunjwge5ttypxb6i_medium?TNS_ADMIN=...` | URL JDBC de Oracle |
| `SPRING_DATASOURCE_USERNAME` | `sl_pedidos` | Usuario dueño del schema |
| `SPRING_DATASOURCE_PASSWORD` | *(sin valor por defecto)* | Contraseña del schema. **No se versiona**; se inyecta desde el `.env` raíz vía Docker Compose |

> `ddl-auto: none` — el schema lo gestiona la base de datos (scripts SQL); Hibernate no toca el DDL.

## Instalación

```bash
./mvnw clean package -DskipTests      # Linux/Mac
.\mvnw.cmd clean package -DskipTests  # Windows (PowerShell)
```

## Ejecución

```bash
./mvnw spring-boot:run        # Linux/Mac
.\mvnw.cmd spring-boot:run    # Windows (PowerShell)
```

El servicio queda disponible en **http://localhost:8082**.

- Swagger UI: http://localhost:8082/swagger-ui.html
- Health check: http://localhost:8082/actuator/health

### Docker (recomendado: vía Docker Compose)

La forma recomendada de ejecutarlo con sus credenciales es el **Docker Compose del monorepo**, que las toma del `.env` raíz (no versionado, fuera del repositorio):

```bash
# Desde la raíz del monorepo SmartLogix (junto a Docker-compose.yml y .env)
docker compose up ms-pedidos     # solo este servicio
docker compose up                # toda la plataforma
```

El `.env` debe definir `PED_DATASOURCE_PASSWORD` (y el resto de credenciales); el `Docker-compose.yml` aborta con un mensaje claro si falta. El wallet se monta desde `ORACLE_WALLET_PATH`.

> Ninguna contraseña está escrita en el código ni en este README: deben proveerse vía `.env` / variables de entorno.

## Endpoints principales

> En despliegue real se accede a través del Api-Gateway con el prefijo `/api` (p. ej. `GET /api/pedidos`).

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/pedidos` | Listar pedidos |
| GET | `/pedidos/{id}` | Obtener pedido por id |
| GET | `/pedidos/{id}/estado` | Estado actual del pedido |
| POST | `/pedidos` | Crear pedido (estado inicial: Pendiente) |
| PATCH | `/pedidos/{id}/estado?estadoId=N` | Cambiar el estado del pedido |
| DELETE | `/pedidos/{id}` | Eliminar pedido |

## Pruebas

No hay Maven global; usa el wrapper. En Windows, `mvnw` necesita `JAVA_HOME`:

```powershell
# PowerShell (Windows)
$env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent (Get-Command java).Source)
.\mvnw.cmd test
```

```bash
# Linux/Mac
./mvnw test
```

Las 31 pruebas (JUnit 5 + Mockito) cubren `PedidoService`, `PedidoController`, `PedidoFacade`, `OrdenObserver` y `PedidoSaga`. El arranque de contexto usa el perfil `test` con H2 en memoria.

## Estructura

```
src/main/java/com/smartlogix/pedidos/
├── PedidosApplication.java
├── config/        # OpenApiConfig
├── controller/    # PedidoController
├── dto/           # PedidoDTO, CrearPedidoRequest, etc.
├── exception/     # GlobalExceptionHandler, NotFound, ServiceUnavailable
├── facade/        # PedidoFacade
├── model/         # Pedido, Estado, EstadoPedidoActual
├── repository/    # Repositorios JPA
├── saga/          # PedidoSaga + PedidoActualizadoEvent
└── service/       # PedidoService, OrdenObserver
```
