/*    */ package com.facebook.presto.jdbc.internal.jetty.util.component;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
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
/*    */ public class DumpableCollection
/*    */   implements Dumpable
/*    */ {
/*    */   private final String _name;
/*    */   private final Collection<?> _collection;
/*    */   
/*    */   public DumpableCollection(String name, Collection<?> collection)
/*    */   {
/* 34 */     this._name = name;
/* 35 */     this._collection = collection;
/*    */   }
/*    */   
/*    */ 
/*    */   public String dump()
/*    */   {
/* 41 */     return ContainerLifeCycle.dump(this);
/*    */   }
/*    */   
/*    */   public void dump(Appendable out, String indent)
/*    */     throws IOException
/*    */   {
/* 47 */     out.append(this._name).append("\n");
/* 48 */     if (this._collection != null) {
/* 49 */       ContainerLifeCycle.dump(out, indent, new Collection[] { this._collection });
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\DumpableCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */