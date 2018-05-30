
	package ar.nadezhda.crypt.steganographer;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.interfaces.Steganographer;

	public class LSB implements Steganographer {

		protected final int HIDING_FACTOR;
		protected final int SHIFT_FACTOR;
		protected final int HIDING_MASK;
		protected final int MASK;

		public LSB(final int hidingFactor, final int hidingMask) {
			this.HIDING_FACTOR = hidingFactor;
			this.SHIFT_FACTOR = 8 / hidingFactor;
			this.HIDING_MASK = hidingMask;
			this.MASK = ~hidingMask;
		}

		@Override
		public long bytesNeededFor(final BoundedFlow payload) {
			return HIDING_FACTOR * payload.getSize();
		}

		@Override
		public long availableSpace(final BoundedFlow carrier) {
			return carrier.getSize() / HIDING_FACTOR;
		}

		@Override
		public Pipelinable<Flow, Flow> merge(final Flow flow) {
			return payload -> new Flow() {

				// Effectively-final hack:
				protected final byte [] target = {0};
				protected final int [] remain = {HIDING_FACTOR - 1};

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume((k, p) -> {
						if (k < BitmapFlow.HEADER_SIZE) {
							drainer.drain(k, p);
							return;
						}
						final long shift = (k - BitmapFlow.HEADER_SIZE) % HIDING_FACTOR;
						final boolean exhausted = payload.isExhausted();
						if (shift == 0) {
							if (!exhausted) {
								try {
									payload.consume((kp, pp) -> {
										target[0] = pp;
									});
								}
								catch (final ExhaustedFlowException ignored) {
									// Ya se controla más arriba.
								}
							}
						}
						if (exhausted && 0 == remain[0]) {
							drainer.drain(k, p);
						}
						else {
							if (exhausted) --remain[0];
							final byte hiding = (byte) (target[0] >> (shift * SHIFT_FACTOR));
							drainer.drain(k, (byte) ((p & MASK) | (hiding & HIDING_MASK)));
						}
					});
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted();
				}
			};
		}

		@Override
		public RegisteredFlow inject(final Flow flow)
				throws PipelineBrokenException {
			return new RegisteredFlow() {

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
					/**/return 0;
				}

				@Override
				public String getName() {
					/**/return null;
				}
			};
		}
	}
