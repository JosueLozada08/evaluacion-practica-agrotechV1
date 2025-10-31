# Evaluación Práctica – Integración con Apache Camel

## Descripción del Proyecto

Este proyecto forma parte de la evaluación práctica del curso de **Integración de Sistemas**.  
El objetivo principal es implementar tres **patrones clásicos de integración empresarial (Enterprise Integration Patterns - EIP)** utilizando **Apache Camel** y **Java 17**.

El sistema simula la comunicación entre tres módulos principales:

- **SensData**: simula los sensores de humedad y temperatura que generan archivos CSV.  
- **AgroAnalyzer**: recibe los archivos, procesa su contenido y almacena los datos en una base de datos compartida.  
- **FieldControl**: consulta las lecturas registradas y solicita información en tiempo real mediante una llamada simulada (RPC).

Con esta implementación se busca demostrar el flujo completo de integración entre sistemas independientes mediante distintos mecanismos de comunicación.

---

## Patrones Implementados

### 1. File Transfer Pattern (SensData → AgroAnalyzer)
- Este patrón permite el intercambio de archivos entre sistemas.  
- Apache Camel detecta automáticamente el archivo `sensores.csv` ubicado en el directorio raíz del proyecto.  
- El archivo es leído, transformado y posteriormente movido a la carpeta `logs/archived/` con un sello de tiempo.  
- Representa el envío asincrónico de datos entre sistemas, simulando una integración tipo FTP pero de manera local.

### 2. Shared Database Pattern (AgroAnalyzer ↔ FieldControl)
- Los datos procesados se almacenan en una base de datos **H2** ubicada en `database/agrotech.db`.  
- Esta base actúa como punto común de intercambio entre los módulos.  
- Apache Camel utiliza el componente **camel-jdbc** para realizar inserciones o actualizaciones de manera idempotente.  
- Los datos pueden ser validados ejecutando consultas SQL sobre la tabla `lecturas`.

### 3. RPC Simulado (FieldControl ↔ AgroAnalyzer)
- Representa la comunicación síncrona entre dos sistemas.  
- Se implementó mediante endpoints internos de Camel (`direct:solicitarLectura` y `direct:rpc.obtenerUltimo`).  
- Permite que un cliente solicite la última lectura de un sensor y reciba la respuesta de forma inmediata.  
- Emula el comportamiento de una llamada directa entre servicios, similar a un servicio REST.

---

## Estructura del Proyecto

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

## Ejecución del Proyecto

### Requisitos Previos
- Java 17 o superior instalado en el sistema.  
- Apache Maven 3.9 o posterior.  
- Acceso a una terminal de comandos (CMD, PowerShell o terminal Linux).

### Pasos para la Ejecución

1. Clonar o descargar el proyecto desde el repositorio.  
2. Abrir una terminal dentro del directorio del proyecto.  
3. Ejecutar el siguiente comando para compilar el código fuente:
   ```bash
   mvn clean package
   ```
4. Una vez compilado correctamente, ejecutar el siguiente comando para iniciar la aplicación:
   ```bash
   mvn exec:java
   ```
5. El sistema quedará en ejecución, monitoreando el directorio raíz en espera del archivo `sensores.csv`.

6. Colocar un archivo `sensores.csv` en el directorio raíz con el siguiente formato:
   ```csv
   id_sensor,fecha,humedad,temperatura
   S001,2025-05-22,45,26.4
   S002,2025-05-22,50,25.1
   S003,2025-05-22,47,27.3
   ```

7. Al detectarse el archivo, Camel procesará las lecturas, las guardará en la base de datos H2 y moverá el archivo procesado a `logs/archived/`.

8. Para validar los resultados, ejecutar en la consola H2 el siguiente comando SQL:
   ```sql
   SELECT * FROM lecturas ORDER BY fecha DESC, id_sensor;
   ```

---

## Evidencias Esperadas

- Archivo `sensores.csv` procesado y movido a la carpeta `logs/archived/`.  
- Base de datos `agrotech.db` actualizada con las lecturas procesadas.  
- Mensajes de log indicando el flujo completo: detección, inserción en base y solicitud RPC.  
- Respuesta JSON del RPC con los datos de la última lectura solicitada.

---

## Reflexión Técnica

| Patrón | Descripción | Ventajas | Riesgos |
|--------|--------------|-----------|----------|
| File Transfer | Intercambio de archivos entre sistemas de forma asincrónica. | Simplicidad y confiabilidad. | No opera en tiempo real. |
| Shared Database | Comunicación mediante una base de datos común. | Persistencia de datos y fácil consulta. | Acoplamiento y posibles conflictos de acceso. |
| RPC Simulado | Comunicación directa y síncrona entre módulos. | Flujo coordinado y en tiempo real. | Alta dependencia entre sistemas. |

---

## Autor

**Josue Lozada**  
Estudiante de Ingeniería de Software – Universidad de Las Américas  
**Fecha:** 30 de octubre de 2025

