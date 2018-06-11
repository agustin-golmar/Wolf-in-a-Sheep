
	package ar.nadezhda.crypt.core.pipe;

	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
	import java.security.NoSuchAlgorithmException;
	import java.util.Optional;

	import javax.crypto.spec.IvParameterSpec;

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
			return (T) new RegisteredFlow() {

				/* Computar el tamaño de cifrado.
				 * Agregar el tamaño en el flujo.
				 * Enviar el IV, si existe.
				 * Luego enviar el flujo encriptado.
				*/

				@Override
				public void consume(final Drainer drainer)
						throws ExhaustedFlowException {
					flow.consume(drainer);
				}

				@Override
				public boolean isExhausted() {
					return flow.isExhausted();
				}

				@Override
				public long getSize() {
					return flow.getSize();
				}

				@Override
				public String getName() {
					return flow.getName();
				}
			};
		}
	}
