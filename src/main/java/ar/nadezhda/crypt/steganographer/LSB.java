
	package ar.nadezhda.crypt.steganographer;

	import java.util.function.Predicate;

	import org.apache.commons.lang3.mutable.MutableBoolean;
	import org.apache.commons.lang3.mutable.MutableByte;
	import org.apache.commons.lang3.mutable.MutableLong;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.NonMergeableFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.core.flow.EmptyFlow;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.interfaces.Steganographer;
	import ar.nadezhda.crypt.support.Message;

	public class LSB implements Steganographer {

		protected final Predicate<Byte> filter;
		protected final int HIDING_FACTOR;
		protected final int SHIFT_FACTOR;
		protected final int MAX_SHIFT;
		protected final int HIDING_MASK;
		protected final int MASK;

		public LSB(final int hidingFactor, final int hidingMask) {
			this(hidingFactor, hidingMask, payload -> true);
		}

		public LSB(
				final int hidingFactor, final int hidingMask,
				final Predicate<Byte> filter) {
			this.filter = filter;
			this.HIDING_FACTOR = hidingFactor;
			this.SHIFT_FACTOR = 8 / hidingFactor;
			this.MAX_SHIFT = 8 - SHIFT_FACTOR;
			this.HIDING_MASK = hidingMask;
			this.MASK = ~hidingMask;
		}

		@Override
		public long bytesNeededFor(final BoundedFlow payload) {
			return HIDING_FACTOR * payload.getSize();
		}

		@Override
		public long availableSpace(final RegisteredFlow carrier) {
			return (carrier.getSize() - BitmapFlow.HEADER_SIZE) / HIDING_FACTOR;
		}

		@Override
		public Pipelinable<BoundedFlow, Flow> merge(final RegisteredFlow flow) {
			return payload -> {
				try {
					return new MergedFlow(payload, flow);
				}
				catch (final NonMergeableFlowException exception) {
					System.err.println(exception.getMessage());
					return new EmptyFlow();
				}
			};
		}

		protected class MergedFlow implements Flow {

			// Effectively-final hack:
			protected final MutableByte target = new MutableByte(0);
			protected final MutableLong ek = new MutableLong(0);
			protected final MutableBoolean available = new MutableBoolean(false);

			protected final BoundedFlow payload;
			protected final BoundedFlow carrier;

			public MergedFlow(final BoundedFlow payload, final RegisteredFlow carrier)
					throws NonMergeableFlowException {
				this.payload = payload;
				this.carrier = carrier;
				if (availableSpace(carrier) < payload.getSize()) {
					throw new NonMergeableFlowException(
						Message.SHEEP_OVERFLOW_ERROR(
							availableSpace(carrier), bytesNeededFor(payload)));
				}
			}

			@Override
			public void consume(final Drainer drainer)
					throws ExhaustedFlowException {
				if (available.isTrue()) {
					final long shift = ek.longValue() % HIDING_FACTOR;
					final byte hiding = (byte) (target.byteValue() >> (MAX_SHIFT - shift * SHIFT_FACTOR));
					carrier.consume((k, p) -> {
						if (!filter.test(p) || k < BitmapFlow.HEADER_SIZE) {
							// Se debería poder ignorar cierta parte (pero
							// dejar que pase), en lugar de hacer esto...
							drainer.drain(k, p);
							return;
						}
						drainer.drain(k, (byte) ((p & MASK) | (hiding & HIDING_MASK)));
						ek.increment();
						if (shift == HIDING_FACTOR - 1) available.setFalse();
					});
				}
				else {
					if (!payload.isExhausted()) {
						payload.consume((kp, pp) -> {
							target.setValue(pp);
							available.setTrue();
						});
					}
					else {
						carrier.consume((k, p) -> {
							drainer.drain(k, p);
						});
					}
				}
			}

			@Override
			public boolean isExhausted() {
				return carrier.isExhausted();
			}

			@Override
			public String toString() {
				return "";
			}
		}

		@Override
		public RegisteredFlow inject(final BitmapFlow flow)
				throws PipelineBrokenException {
			return new RegisteredFlow() {

				// Effectively-final hack:
				protected final MutableByte target = new MutableByte(0);
				protected final MutableLong ek = new MutableLong(0);

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume((k, p) -> {
						if (!filter.test(p) || k < BitmapFlow.HEADER_SIZE) return;
						final long shift = ek.longValue() % HIDING_FACTOR;
						target.setValue(target.byteValue()
							| (p & HIDING_MASK) << (MAX_SHIFT - shift * SHIFT_FACTOR));
						if (HIDING_FACTOR - shift == 1) {
							final long ku = ek.longValue() / HIDING_FACTOR;
							drainer.drain(ku, target.byteValue());
							target.setValue(0);
						}
						ek.increment();
					});
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted();
				}

				@Override
				public long getSize() {
					return flow.getSize();
				}

				@Override
				public String getName() {
					return flow.getName();
				}
			};
		}
	}
