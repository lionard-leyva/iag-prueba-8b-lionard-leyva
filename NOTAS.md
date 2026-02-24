# NOTAS - Decisiones y asunciones

Este documento resume las decisiones tomadas durante la prueba, en orden cronologico desde el primer commit.

## 1) Enfoque inicial y estrategia de entrega
- Se priorizo un flujo incremental tipo TDD (commit de test, luego commit de implementacion).
- Se eligio resolver primero reglas de negocio nucleares (tarifas, capacidad, entrada/salida), y despues infraestructura.
- Se mantuvo H2 en memoria (`jdbc:h2:mem:smartparkdb`) por simplicidad y velocidad de iteracion.

## 2) Reglas de negocio implementadas
- Tarifa base:
  - 2.50 EUR/hora o fraccion para las primeras 3 horas.
  - 2.00 EUR/hora adicional a partir de la cuarta.
- Recargo electrico:
  - +3.50 EUR fijo cuando usa plaza con cargador.
- Capacidad:
  - 100 plazas totales.
  - 20 plazas electricas.
- Entrada:
  - Se rechaza matricula duplicada mientras siga dentro.
- Salida:
  - Cierra sesion, libera plaza y devuelve resumen con fechas y coste.
- Cambio de plaza:
  - Se permite durante la estancia.
  - Ajuste posterior: solo para sesiones en estado `PARKED` (no `ADMITTED`).
  - En `PATCH /parking/occupations`, el cambio debe ser real:
    - no puede reasignar la misma plaza actual,
    - debe mover a otra plaza disponible compatible (electrica/no electrica).
  - Implementacion aplicada:
    - primero se busca plaza alternativa,
    - despues se libera la actual y se ocupa la nueva.
- Inicio del cobro (asuncion funcional):
  - El README no especifica si el tiempo de cobro inicia al entrar o al ocupar plaza.
  - Se asumio que el cobro inicia en `entryTime` (estado `ADMITTED`), no en `PARKED`.
  - Por tanto, un vehiculo admitido pero aun no estacionado tambien acumula tiempo de estancia.

## 3) Modelo de dominio y estados
- Se introdujo explicitamente estado de sesion:
  - `ADMITTED`, `PARKED`, `EXITED`.
- Se introdujo estado de plaza:
  - `FREE`, `OCCUPIED`.
- Se movio el calculo de tarifa a clase dedicada `ParkingRateCalculator`.
- Se fortalecieron invariantes de dominio para transiciones invalidas.

## 4) API y contrato HTTP
- Endpoints:
  - `POST /parking/entries`
  - `POST /parking/occupations`
  - `PATCH /parking/occupations`
  - `POST /parking/exits`
- Se considero especificamente `PATCH` para cambio de plaza:
  - El README define la capacidad de "cambiar de plaza durante la estancia", pero no fija verbo HTTP ni ruta exacta.
  - Se eligio `PATCH /parking/occupations` al tratarse de una actualizacion parcial del estado de ocupacion.
  - Se mantuvo `POST /parking/occupations` para la asignacion inicial y `PATCH` para la modificacion posterior.
- Codigos adoptados:
  - `201` entrada aceptada.
  - `409` entrada rechazada por duplicado/capacidad.
  - `200` operaciones correctas (ocupar/cambiar/salir).
  - `404` cuando el vehiculo no esta dentro.
  - `422` para estados de negocio no procesables.
- Contrato de salida:
  - Campo `licensePlate` (no `plate`) en respuesta de salida.

## 5) Refactor de arquitectura
- Se separo aplicacion/dominio/infraestructura para hacer responsabilidades mas claras.
- Renombres principales:
  - `ParkingService` -> `ParkingUseCaseService`
  - `PricingService` -> `ParkingRateCalculator`
- Se introdujo estilo ports and adapters:
  - Puertos de dominio para repositorios (`ParkingSessionRepositoryPort`, `ParkingSpotRepositoryPort`).
  - Adaptadores JPA para persistencia.
  - Entidades JPA + mappers + repositorios Spring Data.
- Se agrego `ParkingSpotInitializer` para bootstrap de plazas:
  - Se ejecuta al iniciar la aplicacion (`@PostConstruct`).
  - Si la tabla de plazas ya tiene datos (`count() > 0`), no vuelve a insertar (idempotente).
  - Si esta vacia, crea 100 plazas con IDs fijos `1..100`.
  - Las plazas `1..20` se marcan con cargador electrico; `21..100` sin cargador.
  - Objetivo: asegurar dataset minimo estable en H2 sin depender de pasos manuales.

## 6) Decisiones de testing
- Se mantuvo separacion por capas:
  - Dominio: pruebas puras de calculo de tarifa.
  - Aplicacion: pruebas de casos de uso.
  - Infraestructura: pruebas de inicializacion y flujo HTTP de controlador.
- Se movio el e2e de `domain` a capa de controlador:
  - `ParkingFlowIT` -> `ParkingControllerTest` en infraestructura.
- Se anadio cobertura para cambio de plaza en estancia.


## 7) Decisiones de diseno descartadas (conscientemente)
- Se considero un Aggregate Root `Parking` estricto en dominio.
- Se descarto en esta prueba para evitar sobrecarga de complejidad con JPA en un ejercicio acotado.
- En un entorno productivo se valoraria evolucion a CQRS/aggregate mas explicito si el dominio y carga lo justifican.
- Se evaluo usar Flyway para versionado de esquema y seed inicial.
- Se descarto en esta prueba por alcance/tiempo:
  - H2 en memoria se recrea por ejecucion, y el inicializador cubre el seed funcional requerido.
  - Introducir migraciones versionadas en este punto anadia configuracion y scripts SQL extra
    sin cambiar las reglas de negocio pedidas.
  - Para un entorno real o persistencia no efimera, Flyway/Liquibase seria la opcion recomendada.
