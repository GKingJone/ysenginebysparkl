package com.mchange.v2.codegen.bean;

public abstract interface Property
{
  public abstract int getVariableModifiers();
  
  public abstract String getName();
  
  public abstract String getSimpleTypeName();
  
  public abstract String getDefensiveCopyExpression();
  
  public abstract String getDefaultValueExpression();
  
  public abstract int getGetterModifiers();
  
  public abstract int getSetterModifiers();
  
  public abstract boolean isReadOnly();
  
  public abstract boolean isBound();
  
  public abstract boolean isConstrained();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\Property.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */