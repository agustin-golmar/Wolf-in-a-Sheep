
	package ar.nadezhda.crypt.core.pipe;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;

	public class IdentityPipe<T extends Flow>
		implements Pipelinable<T, T> {

		@Override
		public T inject(final T flow)
				throws PipelineBrokenException {
			return flow;
		}
	}
