# Sabores y Saberes — aplicación móvil y Android TV (MVP)

Proyecto académico Android desarrollado con Kotlin, Jetpack Compose, Compose for TV, MVVM, Coroutines, StateFlow, Navigation y DataStore. Las aplicaciones `mobile` y `tv` reutilizan los módulos `core`, `domain` y `data` sin duplicar modelos ni lógica.

## Cómo abrirlo

1. Descomprime el ZIP.
2. En Android Studio selecciona **Open** y elige la carpeta `SaboresYSaberes-Mobile`.
3. Espera la sincronización de Gradle.
4. Para teléfono, crea un emulador normal con Android 7.0 (API 24) o superior y ejecuta `mobile`.
5. Para televisión, crea un dispositivo virtual desde **Device Manager → TV → Android TV (1080p)** y ejecuta `tv`.

No se necesitan claves, cuenta, base de datos externa ni servidor. La primera sincronización de Gradle sí requiere internet.

## Configuración de compilación

El proyecto utiliza Android Gradle Plugin 8.12, `compileSdk 36` y toolchain Java 17 en todos los módulos. Móvil y TV usan Compose BOM `2025.08.00`, evitando dependencias de Compose 1.12 que requieren AGP 9.1 y `compileSdk 37`.

Si Android Studio conservó dependencias de una sincronización anterior, selecciona **File → Sync Project with Gradle Files** y después **Build → Clean Project** antes de volver a ejecutar.

## 1. Problema que resuelve

La información sobre cocina tradicional mexicana suele encontrarse dispersa y sin una presentación sencilla para consulta móvil. Esto dificulta que estudiantes y público general conozcan no solo una receta, sino también su procedencia y significado cultural. La aplicación concentra una muestra pequeña y accesible de platillos de cinco regiones.

## 2. Objetivo general

Desarrollar una aplicación móvil y, en una segunda etapa, una aplicación para Smart TV que faciliten la difusión y preservación de información básica sobre platillos tradicionales mexicanos mediante un catálogo visual, claro y fácil de explorar.

## 3. Objetivos específicos

- Mostrar nombre, región, descripción, ingredientes, preparación e importancia cultural.
- Permitir búsqueda por nombre o región y filtrado regional.
- Guardar favoritos localmente en el teléfono.
- Abrir contenido audiovisual cuando esté disponible.
- Reutilizar modelos, repositorios y casos de uso entre móvil y TV.
- Mantener una interfaz diferente y apropiada para cada dispositivo.

## 4. Alcance

El MVP contiene cinco regiones (Oaxaca, Yucatán, Puebla, Jalisco y Michoacán), diez platillos precargados, catálogo, búsqueda, filtros, detalle, favoritos persistentes y enlaces de video opcionales. El contenido local permite demostrar el proyecto sin desplegar un backend.

## 5. Limitaciones

- Muestra cultural deliberadamente pequeña; no representa toda la gastronomía mexicana.
- Los datos no se editan desde la aplicación.
- Los favoritos solo existen en el dispositivo y no se sincronizan.
- Los videos se abren con una aplicación externa y requieren internet.
- Las ilustraciones del MVP son composiciones gráficas/emoji, no fotografías documentales.
- No incluye autenticación, pagos, comentarios, redes sociales, IA ni panel administrativo.

## 6. Usuarios del sistema

- Público general interesado en gastronomía y cultura mexicana.
- Estudiantes y docentes que necesiten una introducción visual.
- Familias o visitantes que deseen explorar platillos regionales.

No se necesitan roles porque todos los usuarios consultan el mismo contenido.

## 7. Funciones principales

Catálogo, búsqueda, filtro por región, detalle cultural y culinario, favoritos y apertura de videos. Se dejan fuera perfiles, comentarios, calificaciones, notificaciones y administración porque no ayudan de forma directa al objetivo del MVP.

## 8. Arquitectura propuesta

Se usa MVVM con separación por capas:

- UI Compose: pantallas y estado visual.
- ViewModel: filtros, búsqueda y acciones del usuario mediante StateFlow.
- Domain: contratos de repositorio y casos de uso.
- Data: repositorio local, persistencia de favoritos y contrato remoto futuro.
- Core: modelos comunes.

El flujo de dependencias es `mobile → domain/data → core`. El futuro módulo `tv` dependerá de las mismas capas compartidas y tendrá su propia UI y ViewModels cuando la interacción lo requiera.

## 9. Módulos

| Módulo | Responsabilidad |
|---|---|
| `mobile` | Activity, navegación, pantallas Compose y ViewModel móvil |
| `core` | `Dish`, `Region` y utilidades compartidas |
| `domain` | Repositorio abstracto y casos de uso |
| `data` | Contenido local, DataStore, implementación del repositorio y contrato Retrofit |
| `tv` | Pantallas Compose for TV, navegación con control remoto y acceso a videos |

## 10. Modelo de datos

`Dish` contiene: `id`, `name`, `region`, `shortDescription`, lista de `ingredients`, pasos de `preparation`, `culturalHistory`, `imageKey` y `videoUrl` opcional. `Region` es un enum limitado a cinco valores. Los favoritos se almacenan como un conjunto de IDs; no se requiere una entidad de usuario.

## 11. Diagrama de contexto

```mermaid
flowchart TD
    U[Usuario] -->|Busca y explora| M[App móvil]
    U -->|Usará control remoto| T[App Smart TV futura]
    M --> C[Catálogo compartido]
    T --> C
    M --> F[Favoritos locales]
    M --> V[Proveedor externo de video]
    T --> V
```

## 12. Flujo de funcionamiento

1. La aplicación carga el catálogo local por medio del repositorio.
2. El ViewModel combina catálogo, texto de búsqueda, región y favoritos.
3. La interfaz observa el resultado mediante StateFlow.
4. El usuario abre un platillo y consulta toda su información.
5. Si lo marca como favorito, DataStore conserva el ID.
6. Si existe video, Android abre el enlace con una aplicación compatible.

## 13. Pantallas móviles necesarias

Solo se usan dos destinos principales:

1. **Catálogo**, que integra búsqueda, chips regionales y pestaña de favoritos.
2. **Detalle**, con imagen ilustrativa, descripción, ingredientes, preparación, historia y botón de video.

No es necesaria una pantalla separada por región ni una pantalla independiente de búsqueda: integrarlas en el catálogo reduce alcance y navegación.

## 14. Pantallas de Smart TV necesarias

La aplicación de TV usa dos destinos y el reproductor externo:

1. Inicio/exploración con filas de platillos y regiones.
2. Detalle del platillo, diseñado para verse a distancia.
3. Lanzamiento del video en una aplicación compatible.

Favoritos y teclado de búsqueda pueden omitirse inicialmente en TV para simplificar la interacción con control remoto.

## 15. Estructura de Android Studio

```text
SaboresYSaberes/
├── mobile/
│   └── src/main/java/.../mobile/
│       ├── MainActivity.kt
│       ├── SaboresApplication.kt
│       └── ui/
├── core/src/main/java/.../core/model/
├── domain/src/main/java/.../domain/
├── data/src/main/java/.../data/
└── tv/src/main/java/.../tv/
    ├── TvMainActivity.kt
    ├── TvApplication.kt
    └── ui/
```

## 16. Orden recomendado de desarrollo

1. Definir regiones y contenido de muestra.
2. Crear `core` y el modelo `Dish`.
3. Crear contratos y casos de uso en `domain`.
4. Implementar datos locales y favoritos en `data`.
5. Construir catálogo y filtros en móvil.
6. Construir detalle y enlace de video.
7. Probar búsqueda, persistencia y navegación.
8. Probar navegación por foco en el módulo TV reutilizando las capas existentes.
9. Solo si la materia lo exige, reemplazar el origen local por una API pequeña.

## Decisiones para mantener el alcance

Retrofit se incluye como contrato preparado, pero el MVP usa datos locales. Una API real, base de datos remota, fotografías finales y reproductor integrado pueden añadirse después; no son necesarios para demostrar el objetivo cultural, la arquitectura compartida ni el funcionamiento móvil.

## 17. Intercambio de datos Mobile → TV

Para demostrar el intercambio real de datos entre ambas aplicaciones se agregó un flujo mínimo de sincronización local:

1. Ejecuta `sync_server.py` en la computadora con `py sync_server.py` (o `python sync_server.py`).
2. Inicia el módulo `tv` en un emulador de televisión.
3. Inicia el módulo `mobile` en un emulador de teléfono.
4. Abre el detalle de cualquier platillo en Mobile.
5. Presiona **Ver en TV**.
6. Mobile envía el `dishId` al servidor local mediante `POST /selection`.
7. TV consulta `GET /selection` cada segundo y, al detectar una selección nueva, abre automáticamente el detalle del mismo platillo.
8. La TV muestra un mensaje breve: **Recibido desde Mobile: [nombre del platillo]**.

En los emuladores Android Studio se usa `http://10.0.2.2:8765/`, porque `10.0.2.2` representa la computadora anfitriona desde el emulador. Esta implementación no usa Firebase ni un servicio externo. El servidor utiliza únicamente la biblioteca estándar de Python y está pensado para la demostración académica local.

### Evidencia sugerida

- Captura Mobile mostrando el detalle del platillo y el botón **Ver en TV**.
- Captura Mobile inmediatamente después de enviarlo, mostrando el mensaje de confirmación.
- Captura TV mostrando automáticamente el mismo platillo y el mensaje **Recibido desde Mobile**.
- Captura de la consola de `sync_server.py` con el `dishId` recibido como evidencia adicional del intercambio.

> Nota: `android:usesCleartextTraffic="true"` se habilitó únicamente para permitir HTTP local durante la demostración. Para producción debe usarse HTTPS.
