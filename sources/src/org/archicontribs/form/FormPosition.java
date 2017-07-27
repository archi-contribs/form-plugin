package org.archicontribs.form;

public class FormPosition {
    private static String formName = null;
    private static String tabName = null;
    private static String controlName = null;
    private static String controlClass = null;
    private static String columnName = null;
    
    public static String getFormName() {
        return formName;
    }
    public static void setFormName(String formName) {
        FormPosition.formName = formName;
    }
    public static void resetFormName() {
        FormPosition.formName = null;
    }
    
    public static String getTabName() {
        return tabName;
    }
    public static void setTabName(String tabName) {
        FormPosition.tabName = tabName;
    }
    public static void resetTabName() {
        FormPosition.tabName = null;
    }
    
    public static String getControlName() {
        return controlName;
    }
    public static void setControlName(String controlName) {
        FormPosition.controlName = controlName;
    }
    public static void resetControlName() {
        FormPosition.controlName = null;
    }
    
    public static String getControlClass() {
        return controlClass;
    }
    public static void setControlClass(String controlClass) {
        FormPosition.controlClass = controlClass;
    }
    public static void resetControlClass() {
        FormPosition.controlClass = null;
    }
    
    public static String getColumnName() {
        return columnName;
    }
    public static void setColumnName(String columnName) {
        FormPosition.columnName = columnName;
    }
    public static void resetColumnName() {
        FormPosition.columnName = null;
    }
    
    public static String getPosition(String attributeName) {
        StringBuilder str = new StringBuilder();
        
        str.append("In form \"").append(formName).append("\"");
        
        if ( tabName != null )
            str.append("\nIn tab \"").append(tabName).append("\"");
        
        if ( controlName != null )
            str.append("\nIn ").append(controlClass).append(" \"").append(controlName).append("\"");
        
        if ( columnName != null )
            str.append("\nIn column \"").append(columnName).append("\"");
        
        if ( attributeName != null )
            str.append("\nAttribute \"").append(attributeName).append("\"");
        
        return str.toString();
    }
}
