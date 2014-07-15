/* [LGPL] Copyright 2010, 2011 Gima

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package fi.conf.prograts.ar.gl;

import java.io.PrintStream;

/**
 * Shortcuts for string output
 * 
 * @author Gima
 */
public class S {
	
	/** Print a formatted message {@link String#format(String, Object...) String.format(format, args)} with {@link PrintStream#println(String) System.out.println()} */
	public static void printf(String format, Object... args) {
		System.out.println(String.format(format, args));
	}
	
	/** Print a formatted message {@link String#format(String, Object...) String.format(format, args)} with {@link PrintStream#print(String) System.out.print()} */
	public static void printfn(String format, Object... args) {
		System.out.print(String.format(format, args));
	}
	
	/** Print a formatted message {@link String#format(String, Object...) String.format(format, args)} with {@link PrintStream#printl(String) System.err.println()} */
	public static void eprintf(String format, Object... args) {
		System.err.println(String.format(format, args));
	}
	
	/** Print a formatted message {@link String#format(String, Object...) String.format(format, args)} with {@link PrintStream#print(String) System.err.print()} */
	public static void eprintfn(String format, Object... args) {
		System.err.println(String.format(format, args));
	}
	
	/** Return as {@link String}  a formatted message {@link String#format(String, Object...) String.format(format, args)} */
	public static String sprintf(String format, Object... args) {
		return String.format(format, args);
	}
	
	/** Concatenate String array elements to a String, separating each with the specified separator */
	public static String implode(String separator, String[] stringArray) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i<stringArray.length; i++) {
			stringBuilder.append(stringArray[i]);
			stringBuilder.append(separator);
		}
		
		if (stringBuilder.length() >= separator.length()) {
			stringBuilder.setLength(stringBuilder.length() - separator.length());
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * prefox with with eclipse-clickable file name and line number.
	 * add given arguments to output
	 */
	public static void funcArgs(Object... args) {
		StackTraceElement t = new Throwable().getStackTrace()[1];
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<args.length; i++) {
			if (args[i] != null) sb.append(args[i].toString());
			else sb.append("null");
			sb.append(", ");
		}
		
		if (args.length > 0) sb.setLength(sb.length()-2);
		
		S.printf("(%s:%d) %s(%s)",
				t.getFileName(),
				t.getLineNumber(),
				t.getMethodName(),
				sb.toString()
				);
	}
	
	/**
	 * prefix with eclipse-clickable file name and line number 
	 */
	public static void debug(String format, Object... args) {
		StackTraceElement t = new Throwable().getStackTrace()[1];
		
		S.printf("(%s:%d): %s",
				t.getFileName(),
				t.getLineNumber(),
				S.sprintf(format, args)
				);
	}
	
	/**
	 * prefix with eclipse-clickable file name, line number and function name 
	 */
	public static void debugFunc(String format, Object... args) {
		StackTraceElement t = new Throwable().getStackTrace()[1];
		
		S.printf("(%s:%d) %s: %s",
				t.getFileName(),
				t.getLineNumber(),
				t.getMethodName(),
				S.sprintf(format, args)
				);
	}
	
}
