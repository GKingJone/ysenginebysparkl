/*    */ package com.facebook.presto.jdbc.internal.jol.layouters;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jol.datamodel.DataModel;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.ClassData;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.FieldData;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.FieldLayout;
/*    */ import java.util.SortedSet;
/*    */ import java.util.TreeSet;
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
/*    */ public class RawLayouter
/*    */   implements Layouter
/*    */ {
/*    */   private final DataModel model;
/*    */   
/*    */   public RawLayouter(DataModel model)
/*    */   {
/* 46 */     this.model = model;
/*    */   }
/*    */   
/*    */   public ClassLayout layout(ClassData data)
/*    */   {
/* 51 */     SortedSet<FieldLayout> result = new TreeSet();
/*    */     int scale;
/* 53 */     if (data.isArray())
/*    */     {
/* 55 */       int base = this.model.headerSize() + this.model.sizeOf("int");
/* 56 */       scale = this.model.sizeOf(data.arrayComponentType());
/*    */       
/* 58 */       int instanceSize = base + data.arrayLength() * scale;
/* 59 */       result.add(new FieldLayout(FieldData.create(data.arrayClass(), "length", "int"), this.model.headerSize(), this.model.sizeOf("int")));
/* 60 */       result.add(new FieldLayout(FieldData.create(data.arrayClass(), "<elements>", data.arrayComponentType()), base, scale * data.arrayLength()));
/* 61 */       return new ClassLayout(data, result, this.model.headerSize(), instanceSize, false);
/*    */     }
/*    */     
/* 64 */     int offset = this.model.headerSize();
/* 65 */     for (FieldData f : data.fields()) {
/* 66 */       int size = this.model.sizeOf(f.typeClass());
/* 67 */       result.add(new FieldLayout(f, offset, size));
/* 68 */       offset += size;
/*    */     }
/*    */     
/* 71 */     if (result.isEmpty()) {
/* 72 */       return new ClassLayout(data, result, this.model.headerSize(), this.model.headerSize(), false);
/*    */     }
/* 74 */     FieldLayout f = (FieldLayout)result.last();
/* 75 */     return new ClassLayout(data, result, this.model.headerSize(), f.offset() + f.size(), false);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 81 */     return "Raw data (" + this.model + ")";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\layouters\RawLayouter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */