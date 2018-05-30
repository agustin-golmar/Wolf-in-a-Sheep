
	package ar.nadezhda.crypt.core.pipe;

	import java.io.UnsupportedEncodingException;
	import java.nio.ByteBuffer;
	import java.nio.ByteOrder;

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
				protected final long [] size = {-1, 0};
				protected final boolean [] loaded = {false};

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume((k, p) -> {
						if (0 < size[0]) {
							drainer.drain(k - 4, p);
							--size[0];
						}
						else if (sizeBuffer.hasRemaining()) sizeBuffer.put(p);
						else if (size[0] < 0) {
							sizeBuffer.flip();
							size[0] = sizeBuffer.getInt();
							size[1] = size[0];
							if (0 < size[0]) {
								drainer.drain(k - 4, p);
								--size[0];
							}
						}
						else if (p != 0) {
							if (extension.hasRemaining()) {
								extension.put(p);
							}
						}
						else loaded[0] = true;
					});
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted()
						|| (size[0] == 0 && loaded[0]);
				}

				@Override
				public long getSize() {
					return size[1];
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
