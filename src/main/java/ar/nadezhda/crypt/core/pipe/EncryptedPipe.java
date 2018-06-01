
	package ar.nadezhda.crypt.core.pipe;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;

	public class EncryptedPipe<T extends BoundedFlow>
		implements Pipelinable<T, Flow> {

		@Override
		public Flow inject(final T flow)
				throws PipelineBrokenException {
			return new Flow() {

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
