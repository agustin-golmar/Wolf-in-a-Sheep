
	package ar.nadezhda.crypt.support;

	import java.security.MessageDigest;
	import java.security.NoSuchAlgorithmException;
	import java.security.SecureRandom;

	import javax.crypto.SecretKey;
	import javax.crypto.spec.IvParameterSpec;
	import javax.crypto.spec.SecretKeySpec;

	import ar.nadezhda.crypt.interfaces.Cipher;

	public class Random {

		public static SecretKey key(final Cipher cipher, final String password)
				throws NoSuchAlgorithmException {
			return key(cipher.getName(), cipher.getKeySizeInBits(), password);
		}

		public static SecretKey key(
				final String cipher, final int bits, final String password)
					throws NoSuchAlgorithmException {
			final MessageDigest hasher = MessageDigest.getInstance("SHA-256");
			final byte [] hash = hasher.digest(password.getBytes());
			return new SecretKeySpec(hash, 0, bits/8, cipher);
		}

		public static IvParameterSpec IV(final Cipher cipher)
				throws NoSuchAlgorithmException {
			return IV(cipher.getBlockSizeInBytes());
		}

		public static IvParameterSpec IV(final int bytes)
				throws NoSuchAlgorithmException {
			final SecureRandom random = SecureRandom.getInstanceStrong();
			final byte [] IV = new byte [bytes];
			random.nextBytes(IV);
			return new IvParameterSpec(IV);
		}
	}
