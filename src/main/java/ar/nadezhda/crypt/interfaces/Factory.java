
	package ar.nadezhda.crypt.interfaces;

	import java.util.Optional;

	public interface Factory<T> {

		public Optional<T> create(final String name);
	}
