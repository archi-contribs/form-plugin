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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
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
    
    public final static int   editorLeftposition      = 100;
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
    
    private Shell                formDialog        	  = null;
    private Button               btnUp                = null;
    private Button               btnDown              = null;
    
    private enum                 Position {Before, After, Into, End};
    
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
        	formDialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        	
            TreeItem formTreeItem = createPropertiesDialog(jsonForm);
            formTreeItem.setImage(FORM_ICON);
            
            jsonParser.createForm(jsonForm, formDialog, formTreeItem);

            // we create one CTabItem per tab array item
            CTabFolder tabFolder = (CTabFolder)formDialog.getData("tab folder");
            JSONArray tabs = jsonParser.getJSONArray(jsonForm, "tabs");
            if ( tabs != null ) {
            	@SuppressWarnings("unchecked")
				Iterator<JSONObject> tabsIterator = tabs.iterator();
                while (tabsIterator.hasNext()) {
                    JSONObject jsonTab = tabsIterator.next();

                    TreeItem tabTreeItem = new TreeItem(formTreeItem, SWT.NONE);
                    tabTreeItem.setImage(TAB_ICON);
                    
                    CTabItem tabItem = jsonParser.createTab(jsonTab, tabFolder, tabTreeItem);
                    tabTreeItem.setData("widget", tabItem.getControl());
                    
                    
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
                	newItem.setImage(TAB_ICON);
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
            		case "into":   position = Position.Into; break;
            		default:       position = Position.After;
            	}
            	
            	Menu subMenu = new Menu(treeMenu);
            	newItem.setMenu(subMenu);
            	
            	addItemsToMenu(subMenu, position, !prefix.endsWith("column"));
            }
            
            private void addItemsToMenu(Menu subMenu, Position position, boolean showTable) {
            	MenuItem newItem;
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("label");
            	newItem.setImage(LABEL_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "label");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("text");
            	newItem.setImage(TEXT_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "text");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("combo");
            	newItem.setImage(COMBO_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "combo");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	newItem = new MenuItem(subMenu, SWT.NONE);
            	newItem.setText("check box");
            	newItem.setImage(CHECK_ICON);
            	newItem.setData("position", position);
            	newItem.setData("class", "check");
            	newItem.addSelectionListener(addWidgetListener);
            	
            	if ( showTable ) {
            		newItem = new MenuItem(subMenu, SWT.NONE);
                	newItem.setText("table");
                	newItem.setImage(TABLE_ICON);
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
                    	newItem.setImage(TAB_ICON);
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
                    		addItemsToSubMenu("Add column", "into", treeMenu);
                    	}
                    	break;
                    	
            		case "lines":
                    	if ( position == Position.Before ) {
                    		newItem = new MenuItem(treeMenu, SWT.CASCADE);
                        	newItem.setText("Add into ...");
                        	
                        	Menu subMenu = new Menu(treeMenu);
                        	newItem.setMenu(subMenu);
                        	
                        	newItem = new MenuItem(subMenu, SWT.NONE);
                        	newItem.setText("line");
                        	newItem.setImage(LINE_ICON);
                        	newItem.setData("position", Position.Into);
                        	newItem.addSelectionListener(addLineListener);
                    	}
            			break;

            		case "line":
                    	newItem = new MenuItem(treeMenu, SWT.NONE);
                    	newItem.setText(prefix+" line "+suffix);
                    	newItem.setImage(LINE_ICON);
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
            TreeItem treeItem;

            String clazz = jsonParser.getString(jsonControl, "class");
            if ( clazz != null ) {
            	treeItem = new TreeItem(parentTreeItem, SWT.NONE);
            	Widget widget;
	            switch ( clazz.toLowerCase() ) {
	                case "check":
	                	widget = jsonParser.createCheck(jsonControl, parent, treeItem);
	                	treeItem.setImage(CHECK_ICON);
	                	break;
		            case "combo":
	                	widget = jsonParser.createCombo(jsonControl, parent, treeItem);
	                	treeItem.setImage(COMBO_ICON);
	                	break;
	                case "label":
	                	widget = jsonParser.createLabel(jsonControl, parent, treeItem);
	                	treeItem.setImage(LABEL_ICON);
	                	break;
	                case "table":
	                	widget = jsonParser.createTable(jsonControl, parent, treeItem);
	                	treeItem.setImage(TABLE_ICON);
	                	break;
	                case "text":
	                	widget = jsonParser.createText(jsonControl, parent, treeItem);
	                	treeItem.setImage(TEXT_ICON);
	                	break;
	                default:
	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
	            }
	            
	            if ( FormPlugin.areEqualIgnoreCase(clazz, "table") ) {
	            	TreeItem tableTreeItem = treeItem;
    	            TreeItem columnsTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
    	            columnsTreeItem.setImage(COLUMN_ICON);
    	            columnsTreeItem.setText("columns");
    	            columnsTreeItem.setData("class", "columns");
    	            columnsTreeItem.setData("widget", widget);

                    JSONArray columns = jsonParser.getJSONArray(jsonControl, "columns");
                    if ( columns != null ) {
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> columnsIterator = columns.iterator();
                        while (columnsIterator.hasNext()) {
                            JSONObject jsonColumn = columnsIterator.next();
                            
                            clazz = jsonParser.getString(jsonColumn, "class");
                            if ( clazz != null ) {
                            	treeItem = new TreeItem(columnsTreeItem, SWT.NONE);
                	            switch ( clazz.toLowerCase() ) {
	            	                case "check":
	            	                	jsonParser.createCheckColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	treeItem.setImage(CHECK_ICON);
	            	                	break;
	            		            case "combo":
	            	                	 jsonParser.createComboColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	treeItem.setImage(COMBO_ICON);
	            	                	break;
	            	                case "label":
	            	                	jsonParser.createLabelColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	treeItem.setImage(LABEL_ICON);
	            	                	break;
	            	                case "text":
	            	                	jsonParser.createTextColumn(jsonColumn, (Table)widget, treeItem, null);
	            	                	treeItem.setImage(TEXT_ICON);
	            	                	break;
	            	                default:
	            	                	throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"text\").");
                	            }
                            }
                        }
                    }
    	            
    	            TreeItem linesTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
    	            linesTreeItem.setImage(LINE_ICON);
    	            linesTreeItem.setText("lines");
    	            linesTreeItem.setData("class", "lines");
    	            linesTreeItem.setData("widget", widget);
    	            
    	            
    	            JSONArray lines = jsonParser.getJSONArray(jsonControl, "lines");
                    if ( lines != null ) {
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> linesIterator = lines.iterator();
                        while (linesIterator.hasNext()) {
                            JSONObject jsonLine = linesIterator.next();
                            
                            treeItem = new TreeItem(linesTreeItem, SWT.NONE);
                            TableItem tableItem = (TableItem)jsonParser.createLine(jsonLine, (Table)widget, treeItem);
                            
    	    	            treeItem.setData("widget", tableItem);
            	            treeItem.setImage(LINE_ICON);
            	            treeItem.setText(treeItem.getData("name")==null ? "" : (String)treeItem.getData("name"));
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

        JSONObject json = jsonParser.generateJson(tree);

        System.out.println(json.toJSONString());
        
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
			newTreeItem.setImage(TAB_ICON);
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
			logger.error("addTableListener: code not yet written");
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
					case "label":  column = jsonParser.createLabelColumn(null, table, newTreeItem, index); newTreeItem.setImage(LABEL_ICON); break;
					case "text":   column = jsonParser.createTextColumn (null, table, newTreeItem, index); newTreeItem.setImage(TEXT_ICON); break;
					case "combo":  column = jsonParser.createComboColumn(null, table, newTreeItem, index); newTreeItem.setImage(COMBO_ICON); break;
					case "check":  column = jsonParser.createCheckColumn(null, table, newTreeItem, index); newTreeItem.setImage(CHECK_ICON); break;
				}
				
				column.setText("new "+widgetClass);
				
				for ( TableItem tableItem: table.getItems() ) {
					TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
					TableEditor[] newEditors = new TableEditor[table.getColumnCount()];
					
					String[] oldCells = (String[])tableItem.getData("cells");
					String[] newCells = new String[table.getColumnCount()];
					
					int newCol = 0;
					for (int oldCol=0; oldCol < table.getColumnCount(); ++oldCol) {
						if ( oldCol == index ) {
							TableEditor editor= new TableEditor(table);
				            newEditors[index] = editor;
				            
				            switch ( widgetClass ) {
				                case "label":
				                	newCells[index] = "";
				                    logger.trace("      adding label cell with value \"" + newCells[index] + "\"");
				                    Label label = new Label(table, SWT.WRAP | SWT.NONE);
				                    label.setText(newCells[index]);
				                    editor.setEditor(label, tableItem, index);
				                    editor.grabHorizontal = true;
				                    break;
				                    
				                case "text":
				                	newCells[index] = "${void}";
				                    StyledText text = new StyledText(table, SWT.WRAP | SWT.NONE);
				                    logger.trace("      adding text cell with value \"" + newCells[index] + "\"");
				                    text.setText(newCells[index]);
				                    editor.setEditor(text, tableItem, index);
				                    editor.grabHorizontal = true;
				                    break;
				                    
				                case "combo":
				                	newCells[index] = "${void}";
				                    CCombo combo = new CCombo(table, SWT.NONE);
				                    logger.trace("      adding combo cell with value \"" + newCells[index] + "\"");
				                    combo.setText(newCells[index]);
				                    combo.setItems((String[])table.getColumn(index).getData("values"));
				                    editor.setEditor(combo, tableItem, index);
				                    editor.grabHorizontal = true;
				                    break;
				                    
				                case "check":
				                	newCells[index] = "${void}";
				                    Button check = new Button(table, SWT.CHECK);
				                    check.pack();
				                    logger.trace("      adding check cell with value \"" + newCells[index] + "\"");
				                    editor.minimumWidth = check.getSize().x;
				                    editor.horizontalAlignment = SWT.CENTER;
				                    editor.setEditor(check, tableItem, index);
				                    break;
				                    
				                default:
				                    throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to add table item for unknown object class \"" + ((String)table.getColumn(index).getData("class")) + "\"");
				    		}
							++newCol;
						}
						
						if ( oldCol < table.getColumnCount()-1 ) {
							newEditors[newCol] = oldEditors[oldCol];
							newEditors[newCol].setEditor(newEditors[newCol].getEditor(), tableItem, newCol);
							newCells[newCol] = oldCells[oldCol];
						}
						++newCol;
					}
					tableItem.setData("editors", newEditors);
					tableItem.setData("cells", newCells);
					TreeItem lineTreeItem = (TreeItem)tableItem.getData("treeitem");
					if ( lineTreeItem != null )
						lineTreeItem.setData("cells", newCells);
				}
			} else {
				switch (widgetClass) {
					case "label":  jsonParser.createLabel(null, parentComposite, newTreeItem); newTreeItem.setImage(LABEL_ICON); break;
					case "text":   jsonParser.createText (null, parentComposite, newTreeItem); newTreeItem.setImage(TEXT_ICON); break;
					case "combo":  jsonParser.createCombo(null, parentComposite, newTreeItem); newTreeItem.setImage(COMBO_ICON); break;
					case "check":  jsonParser.createCheck(null, parentComposite, newTreeItem); newTreeItem.setImage(CHECK_ICON); break;
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
            newTreeItem.setData("class", "line");
            newTreeItem.setImage(LINE_ICON);
            
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
			if ( selectedTreeItem.getItemCount() != 0 ) {
				while ( selectedTreeItem.getItemCount() != 0 ) {
					deleteTreeItem(selectedTreeItem.getItem(selectedTreeItem.getItemCount()-1));
				}
			}
			
			Widget widget = (Widget)selectedTreeItem.getData("widget");
			if ( widget != null ) {
				if ( widget instanceof TableColumn ) {
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
				
				CTabItem tabItem = (CTabItem)widget.getData("tabItem");
				
				widget.dispose();

				if ( tabItem != null ) 
					tabItem.dispose();
			}
			
			selectedTreeItem.dispose();
		}
    };
    
}
