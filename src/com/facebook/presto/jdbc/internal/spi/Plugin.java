/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockEncodingFactory;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockEncodingSerde;
/*    */ import com.facebook.presto.jdbc.internal.spi.eventlistener.EventListenerFactory;
/*    */ import com.facebook.presto.jdbc.internal.spi.resourceGroups.ResourceGroupConfigurationManagerFactory;
/*    */ import com.facebook.presto.jdbc.internal.spi.security.SystemAccessControlFactory;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.ParametricType;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
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
/*    */ public abstract interface Plugin
/*    */ {
/*    */   public Iterable<ConnectorFactory> getLegacyConnectorFactories()
/*    */   {
/* 34 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Iterable<com.facebook.presto.jdbc.internal.spi.connector.ConnectorFactory> getConnectorFactories()
/*    */   {
/* 39 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Iterable<BlockEncodingFactory<?>> getBlockEncodingFactories(BlockEncodingSerde serde)
/*    */   {
/* 44 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Iterable<Type> getTypes()
/*    */   {
/* 49 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Iterable<ParametricType> getParametricTypes()
/*    */   {
/* 54 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Set<Class<?>> getFunctions()
/*    */   {
/* 59 */     return Collections.emptySet();
/*    */   }
/*    */   
/*    */   public Iterable<SystemAccessControlFactory> getSystemAccessControlFactories()
/*    */   {
/* 64 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Iterable<EventListenerFactory> getEventListenerFactories()
/*    */   {
/* 69 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   public Iterable<ResourceGroupConfigurationManagerFactory> getResourceGroupConfigurationManagerFactories()
/*    */   {
/* 74 */     return Collections.emptyList();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\Plugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */