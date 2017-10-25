package org.archicontribs.form;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Level;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
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
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFolder;

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
    public static final String[] validRefers              = new String[] {"selected", "view", "folder", "model"};   		// default value is first one
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
    
    private String               configFilename       = null;
    private boolean              editMode             = false;
    private EObject              selectedObject       = null;
    
    
    public  final static FormVarList    formVarList   = new FormVarList();
    private static final FormJsonParser jsonParser    = new FormJsonParser();
    
    
    
    public FormDialog(String configFilename, JSONObject jsonForm, EObject selectedObject) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        
        this.configFilename = configFilename;
        this.selectedObject = selectedObject;
        
        logger.trace("Selected object is "+FormPlugin.getDebugName(selectedObject));
        
        formVarList.reset();

        try {
        	formDialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        	
            TreeItem formTreeItem = null;
            // If the dialog is ran against no EObject, then we switch to edit mode 
            editMode = ( selectedObject == null );
            
            if ( editMode ) {
            	// in edit mode, we show up the graphical editor
            	formTreeItem = createPropertiesDialog(jsonForm);
            }
            
            jsonParser.createForm(jsonForm, formDialog, formTreeItem);
            
            // if we are in form mode, then we expand the name of the form
            if ( !editMode ) {
            	formDialog.setText(FormVariable.expand(formDialog.getText(), selectedObject));
            }

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
                    
                    // if we are in form mode, then we expand the tab name in case it contains variables
                    if ( !editMode ) {
                    	tabItem.setText(FormVariable.expand(tabItem.getText(), selectedObject));
                    }
                    
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
            	// in edit mode, we expand the form in the tree and select it
            	formTreeItem.setExpanded(true);
            
            	tree.setSelection(formTreeItem);
            	tree.notifyListeners(SWT.Selection, new Event());        // shows up the form's properties
            } else {
            	// in form mode, the graphical editor is not shown so we activate the form's OK and CANCEL buttons
            	Button cancelButton = (Button)formDialog.getData("cancel button");
            	cancelButton.addSelectionListener(new SelectionListener() {
                    @Override public void widgetSelected(SelectionEvent e) { cancel(); }
                    @Override public void widgetDefaultSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
                });
            	
            	Button okButton = (Button)formDialog.getData("ok button");
            	okButton.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) { ok(); }
                    public void widgetDefaultSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
                });
            	
            	Button exportButton = (Button)formDialog.getData("export button");
            	exportButton.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) { exportToExcel(); }
                    public void widgetDefaultSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
                });
            	
            	
                // If there is at least one Excel sheet specified, then we show up the "export to Excel" button
                @SuppressWarnings("unchecked")
				HashSet<String> excelSheets = (HashSet<String>)formDialog.getData("excel sheets");
                //TODO: must be filled in in FormJsonParser
                exportButton.setVisible(!excelSheets.isEmpty());
            }
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
            @Override public void widgetSelected(SelectionEvent event) {saveConfToJSON();}
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
                String variableValue;
                Control control = null;
                
            	if ( editMode )
            		treeItem = new TreeItem(parentTreeItem, SWT.NONE);

	            switch ( clazz.toLowerCase() ) {
	                case "check":
	                	control = jsonParser.createCheck(jsonControl, parent, treeItem, selectedObject);
	                	
	                	// In form mode
	                	if ( !editMode ) {
	                		// we expand the values 
	                		String[] values = (String[])control.getData("values");
		                    if ( values != null ) {
		                    	for ( int i = 0; i < values.length; ++i ) {
		                    		values[i] = FormVariable.expand(values[i], selectedObject);
		                    	}
		                    }
		                    
		                    // we expand the variable
		                    variableValue = FormVariable.expand((String)control.getData("variable"), selectedObject); 
		                    if ( FormPlugin.isEmpty(variableValue) || (control.getData("forceDefault")!=null && (Boolean)control.getData("forceDefault")) )
		                    	variableValue = FormVariable.expand((String)control.getData("default"), selectedObject);
		                    
		                    if ( values == null || values.length == 0 ) 										// should be "true" or "false"
		                    	((Button)control).setSelection(FormPlugin.areEqualIgnoreCase(variableValue, "true"));
		                    else																				// should be values[0] or values[1]
		                    	((Button)control).setSelection(FormPlugin.areEqualIgnoreCase(variableValue, values[0]));
		                    
		                    // we update the widgets that refer to the same variable when the user changes its value
		                    ((Button)control).addSelectionListener(checkButtonSelectionListener);
	                	}
	                	break;
	                	
		            case "combo":
	                	control = jsonParser.createCombo(jsonControl, parent, treeItem, selectedObject);
	                	
	                	// In form mode
	                	if ( !editMode ) {
		                    // we update the widgets that refer to the same variable when the user changes its value
		                    ((CCombo)control).addModifyListener(textModifyListener);
	                	}
	                	break;
	                	
	                case "label":
	                	control = jsonParser.createLabel(jsonControl, parent, treeItem, selectedObject);
	                	break;
	                	
	                case "table":
	                	Table table = jsonParser.createTable(jsonControl, parent, treeItem, selectedObject);
    	            	TreeItem tableTreeItem = treeItem;
        	            TreeItem columnsTreeItem = null;
        	            
	                	// if form mode, we replace the controls' tooltip by its expanded value in case it contains a variable
	                	table.setToolTipText(FormVariable.expand(table.getToolTipText(), selectedObject));

        	            // required by the graphical editor
        	            if ( editMode ) {
        	            	columnsTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
            	            columnsTreeItem.setImage(FormJsonParser.COLUMN_ICON);
            	            columnsTreeItem.setText("columns");
            	            columnsTreeItem.setData("class", "columns");
            	            columnsTreeItem.setData("widget", table);
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
                                	
                                	TableColumn tableColumn = null;

                    	            switch ( clazz.toLowerCase() ) {
    	            	                case "check":
    	            	                    tableColumn = jsonParser.createCheckColumn(jsonColumn, table, treeItem, null, selectedObject);
    	            	                	break;
    	            		            case "combo":
    	            		                tableColumn = jsonParser.createComboColumn(jsonColumn, table, treeItem, null, selectedObject);
    	            	                	break;
    	            	                case "label":
    	            	                    tableColumn = jsonParser.createLabelColumn(jsonColumn, table, treeItem, null, selectedObject);
    	            	                	break;
    	            	                case "text":
    	            	                    tableColumn = jsonParser.createTextColumn(jsonColumn, table, treeItem, null, selectedObject);
    	            	                	break;
    	            	                default:
    	            	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"text\").");
                    	            }
                    	            tableColumn.addListener(SWT.Selection, sortListener);
                                }
                            }
                        }
        	            
        	            TreeItem linesTreeItem = null;
        	            
        	            if ( editMode ) {
        	            	linesTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
            	            linesTreeItem.setImage(FormJsonParser.LINE_ICON);
            	            linesTreeItem.setText("lines");
            	            linesTreeItem.setData("class", "lines");
            	            linesTreeItem.setData("widget", table);
        	            }
        	            
        	            
        	            JSONArray lines = jsonParser.getJSONArray(jsonControl, "lines");
                        if ( lines != null ) {
                            @SuppressWarnings("unchecked")
    						Iterator<JSONObject> linesIterator = lines.iterator();
                            while (linesIterator.hasNext()) {
                                JSONObject jsonLine = linesIterator.next();
                                
                	            if ( editMode )
                	            	treeItem = new TreeItem(linesTreeItem, SWT.NONE);

                                jsonParser.createLine(jsonLine, table, treeItem, selectedObject);
                                
                	            if ( editMode ) {
                	            	if ( treeItem.getData("name")!=null ) treeItem.setText((String)treeItem.getData("name"));
                	            } else {
                	            	// we replace the line cells with the corresponding expanded values
                	            	// if the lines are generated, then we caculate the 
                	            }
                            }
                        }
                        table.layout();
                        
                        control = (Control)table;
	                	break;
	                	
	                case "text":
	                	control = jsonParser.createText(jsonControl, parent, treeItem, selectedObject);
	                	
	                	if ( !editMode ) {
	                		// if the tooltip is empty but a regexp is defined,then we add a tooltip with a little help message about the regexp
	                		if (  FormPlugin.isEmpty(control.getToolTipText()) && !FormPlugin.isEmpty((String)control.getData("regexp") )) {
	                			control.setData("pattern", Pattern.compile((String)control.getData("regexp")));
	                            control.setToolTipText("Your text should match the following regexp :\n" + (String)control.getData("regexp"));
	                		}
	                		
		                    ((StyledText)control).addModifyListener(textModifyListener);
	                	}
	                	break;
	                default:
	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
	            }
	            
	            if ( !editMode ) {
                    // We reference the variable and the control to the eObject that the variable refers to
                    if ( control.getData("variable") != null ) {
    	                EObject referedEObject;
    	                String unscoppedVariable;
    	                
                    	referedEObject = FormVariable.getReferedEObject((String)control.getData("variable"), selectedObject);
                    	unscoppedVariable = FormVariable.getUnscoppedVariable((String)control.getData("variable"), selectedObject);
                    	control.setData("variable", unscoppedVariable);
                    	control.setData("eObject", referedEObject);
                    	formVarList.set(referedEObject, unscoppedVariable, control);
                    }
	            }
            }
            FormPosition.resetControlName();
            FormPosition.resetControlClass();
    }
    
    /**
     * This method is called when the user chicks on the "OK" button in form mode
     * 
     * It creates a command to modify the objects properties in order to allow undo/redo.
     */
    private void ok() {
        if (logger.isDebugEnabled())
            logger.debug("Ok button selected by user.");
        CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
        try {
            for (Control control : formDialog.getChildren()) {
                save(compoundCommand, control);
            }
        } catch (RuntimeException e) {
            popup(Level.ERROR, "Failed to save variables.", e);
            return;
        }

        IArchimateModel model;

        if (selectedObject instanceof IArchimateModel) {
            model = ((IArchimateModel) selectedObject).getArchimateModel();
        } else if (selectedObject instanceof IDiagramModel) {
            model = ((IDiagramModel) selectedObject).getArchimateModel();
        } else if (selectedObject instanceof IDiagramModelArchimateObject) {
            model = ((IDiagramModelArchimateObject) selectedObject).getDiagramModel().getArchimateModel();
        } else if (selectedObject instanceof IDiagramModelArchimateConnection) {
            model = ((IDiagramModelArchimateConnection) selectedObject).getDiagramModel().getArchimateModel();
        } else if (selectedObject instanceof IArchimateElement) {
            model = ((IArchimateElement) selectedObject).getArchimateModel();
        } else if (selectedObject instanceof IArchimateRelationship) {
            model = ((IArchimateRelationship) selectedObject).getArchimateModel();
        } else if (selectedObject instanceof IFolder) {
            model = ((IFolder) selectedObject).getArchimateModel();
        } else {
            popup(Level.ERROR, "Failed to get the model.");
            return;
        }
        
        logger.trace("Executing "+compoundCommand.size()+" command ...");

        CommandStack stack = (CommandStack) model.getAdapter(CommandStack.class);
        stack.execute(compoundCommand);

        close();
    }
    
    private void save(CompoundCommand compoundCommand, Control control) throws RuntimeException {
        switch (control.getClass().getSimpleName()) {
            case "Label":
                break;					// nothing to save here

            case "Button":
            case "CCombo":
            case "StyledText":
                if (control.getData("variable") != null)
                    do_save(compoundCommand, control);
                break;

            case "CTabFolder":
                for (Control child : ((CTabFolder) control).getChildren()) {
                    save(compoundCommand, child);
                }
                break;
            case "Table":
                for (TableItem item : ((Table) control).getItems()) {
                    for (TableEditor editor : (TableEditor[]) item.getData("editors")) {
                        if (editor != null)
                            save(compoundCommand, editor.getEditor());
                    }
                }
                break;
            case "Composite":
                for (Control child : ((Composite) control).getChildren()) {
                    save(compoundCommand, child);
                }
                break;

            case "ToolBar":
            	// do nothing as it is created by the CTabFolder
            	break;
            	
            default:
                throw new RuntimeException("Save : do not know how to save a " + control.getClass().getSimpleName());
        }
    }

    private void do_save(CompoundCommand compoundCommand, Control control) throws RuntimeException {
        String unscoppedVariable = (String)control.getData("variable");
        EObject referedEObject = (EObject)control.getData("eObject");
        String value;

        switch (control.getClass().getSimpleName()) {
            case "Button":
                String[] values = (String[])control.getData("values");
                if ( values == null )
                    value = String.valueOf(((Button) control).getSelection());
                else
                    value = values[((Button) control).getSelection()?0:1];
                break;
            case "CCombo":
                value = ((CCombo) control).getText();
                break;
            case "StyledText":
                value = ((StyledText) control).getText();
                break;
            default:
                throw new RuntimeException("Do_save : do not know how to save a " + control.getClass().getSimpleName());
        }

        if (logger.isDebugEnabled())
            logger.debug("do_save " + control.getClass().getSimpleName() + " : " + unscoppedVariable + " = \"" + value + "\"");

        if (value == null || value.isEmpty()) {
            String whenEmpty = (String) control.getData("whenEmpty");
            
            if ( whenEmpty == null )
            	whenEmpty = FormDialog.validWhenEmpty[0];

            switch (whenEmpty) {
                case "ignore":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : ignored.");
                    break;
                case "create":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : creating property.");
                    FormVariable.setVariable(compoundCommand, unscoppedVariable, (String)formDialog.getData("variable separator"), "", referedEObject);
                    break;
                case "delete":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : deleting property.");
                    FormVariable.setVariable(compoundCommand, unscoppedVariable, (String)formDialog.getData("variable separator"), null, referedEObject);
                    break;
            }
        } else {
            if (logger.isTraceEnabled())
                logger.trace("   value is not empty.");
            FormVariable.setVariable(compoundCommand, unscoppedVariable, (String)formDialog.getData("variable separator"), value, referedEObject);
        }
    }

    //@SuppressWarnings("deprecation")
    @SuppressWarnings("deprecation")
	private void exportToExcel() {
        if (logger.isDebugEnabled())
            logger.debug("Export button selected by user.");
        
        FileDialog fsd = new FileDialog(formDialog, SWT.SINGLE);
        fsd.setFilterExtensions(new String[] { "*.xls*" });
        fsd.setText("Select Excel File...");
        String excelFile = fsd.open();

        // we wait for the dialog disposal
        while (display.readAndDispatch())
            ;

        Workbook workbook;
        Sheet sheet;
        
        if (excelFile != null) {
            FileInputStream file;
            try {
                file = new FileInputStream(excelFile);
            } catch (FileNotFoundException e) {
                popup(Level.ERROR, "Cannot open the Excel file.", e);
                return;
            }
  
            try {
                workbook = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException | EncryptedDocumentException e) {
                closePopup();
                popup(Level.ERROR, "The file " + excelFile + " seems not to be an Excel file!", e);
                // TODO: add an option to create an empty Excel file
                return;
            }
            
            // we check that all the sheets already exist
            @SuppressWarnings("unchecked")
    		HashSet<String> excelSheets = (HashSet<String>)formDialog.getData("excel sheets");
            for (String sheetName : excelSheets) {
                sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    closePopup();
                    popup(Level.ERROR, "The file " + excelFile + " does not contain a sheet called \"" + sheetName + "\"");
                    // TODO : add a preference to create the sheet
                    try {
                        workbook.close();
                    } catch (IOException ign) {
                        ign.printStackTrace();
                    }
                    return;
                }
            }


            boolean exportOk = true;

            try {
                // we go through all the controls and export the corresponding excel cells
            	CTabFolder tabFolder = (CTabFolder)formDialog.getData("tab folder");
                for (CTabItem tabItem : tabFolder.getItems()) {
                    if (logger.isDebugEnabled())
                        logger.debug("Exporting tab " + tabItem.getText());

                    Composite composite = (Composite) tabItem.getControl();
                    for (Control control : composite.getChildren()) {
                        String excelSheet = (String) control.getData("excelSheet");

                        if (excelSheet != null) {
                            sheet = workbook.getSheet(excelSheet);		// cannot be null as it has been checked before

                            if ((control instanceof StyledText) || (control instanceof Label)
                                    || (control instanceof CCombo) || (control instanceof Button)) {
                                String excelCell = (String) control.getData("excelCell");

                                CellReference ref = new CellReference(excelCell);
                                Row row = sheet.getRow(ref.getRow());
                                if (row == null) {
                                    row = sheet.createRow(ref.getRow());
                                }
                                Cell cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);

                                String text = "";
                                switch (control.getClass().getSimpleName()) {
                                    case "StyledText":
                                        text = ((StyledText) control).getText();
                                        break;
                                    case "Label":
                                        text = ((Label) control).getText();
                                        break;
                                    case "CCombo":
                                        text = ((CCombo) control).getText();
                                        break;
                                    case "Button":
                                        String[]values = (String[])control.getData("values");
                                        if ( values == null )
                                            text = String.valueOf(((Button)control).getSelection());
                                        else
                                            text = values[((Button)control).getSelection()?0:1];
                                        break;
                                }
                                cell.setCellValue(text);
                                if (logger.isTraceEnabled())
                                    logger.trace("   '" + excelSheet + "'!" + excelCell + " -> \"" + text + "\"");
                            } else {
                                if (control instanceof Table) {
                                    if (logger.isDebugEnabled())
                                        logger.debug("Exporting table");
                                    Table table = (Table) control;
                                    int excelFirstLine = (int) table.getData("excelFirstLine") - 1;	            // we decrease the provided value because POI lines begin at zero
                                    for (int line = 0; line < table.getItemCount(); ++line) {
                                        TableItem tableItem = table.getItem(line);
                                        Row row = sheet.getRow(excelFirstLine + line);
                                        if (row == null)
                                            row = sheet.createRow(excelFirstLine + line);

                                        for (int col = 0; col < table.getColumnCount(); ++col) {
                                            TableColumn tableColumn = table.getColumn(col);
                                            String excelColumn = (String) tableColumn.getData("excelColumn");

                                            if (excelColumn != null) {
                                                CellReference ref = new CellReference(excelColumn);
                                                Cell cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);

                                                TableEditor editor = ((TableEditor[]) tableItem.getData("editors"))[col];

                                                String value;
                                                if (editor == null)
                                                    value = tableItem.getText(col);
                                                else
                                                    switch (editor.getEditor().getClass().getSimpleName()) {
                                                        case "StyledText":
                                                            value = ((StyledText)editor.getEditor()).getText();
                                                            break;
                                                        case "Button":
                                                            String[]values = (String[])tableColumn.getData("values");
                                                            if ( values == null )
                                                                value = String.valueOf(((Button)editor.getEditor()).getSelection());
                                                            else
                                                                value = values[((Button)editor.getEditor()).getSelection()?0:1];
                                                            break;
                                                        case "CCombo":
                                                            value = ((CCombo)editor.getEditor()).getText();
                                                            break;
                                                        case "Label":
                                                            value = ((Label)editor.getEditor()).getText();
                                                            break;
                                                        default:
                                                            throw new RuntimeException("ExportToExcel : Do not know how to export columns of class " + editor.getEditor().getClass().getSimpleName());
                                                    }

                                                String excelCellType = (String) tableColumn.getData("excelCellType");
                                                String excelDefault = (String) tableColumn.getData("excelDefault");

                                                if ( value.isEmpty() ) {
                                                    switch (excelDefault) {
                                                        case "blank":
                                                            cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                                            cell.setCellType(CellType.BLANK);
                                                            break;
                                                        case "zero":
                                                            cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                                            switch (excelCellType) {
                                                                case "string":
                                                                    cell.setCellType(CellType.STRING);
                                                                    cell.setCellValue("");
                                                                    break;
                                                                case "numeric":
                                                                    cell.setCellType(CellType.NUMERIC);
                                                                    cell.setCellValue(0.0);
                                                                    break;
                                                                case "boolean":
                                                                    cell.setCellType(CellType.BOOLEAN);
                                                                    cell.setCellValue(false);
                                                                    break;
                                                                case "formula":
                                                                    cell.setCellType(CellType.FORMULA);
                                                                    cell.setCellValue("");
                                                                    break;
                                                            }
                                                            break;
                                                        case "delete":
                                                            cell = row.getCell(ref.getCol(), MissingCellPolicy.RETURN_NULL_AND_BLANK);
                                                            if ( cell != null )
                                                                row.removeCell(cell);
                                                            break;
                                                    }
                                                } else {
                                                    cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                                    switch (excelCellType) {
                                                        case "string":
                                                            cell.setCellType(CellType.STRING);
                                                            cell.setCellValue(value);
                                                            break;
                                                        case "numeric":
                                                            cell.setCellType(CellType.NUMERIC);
                                                            try {
                                                                cell.setCellValue(Double.parseDouble(value));
                                                            } catch (NumberFormatException e) {
                                                                throw new RuntimeException("ExportToExcel : cell " + excelColumn + row.getRowNum() + " : failed to convert \"" + value + "\" to numeric.", e);
                                                            }
                                                            break;
                                                        case "boolean":
                                                            cell.setCellType(CellType.BOOLEAN);
                                                            cell.setCellValue(Boolean.parseBoolean(value));
                                                            break;
                                                        case "formula":
                                                            cell.setCellType(CellType.FORMULA);
                                                            cell.setCellFormula(value);
                                                            break;
                                                        case "blank":
                                                            cell.setCellType(CellType.BLANK);
                                                            break;
    
                                                        default:
                                                            throw new RuntimeException("ExportToExcel : cell " + excelColumn + row.getRowNum() + " : don't know to deal with excell cell Type \"" + excelCellType + "\".\n\nSupported values are blank, boolean, formula, numeric and string.");
                                                    }
                                                }

                                                if (logger.isTraceEnabled())
                                                    logger.trace("   '" + excelSheet + "'!" + excelColumn + (excelFirstLine + line + 1) + " -> \"" + value + "\" (" + cell.getCellTypeEnum().toString() + ")");
                                            }
                                        }
                                    }
                                    
                                    int excelLastLine = (int) table.getData("excelLastLine");
                                    for ( int line = excelFirstLine+table.getItemCount(); line < excelLastLine; ++line) {               // we do not decrease excelFirstLine by 1 because it has already been done earlier
                                        if ( logger.isTraceEnabled() ) logger.trace("   '" + excelSheet + " : removing values from line "+line);
                                        
                                        Row row = sheet.getRow(line);
                                        
                                        for (int col = 0; col < table.getColumnCount(); ++col) {
                                            TableColumn tableColumn = table.getColumn(col);
                                            String excelCellType = (String) tableColumn.getData("excelCellType");
                                            String excelDefault = (String) tableColumn.getData("excelDefault");
                                            String excelColumn = (String) tableColumn.getData("excelColumn");
                                            
                                            if (excelColumn != null) {
                                                CellReference ref = new CellReference(excelColumn);
                                                Cell cell;

                                                switch (excelDefault) {
                                                    case "blank":
                                                        if ( row == null ) {
                                                            row = sheet.createRow(line - 1);
                                                        }
                                                        cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                                        cell.setCellType(CellType.BLANK);
                                                        break;
                                                    case "zero":
                                                        if ( row == null ) {
                                                            row = sheet.createRow(line - 1);
                                                        }
                                                        cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                                        switch (excelCellType) {
                                                            case "string":
                                                                cell.setCellType(CellType.STRING);
                                                                cell.setCellValue("");
                                                                break;
                                                            case "numeric":
                                                                cell.setCellType(CellType.NUMERIC);
                                                                cell.setCellValue(0.0);
                                                                break;
                                                            case "boolean":
                                                                cell.setCellType(CellType.BOOLEAN);
                                                                cell.setCellValue(false);
                                                                break;
                                                            case "formula":
                                                                cell.setCellType(CellType.FORMULA);
                                                                cell.setCellValue("");
                                                                break;
                                                        }
                                                        break;
                                                    case "delete":
                                                        if ( row != null ) {
                                                            cell = row.getCell(ref.getCol(), MissingCellPolicy.RETURN_NULL_AND_BLANK);
                                                            if ( cell != null )
                                                                row.removeCell(cell);
                                                        }
                                                        break;
                                                        
                                                        //TODO: add an option to delete entire line
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                closePopup();
                popup(Level.ERROR, "Failed to update Excel file.", e);
                exportOk = false;
            }

            if (exportOk) {
                workbook.setForceFormulaRecalculation(true);
                if (logger.isDebugEnabled())
                    logger.debug("Saving Excel file");
                try {
                    file.close();
                    FileOutputStream outFile = new FileOutputStream(excelFile);
                    workbook.write(outFile);
                    outFile.close();
                } catch (IOException e) {
                    closePopup();
                    popup(Level.ERROR, "Failed to export updates to Excel file.", e);
                    exportOk = false;
                }
            }

            try {
                workbook.close();
            } catch (IOException ign) {
                ign.printStackTrace();
            }

            closePopup();

            if (exportOk && question("Export to Excel successful.\n\nDo you wish to open the Excel spreadsheet ?")) {
                try {
                    Desktop.getDesktop().open(new File(excelFile));
                } catch (Exception e) {
                    popup(Level.ERROR, "Failed to launch Excel.", e);
                }
            }
        }
    }
    
    
    private void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");
        
        close();
    }

    private void saveConfToJSON() {
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
                
                Table table = jsonParser.createTable(null, parentComposite, newTreeItem, selectedObject);
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
					case "label":  column = jsonParser.createLabelColumn(null, table, newTreeItem, index, selectedObject); break;
					case "text":   column = jsonParser.createTextColumn (null, table, newTreeItem, index, selectedObject); break;
					case "combo":  column = jsonParser.createComboColumn(null, table, newTreeItem, index, selectedObject); break;
					case "check":  column = jsonParser.createCheckColumn(null, table, newTreeItem, index, selectedObject); break;
				}
				
				column.setText("new "+widgetClass);
				column.addListener(SWT.Selection, sortListener);
			} else {
				switch (widgetClass) {
					case "label":  jsonParser.createLabel(null, parentComposite, newTreeItem, selectedObject); break;
					case "text":   jsonParser.createText (null, parentComposite, newTreeItem, selectedObject); break;
					case "combo":  jsonParser.createCombo(null, parentComposite, newTreeItem, selectedObject); break;
					case "check":  jsonParser.createCheck(null, parentComposite, newTreeItem, selectedObject); break;
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
            
                jsonParser.createLine(null, table, newTreeItem, selectedObject);
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
    
    /************************************************************************************************/
    


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
    
    static ModifyListener textModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
        	if ( logger.isTraceEnabled() ) logger.trace("calling textModifyListener");
        	
            updateWidget((Control)e.widget);
        }
    };
    
    static SelectionListener checkButtonSelectionListener = new SelectionListener(){
        @Override public void widgetSelected(SelectionEvent e) {
            updateWidget((Control)e.widget);
        }

        @Override public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };
    
    private static void updateWidget(Control control) {
    	String unscoppedVariable = (String)control.getData("variable");
        EObject referedEObject = (EObject)control.getData("eObject");
    	String content;
    	
    	switch ( control.getClass().getSimpleName() ) {
    		case "Button":
    			String[]values = (String[])control.getData("values");
    			if ( values == null )
    			    content = null;
    			else
    			    content = values[((Button)control).getSelection()?0:1];
    			break;
    		
    		case "StyledText":
                content = ((StyledText)control).getText();
                break;
                
    		case "CCombo":
    			content = ((CCombo)control).getText();
                break;
                
            default:
                throw new RuntimeException("Do not know how to update "+control.getClass().getSimpleName()+" control.");
    	}

        for ( Control otherControl: formVarList.getControls(referedEObject, unscoppedVariable)) {
            if ( otherControl == control ) {
                if (logger.isTraceEnabled()) logger.trace("same combo - ignored");
            } else {
                if (logger.isTraceEnabled()) logger.trace("updating "+otherControl);
                
                switch (control.getClass().getSimpleName()) {
                    case "CCombo":
                        CCombo combo = ((CCombo)otherControl);

                    	combo.removeModifyListener(textModifyListener);
                        combo.setText(content);
                        combo.addModifyListener(textModifyListener);
                        break;
    
                    case "Button":
                    	Button button= (Button)otherControl;

                    	String[]values = (String[])button.getData("values");
                        
                        button.removeSelectionListener(checkButtonSelectionListener);
                        if ( values == null )
                            button.setSelection(((Button)control).getSelection());
                        else
                            button.setSelection(FormPlugin.areEqual(content, values[0]));       // any other value than values[0] implies the button is unchecked
                        button.addSelectionListener(checkButtonSelectionListener);
                        break;
    
                    case "Label":
                        ((Label)otherControl).setText(content);
                        break;
    
                    case "StyledText":
                        StyledText text = (StyledText)otherControl;
                        
                        text.removeModifyListener(textModifyListener);
                        text.setText(content);
                        Pattern pattern = (Pattern)text.getData("pattern");		               // if a regex has been provided, we change the text color to show if it matches
                        if ( pattern != null ) {
                        	text.setStyleRange(new StyleRange(0, content.length(), pattern.matcher(content).matches() ? goodValueColor : badValueColor, null));
                        }
                        text.addModifyListener(textModifyListener);
                        break;
                }
            }
        }
    }
    
    private Listener sortListener=new Listener() {
        public void handleEvent(Event e) {
            // Because of the graphical controls and the tableEditors, it is much more easier and quicker to create a new table rather than add new tableItems and removing the old ones
            Table oldTable=((TableColumn)e.widget).getParent();TableColumn sortedColumn=(TableColumn)e.widget;oldTable.setSortColumn(sortedColumn);Integer sortDirection=(Integer)sortedColumn.getData("sortDirection");if(sortDirection==null||sortDirection==SWT.DOWN)sortDirection=SWT.UP;else sortDirection=SWT.DOWN;sortedColumn.setData("sortDirection",sortDirection);logger.trace("set sort direction "+sortDirection);oldTable.setSortDirection(sortDirection);
            TableItem[]oldTableItems=oldTable.getItems();

            if( (oldTableItems!=null) && (oldTableItems.length>0) ) {
                Table newTable = new Table(oldTable.getParent(),oldTable.getStyle());
                newTable.setLinesVisible(oldTable.getLinesVisible());
                newTable.setHeaderVisible(oldTable.getHeaderVisible());
                newTable.setLocation(oldTable.getLocation());
                newTable.setSize(oldTable.getSize());
                newTable.setLayoutData(oldTable.getLayoutData());
                newTable.setLayout(oldTable.getLayout());

                for(TableColumn oldTableColumn: oldTable.getColumns()) {
                    TableColumn newTableColumn = new TableColumn(newTable,SWT.NONE);
                    newTableColumn.setText(oldTableColumn.getText());
                    newTableColumn.setAlignment(oldTableColumn.getAlignment());
                    newTableColumn.setWidth(oldTableColumn.getWidth());
                    newTableColumn.setResizable(oldTableColumn.getResizable());
                    newTableColumn.setData("class",oldTableColumn.getData("class"));
                    newTableColumn.setData("tooltip",oldTableColumn.getData("tooltip"));
                    newTableColumn.setData("values",oldTableColumn.getData("values"));
                    newTableColumn.setData("regexp",oldTableColumn.getData("regexp"));
                    newTableColumn.setData("excelColumn",oldTableColumn.getData("excelColumn"));
                    newTableColumn.setData("excelCellType",oldTableColumn.getData("excelCellType"));
                    newTableColumn.setData("excelDefault",oldTableColumn.getData("excelDefault"));
                    newTableColumn.setData("sortDirection",oldTableColumn.getData("sortDirection"));
                    newTableColumn.addListener(SWT.Selection,sortListener);
                    newTableColumn.setImage(oldTableColumn.getImage());

                    if( oldTableColumn == oldTable.getSortColumn() ) {
                        newTable.setSortColumn(newTableColumn);
                        newTable.setSortDirection(oldTable.getSortDirection());
                    }
                }

                Arrays.sort(oldTableItems,new TableItemComparator(oldTable.indexOf(sortedColumn),sortDirection));

                for(TableItem oldTableItem:oldTableItems) {
                    TableEditor[]oldEditors = (TableEditor[])oldTableItem.getData("editors");
                    TableEditor[]newEditors = new TableEditor[oldEditors.length];

                    TableItem newTableItem = new TableItem(newTable,SWT.NONE);

                    for ( int column=0; column < oldTable.getColumnCount(); ++column) {
                        TableEditor newEditor = new TableEditor(newTable);
                        switch (oldEditors[column].getEditor().getClass().getSimpleName()) {
                            case"Label":
                                Label oldLabel = (Label)oldEditors[column].getEditor();
                                Label newLabel = new Label(newTable,SWT.WRAP|SWT.NONE);

                                newLabel.setText(oldLabel.getText());
                                newLabel.setToolTipText(oldLabel.getToolTipText());
                                newLabel.setAlignment(oldLabel.getAlignment());
                                newLabel.setData("eObject",oldLabel.getData("eObject"));
                                newLabel.setData("variable",oldLabel.getData("variable"));
                                newLabel.setData("pattern",oldLabel.getData("pattern"));
                                
                                newEditor.grabHorizontal=true;
                                newEditor.setEditor(newLabel,newTableItem,column);
                                
                                formVarList.replaceControl(oldLabel, newLabel);
                                break;
                            
                            case"StyledText":
                                StyledText oldText = (StyledText)oldEditors[column].getEditor();
                                StyledText newText = new StyledText(newTable,SWT.WRAP|SWT.NONE);
  
                                newText.setText(oldText.getText());
                                newText.setToolTipText(oldText.getToolTipText());
                                newText.setAlignment(oldText.getAlignment());
                                newText.setData("eObject",oldText.getData("eObject"));
                                newText.setData("variable",oldText.getData("variable"));
                                newText.setData("pattern",oldText.getData("pattern"));

                                newEditor.grabHorizontal=true;
                                newEditor.setEditor(newText,newTableItem,column);
                                
                                newText.addModifyListener(textModifyListener);
                                formVarList.replaceControl(oldText, newText);
                                break;

                            case"CCombo":
                                CCombo oldCombo=(CCombo)oldEditors[column].getEditor();
                                CCombo newCombo=new CCombo(newTable,SWT.NONE);

                                newCombo.setText(oldCombo.getText());
                                newCombo.setItems(oldCombo.getItems());
                                newCombo.setToolTipText(oldCombo.getToolTipText());
                                newCombo.setData("eObject",oldCombo.getData("eObject"));
                                newCombo.setData("variable",oldCombo.getData("variable"));
                                newCombo.setEditable(false);newEditor.grabHorizontal=true;
                                
                                newEditor.grabHorizontal=true;
                                newEditor.setEditor(newCombo,newTableItem,column);
                                
                                newCombo.addModifyListener(textModifyListener);
                                formVarList.replaceControl(oldCombo, newCombo);
                                break;

                            case"Button":
                                Button oldButton=(Button)oldEditors[column].getEditor();
                                Button newButton=new Button(newTable,SWT.CHECK);

                                newButton.pack();
                                newEditor.minimumWidth=newButton.getSize().x;
                                newEditor.horizontalAlignment=SWT.CENTER;
                                newButton.setAlignment(oldButton.getAlignment());
                                newButton.setData("eObject",oldButton.getData("eObject"));
                                newButton.setData("variable",oldButton.getData("variable"));
                                newButton.setData("values",oldButton.getData("values"));
                                newButton.setSelection(oldButton.getSelection());
                                newButton.addSelectionListener(checkButtonSelectionListener);
                                
                                newEditor.grabHorizontal=true;
                                newEditor.setEditor(newButton,newTableItem,column);
                                
                                newButton.addSelectionListener(checkButtonSelectionListener);
                                formVarList.replaceControl(oldButton, newButton);
                        }
                        newEditors[column]=newEditor;
                    }
                    newTableItem.setData("editors",newEditors);
                }

                logger.debug("Replacing old table with new table");newTable.setVisible(true);oldTable.dispose();}
        }
    };
    
    private class TableItemComparator implements Comparator<TableItem> {
        int columnIndex   = 0;
        int sortDirection = SWT.UP;

        public TableItemComparator(int columnIndex, int sortDirection) {
            this.columnIndex = columnIndex;
            this.sortDirection = sortDirection;
        }

        public int compare(TableItem first, TableItem second) {
            TableEditor[] editorsFirst = (TableEditor[]) first.getData("editors");

            if (editorsFirst[columnIndex] != null) {
                TableEditor[] editorsSecond = (TableEditor[]) second.getData("editors");

                switch (editorsFirst[columnIndex].getEditor().getClass().getSimpleName()) {
                    case "StyledText":
                        logger.trace("comparing \"" + ((StyledText) editorsFirst[columnIndex].getEditor()).getText() + "\" and \"" + ((StyledText) editorsSecond[columnIndex].getEditor()).getText() + "\"");
                        return Collator.getInstance().compare(((StyledText) editorsFirst[columnIndex].getEditor()).getText(), ((StyledText) editorsSecond[columnIndex].getEditor()).getText()) * (sortDirection == SWT.UP ? 1 : -1);
                    case "Button":
                        logger.trace("comparing \"" + ((Button) editorsFirst[columnIndex].getEditor()).getSelection()
                                + "\" and \"" + ((Button) editorsSecond[columnIndex].getEditor()).getSelection()
                                + "\"");
                        return Collator.getInstance().compare(((Button) editorsFirst[columnIndex].getEditor()).getSelection(), ((Button) editorsSecond[columnIndex].getEditor()).getSelection())* (sortDirection == SWT.UP ? 1 : -1);

                    case "CCombo":
                        logger.trace("comparing \"" + ((CCombo) editorsFirst[columnIndex].getEditor()).getText() + "\" and \"" + ((CCombo) editorsSecond[columnIndex].getEditor()).getText() + "\"");
                        return Collator.getInstance().compare(((CCombo) editorsFirst[columnIndex].getEditor()).getText(), ((CCombo) editorsSecond[columnIndex].getEditor()).getText()) * (sortDirection == SWT.UP ? 1 : -1);

                    default:
                        throw new RuntimeException("Do not know how to compare elements of class " + editorsFirst[columnIndex].getClass().getSimpleName());
                }
            }

            return Collator.getInstance().compare(first.getText(columnIndex), second.getText(columnIndex)) * (sortDirection == SWT.UP ? 1 : -1);
        }
    }
}
