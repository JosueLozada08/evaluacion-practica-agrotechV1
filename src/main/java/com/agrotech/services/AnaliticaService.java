package com.agrotech.services;


import javax.sql.DataSource;

public class AnaliticaService {
    private final DataSource ds;
    public AnaliticaService(DataSource ds) { this.ds = ds; }

    /** Retorna el último registro por fecha para un sensor en JSON simple */
    public String getUltimoValor(String id) throws Exception {
        String out = "{}";
        try (var c = ds.getConnection();
             var ps = c.prepareStatement("""
                 SELECT id_sensor, fecha, humedad, temperatura
                 FROM lecturas
                 WHERE id_sensor = ?
                 ORDER BY fecha DESC
                 LIMIT 1
             """)) {
            ps.setString(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    out = String.format(
                        "{\"id\":\"%s\",\"humedad\":%.2f,\"temperatura\":%.2f,\"fecha\":\"%s\"}",
                        rs.getString("id_sensor"),
                        rs.getDouble("humedad"),
                        rs.getDouble("temperatura"),
                        rs.getString("fecha")
                    );
                }
            }
        }
        return out;
    }
}
