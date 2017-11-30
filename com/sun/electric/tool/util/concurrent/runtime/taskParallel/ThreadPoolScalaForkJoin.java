/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: ThreadPoolScalaForkJoin.java
 *
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sun.electric.tool.util.concurrent.runtime.taskParallel;

import com.sun.electric.tool.util.concurrent.patterns.PTask;
import java.util.concurrent.TimeUnit;
import scala.concurrent.forkjoin.ForkJoinPool;
import scala.concurrent.forkjoin.ForkJoinTask;

public class ThreadPoolScalaForkJoin extends IThreadPool {

    private ForkJoinPool pool;
    private int threads;

    private ThreadPoolScalaForkJoin() {
        this(Runtime.getRuntime().availableProcessors());
    }

    private ThreadPoolScalaForkJoin(int threads) {
        this.threads = threads;
    }

    public static ThreadPoolScalaForkJoin initialize() {
        ThreadPoolScalaForkJoin pool = new ThreadPoolScalaForkJoin();
        pool.start();
        return pool;
    }

    public static ThreadPoolScalaForkJoin initialize(int numOfThreads) {
        ThreadPoolScalaForkJoin pool = new ThreadPoolScalaForkJoin(numOfThreads);
        pool.start();
        return pool;
    }

    @Override
    public void start() {
        pool = new ForkJoinPool(threads);
    }

    @Override
    public void shutdown() throws InterruptedException {
        pool.shutdown();
    }

    @Override
    public void join() throws InterruptedException {
        if (threads > 1) 
            pool.awaitTermination(1, TimeUnit.DAYS);

    }

    @Override
    public void sleep() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void weakUp() {
        throw new UnsupportedOperationException();

    }

    @Override
    public void trigger() {
        throw new UnsupportedOperationException();

    }

    @Override
    public void add(PTask item) {
//        System.out.println("- ForkJoin - ");
        pool.execute(new PTaskWrapper(item));
    }

    @Override
    public void add(PTask item, int threadId) {
//        System.out.println("- ForkJoin - ");
        pool.execute(new PTaskWrapper(item));
    }

    @Override
    public int getPoolSize() {
        return pool.getParallelism();
    }

    private class PTaskWrapper extends ForkJoinTask<String> {

        private PTask task;

        public PTaskWrapper(PTask task) {
            this.task = task;
        }

        @Override
        public boolean exec() {
            this.task.setThreadID((int) Thread.currentThread().getId());
            this.task.before();
            this.task.execute();
            this.task.after();
            return true;
        }

        @Override
        public String getRawResult() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void setRawResult(String value) {
            // TODO Auto-generated method stub
        }
    }
}
