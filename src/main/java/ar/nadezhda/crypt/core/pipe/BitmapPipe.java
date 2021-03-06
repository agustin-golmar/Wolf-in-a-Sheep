
	package ar.nadezhda.crypt.core.pipe;

	import java.nio.ByteBuffer;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.support.Message;

	public class BitmapPipe<T extends RegisteredFlow>
		implements Pipelinable<T, BitmapFlow> {

		@Override
		public BitmapFlow inject(final T flow)
				throws PipelineBrokenException {
			final ByteBuffer header = ByteBuffer.allocate(BitmapFlow.HEADER_SIZE);
			final ByteBuffer headerView = header.slice();
			try {
				for (int i = 0; i < BitmapFlow.HEADER_SIZE; ++i) {
					flow.consume((k, p) -> header.put(p));
				}
				header.flip();
			}
			catch (final ExhaustedFlowException exception) {
				throw new PipelineBrokenException(
					Message.UNSUPPORTED_BITMAP_FORMAT);
			}
			return new BitmapFlow() {

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					if (headerView.hasRemaining()) {
						final long k = headerView.position();
						drainer.drain(k, headerView.get());
					}
					else {
						flow.consume((k, payload) -> {
							drainer.drain(k, payload);
						});
					}
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted();
				}

				@Override
				public long getSize() {
					return Header.SIZE.getDWord(header);
				}

				@Override
				public String getSignature() {
					final int signature = Header.SIGNATURE.getWord(header);
					return String.valueOf(new char [] {
						(char) (signature & 0xFF),
						(char) ((signature & 0xFF00) >> 8)
					});
				}

				@Override
				public short getBits() {
					return Header.BITS.getWord(header);
				}

				@Override
				public int getHeight() {
					return Header.HEIGHT.getDWord(header);
				}

				@Override
				public int getWidth() {
					return Header.WIDTH.getDWord(header);
				}

				@Override
				public boolean isCompressed() {
					return Header.COMPRESSION.getDWord(header) != 0;
				}

				@Override
				public String toString() {
					return new StringBuilder(256)
							.append("\t          Size : ")
							.append(getSize())
							.append(" bytes\n\t   Compression : ")
							.append(isCompressed())
							.append("\n\t   Color Depth : ")
							.append(getBits())
							.append(" bits\n\t    Resolution : ")
							.append(getWidth())
							.append("x")
							.append(getHeight())
							.append(" pixels\n\t      Row Size : ")
							.append(getRowSizeInBytes())
							.append(" bytes\n\t   Row Padding : ")
							.append(getRowPaddingInBytes())
							.append(" bytes")
							.toString();
				}

				@Override
				public String getName() {
					return flow.getName();
				}
			};
		}

		public enum Header {

			SIGNATURE		(0, 2),
			SIZE			(2, 4),
			BITMAP			(10, 4),
			WIDTH			(18, 4),
			HEIGHT			(22, 4),
			BITS			(28, 2),
			COMPRESSION		(30, 4);

			protected final int offset;
			protected final int size;

			private Header(final int offset, final int size) {
				this.offset = offset;
				this.size = size;
			}

			public short getWord(final ByteBuffer buffer) {
				short data = 0;
				for (int i = 0; i < size; ++i) {
					data |= (buffer.get(i + offset) & 0xFF) << (8 * i);
				}
				return data;
			}

			public int getDWord(final ByteBuffer buffer) {
				int data = 0;
				for (int i = 0; i < size; ++i) {
					data |= (buffer.get(i + offset) & 0xFF) << (8 * i);
				}
				return data;
			}
		}
	}
