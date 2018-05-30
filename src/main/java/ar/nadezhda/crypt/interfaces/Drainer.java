
	package ar.nadezhda.crypt.interfaces;

		/**
		* <p>Un tipo <i>drainer</i>, define un único método cuyo objetivo es
		* consumir (o drenar), un flujo de bytes.</p>
		*
		* @see ar.nadezhda.crypt.interfaces.Flow
		*/

	@FunctionalInterface
	public interface Drainer {

		/**
		* <p>Consume el siguiente byte (<i>payload</i>), del flujo al cual se
		* aplica. La forma en la que se obtiene dicho byte depende del flujo
		* en cuestión.</p>
		*
		* @param k
		*	La posición del byte consumido dentro del flujo. Esta posición
		*	debe ser relativa al flujo y no a la fuente de la cual se extrae,
		*	es decir, el primer byte consumido siempre se corresponde con
		*	<i>k = 0</i>.
		* @param payload
		*	El siguiente byte del flujo.
		*
		*/
		public void drain(final long k, final byte payload);
	}
