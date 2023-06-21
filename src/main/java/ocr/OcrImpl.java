package ocr;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class OcrImpl implements Ocr {

	private interface Jna extends Library {

		Jna INSTANCE = createJna();

		public String getAvailableRecognizerLanguageTags();

		public Pointer getOcrText(final String languageTag, final Pointer pointer, final int length,
				final IntByReference intByReference);

		public String getOcrLinesAsJson(final String languageTag, final Pointer pointer, final int length,
				final IntByReference intByReference);

	}

	private static Jna createJna() {
		//
		try {
			//
			return cast(Jna.class, Native.load("WindowsMediaOcrLibary.dll", Jna.class));
			//
		} catch (final Throwable e) {
			//
			return null;
			//
		} // try
			//
	}

	private static <T> T cast(final Class<T> clz, final Object value) {
		return clz != null && clz.isInstance(value) ? clz.cast(value) : null;
	}

	@Override
	public List<String> getAvailableRecognizerLanguageTags() {
		//
		final String string = getAvailableRecognizerLanguageTags(Jna.INSTANCE);
		//
		try {
			//
			final List<?> list = cast(List.class,
					testAndApply(Objects::nonNull, string, x -> new ObjectMapper().readValue(x, Object.class), null));
			//
			return list != null ? list.stream().map(x -> toString(x)).toList() : null;
			//
		} catch (final JsonProcessingException e) {
			//
			return Collections.singletonList(string);
			//
		} // try
			//
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

	private static String getAvailableRecognizerLanguageTags(final Jna instance) {
		return instance != null ? instance.getAvailableRecognizerLanguageTags() : null;
	}

	@Override
	public String getOcrText(final String languageTag, final byte[] bs) {
		//
		final int length = bs != null ? bs.length : 0;
		//
		Pointer pointer = null;
		//
		try (final Memory memory = testAndApply(x -> x > 0, length, x -> new Memory(x), null)) {
			//
			if (memory != null) {
				//
				memory.write(0, bs, 0, length);
				//
			} // if
				///
			final IntByReference intByReference = new IntByReference();
			//
			pointer = testAndApply(Objects::nonNull, memory,
					x -> getOcrText(Jna.INSTANCE, languageTag, x, length, intByReference), null);
			//
			return getString(pointer, 0, "UTF-8");
			//
		} // try
			//
	}

	@Override
	public List<String> getOcrLines(final String languageTag, final byte[] bs) {
		//
		final int length = bs != null ? bs.length : 0;
		//
		String string = null;
		//
		try (final Memory memory = testAndApply(x -> x > 0, length, x -> new Memory(x), null)) {
			//
			if (memory != null) {
				//
				memory.write(0, bs, 0, length);
				//
			} // if
				//
			final IntByReference intByReference = new IntByReference();
			//
			final Jna instance = Jna.INSTANCE;
			//
			string = instance != null && memory != null
					? instance.getOcrLinesAsJson(languageTag, memory, length, intByReference)
					: null;
		} // try
			//
		try {
			//
			final List<?> list = cast(List.class,
					testAndApply(Objects::nonNull, string, x -> new ObjectMapper().readValue(x, Object.class), null));
			//
			return list != null ? list.stream().map(x -> StringEscapeUtils.unescapeJava(toString(x))).toList() : null;
			//
		} catch (final JsonProcessingException e) {
			//
			return Collections.singletonList(string);
			//
		} // try
			//
	}

	private static String getString(final Pointer instance, final long offset, final String encoding) {
		return instance != null ? instance.getString(offset, encoding) : null;
	}

	private static Pointer getOcrText(final Jna instance, final String languageTag, final Pointer pointer,
			final int length, final IntByReference intByReference) {
		return instance != null ? instance.getOcrText(languageTag, pointer, length, intByReference) : null;
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse) throws E {
		return predicate != null && predicate.test(value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> instance, final T value)
			throws E {
		return instance != null ? instance.apply(value) : null;
	}

}