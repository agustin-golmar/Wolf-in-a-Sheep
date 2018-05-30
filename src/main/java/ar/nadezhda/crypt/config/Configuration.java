
	package ar.nadezhda.crypt.config;

	import java.util.Optional;

	import com.beust.jcommander.Parameter;
	import com.beust.jcommander.ParameterException;

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
		protected Optional<Cipher> cipher;

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

		public Optional<Cipher> getCipher() {
			return cipher;
		}

		public Optional<CipherMode> getMode() {
			return mode;
		}

		public String getPassword() {
			return password;
		}

		public Mergeable<Flow, Flow, Flow> getSteganographerMerger() {
			return steganographer.get();
		}

		public Pipelinable<BitmapFlow, RegisteredFlow> getSteganographerPipe() {
			return steganographer.get();
		}

		public Pipelinable<BoundedFlow, Flow> getEncryptedPipe() {
			return password == null || password.isEmpty()?
					new IdentityPipe<BoundedFlow>() : getCipher().get();
		}
	}
