package ocr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.function.FailableFunction;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class OcrImpl implements Ocr {

	private interface Jna extends Library {

		Jna INSTANCE = createJna();

		public Pointer getAvailableRecognizerLanguageTags(final IntByReference length);

		public Pointer getOcrText(final String languageTag, final Pointer pointer, final int length,
				final IntByReference intByReference);

		public Pointer getOcrLines(final String languageTag, final Pointer pointer, final int length,
				final IntByReference lengtOut);

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
		return getAvailableRecognizerLanguageTags(Jna.INSTANCE);
		//
	}

	private static List<String> getAvailableRecognizerLanguageTags(final Jna instance) {
		//
		if (instance != null) {
			//
			final IntByReference lengthIbr = new IntByReference();
			//
			final Pointer pointer = instance.getAvailableRecognizerLanguageTags(lengthIbr);
			//
			final int length = lengthIbr.getValue();
			//
			final Pointer[] pointers = pointer != null ? pointer.getPointerArray(0, length) : null;
			//
			List<String> list = null;
			//
			for (int i = 0; pointers != null && i < Math.min(pointers.length, length); i++) {
				//
				add(list = ObjectUtils.getIfNull(list, ArrayList::new), pointers[i].getWideString(0));
				//
			} // for
				//
			return list;
			//
		} // if
			//
		return null;
		//
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
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
		try (final Memory memory = testAndApply(x -> x > 0, length, x -> new Memory(x), null)) {
			//
			if (memory != null) {
				//
				memory.write(0, bs, 0, length);
				//
			} // if
				//
			final IntByReference lengthOut = new IntByReference();
			//
			final Jna instance = Jna.INSTANCE;
			//
			final Pointer pointer = instance != null && memory != null
					? instance.getOcrLines(languageTag, memory, length, lengthOut)
					: null;
			//
			final int length1 = lengthOut.getValue();
			//
			final Pointer[] pointers = pointer != null ? pointer.getPointerArray(0, length1) : null;
			//
			List<String> list = null;
			//
			for (int i = 0; pointers != null && i < Math.min(pointers.length, length1); i++) {
				//
				add(list = ObjectUtils.getIfNull(list, ArrayList::new), pointers[i].getWideString(0));
				//
			} // for
				//
			return list;
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