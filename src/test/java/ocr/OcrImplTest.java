package ocr;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;

import edu.stanford.nlp.util.IntPair;
import io.github.toolfactory.narcissus.Narcissus;

class OcrImplTest {

	private static Method METHOD_CAST, METHOD_TO_STRING, METHOD_TEST_AND_APPLY = null;

	private static Object JNA_INSTANCE = null;

	@BeforeAll
	static void beforeAll() throws ReflectiveOperationException {
		//
		final Class<?> clz = OcrImpl.class;
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
		(METHOD_TO_STRING = clz.getDeclaredMethod("toString", Object.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_APPLY = clz.getDeclaredMethod("testAndApply", Predicate.class, Object.class,
				FailableFunction.class, FailableFunction.class)).setAccessible(true);
		//
		JNA_INSTANCE = Narcissus.getStaticObjectField(Class.forName("ocr.OcrImpl$Jna").getDeclaredField("INSTANCE"));
		//
	}

	private OcrImpl instance = null;

	@BeforeEach
	void beforeEach() {
		//
		instance = new OcrImpl();
		//
	}

	@Test
	void testGetAvailableRecognizerLanguageTags() {
		//
		final Object availableRecognizerLanguageTags = instance != null ? instance.getAvailableRecognizerLanguageTags()
				: null;
		//
		if (JNA_INSTANCE != null) {
			//
			Assertions.assertNotNull(availableRecognizerLanguageTags);
			//
		} else {
			//
			Assertions.assertNull(availableRecognizerLanguageTags);
			//
		} // if
			//
	}

	@Test
	void testGetOcrText() throws IOException {
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

	@Test
	void testToString() throws Throwable {
		//
		Assertions.assertNull(toString(null));
		//
		if (JNA_INSTANCE == null) {
			//
			final String string = "";
			//
			Assertions.assertSame(string, toString(string));
			//
		} // if
			//
	}

	private static String toString(final Object instance) throws Throwable {
		try {
			final Object obj = METHOD_TO_STRING.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(obj.getClass() != null ? obj.getClass().toString() : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
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

}