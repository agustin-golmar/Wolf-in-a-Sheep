
	package ar.nadezhda.crypt.core.pipe;

	import java.io.UnsupportedEncodingException;
	import java.nio.ByteBuffer;
	import java.nio.ByteOrder;

	import org.apache.commons.lang3.mutable.MutableBoolean;
	import org.apache.commons.lang3.mutable.MutableLong;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;

	public class WolfPipe<T extends Flow>
		implements Pipelinable<T, RegisteredFlow> {

		@Override
		public RegisteredFlow inject(final T flow)
				throws PipelineBrokenException {
			final ByteBuffer extension = ByteBuffer.allocate(256);
			final ByteBuffer sizeBuffer = ByteBuffer
					.allocate(4)
					.order(ByteOrder.BIG_ENDIAN);
			return new RegisteredFlow() {

				// Effectively-final hack:
				protected final MutableLong remaining = new MutableLong(-1);
				protected final MutableLong size = new MutableLong(0);
				protected final MutableBoolean loaded = new MutableBoolean(false);

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume((k, p) -> {
						if (0 < remaining.longValue()) {
							drainer.drain(k - 4, p);
							remaining.decrement();
						}
						else if (sizeBuffer.hasRemaining()) {
							sizeBuffer.put(p);
						}
						else if (remaining.longValue() < 0) {
							sizeBuffer.flip();
							remaining.setValue(sizeBuffer.getInt());
							size.setValue(remaining.longValue());
							if (0 < remaining.longValue()) {
								drainer.drain(k - 4, p);
								remaining.decrement();
							}
						}
						else if (p != 0) {
							if (extension.hasRemaining()) {
								extension.put(p);
							}
						}
						else loaded.setTrue();
					});
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted()
						|| (remaining.longValue() == 0 && loaded.isTrue());
				}

				@Override
				public long getSize() {
					return size.longValue();
				}

				@Override
				public String getName() {
					try {
						final byte [] view = new byte [extension.flip().limit()];
						extension.get(view).compact().put(view);
						return new String(view, "UTF-8");
					}
					catch (final UnsupportedEncodingException exception) {
						return "";
					}
				}

				@Override
				public String toString() {
					return getName();
				}
			};
		}
	}
