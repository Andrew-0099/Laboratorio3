# Laboratorio 3 - Herencia

Aplicación de consola en Java para administrar los envíos, vehículos, despachos e ingresos de
EnvíaGT. La solución usa herencia y polimorfismo para las reglas de sobres, paquetes,
paquetes frágiles y cargas refrigeradas.

## Requisitos

- JDK 17 o posterior.
- Una terminal ubicada en la raíz del repositorio.

## Compilar y ejecutar

En Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force bin | Out-Null
javac -encoding UTF-8 -d bin src\*.java
java -cp bin Main
```

En macOS o Linux:

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin src/*.java
java -cp bin Main
```

El programa comienza con ocho envíos y tres vehículos de demostración. Estos datos iniciales
no generan ingresos. El menú permanece activo hasta elegir la opción `0`.

## Ejecutar las pruebas

Windows PowerShell:

```powershell
javac -encoding UTF-8 -d bin src\*.java tests\*.java
java -cp bin PruebasSistema
```

macOS o Linux:

```bash
javac -encoding UTF-8 -d bin src/*.java tests/*.java
java -cp bin PruebasSistema
```

Las pruebas muestran el resultado esperado y obtenido para costos, peso volumétrico,
redondeo por fracción, seguro, tiempos, capacidad, refrigeración, reintentos e ingresos.

## Estructura

- `src/Main.java`: punto de entrada y datos iniciales.
- `src/ControladorEnvios.java`: coordinación del menú.
- `src/VistaConsola.java`: entrada y salida segura por consola.
- `src/SistemaEnvios.java`: colecciones, unicidad, reportes e ingresos.
- `src/Envio.java` y subclases: reglas polimórficas de cada categoría.
- `src/Despacho.java`: asignación, ruta, resultados y liberación del vehículo.
- `tests/PruebasSistema.java`: pruebas automáticas sin dependencias externas.
