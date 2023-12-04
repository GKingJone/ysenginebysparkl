/*      */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.MapMaker;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.annotation.Nullable;
/*      */ import javax.annotation.concurrent.ThreadSafe;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Beta
/*      */ @ThreadSafe
/*      */ public class CycleDetectingLockFactory
/*      */ {
/*      */   @Beta
/*      */   @ThreadSafe
/*      */   public static abstract interface Policy
/*      */   {
/*      */     public abstract void handlePotentialDeadlock(PotentialDeadlockException paramPotentialDeadlockException);
/*      */   }
/*      */   
/*      */   @Beta
/*      */   public static abstract enum Policies
/*      */     implements Policy
/*      */   {
/*  205 */     THROW, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  218 */     WARN, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  235 */     DISABLED;
/*      */     
/*      */ 
/*      */ 
/*      */     private Policies() {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static CycleDetectingLockFactory newInstance(Policy policy)
/*      */   {
/*  246 */     return new CycleDetectingLockFactory(policy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ReentrantLock newReentrantLock(String lockName)
/*      */   {
/*  253 */     return newReentrantLock(lockName, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ReentrantLock newReentrantLock(String lockName, boolean fair)
/*      */   {
/*  262 */     return this.policy == Policies.DISABLED ? new ReentrantLock(fair) : new CycleDetectingReentrantLock(new LockGraphNode(lockName), fair, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ReentrantReadWriteLock newReentrantReadWriteLock(String lockName)
/*      */   {
/*  271 */     return newReentrantReadWriteLock(lockName, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ReentrantReadWriteLock newReentrantReadWriteLock(String lockName, boolean fair)
/*      */   {
/*  281 */     return this.policy == Policies.DISABLED ? new ReentrantReadWriteLock(fair) : new CycleDetectingReentrantReadWriteLock(new LockGraphNode(lockName), fair, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  288 */   private static final ConcurrentMap<Class<? extends Enum>, Map<? extends Enum, LockGraphNode>> lockGraphNodesPerType = new MapMaker().weakKeys().makeMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <E extends Enum<E>> WithExplicitOrdering<E> newInstanceWithExplicitOrdering(Class<E> enumClass, Policy policy)
/*      */   {
/*  298 */     Preconditions.checkNotNull(enumClass);
/*  299 */     Preconditions.checkNotNull(policy);
/*      */     
/*  301 */     Map<E, LockGraphNode> lockGraphNodes = getOrCreateNodes(enumClass);
/*      */     
/*  303 */     return new WithExplicitOrdering(policy, lockGraphNodes);
/*      */   }
/*      */   
/*      */   private static Map<? extends Enum, LockGraphNode> getOrCreateNodes(Class<? extends Enum> clazz)
/*      */   {
/*  308 */     Map<? extends Enum, LockGraphNode> existing = (Map)lockGraphNodesPerType.get(clazz);
/*      */     
/*  310 */     if (existing != null) {
/*  311 */       return existing;
/*      */     }
/*  313 */     Map<? extends Enum, LockGraphNode> created = createNodes(clazz);
/*  314 */     existing = (Map)lockGraphNodesPerType.putIfAbsent(clazz, created);
/*  315 */     return (Map)MoreObjects.firstNonNull(existing, created);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @VisibleForTesting
/*      */   static <E extends Enum<E>> Map<E, LockGraphNode> createNodes(Class<E> clazz)
/*      */   {
/*  326 */     EnumMap<E, LockGraphNode> map = Maps.newEnumMap(clazz);
/*  327 */     E[] keys = (Enum[])clazz.getEnumConstants();
/*  328 */     int numKeys = keys.length;
/*  329 */     ArrayList<LockGraphNode> nodes = Lists.newArrayListWithCapacity(numKeys);
/*      */     
/*      */ 
/*  332 */     for (E key : keys) {
/*  333 */       LockGraphNode node = new LockGraphNode(getLockName(key));
/*  334 */       nodes.add(node);
/*  335 */       map.put(key, node);
/*      */     }
/*      */     
/*  338 */     for (int i = 1; i < numKeys; i++) {
/*  339 */       ((LockGraphNode)nodes.get(i)).checkAcquiredLocks(Policies.THROW, nodes.subList(0, i));
/*      */     }
/*      */     
/*  342 */     for (int i = 0; i < numKeys - 1; i++) {
/*  343 */       ((LockGraphNode)nodes.get(i)).checkAcquiredLocks(Policies.DISABLED, nodes.subList(i + 1, numKeys));
/*      */     }
/*      */     
/*  346 */     return Collections.unmodifiableMap(map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getLockName(Enum<?> rank)
/*      */   {
/*  355 */     String str1 = String.valueOf(String.valueOf(rank.getDeclaringClass().getSimpleName()));String str2 = String.valueOf(String.valueOf(rank.name()));return 1 + str1.length() + str2.length() + str1 + "." + str2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Beta
/*      */   public static final class WithExplicitOrdering<E extends Enum<E>>
/*      */     extends CycleDetectingLockFactory
/*      */   {
/*      */     private final Map<E, LockGraphNode> lockGraphNodes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @VisibleForTesting
/*      */     WithExplicitOrdering(Policy policy, Map<E, LockGraphNode> lockGraphNodes)
/*      */     {
/*  428 */       super(null);
/*  429 */       this.lockGraphNodes = lockGraphNodes;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public ReentrantLock newReentrantLock(E rank)
/*      */     {
/*  436 */       return newReentrantLock(rank, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ReentrantLock newReentrantLock(E rank, boolean fair)
/*      */     {
/*  449 */       return this.policy == Policies.DISABLED ? new ReentrantLock(fair) : new CycleDetectingReentrantLock(this, (LockGraphNode)this.lockGraphNodes.get(rank), fair, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public ReentrantReadWriteLock newReentrantReadWriteLock(E rank)
/*      */     {
/*  457 */       return newReentrantReadWriteLock(rank, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public ReentrantReadWriteLock newReentrantReadWriteLock(E rank, boolean fair)
/*      */     {
/*  471 */       return this.policy == Policies.DISABLED ? new ReentrantReadWriteLock(fair) : new CycleDetectingReentrantReadWriteLock(this, (LockGraphNode)this.lockGraphNodes.get(rank), fair, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  479 */   private static final Logger logger = Logger.getLogger(CycleDetectingLockFactory.class.getName());
/*      */   
/*      */   final Policy policy;
/*      */   
/*      */   private CycleDetectingLockFactory(Policy policy)
/*      */   {
/*  485 */     this.policy = ((Policy)Preconditions.checkNotNull(policy));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  496 */   private static final ThreadLocal<ArrayList<LockGraphNode>> acquiredLocks = new ThreadLocal()
/*      */   {
/*      */     protected ArrayList<LockGraphNode> initialValue() {
/*  499 */       return Lists.newArrayListWithCapacity(3);
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class ExampleStackTrace
/*      */     extends IllegalStateException
/*      */   {
/*  519 */     static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
/*      */     
/*      */ 
/*  522 */     static Set<String> EXCLUDED_CLASS_NAMES = ImmutableSet.of(CycleDetectingLockFactory.class.getName(), ExampleStackTrace.class.getName(), LockGraphNode.class.getName());
/*      */     
/*      */ 
/*      */ 
/*      */     ExampleStackTrace(LockGraphNode node1, LockGraphNode node2)
/*      */     {
/*  528 */       super();
/*  529 */       StackTraceElement[] origStackTrace = getStackTrace();
/*  530 */       int i = 0; for (int n = origStackTrace.length; i < n; i++) {
/*  531 */         if (WithExplicitOrdering.class.getName().equals(origStackTrace[i].getClassName()))
/*      */         {
/*      */ 
/*  534 */           setStackTrace(EMPTY_STACK_TRACE);
/*  535 */           break;
/*      */         }
/*  537 */         if (!EXCLUDED_CLASS_NAMES.contains(origStackTrace[i].getClassName())) {
/*  538 */           setStackTrace((StackTraceElement[])Arrays.copyOfRange(origStackTrace, i, n));
/*  539 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Beta
/*      */   public static final class PotentialDeadlockException
/*      */     extends ExampleStackTrace
/*      */   {
/*      */     private final ExampleStackTrace conflictingStackTrace;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private PotentialDeadlockException(LockGraphNode node1, LockGraphNode node2, ExampleStackTrace conflictingStackTrace)
/*      */     {
/*  577 */       super(node2);
/*  578 */       this.conflictingStackTrace = conflictingStackTrace;
/*  579 */       initCause(conflictingStackTrace);
/*      */     }
/*      */     
/*      */     public ExampleStackTrace getConflictingStackTrace() {
/*  583 */       return this.conflictingStackTrace;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getMessage()
/*      */     {
/*  592 */       StringBuilder message = new StringBuilder(super.getMessage());
/*  593 */       for (Throwable t = this.conflictingStackTrace; t != null; t = t.getCause()) {
/*  594 */         message.append(", ").append(t.getMessage());
/*      */       }
/*  596 */       return message.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static abstract interface CycleDetectingLock
/*      */   {
/*      */     public abstract LockGraphNode getLockGraphNode();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract boolean isAcquiredByCurrentThread();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class LockGraphNode
/*      */   {
/*  625 */     final Map<LockGraphNode, ExampleStackTrace> allowedPriorLocks = new MapMaker().weakKeys().makeMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  632 */     final Map<LockGraphNode, PotentialDeadlockException> disallowedPriorLocks = new MapMaker().weakKeys().makeMap();
/*      */     
/*      */     final String lockName;
/*      */     
/*      */     LockGraphNode(String lockName)
/*      */     {
/*  638 */       this.lockName = ((String)Preconditions.checkNotNull(lockName));
/*      */     }
/*      */     
/*      */     String getLockName() {
/*  642 */       return this.lockName;
/*      */     }
/*      */     
/*      */     void checkAcquiredLocks(Policy policy, List<LockGraphNode> acquiredLocks)
/*      */     {
/*  647 */       int i = 0; for (int size = acquiredLocks.size(); i < size; i++) {
/*  648 */         checkAcquiredLock(policy, (LockGraphNode)acquiredLocks.get(i));
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     void checkAcquiredLock(Policy policy, LockGraphNode acquiredLock)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: aload_2
/*      */       //   2: if_acmpeq +7 -> 9
/*      */       //   5: iconst_1
/*      */       //   6: goto +4 -> 10
/*      */       //   9: iconst_0
/*      */       //   10: ldc 90
/*      */       //   12: aload_2
/*      */       //   13: invokevirtual 92	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode:getLockName	()Ljava/lang/String;
/*      */       //   16: invokestatic 96	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*      */       //   19: dup
/*      */       //   20: invokevirtual 99	java/lang/String:length	()I
/*      */       //   23: ifeq +9 -> 32
/*      */       //   26: invokevirtual 103	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*      */       //   29: goto +12 -> 41
/*      */       //   32: pop
/*      */       //   33: new 59	java/lang/String
/*      */       //   36: dup_x1
/*      */       //   37: swap
/*      */       //   38: invokespecial 105	java/lang/String:<init>	(Ljava/lang/String;)V
/*      */       //   41: invokestatic 109	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkState	(ZLjava/lang/Object;)V
/*      */       //   44: aload_0
/*      */       //   45: getfield 49	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode:allowedPriorLocks	Ljava/util/Map;
/*      */       //   48: aload_2
/*      */       //   49: invokeinterface 113 2 0
/*      */       //   54: ifeq +4 -> 58
/*      */       //   57: return
/*      */       //   58: aload_0
/*      */       //   59: getfield 51	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode:disallowedPriorLocks	Ljava/util/Map;
/*      */       //   62: aload_2
/*      */       //   63: invokeinterface 115 2 0
/*      */       //   68: checkcast 13	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$PotentialDeadlockException
/*      */       //   71: astore_3
/*      */       //   72: aload_3
/*      */       //   73: ifnull +28 -> 101
/*      */       //   76: new 13	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$PotentialDeadlockException
/*      */       //   79: dup
/*      */       //   80: aload_2
/*      */       //   81: aload_0
/*      */       //   82: aload_3
/*      */       //   83: invokevirtual 119	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$PotentialDeadlockException:getConflictingStackTrace	()Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$ExampleStackTrace;
/*      */       //   86: aconst_null
/*      */       //   87: invokespecial 122	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$PotentialDeadlockException:<init>	(Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$ExampleStackTrace;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$1;)V
/*      */       //   90: astore 4
/*      */       //   92: aload_1
/*      */       //   93: aload 4
/*      */       //   95: invokeinterface 126 2 0
/*      */       //   100: return
/*      */       //   101: invokestatic 132	com/facebook/presto/jdbc/internal/guava/collect/Sets:newIdentityHashSet	()Ljava/util/Set;
/*      */       //   104: astore 4
/*      */       //   106: aload_2
/*      */       //   107: aload_0
/*      */       //   108: aload 4
/*      */       //   110: invokespecial 136	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode:findPathTo	(Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;Ljava/util/Set;)Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$ExampleStackTrace;
/*      */       //   113: astore 5
/*      */       //   115: aload 5
/*      */       //   117: ifnonnull +26 -> 143
/*      */       //   120: aload_0
/*      */       //   121: getfield 49	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode:allowedPriorLocks	Ljava/util/Map;
/*      */       //   124: aload_2
/*      */       //   125: new 10	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$ExampleStackTrace
/*      */       //   128: dup
/*      */       //   129: aload_2
/*      */       //   130: aload_0
/*      */       //   131: invokespecial 139	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$ExampleStackTrace:<init>	(Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;)V
/*      */       //   134: invokeinterface 143 3 0
/*      */       //   139: pop
/*      */       //   140: goto +38 -> 178
/*      */       //   143: new 13	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$PotentialDeadlockException
/*      */       //   146: dup
/*      */       //   147: aload_2
/*      */       //   148: aload_0
/*      */       //   149: aload 5
/*      */       //   151: aconst_null
/*      */       //   152: invokespecial 122	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$PotentialDeadlockException:<init>	(Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$ExampleStackTrace;Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$1;)V
/*      */       //   155: astore 6
/*      */       //   157: aload_0
/*      */       //   158: getfield 51	com/facebook/presto/jdbc/internal/guava/util/concurrent/CycleDetectingLockFactory$LockGraphNode:disallowedPriorLocks	Ljava/util/Map;
/*      */       //   161: aload_2
/*      */       //   162: aload 6
/*      */       //   164: invokeinterface 143 3 0
/*      */       //   169: pop
/*      */       //   170: aload_1
/*      */       //   171: aload 6
/*      */       //   173: invokeinterface 126 2 0
/*      */       //   178: return
/*      */       // Line number table:
/*      */       //   Java source line #668	-> byte code offset #0
/*      */       //   Java source line #673	-> byte code offset #44
/*      */       //   Java source line #677	-> byte code offset #57
/*      */       //   Java source line #679	-> byte code offset #58
/*      */       //   Java source line #681	-> byte code offset #72
/*      */       //   Java source line #685	-> byte code offset #76
/*      */       //   Java source line #688	-> byte code offset #92
/*      */       //   Java source line #689	-> byte code offset #100
/*      */       //   Java source line #693	-> byte code offset #101
/*      */       //   Java source line #694	-> byte code offset #106
/*      */       //   Java source line #696	-> byte code offset #115
/*      */       //   Java source line #705	-> byte code offset #120
/*      */       //   Java source line #710	-> byte code offset #143
/*      */       //   Java source line #712	-> byte code offset #157
/*      */       //   Java source line #713	-> byte code offset #170
/*      */       //   Java source line #715	-> byte code offset #178
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	179	0	this	LockGraphNode
/*      */       //   0	179	1	policy	CycleDetectingLockFactory.Policy
/*      */       //   0	179	2	acquiredLock	LockGraphNode
/*      */       //   72	107	3	previousDeadlockException	CycleDetectingLockFactory.PotentialDeadlockException
/*      */       //   92	9	4	exception	CycleDetectingLockFactory.PotentialDeadlockException
/*      */       //   106	73	4	seen	Set<LockGraphNode>
/*      */       //   115	64	5	path	CycleDetectingLockFactory.ExampleStackTrace
/*      */       //   157	21	6	exception	CycleDetectingLockFactory.PotentialDeadlockException
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     private CycleDetectingLockFactory.ExampleStackTrace findPathTo(LockGraphNode node, Set<LockGraphNode> seen)
/*      */     {
/*  729 */       if (!seen.add(this)) {
/*  730 */         return null;
/*      */       }
/*  732 */       ExampleStackTrace found = (ExampleStackTrace)this.allowedPriorLocks.get(node);
/*  733 */       if (found != null) {
/*  734 */         return found;
/*      */       }
/*      */       
/*      */ 
/*  738 */       for (Entry<LockGraphNode, ExampleStackTrace> entry : this.allowedPriorLocks.entrySet()) {
/*  739 */         LockGraphNode preAcquiredLock = (LockGraphNode)entry.getKey();
/*  740 */         found = preAcquiredLock.findPathTo(node, seen);
/*  741 */         if (found != null)
/*      */         {
/*      */ 
/*      */ 
/*  745 */           ExampleStackTrace path = new ExampleStackTrace(preAcquiredLock, this);
/*      */           
/*  747 */           path.setStackTrace(((ExampleStackTrace)entry.getValue()).getStackTrace());
/*  748 */           path.initCause(found);
/*  749 */           return path;
/*      */         }
/*      */       }
/*  752 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void aboutToAcquire(CycleDetectingLock lock)
/*      */   {
/*  761 */     if (!lock.isAcquiredByCurrentThread()) {
/*  762 */       ArrayList<LockGraphNode> acquiredLockList = (ArrayList)acquiredLocks.get();
/*  763 */       LockGraphNode node = lock.getLockGraphNode();
/*  764 */       node.checkAcquiredLocks(this.policy, acquiredLockList);
/*  765 */       acquiredLockList.add(node);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void lockStateChanged(CycleDetectingLock lock)
/*      */   {
/*  776 */     if (!lock.isAcquiredByCurrentThread()) {
/*  777 */       ArrayList<LockGraphNode> acquiredLockList = (ArrayList)acquiredLocks.get();
/*  778 */       LockGraphNode node = lock.getLockGraphNode();
/*      */       
/*      */ 
/*  781 */       for (int i = acquiredLockList.size() - 1; i >= 0; i--) {
/*  782 */         if (acquiredLockList.get(i) == node) {
/*  783 */           acquiredLockList.remove(i);
/*  784 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   final class CycleDetectingReentrantLock
/*      */     extends ReentrantLock implements CycleDetectingLock
/*      */   {
/*      */     private final LockGraphNode lockGraphNode;
/*      */     
/*      */     private CycleDetectingReentrantLock(LockGraphNode lockGraphNode, boolean fair)
/*      */     {
/*  797 */       super();
/*  798 */       this.lockGraphNode = ((LockGraphNode)Preconditions.checkNotNull(lockGraphNode));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public LockGraphNode getLockGraphNode()
/*      */     {
/*  805 */       return this.lockGraphNode;
/*      */     }
/*      */     
/*      */     public boolean isAcquiredByCurrentThread()
/*      */     {
/*  810 */       return isHeldByCurrentThread();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void lock()
/*      */     {
/*  817 */       CycleDetectingLockFactory.this.aboutToAcquire(this);
/*      */       try {
/*  819 */         super.lock();
/*      */       } finally {
/*  821 */         CycleDetectingLockFactory.this.lockStateChanged(this);
/*      */       }
/*      */     }
/*      */     
/*      */     public void lockInterruptibly() throws InterruptedException
/*      */     {
/*  827 */       CycleDetectingLockFactory.this.aboutToAcquire(this);
/*      */       try {
/*  829 */         super.lockInterruptibly();
/*      */       } finally {
/*  831 */         CycleDetectingLockFactory.this.lockStateChanged(this);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean tryLock()
/*      */     {
/*  837 */       CycleDetectingLockFactory.this.aboutToAcquire(this);
/*      */       try {
/*  839 */         return super.tryLock();
/*      */       } finally {
/*  841 */         CycleDetectingLockFactory.this.lockStateChanged(this);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean tryLock(long timeout, TimeUnit unit)
/*      */       throws InterruptedException
/*      */     {
/*  848 */       CycleDetectingLockFactory.this.aboutToAcquire(this);
/*      */       try {
/*  850 */         return super.tryLock(timeout, unit);
/*      */       } finally {
/*  852 */         CycleDetectingLockFactory.this.lockStateChanged(this);
/*      */       }
/*      */     }
/*      */     
/*      */     public void unlock()
/*      */     {
/*      */       try {
/*  859 */         super.unlock();
/*      */       } finally {
/*  861 */         CycleDetectingLockFactory.this.lockStateChanged(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   final class CycleDetectingReentrantReadWriteLock
/*      */     extends ReentrantReadWriteLock
/*      */     implements CycleDetectingLock
/*      */   {
/*      */     private final CycleDetectingReentrantReadLock readLock;
/*      */     
/*      */     private final CycleDetectingReentrantWriteLock writeLock;
/*      */     
/*      */     private final LockGraphNode lockGraphNode;
/*      */     
/*      */ 
/*      */     private CycleDetectingReentrantReadWriteLock(LockGraphNode lockGraphNode, boolean fair)
/*      */     {
/*  880 */       super();
/*  881 */       this.readLock = new CycleDetectingReentrantReadLock(CycleDetectingLockFactory.this, this);
/*  882 */       this.writeLock = new CycleDetectingReentrantWriteLock(CycleDetectingLockFactory.this, this);
/*  883 */       this.lockGraphNode = ((LockGraphNode)Preconditions.checkNotNull(lockGraphNode));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public ReadLock readLock()
/*      */     {
/*  890 */       return this.readLock;
/*      */     }
/*      */     
/*      */     public WriteLock writeLock()
/*      */     {
/*  895 */       return this.writeLock;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public LockGraphNode getLockGraphNode()
/*      */     {
/*  902 */       return this.lockGraphNode;
/*      */     }
/*      */     
/*      */     public boolean isAcquiredByCurrentThread()
/*      */     {
/*  907 */       return (isWriteLockedByCurrentThread()) || (getReadHoldCount() > 0);
/*      */     }
/*      */   }
/*      */   
/*      */   private class CycleDetectingReentrantReadLock
/*      */     extends ReadLock
/*      */   {
/*      */     final CycleDetectingReentrantReadWriteLock readWriteLock;
/*      */     
/*      */     CycleDetectingReentrantReadLock(CycleDetectingReentrantReadWriteLock readWriteLock)
/*      */     {
/*  918 */       super();
/*  919 */       this.readWriteLock = readWriteLock;
/*      */     }
/*      */     
/*      */     public void lock()
/*      */     {
/*  924 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/*  926 */         super.lock();
/*      */       } finally {
/*  928 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public void lockInterruptibly() throws InterruptedException
/*      */     {
/*  934 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/*  936 */         super.lockInterruptibly();
/*      */       } finally {
/*  938 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean tryLock()
/*      */     {
/*  944 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/*  946 */         return super.tryLock();
/*      */       } finally {
/*  948 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean tryLock(long timeout, TimeUnit unit)
/*      */       throws InterruptedException
/*      */     {
/*  955 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/*  957 */         return super.tryLock(timeout, unit);
/*      */       } finally {
/*  959 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public void unlock()
/*      */     {
/*      */       try {
/*  966 */         super.unlock();
/*      */       } finally {
/*  968 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class CycleDetectingReentrantWriteLock
/*      */     extends WriteLock
/*      */   {
/*      */     final CycleDetectingReentrantReadWriteLock readWriteLock;
/*      */     
/*      */     CycleDetectingReentrantWriteLock(CycleDetectingReentrantReadWriteLock readWriteLock)
/*      */     {
/*  980 */       super();
/*  981 */       this.readWriteLock = readWriteLock;
/*      */     }
/*      */     
/*      */     public void lock()
/*      */     {
/*  986 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/*  988 */         super.lock();
/*      */       } finally {
/*  990 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public void lockInterruptibly() throws InterruptedException
/*      */     {
/*  996 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/*  998 */         super.lockInterruptibly();
/*      */       } finally {
/* 1000 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean tryLock()
/*      */     {
/* 1006 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/* 1008 */         return super.tryLock();
/*      */       } finally {
/* 1010 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean tryLock(long timeout, TimeUnit unit)
/*      */       throws InterruptedException
/*      */     {
/* 1017 */       CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
/*      */       try {
/* 1019 */         return super.tryLock(timeout, unit);
/*      */       } finally {
/* 1021 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */     
/*      */     public void unlock()
/*      */     {
/*      */       try {
/* 1028 */         super.unlock();
/*      */       } finally {
/* 1030 */         CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\CycleDetectingLockFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */