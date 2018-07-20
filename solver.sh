
	#! /bin/bash

	#
	# $1 : El nombre de la carpeta en la cual reconstruir la solución. Es el
	#		único parámetro utilizado, y es obligatorio.
	#

	# Carpeta destino de las soluciones:
	TARGET="$1/solution"

	# Construir carpeta:
	mkdir "$TARGET"

	# Descomprimir imágenes a procesar:
	unzip "res/data/images.zip" -d "$1"

	# Extraer la imagen PNG:
	java -jar stegobmp.jar -extract \
			-p "$1/paris.bmp" \
			-out "$TARGET/paris-lsb1" \
			-steg LSB1

	# Extraer instrucciones para decodificar la imagen PNG:
	java -jar stegobmp.jar -extract \
			-p "$1/sherlock.bmp" \
			-out "$TARGET/sherlock-lsbe" \
			-steg LSBE

	# Cambiar extensión de la imagen a ZIP:
	cp "$TARGET/paris-lsb1.png" "$TARGET/paris-lsb1.zip"

	# Descomprimir imagen:
	unzip "$TARGET/paris-lsb1.zip" -d "$TARGET"

	# Decodificar el video WMV final:
	java -jar stegobmp.jar -extract \
			-p "$1/medianoche1.bmp" \
			-out "$TARGET/medianoche1-lsb4-aes192-cfb" \
			-steg LSB4 \
			-a aes192 \
			-m cfb \
			-pass ganador
