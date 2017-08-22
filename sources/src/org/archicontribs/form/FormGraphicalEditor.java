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
    public final static int   editorVerticalMargin = 5;

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
    
    private String           variableSeparator = null;
    private String           globalWhenEmpty   = null;
    
    public FormGraphicalEditor(String configFilename, JSONObject json) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        try {
            createForm(json);
        } catch (IOException e) {
            FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file \"" + configFilename + "\"", e);
            close();
            return;
        } catch (ParseException e) {
            FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \"" + configFilename + "\"", e);
            close();
            return;
        } catch (ClassCastException e) {
            FormDialog.popup(Level.ERROR, "Wrong key type in the configuration file \"" + configFilename + "\"", e);
            close();
            return;
        } catch (RuntimeException e) {
            FormDialog.popup(Level.ERROR, "Please check your configuration file \"" + configFilename +"\"", e);
            close();
            return;
        }

        propertiesDialog.open();
        propertiesDialog.layout();
        
        formDialog.open();
        formDialog.layout();
    }

    /**
     * Parses the configuration file and create the corresponding graphical controls
     */
    private void createForm(JSONObject form) throws IOException, ParseException, RuntimeException {
        // we gather the config file content
        String formName = FormDialog.getString(form, "name", "");
        FormPosition.setFormName(formName);
        variableSeparator = FormDialog.getString(form, "variableSeparator", FormDialog.defaultVariableSeparator);
        int dialogWidth = FormDialog.getInt(form, "width", FormDialog.defaultDialogWidth);
        int dialogHeight = FormDialog.getInt(form, "height", FormDialog.defaultDialogHeight);
        int dialogSpacing = FormDialog.getInt(form, "spacing", FormDialog.defaultDialogSpacing);
        String dialogBackground = FormDialog.getString(form, "background", FormDialog.defaultDialogBackground);
        String dialogForeground = FormDialog.getString(form, "foreground", FormDialog.defaultDialogForeground);
        String refers = FormDialog.getString(form, "refers", FormDialog.defaultRefers);
        int buttonWidth = FormDialog.getInt(form, "buttonWidth", FormDialog.defaultButtonWidth);
        int buttonHeight = FormDialog.getInt(form, "buttonHeight", FormDialog.defaultButtonHeight);
        String buttonOkText = FormDialog.getString(form, "buttonOk", FormDialog.defaultButtonOkText);
        String buttonCancelText = FormDialog.getString(form, "buttonCancel", FormDialog.defaultButtonCancelText);
        String buttonExportText = FormDialog.getString(form, "buttonExport", FormDialog.defaultButtonExportText);
        globalWhenEmpty = FormDialog.getString(form, "whenEmpty", "");

        if (logger.isTraceEnabled()) {
            logger.trace("   name = " + FormDialog.debugValue(formName, ""));
            logger.trace("   variableSeparator = " + FormDialog.debugValue(variableSeparator, FormDialog.defaultVariableSeparator));
            logger.trace("   width = " + FormDialog.debugValue(dialogWidth, FormDialog.defaultDialogWidth));
            logger.trace("   height = " + FormDialog.debugValue(dialogHeight, FormDialog.defaultDialogHeight));
            logger.trace("   spacing = " + FormDialog.debugValue(dialogSpacing, FormDialog.defaultDialogSpacing));
            logger.trace("   background = " + FormDialog.debugValue(dialogBackground, FormDialog.defaultDialogBackground));
            logger.trace("   foreground = " + FormDialog.debugValue(dialogForeground, FormDialog.defaultDialogForeground));
            logger.trace("   buttonWidth = " + FormDialog.debugValue(buttonWidth, FormDialog.defaultButtonWidth));
            logger.trace("   buttonHeight = " + FormDialog.debugValue(buttonHeight, FormDialog.defaultButtonHeight));
            logger.trace("   refers = " + FormDialog.debugValue(refers, FormDialog.defaultRefers));
            logger.trace("   buttonOk = " + FormDialog.debugValue(buttonOkText, FormDialog.defaultButtonOkText));
            logger.trace("   buttonCancel = " + FormDialog.debugValue(buttonCancelText, FormDialog.defaultButtonCancelText));
            logger.trace("   buttonExport = " + FormDialog.debugValue(buttonExportText, FormDialog.defaultButtonExportText));
            logger.trace("   whenEmpty = " + FormDialog.debugValue(globalWhenEmpty, ""));
        }
        
        // we create the formDialog
        
        formDialog  = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );
        formDialog.setText(formName);
        formDialog.setSize(dialogWidth, dialogHeight);
        formDialog.setLayout(null);
        
        int tabFolderWidth = dialogWidth - dialogSpacing * 2;
        int tabFolderHeight = dialogHeight - dialogSpacing * 3 - buttonHeight;
        
        formDialog.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - dialogWidth) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogHeight) / 4, dialogWidth, dialogHeight);
        // we resize the dialog because we want the width and height to be the
        // client's area width and height
        Rectangle area = formDialog.getClientArea();
        formDialog.setSize(dialogWidth * 2 - area.width, dialogHeight * 2 - area.height);
        
        if ( !FormPlugin.isEmpty(dialogBackground) ) {
            String[] colorArray = dialogBackground.split(",");
            formDialog.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        TabFolder tabFolder = new TabFolder(formDialog, SWT.NONE);
        formDialog.setData("tabFolder", tabFolder);
        tabFolder.setBounds(dialogSpacing, dialogSpacing, tabFolderWidth, tabFolderHeight);

        Button buttonCancel = new Button(formDialog, SWT.NONE);
        buttonCancel.setBounds(tabFolderWidth + dialogSpacing - buttonWidth, tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
        buttonCancel.setText(buttonCancelText);
        buttonCancel.setEnabled(true);

        Button buttonOk = new Button(formDialog, SWT.NONE);
        buttonOk.setBounds(tabFolderWidth + dialogSpacing - buttonWidth * 2 - dialogSpacing, tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
        buttonOk.setText(buttonOkText);
        buttonOk.setEnabled(true);
        
        // If there is at least one Excel sheet specified, then we show up the
        // "export to Excel" button
        //if (!excelSheets.isEmpty()) {
        Button buttonExport = new Button(formDialog, SWT.NONE);
        buttonExport.setBounds(tabFolderWidth + dialogSpacing - buttonWidth * 3 - dialogSpacing * 2, tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
        buttonExport.setText(buttonExportText);
        buttonExport.setEnabled(true);
        //}
        
        formDialog.setData("name", formName);
        formDialog.setData("variableSeparator", variableSeparator);
        formDialog.setData("width", dialogWidth);
        formDialog.setData("height", dialogHeight);
        formDialog.setData("spacing", dialogSpacing);
        formDialog.setData("background", dialogBackground);
        formDialog.setData("foreground", dialogForeground);
        formDialog.setData("buttonWidth", buttonWidth);
        formDialog.setData("buttonHeight", buttonHeight);
        formDialog.setData("refers", refers);
        formDialog.setData("buttonOk", buttonOkText);
        formDialog.setData("buttonCancel", buttonCancelText);
        formDialog.setData("buttonExport", buttonExportText);
        formDialog.setData("whenEmpty", globalWhenEmpty);
        
        formDialog.setData("buttonOkControl", buttonOk);
        formDialog.setData("buttonCancelControl", buttonCancel);
        formDialog.setData("buttonExportControl", buttonExport);
        formDialog.setData("keys",  new String[] {"name", /*"variableSeparator",*/ "width", "height", "spacing", "background", "foreground", "buttonWidth","buttonHeight", "refers", "buttonOk", "buttonCancel", "buttonExport"});
        
        // we create the propertiesDialog
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
        tree.addListener(SWT.Selection, new Listener() {
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
                        
                        for ( String key: (String [])control.getData("keys") ) {
                        	composite.set(key, control.getData(key));
                        }
                	}
                }
            }
        });
        
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
        formTreeItem.setText("Form: "+formName);
        formTreeItem.setData("class", "form");
        formTreeItem.setData("control", formDialog);
        
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
        
        createTabs(form, formTreeItem, tabFolder);
        formTreeItem.setExpanded(true);
        
        tree.setSelection(formTreeItem);
        tree.notifyListeners(SWT.Selection, new Event());        // shows up the form's properties and display the corresponding form
    }

    /**
     * Creates the dialog tabItems<br>
     * <br>
     * called by the createContents() method
     */
    private void createTabs(JSONObject form, TreeItem formTreeItem, TabFolder tabFolder) throws RuntimeException {
    	// we iterate over the "tabs" array attributes
        JSONArray tabs = FormDialog.getJSONArray(form, "tabs");

        @SuppressWarnings("unchecked")
        Iterator<JSONObject> tabsIterator = tabs.iterator();
        while (tabsIterator.hasNext()) {
            JSONObject tab = tabsIterator.next();
            String tabName = FormDialog.getString(tab, "name", "");

            FormPosition.setTabName(tabName);

            if (logger.isDebugEnabled())
                logger.debug("Creating tab \"" + FormDialog.debugValue(tabName, FormDialog.defaultTabName) + "\"");

            String tabBackground = FormDialog.getString(tab, "background", FormDialog.defaultTabBackground);
            String tabForeground = FormDialog.getString(tab, "foreground", FormDialog.defaultTabForeground);

            if (logger.isTraceEnabled()) {
                logger.trace("   background = " + FormDialog.debugValue(tabBackground, FormDialog.defaultTabBackground));
                logger.trace("   foreground = " + FormDialog.debugValue(tabForeground, FormDialog.defaultTabForeground));
            }
            
            TabItem tabItem = new TabItem(tabFolder, SWT.MULTI);
            tabItem.setText(tabName);
            
            Composite composite = new Composite(tabFolder, SWT.NONE);
            tabItem.setControl(composite);
            
            composite.setData("name", tabName);
            composite.setData("background",  tabBackground);
            composite.setData("foreground",  tabForeground);
            composite.setData("keys", new String[] {"name", "background", "foreground"});
            
            if ( !FormPlugin.isEmpty(tabBackground) ) {
                String[] colorArray = tabBackground.split(",");
                composite.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
            }
            
            TreeItem tabTreeItem = new TreeItem(formTreeItem, SWT.NONE);
            tabTreeItem.setText("Tab: "+tabName);
            tabTreeItem.setData("class", "tab");
            tabTreeItem.setData("composite", tabComposite);
            tabTreeItem.setData("control", composite);

            createControls(tab, tabTreeItem, composite);
            composite.layout();
        }
    }

    /**
     * Creates the dialog controls. The following controls are currently managed:<br>
     * <li>check
     * <li>combo
     * <li>label
     * <li>table
     * <li>text
     * <br>
     * called by the createTabs() method
     */
    private void createControls(JSONObject tab, TreeItem tabTreeItem, Composite composite) throws RuntimeException {
        // we iterate over the "controls" entries
        @SuppressWarnings("unchecked")
        Iterator<JSONObject> objectsIterator = FormDialog.getJSONArray(tab, "controls").iterator();
        while (objectsIterator.hasNext()) {
            JSONObject jsonObject = objectsIterator.next();
            
            String controlClass = FormDialog.getString(jsonObject, "class").toLowerCase();
            String name = FormDialog.getString(jsonObject, "name", "");
            
            FormPosition.setControlName(name);
            FormPosition.setControlClass(controlClass);
           
            switch ( controlClass ) {
                case "check":
                    createCheck(jsonObject, tabTreeItem, composite);
                    break;
                case "combo":
                    createCombo(jsonObject, tabTreeItem, composite);
                    break;
                case "label":
                    createLabel(jsonObject, tabTreeItem, composite);
                    break;
                case "table":
                    createTable(jsonObject, tabTreeItem, composite);
                    break;
                case "text":
                    createText(jsonObject, tabTreeItem, composite);
                    break;
                default:
                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + jsonObject.get("class") + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
            }
            FormPosition.resetControlName();
            FormPosition.resetControlClass();
        }
    }

    /**
     * Create a Label control<br>
     * <br>
     * called by the createObjects() method
     */
    private void createLabel(JSONObject jsonObject, TreeItem tabTreeItem, Composite composite) {
    	Label label = FormDialog.createLabel(jsonObject, composite);
    	label.setText((String)label.getData("text"));
 	
        TreeItem treeItem = new TreeItem(tabTreeItem, SWT.NONE);
        treeItem.setText("Label: "+ (String)label.getData("name"));
        treeItem.setData("composite", labelComposite);
        treeItem.setData("class", "label");
        treeItem.setData("control", label);        
    }

    /**
     * Create a text control<br>
     * <br>
     * called by the createObjects() method
     */
    private void createText(JSONObject jsonObject, TreeItem tabTreeItem, Composite composite) throws RuntimeException {
    	StyledText text = FormDialog.createText(jsonObject, composite);
    	text.setText((String)text.getData("variable"));
    	
        TreeItem treeItem = new TreeItem(tabTreeItem, SWT.NONE);
        treeItem.setText("Text: "+ (String)text.getData("name"));
        
        treeItem.setData("composite", textComposite);
        treeItem.setData("class", "text");        
        treeItem.setData("control", text);
    }

    /**
     * Create a Combo control<br>
     * <br>
     * called by the createObjects() method
     */
    private void createCombo(JSONObject jsonObject, TreeItem tabTreeItem, Composite composite) throws RuntimeException {
        CCombo combo = FormDialog.createCombo(jsonObject, composite);
        combo.setText((String)combo.getData("variable"));
        
        TreeItem treeItem = new TreeItem(tabTreeItem, SWT.NONE);
        treeItem.setText("Combo: "+ (String)combo.getData("name"));
        
        treeItem.setData("composite", textComposite);
        treeItem.setData("class", "combo");        
        treeItem.setData("control", combo);
    }

    /**
     * Create a check button control<br>
     * <br>
     * called by the createObjects() method
     * 
     * @param jsonObject
     *            the JSON object to parse
     * @param composite
     *            the composite where the control will be created
     */
    private void createCheck(JSONObject jsonObject, TreeItem tabTreeItem, Composite composite) throws RuntimeException {
        String checkName = FormDialog.getString(jsonObject, "name", "");
        
        if (logger.isDebugEnabled())
            logger.debug("   Creating Check \"" + checkName + "\"");
        
        FormPosition.setControlName(checkName);
        FormPosition.setControlClass("check");
        
        String variableName = FormDialog.getString(jsonObject, "variable", "");
        @SuppressWarnings("unchecked")
        String[] values = (String[]) (FormDialog.getJSONArray(jsonObject, "values")).toArray(new String[0]);
        String defaultValue = FormDialog.getString(jsonObject, "default", "");
        boolean forceDefault = FormDialog.getBoolean(jsonObject, "forceDefault", false);
        int x = FormDialog.getInt(jsonObject, "x", 0);
        int y = FormDialog.getInt(jsonObject, "y", 0);
        int width = FormDialog.getInt(jsonObject, "width", 0);
        int height = FormDialog.getInt(jsonObject, "height", 0);
        boolean editable = FormDialog.getBoolean(jsonObject, "editable", true);
        String background = FormDialog.getString(jsonObject, "background", "");
        String foreground = FormDialog.getString(jsonObject, "foreground", "");
        String tooltip = FormDialog.getString(jsonObject, "tooltip", "");
        String alignment = FormDialog.getString(jsonObject, "alignment", "left");
        String whenEmpty = FormDialog.getString(jsonObject, "whenEmpty", globalWhenEmpty);
        String excelSheet = FormDialog.getString(jsonObject, "excelSheet", "");
        String excelCell = FormDialog.getString(jsonObject, "excelCell", "");
        String excelCellType = FormDialog.getString(jsonObject, "excelCellType", "string");
        String excelDefault = FormDialog.getString(jsonObject, "excelDefault", "blank");

        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + FormDialog.debugValue(x, 0));
            logger.trace("      y = " + FormDialog.debugValue(y, 0));
            logger.trace("      width = " + FormDialog.debugValue(width, 0));
            logger.trace("      height = " + FormDialog.debugValue(height, 0));
            logger.trace("      background = " + FormDialog.debugValue(background, ""));
            logger.trace("      foreground = " + FormDialog.debugValue(foreground, ""));
            logger.trace("      alignment = "+FormDialog.debugValue(alignment, "left"));
            logger.trace("      values = " + values);
            logger.trace("      default = " + FormDialog.debugValue(defaultValue, ""));
            logger.trace("      forceDefault = " + FormDialog.debugValue(forceDefault, false));
            logger.trace("      tooltip = " + FormDialog.debugValue(tooltip, ""));
            logger.trace("      editable = " + FormDialog.debugValue(editable, true));
            logger.trace("      whenEmpty = " + FormDialog.debugValue(whenEmpty, globalWhenEmpty));
            logger.trace("      excelSheet = " + FormDialog.debugValue(excelSheet, ""));
            logger.trace("      excelCell = " + FormDialog.debugValue(excelCell, ""));
            logger.trace("      excelCellType = " + FormDialog.debugValue(excelCellType, "string"));
            logger.trace("      excelDefault = " + FormDialog.debugValue(excelDefault, "blank"));
        }

        TreeItem comboTreeItem = new TreeItem(tabTreeItem, SWT.NONE);
        comboTreeItem.setText("Check: "+ checkName);
        comboTreeItem.setData("class", "check");
        comboTreeItem.setData("composite", comboComposite);
        
        comboTreeItem.setData("name", checkName);
        comboTreeItem.setData("x", x);
        comboTreeItem.setData("y", y);
        comboTreeItem.setData("width", width);
        comboTreeItem.setData("height", height);
        comboTreeItem.setData("variable", variableName);
        comboTreeItem.setData("values", values);
        comboTreeItem.setData("default", defaultValue);
        comboTreeItem.setData("forceDefault", forceDefault);
        comboTreeItem.setData("background", background);
        comboTreeItem.setData("foreground", foreground);
        comboTreeItem.setData("tooltip", tooltip);
        comboTreeItem.setData("alignment", alignment);
        comboTreeItem.setData("editable", editable);
        comboTreeItem.setData("whenEmpty", whenEmpty);
        comboTreeItem.setData("excelSheet", excelSheet);
        comboTreeItem.setData("excelCell", excelCell);
        comboTreeItem.setData("excelCellType", excelCellType);
        comboTreeItem.setData("excelDefault", excelDefault);
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
        String tableName = FormDialog.getString(jsonObject, "name", "");
        
        if (logger.isDebugEnabled())
            logger.debug("   Creating table \"" + tableName + "\"");
        
        FormPosition.setControlName(tableName);
        FormPosition.setControlClass("table");
        
        int x = FormDialog.getInt(jsonObject, "x", 0);
        int y = FormDialog.getInt(jsonObject, "y", 0);
        int width = FormDialog.getInt(jsonObject, "width", 100);
        int height = FormDialog.getInt(jsonObject, "height", 50);
        String background = FormDialog.getString(jsonObject, "background", "");
        String foreground = FormDialog.getString(jsonObject, "foreground", "");
        String tooltip = FormDialog.getString(jsonObject, "tooltip", "");
        String excelSheet = FormDialog.getString(jsonObject, "excelSheet", "");
        int excelFirstLine = FormDialog.getInt(jsonObject, "excelFirstLine", 1);
        int excelLastLine = FormDialog.getInt(jsonObject, "excelLastLine", 0);

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
        Iterator<JSONObject> columnsIterator = (FormDialog.getJSONArray(jsonObject, "columns")).iterator();
        while (columnsIterator.hasNext()) {
            JSONObject column = columnsIterator.next();

            String columnName = FormDialog.getString(column, "name", "");
            FormPosition.setColumnName(columnName);
            
            String columnClass = FormDialog.getString(column, "class").toLowerCase();
            
            tooltip = FormDialog.getString(column, "tooltip", "");
            width = FormDialog.getInt(column, "width", 0);
            background = FormDialog.getString(column, "background", "");
            foreground = FormDialog.getString(column, "foreground", "");
            String excelColumn = FormDialog.getString(column, "excelColumn", "");
            String excelCellType = FormDialog.getString(column, "excelCellType", "string");
            String excelDefault = FormDialog.getString(column, "excelDefault", "blank");

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
                    alignment = FormDialog.getString(column, "alignment", "left");
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("      alignment = "+FormDialog.debugValue(alignment, "left"));
                    }
                    
                    columnTreeItem.setData("alignment", alignment);
                    break;
                    
                case "text":
                    String regexp = FormDialog.getString(column, "regexp", "");
                    String defaultText = FormDialog.getString(column, "default", "");
                    String whenEmpty = FormDialog.getString(column, "whenEmpty", globalWhenEmpty);
                    boolean forceDefault = FormDialog.getBoolean(column, "forceDefault", false);
                    alignment = FormDialog.getString(column, "alignment", "left");

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
                    String[] values = (String[]) (FormDialog.getJSONArray(column, "values")).toArray(new String[0]);
                    String defaultValue = FormDialog.getString(column, "default", "");
                    Boolean editable = FormDialog.getBoolean(column, "editable", true);
                    whenEmpty = FormDialog.getString(jsonObject, "whenEmpty", globalWhenEmpty);
                    alignment = FormDialog.getString(column, "alignment", "left");

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
                    values = (String[]) (FormDialog.getJSONArray(column, "values")).toArray(new String[0]);
                    defaultValue = FormDialog.getString(column, "default", "");
                    forceDefault = FormDialog.getBoolean(column, "forceDefault", false);
                    whenEmpty = FormDialog.getString(jsonObject, "whenEmpty", globalWhenEmpty);
                    alignment = FormDialog.getString(column, "alignment", "left");

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
                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" +FormDialog.getString(column, "class") + "\" (valid values are \"check\", \"combo\", \"label\" and \"text\").");
            }
        }
        FormPosition.resetColumnName();


        // we iterate over the "lines" entries
        JSONArray lines = FormDialog.getJSONArray(jsonObject, "lines");
        if (lines != null) {
            Iterator<JSONObject> linesIterator = lines.iterator();
            while (linesIterator.hasNext()) {
                JSONObject line = linesIterator.next();
                
                TreeItem lineTreeItem = new TreeItem(linesTreeItem, SWT.NONE);
                
                lineTreeItem.setData("cells", FormDialog.getJSONArray(line, "cells"));
                lineTreeItem.setData("class", "line");
                
                if ((boolean) FormDialog.getJSON(line, "generate", false) == false) {
                    // static line
                    if (logger.isTraceEnabled())
                        logger.trace("Creating static line");
                                        lineTreeItem.setText("Static line");
                } else {
                    // dynamic lines : we create one line per entry in getChildren()
                    if (logger.isTraceEnabled())
                        logger.trace("Generating dynamic lines");
                    
                    lineTreeItem.setText("Dynamic lines");
                    lineTreeItem.setData("filter", FormDialog.getJSONObject(line, "filter"));
                }
            }
        }
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
