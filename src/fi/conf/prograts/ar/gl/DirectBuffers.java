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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


public class DirectBuffers {
	
	/**
	 * Allocate native memory and associate direct {@link ByteBuffer} with it.
	 *  
	 * @param bytes - How many bytes of memory to allocate for the buffer
	 * @return The created {@link ByteBuffer}.
	 */
	public static ByteBuffer allocateByteBuffer(int bytes) {
		long lPtr = Native.malloc(bytes);
		if (lPtr == 0) throw new Error("Failed to allocate direct byte buffer memory");
		return Native.getDirectByteBuffer(lPtr, bytes);
	}
	
	/**
	 * Free native memory inside {@link Buffer}.
	 * <p>
	 * Use only buffers allocated with JNI's NewDirectByteBuffer method, like {@link #allocateByteBuffer(int)}.
	 * Unless if you know that {@link Buffer}s created by other means can be freed as well. I don't.
	 * 
	 * @param buffer - Buffer whose native memory is to be freed. The class instance will remain. Don't use it anymore. 
	 */
	public static void freeNativeBufferMemory(Buffer buffer) {
		buffer.clear();
		Pointer javaPointer = Native.getDirectBufferPointer(buffer);
		long lPtr = Pointer.nativeValue(javaPointer);
		Native.free(lPtr);
	}
}
