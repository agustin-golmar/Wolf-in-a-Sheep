
	package ar.nadezhda.crypt.core.flow;

	import ar.nadezhda.crypt.core.exception.ExhaustedFlowException;
	import ar.nadezhda.crypt.interfaces.Drainer;
	import ar.nadezhda.crypt.interfaces.Flow;
	import ar.nadezhda.crypt.support.Message;

	public class EmptyFlow implements Flow {

		@Override
		public void consume(final Drainer drainer)
				throws ExhaustedFlowException {
			throw new ExhaustedFlowException(Message.EMPTY_FLOW);
		}

		@Override
		public boolean isExhausted() {
			return true;
		}
	}
