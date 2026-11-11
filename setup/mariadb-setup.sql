-- Usuario y privilegios que la capa de persistencia necesita: la aplicacion
-- abre la conexion como 'progra_c' contra localhost:3306 y crea la base Grupo_7.
--
-- Aplicar como administrador del motor, desde la raiz del repositorio:
--   sudo mariadb < setup/mariadb-setup.sql
-- En una instalacion con MySQL el cliente se llama 'mysql':
--   sudo mysql < setup/mariadb-setup.sql

CREATE USER IF NOT EXISTS 'progra_c'@'localhost' IDENTIFIED BY 'progra_c';
CREATE USER IF NOT EXISTS 'progra_c'@'127.0.0.1' IDENTIFIED BY 'progra_c';

-- BaseDeDatos ejecuta CREATE DATABASE IF NOT EXISTS Grupo_7.
GRANT CREATE ON *.* TO 'progra_c'@'localhost';
GRANT CREATE ON *.* TO 'progra_c'@'127.0.0.1';

-- iniciarBD hace DROP TABLE, CREATE TABLE e INSERT sobre Grupo_7.Asociados.
GRANT ALL PRIVILEGES ON Grupo_7.* TO 'progra_c'@'localhost';
GRANT ALL PRIVILEGES ON Grupo_7.* TO 'progra_c'@'127.0.0.1';

FLUSH PRIVILEGES;
