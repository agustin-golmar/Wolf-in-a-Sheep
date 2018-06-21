
	package ar.nadezhda.crypt.support;

	public class Message {

		public static final String GREETING_BANNER
			= "(2018) Wolf in a Sheep.";

		public static final String EMBED_DESC
			= "If you want to hide a wolf in a sheep.";
		public static final String EXTRACT_DESC
			= "If you want to get back the wolf you hid in a sheep.";
		public static final String IN_DESC
			= "The wolf you want to hide.";
		public static final String P_DESC
			= "The sheep in which you will hide the wolf.";
		public static final String OUT_DESC
			= "The final sheep, with the wolf inside.";
		public static final String STEG_DESC
			= "The steganographer. Must be 'LSB1', 'LSB4' or 'LSBE'.";
		public static final String A_DESC
			= "The cipher involved. Must be 'aes128', 'aes192', 'aes256' or 'des'.";
		public static final String M_DESC
			= "The operating mode of the cipher. Must be 'ecb', 'cfb', 'ofb' or 'cbc'.";
		public static final String PASS_DESC
			= "The password for the cipher.";

		public static final String ONLY_ONE_ERROR
			= "Use only one: [-embed] or [-extract]";
		public static final String OPERATION_NEEDED_ERROR
			= "The following options are required: [-embed] or [-extract]";
		public static final String INPUT_NEEDED_ERROR
			= "The following option is required: [-in]";
		public static final String UNKNOWN_CIPHER_ERROR
			= "Unknown cipher. See usage...";
		public static final String EMPTY_PASSWORD
			= "Cannot use the cipher, because you don't provide a password.";
		public static final String UNKNOWN_MODE_ERROR
			= "Unknown cipher mode. See usage...";
		public static final String UNKNOWN_STEGANOGRAPHER_ERROR
			= "Unknown steganographer. See usage..";

		public static final String CANNOT_OPEN_CARRIER
			= "Cannot open the carrier file (i.e., the sheep).";
		public static final String UNKNOWN_SIGNATURE
			= "Unknown signature. The sheep isn't a bitmap file (BMP v3).";
		public static final String COMPRESSED_BITMAP
			= "The sheep (i.e., the bitmap), is compressed. This is unsupported.";
		public static final String PALETTE_UNSUPPORTED
			= "Color depth unsupported. The palette should be of 24-bits.";
		public static final String CANNOT_OPEN_INPUT
			= "Cannot open the wolf file.";
		public static final String CANNOT_WRITE_OUTPUT
			= "Cannot create the Final Sheep (i.e., the output file).";
		public static final String UNSUPPORTED_BITMAP_FORMAT
			= "The sheep is not in BMP v3 format.";
		public static final String CANNOT_GET_AVAILABLE_SPACE
			= "Warning! The available space in the sheep cannot be calculated.\n" +
			"Proceeding anyway...";
		public static final String EMPTY_FLOW
			= "This is an empty flow. You can't consume it.";
		public static final String INVALID_CIPHER_MODE
			= "The cipher mode specified is not consistent with the input format.";
		public static final String CANNOT_BUILD_ENCRYPTION
			= "Can't build the pipeline for encryption.";
		public static final String CANNOT_BUILD_DECRYPTION
			= "Can't build the pipeline for decryption.";
		public static final String NEED_VALID_IV
			= "Need a valid IV (initialization vector).";

		public static String SHEEP_OVERFLOW_ERROR(
				final long availableSpace, final long bytesNeeded) {
			return "Cannot hide the wolf in the sheep. The wolf is too big.\n" +
				"The sheep supports " + availableSpace + " bytes, but the wolf requires " +
				bytesNeeded + " bytes.";
		}

		public static String EXHAUSTED_FLOW_EXCEPTION(final String message) {
			return "Error consuming the input flow ('" + message + "').";
		}

		public static String CANNOT_ENCODE_EXTENSION(final String extension) {
			return "The extension could not be encoded in UTF-8 ('" + extension + "').";
		}

		public static String CANNOT_GENERATE_FILE_WITH_EXTENSION(
				final String extension) {
			return "Unable to generate the output file with the original extension ('"
				+ extension + "')";
		}

		public static String FILE_IN_USE(final String filename) {
			return "The destination file is in use ('" + filename + "').";
		}

		public static String UNKNOWN_OUTPUT_ERROR(final String filename) {
			return "Unexpected error when generating the output file ('"
				+ filename + "').";
		}

		public static String UNKNOWN_INPUT_ERROR(final String filename) {
			return "Unexpected error when reading from input file ('"
				+ filename + "').";
		}

		public static String CANNOT_OPEN_FINAL_SHEEP(final String filename) {
			return "Cannot open the Final Sheep file ('" + filename + "').";
		}

		public static String FINISH_TIME(final double seconds) {
			return "Finished in " + seconds + " sec.";
		}

		public static String SHEEP_PROPERTIES(final String properties) {
			return "Sheep Properties: \n" + properties + "\n";
		}

		public static String USAGE() {
			return new StringBuilder(1024)
				.append("\tUsage: stegobmp.jar <arguments>\n\n")
				.append("Where <arguments> can be:\n\n")
				.append("\t-embed\n")
				.append("\t\tIf you want to hide a wolf in a sheep.\n")
				.append("\t-extract\n")
				.append("\t\tIf you want to get back the wolf you hid in a sheep.\n\n")
				.append("\t-in <filename>\n")
				.append("\t\tThe wolf you want to hide (only for -embed).\n")
				.append("\t-p <bitmap filename>\n")
				.append("\t\tThe sheep in which you will hide the wolf, or the final sheep with a\n")
				.append("\t\thidden wolf ready for extraction.\n")
				.append("\t-out <filename>\n")
				.append("\t\tThe final sheep, with the wolf inside.\n\n")
				.append("\t-steg <algorithm>\n")
				.append("\t\tThe steganographer. Must be LSB1, LSB4, LSB8 or LSBE.\n")
				.append("\t\t[Default: LSB1]\n")
				.append("\t-a <cipher>\n")
				.append("\t\tThe cipher involved. Must be aes128, aes192, aes256 or des.\n")
				.append("\t\t[Default: aes128]\n")
				.append("\t-m <mode>\n")
				.append("\t\tThe operating mode of the cipher. Must be ECB, CFB, OFB or CBC.\n")
				.append("\t\t[Default: CBC]\n")
				.append("\t-pass <password>\n")
				.append("\t\tThe password for the cipher.\n")
				.toString();
		}
	}
