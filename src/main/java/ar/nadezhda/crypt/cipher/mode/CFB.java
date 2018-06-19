
	package ar.nadezhda.crypt.cipher.mode;

	import ar.nadezhda.crypt.interfaces.CipherMode;

	public class CFB implements CipherMode {

		@Override
		public String getName() {
			return "CFB8";
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
