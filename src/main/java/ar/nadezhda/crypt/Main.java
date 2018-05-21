
	package ar.nadezhda.crypt;

	import com.beust.jcommander.JCommander;
	import com.beust.jcommander.ParameterException;

	import ar.nadezhda.crypt.config.Configuration;
	import ar.nadezhda.crypt.core.Steganography;
	import ar.nadezhda.crypt.support.Message;

	public final class Main {

		public static void main(final String [] arguments) {

			System.out.println(Message.GREETING_BANNER);

			final Configuration config = new Configuration();
			final JCommander cli = JCommander.newBuilder()
				.addObject(config)
				.build();

			cli.setAllowAbbreviatedOptions(false);
			cli.setCaseSensitiveOptions(false);
			cli.setProgramName("stegobmp");

			try {
				cli.parse(arguments);
				config.validate();
				Steganography.with(config);
			}
			catch (final ParameterException exception) {
				System.out.println("\n" + exception.getMessage() + ".\n");
				cli.usage();
			}
		}
	}
