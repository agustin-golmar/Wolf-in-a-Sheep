
	package ar.nadezhda.crypt.core.exception;

	import ar.nadezhda.crypt.support.Message;

	public class ExhaustedFlowException extends Exception {

		public ExhaustedFlowException(final String message) {
			super(Message.EXHAUSTED_FLOW_EXCEPTION(message));
		}

		private static final long serialVersionUID
			= -4075034086138994045L;
	}
