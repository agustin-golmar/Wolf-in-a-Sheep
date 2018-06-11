
	package ar.nadezhda.crypt.cipher.mode;

	import ar.nadezhda.crypt.interfaces.CipherMode;

	public class ECB implements CipherMode {

		@Override
		public String getName() {
			return "ECB";
		}

		@Override
		public boolean needIV() {
			return false;
		}
	}
