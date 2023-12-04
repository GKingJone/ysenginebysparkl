/*     */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ManagedObject("Path Mappings")
/*     */ public class PathMappings<E>
/*     */   implements Iterable<MappedResource<E>>, Dumpable
/*     */ {
/*  44 */   private static final Logger LOG = Log.getLogger(PathMappings.class);
/*  45 */   private List<MappedResource<E>> mappings = new ArrayList();
/*  46 */   private MappedResource<E> defaultResource = null;
/*  47 */   private MappedResource<E> rootResource = null;
/*     */   
/*     */ 
/*     */   public String dump()
/*     */   {
/*  52 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/*  58 */     ContainerLifeCycle.dump(out, indent, new Collection[] { this.mappings });
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="mappings", readonly=true)
/*     */   public List<MappedResource<E>> getMappings()
/*     */   {
/*  64 */     return this.mappings;
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/*  69 */     this.mappings.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<MappedResource<E>> getMatches(String path)
/*     */   {
/*  80 */     boolean matchRoot = "/".equals(path);
/*     */     
/*  82 */     List<MappedResource<E>> ret = new ArrayList();
/*  83 */     int len = this.mappings.size();
/*  84 */     for (int i = 0; i < len; i++)
/*     */     {
/*  86 */       MappedResource<E> mr = (MappedResource)this.mappings.get(i);
/*     */       
/*  88 */       switch (mr.getPathSpec().group)
/*     */       {
/*     */       case ROOT: 
/*  91 */         if (matchRoot)
/*  92 */           ret.add(mr);
/*     */         break;
/*     */       case DEFAULT: 
/*  95 */         if ((matchRoot) || (mr.getPathSpec().matches(path)))
/*  96 */           ret.add(mr);
/*     */         break;
/*     */       default: 
/*  99 */         if (mr.getPathSpec().matches(path))
/* 100 */           ret.add(mr);
/*     */         break;
/*     */       }
/*     */     }
/* 104 */     return ret;
/*     */   }
/*     */   
/*     */   public MappedResource<E> getMatch(String path)
/*     */   {
/* 109 */     if ((path.equals("/")) && (this.rootResource != null))
/*     */     {
/* 111 */       return this.rootResource;
/*     */     }
/*     */     
/* 114 */     int len = this.mappings.size();
/* 115 */     for (int i = 0; i < len; i++)
/*     */     {
/* 117 */       MappedResource<E> mr = (MappedResource)this.mappings.get(i);
/* 118 */       if (mr.getPathSpec().matches(path))
/*     */       {
/* 120 */         return mr;
/*     */       }
/*     */     }
/* 123 */     return this.defaultResource;
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<MappedResource<E>> iterator()
/*     */   {
/* 129 */     return this.mappings.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public void put(PathSpec pathSpec, E resource)
/*     */   {
/* 135 */     MappedResource<E> entry = new MappedResource(pathSpec, resource);
/* 136 */     switch (pathSpec.group)
/*     */     {
/*     */     case DEFAULT: 
/* 139 */       this.defaultResource = entry;
/* 140 */       break;
/*     */     case ROOT: 
/* 142 */       this.rootResource = entry;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 148 */     this.mappings.add(entry);
/* 149 */     if (LOG.isDebugEnabled())
/* 150 */       LOG.debug("Added {} to {}", new Object[] { entry, this });
/* 151 */     Collections.sort(this.mappings);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 157 */     return String.format("%s[size=%d]", new Object[] { getClass().getSimpleName(), Integer.valueOf(this.mappings.size()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\PathMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */