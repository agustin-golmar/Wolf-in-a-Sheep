
	package ar.nadezhda.crypt.factory;

	import java.util.Optional;

	import com.beust.jcommander.IStringConverter;

	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.Factory;
	import ar.nadezhda.crypt.support.ClassBuilder;

	public class CipherFactory
		implements Factory<Cipher>, IStringConverter<Optional<Cipher>> {

		protected static final String path = "ar.nadezhda.crypt.cipher.";
		protected static final CipherFactory instance = new CipherFactory();
		protected CipherFactory() {}

		@Override
		public Optional<Cipher> create(final String name) {
			try {
				return Optional.of((Cipher) ClassBuilder
						.getInstance(path + name.toUpperCase()));
			}
			catch (final ClassNotFoundException exception) {
				return Optional.empty();
			}
		}

		@Override
		public Optional<Cipher> convert(final String value) {
			return get(value);
		}

		public static Optional<Cipher> get(final String name) {
			return instance.create(name);
		}
	}
