package ocr;

import java.util.List;

public interface Ocr {

	public List<String> getAvailableRecognizerLanguageTags();

	public String getOcrText(final String languageTag, final byte[] bs);

}