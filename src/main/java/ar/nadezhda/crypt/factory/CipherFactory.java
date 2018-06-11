
	package ar.nadezhda.crypt.factory;

	import java.lang.reflect.Constructor;
	import java.util.Optional;

	import com.beust.jcommander.IStringConverter;

	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.CipherMode;
	import ar.nadezhda.crypt.interfaces.Factory;
	import ar.nadezhda.crypt.support.ClassBuilder;

	public class CipherFactory
		implements Factory<Constructor<Cipher>>,
			IStringConverter<Optional<Constructor<Cipher>>> {

		protected static final String path = "ar.nadezhda.crypt.cipher.";
		protected static final CipherFactory instance = new CipherFactory();
		protected CipherFactory() {}

		@Override
		@SuppressWarnings("unchecked")
		public Optional<Constructor<Cipher>> create(final String name) {
			try {
				return Optional.of((Constructor<Cipher>) ClassBuilder
						.getConstructor(path + name.toUpperCase(),
							CipherMode.class, String.class));
			}
			catch (final ClassNotFoundException exception) {
				return Optional.empty();
			}
		}

		@Override
		public Optional<Constructor<Cipher>> convert(final String value) {
			return get(value);
		}

		public static Optional<Constructor<Cipher>> get(final String name) {
			return instance.create(name);
		}
	}
