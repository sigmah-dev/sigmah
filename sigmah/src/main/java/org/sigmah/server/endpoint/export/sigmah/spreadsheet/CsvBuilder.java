package org.sigmah.server.endpoint.export.sigmah.spreadsheet;

import java.util.List;

 
public class CsvBuilder {
    
    public static final int INITIAL_STRING_SIZE = 128;	

    private char separator;

    private char quotechar;
    
    private char escapechar;
    
    private String lineEnd;

    /** The character used for escaping quotes. */
    public static final char DEFAULT_ESCAPE_CHARACTER = '"';

    /** The default separator to use if none is supplied to the constructor. */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    
    /** The quote constant to use when you wish to suppress all quoting. */
    public static final char NO_QUOTE_CHARACTER = '\u0000';
    
    /** The escape constant to use when you wish to suppress all escaping. */
    public static final char NO_ESCAPE_CHARACTER = '\u0000';
    
    /** Default line terminator uses platform encoding. */
    public static final String DEFAULT_LINE_END = "\n";
    
    
    public CsvBuilder() { 
        this.separator = DEFAULT_SEPARATOR;
        this.quotechar = DEFAULT_QUOTE_CHARACTER;
        this.escapechar = DEFAULT_ESCAPE_CHARACTER;
     	this.lineEnd = DEFAULT_LINE_END;
    }
   
    /*
     * For given list of arrays builds CSV string 
     */
    public String buildCsv(List<String[]> allLines)  {
    	final StringBuilder container=new StringBuilder(INITIAL_STRING_SIZE);
    	for (String[] line : allLines) {
			buildLine(line,container);
		}
    	
    	return container.toString();
    }
 
    private void buildLine(String[] nextLine,StringBuilder  container) {
    	
    	if (nextLine == null)
    		return;
    	
        StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(separator);
            }

            String nextElement = nextLine[i];
            if (nextElement == null)
                continue;
            if (quotechar !=  NO_QUOTE_CHARACTER)
            	sb.append(quotechar);
            
            sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);

            if (quotechar != NO_QUOTE_CHARACTER)
            	sb.append(quotechar);
        }
        
        sb.append(lineEnd);
        
        container.append(sb);
    }

	private boolean stringContainsSpecialCharacters(String line) {
	    return line.indexOf(quotechar) != -1 || line.indexOf(escapechar) != -1;
    }

	private StringBuilder processLine(String nextElement)
    {
		StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
	    for (int j = 0; j < nextElement.length(); j++) {
	        char nextChar = nextElement.charAt(j);
	        if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
	        	sb.append(escapechar).append(nextChar);
	        } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
	        	sb.append(escapechar).append(nextChar);
	        } else {
	            sb.append(nextChar);
	        }
	    }
	    
	    return sb;
    }

    
}


