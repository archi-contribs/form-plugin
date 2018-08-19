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
import org.eclipse.swt.graphics.Color;
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
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.INameable;

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
    public static final Image IMAGE_ICON              = new Image(display, FormDialog.class.getResourceAsStream("/icons/image.png"));
    public static final Image TEXT_ICON               = new Image(display, FormDialog.class.getResourceAsStream("/icons/text.png"));
    public static final Image RICHTEXT_ICON           = new Image(display, FormDialog.class.getResourceAsStream("/icons/richtext.png"));
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
    public static void createForm(JSONObject jsonObject, Shell form, TreeItem treeItem) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating form");
        
        if ( treeItem != null )
        	treeItem.setData("widget", form);
        
        String  name              = getName(jsonObject, form, treeItem);
        FormPosition.setFormName(name);
        
        Integer width             = getInt(jsonObject, "width", FormDialog.defaultDialogWidth, false);
        Integer height            = getInt(jsonObject, "height", FormDialog.defaultDialogHeight, false);
        Integer spacing           = getInt(jsonObject, "spacing", FormDialog.defaultDialogSpacing, false);
        Integer buttonHeight      = getInt(jsonObject, "buttonHeight", FormDialog.defaultButtonHeight, false);
        String  refers            = getString(jsonObject, "refers", FormDialog.validRefers[0]).toLowerCase();
        String  variableSeparator = getString(jsonObject, "variableSeparator", FormDialog.defaultVariableSeparator);
        String  whenEmpty         = getString(jsonObject, "whenEmpty", FormDialog.validWhenEmpty[0]).toLowerCase();
        
        getForegroundAndBackground(jsonObject, form, treeItem);
        getFilter(jsonObject, form, treeItem);
        
        String  buttonOkText      = getString(jsonObject, "buttonOk", FormDialog.defaultButtonOkText);
        Integer buttonOkWidth     = getInt(jsonObject, "buttonOkWidth", FormDialog.defaultButtonWidth, false);
        String  buttonCancelText  = getString(jsonObject, "buttonCancel", FormDialog.defaultButtonCancelText);
        Integer buttonCancelWidth = getInt(jsonObject, "buttonCancelWidth", FormDialog.defaultButtonWidth, false);
        String  buttonExportText  = getString(jsonObject, "buttonExport", FormDialog.defaultButtonExportText);
        Integer buttonExportWidth = getInt(jsonObject, "buttonExportWidth", FormDialog.defaultButtonWidth, false);
        
        // we register the values from the configuration file that are needed by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "width",             width);
        	setData(treeItem, "height",            height);
        	setData(treeItem, "spacing",           spacing);
        	setData(treeItem, "buttonHeight",      buttonHeight);
        	setData(treeItem, "refers",            refers );
        	setData(treeItem, "variableSeparator", variableSeparator);
        	setData(treeItem, "whenEmpty",         whenEmpty);
        	setData(treeItem, "buttonOk",          buttonOkText);
        	setData(treeItem, "buttonOkWidth",     buttonOkWidth);
        	setData(treeItem, "buttonCancel",      buttonCancelText);
        	setData(treeItem, "buttonCancelWidth", buttonCancelWidth);
        	setData(treeItem, "buttonExport",      buttonExportText);
        	setData(treeItem, "buttonExportWidth", buttonExportWidth);
        	setData(treeItem, "whenEmpty",         whenEmpty);
        }

        // resizing the shell
        form.setBounds((display.getPrimaryMonitor().getBounds().width-width)/2, (display.getPrimaryMonitor().getBounds().height-height)/2, width, height);
        form.setLayout(new FormLayout());
        
        // name
        if ( name != null )
			form.setText(name);			// may be replaced by FormVariable.expand(name, selectedObject) in calling method
        
        // creating the buttons
        Button cancelButton = new Button(form, SWT.NONE);
        cancelButton.setText(FormDialog.defaultButtonCancelText);
        FormData fd = new FormData();
        fd.top = new FormAttachment(100, -(buttonHeight+spacing));
        fd.left = new FormAttachment(100, -(buttonCancelWidth+spacing));
        fd.right = new FormAttachment(100, -spacing);
        fd.bottom = new FormAttachment(100, -spacing);
        cancelButton.setLayoutData(fd);
        
        Button okButton = new Button(form, SWT.NONE);
        okButton.setText(FormDialog.defaultButtonOkText);
        fd = new FormData();
        fd.top = new FormAttachment(100, -(buttonHeight+spacing));
        fd.left = new FormAttachment(cancelButton, -(buttonOkWidth+spacing), SWT.LEFT);
        fd.right = new FormAttachment(cancelButton, -spacing);
        fd.bottom = new FormAttachment(100, -spacing);
        okButton.setLayoutData(fd);
        
        Button exportToExcelButton = new Button(form, SWT.NONE);
        exportToExcelButton.setText(FormDialog.defaultButtonExportText);
        fd = new FormData();
        fd.top = new FormAttachment(100, -(buttonHeight+spacing));
        fd.left = new FormAttachment(okButton, -(buttonExportWidth+spacing), SWT.LEFT);
        fd.right = new FormAttachment(okButton, -spacing);
        fd.bottom = new FormAttachment(100, -spacing);
        exportToExcelButton.setLayoutData(fd);
        
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
            treeItem.setData("export button", exportToExcelButton);
            treeItem.setData("ok button", okButton);
            treeItem.setData("cancel button", cancelButton);
            form.setData("treeItem", treeItem);
        }
        
        // used by form
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
    public static CTabItem createTab(JSONObject jsonObject, CTabFolder parent, TreeItem treeItem) throws RuntimeException, ClassCastException {
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
    public static Label createLabel(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        Label label = new Label(parent, SWT.WRAP);
        
        String  name = getName(jsonObject, label, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getForegroundAndBackground(jsonObject, label, treeItem);
        getText(jsonObject, label, treeItem, selectedObject);
        getXY(jsonObject, label, treeItem);
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
    public static TableColumn createLabelColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        // we create the label
        int tableIndex = index==null ? parent.getColumnCount() : index;
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE, tableIndex);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        getTableColumnWidth(jsonObject, tableColumn, treeItem);
        getForegroundAndBackground(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // If the table already contains items, then we need to update the editors and cells
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == tableIndex ) {
                	TableEditor editor= new TableEditor(parent);
                    newEditors[tableIndex] = editor;
                    
                    newCells[tableIndex] = "";
                    logger.trace("      adding label cell with value \"" + newCells[tableIndex] + "\"");
                    Label label = new Label(parent, SWT.WRAP | SWT.NONE);
                    label.setText(newCells[tableIndex]);
                    if ( tableColumn.getData("background color") != null )
                    	label.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	label.setBackground(parent.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	label.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	label.setForeground(parent.getForeground());
                    editor.setEditor(label, tableItem, tableIndex);
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
                setData(lineTreeItem, "cells", newCells);
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
     * Create a Label control with an image inside
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public static Label createImage(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating image control");
        
        // we create the label
        Label label = new Label(parent, SWT.WRAP);
        
        String  name = getName(jsonObject, label, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("image");
        
        getForegroundAndBackground(jsonObject, label, treeItem);
        getXY(jsonObject, label, treeItem);		// must be called before and after to scale the label and the image
        getImage(jsonObject, label, treeItem, selectedObject);
        getXY(jsonObject, label, treeItem);
        getTooltip(jsonObject, label, treeItem, selectedObject);
        getAlignment(jsonObject, label, treeItem);
        getExcelCellOrColumn(jsonObject, label, treeItem);
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(IMAGE_ICON);
            treeItem.setData("class", "image");
            treeItem.setData("widget", label);
            label.setData("treeItem", treeItem);
        }

        return label;
    }
    
    /**
     * Create a Label column with an image inside
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public static TableColumn createImageColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating image control");
        
        // we create the label
        int tableIndex = index==null ? parent.getColumnCount() : index; 
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE, tableIndex);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("image");
        
        getTableColumnWidth(jsonObject, tableColumn, treeItem);
        getForegroundAndBackground(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // If the table already contains items, then we need to update the editors and cells
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == tableIndex ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[tableIndex] = editor;
                    
                    newCells[tableIndex] = "image.jpg";
                    logger.trace("      adding image cell with value \"" + newCells[tableIndex] + "\"");
                    Label label = new Label(parent, SWT.WRAP | SWT.NONE);
                    label.setText(newCells[tableIndex]);
                    if ( tableColumn.getData("background color") != null )
                        label.setBackground((Color)tableColumn.getData("background color"));
                    else
                        label.setBackground(parent.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                        label.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                        label.setForeground(parent.getForeground());
                    editor.setEditor(label, tableItem, tableIndex);
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
                setData(lineTreeItem, "cells", newCells);
        }
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(IMAGE_ICON);
            treeItem.setData("class", "imageColumn");
            treeItem.setData("widget", tableColumn);
            tableColumn.setData("treeItem", treeItem);
            tableColumn.setData("class", treeItem.getData("class"));
        }
        
        // used by form
        tableColumn.setData("class", "imageColumn");
        
        return tableColumn;
    }

    /**
     * Create a text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public static StyledText createText(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text control");
        
        // we create the text
        StyledText text = new StyledText(parent, SWT.WRAP | SWT.BORDER);
        
        String  name = getName(jsonObject, text, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getVariable(jsonObject, text, treeItem, selectedObject);
        getXY(jsonObject, text, treeItem);
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
     * Create a rich text control<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public static FormRichTextEditor createRichText(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating rich text control");
        
        // we create the rich text
        FormRichTextEditor richText = new FormRichTextEditor(parent, SWT.NONE);
        richText.getEditorConfiguration().setToolbarCollapsible(true);
        richText.getEditorConfiguration().setToolbarInitialExpanded(false);
                
        String  name = getName(jsonObject, richText, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("richtext");

        getVariable(jsonObject, richText, treeItem, selectedObject);
        getXY(jsonObject, richText, treeItem);
        getRegexp(jsonObject, richText, treeItem);
        getForegroundAndBackground(jsonObject, richText, treeItem);
        getTooltip(jsonObject, richText, treeItem, selectedObject);
        getFont(jsonObject, richText, treeItem);
        getExcelCellOrColumn(jsonObject, richText, treeItem);
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(RICHTEXT_ICON);
            treeItem.setData("class", "richtext");
            treeItem.setData("widget", richText);
            richText.setData("treeItem", treeItem);
        }

        return richText;
    }
    
    /**
     * Create a text column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public static TableColumn createTextColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating text column");
        
        // we create the text
        int tableIndex = index==null ? parent.getColumnCount() : index;
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE, tableIndex);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("text");

        getTableColumnWidth(jsonObject, tableColumn, treeItem);
        getDefault(jsonObject, tableColumn, treeItem);
        getRegexp(jsonObject, tableColumn, treeItem);
        getForegroundAndBackground(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getAlignment(jsonObject, tableColumn, treeItem);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // If the table already contains items, then we need to update the editors and cells
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == tableIndex ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[tableIndex] = editor;
                    
                    newCells[tableIndex] = "${void}";
                    StyledText text = new StyledText(parent, SWT.WRAP | SWT.NONE);
                    logger.trace("      adding text cell with value \"" + newCells[tableIndex] + "\"");
                    text.setText(newCells[tableIndex]);
                    editor.setEditor(text, tableItem, tableIndex);
                    if ( tableColumn.getData("background color") != null )
                    	text.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	text.setBackground(parent.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	text.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	text.setForeground(parent.getForeground());
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
                setData(lineTreeItem, "cells", newCells);
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
     * Create a rich text column<br>
     * <br>
     * @param jsonObject the JSON object to parse
     * @param parent     the composite where the control will be created
     */
    public static TableColumn createRichTextColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
        if (logger.isDebugEnabled()) logger.debug("Creating rich text column");
        
        // we create the text
        TableColumn tableColumn;
        int tableIndex = index==null ? parent.getColumnCount() : index;

        tableColumn = new TableColumn(parent, SWT.NONE, tableIndex);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("richtext");

        getTableColumnWidth(jsonObject, tableColumn, treeItem);
        getDefault(jsonObject, tableColumn, treeItem);
        getRegexp(jsonObject, tableColumn, treeItem);
        getForegroundAndBackground(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // If the table already contains items, then we need to update the editors and cells
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == tableIndex ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[tableIndex] = editor;
                    
                    newCells[tableIndex] = "${void}";
                    FormRichTextEditor richText = new FormRichTextEditor(parent, SWT.WRAP | SWT.NONE);
                    richText.getEditorConfiguration().setToolbarCollapsible(true);
                    richText.getEditorConfiguration().setToolbarInitialExpanded(false);
                    logger.trace("      adding text cell with value \"" + newCells[tableIndex] + "\"");
                    richText.setText(newCells[tableIndex]);
                    editor.setEditor(richText, tableItem, tableIndex);
                    if ( tableColumn.getData("background color") != null )
                        richText.setBackground((Color)tableColumn.getData("background color"));
                    else
                        richText.setBackground(parent.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                        richText.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                        richText.setForeground(parent.getForeground());
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
                setData(lineTreeItem, "cells", newCells);
        }
        
        // used by graphical editor
        if ( treeItem != null ) {
            treeItem.setImage(RICHTEXT_ICON);
            treeItem.setData("class", "richtextColumn");
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
	public static CCombo createCombo(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo control");
        
        // we create the combo
    	CCombo combo = new CCombo(parent, SWT.NONE);
        
        String  name = getName(jsonObject, combo, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");

        getVariable(jsonObject, combo, treeItem, selectedObject);
        getXY(jsonObject, combo, treeItem);
        getValues(jsonObject, combo, treeItem);
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
	public static TableColumn createComboColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating combo column");
        
        // we create the combo
        int tableIndex = index==null ? parent.getColumnCount() : index;
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE, tableIndex);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("combo");
        
        getValues(jsonObject, tableColumn, treeItem);
        getDefault(jsonObject, tableColumn, treeItem);
        getForegroundAndBackground(jsonObject, tableColumn, treeItem);
        getTableColumnWidth(jsonObject, tableColumn, treeItem);
        getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
        getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
        
        // If the table already contains items, then we need to update the editors and cells
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == tableIndex ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[tableIndex] = editor;
                    
                    newCells[tableIndex] = "${void}";
                    CCombo combo = new CCombo(parent, SWT.NONE);
                    logger.trace("      adding combo cell with value \"" + newCells[tableIndex] + "\"");
                    combo.setText(newCells[tableIndex]);
                    if ( tableColumn.getData("background color") != null )
                    	combo.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	combo.setBackground(parent.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	combo.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	combo.setForeground(parent.getForeground());
                    String[] values = (String[])parent.getColumn(tableIndex).getData("values");
                    if ( values != null ) combo.setItems(values);
                    editor.setEditor(combo, tableItem, tableIndex);
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
                setData(lineTreeItem, "cells", newCells);
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
	public static Button createCheck(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
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
	public static TableColumn createCheckColumn(JSONObject jsonObject, Table parent, TreeItem treeItem, Integer index, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating check column");
        
        // we create the check button
        int tableIndex = index==null ? parent.getColumnCount() : index;
        TableColumn tableColumn = new TableColumn(parent, SWT.NONE, tableIndex);
        
        String  name = getName(jsonObject, tableColumn, treeItem);
        FormPosition.setTabName(name);
        FormPosition.setControlClass("check");
        
 	   	getValues(jsonObject, tableColumn, treeItem);
        getDefault(jsonObject, tableColumn, treeItem);
 	    getForegroundAndBackground(jsonObject, tableColumn, treeItem);
   		getTableColumnWidth(jsonObject, tableColumn, treeItem);
   		getAlignment(jsonObject, tableColumn, treeItem);
 	   	getTooltip(jsonObject, tableColumn, treeItem, selectedObject);
 	   	getExcelCellOrColumn(jsonObject, tableColumn, treeItem);
 	   	
        // If the table already contains items, then we need to update the editors and cells
        for ( TableItem tableItem: parent.getItems() ) {
            TableEditor[] oldEditors = (TableEditor[])tableItem.getData("editors");
            TableEditor[] newEditors = new TableEditor[parent.getColumnCount()];
            
            String[] oldCells = (String[])tableItem.getData("cells");
            String[] newCells = new String[parent.getColumnCount()];
            
            int newCol = 0;
            for (int oldCol=0; oldCol < parent.getColumnCount(); ++oldCol) {
                if ( oldCol == tableIndex ) {
                    TableEditor editor= new TableEditor(parent);
                    newEditors[tableIndex] = editor;
                    
                    newCells[tableIndex] = "${void}";
                    Button check = new Button(parent, SWT.CHECK);
                    check.pack();
                    if ( tableColumn.getData("background color") != null )
                    	check.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	check.setBackground(parent.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	check.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	check.setForeground(parent.getForeground());
                    logger.trace("      adding check cell with value \"" + newCells[tableIndex] + "\"");
                    editor.minimumWidth = check.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    editor.setEditor(check, tableItem, tableIndex);
                             
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
                setData(lineTreeItem, "cells", newCells);
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
    public static Table createTable(JSONObject jsonObject, Composite parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
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
    public static TableItem createLine(JSONObject jsonObject, Table parent, TreeItem treeItem, EObject selectedObject) throws RuntimeException {
    	if (logger.isDebugEnabled()) logger.debug("Creating table item");
    	
    	TableItem tableItem = null;
    	boolean mustCreateLine = true;
    	String name = getName(jsonObject, null, treeItem);
    	Boolean generate = getGenerate(jsonObject, null, treeItem);
    	getFilter(jsonObject, null, treeItem);
    	
    	// if the selectedObject is provided AND if the lines are generated
    	//    then we check the selectedObject against the filter
    	if ( selectedObject != null && generate != null && generate )
    		mustCreateLine = checkFilter(selectedObject, getJSONObject(jsonObject, "filter"));
    	
    	if ( mustCreateLine ) {
	        tableItem = new TableItem(parent, SWT.NONE);
	        
	        FormPosition.setControlName(name);
	        FormPosition.setControlClass("lines");
	        
	        getCells(jsonObject, tableItem, treeItem, selectedObject);
	        
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
		if ( selectedObject != null && generate != null && generate ) {
			if (selectedObject instanceof IDiagramModelContainer) {
				if (logger.isTraceEnabled()) logger.debug(((INameable)selectedObject).getName()+" is a DiagramModelContainer : getting children");
            	for ( IDiagramModelObject child: ((IDiagramModelContainer) selectedObject).getChildren()) {
            		tableItem = createLine(jsonObject, parent, treeItem, child);
					for ( IDiagramModelConnection relation: child.getSourceConnections() ) {
						tableItem = createLine(jsonObject, parent, treeItem, relation);
					}
            	}
            } else if (selectedObject instanceof IFolder) {
            	if (logger.isTraceEnabled()) logger.debug(((INameable)selectedObject).getName()+" is a Folder : getting elements");
            	for ( EObject child: ((IFolder) selectedObject).getElements()) {
            		tableItem = createLine(jsonObject, parent, treeItem, child);
            	}
                if (logger.isTraceEnabled()) logger.debug(((INameable)selectedObject).getName()+" is a Folder : getting subfolders");
                for ( EObject child: ((IFolder) selectedObject).getFolders()) {
                    tableItem = createLine(jsonObject, parent, treeItem, child);
                }
            } else if (selectedObject instanceof IArchimateModel) {
            	if (logger.isTraceEnabled()) logger.debug(((INameable)selectedObject).getName()+" is an ArchimateModel : getting elements");
            	for (IFolder folder : ((IArchimateModel) selectedObject).getFolders()) {
        			// we do not go through the "views" folder to avoid elements and relationships duplicated
        			if ( folder.getType().ordinal() != FolderType.DIAGRAMS_VALUE ) { 
        				for ( EObject child: folder.getElements()) {
        					tableItem = createLine(jsonObject, parent, treeItem, child);
        				}
        				for ( EObject child: folder.getFolders()) {
        					tableItem = createLine(jsonObject, parent, treeItem, child);
        				}
        			}
            	}
            }
 		}

        return tableItem;
    }
    
    /* **************************************************************/
    /**
     * Checks whether the eObject fits in the filter rules
     */
    public static boolean checkFilter(EObject eObject, JSONObject filterObject) {
        if (filterObject == null) {
            return true;
        }

        String type = getString(filterObject, "genre", "and").toUpperCase();

        if (!type.equals("AND") && !type.equals("OR"))
            throw new RuntimeException("Invalid filter genre. Supported genres are \"AND\" and \"OR\".");

        boolean result;

        Iterator<JSONObject> filterIterator = getJSONArray(filterObject, "tests").iterator();
        while (filterIterator.hasNext()) {
            JSONObject filter = filterIterator.next();
            String attribute = getString(filter, "attribute", null);
            String attributeValue = null;
            String operationRequested = getString(filter, "operation", null);
            String operation = null;
            String value;
            String[] values;
            boolean negate = false;
            
            if ( operationRequested.toLowerCase().startsWith("not ") ) {
                operation=operationRequested.substring(4);
                negate = true;
            } else {
                operation = operationRequested;
                negate = false;
            }

            switch (operation.toLowerCase()) {
                case "equals":
                    value = getString(filter, "value", null);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter \""+attribute+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    attributeValue = FormVariable.expand(attribute, eObject);
                    if (logger.isTraceEnabled())
                        logger.trace("      --> \""+attributeValue+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    result = attributeValue.equals(value);
                    break;

                case "in":
                    value = getString(filter, "value", null);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter \""+attribute+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    attributeValue = FormVariable.expand(attribute, eObject);
                    if (logger.isTraceEnabled())
                        logger.trace("      --> \""+attributeValue+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    values = value.split(",");
                    result = false;
                    for (String str : values) {
                        if (str.equals(attributeValue)) {
                            result = true;
                            break;
                        }
                    }
                    break;

                case "exists":
                    if (logger.isTraceEnabled())
                        logger.trace("   filter \""+attribute+"\" \""+operationRequested+"\"");
                    
                    attributeValue = FormVariable.expand(attribute, eObject);
                    if (logger.isTraceEnabled())
                        logger.trace("      --> \""+attributeValue+"\" \""+operationRequested+"\"");
                    
                    result = attributeValue.isEmpty();
                    break;

                case "iequals":
                    value = getString(filter, "value", null);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter \""+attribute+"\" \""+operationRequested+"\" \""+value+"\"");

                    attributeValue = FormVariable.expand(attribute, eObject);
                    if (logger.isTraceEnabled())
                        logger.trace("      --> \""+attributeValue+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    result = attributeValue.equalsIgnoreCase(value);
                    break;

                case "iin":
                    value = getString(filter, "value", null);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter \""+attribute+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    attributeValue = FormVariable.expand(attribute, eObject);
                    if (logger.isTraceEnabled())
                        logger.trace("      --> \""+attributeValue+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    values = value.split(",");
                    result = false;
                    for (String str : values) {
                        if (str.equals(attributeValue)) {
                            result = true;
                            break;
                        }
                    }
                    break;

                case "matches":
                    value = (String)getJSON(filter, "value");
                    if (logger.isTraceEnabled())
                        logger.trace("   filter \""+attribute+"\" \""+operationRequested+"\" \""+value+"\"");
                    
                    attributeValue = FormVariable.expand(attribute, eObject);
                    if (logger.isTraceEnabled())
                        logger.trace("      --> \""+attributeValue+"\" \""+operationRequested+"\" \""+value+"\"");

                    result = (attributeValue != null) && attributeValue.matches(value);
                    break;

                default:
                    throw new RuntimeException("Unknown operation type \"" + operation + "\" in filter.\n\nValid operations are \"equals\"/\"not equals\", \"iequals\"/\"not iequals\", \"exists\"/\"not exists\" and \"matches\"/\"not matches\".");
            }
            
            // if the operation starts with "not ", then we negate the result
            if ( negate )
                result = !result;
            
            if (logger.isTraceEnabled())
                logger.trace("      --> "+result);

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
    
    private static void getFilter(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	JSONObject filter = getJSONObject(jsonObject, "filter");
    	List<Map<String, String>> tests = null;
    	
    	JSONArray testsJson = getJSONArray(filter, "tests");
    	
    	if ( testsJson != null ) {
    		logger.trace("      filter:");
        	String genre = getString(filter, "genre", "and").toUpperCase();
        	
        	// required by the graphical editor
        	if ( treeItem != null ) {
        		setData(treeItem, "genre", genre);
        	}
        	
        	// required by the form
        	if ( widget != null ) {
        		widget.setData("genre", genre);
        	}
        	
    		tests = new ArrayList<Map<String, String>>();
    		
			Iterator<JSONObject> testIterator = testsJson.iterator();
            while (testIterator.hasNext()) {
            	JSONObject test = testIterator.next();
            	
            	Map<String, String> t = new HashMap<String, String>();
            	logger.trace("      test: ");
            	t.put("attribute", getString(test, "attribute", null));
            	t.put("operation", getString(test, "operation", null));
            	t.put("value",     getString(test, "value", null));
            	
            	tests.add(t);
            }

    	}
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		if ( tests == null || tests.size() == 0 ) {
    			setData(treeItem, "genre", null);
    			setData(treeItem, "tests", null);
    		} else
    			setData(treeItem, "tests", tests);
    	}
    	
    	// required by the form
    	if ( widget != null ) {
    		if ( tests == null || tests.size() == 0 ) {
    			widget.setData("genre", null);
    			widget.setData("tests", null);
    		} else
    			widget.setData("tests", tests);
    	}
    }
    
    private static Boolean getGenerate(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem) {
    	Boolean generate = getBoolean(jsonObject, "generate", false);
    	
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
    
    private static void getCells(JSONObject jsonObject, TableItem tableItem, TreeItem treeItem, EObject selectedObject) {
    	Table table = tableItem.getParent();
    	JSONArray jsonCells = getJSONArray(jsonObject, "cells");
    	String[] cells = null;
    	
    	cells = new String[table.getColumnCount()];
    	TableEditor[] editors = new TableEditor[table.getColumnCount()];
    	
		logger.trace("      new line:");
    	
    	// we get the cells variables, completing if some are missing and ignoring if too many are present
    	for ( int columnNumber = 0; columnNumber < table.getColumnCount(); ++columnNumber ) {
    		// for each cell, we create the corresponding table editor
    		TableColumn tableColumn = table.getColumn(columnNumber);
    		TableEditor editor= new TableEditor(table);
            editors[columnNumber] = editor;
            String[] values;
            
    		switch ( (String)table.getColumn(columnNumber).getData("class") ) {
                case "labelColumn":
                    if ( (jsonCells != null) && (columnNumber < jsonCells.size()) )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "label";
                    logger.trace("      adding label cell with value \"" + cells[columnNumber] + "\"");
                    Label label = new Label(table, SWT.WRAP | SWT.NONE);
                    if ( selectedObject == null )
                    	label.setText(cells[columnNumber]);
                    else
                    	label.setText(FormVariable.expand(cells[columnNumber], selectedObject));
                    if ( tableColumn.getData("background color") != null )
                    	label.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	label.setBackground(table.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	label.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	label.setForeground(table.getForeground());
                    editor.setEditor(label, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "imageColumn":
                    if ( (jsonCells != null) && (columnNumber < jsonCells.size()) )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "image";
                    logger.trace("      adding image cell with value \"" + cells[columnNumber] + "\"");
                    Label labelImage = new Label(table, SWT.WRAP | SWT.NONE);
                    
                    // we set the label's image
                    String imageName;
                    if ( selectedObject == null )
                        imageName = cells[columnNumber];
                    else
                        imageName = FormVariable.expand(cells[columnNumber], selectedObject);
                    
                    if ( !FormPlugin.isEmpty(imageName) ) {
                        //TODO : create a cache for the images.
                        labelImage.setImage(new Image(display, imageName));
                    }
                    
                    if ( tableColumn.getData("background color") != null )
                        labelImage.setBackground((Color)tableColumn.getData("background color"));
                    else
                        labelImage.setBackground(table.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                        labelImage.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                        labelImage.setForeground(table.getForeground());
                    editor.setEditor(labelImage, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "textColumn":
                    if ( (jsonCells != null) && (columnNumber < jsonCells.size()) )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "${void}";
                    StyledText text = new StyledText(table, SWT.WRAP | SWT.NONE);
                    logger.trace("      adding text cell with value \"" + cells[columnNumber] + "\"");
                    if ( selectedObject == null )
                    	text.setText(cells[columnNumber]);
                    else {
                    	text.setText(FormVariable.expand(cells[columnNumber], selectedObject));
                    	
                    	EObject referedEObject = FormVariable.getReferedEObject(cells[columnNumber], selectedObject);
    	                String unscoppedVariable = FormVariable.getUnscoppedVariable(cells[columnNumber], selectedObject);
    	                
                    	text.setData("variable", unscoppedVariable);
                    	text.setData("eObject", referedEObject);
                    	FormDialog.formVarList.set(referedEObject, unscoppedVariable, text);
                    	
                    	if ( (text.getText().isEmpty() || (boolean)tableColumn.getData("forceDefault") == true) && tableColumn.getData("default") != null ) {
                    	    text.setText(FormVariable.expand((String)tableColumn.getData("default"), referedEObject));
                    	}
                    	
                    	text.addModifyListener(FormDialog.textModifyListener);
                    }
                    if ( tableColumn.getData("background color") != null )
                    	text.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	text.setBackground(table.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	text.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	text.setForeground(table.getForeground());
                    editor.setEditor(text, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "comboColumn":
                    if ( (jsonCells != null) && (columnNumber < jsonCells.size()) )
                        cells[columnNumber] = (String)jsonCells.get(columnNumber);
                    else
                        cells[columnNumber] = "${void}";
                    CCombo combo = new CCombo(table, SWT.NONE);
                    logger.trace("      adding combo cell with value \"" + cells[columnNumber] + "\"");
                    values = (String[])table.getColumn(columnNumber).getData("values");
                    if ( selectedObject != null && values != null ) {
                        int nbValues = ((String[])table.getColumn(columnNumber).getData("values")).length; 
                        String[] newValues = new String[nbValues];
                        for ( int i=0; i<nbValues; ++i) {
                            newValues[i] = FormVariable.expand(values[i], selectedObject);
                        }
                        values = newValues;
                    }
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
                    	combo.setData("values", values);
                    	FormDialog.formVarList.set(referedEObject, unscoppedVariable, combo);
                    	
                    	combo.addModifyListener(FormDialog.textModifyListener);
                    	
                        if ( (combo.getText().isEmpty() || (boolean)tableColumn.getData("forceDefault") == true) && tableColumn.getData("default") != null ) {
                            combo.setText((String)tableColumn.getData("default"));
                        }
                    }
                    if ( tableColumn.getData("background color") != null )
                    	combo.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	combo.setBackground(table.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	combo.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	combo.setForeground(table.getForeground());
                    if ( values != null ) combo.setItems(values);
                    editor.setEditor(combo, tableItem, columnNumber);
                    editor.grabHorizontal = true;
                    break;
                    
                case "checkColumn":
                    if ( (jsonCells != null) && (columnNumber < jsonCells.size()) )
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
                    	check.setData("values", values);
                    	FormDialog.formVarList.set(referedEObject, unscoppedVariable, check);
                    	
                    	check.addSelectionListener(FormDialog.checkButtonSelectionListener);
                    }
                    if ( tableColumn.getData("background color") != null )
                    	check.setBackground((Color)tableColumn.getData("background color"));
                    else
                    	check.setBackground(table.getBackground());
                    if ( tableColumn.getData("foreground color") != null )
                    	check.setForeground((Color)tableColumn.getData("foreground color"));
                    else
                    	check.setForeground(table.getForeground());
                    editor.minimumWidth = check.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    editor.setEditor(check, tableItem, columnNumber);
                    break;
                    
                default:
                    throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to add table item for unknown object class \"" + ((String)table.getColumn(columnNumber).getData("class")) + "\"");
    		}
    		editor.layout();
    	}
    	
    	tableItem.setData("editors", editors);
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "cells", cells);
    		
    	}
    	
    	// required by the form
    	tableItem.setData("cells", cells);
    }
    
    /* ********************************************************************************************************************************/
    
    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj the JSONObject
     * @param key the key to search for (case insensitive)
     * @return the value corresponding to the key (the behaviour in case no value is found depends on the <i>ignoreErrors<i> flag). The returned value is an <i>object</i> and therefore might be cast.
     * @throws RuntimeException if the key is not found and the <i>ignoreErrors</i> flag has not been set)
     */
    public static Object getJSON(JSONObject obj, String key) {
    	if ( obj == null )
    		return null;
    	
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
    public static JSONObject getJSONObject(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);
        
        if ( result == null )
        	return null;
        
        if ( !(result instanceof JSONObject) ) {
        	FormPlugin.error(FormPosition.getPosition(key) + "\n\nInvalid value \""+result+"\" : is a " + result.getClass().getSimpleName() + " but should be a JSONObject.");
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
    public static JSONArray getJSONArray(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);

        if ( result == null )
        	return null;
        
        if ( !(result instanceof JSONArray) ) {
        	FormPlugin.error(FormPosition.getPosition(key) + "\n\nInvalid value \""+result+"\" : is a " + result.getClass().getSimpleName() + " but should be a JSONArray.");
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
     * @return the value if found, ClassCastException if the object found is not a String
     */
    public static String getString(JSONObject obj, String key, String defaultValue) throws RuntimeException, ClassCastException {
    	boolean mustSetDefaultValue = false;
    	Object result = getJSON(obj, key);
        
        if ( result == null )
        	mustSetDefaultValue = defaultValue != null;
        else {
            if ( !(result instanceof String) ) {
            	FormPlugin.error(FormPosition.getPosition(key) + "\n\nInvalid value \""+result+"\" : is a " + result.getClass().getSimpleName() + " but should be a String.");
                mustSetDefaultValue = true;
            }
        }

    	String resultDbg = (result!=null && ((String)result).length() > 51) ? (((String)result).substring(0, 50)+"...") : (String)result;
        if ( mustSetDefaultValue ) {
            logger.trace("      "+key+" = "+resultDbg+" (defaulting to "+defaultValue+")");
            result = defaultValue;
        } else {
    		logger.trace("      "+key+" = " + resultDbg);
        }
        
        return (String)result;
    }
    
    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @return the value if found, ClassCastException if the object found is not a String
     */
    public static String getString(JSONObject obj, String key, String defaultValue, String[] validValues) throws RuntimeException, ClassCastException {
        boolean mustSetDefaultValue = false;
    	Object result = getJSON(obj, key);
        
        if ( result == null )
        	mustSetDefaultValue = defaultValue != null;
        else {
            if ( result instanceof String )
            	mustSetDefaultValue = !inArray(validValues, ((String)result).toLowerCase());
            else {
            	FormPlugin.error(FormPosition.getPosition(key) + "\n\nInvalid value \""+result+"\" : is a " + result.getClass().getSimpleName() + " but should be a String.");
                mustSetDefaultValue = true;
            }
        }
        
        if ( mustSetDefaultValue ) {
            logger.trace("      "+key+" = "+result+" (defaulting to "+defaultValue+")");
            result = defaultValue;
        } else
    		logger.trace("      "+key+" = " + result);

        return (String)result;
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
    public static Integer getInt(JSONObject obj, String key, Integer defaultValue, boolean canBeZero) throws RuntimeException, ClassCastException {
        boolean mustSetDefaultValue = false;
    	Object result = getJSON(obj, key);
        
        if ( result != null ) {
            if ( result instanceof Long )
            	mustSetDefaultValue = (Long)result <= 0L || (!canBeZero && (Long)result == 0L);
            else {
            	FormPlugin.error(FormPosition.getPosition(key) + "\n\nInvalid value \""+result+"\" : is a " + result.getClass().getSimpleName() + " but should be an Integer.");
                mustSetDefaultValue = true;
            }
        }

        if ( result == null || mustSetDefaultValue ) {
            logger.trace("      "+key+" = "+result+" (defaulting to "+defaultValue+")");
            return defaultValue;
        }
        
    	logger.trace("      "+key+" = " + result);
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
    public static Boolean getBoolean(JSONObject obj, String key, Boolean defaultValue) throws RuntimeException, ClassCastException {
    	boolean mustSetDefaultValue = false;
        Object result = getJSON(obj, key);
        
        if ( result != null ) {
            if ( !(result instanceof Boolean) ) {
            	FormPlugin.error(FormPosition.getPosition(key) + "\n\nInvalid value \""+result+"\" : is a " + result.getClass().getSimpleName() + " but should be a Boolean.");
                mustSetDefaultValue = true;
            }
        }

        if ( result == null || mustSetDefaultValue ) {
            logger.trace("      "+key+" = "+result+" (defaulting to "+defaultValue+")");
            return defaultValue;
        }
    	
        logger.trace("      "+key+" = " + result);
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
    public static boolean JSONContainsKey(JSONObject obj, String key) {
        Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }

    /* **********************************************************************************************************/
    
    public static boolean inArray(String[] stringArray, String string) {
    	if ( string == null )
    		return true;
    	
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
    
    /* **********************************************************************************************************/
    
    private static void getXY(JSONObject jsonObject, Control control, TreeItem treeItem) {
    	int defaultWidth = 0;
    	int defaultHeight = 0;
        if ( control != null ) {
	    	Point p = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    	defaultWidth = p.x;
	    	defaultHeight = p.y;
        }
        
    	Integer x      = getInt(jsonObject, "x", 0, true);
    	Integer y      = getInt(jsonObject, "y", 0, true);
    	Integer width  = getInt(jsonObject, "width", defaultWidth, true);
    	Integer height = getInt(jsonObject, "height", defaultHeight, true);
    	
        // required by graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "x", 	   x);
        	setData(treeItem, "y", 	   y); 
        	setData(treeItem, "width",  width);
        	setData(treeItem, "height", height);
        }

        // we set the widget position
        if ( control != null )
	    	control.setBounds(x, y, width, height);
    }
    
    private static void getTableColumnWidth(JSONObject jsonObject, TableColumn tableColumn, TreeItem treeItem) {
    	int defaultWidth = 50;
    	if ( tableColumn != null && tableColumn.getData("name") != null )
    		defaultWidth = 10 + ((String)tableColumn.getData("name")).length()*8;
    	
    	Integer width  = getInt(jsonObject, "width", defaultWidth, false);
    	
        if ( logger.isTraceEnabled() ) {
            logger.trace("      width = "  + width);
        }
    	
        // required by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "width",  width);
        }
        
        // we set the tableColumn width
        if ( tableColumn != null ) {
        	tableColumn.setWidth(width);
	    	tableColumn.setResizable(width != 0);
        }
    }
    
    private static void getForegroundAndBackground(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String foreground = getString(jsonObject, "foreground", null);
    	String background = getString(jsonObject, "background", null);
    	
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
    	if ( widget != null ) {
    		if ( widget instanceof TableColumn ) {
    			FormPlugin.setColor((TableColumn)widget, foreground, SWT.FOREGROUND);
    			FormPlugin.setColor((TableColumn)widget, background, SWT.BACKGROUND);
    		} else if ( widget instanceof Control ) {
    			FormPlugin.setColor((Control)widget, foreground, SWT.FOREGROUND);
    			FormPlugin.setColor((Control)widget, background, SWT.BACKGROUND);
    		}
    	}
    }
    
    private static void getAlignment(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String alignment = getString(jsonObject, "alignment", FormDialog.validAlignment[0]).toLowerCase();
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "alignment", alignment);
    	}
    	
    	// we set the widget alignment
    	if ( widget != null ) {    	
    		FormPlugin.setAlignment(widget, alignment);
    	}
    }
    
    private static void getRegexp(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String regexp = getString(jsonObject, "regexp", null);
    	
    	// required by the graphical editor
    	if ( treeItem != null ) {
    		setData(treeItem, "regexp", regexp);
    	}
    	
    	// required by the form
    	if ( widget != null ) {
    		widget.setData("regexp", regexp);
    	}
    }
    
    private static void getVariable(JSONObject jsonObject, Widget widget, TreeItem treeItem, EObject selectedObject) {
    	String  variable      = getString(jsonObject, "variable", null);
    	String  defaultText   = getString(jsonObject, "default", null);
    	Boolean forceDefault  = getBoolean(jsonObject, "forceDefault", false);
    	String  whenEmpty     = getString(jsonObject, "whenEmpty", FormDialog.validWhenEmpty[0], FormDialog.validWhenEmpty);
        Boolean editable = null;
        if ( widget != null && (widget instanceof StyledText || widget instanceof CCombo) )
        	editable = getBoolean(jsonObject, "editable", true);

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
					case "StyledText":         ((StyledText)widget).setText(variableValue); break;
					case "CCombo":             ((CCombo)widget).setText(variableValue); break;
					case "Button":        	   ((Button)widget).setText(variableValue); break;
					case "FormRichTextEditor": ((FormRichTextEditor)widget).setText(variableValue); break;
					default: throw new RuntimeException("Do not know how to set text to a "+widget.getClass().getSimpleName());
				}
			}
		}
    }
    
    private static void getDefault(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
        String  defaultText   = getString(jsonObject, "default", null);
        Boolean forceDefault  = getBoolean(jsonObject, "forceDefault", false);
        String  whenEmpty     = getString(jsonObject, "whenEmpty", FormDialog.validWhenEmpty[0], FormDialog.validWhenEmpty);
        Boolean editable = null;
        if ( FormPlugin.areEqual(FormPosition.getControlClass(), "text") || FormPlugin.areEqual(FormPosition.getControlClass(), "combo") )
            editable = getBoolean(jsonObject, "editable", true);

        // required by the graphical editor
        if ( treeItem != null ) {
            setData(treeItem, "default",      defaultText);
            setData(treeItem, "forceDefault", forceDefault);
            setData(treeItem, "whenEmpty",    whenEmpty);
            if ( FormPlugin.areEqual(FormPosition.getControlClass(), "text") || FormPlugin.areEqual(FormPosition.getControlClass(), "combo") )
                setData(treeItem, "editable",  editable);
        }
        
        // required by the form
        if ( widget != null ) {
            widget.setData("default",      defaultText);
            widget.setData("forceDefault", forceDefault);
            widget.setData("whenEmpty",    whenEmpty);
            if ( FormPlugin.areEqual(FormPosition.getControlClass(), "text") || FormPlugin.areEqual(FormPosition.getControlClass(), "combo") )
                widget.setData("editable",  editable);
        }
    }
    
    private static void getExcelCellOrColumn(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String excelSheet    = getString(jsonObject, "excelSheet", null);
    	String excelCell     = getString(jsonObject, "excelCell", null);
    	String excelColumn   = (widget != null && widget instanceof TableColumn) ? getString(jsonObject, "excelColumn", null) : null;		// excelColumn is used only when TableColumn (excelSheet is referenced in the Table)
    	String excelCellType = getString(jsonObject, "excelCellType", FormDialog.validExcelCellType[0], FormDialog.validExcelCellType);
    	String excelDefault  = getString(jsonObject, "excelDefault", FormDialog.validExcelDefault[0], FormDialog.validExcelDefault);
        
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
    
    private static void getExcelLines(JSONObject jsonObject, Table table, TreeItem treeItem) {
    	String  excelSheet     = getString(jsonObject, "excelSheet", null);
    	Integer excelFirstLine = getInt(jsonObject, "excelFirstLine", 1, false);
    	Integer excelLastLine  = getInt(jsonObject, "excelLastLine", 0, true);
    	
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
    		
            if ( excelSheet != null ) {
                @SuppressWarnings("unchecked")
                HashSet<String> excelSheets = (HashSet<String>)table.getShell().getData("excel sheets");
                excelSheets.add(excelSheet);
            }
    	}
    }
    
    @SuppressWarnings("unchecked")
	private static void getValues(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	JSONArray jsonValues = getJSONArray(jsonObject, "values");
    	String[] values = null;
    	
        if ( logger.isTraceEnabled() )
        	logger.trace("      values = " + (jsonValues==null ? null : FormPlugin.concat((String[])jsonValues.toArray(new String[0]), "", ",")));
    	
        
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
    
    private static void getTooltip(JSONObject jsonObject, Widget widget, TreeItem treeItem, EObject selectedObject) {
    	String tooltip = getString(jsonObject, "tooltip", null);
        
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
    
    private static String getName(JSONObject jsonObject, Widget widget, TreeItem treeItem) {
    	String name = getString(jsonObject, "name", "");
    	String comment = getString(jsonObject, "comment", "");
        
    	// required by the graphical editor
        if ( treeItem != null ) {
        	setData(treeItem, "name", name);
        	setData(treeItem, "comment", comment);
      		treeItem.setText(name);
        }
    	
        // we set the column text to its name
        if ( widget != null && widget instanceof TableColumn )
	        ((TableColumn)widget).setText(name);
        
        return name;
    }

	private static void getText(JSONObject jsonObject, Label label, TreeItem treeItem, EObject selectedObject) {
		String text = getString(jsonObject, "text", "");
	    
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
	
   private static void getImage(JSONObject jsonObject, Label label, TreeItem treeItem, EObject selectedObject) {
        String imageName = getString(jsonObject, "image", null);
        String imageContent = getString(jsonObject, "content", null);
        Boolean scale = getBoolean(jsonObject, "scale", false);
        
        // required by the graphical editor
        if ( treeItem != null ) {
            setData(treeItem, "image", imageName);
            setData(treeItem, "content", imageContent);
            setData(treeItem, "scale", scale);
        }
        
        // we set the label's image
        if ( selectedObject != null ) {
            imageName = FormVariable.expand(imageName, selectedObject);
            imageContent = FormVariable.expand(imageContent, selectedObject);
        }
        
        if ( label != null ) {
            //TODO : create a cache for the images.
        	Image image = label.getImage();
        	
        	if ( image != null ) {
        		image.dispose();
        		image = null;
        	}
    		
        	if ( !FormPlugin.isEmpty(imageContent) ) {
        		image = FormPlugin.stringToImage(imageContent);
        	} else if ( !FormPlugin.isEmpty(imageName) ) {
            	image = new Image(display, imageName);
        	}
        	
            if( image != null ) {
	            try {
	        		int width = label.getBounds().width > 0 ? label.getBounds().width : image.getBounds().width;
	        		int height = label.getBounds().height > 0 ? label.getBounds().height : image.getBounds().height;
                	if ( scale ) {
                		Image scaledImage = new Image(display, image.getImageData().scaledTo(width, height));
                		label.setImage(scaledImage);
                		image.dispose();
                	} else {
                		label.setImage(image);
                	}
	            } catch (@SuppressWarnings("unused") Exception ign) { 
	                // nothing to do
	            }
        	}
        }
    }
    
    private static void getFont(JSONObject jsonObject, Control control, TreeItem treeItem) {
    	String  fontName = getString(jsonObject, "fontName", null);
    	Integer fontSize = getInt(jsonObject, "fontSize", 0, true);
    	Boolean fontBold = getBoolean(jsonObject, "fontBold", false);
    	Boolean fontItalic = getBoolean(jsonObject, "fontItalic", false);
    	
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
    
    public static void setData(TreeItem treeItem, String key, Object value) {
    	if ( treeItem == null || key == null)
    		return;
    	
        addKey(treeItem, key);        
        treeItem.setData(key, value);
    }
    
    public static void addKey(TreeItem treeItem, String key) {
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
    
    /* **********************************************************************************************************/
    @SuppressWarnings("unchecked")
	public static JSONObject generateJson(Tree tree) throws RuntimeException {
    	JSONObject json = new JSONObject();
    	
    	json.put("version", 3);
    	json.put("org.archicontribs.form", generateJson(tree.getItem(0)));
    	
    	return json;
    }
    	
    
    @SuppressWarnings("unchecked")
	private static JSONObject generateJson(TreeItem treeItem) throws RuntimeException {
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
    			
    		default:
    		    // nothing to do
    	}
    	
    	return json;
    }
}
