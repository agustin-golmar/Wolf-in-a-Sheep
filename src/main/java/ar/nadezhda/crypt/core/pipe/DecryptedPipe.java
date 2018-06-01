
	package ar.nadezhda.crypt.core.pipe;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;

	public class DecryptedPipe<T extends Flow>
		implements Pipelinable<T, BoundedFlow> {

		@Override
		public BoundedFlow inject(final T flow)
				throws PipelineBrokenException {
			return new BoundedFlow() {

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume(drainer);
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted();
				}

				@Override
				public long getSize() {
					return 0;
				}
			};
		}
	}
