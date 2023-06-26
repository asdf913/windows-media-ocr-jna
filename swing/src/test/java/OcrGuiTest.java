import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;
import ocr.Ocr;

class OcrGuiTest {

	private static Method METHOD_INIT, METHOD_GET_SELECTED_ITEM, METHOD_GET_OCR_TEXT = null;

	@BeforeAll
	static void beforeAll() throws ReflectiveOperationException {
		//
		final Class<?> clz = OcrGui.class;
		//
		(METHOD_INIT = clz.getDeclaredMethod("init")).setAccessible(true);
		//
		(METHOD_GET_SELECTED_ITEM = clz.getDeclaredMethod("getSelectedItem", ComboBoxModel.class)).setAccessible(true);
		//
		(METHOD_GET_OCR_TEXT = clz.getDeclaredMethod("getOcrText", Ocr.class, String.class, byte[].class))
				.setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private String ocrText = null;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			final String methodName = method != null ? method.getName() : null;
			//
			if (proxy instanceof Ocr) {
				//
				if (Objects.equals(methodName, "getOcrText")) {
					//
					return ocrText;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(methodName);
			//
		}

	}

	private OcrGui instance = null;

	@BeforeEach
	void beforeEach() throws Throwable {
		//
		if (!GraphicsEnvironment.isHeadless()) {
			//
			final Constructor<OcrGui> constructor = OcrGui.class.getDeclaredConstructor();
			//
			if (constructor != null) {
				//
				constructor.setAccessible(true);
				//
			} // if
				//
			instance = constructor != null ? constructor.newInstance() : null;
			//
		} else {
			//
			instance = cast(OcrGui.class, Narcissus.allocateInstance(OcrGui.class));
			//
		} // if
	}

	private static <T> T cast(final Class<T> clz, final Object value) {
		return clz != null && clz.isInstance(value) ? clz.cast(value) : null;
	}

	@Test
	void testInit() {
		//
		Assertions.assertDoesNotThrow(() -> init());
		//
		if (instance != null) {
			//
			instance.setLayout(new MigLayout());
			//
		} // if
			//
		Assertions.assertDoesNotThrow(() -> init());
		//
	}

	private void init() throws Throwable {
		try {
			METHOD_INIT.invoke(instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testActionPerformed() {
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, null));
		//
	}

	private static void actionPerformed(final ActionListener instance, final ActionEvent actionEvent) {
		if (instance != null) {
			instance.actionPerformed(actionEvent);
		}
	}

	@Test
	void testGetSelectedItem() throws Throwable {
		//
		Assertions.assertNull(getSelectedItem(new DefaultComboBoxModel<>()));
		//
	}

	private static Object getSelectedItem(final ComboBoxModel<?> instance) throws Throwable {
		try {
			return (Object) METHOD_GET_SELECTED_ITEM.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetOcrText() throws Throwable {
		//
		Assertions.assertNull(getOcrText(null, null, null));
		//
		Assertions.assertNull(getOcrText(Reflection.newProxy(Ocr.class, new IH()), null, null));
		//
	}

	private static String getOcrText(final Ocr instance, final String languageTag, final byte[] bs) throws Throwable {
		try {
			final Object obj = METHOD_GET_OCR_TEXT.invoke(null, instance, languageTag, bs);
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

}