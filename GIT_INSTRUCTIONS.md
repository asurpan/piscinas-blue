# INSTRUCCIONES DE DESPLIEGUE WEB (IMPORTANTE)

Para que los cambios en la Landing Page y la Política de Privacidad se vean en internet, **SIEMPRE** se deben subir a la rama **`main`**.

## Comandos para subir cambios correctamente:

1. **Añadir archivos corregidos**:
   `git add index.html privacidad.html robots.txt sitemap.xml p.png`

2. **Crear el commit**:
   `git commit -m "Descripción de los cambios"`

3. **Subir a la rama correcta (CRÍTICO)**:
   `git push web-solo main:main`

**NOTA**: Nunca usar la rama `master`, ya que GitHub Pages está configurado para leer de `main`. Si hay conflictos, se puede usar `--force` al final del comando de push.

## Verificación de iconos y SEO:
- Los iconos (Favicon y Redes Sociales) usan rutas absolutas: `https://asurpan.github.io/piscinas-blue/p.png`.
- Search Console lee el archivo `sitemap.xml` para indexar las palabras clave (cloro, agua verde, mantenimiento, gresite).
