
	package ar.nadezhda.crypt.core;

	import java.io.IOException;
	import java.nio.file.NoSuchFileException;
	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
	import java.security.NoSuchAlgorithmException;

	import ar.nadezhda.crypt.config.Configuration;
	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.core.flow.FileFlow;
	import ar.nadezhda.crypt.core.pipe.BitmapPipe;
	import ar.nadezhda.crypt.core.pipe.BitmapValidationPipe;
	import ar.nadezhda.crypt.core.pipe.MetadataPipe;
	import ar.nadezhda.crypt.core.pipe.OutputPipe;
	import ar.nadezhda.crypt.core.pipe.WolfPipe;
	import ar.nadezhda.crypt.support.Message;
	import ar.nadezhda.crypt.support.Timer;

	public class Steganography {

		public static void with(final Configuration config) {
			System.out.println(config);
			final Timer timer = Timer.start();
			try {
				if (config.isEmbed()) {
					embed(config);
				}
				else {
					extract(config);
				}
			}
			catch (final NoSuchFileException exception) {
				System.out.println(
					Message.CANNOT_OPEN_FINAL_SHEEP(
						exception.getMessage()));
			}
			catch (final PipelineBrokenException
					| IOException
					| ExhaustedFlowException exception) {
				System.out.println(exception.getMessage());
				exception.printStackTrace();
			}
			catch (final InvalidKeyException
					| NoSuchAlgorithmException
					| InvalidAlgorithmParameterException exception) {
				exception.printStackTrace();
			}
			System.out.println(
				Message.FINISH_TIME(timer.getTimeInSeconds()));
		}

		protected static void embed(final Configuration config)
				throws PipelineBrokenException, IOException, ExhaustedFlowException,
					InvalidKeyException, NoSuchAlgorithmException,
					InvalidAlgorithmParameterException {

			System.out.println("Piping output...");
			new FileFlow(config.getInputFilename())
				.injectIn(new MetadataPipe<>()
					.plug(config.getEncryptedPipe()))
				.injectIn(config.getSteganographerMerger()
					.merge(new FileFlow(config.getCarrierFilename())
				.injectIn(new BitmapPipe<>()
					.plug(new BitmapValidationPipe<>())))
					.plug(new OutputPipe<>(config.getOutputFilename())))
					.flush();
			System.out.println("Done.");
		}

		protected static void extract(final Configuration config)
				throws PipelineBrokenException, IOException, ExhaustedFlowException,
					InvalidKeyException, InvalidAlgorithmParameterException,
					NoSuchAlgorithmException {

			System.out.println("Piping output...");
			new FileFlow(config.getCarrierFilename())
				.injectIn(new BitmapPipe<>()
					.plug(new BitmapValidationPipe<>())
					.plug(config.getSteganographerPipe())
					.plug(config.getDecryptedPipe())
					.plug(new WolfPipe<>())
					.plug(new OutputPipe<>(config.getOutputFilename())))
					.flush();
			System.out.println("Done.");
		}
	}
