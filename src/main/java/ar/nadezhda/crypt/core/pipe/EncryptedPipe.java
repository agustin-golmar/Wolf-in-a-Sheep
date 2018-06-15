
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

		protected final Optional<IvParameterSpec> IV;
		protected final Cipher cipher;

		public EncryptedPipe(final Cipher cipher)
				throws NoSuchAlgorithmException, InvalidKeyException,
					InvalidAlgorithmParameterException {
			this.IV = cipher.getMode().needIV()?
					Optional.of(Random.IV(cipher)) :
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
			final ByteBuffer ivBuffer = ByteBuffer
					.allocate((int) cipher.getIVSizeInBytes());
			IV.ifPresent(iv -> ivBuffer.put(iv.getIV()));
			final long ivSize = cipher.getIVSizeInBytes();
			ivBuffer.flip();
			final ByteBuffer inputBlock = ByteBuffer
					.allocate(2 * cipher.getBlockSizeInBytes());
			final ByteBuffer outputBlock = inputBlock.slice();
			final long Δ = 4 + ivSize + size;
			System.out.println("Real Size: " + Δ);
			System.out.println("IV Size: " + ivSize);
			System.out.println("Payload Size: " + size);
			return (T) new RegisteredFlow() {

				/* Computar el tamaño de cifrado.
				 * Agregar el tamaño en el flujo.
				 * Enviar el IV, si existe. (Big Endian, Little?)
				 * Luego enviar el flujo encriptado.
				*/

				// Effectively-final hack:
				final MutableLong remaining = new MutableLong(size);
				final MutableBoolean available = new MutableBoolean(false);
				final int blockSize = cipher.getBlockSizeInBytes();

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					if (available.isTrue()) {
						// Hay data encriptada disponible para forwardear.
						drainer.drain(Δ - remaining.getAndDecrement(), outputBlock.get());
						if (!outputBlock.hasRemaining()) available.setFalse();
					}
					else if (sizeBuffer.hasRemaining()) {
						final int k = sizeBuffer.position();
						drainer.drain(k, sizeBuffer.get());
					}
					else if (ivBuffer.hasRemaining()) {
						final int k = ivBuffer.position();
						drainer.drain(4 + k, ivBuffer.get());
					}
					else if (!flow.isExhausted()) {
						flow.consume((k, payload) -> inputBlock.put(payload));
					}
					// Siempre intento encriptar...
					if (available.isFalse()) {
						if (inputBlock.remaining() == blockSize) {
							// El buffer se llenó. Encripto el bloque.
							inputBlock.flip();
							outputBlock.clear();
							try {
								// 9 bloques, 148 bytes totales:
								// 4 + 16 + 128 (8 bloques cifrados)
								// Los 128 son:
								// 4 + 119 + 5 (extension)
								System.out.println("Plain block         (I):" + inputBlock);
								System.out.println("Plain block         (O):" + outputBlock);
								cipher.encrypt(inputBlock, outputBlock);
								outputBlock.flip();
								System.out.println("Encrypted block     (I): " + inputBlock);
								System.out.println(">>> Encrypted block (O): " + outputBlock + "\n");
								available.setTrue();
							}
							catch (final ShortBufferException exception) {
								exception.printStackTrace();
							}
							inputBlock.clear();
						}
						else if (flow.isExhausted() && 0 < inputBlock.position()) {
							//System.out.println("LAST! Plain block         (I-last):" + inputBlock);
							inputBlock.flip();
							outputBlock.clear();
							try {
								System.out.println("Plain block         (I-last):" + inputBlock);
								System.out.println("Plain block         (O-last):" + outputBlock);
								cipher.encryptLast(inputBlock, outputBlock);
								outputBlock.flip();
								System.out.println("Encrypted block     (I-last): " + inputBlock);
								System.out.println(">>> Encrypted block (O-last): " + outputBlock + "\n");
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
							&& !ivBuffer.hasRemaining()
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
