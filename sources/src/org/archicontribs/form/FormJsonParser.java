package org.archicontribs.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Methods to parse JSON objects and create the corresponding SWT widgets
 * 
 * @author Herve Jouin
 *
 */
public class FormJsonParser {
	private static final FormLogger logger            = new FormLogger(FormDialog.class);
	
	protected static Display        display           = Display.getDefault();
	
    /**
     * @param ignoreErrors : set to true if the errors must be ignored, false if an Exception should be raised in case of error.<br><br>
     * This parameter is useful for the graphical editor that should be able to parse the totality of the jsonObject.
     */
	public FormJsonParser() {
	}
	

	
    /**
     * Parses the jsonObject and create the corresponding Shell<br>
     * <br>
     * @param jsonObject the jsonObject to parse<br>
     * @param parent the parent shell
     * 
     * @return the created Shell
     * 
     * @throws ClassCastException when a property does not belong to the right class in the jsonObject (i.e. a String is found while an Integer was expected)
     * @throws RuntimeException when a property has got an unexpected value (i.e. a negative value where a positive one was expected)
     */
    public void createForm(JSONObject jsonObject, Shell form, TreeItem treeItem) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating form");
        
        if ( treeItem != null )
        	treeItem.setData("control", form);
        
        //TODO:
        //TODO: use helper functions
    	//TODO:
        String  name              = getName(jsonObject, form, treeItem); FormPosition.setFormName(name);
        
        Integer width             = getInt(jsonObject, "width");
        Integer height            = getInt(jsonObject, "height");
        Integer spacing           = getInt(jsonObject, "spacing");
        Integer buttonWidth       = getInt(jsonObject, "buttonWidth");
        Integer buttonHeight      = getInt(jsonObject, "buttonHeight");
        String  refers            = getString(jsonObject, "refers");
        String  variableSeparator = getString(jsonObject, "variableSeparator");
        String  whenEmpty         = getString(jsonObject, "whenEmpty");
        
        getForegroundAndBackground(jsonObject, form, treeItem);
        
        String  buttonOkText          = getString(jsonObject, "buttonOk");
        String  buttonCancelText      = getString(jsonObject, "buttonCancel");
        String  buttonExportText      = getString(jsonObject, "buttonExport");
    	
        if (logger.isTraceEnabled()) {
            logger.trace("   variableSeparator = " + variableSeparator);
            logger.trace("   width = " + width);
            logger.trace("   height = " + height);
            logger.trace("   spacing = " + spacing);
            logger.trace("   buttonWidth = " + buttonWidth);
            logger.trace("   buttonHeight = " + buttonHeight);
            logger.trace("   refers = " + refers);
            logger.trace("   buttonOk = " + buttonOkText);
            logger.trace("   buttonCancel = " + buttonCancelText);
            logger.trace("   buttonExport = " + buttonExportText);
            logger.trace("   whenEmpty = " + whenEmpty);
        }
        
        // we register the values from the configuration file that are needed by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "width",             width);
        	setData(treeItem, "height",            height);
        	setData(treeItem, "spacing",           spacing);
        	setData(treeItem, "buttonWidth",       buttonWidth);
        	setData(treeItem, "buttonHeight",      buttonHeight);
        	setData(treeItem, "refers",            refers );
        	setData(treeItem, "variableSeparator", variableSeparator);
        	setData(treeItem, "whenEmpty",         whenEmpty);
        	setData(treeItem, "buttonOk",          buttonOkText);
        	setData(treeItem, "buttonCancel",      buttonCancelText);
        	setData(treeItem, "buttonExport",      buttonExportText);
        	setData(treeItem, "whenEmpty",         whenEmpty);
        }
        
        // width, height, spacing
        if ( width == null || width < 0 )
        	width = FormDialog.defaultDialogWidth;
        
        if ( height == null || height < 0 )
        	width = FormDialog.defaultDialogHeight;
        
        if ( spacing == null || spacing < 0 )
        	spacing = FormDialog.defaultDialogSpacing;
        
        // buttonWidth, buttonHeigh
        if ( buttonWidth == null || buttonWidth < 0 )
        	buttonWidth = FormDialog.defaultButtonWidth;
        
        if ( buttonHeight == null || buttonHeight < 0 )
        	buttonHeight = FormDialog.defaultButtonHeight;

        // resizing the shell
        form.setBounds((display.getPrimaryMonitor().getBounds().width-width)/2, (display.getPrimaryMonitor().getBounds().height-height)/2, width, height);
        form.setLayout(new FormLayout());
        
        // name
        if ( name != null )
			form.setText(name);			// may be replaced by FormVariable.expand(name, selectedObject) in calling method
        
        // creating the buttons
        Button cancelButton = new Button(form, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(100, -(buttonHeight+spacing));
        fd.left = new FormAttachment(100, -(buttonWidth+spacing));
        fd.right = new FormAttachment(100, -spacing);
        fd.bottom = new FormAttachment(100, -spacing);
        cancelButton.setLayoutData(fd);
        if ( buttonCancelText != null )
        	cancelButton.setText(buttonCancelText);
        else
        	cancelButton.setText(FormDialog.defaultButtonCancelText);
        form.setData("cancel button", cancelButton);			         // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        Button okButton = new Button(form, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(100, -(buttonHeight+spacing));
        fd.left = new FormAttachment(cancelButton, -(buttonWidth+spacing), SWT.LEFT);
        fd.right = new FormAttachment(cancelButton, -spacing);
        fd.bottom = new FormAttachment(100, -spacing);
        okButton.setLayoutData(fd);
        if ( buttonOkText != null )
        	okButton.setText(buttonOkText);
        else
        	okButton.setText(FormDialog.defaultButtonOkText);
        form.setData("ok button", okButton);							 // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        Button exportToExcelButton = new Button(form, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(100, -(buttonHeight+spacing));
        fd.left = new FormAttachment(okButton, -(buttonWidth+spacing), SWT.LEFT);
        fd.right = new FormAttachment(okButton, -spacing);
        fd.bottom = new FormAttachment(100, -spacing);
        exportToExcelButton.setLayoutData(fd);
        if ( buttonExportText != null )
        	exportToExcelButton.setText(buttonExportText);
        else
        	exportToExcelButton.setText(FormDialog.defaultButtonExportText);
        form.setData("export button", exportToExcelButton);			 // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        
        // we create the tab folder
        TabFolder tabFolder = new TabFolder(form, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(0, spacing);
        fd.left = new FormAttachment(0, spacing);
        fd.right = new FormAttachment(100, -spacing);
        fd.bottom = new FormAttachment(okButton, -spacing);
        tabFolder.setLayoutData(fd);
        tabFolder.setForeground(form.getForeground());
        tabFolder.setBackground(form.getBackground());
        
        form.setData("tab folder", tabFolder);					        // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        if ( treeItem != null )
        	treeItem.setData("class", "form");
    }
    
    /**
     * Parses the jsonObject and create the corresponding TabItem<br>
     * <br>
     * @param jsonObject the jsonObject to parse<br>
     * @param parent the parent TabFolder
     * 
     * @return the created Shell
     * 
     * @throws ClassCastException when a property does not belong to the right class in the jsonObject (i.e. a String is found while an Integer was expected)
     * @throws RuntimeException when a property has got an unexpected value (i.e. a negative value where a positive one was expected)
     */
    public TabItem createTab(JSONObject jsonObject, TabFolder parent, TreeItem treeItem) throws RuntimeException, ClassCastException {
        // we create the tab item
        TabItem tabItem = new TabItem(parent, SWT.MULTI);
        Composite composite = new Composite(parent, SWT.NONE);
        tabItem.setControl(composite);
        
        String  name = getName(jsonObject, composite, treeItem);
    	logger.debug("Creating tab : " + name);
    	FormPosition.setTabName(name);
        FormPosition.setControlClass("tab");
                
        getForegroundAndBackground(jsonObject, composite, treeItem);

    	// name
        if ( name != null )
        	tabItem.setText(name);						// may be replaced by FormVariable.expand(name, selectedObject) in calling method
        
        if ( treeItem != null )
        	treeItem.setData("class", "tab");

        return tabItem;
    }
    
    /**
     * Create a Label control
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public Label createLabel(JSONObject jsonObject, Composite parent, TreeItem treeItem) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        Label label = new Label(parent, SWT.WRAP);
        
        String  name = getName(jsonObject, label, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getXY(jsonObject, label, treeItem);
        getForegroundAndBackground(jsonObject, label, treeItem);
        getText(jsonObject, label, treeItem);
        getTooltip(jsonObject, label, treeItem);
        getFont(jsonObject, label, treeItem);
        getAlignment(jsonObject, label, treeItem);
        getExcelCellOrColumn(jsonObject, label, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "label");

        return label;
    }
    
    /**
     * Create a Label column
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableColumn createLabelColumn(JSONObject jsonObject, Table parent, TreeItem treeItem) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getWidth(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "labelColumn");

        return tableColumn;
    }

    /**
     * Create a text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public StyledText createText(JSONObject jsonObject, Composite parent, TreeItem treeItem) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        StyledText text = new StyledText(parent, SWT.WRAP | SWT.BORDER);
        
        String  name = getName(jsonObject, text, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getXY(jsonObject, text, treeItem);
        getVariable(jsonObject, text, treeItem);
        getForegroundAndBackground(jsonObject, text, treeItem);
        getTooltip(jsonObject, text, treeItem);
        getFont(jsonObject, text, treeItem);
        getAlignment(jsonObject, text, treeItem);
        getExcelCellOrColumn(jsonObject, text, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "text");

        return text;
    }
    
    /**
     * Create a text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableColumn createTextColumn(JSONObject jsonObject, Table parent, TreeItem treeItem) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getWidth(jsonObject, tableColumn, treeItem);
        getVariable(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "textColumn");

        return tableColumn;
    }

    /**
     * Create a Combo control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createCombo(JSONObject jsonObject, Composite parent, TreeItem treeItem) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo control");
        
        // we create the combo
    	CCombo combo = new CCombo(parent, SWT.NONE);
        
        String  name = getName(jsonObject, combo, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");

        getVariable(jsonObject, combo, treeItem);
        getValues(jsonObject, combo, treeItem);
        getXY(jsonObject, combo, treeItem);
        getForegroundAndBackground(jsonObject, combo, treeItem);
        getTooltip(jsonObject, combo, treeItem);
        getFont(jsonObject, combo, treeItem);
        getExcelCellOrColumn(jsonObject, combo, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "combo");

        return combo;
    }
    
    /**
     * Create a Combo Column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createComboColumn(JSONObject jsonObject, Table parent, TreeItem treeItem) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo column");
        
        // we create the combo
    	TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");
        
        getVariable(jsonObject, tableColumn, treeItem);
        getValues(jsonObject, tableColumn, treeItem);
        getWidth(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "comboColumn");
        
        return tableColumn;
    }
    
    /**
     * Create a check button control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createCheck(JSONObject jsonObject, Composite parent, TreeItem treeItem) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check control");
        
        // we create the combo
    	Button check = new Button(parent, SWT.CHECK);
        
        String  name = getName(jsonObject, check, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");

 	   	getValues(jsonObject, check, treeItem);
 	   	getVariable(jsonObject, check, treeItem);
   		getXY(jsonObject, check, treeItem);
   		getForegroundAndBackground(jsonObject, check, treeItem);
   		getAlignment(jsonObject, check, treeItem);
 	   	getTooltip(jsonObject, check, treeItem);
 	   	getExcelCellOrColumn(jsonObject, check, treeItem);
 	   	
        if ( treeItem != null )
        	treeItem.setData("class", "check");
 	   	
 	   	return check;
    }
    
    /**
     * Create a check button control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createCheckColumn(JSONObject jsonObject, Table parent, TreeItem treeItem) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check column");
        
        // we create the combo
    	TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");
        
 	   	getValues(jsonObject, tableColumn, treeItem);
 	   	getVariable(jsonObject, tableColumn, treeItem);
   		getWidth(jsonObject, tableColumn, treeItem);
   		getAlignment(jsonObject, tableColumn, treeItem);
 	   	getTooltip(jsonObject, tableColumn, treeItem);
 	   	getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
 	   	
        if ( treeItem != null )
        	treeItem.setData("class", "checkColumn");
 	   	
 	   	return tableColumn;
    }
	
    /*
    // we iterate over the "columns" entries
    Iterator<JSONObject> columnsIterator = (getJSONArray(jsonObject, "columns")).iterator();
    while (columnsIterator.hasNext()) {
        JSONObject column = columnsIterator.next();

        String columnName = getString(column, "name", "(no name)");
        FormPosition.setColumnName(columnName);
        
        String columnClass = getString(column, "class");
        String columnTooltip = getString(column, "tooltype", "");
        int columnWidth = getInt(column, "width", 0);
        String excelColumn = getString(column, "excelColumn", "");
        String excelCellType = getString(column, "excelCellType", "");
        String excelDefault = getString(column, "excelDefault", "");
        background = getString(column, "background", "");
        foreground = getString(column, "foreground", "");

        if (logger.isDebugEnabled())
            logger.debug("   Creating column \"" + columnName + "\" of class \"" + columnClass + "\"");
        if (logger.isTraceEnabled()) {
            logger.trace("      background = " + background, ""));
            logger.trace("      foreground = " + foreground, ""));
            logger.trace("      width = " + columnWidth, 0));
            logger.trace("      tooltip = " + columnTooltip, ""));
            logger.trace("      excelColumn = " + excelColumn, ""));
            logger.trace("      excelCellType = " + excelCellType, ""));
            logger.trace("      excelDefault = " + excelDefault, ""));
        }

        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setText(columnName);
        tableColumn.setAlignment(SWT.CENTER);
        tableColumn.setWidth(columnWidth);
        tableColumn.setResizable(columnWidth != 0);
        tableColumn.setData("class", columnClass);
        tableColumn.setData("excelColumn", excelColumn);
        tableColumn.setData("excelCellType", excelCellType.toLowerCase());
        tableColumn.setData("excelDefault", excelDefault.toLowerCase());
        tableColumn.setData("tooltip", tooltip);
        
        if ( columnWidth == 0 )
            columnWidth = (10 + columnName.length() * 8);
            
        if ( !FormPlugin.isEmpty((String)background) ) {
            String[] colorArray = background.split(",");
            tableColumn.setData("background", new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if ( !FormPlugin.isEmpty((String)foreground) ) {
            String[] colorArray = foreground.split(",");
            tableColumn.setData("foreground", new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }
        
        tableColumn.addListener(SWT.Selection, sortListener);
        
        String alignment;

        switch (columnClass.toLowerCase()) {
            case "check":
                if (JSONContainsKey(column, "values")) {
                    String[] values = (String[]) (getJSONArray(column, "values")).toArray(new String[0]);
                    String defaultValue = getString(column, "default", "");
                    boolean forceDefault = getBoolean(column, "forceDefault", false);
                    String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
                    alignment = getString(column, "alignment", "left");

                    if (logger.isTraceEnabled()) {
                        logger.trace("      values = " + values);
                        logger.trace("      default = " + defaultValue, ""));
                        logger.trace("      forceDefault = " + forceDefault, false));
                        logger.trace("      whenEmpty = " + whenEmpty, globalWhenEmpty));
                        logger.trace("      alignment = "+alignment, "left"));
                    }

                    if ( !FormPlugin.isEmpty((String)whenEmpty) ) {
                        whenEmpty = whenEmpty.toLowerCase();
                        if (!inArray(validWhenEmpty, whenEmpty))
                            throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
                    }

                    tableColumn.setData("values", values);
                    tableColumn.setData("default", defaultValue);
                    tableColumn.setData("forceDefault", forceDefault);
                    tableColumn.setData("whenEmpty", whenEmpty);
                    
                    switch ( alignment.toLowerCase() ) {
                        case "":
                        case "left" :
                            tableColumn.setData("alignment", SWT.LEFT);
                            break;
                        case "right" :
                            tableColumn.setData("alignment", SWT.RIGHT);
                            break;
                        case "center":
                            tableColumn.setData("alignment", SWT.CENTER);
                            break;
                        default:
                            throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
                    }
                } else {
                    tableColumn.setData("forceDefault", false);
                }
                break;
            case "combo":
                if (column.containsKey("values")) {
                    String[] values = (String[]) (getJSONArray(column, "values")).toArray(new String[0]);
                    String defaultValue = getString(column, "default", "");
                    Boolean editable = getBoolean(column, "editable", true);
                    String whenEmpty = getString(jsonObject, "whenEmpty", "");

                    if (logger.isTraceEnabled()) {
                        logger.trace("      values = " + values);
                        logger.trace("      default = " + defaultValue, ""));
                        logger.trace("      editable = " + editable, true));
                        logger.trace("      whenEmpty = " + whenEmpty, ""));
                    }

                    if ( !FormPlugin.isEmpty((String)whenEmpty)) {
                        whenEmpty = whenEmpty.toLowerCase();
                        if (!inArray(validWhenEmpty, whenEmpty))
                            throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
                    }

                    tableColumn.setData("values", values);
                    tableColumn.setData("default", defaultValue);
                    tableColumn.setData("editable", editable);
                    tableColumn.setData("whenEmpty", whenEmpty);
                } else {
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nMissing mandatory attribute \"values\".");
                }
                break;
            case "label":
                alignment = getString(column, "alignment", "left");
                
                if (logger.isTraceEnabled()) {
                    logger.trace("      alignment = "+alignment, "left"));
                }
                
                switch ( alignment.toLowerCase() ) {
                    case "right" :
                        tableColumn.setData("alignment", SWT.RIGHT);
                        break;
                    case "left" :
                        tableColumn.setData("alignment", SWT.LEFT);
                        break;
                    case "center":
                        tableColumn.setData("alignment", SWT.CENTER);
                        break;
                    default:
                        throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
                }
                break;
            case "text":
                String regexp = getString(column, "regexp", "");
                String defaultText = getString(column, "default", "");
                String whenEmpty = getString(column, "whenEmpty", "");
                boolean forceDefault = getBoolean(column, "forceDefault", false);
                alignment = getString(column, "alignment", "");

                if (logger.isTraceEnabled()) {
                    logger.trace("      regexp = " + regexp, ""));
                    logger.trace("      default = " + defaultText, ""));
                    logger.trace("      forceDefault = " + forceDefault, false));
                    logger.trace("      whenEmpty = " + whenEmpty, ""));
                    logger.trace("      alignment = "+alignment, ""));
                }

                if ( !FormPlugin.isEmpty((String)whenEmpty) ) {
                    whenEmpty = whenEmpty.toLowerCase();
                    if (!inArray(validWhenEmpty, whenEmpty))
                        throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
                }

                tableColumn.setData("regexp", regexp);
                tableColumn.setData("default", defaultText);
                tableColumn.setData("forceDefault", forceDefault);
                tableColumn.setData("whenEmpty", whenEmpty);
                
                switch ( alignment.toLowerCase() ) {
                    case "":
                    case "left" :
                        tableColumn.setData("alignment", SWT.LEFT);
                        break;
                    case "right" :
                        tableColumn.setData("alignment", SWT.RIGHT);
                        break;
                    case "center":
                        tableColumn.setData("alignment", SWT.CENTER);
                        break;
                    default:
                        throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
                }
                break;
            default:
                throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + getString(column, "class") + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
        }
    }
    FormPosition.resetColumnName();

    table.setSortColumn(table.getColumn(0));
    table.setSortDirection(SWT.UP);
    */
    
    /**
     * Create a table control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public Table createTable(JSONObject jsonObject, Composite parent, TreeItem treeItem) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table control");
    	
        Table table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        String name = getName(jsonObject, table, treeItem);
        FormPosition.setControlName(name);
        FormPosition.setControlClass("table");

        getXY(jsonObject, table, treeItem);
        getForegroundAndBackground(jsonObject, table, treeItem);
        getTooltip(jsonObject, table, treeItem);
        getExcelLines(jsonObject, table, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "table");

        return table;
    }
    
    /**
     * Create a table line<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableItem createLine(JSONObject jsonObject, Table parent, TreeItem treeItem) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table item");
    	
        TableItem tableItem = new TableItem(parent, SWT.NONE);
        
        String name = getName(jsonObject, tableItem, treeItem);
        FormPosition.setControlName(name);
        FormPosition.setControlClass("lines");
        
        getCells(jsonObject, tableItem, treeItem);
        getGenerate(jsonObject, tableItem, treeItem);
        getFilter(jsonObject, tableItem, treeItem);
        
        if ( treeItem != null )
        	treeItem.setData("class", "line");
        
        return tableItem;
    }
    
    /***************************************************************/
    
    private void getFilter(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem) {
    	JSONObject filter = getJSONObject(jsonObject, "filter");
    	List<Map<String, String>> tests = null;
    	
    	String genre = getString(filter, "genre");
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "genre", genre);
    	}
    	
    	// required by the form
    	if ( tableItem != null ) {
    		tableItem.setData("genre", genre);
    	}
    	
    	JSONArray testsJson = getJSONArray(filter, "tests");
    	if ( testsJson != null ) {
    		tests = new ArrayList<Map<String, String>>();
    		
            @SuppressWarnings("unchecked")
			Iterator<JSONObject> testIterator = testsJson.iterator();
            while (testIterator.hasNext()) {
            	JSONObject test = testIterator.next();
            	
            	Map<String, String> t = new HashMap<String, String>();
            	
            	t.put("attribute", getString(test, "attribute"));
            	t.put("operation", getString(test, "operation"));
            	t.put("value",     getString(test, "value"));
            	
            	tests.add(t);
            }

    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "tests", tests);
    	}
    	
    	// required by the form
    	if ( tableItem != null ) {
    		tableItem.setData("tests", tests);
    	}
    }
    
    private void getGenerate(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem) {
    	Boolean generate = getBoolean(jsonObject, "generate");
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "generate", generate);
    	}
    	
    	// required by the form
    	if ( tableItem != null ) {
    		tableItem.setData("generate", generate);
    	}
    }
    
    private void getCells(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem) {
    	Table table = tableItem.getParent();
    	JSONArray jsonCells = getJSONArray(jsonObject, "cells");
    	String[] cells = null;
    	
    	if ( jsonCells != null ) {
	    	cells = new String[table.getColumnCount()];
	    	TableEditor[] editors = new TableEditor[table.getColumnCount()];
	    	
	    	// we get the cells variables, completing if some are missing and ignoring if too many are present
	    	for ( int columnNumber = 0; columnNumber < table.getColumnCount(); ++columnNumber ) {
	    		if ( columnNumber < jsonCells.size() )
	    			cells[columnNumber] = (String)jsonCells.get(columnNumber);
	    		else
	    			cells[columnNumber] = "";
	    		
	    		// for each cell, we create the corresponding table editor
	    		TableEditor editor= new TableEditor(table);
	            editors[columnNumber] = editor;
	            
	    		switch ( (String)table.getColumn(columnNumber).getData("class") ) {
	                case "labelColumn":
	                    logger.trace("      adding label cell with value \"" + cells[columnNumber] + "\"");
	                    Label label = new Label(table, SWT.WRAP | SWT.NONE);
	                    label.setText(cells[columnNumber]);
	                    editor.setEditor(label, tableItem, columnNumber);
	                    editor.grabHorizontal = true;
	                    break;
	                    
	                case "textColumn":
	                    StyledText text = new StyledText(table, SWT.WRAP | SWT.NONE);
	                    logger.trace("      adding text cell with value \"" + cells[columnNumber] + "\"");
	                    text.setText(cells[columnNumber]);
	                    editor.setEditor(text, tableItem, columnNumber);
	                    editor.grabHorizontal = true;
	                    break;
	                    
	                case "comboColumn":
	                    CCombo combo = new CCombo(table, SWT.NONE);
	                    logger.trace("      adding combo cell with value \"" + cells[columnNumber] + "\"");
	                    combo.setText(cells[columnNumber]);
	                    combo.setItems((String[])table.getColumn(columnNumber).getData("values"));
	                    editor.setEditor(combo, tableItem, columnNumber);
	                    editor.grabHorizontal = true;
	                    break;
	                    
	                case "checkColumn":
	                    Button check = new Button(table, SWT.CHECK);
	                    logger.trace("      adding check cell");
	                    editor.minimumWidth = check.getSize().x;
	                    editor.horizontalAlignment = SWT.CENTER;
	                    break;
	                    
	                default:
	                    throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to add table item for unknown object class \"" + ((String)table.getColumn(columnNumber).getData("class")) + "\"");
	    		}
	    	}
	    	
	    	
    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "cells", cells);
    		
    	}
    	
    	// required by the form
    	if ( tableItem != null ) {
    		tableItem.setData("cells", cells);
    	}
    }
    
    
    /*
        // we iterate over the "lines" entries
        JSONArray lines = getJSONArray(jsonObject, "lines");
        if (lines != null) {
            Iterator<JSONObject> linesIterator = lines.iterator();
            while (linesIterator.hasNext()) {
                JSONObject line = linesIterator.next();

                if ((boolean) getJSON(line, "generate", false) == false) {
                    // static line
                    if (logger.isTraceEnabled())
                        logger.trace("Creating static line");
                    addTableItem(table, selectedObject, getJSONArray(line, "cells"));
                } else {
                    // dynamic lines : we create one line per entry in
                    // getChildren()
                    if (logger.isTraceEnabled())
                        logger.trace("Generating dynamic lines");
                    if (selectedObject instanceof IArchimateDiagramModel) {
                        addTableItems(table, ((IArchimateDiagramModel) selectedObject).getChildren(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                    } else {
                        if (selectedObject instanceof IDiagramModelContainer) {
                            addTableItems(table, ((IDiagramModelContainer) selectedObject).getChildren(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                        } else if (selectedObject instanceof IFolder) {
                            addTableItems(table, ((IFolder) selectedObject).getElements(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                        } else if (selectedObject instanceof IArchimateModel) {
                            for (IFolder folder : ((IArchimateModel) selectedObject).getFolders()) {
                                addTableItems(table, folder.getElements(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                            }
                        } else {
                            throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nCannot generate lines for selected component as it is not a container (" + selectedObject.getClass().getSimpleName() + ").");
                        }
                    }
                }
            }
        }
        return table;
    }
    */
    
    
    /*********************************************************************************************************************************/
    
    
    
    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj the JSONObject
     * @param key the key to search for (case insensitive)
     * @return the value corresponding to the key (the behaviour in case no value is found depends on the <i>ignoreErrors<i> flag). The returned value is an <i>object</i> and therefore might be cast.
     * @throws RuntimeException if the key is not found and the <i>ignoreErrors</i> flag has not been set)
     */
    public Object getJSON(JSONObject obj, String key) {
    	if ( obj == null )
    		return null;
    	
        @SuppressWarnings("unchecked")
		Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key)) {
                return obj.get(key1);
            }
        }
        return null;
    }
    
    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj the JSONObject
     * @param key the key to search for (case insensitive)
     * @param defaultValue the value to return in case the value is not found
     * @return the value corresponding to the key if it has been found, else the defaultValue. The returned value is an <i>object</i> and therefore might be cast.
     */
    /*
    @SuppressWarnings("unchecked")
	public static Object getJSON(JSONObject obj, String key, Object defaultValue) {
        Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key)) {
                return obj.get(key1);
            }
        }
        return defaultValue;
    }
    */

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @return the value if found, or the defaultValue provided if not found,
     *         ClassCastException if the object found is not a JSONObject
     */
    public JSONObject getJSONObject(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);
        
        if ( result == null )
        	return null;
        
        if ( !(result instanceof JSONObject) ) {
            FormPlugin.error("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a JSONObject.");
            return null;
        }
        
        return (JSONObject)result;
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @return the value if found, ClassCastException if the object found is not
     *         a JSONArray
     */
    public JSONArray getJSONArray(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);

        if ( result == null )
        	return null;
        
        if ( !(result instanceof JSONArray) ) {
            FormPlugin.error("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a JSONarray.");
            return null;
        }
        
        return (JSONArray) result;
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @return the value if found, ClassCastException if the object found is not
     *         a String
     */
    public String getString(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);
        
        if ( result == null )
        	return null;

        if ( !(result instanceof String) ) {
            FormPlugin.error("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a String.");
            return null;
        }
        
        return (String) result;
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @return the value if found, ClassCastException if the object found is not
     *         an Integer
     */
    public Integer getInt(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);
        
        if ( result == null )
        	return null;

        if ( !(result instanceof Long) ) {
            FormPlugin.error("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be an Integer.");
            return null;
        }
        
        return ((Long)result).intValue();
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, ClassCastException if the object found is not
     *         a boolean
     */
    public Boolean getBoolean(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);
        
        if ( result == null )
        	return null;

        if ( !(result instanceof Boolean) ) {
            FormPlugin.error("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a Boolean.");
            return null;
        }
        
        return (Boolean)result;
    }

    /**
     * Checks if the key exists in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     */
    public boolean JSONContainsKey(JSONObject obj, String key) {
        @SuppressWarnings("unchecked")
        Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }

    /***********************************************************************************************************/
    
    public boolean inArray(String[] stringArray, String string) {
    	if ( string == null )
    		return true;
    	
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
    
    /***********************************************************************************************************/
    
    private void getXY(JSONObject jsonObject, Control control, TreeItem treeItem) {
    	Integer x      = getInt(jsonObject, "x");
    	Integer y      = getInt(jsonObject, "y");
    	Integer width  = getInt(jsonObject, "width");
    	Integer height = getInt(jsonObject, "height");
    	
        if ( logger.isTraceEnabled() ) {
            logger.trace("      x = "      + x);
            logger.trace("      y = " 	   + y);
            logger.trace("      width = "  + width);
            logger.trace("      height = " + height);
        }
    	
        // required by graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "x", 	   x);
        	setData(treeItem, "y", 	   y); 
        	setData(treeItem, "width",  width);
        	setData(treeItem, "height", height);
        }

        // we set the widget position
        if ( control != null ) {
	    	Point p = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    	if ( x == null      || x < 0 )       x = 0;
	    	if ( y == null      || y < 0 )       y = 0;
	    	if ( width == null  || width <= 0  ) width = p.x;
	    	if ( height == null || height <= 0 ) height = p.y;
	    	control.setBounds(x, y, width, height);
        }
    }
    
    private void getWidth(JSONObject jsonObject, TableColumn tableColumn, TreeItem treeItem) {
    	Integer width  = getInt(jsonObject, "width");
    	
        if ( logger.isTraceEnabled() ) {
            logger.trace("      width = "  + width);
        }
    	
        // required by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "width",  width);
        }
        
        // we set the tableColumn width
        if ( tableColumn != null ) {
	    	if ( width == null  || width < 0  ) {
	    		if ( tableColumn.getData("name") == null )
	    			width = 50;
	    		else
	    			width = (10 + ((String)tableColumn.getData("name")).length() * 8);;
	    	}
	    	tableColumn.setWidth(width);
	    	tableColumn.setResizable(width != 0);
        }
    }
    
    private void getForegroundAndBackground(JSONObject jsonObject, Control control, TreeItem treeItem) {
    	String foreground = getString(jsonObject, "foreground");
    	String background = getString(jsonObject, "background");
    	
        if ( logger.isTraceEnabled() ) {
	        logger.trace("      foreground = "    + foreground);
	        logger.trace("      background = " 	  + background);
        }

        // required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "foreground",     foreground);
    		setData(treeItem, "background",     background);
    	}

    	// we set the control color
    	if ( control != null ) {
			FormPlugin.setColor(control, foreground, SWT.FOREGROUND);
			FormPlugin.setColor(control, background, SWT.BACKGROUND);
    	}
    }
    
    private void getAlignment(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String alignment = getString(jsonObject, "alignment");
    	
    	if ( logger.isTraceEnabled() ) {
    		logger.trace("      alignment = " + alignment);
    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "alignment", alignment);
    	}
    	
    	// we set the widget alignment
    	if ( widget != null ) {    	
    		FormPlugin.setAlignment(widget, alignment);
    	}
    }
    
    private void getVariable(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String  variable      = getString(jsonObject, "variable");
    	String  defaultText   = getString(jsonObject, "default");
    	Boolean forceDefault  = getBoolean(jsonObject, "forceDefault");
    	String  whenEmpty     = getString(jsonObject, "whenEmpty");
    	String  regex         = getString(jsonObject, "regexp");
        Boolean editable      = getBoolean(jsonObject, "editable");
        
		if ( logger.isTraceEnabled() ) {
			logger.trace("      variable = "	  + variable);
			logger.trace("      default = " 	  + defaultText);
			logger.trace("      forceDefault = "  + forceDefault);
			logger.trace("      whenEmpty = "     + whenEmpty);
			if ( widget != null && widget instanceof StyledText )
				logger.trace("      regexp = "    + regex);
			if ( widget != null && widget instanceof StyledText || widget instanceof CCombo )
				logger.trace("      editable = "  + editable);
		}
		
        // check whenEmpty value
        if ( !inArray(FormDialog.validWhenEmpty, whenEmpty))
        	FormPlugin.error(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \""+whenEmpty+"\" (valid values are "+FormPlugin.concat(FormDialog.validWhenEmpty, "\"", ",")+").");

        // required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "variable",     variable);
    		setData(treeItem, "default",      defaultText);
    		setData(treeItem, "forceDefault", forceDefault);
    		setData(treeItem, "whenEmpty",    whenEmpty);
	    	if ( widget != null && widget instanceof StyledText )
	    		setData(treeItem, "regexp",    whenEmpty);
	    	if ( widget != null && widget instanceof StyledText || widget instanceof CCombo )
	    		setData(treeItem, "editable",  whenEmpty);
    	}

    	// required by the form
		if ( widget != null ) {
			widget.setData("variable", variable);
			widget.setData("default", defaultText);
			widget.setData("forceDefault", forceDefault);
			widget.setData("whenEmpty",    whenEmpty);
			if ( widget instanceof StyledText )
				widget.setData("regexp",    whenEmpty);
			
	        // editable
	        if ( widget instanceof StyledText && editable != null )
	        	((StyledText)widget).setEditable(editable);
	        if ( widget instanceof CCombo && editable != null )
	        	((CCombo)widget).setEditable(editable);
			
			// we set a default text content for the graphical editor. Real form will replace this text with the variable content.
			switch ( widget.getClass().getSimpleName() ) {
				case "StyledText": ((StyledText)widget).setText(variable); break;
				case "CCombo":     ((CCombo)widget).setText(variable); break;
				case "Button":     ((Button)widget).setText(variable); break;
			}
		}
    }
    
    private void getExcelCellOrColumn(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String excelSheet    = getString(jsonObject, "excelSheet");
    	String excelCell     = getString(jsonObject, "excelCell");
    	String excelColumn   = getString(jsonObject, "excelColumn");
    	String excelCellType = getString(jsonObject, "excelCellType");
    	String excelDefault  = getString(jsonObject, "excelDefault");
    	
        if ( logger.isTraceEnabled() ) {
        	if ( widget != null && widget instanceof TableColumn )			// when TableColumn (excelSheet is referenced in the Table)
        		logger.trace("      excelColumn = " + excelCell);
        	else {															// when Text, CCombo or Button
            	logger.trace("      excelSheet = "  + excelSheet);
        		logger.trace("      excelCell = " 	+ excelCell);
        	}
        	logger.trace("      excelCellType = "   + excelCellType);
        	logger.trace("      excelDefault = "    + excelDefault);
        }
        
        // checking excelCellType value
        if ( !inArray(FormDialog.validExcelCellType, excelCellType))
       		FormPlugin.error(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value \""+excelCellType+"\" (valid values are "+FormPlugin.concat(FormDialog.validExcelCellType, "\"", ",")+").");
        
        // checking excelDefault value
        if ( !inArray(FormDialog.validExcelDefault, excelDefault))
        	FormPlugin.error(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value \""+excelDefault+"\" (valid values are "+FormPlugin.concat(FormDialog.validExcelDefault, "\"", ",")+").");
        
        // required by the graphical editor
        if ( treeItem != null ) {
        	if ( widget != null && widget instanceof TableColumn ) {		// when TableColumn (excelSheet is referenced in the Table)
        		setData(treeItem, "excelColumn",    excelColumn);
        	} else {														// when Text, CCombo or Button
        		setData(treeItem, "excelSheet",     excelSheet);
        		setData(treeItem, "excelCell",      excelCell);
        	}
        	setData(treeItem, "excelCellType",  excelCellType);
           	setData(treeItem, "excelDefault",   excelDefault);
        }
        
        // required by the form
        if ( widget != null ) {
        	if ( widget instanceof TableColumn ) {		// when TableColumn (excelSheet is referenced in the Table)
        		widget.setData("excelColumn",    excelColumn);
        	} else {														// when Text, CCombo or Button
        		widget.setData("excelSheet",     excelSheet);
        		widget.setData("excelCell",      excelCell);
        	}
        	widget.setData("excelCellType",  excelCellType);
        	widget.setData("excelDefault",   excelDefault);
        }
    }
    
    private void getExcelLines(JSONObject jsonObject, Table table, TreeItem treeItem) {
    	String  excelSheet     = getString(jsonObject, "excelSheet");
    	Integer excelFirstLine = getInt(jsonObject, "excelFirstLine");
    	Integer excelLastLine  = getInt(jsonObject, "excelLastLine");
    	
    	if ( logger.isTraceEnabled() ) {
    		logger.trace("      excelSheet = "     + excelSheet);
    		logger.trace("      excelFirstLine = " + excelFirstLine);
    		logger.trace("      excelLastLine = "  + excelLastLine);
    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
	    	setData(treeItem, "excelSheet",     excelSheet);
	    	setData(treeItem, "excelFirstLine", excelFirstLine);
	    	setData(treeItem, "excelLastLine",  excelLastLine);
    	}
    	
    	// required by the form
    	if ( table != null ) {
    		table.setData("excelSheet",     excelSheet);
    		table.setData("excelFirstLine", excelFirstLine);
    		table.setData("excelLastLine",  excelLastLine);
    	}
    }
    
    @SuppressWarnings("unchecked")
	private void getValues(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	JSONArray jsonValues = getJSONArray(jsonObject, "values");
    	String[] values = null;
    	
        if ( logger.isTraceEnabled() )
        	logger.trace("      values = " + (jsonValues==null ? null : jsonValues.toArray(new String[0])));
    	
        
    	if ( jsonValues != null ) {
    		values = (String[])jsonValues.toArray(new String[0]);
    	}
    	
		// required by the graphical editor
		if ( treeItem != null ) {
			setData(treeItem, "values", values);
		}
		
		// required by the form
		if ( widget != null && widget instanceof TableColumn ) {
			widget.setData("values", values);
		}
		
		// we set the combo items
		if ( widget != null && FormPlugin.areEqual(widget.getClass().getSimpleName(), "CCombo") ) {
           		((CCombo)widget).setItems(values);
		}
    }
    
    private void getTooltip(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String tooltip = getString(jsonObject, "tooltip");
    	
        if ( logger.isTraceEnabled() ) {
        	logger.trace("      tooltip = " + tooltip);
        }
        
    	// required by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "tooltip", tooltip);
        }
    	
        // we set the tooltip text
        if ( widget != null && tooltip != null) {
	        if (  widget instanceof Control )
	        	((Control)widget).setToolTipText(tooltip);
	        if ( widget instanceof TableColumn )
	        	((TableColumn)widget).setToolTipText(tooltip);
        }
    }
    
    private String getName(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String name = getString(jsonObject, "name");
    	
        if ( logger.isTraceEnabled() ) {
        	logger.trace("      name = " + name);
        }
        
    	// required by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "name", name);
        	if ( name != null ) 
        		treeItem.setText(name);
        }
    	
        // we set the column text to its name
        if ( widget != null && name != null ) {
	        if ( widget instanceof TableColumn )
	        	((TableColumn)widget).setText(name);
        }
        
        return name;
    }

	private void getText(JSONObject jsonObject, Label label, TreeItem treeItem) {
		String text = getString(jsonObject, "text");
		
	    if ( logger.isTraceEnabled() ) {
	    	logger.trace("      text = " + text);
	    }
	    
	    // required by the graphical editor
		if ( treeItem != null ) {
			setData(treeItem, "text", text);
		}
	    
		// we set the label's text
	    if ( label != null && text != null )
	    	label.setText(text);
	}
    
    private void getFont(JSONObject jsonObject, Control control, TreeItem treeItem) {
    	String  fontName = getString(jsonObject, "fontName");
    	Integer fontSize = getInt(jsonObject, "fontSize");
    	Boolean fontBold = getBoolean(jsonObject, "fontBold");
    	Boolean fontItalic = getBoolean(jsonObject, "fontItalic");
    	
    	if ( logger.isTraceEnabled() ) {
	        logger.trace("      fontName = " + fontName);
	        logger.trace("      fontSize = " + fontSize);
	        logger.trace("      fontBold = " + fontBold);
	        logger.trace("      fontItalic = " + fontItalic);
    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "fontName", fontName);
    		setData(treeItem, "fontSize", fontSize);
    		setData(treeItem, "fontBold", fontBold);
    		setData(treeItem, "fontItalic", fontItalic);
    	}
    	
    	// we set the control's font
		FormPlugin.setFont(control, fontName, fontSize, fontBold, fontItalic);
    }
    
    private void setData(TreeItem treeItem, String key, Object value) {
    	if ( treeItem == null || key == null)
    		return;
    	
        addKey(treeItem, key);        
        treeItem.setData(key, value);
    }
    
    private void addKey(TreeItem treeItem, String key) {
    	if ( treeItem == null || key == null)
    		return;
    	
        @SuppressWarnings("unchecked")
		Set<String> keys = (Set<String>)treeItem.getData("editable keys");
        if ( keys == null ) {
        	keys = new HashSet<String>();
            treeItem.setData("editable keys", keys);
        }
        keys.add(key);
    }
    
    /***********************************************************************************************************/
    @SuppressWarnings("unchecked")
	public JSONObject generateJson(Tree tree) {
    	JSONObject json = new JSONObject();
    	
    	json.put("version", 2);
    	json.put("org.archicontribs.form", generateJson(tree.getItem(0)));
    	
    	return json;
    }
    	
    
    @SuppressWarnings("unchecked")
	private JSONObject generateJson(TreeItem treeItem) {
    	JSONObject json = new JSONObject();
    	
    	for ( String key: (Set<String>)treeItem.getData("editable keys") ) {
    		if ( treeItem.getData(key) != null ) {
    			switch ( key ) {
    				case "values":
    					JSONArray values = new JSONArray();
    					for ( String value: (String[])treeItem.getData(key) )
    						values.add(value);
    					json.put("values", values);
    					break;
    				case "cells":
    					if ( treeItem.getData(key) != null ) {
    						JSONArray cells = new JSONArray();
    						for ( String cell:(String[])treeItem.getData(key))
    							cells.add(cell);
    						json.put("cells", cells);
    					}
    					break;
    				default:
    					json.put(key, treeItem.getData(key));
    			}
    				
    		}
    	}
    	
    	switch ( (String)treeItem.getData("class") ) {
    		case "form":
    			JSONArray tabs = new JSONArray();
    			json.put("tabs", tabs);
    			
    			for ( TreeItem tab: treeItem.getItems() )
    				tabs.add(generateJson(tab));
    				
    			break;
    			
    		case "tab":
    			JSONArray controls = new JSONArray();
    			json.put("controls", controls);
    			
    			for ( TreeItem control: treeItem.getItems() )
    				controls.add(generateJson(control));
    				
    			break;
    			
    		case "table":
    			for ( TreeItem childTreeItem: treeItem.getItems() ) {
    				switch ( childTreeItem.getText() ) {
    					case "columns":
    		    			JSONArray columns = new JSONArray();
    		    			json.put("columns", columns);
    		    			
    		    			for ( TreeItem column: childTreeItem.getItems() )
    		    				columns.add(generateJson(column));
    		    			
    		    			break;
    		    			
    					case "lines":
    		    			JSONArray lines = new JSONArray();
    		    			json.put("lines", lines);
    		    			
    		    			for ( TreeItem line: childTreeItem.getItems() )
    		    				lines.add(generateJson(line));
    		    				
    		    			break;
    		    			
    		    		default:
    		    			throw new RuntimeException("Do not know table item "+childTreeItem.getText());
    				}
    			}
    			break;
    	}
    	
    	return json;
    }
}
