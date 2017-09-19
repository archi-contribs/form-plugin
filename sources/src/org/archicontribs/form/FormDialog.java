package org.archicontribs.form;

import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
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
public class FormDialog extends Dialog {
    private static final FormLogger logger     	          = new FormLogger(FormDialog.class);
    
    public final static Display  display        	      = Display.getDefault();
    
    public static final FontData SYSTEM_FONT              = display.getSystemFont().getFontData()[0];
    public static final Font     TITLE_FONT               = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight() + 2, SWT.BOLD);
    public static final Font     BOLD_FONT                = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight(), SWT.BOLD);

    public static final Color    LIGHT_GREEN_COLOR        = new Color(display, 204, 255, 229);
    public static final Color    LIGHT_RED_COLOR          = new Color(display, 255, 230, 230);
    public static final Color    RED_COLOR                = new Color(display, 240, 0, 0);
    public static final Color    GREEN_COLOR              = new Color(display, 0, 180, 0);
    public static final Color    WHITE_COLOR              = new Color(display, 255, 255, 255);
    public static final Color    GREY_COLOR               = new Color(display, 100, 100, 100);
    public static final Color    BLACK_COLOR              = new Color(display, 0, 0, 0);
    public static final Color    LIGHT_BLUE               = new Color(display, 240, 248, 255);

    private static final Color   badValueColor            = new Color(display, 255, 0, 0);
    private static final Color   goodValueColor           = new Color(display, 0, 100, 0);
    
    public final static Image    binImage         	      = new Image(display, FormDialog.class.getResourceAsStream("/icons/bin.png"));
    public final static Color    blackColor       	      = new Color(display, 0, 0, 0);
    public final static Color    whiteColor       	      = new Color(display, 255, 255, 255);
    
    public final static int      editorLeftposition       = 110;
    public final static int      editorBorderMargin       = 10;
    public final static int      editorVerticalMargin     = 10;

    public static final int      defaultDialogWidth       = 850;
    public static final int      defaultDialogHeight      = 600;
    public static final int      defaultDialogSpacing     = 4;
    public static final String   defaultDialogName        = "Form plugin";
    public static final String   defaultTabBackground     = "";
    public static final String   defaultTabForeground     = "";
    public static final int      defaultButtonWidth       = 90;
    public static final int      defaultButtonHeight      = 25;
    public static final String   defaultButtonOkText      = "Ok";
    public static final String   defaultButtonCancelText  = "Cancel";
    public static final String   defaultButtonExportText  = "Export to Excel";
    public static final String   defaultTabName           = "tab";
    public static final String   defaultVariableSeparator = ":";
    public static final String[] validAlignment           = new String[] {"left", "center", "right"};						// default value is first one
    public static final String[] validRefers              = new String[] {"selected", "container", "model"};				// default value is first one
    public static final String[] validWhenEmpty           = new String[] {"ignore", "create", "delete" };					// default value is first one
    public static final String[] validExcelCellType       = new String[] {"string", "numeric", "boolean", "formula" };		// default value is first one
    public static final String[] validExcelDefault        = new String[] {"blank", "zero", "delete" };						// default value is first one
    
	public static final Image    BIN_ICON                 = new Image(display, FormDialog.class.getResourceAsStream("/icons/bin.png"));
	public static final Image    BAS_ICON                 = new Image(display, FormDialog.class.getResourceAsStream("/icons/flèche_bas.png"));
	public static final Image    HAUT_ICON                = new Image(display, FormDialog.class.getResourceAsStream("/icons/flèche_haut.png"));
	public static final Image    PLUS_ICON                = new Image(display, FormDialog.class.getResourceAsStream("/icons/plus.png"));
	
	
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
    
    private Shell                formDialog        	  = null;
    private Button               btnUp                = null;
    private Button               btnDown              = null;
    
    private enum                 Position {Before, After, Into, End};
    private String               configFilename = null;
    private boolean              editMode = false;
    
    private static final FormJsonParser jsonParser = new FormJsonParser();
    
    
    
    public FormDialog(String configFilename, JSONObject jsonForm, EObject selectedObject) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        
        this.configFilename = configFilename;

        try {
        	formDialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        	
            TreeItem formTreeItem = null;
            // If the dialog is ran against no EObject, then we switch to edit mode 
            editMode = ( selectedObject == null );
            
            if ( editMode )
            	formTreeItem = createPropertiesDialog(jsonForm);
            
            jsonParser.createForm(jsonForm, formDialog, formTreeItem);

            // we create one CTabItem per tab array item
            CTabFolder tabFolder = (CTabFolder)formDialog.getData("tab folder");
            JSONArray tabs = jsonParser.getJSONArray(jsonForm, "tabs");
            if ( tabs != null ) {
            	@SuppressWarnings("unchecked")
				Iterator<JSONObject> tabsIterator = tabs.iterator();
                while (tabsIterator.hasNext()) {
                    JSONObject jsonTab = tabsIterator.next();

                    TreeItem tabTreeItem = null;
                    if ( editMode )
                    	tabTreeItem = new TreeItem(formTreeItem, SWT.NONE);
                    
                    CTabItem tabItem = jsonParser.createTab(jsonTab, tabFolder, tabTreeItem);
                    
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
            
            if ( editMode ) {
            	formTreeItem.setExpanded(true);
            
            	tree.setSelection(formTreeItem);
            	tree.notifyListeners(SWT.Selection, new Event());        // shows up the form's properties
            }
           
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
        
        if ( selectedObject == null ) {
        	propertiesDialog.open();
            propertiesDialog.layout();
        }
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
        
        Sash sash = new Sash(propertiesDialog, SWT.VERTICAL | SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(40, -15);
        fd.bottom = new FormAttachment(40, 15);
        fd.left = new FormAttachment(0, 200);
        sash.setLayoutData(fd);
        sash.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
              ((FormData) sash.getLayoutData()).left = new FormAttachment(0, event.x);
              sash.getParent().layout();
            }
          });
        
        btnUp = new Button(propertiesDialog, SWT.PUSH);
        btnUp.setImage(HAUT_ICON);
        btnUp.setSize(16,16);
        btnUp.pack();
        fd = new FormData();
        fd.left = new FormAttachment(sash, 0, SWT.CENTER);
        fd.top = new FormAttachment(sash, -17, SWT.TOP);
        fd.bottom = new FormAttachment(sash, -1, SWT.TOP);
        btnUp.setLayoutData(fd);
        btnUp.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	tree.setRedraw(false);

            	TreeItem selectedTreeItem = tree.getSelection()[0];
                TreeItem parentTreeItem = selectedTreeItem.getParentItem();
                int index = parentTreeItem.indexOf(selectedTreeItem);
                
           		TreeItem newTreeItem = moveTreeItem(parentTreeItem, selectedTreeItem, index-1);

           		tree.setSelection(newTreeItem);
                tree.showSelection();
                tree.setRedraw(true);
                
                Widget widget = (Widget)newTreeItem.getData("widget");
                switch ( widget.getClass().getSimpleName() ) {
                	case "Composite":
                		// moving a tabItem
                		CTabFolder tabFolder = (CTabFolder)formDialog.getData("tab folder");
                		for ( int i=0; i < tabFolder.getItemCount(); ++i ) {
                			if ( tabFolder.getItem(i).getControl() == widget ) {
                        		CTabItem oldCTabItem = tabFolder.getItem(i);
                				CTabItem newCTabItem = new CTabItem(tabFolder, SWT.NONE, i-1);
                        		newCTabItem.setText(oldCTabItem.getText());
                        		newCTabItem.setControl(oldCTabItem.getControl());
                        		newTreeItem.setData("widget", newCTabItem.getControl());
                        		oldCTabItem.dispose();
                        		break;
                			}
                		}
                		break;
                		
            		case "TableColumn":
                		// moving a table column
                		TableColumn oldTableColumn = (TableColumn)widget;
                		Table table = oldTableColumn.getParent();
                		index = table.indexOf(oldTableColumn);
                		
                		TableColumn newTablecolumn = new TableColumn(table, SWT.NONE, index-1);
                		newTablecolumn.setText(oldTableColumn.getText());
                		newTablecolumn.setWidth(oldTableColumn.getWidth());
                		for ( TableItem tableItem: table.getItems() ) {
                			TableEditor[] editors = (TableEditor[])tableItem.getData("editors");
                			// we switch the editors
                			TableEditor editor1 = editors[index];
                			TableEditor editor2 = editors[index-1];
                			editors[index-1] = editor1;
                			editors[index] = editor2;
                			editor1.setEditor(editor1.getEditor(), tableItem, index-1);
                			editor2.setEditor(editor2.getEditor(), tableItem, index);
                		}
                		newTreeItem.setData("widget", newTablecolumn);
                        oldTableColumn.dispose();
                        break;
                }
            }
        });
        
        btnDown = new Button(propertiesDialog, SWT.PUSH);
        btnDown.setImage(BAS_ICON);
        btnDown.setSize(16,16);
        btnDown.pack();
        fd = new FormData();
        fd.left = new FormAttachment(sash, 0, SWT.CENTER);
        fd.top = new FormAttachment(sash, 1, SWT.BOTTOM);
        fd.bottom = new FormAttachment(sash, 17, SWT.BOTTOM);
        btnDown.setLayoutData(fd);
        btnDown.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	tree.setRedraw(false);

            	TreeItem selectedTreeItem = tree.getSelection()[0];
                TreeItem parentTreeItem = selectedTreeItem.getParentItem();
                int index = parentTreeItem.indexOf(selectedTreeItem);
           		TreeItem newTreeItem = moveTreeItem(parentTreeItem, selectedTreeItem, index+2);
           		
                tree.setSelection(newTreeItem);
                tree.showSelection();
                tree.setRedraw(true);
                
                Widget widget = (Widget)newTreeItem.getData("widget");
                switch ( widget.getClass().getSimpleName() ) {
                	case "Composite":
                		// moving a tabItem
                		CTabFolder tabFolder = (CTabFolder)formDialog.getData("tab folder");
                		for ( int i=0; i < tabFolder.getItemCount(); ++i ) {
                			if ( tabFolder.getItem(i).getControl() == widget ) {
                        		CTabItem oldCTabItem = tabFolder.getItem(i);
                				CTabItem newCTabItem = new CTabItem(tabFolder, SWT.NONE, i+2);
                        		newCTabItem.setText(oldCTabItem.getText());
                        		newCTabItem.setControl(oldCTabItem.getControl());
                        		newTreeItem.setData("widget", newCTabItem.getControl());
                        		oldCTabItem.dispose();
                        		break;
                			}
                		}
                		break;
                		
                	case "TableColumn":
                		// moving a table column
                		TableColumn oldTableColumn = (TableColumn)widget;
                		Table table = oldTableColumn.getParent();
                		index = table.indexOf(oldTableColumn);
                		
                		TableColumn newTablecolumn = new TableColumn(table, SWT.NONE, index+2);
                		newTablecolumn.setText(oldTableColumn.getText());
                		newTablecolumn.setWidth(oldTableColumn.getWidth());
                		for ( TableItem tableItem: table.getItems() ) {
                			TableEditor[] editors = (TableEditor[])tableItem.getData("editors");
                			// we switch the editors
                			TableEditor editor1 = editors[index];
                			TableEditor editor2 = editors[index+1];
                			editors[index+1] = editor1;
                			editors[index] = editor2;
                			editor1.setEditor(editor1.getEditor(), tableItem, index+1);
                			editor2.setEditor(editor2.getEditor(), tableItem, index);
                		}
                		newTreeItem.setData("widget", newTablecolumn);
                        oldTableColumn.dispose();
                        break;
                }
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
                TreeItem selectedTreeItem = tree.getSelection()[0];
                TreeItem parentTreeItem = selectedTreeItem.getParentItem();
                
                // we empty the previous menu
                MenuItem[] items = treeMenu.getItems();
                for (int i = 0; i < items.length; i++) {
                	if ( items[i].getMenu() != null ) {
                		MenuItem[] subItems = items[i].getMenu().getItems();
                		for (int j = 0; j < subItems.length; j++)
                			subItems[j].dispose();
                	}
                	items[i].dispose();
                }
                
                MenuItem newItem;
            	if ( parentTreeItem == null ) {		// the selectedTreeItem is the form
                	newItem = new MenuItem(treeMenu, SWT.NONE);
                	newItem.setText("Add new tab");
                	newItem.setImage(FormJsonParser.TAB_ICON);
                	newItem.setData("position", Position.End);
                	newItem.addSelectionListener(addTabListener);
            	} else {
            		if ( !selectedTreeItem.getData("class").equals("columns") && !selectedTreeItem.getData("class").equals("lines") ) {
	                	newItem = new MenuItem(treeMenu, SWT.NONE);
	                	newItem.setText("Delete "+selectedTreeItem.getData("class"));
	                	newItem.setImage(BIN_ICON);
	                	newItem.addSelectionListener(deleteListener);
            		}
                	
                	addSubMenu(selectedTreeItem, "Insert", "before", Position.Before);
                	addSubMenu(selectedTreeItem, "Add", "after", Position.After);
                } 
            }
            
            private void addItemsToSubMenu(String prefix, String suffix, Menu treeMenu) {
            	MenuItem newItem = new MenuItem(treeMenu, SWT.CASCADE);
            	newItem.setText(prefix + " " + suffix + "...");
            	Position position;
            	switch (suffix) {
            		case "before": position = Position.Before; break;
            		case "after":  position = Position.After; break;
                    case "column":
                    case "line":
            		case "into":   position = Position.Into; break;
            		default:       position = Position.After;
            	}
            	
            	Menu subMenu = new Menu(treeMenu);
            	newItem.setMenu(subMenu);
            	
            	Boolean showTable = !( suffix.equals("column") || suffix.equals("line") || prefix.endsWith("column") || prefix.endsWith("line") );
            	
            	addItemsToMenu(subMenu, position, showTable);
            }
            
            private void addItemsToMenu(Menu subMenu, Position position, boolean showTable) {
            	MenuItem newItem;
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("label");
            	newItem.setImage(FormJsonParser.LABEL_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "label");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("text");
            	newItem.setImage(FormJsonParser.TEXT_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "text");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("combo");
            	newItem.setImage(FormJsonParser.COMBO_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "combo");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("check box");
            	newItem.setImage(FormJsonParser.CHECK_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "check");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	if ( showTable ) {
            		newItem = new MenuItem(subMenu, SWT.NONE);
                	newItem.setText("table");
                	newItem.setImage(FormJsonParser.TABLE_ICON);
                	newItem.setData("position", position);
                	newItem.addSelectionListener(addTableListener);
            	}
            }
            
            private void addSubMenu(TreeItem selectedTreeItem, String prefix, String suffix, Position position) {
            	MenuItem newItem;
            	
            	switch ( (String)selectedTreeItem.getData("class") ) {
            		case "tab":
                    	newItem = new MenuItem(treeMenu, SWT.NONE);
                    	newItem.setText(prefix+" tab "+suffix);
                    	newItem.setImage(FormJsonParser.TAB_ICON);
                    	newItem.setData("position", position);
                    	newItem.addSelectionListener(addTabListener);
                    	
                    	if ( position == Position.Before ) {
                        	addItemsToSubMenu("Add", "into", treeMenu);
                    	}
                    	break;
                    	
            		case "label":
            		case "text":
            		case "combo":
            		case "check":
            		case "table":
                    	addItemsToSubMenu(prefix, suffix, treeMenu);
                    	
                    	if ( position == Position.Before ) {
                        	//TODO: add column and line into table
                    	}
            			break;
            			
            		case "labelColumn":
            		case "textColumn":
            		case "comboColumn":
            		case "checkColumn":
                    	addItemsToSubMenu(prefix+" column", suffix, treeMenu);
            			break;
                    	
            		case "columns":
                    	if ( position == Position.Before ) {
                    		addItemsToSubMenu("Add", "column", treeMenu);
                    	}
                    	break;
                    	
            		case "lines":
                    	if ( position == Position.Before ) {
                    		newItem = new MenuItem(treeMenu, SWT.CASCADE);
                        	newItem.setText("Add line ...");
                        	
                        	Menu subMenu = new Menu(treeMenu);
                        	newItem.setMenu(subMenu);
                        	
                        	newItem = new MenuItem(subMenu, SWT.NONE);
                        	newItem.setText("line");
                        	newItem.setImage(FormJsonParser.LINE_ICON);
                        	newItem.setData("position", Position.Into);
                        	newItem.addSelectionListener(addLineListener);
                    	}
            			break;

            		case "line":
                    	newItem = new MenuItem(treeMenu, SWT.NONE);
                    	newItem.setText(prefix+" line "+suffix);
                    	newItem.setImage(FormJsonParser.LINE_ICON);
                    	newItem.setData("position", position);
                    	newItem.addSelectionListener(addLineListener);
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
        
        return formTreeItem;
    }
    
    /**
     * this listener is called each time a treeItem is selected in the tree
     */
    private Listener treeSelectionListener = new Listener() {
        public void handleEvent(Event event) {
        	scrolledcomposite.setContent(null);
            
            if ( tree.getSelectionCount() != 0 ) {
            	TreeItem selectedTreeItem = tree.getSelection()[0];
            	TreeItem parentTreeItem = selectedTreeItem.getParentItem();
            	
            	btnUp.setEnabled(parentTreeItem!=null && parentTreeItem.indexOf(selectedTreeItem)!=0 && !selectedTreeItem.getData("class").equals("columns") && !selectedTreeItem.getData("class").equals("lines"));
	            btnDown.setEnabled(parentTreeItem!=null && parentTreeItem.indexOf(selectedTreeItem)!=parentTreeItem.getItemCount()-1 && !selectedTreeItem.getData("class").equals("columns") && !selectedTreeItem.getData("class").equals("lines"));
	            
				TreeItem tabTreeItem = selectedTreeItem;
				while ( tabTreeItem != null && !tabTreeItem.getData("class").equals("tab") )
					tabTreeItem = tabTreeItem.getParentItem();
				if ( tabTreeItem != null ) {
					CTabFolder tabFolder = ((CTabFolder)formDialog.getData("tab folder"));
					Widget tabWidget = (Widget)tabTreeItem.getData("widget");
					for ( int i=0; i < tabFolder.getItemCount(); ++i ) {
						if ( tabFolder.getItem(i).getControl() == tabWidget ) {
							tabFolder.setSelection(i);
						}
					}
				}
            	
            	CompositeInterface composite;
            	switch ( (String)selectedTreeItem.getData("class") ) {
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
            			throw new RuntimeException ("Do not know how to manage "+(String)selectedTreeItem.getData("class")+" objects.");
            	}
            	scrolledcomposite.setContent((Composite)composite);
            	
            	if ( composite != null ) {
            		Widget widget = (Widget)selectedTreeItem.getData("widget");
            		
            		composite.setVisible(true);
                    composite.setData("shell", propertiesDialog);
                    composite.setData("treeItem", selectedTreeItem);
                    composite.setData("class", selectedTreeItem.getData("class"));
                    composite.setData("widget", widget);
                    
                    @SuppressWarnings("unchecked")
					Set<String> keys = (Set<String>)selectedTreeItem.getData("editable keys");
                    if ( keys != null ) {
                    	for ( String key: keys)
                    		composite.set(key, selectedTreeItem.getData(key));
                    }
            	}

            	// we adapt the widgets to their content and recalculate the composite size (for the scroll bars)
            	scrolledcomposite.setExpandHorizontal(true);
            	scrolledcomposite.setExpandVertical(true);
            	scrolledcomposite.setMinSize(((Composite)composite).computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
            TreeItem treeItem = null;

            String clazz = jsonParser.getString(jsonControl, "class");
            if ( clazz != null ) {
            	Widget widget;
            	if ( editMode )
            		treeItem = new TreeItem(parentTreeItem, SWT.NONE);

	            switch ( clazz.toLowerCase() ) {
	                case "check":
	                	widget = jsonParser.createCheck(jsonControl, parent, treeItem);
	                	break;
		            case "combo":
	                	widget = jsonParser.createCombo(jsonControl, parent, treeItem);
	                	break;
	                case "label":
	                	widget = jsonParser.createLabel(jsonControl, parent, treeItem);
	                	break;
	                case "table":
	                	widget = jsonParser.createTable(jsonControl, parent, treeItem);
	                	break;
	                case "text":
	                	widget = jsonParser.createText(jsonControl, parent, treeItem);
	                	break;
	                default:
	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
	            }
	            
	            if ( FormPlugin.areEqualIgnoreCase(clazz, "table") ) {
	            	TreeItem tableTreeItem = treeItem;
    	            TreeItem columnsTreeItem = null;
    	            
    	            if ( editMode ) {
    	            	columnsTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
        	            columnsTreeItem.setImage(FormJsonParser.COLUMN_ICON);
        	            columnsTreeItem.setText("columns");
        	            columnsTreeItem.setData("class", "columns");
        	            columnsTreeItem.setData("widget", widget);
    	            }

                    JSONArray columns = jsonParser.getJSONArray(jsonControl, "columns");
                    if ( columns != null ) {
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> columnsIterator = columns.iterator();
                        while (columnsIterator.hasNext()) {
                            JSONObject jsonColumn = columnsIterator.next();
                            
                            clazz = jsonParser.getString(jsonColumn, "class");
                            if ( clazz != null ) {
                            	if ( editMode )
                            		treeItem = new TreeItem(columnsTreeItem, SWT.NONE);

                	            switch ( clazz.toLowerCase() ) {
	            	                case "check":
	            	                	jsonParser.createCheckColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	break;
	            		            case "combo":
	            	                	 jsonParser.createComboColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	break;
	            	                case "label":
	            	                	jsonParser.createLabelColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	break;
	            	                case "text":
	            	                	jsonParser.createTextColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	break;
	            	                default:
	            	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"text\").");
                	            }
                            }
                        }
                    }
    	            
    	            TreeItem linesTreeItem = null;
    	            
    	            if ( editMode ) {
    	            	linesTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
        	            linesTreeItem.setImage(FormJsonParser.LINE_ICON);
        	            linesTreeItem.setText("lines");
        	            linesTreeItem.setData("class", "lines");
        	            linesTreeItem.setData("widget", widget);
    	            }
    	            
    	            
    	            JSONArray lines = jsonParser.getJSONArray(jsonControl, "lines");
                    if ( lines != null ) {
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> linesIterator = lines.iterator();
                        while (linesIterator.hasNext()) {
                            JSONObject jsonLine = linesIterator.next();
                            
            	            if ( editMode )
            	            	treeItem = new TreeItem(linesTreeItem, SWT.NONE);

                            jsonParser.createLine(jsonLine, (Table)widget, treeItem);
                            
            	            if ( editMode )
            	            	treeItem.setText(treeItem.getData("name")==null ? "" : (String)treeItem.getData("name"));
                        }
                    }
	            }
            }
            FormPosition.resetControlName();
            FormPosition.resetControlClass();
    }
    
    private void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");
        
        close();
    }

    private void save() {
        if (logger.isDebugEnabled())
            logger.debug("Save button selected by user.");

        JSONObject json = null;
        try {
        	json = jsonParser.generateJson(tree);
        } catch (RuntimeException e) {
            FormDialog.popup(Level.ERROR, "Failed to convert configuration to JSON format.", e);
            return;
        }
        
        String jsonString = json.toJSONString();
        
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("JavaScript");
        scriptEngine.put("jsonString", jsonString);
        try {
            scriptEngine.eval("result = JSON.stringify(JSON.parse(jsonString), null, 3)");
            jsonString = (String) scriptEngine.get("result");
        } catch (ScriptException e1) {
            // if we cannot indent the json string, that's not a big deal
        }

        try (FileWriter file = new FileWriter(configFilename)) {
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            FormDialog.popup(Level.ERROR, "Failed to write configuration into file \"" + configFilename + "\"", e);
            return;
        }
        
        close();
    }
    
    /**
     * Called when the user clicks on the close button
     */
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
    
    public TreeItem moveTreeItem(TreeItem parentTreeItem, TreeItem sourceTreeItem, int index) {
    	// we create a new treeItem at position index
		TreeItem newTreeItem = new TreeItem(parentTreeItem, SWT.NONE, index);
		newTreeItem.setImage(sourceTreeItem.getImage());
		newTreeItem.setText(sourceTreeItem.getText());
		
		newTreeItem.setData("class", sourceTreeItem.getData("class"));
		newTreeItem.setData("widget", sourceTreeItem.getData("widget"));
		newTreeItem.setData("editable keys", sourceTreeItem.getData("editable keys"));

   		
        @SuppressWarnings("unchecked")
		Set<String> keys = (Set<String>)sourceTreeItem.getData("editable keys");
        if ( keys != null ) {
        	for ( String key: keys)
        		newTreeItem.setData(key, sourceTreeItem.getData(key));
        }
        for (int i=0; sourceTreeItem.getItemCount() > 0; ++i)
        	moveTreeItem(newTreeItem, sourceTreeItem.getItem(0), i);
        
        // now that the new TreeItem is created, we can dispose the old one
		newTreeItem.setExpanded(sourceTreeItem.getExpanded());
        sourceTreeItem.dispose();
        return newTreeItem;
    }
    
    private SelectionListener addTabListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			Position position = (Position)((MenuItem)e.getSource()).getData("position");
			TreeItem selectedTreeItem = tree.getSelection()[0];
			TreeItem parentTreeItem = selectedTreeItem.getParentItem();
			CTabFolder tabFolder = (CTabFolder)formDialog.getData("tab folder"); 
			int index = 0;
			
			if ( parentTreeItem == null ) {
				parentTreeItem = selectedTreeItem;
				index = parentTreeItem.getItemCount();
			} else {
				switch ( position ) {
					case Before: index = parentTreeItem.indexOf(selectedTreeItem);     break;
					case After:  index = parentTreeItem.indexOf(selectedTreeItem) + 1; break;
					case End:    index = parentTreeItem.getItemCount();                break;
					case Into:   logger.error("Cannot insert a tab into another tab"); return;
				}
			}
			
			TreeItem newTreeItem = new TreeItem(parentTreeItem, SWT.NONE, index);
			newTreeItem.setText("new tab");
			newTreeItem.setImage(FormJsonParser.TAB_ICON);
        	newTreeItem.setData("class", "tab");
        	jsonParser.setData(newTreeItem, "name", "new tab");
        	jsonParser.setData(newTreeItem, "foreground", null);
        	jsonParser.setData(newTreeItem, "background", null);
        	
        	CTabItem newTabItem = new CTabItem(tabFolder, SWT.MULTI, index);
			newTabItem.setText("new tab");
			newTabItem.setData("treeItem", newTreeItem);
			
			Composite composite = new Composite(tabFolder, SWT.NONE);
			composite.setForeground(formDialog.getForeground());
			composite.setBackground(formDialog.getBackground());
			composite.setFont(formDialog.getFont());
			composite.setData("tabItem", newTabItem);

			newTabItem.setControl(composite);
			newTreeItem.setData("widget", composite);
			
			tree.setSelection(newTreeItem);
			tree.notifyListeners(SWT.Selection, new Event());        // shows up the tab's properties
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
    };
    
    private SelectionListener addTableListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
	         Position position = (Position)((MenuItem)e.getSource()).getData("position");
	            TreeItem selectedTreeItem = tree.getSelection()[0];
	            TreeItem parentTreeItem = selectedTreeItem.getParentItem();
	            TreeItem currentTreeItem = null;
	            int index = 0;

	            if ( parentTreeItem == null ) {
	                parentTreeItem = selectedTreeItem;
	                index = parentTreeItem.getItemCount();
	            } else {
	                switch ( position ) {
	                    case Before: currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem);     break;
	                    case After:  currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem) + 1; break;
	                    case End:    currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();                break;
	                    case Into:   currentTreeItem = selectedTreeItem; index = selectedTreeItem.getItemCount();              break;
	                }
	            }
	            
	            TreeItem newTreeItem = new TreeItem(currentTreeItem, SWT.NONE, index);
	            newTreeItem.setImage(FormJsonParser.TABLE_ICON);
	            newTreeItem.setText("new table");
	            
                logger.trace("      adding table");
                Composite parentComposite = (Composite)currentTreeItem.getData("widget");
                
                Table table = jsonParser.createTable(null, parentComposite, newTreeItem);
                jsonParser.setData(newTreeItem, "name", "new table");
                
                TreeItem columnsItem = new TreeItem(newTreeItem, SWT.NONE);
                columnsItem.setImage(FormJsonParser.COLUMN_ICON);
                columnsItem.setText("columns");
                columnsItem.setData("class", "columns");
                columnsItem.setData("widget", table);
                
                TreeItem linesItem = new TreeItem(newTreeItem, SWT.NONE);
                linesItem.setImage(FormJsonParser.LINE_ICON);
                linesItem.setText("lines");
                linesItem.setData("class", "lines");
                linesItem.setData("widget", table);
                
                tree.setSelection(newTreeItem);
                tree.notifyListeners(SWT.Selection, new Event());        // shows up the table's properties
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
    };
    
    private SelectionListener addWidgetListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			Position position = (Position)((MenuItem)e.getSource()).getData("position");
			String widgetClass = (String)((MenuItem)e.getSource()).getData("class"); 
			TreeItem selectedTreeItem = tree.getSelection()[0];
			TreeItem parentTreeItem = selectedTreeItem.getParentItem();
			TreeItem currentTreeItem = null;
			int index = 0;

			if ( parentTreeItem == null ) {
				parentTreeItem = selectedTreeItem;
				index = parentTreeItem.getItemCount();
			} else {
				switch ( position ) {
					case Before: currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem);     break;
					case After:  currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem) + 1; break;
					case End:    currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();                break;
					case Into:   currentTreeItem = selectedTreeItem; index = selectedTreeItem.getItemCount();              break;
				}
			}
			
			TreeItem newTreeItem = new TreeItem(currentTreeItem, SWT.NONE, index);
			newTreeItem.setText("new "+widgetClass);
        	newTreeItem.setData("class", widgetClass);
        	
        	Composite parentComposite = (Composite)currentTreeItem.getData("widget");
			if ( parentComposite instanceof Table ) {
				Table table = (Table)parentComposite;
				TableColumn column = null;
				switch (widgetClass) {
					case "label":  column = jsonParser.createLabelColumn(null, table, newTreeItem, index); break;
					case "text":   column = jsonParser.createTextColumn (null, table, newTreeItem, index); break;
					case "combo":  column = jsonParser.createComboColumn(null, table, newTreeItem, index); break;
					case "check":  column = jsonParser.createCheckColumn(null, table, newTreeItem, index); break;
				}
				
				column.setText("new "+widgetClass);
			} else {
				switch (widgetClass) {
					case "label":  jsonParser.createLabel(null, parentComposite, newTreeItem); break;
					case "text":   jsonParser.createText (null, parentComposite, newTreeItem); break;
					case "combo":  jsonParser.createCombo(null, parentComposite, newTreeItem); break;
					case "check":  jsonParser.createCheck(null, parentComposite, newTreeItem); break;
				}
			}
			
        	jsonParser.setData(newTreeItem, "name", "new "+widgetClass);
			
			tree.setSelection(newTreeItem);
			tree.notifyListeners(SWT.Selection, new Event());        // shows up the control's properties
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
    };
    
    private SelectionListener addLineListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
	        Position position = (Position)((MenuItem)e.getSource()).getData("position");
            TreeItem selectedTreeItem = tree.getSelection()[0];
            TreeItem parentTreeItem = selectedTreeItem.getParentItem();
            TreeItem currentTreeItem = null;
            int index = 0;

            if ( parentTreeItem == null ) {
                parentTreeItem = selectedTreeItem;
                index = parentTreeItem.getItemCount();
            } else {
                switch ( position ) {
                    case Before: currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem);     break;
                    case After:  currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem) + 1; break;
                    case End:    currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();                break;
                    case Into:   currentTreeItem = selectedTreeItem; index = selectedTreeItem.getItemCount();              break;
                }
            }
            
            TreeItem newTreeItem = new TreeItem(currentTreeItem, SWT.NONE, index);
            newTreeItem.setText("new line");
            
            Composite parentComposite = (Composite)currentTreeItem.getData("widget");
            if ( parentComposite instanceof Table ) {
                Table table = (Table)parentComposite;
            
                jsonParser.createLine(null, table, newTreeItem);
            }
            
            jsonParser.setData(newTreeItem, "name", "new line");
            
            tree.setSelection(newTreeItem);
            tree.notifyListeners(SWT.Selection, new Event());        // shows up the control's properties
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
    };
    
    private SelectionListener deleteListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TreeItem selectedTreeItem = tree.getSelection()[0];

			
			if ( selectedTreeItem.getItemCount() != 0 ) {
				if ( ! FormDialog.question("The item you are deleting contains other items.\n\nAre you sure you wish to delete them as well ?") )
					return;
				
				while ( selectedTreeItem.getItemCount() != 0 ) {
					deleteTreeItem(selectedTreeItem.getItem(selectedTreeItem.getItemCount()-1));
				}
			}
			
			deleteTreeItem(selectedTreeItem);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		
		public void deleteTreeItem(TreeItem selectedTreeItem) {
			while ( selectedTreeItem.getItemCount() != 0 ) {
				deleteTreeItem(selectedTreeItem.getItem(selectedTreeItem.getItemCount()-1));
			}
			
			Widget widget = (Widget)selectedTreeItem.getData("widget");
			if ( widget != null ) {
				if ( widget instanceof TableColumn && !widget.isDisposed() ) {
					Table table = ((TableColumn)widget).getParent();
					int columnIndex = table.indexOf(((TableColumn)widget));
					
					for ( TableItem tableItem: table.getItems() ) {
						TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
						TableEditor[] newEditors = new TableEditor[table.getColumnCount()-1];
						
						String[] oldCells = (String[])tableItem.getData("cells");
						String[] newCells = new String[table.getColumnCount()-1];
						
						int newCol = 0;
						for (int oldCol=0; oldCol < table.getColumnCount(); ++oldCol) {
							if ( oldCol != columnIndex ) {
								newEditors[newCol] = oldEditors[oldCol];
								newEditors[newCol].setEditor(newEditors[newCol].getEditor(), tableItem, newCol);
								newCells[newCol] = oldCells[oldCol];
								++newCol;
							} else {
								oldEditors[columnIndex].getEditor().dispose();
								oldEditors[columnIndex].dispose();
							}
						}
						tableItem.setData("editors", newEditors);
						tableItem.setData("cells", newCells);
						TreeItem lineTreeItem = (TreeItem)tableItem.getData("treeitem");
						if ( lineTreeItem != null )
							lineTreeItem.setData("cells", newCells);
					}
				}
				
				
				if ( widget != null && !widget.isDisposed() ) {
				    CTabItem tabItem = (CTabItem)widget.getData("tabItem");
				    widget.dispose();
				    if ( tabItem != null ) 
				        tabItem.dispose();
				}
			}
			
			selectedTreeItem.dispose();
		}
    };
    
    /***********************************************************************************************************/
    
    /**
     * Shows up an on screen popup displaying the message and wait for the user
     * to click on the "OK" button
     */
    public static void popup(Level level, String msg) {
        popup(level, msg, null);
    }

    // the popupMessage is a class variable because it will be used in an asyncExec() method.
    private static String popupMessage;

    /**
     * Shows up an on screen popup, displaying the message (and the exception
     * message if any) and wait for the user to click on the "OK" button<br>
     * The exception stacktrace is also printed on the standard error stream
     */
    public static void popup(Level level, String msg, Exception e) {
        popupMessage = msg;
        logger.log(FormDialog.class, level, msg, e);

        if (e != null && !FormPlugin.areEqual(e.getMessage(), msg)) {
            popupMessage += "\n\n" + e.getMessage();
        }

        display.syncExec(new Runnable() {
            @Override
            public void run() {
                switch (level.toInt()) {
                    case Level.FATAL_INT:
                    case Level.ERROR_INT:
                        MessageDialog.openError(display.getActiveShell(), FormPlugin.pluginTitle, popupMessage);
                        break;
                    case Level.WARN_INT:
                        MessageDialog.openWarning(display.getActiveShell(), FormPlugin.pluginTitle, popupMessage);
                        break;
                    default:
                        MessageDialog.openInformation(display.getActiveShell(), FormPlugin.pluginTitle, popupMessage);
                        break;
                }
            }
        });
    }
    
    private static int questionResult;

    /**
     * Shows up an on screen popup displaying the question (and the exception
     * message if any) and wait for the user to click on the "YES" or "NO"
     * button<br>
     * The exception stacktrace is also printed on the standard error stream
     */
    public static boolean question(String msg) {
        return question(msg, new String[] { "Yes", "No" }) == 0;
    }

    /**
     * Shows up an on screen popup displaying the question (and the exception
     * message if any) and wait for the user to click on the "YES" or "NO"
     * button<br>
     * The exception stacktrace is also printed on the standard error stream
     */
    public static int question(String msg, String[] buttonLabels) {
        if (logger.isDebugEnabled())
            logger.debug("question : " + msg);
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                // questionResult =
                // MessageDialog.openQuestion(display.getActiveShell(),
                // DBPlugin.pluginTitle, msg);
                MessageDialog dialog = new MessageDialog(display.getActiveShell(), FormPlugin.pluginTitle, null, msg,
                        MessageDialog.QUESTION, buttonLabels, 0);
                questionResult = dialog.open();
            }
        });
        if (logger.isDebugEnabled())
            logger.debug("answer : " + buttonLabels[questionResult]);
        return questionResult;
    }

    /**
     * shows up an on screen popup displaying the message but does not wait for
     * any user input<br>
     * it is the responsibility of the caller to dismiss the popup
     */
    private static Shell dialogShell = null;
    private static Label dialogLabel = null;

    public static Shell popup(String msg) {
        if ( dialogShell == null ) {
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    dialogShell = new Shell(display, SWT.BORDER | SWT.APPLICATION_MODAL);
                    dialogShell.setSize(400, 50);
                    dialogShell.setBackground(new Color(null, 0, 0, 0));
                    dialogShell.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - dialogShell.getSize().x) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogShell.getSize().y) / 4);
                    FillLayout layout = new FillLayout();
                    layout.marginWidth = 2;
                    layout.marginHeight = 2;
                    dialogShell.setLayout(layout);

                    Composite composite = new Composite(dialogShell, SWT.NONE);
                    composite.setBackground(new Color(null, 240, 248, 255));
                    composite.setLayout( new GridLayout( 1, false ) );

                    dialogLabel = new Label(composite, SWT.NONE);
                    dialogLabel.setBackground(new Color(null, 240, 248, 255));
                    dialogLabel.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true ) );
                    dialogLabel.setFont(new Font(null, "Segoe UI", 10, SWT.BOLD));
                }
            });
        }


        display.syncExec(new Runnable() {

            @Override
            public void run() {
                for (Shell shell : Display.getCurrent().getShells()) {
                    shell.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
                }
                dialogShell.setText(msg);
                dialogLabel.setText(msg);
                dialogShell.open();
            }

        });return dialogShell;}

    public static void closePopup() {
        if (dialogShell != null) {
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    dialogShell.close();
                    dialogShell = null;
                    for (Shell shell : display.getShells()) {
                        shell.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
                    }
                }
            });
        }
    }
    
    /**
     * shows up an on screen popup with a progressbar<br>
     * it is the responsibility of the caller to dismiss the popup
     */
    public static ProgressBar progressbarPopup(String msg) {
        if (logger.isDebugEnabled())
            logger.debug("new progressbarPopup(\"" + msg + "\")");
        Shell shell = new Shell(display, SWT.SHELL_TRIM);
        shell.setSize(600, 100);
        shell.setBackground(BLACK_COLOR);
        shell.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - shell.getSize().x) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - shell.getSize().y) / 4);

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setBackground(LIGHT_BLUE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        label.setBackground(LIGHT_BLUE);
        label.setFont(TITLE_FONT);
        label.setText(msg);

        ProgressBar progressBar = new ProgressBar(composite, SWT.SMOOTH);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        shell.layout();
        shell.open();

        return progressBar;
    }
    
    /************************************************************************************************/
    
    /**
     * Checks whether the eObject fits in the filter rules
     */
    public static boolean checkFilter(EObject eObject, JSONObject filterObject) {
        if (filterObject == null) {
            return true;
        }

        String type = ((String)jsonParser.getJSON(filterObject, "genre")).toUpperCase();
        if ( FormPlugin.isEmpty(type) )
        	type = "AND";

        if (!type.equals("AND") && !type.equals("OR"))
            throw new RuntimeException("Invalid filter genre. Supported genres are \"AND\" and \"OR\".");

        boolean result;

        @SuppressWarnings("unchecked")
        Iterator<JSONObject> filterIterator = ((JSONArray)jsonParser.getJSON(filterObject, "tests")).iterator();
        while (filterIterator.hasNext()) {
            JSONObject filter = filterIterator.next();
            String attribute = (String)jsonParser.getJSON(filter, "attribute");
            String operation = (String)jsonParser.getJSON(filter, "operation");
            String value;
            String[] values;

            String attributeValue = FormVariable.expand(attribute, eObject);

            switch (operation.toLowerCase()) {
                case "equals":
                    value = (String)jsonParser.getJSON(filter, "value");

                    result = attributeValue.equals(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") equals \"" + value + "\" --> " + result);
                    break;

                case "in":
                    value = (String)jsonParser.getJSON(filter, "value");
                    values = value.split(",");
                    result = false;
                    for (String str : values) {
                        if (str.equals(attributeValue)) {
                            result = true;
                            break;
                        }
                    }
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") in \"" + value + "\" --> " + result);
                    break;

                case "exists":
                    result = attributeValue.isEmpty();
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") exists --> " + result);
                    break;

                case "iequals":
                    value = (String)jsonParser.getJSON(filter, "value");

                    result = attributeValue.equalsIgnoreCase(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") equals (ignore case) \"" + value + "\" --> " + result);
                    break;

                case "iin":
                    value = (String)jsonParser.getJSON(filter, "value");
                    values = value.split(",");
                    result = false;
                    for (String str : values) {
                        if (str.equals(attributeValue)) {
                            result = true;
                            break;
                        }
                    }
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") in \"" + value + "\" --> " + result);
                    break;

                case "matches":
                    value = (String)jsonParser.getJSON(filter, "value");

                    result = (attributeValue != null) && attributeValue.matches(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") matches \"" + value + "\" --> " + result);
                    break;

                default:
                    throw new RuntimeException("Unknown operation type \"" + operation + "\" in filter.\n\nValid operations are \"equals\", \"exists\", \"iequals\" and \"matches\".");
            }

            // in AND mode, all the tests must return true, so if the current test is false, then the complete filter returns false
            if (result == false && type.equals("AND"))
                return false;

            // in OR mode, one test at lease must return true, so if the current test is true, then the complete filter returns true
            if (result == true && type.equals("OR"))
                return true;
        }
        // in AND mode, we're here if all the tests were true
        // in OR mode, we're here if all the tests were false
        return type.equals("AND");
    }

    /**
     * Checks whether a value is in an array
     */
    public static boolean inArray(String[] stringArray, String string) {
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
}
