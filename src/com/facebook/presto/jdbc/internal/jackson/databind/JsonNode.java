/*     */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonPointer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.TreeNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.MissingNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JsonNode
/*     */   extends JsonSerializable.Base
/*     */   implements TreeNode, Iterable<JsonNode>
/*     */ {
/*     */   public abstract <T extends JsonNode> T deepCopy();
/*     */   
/*     */   public int size()
/*     */   {
/*  82 */     return 0;
/*     */   }
/*     */   
/*     */   public final boolean isValueNode()
/*     */   {
/*  87 */     switch (getNodeType()) {
/*     */     case ARRAY: case OBJECT: case MISSING: 
/*  89 */       return false;
/*     */     }
/*  91 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public final boolean isContainerNode()
/*     */   {
/*  97 */     JsonNodeType type = getNodeType();
/*  98 */     return (type == JsonNodeType.OBJECT) || (type == JsonNodeType.ARRAY);
/*     */   }
/*     */   
/*     */   public final boolean isMissingNode()
/*     */   {
/* 103 */     return getNodeType() == JsonNodeType.MISSING;
/*     */   }
/*     */   
/*     */   public final boolean isArray()
/*     */   {
/* 108 */     return getNodeType() == JsonNodeType.ARRAY;
/*     */   }
/*     */   
/*     */   public final boolean isObject()
/*     */   {
/* 113 */     return getNodeType() == JsonNodeType.OBJECT;
/*     */   }
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
/*     */   public abstract JsonNode get(int paramInt);
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
/*     */   public JsonNode get(String fieldName)
/*     */   {
/* 156 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonNode path(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonNode path(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<String> fieldNames()
/*     */   {
/* 183 */     return ClassUtil.emptyIterator();
/*     */   }
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
/*     */   public final JsonNode at(JsonPointer ptr)
/*     */   {
/* 200 */     if (ptr.matches()) {
/* 201 */       return this;
/*     */     }
/* 203 */     JsonNode n = _at(ptr);
/* 204 */     if (n == null) {
/* 205 */       return MissingNode.getInstance();
/*     */     }
/* 207 */     return n.at(ptr.tail());
/*     */   }
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
/*     */   public final JsonNode at(String jsonPtrExpr)
/*     */   {
/* 230 */     return at(JsonPointer.compile(jsonPtrExpr));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract JsonNode _at(JsonPointer paramJsonPointer);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonNodeType getNodeType();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isPojo()
/*     */   {
/* 261 */     return getNodeType() == JsonNodeType.POJO;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean isNumber()
/*     */   {
/* 268 */     return getNodeType() == JsonNodeType.NUMBER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isIntegralNumber()
/*     */   {
/* 276 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFloatingPointNumber()
/*     */   {
/* 282 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isShort()
/*     */   {
/* 294 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInt()
/*     */   {
/* 306 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLong()
/*     */   {
/* 318 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 323 */   public boolean isFloat() { return false; }
/*     */   
/* 325 */   public boolean isDouble() { return false; }
/* 326 */   public boolean isBigDecimal() { return false; }
/* 327 */   public boolean isBigInteger() { return false; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isTextual()
/*     */   {
/* 334 */     return getNodeType() == JsonNodeType.STRING;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isBoolean()
/*     */   {
/* 342 */     return getNodeType() == JsonNodeType.BOOLEAN;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isNull()
/*     */   {
/* 350 */     return getNodeType() == JsonNodeType.NULL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isBinary()
/*     */   {
/* 362 */     return getNodeType() == JsonNodeType.BINARY;
/*     */   }
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
/*     */   public boolean canConvertToInt()
/*     */   {
/* 379 */     return false;
/*     */   }
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
/*     */   public boolean canConvertToLong()
/*     */   {
/* 395 */     return false;
/*     */   }
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
/*     */   public String textValue()
/*     */   {
/* 413 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] binaryValue()
/*     */     throws IOException
/*     */   {
/* 426 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean booleanValue()
/*     */   {
/* 437 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Number numberValue()
/*     */   {
/* 447 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public short shortValue()
/*     */   {
/* 459 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 471 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long longValue()
/*     */   {
/* 483 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public float floatValue()
/*     */   {
/* 496 */     return 0.0F;
/*     */   }
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
/* 509 */   public double doubleValue() { return 0.0D; }
/*     */   
/* 511 */   public BigDecimal decimalValue() { return BigDecimal.ZERO; }
/* 512 */   public BigInteger bigIntegerValue() { return BigInteger.ZERO; }
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
/*     */   public abstract String asText();
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
/*     */   public String asText(String defaultValue)
/*     */   {
/* 537 */     String str = asText();
/* 538 */     return str == null ? defaultValue : str;
/*     */   }
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
/*     */   public int asInt()
/*     */   {
/* 552 */     return asInt(0);
/*     */   }
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
/*     */   public int asInt(int defaultValue)
/*     */   {
/* 566 */     return defaultValue;
/*     */   }
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
/*     */   public long asLong()
/*     */   {
/* 580 */     return asLong(0L);
/*     */   }
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
/*     */   public long asLong(long defaultValue)
/*     */   {
/* 594 */     return defaultValue;
/*     */   }
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
/*     */   public double asDouble()
/*     */   {
/* 608 */     return asDouble(0.0D);
/*     */   }
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
/*     */   public double asDouble(double defaultValue)
/*     */   {
/* 622 */     return defaultValue;
/*     */   }
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
/*     */   public boolean asBoolean()
/*     */   {
/* 636 */     return asBoolean(false);
/*     */   }
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
/*     */   public boolean asBoolean(boolean defaultValue)
/*     */   {
/* 650 */     return defaultValue;
/*     */   }
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
/*     */ 
/*     */   public boolean has(String fieldName)
/*     */   {
/* 680 */     return get(fieldName) != null;
/*     */   }
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
/*     */   public boolean has(int index)
/*     */   {
/* 706 */     return get(index) != null;
/*     */   }
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
/*     */   public boolean hasNonNull(String fieldName)
/*     */   {
/* 721 */     JsonNode n = get(fieldName);
/* 722 */     return (n != null) && (!n.isNull());
/*     */   }
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
/*     */   public boolean hasNonNull(int index)
/*     */   {
/* 737 */     JsonNode n = get(index);
/* 738 */     return (n != null) && (!n.isNull());
/*     */   }
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
/*     */   public final Iterator<JsonNode> iterator()
/*     */   {
/* 753 */     return elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<JsonNode> elements()
/*     */   {
/* 762 */     return ClassUtil.emptyIterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<Map.Entry<String, JsonNode>> fields()
/*     */   {
/* 770 */     return ClassUtil.emptyIterator();
/*     */   }
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
/*     */   public abstract JsonNode findValue(String paramString);
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
/*     */   public final List<JsonNode> findValues(String fieldName)
/*     */   {
/* 801 */     List<JsonNode> result = findValues(fieldName, null);
/* 802 */     if (result == null) {
/* 803 */       return Collections.emptyList();
/*     */     }
/* 805 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final List<String> findValuesAsText(String fieldName)
/*     */   {
/* 814 */     List<String> result = findValuesAsText(fieldName, null);
/* 815 */     if (result == null) {
/* 816 */       return Collections.emptyList();
/*     */     }
/* 818 */     return result;
/*     */   }
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
/*     */   public abstract JsonNode findPath(String paramString);
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
/*     */   public abstract JsonNode findParent(String paramString);
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
/*     */   public final List<JsonNode> findParents(String fieldName)
/*     */   {
/* 857 */     List<JsonNode> result = findParents(fieldName, null);
/* 858 */     if (result == null) {
/* 859 */       return Collections.emptyList();
/*     */     }
/* 861 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<JsonNode> findValues(String paramString, List<JsonNode> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<String> findValuesAsText(String paramString, List<String> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<JsonNode> findParents(String paramString, List<JsonNode> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonNode with(String propertyName)
/*     */   {
/* 883 */     throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + getClass().getName() + "), can not call with() on it");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonNode withArray(String propertyName)
/*     */   {
/* 896 */     throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + getClass().getName() + "), can not call withArray() on it");
/*     */   }
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
/*     */   public boolean equals(Comparator<JsonNode> comparator, JsonNode other)
/*     */   {
/* 925 */     return comparator.compare(this, other) == 0;
/*     */   }
/*     */   
/*     */   public abstract String toString();
/*     */   
/*     */   public abstract boolean equals(Object paramObject);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\JsonNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */