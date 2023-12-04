/*    */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
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
/*    */ @ManagedObject("Mapped Resource")
/*    */ public class MappedResource<E>
/*    */   implements Comparable<MappedResource<E>>
/*    */ {
/*    */   private final PathSpec pathSpec;
/*    */   private final E resource;
/*    */   
/*    */   public MappedResource(PathSpec pathSpec, E resource)
/*    */   {
/* 32 */     this.pathSpec = pathSpec;
/* 33 */     this.resource = resource;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int compareTo(MappedResource<E> other)
/*    */   {
/* 42 */     return this.pathSpec.compareTo(other.pathSpec);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 48 */     if (this == obj)
/*    */     {
/* 50 */       return true;
/*    */     }
/* 52 */     if (obj == null)
/*    */     {
/* 54 */       return false;
/*    */     }
/* 56 */     if (getClass() != obj.getClass())
/*    */     {
/* 58 */       return false;
/*    */     }
/* 60 */     MappedResource<?> other = (MappedResource)obj;
/* 61 */     if (this.pathSpec == null)
/*    */     {
/* 63 */       if (other.pathSpec != null)
/*    */       {
/* 65 */         return false;
/*    */       }
/*    */     }
/* 68 */     else if (!this.pathSpec.equals(other.pathSpec))
/*    */     {
/* 70 */       return false;
/*    */     }
/* 72 */     return true;
/*    */   }
/*    */   
/*    */   @ManagedAttribute(value="path spec", readonly=true)
/*    */   public PathSpec getPathSpec()
/*    */   {
/* 78 */     return this.pathSpec;
/*    */   }
/*    */   
/*    */   @ManagedAttribute(value="resource", readonly=true)
/*    */   public E getResource()
/*    */   {
/* 84 */     return (E)this.resource;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 90 */     int prime = 31;
/* 91 */     int result = 1;
/* 92 */     result = 31 * result + (this.pathSpec == null ? 0 : this.pathSpec.hashCode());
/* 93 */     return result;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 99 */     return String.format("MappedResource[pathSpec=%s,resource=%s]", new Object[] { this.pathSpec, this.resource });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\MappedResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */