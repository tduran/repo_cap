package pl.ing.wad.rpt.report;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Obs³uga wartoœci w plikach CSV.
 * 
 * Na podstawie org.apache.commons.lang.StringEscapeUtils ale do rozdzielania wartosci
 * zamiast przecinka jest u¿yty œrednik. 
 * 
 */
public class CsvUtils {
	

	public static final char CR = '\r';
	public static final char LF = '\n';
	public static final char CSV_DELIMITER = ';';
	
	public static final String NEW_LINE = new String(new char[]{CR, LF});

	private static final char CSV_QUOTE = '"';
	
	private static final char SPACE = ' ';

	private static final char[] CSV_SEARCH_CHARS = new char[] { CSV_DELIMITER,
			CSV_QUOTE, CR, LF };

	public static String escapeCsv(String str) {
		if (containsNone(str, CSV_SEARCH_CHARS)) {
			return str;
		}
		try {
			StringWriter writer = new StringWriter();
			escapeCsv(writer, str);
			return writer.toString();
		} catch (IOException ioe) {
			// this should never ever happen while writing to a StringWriter
			throw new RuntimeException(ioe);
		}
	}

	public static void escapeCsv(Writer out, String str) throws IOException {
		if (containsNone(str, CSV_SEARCH_CHARS)) {
			if (str != null) {
				out.write(str);
			}
			return;
		}
		out.write(CSV_QUOTE);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == CSV_QUOTE) {
				out.write(CSV_QUOTE); // escape double quote
			}else if(c == LF || c == CR){//IWA-2039 nowe linie zamienione na spacje
				c = SPACE;
			}
			out.write(c);
		}
		out.write(CSV_QUOTE);
	}
	
    private static boolean containsNone(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static String stringArrayToCVS(String[] row){
    	StringBuilder sb = new StringBuilder();
    	boolean addSerparator = false;
    	for (String field : row) {
			if(addSerparator){
				sb.append(CsvUtils.CSV_DELIMITER);
			}else{
				addSerparator = true;
			}
			sb.append(CsvUtils.escapeCsv(field));
		}
    	return sb.toString();
    }
}
