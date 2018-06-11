
	package ar.nadezhda.crypt.support;

	import java.lang.reflect.Constructor;
	import java.lang.reflect.InvocationTargetException;

	public class ClassBuilder {

		public static Object getInstance(final String name)
				throws ClassNotFoundException {
			try {
				return Class.forName(name)
						.getDeclaredConstructor()
						.newInstance();
			}
			catch (final InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException exception) {
				throw new ClassNotFoundException("", exception);
			}
		}

		public static Constructor<?> getConstructor(
				final String name, final Class<?> ... classes)
				throws ClassNotFoundException {
			try {
				return Class.forName(name)
						.getDeclaredConstructor(classes);
			}
			catch (final IllegalArgumentException
					| NoSuchMethodException
					| SecurityException exception) {
				throw new ClassNotFoundException("", exception);
			}
		}
	}
