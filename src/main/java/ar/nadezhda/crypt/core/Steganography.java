
	package ar.nadezhda.crypt.core;

	import java.io.IOException;

	import ar.nadezhda.crypt.config.Configuration;
	import ar.nadezhda.crypt.core.exception.UnsupportedFormat;
	import ar.nadezhda.crypt.support.Message;

	public class Steganography {

		public Steganography() {
			/*
				[DONE] Leer el portador y validar que sea BMP v3 sin compresión.
					<Depende si es -embed o -extract>
				Validar que entre en el archivo (con/sin encripción).
				Leer el wolf.
				Pasarlo al algoritmo LSB y obtener el buffer de salida.
				Escribir el buffer al archivo final.
			*/
		}

		public static void with(final Configuration config) {
			try {
				final Bitmap carrier = Bitmap.from(config.getCarrierFilename());
				validate(carrier);
				System.out.println("\nSheep Properties:\n" + carrier);
			}
			catch (final IOException exception) {
				System.out.println(Message.CANNOT_OPEN_CARRIER);
			}
			catch (final UnsupportedFormat exception) {
				System.out.println("\n" + exception.getMessage() + "\n");
			}
		}

		protected static void validate(final Bitmap carrier)
				throws UnsupportedFormat {
			if (!carrier.getFileType().equals(Bitmap.SIGNATURE))
				throw new UnsupportedFormat(Message.UNKNOWN_SIGNATURE);
			if (carrier.isCompressed())
				throw new UnsupportedFormat(Message.COMPRESSED_BITMAP);
			if (carrier.getBits() != 24)
				throw new UnsupportedFormat(Message.PALETTE_UNSUPPORTED);
		}
	}
