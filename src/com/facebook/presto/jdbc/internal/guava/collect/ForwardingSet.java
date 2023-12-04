/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.util.Collection;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSet<E>
/*    */   extends ForwardingCollection<E>
/*    */   implements Set<E>
/*    */ {
/*    */   protected abstract Set<E> delegate();
/*    */   
/*    */   public boolean equals(@Nullable Object object)
/*    */   {
/* 59 */     return (object == this) || (delegate().equals(object));
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 63 */     return delegate().hashCode();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected boolean standardRemoveAll(Collection<?> collection)
/*    */   {
/* 76 */     return Sets.removeAllImpl(this, (Collection)Preconditions.checkNotNull(collection));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected boolean standardEquals(@Nullable Object object)
/*    */   {
/* 87 */     return Sets.equalsImpl(this, object);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected int standardHashCode()
/*    */   {
/* 98 */     return Sets.hashCodeImpl(this);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */