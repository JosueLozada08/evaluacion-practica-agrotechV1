package com.agrotech.processors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.agrotech.model.Lectura;

public class CsvToLecturasProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String filePath = exchange.getIn().getHeader("CamelFilePath", String.class);
        List<String> lines = Files.readAllLines(Path.of(filePath));

        List<Lectura> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) { // saltar cabecera
            String line = lines.get(i).trim();
            if (line.isBlank()) continue;
            String[] a = line.split(",");
            // CSV esperado: id_sensor,fecha,humedad,temperatura
            out.add(new Lectura(
                a[0],
                a[1],
                Double.parseDouble(a[2]),
                Double.parseDouble(a[3])
            ));
        }
        exchange.getIn().setBody(out);
    }
}
