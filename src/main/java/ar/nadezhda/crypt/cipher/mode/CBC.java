
	package ar.nadezhda.crypt.cipher.mode;

	import ar.nadezhda.crypt.interfaces.CipherMode;

	public class CBC implements CipherMode {

		@Override
		public String getName() {
			return "CBC";
		}

		@Override
		public boolean needIV() {
			return true;
		}
	}
