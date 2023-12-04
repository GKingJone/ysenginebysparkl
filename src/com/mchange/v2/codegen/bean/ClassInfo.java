package com.mchange.v2.codegen.bean;

public abstract interface ClassInfo
{
  public abstract String getPackageName();
  
  public abstract int getModifiers();
  
  public abstract String getClassName();
  
  public abstract String getSuperclassName();
  
  public abstract String[] getInterfaceNames();
  
  public abstract String[] getGeneralImports();
  
  public abstract String[] getSpecificImports();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\ClassInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */