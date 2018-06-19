
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
					.allocate(EncryptedPipe.INPUT_BLOCKS * cipher.getBlockSizeInBytes());
			/**/System.out.println(sizeBuffer);
			return (T) new Flow() {

				// Effectively-final hack:
				protected final MutableLong remaining = new MutableLong(-1);
				protected final MutableLong size = new MutableLong(0);
				//protected final MutableBoolean loaded = new MutableBoolean(false);
				protected final MutableBoolean available = new MutableBoolean(false);
				protected final MutableLong index = new MutableLong(0);

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume((k, p) -> { // Meter adentro!						<---- !!!
						if (available.isTrue()) {
							//remaining.decrement();
							final byte b = outputBlock.get();
							//System.out.println("Hola!");
							//System.out.print((char) b);
							drainer.drain(index.getAndIncrement(), b);	// Desencriptado!!!
							if (!outputBlock.hasRemaining()) available.setFalse();
						}
						else if (0 < remaining.longValue()) { // Encriptados!!!
							//drainer.drain(k - 4 - ivSize, p); // WTF? Sigo con el resto...
							inputBlock.put(p);
							remaining.decrement();
						}
						else if (sizeBuffer.hasRemaining()) { // Debería ser 144 (el original es 132)
							sizeBuffer.put(p);
						}
						else if (remaining.longValue() < 0) { // Después de que se cargue el size y el iv...
							sizeBuffer.flip();
							remaining.setValue(sizeBuffer.getInt());
							size.setValue(remaining.longValue());
							//System.out.println("Size charged: " + ivBuffer);
							/**/System.out.println("Size: " + size.longValue());
							if (0 < remaining.longValue()) {
								inputBlock.put(p);
								//drainer.drain(k - 4 - ivSize, p); // WTF? Para no perder el primero!
								remaining.decrement();
							}
						}
						//else loaded.setTrue();
						// Intento desencriptar:
						if (available.isFalse()) {
							final boolean inputIsFull = !inputBlock.hasRemaining();
							final boolean lastBytes = remaining.longValue() == 0 && 0 < inputBlock.position();
								/*flow.isExhausted()*/
							if (inputIsFull || lastBytes) {
								System.out.println("0 - Input: " + inputBlock);
								System.out.println("0 - Output: " + outputBlock);
								inputBlock.flip();
								outputBlock.clear();
								System.out.println("1 - Input: " + inputBlock);
								System.out.println("1 - Output: " + outputBlock);
								try {
									System.out.println("(inputIsFull, lastBytes) = (" + inputIsFull + ", " + lastBytes + ")");
									if (inputIsFull) cipher.transform(inputBlock, outputBlock);
									else cipher.transformLast(inputBlock, outputBlock);
									outputBlock.flip();
									available.setTrue();
									System.out.println("2 - Input: " + inputBlock);
									System.out.println("2 - Output: " + outputBlock + "\n");
								}
								catch (final ShortBufferException
										| IllegalBlockSizeException
										| BadPaddingException exception) {
									exception.printStackTrace();
								}
								inputBlock.clear();
							}
						}
					});
				}

				@Override
				public boolean isExhausted() { // Revisar!
					return flow.isExhausted()
							|| (remaining.longValue() == 0 && available.isFalse());//loaded.isTrue());
				}
			};
		}
	}
