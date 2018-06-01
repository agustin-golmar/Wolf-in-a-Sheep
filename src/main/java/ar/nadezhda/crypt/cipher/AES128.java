
	package ar.nadezhda.crypt.cipher;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;

	public class AES128 implements Cipher {

		@Override
		public Flow inject(final BoundedFlow flow)
				throws PipelineBrokenException {
			return new Flow() {

				/*
				 * Necesita que sea Bounded para extraer el tamaño final,
				 * y así agregarlo al principio del flujo.
				 */

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume(drainer);
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted();
				}
			};
		}
	}
