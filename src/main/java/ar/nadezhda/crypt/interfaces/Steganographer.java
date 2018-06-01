
	package ar.nadezhda.crypt.interfaces;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;

	public interface Steganographer
		extends Mergeable<RegisteredFlow, BoundedFlow, Flow>, Pipelinable<BitmapFlow, RegisteredFlow> {

		public long bytesNeededFor(final BoundedFlow payload);
		public long availableSpace(final RegisteredFlow carrier);

		public default Flow hide(final BoundedFlow payload, final RegisteredFlow carrier)
				throws PipelineBrokenException {
			return merge(carrier).inject(payload);
		}

		public default Flow unhide(final BitmapFlow carrier)
				throws PipelineBrokenException {
			return carrier.injectIn(this);
		}
	}
