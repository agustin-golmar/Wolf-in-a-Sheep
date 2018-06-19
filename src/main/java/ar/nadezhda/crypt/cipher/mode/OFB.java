
	package ar.nadezhda.crypt.cipher.mode;

	import ar.nadezhda.crypt.interfaces.CipherMode;

	public class OFB implements CipherMode {

		@Override
		public String getName() {
			return "OFB";
		}

		@Override
		public String getPadding() {
			return "NoPadding";
		}

		@Override
		public boolean needIV() {
			return true;
		}
	}
