package org.archicontribs.form;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.log4j.Priority;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.archicontribs.form.composites.CheckColumnComposite;
import org.archicontribs.form.composites.CheckComposite;
import org.archicontribs.form.composites.ComboColumnComposite;
import org.archicontribs.form.composites.ComboComposite;
import org.archicontribs.form.composites.CompositeInterface;
import org.archicontribs.form.composites.FormComposite;
import org.archicontribs.form.composites.ImageColumnComposite;
import org.archicontribs.form.composites.ImageComposite;
import org.archicontribs.form.composites.LabelColumnComposite;
import org.archicontribs.form.composites.LabelComposite;
import org.archicontribs.form.composites.LineComposite;
import org.archicontribs.form.composites.RichTextComposite;
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
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
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
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;

/**
 * Create a Dialog with graphical controls as described in the configuration
 * file
 * 
 * @author Herve Jouin
 *
 */
public class FormDialog extends Dialog {
    static final FormLogger logger     	                  = new FormLogger(FormDialog.class);

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
    public final static Image    hourglassImage           = new Image(display, FormDialog.class.getResourceAsStream("/img/hourglass.png"));
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
    public static final String[] validFilterGenre         = new String[] {"and", "or"};										// default value is the first one

    public static final Image    BIN_ICON                 = new Image(display, FormDialog.class.getResourceAsStream("/icons/bin.png"));
    public static final Image    BAS_ICON                 = new Image(display, FormDialog.class.getResourceAsStream("/icons/fleche_bas.png"));
    public static final Image    HAUT_ICON                = new Image(display, FormDialog.class.getResourceAsStream("/icons/fleche_haut.png"));
    public static final Image    PLUS_ICON                = new Image(display, FormDialog.class.getResourceAsStream("/icons/plus.png"));

    Shell             	 propertiesDialog  	  = null;
    Tree              	 tree              	  = null;
    ScrolledComposite 	 scrolledcomposite 	  = null;
    FormComposite     	 formComposite     	  = null;
    TabComposite      	 tabComposite      	  = null;
    LabelComposite    	 labelComposite    	  = null;
    ImageComposite    	 imageComposite    	  = null;
    TextComposite     	 textComposite     	  = null;
    RichTextComposite    richtextComposite    = null;
    ComboComposite    	 comboComposite    	  = null;
    CheckComposite    	 checkComposite   	  = null;
    TableComposite    	 tableComposite    	  = null;
    LabelColumnComposite labelColumnComposite = null;
    ImageColumnComposite imageColumnComposite = null;
    TextColumnComposite  textColumnComposite  = null;
    ComboColumnComposite comboColumnComposite = null;
    CheckColumnComposite checkColumnComposite = null;
    LineComposite        lineComposite        = null;

    Shell                formDialog        	  = null;
    Button               btnUp                = null;
    Button               btnDown              = null;

    enum                 Position {Before, After, Into, End}

    private String       configFilename       = null;
    private boolean      editMode             = false;
    EObject              selectedObject       = null;

    public  final static FormVarList    formVarList   = new FormVarList();


    public FormDialog(String configFilename, JSONObject jsonForm, EObject selectedObject) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        this.configFilename = configFilename;
        this.selectedObject = selectedObject;

        logger.trace("Selected object is "+FormPlugin.getDebugName(selectedObject));

        formVarList.reset();

        try {
            this.formDialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

            TreeItem formTreeItem = null;
            // If the dialog is ran against no EObject, then we switch to edit mode 
            this.editMode = ( selectedObject == null );

            if ( this.editMode ) {
                // in edit mode, we show up the graphical editor
                formTreeItem = createPropertiesDialog();
            }

            FormJsonParser.createForm(jsonForm, this.formDialog, formTreeItem);

            // if we are in form mode, then we expand the name of the form
            if ( !this.editMode ) {
                this.formDialog.setText(FormVariable.expand(this.formDialog.getText(), selectedObject));
            }

            Composite compoWait = new Composite(this.formDialog, SWT.NONE);
            FormData fd = new FormData();
            fd.left= new FormAttachment(33);
            fd.right = new FormAttachment(66);
            fd.top = new FormAttachment(33);
            fd.bottom = new FormAttachment(66);
            compoWait.setLayoutData(fd);
            compoWait.setBackground(LIGHT_GREEN_COLOR);
            compoWait.setLayout(new FormLayout());
            compoWait.moveAbove(null);

            Label lblWait = new Label(compoWait, SWT.CENTER);
            fd = new FormData();
            fd.left= new FormAttachment(0);
            fd.right = new FormAttachment(100);
            fd.top = new FormAttachment(30);
            lblWait.setLayoutData(fd);
            lblWait.setFont(TITLE_FONT);
            lblWait.setBackground(LIGHT_GREEN_COLOR);
            lblWait.setText("Please wait, we're creating the form ...");

            Label lblHourglass = new Label(compoWait, SWT.NONE);
            fd = new FormData();
            fd.top = new FormAttachment(lblWait, 5);
            fd.left = new FormAttachment(50, -50);
            lblHourglass.setLayoutData(fd);
            lblHourglass.setBackground(LIGHT_GREEN_COLOR);
            lblHourglass.setImage(FormDialog.hourglassImage);

            this.formDialog.open();
            this.formDialog.layout();
            while ( display.readAndDispatch() ) {
                // nothing to do
            }

            this.formDialog.setEnabled(false);

            // we create one CTabItem per tab array item
            CTabFolder tabFolder = (CTabFolder)this.formDialog.getData("tab folder");
            JSONArray tabs = FormJsonParser.getJSONArray(jsonForm, "tabs");
            if ( tabs != null ) {
                Iterator<JSONObject> tabsIterator = tabs.iterator();
                while (tabsIterator.hasNext()) {
                    JSONObject jsonTab = tabsIterator.next();

                    TreeItem tabTreeItem = null;
                    if ( this.editMode )
                        tabTreeItem = new TreeItem(formTreeItem, SWT.NONE);

                    CTabItem tabItem = FormJsonParser.createTab(jsonTab, tabFolder, tabTreeItem);

                    // if we are in form mode, then we expand the tab name in case it contains variables
                    if ( !this.editMode ) {
                        tabItem.setText(FormVariable.expand(tabItem.getText(), selectedObject));
                    }

                    JSONArray controls = FormJsonParser.getJSONArray(jsonTab, "controls");
                    if ( controls != null ) {
                        Composite tabItemComposite = (Composite)tabItem.getControl();
                        Iterator<JSONObject> controlsIterator = controls.iterator();
                        while (controlsIterator.hasNext()) {
                            JSONObject jsonControl = controlsIterator.next();
                            createControl(jsonControl, tabItemComposite, tabTreeItem);
                        }
                        tabItemComposite.layout();
                    }
                }
            }

            if ( this.editMode ) {
                // in edit mode, we expand the form in the tree and select it
                if ( formTreeItem != null ) {
                    formTreeItem.setExpanded(true);
                    this.tree.setSelection(formTreeItem);
                    this.tree.notifyListeners(SWT.Selection, new Event());        // shows up the form's properties
                }
            } else {
                // in form mode, the graphical editor is not shown so we activate the form's OK and CANCEL buttons
                Button cancelButton = (Button)this.formDialog.getData("cancel button");
                cancelButton.addSelectionListener(new SelectionListener() {
                    @Override public void widgetSelected(SelectionEvent e) { cancel(); }
                    @Override public void widgetDefaultSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
                });

                Button okButton = (Button)this.formDialog.getData("ok button");
                okButton.addSelectionListener(new SelectionListener() {
                    @Override public void widgetSelected(SelectionEvent e) { ok(); }
                    @Override public void widgetDefaultSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
                });

                Button exportButton = (Button)this.formDialog.getData("export button");
                exportButton.addSelectionListener(new SelectionListener() {
                    @Override public void widgetSelected(SelectionEvent e) { exportToExcel(); }
                    @Override public void widgetDefaultSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
                });


                // If there is at least one Excel sheet specified, then we show up the "export to Excel" button
                @SuppressWarnings("unchecked")
                HashSet<String> excelSheets = (HashSet<String>)this.formDialog.getData("excel sheets");
                //TODO: must be filled-in in FormJsonParser
                exportButton.setVisible(!excelSheets.isEmpty());
            }

            compoWait.dispose();
            this.formDialog.layout();
            this.formDialog.setEnabled(true);

        } catch (ClassCastException e) {
            FormDialog.popup(Level.ERROR, "Wrong key type in the configuration file \"" + configFilename + "\"", e);
            if (this.formDialog != null)
                this.formDialog.dispose();
            return;
        } catch (RuntimeException e) {
            FormDialog.popup(Level.ERROR, "Please check your configuration file \"" + configFilename + "\"", e);
            if (this.formDialog != null)
                this.formDialog.dispose();
            return;
        }

        if ( selectedObject == null ) {
            this.propertiesDialog.open();
            this.propertiesDialog.layout();
        }
    }

    /**
     * creates the propertiesDialog shell
     */
    private TreeItem createPropertiesDialog() {
        this.propertiesDialog = new Shell(this.formDialog, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.CLOSE);
        this.propertiesDialog.setSize(750, 500);
        this.propertiesDialog.setLayout(new FormLayout());

        Button btnCancel = new Button(this.propertiesDialog, SWT.NONE);
        FormData fd = new FormData();
        fd.right = new FormAttachment(100, -editorBorderMargin);
        fd.bottom = new FormAttachment(100, -editorBorderMargin);
        btnCancel.setLayoutData(fd);
        btnCancel.setText("Cancel");
        btnCancel.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent event) {cancel();}
            @Override public void widgetDefaultSelected(SelectionEvent e) {widgetSelected(e);}
        });

        Button btnSave = new Button(this.propertiesDialog, SWT.NONE);
        fd = new FormData();
        fd.right = new FormAttachment(btnCancel, -editorBorderMargin);
        fd.bottom = new FormAttachment(100, -editorBorderMargin);
        btnSave.setLayoutData(fd);
        btnSave.setText("Save");
        btnSave.addSelectionListener(new SelectionListener() {
            @Override public void widgetSelected(SelectionEvent event) {saveConfToJSON();}
            @Override public void widgetDefaultSelected(SelectionEvent e) {widgetSelected(e);}
        });

        Label horizontalBar = new Label(this.propertiesDialog, SWT.HORIZONTAL);
        fd = new FormData();
        fd.bottom = new FormAttachment(btnSave, -editorBorderMargin, SWT.TOP);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(0, 0);
        horizontalBar.setLayoutData(fd);

        this.propertiesDialog.addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                cancel();
            }
        });

        Sash sash = new Sash(this.propertiesDialog, SWT.VERTICAL | SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(40, -15);
        fd.bottom = new FormAttachment(40, 15);
        fd.left = new FormAttachment(0, 200);
        sash.setLayoutData(fd);
        sash.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ((FormData) sash.getLayoutData()).left = new FormAttachment(0, event.x);
                sash.getParent().layout();
            }
        });

        this.btnUp = new Button(this.propertiesDialog, SWT.PUSH);
        this.btnUp.setImage(HAUT_ICON);
        this.btnUp.setSize(16,16);
        this.btnUp.pack();
        fd = new FormData();
        fd.left = new FormAttachment(sash, 0, SWT.CENTER);
        fd.top = new FormAttachment(sash, -17, SWT.TOP);
        fd.bottom = new FormAttachment(sash, -1, SWT.TOP);
        this.btnUp.setLayoutData(fd);
        this.btnUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                FormDialog.this.tree.setRedraw(false);

                TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
                TreeItem parentTreeItem = selectedTreeItem.getParentItem();
                int index = parentTreeItem.indexOf(selectedTreeItem);

                TreeItem newTreeItem = moveTreeItem(parentTreeItem, selectedTreeItem, index-1);

                FormDialog.this.tree.setSelection(newTreeItem);
                FormDialog.this.tree.showSelection();
                FormDialog.this.tree.setRedraw(true);

                Widget widget = (Widget)newTreeItem.getData("widget");
                switch ( widget.getClass().getSimpleName() ) {
                    case "Composite":
                        // moving a tabItem
                        CTabFolder tabFolder = (CTabFolder)FormDialog.this.formDialog.getData("tab folder");
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

                    default:
                        // unknown class
                }
            }
        });

        this.btnDown = new Button(this.propertiesDialog, SWT.PUSH);
        this.btnDown.setImage(BAS_ICON);
        this.btnDown.setSize(16,16);
        this.btnDown.pack();
        fd = new FormData();
        fd.left = new FormAttachment(sash, 0, SWT.CENTER);
        fd.top = new FormAttachment(sash, 1, SWT.BOTTOM);
        fd.bottom = new FormAttachment(sash, 17, SWT.BOTTOM);
        this.btnDown.setLayoutData(fd);
        this.btnDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                FormDialog.this.tree.setRedraw(false);

                TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
                TreeItem parentTreeItem = selectedTreeItem.getParentItem();
                int index = parentTreeItem.indexOf(selectedTreeItem);
                TreeItem newTreeItem = moveTreeItem(parentTreeItem, selectedTreeItem, index+2);

                FormDialog.this.tree.setSelection(newTreeItem);
                FormDialog.this.tree.showSelection();
                FormDialog.this.tree.setRedraw(true);

                Widget widget = (Widget)newTreeItem.getData("widget");
                switch ( widget.getClass().getSimpleName() ) {
                    case "Composite":
                        // moving a tabItem
                        CTabFolder tabFolder = (CTabFolder)FormDialog.this.formDialog.getData("tab folder");
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

                    default:
                        // unknown class
                }
            }
        });

        this.tree = new Tree(this.propertiesDialog, SWT.BORDER);
        this.tree.setHeaderVisible(false);
        this.tree.setLinesVisible(false);

        fd = new FormData();
        fd.top = new FormAttachment(0, editorBorderMargin);
        fd.left = new FormAttachment(0, editorBorderMargin);
        fd.right = new FormAttachment(sash, -editorBorderMargin/2);
        fd.bottom = new FormAttachment(horizontalBar, -editorBorderMargin);
        this.tree.setLayoutData(fd);
        this.tree.addListener(SWT.Selection, this.treeSelectionListener);

        Menu treeMenu = new Menu(this.tree);
        this.tree.setMenu(treeMenu);
        treeMenu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {
                TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
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
                    newItem.addSelectionListener(FormDialog.this.addTabListener);
                } else {
                    if ( !selectedTreeItem.getData("class").equals("columns") && !selectedTreeItem.getData("class").equals("lines") ) {
                        newItem = new MenuItem(treeMenu, SWT.NONE);
                        newItem.setText("Delete "+selectedTreeItem.getData("class"));
                        newItem.setImage(BIN_ICON);
                        newItem.addSelectionListener(FormDialog.this.deleteListener);
                    }

                    addSubMenu(selectedTreeItem, "Insert", "before", Position.Before);
                    addSubMenu(selectedTreeItem, "Add", "after", Position.After);
                } 
            }

            private void addItemsToSubMenu(String prefix, String suffix, Menu treeSubMenu) {
                MenuItem newItem = new MenuItem(treeSubMenu, SWT.CASCADE);
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

                Menu subMenu = new Menu(treeSubMenu);
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
                newItem.addSelectionListener(FormDialog.this.addWidgetListener);

                newItem = new MenuItem(subMenu, SWT.NONE);
                newItem.setText("image");
                newItem.setImage(FormJsonParser.IMAGE_ICON);
                newItem.setData("position", position);
                newItem.setData("class", "image");
                newItem.addSelectionListener(FormDialog.this.addWidgetListener);

                newItem = new MenuItem(subMenu, SWT.NONE);
                newItem.setText("text");
                newItem.setImage(FormJsonParser.TEXT_ICON);
                newItem.setData("position", position);
                newItem.setData("class", "text");
                newItem.addSelectionListener(FormDialog.this.addWidgetListener);

                newItem = new MenuItem(subMenu, SWT.NONE);
                newItem.setText("richtext");
                newItem.setImage(FormJsonParser.RICHTEXT_ICON);
                newItem.setData("position", position);
                newItem.setData("class", "richtext");
                newItem.addSelectionListener(FormDialog.this.addWidgetListener);

                newItem = new MenuItem(subMenu, SWT.NONE);
                newItem.setText("combo");
                newItem.setImage(FormJsonParser.COMBO_ICON);
                newItem.setData("position", position);
                newItem.setData("class", "combo");
                newItem.addSelectionListener(FormDialog.this.addWidgetListener);

                newItem = new MenuItem(subMenu, SWT.NONE);
                newItem.setText("check box");
                newItem.setImage(FormJsonParser.CHECK_ICON);
                newItem.setData("position", position);
                newItem.setData("class", "check");
                newItem.addSelectionListener(FormDialog.this.addWidgetListener);

                if ( showTable ) {
                    newItem = new MenuItem(subMenu, SWT.NONE);
                    newItem.setText("table");
                    newItem.setImage(FormJsonParser.TABLE_ICON);
                    newItem.setData("position", position);
                    newItem.addSelectionListener(FormDialog.this.addTableListener);
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
                        newItem.addSelectionListener(FormDialog.this.addTabListener);

                        if ( position == Position.Before ) {
                            addItemsToSubMenu("Add", "into", treeMenu);
                        }
                        break;

                    case "label":
                    case "image":
                    case "text":
                    case "richtext":
                    case "combo":
                    case "check":
                    case "table":
                        addItemsToSubMenu(prefix, suffix, treeMenu);

                        if ( position == Position.Before ) {
                            //TODO: add column and line into table
                        }
                        break;

                    case "labelColumn":
                    case "imageColumn":
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
                            newItem.addSelectionListener(FormDialog.this.addLineListener);
                        }
                        break;

                    case "line":
                        newItem = new MenuItem(treeMenu, SWT.NONE);
                        newItem.setText(prefix+" line "+suffix);
                        newItem.setImage(FormJsonParser.LINE_ICON);
                        newItem.setData("position", position);
                        newItem.addSelectionListener(FormDialog.this.addLineListener);
                        break;

                    default:
                        // unknown class
                }
            }
        });




        TreeItem formTreeItem = new TreeItem(this.tree, SWT.NONE);

        this.scrolledcomposite = new ScrolledComposite(this.propertiesDialog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(0, editorBorderMargin);
        fd.left = new FormAttachment(sash, editorBorderMargin/2);
        fd.right = new FormAttachment(100, -editorBorderMargin);
        fd.bottom = new FormAttachment(horizontalBar, -editorBorderMargin);
        this.scrolledcomposite.setLayoutData(fd);


        // we create the composites
        this.formComposite 		 = new FormComposite(this.scrolledcomposite, SWT.NONE);
        this.tabComposite 		 = new TabComposite(this.scrolledcomposite, SWT.NONE);
        this.labelComposite 	 = new LabelComposite(this.scrolledcomposite, SWT.NONE);
        this.imageComposite 	 = new ImageComposite(this.scrolledcomposite, SWT.NONE);
        this.textComposite 		 = new TextComposite(this.scrolledcomposite, SWT.NONE);
        this.richtextComposite 	 = new RichTextComposite(this.scrolledcomposite, SWT.NONE);
        this.comboComposite 	 = new ComboComposite(this.scrolledcomposite, SWT.NONE);
        this.checkComposite 	 = new CheckComposite(this.scrolledcomposite, SWT.NONE);
        this.tableComposite 	 = new TableComposite(this.scrolledcomposite, SWT.NONE);
        this.labelColumnComposite = new LabelColumnComposite(this.scrolledcomposite, SWT.NONE);
        this.imageColumnComposite = new ImageColumnComposite(this.scrolledcomposite, SWT.NONE);
        this.textColumnComposite  = new TextColumnComposite(this.scrolledcomposite, SWT.NONE);
        this.comboColumnComposite = new ComboColumnComposite(this.scrolledcomposite, SWT.NONE);
        this.checkColumnComposite = new CheckColumnComposite(this.scrolledcomposite, SWT.NONE);
        this.lineComposite        = new LineComposite(this.scrolledcomposite, SWT.NONE);

        return formTreeItem;
    }

    /**
     * this listener is called each time a treeItem is selected in the tree
     */
    private Listener treeSelectionListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            FormDialog.this.scrolledcomposite.setContent(null);

            if ( FormDialog.this.tree.getSelectionCount() != 0 ) {
                TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
                TreeItem parentTreeItem = selectedTreeItem.getParentItem();

                FormDialog.this.btnUp.setEnabled(parentTreeItem!=null && parentTreeItem.indexOf(selectedTreeItem)!=0 && !selectedTreeItem.getData("class").equals("columns") && !selectedTreeItem.getData("class").equals("lines"));
                FormDialog.this.btnDown.setEnabled(parentTreeItem!=null && parentTreeItem.indexOf(selectedTreeItem)!=parentTreeItem.getItemCount()-1 && !selectedTreeItem.getData("class").equals("columns") && !selectedTreeItem.getData("class").equals("lines"));

                TreeItem tabTreeItem = selectedTreeItem;
                while ( tabTreeItem != null && !tabTreeItem.getData("class").equals("tab") )
                    tabTreeItem = tabTreeItem.getParentItem();
                if ( tabTreeItem != null ) {
                    CTabFolder tabFolder = ((CTabFolder)FormDialog.this.formDialog.getData("tab folder"));
                    Widget tabWidget = (Widget)tabTreeItem.getData("widget");
                    for ( int i=0; i < tabFolder.getItemCount(); ++i ) {
                        if ( tabFolder.getItem(i).getControl() == tabWidget ) {
                            tabFolder.setSelection(i);
                        }
                    }
                }

                CompositeInterface composite;
                switch ( (String)selectedTreeItem.getData("class") ) {
                    case "form":		composite = FormDialog.this.formComposite; break;
                    case "tab":			composite = FormDialog.this.tabComposite; break;
                    case "label":		composite = FormDialog.this.labelComposite; break;
                    case "image":       composite = FormDialog.this.imageComposite; break;
                    case "text":		composite = FormDialog.this.textComposite; break;
                    case "richtext":    composite = FormDialog.this.richtextComposite; break;
                    case "combo":		composite = FormDialog.this.comboComposite; break;
                    case "check":		composite = FormDialog.this.checkComposite; break;
                    case "table":		composite = FormDialog.this.tableComposite; break;
                    case "columns":     return;				// TODO: create composite to show how many columns are defined
                    case "labelColumn":	composite = FormDialog.this.labelColumnComposite; break;
                    case "imageColumn":	composite = FormDialog.this.imageColumnComposite; break;
                    case "textColumn":	composite = FormDialog.this.textColumnComposite; break;
                    case "comboColumn":	composite = FormDialog.this.comboColumnComposite; break;
                    case "checkColumn":	composite = FormDialog.this.checkColumnComposite; break;
                    case "lines":       return;				// TODO: create composite to show how many lines are defined
                    case "line":        composite = FormDialog.this.lineComposite; break;
                    default:
                        throw new RuntimeException ("Do not know how to manage "+(String)selectedTreeItem.getData("class")+" objects.");
                }
                FormDialog.this.scrolledcomposite.setContent((Composite)composite);

                if ( composite != null ) {
                    Widget widget = (Widget)selectedTreeItem.getData("widget");

                    composite.setVisible(true);
                    composite.setData("shell", FormDialog.this.propertiesDialog);
                    composite.setData("treeItem", selectedTreeItem);
                    composite.setData("class", selectedTreeItem.getData("class"));
                    composite.setData("widget", widget);

                    @SuppressWarnings("unchecked")
                    Set<String> keys = (Set<String>)selectedTreeItem.getData("editable keys");
                    if ( keys != null ) {
                        for ( String key: keys)
                            composite.set(key, selectedTreeItem.getData(key));
                    }

                    // we adapt the widgets to their content and recalculate the composite size (for the scroll bars)
                    FormDialog.this.scrolledcomposite.setExpandHorizontal(true);
                    FormDialog.this.scrolledcomposite.setExpandVertical(true);
                    FormDialog.this.scrolledcomposite.setMinSize(((Composite)composite).computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
        TreeItem treeItem = null;

        String clazz = FormJsonParser.getString(jsonControl, "class", null);
        if ( clazz == null )
            FormPlugin.error(FormPosition.getPosition(null) + "\n\nMissing \"class\" keyword.");
        else {
            String variableValue;
            Control control = null;

            if ( this.editMode )
                treeItem = new TreeItem(parentTreeItem, SWT.NONE);

            switch ( clazz.toLowerCase() ) {
                case "check":
                    control = FormJsonParser.createCheck(jsonControl, parent, treeItem, this.selectedObject);

                    // In form mode
                    if ( !this.editMode ) {
                        // we expand the values 
                        String[] values = (String[])control.getData("values");
                        if ( values != null ) {
                            for ( int i = 0; i < values.length; ++i ) {
                                values[i] = FormVariable.expand(values[i], this.selectedObject);
                            }
                        }

                        // we expand the variable
                        variableValue = FormVariable.expand((String)control.getData("variable"), this.selectedObject); 
                        if ( FormPlugin.isEmpty(variableValue) || (control.getData("forceDefault")!=null && (Boolean)control.getData("forceDefault")) )
                            variableValue = FormVariable.expand((String)control.getData("default"), this.selectedObject);

                        if ( values == null || values.length == 0 ) 										// should be "true" or "false"
                            ((Button)control).setSelection(FormPlugin.areEqualIgnoreCase(variableValue, "true"));
                        else																				// should be values[0] or values[1]
                            ((Button)control).setSelection(FormPlugin.areEqualIgnoreCase(variableValue, values[0]));

                        // we update the widgets that refer to the same variable when the user changes its value
                        ((Button)control).addSelectionListener(checkButtonSelectionListener);
                    }
                    break;

                case "combo":
                    control = FormJsonParser.createCombo(jsonControl, parent, treeItem, this.selectedObject);

                    // In form mode
                    if ( !this.editMode ) {
                        // we update the widgets that refer to the same variable when the user changes its value
                        ((CCombo)control).addModifyListener(textModifyListener);
                    }
                    break;

                case "label":
                    control = FormJsonParser.createLabel(jsonControl, parent, treeItem, this.selectedObject);
                    break;

                case "image":
                    control = FormJsonParser.createImage(jsonControl, parent, treeItem, this.selectedObject);
                    break;

                case "table":
                    Table table = FormJsonParser.createTable(jsonControl, parent, treeItem, this.selectedObject);
                    TreeItem tableTreeItem = treeItem;
                    TreeItem columnsTreeItem = null;

                    // if form mode, we replace the controls' tooltip by its expanded value in case it contains a variable
                    table.setToolTipText(FormVariable.expand(table.getToolTipText(), this.selectedObject));

                    // required by the graphical editor
                    if ( this.editMode ) {
                        columnsTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
                        columnsTreeItem.setImage(FormJsonParser.COLUMN_ICON);
                        columnsTreeItem.setText("columns");
                        columnsTreeItem.setData("class", "columns");
                        columnsTreeItem.setData("widget", table);
                    }

                    JSONArray columns = FormJsonParser.getJSONArray(jsonControl, "columns");
                    if ( columns != null ) {
                        Iterator<JSONObject> columnsIterator = columns.iterator();
                        while (columnsIterator.hasNext()) {
                            JSONObject jsonColumn = columnsIterator.next();

                            clazz = FormJsonParser.getString(jsonColumn, "class", null);
                            if ( clazz == null )
                                FormPlugin.error(FormPosition.getPosition(null) + "\n\nMissing \"class\" keyword.");
                            else {
                                if ( this.editMode )
                                    treeItem = new TreeItem(columnsTreeItem, SWT.NONE);

                                TableColumn tableColumn = null;

                                switch ( clazz.toLowerCase() ) {
                                    case "check":
                                        tableColumn = FormJsonParser.createCheckColumn(jsonColumn, table, treeItem, null, this.selectedObject);
                                        break;
                                    case "combo":
                                        tableColumn = FormJsonParser.createComboColumn(jsonColumn, table, treeItem, null, this.selectedObject);
                                        break;
                                    case "label":
                                        tableColumn = FormJsonParser.createLabelColumn(jsonColumn, table, treeItem, null, this.selectedObject);
                                        break;
                                    case "image":
                                        tableColumn = FormJsonParser.createImageColumn(jsonColumn, table, treeItem, null, this.selectedObject);
                                        break;
                                    case "text":
                                        tableColumn = FormJsonParser.createTextColumn(jsonColumn, table, treeItem, null, this.selectedObject);
                                        if ( !this.editMode ) {
                                            if ( !FormPlugin.isEmpty((String)tableColumn.getData("regexp")) ) {
                                                tableColumn.setData("pattern", Pattern.compile((String)tableColumn.getData("regexp")));
                                                // if the tooltip is empty but a regexp is defined,then we add a tooltip with a little help message about the regexp
                                                if ( FormPlugin.isEmpty(tableColumn.getToolTipText()) )
                                                    tableColumn.setToolTipText("Your text should match the following regexp :\n" + (String)tableColumn.getData("regexp"));
                                            }                           
                                        }
                                        break;
                                    case "richtext":
                                        tableColumn = FormJsonParser.createRichTextColumn(jsonColumn, table, treeItem, null, this.selectedObject);
                                        break;
                                    default:
                                        throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"text\").");
                                }
                                tableColumn.addListener(SWT.Selection, this.sortListener);
                            }
                        }
                    }

                    TreeItem linesTreeItem = null;

                    if ( this.editMode ) {
                        linesTreeItem = new TreeItem(tableTreeItem, SWT.NONE);
                        linesTreeItem.setImage(FormJsonParser.LINE_ICON);
                        linesTreeItem.setText("lines");
                        linesTreeItem.setData("class", "lines");
                        linesTreeItem.setData("widget", table);
                    }


                    JSONArray lines = FormJsonParser.getJSONArray(jsonControl, "lines");
                    if ( lines != null ) {
                        Iterator<JSONObject> linesIterator = lines.iterator();
                        while (linesIterator.hasNext()) {
                            JSONObject jsonLine = linesIterator.next();

                            if ( this.editMode ) {
                                treeItem = new TreeItem(linesTreeItem, SWT.NONE);
                                if ( treeItem.getData("name")!=null ) treeItem.setText((String)treeItem.getData("name"));
                            }

                            FormJsonParser.createLine(jsonLine, table, treeItem, this.selectedObject);
                        }
                    }
                    table.layout();

                    control = table;
                    break;

                case "text":
                    control = FormJsonParser.createText(jsonControl, parent, treeItem, this.selectedObject);

                    if ( !this.editMode ) {
                        if ( !FormPlugin.isEmpty((String)control.getData("regexp")) ) {
                            control.setData("pattern", Pattern.compile((String)control.getData("regexp")));
                            // if the tooltip is empty but a regexp is defined,then we add a tooltip with a little help message about the regexp
                            if ( FormPlugin.isEmpty(control.getToolTipText()) )
                                control.setToolTipText("Your text should match the following regexp :\n" + (String)control.getData("regexp"));
                        }	                		
                        ((StyledText)control).addModifyListener(textModifyListener);
                    }
                    break;

                case "richtext":
                    control = FormJsonParser.createRichText(jsonControl, parent, treeItem, this.selectedObject);
                    break;
                default:
                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + clazz + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
            }

            if ( !this.editMode ) {
                // We reference the variable and the control to the eObject that the variable refers to
                if ( control.getData("variable") != null ) {
                    EObject referedEObject;
                    String unscoppedVariable;

                    referedEObject = FormVariable.getReferedEObject((String)control.getData("variable"), this.selectedObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable((String)control.getData("variable"), this.selectedObject);
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
    void ok() {
        if (logger.isDebugEnabled())
            logger.debug("Ok button selected by user.");

        IArchimateModel model;

        if (this.selectedObject instanceof IArchimateModel) {
            model = ((IArchimateModel) this.selectedObject).getArchimateModel();
        } else if (this.selectedObject instanceof IDiagramModel) {
            model = ((IDiagramModel) this.selectedObject).getArchimateModel();
        } else if (this.selectedObject instanceof IDiagramModelObject) {
            model = ((IDiagramModelObject) this.selectedObject).getDiagramModel().getArchimateModel();
        } else if (this.selectedObject instanceof IDiagramModelConnection) {
            model = ((IDiagramModelConnection) this.selectedObject).getDiagramModel().getArchimateModel();
        } else if (this.selectedObject instanceof IArchimateElement) {
            model = ((IArchimateElement) this.selectedObject).getArchimateModel();
        } else if (this.selectedObject instanceof IArchimateRelationship) {
            model = ((IArchimateRelationship) this.selectedObject).getArchimateModel();
        } else if (this.selectedObject instanceof IFolder) {
            model = ((IFolder) this.selectedObject).getArchimateModel();
        } else {
            popup(Level.ERROR, "Failed to get the model for "+FormPlugin.getDebugName(this.selectedObject));
            return;
        }

        CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
        try {
            for (Control control : this.formDialog.getChildren()) {
                save(compoundCommand, control);
            }
        } catch (RuntimeException e) {
            popup(Level.ERROR, "Failed to save variables.", e);
            return;
        }

        logger.trace("Executing "+compoundCommand.size()+" command ...");

        CommandStack stack = (CommandStack) model.getAdapter(CommandStack.class);
        stack.execute(compoundCommand);

        close();
    }

    private void save(CompoundCommand compoundCommand, Control control) throws RuntimeException {
        logger.trace("Saving "+control.getClass().getSimpleName()+" "+control.getData("name"));
        switch (control.getClass().getSimpleName()) {
            case "Label":
                break;					// nothing to save here

            case "Button":
            case "CCombo":
            case "StyledText":
            case "FormRichTextEditor":
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
            case "FormRichTextEditor":
                value = ((FormRichTextEditor) control).getText();
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
                    FormVariable.setVariable(compoundCommand, unscoppedVariable, (String)this.formDialog.getData("variable separator"), "", referedEObject);
                    break;
                case "delete":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : deleting property.");
                    FormVariable.setVariable(compoundCommand, unscoppedVariable, (String)this.formDialog.getData("variable separator"), null, referedEObject);
                    break;

                default:
                    // unknown value
            }
        } else {
            if (logger.isTraceEnabled())
                logger.trace("   value is not empty.");
            FormVariable.setVariable(compoundCommand, unscoppedVariable, (String)this.formDialog.getData("variable separator"), value, referedEObject);
        }
    }

    void exportToExcel() {
        if (logger.isDebugEnabled())
            logger.debug("Export button selected by user.");

        FileDialog fsd = new FileDialog(this.formDialog, SWT.SINGLE);
        fsd.setFilterExtensions(new String[] { "*.xls*", "*.xlt*", "*.*" });
        fsd.setText("Select Excel File...");
        String excelFile = fsd.open();

        // we wait for the dialog disposal
        while (display.readAndDispatch()) {
            // nothing to do as the readAnddispatch() method does everything is needed 
        }

        boolean exportOk = false;
        Sheet sheet;

        if (excelFile != null) {
            try ( FileInputStream file = new FileInputStream(excelFile) ){
                try ( Workbook workbook = WorkbookFactory.create(file) ) {
                    // we check that all the sheets already exist
                    @SuppressWarnings("unchecked")
                    HashSet<String> excelSheets = (HashSet<String>)this.formDialog.getData("excel sheets");
                    for (String sheetName : excelSheets) {
                        sheet = workbook.getSheet(sheetName);
                        if (sheet == null) {
                            closePopup();
                            popup(Level.ERROR, "The file " + excelFile + " does not contain a sheet called \"" + sheetName + "\"");
                            // TODO : add a preference to create the sheet
                            return;
                        }
                    }

                    try {
                        // we go through all the controls and export the corresponding excel cells
                        CTabFolder tabFolder = (CTabFolder)this.formDialog.getData("tab folder");
                        for (CTabItem tabItem : tabFolder.getItems()) {
                            if (logger.isDebugEnabled()) logger.debug("Exporting tab " + tabItem.getText());

                            Composite composite = (Composite) tabItem.getControl();
                            for (Control control : composite.getChildren()) {
                                String excelSheet = (String) control.getData("excelSheet");

                                if (excelSheet != null) {
                                    sheet = workbook.getSheet(excelSheet);		// cannot be null as it has been checked before

                                    if ((control instanceof StyledText) || (control instanceof Label)
                                            || (control instanceof CCombo) || (control instanceof Button)) {
                                        String excelCell = (String) control.getData("excelCell");

                                        if ( excelCell != null ) {
                                            CellReference ref = new CellReference(excelCell);
                                            Row row = sheet.getRow(ref.getRow());
                                            if (row == null) {
                                                row = sheet.createRow(ref.getRow());
                                            }

                                            String value = null;
                                            Image image = null;
                                            switch (control.getClass().getSimpleName()) {
                                                case "StyledText":
                                                    value = ((StyledText) control).getText();
                                                    break;
                                                case "Label":
                                                    if ( ((Label) control).getImage() != null )
                                                        image = ((Label) control).getImage();
                                                    else
                                                        value = ((Label) control).getText();
                                                    break;
                                                case "CCombo":
                                                    value = ((CCombo) control).getText();
                                                    break;
                                                case "Button":
                                                    String[]values = (String[])control.getData("values");
                                                    if ( values == null )
                                                        value = String.valueOf(((Button)control).getSelection());
                                                    else
                                                        value = values[((Button)control).getSelection()?0:1];
                                                    break;
                                                default:
                                                    throw new RuntimeException("ExportToExcel : Do not know how to export controls of class " + control.getClass().getSimpleName());
                                            }

                                            if( image != null )
                                                excelWriteImage(row, ref.getCol(), image);
                                            else
                                                excelWriteCell(row, ref.getCol(), (String)control.getData("excelCellType"), value, (String)control.getData("excelDefault"));
                                        }
                                    } else {
                                        if (control instanceof Table) {
                                            if (logger.isDebugEnabled()) logger.debug("Exporting table");
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
                                                        TableEditor editor = ((TableEditor[]) tableItem.getData("editors"))[col];

                                                        String value = null;
                                                        Image image = null;

                                                        if (editor == null)
                                                            value = tableItem.getText(col);
                                                        else {
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
                                                                    if ( ((Label)editor.getEditor()).getImage() != null )
                                                                        image = ((Label)editor.getEditor()).getImage();
                                                                    else
                                                                        value = ((Label)editor.getEditor()).getText();
                                                                    break;
                                                                default:
                                                                    throw new RuntimeException("ExportToExcel : Do not know how to export columns of class " + editor.getEditor().getClass().getSimpleName());
                                                            }
                                                        }

                                                        if( image != null )
                                                            excelWriteImage(row, ref.getCol(), image);
                                                        else
                                                            excelWriteCell(row, ref.getCol(), (String)tableColumn.getData("excelCellType"), value, (String)tableColumn.getData("excelDefault"));
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

                                                    if ( excelCellType == null )
                                                        excelCellType = "string";
                                                    if (excelDefault == null )
                                                        excelDefault = "zero";

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
                                                                    default:
                                                                        // unknown value
                                                                }
                                                                break;

                                                            case "delete":
                                                                if ( row != null ) {
                                                                    cell = row.getCell(ref.getCol(), MissingCellPolicy.RETURN_NULL_AND_BLANK);
                                                                    if ( cell != null )
                                                                        row.removeCell(cell);
                                                                }
                                                                break;

                                                            default:
                                                                // unknown value

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
                        exportOk = true;
                    } catch (Exception e) {
                        closePopup();
                        popup(Level.ERROR, "Failed to update Excel file.", e);
                    }

                    if (exportOk) {
                        workbook.setForceFormulaRecalculation(true);
                        if (logger.isDebugEnabled())
                            logger.debug("Saving Excel file");

                        try ( FileOutputStream outFile = new FileOutputStream(excelFile) ) {
                            workbook.write(outFile);
                        } catch (Exception e) {
                            closePopup();
                            popup(Level.ERROR, "Failed to export updates to Excel file.", e);
                            exportOk = false;
                        }
                    }
                } catch (IOException | InvalidFormatException | EncryptedDocumentException e) {
                    closePopup();
                    popup(Level.ERROR, "The file " + excelFile + " seems not to be an Excel file!", e);
                    // TODO: add an option to create an empty Excel file
                    exportOk = false;
                }
            } catch (IOException e) {
                popup(Level.ERROR, "Cannot open the Excel file.", e);
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

    private static void excelWriteCell(Row row, short column, String cellType, String value, String whenEmpty) throws RuntimeException {
        Cell cell = null;

        String excelCellType =  FormPlugin.isEmpty(cellType) ? "string" : cellType;
        String excelDefault  =  FormPlugin.isEmpty(whenEmpty) ? "blank" : whenEmpty;

        if ( value.isEmpty() ) {
            switch (excelDefault) {
                case "blank":
                    cell = row.getCell(column, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.BLANK);
                    break;

                case "delete":
                    cell = row.getCell(column, MissingCellPolicy.RETURN_NULL_AND_BLANK);
                    if ( cell != null )
                        row.removeCell(cell);
                    break;

                case "zero":
                    cell = row.getCell(column, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    switch (excelCellType) {
                        case "boolean":
                            cell.setCellType(CellType.BOOLEAN);
                            cell.setCellValue(false);
                            break;
                        case "formula":
                            cell.setCellType(CellType.FORMULA);
                            cell.setCellValue("");
                            break;
                        case "numeric":
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellValue(0.0);
                            break;
                        case "string":
                            cell.setCellType(CellType.STRING);
                            cell.setCellValue("");
                            break;
                        default:
                            throw new RuntimeException("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : don't know to deal with excell cell type \"" + excelCellType + "\".\n\nSupported values are blank, boolean, formula, numeric and string.");
                    }
                    break;

                default:
                    cell = row.getCell(column, MissingCellPolicy.RETURN_NULL_AND_BLANK);
                    throw new RuntimeException("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : don't know to deal with excell cell default \"" + excelCellType + "\".\n\nSupported values are blank, boolean, formula, numeric and string.");
            }
            return;
        } 

        // if we're here, this mean there is a value to write to the cell
        cell = row.getCell(column, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        switch (excelCellType) {
            case "boolean":
                cell.setCellType(CellType.BOOLEAN);
                cell.setCellValue(Boolean.parseBoolean(value));
                logger.trace("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : exported boolean \"" + Boolean.parseBoolean(value) + "\"");
                break;
            case "formula":
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula(value);
                logger.trace("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : exported formula \"" + value + "\"");
                break;
            case "numeric":
                cell.setCellType(CellType.NUMERIC);
                try {
                    cell.setCellValue(Double.parseDouble(value));
                    logger.trace("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : exported numeric \"" + Double.parseDouble(value) + "\"");
                } catch (NumberFormatException e) {
                    throw new RuntimeException("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : failed to convert \"" + value + "\" to numeric.", e);
                }
                break;
            case "string":
                cell.setCellType(CellType.STRING);
                cell.setCellValue(value);
                logger.trace("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : exported string \"" + value + "\"");
                break;

            default:
                throw new RuntimeException("ExportToExcel : cell " + cell.getAddress().formatAsString() + " : don't know to deal with excell cell type \"" + excelCellType + "\".\n\nSupported values are blank, boolean, formula, numeric and string.");
        }
    }

    @SuppressWarnings("resource")
    private static void excelWriteImage(Row row, short column, Image image) throws RuntimeException {
        Sheet sheet = row.getSheet();
        Workbook wb = sheet.getWorkbook();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        // we first remove any image that is anchored to the same cell
        if (drawing instanceof HSSFPatriarch) {
            HSSFPatriarch hp = (HSSFPatriarch)drawing;
            for (HSSFShape hs : hp.getChildren()) {
                if (hs instanceof Picture)  {
                    ClientAnchor anchor = ((Picture)hs).getClientAnchor();
                    if ( anchor.getCol1() == column && anchor.getRow1() == row.getRowNum() )
                        hp.removeShape(hs);
                }
            }
        } else {
            XSSFDrawing xdrawing = (XSSFDrawing)drawing;
            for (XSSFShape xs : xdrawing.getShapes()) {
                if (xs instanceof Picture) {
                    ClientAnchor anchor = ((Picture)xs).getClientAnchor();
                    if ( anchor.getCol1() == column && anchor.getRow1() == row.getRowNum() ) {
                        logger.info("export of image cancelled as an image is already present in sheet \""+sheet.getSheetName()+"\" row="+anchor.getRow1()+" col="+anchor.getCol1());
                        return;
                    }
                }
            }
        }



        // we create a PNG from the widget image
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[] {image.getImageData()};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageLoader.save(baos, SWT.IMAGE_PNG);

        // we add the PNG to Excel
        int imageIndex = wb.addPicture(baos.toByteArray(), Workbook.PICTURE_TYPE_PNG);        
        ClientAnchor anchor = wb.getCreationHelper().createClientAnchor();
        anchor.setRow1(row.getRowNum());
        anchor.setCol1(column);

        logger.trace("exporting image ...");
        Picture pict = drawing.createPicture(anchor, imageIndex);
        pict.resize();		//Reset the image to the original size
    }

    void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");

        close();
    }

    void saveConfToJSON() {
        if (logger.isDebugEnabled())
            logger.debug("Save button selected by user.");

        JSONObject json = null;
        try {
            json = FormJsonParser.generateJson(this.tree);
        } catch (RuntimeException e) {
            FormDialog.popup(Level.ERROR, "Failed to convert configuration to JSON format.", e);
            return;
        }

        String jsonString = json.toJSONString();

        // we try to indent the Json string
        // do not work from Jdk 15 !!!
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine scriptEngine = manager.getEngineByName("JavaScript");
            scriptEngine.put("jsonString", jsonString);
            scriptEngine.eval("result = JSON.stringify(JSON.parse(jsonString), null, 3)");
            jsonString = (String) scriptEngine.get("result");
        } catch (@SuppressWarnings("unused") Exception ign) {
            // if we cannot indent the json string, that's not a big deal
        }

        try (FileWriter file = new FileWriter(this.configFilename)) {
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            FormDialog.popup(Level.ERROR, "Failed to write configuration into file \"" + this.configFilename + "\"", e);
            return;
        }

        close();
    }

    /**
     * Called when the user clicks on the close button
     */
    private void close() {
        if ( this.formDialog != null ) {
            this.formDialog.dispose();
            this.formDialog = null;
        }

        if ( this.propertiesDialog != null ) {
            this.propertiesDialog.dispose();
            this.propertiesDialog = null;
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

    SelectionListener addTabListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Position position = (Position)((MenuItem)e.getSource()).getData("position");
            TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
            TreeItem parentTreeItem = selectedTreeItem.getParentItem();
            CTabFolder tabFolder = (CTabFolder)FormDialog.this.formDialog.getData("tab folder"); 
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
                    default:
                        // unknown position
                }
            }

            TreeItem newTreeItem = new TreeItem(parentTreeItem, SWT.NONE, index);
            newTreeItem.setText("new tab");
            newTreeItem.setImage(FormJsonParser.TAB_ICON);
            newTreeItem.setData("class", "tab");
            FormJsonParser.setData(newTreeItem, "name", "new tab");
            FormJsonParser.setData(newTreeItem, "foreground", null);
            FormJsonParser.setData(newTreeItem, "background", null);

            CTabItem newTabItem = new CTabItem(tabFolder, SWT.MULTI, index);
            newTabItem.setText("new tab");
            newTabItem.setData("treeItem", newTreeItem);

            Composite composite = new Composite(tabFolder, SWT.NONE);
            composite.setForeground(FormDialog.this.formDialog.getForeground());
            composite.setBackground(FormDialog.this.formDialog.getBackground());
            composite.setFont(FormDialog.this.formDialog.getFont());
            composite.setData("tabItem", newTabItem);

            newTabItem.setControl(composite);
            newTreeItem.setData("widget", composite);

            FormDialog.this.tree.setSelection(newTreeItem);
            FormDialog.this.tree.notifyListeners(SWT.Selection, new Event());        // shows up the tab's properties
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    SelectionListener addTableListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Position position = (Position)((MenuItem)e.getSource()).getData("position");
            TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
            TreeItem parentTreeItem = selectedTreeItem.getParentItem();
            TreeItem currentTreeItem = null;
            int index = 0;

            if ( parentTreeItem == null ) {
                parentTreeItem = selectedTreeItem;
                currentTreeItem = selectedTreeItem;
                index = parentTreeItem.getItemCount();
            } else {
                switch ( position ) {
                    case Before: currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem);     break;
                    case After:  currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem)+1;   break;
                    case End:    currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();                break;
                    case Into:   currentTreeItem = selectedTreeItem; index = selectedTreeItem.getItemCount();              break;
                    default:
                        // unknown position, let's default to "End"
                        currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();
                }
            }

            TreeItem newTreeItem = new TreeItem(currentTreeItem, SWT.NONE, index);
            newTreeItem.setImage(FormJsonParser.TABLE_ICON);
            newTreeItem.setText("new table");

            logger.trace("      adding table");
            Composite parentComposite = (Composite)currentTreeItem.getData("widget");

            Table table = FormJsonParser.createTable(null, parentComposite, newTreeItem, FormDialog.this.selectedObject);
            FormJsonParser.setData(newTreeItem, "name", "new table");

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

            FormDialog.this.tree.setSelection(newTreeItem);
            FormDialog.this.tree.notifyListeners(SWT.Selection, new Event());        // shows up the table's properties
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    SelectionListener addWidgetListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Position position = (Position)((MenuItem)e.getSource()).getData("position");
            String widgetClass = (String)((MenuItem)e.getSource()).getData("class"); 
            TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
            TreeItem parentTreeItem = selectedTreeItem.getParentItem();
            TreeItem currentTreeItem = null;
            int index = 0;

            if ( parentTreeItem == null ) {
                parentTreeItem = selectedTreeItem;
                currentTreeItem = selectedTreeItem;
                index = parentTreeItem.getItemCount();
            } else {
                switch ( position ) {
                    case Before: currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem);     break;
                    case After:  currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem) + 1; break;
                    case End:    currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();                break;
                    case Into:   currentTreeItem = selectedTreeItem; index = selectedTreeItem.getItemCount();              break;
                    default:
                        // unknown position, let's default to "End"
                        currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();
                }
            }

            TreeItem newTreeItem = new TreeItem(currentTreeItem, SWT.NONE, index);

            Composite parentComposite = (Composite)currentTreeItem.getData("widget");
            if ( parentComposite instanceof Table ) {
                Table table = (Table)parentComposite;
                TableColumn column = null;
                switch (widgetClass) {
                    case "label":      column = FormJsonParser.createLabelColumn(null, table, newTreeItem, index, FormDialog.this.selectedObject); break;
                    case "image":      column = FormJsonParser.createImageColumn(null, table, newTreeItem, index, FormDialog.this.selectedObject); break;
                    case "text":       column = FormJsonParser.createTextColumn (null, table, newTreeItem, index, FormDialog.this.selectedObject); break;
                    case "richtext":   column = FormJsonParser.createRichTextColumn (null, table, newTreeItem, index, FormDialog.this.selectedObject); break;
                    case "combo":      column = FormJsonParser.createComboColumn(null, table, newTreeItem, index, FormDialog.this.selectedObject); break;
                    case "check":      column = FormJsonParser.createCheckColumn(null, table, newTreeItem, index, FormDialog.this.selectedObject); break;
                    default:
                        // unknown class
                }

                if( column != null ) {
                    column.setText("new "+widgetClass);
                    column.addListener(SWT.Selection, FormDialog.this.sortListener);
                }
            } else {
                switch (widgetClass) {
                    case "label":      FormJsonParser.createLabel(null, parentComposite, newTreeItem, FormDialog.this.selectedObject); break;
                    case "image":      FormJsonParser.createImage(null, parentComposite, newTreeItem, FormDialog.this.selectedObject); break;
                    case "text":       FormJsonParser.createText (null, parentComposite, newTreeItem, FormDialog.this.selectedObject); break;
                    case "richtext":   FormJsonParser.createRichText (null, parentComposite, newTreeItem, FormDialog.this.selectedObject); break;
                    case "combo":      FormJsonParser.createCombo(null, parentComposite, newTreeItem, FormDialog.this.selectedObject); break;
                    case "check":      FormJsonParser.createCheck(null, parentComposite, newTreeItem, FormDialog.this.selectedObject); break;
                    default:
                        // unknown class
                }
            }

            newTreeItem.setText("new "+widgetClass);
            FormJsonParser.setData(newTreeItem, "name", "new "+widgetClass);

            FormDialog.this.tree.setSelection(newTreeItem);
            FormDialog.this.tree.notifyListeners(SWT.Selection, new Event());        // shows up the control's properties
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    SelectionListener addLineListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Position position = (Position)((MenuItem)e.getSource()).getData("position");
            TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];
            TreeItem parentTreeItem = selectedTreeItem.getParentItem();
            TreeItem currentTreeItem = null;
            int index = 0;

            if ( parentTreeItem == null ) {
                parentTreeItem = selectedTreeItem;
                currentTreeItem = selectedTreeItem;
                index = parentTreeItem.getItemCount();
            } else {
                switch ( position ) {
                    case Before: currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem);     break;
                    case After:  currentTreeItem = parentTreeItem;   index = parentTreeItem.indexOf(selectedTreeItem) + 1; break;
                    case End:    currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();                break;
                    case Into:   currentTreeItem = selectedTreeItem; index = selectedTreeItem.getItemCount();              break;
                    default:
                        // unknown position, let default to "End"
                        currentTreeItem = parentTreeItem;   index = parentTreeItem.getItemCount();
                }
            }

            TreeItem newTreeItem = new TreeItem(currentTreeItem, SWT.NONE, index);
            newTreeItem.setText("new line");

            Composite parentComposite = (Composite)currentTreeItem.getData("widget");
            if ( parentComposite instanceof Table ) {
                Table table = (Table)parentComposite;

                FormJsonParser.createLine(null, table, newTreeItem, FormDialog.this.selectedObject);
            }

            FormJsonParser.setData(newTreeItem, "name", "new line");

            FormDialog.this.tree.setSelection(newTreeItem);
            FormDialog.this.tree.notifyListeners(SWT.Selection, new Event());        // shows up the control's properties
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };

    SelectionListener deleteListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            TreeItem selectedTreeItem = FormDialog.this.tree.getSelection()[0];


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


                if ( !widget.isDisposed() ) {
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
    static String popupMessage;

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
                    case Priority.FATAL_INT:
                    case Priority.ERROR_INT:
                        MessageDialog.openError(display.getActiveShell(), FormPlugin.pluginTitle, popupMessage);
                        break;
                    case Priority.WARN_INT:
                        MessageDialog.openWarning(display.getActiveShell(), FormPlugin.pluginTitle, popupMessage);
                        break;
                    default:
                        MessageDialog.openInformation(display.getActiveShell(), FormPlugin.pluginTitle, popupMessage);
                        break;
                }
            }
        });
    }

    static int questionResult;

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
    static Shell dialogShell = null;
    static Label dialogLabel = null;

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
        @Override
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

    static void updateWidget(Control control) {
        String unscoppedVariable = (String)control.getData("variable");
        EObject referedEObject = (EObject)control.getData("eObject");
        String content;

        switch ( control.getClass().getSimpleName() ) {
            case "Button":
                String[]values = (String[])control.getData("values");
                if ( values == null )
                    content = "";
                else
                    content = values[((Button)control).getSelection()?0:1];
                break;

            case "StyledText":
                content = ((StyledText)control).getText();

                Pattern pattern = (Pattern)((StyledText)control).getData("pattern");                    // if a regex has been provided, we change the text color to show if it matches
                if ( pattern != null ) {
                    ((StyledText)control).setStyleRange(new StyleRange(0, content.length(), pattern.matcher(content).matches() ? goodValueColor : badValueColor, null));
                }
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

                    default:
                        // unknown class
                }
            }
        }
    }

    Listener sortListener=new Listener() {
        @Override
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
                newTable.setToolTipText(oldTable.getToolTipText());
                newTable.setForeground(oldTable.getForeground());
                newTable.setBackground(oldTable.getBackground());
                newTable.setFont(oldTable.getFont());
                newTable.setData("excelSheet", oldTable.getData("excelSheet"));
                newTable.setData("excelFirstLine", oldTable.getData("excelFirstLine"));
                newTable.setData("excelLastLine", oldTable.getData("excelLastLine"));

                for(TableColumn oldTableColumn: oldTable.getColumns()) {
                    TableColumn newTableColumn = new TableColumn(newTable,SWT.NONE);
                    newTableColumn.setText(oldTableColumn.getText());
                    newTableColumn.setAlignment(oldTableColumn.getAlignment());
                    newTableColumn.setWidth(oldTableColumn.getWidth());
                    newTableColumn.setResizable(oldTableColumn.getResizable());
                    newTableColumn.setToolTipText(oldTable.getToolTipText());
                    newTableColumn.setData("class",oldTableColumn.getData("class"));
                    newTableColumn.setData("tooltip",oldTableColumn.getData("tooltip"));
                    newTableColumn.setData("values",oldTableColumn.getData("values"));
                    newTableColumn.setData("regexp",oldTableColumn.getData("regexp"));
                    newTableColumn.setData("excelColumn",oldTableColumn.getData("excelColumn"));
                    newTableColumn.setData("excelCellType",oldTableColumn.getData("excelCellType"));
                    newTableColumn.setData("excelDefault",oldTableColumn.getData("excelDefault"));
                    newTableColumn.setData("sortDirection",oldTableColumn.getData("sortDirection"));
                    newTableColumn.addListener(SWT.Selection,FormDialog.this.sortListener);
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
                                break;

                            default:
                                // unknown class
                        }
                        newEditors[column]=newEditor;
                    }
                    newTableItem.setData("editors",newEditors);
                }

                logger.debug("Replacing old table with new table");newTable.setVisible(true);oldTable.dispose();
                newTable.layout();
            }
        }
    };

    private class TableItemComparator implements Comparator<TableItem> {
        int columnIndex   = 0;
        int sortDirection = SWT.UP;

        public TableItemComparator(int columnIndex, int sortDirection) {
            this.columnIndex = columnIndex;
            this.sortDirection = sortDirection;
        }

        @Override
        public int compare(TableItem first, TableItem second) {
            TableEditor[] editorsFirst = (TableEditor[]) first.getData("editors");

            if (editorsFirst[this.columnIndex] != null) {
                TableEditor[] editorsSecond = (TableEditor[]) second.getData("editors");

                switch (editorsFirst[this.columnIndex].getEditor().getClass().getSimpleName()) {
                    case "StyledText":
                        logger.trace("comparing \"" + ((StyledText) editorsFirst[this.columnIndex].getEditor()).getText() + "\" and \"" + ((StyledText) editorsSecond[this.columnIndex].getEditor()).getText() + "\"");
                        return Collator.getInstance().compare(((StyledText) editorsFirst[this.columnIndex].getEditor()).getText(), ((StyledText) editorsSecond[this.columnIndex].getEditor()).getText()) * (this.sortDirection == SWT.UP ? 1 : -1);
                    case "Button":
                        logger.trace("comparing \"" + ((Button) editorsFirst[this.columnIndex].getEditor()).getSelection()
                                + "\" and \"" + ((Button) editorsSecond[this.columnIndex].getEditor()).getSelection()
                                + "\"");
                        return Collator.getInstance().compare(((Button) editorsFirst[this.columnIndex].getEditor()).getSelection(), ((Button) editorsSecond[this.columnIndex].getEditor()).getSelection())* (this.sortDirection == SWT.UP ? 1 : -1);

                    case "CCombo":
                        logger.trace("comparing \"" + ((CCombo) editorsFirst[this.columnIndex].getEditor()).getText() + "\" and \"" + ((CCombo) editorsSecond[this.columnIndex].getEditor()).getText() + "\"");
                        return Collator.getInstance().compare(((CCombo) editorsFirst[this.columnIndex].getEditor()).getText(), ((CCombo) editorsSecond[this.columnIndex].getEditor()).getText()) * (this.sortDirection == SWT.UP ? 1 : -1);

                    default:
                        throw new RuntimeException("Do not know how to compare elements of class " + editorsFirst[this.columnIndex].getClass().getSimpleName());
                }
            }

            return Collator.getInstance().compare(first.getText(this.columnIndex), second.getText(this.columnIndex)) * (this.sortDirection == SWT.UP ? 1 : -1);
        }
    }
}
