package org.archicontribs.form;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Methods to parse JSON objects and create the corresponding SWT widgets
 * 
 * @author Herve Jouin
 *
 */
public class FormJsonParser {
	private static final FormLogger logger            = new FormLogger(FormDialog.class);
	
	protected static Display        display           = Display.getDefault();
	
    /**
     * @param ignoreErrors : set to true if the errors must be ignored, false if an Exception should be raised in case of error.<br><br>
     * This parameter is useful for the graphical editor that should be able to parse the totality of the jsonObject.
     */
	public FormJsonParser() {
	}
	

	
    /**
     * Parses the jsonObject and create the corresponding Shell<br>
     * <br>
     * @param parent the parent shell
     * @param jsonObject the jsonObject to parse<br>
     * 
     * @return the created Shell
     * 
     * @throws ClassCastException when a property does not belong to the right class in the jsonObject (i.e. a String is found while an Integer was expected)
     * @throws RuntimeException when a property has got an unexpected value (i.e. a negative value where a positive one was expected)
     */
    public Shell createShell(JSONObject jsonObject, Shell parent) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating form");
    	
        String  name              = getString(jsonObject, "name"); FormPosition.setFormName(name);
        Integer width             = getInt(jsonObject, "width");
        Integer height            = getInt(jsonObject, "height");
        Integer spacing           = getInt(jsonObject, "spacing");
        Integer buttonWidth       = getInt(jsonObject, "buttonWidth");
        Integer buttonHeight      = getInt(jsonObject, "buttonHeight");
        String  refers            = getString(jsonObject, "refers");
        String  variableSeparator = getString(jsonObject, "variableSeparator");
        String  whenEmpty         = getString(jsonObject, "whenEmpty");
        String  foreground        = getString(jsonObject, "foreground");
        String  background        = getString(jsonObject, "background");
        String  buttonOkText          = getString(jsonObject, "buttonOk");
        String  buttonCancelText      = getString(jsonObject, "buttonCancel");
        String  buttonExportText      = getString(jsonObject, "buttonExport");
    	
        if (logger.isTraceEnabled()) {
            logger.trace("   name = " + debugValue(name, FormDialog.defaultDialogName));
            logger.trace("   variableSeparator = " + debugValue(variableSeparator, FormDialog.defaultVariableSeparator));
            logger.trace("   width = " + debugValue(width, FormDialog.defaultDialogWidth));
            logger.trace("   height = " + debugValue(height, FormDialog.defaultDialogHeight));
            logger.trace("   spacing = " + debugValue(spacing, FormDialog.defaultDialogSpacing));
            logger.trace("   foreground = " + foreground);
            logger.trace("   background = " + background);
            logger.trace("   buttonWidth = " + debugValue(buttonWidth, FormDialog.defaultButtonWidth));
            logger.trace("   buttonHeight = " + debugValue(buttonHeight, FormDialog.defaultButtonHeight));
            logger.trace("   refers = " + debugValue(refers, FormDialog.validRefers[0]));
            logger.trace("   buttonOk = " + debugValue(buttonOkText, FormDialog.defaultButtonOkText));
            logger.trace("   buttonCancel = " + debugValue(buttonCancelText, FormDialog.defaultButtonCancelText));
            logger.trace("   buttonExport = " + debugValue(buttonExportText, FormDialog.defaultButtonExportText));
            logger.trace("   whenEmpty = " + debugValue(whenEmpty, FormDialog.validWhenEmpty[0]));
        }
        
        // we create the shell
        Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        
        // we register the values from the configuration file that are needed by the graphical editor
        shell.setData("name",              name);
        shell.setData("width",             width);
        shell.setData("height",            height);
        shell.setData("spacing",           spacing);
        shell.setData("buttonWidth",       buttonWidth);
        shell.setData("buttonHeight",      buttonHeight);
        shell.setData("refers",            refers );
        shell.setData("variableSeparator", variableSeparator);
        shell.setData("whenEmpty",         whenEmpty);
        shell.setData("foreground",        foreground);
        shell.setData("background",        background);
        shell.setData("buttonOk",          buttonOkText);
        shell.setData("buttonCancel",      buttonCancelText);
        shell.setData("buttonExport",      buttonExportText);
        shell.setData("whenEmpty",         whenEmpty);
        // list of the keys that needs to be edited in the graphical editor
        // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        shell.setData("editable keys", new String[] {"name", "width", "height", "spacing", "buttonWidth", "buttonHeight", "refers", "variableSeparator", "whenEmpty", "foreground", "background", "buttonOk", "buttonCancel", "buttonExport", "whenEmpty"});
        
        // width, height, spacing
        if ( width == null || width < 0 )
        	width = FormDialog.defaultDialogWidth;
        
        if ( height == null || height < 0 )
        	width = FormDialog.defaultDialogHeight;
        
        if ( spacing == null || spacing < 0 )
        	spacing = FormDialog.defaultDialogSpacing;
        
        // buttonWidth, buttonHeigh
        if ( buttonWidth == null || buttonWidth < 0 )
        	buttonWidth = FormDialog.defaultButtonWidth;
        
        if ( buttonHeight == null || buttonHeight < 0 )
        	buttonHeight = FormDialog.defaultButtonHeight;

        // resizing the shell
        shell.setBounds((display.getPrimaryMonitor().getBounds().width-width)/2, (display.getPrimaryMonitor().getBounds().height-height)/2, width, height);
        shell.setLayout(new FormLayout());
        
        // name
        if ( name != null )
			shell.setText(name);			// may be replaced by FormVariable.expand(name, selectedObject) in calling method

        // foreground, background
        FormPlugin.setColor(shell,  foreground, SWT.FOREGROUND);
        FormPlugin.setColor(shell,  foreground, SWT.BACKGROUND);
        
        // creating the buttons
        Button cancelButton = new Button(shell, SWT.NONE);
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
        shell.setData("cancel button", cancelButton);			         // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        Button okButton = new Button(shell, SWT.NONE);
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
        shell.setData("ok button", okButton);							 // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        Button exportToExcelButton = new Button(shell, SWT.NONE);
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
        shell.setData("export button", exportToExcelButton);			 // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        
        // we create the tab folder
        TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(0, spacing);
        fd.left = new FormAttachment(0, spacing);
        fd.right = new FormAttachment(100, -spacing);
        fd.bottom = new FormAttachment(okButton, -spacing);
        tabFolder.setLayoutData(fd);
        tabFolder.setForeground(shell.getForeground());
        tabFolder.setBackground(shell.getBackground());
        
        shell.setData("tab folder", tabFolder);					        // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        
        shell.setData("tabs array", getJSONArray(jsonObject, "tabs"));	// we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file

        return shell;
    }
    
    /**
     * Parses the jsonObject and create the corresponding TabItem<br>
     * <br>
     * @param parent the parent TabFolder
     * @param jsonObject the jsonObject to parse<br>
     * 
     * @return the created Shell
     * 
     * @throws ClassCastException when a property does not belong to the right class in the jsonObject (i.e. a String is found while an Integer was expected)
     * @throws RuntimeException when a property has got an unexpected value (i.e. a negative value where a positive one was expected)
     */
    public TabItem createTab(JSONObject jsonObject, TabFolder parent) throws RuntimeException, ClassCastException {
    	if (logger.isDebugEnabled()) logger.debug("Creating tab");
    	
        String  name              = getString(jsonObject, "name");
        FormPosition.setTabName(name);
        FormPosition.setControlClass("tab");
        
        String  foreground        = getString(jsonObject, "foreground");
        String  background        = getString(jsonObject, "background");
    	
        if (logger.isTraceEnabled()) {
            logger.trace("   name = " + debugValue(name, FormDialog.defaultDialogName));
            logger.trace("   foreground = " + debugValue(foreground, "the form's foreground"));
            logger.trace("   background = " + debugValue(background, "the form's background"));
        }
        
        // we create the tab item
        TabItem tabItem = new TabItem(parent, SWT.MULTI);
        Composite composite = new Composite(parent, SWT.NONE);
        tabItem.setControl(composite);
        
        // we register the values from the configuration file that are needed by the graphical editor
        composite.setData("name",              name);
        composite.setData("foreground",        foreground);
        composite.setData("background",        background);
        // list of the keys that needs to be edited in the graphical editor
        // we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file
        composite.setData("editable keys", new String[] {"name", "foreground", "background"});

    	// name
        if ( name != null )
        	tabItem.setText(name);						// may be replaced by FormVariable.expand(name, selectedObject) in calling method

        // foreground, background
        FormPlugin.setColor(composite, foreground, SWT.FOREGROUND);
        FormPlugin.setColor(composite, background, SWT.BACKGROUND);
        
        tabItem.setData("controls array", getJSONArray(jsonObject, "controls"));	// we insert a space in the key name in order to guarantee that it will never conflict with a keyword in the configuration file

        return tabItem;
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
    public Label createLabel(JSONObject jsonObject, Composite parent) throws RuntimeException, ClassCastException {
        if (logger.isDebugEnabled()) logger.debug("Creating label control");
        
        String  name              = getString(jsonObject, "name");
        FormPosition.setTabName(name);
        FormPosition.setControlClass("label");
        
        Integer x                 = getInt(jsonObject, "x");
        Integer y                 = getInt(jsonObject, "y");
        Integer width             = getInt(jsonObject, "width");
        Integer height            = getInt(jsonObject, "height");
        String  text              = getString(jsonObject, "text");
        String  foreground        = getString(jsonObject, "foreground");
        String  background        = getString(jsonObject, "background");
        String  tooltip           = getString(jsonObject, "tooltip");
        String  fontName          = getString(jsonObject, "fontName");
        Integer fontSize          = getInt(jsonObject, "fontSize");
        Boolean fontBold          = getBoolean(jsonObject, "fontBold");
        Boolean fontItalic        = getBoolean(jsonObject, "fontItalic");
        String  alignment         = getString(jsonObject, "alignment");
        String  excelSheet        = getString(jsonObject, "excelSheet");
        String  excelCell         = getString(jsonObject, "excelCell");
        String  excelCellType     = getString(jsonObject, "excelCellType");
        String  excelDefault      = getString(jsonObject, "excelDefault");
        
        // we create the label
        Label label = new Label(parent, SWT.WRAP);

        if (logger.isTraceEnabled()) {
            logger.trace("      name = " + name);
            logger.trace("      text = " + text);
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, "calculated from content"));
            logger.trace("      height = " + debugValue(height, label.getBounds().y));
            logger.trace("      foreground = " + debugValue(foreground, "parent's"));
            logger.trace("      background = " + debugValue(background, "parent's"));
            logger.trace("      tooltip = " + tooltip);
            logger.trace("      fontName = " + debugValue(fontName, "parent's"));
            logger.trace("      fontSize = " + debugValue(fontSize, "parent's"));
            logger.trace("      fontBold = " + debugValue(fontBold, "parent's"));
            logger.trace("      fontItalic = " + debugValue(fontItalic, "parent's"));
            logger.trace("      alignment = " + debugValue(alignment, "OS default"));
            logger.trace("      excelSheet = " + excelSheet);
            logger.trace("      excelCell = " + excelCell);
            logger.trace("      excelCellType = " + debugValue(excelCellType, FormDialog.validExcelCellType[0]));
            logger.trace("      excelDefault = " + debugValue(excelDefault, FormDialog.validExcelDefault[0]));
        }
        
        // we register the values from the configuration file that are needed by the graphical editor
        label.setData("name", name);
        label.setData("text", text);
        label.setData("x", x);
        label.setData("y", y);
        label.setData("width", width);
        label.setData("height", height);
        label.setData("background", background);
        label.setData("foreground", foreground);
        label.setData("tooltip", tooltip);
        label.setData("fontName", fontName);
        label.setData("fontSize", fontSize);
        label.setData("fontBold", fontBold);
        label.setData("fontItalic", fontItalic);
        label.setData("alignment", alignment);
        label.setData("excelSheet", excelSheet);
        label.setData("excelCell", excelCell);
        label.setData("excelCellType", excelCellType);
        label.setData("excelDefault", excelDefault);
        label.setData("editable keys", new String[]{"name", "text", "x", "y", "width", "height", "background", "foreground", "tooltip", "fontName", "fontSize", "fontBold", "fontItalic", "alignment", "excelSheet", "excelCell", "excelCellType", "excelDefault"});

		// text
        if ( text != null )
        	label.setText(text);
        
        // x, y, width, height
        Point p = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if ( x == null      || x < 0 )       x = 0;
        if ( y == null      || y < 0 )       y = 0;
        if ( width == null  || width <= 0  ) width = p.x;
        if ( height == null || height <= 0 ) height = p.y;
		label.setBounds(x, y, width, height);
		
        // foreground, background
		FormPlugin.setColor(label, foreground, SWT.FOREGROUND);
		FormPlugin.setColor(label, background, SWT.BACKGROUND);
        
        // fontName, fontSize, fontBold, fontItalic
		FormPlugin.setFont(label, fontName, fontSize, fontBold, fontItalic);
        
        // alignment
		FormPlugin.setAlignment(label, alignment);
        
        // tooltip
		if ( !FormPlugin.isEmpty(tooltip) )
			label.setToolTipText(tooltip);
		
        // excelCellType
        if ( !FormPlugin.isEmpty(excelCellType) && !FormPlugin.areEqualIgnoreCase(excelCellType, "string") && !FormPlugin.areEqualIgnoreCase(excelCellType, "numeric") && !FormPlugin.areEqual(excelCellType, "boolean") && !FormPlugin.areEqualIgnoreCase(excelCellType, "formula") )
       		FormPlugin.error(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value, must be \"string\", \"numeric\", \"boolean\" or \"formula\".");
        
        // ExcelDefault
        if ( !FormPlugin.isEmpty(excelDefault) && !FormPlugin.areEqualIgnoreCase(excelDefault, "blank") && !FormPlugin.areEqualIgnoreCase(excelDefault, "zero") && !FormPlugin.areEqualIgnoreCase(excelDefault, "delete") )
        	FormPlugin.error(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value, must be \"blank\", \"zero\" or \"delete\"."); 

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
    public StyledText createText(JSONObject jsonObject, Composite parent) throws RuntimeException {
        logger.debug("   Creating text control");
        
        String name           = getString(jsonObject, "name");
        FormPosition.setControlName(name);
        FormPosition.setControlClass("text");
        
        Integer x             = getInt(jsonObject, "x");
        Integer y             = getInt(jsonObject, "y");
        Integer width         = getInt(jsonObject, "width");
        Integer height        = getInt(jsonObject, "height");
        String  variable      = getString(jsonObject, "variable");
        String  defaultText   = getString(jsonObject, "default");
        Boolean forceDefault  = getBoolean(jsonObject, "forceDefault");
        String  regex         = getString(jsonObject, "regexp");
        Boolean editable      = getBoolean(jsonObject, "editable");
        String  background    = getString(jsonObject, "background");
        String  foreground    = getString(jsonObject, "foreground");
        String  tooltip       = getString(jsonObject, "tooltip");
        String  fontName      = getString(jsonObject, "fontName");
        Integer fontSize      = getInt(jsonObject, "fontSize");
        Boolean fontBold      = getBoolean(jsonObject, "fontBold");
        Boolean fontItalic    = getBoolean(jsonObject, "fontItalic");
        String  alignment     = getString(jsonObject, "alignment");
        String  whenEmpty     = getString(jsonObject, "whenEmpty");
        String  excelSheet    = getString(jsonObject, "excelSheet");
        String  excelCell     = getString(jsonObject, "excelCell");
        String  excelCellType = getString(jsonObject, "excelCellType");
        String  excelDefault  = getString(jsonObject, "excelDefault");

        // we create the text
        StyledText text = new StyledText(parent, SWT.WRAP | SWT.BORDER);
        
        if (logger.isTraceEnabled()) {
            logger.trace("      name = " + name);
            logger.trace("      variable = " + variable);
            logger.trace("      default = " + defaultText);
            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, "calculated from content"));
            logger.trace("      height = " + debugValue(height, text.getBounds().y));
            logger.trace("      background = " + debugValue(background, "parent's"));
            logger.trace("      foreground = " + debugValue(foreground, "parent's"));
            logger.trace("      regexp = " + debugValue(regex, ""));
            logger.trace("      tooltip = " + debugValue(tooltip, ""));
            logger.trace("      fontName = " + debugValue(fontName, "parent's"));
            logger.trace("      fontSize = " + debugValue(fontSize, "parent's"));
            logger.trace("      fontBold = " + debugValue(fontBold, "parent's"));
            logger.trace("      fontItalic = " + debugValue(fontItalic, "parent's"));
            logger.trace("      alignment = "+alignment);
            logger.trace("      editable = " + debugValue(editable, true));
            logger.trace("      whenEmpty = " + debugValue(whenEmpty, FormDialog.validWhenEmpty[0]));
            logger.trace("      excelSheet = " + excelSheet);
            logger.trace("      excelCell = " + excelCell);
            logger.trace("      excelCellType = " + debugValue(excelCellType, FormDialog.validExcelCellType[0]));
            logger.trace("      excelDefault = " + debugValue(excelDefault, FormDialog.validExcelDefault[0]));
        }
        
        text.setData("name", name);
        text.setData("variable", variable);
        text.setData("defaultText", defaultText);
        text.setData("forceDefault", forceDefault);
        text.setData("x", x);
        text.setData("y", y);
        text.setData("width", width);
        text.setData("height", height);
        text.setData("background", background);
        text.setData("foreground", foreground);
        text.setData("regexp", regex);
        text.setData("tooltip", tooltip);
        text.setData("fontName", fontName);
        text.setData("fontSize", fontSize);
        text.setData("fontBold", fontBold);
        text.setData("fontItalic", fontItalic);
        text.setData("alignment", alignment);
        text.setData("editable", editable);
        text.setData("whenEmpty", whenEmpty);
        text.setData("excelSheet", excelSheet);
        text.setData("excelCell", excelCell);
        text.setData("excelCellType", excelCellType);
        text.setData("excelDefault", excelDefault);
        text.setData("editable keys", new String[]{"name", "variable", "defaultText", "forceDefault", "x", "y", "width", "height", "background", "foreground", "tooltip", "regexp", "fontName", "fontSize", "fontBold", "fontItalic", "alignment", "editable", "whenEmpty", "excelSheet", "excelCell", "excelCellType", "excelDefault"});
        
		// text
        if ( variable != null )
        	text.setText(variable);
        
        // editable
        if ( editable != null )
        	text.setEditable(editable);
        
        // x, y, width, height
        Point p = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if ( x == null      || x < 0 )       x = 0;
        if ( y == null      || y < 0 )       y = 0;
        if ( width == null  || width <= 0  ) width = p.x;
        if ( height == null || height <= 0 ) height = p.y;
		text.setBounds(x, y, width, height);
		
		// foreground, background
		FormPlugin.setColor(text, foreground, SWT.FOREGROUND);
		FormPlugin.setColor(text, background, SWT.BACKGROUND);
                
        // fontName, fontSize, fontBold, fontItalic
        FormPlugin.setFont(text, fontName, fontSize, fontBold, fontItalic);
		        
        // alignment
        FormPlugin.setAlignment(text, alignment);
        
        // tooltip
		if ( !FormPlugin.isEmpty(tooltip) )
			text.setToolTipText(tooltip);
                
        // excelCellType
        if ( !FormPlugin.isEmpty((String)excelCellType) && !inArray(FormDialog.validExcelCellType, excelCellType))
       		FormPlugin.error(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value \""+excelCellType+"\" (valid values are "+FormDialog.validExcelCellType+").");
        
        // ExcelDefault
        if ( !FormPlugin.isEmpty(excelDefault) && !inArray(FormDialog.validExcelDefault, excelDefault))
        	FormPlugin.error(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value \""+excelDefault+"\" (valid values are "+FormDialog.validExcelDefault+").");
        
        // WhenEmpty
        if ( !FormPlugin.isEmpty((String)whenEmpty) && !inArray(FormDialog.validWhenEmpty, whenEmpty))
        	FormPlugin.error(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \""+whenEmpty+"\" (valid values are "+FormDialog.validWhenEmpty+").");
        
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
    public CCombo createCombo(JSONObject jsonObject, Composite parent) throws RuntimeException {
    	logger.debug("   Creating combo control");
        
        String name           = getString(jsonObject, "name");
        FormPosition.setControlName(name);
        FormPosition.setControlClass("combo");

        Integer x             = getInt(jsonObject, "x");
        Integer y             = getInt(jsonObject, "y");
        Integer width         = getInt(jsonObject, "width");
        Integer height        = getInt(jsonObject, "height");
        String  variable      = getString(jsonObject, "variable");
        String  defaultText   = getString(jsonObject, "default");
        Boolean forceDefault  = getBoolean(jsonObject, "forceDefault");
        @SuppressWarnings("unchecked")
        String[] values       = (String[]) (getJSONArray(jsonObject, "values")).toArray(new String[0]);
        String  background    = getString(jsonObject, "background");
        String  foreground    = getString(jsonObject, "foreground");
        String  tooltip       = getString(jsonObject, "tooltip");
        String  fontName      = getString(jsonObject, "fontName");
        Integer fontSize      = getInt(jsonObject, "fontSize");
        Boolean fontBold      = getBoolean(jsonObject, "fontBold");
        Boolean fontItalic    = getBoolean(jsonObject, "fontItalic");
        Boolean editable      = getBoolean(jsonObject, "editable");
        String  whenEmpty     = getString(jsonObject, "whenEmpty");
        String  excelSheet    = getString(jsonObject, "excelSheet");
        String  excelCell     = getString(jsonObject, "excelCell");
        String  excelCellType = getString(jsonObject, "excelCellType");
        String  excelDefault  = getString(jsonObject, "excelDefault");
        
        CCombo combo = new CCombo(parent, SWT.NONE);

        if (logger.isTraceEnabled()) {
            logger.trace("      name = " + name);
            logger.trace("      variable = " + variable);
            logger.trace("      values = " + values);
            logger.trace("      default = " + defaultText);
            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, "calculated from content"));
            logger.trace("      height = " + debugValue(height, combo.getBounds().y));
            logger.trace("      background = " + debugValue(background, "parent's"));
            logger.trace("      foreground = " + debugValue(foreground, "parent's"));
            logger.trace("      tooltip = " + debugValue(tooltip, ""));
            logger.trace("      fontName = " + debugValue(fontName, "parent's"));
            logger.trace("      fontSize = " + debugValue(fontSize, "parent's"));
            logger.trace("      fontBold = " + debugValue(fontBold, "parent's"));
            logger.trace("      fontItalic = " + debugValue(fontItalic, "parent's"));
            logger.trace("      editable = " + debugValue(editable, true));
            logger.trace("      whenEmpty = " + debugValue(whenEmpty, FormDialog.validWhenEmpty[0]));
            logger.trace("      excelSheet = " + excelSheet);
            logger.trace("      excelCell = " + excelCell);
            logger.trace("      excelCellType = " + debugValue(excelCellType, FormDialog.validExcelCellType[0]));
            logger.trace("      excelDefault = " + debugValue(excelDefault, FormDialog.validExcelDefault[0]));
        }

        combo.setData("name", name);
        combo.setData("variable", variable);
        combo.setData("values", values);
        combo.setData("default", defaultText);
        combo.setData("forceDefault", forceDefault);
        combo.setData("x", x);
        combo.setData("y", y);
        combo.setData("width", width);
        combo.setData("height", height);
        combo.setData("background", background);
        combo.setData("foreground", foreground);
        combo.setData("tooltip", tooltip);
        combo.setData("fontName", fontName);
        combo.setData("fontSize", fontSize);
        combo.setData("fontBold", fontBold);
        combo.setData("fontItalic", fontItalic);
        combo.setData("editable", editable);
        combo.setData("whenEmpty", whenEmpty);
        combo.setData("excelSheet", excelSheet);
        combo.setData("excelCell", excelCell);
        combo.setData("excelCellType", excelCellType);
        combo.setData("excelDefault", excelDefault);
        combo.setData("editable keys", new String[]{"name", "variable", "values", "defaultText", "forceDefault",  "x", "y", "width", "height", "background", "foreground", "tooltip", "fontName", "fontSize", "fontBold", "fontItalic", "editable", "whenEmpty", "excelSheet", "excelCell", "excelCellType", "excelDefault"});
        
        // values
        if ( values != null )
        	combo.setItems(values);
        
        // editable
        if ( editable != null )
        	combo.setEditable(editable);
        
		// text
        if ( variable != null )
        	combo.setText(variable);
        
        // x, y, width, height
        Point p = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if ( x == null      || x < 0 )       x = 0;
        if ( y == null      || y < 0 )       y = 0;
        if ( width == null  || width <= 0  ) width = p.x;
        if ( height == null || height <= 0 ) height = p.y;
		combo.setBounds(x, y, width, height);
		
		// foreground, background
		FormPlugin.setColor(combo, foreground, SWT.FOREGROUND);
		FormPlugin.setColor(combo, background, SWT.BACKGROUND);
                
        // fontName, fontSize, fontBold, fontItalic
        FormPlugin.setFont(combo, fontName, fontSize, fontBold, fontItalic);
        
        // tooltip
		if ( !FormPlugin.isEmpty(tooltip) )
			combo.setToolTipText(tooltip);
                
        // excelCellType
        if ( !FormPlugin.isEmpty((String)excelCellType) && !inArray(FormDialog.validExcelCellType, excelCellType))
       		FormPlugin.error(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value \""+excelCellType+"\" (valid values are "+FormDialog.validExcelCellType+").");
        
        // ExcelDefault
        if ( !FormPlugin.isEmpty(excelDefault) && !inArray(FormDialog.validExcelDefault, excelDefault))
        	FormPlugin.error(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value \""+excelDefault+"\" (valid values are "+FormDialog.validExcelDefault+").");
        
        // WhenEmpty
        if ( !FormPlugin.isEmpty((String)whenEmpty) && !inArray(FormDialog.validWhenEmpty, whenEmpty))
        	FormPlugin.error(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \""+whenEmpty+"\" (valid values are "+FormDialog.validWhenEmpty+").");
   
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
    public Button createCheck(JSONObject jsonObject, Composite parent) throws RuntimeException {
    	logger.debug("   Creating check control");
        
        String name           = getString(jsonObject, "name");
        FormPosition.setControlName(name);
        FormPosition.setControlClass("check");
        
        @SuppressWarnings("unchecked")
        String[] values = (String[]) (getJSONArray(jsonObject, "values")).toArray(new String[0]);

        String  variable      = getString(jsonObject, "variable");
        String  defaultText   = getString(jsonObject, "default");
        Boolean forceDefault  = getBoolean(jsonObject, "forceDefault");
        Integer x             = getInt(jsonObject, "x");
        Integer y             = getInt(jsonObject, "y");
        Integer width         = getInt(jsonObject, "width");
        Integer height        = getInt(jsonObject, "height");
        String background     = getString(jsonObject, "background");
        String foreground     = getString(jsonObject, "foreground");
        String tooltip        = getString(jsonObject, "tooltip");
        String alignment      = getString(jsonObject, "alignment");
        String whenEmpty      = getString(jsonObject, "whenEmpty");
        String excelSheet     = getString(jsonObject, "excelSheet");
        String excelCell      = getString(jsonObject, "excelCell");
        String excelCellType  = getString(jsonObject, "excelCellType");
        String excelDefault   = getString(jsonObject, "excelDefault");
        
        Button check = new Button(parent, SWT.CHECK);

        if (logger.isTraceEnabled()) {
            logger.trace("      name = " + name);
            logger.trace("      variable = " + variable);
            logger.trace("      values = " + values);
            logger.trace("      default = " + debugValue(defaultText, ""));
            logger.trace("      forceDefault = " + debugValue(forceDefault, false));
            logger.trace("      x = " + debugValue(x, 0));
            logger.trace("      y = " + debugValue(y, 0));
            logger.trace("      width = " + debugValue(width, 0));
            logger.trace("      height = " + debugValue(height, 0));
            logger.trace("      background = " + debugValue(background, ""));
            logger.trace("      foreground = " + debugValue(foreground, ""));
            logger.trace("      alignment = "+debugValue(alignment, ""));
            logger.trace("      tooltip = " + debugValue(tooltip, ""));
            logger.trace("      whenEmpty = " + debugValue(whenEmpty, ""));
            logger.trace("      excelSheet = " + debugValue(excelSheet, ""));
            logger.trace("      excelCell = " + debugValue(excelCell, ""));
            logger.trace("      excelCellType = " + debugValue(excelCellType, ""));
            logger.trace("      excelDefault = " + debugValue(excelDefault, ""));
        }
        
        check.setData("name", name);
        check.setData("variable", variable);
        check.setData("values", values);
        check.setData("default", defaultText);
        check.setData("forceDefault", forceDefault);
        check.setData("x", x);
        check.setData("y", y);
        check.setData("width", width);
        check.setData("height", height);
        check.setData("background", background);
        check.setData("foreground", foreground);
        check.setData("alignment", alignment);
        check.setData("tooltip", tooltip);
        check.setData("whenEmpty", whenEmpty);
        check.setData("excelSheet", excelSheet);
        check.setData("excelCell", excelCell);
        check.setData("excelCellType", excelCellType);
        check.setData("excelDefault", excelDefault);
        check.setData("editable keys", new String[]{"name", "variable", "values", "defaultText", "forceDefault",  "x", "y", "width", "height", "background", "foreground", "alignment", "tooltip", "whenEmpty", "excelSheet", "excelCell", "excelCellType", "excelDefault"});
        
        // x, y, width, height
        Point p = check.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if ( x == null      || x < 0 )       x = 0;
        if ( y == null      || y < 0 )       y = 0;
        if ( width == null  || width <= 0  ) width = p.x;
        if ( height == null || height <= 0 ) height = p.y;
		check.setBounds(x, y, width, height);
		
		// foreground, background
		FormPlugin.setColor(check, foreground, SWT.FOREGROUND);
		FormPlugin.setColor(check, background, SWT.BACKGROUND);
		
		// alignment
		FormPlugin.setAlignment(check, alignment);
		
        // tooltip
		if ( !FormPlugin.isEmpty(tooltip) )
			check.setToolTipText(tooltip);
                
        // excelCellType
        if ( !FormPlugin.isEmpty((String)excelCellType) && !inArray(FormDialog.validExcelCellType, excelCellType))
       		FormPlugin.error(FormPosition.getPosition("excelCellType") + "\n\nInvalid excelCellType value \""+excelCellType+"\" (valid values are "+FormDialog.validExcelCellType+").");
        
        // ExcelDefault
        if ( !FormPlugin.isEmpty(excelDefault) && !inArray(FormDialog.validExcelDefault, excelDefault))
        	FormPlugin.error(FormPosition.getPosition("excelDefault") + "\n\nInvalid excelDefault value \""+excelDefault+"\" (valid values are "+FormDialog.validExcelDefault+").");
        
        // WhenEmpty
        if ( !FormPlugin.isEmpty((String)whenEmpty) && !inArray(FormDialog.validWhenEmpty, whenEmpty))
        	FormPlugin.error(FormPosition.getPosition("whenEmpty") + "\n\nInvalid value \""+whenEmpty+"\" (valid values are "+FormDialog.validWhenEmpty+").");
   
        return check;
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
     * @param obj the JSONObject
     * @param key the key to search for (case insensitive)
     * @param defaultValue the value to return in case the value is not found
     * @return the value corresponding to the key if it has been found, else the defaultValue. The returned value is an <i>object</i> and therefore might be cast.
     */
    /*
    @SuppressWarnings("unchecked")
	public static Object getJSON(JSONObject obj, String key, Object defaultValue) {
        Iterator<String> iter = obj.keySet().iterator();
        while (iter.hasNext()) {
            String key1 = iter.next();
            if (key1.equalsIgnoreCase(key)) {
                return obj.get(key1);
            }
        }
        return defaultValue;
    }
    */

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

    public String debugValue(Object value, Object defaultValue) {
        if (value == null) {
        	if ( defaultValue == null )
        		return "null";
        	else
        		return "null (defaults to "+String.valueOf(defaultValue)+")";
        } else
            return String.valueOf(value);
    }
    
    public boolean inArray(String[] stringArray, String string) {
        for (String s : stringArray) {
            if (FormPlugin.areEqual(s, string))
                return true;
        }
        return false;
    }
}
