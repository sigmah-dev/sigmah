/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet;

import java.util.ArrayList;
import java.util.List;

 
public class CsvParser{

    private boolean hasNext = true;

    private final char separator;

    private final char quotechar;
    
    private final char escape;
    
    private String[] csvLines; 
    
    private int lineCount;
  
    public static final int INITIAL_READ_SIZE = 64;
    
    /**
     * The default escape character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_ESCAPE_CHARACTER = '\\';    
    
    public CsvParser() {
      	this.separator = CsvBuilder.DEFAULT_SEPARATOR;
        this.quotechar =  CsvBuilder.DEFAULT_QUOTE_CHARACTER;
        this.escape =  DEFAULT_ESCAPE_CHARACTER;
    }
    
 
    /*
     * Parses CSV string
     * returns list of arrays which represents each line of CSV
     */
    public List<String[]> parseCsv(String csvString) {
    	hasNext=true;
    	csvLines = csvString.split(CsvBuilder.DEFAULT_LINE_END);
    	lineCount=0;
    	
        List<String[]> allElements = new ArrayList<String[]>();
        while (hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null)
                allElements.add(nextLineAsTokens);
        }
        return allElements;

    }
 
    private String[] readNext()   {

        String nextLine = getNextLine();
        return hasNext ? parseLine(nextLine) : null;
    }
 
    private String getNextLine()  {
    	 
    	String nextLine = null;
    	
    	if(lineCount <= csvLines.length-1){
    		nextLine=csvLines[lineCount++];
    	}
        
        if (nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    /**
     * Parses an incoming String and returns an array of elements.
     * 
     * @param nextLine
     *            the string to parse
     * @return the comma-tokenized list of elements, or null if nextLine is null
     */
    private String[] parseLine(String nextLine)  {

        if (nextLine == null) {
            return null;
        }

        List<String>tokensOnThisLine = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(INITIAL_READ_SIZE);
        boolean inQuotes = false;
        do {
        	if (inQuotes) {
                // continuing a quoted section, reappend newline
                sb.append("\n");
                nextLine = getNextLine();
                if (nextLine == null)
                    break;
            }
            for (int i = 0; i < nextLine.length(); i++) {

                char c = nextLine.charAt(i);
                if (c == this.escape) {
                	if( isEscapable(nextLine, inQuotes, i) ){ 
                		sb.append(nextLine.charAt(i+1));
                		i++;
                	} else {
                		i++; // ignore the escape
                	}
                } else if (c == quotechar) {
                	if( isEscapedQuote(nextLine, inQuotes, i) ){ 
                		sb.append(nextLine.charAt(i+1));
                		i++;
                	}else{
                		inQuotes = !inQuotes;
                		// the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                		if(i>2 //not on the beginning of the line
                				&& nextLine.charAt(i-1) != this.separator //not at the beginning of an escape sequence 
                				&& nextLine.length()>(i+1) &&
                				nextLine.charAt(i+1) != this.separator //not at the	end of an escape sequence
                		){
                			sb.append(c);
                		}
                	}
                } else if (c == separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString());
                    sb = new StringBuilder(INITIAL_READ_SIZE); // start work on next token
                } else {
                    sb.append(c);
                }
            }
        } while (inQuotes);
        tokensOnThisLine.add(sb.toString());
        return tokensOnThisLine.toArray(new String[0]);

    }

	/**  
	 * precondition: the current character is a quote or an escape
	 * @param nextLine the current line
	 * @param inQuotes true if the current context is quoted
	 * @param i current index in line
	 * @return true if the following character is a quote
	 */
	private boolean isEscapedQuote(String nextLine, boolean inQuotes, int i) {
		return inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
		    && nextLine.length() > (i+1)  // there is indeed another character to check.
		    && nextLine.charAt(i+1) == quotechar;
	}

	/**  
	 * precondition: the current character is an escape
	 * @param nextLine the current line
	 * @param inQuotes true if the current context is quoted
	 * @param i current index in line
	 * @return true if the following character is a quote
	 */
	private boolean isEscapable(String nextLine, boolean inQuotes, int i) {
		return inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
		    && nextLine.length() > (i+1)  // there is indeed another character to check.
		    && ( nextLine.charAt(i+1) == quotechar || nextLine.charAt(i+1) == this.escape);
	}

    
}

