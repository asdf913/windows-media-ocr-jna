package ocr;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;
import com.google.common.reflect.Reflection;
import com.sun.jna.Pointer;

import edu.stanford.nlp.util.IntPair;
import io.github.toolfactory.narcissus.Narcissus;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

class OcrImplTest {

	private static Method METHOD_CAST, METHOD_TEST_AND_APPLY, METHOD_GET_STRING,
			METHOD_GET_AVAILABLE_RECOGNIZER_LANGUAGE_TAGS, METHOD_ADD = null;

	private static Object JNA_INSTANCE = null;

	private static Class<?> CLASS_JNA = null;

	@BeforeAll
	static void beforeAll() throws ReflectiveOperationException {
		//
		final Class<?> clz = OcrImpl.class;
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_APPLY = clz.getDeclaredMethod("testAndApply", Predicate.class, Object.class,
				FailableFunction.class, FailableFunction.class)).setAccessible(true);
		//
		(METHOD_GET_STRING = clz.getDeclaredMethod("getString", Pointer.class, Long.TYPE, String.class))
				.setAccessible(true);
		//
		(METHOD_GET_AVAILABLE_RECOGNIZER_LANGUAGE_TAGS = clz.getDeclaredMethod("getAvailableRecognizerLanguageTags",
				CLASS_JNA = Class.forName("ocr.OcrImpl$Jna"))).setAccessible(true);
		//
		(METHOD_ADD = clz.getDeclaredMethod("add", Collection.class, Object.class)).setAccessible(true);
		//
		JNA_INSTANCE = Narcissus
				.getStaticObjectField(CLASS_JNA != null ? CLASS_JNA.getDeclaredField("INSTANCE") : null);
		//
	}

	private static class MH implements MethodHandler {

		@Override
		public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args)
				throws Throwable {
			//
			final String methodName = thisMethod != null ? thisMethod.getName() : null;
			//
			if (self instanceof Pointer) {
				//
				if (Objects.equals(methodName, "getString")) {
					//
					return null;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(methodName);
			//
		}

	}

	private static class IH implements InvocationHandler {

		private Pointer pointer = null;

		@Override
		public Object invoke(final Object proxy, @Nullable final Method method, @Nullable final Object[] args)
				throws Throwable {
			//
			final String methodName = method != null ? method.getName() : null;
			//
			if (CLASS_JNA != null && CLASS_JNA.isInstance(proxy)) {
				//
				if (Objects.equals(methodName, "getAvailableRecognizerLanguageTags")) {
					//
					return pointer;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(methodName);
			//
		}

	}

	private OcrImpl instance = null;

	@BeforeEach
	void beforeEach() {
		//
		instance = new OcrImpl();
		//
	}

	@Test
	void testGetAvailableRecognizerLanguageTags() throws Throwable {
		//
		final Object availableRecognizerLanguageTags = instance != null ? instance.getAvailableRecognizerLanguageTags()
				: null;
		//
		final IH ih = new IH();
		//
		final Object jna = Reflection.newProxy(CLASS_JNA, ih);
		//
		Assertions.assertNull(getAvailableRecognizerLanguageTags(jna));
		//
		if (JNA_INSTANCE != null) {
			//
			Assertions.assertNotNull(availableRecognizerLanguageTags);
			//
			Assertions.assertNull(getAvailableRecognizerLanguageTags(null));
			//
		} else {
			//
			Assertions.assertNull(availableRecognizerLanguageTags);
			//
			ih.pointer = new Pointer(0);
			//
			Assertions.assertNull(getAvailableRecognizerLanguageTags(jna));
			//
		} // if
			//
	}

	private static List<String> getAvailableRecognizerLanguageTags(final Object jna) throws Throwable {
		try {
			final Object obj = METHOD_GET_AVAILABLE_RECOGNIZER_LANGUAGE_TAGS.invoke(null, jna);
			if (obj == null) {
				return null;
			} else if (obj instanceof List) {
				return (List) obj;
			}
			throw new Throwable(toString(obj.getClass()));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetOcrText() throws IOException {
		//
		Assertions.assertNull(instance != null ? instance.getOcrText(null, null) : null);
		//
		final List<String> availableRecognizerLanguageTags = instance != null
				? instance.getAvailableRecognizerLanguageTags()
				: null;
		//
		final String string = "Hello world";
		//
		final byte[] bs = createBufferedImageBytes(new IntPair(250, 250), new Font("TimesRoman", Font.PLAIN, 40),
				Color.RED, string);
		//
		if (JNA_INSTANCE != null) {
			//
			final String result = StringUtils.trim(instance != null ? instance
					.getOcrText(availableRecognizerLanguageTags != null && !availableRecognizerLanguageTags.isEmpty()
							? availableRecognizerLanguageTags.get(0)
							: null, bs)
					: null);
			//
			Assertions.assertNotNull(result, String.join("!=", string, result));
			//
		} else {
			//
			Assertions.assertNull(StringUtils.trim(instance != null ? instance
					.getOcrText(availableRecognizerLanguageTags != null && !availableRecognizerLanguageTags.isEmpty()
							? availableRecognizerLanguageTags.get(0)
							: null, bs)
					: null));
			//
		} // if
			//
	}

	@Test
	void testGetOcrLines() throws IOException {
		//
		Assertions.assertNull(instance != null ? instance.getOcrLines(null, null) : null);
		//
		final List<String> availableRecognizerLanguageTags = instance != null
				? instance.getAvailableRecognizerLanguageTags()
				: null;
		//
		final String string = "中文字";
		//
		final byte[] bs = createBufferedImageBytes(new IntPair(250, 250), new Font("TimesRoman", Font.PLAIN, 40),
				Color.RED, string);
		//
		if (JNA_INSTANCE != null) {
			//
			String languageTag = null;
			//
			if (availableRecognizerLanguageTags != null) {
				//
				if (availableRecognizerLanguageTags.contains("ja")) {
					//
					languageTag = "ja";
					//
				} else if (!availableRecognizerLanguageTags.isEmpty()) {
					//
					languageTag = availableRecognizerLanguageTags.get(0);
					//
				} // if
					//
			} // if
				//
			final List<String> strings = instance != null ? instance.getOcrLines(languageTag, bs) : null;
			//
			Assertions.assertEquals(Collections.singletonList(string),
					strings != null ? strings.stream().map(x -> StringUtils.replace(x, " ", "")).toList() : null);
			//
		} else {
			//
			Assertions.assertNull(instance != null ? instance
					.getOcrLines(availableRecognizerLanguageTags != null && !availableRecognizerLanguageTags.isEmpty()
							? availableRecognizerLanguageTags.get(0)
							: null, bs)
					: null);
			//
		} // if
			//
	}

	private static byte[] createBufferedImageBytes(final IntPair dimension, final Font font, final Color color,
			final String string) throws IOException {
		//
		final BufferedImage bufferedImage = new BufferedImage(dimension != null ? dimension.getSource() : 0,
				dimension != null ? dimension.getTarget() : 0, BufferedImage.TYPE_INT_RGB);
		//
		final Graphics graphics = bufferedImage.getGraphics();
		//
		if (graphics != null) {
			//
			graphics.setColor(color);
			//
			graphics.setFont(font);
			//
			final FontMetrics fm = graphics.getFontMetrics();
			//
			graphics.drawString(string, bufferedImage.getWidth() - fm.stringWidth(string) - 5, fm.getHeight());
			//
		} // if
			//
		byte[] bs = null;
		//
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			//
			ImageIO.write(bufferedImage, "jpg", baos);
			//
			bs = baos.toByteArray();
			//
		} // try
			//
		return bs;
		//
	}

	@Test
	void testCast() throws Throwable {
		//
		Assertions.assertNull(cast(null, null));
		//
		Assertions.assertNull(cast(Object.class, null));
		//
		if (JNA_INSTANCE == null) {
			//
			final Object object = new Object();
			//
			Assertions.assertSame(object, cast(Object.class, object));
			//
		} // if
			//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) throws Throwable {
		try {
			return (T) METHOD_CAST.invoke(null, clz, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

	@Test
	void testTestAndApply() throws Throwable {
		//
		Assertions.assertNull(testAndApply(null, null, null, null));
		//
		Assertions.assertNull(testAndApply(Predicates.alwaysFalse(), null, null, null));
		//
		if (JNA_INSTANCE == null) {
			//
			Assertions.assertNull(testAndApply(Predicates.alwaysTrue(), null, x -> null, null));
			//
		} // if
			//
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse)
			throws Throwable {
		try {
			return (R) METHOD_TEST_AND_APPLY.invoke(null, predicate, value, functionTrue, functionFalse);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetString() throws Throwable {
		//
		if (JNA_INSTANCE == null) {
			//
			Assertions.assertNull(getString(createProxy(Pointer.class, new MH()), 0, null));
			//
		} // if
			//
	}

	private static <T> T createProxy(final Class<T> superClass, final MethodHandler mh) throws Throwable {
		//
		final ProxyFactory proxyFactory = new ProxyFactory();
		//
		proxyFactory.setSuperclass(superClass);
		//
		final Class<?> clz = proxyFactory.createClass();
		//
		final Constructor<?> constructor = clz != null ? clz.getDeclaredConstructor() : null;
		//
		final Object instance = constructor != null ? constructor.newInstance() : null;
		//
		if (instance instanceof ProxyObject) {
			//
			((ProxyObject) instance).setHandler(mh);
			//
		} // if
			//
		return (T) cast(clz, instance);
		//
	}

	private static String getString(final Pointer instance, final long offset, final String encoding) throws Throwable {
		try {
			final Object obj = METHOD_GET_STRING.invoke(null, instance, offset, encoding);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(obj.getClass()));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testAdd() {
		//
		Assertions.assertDoesNotThrow(() -> add(null, null));
		//
		if (JNA_INSTANCE == null) {
			//
			final Collection<?> collection = new LinkedHashSet<>();
			//
			Assertions.assertDoesNotThrow(() -> add(collection, null));
			//
			Assertions.assertEquals(Collections.singleton(null), collection);
			//
		} // if
			//
	}

	private static <E> void add(final Collection<E> instance, final E item) throws Throwable {
		try {
			METHOD_ADD.invoke(null, instance, item);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}