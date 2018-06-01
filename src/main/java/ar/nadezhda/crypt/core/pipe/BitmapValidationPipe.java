
	package ar.nadezhda.crypt.core.pipe;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.support.Message;

	public class BitmapValidationPipe<T extends BitmapFlow>
		implements Pipelinable<T, T> {

		@Override
		public T inject(final T flow)
				throws PipelineBrokenException {
			if (!flow.getSignature().equals(BitmapFlow.SIGNATURE))
				throw new PipelineBrokenException(Message.UNKNOWN_SIGNATURE);
			System.out.println(Message.SHEEP_PROPERTIES(flow.toString()));
			if (flow.isCompressed())
				throw new PipelineBrokenException(Message.COMPRESSED_BITMAP);
			if (flow.getBits() != 24)
				throw new PipelineBrokenException(Message.PALETTE_UNSUPPORTED);
			return flow;
		}
	}
