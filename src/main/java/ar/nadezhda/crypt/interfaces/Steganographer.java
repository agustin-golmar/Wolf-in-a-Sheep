
	package ar.nadezhda.crypt.interfaces;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;

	public interface Steganographer
		extends Mergeable<BoundedFlow, BoundedFlow, Flow>, Pipelinable<BitmapFlow, RegisteredFlow> {

		public long bytesNeededFor(final BoundedFlow payload);
		public long availableSpace(final BoundedFlow carrier);

		public default Flow hide(final BoundedFlow payload, final BoundedFlow carrier)
				throws PipelineBrokenException {
			return merge(carrier).inject(payload);
		}

		public default Flow unhide(final BitmapFlow carrier)
				throws PipelineBrokenException {
			return carrier.injectIn(this);
		}
	}
