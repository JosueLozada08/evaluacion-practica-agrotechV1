package com.agrotech.routers;

import org.apache.camel.builder.RouteBuilder;

import com.agrotech.processors.CsvToLecturasProcessor;

public class IntegrationRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Manejo simple de errores
        onException(Exception.class)
            .log("💥 Error procesando: ${exception.message}")
            .handled(true);

        // ========== 1) FILE TRANSFER ==========
        from("file:{{app.input.dir}}"
                + "?include=sensores\\.csv"
                + "&preMove=.inprogress/${file:name}"
                + "&readLock=changed&readLockMinAge=1000&readLockTimeout=10000"
                + "&move={{app.archived.dir}}/${date:now:yyyyMMdd_HHmmss}_${file:name}")
            .routeId("file-transfer")
            .log("[FILE] Recibido ${header.CamelFileName}")
            .process(new CsvToLecturasProcessor())     // -> List<Lectura>
            .to("direct:ingesta");

        // ========== 2) SHARED DATABASE (camel-sql con MERGE idempotente) ==========
        from("direct:ingesta")
            .routeId("shared-db")
            .split(body()) // body: List<Lectura> -> Lectura
                .setHeader("id_sensor",   simple("${body.id_sensor}"))
                .setHeader("fecha",       simple("${body.fecha}"))
                .setHeader("humedad",     simple("${body.humedad}"))
                .setHeader("temperatura", simple("${body.temperatura}"))
                .to("sql:MERGE INTO lecturas (id_sensor, fecha, humedad, temperatura) "
                    + "KEY(id_sensor, fecha) "
                    + "VALUES (:#id_sensor, :#fecha, :#humedad, :#temperatura)"
                    + "?dataSource=#dataSource")
                .log("[DB] Upsert ${headers.id_sensor}/${headers.fecha}")
            .end()
            .log("[DB] Ingesta completada.");

        // ========== 3) RPC SIMULADO ==========
        // Cliente
        from("direct:solicitarLectura")
            .routeId("rpc-client")
            .setHeader("id_sensor", simple("${body}"))
            .to("direct:rpc.obtenerUltimo?timeout=2000")
            .log("[CLIENTE] Respuesta: ${body}");

        // Servidor
        from("direct:rpc.obtenerUltimo")
            .routeId("rpc-server")
            .log("[SERVIDOR] Solicitud para sensor ${header.id_sensor}")
            .bean("analiticaService", "getUltimoValor(${header.id_sensor})");

        // Disparador de ejemplo (una vez a los 3s) para ver el RPC
        from("timer:rpcOnce?repeatCount=1&delay=3000")
            .setBody(constant("S002"))
            .to("direct:solicitarLectura");
    }
}
