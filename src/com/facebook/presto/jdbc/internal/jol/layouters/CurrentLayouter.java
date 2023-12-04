/*    */ package com.facebook.presto.jdbc.internal.jol.layouters;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jol.datamodel.CurrentDataModel;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.ClassData;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.FieldData;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.FieldLayout;
/*    */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport;
/*    */ import java.util.Collection;
/*    */ import java.util.SortedSet;
/*    */ import java.util.TreeSet;
/*    */ import sun.misc.Unsafe;
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
/*    */ public class CurrentLayouter
/*    */   implements Layouter
/*    */ {
/*    */   public ClassLayout layout(ClassData data)
/*    */   {
/* 47 */     CurrentDataModel model = new CurrentDataModel();
/*    */     int instanceSize;
/* 49 */     if (data.isArray()) {
/*    */       try
/*    */       {
/* 52 */         int base = VMSupport.U.arrayBaseOffset(Class.forName(data.arrayClass()));
/* 53 */         int scale = VMSupport.U.arrayIndexScale(Class.forName(data.arrayClass()));
/*    */         
/* 55 */         instanceSize = VMSupport.align(base + data.arrayLength() * scale, 8);
/*    */         
/* 57 */         SortedSet<FieldLayout> result = new TreeSet();
/* 58 */         result.add(new FieldLayout(FieldData.create(data.arrayClass(), "length", "int"), model.headerSize(), model.sizeOf("int")));
/* 59 */         result.add(new FieldLayout(FieldData.create(data.arrayClass(), "<elements>", data.arrayComponentType()), base, scale * data.arrayLength()));
/* 60 */         return new ClassLayout(data, result, model.headerSize(), instanceSize, false);
/*    */       } catch (ClassNotFoundException e) {
/* 62 */         throw new IllegalStateException("Should not reach here.", e);
/*    */       }
/*    */     }
/*    */     
/* 66 */     Collection<FieldData> fields = data.fields();
/*    */     
/* 68 */     SortedSet<FieldLayout> result = new TreeSet();
/* 69 */     for (FieldData f : fields) {
/* 70 */       result.add(new FieldLayout(f, f.vmOffset(), model.sizeOf(f.typeClass())));
/*    */     }
/*    */     int instanceSize;
/*    */     int instanceSize;
/* 74 */     if (result.isEmpty()) {
/* 75 */       instanceSize = VMSupport.align(model.headerSize());
/*    */     } else {
/* 77 */       FieldLayout f = (FieldLayout)result.last();
/* 78 */       instanceSize = VMSupport.align(f.offset() + f.size());
/*    */     }
/* 80 */     return new ClassLayout(data, result, model.headerSize(), instanceSize, true);
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 85 */     return "Current VM Layout";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\layouters\CurrentLayouter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */