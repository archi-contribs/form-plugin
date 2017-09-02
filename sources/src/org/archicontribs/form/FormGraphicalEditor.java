package org.archicontribs.form;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;
import org.archicontribs.form.composites.CheckColumnComposite;
import org.archicontribs.form.composites.CheckComposite;
import org.archicontribs.form.composites.ComboColumnComposite;
import org.archicontribs.form.composites.ComboComposite;
import org.archicontribs.form.composites.CompositeInterface;
import org.archicontribs.form.composites.FormComposite;
import org.archicontribs.form.composites.LabelColumnComposite;
import org.archicontribs.form.composites.LabelComposite;
import org.archicontribs.form.composites.LineComposite;
import org.archicontribs.form.composites.TabComposite;
import org.archicontribs.form.composites.TableComposite;
import org.archicontribs.form.composites.TextColumnComposite;
import org.archicontribs.form.composites.TextComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
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
 * Create a Dialog with graphical controls as described in the configuration
 * file
 * 
 * @author Herve Jouin
 *
 */
public class FormGraphicalEditor extends Dialog {
    private static final FormLogger logger     	      = new FormLogger(FormGraphicalEditor.class);
    
    public final static Display display        	      = Display.getDefault();
    public final static Image binImage         	      = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/bin.png"));
    public final static Color blackColor       	      = new Color(display, 0, 0, 0);
    public final static Color whiteColor       	      = new Color(display, 255, 255, 255);
    
    public final static int   editorLeftposition      = 150;
    public final static int   editorBorderMargin      = 10;
    public final static int   editorVerticalMargin    = 10;

    private Shell             	 propertiesDialog  	  = null;
    private Tree              	 tree              	  = null;
    private ScrolledComposite 	 scrolledcomposite 	  = null;
    private FormComposite     	 formComposite     	  = null;
    private TabComposite      	 tabComposite      	  = null;
    private LabelComposite    	 labelComposite    	  = null;
    private TextComposite     	 textComposite     	  = null;
    private ComboComposite    	 comboComposite    	  = null;
    private CheckComposite    	 checkComposite   	  = null;
    private TableComposite    	 tableComposite    	  = null;
    private LabelColumnComposite labelColumnComposite = null;
    private TextColumnComposite  textColumnComposite  = null;
    private ComboColumnComposite comboColumnComposite = null;
    private CheckColumnComposite checkColumnComposite = null;
    private LineComposite        lineComposite        = null;
    
    private Shell             formDialog        	  = null;
    
	public static final Image FORM_ICON         	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/form.png"));
	public static final Image TAB_ICON          	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/tab.png"));
	public static final Image LABEL_ICON        	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/label.png"));
	public static final Image TEXT_ICON         	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/text.png"));
	public static final Image CHECK_ICON        	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/check.png"));
	public static final Image COMBO_ICON        	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/combo.png"));
	public static final Image TABLE_ICON        	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/table.png"));
	public static final Image COLUMN_ICON       	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/column.png"));
	public static final Image LINE_ICON         	  = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/line.png"));
	
	public static final Image BIN_ICON                = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/bin.png"));
	public static final Image BAS_ICON                = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/flèche_bas.png"));
	public static final Image HAUT_ICON               = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/flèche_haut.png"));
	public static final Image PLUS_ICON               = new Image(display, FormGraphicalEditor.class.getResourceAsStream("/icons/plus.png"));
	    
    
    
    private static final FormJsonParser jsonParser = new FormJsonParser();
    
    public FormGraphicalEditor(String configFilename, JSONObject jsonForm) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        try {
            formDialog = jsonParser.createShell(jsonForm, getParent());
            
            TreeItem formTreeItem = createPropertiesDialog(jsonForm);
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
            JSONArray tabs = jsonParser.getJSONArray(jsonForm, "tabs");
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
                    
                    JSONArray controls = jsonParser.getJSONArray(jsonTab, "controls");
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
           
            // TODO: If there is at least one Excel sheet specified, then we show up the "export to Excel" button
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
    	propertiesDialog.setSize(750, 500);
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
        fd.left = new FormAttachment(0, 200);
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
        
        TreeItem formTreeItem = new TreeItem(tree, SWT.NONE);
        
        scrolledcomposite = new ScrolledComposite(propertiesDialog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(0, editorBorderMargin);
        fd.left = new FormAttachment(sash, editorBorderMargin/2);
        fd.right = new FormAttachment(100, -editorBorderMargin);
        fd.bottom = new FormAttachment(horizontalBar, -editorBorderMargin);
        scrolledcomposite.setLayoutData(fd);
        
        
        // we create the composites
        formComposite 		 = new FormComposite(scrolledcomposite, SWT.NONE);
        tabComposite 		 = new TabComposite(scrolledcomposite, SWT.NONE);
        labelComposite 		 = new LabelComposite(scrolledcomposite, SWT.NONE);
        textComposite 		 = new TextComposite(scrolledcomposite, SWT.NONE);
        comboComposite 		 = new ComboComposite(scrolledcomposite, SWT.NONE);
        checkComposite 		 = new CheckComposite(scrolledcomposite, SWT.NONE);
        tableComposite 		 = new TableComposite(scrolledcomposite, SWT.NONE);
        labelColumnComposite = new LabelColumnComposite(scrolledcomposite, SWT.NONE);
        textColumnComposite  = new TextColumnComposite(scrolledcomposite, SWT.NONE);
        comboColumnComposite = new ComboColumnComposite(scrolledcomposite, SWT.NONE);
        checkColumnComposite = new CheckColumnComposite(scrolledcomposite, SWT.NONE);
        lineComposite        = new LineComposite(scrolledcomposite, SWT.NONE);
        
        Button up = new Button(propertiesDialog, SWT.PUSH);
        up.setImage(HAUT_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(tree, (int)(editorBorderMargin*1.5));
        fd.left = new FormAttachment(tree, 0, SWT.LEFT);
        fd.right = new FormAttachment(up, 16, SWT.LEFT);
        fd.bottom = new FormAttachment(up, 16, SWT.TOP);
        up.setLayoutData(fd);
        
        Button down = new Button(propertiesDialog, SWT.PUSH);
        down.setImage(BAS_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(tree, (int)(editorBorderMargin*1.5));
        fd.left = new FormAttachment(up, editorBorderMargin/2);
        fd.right = new FormAttachment(down, 16, SWT.LEFT);
        fd.bottom = new FormAttachment(down, 16, SWT.TOP);
        down.setLayoutData(fd);
        
        Button delete = new Button(propertiesDialog, SWT.PUSH);
        delete.setImage(BIN_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(tree, (int)(editorBorderMargin*1.5));
        fd.left = new FormAttachment(down, editorBorderMargin/2);
        fd.right = new FormAttachment(delete, 16, SWT.LEFT);
        fd.bottom = new FormAttachment(delete, 16, SWT.TOP);
        delete.setLayoutData(fd);
        
        Button add = new Button(propertiesDialog, SWT.PUSH);
        add.setImage(PLUS_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(tree, (int)(editorBorderMargin*1.5));
        fd.left = new FormAttachment(delete, editorBorderMargin/2);
        fd.right = new FormAttachment(add, 16, SWT.LEFT);
        fd.bottom = new FormAttachment(add, 16, SWT.TOP);
        add.setLayoutData(fd);
        
        Button tab = new Button(propertiesDialog, SWT.RADIO);
        fd = new FormData();
        fd.top = new FormAttachment(tree, editorBorderMargin/2);
        fd.left = new FormAttachment(add, editorBorderMargin);
        tab.setLayoutData(fd);
        
        Label tabIcon = new Label(propertiesDialog, SWT.NONE);
        tabIcon.setImage(TAB_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(tab, 0);
        fd.left = new FormAttachment(tab, 0, SWT.CENTER);
        tabIcon.setLayoutData(fd);
        
        Button label = new Button(propertiesDialog, SWT.RADIO);
        fd = new FormData();
        fd.top = new FormAttachment(tree, editorBorderMargin/2);
        fd.left = new FormAttachment(tab, editorBorderMargin);
        label.setLayoutData(fd);
        
        Label labelIcon = new Label(propertiesDialog, SWT.NONE);
        labelIcon.setImage(LABEL_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(label, 0);
        fd.left = new FormAttachment(label, 0, SWT.CENTER);
        labelIcon.setLayoutData(fd);
        
        Button text = new Button(propertiesDialog, SWT.RADIO);
        fd = new FormData();
        fd.top = new FormAttachment(tree, editorBorderMargin/2);
        fd.left = new FormAttachment(label, editorBorderMargin);
        text.setLayoutData(fd);
        
        Label textIcon = new Label(propertiesDialog, SWT.NONE);
        textIcon.setImage(TEXT_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(text, 0);
        fd.left = new FormAttachment(text, 0, SWT.CENTER);
        textIcon.setLayoutData(fd);
        
        Button check = new Button(propertiesDialog, SWT.RADIO);
        fd = new FormData();
        fd.top = new FormAttachment(tree, editorBorderMargin/2);
        fd.left = new FormAttachment(text, editorBorderMargin);
        check.setLayoutData(fd);
        check.setEnabled(false);
        
        Label checkIcon = new Label(propertiesDialog, SWT.NONE);
        checkIcon.setImage(CHECK_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(check, 0);
        fd.left = new FormAttachment(check, 0, SWT.CENTER);
        checkIcon.setLayoutData(fd);
        
        Button combo = new Button(propertiesDialog, SWT.RADIO);
        fd = new FormData();
        fd.top = new FormAttachment(tree, editorBorderMargin/2);
        fd.left = new FormAttachment(check, editorBorderMargin);
        combo.setLayoutData(fd);
        
        Label comboIcon = new Label(propertiesDialog, SWT.NONE);
        comboIcon.setImage(COMBO_ICON);
        fd = new FormData();
        fd.top = new FormAttachment(combo, 0);
        fd.left = new FormAttachment(combo, 0, SWT.CENTER);
        comboIcon.setLayoutData(fd);
        
        
        return formTreeItem;
    }
    
    /**
     * this listener is called each time a treeItem is selected in the tree
     */
    private Listener treeSelectionListener = new Listener() {
        public void handleEvent(Event event) {
        	scrolledcomposite.setContent(null);
            
            if ( tree.getSelectionCount() != 0 ) {
            	TreeItem treeItem = tree.getSelection()[0];
            	CompositeInterface composite;
            	switch ( (String)treeItem.getData("class") ) {
            		case "form":		composite = formComposite; break;
            		case "tab":			composite = tabComposite; break;
            		case "label":		composite = labelComposite; break;
            		case "text":		composite = textComposite; break;
            		case "combo":		composite = comboComposite; break;
            		case "check":		composite = checkComposite; break;
            		case "table":		composite = tableComposite; break;
            		case "columns":     return;				// TODO: create composite to show how many columns are defined
            		case "labelColumn":	composite = labelColumnComposite; break;
            		case "textColumn":	composite = textColumnComposite; break;
            		case "comboColumn":	composite = comboColumnComposite; break;
            		case "checkColumn":	composite = checkColumnComposite; break;
            		case "lines":       return;				// TODO: create composite to show how many lines are defined
            		case "line":        composite = lineComposite; break;
            		default:
            			throw new RuntimeException ("Do not know how to manage "+(String)treeItem.getData("class")+" objects.");
            	}
            	scrolledcomposite.setContent((Composite)composite);
            	scrolledcomposite.setExpandHorizontal(true);
            	scrolledcomposite.setExpandVertical(true);
            	scrolledcomposite.setMinSize(((Composite)composite).computeSize(SWT.DEFAULT, SWT.DEFAULT));
            	
            	if ( composite != null ) {
            		Widget widget = (Widget)treeItem.getData("control");
            		
            		composite.setVisible(true);
                    composite.setData("shell", propertiesDialog);
                    composite.setData("treeItem", treeItem);
                    composite.setData("class", treeItem.getData("class"));
                    composite.setData("control", widget);
                    
                    @SuppressWarnings("unchecked")
					Set<String> keys = (Set<String>)widget.getData("editable keys");
                    if ( keys != null ) {
                    	for ( String key: keys)
                    	composite.set(key, widget.getData(key));
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
    private void createControl(JSONObject jsonControl, Composite parent, TreeItem parentTreeItem) throws RuntimeException {
            FormJsonParser jsonParser = new FormJsonParser();
            TreeItem treeItem;

            String clazz = jsonParser.getString(jsonControl, "class");
            if ( clazz != null ) {
            	treeItem = new TreeItem(parentTreeItem, SWT.NONE);
            	Widget widget;
	            switch ( clazz.toLowerCase() ) {
	                case "check":
	                	widget = jsonParser.createCheck(jsonControl, parent);
	                	treeItem.setImage(CHECK_ICON);
	                	break;
		            case "combo":
	                	widget = jsonParser.createCombo(jsonControl, parent);
	                	treeItem.setImage(COMBO_ICON);
	                	break;
	                case "label":
	                	widget = jsonParser.createLabel(jsonControl, parent);
	                	treeItem.setImage(LABEL_ICON);
	                	break;
	                case "table":
	                	widget = jsonParser.createTable(jsonControl, parent);
	                	treeItem.setImage(TABLE_ICON);
	                	break;
	                case "text":
	                	widget = jsonParser.createText(jsonControl, parent);
	                	treeItem.setImage(TEXT_ICON);
	                	break;
	                default:
	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + jsonControl.get("class") + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
	            }
	            
	            treeItem.setText(widget.getData("name")==null ? "" : (String)widget.getData("name"));
	            treeItem.setData("class", clazz);
	            treeItem.setData("control", widget);
	            
	            widget.setData("treeItem", treeItem);
	            
	            if ( FormPlugin.areEqualIgnoreCase(clazz, "table") ) {
	            	TreeItem tableTreeItem = treeItem;
    	            TreeItem columnsTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
    	            columnsTreeItem.setImage(COLUMN_ICON);
    	            columnsTreeItem.setText("columns");
    	            columnsTreeItem.setData("class", "columns");

                    JSONArray columns = jsonParser.getJSONArray(jsonControl, "columns");
                    if ( columns != null ) {
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> columnsIterator = columns.iterator();
                        while (columnsIterator.hasNext()) {
                            JSONObject jsonColumn = columnsIterator.next();
                            
                            clazz = jsonParser.getString(jsonColumn, "class");
                            if ( clazz != null ) {
                            	treeItem = new TreeItem(columnsTreeItem, SWT.NONE);
                            	TableColumn tableColumn;
                	            switch ( clazz.toLowerCase() ) {
	            	                case "check":
	            	                	tableColumn = (TableColumn)jsonParser.createCheckColumn(jsonColumn, (Table)widget);
	            	                	treeItem.setImage(CHECK_ICON);
	            	                	break;
	            		            case "combo":
	            	                	tableColumn = (TableColumn)jsonParser.createComboColumn(jsonColumn, (Table)widget);
	            	                	treeItem.setImage(COMBO_ICON);
	            	                	break;
	            	                case "label":
	            	                	tableColumn = (TableColumn)jsonParser.createLabelColumn(jsonColumn, (Table)widget);
	            	                	treeItem.setImage(LABEL_ICON);
	            	                	break;
	            	                case "text":
	            	                	tableColumn = (TableColumn)jsonParser.createTextColumn(jsonColumn, (Table)widget);
	            	                	treeItem.setImage(TEXT_ICON);
	            	                	break;
	            	                default:
	            	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + jsonControl.get("class") + "\" (valid values are \"check\", \"combo\", \"label\", \"text\").");
                	            }
            	                	
	    	    	            treeItem.setText(tableColumn.getData("name")==null ? "" : (String)tableColumn.getData("name"));
	    	    	            treeItem.setData("class", clazz+"Column");
	    	    	            treeItem.setData("control", tableColumn);
	    	    	            
	    	    	            tableColumn.setData("treeItem", treeItem);
	    	    	            tableColumn.setData("class", clazz+"Column");
                            }
                        }
                    }
    	            
    	            TreeItem linesTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
    	            linesTreeItem.setImage(LINE_ICON);
    	            linesTreeItem.setText("lines");
    	            linesTreeItem.setData("class", "lines");
    	            
    	            
    	            JSONArray lines = jsonParser.getJSONArray(jsonControl, "lines");
                    if ( lines != null ) {
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> linesIterator = lines.iterator();
                        while (linesIterator.hasNext()) {
                            JSONObject jsonLine = linesIterator.next();
                            
                            treeItem = new TreeItem(linesTreeItem, SWT.NONE);
                            TableItem tableItem = (TableItem)jsonParser.createLine(jsonLine, (Table)widget);
                            
            	            treeItem.setData("class", "line");
    	    	            treeItem.setData("control", tableItem);
            	            treeItem.setImage(LINE_ICON);
            	            treeItem.setText(tableItem.getData("name")==null ? "" : (String)tableItem.getData("name"));
                        }
                    }
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
	/*
    @SuppressWarnings("unchecked")
    private void createTable(JSONObject jsonObject, TreeItem tabTreeItem, Composite composite) throws RuntimeException {

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
    }
            */

    private void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");
        
        close();
    }

    private void save() {
        if (logger.isDebugEnabled())
            logger.debug("Save button selected by user.");

        JSONObject json = jsonParser.generateJSON(tree.getItems()[0], formDialog);

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
