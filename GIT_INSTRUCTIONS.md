# INSTRUCCIONES DE DESPLIEGUE WEB (IMPORTANTE)

Para que los cambios en la Landing Page y la Política de Privacidad se vean en internet, **SIEMPRE** se deben subir a la rama **`main`**.

## Comandos para subir cambios correctamente:

1. **Añadir archivos corregidos**:
   `git add index.html privacidad.html robots.txt sitemap.xml p.png logo.png .gitignore`

2. **Crear el commit**:
   `git commit -m "Descripción de los cambios"`

3. **Subir a la rama correcta (CRÍTICO)**:
   `git push web-solo main:main`

**NOTA**: Nunca usar la rama `master`, ya que GitHub Pages está configurado para leer de `main`. Si hay conflictos, se puede usar `--force` al final del comando de push.

## Reglas de Oro para Imágenes y Redes Sociales:
- **Peso de Imagen**: Las imágenes de vista previa (`og:image`) **NUNCA** deben superar los **500 KB**. Si pesan más de 1 MB, Facebook/WhatsApp las bloquearán y se verá un cuadro blanco.
- **Imagen Optimizada**: Usa siempre `logo.png` para el meta-tag `og:image`.
- **Git Ignore**: Si añades una imagen nueva en la raíz, debes permitirla explícitamente en el archivo `.gitignore` añadiendo `!nombre_imagen.png`.
- **Rutas**: Las rutas en `index.html` deben incluir siempre el nombre del repositorio: `https://asurpan.github.io/piscinas-blue/`.

## Verificación de iconos y SEO:
- Los iconos y redes sociales usan: `https://asurpan.github.io/piscinas-blue/logo.png`.
- Después de subir cambios, limpiar caché en: [Facebook Sharing Debugger](https://developers.facebook.com/tools/debug/).
