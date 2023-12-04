/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.StandardLogger;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.sql.DriverPropertyInfo;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TreeMap;
/*      */ import javax.naming.RefAddr;
/*      */ import javax.naming.Reference;
/*      */ import javax.naming.StringRefAddr;
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
/*      */ public class ConnectionPropertiesImpl
/*      */   implements Serializable, ConnectionProperties
/*      */ {
/*      */   private static final long serialVersionUID = 4257801713007640580L;
/*      */   
/*      */   static class BooleanConnectionProperty
/*      */     extends ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 2540132501709159404L;
/*      */     
/*      */     BooleanConnectionProperty(String propertyNameToSet, boolean defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*   62 */       super(Boolean.valueOf(defaultValueToSet), null, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*   70 */       return new String[] { "true", "false", "yes", "no" };
/*      */     }
/*      */     
/*      */     boolean getValueAsBoolean() {
/*   74 */       return ((Boolean)this.valueAsObject).booleanValue();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*   82 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor)
/*      */       throws SQLException
/*      */     {
/*   90 */       if (extractedValue != null) {
/*   91 */         validateStringValues(extractedValue, exceptionInterceptor);
/*      */         
/*   93 */         this.valueAsObject = Boolean.valueOf((extractedValue.equalsIgnoreCase("TRUE")) || (extractedValue.equalsIgnoreCase("YES")));
/*   94 */         this.wasExplicitlySet = true;
/*      */       } else {
/*   96 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*   98 */       this.updateCount += 1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  106 */       return false;
/*      */     }
/*      */     
/*      */     void setValue(boolean valueFlag) {
/*  110 */       this.valueAsObject = Boolean.valueOf(valueFlag);
/*  111 */       this.wasExplicitlySet = true;
/*  112 */       this.updateCount += 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static abstract class ConnectionProperty
/*      */     implements Serializable
/*      */   {
/*      */     static final long serialVersionUID = -6644853639584478367L;
/*      */     
/*      */     String[] allowableValues;
/*      */     
/*      */     String categoryName;
/*      */     
/*      */     Object defaultValue;
/*      */     
/*      */     int lowerBound;
/*      */     
/*      */     int order;
/*      */     
/*      */     String propertyName;
/*      */     
/*      */     String sinceVersion;
/*      */     
/*      */     int upperBound;
/*      */     
/*      */     Object valueAsObject;
/*      */     
/*      */     boolean required;
/*      */     String description;
/*  142 */     int updateCount = 0;
/*      */     
/*  144 */     boolean wasExplicitlySet = false;
/*      */     
/*      */ 
/*      */     public ConnectionProperty() {}
/*      */     
/*      */ 
/*      */     ConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  152 */       this.description = descriptionToSet;
/*  153 */       this.propertyName = propertyNameToSet;
/*  154 */       this.defaultValue = defaultValueToSet;
/*  155 */       this.valueAsObject = defaultValueToSet;
/*  156 */       this.allowableValues = allowableValuesToSet;
/*  157 */       this.lowerBound = lowerBoundToSet;
/*  158 */       this.upperBound = upperBoundToSet;
/*  159 */       this.required = false;
/*  160 */       this.sinceVersion = sinceVersionToSet;
/*  161 */       this.categoryName = category;
/*  162 */       this.order = orderInCategory;
/*      */     }
/*      */     
/*      */     String[] getAllowableValues() {
/*  166 */       return this.allowableValues;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     String getCategoryName()
/*      */     {
/*  173 */       return this.categoryName;
/*      */     }
/*      */     
/*      */     Object getDefaultValue() {
/*  177 */       return this.defaultValue;
/*      */     }
/*      */     
/*      */     int getLowerBound() {
/*  181 */       return this.lowerBound;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     int getOrder()
/*      */     {
/*  188 */       return this.order;
/*      */     }
/*      */     
/*      */     String getPropertyName() {
/*  192 */       return this.propertyName;
/*      */     }
/*      */     
/*      */     int getUpperBound() {
/*  196 */       return this.upperBound;
/*      */     }
/*      */     
/*      */     Object getValueAsObject() {
/*  200 */       return this.valueAsObject;
/*      */     }
/*      */     
/*      */     int getUpdateCount() {
/*  204 */       return this.updateCount;
/*      */     }
/*      */     
/*      */     boolean isExplicitlySet() {
/*  208 */       return this.wasExplicitlySet;
/*      */     }
/*      */     
/*      */     abstract boolean hasValueConstraints();
/*      */     
/*      */     void initializeFrom(Properties extractFrom, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  214 */       String extractedValue = extractFrom.getProperty(getPropertyName());
/*  215 */       extractFrom.remove(getPropertyName());
/*  216 */       initializeFrom(extractedValue, exceptionInterceptor);
/*      */     }
/*      */     
/*      */     void initializeFrom(Reference ref, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  220 */       RefAddr refAddr = ref.get(getPropertyName());
/*      */       
/*  222 */       if (refAddr != null) {
/*  223 */         String refContentAsString = (String)refAddr.getContent();
/*      */         
/*  225 */         initializeFrom(refContentAsString, exceptionInterceptor);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     abstract void initializeFrom(String paramString, ExceptionInterceptor paramExceptionInterceptor)
/*      */       throws SQLException;
/*      */     
/*      */ 
/*      */     abstract boolean isRangeBased();
/*      */     
/*      */     void setCategoryName(String categoryName)
/*      */     {
/*  238 */       this.categoryName = categoryName;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     void setOrder(int order)
/*      */     {
/*  246 */       this.order = order;
/*      */     }
/*      */     
/*      */     void setValueAsObject(Object obj) {
/*  250 */       this.valueAsObject = obj;
/*  251 */       this.updateCount += 1;
/*      */     }
/*      */     
/*      */     void storeTo(Reference ref) {
/*  255 */       if (getValueAsObject() != null) {
/*  256 */         ref.add(new StringRefAddr(getPropertyName(), getValueAsObject().toString()));
/*      */       }
/*      */     }
/*      */     
/*      */     DriverPropertyInfo getAsDriverPropertyInfo() {
/*  261 */       DriverPropertyInfo dpi = new DriverPropertyInfo(this.propertyName, null);
/*  262 */       dpi.choices = getAllowableValues();
/*  263 */       dpi.value = (this.valueAsObject != null ? this.valueAsObject.toString() : null);
/*  264 */       dpi.required = this.required;
/*  265 */       dpi.description = this.description;
/*      */       
/*  267 */       return dpi;
/*      */     }
/*      */     
/*      */     void validateStringValues(String valueToValidate, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  271 */       String[] validateAgainst = getAllowableValues();
/*      */       
/*  273 */       if (valueToValidate == null) {
/*  274 */         return;
/*      */       }
/*      */       
/*  277 */       if ((validateAgainst == null) || (validateAgainst.length == 0)) {
/*  278 */         return;
/*      */       }
/*      */       
/*  281 */       for (int i = 0; i < validateAgainst.length; i++) {
/*  282 */         if ((validateAgainst[i] != null) && (validateAgainst[i].equalsIgnoreCase(valueToValidate))) {
/*  283 */           return;
/*      */         }
/*      */       }
/*      */       
/*  287 */       StringBuilder errorMessageBuf = new StringBuilder();
/*      */       
/*  289 */       errorMessageBuf.append("The connection property '");
/*  290 */       errorMessageBuf.append(getPropertyName());
/*  291 */       errorMessageBuf.append("' only accepts values of the form: ");
/*      */       
/*  293 */       if (validateAgainst.length != 0) {
/*  294 */         errorMessageBuf.append("'");
/*  295 */         errorMessageBuf.append(validateAgainst[0]);
/*  296 */         errorMessageBuf.append("'");
/*      */         
/*  298 */         for (int i = 1; i < validateAgainst.length - 1; i++) {
/*  299 */           errorMessageBuf.append(", ");
/*  300 */           errorMessageBuf.append("'");
/*  301 */           errorMessageBuf.append(validateAgainst[i]);
/*  302 */           errorMessageBuf.append("'");
/*      */         }
/*      */         
/*  305 */         errorMessageBuf.append(" or '");
/*  306 */         errorMessageBuf.append(validateAgainst[(validateAgainst.length - 1)]);
/*  307 */         errorMessageBuf.append("'");
/*      */       }
/*      */       
/*  310 */       errorMessageBuf.append(". The value '");
/*  311 */       errorMessageBuf.append(valueToValidate);
/*  312 */       errorMessageBuf.append("' is not in this set.");
/*      */       
/*  314 */       throw SQLError.createSQLException(errorMessageBuf.toString(), "S1009", exceptionInterceptor);
/*      */     }
/*      */   }
/*      */   
/*      */   static class IntegerConnectionProperty
/*      */     extends ConnectionProperty implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -3004305481796850832L;
/*  322 */     int multiplier = 1;
/*      */     
/*      */     public IntegerConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  326 */       super(defaultValueToSet, allowableValuesToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  332 */       super(Integer.valueOf(defaultValueToSet), null, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
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
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  345 */       this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*  353 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int getLowerBound()
/*      */     {
/*  361 */       return this.lowerBound;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int getUpperBound()
/*      */     {
/*  369 */       return this.upperBound;
/*      */     }
/*      */     
/*      */     int getValueAsInt() {
/*  373 */       return ((Integer)this.valueAsObject).intValue();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  381 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor)
/*      */       throws SQLException
/*      */     {
/*  389 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  392 */           int intValue = (int)(Double.valueOf(extractedValue).doubleValue() * this.multiplier);
/*      */           
/*  394 */           setValue(intValue, extractedValue, exceptionInterceptor);
/*      */         } catch (NumberFormatException nfe) {
/*  396 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values. The value '" + extractedValue + "' can not be converted to an integer.", "S1009", exceptionInterceptor);
/*      */         }
/*      */         
/*      */       } else {
/*  400 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*  402 */       this.updateCount += 1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  410 */       return getUpperBound() != getLowerBound();
/*      */     }
/*      */     
/*      */     void setValue(int intValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  414 */       setValue(intValue, null, exceptionInterceptor);
/*      */     }
/*      */     
/*      */     void setValue(int intValue, String valueAsString, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  418 */       if ((isRangeBased()) && (
/*  419 */         (intValue < getLowerBound()) || (intValue > getUpperBound()))) {
/*  420 */         throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values in the range of " + getLowerBound() + " - " + getUpperBound() + ", the value '" + (valueAsString == null ? Integer.valueOf(intValue) : valueAsString) + "' exceeds this range.", "S1009", exceptionInterceptor);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  427 */       this.valueAsObject = Integer.valueOf(intValue);
/*  428 */       this.wasExplicitlySet = true;
/*  429 */       this.updateCount += 1;
/*      */     }
/*      */   }
/*      */   
/*      */   public static class LongConnectionProperty extends IntegerConnectionProperty
/*      */   {
/*      */     private static final long serialVersionUID = 6068572984340480895L;
/*      */     
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, long lowerBoundToSet, long upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  439 */       super(Long.valueOf(defaultValueToSet), null, (int)lowerBoundToSet, (int)upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */ 
/*      */     LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  445 */       this(propertyNameToSet, defaultValueToSet, 0L, 0L, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */     void setValue(long longValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  449 */       setValue(longValue, null, exceptionInterceptor);
/*      */     }
/*      */     
/*      */     void setValue(long longValue, String valueAsString, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  453 */       if ((isRangeBased()) && (
/*  454 */         (longValue < getLowerBound()) || (longValue > getUpperBound()))) {
/*  455 */         throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values in the range of " + getLowerBound() + " - " + getUpperBound() + ", the value '" + (valueAsString == null ? Long.valueOf(longValue) : valueAsString) + "' exceeds this range.", "S1009", exceptionInterceptor);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  461 */       this.valueAsObject = Long.valueOf(longValue);
/*  462 */       this.wasExplicitlySet = true;
/*  463 */       this.updateCount += 1;
/*      */     }
/*      */     
/*      */     long getValueAsLong() {
/*  467 */       return ((Long)this.valueAsObject).longValue();
/*      */     }
/*      */     
/*      */     void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*      */     {
/*  472 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  475 */           long longValue = Double.valueOf(extractedValue).longValue();
/*      */           
/*  477 */           setValue(longValue, extractedValue, exceptionInterceptor);
/*      */         } catch (NumberFormatException nfe) {
/*  479 */           throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values. The value '" + extractedValue + "' can not be converted to a long integer.", "S1009", exceptionInterceptor);
/*      */         }
/*      */         
/*      */       } else {
/*  483 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*  485 */       this.updateCount += 1;
/*      */     }
/*      */   }
/*      */   
/*      */   static class MemorySizeConnectionProperty
/*      */     extends IntegerConnectionProperty implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7351065128998572656L;
/*      */     private String valueAsString;
/*      */     
/*      */     MemorySizeConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  497 */       super(defaultValueToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */     void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*      */     {
/*  502 */       this.valueAsString = extractedValue;
/*  503 */       this.multiplier = 1;
/*      */       
/*  505 */       if (extractedValue != null) {
/*  506 */         if ((extractedValue.endsWith("k")) || (extractedValue.endsWith("K")) || (extractedValue.endsWith("kb")) || (extractedValue.endsWith("Kb")) || (extractedValue.endsWith("kB")) || (extractedValue.endsWith("KB")))
/*      */         {
/*  508 */           this.multiplier = 1024;
/*  509 */           int indexOfK = StringUtils.indexOfIgnoreCase(extractedValue, "k");
/*  510 */           extractedValue = extractedValue.substring(0, indexOfK);
/*  511 */         } else if ((extractedValue.endsWith("m")) || (extractedValue.endsWith("M")) || (extractedValue.endsWith("mb")) || (extractedValue.endsWith("Mb")) || (extractedValue.endsWith("mB")) || (extractedValue.endsWith("MB")))
/*      */         {
/*  513 */           this.multiplier = 1048576;
/*  514 */           int indexOfM = StringUtils.indexOfIgnoreCase(extractedValue, "m");
/*  515 */           extractedValue = extractedValue.substring(0, indexOfM);
/*  516 */         } else if ((extractedValue.endsWith("g")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("gb")) || (extractedValue.endsWith("Gb")) || (extractedValue.endsWith("gB")) || (extractedValue.endsWith("GB")))
/*      */         {
/*  518 */           this.multiplier = 1073741824;
/*  519 */           int indexOfG = StringUtils.indexOfIgnoreCase(extractedValue, "g");
/*  520 */           extractedValue = extractedValue.substring(0, indexOfG);
/*      */         }
/*      */       }
/*      */       
/*  524 */       super.initializeFrom(extractedValue, exceptionInterceptor);
/*      */     }
/*      */     
/*      */     void setValue(String value, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  528 */       initializeFrom(value, exceptionInterceptor);
/*      */     }
/*      */     
/*      */     String getValueAsString() {
/*  532 */       return this.valueAsString;
/*      */     }
/*      */   }
/*      */   
/*      */   static class StringConnectionProperty extends ConnectionProperty implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 5432127962785948272L;
/*      */     
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  542 */       this(propertyNameToSet, defaultValueToSet, null, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String[] allowableValuesToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  554 */       super(defaultValueToSet, allowableValuesToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */     
/*      */     String getValueAsString() {
/*  558 */       return (String)this.valueAsObject;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  566 */       return (this.allowableValues != null) && (this.allowableValues.length > 0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor)
/*      */       throws SQLException
/*      */     {
/*  574 */       if (extractedValue != null) {
/*  575 */         validateStringValues(extractedValue, exceptionInterceptor);
/*      */         
/*  577 */         this.valueAsObject = extractedValue;
/*  578 */         this.wasExplicitlySet = true;
/*      */       } else {
/*  580 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*  582 */       this.updateCount += 1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  590 */       return false;
/*      */     }
/*      */     
/*      */     void setValue(String valueFlag) {
/*  594 */       this.valueAsObject = valueFlag;
/*  595 */       this.wasExplicitlySet = true;
/*  596 */       this.updateCount += 1;
/*      */     }
/*      */   }
/*      */   
/*  600 */   private static final String CONNECTION_AND_AUTH_CATEGORY = Messages.getString("ConnectionProperties.categoryConnectionAuthentication");
/*      */   
/*  602 */   private static final String NETWORK_CATEGORY = Messages.getString("ConnectionProperties.categoryNetworking");
/*      */   
/*  604 */   private static final String DEBUGING_PROFILING_CATEGORY = Messages.getString("ConnectionProperties.categoryDebuggingProfiling");
/*      */   
/*  606 */   private static final String HA_CATEGORY = Messages.getString("ConnectionProperties.categorryHA");
/*      */   
/*  608 */   private static final String MISC_CATEGORY = Messages.getString("ConnectionProperties.categoryMisc");
/*      */   
/*  610 */   private static final String PERFORMANCE_CATEGORY = Messages.getString("ConnectionProperties.categoryPerformance");
/*      */   
/*  612 */   private static final String SECURITY_CATEGORY = Messages.getString("ConnectionProperties.categorySecurity");
/*      */   
/*  614 */   private static final String[] PROPERTY_CATEGORIES = { CONNECTION_AND_AUTH_CATEGORY, NETWORK_CATEGORY, HA_CATEGORY, SECURITY_CATEGORY, PERFORMANCE_CATEGORY, DEBUGING_PROFILING_CATEGORY, MISC_CATEGORY };
/*      */   
/*      */ 
/*  617 */   private static final ArrayList<Field> PROPERTY_LIST = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  622 */   private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
/*      */   private BooleanConnectionProperty allowLoadLocalInfile;
/*      */   private BooleanConnectionProperty allowMultiQueries;
/*      */   private BooleanConnectionProperty allowNanAndInf;
/*      */   private BooleanConnectionProperty allowUrlInLocalInfile;
/*      */   private BooleanConnectionProperty alwaysSendSetIsolation;
/*      */   private BooleanConnectionProperty autoClosePStmtStreams;
/*      */   private BooleanConnectionProperty allowMasterDownConnections;
/*      */   private BooleanConnectionProperty allowSlaveDownConnections;
/*      */   private BooleanConnectionProperty readFromMasterWhenNoSlaves;
/*      */   private BooleanConnectionProperty autoDeserialize;
/*      */   private BooleanConnectionProperty autoGenerateTestcaseScript;
/*      */   private boolean autoGenerateTestcaseScriptAsBoolean;
/*      */   private BooleanConnectionProperty autoReconnect;
/*      */   private BooleanConnectionProperty autoReconnectForPools;
/*      */   private boolean autoReconnectForPoolsAsBoolean;
/*      */   private MemorySizeConnectionProperty blobSendChunkSize;
/*      */   private BooleanConnectionProperty autoSlowLog;
/*      */   
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  632 */       Field[] declaredFields = ConnectionPropertiesImpl.class.getDeclaredFields();
/*      */       
/*  634 */       for (int i = 0; i < declaredFields.length; i++) {
/*  635 */         if (ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType())) {
/*  636 */           PROPERTY_LIST.add(declaredFields[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  640 */       RuntimeException rtEx = new RuntimeException();
/*  641 */       rtEx.initCause(ex);
/*      */       
/*  643 */       throw rtEx;
/*      */     }
/*      */   }
/*      */   
/*      */   private BooleanConnectionProperty blobsAreStrings;
/*      */   private BooleanConnectionProperty functionsNeverReturnBlobs;
/*      */   private BooleanConnectionProperty cacheCallableStatements;
/*      */   private BooleanConnectionProperty cachePreparedStatements;
/*      */   private BooleanConnectionProperty cacheResultSetMetadata;
/*      */   private boolean cacheResultSetMetaDataAsBoolean;
/*      */   
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/*  648 */     return null;
/*      */   }
/*      */   
/*      */   private StringConnectionProperty serverConfigCacheFactory;
/*      */   private BooleanConnectionProperty cacheServerConfiguration;
/*      */   private IntegerConnectionProperty callableStatementCacheSize;
/*      */   private BooleanConnectionProperty capitalizeTypeNames;
/*      */   private StringConnectionProperty characterEncoding;
/*      */   private String characterEncodingAsString;
/*      */   protected boolean characterEncodingIsAliasForSjis;
/*      */   private StringConnectionProperty characterSetResults;
/*      */   private StringConnectionProperty connectionAttributes;
/*      */   private StringConnectionProperty clientInfoProvider;
/*      */   private BooleanConnectionProperty clobberStreamingResults;
/*      */   private StringConnectionProperty clobCharacterEncoding;
/*      */   private BooleanConnectionProperty compensateOnDuplicateKeyUpdateCounts;
/*      */   private StringConnectionProperty connectionCollation;
/*      */   private StringConnectionProperty connectionLifecycleInterceptors;
/*      */   private IntegerConnectionProperty connectTimeout;
/*      */   private BooleanConnectionProperty continueBatchOnError;
/*      */   private BooleanConnectionProperty createDatabaseIfNotExist;
/*      */   private IntegerConnectionProperty defaultFetchSize;
/*      */   
/*      */   protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/*  666 */     new ConnectionPropertiesImpl() { private static final long serialVersionUID = 4257801713007640581L; }.exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
/*      */   }
/*      */   
/*      */   public ConnectionPropertiesImpl()
/*      */   {
/*  671 */     this.allowLoadLocalInfile = new BooleanConnectionProperty("allowLoadLocalInfile", true, Messages.getString("ConnectionProperties.loadDataLocal"), "3.0.3", SECURITY_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  674 */     this.allowMultiQueries = new BooleanConnectionProperty("allowMultiQueries", false, Messages.getString("ConnectionProperties.allowMultiQueries"), "3.1.1", SECURITY_CATEGORY, 1);
/*      */     
/*      */ 
/*  677 */     this.allowNanAndInf = new BooleanConnectionProperty("allowNanAndInf", false, Messages.getString("ConnectionProperties.allowNANandINF"), "3.1.5", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  680 */     this.allowUrlInLocalInfile = new BooleanConnectionProperty("allowUrlInLocalInfile", false, Messages.getString("ConnectionProperties.allowUrlInLoadLocal"), "3.1.4", SECURITY_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  683 */     this.alwaysSendSetIsolation = new BooleanConnectionProperty("alwaysSendSetIsolation", true, Messages.getString("ConnectionProperties.alwaysSendSetIsolation"), "3.1.7", PERFORMANCE_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  686 */     this.autoClosePStmtStreams = new BooleanConnectionProperty("autoClosePStmtStreams", false, Messages.getString("ConnectionProperties.autoClosePstmtStreams"), "3.1.12", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  689 */     this.allowMasterDownConnections = new BooleanConnectionProperty("allowMasterDownConnections", false, Messages.getString("ConnectionProperties.allowMasterDownConnections"), "5.1.27", HA_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  692 */     this.allowSlaveDownConnections = new BooleanConnectionProperty("allowSlaveDownConnections", false, Messages.getString("ConnectionProperties.allowSlaveDownConnections"), "5.1.38", HA_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  695 */     this.readFromMasterWhenNoSlaves = new BooleanConnectionProperty("readFromMasterWhenNoSlaves", false, Messages.getString("ConnectionProperties.readFromMasterWhenNoSlaves"), "5.1.38", HA_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  698 */     this.autoDeserialize = new BooleanConnectionProperty("autoDeserialize", false, Messages.getString("ConnectionProperties.autoDeserialize"), "3.1.5", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  701 */     this.autoGenerateTestcaseScript = new BooleanConnectionProperty("autoGenerateTestcaseScript", false, Messages.getString("ConnectionProperties.autoGenerateTestcaseScript"), "3.1.9", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  704 */     this.autoGenerateTestcaseScriptAsBoolean = false;
/*      */     
/*  706 */     this.autoReconnect = new BooleanConnectionProperty("autoReconnect", false, Messages.getString("ConnectionProperties.autoReconnect"), "1.1", HA_CATEGORY, 0);
/*      */     
/*      */ 
/*  709 */     this.autoReconnectForPools = new BooleanConnectionProperty("autoReconnectForPools", false, Messages.getString("ConnectionProperties.autoReconnectForPools"), "3.1.3", HA_CATEGORY, 1);
/*      */     
/*      */ 
/*  712 */     this.autoReconnectForPoolsAsBoolean = false;
/*      */     
/*  714 */     this.blobSendChunkSize = new MemorySizeConnectionProperty("blobSendChunkSize", 1048576, 0, 0, Messages.getString("ConnectionProperties.blobSendChunkSize"), "3.1.9", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  717 */     this.autoSlowLog = new BooleanConnectionProperty("autoSlowLog", true, Messages.getString("ConnectionProperties.autoSlowLog"), "5.1.4", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  720 */     this.blobsAreStrings = new BooleanConnectionProperty("blobsAreStrings", false, "Should the driver always treat BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*  724 */     this.functionsNeverReturnBlobs = new BooleanConnectionProperty("functionsNeverReturnBlobs", false, "Should the driver always treat data from functions returning BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  729 */     this.cacheCallableStatements = new BooleanConnectionProperty("cacheCallableStmts", false, Messages.getString("ConnectionProperties.cacheCallableStatements"), "3.1.2", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  732 */     this.cachePreparedStatements = new BooleanConnectionProperty("cachePrepStmts", false, Messages.getString("ConnectionProperties.cachePrepStmts"), "3.0.10", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  735 */     this.cacheResultSetMetadata = new BooleanConnectionProperty("cacheResultSetMetadata", false, Messages.getString("ConnectionProperties.cacheRSMetadata"), "3.1.1", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  740 */     this.serverConfigCacheFactory = new StringConnectionProperty("serverConfigCacheFactory", PerVmServerConfigCacheFactory.class.getName(), Messages.getString("ConnectionProperties.serverConfigCacheFactory"), "5.1.1", PERFORMANCE_CATEGORY, 12);
/*      */     
/*      */ 
/*      */ 
/*  744 */     this.cacheServerConfiguration = new BooleanConnectionProperty("cacheServerConfiguration", false, Messages.getString("ConnectionProperties.cacheServerConfiguration"), "3.1.5", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  747 */     this.callableStatementCacheSize = new IntegerConnectionProperty("callableStmtCacheSize", 100, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.callableStmtCacheSize"), "3.1.2", PERFORMANCE_CATEGORY, 5);
/*      */     
/*      */ 
/*  750 */     this.capitalizeTypeNames = new BooleanConnectionProperty("capitalizeTypeNames", true, Messages.getString("ConnectionProperties.capitalizeTypeNames"), "2.0.7", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  753 */     this.characterEncoding = new StringConnectionProperty("characterEncoding", null, Messages.getString("ConnectionProperties.characterEncoding"), "1.1g", MISC_CATEGORY, 5);
/*      */     
/*      */ 
/*  756 */     this.characterEncodingAsString = null;
/*      */     
/*  758 */     this.characterEncodingIsAliasForSjis = false;
/*      */     
/*  760 */     this.characterSetResults = new StringConnectionProperty("characterSetResults", null, Messages.getString("ConnectionProperties.characterSetResults"), "3.0.13", MISC_CATEGORY, 6);
/*      */     
/*      */ 
/*  763 */     this.connectionAttributes = new StringConnectionProperty("connectionAttributes", null, Messages.getString("ConnectionProperties.connectionAttributes"), "5.1.25", MISC_CATEGORY, 7);
/*      */     
/*      */ 
/*  766 */     this.clientInfoProvider = new StringConnectionProperty("clientInfoProvider", "com.mysql.jdbc.JDBC4CommentClientInfoProvider", Messages.getString("ConnectionProperties.clientInfoProvider"), "5.1.0", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  769 */     this.clobberStreamingResults = new BooleanConnectionProperty("clobberStreamingResults", false, Messages.getString("ConnectionProperties.clobberStreamingResults"), "3.0.9", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  772 */     this.clobCharacterEncoding = new StringConnectionProperty("clobCharacterEncoding", null, Messages.getString("ConnectionProperties.clobCharacterEncoding"), "5.0.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  775 */     this.compensateOnDuplicateKeyUpdateCounts = new BooleanConnectionProperty("compensateOnDuplicateKeyUpdateCounts", false, Messages.getString("ConnectionProperties.compensateOnDuplicateKeyUpdateCounts"), "5.1.7", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*  777 */     this.connectionCollation = new StringConnectionProperty("connectionCollation", null, Messages.getString("ConnectionProperties.connectionCollation"), "3.0.13", MISC_CATEGORY, 7);
/*      */     
/*      */ 
/*  780 */     this.connectionLifecycleInterceptors = new StringConnectionProperty("connectionLifecycleInterceptors", null, Messages.getString("ConnectionProperties.connectionLifecycleInterceptors"), "5.1.4", CONNECTION_AND_AUTH_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  783 */     this.connectTimeout = new IntegerConnectionProperty("connectTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.connectTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 9);
/*      */     
/*      */ 
/*  786 */     this.continueBatchOnError = new BooleanConnectionProperty("continueBatchOnError", true, Messages.getString("ConnectionProperties.continueBatchOnError"), "3.0.3", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  789 */     this.createDatabaseIfNotExist = new BooleanConnectionProperty("createDatabaseIfNotExist", false, Messages.getString("ConnectionProperties.createDatabaseIfNotExist"), "3.1.9", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  792 */     this.defaultFetchSize = new IntegerConnectionProperty("defaultFetchSize", 0, Messages.getString("ConnectionProperties.defaultFetchSize"), "3.1.9", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  797 */     this.detectServerPreparedStmts = new BooleanConnectionProperty("useServerPrepStmts", false, Messages.getString("ConnectionProperties.useServerPrepStmts"), "3.1.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  800 */     this.dontTrackOpenResources = new BooleanConnectionProperty("dontTrackOpenResources", false, Messages.getString("ConnectionProperties.dontTrackOpenResources"), "3.1.7", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  803 */     this.dumpQueriesOnException = new BooleanConnectionProperty("dumpQueriesOnException", false, Messages.getString("ConnectionProperties.dumpQueriesOnException"), "3.1.3", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  806 */     this.dynamicCalendars = new BooleanConnectionProperty("dynamicCalendars", false, Messages.getString("ConnectionProperties.dynamicCalendars"), "3.1.5", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  809 */     this.elideSetAutoCommits = new BooleanConnectionProperty("elideSetAutoCommits", false, Messages.getString("ConnectionProperties.eliseSetAutoCommit"), "3.1.3", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  812 */     this.emptyStringsConvertToZero = new BooleanConnectionProperty("emptyStringsConvertToZero", true, Messages.getString("ConnectionProperties.emptyStringsConvertToZero"), "3.1.8", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  815 */     this.emulateLocators = new BooleanConnectionProperty("emulateLocators", false, Messages.getString("ConnectionProperties.emulateLocators"), "3.1.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  818 */     this.emulateUnsupportedPstmts = new BooleanConnectionProperty("emulateUnsupportedPstmts", true, Messages.getString("ConnectionProperties.emulateUnsupportedPstmts"), "3.1.7", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  821 */     this.enablePacketDebug = new BooleanConnectionProperty("enablePacketDebug", false, Messages.getString("ConnectionProperties.enablePacketDebug"), "3.1.3", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  824 */     this.enableQueryTimeouts = new BooleanConnectionProperty("enableQueryTimeouts", true, Messages.getString("ConnectionProperties.enableQueryTimeouts"), "5.0.6", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  827 */     this.explainSlowQueries = new BooleanConnectionProperty("explainSlowQueries", false, Messages.getString("ConnectionProperties.explainSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  830 */     this.exceptionInterceptors = new StringConnectionProperty("exceptionInterceptors", null, Messages.getString("ConnectionProperties.exceptionInterceptors"), "5.1.8", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*  834 */     this.failOverReadOnly = new BooleanConnectionProperty("failOverReadOnly", true, Messages.getString("ConnectionProperties.failoverReadOnly"), "3.0.12", HA_CATEGORY, 2);
/*      */     
/*      */ 
/*  837 */     this.gatherPerformanceMetrics = new BooleanConnectionProperty("gatherPerfMetrics", false, Messages.getString("ConnectionProperties.gatherPerfMetrics"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 1);
/*      */     
/*      */ 
/*  840 */     this.generateSimpleParameterMetadata = new BooleanConnectionProperty("generateSimpleParameterMetadata", false, Messages.getString("ConnectionProperties.generateSimpleParameterMetadata"), "5.0.5", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  843 */     this.highAvailabilityAsBoolean = false;
/*      */     
/*  845 */     this.holdResultsOpenOverStatementClose = new BooleanConnectionProperty("holdResultsOpenOverStatementClose", false, Messages.getString("ConnectionProperties.holdRSOpenOverStmtClose"), "3.1.7", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  848 */     this.includeInnodbStatusInDeadlockExceptions = new BooleanConnectionProperty("includeInnodbStatusInDeadlockExceptions", false, Messages.getString("ConnectionProperties.includeInnodbStatusInDeadlockExceptions"), "5.0.7", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  851 */     this.includeThreadDumpInDeadlockExceptions = new BooleanConnectionProperty("includeThreadDumpInDeadlockExceptions", false, Messages.getString("ConnectionProperties.includeThreadDumpInDeadlockExceptions"), "5.1.15", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  854 */     this.includeThreadNamesAsStatementComment = new BooleanConnectionProperty("includeThreadNamesAsStatementComment", false, Messages.getString("ConnectionProperties.includeThreadNamesAsStatementComment"), "5.1.15", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  857 */     this.ignoreNonTxTables = new BooleanConnectionProperty("ignoreNonTxTables", false, Messages.getString("ConnectionProperties.ignoreNonTxTables"), "3.0.9", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  860 */     this.initialTimeout = new IntegerConnectionProperty("initialTimeout", 2, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.initialTimeout"), "1.1", HA_CATEGORY, 5);
/*      */     
/*      */ 
/*  863 */     this.isInteractiveClient = new BooleanConnectionProperty("interactiveClient", false, Messages.getString("ConnectionProperties.interactiveClient"), "3.1.0", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  866 */     this.jdbcCompliantTruncation = new BooleanConnectionProperty("jdbcCompliantTruncation", true, Messages.getString("ConnectionProperties.jdbcCompliantTruncation"), "3.1.2", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  869 */     this.jdbcCompliantTruncationForReads = this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */     
/*  871 */     this.largeRowSizeThreshold = new MemorySizeConnectionProperty("largeRowSizeThreshold", 2048, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.largeRowSizeThreshold"), "5.1.1", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  874 */     this.loadBalanceStrategy = new StringConnectionProperty("loadBalanceStrategy", "random", null, Messages.getString("ConnectionProperties.loadBalanceStrategy"), "5.0.6", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  877 */     this.loadBalanceBlacklistTimeout = new IntegerConnectionProperty("loadBalanceBlacklistTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalanceBlacklistTimeout"), "5.1.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  880 */     this.loadBalancePingTimeout = new IntegerConnectionProperty("loadBalancePingTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalancePingTimeout"), "5.1.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  883 */     this.loadBalanceValidateConnectionOnSwapServer = new BooleanConnectionProperty("loadBalanceValidateConnectionOnSwapServer", false, Messages.getString("ConnectionProperties.loadBalanceValidateConnectionOnSwapServer"), "5.1.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  886 */     this.loadBalanceConnectionGroup = new StringConnectionProperty("loadBalanceConnectionGroup", null, Messages.getString("ConnectionProperties.loadBalanceConnectionGroup"), "5.1.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  889 */     this.loadBalanceExceptionChecker = new StringConnectionProperty("loadBalanceExceptionChecker", "com.mysql.jdbc.StandardLoadBalanceExceptionChecker", null, Messages.getString("ConnectionProperties.loadBalanceExceptionChecker"), "5.1.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*  893 */     this.loadBalanceSQLStateFailover = new StringConnectionProperty("loadBalanceSQLStateFailover", null, Messages.getString("ConnectionProperties.loadBalanceSQLStateFailover"), "5.1.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  896 */     this.loadBalanceSQLExceptionSubclassFailover = new StringConnectionProperty("loadBalanceSQLExceptionSubclassFailover", null, Messages.getString("ConnectionProperties.loadBalanceSQLExceptionSubclassFailover"), "5.1.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  899 */     this.loadBalanceEnableJMX = new BooleanConnectionProperty("loadBalanceEnableJMX", false, Messages.getString("ConnectionProperties.loadBalanceEnableJMX"), "5.1.13", MISC_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  902 */     this.loadBalanceHostRemovalGracePeriod = new IntegerConnectionProperty("loadBalanceHostRemovalGracePeriod", 15000, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalanceHostRemovalGracePeriod"), "5.1.39", MISC_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  905 */     this.loadBalanceAutoCommitStatementRegex = new StringConnectionProperty("loadBalanceAutoCommitStatementRegex", null, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementRegex"), "5.1.15", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  908 */     this.loadBalanceAutoCommitStatementThreshold = new IntegerConnectionProperty("loadBalanceAutoCommitStatementThreshold", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementThreshold"), "5.1.15", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  911 */     this.localSocketAddress = new StringConnectionProperty("localSocketAddress", null, Messages.getString("ConnectionProperties.localSocketAddress"), "5.0.5", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  914 */     this.locatorFetchBufferSize = new MemorySizeConnectionProperty("locatorFetchBufferSize", 1048576, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.locatorFetchBufferSize"), "3.2.1", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  917 */     this.loggerClassName = new StringConnectionProperty("logger", STANDARD_LOGGER_NAME, Messages.getString("ConnectionProperties.logger", new Object[] { Log.class.getName(), STANDARD_LOGGER_NAME }), "3.1.1", DEBUGING_PROFILING_CATEGORY, 0);
/*      */     
/*      */ 
/*      */ 
/*  921 */     this.logSlowQueries = new BooleanConnectionProperty("logSlowQueries", false, Messages.getString("ConnectionProperties.logSlowQueries"), "3.1.2", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  924 */     this.logXaCommands = new BooleanConnectionProperty("logXaCommands", false, Messages.getString("ConnectionProperties.logXaCommands"), "5.0.5", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  927 */     this.maintainTimeStats = new BooleanConnectionProperty("maintainTimeStats", true, Messages.getString("ConnectionProperties.maintainTimeStats"), "3.1.9", PERFORMANCE_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/*  930 */     this.maintainTimeStatsAsBoolean = true;
/*      */     
/*  932 */     this.maxQuerySizeToLog = new IntegerConnectionProperty("maxQuerySizeToLog", 2048, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.maxQuerySizeToLog"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 4);
/*      */     
/*      */ 
/*  935 */     this.maxReconnects = new IntegerConnectionProperty("maxReconnects", 3, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.maxReconnects"), "1.1", HA_CATEGORY, 4);
/*      */     
/*      */ 
/*  938 */     this.retriesAllDown = new IntegerConnectionProperty("retriesAllDown", 120, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.retriesAllDown"), "5.1.6", HA_CATEGORY, 4);
/*      */     
/*      */ 
/*  941 */     this.maxRows = new IntegerConnectionProperty("maxRows", -1, -1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.maxRows"), Messages.getString("ConnectionProperties.allVersions"), MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  944 */     this.maxRowsAsInt = -1;
/*      */     
/*  946 */     this.metadataCacheSize = new IntegerConnectionProperty("metadataCacheSize", 50, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.metadataCacheSize"), "3.1.1", PERFORMANCE_CATEGORY, 5);
/*      */     
/*      */ 
/*  949 */     this.netTimeoutForStreamingResults = new IntegerConnectionProperty("netTimeoutForStreamingResults", 600, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.netTimeoutForStreamingResults"), "5.1.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  952 */     this.noAccessToProcedureBodies = new BooleanConnectionProperty("noAccessToProcedureBodies", false, "When determining procedure parameter types for CallableStatements, and the connected user  can't access procedure bodies through \"SHOW CREATE PROCEDURE\" or select on mysql.proc  should the driver instead create basic metadata (all parameters reported as IN VARCHARs, but allowing registerOutParameter() to be called on them anyway) instead of throwing an exception?", "5.0.3", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  959 */     this.noDatetimeStringSync = new BooleanConnectionProperty("noDatetimeStringSync", false, Messages.getString("ConnectionProperties.noDatetimeStringSync"), "3.1.7", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  962 */     this.noTimezoneConversionForTimeType = new BooleanConnectionProperty("noTimezoneConversionForTimeType", false, Messages.getString("ConnectionProperties.noTzConversionForTimeType"), "5.0.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  965 */     this.noTimezoneConversionForDateType = new BooleanConnectionProperty("noTimezoneConversionForDateType", true, Messages.getString("ConnectionProperties.noTzConversionForDateType"), "5.1.35", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  968 */     this.cacheDefaultTimezone = new BooleanConnectionProperty("cacheDefaultTimezone", true, Messages.getString("ConnectionProperties.cacheDefaultTimezone"), "5.1.35", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  971 */     this.nullCatalogMeansCurrent = new BooleanConnectionProperty("nullCatalogMeansCurrent", true, Messages.getString("ConnectionProperties.nullCatalogMeansCurrent"), "3.1.8", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  974 */     this.nullNamePatternMatchesAll = new BooleanConnectionProperty("nullNamePatternMatchesAll", true, Messages.getString("ConnectionProperties.nullNamePatternMatchesAll"), "3.1.8", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  977 */     this.packetDebugBufferSize = new IntegerConnectionProperty("packetDebugBufferSize", 20, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.packetDebugBufferSize"), "3.1.3", DEBUGING_PROFILING_CATEGORY, 7);
/*      */     
/*      */ 
/*  980 */     this.padCharsWithSpace = new BooleanConnectionProperty("padCharsWithSpace", false, Messages.getString("ConnectionProperties.padCharsWithSpace"), "5.0.6", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  983 */     this.paranoid = new BooleanConnectionProperty("paranoid", false, Messages.getString("ConnectionProperties.paranoid"), "3.0.1", SECURITY_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  986 */     this.pedantic = new BooleanConnectionProperty("pedantic", false, Messages.getString("ConnectionProperties.pedantic"), "3.0.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  989 */     this.pinGlobalTxToPhysicalConnection = new BooleanConnectionProperty("pinGlobalTxToPhysicalConnection", false, Messages.getString("ConnectionProperties.pinGlobalTxToPhysicalConnection"), "5.0.1", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  992 */     this.populateInsertRowWithDefaultValues = new BooleanConnectionProperty("populateInsertRowWithDefaultValues", false, Messages.getString("ConnectionProperties.populateInsertRowWithDefaultValues"), "5.0.5", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*  995 */     this.preparedStatementCacheSize = new IntegerConnectionProperty("prepStmtCacheSize", 25, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.prepStmtCacheSize"), "3.0.10", PERFORMANCE_CATEGORY, 10);
/*      */     
/*      */ 
/*  998 */     this.preparedStatementCacheSqlLimit = new IntegerConnectionProperty("prepStmtCacheSqlLimit", 256, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.prepStmtCacheSqlLimit"), "3.0.10", PERFORMANCE_CATEGORY, 11);
/*      */     
/*      */ 
/* 1001 */     this.parseInfoCacheFactory = new StringConnectionProperty("parseInfoCacheFactory", PerConnectionLRUFactory.class.getName(), Messages.getString("ConnectionProperties.parseInfoCacheFactory"), "5.1.1", PERFORMANCE_CATEGORY, 12);
/*      */     
/*      */ 
/* 1004 */     this.processEscapeCodesForPrepStmts = new BooleanConnectionProperty("processEscapeCodesForPrepStmts", true, Messages.getString("ConnectionProperties.processEscapeCodesForPrepStmts"), "3.1.12", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1007 */     this.profilerEventHandler = new StringConnectionProperty("profilerEventHandler", "com.mysql.jdbc.profiler.LoggingProfilerEventHandler", Messages.getString("ConnectionProperties.profilerEventHandler"), "5.1.6", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1011 */     this.profileSql = new StringConnectionProperty("profileSql", null, Messages.getString("ConnectionProperties.profileSqlDeprecated"), "2.0.14", DEBUGING_PROFILING_CATEGORY, 3);
/*      */     
/*      */ 
/* 1014 */     this.profileSQL = new BooleanConnectionProperty("profileSQL", false, Messages.getString("ConnectionProperties.profileSQL"), "3.1.0", DEBUGING_PROFILING_CATEGORY, 1);
/*      */     
/*      */ 
/* 1017 */     this.profileSQLAsBoolean = false;
/*      */     
/* 1019 */     this.propertiesTransform = new StringConnectionProperty("propertiesTransform", null, Messages.getString("ConnectionProperties.connectionPropertiesTransform"), "3.1.4", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1022 */     this.queriesBeforeRetryMaster = new IntegerConnectionProperty("queriesBeforeRetryMaster", 50, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.queriesBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 7);
/*      */     
/*      */ 
/* 1025 */     this.queryTimeoutKillsConnection = new BooleanConnectionProperty("queryTimeoutKillsConnection", false, Messages.getString("ConnectionProperties.queryTimeoutKillsConnection"), "5.1.9", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1028 */     this.reconnectAtTxEnd = new BooleanConnectionProperty("reconnectAtTxEnd", false, Messages.getString("ConnectionProperties.reconnectAtTxEnd"), "3.0.10", HA_CATEGORY, 4);
/*      */     
/*      */ 
/* 1031 */     this.reconnectTxAtEndAsBoolean = false;
/*      */     
/* 1033 */     this.relaxAutoCommit = new BooleanConnectionProperty("relaxAutoCommit", false, Messages.getString("ConnectionProperties.relaxAutoCommit"), "2.0.13", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1036 */     this.reportMetricsIntervalMillis = new IntegerConnectionProperty("reportMetricsIntervalMillis", 30000, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.reportMetricsIntervalMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 3);
/*      */     
/*      */ 
/* 1039 */     this.requireSSL = new BooleanConnectionProperty("requireSSL", false, Messages.getString("ConnectionProperties.requireSSL"), "3.1.0", SECURITY_CATEGORY, 3);
/*      */     
/*      */ 
/* 1042 */     this.resourceId = new StringConnectionProperty("resourceId", null, Messages.getString("ConnectionProperties.resourceId"), "5.0.1", HA_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1045 */     this.resultSetSizeThreshold = new IntegerConnectionProperty("resultSetSizeThreshold", 100, Messages.getString("ConnectionProperties.resultSetSizeThreshold"), "5.0.5", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1048 */     this.retainStatementAfterResultSetClose = new BooleanConnectionProperty("retainStatementAfterResultSetClose", false, Messages.getString("ConnectionProperties.retainStatementAfterResultSetClose"), "3.1.11", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1051 */     this.rewriteBatchedStatements = new BooleanConnectionProperty("rewriteBatchedStatements", false, Messages.getString("ConnectionProperties.rewriteBatchedStatements"), "3.1.13", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1054 */     this.rollbackOnPooledClose = new BooleanConnectionProperty("rollbackOnPooledClose", true, Messages.getString("ConnectionProperties.rollbackOnPooledClose"), "3.0.15", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1057 */     this.roundRobinLoadBalance = new BooleanConnectionProperty("roundRobinLoadBalance", false, Messages.getString("ConnectionProperties.roundRobinLoadBalance"), "3.1.2", HA_CATEGORY, 5);
/*      */     
/*      */ 
/* 1060 */     this.runningCTS13 = new BooleanConnectionProperty("runningCTS13", false, Messages.getString("ConnectionProperties.runningCTS13"), "3.1.7", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1063 */     this.secondsBeforeRetryMaster = new IntegerConnectionProperty("secondsBeforeRetryMaster", 30, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.secondsBeforeRetryMaster"), "3.0.2", HA_CATEGORY, 8);
/*      */     
/*      */ 
/* 1066 */     this.selfDestructOnPingSecondsLifetime = new IntegerConnectionProperty("selfDestructOnPingSecondsLifetime", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.selfDestructOnPingSecondsLifetime"), "5.1.6", HA_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1069 */     this.selfDestructOnPingMaxOperations = new IntegerConnectionProperty("selfDestructOnPingMaxOperations", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.selfDestructOnPingMaxOperations"), "5.1.6", HA_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1072 */     this.replicationEnableJMX = new BooleanConnectionProperty("replicationEnableJMX", false, Messages.getString("ConnectionProperties.loadBalanceEnableJMX"), "5.1.27", HA_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1075 */     this.serverTimezone = new StringConnectionProperty("serverTimezone", null, Messages.getString("ConnectionProperties.serverTimezone"), "3.0.2", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1078 */     this.sessionVariables = new StringConnectionProperty("sessionVariables", null, Messages.getString("ConnectionProperties.sessionVariables"), "3.1.8", MISC_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1081 */     this.slowQueryThresholdMillis = new IntegerConnectionProperty("slowQueryThresholdMillis", 2000, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.slowQueryThresholdMillis"), "3.1.2", DEBUGING_PROFILING_CATEGORY, 9);
/*      */     
/*      */ 
/* 1084 */     this.slowQueryThresholdNanos = new LongConnectionProperty("slowQueryThresholdNanos", 0L, Messages.getString("ConnectionProperties.slowQueryThresholdNanos"), "5.0.7", DEBUGING_PROFILING_CATEGORY, 10);
/*      */     
/*      */ 
/* 1087 */     this.socketFactoryClassName = new StringConnectionProperty("socketFactory", StandardSocketFactory.class.getName(), Messages.getString("ConnectionProperties.socketFactory"), "3.0.3", CONNECTION_AND_AUTH_CATEGORY, 4);
/*      */     
/*      */ 
/* 1090 */     this.socksProxyHost = new StringConnectionProperty("socksProxyHost", null, Messages.getString("ConnectionProperties.socksProxyHost"), "5.1.34", NETWORK_CATEGORY, 1);
/*      */     
/*      */ 
/* 1093 */     this.socksProxyPort = new IntegerConnectionProperty("socksProxyPort", SocksProxySocketFactory.SOCKS_DEFAULT_PORT, 0, 65535, Messages.getString("ConnectionProperties.socksProxyPort"), "5.1.34", NETWORK_CATEGORY, 2);
/*      */     
/*      */ 
/* 1096 */     this.socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", CONNECTION_AND_AUTH_CATEGORY, 10);
/*      */     
/*      */ 
/* 1099 */     this.statementInterceptors = new StringConnectionProperty("statementInterceptors", null, Messages.getString("ConnectionProperties.statementInterceptors"), "5.1.1", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1102 */     this.strictFloatingPoint = new BooleanConnectionProperty("strictFloatingPoint", false, Messages.getString("ConnectionProperties.strictFloatingPoint"), "3.0.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1105 */     this.strictUpdates = new BooleanConnectionProperty("strictUpdates", true, Messages.getString("ConnectionProperties.strictUpdates"), "3.0.4", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1108 */     this.overrideSupportsIntegrityEnhancementFacility = new BooleanConnectionProperty("overrideSupportsIntegrityEnhancementFacility", false, Messages.getString("ConnectionProperties.overrideSupportsIEF"), "3.1.12", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1112 */     this.tcpNoDelay = new BooleanConnectionProperty("tcpNoDelay", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpNoDelay"), "5.0.7", NETWORK_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1116 */     this.tcpKeepAlive = new BooleanConnectionProperty("tcpKeepAlive", Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpKeepAlive"), "5.0.7", NETWORK_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1120 */     this.tcpRcvBuf = new IntegerConnectionProperty("tcpRcvBuf", Integer.parseInt("0"), 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.tcpSoRcvBuf"), "5.0.7", NETWORK_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1124 */     this.tcpSndBuf = new IntegerConnectionProperty("tcpSndBuf", Integer.parseInt("0"), 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.tcpSoSndBuf"), "5.0.7", NETWORK_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1128 */     this.tcpTrafficClass = new IntegerConnectionProperty("tcpTrafficClass", Integer.parseInt("0"), 0, 255, Messages.getString("ConnectionProperties.tcpTrafficClass"), "5.0.7", NETWORK_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1132 */     this.tinyInt1isBit = new BooleanConnectionProperty("tinyInt1isBit", true, Messages.getString("ConnectionProperties.tinyInt1isBit"), "3.0.16", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1135 */     this.traceProtocol = new BooleanConnectionProperty("traceProtocol", false, Messages.getString("ConnectionProperties.traceProtocol"), "3.1.2", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1138 */     this.treatUtilDateAsTimestamp = new BooleanConnectionProperty("treatUtilDateAsTimestamp", true, Messages.getString("ConnectionProperties.treatUtilDateAsTimestamp"), "5.0.5", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1141 */     this.transformedBitIsBoolean = new BooleanConnectionProperty("transformedBitIsBoolean", false, Messages.getString("ConnectionProperties.transformedBitIsBoolean"), "3.1.9", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1144 */     this.useBlobToStoreUTF8OutsideBMP = new BooleanConnectionProperty("useBlobToStoreUTF8OutsideBMP", false, Messages.getString("ConnectionProperties.useBlobToStoreUTF8OutsideBMP"), "5.1.3", MISC_CATEGORY, 128);
/*      */     
/*      */ 
/* 1147 */     this.utf8OutsideBmpExcludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpExcludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpExcludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */     
/*      */ 
/* 1150 */     this.utf8OutsideBmpIncludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpIncludedColumnNamePattern", null, Messages.getString("ConnectionProperties.utf8OutsideBmpIncludedColumnNamePattern"), "5.1.3", MISC_CATEGORY, 129);
/*      */     
/*      */ 
/* 1153 */     this.useCompression = new BooleanConnectionProperty("useCompression", false, Messages.getString("ConnectionProperties.useCompression"), "3.0.17", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1156 */     this.useColumnNamesInFindColumn = new BooleanConnectionProperty("useColumnNamesInFindColumn", false, Messages.getString("ConnectionProperties.useColumnNamesInFindColumn"), "5.1.7", MISC_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1159 */     this.useConfigs = new StringConnectionProperty("useConfigs", null, Messages.getString("ConnectionProperties.useConfigs"), "3.1.5", CONNECTION_AND_AUTH_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1162 */     this.useCursorFetch = new BooleanConnectionProperty("useCursorFetch", false, Messages.getString("ConnectionProperties.useCursorFetch"), "5.0.0", PERFORMANCE_CATEGORY, Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 1165 */     this.useDynamicCharsetInfo = new BooleanConnectionProperty("useDynamicCharsetInfo", true, Messages.getString("ConnectionProperties.useDynamicCharsetInfo"), "5.0.6", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1168 */     this.useDirectRowUnpack = new BooleanConnectionProperty("useDirectRowUnpack", true, "Use newer result set row unpacking code that skips a copy from network buffers  to a MySQL packet instance and instead reads directly into the result set row data buffers.", "5.1.1", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1173 */     this.useFastIntParsing = new BooleanConnectionProperty("useFastIntParsing", true, Messages.getString("ConnectionProperties.useFastIntParsing"), "3.1.4", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1176 */     this.useFastDateParsing = new BooleanConnectionProperty("useFastDateParsing", true, Messages.getString("ConnectionProperties.useFastDateParsing"), "5.0.5", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1179 */     this.useHostsInPrivileges = new BooleanConnectionProperty("useHostsInPrivileges", true, Messages.getString("ConnectionProperties.useHostsInPrivileges"), "3.0.2", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/* 1181 */     this.useInformationSchema = new BooleanConnectionProperty("useInformationSchema", false, Messages.getString("ConnectionProperties.useInformationSchema"), "5.0.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/* 1183 */     this.useJDBCCompliantTimezoneShift = new BooleanConnectionProperty("useJDBCCompliantTimezoneShift", false, Messages.getString("ConnectionProperties.useJDBCCompliantTimezoneShift"), "5.0.0", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1186 */     this.useLocalSessionState = new BooleanConnectionProperty("useLocalSessionState", false, Messages.getString("ConnectionProperties.useLocalSessionState"), "3.1.7", PERFORMANCE_CATEGORY, 5);
/*      */     
/*      */ 
/* 1189 */     this.useLocalTransactionState = new BooleanConnectionProperty("useLocalTransactionState", false, Messages.getString("ConnectionProperties.useLocalTransactionState"), "5.1.7", PERFORMANCE_CATEGORY, 6);
/*      */     
/*      */ 
/* 1192 */     this.useLegacyDatetimeCode = new BooleanConnectionProperty("useLegacyDatetimeCode", true, Messages.getString("ConnectionProperties.useLegacyDatetimeCode"), "5.1.6", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1195 */     this.sendFractionalSeconds = new BooleanConnectionProperty("sendFractionalSeconds", true, Messages.getString("ConnectionProperties.sendFractionalSeconds"), "5.1.37", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1198 */     this.useNanosForElapsedTime = new BooleanConnectionProperty("useNanosForElapsedTime", false, Messages.getString("ConnectionProperties.useNanosForElapsedTime"), "5.0.7", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1201 */     this.useOldAliasMetadataBehavior = new BooleanConnectionProperty("useOldAliasMetadataBehavior", false, Messages.getString("ConnectionProperties.useOldAliasMetadataBehavior"), "5.0.4", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1204 */     this.useOldUTF8Behavior = new BooleanConnectionProperty("useOldUTF8Behavior", false, Messages.getString("ConnectionProperties.useOldUtf8Behavior"), "3.1.6", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1207 */     this.useOldUTF8BehaviorAsBoolean = false;
/*      */     
/* 1209 */     this.useOnlyServerErrorMessages = new BooleanConnectionProperty("useOnlyServerErrorMessages", true, Messages.getString("ConnectionProperties.useOnlyServerErrorMessages"), "3.0.15", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1212 */     this.useReadAheadInput = new BooleanConnectionProperty("useReadAheadInput", true, Messages.getString("ConnectionProperties.useReadAheadInput"), "3.1.5", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1215 */     this.useSqlStateCodes = new BooleanConnectionProperty("useSqlStateCodes", true, Messages.getString("ConnectionProperties.useSqlStateCodes"), "3.1.3", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1218 */     this.useSSL = new BooleanConnectionProperty("useSSL", false, Messages.getString("ConnectionProperties.useSSL"), "3.0.2", SECURITY_CATEGORY, 2);
/*      */     
/*      */ 
/* 1221 */     this.useSSPSCompatibleTimezoneShift = new BooleanConnectionProperty("useSSPSCompatibleTimezoneShift", false, Messages.getString("ConnectionProperties.useSSPSCompatibleTimezoneShift"), "5.0.5", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1224 */     this.useStreamLengthsInPrepStmts = new BooleanConnectionProperty("useStreamLengthsInPrepStmts", true, Messages.getString("ConnectionProperties.useStreamLengthsInPrepStmts"), "3.0.2", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1227 */     this.useTimezone = new BooleanConnectionProperty("useTimezone", false, Messages.getString("ConnectionProperties.useTimezone"), "3.0.2", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1230 */     this.useUltraDevWorkAround = new BooleanConnectionProperty("ultraDevHack", false, Messages.getString("ConnectionProperties.ultraDevHack"), "2.0.3", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1233 */     this.useUnbufferedInput = new BooleanConnectionProperty("useUnbufferedInput", true, Messages.getString("ConnectionProperties.useUnbufferedInput"), "3.0.11", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1236 */     this.useUnicode = new BooleanConnectionProperty("useUnicode", true, Messages.getString("ConnectionProperties.useUnicode"), "1.1g", MISC_CATEGORY, 0);
/*      */     
/*      */ 
/*      */ 
/* 1240 */     this.useUnicodeAsBoolean = true;
/*      */     
/* 1242 */     this.useUsageAdvisor = new BooleanConnectionProperty("useUsageAdvisor", false, Messages.getString("ConnectionProperties.useUsageAdvisor"), "3.1.1", DEBUGING_PROFILING_CATEGORY, 10);
/*      */     
/*      */ 
/* 1245 */     this.useUsageAdvisorAsBoolean = false;
/*      */     
/* 1247 */     this.yearIsDateType = new BooleanConnectionProperty("yearIsDateType", true, Messages.getString("ConnectionProperties.yearIsDateType"), "3.1.9", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1250 */     this.zeroDateTimeBehavior = new StringConnectionProperty("zeroDateTimeBehavior", "exception", new String[] { "exception", "round", "convertToNull" }, Messages.getString("ConnectionProperties.zeroDateTimeBehavior", new Object[] { "exception", "round", "convertToNull" }), "3.1.4", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1256 */     this.useJvmCharsetConverters = new BooleanConnectionProperty("useJvmCharsetConverters", false, Messages.getString("ConnectionProperties.useJvmCharsetConverters"), "5.0.1", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1259 */     this.useGmtMillisForDatetimes = new BooleanConnectionProperty("useGmtMillisForDatetimes", false, Messages.getString("ConnectionProperties.useGmtMillisForDatetimes"), "3.1.12", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1262 */     this.dumpMetadataOnColumnNotFound = new BooleanConnectionProperty("dumpMetadataOnColumnNotFound", false, Messages.getString("ConnectionProperties.dumpMetadataOnColumnNotFound"), "3.1.13", DEBUGING_PROFILING_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1267 */     this.clientCertificateKeyStoreUrl = new StringConnectionProperty("clientCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 5);
/*      */     
/*      */ 
/* 1270 */     this.trustCertificateKeyStoreUrl = new StringConnectionProperty("trustCertificateKeyStoreUrl", null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreUrl"), "5.1.0", SECURITY_CATEGORY, 8);
/*      */     
/*      */ 
/* 1273 */     this.clientCertificateKeyStoreType = new StringConnectionProperty("clientCertificateKeyStoreType", "JKS", Messages.getString("ConnectionProperties.clientCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 6);
/*      */     
/*      */ 
/* 1276 */     this.clientCertificateKeyStorePassword = new StringConnectionProperty("clientCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.clientCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 7);
/*      */     
/*      */ 
/* 1279 */     this.trustCertificateKeyStoreType = new StringConnectionProperty("trustCertificateKeyStoreType", "JKS", Messages.getString("ConnectionProperties.trustCertificateKeyStoreType"), "5.1.0", SECURITY_CATEGORY, 9);
/*      */     
/*      */ 
/* 1282 */     this.trustCertificateKeyStorePassword = new StringConnectionProperty("trustCertificateKeyStorePassword", null, Messages.getString("ConnectionProperties.trustCertificateKeyStorePassword"), "5.1.0", SECURITY_CATEGORY, 10);
/*      */     
/*      */ 
/* 1285 */     this.verifyServerCertificate = new BooleanConnectionProperty("verifyServerCertificate", true, Messages.getString("ConnectionProperties.verifyServerCertificate"), "5.1.6", SECURITY_CATEGORY, 4);
/*      */     
/*      */ 
/* 1288 */     this.useAffectedRows = new BooleanConnectionProperty("useAffectedRows", false, Messages.getString("ConnectionProperties.useAffectedRows"), "5.1.7", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1291 */     this.passwordCharacterEncoding = new StringConnectionProperty("passwordCharacterEncoding", null, Messages.getString("ConnectionProperties.passwordCharacterEncoding"), "5.1.7", SECURITY_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1294 */     this.maxAllowedPacket = new IntegerConnectionProperty("maxAllowedPacket", -1, Messages.getString("ConnectionProperties.maxAllowedPacket"), "5.1.8", NETWORK_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1297 */     this.authenticationPlugins = new StringConnectionProperty("authenticationPlugins", null, Messages.getString("ConnectionProperties.authenticationPlugins"), "5.1.19", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1300 */     this.disabledAuthenticationPlugins = new StringConnectionProperty("disabledAuthenticationPlugins", null, Messages.getString("ConnectionProperties.disabledAuthenticationPlugins"), "5.1.19", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1303 */     this.defaultAuthenticationPlugin = new StringConnectionProperty("defaultAuthenticationPlugin", "com.mysql.jdbc.authentication.MysqlNativePasswordPlugin", Messages.getString("ConnectionProperties.defaultAuthenticationPlugin"), "5.1.19", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/*      */ 
/* 1307 */     this.disconnectOnExpiredPasswords = new BooleanConnectionProperty("disconnectOnExpiredPasswords", true, Messages.getString("ConnectionProperties.disconnectOnExpiredPasswords"), "5.1.23", CONNECTION_AND_AUTH_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1310 */     this.getProceduresReturnsFunctions = new BooleanConnectionProperty("getProceduresReturnsFunctions", true, Messages.getString("ConnectionProperties.getProceduresReturnsFunctions"), "5.1.26", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1313 */     this.detectCustomCollations = new BooleanConnectionProperty("detectCustomCollations", false, Messages.getString("ConnectionProperties.detectCustomCollations"), "5.1.29", MISC_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1316 */     this.serverRSAPublicKeyFile = new StringConnectionProperty("serverRSAPublicKeyFile", null, Messages.getString("ConnectionProperties.serverRSAPublicKeyFile"), "5.1.31", SECURITY_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1319 */     this.allowPublicKeyRetrieval = new BooleanConnectionProperty("allowPublicKeyRetrieval", false, Messages.getString("ConnectionProperties.allowPublicKeyRetrieval"), "5.1.31", SECURITY_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1322 */     this.dontCheckOnDuplicateKeyUpdateInSQL = new BooleanConnectionProperty("dontCheckOnDuplicateKeyUpdateInSQL", false, Messages.getString("ConnectionProperties.dontCheckOnDuplicateKeyUpdateInSQL"), "5.1.32", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1325 */     this.readOnlyPropagatesToServer = new BooleanConnectionProperty("readOnlyPropagatesToServer", true, Messages.getString("ConnectionProperties.readOnlyPropagatesToServer"), "5.1.35", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */     
/*      */ 
/* 1328 */     this.enabledSSLCipherSuites = new StringConnectionProperty("enabledSSLCipherSuites", null, Messages.getString("ConnectionProperties.enabledSSLCipherSuites"), "5.1.35", SECURITY_CATEGORY, 11);
/*      */     
/*      */ 
/* 1331 */     this.enableEscapeProcessing = new BooleanConnectionProperty("enableEscapeProcessing", true, Messages.getString("ConnectionProperties.enableEscapeProcessing"), "5.1.37", PERFORMANCE_CATEGORY, Integer.MIN_VALUE);
/*      */   }
/*      */   
/*      */   protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/* 1335 */     initializeProperties(info);
/*      */     
/* 1337 */     int numProperties = PROPERTY_LIST.size();
/*      */     
/* 1339 */     int listSize = numProperties + slotsToReserve;
/*      */     
/* 1341 */     DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
/*      */     
/* 1343 */     for (int i = slotsToReserve; i < listSize; i++) {
/* 1344 */       Field propertyField = (Field)PROPERTY_LIST.get(i - slotsToReserve);
/*      */       try
/*      */       {
/* 1347 */         ConnectionProperty propToExpose = (ConnectionProperty)propertyField.get(this);
/*      */         
/* 1349 */         if (info != null) {
/* 1350 */           propToExpose.initializeFrom(info, getExceptionInterceptor());
/*      */         }
/*      */         
/* 1353 */         driverProperties[i] = propToExpose.getAsDriverPropertyInfo();
/*      */       } catch (IllegalAccessException iae) {
/* 1355 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.InternalPropertiesFailure"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1360 */     return driverProperties;
/*      */   }
/*      */   
/*      */   protected Properties exposeAsProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 1364 */     if (info == null) {
/* 1365 */       info = new Properties();
/*      */     }
/*      */     
/* 1368 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 1370 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 1371 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 1374 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */         
/* 1376 */         Object propValue = propToGet.getValueAsObject();
/*      */         
/* 1378 */         if (propValue != null) {
/* 1379 */           info.setProperty(propToGet.getPropertyName(), propValue.toString());
/*      */         }
/*      */       } catch (IllegalAccessException iae) {
/* 1382 */         throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/* 1386 */     return info;
/*      */   }
/*      */   
/*      */   class XmlMap
/*      */   {
/* 1390 */     protected Map<Integer, Map<String, ConnectionProperty>> ordered = new TreeMap();
/* 1391 */     protected Map<String, ConnectionProperty> alpha = new TreeMap();
/*      */     
/*      */     XmlMap() {}
/*      */   }
/*      */   
/*      */   public String exposeAsXml()
/*      */     throws SQLException
/*      */   {
/* 1400 */     StringBuilder xmlBuf = new StringBuilder();
/* 1401 */     xmlBuf.append("<ConnectionProperties>");
/*      */     
/* 1403 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 1405 */     int numCategories = PROPERTY_CATEGORIES.length;
/*      */     
/* 1407 */     Map<String, XmlMap> propertyListByCategory = new HashMap();
/*      */     
/* 1409 */     for (int i = 0; i < numCategories; i++) {
/* 1410 */       propertyListByCategory.put(PROPERTY_CATEGORIES[i], new XmlMap());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1417 */     StringConnectionProperty userProp = new StringConnectionProperty("user", null, Messages.getString("ConnectionProperties.Username"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483647);
/*      */     
/*      */ 
/* 1420 */     StringConnectionProperty passwordProp = new StringConnectionProperty("password", null, Messages.getString("ConnectionProperties.Password"), Messages.getString("ConnectionProperties.allVersions"), CONNECTION_AND_AUTH_CATEGORY, -2147483646);
/*      */     
/*      */ 
/*      */ 
/* 1424 */     XmlMap connectionSortMaps = (XmlMap)propertyListByCategory.get(CONNECTION_AND_AUTH_CATEGORY);
/* 1425 */     TreeMap<String, ConnectionProperty> userMap = new TreeMap();
/* 1426 */     userMap.put(userProp.getPropertyName(), userProp);
/*      */     
/* 1428 */     connectionSortMaps.ordered.put(Integer.valueOf(userProp.getOrder()), userMap);
/*      */     
/* 1430 */     TreeMap<String, ConnectionProperty> passwordMap = new TreeMap();
/* 1431 */     passwordMap.put(passwordProp.getPropertyName(), passwordProp);
/*      */     
/* 1433 */     connectionSortMaps.ordered.put(new Integer(passwordProp.getOrder()), passwordMap);
/*      */     try
/*      */     {
/* 1436 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 1437 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/* 1438 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/* 1439 */         XmlMap sortMaps = (XmlMap)propertyListByCategory.get(propToGet.getCategoryName());
/* 1440 */         int orderInCategory = propToGet.getOrder();
/*      */         
/* 1442 */         if (orderInCategory == Integer.MIN_VALUE) {
/* 1443 */           sortMaps.alpha.put(propToGet.getPropertyName(), propToGet);
/*      */         } else {
/* 1445 */           Integer order = Integer.valueOf(orderInCategory);
/* 1446 */           Map<String, ConnectionProperty> orderMap = (Map)sortMaps.ordered.get(order);
/*      */           
/* 1448 */           if (orderMap == null) {
/* 1449 */             orderMap = new TreeMap();
/* 1450 */             sortMaps.ordered.put(order, orderMap);
/*      */           }
/*      */           
/* 1453 */           orderMap.put(propToGet.getPropertyName(), propToGet);
/*      */         }
/*      */       }
/*      */       
/* 1457 */       for (int j = 0; j < numCategories; j++) {
/* 1458 */         XmlMap sortMaps = (XmlMap)propertyListByCategory.get(PROPERTY_CATEGORIES[j]);
/*      */         
/* 1460 */         xmlBuf.append("\n <PropertyCategory name=\"");
/* 1461 */         xmlBuf.append(PROPERTY_CATEGORIES[j]);
/* 1462 */         xmlBuf.append("\">");
/*      */         
/* 1464 */         for (Map<String, ConnectionProperty> orderedEl : sortMaps.ordered.values()) {
/* 1465 */           for (ConnectionProperty propToGet : orderedEl.values()) {
/* 1466 */             xmlBuf.append("\n  <Property name=\"");
/* 1467 */             xmlBuf.append(propToGet.getPropertyName());
/* 1468 */             xmlBuf.append("\" required=\"");
/* 1469 */             xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */             
/* 1471 */             xmlBuf.append("\" default=\"");
/*      */             
/* 1473 */             if (propToGet.getDefaultValue() != null) {
/* 1474 */               xmlBuf.append(propToGet.getDefaultValue());
/*      */             }
/*      */             
/* 1477 */             xmlBuf.append("\" sortOrder=\"");
/* 1478 */             xmlBuf.append(propToGet.getOrder());
/* 1479 */             xmlBuf.append("\" since=\"");
/* 1480 */             xmlBuf.append(propToGet.sinceVersion);
/* 1481 */             xmlBuf.append("\">\n");
/* 1482 */             xmlBuf.append("    ");
/* 1483 */             String escapedDescription = propToGet.description;
/* 1484 */             escapedDescription = escapedDescription.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
/*      */             
/* 1486 */             xmlBuf.append(escapedDescription);
/* 1487 */             xmlBuf.append("\n  </Property>");
/*      */           }
/*      */         }
/*      */         
/* 1491 */         for (ConnectionProperty propToGet : sortMaps.alpha.values()) {
/* 1492 */           xmlBuf.append("\n  <Property name=\"");
/* 1493 */           xmlBuf.append(propToGet.getPropertyName());
/* 1494 */           xmlBuf.append("\" required=\"");
/* 1495 */           xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */           
/* 1497 */           xmlBuf.append("\" default=\"");
/*      */           
/* 1499 */           if (propToGet.getDefaultValue() != null) {
/* 1500 */             xmlBuf.append(propToGet.getDefaultValue());
/*      */           }
/*      */           
/* 1503 */           xmlBuf.append("\" sortOrder=\"alpha\" since=\"");
/* 1504 */           xmlBuf.append(propToGet.sinceVersion);
/* 1505 */           xmlBuf.append("\">\n");
/* 1506 */           xmlBuf.append("    ");
/* 1507 */           xmlBuf.append(propToGet.description);
/* 1508 */           xmlBuf.append("\n  </Property>");
/*      */         }
/*      */         
/* 1511 */         xmlBuf.append("\n </PropertyCategory>");
/*      */       }
/*      */     } catch (IllegalAccessException iae) {
/* 1514 */       throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/* 1517 */     xmlBuf.append("\n</ConnectionProperties>");
/*      */     
/* 1519 */     return xmlBuf.toString();
/*      */   }
/*      */   
/*      */   public boolean getAllowLoadLocalInfile()
/*      */   {
/* 1528 */     return this.allowLoadLocalInfile.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAllowMultiQueries()
/*      */   {
/* 1537 */     return this.allowMultiQueries.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAllowNanAndInf()
/*      */   {
/* 1546 */     return this.allowNanAndInf.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAllowUrlInLocalInfile()
/*      */   {
/* 1555 */     return this.allowUrlInLocalInfile.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAlwaysSendSetIsolation()
/*      */   {
/* 1564 */     return this.alwaysSendSetIsolation.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAutoDeserialize()
/*      */   {
/* 1573 */     return this.autoDeserialize.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAutoGenerateTestcaseScript()
/*      */   {
/* 1582 */     return this.autoGenerateTestcaseScriptAsBoolean;
/*      */   }
/*      */   
/*      */   public boolean getAutoReconnectForPools()
/*      */   {
/* 1591 */     return this.autoReconnectForPoolsAsBoolean;
/*      */   }
/*      */   
/*      */   public int getBlobSendChunkSize()
/*      */   {
/* 1600 */     return this.blobSendChunkSize.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getCacheCallableStatements()
/*      */   {
/* 1609 */     return this.cacheCallableStatements.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getCachePreparedStatements()
/*      */   {
/* 1618 */     return ((Boolean)this.cachePreparedStatements.getValueAsObject()).booleanValue();
/*      */   }
/*      */   
/*      */   public boolean getCacheResultSetMetadata()
/*      */   {
/* 1627 */     return this.cacheResultSetMetaDataAsBoolean;
/*      */   }
/*      */   
/*      */   public boolean getCacheServerConfiguration()
/*      */   {
/* 1636 */     return this.cacheServerConfiguration.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getCallableStatementCacheSize()
/*      */   {
/* 1645 */     return this.callableStatementCacheSize.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getCapitalizeTypeNames()
/*      */   {
/* 1654 */     return this.capitalizeTypeNames.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public String getCharacterSetResults()
/*      */   {
/* 1663 */     return this.characterSetResults.getValueAsString();
/*      */   }
/*      */   
/*      */   public String getConnectionAttributes()
/*      */   {
/* 1667 */     return this.connectionAttributes.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setConnectionAttributes(String val)
/*      */   {
/* 1671 */     this.connectionAttributes.setValue(val);
/*      */   }
/*      */   
/*      */   public boolean getClobberStreamingResults()
/*      */   {
/* 1680 */     return this.clobberStreamingResults.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public String getClobCharacterEncoding()
/*      */   {
/* 1689 */     return this.clobCharacterEncoding.getValueAsString();
/*      */   }
/*      */   
/*      */   public String getConnectionCollation()
/*      */   {
/* 1698 */     return this.connectionCollation.getValueAsString();
/*      */   }
/*      */   
/*      */   public int getConnectTimeout()
/*      */   {
/* 1707 */     return this.connectTimeout.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getContinueBatchOnError()
/*      */   {
/* 1716 */     return this.continueBatchOnError.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getCreateDatabaseIfNotExist()
/*      */   {
/* 1725 */     return this.createDatabaseIfNotExist.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getDefaultFetchSize()
/*      */   {
/* 1734 */     return this.defaultFetchSize.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getDontTrackOpenResources()
/*      */   {
/* 1743 */     return this.dontTrackOpenResources.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getDumpQueriesOnException()
/*      */   {
/* 1752 */     return this.dumpQueriesOnException.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getDynamicCalendars()
/*      */   {
/* 1761 */     return this.dynamicCalendars.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getElideSetAutoCommits()
/*      */   {
/* 1770 */     return this.elideSetAutoCommits.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getEmptyStringsConvertToZero()
/*      */   {
/* 1779 */     return this.emptyStringsConvertToZero.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getEmulateLocators()
/*      */   {
/* 1788 */     return this.emulateLocators.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getEmulateUnsupportedPstmts()
/*      */   {
/* 1797 */     return this.emulateUnsupportedPstmts.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getEnablePacketDebug()
/*      */   {
/* 1806 */     return this.enablePacketDebug.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public String getEncoding()
/*      */   {
/* 1815 */     return this.characterEncodingAsString;
/*      */   }
/*      */   
/*      */   public boolean getExplainSlowQueries()
/*      */   {
/* 1824 */     return this.explainSlowQueries.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getFailOverReadOnly()
/*      */   {
/* 1833 */     return this.failOverReadOnly.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getGatherPerformanceMetrics()
/*      */   {
/* 1842 */     return this.gatherPerformanceMetrics.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   protected boolean getHighAvailability()
/*      */   {
/* 1846 */     return this.highAvailabilityAsBoolean;
/*      */   }
/*      */   
/*      */   public boolean getHoldResultsOpenOverStatementClose()
/*      */   {
/* 1855 */     return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getIgnoreNonTxTables()
/*      */   {
/* 1864 */     return this.ignoreNonTxTables.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getInitialTimeout()
/*      */   {
/* 1873 */     return this.initialTimeout.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getInteractiveClient()
/*      */   {
/* 1882 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getIsInteractiveClient()
/*      */   {
/* 1891 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getJdbcCompliantTruncation()
/*      */   {
/* 1900 */     return this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getLocatorFetchBufferSize()
/*      */   {
/* 1909 */     return this.locatorFetchBufferSize.getValueAsInt();
/*      */   }
/*      */   
/*      */   public String getLogger()
/*      */   {
/* 1918 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */   
/*      */   public String getLoggerClassName()
/*      */   {
/* 1927 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */   
/*      */   public boolean getLogSlowQueries()
/*      */   {
/* 1936 */     return this.logSlowQueries.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getMaintainTimeStats()
/*      */   {
/* 1945 */     return this.maintainTimeStatsAsBoolean;
/*      */   }
/*      */   
/*      */   public int getMaxQuerySizeToLog()
/*      */   {
/* 1954 */     return this.maxQuerySizeToLog.getValueAsInt();
/*      */   }
/*      */   
/*      */   public int getMaxReconnects()
/*      */   {
/* 1963 */     return this.maxReconnects.getValueAsInt();
/*      */   }
/*      */   
/*      */   public int getMaxRows()
/*      */   {
/* 1972 */     return this.maxRowsAsInt;
/*      */   }
/*      */   
/*      */   public int getMetadataCacheSize()
/*      */   {
/* 1981 */     return this.metadataCacheSize.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getNoDatetimeStringSync()
/*      */   {
/* 1990 */     return this.noDatetimeStringSync.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getNullCatalogMeansCurrent()
/*      */   {
/* 1999 */     return this.nullCatalogMeansCurrent.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getNullNamePatternMatchesAll()
/*      */   {
/* 2008 */     return this.nullNamePatternMatchesAll.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getPacketDebugBufferSize()
/*      */   {
/* 2017 */     return this.packetDebugBufferSize.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getParanoid()
/*      */   {
/* 2026 */     return this.paranoid.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getPedantic()
/*      */   {
/* 2035 */     return this.pedantic.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getPreparedStatementCacheSize()
/*      */   {
/* 2044 */     return ((Integer)this.preparedStatementCacheSize.getValueAsObject()).intValue();
/*      */   }
/*      */   
/*      */   public int getPreparedStatementCacheSqlLimit()
/*      */   {
/* 2053 */     return ((Integer)this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
/*      */   }
/*      */   
/*      */   public boolean getProfileSql()
/*      */   {
/* 2062 */     return this.profileSQLAsBoolean;
/*      */   }
/*      */   
/*      */   public boolean getProfileSQL()
/*      */   {
/* 2071 */     return this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public String getPropertiesTransform()
/*      */   {
/* 2080 */     return this.propertiesTransform.getValueAsString();
/*      */   }
/*      */   
/*      */   public int getQueriesBeforeRetryMaster()
/*      */   {
/* 2089 */     return this.queriesBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getReconnectAtTxEnd()
/*      */   {
/* 2098 */     return this.reconnectTxAtEndAsBoolean;
/*      */   }
/*      */   
/*      */   public boolean getRelaxAutoCommit()
/*      */   {
/* 2107 */     return this.relaxAutoCommit.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getReportMetricsIntervalMillis()
/*      */   {
/* 2116 */     return this.reportMetricsIntervalMillis.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getRequireSSL()
/*      */   {
/* 2125 */     return this.requireSSL.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getRetainStatementAfterResultSetClose()
/*      */   {
/* 2129 */     return this.retainStatementAfterResultSetClose.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getRollbackOnPooledClose()
/*      */   {
/* 2138 */     return this.rollbackOnPooledClose.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getRoundRobinLoadBalance()
/*      */   {
/* 2147 */     return this.roundRobinLoadBalance.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getRunningCTS13()
/*      */   {
/* 2156 */     return this.runningCTS13.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public int getSecondsBeforeRetryMaster()
/*      */   {
/* 2165 */     return this.secondsBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */   
/*      */   public String getServerTimezone()
/*      */   {
/* 2174 */     return this.serverTimezone.getValueAsString();
/*      */   }
/*      */   
/*      */   public String getSessionVariables()
/*      */   {
/* 2183 */     return this.sessionVariables.getValueAsString();
/*      */   }
/*      */   
/*      */   public int getSlowQueryThresholdMillis()
/*      */   {
/* 2192 */     return this.slowQueryThresholdMillis.getValueAsInt();
/*      */   }
/*      */   
/*      */   public String getSocketFactoryClassName()
/*      */   {
/* 2201 */     return this.socketFactoryClassName.getValueAsString();
/*      */   }
/*      */   
/*      */   public int getSocketTimeout()
/*      */   {
/* 2210 */     return this.socketTimeout.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getStrictFloatingPoint()
/*      */   {
/* 2219 */     return this.strictFloatingPoint.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getStrictUpdates()
/*      */   {
/* 2228 */     return this.strictUpdates.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getTinyInt1isBit()
/*      */   {
/* 2237 */     return this.tinyInt1isBit.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getTraceProtocol()
/*      */   {
/* 2246 */     return this.traceProtocol.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getTransformedBitIsBoolean()
/*      */   {
/* 2255 */     return this.transformedBitIsBoolean.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseCompression()
/*      */   {
/* 2264 */     return this.useCompression.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseFastIntParsing()
/*      */   {
/* 2273 */     return this.useFastIntParsing.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseHostsInPrivileges()
/*      */   {
/* 2282 */     return this.useHostsInPrivileges.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseInformationSchema()
/*      */   {
/* 2291 */     return this.useInformationSchema.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseLocalSessionState()
/*      */   {
/* 2300 */     return this.useLocalSessionState.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseOldUTF8Behavior()
/*      */   {
/* 2309 */     return this.useOldUTF8BehaviorAsBoolean;
/*      */   }
/*      */   
/*      */   public boolean getUseOnlyServerErrorMessages()
/*      */   {
/* 2318 */     return this.useOnlyServerErrorMessages.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseReadAheadInput()
/*      */   {
/* 2327 */     return this.useReadAheadInput.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseServerPreparedStmts()
/*      */   {
/* 2336 */     return this.detectServerPreparedStmts.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseSqlStateCodes()
/*      */   {
/* 2345 */     return this.useSqlStateCodes.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseSSL()
/*      */   {
/* 2354 */     return this.useSSL.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean isUseSSLExplicit()
/*      */   {
/* 2363 */     return this.useSSL.wasExplicitlySet;
/*      */   }
/*      */   
/*      */   public boolean getUseStreamLengthsInPrepStmts()
/*      */   {
/* 2372 */     return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseTimezone()
/*      */   {
/* 2381 */     return this.useTimezone.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseUltraDevWorkAround()
/*      */   {
/* 2390 */     return this.useUltraDevWorkAround.getValueAsBoolean();
/*      */   }
/*      */   private BooleanConnectionProperty detectServerPreparedStmts;
/*      */   private BooleanConnectionProperty dontTrackOpenResources;
/*      */   private BooleanConnectionProperty dumpQueriesOnException;
/*      */   private BooleanConnectionProperty dynamicCalendars;
/*      */   private BooleanConnectionProperty elideSetAutoCommits;
/*      */   private BooleanConnectionProperty emptyStringsConvertToZero;
/*      */   private BooleanConnectionProperty emulateLocators;
/*      */   private BooleanConnectionProperty emulateUnsupportedPstmts;
/*      */   private BooleanConnectionProperty enablePacketDebug;
/*      */   private BooleanConnectionProperty enableQueryTimeouts;
/*      */   
/*      */   public boolean getUseUnbufferedInput()
/*      */   {
/* 2399 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   private BooleanConnectionProperty explainSlowQueries;
/*      */   private StringConnectionProperty exceptionInterceptors;
/*      */   private BooleanConnectionProperty failOverReadOnly;
/*      */   private BooleanConnectionProperty gatherPerformanceMetrics;
/*      */   private BooleanConnectionProperty generateSimpleParameterMetadata;
/*      */   private boolean highAvailabilityAsBoolean;
/*      */   private BooleanConnectionProperty holdResultsOpenOverStatementClose;
/*      */   private BooleanConnectionProperty includeInnodbStatusInDeadlockExceptions;
/*      */   private BooleanConnectionProperty includeThreadDumpInDeadlockExceptions;
/*      */   private BooleanConnectionProperty includeThreadNamesAsStatementComment;
/*      */   
/*      */   public boolean getUseUnicode()
/*      */   {
/* 2408 */     return this.useUnicodeAsBoolean;
/*      */   }
/*      */   
/*      */   private BooleanConnectionProperty ignoreNonTxTables;
/*      */   private IntegerConnectionProperty initialTimeout;
/*      */   private BooleanConnectionProperty isInteractiveClient;
/*      */   private BooleanConnectionProperty jdbcCompliantTruncation;
/*      */   private boolean jdbcCompliantTruncationForReads;
/*      */   protected MemorySizeConnectionProperty largeRowSizeThreshold;
/*      */   private StringConnectionProperty loadBalanceStrategy;
/*      */   private IntegerConnectionProperty loadBalanceBlacklistTimeout;
/*      */   private IntegerConnectionProperty loadBalancePingTimeout;
/*      */   
/*      */   public boolean getUseUsageAdvisor()
/*      */   {
/* 2417 */     return this.useUsageAdvisorAsBoolean;
/*      */   }
/*      */   
/*      */   private BooleanConnectionProperty loadBalanceValidateConnectionOnSwapServer;
/*      */   private StringConnectionProperty loadBalanceConnectionGroup;
/*      */   private StringConnectionProperty loadBalanceExceptionChecker;
/*      */   private StringConnectionProperty loadBalanceSQLStateFailover;
/*      */   private StringConnectionProperty loadBalanceSQLExceptionSubclassFailover;
/*      */   private BooleanConnectionProperty loadBalanceEnableJMX;
/*      */   private IntegerConnectionProperty loadBalanceHostRemovalGracePeriod;
/*      */   private StringConnectionProperty loadBalanceAutoCommitStatementRegex;
/*      */   private IntegerConnectionProperty loadBalanceAutoCommitStatementThreshold;
/*      */   
/*      */   public boolean getYearIsDateType()
/*      */   {
/* 2426 */     return this.yearIsDateType.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   private StringConnectionProperty localSocketAddress;
/*      */   private MemorySizeConnectionProperty locatorFetchBufferSize;
/*      */   private StringConnectionProperty loggerClassName;
/*      */   private BooleanConnectionProperty logSlowQueries;
/*      */   private BooleanConnectionProperty logXaCommands;
/*      */   private BooleanConnectionProperty maintainTimeStats;
/*      */   private boolean maintainTimeStatsAsBoolean;
/*      */   
/*      */   public String getZeroDateTimeBehavior()
/*      */   {
/* 2435 */     return this.zeroDateTimeBehavior.getValueAsString();
/*      */   }
/*      */   
/*      */   private IntegerConnectionProperty maxQuerySizeToLog;
/*      */   private IntegerConnectionProperty maxReconnects;
/*      */   private IntegerConnectionProperty retriesAllDown;
/*      */   private IntegerConnectionProperty maxRows;
/*      */   private int maxRowsAsInt;
/*      */   private IntegerConnectionProperty metadataCacheSize;
/*      */   private IntegerConnectionProperty netTimeoutForStreamingResults;
/*      */   private BooleanConnectionProperty noAccessToProcedureBodies;
/*      */   private BooleanConnectionProperty noDatetimeStringSync;
/*      */   private BooleanConnectionProperty noTimezoneConversionForTimeType;
/*      */   private BooleanConnectionProperty noTimezoneConversionForDateType;
/*      */   private BooleanConnectionProperty cacheDefaultTimezone;
/*      */   private BooleanConnectionProperty nullCatalogMeansCurrent;
/*      */   private BooleanConnectionProperty nullNamePatternMatchesAll;
/*      */   private IntegerConnectionProperty packetDebugBufferSize;
/*      */   private BooleanConnectionProperty padCharsWithSpace;
/*      */   private BooleanConnectionProperty paranoid;
/*      */   
/*      */   protected void initializeFromRef(Reference ref)
/*      */     throws SQLException
/*      */   {
/* 2448 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 2450 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 2451 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 2454 */         ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */         
/* 2456 */         if (ref != null) {
/* 2457 */           propToSet.initializeFrom(ref, getExceptionInterceptor());
/*      */         }
/*      */       } catch (IllegalAccessException iae) {
/* 2460 */         throw SQLError.createSQLException("Internal properties failure", "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/* 2464 */     postInitialization();
/*      */   }
/*      */   
/*      */   protected void initializeProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 2475 */     if (info != null)
/*      */     {
/* 2477 */       String profileSqlLc = info.getProperty("profileSql");
/*      */       
/* 2479 */       if (profileSqlLc != null) {
/* 2480 */         info.put("profileSQL", profileSqlLc);
/*      */       }
/*      */       
/* 2483 */       Properties infoCopy = (Properties)info.clone();
/*      */       
/* 2485 */       infoCopy.remove("HOST");
/* 2486 */       infoCopy.remove("user");
/* 2487 */       infoCopy.remove("password");
/* 2488 */       infoCopy.remove("DBNAME");
/* 2489 */       infoCopy.remove("PORT");
/* 2490 */       infoCopy.remove("profileSql");
/*      */       
/* 2492 */       int numPropertiesToSet = PROPERTY_LIST.size();
/*      */       
/* 2494 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 2495 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */         try
/*      */         {
/* 2498 */           ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */           
/* 2500 */           propToSet.initializeFrom(infoCopy, getExceptionInterceptor());
/*      */         } catch (IllegalAccessException iae) {
/* 2502 */           throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unableToInitDriverProperties") + iae.toString(), "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2507 */       postInitialization();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void postInitialization()
/*      */     throws SQLException
/*      */   {
/* 2514 */     if (this.profileSql.getValueAsObject() != null) {
/* 2515 */       this.profileSQL.initializeFrom(this.profileSql.getValueAsObject().toString(), getExceptionInterceptor());
/*      */     }
/*      */     
/* 2518 */     this.reconnectTxAtEndAsBoolean = ((Boolean)this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
/*      */     
/*      */ 
/* 2521 */     if (getMaxRows() == 0)
/*      */     {
/* 2523 */       this.maxRows.setValueAsObject(Integer.valueOf(-1));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2529 */     String testEncoding = (String)this.characterEncoding.getValueAsObject();
/*      */     
/* 2531 */     if (testEncoding != null) {
/*      */       try
/*      */       {
/* 2534 */         String testString = "abc";
/* 2535 */         StringUtils.getBytes(testString, testEncoding);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 2537 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unsupportedCharacterEncoding", new Object[] { testEncoding }), "0S100", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2544 */     if (((Boolean)this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
/*      */       try {
/* 2546 */         Class.forName("java.util.LinkedHashMap");
/*      */       } catch (ClassNotFoundException cnfe) {
/* 2548 */         this.cacheResultSetMetadata.setValue(false);
/*      */       }
/*      */     }
/*      */     
/* 2552 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/* 2553 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/* 2554 */     this.characterEncodingAsString = ((String)this.characterEncoding.getValueAsObject());
/* 2555 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/* 2556 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/* 2557 */     this.maxRowsAsInt = ((Integer)this.maxRows.getValueAsObject()).intValue();
/* 2558 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/* 2559 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/* 2560 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/* 2561 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/* 2562 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/* 2563 */     this.jdbcCompliantTruncationForReads = getJdbcCompliantTruncation();
/*      */     
/* 2565 */     if (getUseCursorFetch()) {
/* 2567 */       setDetectServerPreparedStmts(true);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAllowLoadLocalInfile(boolean property)
/*      */   {
/* 2577 */     this.allowLoadLocalInfile.setValue(property);
/*      */   }
/*      */   
/*      */   public void setAllowMultiQueries(boolean property)
/*      */   {
/* 2586 */     this.allowMultiQueries.setValue(property);
/*      */   }
/*      */   
/*      */   public void setAllowNanAndInf(boolean flag)
/*      */   {
/* 2595 */     this.allowNanAndInf.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/* 2604 */     this.allowUrlInLocalInfile.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/* 2613 */     this.alwaysSendSetIsolation.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setAutoDeserialize(boolean flag)
/*      */   {
/* 2622 */     this.autoDeserialize.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setAutoGenerateTestcaseScript(boolean flag)
/*      */   {
/* 2631 */     this.autoGenerateTestcaseScript.setValue(flag);
/* 2632 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAutoReconnect(boolean flag)
/*      */   {
/* 2641 */     this.autoReconnect.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/* 2650 */     this.autoReconnectForPools.setValue(property);
/* 2651 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAutoReconnectForPools(boolean flag)
/*      */   {
/* 2660 */     this.autoReconnectForPools.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setBlobSendChunkSize(String value)
/*      */     throws SQLException
/*      */   {
/* 2669 */     this.blobSendChunkSize.setValue(value, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setCacheCallableStatements(boolean flag)
/*      */   {
/* 2678 */     this.cacheCallableStatements.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setCachePreparedStatements(boolean flag)
/*      */   {
/* 2687 */     this.cachePreparedStatements.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setCacheResultSetMetadata(boolean property)
/*      */   {
/* 2696 */     this.cacheResultSetMetadata.setValue(property);
/* 2697 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setCacheServerConfiguration(boolean flag)
/*      */   {
/* 2706 */     this.cacheServerConfiguration.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setCallableStatementCacheSize(int size)
/*      */     throws SQLException
/*      */   {
/* 2715 */     this.callableStatementCacheSize.setValue(size, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/* 2724 */     this.capitalizeTypeNames.setValue(property);
/*      */   }
/*      */   
/*      */   public void setCapitalizeTypeNames(boolean flag)
/*      */   {
/* 2733 */     this.capitalizeTypeNames.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setCharacterEncoding(String encoding)
/*      */   {
/* 2742 */     this.characterEncoding.setValue(encoding);
/*      */   }
/*      */   
/*      */   public void setCharacterSetResults(String characterSet)
/*      */   {
/* 2751 */     this.characterSetResults.setValue(characterSet);
/*      */   }
/*      */   
/*      */   public void setClobberStreamingResults(boolean flag)
/*      */   {
/* 2760 */     this.clobberStreamingResults.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setClobCharacterEncoding(String encoding)
/*      */   {
/* 2769 */     this.clobCharacterEncoding.setValue(encoding);
/*      */   }
/*      */   
/*      */   public void setConnectionCollation(String collation)
/*      */   {
/* 2778 */     this.connectionCollation.setValue(collation);
/*      */   }
/*      */   
/*      */   public void setConnectTimeout(int timeoutMs)
/*      */     throws SQLException
/*      */   {
/* 2787 */     this.connectTimeout.setValue(timeoutMs, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setContinueBatchOnError(boolean property)
/*      */   {
/* 2796 */     this.continueBatchOnError.setValue(property);
/*      */   }
/*      */   
/*      */   public void setCreateDatabaseIfNotExist(boolean flag)
/*      */   {
/* 2805 */     this.createDatabaseIfNotExist.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setDefaultFetchSize(int n)
/*      */     throws SQLException
/*      */   {
/* 2814 */     this.defaultFetchSize.setValue(n, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setDetectServerPreparedStmts(boolean property)
/*      */   {
/* 2823 */     this.detectServerPreparedStmts.setValue(property);
/*      */   }
/*      */   
/*      */   public void setDontTrackOpenResources(boolean flag)
/*      */   {
/* 2832 */     this.dontTrackOpenResources.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setDumpQueriesOnException(boolean flag)
/*      */   {
/* 2841 */     this.dumpQueriesOnException.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setDynamicCalendars(boolean flag)
/*      */   {
/* 2850 */     this.dynamicCalendars.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setElideSetAutoCommits(boolean flag)
/*      */   {
/* 2859 */     this.elideSetAutoCommits.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setEmptyStringsConvertToZero(boolean flag)
/*      */   {
/* 2868 */     this.emptyStringsConvertToZero.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setEmulateLocators(boolean property)
/*      */   {
/* 2877 */     this.emulateLocators.setValue(property);
/*      */   }
/*      */   
/*      */   public void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/* 2886 */     this.emulateUnsupportedPstmts.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setEnablePacketDebug(boolean flag)
/*      */   {
/* 2895 */     this.enablePacketDebug.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setEncoding(String property)
/*      */   {
/* 2904 */     this.characterEncoding.setValue(property);
/* 2905 */     this.characterEncodingAsString = this.characterEncoding.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setExplainSlowQueries(boolean flag)
/*      */   {
/* 2914 */     this.explainSlowQueries.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setFailOverReadOnly(boolean flag)
/*      */   {
/* 2923 */     this.failOverReadOnly.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/* 2932 */     this.gatherPerformanceMetrics.setValue(flag);
/*      */   }
/*      */   
/*      */   protected void setHighAvailability(boolean property)
/*      */   {
/* 2936 */     this.autoReconnect.setValue(property);
/* 2937 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/* 2946 */     this.holdResultsOpenOverStatementClose.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setIgnoreNonTxTables(boolean property)
/*      */   {
/* 2955 */     this.ignoreNonTxTables.setValue(property);
/*      */   }
/*      */   
/*      */   public void setInitialTimeout(int property)
/*      */     throws SQLException
/*      */   {
/* 2964 */     this.initialTimeout.setValue(property, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setIsInteractiveClient(boolean property)
/*      */   {
/* 2973 */     this.isInteractiveClient.setValue(property);
/*      */   }
/*      */   
/*      */   public void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/* 2982 */     this.jdbcCompliantTruncation.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setLocatorFetchBufferSize(String value)
/*      */     throws SQLException
/*      */   {
/* 2991 */     this.locatorFetchBufferSize.setValue(value, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setLogger(String property)
/*      */   {
/* 3000 */     this.loggerClassName.setValueAsObject(property);
/*      */   }
/*      */   
/*      */   public void setLoggerClassName(String className)
/*      */   {
/* 3009 */     this.loggerClassName.setValue(className);
/*      */   }
/*      */   
/*      */   public void setLogSlowQueries(boolean flag)
/*      */   {
/* 3018 */     this.logSlowQueries.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setMaintainTimeStats(boolean flag)
/*      */   {
/* 3027 */     this.maintainTimeStats.setValue(flag);
/* 3028 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes)
/*      */     throws SQLException
/*      */   {
/* 3037 */     this.maxQuerySizeToLog.setValue(sizeInBytes, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setMaxReconnects(int property)
/*      */     throws SQLException
/*      */   {
/* 3046 */     this.maxReconnects.setValue(property, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setMaxRows(int property)
/*      */     throws SQLException
/*      */   {
/* 3055 */     this.maxRows.setValue(property, getExceptionInterceptor());
/* 3056 */     this.maxRowsAsInt = this.maxRows.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setMetadataCacheSize(int value)
/*      */     throws SQLException
/*      */   {
/* 3065 */     this.metadataCacheSize.setValue(value, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setNoDatetimeStringSync(boolean flag)
/*      */   {
/* 3074 */     this.noDatetimeStringSync.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/* 3083 */     this.nullCatalogMeansCurrent.setValue(value);
/*      */   }
/*      */   
/*      */   public void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/* 3092 */     this.nullNamePatternMatchesAll.setValue(value);
/*      */   }
/*      */   
/*      */   public void setPacketDebugBufferSize(int size)
/*      */     throws SQLException
/*      */   {
/* 3101 */     this.packetDebugBufferSize.setValue(size, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setParanoid(boolean property)
/*      */   {
/* 3110 */     this.paranoid.setValue(property);
/*      */   }
/*      */   
/*      */   public void setPedantic(boolean property)
/*      */   {
/* 3119 */     this.pedantic.setValue(property);
/*      */   }
/*      */   
/*      */   public void setPreparedStatementCacheSize(int cacheSize)
/*      */     throws SQLException
/*      */   {
/* 3128 */     this.preparedStatementCacheSize.setValue(cacheSize, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */     throws SQLException
/*      */   {
/* 3137 */     this.preparedStatementCacheSqlLimit.setValue(cacheSqlLimit, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setProfileSql(boolean property)
/*      */   {
/* 3146 */     this.profileSQL.setValue(property);
/* 3147 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setProfileSQL(boolean flag)
/*      */   {
/* 3156 */     this.profileSQL.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setPropertiesTransform(String value)
/*      */   {
/* 3165 */     this.propertiesTransform.setValue(value);
/*      */   }
/*      */   
/*      */   public void setQueriesBeforeRetryMaster(int property)
/*      */     throws SQLException
/*      */   {
/* 3174 */     this.queriesBeforeRetryMaster.setValue(property, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setReconnectAtTxEnd(boolean property)
/*      */   {
/* 3183 */     this.reconnectAtTxEnd.setValue(property);
/* 3184 */     this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setRelaxAutoCommit(boolean property)
/*      */   {
/* 3193 */     this.relaxAutoCommit.setValue(property);
/*      */   }
/*      */   
/*      */   public void setReportMetricsIntervalMillis(int millis)
/*      */     throws SQLException
/*      */   {
/* 3202 */     this.reportMetricsIntervalMillis.setValue(millis, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setRequireSSL(boolean property)
/*      */   {
/* 3211 */     this.requireSSL.setValue(property);
/*      */   }
/*      */   
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/* 3220 */     this.retainStatementAfterResultSetClose.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setRollbackOnPooledClose(boolean flag)
/*      */   {
/* 3229 */     this.rollbackOnPooledClose.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/* 3238 */     this.roundRobinLoadBalance.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setRunningCTS13(boolean flag)
/*      */   {
/* 3247 */     this.runningCTS13.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setSecondsBeforeRetryMaster(int property)
/*      */     throws SQLException
/*      */   {
/* 3256 */     this.secondsBeforeRetryMaster.setValue(property, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setServerTimezone(String property)
/*      */   {
/* 3265 */     this.serverTimezone.setValue(property);
/*      */   }
/*      */   
/*      */   public void setSessionVariables(String variables)
/*      */   {
/* 3274 */     this.sessionVariables.setValue(variables);
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdMillis(int millis)
/*      */     throws SQLException
/*      */   {
/* 3283 */     this.slowQueryThresholdMillis.setValue(millis, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setSocketFactoryClassName(String property)
/*      */   {
/* 3292 */     this.socketFactoryClassName.setValue(property);
/*      */   }
/*      */   
/*      */   public void setSocketTimeout(int property)
/*      */     throws SQLException
/*      */   {
/* 3301 */     this.socketTimeout.setValue(property, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setStrictFloatingPoint(boolean property)
/*      */   {
/* 3310 */     this.strictFloatingPoint.setValue(property);
/*      */   }
/*      */   
/*      */   public void setStrictUpdates(boolean property)
/*      */   {
/* 3319 */     this.strictUpdates.setValue(property);
/*      */   }
/*      */   
/*      */   public void setTinyInt1isBit(boolean flag)
/*      */   {
/* 3328 */     this.tinyInt1isBit.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setTraceProtocol(boolean flag)
/*      */   {
/* 3337 */     this.traceProtocol.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/* 3346 */     this.transformedBitIsBoolean.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseCompression(boolean property)
/*      */   {
/* 3355 */     this.useCompression.setValue(property);
/*      */   }
/*      */   
/*      */   public void setUseFastIntParsing(boolean flag)
/*      */   {
/* 3364 */     this.useFastIntParsing.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseHostsInPrivileges(boolean property)
/*      */   {
/* 3373 */     this.useHostsInPrivileges.setValue(property);
/*      */   }
/*      */   
/*      */   public void setUseInformationSchema(boolean flag)
/*      */   {
/* 3382 */     this.useInformationSchema.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseLocalSessionState(boolean flag)
/*      */   {
/* 3391 */     this.useLocalSessionState.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/* 3400 */     this.useOldUTF8Behavior.setValue(flag);
/* 3401 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/* 3410 */     this.useOnlyServerErrorMessages.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseReadAheadInput(boolean flag)
/*      */   {
/* 3419 */     this.useReadAheadInput.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseServerPreparedStmts(boolean flag)
/*      */   {
/* 3428 */     this.detectServerPreparedStmts.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseSqlStateCodes(boolean flag)
/*      */   {
/* 3437 */     this.useSqlStateCodes.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseSSL(boolean property)
/*      */   {
/* 3446 */     this.useSSL.setValue(property);
/*      */   }
/*      */   
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/* 3455 */     this.useStreamLengthsInPrepStmts.setValue(property);
/*      */   }
/*      */   
/*      */   public void setUseTimezone(boolean property)
/*      */   {
/* 3464 */     this.useTimezone.setValue(property);
/*      */   }
/*      */   
/*      */   public void setUseUltraDevWorkAround(boolean property)
/*      */   {
/* 3473 */     this.useUltraDevWorkAround.setValue(property);
/*      */   }
/*      */   
/*      */   public void setUseUnbufferedInput(boolean flag)
/*      */   {
/* 3482 */     this.useUnbufferedInput.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setUseUnicode(boolean flag)
/*      */   {
/* 3491 */     this.useUnicode.setValue(flag);
/* 3492 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/* 3501 */     this.useUsageAdvisor.setValue(useUsageAdvisorFlag);
/* 3502 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setYearIsDateType(boolean flag)
/*      */   {
/* 3511 */     this.yearIsDateType.setValue(flag);
/*      */   }
/*      */   
/*      */   public void setZeroDateTimeBehavior(String behavior)
/*      */   {
/* 3520 */     this.zeroDateTimeBehavior.setValue(behavior);
/*      */   }
/*      */   
/*      */   protected void storeToRef(Reference ref)
/*      */     throws SQLException
/*      */   {
/* 3524 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */     
/* 3526 */     for (int i = 0; i < numPropertiesToSet; i++)
/*      */     {
/* 3527 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 3530 */         ConnectionProperty propToStore = (ConnectionProperty)propertyField.get(this);
/*      */         
/* 3532 */         if (ref != null) {
/* 3533 */           propToStore.storeTo(ref);
/*      */         }
/*      */       }
/*      */       catch (IllegalAccessException iae)
/*      */       {
/* 3536 */         throw SQLError.createSQLException(Messages.getString("ConnectionProperties.errorNotExpected"), getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean useUnbufferedInput()
/*      */   {
/* 3547 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getUseCursorFetch()
/*      */   {
/* 3556 */     return this.useCursorFetch.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseCursorFetch(boolean flag)
/*      */   {
/* 3565 */     this.useCursorFetch.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getOverrideSupportsIntegrityEnhancementFacility()
/*      */   {
/* 3574 */     return this.overrideSupportsIntegrityEnhancementFacility.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/* 3583 */     this.overrideSupportsIntegrityEnhancementFacility.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getNoTimezoneConversionForTimeType()
/*      */   {
/* 3592 */     return this.noTimezoneConversionForTimeType.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/* 3601 */     this.noTimezoneConversionForTimeType.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getNoTimezoneConversionForDateType()
/*      */   {
/* 3610 */     return this.noTimezoneConversionForDateType.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setNoTimezoneConversionForDateType(boolean flag)
/*      */   {
/* 3619 */     this.noTimezoneConversionForDateType.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getCacheDefaultTimezone()
/*      */   {
/* 3628 */     return this.cacheDefaultTimezone.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setCacheDefaultTimezone(boolean flag)
/*      */   {
/* 3637 */     this.cacheDefaultTimezone.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseJDBCCompliantTimezoneShift()
/*      */   {
/* 3646 */     return this.useJDBCCompliantTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/* 3655 */     this.useJDBCCompliantTimezoneShift.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getAutoClosePStmtStreams()
/*      */   {
/* 3664 */     return this.autoClosePStmtStreams.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAutoClosePStmtStreams(boolean flag)
/*      */   {
/* 3673 */     this.autoClosePStmtStreams.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getProcessEscapeCodesForPrepStmts()
/*      */   {
/* 3682 */     return this.processEscapeCodesForPrepStmts.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/* 3691 */     this.processEscapeCodesForPrepStmts.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseGmtMillisForDatetimes()
/*      */   {
/* 3700 */     return this.useGmtMillisForDatetimes.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/* 3709 */     this.useGmtMillisForDatetimes.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getDumpMetadataOnColumnNotFound()
/*      */   {
/* 3718 */     return this.dumpMetadataOnColumnNotFound.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag)
/*      */   {
/* 3727 */     this.dumpMetadataOnColumnNotFound.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getResourceId()
/*      */   {
/* 3736 */     return this.resourceId.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setResourceId(String resourceId)
/*      */   {
/* 3745 */     this.resourceId.setValue(resourceId);
/*      */   }
/*      */   
/*      */   public boolean getRewriteBatchedStatements()
/*      */   {
/* 3754 */     return this.rewriteBatchedStatements.getValueAsBoolean();
/*      */   }
/*      */   private BooleanConnectionProperty pedantic;
/*      */   private BooleanConnectionProperty pinGlobalTxToPhysicalConnection;
/*      */   private BooleanConnectionProperty populateInsertRowWithDefaultValues;
/*      */   private IntegerConnectionProperty preparedStatementCacheSize;
/*      */   private IntegerConnectionProperty preparedStatementCacheSqlLimit;
/*      */   private StringConnectionProperty parseInfoCacheFactory;
/*      */   private BooleanConnectionProperty processEscapeCodesForPrepStmts;
/*      */   private StringConnectionProperty profilerEventHandler;
/*      */   private StringConnectionProperty profileSql;
/*      */   private BooleanConnectionProperty profileSQL;
/*      */   private boolean profileSQLAsBoolean;
/*      */   private StringConnectionProperty propertiesTransform;
/*      */   
/*      */   public void setRewriteBatchedStatements(boolean flag)
/*      */   {
/* 3763 */     this.rewriteBatchedStatements.setValue(flag);
/*      */   }
/*      */   
/*      */   private IntegerConnectionProperty queriesBeforeRetryMaster;
/*      */   private BooleanConnectionProperty queryTimeoutKillsConnection;
/*      */   private BooleanConnectionProperty reconnectAtTxEnd;
/*      */   private boolean reconnectTxAtEndAsBoolean;
/*      */   private BooleanConnectionProperty relaxAutoCommit;
/*      */   private IntegerConnectionProperty reportMetricsIntervalMillis;
/*      */   private BooleanConnectionProperty requireSSL;
/*      */   private StringConnectionProperty resourceId;
/*      */   private IntegerConnectionProperty resultSetSizeThreshold;
/*      */   private BooleanConnectionProperty retainStatementAfterResultSetClose;
/*      */   private BooleanConnectionProperty rewriteBatchedStatements;
/*      */   
/*      */   public boolean getJdbcCompliantTruncationForReads()
/*      */   {
/* 3772 */     return this.jdbcCompliantTruncationForReads;
/*      */   }
/*      */   
/*      */   private BooleanConnectionProperty rollbackOnPooledClose;
/*      */   private BooleanConnectionProperty roundRobinLoadBalance;
/*      */   private BooleanConnectionProperty runningCTS13;
/*      */   private IntegerConnectionProperty secondsBeforeRetryMaster;
/*      */   private IntegerConnectionProperty selfDestructOnPingSecondsLifetime;
/*      */   private IntegerConnectionProperty selfDestructOnPingMaxOperations;
/*      */   private BooleanConnectionProperty replicationEnableJMX;
/*      */   private StringConnectionProperty serverTimezone;
/*      */   private StringConnectionProperty sessionVariables;
/*      */   private IntegerConnectionProperty slowQueryThresholdMillis;
/*      */   private LongConnectionProperty slowQueryThresholdNanos;
/*      */   private StringConnectionProperty socketFactoryClassName;
/*      */   private StringConnectionProperty socksProxyHost;
/*      */   private IntegerConnectionProperty socksProxyPort;
/*      */   
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 3781 */     this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads;
/*      */   }
/*      */   
/*      */   private IntegerConnectionProperty socketTimeout;
/*      */   private StringConnectionProperty statementInterceptors;
/*      */   private BooleanConnectionProperty strictFloatingPoint;
/*      */   private BooleanConnectionProperty strictUpdates;
/*      */   private BooleanConnectionProperty overrideSupportsIntegrityEnhancementFacility;
/*      */   private BooleanConnectionProperty tcpNoDelay;
/*      */   private BooleanConnectionProperty tcpKeepAlive;
/*      */   
/*      */   public boolean getUseJvmCharsetConverters()
/*      */   {
/* 3790 */     return this.useJvmCharsetConverters.getValueAsBoolean(); }
/*      */   
/*      */   private IntegerConnectionProperty tcpRcvBuf;
/*      */   private IntegerConnectionProperty tcpSndBuf;
/*      */   private IntegerConnectionProperty tcpTrafficClass;
/*      */   private BooleanConnectionProperty tinyInt1isBit;
/*      */   protected BooleanConnectionProperty traceProtocol;
/*      */   private BooleanConnectionProperty treatUtilDateAsTimestamp;
/*      */   private BooleanConnectionProperty transformedBitIsBoolean; private BooleanConnectionProperty useBlobToStoreUTF8OutsideBMP; private StringConnectionProperty utf8OutsideBmpExcludedColumnNamePattern; private StringConnectionProperty utf8OutsideBmpIncludedColumnNamePattern; private BooleanConnectionProperty useCompression;
/* 3799 */   public void setUseJvmCharsetConverters(boolean flag) { this.useJvmCharsetConverters.setValue(flag); }
/*      */   
/*      */   private BooleanConnectionProperty useColumnNamesInFindColumn;
/*      */   private StringConnectionProperty useConfigs;
/*      */   private BooleanConnectionProperty useCursorFetch;
/*      */   private BooleanConnectionProperty useDynamicCharsetInfo;
/*      */   private BooleanConnectionProperty useDirectRowUnpack;
/*      */   private BooleanConnectionProperty useFastIntParsing;
/*      */   private BooleanConnectionProperty useFastDateParsing; private BooleanConnectionProperty useHostsInPrivileges; private BooleanConnectionProperty useInformationSchema;
/* 3808 */   public boolean getPinGlobalTxToPhysicalConnection() { return this.pinGlobalTxToPhysicalConnection.getValueAsBoolean(); }
/*      */   
/*      */   private BooleanConnectionProperty useJDBCCompliantTimezoneShift;
/*      */   private BooleanConnectionProperty useLocalSessionState;
/*      */   private BooleanConnectionProperty useLocalTransactionState;
/*      */   private BooleanConnectionProperty useLegacyDatetimeCode;
/*      */   private BooleanConnectionProperty sendFractionalSeconds;
/*      */   private BooleanConnectionProperty useNanosForElapsedTime;
/*      */   private BooleanConnectionProperty useOldAliasMetadataBehavior; private BooleanConnectionProperty useOldUTF8Behavior;
/* 3817 */   public void setPinGlobalTxToPhysicalConnection(boolean flag) { this.pinGlobalTxToPhysicalConnection.setValue(flag); }
/*      */   
/*      */   private boolean useOldUTF8BehaviorAsBoolean;
/*      */   private BooleanConnectionProperty useOnlyServerErrorMessages;
/*      */   private BooleanConnectionProperty useReadAheadInput;
/*      */   private BooleanConnectionProperty useSqlStateCodes;
/*      */   private BooleanConnectionProperty useSSL;
/*      */   private BooleanConnectionProperty useSSPSCompatibleTimezoneShift;
/*      */   private BooleanConnectionProperty useStreamLengthsInPrepStmts;
/*      */   private BooleanConnectionProperty useTimezone;
/*      */   private BooleanConnectionProperty useUltraDevWorkAround;
/*      */   private BooleanConnectionProperty useUnbufferedInput;
/*      */   private BooleanConnectionProperty useUnicode;
/*      */   private boolean useUnicodeAsBoolean; private BooleanConnectionProperty useUsageAdvisor;
/* 3831 */   public void setGatherPerfMetrics(boolean flag) { setGatherPerformanceMetrics(flag); }
/*      */   
/*      */   private boolean useUsageAdvisorAsBoolean;
/*      */   private BooleanConnectionProperty yearIsDateType;
/*      */   private StringConnectionProperty zeroDateTimeBehavior;
/*      */   private BooleanConnectionProperty useJvmCharsetConverters;
/*      */   private BooleanConnectionProperty useGmtMillisForDatetimes;
/*      */   private BooleanConnectionProperty dumpMetadataOnColumnNotFound;
/*      */   private StringConnectionProperty clientCertificateKeyStoreUrl; private StringConnectionProperty trustCertificateKeyStoreUrl; private StringConnectionProperty clientCertificateKeyStoreType; private StringConnectionProperty clientCertificateKeyStorePassword; private StringConnectionProperty trustCertificateKeyStoreType; private StringConnectionProperty trustCertificateKeyStorePassword; private BooleanConnectionProperty verifyServerCertificate; private BooleanConnectionProperty useAffectedRows;
/* 3840 */   public boolean getGatherPerfMetrics() { return getGatherPerformanceMetrics(); }
/*      */   
/*      */   private StringConnectionProperty passwordCharacterEncoding;
/*      */   private IntegerConnectionProperty maxAllowedPacket;
/*      */   private StringConnectionProperty authenticationPlugins;
/*      */   private StringConnectionProperty disabledAuthenticationPlugins;
/*      */   private StringConnectionProperty defaultAuthenticationPlugin;
/*      */   private BooleanConnectionProperty disconnectOnExpiredPasswords;
/*      */   private BooleanConnectionProperty getProceduresReturnsFunctions;
/* 3849 */   private BooleanConnectionProperty detectCustomCollations; private StringConnectionProperty serverRSAPublicKeyFile; private BooleanConnectionProperty allowPublicKeyRetrieval; private BooleanConnectionProperty dontCheckOnDuplicateKeyUpdateInSQL; private BooleanConnectionProperty readOnlyPropagatesToServer; private StringConnectionProperty enabledSSLCipherSuites; private BooleanConnectionProperty enableEscapeProcessing; public void setUltraDevHack(boolean flag) { setUseUltraDevWorkAround(flag); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUltraDevHack()
/*      */   {
/* 3858 */     return getUseUltraDevWorkAround();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setInteractiveClient(boolean property)
/*      */   {
/* 3867 */     setIsInteractiveClient(property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSocketFactory(String name)
/*      */   {
/* 3876 */     setSocketFactoryClassName(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSocketFactory()
/*      */   {
/* 3885 */     return getSocketFactoryClassName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUseServerPrepStmts(boolean flag)
/*      */   {
/* 3894 */     setUseServerPreparedStmts(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUseServerPrepStmts()
/*      */   {
/* 3903 */     return getUseServerPreparedStmts();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCacheCallableStmts(boolean flag)
/*      */   {
/* 3912 */     setCacheCallableStatements(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getCacheCallableStmts()
/*      */   {
/* 3921 */     return getCacheCallableStatements();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCachePrepStmts(boolean flag)
/*      */   {
/* 3930 */     setCachePreparedStatements(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getCachePrepStmts()
/*      */   {
/* 3939 */     return getCachePreparedStatements();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCallableStmtCacheSize(int cacheSize)
/*      */     throws SQLException
/*      */   {
/* 3948 */     setCallableStatementCacheSize(cacheSize);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCallableStmtCacheSize()
/*      */   {
/* 3957 */     return getCallableStatementCacheSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPrepStmtCacheSize(int cacheSize)
/*      */     throws SQLException
/*      */   {
/* 3966 */     setPreparedStatementCacheSize(cacheSize);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPrepStmtCacheSize()
/*      */   {
/* 3975 */     return getPreparedStatementCacheSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit)
/*      */     throws SQLException
/*      */   {
/* 3984 */     setPreparedStatementCacheSqlLimit(sqlLimit);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPrepStmtCacheSqlLimit()
/*      */   {
/* 3993 */     return getPreparedStatementCacheSqlLimit();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getNoAccessToProcedureBodies()
/*      */   {
/* 4002 */     return this.noAccessToProcedureBodies.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/* 4011 */     this.noAccessToProcedureBodies.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUseOldAliasMetadataBehavior()
/*      */   {
/* 4020 */     return this.useOldAliasMetadataBehavior.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/* 4029 */     this.useOldAliasMetadataBehavior.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getClientCertificateKeyStorePassword()
/*      */   {
/* 4038 */     return this.clientCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setClientCertificateKeyStorePassword(String value)
/*      */   {
/* 4047 */     this.clientCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getClientCertificateKeyStoreType()
/*      */   {
/* 4056 */     return this.clientCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setClientCertificateKeyStoreType(String value)
/*      */   {
/* 4065 */     this.clientCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getClientCertificateKeyStoreUrl()
/*      */   {
/* 4074 */     return this.clientCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setClientCertificateKeyStoreUrl(String value)
/*      */   {
/* 4083 */     this.clientCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrustCertificateKeyStorePassword()
/*      */   {
/* 4092 */     return this.trustCertificateKeyStorePassword.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/* 4101 */     this.trustCertificateKeyStorePassword.setValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrustCertificateKeyStoreType()
/*      */   {
/* 4110 */     return this.trustCertificateKeyStoreType.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustCertificateKeyStoreType(String value)
/*      */   {
/* 4119 */     this.trustCertificateKeyStoreType.setValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrustCertificateKeyStoreUrl()
/*      */   {
/* 4128 */     return this.trustCertificateKeyStoreUrl.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/* 4137 */     this.trustCertificateKeyStoreUrl.setValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUseSSPSCompatibleTimezoneShift()
/*      */   {
/* 4146 */     return this.useSSPSCompatibleTimezoneShift.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/* 4155 */     this.useSSPSCompatibleTimezoneShift.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getTreatUtilDateAsTimestamp()
/*      */   {
/* 4164 */     return this.treatUtilDateAsTimestamp.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/* 4173 */     this.treatUtilDateAsTimestamp.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUseFastDateParsing()
/*      */   {
/* 4182 */     return this.useFastDateParsing.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUseFastDateParsing(boolean flag)
/*      */   {
/* 4191 */     this.useFastDateParsing.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getLocalSocketAddress()
/*      */   {
/* 4200 */     return this.localSocketAddress.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLocalSocketAddress(String address)
/*      */   {
/* 4209 */     this.localSocketAddress.setValue(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUseConfigs(String configs)
/*      */   {
/* 4218 */     this.useConfigs.setValue(configs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getUseConfigs()
/*      */   {
/* 4227 */     return this.useConfigs.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getGenerateSimpleParameterMetadata()
/*      */   {
/* 4236 */     return this.generateSimpleParameterMetadata.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag)
/*      */   {
/* 4245 */     this.generateSimpleParameterMetadata.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getLogXaCommands()
/*      */   {
/* 4254 */     return this.logXaCommands.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLogXaCommands(boolean flag)
/*      */   {
/* 4263 */     this.logXaCommands.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getResultSetSizeThreshold()
/*      */   {
/* 4272 */     return this.resultSetSizeThreshold.getValueAsInt();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setResultSetSizeThreshold(int threshold)
/*      */     throws SQLException
/*      */   {
/* 4281 */     this.resultSetSizeThreshold.setValue(threshold, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getNetTimeoutForStreamingResults()
/*      */   {
/* 4290 */     return this.netTimeoutForStreamingResults.getValueAsInt();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNetTimeoutForStreamingResults(int value)
/*      */     throws SQLException
/*      */   {
/* 4299 */     this.netTimeoutForStreamingResults.setValue(value, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getEnableQueryTimeouts()
/*      */   {
/* 4308 */     return this.enableQueryTimeouts.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEnableQueryTimeouts(boolean flag)
/*      */   {
/* 4317 */     this.enableQueryTimeouts.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getPadCharsWithSpace()
/*      */   {
/* 4326 */     return this.padCharsWithSpace.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPadCharsWithSpace(boolean flag)
/*      */   {
/* 4335 */     this.padCharsWithSpace.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getUseDynamicCharsetInfo()
/*      */   {
/* 4344 */     return this.useDynamicCharsetInfo.getValueAsBoolean();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/* 4353 */     this.useDynamicCharsetInfo.setValue(flag);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getClientInfoProvider()
/*      */   {
/* 4362 */     return this.clientInfoProvider.getValueAsString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setClientInfoProvider(String classname)
/*      */   {
/* 4371 */     this.clientInfoProvider.setValue(classname);
/*      */   }
/*      */   
/*      */   public boolean getPopulateInsertRowWithDefaultValues() {
/* 4375 */     return this.populateInsertRowWithDefaultValues.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag) {
/* 4379 */     this.populateInsertRowWithDefaultValues.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceStrategy() {
/* 4383 */     return this.loadBalanceStrategy.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceStrategy(String strategy) {
/* 4387 */     this.loadBalanceStrategy.setValue(strategy);
/*      */   }
/*      */   
/*      */   public boolean getTcpNoDelay() {
/* 4391 */     return this.tcpNoDelay.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setTcpNoDelay(boolean flag) {
/* 4395 */     this.tcpNoDelay.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getTcpKeepAlive() {
/* 4399 */     return this.tcpKeepAlive.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setTcpKeepAlive(boolean flag) {
/* 4403 */     this.tcpKeepAlive.setValue(flag);
/*      */   }
/*      */   
/*      */   public int getTcpRcvBuf() {
/* 4407 */     return this.tcpRcvBuf.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setTcpRcvBuf(int bufSize) throws SQLException {
/* 4411 */     this.tcpRcvBuf.setValue(bufSize, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getTcpSndBuf() {
/* 4415 */     return this.tcpSndBuf.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setTcpSndBuf(int bufSize) throws SQLException {
/* 4419 */     this.tcpSndBuf.setValue(bufSize, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getTcpTrafficClass() {
/* 4423 */     return this.tcpTrafficClass.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setTcpTrafficClass(int classFlags) throws SQLException {
/* 4427 */     this.tcpTrafficClass.setValue(classFlags, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public boolean getUseNanosForElapsedTime() {
/* 4431 */     return this.useNanosForElapsedTime.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseNanosForElapsedTime(boolean flag) {
/* 4435 */     this.useNanosForElapsedTime.setValue(flag);
/*      */   }
/*      */   
/*      */   public long getSlowQueryThresholdNanos() {
/* 4439 */     return this.slowQueryThresholdNanos.getValueAsLong();
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
/* 4443 */     this.slowQueryThresholdNanos.setValue(nanos, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public String getStatementInterceptors() {
/* 4447 */     return this.statementInterceptors.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setStatementInterceptors(String value) {
/* 4451 */     this.statementInterceptors.setValue(value);
/*      */   }
/*      */   
/*      */   public boolean getUseDirectRowUnpack() {
/* 4455 */     return this.useDirectRowUnpack.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseDirectRowUnpack(boolean flag) {
/* 4459 */     this.useDirectRowUnpack.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getLargeRowSizeThreshold() {
/* 4463 */     return this.largeRowSizeThreshold.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLargeRowSizeThreshold(String value) throws SQLException {
/* 4467 */     this.largeRowSizeThreshold.setValue(value, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public boolean getUseBlobToStoreUTF8OutsideBMP() {
/* 4471 */     return this.useBlobToStoreUTF8OutsideBMP.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
/* 4475 */     this.useBlobToStoreUTF8OutsideBMP.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getUtf8OutsideBmpExcludedColumnNamePattern() {
/* 4479 */     return this.utf8OutsideBmpExcludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
/* 4483 */     this.utf8OutsideBmpExcludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */   
/*      */   public String getUtf8OutsideBmpIncludedColumnNamePattern() {
/* 4487 */     return this.utf8OutsideBmpIncludedColumnNamePattern.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
/* 4491 */     this.utf8OutsideBmpIncludedColumnNamePattern.setValue(regexPattern);
/*      */   }
/*      */   
/*      */   public boolean getIncludeInnodbStatusInDeadlockExceptions() {
/* 4495 */     return this.includeInnodbStatusInDeadlockExceptions.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
/* 4499 */     this.includeInnodbStatusInDeadlockExceptions.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getBlobsAreStrings() {
/* 4503 */     return this.blobsAreStrings.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setBlobsAreStrings(boolean flag) {
/* 4507 */     this.blobsAreStrings.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getFunctionsNeverReturnBlobs() {
/* 4511 */     return this.functionsNeverReturnBlobs.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag) {
/* 4515 */     this.functionsNeverReturnBlobs.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getAutoSlowLog() {
/* 4519 */     return this.autoSlowLog.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAutoSlowLog(boolean flag) {
/* 4523 */     this.autoSlowLog.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getConnectionLifecycleInterceptors() {
/* 4527 */     return this.connectionLifecycleInterceptors.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setConnectionLifecycleInterceptors(String interceptors) {
/* 4531 */     this.connectionLifecycleInterceptors.setValue(interceptors);
/*      */   }
/*      */   
/*      */   public String getProfilerEventHandler() {
/* 4535 */     return this.profilerEventHandler.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setProfilerEventHandler(String handler) {
/* 4539 */     this.profilerEventHandler.setValue(handler);
/*      */   }
/*      */   
/*      */   public boolean getVerifyServerCertificate() {
/* 4543 */     return this.verifyServerCertificate.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setVerifyServerCertificate(boolean flag) {
/* 4547 */     this.verifyServerCertificate.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseLegacyDatetimeCode() {
/* 4551 */     return this.useLegacyDatetimeCode.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseLegacyDatetimeCode(boolean flag) {
/* 4555 */     this.useLegacyDatetimeCode.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getSendFractionalSeconds() {
/* 4559 */     return this.sendFractionalSeconds.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setSendFractionalSeconds(boolean flag) {
/* 4563 */     this.sendFractionalSeconds.setValue(flag);
/*      */   }
/*      */   
/*      */   public int getSelfDestructOnPingSecondsLifetime() {
/* 4567 */     return this.selfDestructOnPingSecondsLifetime.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
/* 4571 */     this.selfDestructOnPingSecondsLifetime.setValue(seconds, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getSelfDestructOnPingMaxOperations() {
/* 4575 */     return this.selfDestructOnPingMaxOperations.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
/* 4579 */     this.selfDestructOnPingMaxOperations.setValue(maxOperations, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public boolean getUseColumnNamesInFindColumn() {
/* 4583 */     return this.useColumnNamesInFindColumn.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseColumnNamesInFindColumn(boolean flag) {
/* 4587 */     this.useColumnNamesInFindColumn.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseLocalTransactionState() {
/* 4591 */     return this.useLocalTransactionState.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setUseLocalTransactionState(boolean flag) {
/* 4595 */     this.useLocalTransactionState.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getCompensateOnDuplicateKeyUpdateCounts() {
/* 4599 */     return this.compensateOnDuplicateKeyUpdateCounts.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
/* 4603 */     this.compensateOnDuplicateKeyUpdateCounts.setValue(flag);
/*      */   }
/*      */   
/*      */   public int getLoadBalanceBlacklistTimeout() {
/* 4607 */     return this.loadBalanceBlacklistTimeout.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException {
/* 4611 */     this.loadBalanceBlacklistTimeout.setValue(loadBalanceBlacklistTimeout, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getLoadBalancePingTimeout() {
/* 4615 */     return this.loadBalancePingTimeout.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException {
/* 4619 */     this.loadBalancePingTimeout.setValue(loadBalancePingTimeout, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public void setRetriesAllDown(int retriesAllDown) throws SQLException {
/* 4623 */     this.retriesAllDown.setValue(retriesAllDown, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getRetriesAllDown() {
/* 4627 */     return this.retriesAllDown.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setUseAffectedRows(boolean flag) {
/* 4631 */     this.useAffectedRows.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getUseAffectedRows() {
/* 4635 */     return this.useAffectedRows.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setPasswordCharacterEncoding(String characterSet) {
/* 4639 */     this.passwordCharacterEncoding.setValue(characterSet);
/*      */   }
/*      */   
/*      */   public String getPasswordCharacterEncoding() {
/*      */     String encoding;
/* 4644 */     if ((encoding = this.passwordCharacterEncoding.getValueAsString()) != null) {
/* 4645 */       return encoding;
/*      */     }
/* 4647 */     if ((getUseUnicode()) && ((encoding = getEncoding()) != null)) {
/* 4648 */       return encoding;
/*      */     }
/* 4650 */     return "UTF-8";
/*      */   }
/*      */   
/*      */   public void setExceptionInterceptors(String exceptionInterceptors) {
/* 4654 */     this.exceptionInterceptors.setValue(exceptionInterceptors);
/*      */   }
/*      */   
/*      */   public String getExceptionInterceptors() {
/* 4658 */     return this.exceptionInterceptors.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setMaxAllowedPacket(int max) throws SQLException {
/* 4662 */     this.maxAllowedPacket.setValue(max, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getMaxAllowedPacket() {
/* 4666 */     return this.maxAllowedPacket.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getQueryTimeoutKillsConnection() {
/* 4670 */     return this.queryTimeoutKillsConnection.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection) {
/* 4674 */     this.queryTimeoutKillsConnection.setValue(queryTimeoutKillsConnection);
/*      */   }
/*      */   
/*      */   public boolean getLoadBalanceValidateConnectionOnSwapServer() {
/* 4678 */     return this.loadBalanceValidateConnectionOnSwapServer.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer) {
/* 4682 */     this.loadBalanceValidateConnectionOnSwapServer.setValue(loadBalanceValidateConnectionOnSwapServer);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceConnectionGroup() {
/* 4686 */     return this.loadBalanceConnectionGroup.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup) {
/* 4690 */     this.loadBalanceConnectionGroup.setValue(loadBalanceConnectionGroup);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceExceptionChecker() {
/* 4694 */     return this.loadBalanceExceptionChecker.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker) {
/* 4698 */     this.loadBalanceExceptionChecker.setValue(loadBalanceExceptionChecker);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceSQLStateFailover() {
/* 4702 */     return this.loadBalanceSQLStateFailover.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover) {
/* 4706 */     this.loadBalanceSQLStateFailover.setValue(loadBalanceSQLStateFailover);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceSQLExceptionSubclassFailover() {
/* 4710 */     return this.loadBalanceSQLExceptionSubclassFailover.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover) {
/* 4714 */     this.loadBalanceSQLExceptionSubclassFailover.setValue(loadBalanceSQLExceptionSubclassFailover);
/*      */   }
/*      */   
/*      */   public boolean getLoadBalanceEnableJMX() {
/* 4718 */     return this.loadBalanceEnableJMX.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX) {
/* 4722 */     this.loadBalanceEnableJMX.setValue(loadBalanceEnableJMX);
/*      */   }
/*      */   
/*      */   public void setLoadBalanceHostRemovalGracePeriod(int loadBalanceHostRemovalGracePeriod) throws SQLException {
/* 4726 */     this.loadBalanceHostRemovalGracePeriod.setValue(loadBalanceHostRemovalGracePeriod, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getLoadBalanceHostRemovalGracePeriod() {
/* 4730 */     return this.loadBalanceHostRemovalGracePeriod.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException {
/* 4734 */     this.loadBalanceAutoCommitStatementThreshold.setValue(loadBalanceAutoCommitStatementThreshold, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public int getLoadBalanceAutoCommitStatementThreshold() {
/* 4738 */     return this.loadBalanceAutoCommitStatementThreshold.getValueAsInt();
/*      */   }
/*      */   
/*      */   public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex) {
/* 4742 */     this.loadBalanceAutoCommitStatementRegex.setValue(loadBalanceAutoCommitStatementRegex);
/*      */   }
/*      */   
/*      */   public String getLoadBalanceAutoCommitStatementRegex() {
/* 4746 */     return this.loadBalanceAutoCommitStatementRegex.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
/* 4750 */     this.includeThreadDumpInDeadlockExceptions.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getIncludeThreadDumpInDeadlockExceptions() {
/* 4754 */     return this.includeThreadDumpInDeadlockExceptions.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setIncludeThreadNamesAsStatementComment(boolean flag) {
/* 4758 */     this.includeThreadNamesAsStatementComment.setValue(flag);
/*      */   }
/*      */   
/*      */   public boolean getIncludeThreadNamesAsStatementComment() {
/* 4762 */     return this.includeThreadNamesAsStatementComment.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAuthenticationPlugins(String authenticationPlugins) {
/* 4766 */     this.authenticationPlugins.setValue(authenticationPlugins);
/*      */   }
/*      */   
/*      */   public String getAuthenticationPlugins() {
/* 4770 */     return this.authenticationPlugins.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins) {
/* 4774 */     this.disabledAuthenticationPlugins.setValue(disabledAuthenticationPlugins);
/*      */   }
/*      */   
/*      */   public String getDisabledAuthenticationPlugins() {
/* 4778 */     return this.disabledAuthenticationPlugins.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin) {
/* 4782 */     this.defaultAuthenticationPlugin.setValue(defaultAuthenticationPlugin);
/*      */   }
/*      */   
/*      */   public String getDefaultAuthenticationPlugin() {
/* 4786 */     return this.defaultAuthenticationPlugin.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setParseInfoCacheFactory(String factoryClassname) {
/* 4790 */     this.parseInfoCacheFactory.setValue(factoryClassname);
/*      */   }
/*      */   
/*      */   public String getParseInfoCacheFactory() {
/* 4794 */     return this.parseInfoCacheFactory.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setServerConfigCacheFactory(String factoryClassname) {
/* 4798 */     this.serverConfigCacheFactory.setValue(factoryClassname);
/*      */   }
/*      */   
/*      */   public String getServerConfigCacheFactory() {
/* 4802 */     return this.serverConfigCacheFactory.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords) {
/* 4806 */     this.disconnectOnExpiredPasswords.setValue(disconnectOnExpiredPasswords);
/*      */   }
/*      */   
/*      */   public boolean getDisconnectOnExpiredPasswords() {
/* 4810 */     return this.disconnectOnExpiredPasswords.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public boolean getAllowMasterDownConnections() {
/* 4814 */     return this.allowMasterDownConnections.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
/* 4818 */     this.allowMasterDownConnections.setValue(connectIfMasterDown);
/*      */   }
/*      */   
/*      */   public boolean getAllowSlaveDownConnections() {
/* 4822 */     return this.allowSlaveDownConnections.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAllowSlaveDownConnections(boolean connectIfSlaveDown) {
/* 4826 */     this.allowSlaveDownConnections.setValue(connectIfSlaveDown);
/*      */   }
/*      */   
/*      */   public boolean getReadFromMasterWhenNoSlaves() {
/* 4830 */     return this.readFromMasterWhenNoSlaves.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setReadFromMasterWhenNoSlaves(boolean useMasterIfSlavesDown) {
/* 4834 */     this.readFromMasterWhenNoSlaves.setValue(useMasterIfSlavesDown);
/*      */   }
/*      */   
/*      */   public boolean getReplicationEnableJMX() {
/* 4838 */     return this.replicationEnableJMX.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setReplicationEnableJMX(boolean replicationEnableJMX) {
/* 4842 */     this.replicationEnableJMX.setValue(replicationEnableJMX);
/*      */   }
/*      */   
/*      */   public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
/* 4846 */     this.getProceduresReturnsFunctions.setValue(getProcedureReturnsFunctions);
/*      */   }
/*      */   
/*      */   public boolean getGetProceduresReturnsFunctions() {
/* 4850 */     return this.getProceduresReturnsFunctions.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setDetectCustomCollations(boolean detectCustomCollations) {
/* 4854 */     this.detectCustomCollations.setValue(detectCustomCollations);
/*      */   }
/*      */   
/*      */   public boolean getDetectCustomCollations() {
/* 4858 */     return this.detectCustomCollations.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public String getServerRSAPublicKeyFile() {
/* 4862 */     return this.serverRSAPublicKeyFile.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setServerRSAPublicKeyFile(String serverRSAPublicKeyFile) throws SQLException {
/* 4866 */     if (this.serverRSAPublicKeyFile.getUpdateCount() > 0) {
/* 4867 */       throw SQLError.createSQLException(Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[] { "'serverRSAPublicKeyFile'" }), "S1009", null);
/*      */     }
/*      */     
/* 4870 */     this.serverRSAPublicKeyFile.setValue(serverRSAPublicKeyFile);
/*      */   }
/*      */   
/*      */   public boolean getAllowPublicKeyRetrieval() {
/* 4874 */     return this.allowPublicKeyRetrieval.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval) throws SQLException {
/* 4878 */     if (this.allowPublicKeyRetrieval.getUpdateCount() > 0) {
/* 4879 */       throw SQLError.createSQLException(Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[] { "'allowPublicKeyRetrieval'" }), "S1009", null);
/*      */     }
/*      */     
/*      */ 
/* 4883 */     this.allowPublicKeyRetrieval.setValue(allowPublicKeyRetrieval);
/*      */   }
/*      */   
/*      */   public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean dontCheckOnDuplicateKeyUpdateInSQL) {
/* 4887 */     this.dontCheckOnDuplicateKeyUpdateInSQL.setValue(dontCheckOnDuplicateKeyUpdateInSQL);
/*      */   }
/*      */   
/*      */   public boolean getDontCheckOnDuplicateKeyUpdateInSQL() {
/* 4891 */     return this.dontCheckOnDuplicateKeyUpdateInSQL.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setSocksProxyHost(String socksProxyHost) {
/* 4895 */     this.socksProxyHost.setValue(socksProxyHost);
/*      */   }
/*      */   
/*      */   public String getSocksProxyHost() {
/* 4899 */     return this.socksProxyHost.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setSocksProxyPort(int socksProxyPort) throws SQLException {
/* 4903 */     this.socksProxyPort.setValue(socksProxyPort, null);
/*      */   }
/*      */   
/*      */   public int getSocksProxyPort() {
/* 4907 */     return this.socksProxyPort.getValueAsInt();
/*      */   }
/*      */   
/*      */   public boolean getReadOnlyPropagatesToServer() {
/* 4911 */     return this.readOnlyPropagatesToServer.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setReadOnlyPropagatesToServer(boolean flag) {
/* 4915 */     this.readOnlyPropagatesToServer.setValue(flag);
/*      */   }
/*      */   
/*      */   public String getEnabledSSLCipherSuites() {
/* 4919 */     return this.enabledSSLCipherSuites.getValueAsString();
/*      */   }
/*      */   
/*      */   public void setEnabledSSLCipherSuites(String cipherSuites) {
/* 4923 */     this.enabledSSLCipherSuites.setValue(cipherSuites);
/*      */   }
/*      */   
/*      */   public boolean getEnableEscapeProcessing() {
/* 4927 */     return this.enableEscapeProcessing.getValueAsBoolean();
/*      */   }
/*      */   
/*      */   public void setEnableEscapeProcessing(boolean flag) {
/* 4931 */     this.enableEscapeProcessing.setValue(flag);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ConnectionPropertiesImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */