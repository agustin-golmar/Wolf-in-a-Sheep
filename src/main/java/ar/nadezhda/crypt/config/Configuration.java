
	package ar.nadezhda.crypt.config;

	import java.lang.reflect.Constructor;
	import java.lang.reflect.InvocationTargetException;
	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
	import java.security.NoSuchAlgorithmException;
	import java.util.Optional;

	import com.beust.jcommander.Parameter;
	import com.beust.jcommander.ParameterException;

	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.core.pipe.DecryptedPipe;
	import ar.nadezhda.crypt.core.pipe.EncryptedPipe;
	import ar.nadezhda.crypt.core.pipe.IdentityPipe;
	import ar.nadezhda.crypt.factory.CipherFactory;
	import ar.nadezhda.crypt.factory.CipherModeFactory;
	import ar.nadezhda.crypt.factory.SteganographerFactory;
	import ar.nadezhda.crypt.interfaces.BitmapFlow;
	import ar.nadezhda.crypt.interfaces.BoundedFlow;
	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.CipherMode;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.interfaces.Mergeable;
	import ar.nadezhda.crypt.interfaces.Pipelinable;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;
	import ar.nadezhda.crypt.interfaces.Steganographer;
	import ar.nadezhda.crypt.support.Message;

	public class Configuration {

		@Parameter(
			order = 0,
			names = "-embed",
			description = Message.EMBED_DESC
		)
		protected boolean embed;

		@Parameter(
			order = 1,
			names = "-extract",
			description = Message.EXTRACT_DESC
		)
		protected boolean extract;

		@Parameter(
			order = 2,
			names = "-in",
			description = Message.IN_DESC
		)
		protected String inputFilename;

		@Parameter(
			order = 3,
			names = "-p",
			required = true,
			description = Message.P_DESC
		)
		protected String carrierFilename;

		@Parameter(
			order = 4,
			names = "-out",
			required = true,
			description = Message.OUT_DESC
		)
		protected String outputFilename;

		@Parameter(
			order = 5,
			names = "-steg",
			converter = SteganographerFactory.class,
			required = true,
			description = Message.STEG_DESC
		)
		protected Optional<Steganographer> steganographer;

		@Parameter(
			order = 6,
			names = "-a",
			converter = CipherFactory.class,
			description = Message.A_DESC
		)
		protected Optional<Constructor<Cipher>> cipher;

		@Parameter(
			order = 7,
			names = "-m",
			converter = CipherModeFactory.class,
			description = Message.M_DESC
		)
		protected Optional<CipherMode> mode;

		@Parameter(
			order = 8,
			names = "-pass",
			password = false,
			description = Message.PASS_DESC
		)
		protected String password;

		public Configuration() {
			this.steganographer = SteganographerFactory.get("LSB1");
			this.cipher = CipherFactory.get("aes128");
			this.mode = CipherModeFactory.get("cbc");
		}

		public Configuration validate() {
			if (embed && extract)
				throw new ParameterException(Message.ONLY_ONE_ERROR);
			if (!embed && !extract)
				throw new ParameterException(Message.OPERATION_NEEDED_ERROR);
			if (embed && inputFilename == null)
				throw new ParameterException(Message.INPUT_NEEDED_ERROR);
			if (!cipher.isPresent())
				throw new ParameterException(Message.UNKNOWN_CIPHER_ERROR);
			if (cipher.isPresent() && password != null && password.isEmpty())
				throw new ParameterException(Message.EMPTY_PASSWORD);
			if (!mode.isPresent())
				throw new ParameterException(Message.UNKNOWN_MODE_ERROR);
			if (!steganographer.isPresent())
				throw new ParameterException(Message.UNKNOWN_STEGANOGRAPHER_ERROR);
			return this;
		}

		public boolean isEmbed() {
			return embed;
		}

		public boolean isExtract() {
			return extract;
		}

		public String getInputFilename() {
			return inputFilename;
		}

		public String getCarrierFilename() {
			return carrierFilename;
		}

		public String getOutputFilename() {
			return outputFilename;
		}

		public Optional<Steganographer> getSteganographer() {
			return steganographer;
		}

		public Optional<Constructor<Cipher>> getCipher() {
			return cipher;
		}

		public Optional<CipherMode> getMode() {
			return mode;
		}

		public String getPassword() {
			return password;
		}

		public Mergeable<RegisteredFlow, BoundedFlow, Flow> getSteganographerMerger() {
			return steganographer.get();
		}

		public Pipelinable<BitmapFlow, RegisteredFlow> getSteganographerPipe() {
			return steganographer.get();
		}

		public <T extends RegisteredFlow> Pipelinable<T, T> getEncryptedPipe()
				throws InvalidKeyException, NoSuchAlgorithmException,
					InvalidAlgorithmParameterException, PipelineBrokenException {
			if (password == null || password.isEmpty()) {
				return new IdentityPipe<T>();
			}
			else {
				try {
					final Cipher cipher = getCipher().get()
							.newInstance(mode.get(), password);
					return new EncryptedPipe<T>(cipher);
				}
				catch (final InstantiationException
						| IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException exception) {
					exception.printStackTrace();
					throw new PipelineBrokenException(
						"Can't build the pipeline for encryption.");
				}
			}
		}

		public <T extends Flow> Pipelinable<T, T> getDecryptedPipe()
				throws PipelineBrokenException, InvalidKeyException,
					InvalidAlgorithmParameterException, NoSuchAlgorithmException {
			if (password == null || password.isEmpty()) {
				return new IdentityPipe<T>();
			}
			else {
				try {
					final Cipher cipher = getCipher().get()
							.newInstance(mode.get(), password);
					return new DecryptedPipe<T>(cipher);
				}
				catch (final InstantiationException
						| IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException exception) {
					exception.printStackTrace();
					throw new PipelineBrokenException(
						"Can't build the pipeline for decryption.");
				}
			}
		}

		@Override
		public String toString() {
			return new StringBuffer()
					.append("Configuration:\n")
					.append("\tSteganographer: ")
					.append(steganographer)
					.append("\n\tInput: ")
					.append(inputFilename)
					.append("\n\tCarrier: ")
					.append(carrierFilename)
					.append("\n\tOutput: ")
					.append(outputFilename)
					.append("\n\tCipher: ")
					.append(cipher)
					.append("\n\tMode: ")
					.append(mode)
					.append("\n\tPassword: ")
					.append(password)
					.toString();
		}
	}
