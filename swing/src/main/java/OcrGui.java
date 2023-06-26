import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
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
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.meeuw.functional.Predicates;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;
import ocr.Ocr;
import ocr.OcrImpl;

public class OcrGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6359603394039352706L;

	private JTextComponent jtcFile, jtcText = null;

	private ComboBoxModel<?> cbmLanaguageTag = null;

	private AbstractButton abFile = null;

	private Ocr ocr = null;

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
		final Ocr ocr = getOcr();
		//
		final DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>();
		//
		testAndAccept(Objects::nonNull, ocr != null ? ocr.getAvailableRecognizerLanguageTags() : null, dcbm::addAll);
		//
		final String wrap = "wrap";
		//
		final LayoutManager lm = this.isRootPaneCheckingEnabled() ? getLayout(getContentPane()) : this.getLayout();
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
		if (Objects.equals(evt != null ? evt.getSource() : null, abFile)) {
			//
			final String languageTag = cbmLanaguageTag != null ? toString(cbmLanaguageTag.getSelectedItem()) : null;
			//
			if (languageTag == null) {
				//
				if (!GraphicsEnvironment.isHeadless() && !Arrays.stream(new Throwable().getStackTrace())
						.anyMatch(x -> Arrays
								.asList("org.eclipse.jdt.internal.junit5.runner.JUnit5TestReference",
										"org.apache.maven.surefire.junitplatform.JUnitPlatformProvider")
								.contains(x != null ? x.getClassName() : null)
						//
						)) {
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
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				//
				file = jfc.getSelectedFile();
				//
			} // if
				//
			setText(jtcFile, getAbsolutePath(file));
			//
			try {
				//
				final Ocr ocr = getOcr();
				//
				setText(jtcText,
						ocr != null ? ocr.getOcrText(
								cbmLanaguageTag != null ? toString(cbmLanaguageTag.getSelectedItem()) : null,
								file != null ? FileUtils.readFileToByteArray(file) : null) : null);
				//
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // try
				//
		} // if
			//
	}

	private static String getAbsolutePath(final File instance) {
		return instance != null ? instance.getAbsolutePath() : null;
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