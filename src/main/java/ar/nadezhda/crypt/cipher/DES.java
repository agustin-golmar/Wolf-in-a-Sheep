
	package ar.nadezhda.crypt.cipher;

	import java.security.NoSuchAlgorithmException;

	import javax.crypto.NoSuchPaddingException;

	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.CipherMode;

	public class DES extends Cipher {

		public DES(final CipherMode mode, final String password)
				throws NoSuchAlgorithmException, NoSuchPaddingException {
			super(mode, password);
		}

		@Override
		public String getName() {
			return "DES";
		}

		@Override
		public int getKeySizeInBits() {
			// Para que sea compatible con OpenSSL:
			return 64;
		}
	}
