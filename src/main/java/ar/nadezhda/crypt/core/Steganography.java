
	package ar.nadezhda.crypt.core;

	import java.io.IOException;

	import ar.nadezhda.crypt.config.Configuration;
	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.core.flow.FileFlow;
	import ar.nadezhda.crypt.core.pipe.BitmapPipe;
	import ar.nadezhda.crypt.core.pipe.BitmapValidationPipe;
	import ar.nadezhda.crypt.core.pipe.MetadataPipe;
	import ar.nadezhda.crypt.core.pipe.OutputPipe;

	public class Steganography {

		public static void with(final Configuration config) {
			try {
				if (config.isEmbed()) {
					embed(config);
				}
				else {
					extract(config);
				}
			}
			catch (final PipelineBrokenException
					| IOException
					| ExhaustedFlowException exception) {
				System.out.println(exception.getMessage());
				exception.printStackTrace();
			}
		}

		protected static void embed(final Configuration config)
				throws PipelineBrokenException, IOException, ExhaustedFlowException {

			System.out.println("Piping output...");
			new FileFlow(config.getInputFilename())
				.injectIn(new MetadataPipe()
					.plug(config.getEncryptedPipe()))
				.injectIn(config.getSteganographer().get()
					.merge(new FileFlow(config.getCarrierFilename())
				.injectIn(new BitmapPipe()
					.plug(new BitmapValidationPipe())))
					.plug(new OutputPipe<>(config.getOutputFilename())))
					.flush();
			System.out.println("Done.");
		}

		protected static void extract(final Configuration config)
				throws PipelineBrokenException, IOException, ExhaustedFlowException {

			System.out.println("Piping output...");
			new FileFlow(config.getCarrierFilename())
				.injectIn(new BitmapPipe()
				.plug(new BitmapValidationPipe())
				//.plug(config.getSteganographerPipe())
				//.plug(new DecryptedPipe())
				.plug(new OutputPipe<>(config.getOutputFilename())))
				.flush();
			System.out.println("Done.");
		}
	}
