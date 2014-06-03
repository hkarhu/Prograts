/* [LGPL] Copyright 2011 Gima

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
package coderats.ar.gl;

public class ThreadWorkRecipeException extends Exception {

	private static final long serialVersionUID = 1L;

	public ThreadWorkRecipeException() {
	}

	public ThreadWorkRecipeException(String message) {
		super(message);
	}

	public ThreadWorkRecipeException(Throwable cause) {
		super(cause);
	}

	public ThreadWorkRecipeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ThreadWorkRecipeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
