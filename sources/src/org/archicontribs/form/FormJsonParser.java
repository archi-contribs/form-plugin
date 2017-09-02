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
    public Shell createShell(JSONObject jsonObject, Shell parent) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating form");
        
        // we create the shell
        Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        
        //TODO: use helper functions
    	
        String  name              = getName(jsonObject, shell); FormPosition.setFormName(name);
        Integer width             = getInt(jsonObject, "width");
        Integer height            = getInt(jsonObject, "height");
        Integer spacing           = getInt(jsonObject, "spacing");
        Integer buttonWidth       = getInt(jsonObject, "buttonWidth");
        Integer buttonHeight      = getInt(jsonObject, "buttonHeight");
        String  refers            = getString(jsonObject, "refers");
        String  variableSeparator = getString(jsonObject, "variableSeparator");
        String  whenEmpty         = getString(jsonObject, "whenEmpty");
        getForegroundAndBackground(jsonObject, shell);
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
        shell.setData("name",              name);				addKey(shell, "name");
        shell.setData("width",             width);				addKey(shell, "width");
        shell.setData("height",            height);				addKey(shell, "height");
        shell.setData("spacing",           spacing);			addKey(shell, "spacing");
        shell.setData("buttonWidth",       buttonWidth);		addKey(shell, "buttonWidth");
        shell.setData("buttonHeight",      buttonHeight);		addKey(shell, "buttonHeight");
        shell.setData("refers",            refers );			addKey(shell, "refers");
        shell.setData("variableSeparator", variableSeparator);	addKey(shell, "variableSeparator");
        shell.setData("whenEmpty",         whenEmpty);			addKey(shell, "whenEmpty");
        shell.setData("buttonOk",          buttonOkText);		addKey(shell, "buttonOk");
        shell.setData("buttonCancel",      buttonCancelText);	addKey(shell, "buttonCancel");
        shell.setData("buttonExport",      buttonExportText);	addKey(shell, "buttonExport");
        shell.setData("whenEmpty",         whenEmpty);			addKey(shell, "whenEmpty");
        
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
        shell.setBounds((display.getPrimaryMonitor().getBounds().width-width)/2, (display.getPrimaryMonitor().getBounds().height-height)/2, width, height);
        shell.setLayout(new FormLayout());
        
        // name
        if ( name != null )
			shell.setText(name);			// may be replaced by FormVariable.expand(name, selectedObject) in calling method
        
        // creating the buttons
        Button cancelButton = new Button(shell, SWT.NONE);
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
        shell.setData("cancel button", cancelButton);			         // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        Button okButton = new Button(shell, SWT.NONE);
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
        shell.setData("ok button", okButton);							 // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        Button exportToExcelButton = new Button(shell, SWT.NONE);
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
        shell.setData("export button", exportToExcelButton);			 // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        
        // we create the tab folder
        TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(0, spacing);
        fd.left = new FormAttachment(0, spacing);
        fd.right = new FormAttachment(100, -spacing);
        fd.bottom = new FormAttachment(okButton, -spacing);
        tabFolder.setLayoutData(fd);
        tabFolder.setForeground(shell.getForeground());
        tabFolder.setBackground(shell.getBackground());
        
        shell.setData("tab folder", tabFolder);					        // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file

        return shell;
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
    public TabItem createTab(JSONObject jsonObject, TabFolder parent) throws RuntimeException, ClassCastException {
        // we create the tab item
        TabItem tabItem = new TabItem(parent, SWT.MULTI);
        Composite composite = new Composite(parent, SWT.NONE);
        tabItem.setControl(composite);
        
        String  name = getName(jsonObject, composite);
    	logger.debug("Creating tab : " + name);
    	FormPosition.setTabName(name);
        FormPosition.setControlClass("tab");
                
        getForegroundAndBackground(jsonObject, composite);

    	// name
        if ( name != null )
        	tabItem.setText(name);						// may be replaced by FormVariable.expand(name, selectedObject) in calling method


        return tabItem;
    }
    
    /**
     * Create a Label control
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public Label createLabel(JSONObject jsonObject, Composite parent) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        Label label = new Label(parent, SWT.WRAP);
        
        String  name = getName(jsonObject, label);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getXY(jsonObject, label);
        getForegroundAndBackground(jsonObject, label);
        getText(jsonObject, label);
        getTooltip(jsonObject, label);
        getFont(jsonObject, label);
        getAlignment(jsonObject, label);
        getExcelCellOrColumn(jsonObject, label);

        return label;
    }
    
    /**
     * Create a Label column
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableColumn createLabelColumn(JSONObject jsonObject, Table parent) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getWidth(jsonObject, tableColumn);
        getTooltip(jsonObject, tableColumn);
        getAlignment(jsonObject, tableColumn);
        getExcelCellOrColumn(jsonObject, tableColumn);

        return tableColumn;
    }

    /**
     * Create a text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public StyledText createText(JSONObject jsonObject, Composite parent) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        StyledText text = new StyledText(parent, SWT.WRAP | SWT.BORDER);
        
        String  name = getName(jsonObject, text);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getXY(jsonObject, text);
        getVariable(jsonObject, text);
        getForegroundAndBackground(jsonObject, text);
        getTooltip(jsonObject, text);
        getFont(jsonObject, text);
        getAlignment(jsonObject, text);
        getExcelCellOrColumn(jsonObject, text);

        return text;
    }
    
    /**
     * Create a text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableColumn createTextColumn(JSONObject jsonObject, Table parent) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getWidth(jsonObject, tableColumn);
        getVariable(jsonObject, tableColumn);
        getTooltip(jsonObject, tableColumn);
        getAlignment(jsonObject, tableColumn);
        getExcelCellOrColumn(jsonObject, tableColumn);

        return tableColumn;
    }

    /**
     * Create a Combo control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createCombo(JSONObject jsonObject, Composite parent) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo control");
        
        // we create the combo
    	CCombo combo = new CCombo(parent, SWT.NONE);
        
        String  name = getName(jsonObject, combo);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");

        getVariable(jsonObject, combo);
        getValues(jsonObject, combo);
        getXY(jsonObject, combo);
        getForegroundAndBackground(jsonObject, combo);
        getTooltip(jsonObject, combo);
        getFont(jsonObject, combo);
        getExcelCellOrColumn(jsonObject, combo);

        return combo;
    }
    
    /**
     * Create a Combo Column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createComboColumn(JSONObject jsonObject, Table parent) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo column");
        
        // we create the combo
    	TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");
        
        getVariable(jsonObject, tableColumn);
        getValues(jsonObject, tableColumn);
        getWidth(jsonObject, tableColumn);
        getTooltip(jsonObject, tableColumn);
        getExcelCellOrColumn(jsonObject, tableColumn);
        
        return tableColumn;
    }
    
    /**
     * Create a check button control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createCheck(JSONObject jsonObject, Composite parent) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check control");
        
        // we create the combo
    	Button check = new Button(parent, SWT.CHECK);
        
        String  name = getName(jsonObject, check);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");

 	   	getValues(jsonObject, check);
 	   	getVariable(jsonObject, check);
   		getXY(jsonObject, check);
   		getForegroundAndBackground(jsonObject, check);
   		getAlignment(jsonObject, check);
 	   	getTooltip(jsonObject, check);
 	   	getExcelCellOrColumn(jsonObject, check);
 	   	
 	   	return check;
    }
    
    /**
     * Create a check button control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Widget createCheckColumn(JSONObject jsonObject, Table parent) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check column");
        
        // we create the combo
    	TableColumn tableColumn = new TableColumn(parent, SWT.NONE);
        
        String  name = getName(jsonObject, tableColumn);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");
        
 	   	getValues(jsonObject, tableColumn);
 	   	getVariable(jsonObject, tableColumn);
   		getWidth(jsonObject, tableColumn);
   		getAlignment(jsonObject, tableColumn);
 	   	getTooltip(jsonObject, tableColumn);
 	   	getExcelCellOrColumn(jsonObject, tableColumn);
 	   	
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
    public Table createTable(JSONObject jsonObject, Composite parent) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table control");
    	
        Table table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        String name = getName(jsonObject, table);
        FormPosition.setControlName(name);
        FormPosition.setControlClass("table");

        getXY(jsonObject, table);
        getForegroundAndBackground(jsonObject, table);
        getTooltip(jsonObject, table);
        getExcelLines(jsonObject, table);

        return table;
    }
    
    /**
     * Create a table line<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableItem createLine(JSONObject jsonObject, Table parent) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table item");
    	
        TableItem tableItem = new TableItem(parent, SWT.NONE);
        
        String name = getName(jsonObject, tableItem);
        FormPosition.setControlName(name);
        FormPosition.setControlClass("lines");
        
        getCells(jsonObject, tableItem);
        getGenerate(jsonObject, tableItem);
        getFilter(jsonObject, tableItem);
        return tableItem;
    }
    
    private void getFilter(JSONObject jsonObject, TableItem tableItem) {
    	JSONObject filter = getJSONObject(jsonObject, "filter");
    	
    	String genre = getString(filter, "genre");
    	tableItem.setData("genre", genre);
    	addKey(tableItem, "genre");
    	
    	JSONArray testsJson = getJSONArray(filter, "tests");
    	if ( testsJson != null ) {
    		List<Map<String, String>> tests = new ArrayList<Map<String, String>>();
    		
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
        	tableItem.setData("tests", tests);
    	}
    	
    	addKey(tableItem, "tests");
    }
    
    private void getGenerate(JSONObject jsonObject, TableItem tableItem) {
    	Boolean generate = getBoolean(jsonObject, "generate");
    	
    	tableItem.setData("generate", generate);
    	addKey(tableItem, "generate");
    }
    
    private void getCells(JSONObject jsonObject, TableItem tableItem) {
    	Table table = tableItem.getParent();
    	JSONArray jsonCells = getJSONArray(jsonObject, "cells");
    	
    	if ( jsonCells != null ) {
	    	String[] cells = new String[table.getColumnCount()];
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
	    	
	    	tableItem.setData("cells", cells);
    	}
    	
    	addKey(tableItem, "cells");
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
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
    
    /***********************************************************************************************************/
    
    private void getXY(JSONObject jsonObject, Control control) {
    	Integer x      = getInt(jsonObject, "x");
    	Integer y      = getInt(jsonObject, "y");
    	Integer width  = getInt(jsonObject, "width");
    	Integer height = getInt(jsonObject, "height");
    	
    	control.setData("x", 	  x);         addKey(control, "x");
    	control.setData("y", 	  y);         addKey(control, "y");
    	control.setData("width",  width);     addKey(control, "width");
    	control.setData("height", height);    addKey(control, "height");
        
        if ( logger.isTraceEnabled() ) {
            logger.trace("      x = "      + x);
            logger.trace("      y = " 	   + y);
            logger.trace("      width = "  + width);
            logger.trace("      height = " + height);
        }
        
    	Point p = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    	if ( x == null      || x < 0 )       x = 0;
    	if ( y == null      || y < 0 )       y = 0;
    	if ( width == null  || width <= 0  ) width = p.x;
    	if ( height == null || height <= 0 ) height = p.y;
    	control.setBounds(x, y, width, height);
    }
    
    private void getWidth(JSONObject jsonObject, TableColumn tableColumn) {
    	Integer width  = getInt(jsonObject, "width");
    	
    	tableColumn.setData("width",  width);     addKey(tableColumn, "width");
        
        if ( logger.isTraceEnabled() ) {
            logger.trace("      width = "  + width);
        }
        
    	if ( width == null  || width < 0  ) {
    		if ( tableColumn.getData("name") == null )
    			width = 50;
    		else
    			width = (10 + ((String)tableColumn.getData("name")).length() * 8);;
    	}
    	tableColumn.setWidth(width);
    	tableColumn.setResizable(width != 0);
    }
    
    private void getForegroundAndBackground(JSONObject jsonObject, Control control) {
    	String foreground = getString(jsonObject, "foreground");
    	String background = getString(jsonObject, "background");

    	control.setData("foreground",     foreground);     addKey(control, "foreground");
    	control.setData("background",     background);     addKey(control, "background");

        if ( logger.isTraceEnabled() ) {
	        logger.trace("      foreground = "    + foreground);
	        logger.trace("      background = " 	  + background);
        }
        
		FormPlugin.setColor(control, foreground, SWT.FOREGROUND);
		FormPlugin.setColor(control, background, SWT.BACKGROUND);
    }
    
    private void getAlignment(JSONObject jsonObject, Widget widget) {
    	String alignment = getString(jsonObject, "alignment");
    	
    	widget.setData("alignment", alignment);
    	addKey(widget, "alignment");
    	
    	if ( logger.isTraceEnabled() )
    		logger.trace("      alignment = " + alignment);
    	
		FormPlugin.setAlignment(widget, alignment);
    }
    
    private void getVariable(JSONObject jsonObject, Widget widget) {
    	String  variable      = getString(jsonObject, "variable");
    	String  defaultText   = getString(jsonObject, "default");
    	Boolean forceDefault  = getBoolean(jsonObject, "forceDefault");
    	String  whenEmpty     = getString(jsonObject, "whenEmpty");
    	String  regex         = getString(jsonObject, "regexp");
        Boolean editable      = getBoolean(jsonObject, "editable");

    	
    	widget.setData("variable",     variable);	    addKey(widget, "variable");
    	widget.setData("default",      defaultText);	addKey(widget, "default");
    	widget.setData("forceDefault", forceDefault);	addKey(widget, "forceDefault");
    	widget.setData("whenEmpty",    whenEmpty);      addKey(widget, "whenEmpty");
    	if ( widget instanceof StyledText ) {
    		widget.setData("regexp",    whenEmpty);      addKey(widget, "regexp");
    	}
    	if ( widget instanceof StyledText || widget instanceof CCombo ) {
    		widget.setData("editable",  whenEmpty);      addKey(widget, "editable");
    	}
			
		if ( logger.isTraceEnabled() ) {
			logger.trace("      variable = "	  + variable);
			logger.trace("      default = " 	  + defaultText);
			logger.trace("      forceDefault = "  + forceDefault);
			logger.trace("      whenEmpty = "     + whenEmpty);
			if ( widget instanceof StyledText )
				logger.trace("      regexp = "    + regex);
			if ( widget instanceof StyledText || widget instanceof CCombo )
				logger.trace("      editable = "  + editable);
		}
		
		switch ( widget.getClass().getSimpleName() ) {
			case "StyledText": ((StyledText)widget).setText(variable); break;
			case "CCombo":     ((CCombo)widget).setText(variable); break;
			case "Button":     ((Button)widget).setText(variable); break;
		}
		
        // WhenEmpty
        if ( !FormPlugin.isEmpty((String)widget.getData("whenEmpty")) && !inArray(FormDialog.validWhenEmpty, (String)widget.getData("whenEmpty")))
        	FormPlugin.error(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \""+(String)widget.getData("whenEmpty")+"\" (valid values are "+FormDialog.validWhenEmpty+").");
    }
    
    private void getExcelCellOrColumn(JSONObject jsonObject, Widget widget) {
    	String excelSheet    = getString(jsonObject, "excelSheet");
    	String excelCell     = getString(jsonObject, "excelCell");
    	String excelColumn   = getString(jsonObject, "excelColumn");
    	String excelCellType = getString(jsonObject, "excelCellType");
    	String excelDefault  = getString(jsonObject, "excelDefault");
        
    	if ( widget instanceof Control ) {								// when Text, CCombo or Button
    		widget.setData("excelSheet",     excelSheet);	addKey(widget, "excelSheet");
    		widget.setData("excelCell",      excelCell);	addKey(widget, "excelCell");
    	}
    	else {															// when TableColumn (excelSheet is referenced in the Table)
    		widget.setData("excelColumn",    excelColumn);	addKey(widget, "excelColumn");
    	}
    	widget.setData("excelCellType",  excelCellType);	addKey(widget, "excelCellType");
    	widget.setData("excelDefault",   excelDefault);	    addKey(widget, "excelDefault");
        
        if ( logger.isTraceEnabled() ) {
        	logger.trace("      excelSheet = " 	    + excelSheet);
        	if ( widget instanceof Control )
        		logger.trace("      excelCell = " 	+ excelCell);
        	else
        		logger.trace("      excelColumn = " + excelCell);
        	logger.trace("      excelCellType = "   + excelCellType);
        	logger.trace("      excelDefault = "    + excelDefault);
        }
        
        if ( !FormPlugin.isEmpty(excelCellType) && !inArray(FormDialog.validExcelCellType, excelCellType))
       		FormPlugin.error(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value \""+excelCellType+"\" (valid values are "+FormDialog.validExcelCellType+").");
        
        if ( !FormPlugin.isEmpty(excelDefault) && !inArray(FormDialog.validExcelDefault, excelDefault))
        	FormPlugin.error(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value \""+excelDefault+"\" (valid values are "+FormDialog.validExcelDefault+").");
    }
    
    private void getExcelLines(JSONObject jsonObject, Table table) {
    	String  excelSheet     = getString(jsonObject, "excelSheet");
    	Integer excelFirstLine = getInt(jsonObject, "excelFirstLine");
    	Integer excelLastLine  = getInt(jsonObject, "excelLastLine");
    	
    	table.setData("excelSheet",     excelSheet);		addKey(table, "excelSheet");
    	table.setData("excelFirstLine", excelFirstLine);    addKey(table, "excelFirstLine");
    	table.setData("excelLastLine",  excelLastLine);     addKey(table, "excelLastLine");

    	if ( logger.isTraceEnabled() ) {
    		logger.trace("      excelSheet = "     + excelSheet);
    		logger.trace("      excelFirstLine = " + excelFirstLine);
    		logger.trace("      excelLastLine = "  + excelLastLine);
    	}
    }
    
    @SuppressWarnings("unchecked")
	private void getValues(JSONObject jsonObject, Widget widget) {
    	JSONArray jsonValues = getJSONArray(jsonObject, "values");
    	
    	if ( jsonValues != null ) {
    		String[] values = (String[])jsonValues.toArray(new String[0]);
           	widget.setData("values", jsonValues==null ? null : values);
           	
           	if ( FormPlugin.areEqual(widget.getClass().getSimpleName(), "CCombo") )
           		((CCombo)widget).setItems(values);
    	}
    	
    	addKey(widget, "values");
        
        if ( logger.isTraceEnabled() )
        	logger.trace("      values = " + (jsonValues==null ? null : jsonValues.toArray(new String[0])));
    }
    
    private void getTooltip(JSONObject jsonObject, Widget widget) {
    	String tooltip = getString(jsonObject, "tooltip");
        
    	widget.setData("tooltip", tooltip);
    	addKey(widget, "tooltip");
        
        if ( logger.isTraceEnabled() ) {
        	logger.trace("      tooltip = " + tooltip);
        }
        
        if ( tooltip != null &&  widget instanceof Control )
        	((Control)widget).setToolTipText(tooltip);
    }
    
    private String getName(JSONObject jsonObject, Widget widget) {
    	String name = getString(jsonObject, "name");
        
    	widget.setData("name", name);
    	addKey(widget, "name");
        
        if ( logger.isTraceEnabled() ) {
        	logger.trace("      name = " + name);
        }
        
        if ( name != null && FormPlugin.areEqual(widget.getClass().getSimpleName(), "TableColumn") )
        	((TableColumn)widget).setText(name);

        return name;
    }

	private void getText(JSONObject jsonObject, Label label) {
		String text = getString(jsonObject, "text");
	    
		label.setData("text", text);
		addKey(label, "text");
	    
	    if ( logger.isTraceEnabled() ) {
	    	logger.trace("      text = " + text);
	    }
	    
	    if ( text != null )
	    	label.setText(text);
	}
    
    private void getFont(JSONObject jsonObject, Control control) {
    	String  fontName = getString(jsonObject, "fontName");
    	Integer fontSize = getInt(jsonObject, "fontSize");
    	Boolean fontBold = getBoolean(jsonObject, "fontBold");
    	Boolean fontItalic = getBoolean(jsonObject, "fontItalic");
    	
    	control.setData("fontName", fontName);		addKey(control, "fontName");
    	control.setData("fontSize", fontSize);		addKey(control, "fontSize");
    	control.setData("fontBold", fontBold);		addKey(control, "fontBold");
    	control.setData("fontItalic", fontItalic);	addKey(control, "fontItalic");
    	
    	if ( logger.isTraceEnabled() ) {
	        logger.trace("      fontName = " + fontName);
	        logger.trace("      fontSize = " + fontSize);
	        logger.trace("      fontBold = " + fontBold);
	        logger.trace("      fontItalic = " + fontItalic);
    	}
    	
		FormPlugin.setFont(control, fontName, fontSize, fontBold, fontItalic);
    }
    
    private void addKey(Widget widget, String key) {
        @SuppressWarnings("unchecked")
		Set<String> keys = (Set<String>)widget.getData("editable keys");
        if ( keys == null ) {
        	keys = new HashSet<String>();
            widget.setData("editable keys", keys);
        }
        keys.add(key);
    }
    
    /***********************************************************************************************************/
    @SuppressWarnings("unchecked")
	public JSONObject generateJSON(TreeItem formTreeItem, Shell form) {
    	JSONObject resultJson = new JSONObject();
    	JSONObject formJson = new JSONObject();
    	
    	resultJson.put("version", 2);
    	resultJson.put("org.archicontribs.form", formJson);
    	
    	for ( String key: (Set<String>)form.getData("editable keys") ) {
    		if ( form.getData(key) != null ) {
    			formJson.put(key, form.getData(key));
    		}
    	}
    	
    	//TODO: iterate over the sub tree items, getthe data from the widgets (getData("control")
    	
    	return resultJson;
    }
}
