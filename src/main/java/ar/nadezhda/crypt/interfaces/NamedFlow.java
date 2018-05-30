
	package ar.nadezhda.crypt.interfaces;

		/**
		* <p>Representa un flujo que posee nombre, es decir, un
		* identificador.</p>
		*
		* @see ar.nadezhda.crypt.interfaces.Flow
		*/

	public interface NamedFlow extends Flow {

		public String getName();
	}
