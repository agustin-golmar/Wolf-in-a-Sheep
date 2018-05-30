
	package ar.nadezhda.crypt.core.flow;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;

	public class EmptyFlow implements Flow {

		@Override
		public void consume(final Drainer drainer)
				throws ExhaustedFlowException {
			throw new ExhaustedFlowException(
				"This is an empty flow. You can't consume it.");
		}

		@Override
		public boolean isExhausted() {
			return true;
		}
	}
