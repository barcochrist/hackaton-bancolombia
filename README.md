#Hackaton Bancolombia

##Problema

###Descripción
Un importante banco desea ofrecer una cena a grupos selectos de clientes. El banco definirá 6 grupos de clientes según ciertos criterios, los cuales serán invitados y cada grupo contará con una mesa. Las mesas cuentan con 8 puestos y en caso de que un grupo tenga menos de 4 clientes, la mesa no sería instalada y se considerará cancelada.
Se requiere construir un programa que reciba los criterios (filtros) para formar cada grupo y que según esto determine los clientes a ser invitados. La salida del programa debe entregar los códigos de cliente asignados a cada mesa.

###Consideraciones
En caso de que los filtros devuelvan más de 8 personas, se tomarían las primeras 8 en función de sus depósitos totales (tomando los primeros 8 con mayor monto total sumando el balance de todas sus cuentas y en caso de mismo monto, ordenar por código de manera ascendente como criterio de prioridad para la mesa). En caso de que el total de personas sea menor a 4 personas, marcar la mesa como CANCELADA (ver especificación de la salida).

Es importante tener en cuenta lo siguiente, y es que solo se debe incluir a una persona por empresa (la de mayor monto), es decir, todas las personas en una mesa deben ser de empresas diferentes. Por lo tanto, si dentro de las 8 personas, dos son de la misma empresa, se sacaría de la lista a la persona con menor monto en las cuentas y su lugar sería tomado por el siguiente cliente que cumpla con los requisitos. Algunos clientes tienen su número de identificación encriptado (marcados con el flag encrypt en 1) y requiere ser desencriptado utilizando el web service: https://test.evalartapp.com/extapiquest/code_decrypt/ (Pasar el código a desencriptar al final de la url y capturar la cadena devuelta por el web service con el código desencriptado).

De igual manera, el número de hombres y mujeres debe ser el mismo en cada mesa. En caso de que haya una diferencia, se debe eliminar de la mesa a aquellos con los montos más bajos y remplazarlos por las siguientes personas del sexo opuesto que cumplan con los criterios. En caso de que no sea posible armar una mesa de al menos 4 personas, la mesa deberá ser cancelada.

###Formato de Entrada
La entrada será un archivo que su programa debe ser capaz de procesar (descargar el archivo entrada.txt). El archivo contiene cabeceras con el nombre de las mesas y los criterios de filtro para cada una. Estos filtros permiten identificar que clientes son candidatos para ser invitados a esa  mesa en particular (Se debe tomar esto en cuenta además de los criterios que aplican a todas las mesas). El formato para los filtros es el código del campo a filtrar, seguido de dos puntos y el valor a utilizar. Los posibles filtros son:
- Tipo de Cliente (TC)
- Código de ubicación geográfica (UG)
- Rango Inicial Balance (RI)
- Rango Final Balance (RF)

Aquí un ejemplo de cómo se veria la entrada:
```
<General>
TC: 1 
UG: 3
RI: 100000
RF: 200000
<Mesa 1>
UG:2
RI: 500000
<Mesa 2>
UG: 1
RF: 10000
```

En este ejemplo, se solicita la información para llenar 3 mesas (Genera, Mesa 1 y Mesa 2) con diversos criterios. Por ejemplo, la mesa dos serían clientes de la Ubicación Geográfica 2 y con un total en sus cuentas superior a 500000. Todos los filtros corresponden a información contenida en la base de datos de cuentas y clientes (account y client).

###Estructura de Base de Datos
La base de datos cuenta con dos tablas, account y client. Debe descargar el archivo bd.txt e importarlo a una base de datos MySQL. Client contiene los datos de los clientes y account las cuentas. Un cliente puede tener entre 0 y varias cuentas.

Estructura de client:
```
id: Un identificador interno
code: El código del cliente (el dato que se debe usar para los resultados) male: 1 si es hombre, 0 si es mujer
encrypt: 0 si no está encriptado, 1 si requiere desencriptar con el webservice type: El código del tipo de cliente
location: El código de ubicación del cliente
company: El código de la empresa a la que pertenece el cliente
```
Estructura de account:
```
id: Un identificador interno
client_id: el id del cliente que posee la cuenta
balance: El monto que posee el cliente en esa cuenta
```

###Formato de Salida
El formato de salida debe tener una estructura similar a la de entrada, pero en vez de los filtros, se listarían los códigos de los clientes de la lista, separados por comas, sin espacios y ordenados según el monto de sus cuentas (y en caso de coincidir el monto, ordenado por código), de mayor a menor. Para mesas que no alcanzaron los 4 integrantes, se colocaría la palabra **CANCELADA**. Por ejemplo:

```
<General>
C10029,C10129,C12105,C10126,C10088,C10091,C10051,C10354
<Mesa 1>
C10629,C15129,C12122,C10198
<Mesa 2>
CANCELADA
```

Estos ejemplos son ficticios y no corresponden a los resultados que pudiera obtener realmente.

##Acerca del proyecto

La solución se hace utilizando el lenguaje de programación Java a traves de la construcción de un servicio web que brinda un endpoint 
para procesar el archivo de carga en formato txt y obtener la respuesta de los invitados para cada mesa.

###Tecnologiás
Las siguientes tecnologias y herramientas de desarrollo de software fueron utilizadas en el proyecto:

- Java 11
- Spring boot
- Gradle
- JDBC
- MySQL
- Postman
- IntelliJ IDEA

###Requerisitos para desplegar
Para desplegar la aplicación solo necesitas lo siguiente en tu ambiente:
- Java 11
- Base de datos MySQL
- IDE Java (IntelliJ IDEA)
- Recomandado: Software para consumo de API Rest (postman)

###¿Como desplegar?
Para ejecutar la aplicación debes seguir los siguientes pasos:

1. Importar el proyecto en el IDE de tu preferencia 
2. Crear la base de datos utilizando el script del archivo `database.sql`
3. Crear la estructura de la base de datos utilizando el script del archivo `schema.sql`
4. Crear los datos iniciales utilizando el script del archivo `insert.sql`
5. Ejecutar la aplicación utilizando Gradle con el comando: `./gradlew bootRun`
6. Ejecutar Postman o CURL y consumir la url: http://localhost:8080/api/clients/process enviando como parametro el archivo de entrada (con el formato específico)

> Para obtener un ejemplo de la petición y la respuesta por favor abrir la colección de Postman que se encuentra en el proyecto con el nombre: **hackaton_bancolombia.postman_collection.json**

