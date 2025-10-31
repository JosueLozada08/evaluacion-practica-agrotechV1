# Evaluación Práctica – Integración con Apache Camel

## 🧩 Descripción del Proyecto

Este proyecto implementa **tres patrones clásicos de integración empresarial (EIP)** usando **Apache Camel** y **Java 17**.  
El objetivo es simular la comunicación entre tres sistemas: **SensData**, **AgroAnalyzer** y **FieldControl**, aplicando los patrones **File Transfer**, **Shared Database** y **RPC (Remote Procedure Call)**.

---

## ⚙️ Patrones Implementados

### 1️⃣ File Transfer Pattern (SensData → AgroAnalyzer)
- Detecta automáticamente el archivo `sensores.csv` en el directorio raíz.  
- Convierte su contenido a JSON.  
- Mueve el archivo procesado a la carpeta `logs/archived/` con timestamp.

### 2️⃣ Shared Database Pattern (AgroAnalyzer ↔ FieldControl)
- Inserta o actualiza lecturas en la base de datos **H2** (archivo `database/agrotech.db`).  
- Se utiliza el componente `camel-sql` para ejecutar un **MERGE** (idempotente).  
- Los datos se pueden verificar en la consola H2.

### 3️⃣ RPC Simulado (FieldControl ↔ AgroAnalyzer)
- Simula una comunicación síncrona: el cliente solicita la última lectura de un sensor específico y el servidor responde.  
- Implementado con endpoints internos `direct:solicitarLectura` y `direct:rpc.obtenerUltimo`.

---

## 📁 Estructura del Proyecto

```
evaluacion-practica-agrotech/
├── src/
│   └── main/java/com/agrotech/
│       ├── App.java
│       ├── config/DataSourceConfig.java
│       ├── model/Lectura.java
│       ├── processors/CsvToLecturasProcessor.java
│       ├── routes/IntegrationRoutes.java
│       └── service/AnaliticaService.java
├── src/main/resources/application.properties
├── database/
├── logs/
├── sensores.csv
├── pom.xml
└── README.md
```

---

## 🚀 Ejecución del Proyecto

### 🧰 Requisitos
- **Java 17 o superior**
- **Apache Maven 3.9+**
- **H2 Database (in-memory o embebida)**

### ▶️ Comandos
```bash
mvn clean package
mvn exec:java
```

### ⚙️ Parámetros configurables
En `application.properties`:
```properties
app.input.dir=./
app.archived.dir=./logs/archived
```

---

## 🧠 Validación

1. Coloca `sensores.csv` en la raíz del proyecto.  
2. Ejecuta el programa (`mvn exec:java`).  
3. Verifica los logs:
   ```
   [FILE] Recibido sensores.csv
   [DB] Upsert S001/2025-05-22
   [SERVIDOR] Solicitud para sensor S002
   [CLIENTE] Respuesta: {"id":"S002","humedad":50.00,"temperatura":25.10,"fecha":"2025-05-22"}
   ```
4. Revisa la consola H2:
   ```sql
   SELECT * FROM lecturas ORDER BY fecha DESC, id_sensor;
   ```

---

## 🧾 Evidencias Sugeridas para el Informe

- 📂 Archivo archivado en `logs/archived/YYYYMMDD_HHmmss_sensores.csv`
- 🧮 Consulta en H2 mostrando lecturas
- 💬 Log del RPC `[CLIENTE] Respuesta: {...}`
- 🗂 Estructura del proyecto
- 🧠 Resumen teórico (patrones aplicados y riesgos)

---

## 📘 Reflexión (resumen)

| Patrón | Función | Ventajas | Riesgos |
|--------|----------|-----------|----------|
| **File Transfer** | Intercambio asincrónico de archivos CSV. | Sencillo, confiable. | No en tiempo real. |
| **Shared Database** | Comunicación vía BD común. | Evita APIs. | Riesgo de acoplamiento y bloqueos. |
| **RPC Simulado** | Comunicación síncrona directa. | Eficiente para consultas simples. | Alto acoplamiento. |

---

## 👨‍💻 Autor

**Josue Lozada**  
Ingeniería de Software – Universidad de Las Américas  
**Fecha:** 30-Oct-2025

