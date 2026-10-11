# Plataforma de Pólizas - Core Inmobiliario

API REST desarrollada en Spring Boot para la gestión integral del ciclo de vida de pólizas de arrendamiento (Individuales y Colectivas). Incluye la administración de riesgos, recálculos automáticos de primas, trazabilidad financiera (histórico de movimientos) e integración asíncrona con sistemas Core legados.

## 🚀 Despliegue y Entorno de Producción

La aplicación está preparada para operar tanto en un entorno local de desarrollo como en producción (ej. AWS Elastic Beanstalk, EC2, ECS) detrás de un **API Gateway**.

### Variables de Entorno
Para el correcto funcionamiento, configura las siguientes variables en tu entorno:

| Variable | Descripción | Ejemplo | 
| ----- | ----- | ----- | 
| `DB_NAME` | Nombre de la base de datos de AWS RDS (PostgreSQL) | `polizasdb` | 
| `DB_HOST` | URL de conexión a AWS RDS (PostgreSQL) | `polizas-db.c81uywke6p42.us-east-1.rds.amazonaws.com` | 
| `DB_USER` | Usuario de la base de datos | `adminpolizas` | 
| `DB_PASSWORD` | Contraseña de la base de datos | `PruebaTecnica2026!` | 
| `API_SECURITY_KEY` | Llave de seguridad de la aplicación | `123456` | 

---

## 📖 Documentación de Endpoints y Pruebas (cURLs)

> **Importante:** Todas las peticiones requieren el header de autenticación `api-key`. Asegúrate de incluirlo siempre. En los ejemplos usamos `http://localhost:8080`, cámbialo por tu URL de producción cuando aplique.

### 🏢 1. Gestión de Pólizas (`poliza-controller`)

**1.1 Listar todas las pólizas (con filtros opcionales)**
Filtra por `tipo` (INDIVIDUAL/COLECTIVA) o `estado` (ACTIVA/CANCELADA/RENOVADA).
```bash
curl -X GET "http://localhost:8080/api/v1/polizas?tipo=COLECTIVA&estado=ACTIVA" \
     -H "api-key: 123456"
```

**1.2 Agregar Riesgo a Póliza**
Solo permitido para pólizas COLECTIVAS. Calcula meses restantes y ajusta la prima.
```bash
curl -X POST "http://localhost:8080/api/v1/polizas/6/riesgos" \
     -H "api-key: 123456" \
     -H "Content-Type: application/json" \
     -d '{
           "direccion": "Nueva Oficina 404",
           "canonIndividual": 2500000.00,
           "aseguradoId": 3,
           "beneficiarioId": 24
         }'
```

**1.3 Listar Riesgos de una Póliza**
```bash
curl -X GET "http://localhost:8080/api/v1/polizas/6/riesgos" \
     -H "api-key: 123456"
```

**1.4 Renovar Póliza**
Aplica el IPC del año actual, clona los riesgos y crea una nueva póliza ACTIVA.
```bash
curl -X POST "http://localhost:8080/api/v1/polizas/1/renovar" \
     -H "api-key: 123456"
```

**1.5 Cancelar Póliza**
Cancela la póliza y, en cascada, cancela todos sus riesgos asociados.
```bash
curl -X POST "http://localhost:8080/api/v1/polizas/8/cancelar" \
     -H "api-key: 123456"
```

---

### 🏠 2. Gestión de Riesgos (`riesgo-controller`)

**2.1 Cancelar un Riesgo Específico**
Excluye el riesgo, resta sus valores del total de la póliza y registra el movimiento en el histórico.
```bash
curl -X POST "http://localhost:8080/api/v1/riesgos/12/cancelar" \
     -H "api-key: 123456"
```

---

### 👥 3. Gestión de Actores (`actor-controller`)

**3.1 Listar Actores**
Lista inquilinos, propietarios y gestores. Útil para llenar selectores en el Frontend.
```bash
curl -X GET "http://localhost:8080/api/v1/actores" \
     -H "api-key: 123456"
```

---

### 📈 4. Gestión de IPC (`Gestión de IPC`)

**4.1 Listar todo el histórico de IPC**
```bash
curl -X GET "http://localhost:8080/api/ipc" \
     -H "api-key: 123456"
```

**4.2 Registrar nuevo IPC**
```bash
curl -X POST "http://localhost:8080/api/ipc" \
     -H "api-key: 123456" \
     -H "Content-Type: application/json" \
     -d '{
           "periodo": 2026,
           "valor": 10.50
         }'
```

**4.3 Obtener el último IPC registrado para un año**
```bash
curl -X GET "http://localhost:8080/api/ipc/2026/ultimo" \
     -H "api-key: 123456"
```

---

### 🛠️ 5. Testing y Core Mock

**5.1 Reset de Base de Datos (`database-reset-controller`)**
*⚠️ SOLO PARA AMBIENTES DE DESARROLLO/PRUEBAS.* Limpia todas las tablas, reinicia secuencias y carga la data semilla.
```bash
curl -X POST "http://localhost:8080/api/v1/test/reset-db" \
     -H "api-key: 123456"
```

**5.2 Mock del Core Legado (`core-mock-controller`)**
Endpoint interno simulado que recibe notificaciones asíncronas de actualizaciones de pólizas.
```bash
curl -X POST "http://localhost:8080/core-mock/evento" \
     -H "api-key: 123456" \
     -H "Content-Type: application/json" \
     -d '{"evento": "ACTUALIZACION", "polizaId": 1}'
```