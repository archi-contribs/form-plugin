package org.archicontribs.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;

/**
 * Helper methods to parse JSON objects and create the corresponding SWT widgets
 * 
 * @author Herve Jouin
 *
 */
public class FormJsonParser {
	private static final FormLogger logger            = new FormLogger(FormDialog.class);
	
	protected static Display        display           = Display.getDefault();
	
    public static final Image FORM_ICON               = new Image(display, FormDialog.class.getResourceAsStream("/icons/form.png"));
    public static final Image TAB_ICON                = new Image(display, FormDialog.class.getResourceAsStream("/icons/tab.png"));
    public static final Image LABEL_ICON              = new Image(display, FormDialog.class.getResourceAsStream("/icons/label.png"));
    public static final Image TEXT_ICON               = new Image(display, FormDialog.class.getResourceAsStream("/icons/text.png"));
    public static final Image CHECK_ICON              = new Image(display, FormDialog.class.getResourceAsStream("/icons/check.png"));
    public static final Image COMBO_ICON              = new Image(display, FormDialog.class.getResourceAsStream("/icons/combo.png"));
    public static final Image TABLE_ICON              = new Image(display, FormDialog.class.getResourceAsStream("/icons/table.png"));
    public static final Image COLUMN_ICON             = new Image(display, FormDialog.class.getResourceAsStream("/icons/column.png"));
    public static final Image LINE_ICON               = new Image(display, FormDialog.class.getResourceAsStream("/icons/line.png"));
	
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
        	treeItem.setData("widget", form);
        
        String  name              = getName(jsonObject, form, treeItem);
        FormPosition.setFormName(name);
        
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
            logger.trace("      variableSeparator = " + variableSeparator);
            logger.trace("      width = " + width);
            logger.trace("      height = " + height);
            logger.trace("      spacing = " + spacing);
            logger.trace("      buttonWidth = " + buttonWidth);
            logger.trace("      buttonHeight = " + buttonHeight);
            logger.trace("      refers = " + refers);
            logger.trace("      buttonOk = " + buttonOkText);
            logger.trace("      buttonCancel = " + buttonCancelText);
            logger.trace("      buttonExport = " + buttonExportText);
            logger.trace("      whenEmpty = " + whenEmpty);
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
        	height = FormDialog.defaultDialogHeight;
        
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
        
        // we create the tab folder
        CTabFolder tabFolder = new CTabFolder(form, SWT.BORDER);
        tabFolder.setSimple(false);
        tabFolder.setBorderVisible(true);
        fd = new FormData();
        fd.top = new FormAttachment(0, spacing);
        fd.left = new FormAttachment(0, spacing);
        fd.right = new FormAttachment(100, -spacing);
        fd.bottom = new FormAttachment(okButton, -spacing);
        tabFolder.setLayoutData(fd);
        tabFolder.setForeground(form.getForeground());
        tabFolder.setBackground(form.getBackground());
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(FORM_ICON);
        	treeItem.setData("class", "form");
            treeItem.setData("widget", form);
            form.setData("treeItem", treeItem);
        }
        
        // used by form
        if ( variableSeparator == null )
        	variableSeparator = FormDialog.defaultVariableSeparator;
        form.setData("variable separator", variableSeparator);			// we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        form.setData("tab folder", tabFolder);
        form.setData("export button", exportToExcelButton);
        form.setData("ok button", okButton);
        form.setData("cancel button", cancelButton);
        form.setData("excel sheets", new HashSet<String>());
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
    public CTabItem createTab(JSONObject jsonObject, CTabFolder parent, TreeItem treeItem) throws RuntimeException, ClassCastException {
        // we create the tab item
        CTabItem tabItem = new CTabItem(parent, SWT.MULTI);
        Composite composite = new Composite(parent, SWT.NONE);
        tabItem.setControl(composite);
        composite.setData("tabItem", tabItem);
        
        // if it is the first tab to be created, then we select it
        if ( parent.getItemCount() == 1 )
        	parent.setSelection(tabItem);
        
        String  name = getName(jsonObject, composite, treeItem);
    	logger.debug("Creating tab : " + name);
    	FormPosition.setTabName(name);
        FormPosition.setControlClass("tab");
                
        getForegroundAndBackground(jsonObject, composite, treeItem);

    	// name
        if ( name != null )
        	tabItem.setText(name);						// may be replaced by FormVariable.expand(name, selectedObject) in calling method
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(TAB_ICON);
        	treeItem.setData("class", "tab");
            treeItem.setData("widget", composite);
            composite.setData("treeItem", treeItem);
        }

        return tabItem;
    }
    
    /**
     * Create a Label control
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public Label createLabel(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        Label label = new Label(parent, SWT.WRAP);
        
        String  name = getName(jsonObject, label, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getXY(jsonObject, label, treeItem);
        getForegroundAndBackground(jsonObject, label, treeItem);
        getText(jsonObject, label, treeItem, selectedObject);
        getTooltip(jsonObject, label, treeItem, selectedObject);
        getFont(jsonObject, label, treeItem);
        getAlignment(jsonObject, label, treeItem);
        getExcelCellOrColumn(jsonObject, label, treeItem);
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(LABEL_ICON);
        	treeItem.setData("class", "label");
            treeItem.setData("widget", label);
            label.setData("treeItem", treeItem);
        }

        return label;
    }
    
    /**
     * Create a Label column
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableColumn createLabelColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        TableColumn tableColumn;
        if ( index == null )
            index = parent.getColumnCount();

        tableColumn = new TableColumn(parent, SWT.NONE, index);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getWidth(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // we update the editors and cells if necessary
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == index ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[index] = editor;
                    
                    newCells[index] = "";
                    logger.trace("      adding label cell with value \"" + newCells[index] + "\"");
                    Label label = new Label(parent, SWT.WRAP | SWT.NONE);
                    label.setText(newCells[index]);
                    editor.setEditor(label, tableItem, index);
                    editor.grabHorizontal = true;
                             
                    ++newCol;
                }
                
                if ( oldCol < parent.getColumnCount()-1 ) {
                    newEditors[newCol] = oldEditors[oldCol];
                    newEditors[newCol].setEditor(newEditors[newCol].getEditor(), tableItem, newCol);
                    newCells[newCol] = oldCells[oldCol];
                }
                ++newCol;
            }
            tableItem.setData("editors", newEditors);
            tableItem.setData("cells", newCells);
            TreeItem lineTreeItem = (TreeItem)tableItem.getData("treeItem");
            if ( lineTreeItem != null )
                lineTreeItem.setData("cells", newCells);
        }
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(LABEL_ICON);
        	treeItem.setData("class", "labelColumn");
            treeItem.setData("widget", tableColumn);
            tableColumn.setData("treeItem", treeItem);
            tableColumn.setData("class", treeItem.getData("class"));
        }
        
        // used by form
        tableColumn.setData("class", "labelColumn");
        
        return tableColumn;
    }

    /**
     * Create a text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public StyledText createText(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        StyledText text = new StyledText(parent, SWT.WRAP | SWT.BORDER);
        
        String  name = getName(jsonObject, text, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getXY(jsonObject, text, treeItem);
        getVariable(jsonObject, text, treeItem, selectedObject);
        getRegexp(jsonObject, text, treeItem);
        getForegroundAndBackground(jsonObject, text, treeItem);
        getTooltip(jsonObject, text, treeItem, selectedObject);
        getFont(jsonObject, text, treeItem);
        getAlignment(jsonObject, text, treeItem);
        getExcelCellOrColumn(jsonObject, text, treeItem);
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(TEXT_ICON);
        	treeItem.setData("class", "text");
        	treeItem.setData("widget", text);
        	text.setData("treeItem", treeItem);
        }

        return text;
    }
    
    /**
     * Create a text column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableColumn createTextColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        TableColumn tableColumn;
        if ( index == null )
            index = parent.getColumnCount();

        tableColumn = new TableColumn(parent, SWT.NONE, index);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getWidth(jsonObject, tableColumn, treeItem);
        getRegexp(jsonObject, tableColumn, treeItem);
        getForegroundAndBackground(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // we update the editors and cells if necessary
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == index ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[index] = editor;
                    
                    newCells[index] = "${void}";
                    StyledText text = new StyledText(parent, SWT.WRAP | SWT.NONE);
                    logger.trace("      adding text cell with value \"" + newCells[index] + "\"");
                    text.setText(newCells[index]);
                    editor.setEditor(text, tableItem, index);
                    editor.grabHorizontal = true;
                             
                    ++newCol;
                }
                
                if ( oldCol < parent.getColumnCount()-1 ) {
                    newEditors[newCol] = oldEditors[oldCol];
                    newEditors[newCol].setEditor(newEditors[newCol].getEditor(), tableItem, newCol);
                    newCells[newCol] = oldCells[oldCol];
                }
                ++newCol;
            }
            tableItem.setData("editors", newEditors);
            tableItem.setData("cells", newCells);
            TreeItem lineTreeItem = (TreeItem)tableItem.getData("treeItem");
            if ( lineTreeItem != null )
                lineTreeItem.setData("cells", newCells);
        }
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(TEXT_ICON);
        	treeItem.setData("class", "textColumn");
            treeItem.setData("widget", tableColumn);
            tableColumn.setData("treeItem", treeItem);
            tableColumn.setData("class", treeItem.getData("class"));
        }
        
        // used by form
        tableColumn.setData("class", "textColumn");

        return tableColumn;
    }

    /**
     * Create a Combo control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public CCombo createCombo(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo control");
        
        // we create the combo
    	CCombo combo = new CCombo(parent, SWT.NONE);
        
        String  name = getName(jsonObject, combo, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");

        getVariable(jsonObject, combo, treeItem, selectedObject);
        getValues(jsonObject, combo, treeItem);
        getXY(jsonObject, combo, treeItem);
        getForegroundAndBackground(jsonObject, combo, treeItem);
        getTooltip(jsonObject, combo, treeItem, selectedObject);
        getFont(jsonObject, combo, treeItem);
        getExcelCellOrColumn(jsonObject, combo, treeItem);
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(COMBO_ICON);
        	treeItem.setData("class", "combo");
            treeItem.setData("widget", combo);
            combo.setData("treeItem", treeItem);
        }

        return combo;
    }
    
    /**
     * Create a Combo Column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public TableColumn createComboColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo column");
        
        // we create the combo
        TableColumn tableColumn;
        if ( index == null )
            index = parent.getColumnCount();

        tableColumn = new TableColumn(parent, SWT.NONE, index);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");
        
        getValues(jsonObject, tableColumn, treeItem);
        getWidth(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // we update the editors and cells if necessary
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == index ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[index] = editor;
                    
                    newCells[index] = "${void}";
                    CCombo combo = new CCombo(parent, SWT.NONE);
                    logger.trace("      adding combo cell with value \"" + newCells[index] + "\"");
                    combo.setText(newCells[index]);
                    String[] values = (String[])parent.getColumn(index).getData("values");
                    if ( values != null ) combo.setItems(values);
                    editor.setEditor(combo, tableItem, index);
                    editor.grabHorizontal = true;
                             
                    ++newCol;
                }
                
                if ( oldCol < parent.getColumnCount()-1 ) {
                    newEditors[newCol] = oldEditors[oldCol];
                    newEditors[newCol].setEditor(newEditors[newCol].getEditor(), tableItem, newCol);
                    newCells[newCol] = oldCells[oldCol];
                }
                ++newCol;
            }
            tableItem.setData("editors", newEditors);
            tableItem.setData("cells", newCells);
            TreeItem lineTreeItem = (TreeItem)tableItem.getData("treeItem");
            if ( lineTreeItem != null )
                lineTreeItem.setData("cells", newCells);
        }
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(COMBO_ICON);
        	treeItem.setData("class", "comboColumn");
            treeItem.setData("widget", tableColumn);
            tableColumn.setData("treeItem", treeItem);
            tableColumn.setData("class", treeItem.getData("class"));
        }
        
        // used by graphical editor
        // used by form
        tableColumn.setData("class", "comboColumn");
        
        return tableColumn;
    }
    
    /**
     * Create a check button control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public Button createCheck(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check control");
        
        // we create the combo
    	Button check = new Button(parent, SWT.CHECK);
        
        String  name = getName(jsonObject, check, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");

 	   	getValues(jsonObject, check, treeItem);
 	   	getVariable(jsonObject, check, treeItem, selectedObject);
   		getXY(jsonObject, check, treeItem);
   		getForegroundAndBackground(jsonObject, check, treeItem);
   		getAlignment(jsonObject, check, treeItem);
 	   	getTooltip(jsonObject, check, treeItem, selectedObject);
 	   	getExcelCellOrColumn(jsonObject, check, treeItem);
 	   	
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(CHECK_ICON);
            treeItem.setData("class", "check");
            treeItem.setData("widget", check);
            check.setData("treeItem", treeItem);
        }
            
 	   	return check;
    }
    
    /**
     * Create a check button column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
	public TableColumn createCheckColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check column");
        
        // we create the check button
        TableColumn tableColumn;
        if ( index == null )
            index = parent.getColumnCount();

       	tableColumn = new TableColumn(parent, SWT.NONE, index);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");
        
 	   	getValues(jsonObject, tableColumn, treeItem);
   		getWidth(jsonObject, tableColumn, treeItem);
   		getAlignment(jsonObject, tableColumn, treeItem);
 	   	getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
 	   	getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
 	   	
        // we update the editors and cells if necessary
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == index ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[index] = editor;
                    
                    newCells[index] = "${void}";
                    Button check = new Button(parent, SWT.CHECK);
                    check.pack();
                    logger.trace("      adding check cell with value \"" + newCells[index] + "\"");
                    editor.minimumWidth = check.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    editor.setEditor(check, tableItem, index);
                             
                    ++newCol;
                }
                
                if ( oldCol < parent.getColumnCount()-1 ) {
                    newEditors[newCol] = oldEditors[oldCol];
                    newEditors[newCol].setEditor(newEditors[newCol].getEditor(), tableItem, newCol);
                    newCells[newCol] = oldCells[oldCol];
                }
                ++newCol;
            }
            tableItem.setData("editors", newEditors);
            tableItem.setData("cells", newCells);
            TreeItem lineTreeItem = (TreeItem)tableItem.getData("treeItem");
            if ( lineTreeItem != null )
                lineTreeItem.setData("cells", newCells);
        }
 	   	
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(CHECK_ICON);
        	treeItem.setData("class", "checkColumn");
            treeItem.setData("widget", tableColumn);
            tableColumn.setData("treeItem", treeItem);
            tableColumn.setData("class", treeItem.getData("class"));
        }
        
        // used by form
        tableColumn.setData("class", "checkColumn");
 	   	
 	   	return tableColumn;
    }
	
    /**
     * Create a table control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public Table createTable(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table control");
    	
        Table table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        String name = getName(jsonObject, table, treeItem);
        FormPosition.setControlName(name);
        FormPosition.setControlClass("table");

        getXY(jsonObject, table, treeItem);
        getForegroundAndBackground(jsonObject, table, treeItem);
        getTooltip(jsonObject, table, treeItem, selectedObject);
        getExcelLines(jsonObject, table, treeItem);
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(TABLE_ICON);
        	treeItem.setData("class", "table");
        	treeItem.setData("widget", table);
        	table.setData("treeItem", treeItem);
        }

        return table;
    }
    
    /**
     * Create a table line<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public TableItem createLine(JSONObject jsonObject, Table parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table item");
    	
    	TableItem tableItem = null;
    	boolean mustCreateLine = true;
    	Boolean generate = getBoolean(jsonObject, "generate");
    	
    	// if the selectedObject is provided AND if the lines are generated
    	//    then we check the selectedObject against the filter
    	if ( selectedObject != null && generate != null && generate )
    		mustCreateLine = checkFilter(selectedObject, getJSONObject(jsonObject, "filter"));
    	
    	if ( mustCreateLine ) {
	        tableItem = new TableItem(parent, SWT.NONE);
	        
	        String name = getName(jsonObject, tableItem, treeItem);
	        FormPosition.setControlName(name);
	        FormPosition.setControlClass("lines");
	        
	        getCells(jsonObject, tableItem, treeItem, selectedObject);
	        getGenerate(jsonObject, tableItem, treeItem);
	        getFilter(jsonObject, tableItem, treeItem);
	        
	        // used by graphical editor
	        if ( treeItem != null ) {
	            treeItem.setImage(LINE_ICON);
	        	treeItem.setData("class", "line");
	        	treeItem.setData("widget", tableItem);
	            tableItem.setData("treeItem", treeItem);
	        }
    	}
		
    	// if the selected object is a container and if the lines are generated
    	//    then we create a line for every object's child
		if ( generate != null && generate ) {
			if (selectedObject instanceof IArchimateDiagramModel) {
				for ( IDiagramModelObject child: ((IArchimateDiagramModel) selectedObject).getChildren()) {
					tableItem = createLine(jsonObject, parent, treeItem, child);
					for ( IDiagramModelConnection relation: child.getSourceConnections() ) {
						tableItem = createLine(jsonObject, parent, treeItem, relation);
					}
				}
			} else if (selectedObject instanceof IDiagramModelContainer) {
            	for ( IDiagramModelObject child: ((IDiagramModelContainer) selectedObject).getChildren()) {
            		tableItem = createLine(jsonObject, parent, treeItem, child);
					for ( IDiagramModelConnection relation: child.getSourceConnections() ) {
						tableItem = createLine(jsonObject, parent, treeItem, relation);
					}
            	}
            } else if (selectedObject instanceof IFolder) {
            	for ( EObject child: ((IFolder) selectedObject).getElements()) {
            		tableItem = createLine(jsonObject, parent, treeItem, child);
            	}
            } else if (selectedObject instanceof IArchimateModel) {
            	for (IFolder folder : ((IArchimateModel) selectedObject).getFolders()) {
            		for ( EObject child: folder.getElements()) {
            			tableItem = createLine(jsonObject, parent, treeItem, child);
            		}
            	}
            }
 		}

        return tableItem;
    }
    
    /***************************************************************/
    /**
     * Checks whether the eObject fits in the filter rules
     */
    public boolean checkFilter(EObject eObject, JSONObject filterObject) {
        if (filterObject == null) {
            return true;
        }

        String type = getString(filterObject, "genre");
        if ( FormPlugin.isEmpty(type) )
        	type = "AND";
        else
        	type = type.toUpperCase();

        if (!type.equals("AND") && !type.equals("OR"))
            throw new RuntimeException("Invalid filter genre. Supported genres are \"AND\" and \"OR\".");

        boolean result;

        @SuppressWarnings("unchecked")
        Iterator<JSONObject> filterIterator = getJSONArray(filterObject, "tests").iterator();
        while (filterIterator.hasNext()) {
            JSONObject filter = filterIterator.next();
            String attribute = getString(filter, "attribute");
            String operation = getString(filter, "operation");
            String value;
            String[] values;

            String attributeValue = FormVariable.expand(attribute, eObject);

            switch (operation.toLowerCase()) {
                case "equals":
                    value = getString(filter, "value");

                    result = attributeValue.equals(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") equals \"" + value + "\" --> " + result);
                    break;

                case "in":
                    value = getString(filter, "value");
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
                    value = getString(filter, "value");

                    result = attributeValue.equalsIgnoreCase(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") equals (ignore case) \"" + value + "\" --> " + result);
                    break;

                case "iin":
                    value = getString(filter, "value");
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
                    value = (String)getJSON(filter, "value");

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
    
    private Boolean getGenerate(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem) {
    	Boolean generate = getBoolean(jsonObject, "generate");
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "generate", generate);
    	}
    	
    	// required by the form
    	if ( tableItem != null ) {
    		tableItem.setData("generate", generate);
    	}
    	
    	return generate;
    }
    
    private void getCells(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem, EObject selectedObject) {
    	Table table = tableItem.getParent();
    	JSONArray jsonCells = getJSONArray(jsonObject, "cells");
    	String[] cells = null;
    	int nbJsonCells = 0;
    	
    	if ( jsonCells != null )
    	    nbJsonCells = jsonCells.size();

    	cells = new String[table.getColumnCount()];
    	TableEditor[] editors = new TableEditor[table.getColumnCount()];
    	
    	// we get the cells variables, completing if some are missing and ignoring if too many are present
    	for ( int columnNumber = 0; columnNumber < table.getColumnCount(); ++columnNumber ) {
    		// for each cell, we create the corresponding table editor
    		TableEditor editor= new TableEditor(table);
            editors[columnNumber] = editor;
            String[] values;
            
    		switch ( (String)table.getColumn(columnNumber).getData("class") ) {
                case "labelColumn":
                    if ( columnNumber < nbJsonCells )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "label";
                    logger.trace("      adding label cell with value \"" + cells[columnNumber] + "\"");
                    Label label = new Label(table, SWT.WRAP | SWT.NONE);
                    if ( selectedObject == null )
                    	label.setText(cells[columnNumber]);
                    else
                    	label.setText(FormVariable.expand(cells[columnNumber], selectedObject));
                    editor.setEditor(label, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "textColumn":
                    if ( columnNumber < nbJsonCells )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "${void}";
                    StyledText text = new StyledText(table, SWT.WRAP | SWT.NONE);
                    logger.trace("      adding text cell with value \"" + cells[columnNumber] + "\"");
                    if ( selectedObject == null )
                    	text.setText(cells[columnNumber]);
                    else {
                    	text.setText(FormVariable.expand(cells[columnNumber], selectedObject));
                    	
                    	EObject referedEObject;
    	                String unscoppedVariable;
                    	referedEObject = FormVariable.getReferedEObject(cells[columnNumber], selectedObject);
                    	unscoppedVariable = FormVariable.getUnscoppedVariable(cells[columnNumber], selectedObject);
                    	text.setData("variable", unscoppedVariable);
                    	text.setData("eObject", referedEObject);
                    	FormDialog.formVarList.set(referedEObject, unscoppedVariable, text);
                    	
                    	text.addModifyListener(FormDialog.textModifyListener);
                    }
                    editor.setEditor(text, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "comboColumn":
                    if ( columnNumber < nbJsonCells )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "${void}";
                    CCombo combo = new CCombo(table, SWT.NONE);
                    logger.trace("      adding combo cell with value \"" + cells[columnNumber] + "\"");
                    if ( selectedObject == null )
                    	combo.setText(cells[columnNumber]);
                    else {
                    	combo.setText(FormVariable.expand(cells[columnNumber], selectedObject));
                    	
                    	EObject referedEObject;
    	                String unscoppedVariable;
                    	referedEObject = FormVariable.getReferedEObject(cells[columnNumber], selectedObject);
                    	unscoppedVariable = FormVariable.getUnscoppedVariable(cells[columnNumber], selectedObject);
                    	combo.setData("variable", unscoppedVariable);
                    	combo.setData("eObject", referedEObject);
                    	FormDialog.formVarList.set(referedEObject, unscoppedVariable, combo);
                    	
                    	combo.addModifyListener(FormDialog.textModifyListener);
                    }
                    values = (String[])table.getColumn(columnNumber).getData("values");
                    if ( selectedObject != null && values != null ) {
                		int nbValues = ((String[])table.getColumn(columnNumber).getData("values")).length; 
                		String[] newValues = new String[nbValues];
                		for ( int i=0; i<nbValues; ++i) {
                			newValues[i] = FormVariable.expand(values[i], selectedObject);
                		}
                		values = newValues;
                    }
                    if ( values != null ) combo.setItems(values);
                    editor.setEditor(combo, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "checkColumn":
                    if ( columnNumber < nbJsonCells )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "${void}";
                    Button check = new Button(table, SWT.CHECK);
                    check.pack();
                    logger.trace("      adding check cell with value \"" + cells[columnNumber] + "\"");
                    values = (String[])table.getColumn(columnNumber).getData("values");
                    if ( selectedObject != null && values != null ) {
                		int nbValues = ((String[])table.getColumn(columnNumber).getData("values")).length; 
                		String[] newValues = new String[nbValues];
                		for ( int i=0; i<nbValues; ++i) {
                			newValues[i] = FormVariable.expand(values[i], selectedObject);
                		}
                		values = newValues;
                    }
                    String trueValue = (values == null) ? "true" : values[0];
                    if ( selectedObject == null )
                    	check.setSelection(FormPlugin.areEqual(cells[columnNumber], trueValue));
                    else {
                    	check.setSelection(FormPlugin.areEqual(FormVariable.expand(cells[columnNumber], selectedObject), trueValue));
                    	
                    	EObject referedEObject;
    	                String unscoppedVariable;
                    	referedEObject = FormVariable.getReferedEObject(cells[columnNumber], selectedObject);
                    	unscoppedVariable = FormVariable.getUnscoppedVariable(cells[columnNumber], selectedObject);
                    	check.setData("variable", unscoppedVariable);
                    	check.setData("eObject", referedEObject);
                    	FormDialog.formVarList.set(referedEObject, unscoppedVariable, check);
                    	
                    	check.addSelectionListener(FormDialog.checkButtonSelectionListener);
                    }
                    editor.minimumWidth = check.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    editor.setEditor(check, tableItem, columnNumber);
                    break;
                    
                default:
                    throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to add table item for unknown object class \"" + ((String)table.getColumn(columnNumber).getData("class")) + "\"");
    		}
	    	tableItem.setData("editors", editors);
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
    
    private void getForegroundAndBackground(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
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
    	if ( widget != null && widget instanceof Control ) {
			FormPlugin.setColor((Control)widget, foreground, SWT.FOREGROUND);
			FormPlugin.setColor((Control)widget, background, SWT.BACKGROUND);
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
    
    private void getRegexp(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String regexp = getString(jsonObject, "regexp");
    	
    	if ( logger.isTraceEnabled() ) {
    		logger.trace("      regexp = " + regexp);
    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "regexp", regexp);
    	}
    	
    	// required by the form
    	if ( widget != null ) {
    		widget.setData("regexp", regexp);
    	}
    }
    
    private void getVariable(JSONObject jsonObject, Widget widget, TreeItem treeItem, EObject selectedObject) {
    	String  variable      = getString(jsonObject, "variable");
    	String  defaultText   = getString(jsonObject, "default");
    	Boolean forceDefault  = getBoolean(jsonObject, "forceDefault");
    	String  whenEmpty     = getString(jsonObject, "whenEmpty");
        Boolean editable      = getBoolean(jsonObject, "editable");
        
		if ( logger.isTraceEnabled() ) {
			logger.trace("      variable = "	  + variable);
			logger.trace("      default = " 	  + defaultText);
			logger.trace("      forceDefault = "  + forceDefault);
			logger.trace("      whenEmpty = "     + whenEmpty);
			if ( widget != null && (widget instanceof StyledText || widget instanceof CCombo) )
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
	    	if ( widget != null && (widget instanceof StyledText || widget instanceof CCombo) )
	    		setData(treeItem, "editable",  editable);
    	}

    	// required by the form
		if ( widget != null ) {
			widget.setData("variable", variable);
			widget.setData("default", defaultText);
			widget.setData("forceDefault", forceDefault);
			widget.setData("whenEmpty",    whenEmpty);
			
	        // editable
	        if ( widget instanceof StyledText && editable != null )
	        	((StyledText)widget).setEditable(editable);
	        if ( widget instanceof CCombo && editable != null )
	        	((CCombo)widget).setEditable(editable);
	        
            // if the selectedObject is specified, we replace the variable by it's expanded value
	        String variableValue = variable;
	        if ( selectedObject != null ) {
	        	variableValue = FormVariable.expand(variable, selectedObject);
            	if ( FormPlugin.isEmpty(variableValue) || (forceDefault!=null && forceDefault) )
            		variableValue = FormVariable.expand(defaultText, selectedObject);
	        }
			
			// we set a default text content for the graphical editor. Real form will replace this text with the variable content.
			if ( variable != null ) {
				switch ( widget.getClass().getSimpleName() ) {
					case "StyledText": ((StyledText)widget).setText(variableValue); break;
					case "CCombo":     ((CCombo)widget).setText(variableValue); break;
					case "Button":     ((Button)widget).setText(variableValue); break;
				}
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
        	if ( widget instanceof TableColumn ) {
        		// when TableColumn (excelSheet is referenced in the Table)
        		widget.setData("excelColumn",    excelColumn);
        	} else {														// when Text, CCombo or Button
        		widget.setData("excelSheet",     excelSheet);
        		widget.setData("excelCell",      excelCell);
        		
        		if ( excelSheet != null ) {
        			@SuppressWarnings("unchecked")
        			HashSet<String> excelSheets = (HashSet<String>)((Control)widget).getShell().getData("excel sheets");
        			excelSheets.add(excelSheet);
        		}
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
		if ( widget != null ) {
			widget.setData("values", values);
		}
		
		// we set the combo items
		if ( widget != null && FormPlugin.areEqual(widget.getClass().getSimpleName(), "CCombo") && values != null) {
       		((CCombo)widget).setItems(values);
		}
    }
    
    private void getTooltip(JSONObject jsonObject, Widget widget, TreeItem treeItem, EObject selectedObject) {
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
        	if ( selectedObject != null )
        		tooltip = FormVariable.expand(tooltip, selectedObject);
        	
	        if (  widget instanceof Control ) {
	        	((Control)widget).setToolTipText(tooltip);
	        } else if ( widget instanceof TableColumn ) {
	        	((TableColumn)widget).setToolTipText(tooltip);
	        }
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

	private void getText(JSONObject jsonObject, Label label, TreeItem treeItem, EObject selectedObject) {
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
	    	if ( selectedObject == null )
	    		label.setText(text);
	    	else
	    		label.setText(FormVariable.expand(text, selectedObject));
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
    
    public void setData(TreeItem treeItem, String key, Object value) {
    	if ( treeItem == null || key == null)
    		return;
    	
        addKey(treeItem, key);        
        treeItem.setData(key, value);
    }
    
    public void addKey(TreeItem treeItem, String key) {
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
	public JSONObject generateJson(Tree tree) throws RuntimeException {
    	JSONObject json = new JSONObject();
    	
    	json.put("version", 3);
    	json.put("org.archicontribs.form", generateJson(tree.getItem(0)));
    	
    	return json;
    }
    	
    
    @SuppressWarnings("unchecked")
	private JSONObject generateJson(TreeItem treeItem) throws RuntimeException {
    	JSONObject json = new JSONObject();
    	JSONObject filter = null;
    	
    	for ( String key: (Set<String>)treeItem.getData("editable keys") ) {
    		if ( treeItem.getData(key) != null ) {
    			switch ( key ) {
    				case "cells":
    				case "values":
						JSONArray array = new JSONArray();
						for ( String item: (String[])treeItem.getData(key))
							array.add(item);
						json.put(key, array);
    					break;
    					
    				case "tests":
    				case "genre":
    					if ( filter == null ) filter = new JSONObject();
    					filter.put(key, treeItem.getData(key));
    					break;
    					
    				default:
    					Object value = treeItem.getData(key);
    					if ( value != null ) {
    						json.put(key, value);
    					}
    						
    			}
    		}
    	}
    	
    	String clazz = (String)treeItem.getData("class");
    	if ( clazz != null ) {
    		if ( clazz.endsWith("Column") )
    			json.put("class", clazz.substring(0, clazz.length()-6));
    		else if ( !clazz.equals("line") && !clazz.equals("column") && !clazz.equals("form") && !clazz.equals("tab") )
        		json.put("class", clazz);
    	}
    	
    	if ( filter != null ) {
    		json.put("filter", filter);
    		filter = null;
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
