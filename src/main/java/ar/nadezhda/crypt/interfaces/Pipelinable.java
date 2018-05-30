
	package ar.nadezhda.crypt.interfaces;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;

		/**
		* <p>Un tipo <i>pipelinable</i> permite conectar dos flujos (que
		* pueden ser de diferente tipo). De esta forma se puede aplicar una
		* transformación compleja en base a componentes pequeños y
		* reutilizables.</p>
		*
		* @param <T>
		*	El tipo del flujo de origen.
		* @param <U>
		*	El tipo del flujo final, resultado de aplicar una transformación
		*	sobre el primer flujo.
		*
		* @see ar.nadezhda.crypt.interfaces.Flow
		*
		*/

	@FunctionalInterface
	public interface Pipelinable<T extends Flow, U extends Flow> {

		public U inject(final T flow)
				throws PipelineBrokenException;

		public default <V extends Flow> Pipelinable<T, V> plug(
				final Pipelinable<U, V> pipe) {
			return flow -> pipe.inject(this.inject(flow));
		}
	}
