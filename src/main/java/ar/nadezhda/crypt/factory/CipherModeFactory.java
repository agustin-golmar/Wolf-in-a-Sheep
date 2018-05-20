
	package ar.nadezhda.crypt.factory;

	import java.util.Optional;

	import com.beust.jcommander.IStringConverter;

	import ar.nadezhda.crypt.interfaces.CipherMode;
	import ar.nadezhda.crypt.interfaces.Factory;
	import ar.nadezhda.crypt.support.ClassBuilder;

	public class CipherModeFactory
		implements Factory<CipherMode>, IStringConverter<Optional<CipherMode>> {

		protected static final String path = "ar.nadezhda.crypt.cipher.mode.";
		protected static final CipherModeFactory instance = new CipherModeFactory();
		protected CipherModeFactory() {}

		@Override
		public Optional<CipherMode> create(final String name) {
			try {
				return Optional.of((CipherMode) ClassBuilder
						.getInstance(path + name.toUpperCase()));
			}
			catch (final ClassNotFoundException exception) {
				return Optional.empty();
			}
		}

		@Override
		public Optional<CipherMode> convert(final String value) {
			return get(value);
		}

		public static Optional<CipherMode> get(final String name) {
			return instance.create(name);
		}
	}
