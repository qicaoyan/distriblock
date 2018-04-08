package com.ming.distriblock.core;

import com.ming.distriblock.core.method.DistribMethod;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xueming on 2018/4/2.
 * 分布式锁对象，为了多个服务之间的锁协调
 * 多个服务可以采用共享/互斥的方式来获取同一个锁
 */
public class DistribLock implements Serializable {
    private static Logger logger = Logger.getLogger(DistribLock.class);
    /**
     * 分布式锁博旭拥有一个唯一的ID， 由请求锁对象的服务者唯一构造生成
     */
    private String primaryId;
    /**
     * 该锁是否是独占的
     */
    private boolean isExclusive = false;
    /**
     * 真实的锁对象
     */
    private final DistribSync sync;
    /**
     * 该锁是否被获取
     */
    private volatile AtomicBoolean isAcquired;

    /**
     * 该锁被哪个分布式方法对象持有
     */
    private DistribMethod distribMethod;

    private final Lock lock;

    public DistribLock(String primaryId){
        this.primaryId = primaryId;
        this.isAcquired = new AtomicBoolean(false);
        sync = new DistribSync();
        this.lock = new ReentrantLock();
    }

    public DistribLock(String primaryId, boolean isExclusive){
        this.primaryId = primaryId;
        this.isExclusive = isExclusive;
        this.isAcquired = new AtomicBoolean(false);
        sync = new DistribSync();
        this.lock = new ReentrantLock();
    }

    public void lock(DistribMethod distribMethod){
        this.sync.acquire(distribMethod, 1);
//        lock.lock();
        this.isAcquired.compareAndSet(false, true);
    }

    public void unlock(DistribMethod distribMethod){
        this.sync.release(distribMethod, 1);
//        this.lock.unlock();
        this.isAcquired.compareAndSet(true, false);
    }


    public DistribMethod getDistribMethod() {
        return distribMethod;
    }

    public void setDistribMethod(DistribMethod distribMethod) {
        this.distribMethod = distribMethod;
    }


    /**
     * 分布式锁同步器
     */
    static final class DistribSync {
        private static final long serialVersionUID = -3000897897090466540L;

        private AtomicInteger state = new AtomicInteger(0);

        private DistribMethod exclusiveDistrbMethod = null;

        /**
         * 头结点
         */
        private AtomicReference<Node> head = new AtomicReference<>(null);
        /**
         * 尾结点
         */
        private AtomicReference<Node> tail = new AtomicReference<>(null);

        /**
         * 当分布式方法获取锁对象需要进行等待时，该方法被阻塞，
         * 直到获取到锁对象
         * @param method
         * @param args
         */
        public void acquire(DistribMethod method, int args){
            if (!tryAcquire(method, args) ){
                acquireQueued(addWaiter(method, Node.EXCLUSIVE), args);
            }
        }

        public void release(DistribMethod method, int args){
            /**
             * 释放锁成功
             */
            if(tryRelease(method, args)){
                Node currHead = head.get();
                Node headNext = head.get().next;
                if(currHead != null && currHead.method == method){
                    head.compareAndSet(currHead, headNext);
                }
            }
        }

        static final class Node {

            static final Node EXCLUSIVE = null;
            /**
             * 结点所对应的分布式方法（在队列中等待）
             */
            volatile DistribMethod method;

            volatile Node prev;

            volatile Node  next;

            volatile Node nextWaiter;
            /** waitStatus value to indicate thread has cancelled */
            static final int CANCELLED =  1;
            /** waitStatus value to indicate successor's thread needs unparking */
            static final int SIGNAL    = -1;
            /** waitStatus value to indicate thread is waiting on condition */
            static final int CONDITION = -2;
            /**
             * waitStatus value to indicate the next acquireShared should
             * unconditionally propagate
             */
            static final int PROPAGATE = -3;

            int waitStatus;

            public Node(){

            }

            public Node(DistribMethod distribMethod, Node mode){
                this.method = distribMethod;
                this.nextWaiter = mode;
            }

            final Node predecessor() throws NullPointerException {
                Node p = prev;
                if (p == null)
                    throw new NullPointerException();
                else
                    return p;
            }
        }


        /**
         * Fair version of tryAcquire.  Don't grant access unless
         * recursive call or no waiters or is first.
         */
        protected final boolean tryAcquire(DistribMethod distribMethod, int acquires) {
              int c = state.get();
              if(c == 0){
                  if(!hasQueuedPredecessors(distribMethod) &&
                          state.compareAndSet(0, acquires)){
                      this.exclusiveDistrbMethod = distribMethod;
                      logger.info("设置当前独占锁，服务实例为:" + this.exclusiveDistrbMethod.getServiceInstance().getServiceId());
                      return true;
                  }
              }else if(distribMethod.equals(this.exclusiveDistrbMethod)){
                  int nextc = c + acquires;
                  if(nextc < 0){
                    throw new Error("Maximum lock count exceeded");
                  }
                  this.exclusiveDistrbMethod = distribMethod;
                  state.compareAndSet(c, nextc);
                  return true;
              }
              return false;
        }

        public final boolean hasQueuedPredecessors(DistribMethod method) {
            // The correctness of this depends on head being initialized
            // before tail and on head.next being accurate if the current
            // thread is first in queue.
            Node t = tail.get(); // Read fields in reverse initialization order
            Node h = head.get();
            Node s;
            return h != t &&
                    ((s = h.next) == null || !s.method.equals(method));
        }

        /**
         * 请求加入队列
         * @param node
         * @param arg
         * @return
         */
        final boolean acquireQueued(final Node node, int arg) {
            try {
                for (;;) {
                    final Node p = node.predecessor();
                    if (p == head.get() && tryAcquire(node.method, arg)) {
                        head.set(node);
                        p.next = null; // help GC
                        return true;
                    }
                    /**
                     * 否则，依次检查当前节点前面的节点是否是SIGNAL状态，或者如果有服务状态已经不可用，
                     * 则移除
                     * 待实现剔除功能
                     */
                    Node pred = node;
//                    do {
//                        node.prev = pred = pred.prev;
//                    } while (pred != null &&
//                            !pred.method.getServiceInstance().isAvailable());
//                    if(pred != null){
//                        pred.next = node;
//                    }
                }
            } catch (Exception e){
                return false;
            }
        }

        /**
         * 需要实现
         * @return
         */
        private boolean shouldRemoveUnavaiable(){
            return false;
        }

        private Node addWaiter(DistribMethod distribMethod, Node mode) {
            Node node = new Node(distribMethod, mode);
            // Try the fast path of enq; backup to full enq on failure
            Node pred = tail.get();
            if (pred != null) {
                node.prev = pred;
                if (tail.compareAndSet(pred, node)) {
                    pred.next = node;
                    return node;
                }
            }
            enq(node);
            return node;
        }

        private Node enq(final Node node) {
            for (;;) {
                Node t = tail.get();
                if (t == null) { // Must initialize
                    if (head.compareAndSet(null, new Node()))
                        tail.set(head.get());
                } else {
                    node.prev = t;
                    if (tail.compareAndSet(t, node)) {
                        t.next = node;
                        return t;
                    }
                }
            }
        }


        /**
         * 释放分布式锁
         */
        private boolean tryRelease(DistribMethod method, int releases){
            /**
             * 锁状态递减
             */
            int c = state.get() - releases;
            /**
             * 当前释放分布式锁的方法必须是当前独占的分布式方法，
             * 否则将抛出异常
             */
            if(!method.equals(this.exclusiveDistrbMethod)){
                logger.error("当前释放锁的分布式方法非当前独占方法，method:" +
                        method.getServiceInstance().getServiceId() + "  exclusiveDistrbMethod:" + exclusiveDistrbMethod.getServiceInstance().getServiceId());
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if(c == 0){
                exclusiveDistrbMethod = null;
                free = true;
            }
            state.set(c);
            return free;
        }
    }

}
