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
package fi.conf.prograts.ar.gl;

import java.util.Iterator;
import java.util.LinkedList;

public class ListenerManager<T> implements Iterable<T> {
	
	private final LinkedList<T> listeners;

	public ListenerManager() {
		listeners = new LinkedList<T>();
	}

	public void add(T listener) {
		listeners.add(listener);
	}

	public void remove(T listener) {
		listeners.remove(listener);
	}

	@Override
	public Iterator<T> iterator() {
		return listeners.iterator();
	}

	public LinkedList<T> getListeners() {
		return listeners;
	}
	
}
