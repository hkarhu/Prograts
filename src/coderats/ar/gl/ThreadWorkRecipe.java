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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ThreadWorkRecipe {
	
	private final ConcurrentLinkedQueue<WorkMetadata> worklist;
	private final Object recipeResultLock;
	private volatile AtomicReferenceArray<Object> recipeResult;
	
	public ThreadWorkRecipe() {
		worklist = new ConcurrentLinkedQueue<>();
		recipeResultLock = new Exchanger<>();
	}

	public void add(ThreadWork work, ExecutorService executorService, Object... additionalParams) {
		worklist.add(new WorkMetadata(work, executorService, additionalParams, worklist.size()+1));
	}

	public AtomicReferenceArray<Object> getResultingCallParams() throws InterruptedException {
		synchronized (recipeResultLock) {
			if (recipeResult == null) recipeResultLock.wait();
		}
		return recipeResult;
	}
	
	public AtomicReferenceArray<Object> loopPollResultingCallParams(DrainableExecutorService drainableExecutorService) throws InterruptedException {
		while (recipeResult == null) {
			drainableExecutorService.executePending();
			Thread.sleep(10);
		}
		return recipeResult;
	}
	
	public void nextWork(Object... callParams) {
		
		final WorkMetadata nextMeta = worklist.poll();
		if (nextMeta == null) {
			recipeResult = new AtomicReferenceArray<>(callParams);
			synchronized (recipeResultLock) {
				recipeResultLock.notifyAll();
			}
			return;
		}
		
		try {
			_fireNext(nextMeta, callParams);
		}
		catch (RejectedExecutionException e) {
			if (nextMeta.executorService.isShutdown()) return;
			else throw e;
		}
	}

	private void _fireNext(final WorkMetadata meta, final Object[] callParams) {
		meta.executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					_run();
				}
				catch (Throwable e) {
					// exceptional situation, catch it and forward it
					try {
						Method onFailMethod = meta.work._getOnFailMethod();
						onFailMethod.setAccessible(true); // anonymous inner class is package private
						onFailMethod.invoke(meta.work, e.getCause());
					}
					catch (Exception e2) {
						e.printStackTrace();
					}
				}
			}
			
			public void _run() {
				try {
					Object[] params = concatParams(callParams, meta.additionalParams);
//					System.out.println("1.............");
//					for(Object p : params) System.out.println(p.getClass().getName());
//					System.out.println("2.............");
					Method callMethod = meta.work._getCallMethod();
					callMethod.setAccessible(true); // anonymous inner class is package private
					callMethod.invoke(meta.work, params);
				}
				catch (IllegalAccessException e) {
					// don't handle this, not my business
					e.printStackTrace();
				}
				catch (IllegalArgumentException e) {
					S.eprintf("nextCall IllegalArgumentException:");
					for (Object o : meta.additionalParams) {
						System.err.println(o);
					}
					S.eprintf(":nextCall IllegalArgumentException");
					System.err.flush();
					new ThreadWorkRecipeException(S.sprintf(
							"Work #%d: nextCall() parameters don't match next ThreadWork's 'call' method parameters",
							meta.workNum
							), e).printStackTrace();
				}
				catch (InvocationTargetException e) {
					// runnable threw an exception, try to pass it to the onFail handler
					try {
						Method onFailMethod = meta.work._getOnFailMethod();
						if (onFailMethod != null) {
							onFailMethod.setAccessible(true); // anonymous inner class is package private
							onFailMethod.invoke(meta.work, e.getCause());
							return;
						}
					}
					catch (Exception e2) {
						e2.printStackTrace();
					}
					e.getCause().printStackTrace();
				}
				
			} // method
		});
	}

	protected Object[] concatParams(Object[] callParams, Object[] additionalParams) {
		if (additionalParams.length == 0) return callParams;
		
		Object[] concatParamsArray = new Object[callParams.length + additionalParams.length];
		
		for (int i=0; i<callParams.length; i++) {
			concatParamsArray[i] = callParams[i];
		}
		
		for (int i=0; i<additionalParams.length; i++) {
			concatParamsArray[i+callParams.length] = additionalParams[i];
		}
		
		return concatParamsArray;
	}

	/**
	 * Checks if callParams params can be cast work's call method parameters
	 
	protected boolean checkParams(Object[] callParams, AtomicReferenceArray<Class<?>> workCallParams) {
		if (callParams.length != workCallParams.length()) return false;
		
		for (int i=0; i<callParams.length; i++) {
			if (workCallParams.get(i).isAssignableFrom(callParams[i].getClass()) == false) return false;
		}
		
		return true;
	}*/
	
}

class WorkMetadata {
	final ThreadWork work;
	final ExecutorService executorService;
	final Object[] additionalParams;
	final int workNum;
	
	public WorkMetadata(ThreadWork work, ExecutorService executorService, Object[] additionalParams, int workNum) {
//		S.eprintf("WorkMetadata CONSTRUCTOR -->");
//		new Exception().printStackTrace();
//		for(Object o : additionalParams) {
//			System.out.println(o);
//		}
//		S.eprintf("<-- WorkMetadata CONSTRUCTOR");
//		System.err.flush();
		this.work = work;
		this.executorService = executorService;
		this.additionalParams = additionalParams;
		this.workNum = workNum;
	}
	
}
