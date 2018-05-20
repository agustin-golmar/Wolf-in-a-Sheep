
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
		public static final String UNKNOWN_MODE_ERROR
			= "Unknown cipher mode. See usage...";
		public static final String UNKNOWN_STEGANOGRAPHER_ERROR
			= "Unknown steganographer. See usage...";
	}
