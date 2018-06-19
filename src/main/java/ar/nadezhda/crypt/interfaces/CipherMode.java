
	package ar.nadezhda.crypt.interfaces;

	public interface CipherMode {

		public static final String DEFAULT_PADDING = "PKCS5Padding";

		public String getName();

		public default String getPadding() {
			return DEFAULT_PADDING;
		}

		public boolean needIV();
	}
