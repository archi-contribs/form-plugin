package org.archicontribs.form;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
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
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
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
public class FormGenerate extends Dialog {
    private static final FormLogger logger            = new FormLogger(FormGenerate.class);
    
    protected static Display display           = Display.getDefault();

    private Shell            formDialog        = null;
    private Shell            propertiesDialog  = null;
    private TabFolder        tabFolder         = null;
    private Tree             tree              = null;
    private Composite        formProperties    = null;
    private Composite        tabProperties     = null;
    private Composite        labelPropeties    = null;
    private Composite        textProperties    = null;
    private Composite        comboProperties   = null;
    private Composite        checkProperties   = null;
    private Composite        tableProperties   = null;
    private Composite        columnProperties  = null;
    private Composite        LineProperties    = null;
    
    private String           variableSeparator       = null;
    private String           globalWhenEmpty         = null;

    public FormGenerate(String configFilename, JSONObject json) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        try {
            createForm(json);
        } catch (IOException e) {
            popup(Level.ERROR, "I/O Error while reading configuration file \"" + configFilename + "\"", e);
            close();
            return;
        } catch (ParseException e) {
            popup(Level.ERROR, "Parsing error while reading configuration file \"" + configFilename + "\"", e);
            close();
            return;
        } catch (ClassCastException e) {
            popup(Level.ERROR, "Wrong key type in the configuration file \"" + configFilename + "\"", e);
            close();
            return;
        } catch (RuntimeException e) {
            popup(Level.ERROR, "Please check your configuration file \"" + configFilename +"\"", e);
            close();
            return;
        }

        formDialog.open();
        formDialog.layout();
        
        propertiesDialog.open();
        propertiesDialog.layout();
    }

    private final String[] whenEmptyValidStrings   = new String[] { "ignore", "create", "delete" };

    /**
     * Parses the configuration file and create the corresponding graphical controls
     */
    private void createForm(JSONObject form) throws IOException, ParseException, RuntimeException {
        // we gather the config file content
        String formName = FormDialog.getString(form, "name", FormDialog.defaultDialogName);
        FormPosition.setFormName(formName);
        variableSeparator = FormDialog.getString(form, "variableSeparator", FormDialog.defaultVariableSeparator);
        int dialogWidth = FormDialog.getInt(form, "width", FormDialog.defaultDialogWidth);
        int dialogHeight = FormDialog.getInt(form, "height", FormDialog.defaultDialogHeight);
        int dialogSpacing = FormDialog.getInt(form, "spacing", FormDialog.defaultDialogSpacing);
        String dialogBackground = FormDialog.getString(form, "background", FormDialog.defaultDialogBackground);
        int buttonWidth = FormDialog.getInt(form, "buttonWidth", FormDialog.defaultButtonWidth);
        int buttonHeight = FormDialog.getInt(form, "buttonHeight", FormDialog.defaultButtonHeight);
        String buttonOkText = FormDialog.getString(form, "buttonOk", FormDialog.defaultButtonOkText);
        String buttonCancelText = FormDialog.getString(form, "buttonCancel", FormDialog.defaultButtonCancelText);
        String buttonExportText = FormDialog.getString(form, "buttonExport", FormDialog.defaultButtonExportText);
        globalWhenEmpty = FormDialog.getString(form, "whenEmpty", null);

        if (logger.isTraceEnabled()) {
            logger.trace("   name = " + FormDialog.debugValue(formName, FormDialog.defaultDialogName));
            logger.trace("   variableSeparator = " + FormDialog.debugValue(variableSeparator, FormDialog.defaultVariableSeparator));
            logger.trace("   width = " + FormDialog.debugValue(dialogWidth, FormDialog.defaultDialogWidth));
            logger.trace("   height = " + FormDialog.debugValue(dialogHeight, FormDialog.defaultDialogHeight));
            logger.trace("   spacing = " + FormDialog.debugValue(dialogSpacing, FormDialog.defaultDialogSpacing));
            logger.trace("   background = " + FormDialog.debugValue(dialogBackground, FormDialog.defaultDialogBackground));
            logger.trace("   buttonWidth = " + FormDialog.debugValue(buttonWidth, FormDialog.defaultButtonWidth));
            logger.trace("   buttonHeight = " + FormDialog.debugValue(buttonHeight, FormDialog.defaultButtonHeight));
            logger.trace("   refers = " + FormDialog.debugValue(FormDialog.getString(form, "refers", "selected"), "selected"));       // used in FormMenu class but deserves a debug line
            logger.trace("   ok = " + FormDialog.debugValue(buttonOkText, FormDialog.defaultButtonOkText));
            logger.trace("   cancel = " + FormDialog.debugValue(buttonCancelText, FormDialog.defaultButtonCancelText));
            logger.trace("   whenEmpty = " + FormDialog.debugValue(globalWhenEmpty, null));
        }
        
        if (globalWhenEmpty != null) {
            globalWhenEmpty = globalWhenEmpty.toLowerCase();
            if (!FormDialog.inArray(whenEmptyValidStrings, globalWhenEmpty))
                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + globalWhenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
        }
        
        // we create the properties and form dialogs
        propertiesDialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        propertiesDialog.setSize(600, 400);
        propertiesDialog.setLayout(new FormLayout());
        
        tree = new Tree(propertiesDialog, SWT.BORDER);
        tree.setHeaderVisible(false);
        tree.setLinesVisible(true);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(50, -5);
        fd.bottom = new FormAttachment(100, -10);
        tree.setLayoutData(fd);
        tree.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                // we hide the current composite and show up the composite linked to the selected treeItem
            }
        });
        
        TreeItem formTreeItem = new TreeItem(tree, SWT.NONE);
        formTreeItem.setText("Form: "+formName);
        
        
        formDialog = new Shell(propertiesDialog, SWT.TITLE | SWT.BORDER);
        formDialog.setText(formName);
        formDialog.setData("jsonObject", form);

        int tabFolderWidth = dialogWidth - dialogSpacing * 2;
        int tabFolderHeight = dialogHeight - dialogSpacing * 3 - buttonHeight;

        formDialog.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - dialogWidth) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogHeight) / 4, dialogWidth, dialogHeight);
        // we resize the dialog because we want the width and height to be the
        // client's area width and height
        Rectangle area = formDialog.getClientArea();
        formDialog.setSize(dialogWidth * 2 - area.width, dialogHeight * 2 - area.height);

        if (dialogBackground != null) {
            String[] colorArray = dialogBackground.split(",");
            formDialog.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        tabFolder = new TabFolder(formDialog, SWT.BORDER);
        tabFolder.setBounds(dialogSpacing, dialogSpacing, tabFolderWidth, tabFolderHeight);

        Button cancelButton = new Button(formDialog, SWT.NONE);
        cancelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth, tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
        cancelButton.setText(buttonCancelText);
        cancelButton.setEnabled(true);

        Button okButton = new Button(formDialog, SWT.NONE);
        okButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth * 2 - dialogSpacing,
                tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
        okButton.setText(buttonOkText);
        okButton.setEnabled(true);

        // createTabs(form, tabFolder);

        // If there is at least one Excel sheet specified, then we show up the
        // "export to Excel" button
        /*
        if (!excelSheets.isEmpty()) {
            Button exportToExcelButton = new Button(dialog, SWT.NONE);
            exportToExcelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth * 3 - dialogSpacing * 2,
                    tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
            exportToExcelButton.setText(buttonExportText);
            exportToExcelButton.setEnabled(true);
        }
        */
        
        // create the composites with the properties for the following controls:
        //    form
        //    tab
        //    label
        //    text
        //    combo
        //    table
        //    columns
        //    lines
        
        tree.setSelection(formTreeItem);
        tree.notifyListeners(SWT.Selection, new Event());        // shows up the form's properties
    }

    /**
     * Creates the dialog tabItems<br>
     * <br>
     * called by the createContents() method
     */
    private void createTabs(JSONObject form, TabFolder tabFolder) throws RuntimeException {
        // we iterate over the "tabs" array attributes
        JSONArray tabs = getJSONArray(form, "tabs");

        @SuppressWarnings("unchecked")
        Iterator<JSONObject> tabsIterator = tabs.iterator();
        while (tabsIterator.hasNext()) {
            // we create one TabItem per array item
            JSONObject tab = tabsIterator.next();

            String tabName = getString(tab, "name", defaultName);

            FormPosition.setTabName(tabName);

            String tabText = FormVariable.expand(tabName, variableSeparator, selectedObject);

            if (logger.isDebugEnabled())
                logger.debug("Creating tab " + debugValue(tabText, defaultName));

            String tabBackground = getString(tab, "background", defaultTabBackground);

            if (logger.isTraceEnabled())
                logger.trace("   background = " + debugValue(tabBackground, defaultTabBackground));

            TabItem tabItem = new TabItem(tabFolder, SWT.MULTI);
            tabItem.setText(tabText);
            Composite composite = new Composite(tabFolder, SWT.NONE);
            tabItem.setControl(composite);

            if (tabBackground != null) {
                String[] colorArray = tabBackground.split(",");
                composite.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
            }

            createControls(tab, composite);
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
     * 
     * @param tab
     *            The JSON object to parse
     * @param composite
     *            The composite where the control will be created
     */
    private void createControls(JSONObject tab, Composite composite) throws RuntimeException {
        // we iterate over the "controls" entries
        @SuppressWarnings("unchecked")
        Iterator<JSONObject> objectsIterator = getJSONArray(tab, "controls").iterator();
        while (objectsIterator.hasNext()) {
            JSONObject jsonObject = objectsIterator.next();

            switch (getString(jsonObject, "class").toLowerCase()) {
                case "check":
                    createCheck(jsonObject, composite);
                    break;
                case "combo":
                    createCombo(jsonObject, composite);
                    break;
                case "label":
                    createLabel(jsonObject, composite);
                    break;
                case "table":
                    createTable(jsonObject, composite);
                    break;
                case "text":
                    createText(jsonObject, composite);
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
     * 
     * @param jsonObject
     *            the JSON object to parse
     * @param composite
     *            the composite where the control will be created
     */
    private Label createLabel(JSONObject jsonObject, Composite composite) {
        String labelName = getString(jsonObject, "text", "label");
        String labelText = FormVariable.expand(labelName, variableSeparator, selectedObject);

        if (logger.isDebugEnabled())
            logger.debug("   Creating label control " + debugValue(labelText, "label"));

        Label label = new Label(composite, SWT.WRAP);		                  // we create the control at the very beginning because we need its default size which is dependent on its content
        label.setText(labelText);
        label.pack();

        int x = getInt(jsonObject, "x", 0);
        int y = getInt(jsonObject, "y", 0);
        int width = getInt(jsonObject, "width", label.getSize().x);
        int height = getInt(jsonObject, "height", label.getSize().y);
        String controlName = getString(jsonObject, "name", labelName);
        FormPosition.setControlName(controlName); FormPosition.setControlClass("label");
        String background = getString(jsonObject, "background", null);
        String foreground = getString(jsonObject, "foreground", null);
        String tooltip = getString(jsonObject, "tooltip", null);
        String fontName = getString(jsonObject, "fontName", null);
        int fontSize = getInt(jsonObject, "fontSize", label.getFont().getFontData()[0].getHeight());
        boolean fontBold = getBoolean(jsonObject, "fontBold", false);
        boolean fontItalic = getBoolean(jsonObject, "fontItalic", false);
        String alignment = getString(jsonObject, "alignment", "left");
        String excelSheet = getString(jsonObject, "excelSheet", null);
        String excelCell = getString(jsonObject, "excelCell", null);
        String excelCellType = getString(jsonObject, "excelCellType", "string").toLowerCase();
        String excelDefault = getString(jsonObject, "excelDefault", "blank").toLowerCase();

        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, label.getSize().x));
            logger.trace("      height = " + debugValue(height, label.getSize().y));
            logger.trace("      name = " + debugValue(controlName, labelName));
            logger.trace("      background = " + debugValue(background, null));
            logger.trace("      foreground = " + debugValue(foreground, null));
            logger.trace("      tooltip = " + debugValue(tooltip, null));
            logger.trace("      fontName = " + debugValue(fontName, null));
            logger.trace("      fontSize = " + debugValue(fontSize, label.getFont().getFontData()[0].getHeight()));
            logger.trace("      fontBold = " + debugValue(fontBold, false));
            logger.trace("      fontItalic = " + debugValue(fontItalic, false));
            logger.trace("      alignment = "+debugValue(alignment, "left"));
            logger.trace("      excelSheet = " + debugValue(excelSheet, null));
            logger.trace("      excelCell = " + debugValue(excelCell, null));
            logger.trace("      excelCellType = " + debugValue(excelCellType, "string"));
            logger.trace("      excelDefault = " + debugValue(excelDefault, "blank"));
        }

        label.setLocation(x, y);
        label.setSize(width, height);

        if (background != null) {
            String[] colorArray = background.split(",");
            label.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        } else {
            label.setBackground(composite.getBackground());
        }

        if (foreground != null) {
            String[] colorArray = foreground.split(",");
            label.setForeground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (fontName != null) {
            int style = SWT.NORMAL;
            if (fontBold)
                style |= SWT.BOLD;
            if (fontItalic)
                style |= SWT.ITALIC;
            label.setFont(new Font(label.getDisplay(), fontName, fontSize, style));
        } else {
            if ((fontSize != label.getFont().getFontData()[0].getHeight()) || fontBold || fontItalic) {
                int style = SWT.NORMAL;
                if (fontBold)
                    style |= SWT.BOLD;
                if (fontItalic)
                    style |= SWT.ITALIC;
                label.setFont(FontDescriptor.createFrom(label.getFont()).setHeight(fontSize).setStyle(style).createFont(label.getDisplay()));
            }
        }
        
        switch ( alignment.toLowerCase() ) {
            case "right" :
                label.setAlignment(SWT.RIGHT);
                break;
            case "left" :
                label.setAlignment(SWT.LEFT);
                break;
            case "center":
                label.setAlignment(SWT.CENTER);
                break;
            default:
                throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
        }

        if ( !FormPlugin.areEqual(excelCellType, "string") && !FormPlugin.areEqual(excelCellType, "numeric") && !FormPlugin.areEqual(excelCellType, "boolean") && !FormPlugin.areEqual(excelCellType, "formula") )
            throw new RuntimeException(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value, must be \"string\", \"numeric\", \"boolean\" or \"formula\"."); 
        
        if ( !FormPlugin.areEqual(excelDefault, "blank") && !FormPlugin.areEqual(excelDefault, "zero") && !FormPlugin.areEqual(excelDefault, "delete") )
            throw new RuntimeException(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value, must be \"blank\", \"zero\" or \"delete\"."); 

        if (excelSheet != null) {
            excelSheets.add(excelSheet);
            label.setData("excelSheet", excelSheet);
            label.setData("excelCell", excelCell);
            label.setData("excelCellType", excelCellType.toLowerCase());
            label.setData("excelDefault", excelDefault.toLowerCase());
        }

        if (tooltip != null) {
            label.setToolTipText(FormVariable.expand(tooltip, variableSeparator, selectedObject));
        }

        // We reference the variable and the control to the eObject that the variable refers to
        formVarList.set(FormVariable.getReferedEObject(labelName, variableSeparator, selectedObject), FormVariable.getUnscoppedVariable(labelName,variableSeparator, selectedObject), label);

        return label;
    }

    /**
     * Create a text control<br>
     * <br>
     * called by the createObjects() method
     * 
     * @param jsonObject
     *            the JSON object to parse
     * @param composite
     *            the composite where the control will be created
     */
    private StyledText createText(JSONObject jsonObject, Composite composite) throws RuntimeException {
        String variableName = getString(jsonObject, "variable");
        String defaultText = getString(jsonObject, "default", null);
        boolean forceDefault = getBoolean(jsonObject, "forceDefault", false);

        String variableValue = FormVariable.expand(variableName, variableSeparator, selectedObject);

        if (variableValue.isEmpty() || forceDefault)
            variableValue = FormVariable.expand(defaultText, variableSeparator, selectedObject);

        StyledText text = new StyledText(composite, SWT.WRAP | SWT.BORDER);		                   // we create the control at the very beginning because we need its default size which is dependent on its content

        text.setText(variableValue);
        text.pack();

        int x = getInt(jsonObject, "x", 0);
        int y = getInt(jsonObject, "y", 0);
        int width = getInt(jsonObject, "width", text.getSize().x);
        int height = getInt(jsonObject, "height", text.getSize().y);
        String controlName = getString(jsonObject, "name", variableName);
        FormPosition.setControlName(controlName);
        FormPosition.setControlClass("text");
        String background = getString(jsonObject, "background", null);
        String foreground = getString(jsonObject, "foreground", null);
        String regex = getString(jsonObject, "regexp", null);
        String tooltip = getString(jsonObject, "tooltip", null);
        String fontName = getString(jsonObject, "fontName", null);
        int fontSize = getInt(jsonObject, "fontSize", text.getFont().getFontData()[0].getHeight());
        boolean fontBold = getBoolean(jsonObject, "fontBold", false);
        boolean fontItalic = getBoolean(jsonObject, "fontItalic", false);
        String alignment = getString(jsonObject, "alignment", "left");
        boolean editable = getBoolean(jsonObject, "editable", true);
        String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
        String excelSheet = getString(jsonObject, "excelSheet", null);
        String excelCell = getString(jsonObject, "excelCell", null);
        String excelCellType = getString(jsonObject, "excelCellType", "string");
        String excelDefault = getString(jsonObject, "excelDefault", "blank");

        if (logger.isDebugEnabled())
            logger.debug("   Creating Text control \"" + variableName + "\"");
        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, text.getSize().x));
            logger.trace("      height = " + debugValue(height, text.getSize().y));
            logger.trace("      name = " + debugValue(controlName, variableName));
            logger.trace("      variable = " + variableName);
            logger.trace("      default = " + debugValue(defaultText, ""));
            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
            logger.trace("      background = " + debugValue(background, null));
            logger.trace("      foreground = " + debugValue(foreground, null));
            logger.trace("      regexp = " + debugValue(regex, null));
            logger.trace("      tooltip = " + debugValue(tooltip, null));
            logger.trace("      fontName = " + debugValue(fontName, null));
            logger.trace("      fontSize = " + debugValue(fontSize, text.getFont().getFontData()[0].getHeight()));
            logger.trace("      fontBold = " + debugValue(fontBold, false));
            logger.trace("      fontItalic = " + debugValue(fontItalic, false));
            logger.trace("      alignment = "+debugValue(alignment, "left"));
            logger.trace("      editable = " + debugValue(editable, true));
            logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
            logger.trace("      excelSheet = " + debugValue(excelSheet, null));
            logger.trace("      excelCell = " + debugValue(excelCell, null));
            logger.trace("      excelCellType = " + debugValue(excelCellType, "string"));
            logger.trace("      excelDefault = " + debugValue(excelDefault, "blank"));
        }

        if (whenEmpty != null) {
            whenEmpty = whenEmpty.toLowerCase();
            if (!inArray(whenEmptyValidStrings, whenEmpty))
                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
        }

        text.setLocation(x, y);
        text.setSize(width, height);
        text.setEditable(editable);

        if (background != null) {
            String[] colorArray = background.split(",");
            text.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (foreground != null) {
            String[] colorArray = foreground.split(",");
            text.setForeground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (fontName != null) {
            int style = SWT.NORMAL;
            if (fontBold)
                style |= SWT.BOLD;
            if (fontItalic)
                style |= SWT.ITALIC;
            text.setFont(new Font(text.getDisplay(), fontName, fontSize, style));
        } else {
            if ((fontSize != text.getFont().getFontData()[0].getHeight()) || fontBold || fontItalic) {
                int style = SWT.NORMAL;
                if (fontBold)
                    style |= SWT.BOLD;
                if (fontItalic)
                    style |= SWT.ITALIC;
                text.setFont(FontDescriptor.createFrom(text.getFont()).setHeight(fontSize).setStyle(style).createFont(text.getDisplay()));
            }
        }
        
        switch ( alignment.toLowerCase() ) {
            case "right" :
                text.setAlignment(SWT.RIGHT);
                break;
            case "left" :
                text.setAlignment(SWT.LEFT);
                break;
            case "center":
                text.setAlignment(SWT.CENTER);
                break;
            default:
                throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
        }
        
        // We reference the variable and the control to the eObject that the variable refers to
        EObject referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, selectedObject);
        String unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, selectedObject);
        text.setData("variable", unscoppedVariable);
        text.setData("eObject", referedEObject);
        text.setData("whenEmpty", whenEmpty);
        formVarList.set(referedEObject, unscoppedVariable, text);

        if (excelSheet != null) {
            excelSheets.add(excelSheet);
            text.setData("excelSheet", excelSheet);
            text.setData("excelCell", excelCell);
            text.setData("excelCellType", excelCellType.toLowerCase());
            text.setData("excelDefault", excelDefault.toLowerCase());
        }

        if (tooltip != null) {
            text.setToolTipText(FormVariable.expand(tooltip, variableSeparator, selectedObject));
        } else
            if (regex != null) {
                text.setData("pattern", Pattern.compile(regex));
                text.setToolTipText("Your text should match the following regex :\n" + regex);
            }

        text.addModifyListener(textModifyListener);
        return text;
    }

    /**
     * Create a Combo control<br>
     * <br>
     * called by the createObjects() method
     * 
     * @param jsonObject
     *            the JSON object to parse
     * @param composite
     *            the composite where the control will be created
     */
    private CCombo createCombo(JSONObject jsonObject, Composite composite) throws RuntimeException {
        @SuppressWarnings("unchecked")
        String[] values = (String[]) (getJSONArray(jsonObject, "values")).toArray(new String[0]);

        String variableName = getString(jsonObject, "variable");
        String defaultText = getString(jsonObject, "default", "");
        boolean forceDefault = getBoolean(jsonObject, "forceDefault", false);

        String variableValue = FormVariable.expand(variableName, variableSeparator, selectedObject);

        if (variableValue.isEmpty() || forceDefault)
            variableValue = FormVariable.expand(defaultText, variableSeparator, selectedObject);

        CCombo combo = new CCombo(composite, SWT.NONE);						// we create the control at the very beginning because we need its default size which is dependent on its content

        combo.setItems(values);
        combo.setText(variableValue);
        combo.pack();

        int x = getInt(jsonObject, "x", 0);
        int y = getInt(jsonObject, "y", 0);
        int width = getInt(jsonObject, "width", combo.getSize().x);
        int height = getInt(jsonObject, "height", combo.getSize().y);
        String controlName = getString(jsonObject, "name", variableName);
        FormPosition.setControlName(controlName);
        FormPosition.setControlClass("combo");
        String background = getString(jsonObject, "background", null);
        String foreground = getString(jsonObject, "foreground", null);
        String tooltip = getString(jsonObject, "tooltip", null);
        String fontName = getString(jsonObject, "fontName", null);
        int fontSize = getInt(jsonObject, "fontSize", combo.getFont().getFontData()[0].getHeight());
        boolean fontBold = getBoolean(jsonObject, "fontBold", false);
        boolean fontItalic = getBoolean(jsonObject, "fontItalic", false);
        boolean editable = getBoolean(jsonObject, "editable", true);
        String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
        String excelSheet = getString(jsonObject, "excelSheet", null);
        String excelCell = getString(jsonObject, "excelCell", null);
        String excelCellType = getString(jsonObject, "excelCellType", "string");
        String excelDefault = getString(jsonObject, "excelDefault", "blank");

        if (logger.isDebugEnabled())
            logger.debug("   Creating combo \"" + variableName + "\"");
        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + x);
            logger.trace("      y = " + y);
            logger.trace("      width = " + debugValue(width, combo.getSize().x));
            logger.trace("      height = " + debugValue(height, combo.getSize().y));
            logger.trace("      name = " + debugValue(controlName, variableName));
            logger.trace("      background = " + debugValue(background, null));
            logger.trace("      foreground = " + debugValue(foreground, null));
            logger.trace("      values = " + values);
            logger.trace("      default = " + debugValue(defaultText, ""));
            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
            logger.trace("      tooltip = " + debugValue(tooltip, null));
            logger.trace("      fontName = " + debugValue(fontName, null));
            logger.trace("      fontSize = " + debugValue(fontSize, combo.getFont().getFontData()[0].getHeight()));
            logger.trace("      fontBold = " + debugValue(fontBold, false));
            logger.trace("      fontItalic = " + debugValue(fontItalic, false));
            logger.trace("      editable = " + debugValue(editable, true));
            logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
            logger.trace("      excelSheet = " + debugValue(excelSheet, null));
            logger.trace("      excelCell = " + debugValue(excelCell, null));
            logger.trace("      excelCellType = " + debugValue(excelCellType, "string"));
            logger.trace("      excelDefault = " + debugValue(excelDefault, "blank"));
        }

        if (whenEmpty != null) {
            whenEmpty = whenEmpty.toLowerCase();
            if (!inArray(whenEmptyValidStrings, whenEmpty))
                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
        }

        combo.setLocation(x, y);
        combo.setSize(width, height);
        combo.setEditable(editable);

        if (background != null) {
            String[] colorArray = background.split(",");
            combo.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (foreground != null) {
            String[] colorArray = foreground.split(",");
            combo.setForeground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (fontName != null) {
            int style = SWT.NORMAL;
            if (fontBold)
                style |= SWT.BOLD;
            if (fontItalic)
                style |= SWT.ITALIC;
            combo.setFont(new Font(combo.getDisplay(), fontName, fontSize, style));
        } else {
            if ((fontSize != combo.getFont().getFontData()[0].getHeight()) || fontBold || fontItalic) {
                int style = SWT.NORMAL;
                if (fontBold)
                    style |= SWT.BOLD;
                if (fontItalic)
                    style |= SWT.ITALIC;
                combo.setFont(FontDescriptor.createFrom(combo.getFont()).setHeight(fontSize).setStyle(style).createFont(combo.getDisplay()));
            }
        }

        // We reference the variable and the control to the eObject that the variable refers to
        EObject referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, selectedObject);
        String unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, selectedObject);
        combo.setData("variable", unscoppedVariable);
        combo.setData("eObject", referedEObject);
        combo.setData("whenEmpty", whenEmpty);
        formVarList.set(referedEObject, unscoppedVariable, combo);

        if (excelSheet != null) {
            excelSheets.add(excelSheet);
            combo.setData("excelSheet", excelSheet);
            combo.setData("excelCell", excelCell);
            combo.setData("excelCellType", excelCellType.toLowerCase());
            combo.setData("excelDefault", excelDefault.toLowerCase());
        }

        if (tooltip != null) {
            combo.setToolTipText(FormVariable.expand(tooltip, variableSeparator, selectedObject));
        }
        
        combo.addModifyListener(textModifyListener);
        return combo;
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
    private Button createCheck(JSONObject jsonObject, Composite composite) throws RuntimeException {
        @SuppressWarnings("unchecked")
        String[] values = (String[]) (getJSONArray(jsonObject, "values")).toArray(new String[0]);

        String variableName = getString(jsonObject, "variable");
        String value = FormVariable.expand(variableName, variableSeparator, selectedObject);
        String defaultValue = getString(jsonObject, "default", "");
        boolean forceDefault = getBoolean(jsonObject, "forceDefault", false);

        Button check = new Button(composite, SWT.CHECK);
        if ( values == null || values.length == 0 ) {
            check.setData("values", null);
            check.setSelection(Boolean.valueOf((value.isEmpty() || forceDefault)?defaultValue:value));
        } else {
            check.setData("values", values);
            check.setSelection(values[0].equals((value.isEmpty() || forceDefault)?defaultValue:value));
        }
        check.pack();

        int x = getInt(jsonObject, "x", 0);
        int y = getInt(jsonObject, "y", 0);
        int width = getInt(jsonObject, "width", check.getSize().x);
        int height = getInt(jsonObject, "height", check.getSize().y);
        String controlName = getString(jsonObject, "name", variableName);
        FormPosition.setControlName(controlName);
        FormPosition.setControlClass("check");
        String background = getString(jsonObject, "background", null);
        String foreground = getString(jsonObject, "foreground", null);
        String tooltip = getString(jsonObject, "tooltip", null);
        String alignment = getString(jsonObject, "alignment", "left");
        String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
        String excelSheet = getString(jsonObject, "excelSheet", null);
        String excelCell = getString(jsonObject, "excelCell", null);
        String excelCellType = getString(jsonObject, "excelCellType", "string");
        String excelDefault = getString(jsonObject, "excelDefault", "blank");

        if (logger.isDebugEnabled())
            logger.debug("   Creating check \"" + variableName + "\"");
        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, check.getSize().x));
            logger.trace("      height = " + debugValue(height, check.getSize().y));
            logger.trace("      name = " + debugValue(controlName, variableName));
            logger.trace("      background = " + debugValue(background, null));
            logger.trace("      foreground = " + debugValue(foreground, null));
            logger.trace("      alignment = "+debugValue(alignment, "left"));
            logger.trace("      values = " + values);
            logger.trace("      default = " + debugValue(defaultValue, ""));
            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
            logger.trace("      tooltip = " + debugValue(tooltip, null));
            logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
            logger.trace("      excelSheet = " + debugValue(excelSheet, null));
            logger.trace("      excelCell = " + debugValue(excelCell, null));
            logger.trace("      excelCellType = " + debugValue(excelCellType, "string"));
            logger.trace("      excelDefault = " + debugValue(excelDefault, "blank"));
        }

        if (whenEmpty != null) {
            whenEmpty = whenEmpty.toLowerCase();
            if (!inArray(whenEmptyValidStrings, whenEmpty))
                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
        }

        check.setLocation(x, y);
        check.setSize(width, height);

        if (background != null) {
            String[] colorArray = background.split(",");
            check.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (foreground != null) {
            String[] colorArray = foreground.split(",");
            check.setForeground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }
        
        switch ( alignment.toLowerCase() ) {
            case "right" :
                check.setAlignment(SWT.RIGHT);
                break;
            case "left" :
                check.setAlignment(SWT.LEFT);
                break;
            case "center":
                check.setAlignment(SWT.CENTER);
                break;
            default:
                throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
        }

        // We reference the variable and the control to the eObject that the variable refers to
        EObject referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, selectedObject);
        String unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, selectedObject);
        check.setData("variable", unscoppedVariable);
        check.setData("eObject", referedEObject);
        check.setData("whenEmpty", whenEmpty);
        formVarList.set(referedEObject, unscoppedVariable, check);

        if (excelSheet != null) {
            excelSheets.add(excelSheet);
            check.setData("excelSheet", excelSheet);
            check.setData("excelCell", excelCell);
            check.setData("excelCellType", excelCellType.toLowerCase());
            check.setData("excelDefault", excelDefault.toLowerCase());
        }

        if (tooltip != null) {
            check.setToolTipText(FormVariable.expand(tooltip, variableSeparator, selectedObject));
        }

        check.addSelectionListener(checkButtonSelectionListener);
        return check;
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
    private Table createTable(JSONObject jsonObject, Composite composite) throws RuntimeException {
        int x = getInt(jsonObject, "x", 0);
        int y = getInt(jsonObject, "y", 0);
        int width = getInt(jsonObject, "width", 100);
        int height = getInt(jsonObject, "height", 50);
        String controlName = getString(jsonObject, "name", defaultName);
        FormPosition.setControlName(controlName);
        FormPosition.setControlClass("table");
        String background = getString(jsonObject, "background", null);
        String foreground = getString(jsonObject, "foreground", null);
        String tooltip = getString(jsonObject, "tooltip", null);
        String excelSheet = getString(jsonObject, "excelSheet", null);
        int excelFirstLine = getInt(jsonObject, "excelFirstLine", 1);
        int excelLastLine = getInt(jsonObject, "excelLastLine", 0);

        if (logger.isDebugEnabled())
            logger.debug("   Creating table");
        if (logger.isTraceEnabled()) {
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, 100));
            logger.trace("      height = " + debugValue(height, 50));
            logger.trace("      name = " + debugValue(controlName, defaultName));
            logger.trace("      background = " + debugValue(background, null));
            logger.trace("      foreground = " + debugValue(foreground, null));
            logger.trace("      tooltip = " + debugValue(tooltip, null));
            logger.trace("      excelSheet = " + debugValue(excelSheet, null));
            logger.trace("      excelFirstLine = " + debugValue(excelFirstLine, 1));
            logger.trace("      excelLastLine = " + debugValue(excelLastLine, 0));
        }

        Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLocation(x, y);
        table.setSize(width, height);

        if (background != null) {
            String[] colorArray = background.split(",");
            table.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (foreground != null) {
            String[] colorArray = foreground.split(",");
            table.setForeground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        if (tooltip != null) {
            table.setToolTipText(FormVariable.expand(tooltip, variableSeparator, selectedObject));
        }

        if (excelSheet != null) {
            excelSheets.add(excelSheet);
            table.setData("excelSheet", excelSheet);
            table.setData("excelFirstLine", excelFirstLine);
            table.setData("excelLastLine", excelLastLine);
        }

        // we iterate over the "columns" entries
        Iterator<JSONObject> columnsIterator = (getJSONArray(jsonObject, "columns")).iterator();
        while (columnsIterator.hasNext()) {
            JSONObject column = columnsIterator.next();

            String columnName = getString(column, "name", "(no name)");
            FormPosition.setColumnName(columnName);
            String columnClass = getString(column, "class");
            String columnTooltip = getString(column, "tooltype", null);
            int columnWidth = getInt(column, "width", (10 + columnName.length() * 8));
            String excelColumn = getString(column, "excelColumn", null);
            String excelCellType = getString(column, "excelCellType", "string");
            String excelDefault = getString(column, "excelDefault", "blank");
            background = getString(column, "background", null);
            foreground = getString(column, "foreground", null);

            if (logger.isDebugEnabled())
                logger.debug("   Creating column \"" + columnName + "\" of class \"" + columnClass + "\"");
            if (logger.isTraceEnabled()) {
                logger.trace("      background = " + debugValue(background, null));
                logger.trace("      foreground = " + debugValue(foreground, null));
                logger.trace("      width = " + debugValue(columnWidth, (10 + columnName.length() * 8)));
                logger.trace("      tooltip = " + debugValue(columnTooltip, null));
                logger.trace("      excelColumn = " + debugValue(excelColumn, null));
                logger.trace("      excelCellType = " + debugValue(excelCellType, "string"));
                logger.trace("      excelDefault = " + debugValue(excelDefault, "blank"));
            }

            TableColumn tableColumn = new TableColumn(table, SWT.NONE);
            tableColumn.setText(columnName);
            tableColumn.setAlignment(SWT.CENTER);
            tableColumn.setWidth(columnWidth);
            tableColumn.setResizable(columnWidth != 0);
            tableColumn.setData("class", columnClass);
            tableColumn.setData("excelColumn", excelColumn);
            tableColumn.setData("excelCellType", excelCellType.toLowerCase());
            tableColumn.setData("excelDefault", excelDefault.toLowerCase());
            tableColumn.setData("tooltip", tooltip);
            
            if (background != null) {
                String[] colorArray = background.split(",");
                tableColumn.setData("background", new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
            }

            if (foreground != null) {
                String[] colorArray = foreground.split(",");
                tableColumn.setData("foreground", new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
            }
            
            tableColumn.addListener(SWT.Selection, sortListener);
            
            String alignment;

            switch (columnClass.toLowerCase()) {
                case "check":
                    if (JSONContainsKey(column, "values")) {
                        String[] values = (String[]) (getJSONArray(column, "values")).toArray(new String[0]);
                        String defaultValue = getString(column, "default", null);
                        boolean forceDefault = getBoolean(column, "forceDefault", false);
                        String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
                        alignment = getString(column, "alignment", "left");

                        if (logger.isTraceEnabled()) {
                            logger.trace("      values = " + values);
                            logger.trace("      default = " + debugValue(defaultValue, null));
                            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
                            logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
                            logger.trace("      alignment = "+debugValue(alignment, "left"));
                        }

                        if (whenEmpty != null) {
                            whenEmpty = whenEmpty.toLowerCase();
                            if (!inArray(whenEmptyValidStrings, whenEmpty))
                                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
                        }

                        tableColumn.setData("values", values);
                        tableColumn.setData("default", defaultValue);
                        tableColumn.setData("forceDefault", forceDefault);
                        tableColumn.setData("whenEmpty", whenEmpty);
                        
                        switch ( alignment.toLowerCase() ) {
                            case "right" :
                                tableColumn.setData("alignment", SWT.RIGHT);
                                break;
                            case "left" :
                                tableColumn.setData("alignment", SWT.LEFT);
                                break;
                            case "center":
                                tableColumn.setData("alignment", SWT.CENTER);
                                break;
                            default:
                                throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
                        }
                    } else {
                        tableColumn.setData("forceDefault", false);
                    }
                    break;
                case "combo":
                    if (column.containsKey("values")) {
                        String[] values = (String[]) (getJSONArray(column, "values")).toArray(new String[0]);
                        String defaultValue = getString(column, "default", null);
                        Boolean editable = getBoolean(column, "editable", true);
                        String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);

                        if (logger.isTraceEnabled()) {
                            logger.trace("      values = " + values);
                            logger.trace("      default = " + debugValue(defaultValue, null));
                            logger.trace("      editable = " + debugValue(editable, true));
                            logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
                        }

                        if (whenEmpty != null) {
                            whenEmpty = whenEmpty.toLowerCase();
                            if (!inArray(whenEmptyValidStrings, whenEmpty))
                                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
                        }

                        tableColumn.setData("values", values);
                        tableColumn.setData("default", defaultValue);
                        tableColumn.setData("editable", editable);
                        tableColumn.setData("whenEmpty", whenEmpty);
                    } else {
                        throw new RuntimeException(
                                FormPosition.getPosition(null) + "\n\nMissing mandatory attribute \"values\".");
                    }
                    break;
                case "label":
                    alignment = getString(column, "alignment", "left");
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("      alignment = "+debugValue(alignment, "left"));
                    }
                    
                    switch ( alignment.toLowerCase() ) {
                        case "right" :
                            tableColumn.setData("alignment", SWT.RIGHT);
                            break;
                        case "left" :
                            tableColumn.setData("alignment", SWT.LEFT);
                            break;
                        case "center":
                            tableColumn.setData("alignment", SWT.CENTER);
                            break;
                        default:
                            throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
                    }
                    break;
                case "text":
                    String regexp = getString(column, "regexp", null);
                    String defaultText = getString(column, "default", null);
                    String whenEmpty = getString(column, "whenEmpty", globalWhenEmpty);
                    boolean forceDefault = getBoolean(column, "forceDefault", false);
                    alignment = getString(column, "alignment", "left");

                    if (logger.isTraceEnabled()) {
                        logger.trace("      regexp = " + debugValue(regexp, null));
                        logger.trace("      default = " + debugValue(defaultText, null));
                        logger.trace("      forceDefault = " + debugValue(forceDefault, false));
                        logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
                        logger.trace("      alignment = "+debugValue(alignment, "left"));
                    }

                    if (whenEmpty != null) {
                        whenEmpty = whenEmpty.toLowerCase();
                        if (!inArray(whenEmptyValidStrings, whenEmpty))
                            throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + whenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
                    }

                    tableColumn.setData("regexp", regexp);
                    tableColumn.setData("default", defaultText);
                    tableColumn.setData("forceDefault", forceDefault);
                    tableColumn.setData("whenEmpty", whenEmpty);
                    
                    switch ( alignment.toLowerCase() ) {
                        case "right" :
                            tableColumn.setData("alignment", SWT.RIGHT);
                            break;
                        case "left" :
                            tableColumn.setData("alignment", SWT.LEFT);
                            break;
                        case "center":
                            tableColumn.setData("alignment", SWT.CENTER);
                            break;
                        default:
                            throw new RuntimeException(FormPosition.getPosition("alignment") + "\n\nInvalid alignment value, must be \"right\", \"left\" or \"center\"."); 
                    }
                    break;
                default:
                    throw new RuntimeException(FormPosition.getPosition("class") + "\n\nInvalid value \"" + getString(column, "class") + "\" (valid values are \"check\", \"combo\", \"label\", \"table\", \"text\").");
            }
        }
        FormPosition.resetColumnName();

        table.setSortColumn(table.getColumn(0));
        table.setSortDirection(SWT.UP);

        // we iterate over the "lines" entries
        JSONArray lines = getJSONArray(jsonObject, "lines");
        if (lines != null) {
            Iterator<JSONObject> linesIterator = lines.iterator();
            while (linesIterator.hasNext()) {
                JSONObject line = linesIterator.next();

                if ((boolean) getJSON(line, "generate", false) == false) {
                    // static line
                    if (logger.isTraceEnabled())
                        logger.trace("Creating static line");
                    addTableItem(table, selectedObject, getJSONArray(line, "cells"));
                } else {
                    // dynamic lines : we create one line per entry in
                    // getChildren()
                    if (logger.isTraceEnabled())
                        logger.trace("Generating dynamic lines");
                    if (selectedObject instanceof IArchimateDiagramModel) {
                        addTableItems(table, ((IArchimateDiagramModel) selectedObject).getChildren(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                    } else {
                        if (selectedObject instanceof IDiagramModelContainer) {
                            addTableItems(table, ((IDiagramModelContainer) selectedObject).getChildren(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                        } else if (selectedObject instanceof IFolder) {
                            addTableItems(table, ((IFolder) selectedObject).getElements(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                        } else if (selectedObject instanceof IArchimateModel) {
                            for (IFolder folder : ((IArchimateModel) selectedObject).getFolders()) {
                                addTableItems(table, folder.getElements(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
                            }
                        } else {
                            throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nCannot generate lines for selected component as it is not a container (" + selectedObject.getClass().getSimpleName() + ").");
                        }
                    }
                }
            }
        }
        return table;
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
    @SuppressWarnings("unchecked")
    private void addTableItems(Table table, EList<?> list, JSONArray values, JSONObject filter)
            throws RuntimeException {
        if ((list == null) || list.isEmpty())
            return;

        if (list.get(0) instanceof IDiagramModelObject) {
            for (IDiagramModelObject diagramObject : (EList<IDiagramModelObject>) list) {
                if (logger.isTraceEnabled())
                    logger.trace("Found diagram object " + diagramObject.getName());
                if (checkFilter(diagramObject, variableSeparator, filter)) {
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
                    if (checkFilter(diagramConnection, variableSeparator, filter)) {
                        addTableItem(table, (EObject) diagramConnection.getArchimateRelationship(), values);
                    }
                }
            } else
                if (list.get(0) instanceof IArchimateElement) {
                    for (IArchimateElement element : (EList<IArchimateElement>) list) {
                        if (logger.isTraceEnabled())
                            logger.trace("Found element " + element.getName());
                        if (checkFilter(element, variableSeparator, filter)) {
                            addTableItem(table, element, values);
                        }
                    }
                } else
                    if (list.get(0) instanceof IArchimateRelationship) {
                        for (IArchimateRelationship relation : (EList<IArchimateRelationship>) list) {
                            if (logger.isTraceEnabled())
                                logger.trace("Found relationship " + relation.getName());
                            if (checkFilter(relation, variableSeparator, filter)) {
                                addTableItem(table, relation, values);
                            }
                        }
                    } else
                        if (list.get(0) instanceof IDiagramModel) {
                            for (IDiagramModel view : (EList<IDiagramModel>) list) {
                                if (logger.isTraceEnabled())
                                    logger.trace("Found diagram model " + view.getName());
                                if (checkFilter(view, variableSeparator, filter)) {
                                    addTableItem(table, view, values);
                                }
                            }
                        } else {
                            throw new RuntimeException(FormPosition.getPosition("lines") + "\n\nFailed to generate lines for unknown object class \"" + list.get(0).getClass().getSimpleName() + "\"");
                        }
    }

    /**
     * Adds a line (TableItem) in the Table<br>
     * 
     * @param table
     *            the table in which create the lines
     * @param jsonArray
     *            the array of JSONObjects that contain the values to insert
     *            (one per column)
     */
    private void addTableItem(Table table, EObject eObject, JSONArray jsonArray) throws RuntimeException {
        TableItem tableItem = new TableItem(table, SWT.NONE);
        EObject referedEObject;
        String unscoppedVariable;

        // we need to store the widgets to retreive them later on
        TableEditor[] editors = new TableEditor[jsonArray.size()];

        logger.trace("   adding line for " + eObject.getClass().getSimpleName() + " \""
                + (((INameable) eObject).getName() == null ? "" : ((INameable) eObject).getName()) + "\"");

        for (int columnNumber = 0; columnNumber < jsonArray.size(); ++columnNumber) {
            TableColumn tableColumn = table.getColumn(columnNumber);
            String variableName = (String) jsonArray.get(columnNumber);
            String itemText = FormVariable.expand(variableName, variableSeparator, eObject); 

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
                    referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, eObject);
                    formVarList.set(referedEObject, unscoppedVariable, label);
                    break;

                case "text":
                    editor = new TableEditor(table);
                    StyledText text = new StyledText(table, SWT.WRAP);
                    if ( ((String)tableColumn.getData("default") != null && (itemText.isEmpty()) || (boolean)tableColumn.getData("forceDefault"))) {
                        itemText = FormVariable.expand((String)tableColumn.getData("default"), variableSeparator, eObject);
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
                    referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, eObject);
                    text.setData("eObject", referedEObject);
                    text.setData("variable", unscoppedVariable);
                    text.setData("whenEmpty", tableColumn.getData("whenEmpty"));
                    formVarList.set(referedEObject, unscoppedVariable, text);

                    if (table.getColumn(columnNumber).getData("tooltip") != null) {
                        text.setToolTipText(FormVariable.expand((String) table.getColumn(columnNumber).getData("tooltip"), variableSeparator, eObject));
                    } else {
                        if (table.getColumn(columnNumber).getData("regexp") != null) {
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
                        String defaultValue = (String)tableColumn.getData("default");
                        if (defaultValue != null)
                            itemText = defaultValue;
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
                    referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, eObject);
                    combo.setData("eObject", referedEObject);
                    combo.setData("variable", unscoppedVariable);
                    combo.setData("whenEmpty", tableColumn.getData("whenEmpty"));
                    formVarList.set(referedEObject, unscoppedVariable, combo);
                    Boolean editable = (Boolean)tableColumn.getData("editable");
                    combo.setEditable(editable != null && editable);

                    if (tableColumn.getData("tooltip") != null) {
                        combo.setToolTipText(FormVariable.expand((String)tableColumn.getData("tooltip"), variableSeparator, eObject));
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
                    referedEObject = FormVariable.getReferedEObject(variableName, variableSeparator, eObject);
                    unscoppedVariable = FormVariable.getUnscoppedVariable(variableName, variableSeparator, eObject);
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

                    if (tableColumn.getData("tooltip") != null) {
                        check.setToolTipText(FormVariable.expand((String)tableColumn.getData("tooltip"), variableSeparator, eObject));
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
        logger.log(FormGenerate.class, level, msg, e);

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

    private void cancel() {
        if (logger.isDebugEnabled())
            logger.debug("Cancel button selected by user.");
        
        close();
    }

    private void ok() {
        if (logger.isDebugEnabled())
            logger.debug("Ok button selected by user.");

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
