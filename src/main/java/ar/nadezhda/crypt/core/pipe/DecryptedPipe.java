
	package ar.nadezhda.crypt.core.pipe;

	import java.nio.ByteBuffer;
	import java.nio.ByteOrder;
	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
	import java.security.NoSuchAlgorithmException;
	import java.util.Optional;

	import javax.crypto.BadPaddingException;
	import javax.crypto.IllegalBlockSizeException;
	import javax.crypto.ShortBufferException;
	import javax.crypto.spec.IvParameterSpec;

	import org.apache.commons.lang3.mutable.MutableBoolean;
	import org.apache.commons.lang3.mutable.MutableLong;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.support.Message;
	import ar.nadezhda.crypt.support.Random;

	public class DecryptedPipe<T extends Flow>
		implements Pipelinable<T, T> {

		protected final Optional<IvParameterSpec> IV;
		protected final Cipher cipher;

		public DecryptedPipe(final Cipher cipher)
				throws InvalidKeyException, InvalidAlgorithmParameterException,
					NoSuchAlgorithmException {
			this.IV = cipher.getMode().needIV()?
					Optional.of(Random.IVfromKey(cipher)) :
					Optional.empty();
			this.cipher = cipher.onDecrypt(IV);
		}

		@Override
		@SuppressWarnings("unchecked")
		public T inject(final T flow)
				throws PipelineBrokenException {
			final ByteBuffer sizeBuffer = ByteBuffer
					.allocate(4)
					.order(ByteOrder.BIG_ENDIAN);
			final ByteBuffer inputBlock = ByteBuffer
					.allocate(EncryptedPipe.INPUT_BLOCKS * cipher.getBlockSizeInBytes());
			final ByteBuffer outputBlock = ByteBuffer
					.allocate((1 + EncryptedPipe.INPUT_BLOCKS) * cipher.getBlockSizeInBytes());
			return (T) new Flow() {

				// Effectively-final hack:
				protected final MutableLong remaining = new MutableLong(-1);
				protected final MutableBoolean available = new MutableBoolean(false);
				protected final MutableLong index = new MutableLong(0);
				protected final MutableBoolean lastBlock = new MutableBoolean(false);
				protected final MutableBoolean finish = new MutableBoolean(false);

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					if (available.isTrue() || lastBlock.isTrue()) {
						drainer.drain(index.getAndIncrement(), outputBlock.get());
						if (!outputBlock.hasRemaining()) {
							if (lastBlock.isTrue()) finish.setTrue();
							available.setFalse();
						}
					}
					else if (0 < remaining.longValue()) {
						flow.consume((k, p) -> {
							inputBlock.put(p);
							remaining.decrement();
						});
					}
					else if (sizeBuffer.hasRemaining()) {
						flow.consume((k, p) -> sizeBuffer.put(p));
					}
					else if (remaining.longValue() < 0) {
						sizeBuffer.flip();
						remaining.setValue(sizeBuffer.getInt());
					}
					if (available.isFalse() && finish.isFalse()) {
						final boolean inputIsFull =  0 < remaining.longValue()
								&& !inputBlock.hasRemaining();
						final boolean lastBytes = remaining.longValue() == 0;
						if (inputIsFull || lastBytes) {
							inputBlock.flip();
							outputBlock.clear();
							try {
								if (inputIsFull) {
									cipher.transform(inputBlock, outputBlock);
								}
								else {
									lastBlock.setTrue();
									cipher.transformLast(inputBlock, outputBlock);
								}
								outputBlock.flip();
								if (outputBlock.hasRemaining()) available.setTrue();
							}
							catch (final IllegalBlockSizeException exception) {
								throw new ExhaustedFlowException(
									Message.INVALID_CIPHER_MODE);
							}
							catch (final ShortBufferException
									| BadPaddingException exception) {
								throw new ExhaustedFlowException(exception.getMessage());
							}
							inputBlock.clear();
						}
					}
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted() && finish.isTrue();
				}
			};
		}
	}
