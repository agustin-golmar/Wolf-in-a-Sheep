
	package ar.nadezhda.crypt.core.flow;

	import java.io.IOException;
	import java.nio.ByteBuffer;
	import java.nio.channels.FileChannel;
	import java.nio.file.Paths;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.support.Message;

		/**
		* <p>Un flujo de datos que proviene de un archivo de sólo lectura. El
		* flujo mantiene un buffer estático con el cual reduce
		* significativamente la cantidad de accesos a disco (ya que el flujo
		* es consumido byte a byte).</p>
		*/

	public class FileFlow implements RegisteredFlow {

		public static final int BUFFER_SIZE = 8192;

		protected final FileChannel channel;
		protected final ByteBuffer buffer;
		protected final String filename;
		protected final long size;

		protected long k;

		public FileFlow(final String filename) throws IOException {
			this.channel = FileChannel.open(Paths.get(filename));
			this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
			this.filename = filename;
			this.size = channel.size();
			this.buffer.flip();
			this.k = 0;
		}

		@Override
		public void consume(final Drainer drainer)
				throws ExhaustedFlowException {
			try {
				if (buffer.hasRemaining()) {
					drainer.drain(k++, buffer.get());
				}
				else {
					buffer.clear();
					if (channel.read(buffer) < 0) {
						channel.close();
					}
					buffer.flip();
					if (buffer.hasRemaining()) {
						drainer.drain(k++, buffer.get());
					}
				}
			}
			catch (final IOException exception) {
				throw new ExhaustedFlowException(
					Message.UNKNOWN_INPUT_ERROR(filename));
			}
		}

		@Override
		public boolean isExhausted() {
			return !channel.isOpen() || k == size;
		}

		@Override
		public long getSize() {
			return size;
		}

		@Override
		public String getName() {
			return filename;
		}
	}
