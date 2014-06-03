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

import java.lang.reflect.Method;

public abstract class ThreadWork {
	
	private final Method callMethod;
	private final Method onFailMethod;

	public ThreadWork() {
		callMethod = _resolveFirstMatchingMethod("call");
		onFailMethod = _resolveOnFailMethod();
	}
	
	private Method _resolveFirstMatchingMethod(String methodName) {
		for (Method m : getClass().getDeclaredMethods()) {
			if (m.getName().equals(methodName)) return m;
		}
		return null;
	}
	
	private Method _resolveOnFailMethod() {
		Method m = _resolveFirstMatchingMethod("onFail");
		
		if (m == null) return null;
		if (m.getParameterTypes().length == 0) return null;
		if (m.getParameterTypes()[0].isAssignableFrom(Throwable.class) == false) return null;
		
		return m;
	}

	Method _getCallMethod() {
		return callMethod;
	}
	
	Method _getOnFailMethod() {
		return onFailMethod;
	}
	
	public void test() {
		System.out.println("test");
	}
	
}
