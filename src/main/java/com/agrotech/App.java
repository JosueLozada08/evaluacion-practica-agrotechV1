package com.agrotech;

import javax.sql.DataSource;

import org.apache.camel.main.Main;

import com.agrotech.config.DataSourceConfig;
import com.agrotech.routers.IntegrationRoutes;
import com.agrotech.services.AnaliticaService;

public class App {
    public static void main(String[] args) throws Exception {
        // Crear DataSource (H2) e inicializar esquema
        DataSource ds = DataSourceConfig.createAndInit();

        // Levantar Camel Main y registrar beans
        Main main = new Main();
        main.bind("dataSource", ds);
        main.bind("analiticaService", new AnaliticaService(ds));
        main.configure().addRoutesBuilder(new IntegrationRoutes());
        main.run(args);
    }
}
