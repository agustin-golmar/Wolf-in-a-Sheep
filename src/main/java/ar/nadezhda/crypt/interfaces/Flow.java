
	package ar.nadezhda.crypt.interfaces;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;

		/**
		* <p>Un <i>flujo</i> representa una fuente de bytes que puede ser
		* <i>drenada</i> por otra entidad. El origen del flujo de bytes no se
		* encuentra limitado de ninguna forma.</p>
		*
		* @see ar.nadezhda.crypt.interfaces.Drainer
		*/

	public interface Flow {

		/**
		* <p>Consume el siguiente byte del flujo, si existe.</p>
		*
		* @param drainer
		*	El consumidor del flujo. Puede variar cada vez que se llama al
		*	método, <i>i.e.</i>, el flujo puede ser consumido por múltiples
		*	entidades.
		*
		* @throws ExhaustedFlowException
		*	En caso de que se intente consumir un flujo agotado.
		*
		*/
		public void consume(final Drainer drainer)
				throws ExhaustedFlowException;

		/**
		* <p>Indica si el flujo está agotado, es decir, si ya no queda más
		* información para consumir.</p>
		*
		* @return
		*	Verdadero, si el flujo está vacío. Falso de otra forma.
		*
		*/
		public boolean isExhausted();

		/**
		* <p>Permite inyectar un flujo en una cadena de <i>pipes</i>, que
		* puede o no estar compuesta por más de 1 pipe. Esto permite escribir
		* expresiones en orden inverso (<i>i.e.</i>, el flujo se especifica al
		* principio de un <i>pipeline</i>, y no al final).</p>
		*
		* @param pipe
		*	El <i>pipe</i> en el cual inyectar el flujo. El tipo debe ser
		*	compatible.
		*
		* @return
		*	El flujo obtenido luego de la transformación.
		*
		* @throws PipelineBrokenException
		*	En caso de que no se haya podido inyectar el flujo en el
		*	<i>pipeline</i>.
		*
		*/
		@SuppressWarnings({"unchecked"})
		public default <T extends Flow, U extends Flow> U injectIn(
				final Pipelinable<T, U> pipe)
				throws PipelineBrokenException {
			return pipe.inject((T) this);
		}
	}
