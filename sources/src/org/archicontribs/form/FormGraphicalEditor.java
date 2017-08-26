package org.archicontribs.form;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.archicontribs.form.composites.CompositeInterface;
import org.archicontribs.form.composites.FormComposite;
import org.archicontribs.form.composites.LabelComposite;
import org.archicontribs.form.composites.TabComposite;
import org.archicontribs.form.composites.TextComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Create a Dialog with graphical controls as described in the configuration
 * file
 * 
 * @author Herve Jouin
 *
 */
public class FormGraphicalEditor extends Dialog {
    private static final FormLogger logger     = new FormLogger(FormGraphicalEditor.class);
    
    public final static Display display        = Display.getDefault();
    public final static Image binImage         = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/bin.png"));
    public final static Color blackColor       = new Color(display, 0, 0, 0);
    public final static Color whiteColor       = new Color(display, 255, 255, 255);
    
    public final static int   editorLeftposition   = 150;
    public final static int   editorBorderMargin   = 10;
    public final static int   editorVerticalMargin = 10;

    private Shell             propertiesDialog  = null;
    private Tree              tree              = null;
    private ScrolledComposite scrolledcomposite = null;
    private FormComposite     formComposite     = null;
    private TabComposite      tabComposite      = null;
    private LabelComposite    labelComposite    = null;
    private TextComposite     textComposite     = null;
    private Composite         comboComposite    = null;
    private Composite         checkComposite    = null;
    private Composite         tableComposite    = null;
    private Composite         columnComposite   = null;
    private Composite         lineComposite     = null;
    
    private Shell             formDialog        = null;
    
	public static final Image FORM_ICON         = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/form.png"));
	public static final Image TAB_ICON          = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/tab.png"));
	public static final Image LABEL_ICON        = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/label.png"));
	public static final Image TEXT_ICON         = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/text.png"));
	public static final Image CHECK_ICON        = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/check.png"));
	public static final Image COMBO_ICON        = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/combo.png"));
	public static final Image TABLE_ICON        = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/table.png"));
	public static final Image COLUMN_ICON       = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/column.png"));
	public static final Image LINE_ICON         = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/line.png"));
    
    
    private static final FormJsonParser jsonParser = new FormJsonParser();
    
    public FormGraphicalEditor(String configFilename, JSONObject json) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        try {
            formDialog = jsonParser.createShell(json, getParent());
            
            TreeItem formTreeItem = createPropertiesDialog(json);
            formTreeItem.setImage(FORM_ICON);
            formTreeItem.setText(formDialog.getText());
            formTreeItem.setData("class", "form");
            formTreeItem.setData("control", formDialog);

            
            formComposite.setData("ok button", formDialog.getData("ok button"));
            formComposite.setData("cancel button", formDialog.getData("cancel button"));
            formComposite.setData("export button", formDialog.getData("export button"));
            formComposite.setData("tab folder", formDialog.getData("tab folder"));
            formComposite.setData("tabs array", formDialog.getData("tabs array"));
            
            TabFolder tabFolder = (TabFolder)formDialog.getData("tab folder");
            
            // we create one TabItem per tab array item
            JSONArray tabs = (JSONArray)formDialog.getData("tabs array");
            if ( tabs != null ) {
            	@SuppressWarnings("unchecked")
				Iterator<JSONObject> tabsIterator = tabs.iterator();
                while (tabsIterator.hasNext()) {
                    JSONObject jsonTab = tabsIterator.next();
                    TabItem tabItem = jsonParser.createTab(jsonTab, tabFolder);
                    
                    TreeItem tabTreeItem = new TreeItem(formTreeItem, SWT.NONE);
                    tabTreeItem.setImage(TAB_ICON);
                    tabTreeItem.setText(tabItem.getText());
                    tabTreeItem.setData("class", "tab");
                    tabTreeItem.setData("control", tabItem.getControl());
                    
                    JSONArray controls = (JSONArray)tabItem.getData("controls array");
                    if ( controls != null ) {
                        Composite tabItemComposite = (Composite)tabItem.getControl();
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> controlsIterator = controls.iterator();
                        while (controlsIterator.hasNext()) {
                            JSONObject jsonControl = controlsIterator.next();
                            createControl(jsonControl, tabItemComposite, tabTreeItem);
                        }
                        tabItemComposite.layout();
                    }
                }
            }
            
            formTreeItem.setExpanded(true);
            
            tree.setSelection(formTreeItem);
            tree.notifyListeners(SWT.Selection, new Event());        // shows up the form's properties
           
            // If there is at least one Excel sheet specified, then we show up the "export to Excel" button
            //if (excelSheets.isEmpty()) {
            //    exportButton.setVisible(false);
            //}
        } catch (ClassCastException e) {
            FormDialog.popup(Level.ERROR, "Wrong key type in the configuration file \"" + configFilename + "\"", e);
            if (formDialog != null)
            	formDialog.dispose();
            return;
        } catch (RuntimeException e) {
            FormDialog.popup(Level.ERROR, "Please check your configuration file \"" + configFilename + "\"", e);
            if (formDialog != null)
            	formDialog.dispose();
            return;
        }

        formDialog.open();
        formDialog.layout();
        
        propertiesDialog.open();
        propertiesDialog.layout();
    }

    /**
     * creates the propertiesDialog shell
     */
    private TreeItem createPropertiesDialog(JSONObject json) {
    	propertiesDialog = new Shell(formDialog, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.CLOSE);
    	propertiesDialog.setSize(1000, 600);
    	propertiesDialog.setLayout(new FormLayout());
        
        Button btnCancel = new Button(propertiesDialog, SWT.NONE);
        FormData fd = new FormData();
        fd.right = new FormAttachment(100, -editorBorderMargin);
        fd.bottom = new FormAttachment(100, -editorBorderMargin);
        btnCancel.setLayoutData(fd);
        btnCancel.setText("Cancel");
        btnCancel.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent event) {cancel();}
            @Override public void widgetDefaultSelected(SelectionEvent e) {widgetSelected(e);}
        });
        
        Button btnSave = new Button(propertiesDialog, SWT.NONE);
        fd = new FormData();
        fd.right = new FormAttachment(btnCancel, -editorBorderMargin);
        fd.bottom = new FormAttachment(100, -editorBorderMargin);
        btnSave.setLayoutData(fd);
        btnSave.setText("Save");
        btnSave.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent event) {save();}
            @Override public void widgetDefaultSelected(SelectionEvent e) {widgetSelected(e);}
        });
        
        Label horizontalBar = new Label(propertiesDialog, SWT.HORIZONTAL);
        fd = new FormData();
        fd.bottom = new FormAttachment(btnSave, -editorBorderMargin, SWT.TOP);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(0, 0);
        horizontalBar.setLayoutData(fd);
        
        propertiesDialog.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
              cancel();
            }
        });
        
        Sash sash = new Sash(propertiesDialog, SWT.VERTICAL);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.bottom = new FormAttachment(horizontalBar, -editorBorderMargin);
        fd.left = new FormAttachment(0, 300);
        sash.setLayoutData(fd);
        sash.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
              ((FormData) sash.getLayoutData()).left = new FormAttachment(0, event.x);
              sash.getParent().layout();
            }
          });
        
        tree = new Tree(propertiesDialog, SWT.BORDER);
        tree.setHeaderVisible(false);
        tree.setLinesVisible(false);
        fd = new FormData();
        fd.top = new FormAttachment(0, editorBorderMargin);
        fd.left = new FormAttachment(0, editorBorderMargin);
        fd.right = new FormAttachment(sash, -editorBorderMargin/2);
        fd.bottom = new FormAttachment(horizontalBar, -editorBorderMargin);
        tree.setLayoutData(fd);
        tree.addListener(SWT.Selection, treeSelectionListener);
        
        Menu treeMenu = new Menu(tree);
        tree.setMenu(treeMenu);
        treeMenu.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                MenuItem[] items = treeMenu.getItems();
                for (int i = 0; i < items.length; i++)
                    items[i].dispose();
                
                TreeItem selectedItem = tree.getSelection()[0];
                MenuItem newItem;
                switch ( (String)selectedItem.getData("class") ) {
                	case "form":
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add tab");
                        break;
                        
                	case "tab":
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Insert tab before ...");
                		newItem = new MenuItem(treeMenu, SWT.SEPARATOR);
            			newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Add tab after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Add label after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Add text after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Add combo after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Add check after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("Add table after");
                        break;
                        
                	case "label":
                	case "text":
                	case "combo":
                	case "check":
                	case "table":
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert label before");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert text before");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert combo before");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert check before");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert table before");
                        newItem = new MenuItem(treeMenu, SWT.SEPARATOR);
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add label after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add text after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add combo after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add check after");
                        newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add table after");
                        break;

                	case "columns":
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add column");
                		break;
                		
                	case "column":
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert column before");
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add column after");
                		break;
                		
                	case "lines":
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add line");
                		break;
                		
                	case "line":
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("insert line before");
                		newItem = new MenuItem(treeMenu, SWT.NONE); newItem.setText("add line after");
                		break;
                }
            }
        });
        
        TreeItem formTreeItem = new TreeItem(tree, SWT.NONE);
        
        scrolledcomposite = new ScrolledComposite(propertiesDialog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(0, editorBorderMargin);
        fd.left = new FormAttachment(sash, editorBorderMargin/2);
        fd.right = new FormAttachment(100, -editorBorderMargin);
        fd.bottom = new FormAttachment(horizontalBar, -editorBorderMargin);
        scrolledcomposite.setLayoutData(fd);
        
        
        // we create the composites
        formComposite = new FormComposite(scrolledcomposite, SWT.BORDER);
        tabComposite = new TabComposite(scrolledcomposite, SWT.BORDER);
        labelComposite = new LabelComposite(scrolledcomposite, SWT.BORDER);
        textComposite = new TextComposite(scrolledcomposite, SWT.BORDER);
        comboComposite = new Composite(scrolledcomposite, SWT.BORDER);
        checkComposite = new Composite(scrolledcomposite, SWT.BORDER);
        tableComposite = new Composite(scrolledcomposite, SWT.BORDER);
        columnComposite = new Composite(scrolledcomposite, SWT.BORDER);
        lineComposite = new Composite(scrolledcomposite, SWT.BORDER);
        
        formTreeItem.setData("composite", formComposite);
        
        return formTreeItem;
    }
    
    /**
     * this listener is called each time a treeItem is selected in the tree
     */
    private Listener treeSelectionListener = new Listener() {
        public void handleEvent(Event event) {
            // hide the current composite and show up the composite linked to the selected treeItem
        	/*
            formComposite.setVisible(false);
            tabComposite.setVisible(false);
            labelComposite.setVisible(false);
            textComposite.setVisible(false);
            comboComposite.setVisible(false);
            checkComposite.setVisible(false);
            tableComposite.setVisible(false);
            columnComposite.setVisible(false);
            lineComposite.setVisible(false);
            */
        	scrolledcomposite.setContent(null);
            
            if ( tree.getSelectionCount() != 0 ) {
            	TreeItem treeItem = tree.getSelection()[0];
            	CompositeInterface composite = (CompositeInterface)treeItem.getData("composite");
            	scrolledcomposite.setContent((Composite)composite);
            	scrolledcomposite.setExpandHorizontal(true);
            	scrolledcomposite.setExpandVertical(true);
            	scrolledcomposite.setMinSize(((Composite)composite).computeSize(SWT.DEFAULT, SWT.DEFAULT));
            	
            	if ( composite != null ) {
            		Control control = (Control)treeItem.getData("control");
            		
            		composite.setVisible(true);
                    composite.setData("shell", propertiesDialog);
                    composite.setData("treeItem", treeItem);
                    composite.setData("class", treeItem.getData("class"));
                    composite.setData("control", control);
                    
                    for ( String key: (String [])control.getData("editable keys") ) {
                    	composite.set(key, control.getData(key));
                    }
            	}
            }
        }
    };

    /**
     * Creates the dialog controls. The following controls are currently managed:<br>
     * <li>check
     * <li>combo
     * <li>label
     * <li>table
     * <li>text
     * <br>
     * called by the createTabs() method
     * 
     * @param tab
     *            The JSON object to parse
     * @param composite
     *            The composite where the control will be created
     */
    private void createControl(JSONObject jsonObject, Composite parent, TreeItem parentTreeItem) throws RuntimeException {
            FormJsonParser jsonParser = new FormJsonParser();
            TreeItem treeItem;

            String clazz = jsonParser.getString(jsonObject, "class");
            if ( clazz != null ) {
	            switch ( clazz.toLowerCase() ) {
	                case "check":
	                    Button check = jsonParser.createCheck(jsonObject, parent);
	    	            treeItem = new TreeItem(parentTreeItem, SWT.NONE);
	    	            treeItem.setImage(CHECK_ICON);
	    	            treeItem.setText((String)check.getData("name"));
	    	            treeItem.setData("composite", checkComposite);
	    	            treeItem.setData("class", "check");
	    	            treeItem.setData("control", check); 
	                    break;
	                    
	                case "combo":
	                    CCombo combo = jsonParser.createCombo(jsonObject, parent);
	    	            treeItem = new TreeItem(parentTreeItem, SWT.NONE);
	    	            treeItem.setImage(COMBO_ICON);
	    	            treeItem.setText((String)combo.getData("name"));
	    	            treeItem.setData("composite", comboComposite);
	    	            treeItem.setData("class", "combo");
	    	            treeItem.setData("control", combo); 
	                    break;
	                    
	                case "label":
	                    Label label = jsonParser.createLabel(jsonObject, parent);
	    	            treeItem = new TreeItem(parentTreeItem, SWT.NONE);
	    	            treeItem.setImage(LABEL_ICON);
	    	            treeItem.setText((String)label.getData("name"));
	    	            treeItem.setData("composite", labelComposite);
	    	            treeItem.setData("class", "label");
	    	            treeItem.setData("control", label); 
	                    break;
	                    
	                case "table":
	                	//TODO : jsonParser.createTable(jsonObject, parent);
	                    break;
	                    
	                case "text":
	                    StyledText text = jsonParser.createText(jsonObject, parent);
	    	            treeItem = new TreeItem(parentTreeItem, SWT.NONE);
	    	            treeItem.setImage(TEXT_ICON);
	    	            treeItem.setText((String)text.getData("name"));
	    	            treeItem.setData("composite", textComposite);
	    	            treeItem.setData("class", "text");
	    	            treeItem.setData("control", text); 
	                    break;
	                    
	                default:
	                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + jsonObject.get("class") + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
	            }
            }
            FormPosition.resetControlName();
            FormPosition.resetControlClass();
    }
    


     /**
     * Create a Table control<br>
     * <br>
     * called by the createObjects() method
     * 
     * @param jsonObject
     *            the JSON object to parse
     * @param composite
     *            the composite where the control will be created
     */
    @SuppressWarnings("unchecked")
    private void createTable(JSONObject jsonObject, TreeItem tabTreeItem, Composite composite) throws RuntimeException {
    	/*
        String tableName = jsonParser.getString(jsonObject, "name", "");
        
        if (logger.isDebugEnabled())
            logger.debug("   Creating table \"" + tableName + "\"");
        
        FormPosition.setControlName(tableName);
        FormPosition.setControlClass("table");
        
        int x = jsonParser.getInt(jsonObject, "x", 0);
        int y = jsonParser.getInt(jsonObject, "y", 0);
        int width = jsonParser.getInt(jsonObject, "width", 100);
        int height = jsonParser.getInt(jsonObject, "height", 50);
        String background = jsonParser.getString(jsonObject, "background", "");
        String foreground = jsonParser.getString(jsonObject, "foreground", "");
        String tooltip = jsonParser.getString(jsonObject, "tooltip", "");
        String excelSheet = jsonParser.getString(jsonObject, "excelSheet", "");
        int excelFirstLine = jsonParser.getInt(jsonObject, "excelFirstLine", 1);
        int excelLastLine = jsonParser.getInt(jsonObject, "excelLastLine", 0);

        if (logger.isDebugEnabled())
            logger.debug("   Creating table");
        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + FormDialog.debugValue(x, 0));
            logger.trace("      y = " + FormDialog.debugValue(y, 0));
            logger.trace("      width = " + FormDialog.debugValue(width, 100));
            logger.trace("      height = " + FormDialog.debugValue(height, 50));
            logger.trace("      background = " + FormDialog.debugValue(background, ""));
            logger.trace("      foreground = " + FormDialog.debugValue(foreground, ""));
            logger.trace("      tooltip = " + FormDialog.debugValue(tooltip, ""));
            logger.trace("      excelSheet = " + FormDialog.debugValue(excelSheet, ""));
            logger.trace("      excelFirstLine = " + FormDialog.debugValue(excelFirstLine, 1));
            logger.trace("      excelLastLine = " + FormDialog.debugValue(excelLastLine, 0));
        }
        
        TreeItem tableTreeItem = new TreeItem(tabTreeItem, SWT.NONE);
        tableTreeItem.setText("Table: "+ tableName);
        tableTreeItem.setData("class", "table");
        tableTreeItem.setData("name", tableName);
        tableTreeItem.setData("x", x);
        tableTreeItem.setData("y", y);
        tableTreeItem.setData("width", width);
        tableTreeItem.setData("height", height);
        tableTreeItem.setData("background", background);
        tableTreeItem.setData("foreground", foreground);
        tableTreeItem.setData("tooltip", tooltip);
        tableTreeItem.setData("excelSheet", excelSheet);
        tableTreeItem.setData("excelFirstLine", excelFirstLine);
        tableTreeItem.setData("excelLastLine", excelLastLine);
        
        TreeItem columnsTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
        columnsTreeItem.setText("Columns");
        columnsTreeItem.setData("class", "columns");
        
        TreeItem linesTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
        linesTreeItem.setText("Lines");
        linesTreeItem.setData("class", "lines");
        
        // we iterate over the "columns" entries
        Iterator<JSONObject> columnsIterator = (jsonParser.getJSONArray(jsonObject, "columns")).iterator();
        while (columnsIterator.hasNext()) {
            JSONObject column = columnsIterator.next();

            String columnName = jsonParser.getString(column, "name", "");
            FormPosition.setColumnName(columnName);
            
            String columnClass = jsonParser.getString(column, "class").toLowerCase();
            
            tooltip = jsonParser.getString(column, "tooltip", "");
            width = jsonParser.getInt(column, "width", 0);
            background = jsonParser.getString(column, "background", "");
            foreground = jsonParser.getString(column, "foreground", "");
            String excelColumn = jsonParser.getString(column, "excelColumn", "");
            String excelCellType = jsonParser.getString(column, "excelCellType", "string");
            String excelDefault = jsonParser.getString(column, "excelDefault", "blank");

            if (logger.isDebugEnabled())
                logger.debug("   Creating column \"" + columnName + "\" of class \"" + columnClass + "\"");
            if (logger.isTraceEnabled()) {
                logger.trace("      width = " + FormDialog.debugValue(width, 0));
                logger.trace("      background = " + FormDialog.debugValue(background, ""));
                logger.trace("      foreground = " + FormDialog.debugValue(foreground, ""));
                logger.trace("      tooltip = " + FormDialog.debugValue(tooltip, ""));
                logger.trace("      excelColumn = " + FormDialog.debugValue(excelColumn, ""));
                logger.trace("      excelCellType = " + FormDialog.debugValue(excelCellType, "string"));
                logger.trace("      excelDefault = " + FormDialog.debugValue(excelDefault, "blank"));
            }
            
            TreeItem columnTreeItem = new TreeItem(columnsTreeItem, SWT.NONE);
            columnTreeItem.setText("Column: "+ columnName);
            columnTreeItem.setData("class", "column");
            columnTreeItem.setData("name", columnName);
            columnTreeItem.setData("class", columnClass);
            columnTreeItem.setData("width", width);
            columnTreeItem.setData("background", background);
            columnTreeItem.setData("foreground", foreground);
            columnTreeItem.setData("tooltip", tooltip);
            columnTreeItem.setData("excelColumn", excelColumn);
            columnTreeItem.setData("excelCellType", excelCellType);
            columnTreeItem.setData("excelDefault", excelDefault);

            String alignment;

            switch (columnClass) {
                case "label":
                    alignment = jsonParser.getString(column, "alignment", "left");
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("      alignment = "+FormDialog.debugValue(alignment, "left"));
                    }
                    
                    columnTreeItem.setData("alignment", alignment);
                    break;
                    
                case "text":
                    String regexp = jsonParser.getString(column, "regexp", "");
                    String defaultText = jsonParser.getString(column, "default", "");
                    String whenEmpty = jsonParser.getString(column, "whenEmpty", globalWhenEmpty);
                    boolean forceDefault = jsonParser.getBoolean(column, "forceDefault", false);
                    alignment = jsonParser.getString(column, "alignment", "left");

                    if (logger.isTraceEnabled()) {
                        logger.trace("      regexp = " + FormDialog.debugValue(regexp, ""));
                        logger.trace("      default = " + FormDialog.debugValue(defaultText, ""));
                        logger.trace("      forceDefault = " + FormDialog.debugValue(forceDefault, false));
                        logger.trace("      whenEmpty = " + FormDialog.debugValue(whenEmpty, globalWhenEmpty));
                        logger.trace("      alignment = "+FormDialog.debugValue(alignment, "left"));
                    }

                    columnTreeItem.setData("regexp", regexp);
                    columnTreeItem.setData("default", defaultText);
                    columnTreeItem.setData("forceDefault", forceDefault);
                    columnTreeItem.setData("whenEmpty", whenEmpty);
                    columnTreeItem.setData("alignment", alignment);
                    break;
                    
                case "combo":
                    String[] values = (String[]) (jsonParser.getJSONArray(column, "values")).toArray(new String[0]);
                    String defaultValue = jsonParser.getString(column, "default", "");
                    Boolean editable = jsonParser.getBoolean(column, "editable", true);
                    whenEmpty = jsonParser.getString(jsonObject, "whenEmpty", globalWhenEmpty);
                    alignment = jsonParser.getString(column, "alignment", "left");

                    if (logger.isTraceEnabled()) {
                        logger.trace("      values = " + values);
                        logger.trace("      default = " + FormDialog.debugValue(defaultValue, ""));
                        logger.trace("      editable = " + FormDialog.debugValue(editable, true));
                        logger.trace("      whenEmpty = " + FormDialog.debugValue(whenEmpty, globalWhenEmpty));
                        logger.trace("      alignment = "+FormDialog.debugValue(alignment, "left"));
                    }

                    columnTreeItem.setData("values", values);
                    columnTreeItem.setData("default", defaultValue);
                    columnTreeItem.setData("editable", editable);
                    columnTreeItem.setData("whenEmpty", whenEmpty);
                    columnTreeItem.setData("alignment", alignment);
                    break;
                case "check":
                    values = (String[]) (jsonParser.getJSONArray(column, "values")).toArray(new String[0]);
                    defaultValue = jsonParser.getString(column, "default", "");
                    forceDefault = jsonParser.getBoolean(column, "forceDefault", false);
                    whenEmpty = jsonParser.getString(jsonObject, "whenEmpty", globalWhenEmpty);
                    alignment = jsonParser.getString(column, "alignment", "left");

                    if (logger.isTraceEnabled()) {
                        logger.trace("      values = " + values);
                        logger.trace("      default = " + FormDialog.debugValue(defaultValue, ""));
                        logger.trace("      forceDefault = " + FormDialog.debugValue(forceDefault, false));
                        logger.trace("      whenEmpty = " + FormDialog.debugValue(whenEmpty, globalWhenEmpty));
                        logger.trace("      alignment = "+FormDialog.debugValue(alignment, "left"));
                    }

                    columnTreeItem.setData("values", values);
                    columnTreeItem.setData("default", defaultValue);
                    columnTreeItem.setData("forceDefault", forceDefault);
                    columnTreeItem.setData("whenEmpty", whenEmpty);
                    columnTreeItem.setData("alignment", alignment);
                    break;
                default:
                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" +jsonParser.getString(column, "class") + "\" (valid values are \"check\", \"combo\", \"label\" and \"text\").");
            }
        }
        FormPosition.resetColumnName();


        // we iterate over the "lines" entries
        JSONArray lines = jsonParser.getJSONArray(jsonObject, "lines");
        if (lines != null) {
            Iterator<JSONObject> linesIterator = lines.iterator();
            while (linesIterator.hasNext()) {
                JSONObject line = linesIterator.next();
                
                TreeItem lineTreeItem = new TreeItem(linesTreeItem, SWT.NONE);
                
                lineTreeItem.setData("cells", jsonParser.getJSONArray(line, "cells"));
                lineTreeItem.setData("class", "line");
                
                if ((boolean) jsonParser.getJSON(line, "generate", false) == false) {
                    // static line
                    if (logger.isTraceEnabled())
                        logger.trace("Creating static line");
                                        lineTreeItem.setText("Static line");
                } else {
                    // dynamic lines : we create one line per entry in getChildren()
                    if (logger.isTraceEnabled())
                        logger.trace("Generating dynamic lines");
                    
                    lineTreeItem.setText("Dynamic lines");
                    lineTreeItem.setData("filter", jsonParser.getJSONObject(line, "filter"));
                }
            }
        }
        */
    }

    private void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");
        
        close();
    }

    private void save() {
        if (logger.isDebugEnabled())
            logger.debug("Save button selected by user.");

        //TODO: save the json content 

        close();
    }
    
    private void close() {
        if ( formDialog != null ) {
            formDialog.dispose();
            formDialog = null;
        }
        
        if ( propertiesDialog != null ) {
            propertiesDialog.dispose();
            propertiesDialog = null;
        }
    }
}
