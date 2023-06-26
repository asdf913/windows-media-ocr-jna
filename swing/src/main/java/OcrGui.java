import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.meeuw.functional.Predicates;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;
import ocr.Ocr;
import ocr.OcrImpl;

public class OcrGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6359603394039352706L;

	private JTextComponent jtcFile, jtcUrl, jtcText = null;

	private transient ComboBoxModel<?> cbmLanaguageTag = null;

	private AbstractButton abFile, abUrl = null;

	private transient Ocr ocr = null;

	private OcrGui() {
	}

	private void init() {
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
		testAndAccept(predicate, new JLabel("Language Tag"), this::add);
		//
		final Ocr o = getOcr();
		//
		final DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>();
		//
		testAndAccept(Objects::nonNull, o != null ? o.getAvailableRecognizerLanguageTags() : null, dcbm::addAll);
		//
		final String wrap = "wrap";
		//
		final LayoutManager lm = isRootPaneCheckingEnabled() ? getLayout(getContentPane()) : getLayout();
		//
		final boolean isMigLayout = lm instanceof MigLayout;
		//
		final BiPredicate<Component, Object> biPredicate = Predicates.biAlways(isGui, null);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, new JComboBox<>(cbmLanaguageTag = dcbm), wrap, this::add);
			//
		} // if
			//
			// File
			//
		testAndAccept(predicate, new JLabel("File"), this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, jtcFile = new JTextField(), "wmin 200px", this::add);
			//
		} // if
			//
		setEditable(jtcFile, false);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, abFile = new JButton("File"), wrap, this::add);
			//
		} // if
			//
		addActionListener(abFile, this);
		//
		// URL
		//
		testAndAccept(predicate, new JLabel("URL"), this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, this.jtcUrl = new JTextField(), "growx", this::add);
			//
		} // if
			//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, abUrl = new JButton("URL"), wrap, this::add);
			//
		} // if
			//
		addActionListener(abUrl, this);
		//
		testAndAccept(predicate, new JLabel("Text"), this::add);
		//
		if (isMigLayout) {
			//
			testAndAccept(biPredicate, jtcText = new JTextField(), "growx", this::add);
			//
		} // if
			//
		setEditable(jtcText, false);
		//
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
		final OcrGui instance = new OcrGui();
		//
		instance.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//
		instance.setLayout(new MigLayout());
		//
		instance.init();
		//
		instance.pack();
		//
		instance.setVisible(true);
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
			final boolean isHeadless = GraphicsEnvironment.isHeadless();
			//
			final boolean isNotUnderDebugOrTest = Arrays.stream(new Throwable().getStackTrace())
					.noneMatch(x -> Arrays
							.asList("org.eclipse.jdt.internal.junit5.runner.JUnit5TestReference",
									"org.apache.maven.surefire.junitplatform.JUnitPlatformProvider")
							.contains(getClassName(x))
					//
					);
			//
			if (languageTag == null) {
				//
				if (!isHeadless && isNotUnderDebugOrTest) {
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
			if (!isHeadless && isNotUnderDebugOrTest && jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				//
				file = jfc.getSelectedFile();
				//
			} // if
				//
			setText(jtcFile, getAbsolutePath(file));
			//
			try {
				//
				setText(jtcText,
						getOcrText(getOcr(), languageTag, file != null ? FileUtils.readFileToByteArray(file) : null));
				//
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // try
				//
		} else if (Objects.equals(source, abUrl)) {
			//
			final String languageTag = toString(getSelectedItem(cbmLanaguageTag));
			//
			try (final InputStream is = openStream(
					testAndApply(StringUtils::isNotBlank, getText(jtcUrl), URL::new, null))) {
				//
				setText(jtcText, getOcrText(getOcr(), languageTag,
						testAndApply(Objects::nonNull, is, IOUtils::toByteArray, null)));
				//
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
		} // if
			//
	}

	private static InputStream openStream(final URL instance) throws IOException {
		return instance != null ? instance.openStream() : null;
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

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

}