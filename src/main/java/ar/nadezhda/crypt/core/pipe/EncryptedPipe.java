
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
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.support.Random;

	public class EncryptedPipe<T extends RegisteredFlow>
		implements Pipelinable<T, T> {

		public static final int INPUT_BLOCKS = 512;

		protected final Optional<IvParameterSpec> IV;
		protected final Cipher cipher;

		public EncryptedPipe(final Cipher cipher)
				throws NoSuchAlgorithmException, InvalidKeyException,
					InvalidAlgorithmParameterException {
			this.IV = cipher.getMode().needIV()?
					Optional.of(Random.IVfromKey(cipher)) :
					Optional.empty();
			this.cipher = cipher.onEncrypt(IV);
		}

		@Override
		@SuppressWarnings("unchecked")
		public T inject(final T flow)
				throws PipelineBrokenException {
			final long size = cipher.getOutputSizeInBytes((int) flow.getSize());
			final ByteBuffer sizeBuffer = ByteBuffer
					.allocate(4)
					.order(ByteOrder.BIG_ENDIAN)
					.putInt((int) size);
			sizeBuffer.flip();
			final ByteBuffer inputBlock = ByteBuffer
					.allocate(INPUT_BLOCKS * cipher.getBlockSizeInBytes());
			final ByteBuffer outputBlock = ByteBuffer
					.allocate((INPUT_BLOCKS + 1) * cipher.getBlockSizeInBytes());
			final long Δ = 4 + size;
			return (T) new RegisteredFlow() {

				// Effectively-final hack:
				protected final MutableLong remaining = new MutableLong(size);
				protected final MutableBoolean available = new MutableBoolean(false);

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					if (available.isTrue()) {
						drainer.drain(Δ - remaining.getAndDecrement(), outputBlock.get());
						if (!outputBlock.hasRemaining()) available.setFalse();
					}
					else if (sizeBuffer.hasRemaining()) {
						final int k = sizeBuffer.position();
						drainer.drain(k, sizeBuffer.get());
					}
					else if (!flow.isExhausted()) {
						flow.consume((k, payload) -> inputBlock.put(payload));
					}
					if (available.isFalse()) {
						final boolean inputIsFull = !inputBlock.hasRemaining();
						final boolean lastBytes = flow.isExhausted() && 0 < inputBlock.position();
						if (inputIsFull || lastBytes) {
							inputBlock.flip();
							outputBlock.clear();
							try {
								if (inputIsFull) cipher.transform(inputBlock, outputBlock);
								else cipher.transformLast(inputBlock, outputBlock);
								outputBlock.flip();
								available.setTrue();
							}
							catch (final ShortBufferException
									| IllegalBlockSizeException
									| BadPaddingException exception) {
								exception.printStackTrace();
							}
							inputBlock.clear();
						}
					}
				}

				@Override
				public boolean isExhausted() {
					return remaining.longValue() == 0
							&& !sizeBuffer.hasRemaining();
				}

				@Override
				public long getSize() {
					return Δ;
				}

				@Override
				public String getName() {
					return flow.getName();
				}
			};
		}
	}
