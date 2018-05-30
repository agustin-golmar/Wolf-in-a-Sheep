
	package ar.nadezhda.crypt.interfaces;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;

	public interface FlushableFlow extends Flow {

		public default void flush(final Drainer drainer)
				throws ExhaustedFlowException {
			while (!isExhausted()) {
				consume(drainer);
			}
		}

		public default void flush()
				throws ExhaustedFlowException {
			flush((k, payload) -> {});
		}
	}
