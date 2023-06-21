package ocr;

import java.util.List;

public interface Ocr {

	public List<String> getAvailableRecognizerLanguageTags();

	public String getOcrText(final String languageTag, final byte[] bs);

	public List<String> getOcrLines(final String languageTag, final byte[] bs);

}