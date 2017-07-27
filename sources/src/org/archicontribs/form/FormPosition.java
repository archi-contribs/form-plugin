package org.archicontribs.form;

public class FormPosition {
    private static String formName = null;
    private static String tabName = null;
    private static String controlName = null;
    private static String controlClass = null;
    private static String columnName = null;
    
    public String getFormName() {
        return formName;
    }
    public void setFormName(String formName) {
        FormPosition.formName = formName;
    }
    public void resetFormName() {
        FormPosition.formName = null;
    }
    
    public String getTabName() {
        return tabName;
    }
    public void setTabName(String tabName) {
        FormPosition.tabName = tabName;
    }
    public void resetTabName() {
        FormPosition.tabName = null;
    }
    
    public String getControlName() {
        return controlName;
    }
    public void setControlName(String controlName) {
        FormPosition.controlName = controlName;
    }
    public void resetControlName() {
        FormPosition.controlName = null;
    }
    
    public String getControlClass() {
        return controlClass;
    }
    public void setControlClass(String controlClass) {
        FormPosition.controlClass = controlClass;
    }
    public void resetControlClass() {
        FormPosition.controlClass = null;
    }
    
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        FormPosition.columnName = columnName;
    }
    public void resetColumnName() {
        FormPosition.columnName = null;
    }
    
    public String getPosition(String attributeName) {
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
