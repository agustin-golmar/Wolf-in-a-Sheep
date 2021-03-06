
	package ar.nadezhda.crypt.cipher;

	import java.security.NoSuchAlgorithmException;
	import javax.crypto.NoSuchPaddingException;

	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.CipherMode;

	public class AES192 extends Cipher {

		public AES192(final CipherMode mode, final String password)
				throws NoSuchAlgorithmException, NoSuchPaddingException {
			super(mode, password);
		}

		@Override
		public String getName() {
			return "AES";
		}

		@Override
		public int getKeySizeInBits() {
			return 192;
		}
	}
