
	package ar.nadezhda.crypt.factory;

	import java.util.Optional;

	import com.beust.jcommander.IStringConverter;

	import ar.nadezhda.crypt.interfaces.Factory;
	import ar.nadezhda.crypt.interfaces.Steganographer;
	import ar.nadezhda.crypt.support.ClassBuilder;

	public class SteganographerFactory
		implements Factory<Steganographer>, IStringConverter<Optional<Steganographer>> {

		protected static final String path = "ar.nadezhda.crypt.steganographer.";
		protected static final SteganographerFactory instance = new SteganographerFactory();
		protected SteganographerFactory() {}

		@Override
		public Optional<Steganographer> create(final String name) {
			try {
				return Optional.of((Steganographer) ClassBuilder
						.getInstance(path + name.toUpperCase()));
			}
			catch (final ClassNotFoundException exception) {
				return Optional.empty();
			}
		}

		@Override
		public Optional<Steganographer> convert(final String value) {
			return get(value);
		}

		public static Optional<Steganographer> get(final String name) {
			return instance.create(name);
		}
	}
