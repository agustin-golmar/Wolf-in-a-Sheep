
	package ar.nadezhda.crypt.interfaces;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;

	public interface Steganographer
		extends Mergeable<Flow, Flow, Flow>, Pipelinable<Flow, RegisteredFlow> {

		public long bytesNeededFor(final BoundedFlow payload);
		public long availableSpace(final BoundedFlow carrier);

		public default Flow hide(final Flow payload, final Flow carrier)
				throws PipelineBrokenException {
			return merge(carrier).inject(payload);
		}

		//public Flow unhide(final Flow carrier);
	}
