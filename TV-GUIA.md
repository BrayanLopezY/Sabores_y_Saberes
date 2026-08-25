# Guía de la aplicación Smart TV

## Funciones incluidas

- Platillo destacado en la portada.
- Filas horizontales agrupadas por Oaxaca, Yucatán, Puebla, Jalisco y Michoacán.
- Navegación mediante flechas y botón OK del control remoto.
- Borde dorado y aumento de tamaño para mostrar qué elemento tiene el foco.
- Detalle con descripción, ingredientes, preparación e importancia cultural.
- Botón de video cuando el platillo tiene un enlace disponible.
- Botón para regresar al catálogo.

## Cómo ejecutar el módulo TV

1. Abre el proyecto completo en Android Studio.
2. En **Tools → Device Manager**, selecciona **Create Virtual Device**.
3. Abre la categoría **TV** y elige **Android TV (1080p)**.
4. Descarga una imagen de sistema estable si Android Studio lo solicita.
5. En la barra superior elige la configuración `tv`.
6. Presiona **Run**.
7. Usa las flechas del emulador o del teclado para cambiar el foco y Enter como botón OK.

## Decisiones de alcance

La TV está orientada a descubrir y consultar contenido desde lejos. Por eso no incluye favoritos, escritura con teclado, cuentas ni edición de contenido. La búsqueda puede añadirse posteriormente si las pruebas con usuarios demuestran que realmente es necesaria.

Los platillos se leen desde `data`, exactamente igual que en móvil. Los modelos están en `core` y el contrato/casos de uso en `domain`. No se mantiene una segunda copia del catálogo dentro de `tv`.

## Recibir un platillo desde Mobile

1. Desde la carpeta raíz ejecuta `py sync_server.py`.
2. Mantén la consola abierta.
3. Ejecuta primero la app TV y luego la app Mobile.
4. En Mobile abre un platillo y presiona **Ver en TV**.
5. En aproximadamente un segundo la TV abrirá automáticamente el detalle recibido.

Ambos emuladores se comunican con el mismo servidor de la computadora mediante `10.0.2.2:8765`.
