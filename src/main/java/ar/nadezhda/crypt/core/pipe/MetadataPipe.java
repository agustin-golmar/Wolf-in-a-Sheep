
	package ar.nadezhda.crypt.core.pipe;

	import java.io.UnsupportedEncodingException;
	import java.nio.ByteBuffer;
	import java.nio.ByteOrder;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.support.Message;

	public class MetadataPipe<T extends RegisteredFlow>
		implements Pipelinable<T, RegisteredFlow> {

		@Override
		public RegisteredFlow inject(final T flow)
				throws PipelineBrokenException {
			final long size = flow.getSize();
			final String name = flow.getName();
			final int index = name.lastIndexOf(".");
			final String extension = 0 < index? name.substring(index) : "";
			final ByteBuffer extensionBuffer = ByteBuffer.allocate(1 + extension.length());
			final ByteBuffer sizeBuffer = ByteBuffer
					.allocate(4)
					.order(ByteOrder.BIG_ENDIAN)
					.putInt((int) size);
			sizeBuffer.flip();
			try {
				extensionBuffer.put(extension.getBytes("UTF-8"))
					.put((byte) 0)
					.flip();
			}
			catch (final UnsupportedEncodingException exception) {
				exception.printStackTrace();
				throw new PipelineBrokenException(
					Message.CANNOT_ENCODE_EXTENSION(extension));
			}
			return new RegisteredFlow() {

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					if (sizeBuffer.hasRemaining()) {
						final int k = sizeBuffer.position();
						drainer.drain(k, sizeBuffer.get());
					}
					else if (!flow.isExhausted()) {
						flow.consume((k, payload) -> {
							drainer.drain(4 + k, payload);
						});
					}
					else if (extensionBuffer.hasRemaining()) {
						final int k = extensionBuffer.position();
						drainer.drain(4 + size + k, extensionBuffer.get());
					}
					else {
						throw new ExhaustedFlowException(
							flow.getClass().getName());
					}
				}

				@Override
				public boolean isExhausted() {
					return !extensionBuffer.hasRemaining();
				}

				@Override
				public long getSize() {
					return 4 + size + extensionBuffer.capacity();
				}

				@Override
				public String getName() {
					return flow.getName();
				}
			};
		}
	}
