# Prueba Técnica: Microservicio de Gestión de Parking "SmartPark"

## Objetivo

Diseñar e implementar un microservicio en **Java y Spring Boot** para la gestión de un parking exclusivo para empleados de Iberia. El sistema debe controlar la disponibilidad de plazas y calcular el coste.

## Funcionalidad Requerida

El microservicio deberá exponer una **API REST** que permita las siguientes operaciones:

1. **Registrar Entrada al Parking**: Recibe la matrícula, que utilizaremos como identificador, y si el usuario desea usar un cargador eléctrico. En base a la ocupación, el sistema decide si admite la entrada o deniega el acceso.

2. **Ocupar Plaza**: Gracias a unos sensores que detectan la matrícula y la plaza ocupada, deberás registrar esta información. Además, se permite cambiar de plaza durante la estancia.

3. **Registrar Salida**: Cuando retira el vehículo del parking, libera la plaza y devuelve un resumen del servicio incluyendo el tiempo de la estancia y el importe total a pagar.

## Reglas de Negocio Clave

* **Capacidad del Parking**: El parking cuenta con 100 plazas, de las cuales, 20 tienen cargador eléctrico.
* **Tarifa Base**: Estacionar tiene un coste de 2,50€ la hora (o fracción) durante las tres primeras horas, y 2€ por cada hora adicional.
* **Suplemento Eléctrico**: Si el vehículo ocupa una plaza con cargador eléctrico, aplica un coste fijo de 3,50€ en concepto de uso del cargador.

## Requerimientos Adicionales

* **Historial de Commits**: Es muy importante que se desarrolle iterativamente, haciendo commits pequeños e incrementales, aunque no compilen, que muestren cómo te has aproximado a la solución.
* **Base de Datos:** Por simplicidad, utiliza la base de datos en memoria (`H2`) que ya encontrarás preconfigurada en el proyecto base.
* **Libertad de Diseño**: Tienes total libertad para definir la arquitectura y el modelo de datos que consideres más apropiado. Lo importante será tener una separación clara de las capas y sus responsabilidades, y que el código sea de calidad.
* **Libertad de Versiones**: Puedes cambiar la versión de Java y de Spring del `pom.xml`, así como incluir las librerías que consideres necesarias.
* **Toma la iniciativa**: Si te surge alguna duda funcional o técnica, actúa de forma proactiva, y documenta en el fichero `NOTAS.md` aquellas decisiones y asunciones que hagas.
* **Testing**: Se valorará muy positivamente la implementación de los tests necesarios.
* **(Opcional / Extra) Definición de Tarea para Despliegue en AWS:** Si te es familiar, y de manera opcional, crea una Task Definition para Amazon ECS. No es necesario que sea funcional, sino que demuestre tu comprensión de los conceptos.

## Pasos para Empezar

Para comenzar a trabajar, sigue estas instrucciones.

1. Clona este repositorio.
2. Crea tu rama de trabajo (es importante que no trabajes directamente sobre `main`).
3. Usa tu IDE favorito e implementa el ejercicio propuesto.

## Entrega

Ve _pusheando_ el progreso a medida que vayas desarrollando. No pasa nada si son soluciones parciales que no compilen; nos gustará ver cómo te fuiste aproximando a la solución. 

Un vez termines, o si te quedas sin tiempo, deja puesto un pull request de tu rama contra la rama `main` con lo que lleves.

¡Mucha suerte!