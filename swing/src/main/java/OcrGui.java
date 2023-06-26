import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

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
		add(new JLabel("Language Tag"));
		//
		final Ocr ocr = getOcr();
		//
		final List<String> languageTags = ocr != null ? ocr.getAvailableRecognizerLanguageTags() : null;
		//
		final DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>();
		//
		dcbm.addAll(languageTags);
		//
		final String wrap = "wrap";
		//
		final LayoutManager lm = this.isRootPaneCheckingEnabled() ? getLayout(getContentPane()) : this.getLayout();
		//
		final boolean isMigLayout = lm instanceof MigLayout;
		//
		if (isMigLayout) {
			//
			add(new JComboBox<>(cbmLanaguageTag = dcbm), wrap);
			//
		} // if
			//
		add(new JLabel("File"));
		//
		if (isMigLayout) {
			//
			add(jtcFile = new JTextField(), "wmin 200px");
			//
		} // if
			//
		setEditable(jtcFile, false);
		//
		if (isMigLayout) {
			//
			add(abFile = new JButton("File"), wrap);
			//
		} // if
			//
		addActionListener(abFile, this);
		//
		add(new JLabel("Text"));
		//
		if (isMigLayout) {
			//
			add(jtcText = new JTextField(), "growx");
			//
		} // if
			//
		setEditable(jtcText, false);
		//
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
		instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
						.anyMatch(x -> StringUtils.equals("org.eclipse.jdt.internal.junit5.runner.JUnit5TestReference",
								x != null ? x.getClassName() : null))) {
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