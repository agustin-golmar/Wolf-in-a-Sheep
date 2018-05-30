
	package ar.nadezhda.crypt.interfaces;

	public interface Mergeable
		<T extends Flow, U extends Flow, V extends Flow> {

		public Pipelinable<U, V> merge(final T flow);
	}
