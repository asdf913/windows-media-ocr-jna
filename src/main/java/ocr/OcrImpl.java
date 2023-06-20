package ocr;

import java.util.Base64;
import java.util.Collections;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class OcrImpl implements Ocr {

	private interface Jna extends Library {

		Jna INSTANCE = createJna();

		public String getAvailableRecognizerLanguageTags();

		public String getOcrText(final String languageTag, final String base64EncodedString);

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
			final List<?> list = cast(List.class, new ObjectMapper().readValue(string, Object.class));
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
		return getOcrText(Jna.INSTANCE, languageTag,
				testAndApply(Objects::nonNull, bs, x -> encodeToString(Base64.getEncoder(), x), null));
		//
	}

	private static String getOcrText(final Jna instance, final String languageTag, final String base64EncodedString) {
		return instance != null ? instance.getOcrText(languageTag, base64EncodedString) : null;
	}

	private static String encodeToString(final Encoder instance, final byte[] src) {
		return instance != null ? instance.encodeToString(src) : null;
	}

	private static <T, R> R testAndApply(final Predicate<T> predicate, final T value, final Function<T, R> functionTrue,
			final Function<T, R> functionFalse) {
		return predicate != null && predicate.test(value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <R, T> R apply(Function<T, R> instance, final T value) {
		return instance != null ? instance.apply(value) : null;
	}

}