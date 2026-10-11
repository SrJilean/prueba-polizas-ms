package com.seguros.polizas.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class DatabaseResetController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/reset-db")
    @Transactional
    public ResponseEntity<String> reiniciarBaseDeDatos() {
        log.info("Iniciando reseteo de la base de datos de pruebas...");

        // Usamos Text Blocks (disponible desde Java 15+) para meter todo el SQL
        String sql = """
                -- 1. LIMPIEZA DE TABLAS (Orden estricto por llaves foráneas)
                DELETE FROM historico_movimientos;
                DELETE FROM siniestros;
                DELETE FROM riesgos;
                DELETE FROM polizas;
                DELETE FROM actores;

                -- 2. REINICIO DE SECUENCIAS (Para que los IDs vuelvan a empezar en 1)
                ALTER SEQUENCE actores_id_seq RESTART WITH 1;
                ALTER SEQUENCE polizas_id_seq RESTART WITH 1;
                ALTER SEQUENCE riesgos_id_seq RESTART WITH 1;
                ALTER SEQUENCE historico_movimientos_id_seq RESTART WITH 1;

                -- 3. INSERCIÓN DE ACTORES MASIVA
                INSERT INTO actores (tipo_documento, numero_documento, nombre_razon_social, rol) 
                VALUES 
                ('CC', '1090000001', 'Carlos Ruiz (Inquilino)', 'ARRENDATARIO'),
                ('CC', '1090000002', 'Laura Martínez (Inquilino)', 'ARRENDATARIO'),
                ('CC', '1090000003', 'Andrés Felipe Gómez (Inquilino)', 'ARRENDATARIO'),
                ('CC', '1090000004', 'María José Silva (Inquilino)', 'ARRENDATARIO'),
                ('CE', '500000001', 'John Doe (Inquilino Extranjero)', 'ARRENDATARIO'),
                ('CC', '1090000005', 'Diana Patricia Castro', 'ARRENDATARIO'),
                ('CC', '1090000006', 'Jorge Enrique Abello', 'ARRENDATARIO'),
                ('CC', '1090000007', 'Ana María Orozco', 'ARRENDATARIO'),
                ('CC', '1090000008', 'Luis Fernando Montoya', 'ARRENDATARIO'),
                ('CE', '500000002', 'Sarah Connor (Extranjera)', 'ARRENDATARIO'),
                ('CC', '1090000009', 'Camila Vargas', 'ARRENDATARIO'),
                ('CC', '1090000010', 'Santiago Pérez', 'ARRENDATARIO'),
                ('NIT', '901000111-5', 'Tech Solutions S.A.S (Inquilino Corporativo)', 'ARRENDATARIO'),
                ('NIT', '901000222-6', 'Logística Avanzada LTDA (Inquilino Comercial)', 'ARRENDATARIO'),
                ('CC', '1090000011', 'Valeria Franco', 'ARRENDATARIO'),
                ('CC', '60000001', 'Don Ramón (Propietario)', 'ARRENDADOR'),
                ('CC', '60000002', 'Doña Florinda (Propietaria)', 'ARRENDADOR'),
                ('CC', '60000003', 'Pedro Pérez (Propietario)', 'ARRENDADOR'),
                ('CC', '60000004', 'Ana Lucía Torres (Propietaria)', 'ARRENDADOR'),
                ('CC', '60000005', 'Roberto Carlos Jaramillo', 'ARRENDADOR'),
                ('CC', '60000006', 'Marta Lucía Ramírez', 'ARRENDADOR'),
                ('CC', '60000007', 'Hernán Darío Gómez', 'ARRENDADOR'),
                ('CC', '60000008', 'Gloria Estefan (Propietaria)', 'ARRENDADOR'),
                ('NIT', '800123456-7', 'Inversiones Inmobiliarias SURA', 'ARRENDADOR'),
                ('NIT', '800987654-3', 'Fondo de Pensiones y Cesantías (Propietario)', 'ARRENDADOR'),
                ('NIT', '900888777-1', 'Bienes Raíces Norte S.A.S', 'GESTOR_INMOBILIARIO'),
                ('NIT', '901222333-2', 'Inmobiliaria Central', 'GESTOR_INMOBILIARIO'),
                ('NIT', '900555444-3', 'Administraciones El Bosque', 'GESTOR_INMOBILIARIO'),
                ('NIT', '901999888-4', 'Century 21 Colombia', 'GESTOR_INMOBILIARIO'),
                ('NIT', '900111222-5', 'RE/MAX Properties', 'GESTOR_INMOBILIARIO');

                -- 4. INSERCIÓN DE PÓLIZAS
                INSERT INTO polizas (numero_poliza, tipo_poliza, estado, fecha_inicio, vigencia_meses, valor_canon_total, valor_prima_total, tomador_id) 
                VALUES 
                ('POL-IND-001', 'INDIVIDUAL', 'ACTIVA', '2026-10-01', 12, 1200000.00, 14400000.00, 1),
                ('POL-IND-002', 'INDIVIDUAL', 'ACTIVA', '2026-10-05', 12, 1500000.00, 18000000.00, 2),
                ('POL-IND-003', 'INDIVIDUAL', 'ACTIVA', '2026-09-15', 12, 5000000.00, 60000000.00, 13),
                ('POL-IND-004', 'INDIVIDUAL', 'ACTIVA', '2026-08-20', 12, 8000000.00, 96000000.00, 14),
                ('POL-IND-005', 'INDIVIDUAL', 'ACTIVA', '2026-10-10', 12, 2000000.00, 24000000.00, 5),
                ('POL-COL-101', 'COLECTIVA', 'ACTIVA', '2026-01-01', 12, 4500000.00, 54000000.00, 26),
                ('POL-COL-102', 'COLECTIVA', 'ACTIVA', '2026-05-15', 12, 6000000.00, 72000000.00, 27),
                ('POL-COL-103', 'COLECTIVA', 'ACTIVA', '2026-07-10', 12, 9000000.00, 108000000.00, 28);

                -- 5. INSERCIÓN DE RIESGOS
                INSERT INTO riesgos (poliza_id, direccion, canon_individual, prima_individual, valor_asegurado, estado_riesgo, asegurado_id, beneficiario_id)
                VALUES 
                (1, 'Calle 10 # 5-20, Apto 101', 1200000.00, 14400000.00, 43200000.00, 'ACTIVO', 1, 16),
                (2, 'Carrera 15 # 22-10, Casa 5', 1500000.00, 18000000.00, 54000000.00, 'ACTIVO', 2, 17),
                (3, 'Av. Siempre Viva 123, Bodega A', 5000000.00, 60000000.00, 180000000.00, 'ACTIVO', 13, 24),
                (4, 'Centro Logístico Sur, Bodega 4', 8000000.00, 96000000.00, 288000000.00, 'ACTIVO', 14, 25),
                (5, 'Edificio El Mirador, Penthouse', 2000000.00, 24000000.00, 72000000.00, 'ACTIVO', 5, 18),
                (6, 'Edificio Norte Apto 201', 1000000.00, 12000000.00, 36000000.00, 'ACTIVO', 6, 19),
                (6, 'Edificio Norte Apto 202', 1500000.00, 18000000.00, 54000000.00, 'ACTIVO', 7, 19),
                (6, 'Edificio Norte Apto 203', 2000000.00, 24000000.00, 72000000.00, 'ACTIVO', 8, 19),
                (7, 'Centro Comercial Plaza, Local 1', 3000000.00, 36000000.00, 108000000.00, 'ACTIVO', 9, 20),
                (7, 'Centro Comercial Plaza, Local 2', 3000000.00, 36000000.00, 108000000.00, 'ACTIVO', 10, 20),
                (8, 'Conjunto El Bosque, Casa 1', 2000000.00, 24000000.00, 72000000.00, 'ACTIVO', 11, 21),
                (8, 'Conjunto El Bosque, Casa 2', 3000000.00, 36000000.00, 108000000.00, 'ACTIVO', 12, 22),
                (8, 'Conjunto El Bosque, Casa 3', 4000000.00, 48000000.00, 144000000.00, 'ACTIVO', 15, 23);
                """;

        try {
            jdbcTemplate.execute(sql);
            return ResponseEntity.ok("Ambiente de pruebas reiniciado exitosamente. Tablas limpiadas y datos base insertados.");
        } catch (Exception e) {
            log.error("Error al reiniciar la base de datos: ", e);
            return ResponseEntity.internalServerError().body("Error al reiniciar: " + e.getMessage());
        }
    }
}