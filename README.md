# Trabajo Práctico Final — Estrategia de Testing
### Aplicación Clínica

Trabajo Práctico Final de la asignatura **Calidad de Software** (UNMdP — Facultad de Ingeniería), sobre la aplicación **Clínica** provista por la cátedra.

**Autores:** Lucas Cardozo · Agustín Zalazar

## Qué es

El trabajo consiste en construir una estrategia de testing para la aplicación Clínica y demostrarla con tres consignas:

1. **Testing unitario aislado** de `AsociadoDTO`: asignación, recuperación y validaciones de integridad de los setters, sin levantar ningún servicio externo.
2. **Testing de integración** contra un motor real: `AsociadoDTO`, `DataAccessObject` y `BaseDeDatos`, verificando el patrón Singleton de la conexión, la creación automática del esquema y de los datos semilla, y el CRUD parametrizado.
3. **Testing con mocks y concurrencia** sobre `Ambulancia`, la interfaz `State` y la rutina `RetornoAutomatico`: dobles de prueba sobre la interfaz de estados y verificación del flujo completo de la máquina de estados con hilos independientes y bloques sincronizados.

Las consignas 1 y 2 exigen **detectar** los fallos del código provisto; solo la consigna 2 autoriza corregirlo. La consigna 3 pide demostrar que la suite **descubre** los problemas de sincronización y de estado, sin corregirlos: por eso sus casos quedan en rojo.

## Requisitos

| Herramienta | Versión |
| --- | --- |
| Java | 8 |
| Maven | 3 |
| MySQL / MariaDB | MySQL 8 o MariaDB local (las corridas de la entrega usaron MariaDB 13.0.2) |

El motor de base de datos tiene que estar levantado antes de correr la suite de integración.

## Base de datos

La conexión está configurada en el código con estos valores:

| Dato | Valor |
| --- | --- |
| Host y puerto | `localhost:3306` |
| Usuario | `progra_c` |
| Contraseña | `progra_c` |
| Base | `Grupo_7` |

La base `Grupo_7` no hay que crearla a mano: la aplicación la crea al inicializar, y por eso el usuario necesita permiso `CREATE`.

El script que crea el usuario y le asigna los permisos está en `setup/mariadb-setup.sql`. Se aplica como administrador del motor, desde la raíz del repositorio:

```bash
sudo mariadb < setup/mariadb-setup.sql
```

En una instalación con MySQL, el cliente se llama `mysql` en lugar de `mariadb`:

```bash
sudo mysql < setup/mariadb-setup.sql
```

La suite de integración reinicializa el esquema de `Grupo_7` en cada corrida (tabla `Asociados` y datos semilla), así que tampoco hace falta preparar la base a mano.

## Cómo correr las pruebas

Los comandos se ejecutan desde la raíz del repositorio.

**Consignas 1 y 2 (unitario e integración), en verde:**

```bash
mvn -f TPE2026/pom.xml test -Dtest=AsociadoDTOTest,BaseDeDatosTest,DataAccessObjectTest
```

Son **57 casos** y el build termina en `BUILD SUCCESS`.

**Suite completa:**

```bash
mvn -f TPE2026/pom.xml test
```

Son **73 casos**: **69 en verde** y **4 en rojo**, y el build termina en `BUILD FAILURE` **a propósito**. Los 4 casos rojos son los de la consigna 3 (`M7`, `C3`, `C4`, `C5`): esa consigna pide demostrar que la suite detecta los problemas, no corregirlos, así que el código de producción queda sin tocar y los casos fallan.

## Estructura

```
TP-Testing-G11/
├── README.md                      este documento
├── INFORME.md                     informe final del trabajo (fuente)
├── INFORME.pdf                    informe final del trabajo (PDF)
├── Enunciado_Trabajo_final.pdf    consignas y criterios de entrega
├── SRS_Persistencia_Clinica.pdf   requisitos de la capa de persistencia
├── evidencia/                     salidas de las corridas y capturas de ejecución
├── setup/                         script del usuario y permisos del motor
└── TPE2026/                       proyecto Maven: código y suites de pruebas
```

> **Nota sobre los informes:** el informe de este trabajo es `INFORME.md` / `INFORME.pdf`, en la raíz del repositorio. `TPE2026/Informe-Prog-III-original.pdf` es el informe del proyecto original de Programación III que entregó la cátedra: es material provisto, no parte de la entrega.

`TPE2026/` contiene el proyecto original de la cátedra (`src/main`) y las suites de pruebas (`src/test/java`):

| Suite | Consigna |
| --- | --- |
| `persistencia/AsociadoDTOTest` | 1 — unitario |
| `persistencia/BaseDeDatosTest` | 2 — integración |
| `persistencia/DataAccessObjectTest` | 2 — integración |
| `modelo/modeloDominio/ambulancia/AmbulanciaStateMockTest` | 3 — mocks |
| `modelo/modeloDominio/ambulancia/AmbulanciaConcurrenciaTest` | 3 — concurrencia |

## Artefactos generados

`TPE2026/Javadoc/`, `TPE2026/target/` y los diagramas `TPE2026/UML*.png` son artefactos generados (el primero por la herramienta `javadoc`, el segundo por Maven, los diagramas por el modelado del proyecto original). No forman parte de la entrega y están ignorados por `TPE2026/.gitignore`.
