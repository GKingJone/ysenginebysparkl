/*     */ package com.facebook.presto.jdbc.internal.spi.resourceGroups;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
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
/*     */ public final class ResourceGroupId
/*     */ {
/*     */   private final List<String> segments;
/*     */   
/*     */   public ResourceGroupId(String name)
/*     */   {
/*  32 */     this(Collections.singletonList(Objects.requireNonNull(name, "name is null")));
/*     */   }
/*     */   
/*     */   public ResourceGroupId(ResourceGroupId parent, String name)
/*     */   {
/*  37 */     this(append(((ResourceGroupId)Objects.requireNonNull(parent, "parent is null")).segments, (String)Objects.requireNonNull(name, "name is null")));
/*     */   }
/*     */   
/*     */   private static List<String> append(List<String> list, String element)
/*     */   {
/*  42 */     List<String> result = new ArrayList(list);
/*  43 */     result.add(element);
/*  44 */     return result;
/*     */   }
/*     */   
/*     */   private ResourceGroupId(List<String> segments)
/*     */   {
/*  49 */     checkArgument(!segments.isEmpty(), "Resource group id is empty", new Object[0]);
/*  50 */     for (String segment : segments) {
/*  51 */       checkArgument(!segment.isEmpty(), "Empty segment in resource group id", new Object[0]);
/*     */     }
/*  53 */     this.segments = segments;
/*     */   }
/*     */   
/*     */   public String getLastSegment()
/*     */   {
/*  58 */     return (String)this.segments.get(this.segments.size() - 1);
/*     */   }
/*     */   
/*     */   public List<String> getSegments()
/*     */   {
/*  63 */     return this.segments;
/*     */   }
/*     */   
/*     */   public Optional<ResourceGroupId> getParent()
/*     */   {
/*  68 */     if (this.segments.size() == 1) {
/*  69 */       return Optional.empty();
/*     */     }
/*  71 */     return Optional.of(new ResourceGroupId(this.segments.subList(0, this.segments.size() - 1)));
/*     */   }
/*     */   
/*     */   private static void checkArgument(boolean argument, String format, Object... args)
/*     */   {
/*  76 */     if (!argument) {
/*  77 */       throw new IllegalArgumentException(String.format(format, args));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  84 */     return 
/*  85 */       (String)this.segments.stream().collect(Collectors.joining("."));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  91 */     if (this == o) {
/*  92 */       return true;
/*     */     }
/*  94 */     if ((o == null) || (getClass() != o.getClass())) {
/*  95 */       return false;
/*     */     }
/*  97 */     ResourceGroupId that = (ResourceGroupId)o;
/*  98 */     return Objects.equals(this.segments, that.segments);
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 104 */     return Objects.hash(new Object[] { this.segments });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\resourceGroups\ResourceGroupId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */