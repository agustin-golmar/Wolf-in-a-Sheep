
	package ar.nadezhda.crypt.cipher;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.core.exception.PipelineBrokenException;
	import ar.nadezhda.crypt.interfaces.Cipher;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.RegisteredFlow;

	public class AES256 implements Cipher {

		public AES256() {
		}

		@Override
		public RegisteredFlow inject(final RegisteredFlow flow)
				throws PipelineBrokenException {
			return new RegisteredFlow() {

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
