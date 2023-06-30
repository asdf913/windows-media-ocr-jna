import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meeuw.functional.Consumers;

import com.google.common.base.Predicates;
import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import net.miginfocom.swing.MigLayout;
import ocr.Ocr;

class OcrGuiTest {

	private static Method METHOD_CAST, METHOD_INIT, METHOD_GET_SELECTED_ITEM_COMBO_BOX_MODEL,
			METHOD_GET_SELECTED_ITEM_J_COMBO_BOX, METHOD_GET_OCR_TEXT, METHOD_GET_CLASS, METHOD_TO_STRING,
			METHOD_GET_CLASS_NAME1, METHOD_GET_CLASS_NAME2, METHOD_GET_ABSOLUTE_PATH, METHOD_SET_TEXT,
			METHOD_TEST_AND_ACCEPT3, METHOD_TEST_AND_ACCEPT4, METHOD_STREAM, METHOD_FILTER, METHOD_TO_LIST,
			METHOD_GET_NAME_MEMBER, METHOD_GET_NAME_CLASS, METHOD_GET_LAYOUT, METHOD_TEST_AND_APPLY,
			METHOD_CREATE_PROPERTIES, METHOD_SHOW_EXCEPTION_ERROR_OR_PRINT_STACK_TRACE, METHOD_ADD_ACTION_LISTENER,
			METHOD_GET_SYSTEM_CLIP_BOARD, METHOD_SET_CONTENTS, METHOD_IS_RAISE_THROWABLE_ONLY, METHOD_MAP,
			METHOD_FOR_NAME, METHOD_GET_RESOURCE_AS_STREAM, METHOD_PARSE, METHOD_GET_METHOD,
			METHOD_IS_UNDER_DEBUG_OR_MAVEN, METHOD_GET_AVAILABLE_RECOGNIZER_LANGUAGE_TAGS, METHOD_IS_ASSIGNABLE_FROM,
			METHOD_FILTER_STACK_TRACE, METHOD_OPEN_CONNECTION, METHOD_GET_INPUT_STREAM, METHOD_GET_KEY,
			METHOD_GET_VALUE, METHOD_ENTRY_SET, METHOD_SORTED, METHOD_FOR_EACH, METHOD_GET_DECLARED_METHOD = null;

	@BeforeAll
	static void beforeAll() throws ReflectiveOperationException {
		//
		final Class<?> clz = OcrGui.class;
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
		(METHOD_INIT = clz.getDeclaredMethod("init", Map.class)).setAccessible(true);
		//
		(METHOD_GET_SELECTED_ITEM_COMBO_BOX_MODEL = clz.getDeclaredMethod("getSelectedItem", ComboBoxModel.class))
				.setAccessible(true);
		//
		(METHOD_GET_SELECTED_ITEM_J_COMBO_BOX = clz.getDeclaredMethod("getSelectedItem", JComboBox.class))
				.setAccessible(true);
		//
		(METHOD_GET_OCR_TEXT = clz.getDeclaredMethod("getOcrText", Ocr.class, String.class, byte[].class))
				.setAccessible(true);
		//
		(METHOD_GET_CLASS = clz.getDeclaredMethod("getClass", Object.class)).setAccessible(true);
		//
		(METHOD_TO_STRING = clz.getDeclaredMethod("toString", Object.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS_NAME1 = clz.getDeclaredMethod("getClassName", StackTraceElement.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS_NAME2 = clz.getDeclaredMethod("getClassName", FieldOrMethod.class, ConstantPoolGen.class))
				.setAccessible(true);
		//
		(METHOD_GET_ABSOLUTE_PATH = clz.getDeclaredMethod("getAbsolutePath", File.class)).setAccessible(true);
		//
		(METHOD_SET_TEXT = clz.getDeclaredMethod("setText", JTextComponent.class, String.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_ACCEPT3 = clz.getDeclaredMethod("testAndAccept", Predicate.class, Object.class,
				FailableConsumer.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_ACCEPT4 = clz.getDeclaredMethod("testAndAccept", BiPredicate.class, Object.class, Object.class,
				BiConsumer.class)).setAccessible(true);
		//
		(METHOD_STREAM = clz.getDeclaredMethod("stream", Collection.class)).setAccessible(true);
		//
		(METHOD_FILTER = clz.getDeclaredMethod("filter", Stream.class, Predicate.class)).setAccessible(true);
		//
		(METHOD_TO_LIST = clz.getDeclaredMethod("toList", Stream.class)).setAccessible(true);
		//
		(METHOD_GET_NAME_MEMBER = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_GET_NAME_CLASS = clz.getDeclaredMethod("getName", Class.class)).setAccessible(true);
		//
		(METHOD_GET_LAYOUT = clz.getDeclaredMethod("getLayout", Container.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_APPLY = clz.getDeclaredMethod("testAndApply", Predicate.class, Object.class,
				FailableFunction.class, FailableFunction.class)).setAccessible(true);
		//
		(METHOD_CREATE_PROPERTIES = clz.getDeclaredMethod("createProperties", File.class)).setAccessible(true);
		//
		(METHOD_SHOW_EXCEPTION_ERROR_OR_PRINT_STACK_TRACE = clz
				.getDeclaredMethod("showExceptionOrErrorOrPrintStackTrace", Logger.class, Throwable.class))
				.setAccessible(true);
		//
		(METHOD_ADD_ACTION_LISTENER = clz.getDeclaredMethod("addActionListener", ActionListener.class,
				AbstractButton.class, AbstractButton.class, AbstractButton[].class)).setAccessible(true);
		//
		(METHOD_GET_SYSTEM_CLIP_BOARD = clz.getDeclaredMethod("getSystemClipboard", Toolkit.class)).setAccessible(true);
		//
		(METHOD_SET_CONTENTS = clz.getDeclaredMethod("setContents", Clipboard.class, Transferable.class,
				ClipboardOwner.class)).setAccessible(true);
		//
		(METHOD_IS_RAISE_THROWABLE_ONLY = clz.getDeclaredMethod("isRaiseThrowableOnly", Class.class, Method.class))
				.setAccessible(true);
		//
		(METHOD_MAP = clz.getDeclaredMethod("map", Stream.class, Function.class)).setAccessible(true);
		//
		(METHOD_FOR_NAME = clz.getDeclaredMethod("forName", String.class)).setAccessible(true);
		//
		(METHOD_GET_RESOURCE_AS_STREAM = clz.getDeclaredMethod("getResourceAsStream", Class.class, String.class))
				.setAccessible(true);
		//
		(METHOD_PARSE = clz.getDeclaredMethod("parse", ClassParser.class)).setAccessible(true);
		//
		(METHOD_GET_METHOD = clz.getDeclaredMethod("getMethod", JavaClass.class, Method.class)).setAccessible(true);
		//
		(METHOD_IS_UNDER_DEBUG_OR_MAVEN = clz.getDeclaredMethod("isUnderDebugOrMaven")).setAccessible(true);
		//
		(METHOD_GET_AVAILABLE_RECOGNIZER_LANGUAGE_TAGS = clz.getDeclaredMethod("getAvailableRecognizerLanguageTags",
				Ocr.class)).setAccessible(true);
		//
		(METHOD_IS_ASSIGNABLE_FROM = clz.getDeclaredMethod("isAssignableFrom", Class.class, Class.class))
				.setAccessible(true);
		//
		(METHOD_FILTER_STACK_TRACE = clz.getDeclaredMethod("filterStackTrace", Throwable.class, Class.class))
				.setAccessible(true);
		//
		(METHOD_OPEN_CONNECTION = clz.getDeclaredMethod("openConnection", URL.class)).setAccessible(true);
		//
		(METHOD_GET_INPUT_STREAM = clz.getDeclaredMethod("getInputStream", URLConnection.class)).setAccessible(true);
		//
		(METHOD_GET_KEY = clz.getDeclaredMethod("getKey", Entry.class)).setAccessible(true);
		//
		(METHOD_GET_VALUE = clz.getDeclaredMethod("getValue", Entry.class)).setAccessible(true);
		//
		(METHOD_ENTRY_SET = clz.getDeclaredMethod("entrySet", Map.class)).setAccessible(true);
		//
		(METHOD_SORTED = clz.getDeclaredMethod("sorted", Stream.class, Comparator.class)).setAccessible(true);
		//
		(METHOD_FOR_EACH = clz.getDeclaredMethod("forEach", Stream.class, Consumer.class)).setAccessible(true);
		//
		(METHOD_GET_DECLARED_METHOD = clz.getDeclaredMethod("getDeclaredMethod", Class.class, String.class,
				Class[].class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private String ocrText = null;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			final String methodName = getName(method);
			//
			if (proxy instanceof Ocr) {
				//
				if (Objects.equals(methodName, "getOcrText")) {
					//
					return ocrText;
					//
				} // if
					//
			} else if (proxy instanceof Stream) {
				//
				if (Objects.equals(methodName, "filter")) {
					//
					return proxy;
					//
				} else if (Objects.equals(methodName, "map")) {
					//
					return null;
					//
				} else if (Objects.equals(methodName, "forEach")) {
					//
					return null;
					//
				} else if (Objects.equals(methodName, "sorted")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Logger) {
				//
				if (Objects.equals(methodName, "error")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Map) {
				//
				if (Objects.equals(methodName, "entrySet")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Entry) {
				//
				if (Objects.equals(methodName, "getKey")) {
					//
					return null;
					//
				} else if (Objects.equals(methodName, "getValue")) {
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

	private static class MH implements MethodHandler {

		@Override
		public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args)
				throws Throwable {
			//
			final String methodName = getName(thisMethod);
			//
			if (self instanceof Toolkit) {
				//
				if (Objects.equals(methodName, "getSystemClipboard")) {
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

	private OcrGui instance = null;

	private IH ih = null;

	private Entry<?, ?> entry = null;

	private Stream<?> stream = null;

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
			//
		entry = Reflection.newProxy(Entry.class, ih = new IH());
		//
		stream = Reflection.newProxy(Stream.class, ih);
		//
	}

	@Test
	void testMain() {
		//
		Assertions.assertDoesNotThrow(() -> OcrGui.main(null));
		//
	}

	@Test
	void testCast() throws Throwable {
		//
		Assertions.assertNull(cast(null, null));
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
	void testInit() {
		//
		Assertions.assertDoesNotThrow(() -> init(null));
		//
		if (instance != null) {
			//
			instance.setLayout(new MigLayout());
			//
		} // if
			//
		Assertions.assertDoesNotThrow(() -> init(null));
		//
		final Map<Object, ?> map = new LinkedHashMap<>();
		//
		Assertions.assertDoesNotThrow(() -> init(map));
		//
		map.put("languageTag", null);
		//
		Assertions.assertDoesNotThrow(() -> init(map));
		//
	}

	private void init(final Map<?, ?> map) throws Throwable {
		try {
			METHOD_INIT.invoke(instance, map);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testActionPerformed() throws Throwable {
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, new ActionEvent("", 0, null)));
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, null));
		//
		FieldUtils.writeDeclaredField(instance, "cbmLanaguageTag", new DefaultComboBoxModel<>(new Object[] { "" }),
				true);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, null));
		//
		// abUrl
		//
		final AbstractButton abUrl = new JButton();
		//
		FieldUtils.writeDeclaredField(instance, "abUrl", abUrl, true);
		//
		final ActionEvent actionEvent1 = new ActionEvent(abUrl, 0, null);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent1));
		//
		final JTextComponent jtcUrl = new JTextField();
		//
		FieldUtils.writeDeclaredField(instance, "jtcUrl", jtcUrl, true);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent1));
		//
		setText(jtcUrl, " ");
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent1));
		//
		final DefaultTableModel dtmResponseHeaders = new DefaultTableModel();
		//
		dtmResponseHeaders.addRow(new Object[] { null });
		//
		FieldUtils.writeDeclaredField(instance, "dtmResponseHeaders", dtmResponseHeaders, true);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent1));
		//
		// abCopyText
		//
		final AbstractButton abCopyText = new JButton();
		//
		FieldUtils.writeDeclaredField(instance, "abCopyText", abCopyText, true);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, new ActionEvent(abCopyText, 0, null)));
		//
		final DefaultComboBoxModel<Object> dcbm = new DefaultComboBoxModel<>(new String[] { "ja" });
		//
		final JComboBox<?> jcbLanaguageTag = new JComboBox<>(dcbm);
		//
		FieldUtils.writeDeclaredField(instance, "jcbLanaguageTag", jcbLanaguageTag, true);
		//
		final ActionEvent actionEvent2 = new ActionEvent(jcbLanaguageTag, 0, null);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent2));
		//
		dcbm.removeAllElements();
		//
		dcbm.addElement("en");
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent2));
		//
		dcbm.removeAllElements();
		//
		dcbm.addElement("zh-Hans-HK");
		//
		FieldUtils.writeDeclaredField(instance, "jlLanguageTag", new JLabel(), true);
		//
		Assertions.assertDoesNotThrow(() -> actionPerformed(instance, actionEvent2));
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
		Assertions.assertNull(getSelectedItem((JComboBox<?>) null));
		//
		Assertions.assertNull(getSelectedItem(new DefaultComboBoxModel<>()));
		//
	}

	private static Object getSelectedItem(final ComboBoxModel<?> instance) throws Throwable {
		try {
			return (Object) METHOD_GET_SELECTED_ITEM_COMBO_BOX_MODEL.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Object getSelectedItem(final JComboBox<?> instance) throws Throwable {
		try {
			return (Object) METHOD_GET_SELECTED_ITEM_J_COMBO_BOX.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetOcrText() throws Throwable {
		//
		Assertions.assertNull(getOcrText(null, null, null));
		//
		Assertions.assertNull(getOcrText(Reflection.newProxy(Ocr.class, ih), null, null));
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

	@Test
	void testGetClass() throws Throwable {
		//
		Assertions.assertNull(getClass(null));
		//
	}

	private static Class<?> getClass(final Object instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_CLASS.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Class<?>) {
				return (Class<?>) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static String toString(final Object instance) throws Throwable {
		try {
			final Object obj = METHOD_TO_STRING.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetClassName() throws Throwable {
		//
		Assertions.assertNull(getClassName(null));
		//
		Assertions.assertNull(getClassName(null, null));
		//
		if (GraphicsEnvironment.isHeadless()) {
			//
			final String className = "className";
			//
			Assertions.assertEquals(className, getClassName(new StackTraceElement(className, "", null, 0)));
			//
		} // if
			//
	}

	private static String getClassName(final StackTraceElement instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_CLASS_NAME1.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static String getClassName(final FieldOrMethod instance, final ConstantPoolGen cpg) throws Throwable {
		try {
			final Object obj = METHOD_GET_CLASS_NAME2.invoke(null, instance, cpg);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetAbsolutePath() throws Throwable {
		//
		Assertions.assertNotNull(getAbsolutePath(new File(".")));
		//
	}

	private static String getAbsolutePath(final File instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_ABSOLUTE_PATH.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetText() {
		//
		Assertions.assertDoesNotThrow(() -> setText(new JTextField(), null));
		//
	}

	private static void setText(final JTextComponent instance, final String text) throws Throwable {
		try {
			METHOD_SET_TEXT.invoke(null, instance, text);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testTestAndAccept() {
		//
		Assertions.assertDoesNotThrow(() -> testAndAccept(null, null, null));
		//
		Assertions.assertDoesNotThrow(() -> testAndAccept(null, null, null, null));
		//
		Assertions.assertDoesNotThrow(() -> testAndAccept(Predicates.alwaysTrue(), null, null));
		//
		Assertions.assertDoesNotThrow(() -> testAndAccept((a, b) -> true, null, null, null));
		//
		if (!GraphicsEnvironment.isHeadless()) {
			//
			Assertions.assertDoesNotThrow(() -> testAndAccept(Predicates.alwaysFalse(), null, null));
			//
			Assertions.assertDoesNotThrow(() -> testAndAccept((a, b) -> false, null, null, null));
			//
		} else {
			//
			Assertions.assertDoesNotThrow(() -> testAndAccept((a, b) -> true, null, null, (a, b) -> {
			}));
			//
		} // if
			//
	}

	private static <T, E extends Throwable> void testAndAccept(final Predicate<T> predicate, final T value,
			final FailableConsumer<T, E> consumer) throws Throwable {
		try {
			METHOD_TEST_AND_ACCEPT3.invoke(null, predicate, value, consumer);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static <T, U> void testAndAccept(final BiPredicate<T, U> predicate, final T t, final U u,
			final BiConsumer<T, U> consumer) throws Throwable {
		try {
			METHOD_TEST_AND_ACCEPT4.invoke(null, predicate, t, u, consumer);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testStream() throws Throwable {
		//
		Assertions.assertNull(stream(null));
		//
	}

	private static <T> Stream<T> stream(final Collection<T> instance) throws Throwable {
		try {
			final Object obj = METHOD_STREAM.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Stream) {
				return (Stream) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testFilter() throws Throwable {
		//
		Assertions.assertNull(filter(null, null));
		//
		Assertions.assertNull(filter(Stream.empty(), null));
		//
		Assertions.assertSame(stream, filter(stream, null));
		//
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate)
			throws Throwable {
		try {
			final Object obj = METHOD_FILTER.invoke(null, instance, predicate);
			if (obj == null) {
				return null;
			} else if (obj instanceof Stream) {
				return (Stream) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testToList() throws Throwable {
		//
		Assertions.assertNull(toList(null));
		//
	}

	private static <T> List<T> toList(final Stream<T> instance) throws Throwable {
		try {
			final Object obj = METHOD_TO_LIST.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof List) {
				return (List) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetName() throws Throwable {
		//
		Assertions.assertNull(getName((Member) null));
		//
		Assertions.assertNull(getName((Class<?>) null));
		//
	}

	private static String getName(final Member instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_NAME_MEMBER.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static String getName(final Class<?> instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_NAME_CLASS.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetLayout() throws Throwable {
		//
		Assertions.assertNull(getLayout(null));
		//
		if (GraphicsEnvironment.isHeadless()) {
			//
			Assertions.assertNull(getLayout(new Container()));
			//
		} // if
			//
	}

	private static LayoutManager getLayout(final Container instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_LAYOUT.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof LayoutManager) {
				return (LayoutManager) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testTestAndApply() throws Throwable {
		//
		Assertions.assertNull(testAndApply(null, null, null, x -> null));
		//
		Assertions.assertNull(testAndApply(Predicates.alwaysTrue(), null, null, null));
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
	void testCreateProperties() throws Throwable {
		//
		Assertions.assertNull(createProperties(null));
		//
		Assertions.assertNull(createProperties(new File(RandomStringUtils.randomAlphabetic(1))));
		//
		File file = new File(".");
		//
		if (!file.isFile()) {
			//
			Assertions.assertNull(createProperties(file));
			//
		} else {
			//
			Assertions.assertNotNull(createProperties(file));
			//
		} // if
			//
		if (!(file = new File("pom.xml")).isFile()) {
			//
			Assertions.assertNull(createProperties(file));
			//
		} else {
			//
			Assertions.assertNotNull(createProperties(file));
			//
		} // if
			//
	}

	private static Properties createProperties(final File file) throws Throwable {
		try {
			final Object obj = METHOD_CREATE_PROPERTIES.invoke(null, file);
			if (obj == null) {
				return null;
			} else if (obj instanceof Properties) {
				return (Properties) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testShowExceptionOrErrorOrPrintStackTrace() {
		//
		Assertions.assertDoesNotThrow(() -> showExceptionOrErrorOrPrintStackTrace(null, null));
		//
		Assertions.assertDoesNotThrow(
				() -> showExceptionOrErrorOrPrintStackTrace(Reflection.newProxy(Logger.class, ih), null));
		//
		final Throwable throwable = new Throwable();
		//
		Assertions.assertDoesNotThrow(() -> showExceptionOrErrorOrPrintStackTrace(null, throwable));
		//
	}

	private static void showExceptionOrErrorOrPrintStackTrace(final Logger logger, final Throwable throwable)
			throws Throwable {
		try {
			METHOD_SHOW_EXCEPTION_ERROR_OR_PRINT_STACK_TRACE.invoke(null, logger, throwable);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testAddActionListener() {
		//
		Assertions.assertDoesNotThrow(() -> addActionListener(null, null, null, (AbstractButton[]) null));
		//
	}

	private static void addActionListener(final ActionListener actionListener, final AbstractButton a,
			final AbstractButton b, final AbstractButton... abs) throws Throwable {
		try {
			METHOD_ADD_ACTION_LISTENER.invoke(null, actionListener, a, b, abs);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetSystemClipboard() throws Throwable {
		//
		Assertions.assertNull(getSystemClipboard(null));
		//
		if (GraphicsEnvironment.isHeadless()) {
			//
			Assertions.assertNull(getSystemClipboard(createProxy(Toolkit.class, new MH())));
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

	private static Clipboard getSystemClipboard(final Toolkit instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_SYSTEM_CLIP_BOARD.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Clipboard) {
				return (Clipboard) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetContents() throws Throwable {
		//
		Assertions.assertDoesNotThrow(() -> setContents(null, null, null));
		//
		Assertions.assertDoesNotThrow(() -> setContents(new Clipboard(null), null, null));
		//
	}

	private static void setContents(final Clipboard instance, final Transferable contents, final ClipboardOwner owner)
			throws Throwable {
		try {
			METHOD_SET_CONTENTS.invoke(null, instance, contents, owner);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testIsRaiseThrowableOnly() throws Throwable {
		//
		Assertions.assertFalse(isRaiseThrowableOnly(null, null));
		//
	}

	private static boolean isRaiseThrowableOnly(final Class<?> clz, final Method method) throws Throwable {
		try {
			final Object obj = METHOD_IS_RAISE_THROWABLE_ONLY.invoke(null, clz, method);
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testMap() throws Throwable {
		//
		Assertions.assertNull(map(Stream.empty(), null));
		//
		Assertions.assertNull(map(stream, null));
		//
	}

	private static <T, R> Stream<R> map(final Stream<T> instance, final Function<? super T, ? extends R> mapper)
			throws Throwable {
		try {
			final Object obj = METHOD_MAP.invoke(null, instance, mapper);
			if (obj == null) {
				return null;
			} else if (obj instanceof Stream) {
				return (Stream) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testForName() throws Throwable {
		//
		Assertions.assertNull(forName(null));
		//
		Assertions.assertNull(forName("A"));
		//
		final Class<?> clz = getClass("");
		//
		final String string = getName(clz);
		//
		Assertions.assertSame(clz, forName(string));
		//
	}

	private static Class<?> forName(final String className) throws Throwable {
		try {
			final Object obj = METHOD_FOR_NAME.invoke(null, className);
			if (obj == null) {
				return null;
			} else if (obj instanceof Class) {
				return (Class<?>) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetResourceAsStream() throws Throwable {
		//
		Assertions.assertNull(getResourceAsStream(Class.class, null));
		//
	}

	private static InputStream getResourceAsStream(final Class<?> instance, final String name) throws Throwable {
		try {
			final Object obj = METHOD_GET_RESOURCE_AS_STREAM.invoke(null, instance, name);
			if (obj == null) {
				return null;
			} else if (obj instanceof InputStream) {
				return (InputStream) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetMethod() throws Throwable {
		//
		final Class<?> clz = String.class;
		//
		try (final InputStream is = getResourceAsStream(clz,
				String.format("/%1$s.class", StringUtils.replace(getName(clz), ".", "/")))) {
			//
			Assertions.assertNull(
					getMethod(parse(testAndApply(Objects::nonNull, is, x -> new ClassParser(x, null), null)), null));
			//
		} // try
			//
	}

	private static JavaClass parse(final ClassParser instance) throws Throwable {
		try {
			final Object obj = METHOD_PARSE.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof JavaClass) {
				return (JavaClass) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static org.apache.bcel.classfile.Method getMethod(final JavaClass instance, final Method method)
			throws Throwable {
		try {
			final Object obj = METHOD_GET_METHOD.invoke(null, instance, method);
			if (obj == null) {
				return null;
			} else if (obj instanceof org.apache.bcel.classfile.Method) {
				return (org.apache.bcel.classfile.Method) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void tsteIsUnderDebugOrMaven() throws Throwable {
		//
		Assertions.assertTrue(isUnderDebugOrMaven());
		//
	}

	private static boolean isUnderDebugOrMaven() throws Throwable {
		try {
			final Object obj = METHOD_IS_UNDER_DEBUG_OR_MAVEN.invoke(null);
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetAvailableRecognizerLanguageTags() throws Throwable {
		//
		Assertions.assertNull(getAvailableRecognizerLanguageTags(null));
		//
	}

	private static List<String> getAvailableRecognizerLanguageTags(final Ocr instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_AVAILABLE_RECOGNIZER_LANGUAGE_TAGS.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof List) {
				return (List) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testIsAssignableFrom() throws Throwable {
		//
		Assertions.assertFalse(isAssignableFrom(null, null));
		//
		Assertions.assertFalse(isAssignableFrom(Throwable.class, null));
		//
		Assertions.assertFalse(isAssignableFrom(Throwable.class, String.class));
		//
	}

	private static boolean isAssignableFrom(final Class<?> a, final Class<?> b) throws Throwable {
		try {
			final Object obj = METHOD_IS_ASSIGNABLE_FROM.invoke(null, a, b);
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testFilterStackTrace() {
		//
		Assertions.assertDoesNotThrow(() -> filterStackTrace(null, null));
		//
		final Throwable throwable = new Throwable();
		//
		Assertions.assertDoesNotThrow(() -> filterStackTrace(throwable, null));
		//
		Assertions.assertDoesNotThrow(() -> filterStackTrace(throwable, OcrGuiTest.class));
		//
	}

	private static void filterStackTrace(final Throwable throwable, final Class<?> clz) throws Throwable {
		try {
			METHOD_FILTER_STACK_TRACE.invoke(null, throwable, clz);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testOpenConnection() throws Throwable {
		//
		Assertions.assertNotNull(openConnection(new File("pom.xml").toURI().toURL()));
		//
	}

	private static URLConnection openConnection(final URL instance) throws Throwable {
		try {
			final Object obj = METHOD_OPEN_CONNECTION.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof URLConnection) {
				return (URLConnection) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetInputStream() throws Throwable {
		//
		try (final InputStream is = getInputStream(openConnection(new File("pom.xml").toURI().toURL()))) {
			//
			Assertions.assertNotNull(is);
			//
		} // try
			//
	}

	private static InputStream getInputStream(final URLConnection instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_INPUT_STREAM.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof InputStream) {
				return (InputStream) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetKey() throws Throwable {
		//
		Assertions.assertNull(getKey(null));
		//
		Assertions.assertNull(getKey(entry));
		//
	}

	private static <K> K getKey(final Entry<K, ?> instance) throws Throwable {
		try {
			return (K) METHOD_GET_KEY.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetValue() throws Throwable {
		//
		Assertions.assertNull(getValue(null));
		//
		Assertions.assertNull(getValue(entry));
		//
	}

	private static <V> V getValue(final Entry<?, V> instance) throws Throwable {
		try {
			return (V) METHOD_GET_VALUE.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testEntrySet() throws Throwable {
		//
		Assertions.assertNull(entrySet(Reflection.newProxy(Map.class, ih)));
		//
	}

	private static <K, V> Set<Entry<K, V>> entrySet(final Map<K, V> instance) throws Throwable {
		try {
			final Object obj = METHOD_ENTRY_SET.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Set) {
				return (Set) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSorted() throws Throwable {
		//
		Assertions.assertNull(sorted(null, null));
		//
		Assertions.assertNull(sorted(stream, null));
		//
		final Stream<?> stream = Stream.empty();
		//
		Assertions.assertNull(sorted(stream, null));
		//
		Assertions.assertNotNull(sorted(stream, (a, b) -> 0));
		//
	}

	private static <T> Stream<T> sorted(final Stream<T> instance, final Comparator<? super T> comparator)
			throws Throwable {
		try {
			final Object obj = METHOD_SORTED.invoke(null, instance, comparator);
			if (obj == null) {
				return null;
			} else if (obj instanceof Stream) {
				return (Stream) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testForEach() {
		//
		Assertions.assertDoesNotThrow(() -> forEach(stream, null));
		//
		final Stream<?> stream = Stream.empty();
		//
		Assertions.assertDoesNotThrow(() -> forEach(stream, null));
		//
		Assertions.assertDoesNotThrow(() -> forEach(stream, Consumers.nop()));
		//
	}

	private static <T> void forEach(final Stream<T> instance, final Consumer<? super T> action) throws Throwable {
		try {
			METHOD_FOR_EACH.invoke(null, instance, action);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetDeclaredMethod() throws Throwable {
		//
		Assertions.assertNull(getDeclaredMethod(null, null));
		//
	}

	private static Method getDeclaredMethod(final Class<?> instance, final String name,
			final Class<?>... parameterTypes) throws Throwable {
		try {
			final Object obj = METHOD_GET_DECLARED_METHOD.invoke(null, instance, name, parameterTypes);
			if (obj == null) {
				return null;
			} else if (obj instanceof Method) {
				return (Method) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}