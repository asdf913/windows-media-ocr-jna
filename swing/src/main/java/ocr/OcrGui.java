package ocr;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
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

import javax.activation.MimeType;
import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meeuw.functional.Predicates;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;

public class OcrGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6359603394039352706L;

	private static final Logger LOG = LogManager.getLogger(OcrGui.class);

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Note {
		String value();
	}

	@Note("File")
	private JTextComponent jtcFile = null;

	@Note("URL")
	private JTextComponent jtcUrl = null;

	private JTextComponent jtcText = null;

	private transient ComboBoxModel<?> cbmLanaguageTag = null;

	private JComboBox<?> jcbLanaguageTag = null;

	@Note("File")
	private AbstractButton abFile = null;

	@Note("URL")
	private AbstractButton abUrl = null;

	private AbstractButton abCopyText = null;

	private JLabel jlLanguageTag = null;

	private DefaultTableModel dtmResponseHeaders = null;

	private transient Ocr ocr = null;

	private OcrGui() {
	}

	private void init(final Map<?, ?> map) {
		//
		// If "java.awt.Container.component" is null, return this method immediately
		//
		// The below check is for "-Djava.awt.headless=true"
		//
		final List<Field> fs = toList(filter(stream(FieldUtils.getAllFieldsList(getClass(this))),
				f -> Objects.equals(getName(f), "component")));
		//
		final Field f = IterableUtils.size(fs) == 1 ? IterableUtils.get(fs, 0) : null;
		//
		final boolean isGui = f == null || Narcissus.getObjectField(this, f) != null;
		//
		final Predicate<Component> predicate = Predicates.always(isGui, null);
		//
		// Language Tag
		//
		testAndAccept(predicate, new JLabel("Language Tag"), this::add);
		//
		final Ocr o = getOcr();
		//
		final DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>();
		//
		testAndAccept(Objects::nonNull, getAvailableRecognizerLanguageTags(o), dcbm::addAll);
		//
		testAndAccept(x -> containsKey(map, x), "languageTag", x -> dcbm.setSelectedItem(get(map, "languageTag")));
		//
		final String wrap = "wrap";
		//
		final LayoutManager lm = isRootPaneCheckingEnabled() ? getLayout(getContentPane()) : getLayout();
		//
		final boolean isMigLayout = lm instanceof MigLayout;
		//
		final BiPredicate<Component, Object> biPredicate = Predicates.biAlways(isGui, null);
		//
		(jcbLanaguageTag = new JComboBox<>(cbmLanaguageTag = dcbm)).addActionListener(this);
		//
		testAndAccept(predicate, jcbLanaguageTag, this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, jlLanguageTag = new JLabel(), wrap, this::add);
			//
		} // if
			//
		actionPerformed(new ActionEvent(jcbLanaguageTag, 0, null));
		//
		// File
		//
		testAndAccept(predicate, new JLabel("File"), this::add);
		//
		final String growx = "growx";
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, jtcFile = new JTextField(),
					String.format("%1$s,wmin %2$spx,span %3$s", growx, 200, 2), this::add);
			//
		} // if
			//
		setEditable(jtcFile, false);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, abFile = new JButton("File"), String.join(",", wrap, growx), this::add);
			//
		} // if
			//
			// URL
			//
		testAndAccept(predicate, new JLabel("URL"), this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, this.jtcUrl = new JTextField(toString(get(map, "url"))),
					String.format("%1$s,span %2$s", growx, 2), this::add);
			//
		} // if
			//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, abUrl = new JButton("URL"), String.join(",", wrap, growx), this::add);
			//
		} // if
			//
		testAndAccept(predicate, new JLabel("Text"), this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, jtcText = new JTextField(), String.format("%1$s,span %2$s", growx, 2),
					this::add);
			//
		} // if
			//
		setEditable(jtcText, false);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, abCopyText = new JButton("Copy"), String.join(",", wrap, growx), this::add);
			//
		} // if
			//
		testAndAccept(predicate, new JLabel("HTTP Response Header(s)"), this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate,
					new JScrollPane(
							new JTable(dtmResponseHeaders = new DefaultTableModel(new Object[] { "Key", "Value" }, 0))),
					String.format("span %1$s", 2), this::add);
			//
		} // if
			//
		addActionListener(this, abFile, abUrl, abCopyText);
		//
	}

	private static List<String> getAvailableRecognizerLanguageTags(final Ocr instance) {
		return instance != null ? instance.getAvailableRecognizerLanguageTags() : null;
	}

	private static boolean containsKey(final Map<?, ?> instance, final Object key) {
		return instance != null && instance.containsKey(key);
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static <T, E extends Throwable> void testAndAccept(final Predicate<T> predicate, final T value,
			final FailableConsumer<T, E> consumer) throws E {
		if (test(predicate, value) && consumer != null) {
			consumer.accept(value);
		}
	}

	private static final <T> boolean test(final Predicate<T> instance, final T value) {
		return instance != null && instance.test(value);
	}

	private static <T, U> void testAndAccept(final BiPredicate<T, U> predicate, final T t, final U u,
			final BiConsumer<T, U> consumer) {
		if (predicate != null && predicate.test(t, u) && consumer != null) {
			consumer.accept(t, u);
		}
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static <E> Stream<E> stream(final Collection<E> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		//
		return instance != null && (predicate != null || Proxy.isProxyClass(getClass(instance)))
				? instance.filter(predicate)
				: null;
		//
	}

	private static <T> List<T> toList(final Stream<T> instance) {
		return instance != null ? instance.toList() : null;
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static void setEditable(final JTextComponent instance, final boolean b) {
		if (instance != null) {
			instance.setEditable(b);
		}
	}

	private static void addActionListener(final ActionListener actionListener, final AbstractButton a,
			final AbstractButton b, final AbstractButton... abs) {
		//
		addActionListener(a, actionListener);
		//
		addActionListener(b, actionListener);
		//
		for (int i = 0; abs != null && i < abs.length; i++) {
			//
			addActionListener(abs[i], actionListener);
			//
		} // for
			//
	}

	private static void addActionListener(final AbstractButton instance, final ActionListener actionListener) {
		if (instance != null) {
			instance.addActionListener(actionListener);
		}
	}

	private static LayoutManager getLayout(final Container instance) {
		return instance != null ? instance.getLayout() : null;
	}

	public Ocr getOcr() {
		if (ocr == null) {
			ocr = new OcrImpl();
		}
		return ocr;
	}

	public static void main(final String[] args) {
		//
		final boolean isHeadless = GraphicsEnvironment.isHeadless();
		//
		final OcrGui instance = isHeadless ? cast(OcrGui.class, Narcissus.allocateInstance(OcrGui.class))
				: new OcrGui();
		//
		if (instance != null) {
			//
			instance.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			//
			instance.setLayout(new MigLayout());
			//
			instance.init(createProperties(new File("OcrGui.properties")));
			//
			if (Boolean.logicalAnd(!isHeadless, !isUnderDebugOrMaven())) {
				//
				instance.pack();
				//
				instance.setVisible(true);
				//
			} // if
				//
		} // if
			//
	}

	private static Properties createProperties(final File file) {
		//
		Properties properties = null;
		//
		try (final InputStream is = testAndApply(f -> f != null && f.exists() && f.isFile() && f.canRead(), file,
				FileInputStream::new, null)) {
			//
			if (is != null) {
				//
				(properties = new Properties()).load(is);
				//
			} // if
				//
		} catch (final IOException e) {
			//
			showExceptionOrErrorOrPrintStackTrace(LOG, e);
			//
		} // try
			//
		return properties;
		//
	}

	private static void showExceptionOrErrorOrPrintStackTrace(final Logger logger, final Throwable throwable) {
		//
		if (throwable != null) {
			//
			if (throwable.getMessage() == null) {
				//
				try {
					//
					Narcissus.setObjectField(throwable, Throwable.class.getDeclaredField("detailMessage"), "");
					//
				} catch (final NoSuchFieldException e) {
					//
					showExceptionOrErrorOrPrintStackTrace(logger, e);
					//
				} // try
					//
			} // if
				//
			if (!GraphicsEnvironment.isHeadless() && !isUnderDebugOrMaven()) {
				//
				TaskDialogs.showException(throwable);
				//
				return;
				//
			} // if
				//
		} // if
			//
		if (logger != null) {
			//
			logger.error(throwable);
			//
		} else if (throwable != null) {
			//
			throwable.printStackTrace();
			//
		} // if
			//
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		//
		final Object source = getSource(evt);
		//
		if (Objects.equals(source, abFile)) {
			//
			final String languageTag = toString(getSelectedItem(cbmLanaguageTag));
			//
			final boolean isNotHeadLessAndisNotUnderDebugOrTest = Boolean.logicalAnd(!GraphicsEnvironment.isHeadless(),
					!isUnderDebugOrMaven());
			//
			if (languageTag == null) {
				//
				if (isNotHeadLessAndisNotUnderDebugOrTest) {
					//
					JOptionPane.showMessageDialog(null, "Please select a Language Tag");
					//
				} // if
					//
				return;
				//
			} // if
				//
			final JFileChooser jfc = new JFileChooser(".");
			//
			File file = null;
			//
			if (isNotHeadLessAndisNotUnderDebugOrTest && jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				//
				file = jfc.getSelectedFile();
				//
			} // if
				//
			setText(jtcFile, getAbsolutePath(file));
			//
			try {
				//
				setText(jtcText, getOcrText(getOcr(), languageTag,
						testAndApply(Objects::nonNull, file, FileUtils::readFileToByteArray, null)));
				//
			} catch (final IOException e) {
				//
				showExceptionOrErrorOrPrintStackTrace(LOG, e);
				//
			} // try
				//
		} else if (Objects.equals(source, abUrl)) {
			//
			actionPerformedAbUrl();
			//
		} else if (Objects.equals(source, abCopyText)) {
			//
			actionPerformedAbCopyText();
			//
		} else if (Objects.equals(source, jcbLanaguageTag)) {
			//
			actionPerformeJcbLanaguageTag();
			//
		} // if
			//

	}

	private static boolean isUnderDebugOrMaven() {
		//
		return Arrays.stream(new Throwable().getStackTrace())
				.anyMatch(x -> Arrays
						.asList("org.eclipse.jdt.internal.junit5.runner.JUnit5TestReference",
								"org.apache.maven.surefire.junitplatform.JUnitPlatformProvider")
						.contains(getClassName(x)));
		//
	}

	private void actionPerformedAbUrl() {
		//
		for (int i = (dtmResponseHeaders != null ? dtmResponseHeaders.getRowCount() : 0) - 1; i >= 0; i--) {
			//
			dtmResponseHeaders.removeRow(i);
			//
		} // for
			//
		setText(jtcText, null);
		//
		HttpURLConnection httpURLConnection = null;
		//
		try {
			//
			httpURLConnection = cast(HttpURLConnection.class,
					openConnection(testAndApply(StringUtils::isNotBlank, getText(jtcUrl), URL::new, null)));
			//
		} catch (final IOException e) {
			//
			filterStackTrace(e, OcrGui.class);
			//
			showExceptionOrErrorOrPrintStackTrace(LOG, e);
			//
		}
		//
		try (final InputStream is = getInputStream(httpURLConnection)) {
			//
			final Map<String, List<String>> headerFields = getHeaderFields(httpURLConnection);
			//
			forEach(sorted(stream(entrySet(headerFields)), (a, b) -> StringUtils.compare(getKey(a), getKey(b))),
					en -> dtmResponseHeaders.addRow(new Object[] { getKey(en), getValue(en) }));
			//
			final List<String> contentTypes = testAndApply(x -> containsKey(headerFields, x), "Content-Type",
					x -> get(headerFields, x), null);
			//
			if (contentTypes != null && contentTypes.size() == 1) {
				//
				final String primaryType = getPrimaryType(
						testAndApply(StringUtils::isNotBlank, contentTypes.get(0), MimeType::new, null));
				//
				if (!StringUtils.equalsIgnoreCase(primaryType, "image") && JOptionPane.showConfirmDialog(null,
						String.format("The \"Content-Type\" header return \"%1$s\".%2$sContinue?", primaryType,
								System.lineSeparator()),
						"Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					//
					return;
					//
				} // if
					//
			} // if
				//
			setText(jtcText, getOcrText(getOcr(), toString(getSelectedItem(cbmLanaguageTag)),
					testAndApply(Objects::nonNull, is, IOUtils::toByteArray, null)));
			//
		} catch (final Throwable e) {
			//
			filterStackTrace(e, OcrGui.class);
			//
			showExceptionOrErrorOrPrintStackTrace(LOG, e);
			//
		} // try
			//
	}

	private static String getPrimaryType(final MimeType instance) {
		return instance != null ? instance.getPrimaryType() : null;
	}

	private static <K> K getKey(final Entry<K, ?> instance) {
		return instance != null ? instance.getKey() : null;
	}

	private static <V> V getValue(final Entry<?, V> instance) {
		return instance != null ? instance.getValue() : null;
	}

	private static <K, V> Set<Entry<K, V>> entrySet(final Map<K, V> instance) {
		return instance != null ? instance.entrySet() : null;
	}

	private static <T> Stream<T> sorted(final Stream<T> instance, final Comparator<? super T> comparator) {
		//
		return instance != null && (comparator != null || Proxy.isProxyClass(getClass(instance)))
				? instance.sorted(comparator)
				: null;
		//
	}

	private static <T> void forEach(final Stream<T> instance, final Consumer<? super T> action) {
		//
		if (instance != null && (action != null || Proxy.isProxyClass(getClass(instance)))) {
			//
			instance.forEach(action);
			//
		} // if
			//
	}

	private static URLConnection openConnection(final URL instance) throws IOException {
		return instance != null ? instance.openConnection() : null;
	}

	private static InputStream getInputStream(final URLConnection instance) throws IOException {
		return instance != null ? instance.getInputStream() : null;
	}

	private static Map<String, List<String>> getHeaderFields(final HttpURLConnection instance) {
		return instance != null ? instance.getHeaderFields() : null;
	}

	private static <T> T cast(final Class<T> clz, final Object value) {
		return clz != null && clz.isInstance(value) ? clz.cast(value) : null;
	}

	private static void filterStackTrace(final Throwable throwable, final Class<?> clz) {
		//
		final StackTraceElement[] stes = throwable != null ? throwable.getStackTrace() : null;
		//
		Integer index = null;
		//
		for (int i = (stes != null ? stes.length : 0) - 1; i >= 0; i--) {
			//
			if (Objects.equals(clz, forName(getClassName(stes != null ? stes[i] : null)))) {
				//
				index = Integer.valueOf(i);
				//
				break;
				//
			} // if
				//
		} // for
			//
		if (index != null) {
			//
			try {
				//
				Narcissus.setObjectField(throwable, Throwable.class.getDeclaredField("stackTrace"),
						ArrayUtils.subarray(stes, 0, index.intValue() + 1));
				//
			} catch (final NoSuchFieldException ex) {
				//
				showExceptionOrErrorOrPrintStackTrace(LOG, ex);
				//
			} // try
				//
		} // if
			//
	}

	private void actionPerformedAbCopyText() {
		//
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		//
		final Class<?> clz = getClass(toolkit);
		//
		try {
			//
			if (isRaiseThrowableOnly(clz, getDeclaredMethod(clz, "getSystemClipboard"))) {
				//
				return;
				//
			} // if
				//
		} catch (final NoSuchMethodException e) {
			//
			showExceptionOrErrorOrPrintStackTrace(LOG, e);
			//
		} // try
			//
		if (!isUnderDebugOrMaven()) {
			//
			setContents(getSystemClipboard(toolkit), new StringSelection(getText(jtcText)), null);
			//
		} // if
			//
	}

	private static Method getDeclaredMethod(final Class<?> instance, final String name,
			final Class<?>... parameterTypes) throws NoSuchMethodException {
		return instance != null ? instance.getDeclaredMethod(name, parameterTypes) : null;
	}

	private void actionPerformeJcbLanaguageTag() {
		//
		final Locale locale = testAndApply(Objects::nonNull, toString(getSelectedItem(jcbLanaguageTag)),
				Locale::forLanguageTag, null);
		//
		if (locale != null) {
			//
			final String displayLanguage1 = locale.getDisplayLanguage();
			//
			final StringBuilder sb = new StringBuilder(StringUtils.defaultIfBlank(displayLanguage1, ""));
			//
			final String displayLanguage2 = locale.getDisplayLanguage(locale);
			//
			testAndAccept(
					Predicates.always(Boolean.logicalAnd(StringUtils.isNotBlank(displayLanguage2),
							!StringUtils.equalsIgnoreCase(displayLanguage1, displayLanguage2)), null),
					displayLanguage2, x -> sb.append(StringUtils.joinWith("", '/', x)));
			//
			testAndAccept(StringUtils::isNotBlank, locale.getDisplayScript(locale),
					x -> sb.append(String.format(" (%1$s)", x)));
			//
			setText(jlLanguageTag, toString(sb));
			//
		} // if
			//
	}

	private static boolean isRaiseThrowableOnly(final Class<?> clz, final Method method) {
		//
		try (final InputStream is = getResourceAsStream(clz,
				String.format("/%1$s.class", StringUtils.replace(getName(clz), ".", "/")))) {
			//
			final org.apache.bcel.classfile.Method m = getMethod(
					parse(testAndApply(Objects::nonNull, is, x -> new ClassParser(x, null), null)), method);
			//
			final MethodGen mg = testAndApply(Objects::nonNull, m, x -> new MethodGen(x, null, null), null);
			//
			final InstructionList il = mg != null ? mg.getInstructionList() : null;
			//
			final Instruction[] ins = il != null ? il.getInstructions() : null;
			//
			final ConstantPool cp = m != null ? m.getConstantPool() : null;
			//
			ConstantPoolGen cpg = null;
			//
			final int length = ins != null ? ins.length : 0;
			//
			String className = null;
			//
			for (int i = 0; i < length; i++) {
				//
				if (ins[i] instanceof InvokeInstruction ii) {
					//
					className = getClassName(ii, cpg = ObjectUtils.getIfNull(cpg,
							() -> testAndApply(Objects::nonNull, cp, ConstantPoolGen::new, null)));
					//
				} // if
					//
			} // for
				//
				// The below method
				//
				// void methodA(){throw new RuntimeException();}
				//
				// generates
				//
				// new[187](3) 371
				// dup[89](1)
				// invokespecial[183](3) 373
				// athrow[191](1)
				//
				// instructions
				//
			if (Objects.equals(Arrays.asList(NEW.class, DUP.class, INVOKESPECIAL.class, ATHROW.class),
					toList(map(testAndApply(Objects::nonNull, ins, Arrays::stream, null), OcrGui::getClass)))) {
				//
				final Class<?> c = forName(className);
				//
				if (isAssignableFrom(Throwable.class, c)) {
					//
					return true;
					//
				} // if
					//
			} // if
				//
		} catch (final IOException e) {
			//
			showExceptionOrErrorOrPrintStackTrace(LOG, e);
			//
		} // try
			//
		return false;
		//
	}

	private static boolean isAssignableFrom(final Class<?> a, final Class<?> b) {
		return a != null && b != null && a.isAssignableFrom(b);
	}

	private static String getClassName(final FieldOrMethod instance, final ConstantPoolGen cpg) {
		return instance != null ? instance.getClassName(cpg) : null;
	}

	private static org.apache.bcel.classfile.Method getMethod(final JavaClass instance, final Method method) {
		return instance != null && method != null ? instance.getMethod(method) : null;
	}

	private static InputStream getResourceAsStream(final Class<?> instance, final String name) {
		return instance != null && name != null ? instance.getResourceAsStream(name) : null;
	}

	private static <T, R> Stream<R> map(final Stream<T> instance, final Function<? super T, ? extends R> mapper) {
		//
		return instance != null && (Proxy.isProxyClass(getClass(instance)) || mapper != null) ? instance.map(mapper)
				: null;
		//
	}

	private static Class<?> forName(final String className) {
		try {
			return StringUtils.isNotBlank(className) ? Class.forName(className) : null;
		} catch (final ClassNotFoundException e) {
			return null;
		}
	}

	private static JavaClass parse(final ClassParser instance) throws IOException {
		return instance != null ? instance.parse() : null;
	}

	private static void setContents(final Clipboard instance, final Transferable contents, final ClipboardOwner owner) {
		if (instance != null) {
			instance.setContents(contents, owner);
		}
	}

	private static Clipboard getSystemClipboard(final Toolkit instance) {
		return instance != null ? instance.getSystemClipboard() : null;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse) throws E {
		return predicate != null && predicate.test(value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> instance, final T value)
			throws E {
		return instance != null ? instance.apply(value) : null;
	}

	private static String getClassName(final StackTraceElement instance) {
		return instance != null ? instance.getClassName() : null;
	}

	private static String getOcrText(final Ocr instance, final String languageTag, final byte[] bs) {
		return instance != null ? instance.getOcrText(languageTag, bs) : null;
	}

	private static Object getSelectedItem(final ComboBoxModel<?> instance) {
		return instance != null ? instance.getSelectedItem() : null;
	}

	private static Object getSelectedItem(final JComboBox<?> instance) {
		return instance != null ? instance.getSelectedItem() : null;
	}

	private static Object getSource(final EventObject instance) {
		return instance != null ? instance.getSource() : null;
	}

	private static String getAbsolutePath(final File instance) {
		return instance != null ? instance.getAbsolutePath() : null;
	}

	private static String getText(final JTextComponent instance) {
		return instance != null ? instance.getText() : null;
	}

	private static void setText(final JTextComponent instance, final String text) {
		if (instance != null) {
			instance.setText(text);
		}
	}

	private static void setText(final JLabel instance, final String text) {
		if (instance != null) {
			instance.setText(text);
		}
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

}