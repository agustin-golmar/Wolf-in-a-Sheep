
	package ar.nadezhda.crypt.interfaces;

		/**
		* <p>Representa un flujo cuyo limite existe, es decir, el flujo es
		* finito.</p>
		*
		* @see ar.nadezhda.crypt.interfaces.Flow
		*/

	public interface BoundedFlow extends Flow {

		public long getSize();
	}
