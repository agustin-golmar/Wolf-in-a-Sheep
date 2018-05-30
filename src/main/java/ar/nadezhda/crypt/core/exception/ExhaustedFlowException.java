
	package ar.nadezhda.crypt.core.exception;

	public class ExhaustedFlowException extends Exception {

		public ExhaustedFlowException(final String message) {
			super("Error al consumir el flujo de entrada ('" + message + "').");
		}

		private static final long serialVersionUID
			= -4075034086138994045L;
	}
