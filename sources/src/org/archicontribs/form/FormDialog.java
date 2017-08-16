package org.archicontribs.form;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class FormDialog extends Dialog {
    // TODO: add a "continueonerror" property
    private static final FormLogger logger            = new FormLogger(FormDialog.class);

    protected static Display        display           = Display.getDefault();
    public static final FontData    SYSTEM_FONT       = display.getSystemFont().getFontData()[0];
    public static final Font        TITLE_FONT        = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight() + 2, SWT.BOLD);
    public static final Font        BOLD_FONT         = new Font(display, SYSTEM_FONT.getName(), SYSTEM_FONT.getHeight(), SWT.BOLD);

    public static final Color       LIGHT_GREEN_COLOR = new Color(display, 204, 255, 229);
    public static final Color       LIGHT_RED_COLOR   = new Color(display, 255, 230, 230);
    public static final Color       RED_COLOR         = new Color(display, 240, 0, 0);
    public static final Color       GREEN_COLOR       = new Color(display, 0, 180, 0);
    public static final Color       WHITE_COLOR       = new Color(display, 255, 255, 255);
    public static final Color       GREY_COLOR        = new Color(display, 100, 100, 100);
    public static final Color       BLACK_COLOR       = new Color(display, 0, 0, 0);
    public static final Color       LIGHT_BLUE        = new Color(display, 240, 248, 255);

    private static final Color      badValueColor     = new Color(display, 255, 0, 0);
    private static final Color      goodValueColor    = new Color(display, 0, 100, 0);

    private final FormVarList       formVarList       = new FormVarList();

    private EObject                 selectedObject    = null;
    private Shell                   dialog            = null;
    private TabFolder               tabFolder         = null;

    private HashSet<String>         excelSheets       = new HashSet<String>();

    public FormDialog(String configFilename, JSONObject json, EObject selectedObject) {
        super(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        this.selectedObject = selectedObject;

        if (logger.isDebugEnabled())
            logger.debug("Creating new FormDialog for " + selectedObject.getClass().getSimpleName() + " \"" + ((INameable) selectedObject).getName() + "\".");

        try {
            createContents(json);
        } catch (IOException e) {
            popup(Level.ERROR, "I/O Error while reading configuration file \"" + configFilename + "\"", e);
            if (dialog != null)
                dialog.dispose();
            return;
        } catch (ParseException e) {
            popup(Level.ERROR, "Parsing error while reading configuration file \"" + configFilename + "\"", e);
            if (dialog != null)
                dialog.dispose();
            return;
        } catch (ClassCastException e) {
            popup(Level.ERROR, "Wrong key type in the configuration file \"" + configFilename + "\"", e);
            if (dialog != null)
                dialog.dispose();
            return;
        } catch (RuntimeException e) {
            popup(Level.ERROR, "Please check your configuration file \"" + configFilename + "\"", e);
            if (dialog != null)
                dialog.dispose();
            return;
        }

        dialog.open();
        dialog.layout();
    }

    private final String[] whenEmptyValidStrings   = new String[] { "ignore", "create", "delete" };

    public static final int      defaultDialogWidth      = 850;
    public static final int      defaultDialogHeight     = 600;
    public static final int      defaultDialogSpacing    = 4;
    public static final String   defaultDialogName       = "Form plugin";
    public static final String   defaultDialogBackground = null;
    public static final String   defaultTabBackground    = null;
    public static final int      defaultButtonWidth      = 90;
    public static final int      defaultButtonHeight     = 25;
    public static final String   defaultButtonOkText     = "Ok";
    public static final String   defaultButtonCancelText = "Cancel";
    public static final String   defaultButtonExportText = "Export to Excel";
    public static final String   defaultName             = "tab";
    public static final String   defaultVariableSeparator= ":";
    
    private String         variableSeparator       = null;
    
    private String         globalWhenEmpty         = null;
    private String         excelRunBefore          = null;
    private String         excelRunAfter           = null;

    /**
     * Parses the configuration file and create the corresponding graphical
     * controls
     */
    private void createContents(JSONObject form) throws IOException, ParseException, RuntimeException {
        variableSeparator = getString(form, "variableSeparator", defaultVariableSeparator);
        String formName = FormVariable.expand(getString(form, "name", defaultDialogName), variableSeparator,
                selectedObject);
        FormPosition.setFormName(formName);
        int dialogWidth = getInt(form, "width", defaultDialogWidth);
        int dialogHeight = getInt(form, "height", defaultDialogHeight);
        int dialogSpacing = getInt(form, "spacing", defaultDialogSpacing);
        String dialogBackground = getString(form, "background", defaultDialogBackground);
        int buttonWidth = getInt(form, "buttonWidth", defaultButtonWidth);
        int buttonHeight = getInt(form, "buttonHeight", defaultButtonHeight);
        String buttonOkText = FormVariable.expand(getString(form, "buttonOk", defaultButtonOkText), variableSeparator,
                selectedObject);
        String buttonCancelText = FormVariable.expand(getString(form, "buttonCancel", defaultButtonCancelText),
                variableSeparator, selectedObject);
        String buttonExportText = FormVariable.expand(getString(form, "buttonExport", defaultButtonExportText),
                variableSeparator, selectedObject);
        globalWhenEmpty = getString(form, "whenEmpty", null);
        excelRunBefore = getString(form, "excelRunBefore", null);
        excelRunAfter = getString(form, "excelRunAfter", null);

        if (logger.isTraceEnabled()) {
            logger.trace("   name = " + debugValue(formName, defaultDialogName));
            logger.trace("   variableSeparator = " + debugValue(variableSeparator, "."));
            logger.trace("   width = " + debugValue(dialogWidth, defaultDialogWidth));
            logger.trace("   height = " + debugValue(dialogHeight, defaultDialogHeight));
            logger.trace("   spacing = " + debugValue(dialogSpacing, defaultDialogSpacing));
            logger.trace("   background = " + debugValue(dialogBackground, defaultDialogBackground));
            logger.trace("   width = " + debugValue(buttonWidth, defaultButtonWidth));
            logger.trace("   height = " + debugValue(buttonHeight, defaultButtonHeight));
            logger.trace("   refers = " + debugValue(getString(form, "refers", "selected"), "selected"));       // used in FormMenu class but deserves a debug line
            logger.trace("   ok = " + debugValue(buttonOkText, defaultButtonOkText));
            logger.trace("   cancel = " + debugValue(buttonCancelText, defaultButtonCancelText));
            logger.trace("   whenEmpty = " + debugValue(globalWhenEmpty, null));
            logger.trace("   excelRunBefore = " + debugValue(excelRunBefore, null));
            logger.trace("   excelRunAfter = " + debugValue(excelRunAfter, null));
        }

        if (globalWhenEmpty != null) {
            globalWhenEmpty = globalWhenEmpty.toLowerCase();
            if (!inArray(whenEmptyValidStrings, globalWhenEmpty))
                throw new RuntimeException(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \"" + globalWhenEmpty + "\" (valid values are \"ignore\", \"create\" and \"delete\").");
        }

        dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText(FormVariable.expand(formName, variableSeparator, selectedObject));
        dialog.setLayout(null);

        int tabFolderWidth = dialogWidth - dialogSpacing * 2;
        int tabFolderHeight = dialogHeight - dialogSpacing * 3 - buttonHeight;

        dialog.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - dialogWidth) / 4,
                (Toolkit.getDefaultToolkit().getScreenSize().height - dialogHeight) / 4, dialogWidth, dialogHeight);
        // we resize the dialog because we want the width and height to be the
        // client's area width and height
        Rectangle area = dialog.getClientArea();
        dialog.setSize(dialogWidth * 2 - area.width, dialogHeight * 2 - area.height);

        if (dialogBackground != null) {
            String[] colorArray = dialogBackground.split(",");
            dialog.setBackground(new Color(display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
        }

        tabFolder = new TabFolder(dialog, SWT.BORDER);
        tabFolder.setBounds(dialogSpacing, dialogSpacing, tabFolderWidth, tabFolderHeight);

        Button cancelButton = new Button(dialog, SWT.NONE);
        cancelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth, tabFolderHeight + dialogSpacing * 2,
                buttonWidth, buttonHeight);
        cancelButton.setText(buttonCancelText);
        cancelButton.setEnabled(true);
        cancelButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                this.widgetDefaultSelected(e);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                cancel();
            }
        });

        Button okButton = new Button(dialog, SWT.NONE);
        okButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth * 2 - dialogSpacing,
                tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
        okButton.setText(buttonOkText);
        okButton.setEnabled(true);
        okButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                this.widgetDefaultSelected(e);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                ok();
            }
        });

        createTabs(form, tabFolder);

        // If there is at least one Excel sheet specified, then we show up the
        // "export to Excel" button
        if (!excelSheets.isEmpty()) {
            Button exportToExcelButton = new Button(dialog, SWT.NONE);
            exportToExcelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth * 3 - dialogSpacing * 2,
                    tabFolderHeight + dialogSpacing * 2, buttonWidth, buttonHeight);
            exportToExcelButton.setText(buttonExportText);
            exportToExcelButton.setEnabled(true);
            exportToExcelButton.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    this.widgetDefaultSelected(e);
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                    exportToExcel();
                }
            });
        }
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
        dialog.dispose();
    }

    private void ok() {
        if (logger.isDebugEnabled())
            logger.debug("Ok button selected by user.");
        CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();
        try {
            for (Control control : dialog.getChildren()) {
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

        dialog.dispose();
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

            if (whenEmpty == null)
                whenEmpty = "ignore";

            switch (whenEmpty) {
                case "ignore":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : ignored.");
                    break;
                case "create":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : creating property.");
                    FormVariable.setVariable(unscoppedVariable, variableSeparator, "", referedEObject);
                    break;
                case "delete":
                    if (logger.isTraceEnabled())
                        logger.trace("   value is empty : deleting property.");
                    FormVariable.setVariable(unscoppedVariable, variableSeparator, null, referedEObject);
                    break;
            }
        } else {
            if (logger.isTraceEnabled())
                logger.trace("   value is not empty.");
            FormVariable.setVariable(unscoppedVariable, variableSeparator, value, referedEObject);
        }
    }

    @SuppressWarnings("deprecation")
    private void exportToExcel() {
        if (logger.isDebugEnabled())
            logger.debug("Export button selected by user.");
        
        FileDialog fsd = new FileDialog(dialog, SWT.SINGLE);
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
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @return the value if found
     * @throws RuntimeException
     *             if the key is not found
     */
    public static Object getJSON(JSONObject obj, String key) throws RuntimeException {
        Object result = getJSON(obj, key, null);
        if (result == null)
            throw new RuntimeException("key \"" + key + "\" not found.");
        return result;
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

        if (result instanceof JSONObject)
            return (JSONObject) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a JSONObject.");
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

        if (result instanceof JSONArray)
            return (JSONArray) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a JSONarray.");
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
    public static String getString(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);

        if (result instanceof String)
            return (String) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a String.");
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
    public static int getInt(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);

        if (result instanceof Long)
            return (int) (long) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be an Integer.");
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
    public static Boolean getBoolean(JSONObject obj, String key) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key);

        if (result instanceof Boolean)
            return (Boolean) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a Boolean.");
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
        @SuppressWarnings("unchecked")
        Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, or the defaultValue provided if not found
     */
    public static Object getJSON(JSONObject obj, String key, Object defaultValue) {
        @SuppressWarnings("unchecked")
        Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key)) {
                return obj.get(key1);
            }
        }
        return defaultValue;
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, or the defaultValue provided if not found,
     *         ClassCastException if the object found is not a JSONObject
     */
    public static JSONObject getJSONObject(JSONObject obj, String key, Object defaultValue)
            throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key, defaultValue);

        if (result == null || result instanceof JSONObject)
            return (JSONObject) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a JSONObject.");
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, or the defaultValue provided if not found,
     *         ClassCastException if the object found is not a JSONArray
     */
    public static JSONArray getJSONArray(JSONObject obj, String key, Object defaultValue) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key, defaultValue);

        if (result == null || result instanceof JSONArray)
            return (JSONArray) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a JSONarray.");
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, or the defaultValue provided if not found,
     *         ClassCastException if the object found is not a String
     */
    public static String getString(JSONObject obj, String key, Object defaultValue) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key, defaultValue);

        if (result == null || result instanceof String)
            return (String) result;

        throw new ClassCastException(
                "Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a String.");
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, or the defaultValue provided if not found,
     *         ClassCastException if the object found is not an Integer
     */
    public static int getInt(JSONObject obj, String key, int defaultValue) throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key, Long.valueOf(defaultValue));

        if (result instanceof Long)
            return (int) (long) result;

        throw new ClassCastException("Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be an Integer.");
    }

    /**
     * Gets the value corresponding to the key in the JSONObject
     * 
     * @param obj
     *            the JSONObject
     * @param key
     *            the key (case insensitive)
     * @param defaultValue
     * @return the value if found, or the defaultValue provided if not found,
     *         ClassCastException if the object found is not a Boolean
     */
    public static Boolean getBoolean(JSONObject obj, String key, Boolean defaultValue)
            throws RuntimeException, ClassCastException {
        Object result = getJSON(obj, key, defaultValue);

        if (result == null || result instanceof Boolean)
            return (Boolean) result;

        throw new ClassCastException(
                "Key \"" + key + "\" is a " + result.getClass().getSimpleName() + " but should be a Boolean.");
    }

    /**
     * Checks whether the eObject fits in the filter rules
     */
    public static boolean checkFilter(EObject eObject, String separator, JSONObject filterObject) {
        if (filterObject == null) {
            return true;
        }

        String type = ((String) getJSON(filterObject, "genre", "AND")).toUpperCase();

        if (!type.equals("AND") && !type.equals("OR"))
            throw new RuntimeException("Invalid filter genre. Supported genres are \"AND\" and \"OR\".");

        boolean result;

        @SuppressWarnings("unchecked")
        Iterator<JSONObject> filterIterator = ((JSONArray) getJSON(filterObject, "tests")).iterator();
        while (filterIterator.hasNext()) {
            JSONObject filter = filterIterator.next();
            String attribute = (String) getJSON(filter, "attribute");
            String operation = (String) getJSON(filter, "operation");
            String value;
            String[] values;

            String attributeValue = FormVariable.expand(attribute, separator, eObject);

            switch (operation.toLowerCase()) {
                case "equals":
                    value = (String) getJSON(filter, "value");

                    result = attributeValue.equals(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") equals \"" + value + "\" --> " + result);
                    break;

                case "in":
                    value = (String) getJSON(filter, "value");
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
                    value = (String) getJSON(filter, "value");

                    result = attributeValue.equalsIgnoreCase(value);
                    if (logger.isTraceEnabled())
                        logger.trace("   filter " + attribute + "(\"" + attributeValue + "\") equals (ignore case) \"" + value + "\" --> " + result);
                    break;

                case "iin":
                    value = (String) getJSON(filter, "value");
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
                    value = (String) getJSON(filter, "value");

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

    public String debugValue(String value, String defaultValue) {
        StringBuilder result = new StringBuilder();
        result.append("\"" + value + "\"");
        if (FormPlugin.areEqual(value, defaultValue))
            result.append(" (default)");
        return result.toString();
    }

    public String debugValue(int value, int defaultValue) {
        StringBuilder result = new StringBuilder();
        result.append(value);
        if (value == defaultValue)
            result.append(" (default)");
        return result.toString();
    }

    public String debugValue(Boolean value, Boolean defaultValue) {
        StringBuilder result = new StringBuilder();
        if (value == null)
            result.append("null");
        else
            result.append(value ? "true" : "false");
        if (value == defaultValue)
            result.append(" (default)");
        return result.toString();
    }

    public static boolean inArray(String[] stringArray, String string) {
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
}
