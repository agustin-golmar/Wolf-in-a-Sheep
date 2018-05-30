
	package ar.nadezhda.crypt.interfaces;

	public interface BitmapFlow extends BoundedFlow {

		public static final String SIGNATURE = "BM";
		public static final int HEADER_SIZE = 54;

		public String getSignature();

		public short getBits();
		public int getHeight();
		public int getWidth();

		public boolean isCompressed();

		public default int getRowPaddingInBytes() {
			return getRowSizeInBytes() - getBits() * getWidth() / 8;
		}

		public default int getRowSizeInBytes() {
			return 4 * Math.floorDiv(getBits() * getWidth() + 31, 32);
		}
	}
