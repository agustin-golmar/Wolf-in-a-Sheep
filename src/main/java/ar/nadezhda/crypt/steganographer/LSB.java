
	package ar.nadezhda.crypt.steganographer;

	import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableByte;
	import org.apache.commons.lang3.mutable.MutableInt;
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
			protected final MutableInt remain = new MutableInt(HIDING_FACTOR - 1);
			protected final MutableLong ek = new MutableLong(0);

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

			protected final MutableBoolean available = new MutableBoolean(false);

			@Override
			public void consume(final Drainer drainer)
					throws ExhaustedFlowException {
				/* [FLUJO DIRECTO]
				 * Consumir 1 byte del carrier hasta que quede vacío.
				 * Si no hay que usarlo se forwardea.
				 * Si hay que usarlo se computa el shifting.
				 * Se verifica que el payload se haya enviado o no.
				 *		En esta instancia, no siempre se lee algo del payload.
				 * Si no se envió, se lee otro byte del payload.
				 * Se oculta el byte y se forwardea.
				 * 
				 */
				/* [FLUJO INVERTIDO]
				 * Se consume el payload si se necesita.
				 * Si no hay nada no se hace nada.
				 * Si hay algo se procede con LSB.
				 * Si el payload queda exhausto, se continúa a descargar el carrier.
				 */
				// HIDING_FACTOR = 1 (cantidad de bytes de carrier por payload)
				// target = 0
				// remain = HIDING_FACTOR - 1 = 0 (LSB8)
				// ek = 0 (effective k, es decir, sin el header de 54 bytes)

				// Manejar el remain! No hay filtro del header!
				if (available.isTrue()) {
					System.out.println("available.isTrue()");
					// target tiene un byte que no fue consumido por completo
					final long shift = ek.longValue() % HIDING_FACTOR;
					final byte hiding = (byte) (target.byteValue() >> (MAX_SHIFT - shift * SHIFT_FACTOR));
					carrier.consume((k, p) -> {
						if (!filter.test(p) || k < BitmapFlow.HEADER_SIZE) {
							// Se debería poder ignorar cierta parte (pero
							// dejar que pase), en lugar de hacer esto...
							drainer.drain(k, p);
							return;
						}
						System.out.println("\tcarrier.consume(...)");
						drainer.drain(k, (byte) ((p & MASK) | (hiding & HIDING_MASK)));
						ek.increment();

						// Ya lo enviamos. Es cierto solo en LSB8.
						if (shift == HIDING_FACTOR - 1) {
							available.setFalse();
							System.out.println("Last Shift: " + shift);
						}
						else {
							System.out.println("Shift: " + shift);
						}
					});
				}
				else {
					//System.out.println("available.isFalse()");
					// Se necesita más payload...
					if (!payload.isExhausted()) {
						payload.consume((kp, pp) -> {
							System.out.println("\tpayload.consume(" + kp + ", " + pp + ")");
							target.setValue(pp);
							available.setTrue();
						});
					}
					else {
						//System.out.println("PAYLOAD EXHAUSTED!!! Go with the rest!");
						//System.out.println("\tpayload.isExhausted!");
						// No hay más payload! Consumo lo que queda del carrier...
						carrier.consume((k, p) -> {
							drainer.drain(k, p);
						});
					}
				}
				/*carrier.consume((k, p) -> {
					if (!filter.test(p) || k < BitmapFlow.HEADER_SIZE) {
						// Se debería poder ignorar cierta parte (pero
						// dejar que pase), en lugar de hacer esto...
						drainer.drain(k, p);
						return;
					}
					final long shift = ek.longValue() % HIDING_FACTOR;
					final boolean exhausted = payload.isExhausted();
					System.out.println("ENTER: isExhausted? : " + payload.isExhausted());
					if (shift == 0) {
						if (!exhausted) {
							// Necesito más payload y todavía hay disponible!
							// Como hay disponible, le puedo pedir hasta que me lo de.
							// ...
							try {
								final MutableBoolean newByte = new MutableBoolean(false);
								while (newByte.isFalse()) {
									payload.consume((kp, pp) -> {
										target.setValue(pp);
										newByte.setTrue();
									});
									System.out.println("\tLoop...");
									System.out.println("\t\tisExhausted? : " + payload.isExhausted());
								}
								System.out.println("Payload loaded! -> (" + target.byteValue() + ")");
								System.out.println("\tisExhausted? : " + payload.isExhausted());
								// Algo se cargó!
							}
							catch (final ExhaustedFlowException ignored) {
								// Ya se controla más arriba.
							}
						}
					}
					if (exhausted && 0 == remain.intValue()) {
						drainer.drain(k, p);
					}
					else {
						if (exhausted) remain.decrement();
						final byte hiding = (byte) (target.byteValue() >> (MAX_SHIFT - shift * SHIFT_FACTOR));
						drainer.drain(k, (byte) ((p & MASK) | (hiding & HIDING_MASK)));
					}
					ek.increment();
				});*/
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
