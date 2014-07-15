/* [LGPL] Copyright 2011-2013 Gima

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Queue tasks with {@link #execute(Runnable)} for later execution by the thread calling {@link #executePending()}.
 * 
 * @ThreadSafety Any method can be called from any thread.
 */
public class DrainableExecutorService extends AbstractExecutorService {
	
	private final LinkedBlockingQueue<Runnable> extCmdQueue = new LinkedBlockingQueue<Runnable>();
	private volatile boolean isShutdown = false;
	private final long tid;
	
	public DrainableExecutorService() {
		tid = Thread.currentThread().getId();
	}

	/**
	 * Calls {@link #shutdown()}, returns true.
	 * @return true
	 */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		shutdown();
		return true;
	}
	
	@Override
	public boolean isShutdown() {
		return isShutdown;
	}

	@Override
	public boolean isTerminated() {
		return isShutdown() && extCmdQueue.isEmpty();
	}

	/**
	 * Stop accepting new tasks.
	 */
	@Override
	public void shutdown() {
		isShutdown = true;
	}

	/**
	 * Stop accepting new tasks and remove all submitted tasks from the queue.
	 */
	@Override
	public List<Runnable> shutdownNow() {
		isShutdown = true;
		List<Runnable> notExecuted = new ArrayList<Runnable>();
		extCmdQueue.drainTo(notExecuted);
		return notExecuted;
	}

	/**
	 * Queue a Runnable for later execution.
	 * @throws RejectedExecutionException If already shut down.
	 */
	@Override
	public void execute(Runnable runnable) {
		if (isShutdown) throw new RejectedExecutionException("ExecutorService already shut down");
		
		extCmdQueue.add(runnable);
	}
	
	/**
	 * Execute all commands that have been queued.
	 */
	public void executePending() {
		List<Runnable> runnables = new ArrayList<Runnable>();
		extCmdQueue.drainTo(runnables);
		
		for (Runnable runnable : runnables) {
			runnable.run();
		}
	}

	public long getThreadID() {
		return tid;
	}
	
}
