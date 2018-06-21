
	package ar.nadezhda.crypt.interfaces;

	import java.nio.ByteBuffer;
	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
	import java.security.NoSuchAlgorithmException;
	import java.util.Optional;

	import javax.crypto.BadPaddingException;
	import javax.crypto.IllegalBlockSizeException;
	import javax.crypto.NoSuchPaddingException;
	import javax.crypto.SecretKey;
	import javax.crypto.ShortBufferException;
	import javax.crypto.spec.IvParameterSpec;

	import ar.nadezhda.crypt.support.Message;
	import ar.nadezhda.crypt.support.Random;

	public abstract class Cipher {

		protected final javax.crypto.Cipher cipher;
		protected final String transform;
		protected final CipherMode mode;
		protected final SecretKey key;
		protected final String password;

		public Cipher(final CipherMode mode, final String password)
				throws NoSuchAlgorithmException, NoSuchPaddingException {
			this.transform = getName() + "/" + mode.getName() + "/" + mode.getPadding();
			this.cipher = javax.crypto.Cipher.getInstance(transform);
			this.mode = mode;
			this.password = password;
			this.key = Random.key(this, password);
		}

		public int getBlockSizeInBytes() {
			return cipher.getBlockSize();
		}

		public SecretKey getKey() {
			return key;
		}

		public long getIVSizeInBytes() {
			return mode.needIV()? getBlockSizeInBytes() : 0;
		}

		public CipherMode getMode() {
			return mode;
		}

		public long getOutputSizeInBytes(final int size) {
			return cipher.getOutputSize(size);
		}

		public String getPassword() {
			return password;
		}

		public Cipher transform(
				final ByteBuffer inputBlock, final ByteBuffer outputBlock)
				throws ShortBufferException {
			cipher.update(inputBlock, outputBlock);
			return this;
		}

		public Cipher transformLast(
				final ByteBuffer inputBlock, final ByteBuffer outputBlock)
						throws ShortBufferException,
							IllegalBlockSizeException, BadPaddingException {
			cipher.doFinal(inputBlock, outputBlock);
			return this;
		}

		public Cipher on(final int mode, final Optional<IvParameterSpec> IV)
				throws InvalidKeyException, InvalidAlgorithmParameterException {
			if (this.mode.needIV()) {
				if (IV.isPresent()) {
					cipher.init(mode, key, IV.get());
				}
				else throw new InvalidAlgorithmParameterException(
					Message.NEED_VALID_IV);
			}
			else {
				cipher.init(mode, key);
			}
			return this;
		}

		public Cipher onDecrypt(final Optional<IvParameterSpec> IV)
				throws InvalidKeyException, InvalidAlgorithmParameterException {
			return on(javax.crypto.Cipher.DECRYPT_MODE, IV);
		}

		public Cipher onEncrypt(final Optional<IvParameterSpec> IV)
				throws InvalidKeyException, InvalidAlgorithmParameterException {
			return on(javax.crypto.Cipher.ENCRYPT_MODE, IV);
		}

		public abstract String getName();
		public abstract int getKeySizeInBits();
	}
