
	package ar.nadezhda.crypt.steganographer;

	import java.io.IOException;
	import java.nio.ByteBuffer;
	import java.nio.channels.FileChannel;
	import java.nio.file.Paths;
	import java.util.function.Predicate;

	import ar.nadezhda.crypt.core.flow.FileFlow;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.support.Message;

	public class LSBE extends LSB {

		protected long availableSpace;

		protected static final Predicate<Byte> filter
			= payload -> payload == (byte) 254 || payload == (byte) 255;

		public LSBE() {
			super(8, 0x01, filter);
			this.availableSpace = -1;
		}

		@Override
		public long availableSpace(final RegisteredFlow carrier) {
			if (availableSpace < 0) {
				availableSpace = 0;
				try {
					final ByteBuffer buffer = ByteBuffer.allocate(FileFlow.BUFFER_SIZE);
					final FileChannel channel = FileChannel
							.open(Paths.get(carrier.getName()))
							.position(BitmapFlow.HEADER_SIZE);
					while (0 <= channel.read(buffer)) {
						for (int i = 0; i < buffer.limit(); ++i) {
							if (filter.test(buffer.get(i))) {
								++availableSpace;
							}
						}
						buffer.clear();
					}
				}
				catch (final IOException exception) {
					availableSpace = Long.MAX_VALUE;
					System.out.println(Message.CANNOT_GET_AVAILABLE_SPACE);
					exception.printStackTrace();
				}
			}
			return availableSpace;
		}
	}
