package org.archicontribs.form;

import java.awt.Toolkit;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
import com.archimatetool.model.INameable;
import com.florianingerl.util.regex.Pattern;

/**
 * Create a Dialog with graphical controls as described in the configuration
 * file
 * 
 * @author Herve Jouin
 *
 */
public class FormDialog extends Dialog {
    // TODO: add a "continue on error" option
    private static final FormLogger logger            = new FormLogger(FormDialog.class);

    protected static    Display  display           = Display.getDefault();
    public static final FontData SYSTEM_FONT       = display.getSystemFont().getFontData()[0];
    public static final Font     TITLE_FONT        = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight() + 2, SWT.BOLD);
    public static final Font     BOLD_FONT         = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight(), SWT.BOLD);

    public static final Color    LIGHT_GREEN_COLOR = new Color(display, 204, 255, 229);
    public static final Color    LIGHT_RED_COLOR   = new Color(display, 255, 230, 230);
    public static final Color    RED_COLOR         = new Color(display, 240, 0, 0);
    public static final Color    GREEN_COLOR       = new Color(display, 0, 180, 0);
    public static final Color    WHITE_COLOR       = new Color(display, 255, 255, 255);
    public static final Color    GREY_COLOR        = new Color(display, 100, 100, 100);
    public static final Color    BLACK_COLOR       = new Color(display, 0, 0, 0);
    public static final Color    LIGHT_BLUE        = new Color(display, 240, 248, 255);

    private static final Color   badValueColor     = new Color(display, 255, 0, 0);
    private static final Color   goodValueColor    = new Color(display, 0, 100, 0);
    
    private final FormVarList    formVarList       = new FormVarList();

    private EObject              selectedObject    = null;
    private Shell                formDialog        = null;

    private HashSet<String>      excelSheets       = new HashSet<String>();
    
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
    
	private static final FormJsonParser jsonParser = new FormJsonParser();

    private String globalVariableSeparator = ":";
    
    public FormDialog(String configFilename, JSONObject json, EObject selectedObject) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        this.selectedObject = selectedObject;

        if (logger.isDebugEnabled())
            logger.debug("Creating new FormDialog for " + selectedObject.getClass().getSimpleName() + " \"" + ((INameable) selectedObject).getName() + "\".");

        try {
            formDialog = jsonParser.createShell(json, getParent());
            
            // we replace the name of the dialog in case there are some variables in it
            if ( formDialog.getData("name") != null )
            	formDialog.setText(FormVariable.expand((String)formDialog.getData("name"), selectedObject));
            
            // we set the listener on the buttons
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
            
            TabFolder tabFolder = (TabFolder)formDialog.getData("tab folder");
            
            // we create one TabItem per tab array item
            JSONArray tabs = jsonParser.getJSONArray(json, "tabs");
            if ( tabs != null ) {
            	@SuppressWarnings("unchecked")
				Iterator<JSONObject> tabsIterator = tabs.iterator();
                while (tabsIterator.hasNext()) {
                    JSONObject jsonTab = tabsIterator.next();
                    TabItem tabItem = jsonParser.createTab(jsonTab, tabFolder);
                    
                    // we replace the name of the dialog in case there are some variables in it
                    if ( tabItem.getData("name") != null )
                    	tabItem.setText(FormVariable.expand((String)tabItem.getData("name"), selectedObject));
                    
                    JSONArray controls = jsonParser.getJSONArray(jsonTab, "controls");
                    if ( controls != null ) {
                        Composite tabItemComposite = (Composite)tabItem.getControl();
                        @SuppressWarnings("unchecked")
						Iterator<JSONObject> controlsIterator = controls.iterator();
                        while (controlsIterator.hasNext()) {
                            JSONObject jsonControl = controlsIterator.next();
                            createControl(jsonControl, tabItemComposite);
                        }
                        tabItemComposite.layout();
                    }
                }
            }
            

            // If there is at least one Excel sheet specified, then we show up the "export to Excel" button
            if (excelSheets.isEmpty()) {
                exportButton.setVisible(false);
            }
        } catch (ClassCastException e) {
            popup(Level.ERROR, "Wrong key type in the configuration file \"" + configFilename + "\"", e);
            if (formDialog != null)
                formDialog.dispose();
            return;
        } catch (RuntimeException e) {
            popup(Level.ERROR, "Please check your configuration file \"" + configFilename + "\"", e);
            if (formDialog != null)
                formDialog.dispose();
            return;
        }

        formDialog.open();
        formDialog.layout();
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
     * 
     * @param tab
     *            The JSON object to parse
     * @param composite
     *            The composite where the control will be created
     */
    private void createControl(JSONObject jsonObject, Composite parent) throws RuntimeException {
            String variableValue;
            EObject referedEObject;
            String unscoppedVariable;
            FormJsonParser jsonParser = new FormJsonParser();

            String clazz = jsonParser.getString(jsonObject, "class");
            if ( clazz != null ) {
	            switch ( clazz.toLowerCase() ) {
	                case "check":
	                    Button check = (Button)jsonParser.createCheck(jsonObject, parent);
	                    
	                    // we replace the combo's text by the expanded variable, or by the defaulText if empty 
	                    String[] values = (String[])check.getData("values");
	                    variableValue = FormVariable.expand(jsonParser.getString(jsonObject, "variable"), selectedObject);
	                    if ( FormPlugin.isEmpty(variableValue) || (check.getData("forceDefault")!=null && (Boolean)check.getData("forceDefault")) )
	                    	variableValue = FormVariable.expand(jsonParser.getString(jsonObject, "default"), selectedObject);
	                    
	                    if ( values == null || values.length == 0 ) 										// should be "true" or "false"
	                    	check.setSelection(FormPlugin.areEqualIgnoreCase(variableValue, "true"));
	                    else																				// should be values[0] or values[1]
	                    	check.setSelection(FormPlugin.areEqualIgnoreCase(variableValue, values[0]));
	                    
	                    // we replace the tooltip by its expanded value in case it contains a variable
	                    check.setToolTipText(FormVariable.expand((String)check.getData("tooltip"), selectedObject));
	                    
	                    // We reference the variable and the control to the eObject that the variable refers to
	                    referedEObject = FormVariable.getReferedEObject((String)check.getData("variable"), selectedObject);
	                    unscoppedVariable = FormVariable.getUnscoppedVariable((String)check.getData("variable"), selectedObject);
	                    check.setData("variable", unscoppedVariable);
	                    check.setData("eObject", referedEObject);
	                    formVarList.set(referedEObject, unscoppedVariable, check);
	
	                    // if an excelSheet property has been set, then we register it to show-up the export to excel button
	                    if ( !FormPlugin.isEmpty((String)check.getData("excelSheet")) )
	                        excelSheets.add((String)check.getData("excelSheet"));
	                    
	                    check.addSelectionListener(checkButtonSelectionListener);
	                    break;
	                    
	                case "combo":
	                    CCombo combo = (CCombo)jsonParser.createCombo(jsonObject, parent);
	                    
	                    // we replace the combo's text by the expanded variable, or by the defaulText if empty 
	                    variableValue = FormVariable.expand(jsonParser.getString(jsonObject, "variable"), selectedObject);
	                    if ( FormPlugin.isEmpty(variableValue) || (combo.getData("forceDefault")!=null && (boolean)combo.getData("forceDefault")) )
	                    	variableValue = FormVariable.expand((String)combo.getData("defaultText"), selectedObject);
	                    combo.setText(variableValue);
	                    
	                    // we replace the tooltip by its expanded value in case it contains a variable
	                    combo.setToolTipText(FormVariable.expand((String)combo.getData("tooltip"), selectedObject));
	                    
	                    // We reference the variable and the control to the eObject that the variable refers to
	                    referedEObject = FormVariable.getReferedEObject((String)combo.getData("variable"), selectedObject);
	                    unscoppedVariable = FormVariable.getUnscoppedVariable((String)combo.getData("variable"), selectedObject);
	                    combo.setData("variable", unscoppedVariable);
	                    combo.setData("eObject", referedEObject);
	                    formVarList.set(referedEObject, unscoppedVariable, combo);
	
	                    // if an excelSheet property has been set, then we register it to show-up the export to excel button
	                    if ( !FormPlugin.isEmpty((String)combo.getData("excelSheet")) )
	                        excelSheets.add((String)combo.getData("excelSheet"));
	                    
	                    combo.addModifyListener(textModifyListener);
	                    break;
	                    
	                case "label":
	                    Label label = (Label)jsonParser.createLabel(jsonObject, parent);
	                    
	                    // we replace the combo's text by the expanded variable 
	                    variableValue = FormVariable.expand((String)label.getData("text"), selectedObject);
	                    label.setText(variableValue);
	                    
	                    // we replace the tooltip by its expanded value in case it contains a variable
	                    label.setToolTipText(FormVariable.expand((String)label.getData("tooltip"), selectedObject));
	                    
	                    // if an excelSheet property has been set, then we register it to show-up the export to excel button
	                    if ( !FormPlugin.isEmpty((String)label.getData("excelSheet")) )
	                        excelSheets.add((String)label.getData("excelSheet"));
	                    
	                    // We reference the variable and the control to the eObject that the variable refers to
	                    formVarList.set(FormVariable.getReferedEObject(variableValue, selectedObject), FormVariable.getUnscoppedVariable(variableValue, selectedObject), label);
	                    break;
	                    
	                case "table":
	                	Table table = jsonParser.createTable(jsonObject, parent);
	                	
	                	JSONArray columns = jsonParser.getJSONArray(jsonObject, "columns");
	                    if ( columns != null ) {
	                        @SuppressWarnings("unchecked")
							Iterator<JSONObject> columnsIterator = columns.iterator();
	                        while (columnsIterator.hasNext()) {
	                            JSONObject jsonColumn = columnsIterator.next();

	                            clazz = jsonParser.getString(jsonColumn, "class");
	                            if ( clazz != null ) {
	                	            switch ( clazz.toLowerCase() ) {
	                	            	case "check":
	                	            		TableColumn tableColumn = (TableColumn)jsonParser.createCheckColumn(jsonObject, table);
	                	            		
	                	            		tableColumn.addListener(SWT.Selection, sortListener);
	                	            		break;
	                	            }
	                            }
	                        }
	                    }
	                	
	    	            //TODO : columns and lines
	                    break;
	                    
	                case "text":
	                    StyledText text = (StyledText)jsonParser.createText(jsonObject, parent);
	                    
	                    // we replace the combo's text by the expanded variable, or by the defaulText if empty 
	                    variableValue = FormVariable.expand(jsonParser.getString(jsonObject, "variable"), selectedObject);
	                    if ( FormPlugin.isEmpty(variableValue) || (text.getData("forceDefault")!=null && (boolean)text.getData("forceDefault")) )
	                        variableValue = FormVariable.expand((String)text.getData("defaultText"), selectedObject);
	                    text.setText(variableValue);
	                    
	                    // we replace the tooltip by its expanded value in case it contains a variable
	                    text.setToolTipText(FormVariable.expand((String)text.getData("tooltip"), selectedObject));
	                    	
	                    // if the tooltip is empty but a regexp is defined,then we add a tooltip with a little help message about the regexp 
                        if ( FormPlugin.isEmpty(text.getToolTipText()) && !FormPlugin.isEmpty((String)text.getData("regexp")) ) {
                            text.setData("pattern", Pattern.compile((String)text.getData("regexp")));
                            text.setToolTipText("Your text should match the following regexp :\n" + (String)text.getData("regexp"));
                        }
	
	                    // We reference the variable and the control to the eObject that the variable refers to
	                    referedEObject = FormVariable.getReferedEObject((String)text.getData("variable"), selectedObject);
	                    unscoppedVariable = FormVariable.getUnscoppedVariable((String)text.getData("variable"), selectedObject);
	                    text.setData("variable", unscoppedVariable);
	                    text.setData("eObject", referedEObject);
	                    formVarList.set(referedEObject, unscoppedVariable, text);
	                    
	                    // if an excelSheet property has been set, then we register it to show-up the export to excel button
	                    if ( !FormPlugin.isEmpty((String)text.getData("excelSheet")) )
	                        excelSheets.add((String)text.getData("excelSheet"));
	                    
	                    text.addModifyListener(textModifyListener);
	                    break;
	                    
	                default:
	                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + jsonObject.get("class") + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
	            }
            }
            FormPosition.resetControlName();
            FormPosition.resetControlClass();
    }





    /**
     * Create TableItems in the Table control<br>
     * <br>
     * called by the createTable() method
     * 
     * @param table
     *            the table in which the tableItems will be creates
     * @param list
     *            the list of objects corresponding to the tableItems to create
     * @param values
     *            the JSON array representing the variables used to fill in the
     *            tableItem columns
     * @param filter
     *            the JSONObject representing a filter if any
     */
    /*
    @SuppressWarnings("unchecked")
    private void addTableItems(Table table, EList<?> list, JSONArray values, JSONObject filter) throws RuntimeException {
        if ((list == null) || list.isEmpty())
            return;

        if (list.get(0) instanceof IDiagramModelObject) {
            for (IDiagramModelObject diagramObject : (EList<IDiagramModelObject>) list) {
                if (logger.isTraceEnabled())
                    logger.trace("Found diagram object " + diagramObject.getName());
                if (checkFilter(diagramObject, filter)) {
                    if (diagramObject instanceof IDiagramModelArchimateObject)
                        addTableItem(table, (EObject) (((IDiagramModelArchimateObject) diagramObject).getArchimateElement()), values);
                    else
                        addTableItem(table, (EObject) diagramObject, values);
                }

                addTableItems(table, diagramObject.getSourceConnections(), values, filter);

                if (diagramObject instanceof IDiagramModelArchimateObject) {
                    addTableItems(table, ((IDiagramModelArchimateObject) diagramObject).getChildren(), values, filter);
                }
            }
        } else
            if (list.get(0) instanceof IDiagramModelArchimateConnection) {
                for (IDiagramModelArchimateConnection diagramConnection : (EList<IDiagramModelArchimateConnection>) list) {
                    if (logger.isTraceEnabled())
                        logger.trace("Found diagram connection " + diagramConnection.getName());
                    if (checkFilter(diagramConnection, filter)) {
                        addTableItem(table, (EObject) diagramConnection.getArchimateRelationship(), values);
                    }
                }
            } else
                if (list.get(0) instanceof IArchimateElement) {
                    for (IArchimateElement element : (EList<IArchimateElement>) list) {
                        if (logger.isTraceEnabled())
                            logger.trace("Found element " + element.getName());
                        if (checkFilter(element, filter)) {
                            addTableItem(table, element, values);
                        }
                    }
                } else
                    if (list.get(0) instanceof IArchimateRelationship) {
                        for (IArchimateRelationship relation : (EList<IArchimateRelationship>) list) {
                            if (logger.isTraceEnabled())
                                logger.trace("Found relationship " + relation.getName());
                            if (checkFilter(relation, filter)) {
                                addTableItem(table, relation, values);
                            }
                        }
                    } else
                        if (list.get(0) instanceof IDiagramModel) {
                            for (IDiagramModel view : (EList<IDiagramModel>) list) {
                                if (logger.isTraceEnabled())
                                    logger.trace("Found diagram model " + view.getName());
                                if (checkFilter(view, filter)) {
                                    addTableItem(table, view, values);
                                }
                            }
                        } else {
                        	if ( !(list.get(0) instanceof IDiagramModelConnection) ) {
                        		throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to generate lines for unknown object class \"" + list.get(0).getClass().getSimpleName() + "\"");
                        	}
                        }
    }
    */

    /**
     * Adds a line (TableItem) in the Table<br>
     * 
     * @param table
     *            the table in which create the lines
     * @param jsonArray
     *            the array of JSONObjects that contain the values to insert
     *            (one per column)
     */
    /*
    private void addTableItem(Table table, EObject eObject, JSONArray jsonArray) throws RuntimeException {
        TableItem tableItem = new TableItem(table, SWT.NONE);
        EObject referedEObject;
        String unscoppedVariable;

        // we need to store the widgets to retreive them later on
        TableEditor[] editors = new TableEditor[jsonArray.size()];

        logger.trace("   adding line for " + eObject.getClass().getSimpleName() + " \"" + (((INameable) eObject).getName() == null ? "" : ((INameable) eObject).getName()) + "\"");

        for (int columnNumber = 0; columnNumber < jsonArray.size(); ++columnNumber) {
            TableColumn tableColumn = table.getColumn(columnNumber);
            String variableName = (String) jsonArray.get(columnNumber);
            String itemText = FormVariable.expand(variableName, eObject); 

            TableEditor editor;
            switch (((String)tableColumn.getData("class")).toLowerCase()) {
                case "label":
                    logger.trace("      adding label cell with value \"" + itemText + "\"");
                    editor = new TableEditor(table);
                    Label label = new Label(table, SWT.WRAP);
                    label.setText(itemText);
                    editor.grabHorizontal = true;
                    editor.setEditor(label, tableItem, columnNumber);
                    label.setAlignment((int)tableColumn.getData("alignment"));
                    if ( tableColumn.getData("foreground") != null ) {
                        label.setForeground((Color)tableColumn.getData("foreground"));
                    }
                    if ( tableColumn.getData("background") != null ) {
                        label.setBackground((Color)tableColumn.getData("background"));
                    }
                    editors[columnNumber] = editor;
                    // We reference the variable and the control to the eObject that the variable refers to
                    referedEObject = FormVariable.getReferedEObject(variableName, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, eObject);
                    formVarList.set(referedEObject, unscoppedVariable, label);
                    break;

                case "text":
                    editor = new TableEditor(table);
                    StyledText text = new StyledText(table, SWT.WRAP);
                    if ( !FormPlugin.isEmpty((String)tableColumn.getData("default")) && (itemText.isEmpty()) || (boolean)tableColumn.getData("forceDefault") ) {
                        itemText = FormVariable.expand((String)tableColumn.getData("default"), eObject);
                    }
                    logger.trace("      adding text cell with value \"" + itemText + "\"");
                    text.setAlignment((int)tableColumn.getData("alignment"));
                    text.setText(itemText);
                    text.setToolTipText((String)tableColumn.getData("tooltip"));
                    
                    if ( tableColumn.getData("foreground") != null ) {
                        text.setForeground((Color)tableColumn.getData("foreground"));
                    }
                    if ( tableColumn.getData("background") != null ) {
                        text.setBackground((Color)tableColumn.getData("background"));
                    }
                    
                    // We reference the variable and the control to the eObject that the variable refers to
                    referedEObject = FormVariable.getReferedEObject(variableName, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, eObject);
                    text.setData("eObject", referedEObject);
                    text.setData("variable", unscoppedVariable);
                    text.setData("whenEmpty", tableColumn.getData("whenEmpty"));
                    formVarList.set(referedEObject, unscoppedVariable, text);

                    if ( !FormPlugin.isEmpty((String)table.getColumn(columnNumber).getData("tooltip")) ) {
                        text.setToolTipText(FormVariable.expand((String) table.getColumn(columnNumber).getData("tooltip"), eObject));
                    } else {
                        if ( !FormPlugin.isEmpty((String)table.getColumn(columnNumber).getData("regexp")) ) {
                            String regex = (String) table.getColumn(columnNumber).getData("regexp");
                            text.setData("pattern", Pattern.compile(regex));
                            text.setToolTipText("Your text should match the following regex :\n" + regex);
                        }
                    }
                    
                    editor.grabHorizontal = true;
                    editor.setEditor(text, tableItem, columnNumber);
                    editors[columnNumber] = editor;
                    
                    text.addModifyListener(textModifyListener);
                    break;

                case "combo":
                    editor = new TableEditor(table);
                    CCombo combo = new CCombo(table, SWT.NONE);
                    if (itemText.isEmpty()) {
                        if ( !FormPlugin.isEmpty((String)tableColumn.getData("default")) )
                            itemText = (String)tableColumn.getData("default");
                    }
                    logger.trace("      adding combo cell with value \"" + itemText + "\"");
                    combo.setText(itemText);
                    combo.setItems((String[])tableColumn.getData("values"));
                    combo.setToolTipText((String)tableColumn.getData("tooltip"));
                    
                    if ( tableColumn.getData("foreground") != null ) {
                        combo.setForeground((Color)tableColumn.getData("foreground"));
                    }
                    if ( tableColumn.getData("background") != null ) {
                        combo.setBackground((Color)tableColumn.getData("background"));
                    }
                    
                    // We reference the variable and the control to the eObject that the variable refers to
                    referedEObject = FormVariable.getReferedEObject(variableName, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, eObject);
                    combo.setData("eObject", referedEObject);
                    combo.setData("variable", unscoppedVariable);
                    combo.setData("whenEmpty", tableColumn.getData("whenEmpty"));
                    formVarList.set(referedEObject, unscoppedVariable, combo);
                    Boolean editable = (Boolean)tableColumn.getData("editable");
                    combo.setEditable(editable != null && editable);

                    if ( !FormPlugin.isEmpty((String)tableColumn.getData("tooltip")) ) {
                        combo.setToolTipText(FormVariable.expand((String)tableColumn.getData("tooltip"), eObject));
                    }

                    editor.grabHorizontal = true;
                    editor.setEditor(combo, tableItem, columnNumber);
                    editors[columnNumber] = editor;
                    combo.addModifyListener(textModifyListener);
                    break;

                case "check":
                    editor = new TableEditor(table);
                    Button check = new Button(table, SWT.CHECK);
                    check.pack();
                    editor.minimumWidth = check.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    check.setAlignment((int)tableColumn.getData("alignment"));
                    
                    // We reference the variable and the control to the eObject that the variable refers to
                    referedEObject = FormVariable.getReferedEObject(variableName, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, eObject);
                    check.setData("eObject", referedEObject);
                    check.setData("variable", unscoppedVariable);
                    check.setData("whenEmpty", tableColumn.getData("whenEmpty"));
                    formVarList.set(referedEObject, unscoppedVariable, check);
                    
                    if ( tableColumn.getData("foreground") != null ) {
                        check.setForeground((Color)tableColumn.getData("foreground"));
                    }
                    if ( tableColumn.getData("background") != null ) {
                        check.setBackground((Color)tableColumn.getData("background"));
                    }
                    
                    String[] values = (String[])tableColumn.getData("values");
                    String defaultValue = (String)tableColumn.getData("default");
                    
                    if ( values == null || values.length == 0 ) {
                        check.setData("values", null);
                        check.setSelection(Boolean.valueOf((itemText.isEmpty() || (boolean)tableColumn.getData("forceDefault"))?defaultValue:itemText));
                        logger.trace("      adding check cell with value \"" + Boolean.valueOf((itemText.isEmpty() || (boolean)tableColumn.getData("forceDefault"))?defaultValue:itemText) + "\"");
                    } else {
                        check.setData("values", values);
                        check.setSelection(values[0].equals((itemText.isEmpty() || (boolean)tableColumn.getData("forceDefault"))?defaultValue:itemText));
                        logger.trace("      adding check cell with value \"" + values[0].equals((itemText.isEmpty() || (boolean)tableColumn.getData("forceDefault"))?defaultValue:itemText) + "\"");
                    }
                    check.pack();

                    if ( !FormPlugin.isEmpty((String)tableColumn.getData("tooltip")) ) {
                        check.setToolTipText(FormVariable.expand((String)tableColumn.getData("tooltip"), eObject));
                    }

                    check.addSelectionListener(checkButtonSelectionListener);

                    editor.setEditor(check, tableItem, columnNumber);
                    editors[columnNumber] = editor;
                    break;

                default:
                    throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to add table item for unknown object class \"" + ((String)tableColumn.getData("class")) + "\"");
            }
        }
        tableItem.setData("editors", editors);
    }
    */

    /**
     * Shows up an on screen popup displaying the message and wait for the user
     * to click on the "OK" button
     */
    public static void popup(Level level, String msg) {
        popup(level, msg, null);
    }

    // the popupMessage is a class variable because it will be used in an
    // asyncExec() method.
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

    ModifyListener textModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
        	if ( logger.isTraceEnabled() ) logger.trace("calling textModifyListener");
        	
            updateWidget((Control)e.widget);
        }
    };
    
    SelectionListener checkButtonSelectionListener = new SelectionListener(){
        @Override public void widgetSelected(SelectionEvent e) {
            updateWidget((Control)e.widget);
        }

        @Override public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    };
    
    private void updateWidget(Control control) {
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
                            button.setSelection(FormPlugin.areEqual(content, values[1]));       // any other value than values[1] implies the button is unchecked
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

    private void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");
        formDialog.dispose();
    }

    private void ok() {
        if (logger.isDebugEnabled())
            logger.debug("Ok button selected by user.");
        CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
        try {
            for (Control control : formDialog.getChildren()) {
                save(control);
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

        CommandStack stack = (CommandStack) model.getAdapter(CommandStack.class);
        stack.execute(compoundCommand);

        formDialog.dispose();
    }

    private void save(Control control) throws RuntimeException {
        switch (control.getClass().getSimpleName()) {
            case "Label":
                break;					// nothing to save here

            case "Button":
            case "CCombo":
            case "StyledText":
                if (control.getData("variable") != null)
                    do_save(control);
                break;

            case "TabFolder":
                for (Control child : ((TabFolder) control).getChildren()) {
                    save(child);
                }
                break;
            case "Table":
                for (TableItem item : ((Table) control).getItems()) {
                    for (TableEditor editor : (TableEditor[]) item.getData("editors")) {
                        if (editor != null)
                            save(editor.getEditor());
                    }
                }
                break;
            case "Composite":
                for (Control child : ((Composite) control).getChildren()) {
                    save(child);
                }
                break;

            default:
                throw new RuntimeException("Save : do not know how to save a " + control.getClass().getSimpleName());
        }
    }

    private void do_save(Control control) throws RuntimeException {
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

            switch (whenEmpty) {
                case "ignore":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : ignored.");
                    break;
                case "create":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : creating property.");
                    FormVariable.setVariable(unscoppedVariable, globalVariableSeparator, "", referedEObject);
                    break;
                case "delete":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : deleting property.");
                    FormVariable.setVariable(unscoppedVariable, globalVariableSeparator, null, referedEObject);
                    break;
            }
        } else {
            if (logger.isTraceEnabled())
                logger.trace("   value is not empty.");
            FormVariable.setVariable(unscoppedVariable, globalVariableSeparator, value, referedEObject);
        }
    }

    @SuppressWarnings("deprecation")
    private void exportToExcel() {
    /*
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
                for (TabItem tabItem : tabFolder.getItems()) {
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
                            } else
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
                } catch (IOException e) {
                    popup(Level.ERROR, "Failed to launch Excel.", e);
                }
            }
        }
        */
    }

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



    public static boolean inArray(String[] stringArray, String string) {
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
}
