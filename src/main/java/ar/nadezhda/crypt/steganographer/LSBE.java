
	package ar.nadezhda.crypt.steganographer;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.interfaces.Steganographer;

	public class LSBE implements Steganographer {

		@Override
		public long bytesNeededFor(final BoundedFlow payload) {
			return 0;
		}

		@Override
		public long availableSpace(final BoundedFlow carrier) {
			return 0;
		}

		@Override
		public Pipelinable<BoundedFlow, Flow> merge(final BoundedFlow flow) {
			return null;
		}

		@Override
		public RegisteredFlow inject(final BitmapFlow flow)
				throws PipelineBrokenException {
			return null;
		}
	}
