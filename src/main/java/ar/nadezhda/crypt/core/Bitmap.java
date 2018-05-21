
	package ar.nadezhda.crypt.core;

	import java.io.IOException;
	import java.nio.ByteBuffer;
	import java.nio.file.Files;
	import java.nio.file.Paths;

	public class Bitmap {

		public static final String SIGNATURE = "BM";
		public static final int HEADER_SIZE = 54;

		protected final ByteBuffer header;
		protected final ByteBuffer body;

		public Bitmap(final String filename)
				throws IOException {
			final ByteBuffer bitmap = ByteBuffer
					.wrap(Files.readAllBytes(Paths.get(filename)));
			final int size = bitmap.limit();
			bitmap.limit(HEADER_SIZE);
			this.header = bitmap.slice();
			bitmap.limit(size).position(HEADER_SIZE);
			this.body = bitmap.slice();
		}

		public static Bitmap from(final String filename)
				throws IOException {
			return new Bitmap(filename);
		}

		public ByteBuffer getHeader() {
			return header;
		}

		public ByteBuffer getBody() {
			return body;
		}

		public short getBits() {
			return Header.BITS.getWord(header);
		}

		public String getFileType() {
			return String.valueOf(new char [] {
				(char) header.get(Header.FILE_TYPE.offset),
				(char) header.get(Header.FILE_TYPE.offset + 1)
			});
		}

		public int getHeight() {
			return Header.HEIGHT.getDWord(header);
		}

		public int getSize() {
			return Header.SIZE.getDWord(header);
		}

		public int getWidth() {
			return Header.WIDTH.getDWord(header);
		}

		public boolean isCompressed() {
			return Header.COMPRESSION.getDWord(header) != 0;
		}

		@Override
		public String toString() {
			return new StringBuilder(128)
					.append("\tSize: ")
					.append(getSize())
					.append(" bytes\n\tCompression: ")
					.append(isCompressed())
					.append("\n\tColor Depth: ")
					.append(getBits())
					.append(" bits\n\tResolution: ")
					.append(getWidth())
					.append("x")
					.append(getHeight())
					.append(" pixels")
					.toString();
		}

		public enum Header {

			FILE_TYPE		(0, 2),
			SIZE			(2, 4),
			BITMAP			(10, 4),
			WIDTH			(18, 4),
			HEIGHT			(22, 4),
			BITS			(28, 2),
			COMPRESSION		(30, 4);

			protected final int offset;
			protected final int size;

			private Header(final int offset, final int size) {
				this.offset = offset;
				this.size = size;
			}

			public short getWord(final ByteBuffer buffer) {
				short data = 0;
				for (int i = 0; i < size; ++i) {
					data |= (buffer.get(i + offset) & 0xFF) << (8 * i);
				}
				return data;
			}

			public int getDWord(final ByteBuffer buffer) {
				int data = 0;
				for (int i = 0; i < size; ++i) {
					data |= (buffer.get(i + offset) & 0xFF) << (8 * i);
				}
				return data;
			}
		}
	}
