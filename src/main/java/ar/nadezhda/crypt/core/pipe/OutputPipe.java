
	package ar.nadezhda.crypt.core.pipe;

	import java.io.IOException;
	import java.nio.ByteBuffer;
	import java.nio.channels.FileChannel;
	import java.nio.file.FileSystemException;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.nio.file.StandardCopyOption;
	import java.nio.file.StandardOpenOption;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.core.flow.FileFlow;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.FlushableFlow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.support.Message;

	public class OutputPipe<T extends Flow>
		implements Pipelinable<T, FlushableFlow> {

		protected final String filename;

		public OutputPipe(final String filename) {
			this.filename = filename;
		}

		@Override
		public FlushableFlow inject(final T flow)
				throws PipelineBrokenException {
			try {
				final ByteBuffer buffer = ByteBuffer.allocate(FileFlow.BUFFER_SIZE);
				final FileChannel channel = FileChannel.open(
						Paths.get(filename),
						StandardOpenOption.WRITE,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
				return new FlushableFlow() {

					@Override
					public void consume(final Drainer drainer)
							throws ExhaustedFlowException {
						try {
							while (!flow.isExhausted()) {
								if (buffer.hasRemaining()) {
									flow.consume((k, payload) -> {
										buffer.put(payload);
									});
								}
								else {
									buffer.flip();
									if (0 < channel.write(buffer)) {
										buffer.compact();
									}
								}
							}
							if (buffer.hasRemaining()) buffer.flip();
							while (buffer.hasRemaining()) {
								channel.write(buffer);
							}
							channel.close();
						}
						catch (final IOException exception) {
							throw new ExhaustedFlowException(
								flow.getClass().getName());
						}
					}

					@Override
					public boolean isExhausted() {
						return flow.isExhausted();
					}

					@Override
					public void flush()
							throws ExhaustedFlowException {
						consume((k, payload) -> {});
						try {
							final Path output = Paths.get(filename);
							if (Files.size(output) == 0) {
								Files.deleteIfExists(output);
							}
							else {
								Files.move(
									output,
									Paths.get(filename + flow.toString()),
									StandardCopyOption.REPLACE_EXISTING);
							}
						}
						catch (final IOException exception) {
							exception.printStackTrace();
							throw new ExhaustedFlowException(
								Message.CANNOT_GENERATE_FILE_WITH_EXTENSION(
									flow.toString()));
						}
					}
				};
			}
			catch (final FileSystemException exception) {
				throw new PipelineBrokenException(
					Message.FILE_IN_USE(filename));
			}
			catch (final IOException exception) {
				throw new PipelineBrokenException(
					Message.UNKNOWN_OUTPUT_ERROR(filename));
			}
		}
	}
