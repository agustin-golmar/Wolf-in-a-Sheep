![...](res/image/readme-header.png)

# Wolf in a Sheep

A steganographer-encrypter application for hiding a wolf in a sheep. This only
work over images of type _BMP v3_ of 24-bits, without compression.

## Build

To build the project, it is necessary to have _Maven +3.5.0_, and
_Java SE 8 Release_ installed. Then, run:

```
$ mvn clean package
```

This will generate a _\*.jar_ in the root folder. If you find any issues with
the building, remove the _\*.jar_ files from the _Maven_ local repository
with:

```
$ rm -fr ~/.m2/repository/ar/nadezhda/*
```

Or do it manually, if you prefer.

## Execution

In the root folder (after build):

```
$ java -jar stegobmp.jar <arguments>
```

Where the arguments can be:

* `-embed`: If you want to hide a wolf in a sheep.
* `-extract`: If you want to get back the wolf you hid in a sheep.
* `-in`: The wolf you want to hide (only for _-embed_).
* `-p`: The sheep in which you will hide the wolf.
* `-out`: The final sheep, with the wolf inside.
* `-steg`: The steganographer. Must be _LSB1_, _LSB4_ or _LSBE_.
* `-a`: The cipher involved. Must be _aes128_, _aes192_, _aes256_ or _des_.
* `-m`: The operating mode of the cipher. Must be _ecb_, _cfb_, _ofb_ or _cbc_.
* `-pass`: The password for the cipher.

By default, `stegobmp` will:

* Use _aes128_ as the cipher (in case you provide a password).
* Use _cbc_ as the cipher mode.
* Use _MD5_ for the hashing of the password (without _salt_).
* Use _PKCS5_ as the wolf-padding mechanism.
* Use 8-bits feedback mode.

## Designer

This project has been built, designed and maintained by:

* [Agustín Golmar](https://github.com/agustin-golmar)

## Bibliography

__"Efficient Method of Audio Steganography by Modified LSB Algorithm and
Strong Encryption Key with Enhanced Security"__. R. Sridevi, Dr. A. Damodaram
and Dr. Svl. Narasimham. _Vol. 5, N° 6, Journal of Theoretical and Applied
Information Technology. JNTUCEH, Hyderabad. 30th June, 2009_.

__"Steganography and Digital Watermarking"__. Jonathan Cummins, Patrick
Diskin, Samuel Lau and Robert Parlett. _School of Computer Science, The
University of Birmingham. 2004_.

__"Análisis de Técnicas Esteganográficas y Estegoanálisis en Canales
Encubiertos, Imágenes y Archivos de Sonido"__. Gustavo A. Isaza E., Carlos
Alberto Espinosa A. and Sandra M. Ocampo C. _Vector, Vol. 1, N° 1, p29-38.
December 2006_.

__"Esteganografía"__. Dr. Roberto Gómez Cárdenas. _Maestría en Seguridad
Informática_. ITESM-CEM. Tecnológico de Monterrey.

__"Exploring Steganography: Seeing the Unseen"__. Neil F. Johnson and Sushil
Jajodia. _George Mason University. February, 1998_.

__"Enhanced Least Significant Bit Algorithm for Image Steganography"__. Shilpa
Gupta, Geeta Gujral and Neha Aggarwal. _IJCEM International Journal of
Computational Engineering & Management. Vol. 15, Issue 4. July, 2012_.
