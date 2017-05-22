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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
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
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.StyledText;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.archimatetool.editor.model.commands.EObjectFeatureCommand;
import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * Create a Dialog with graphical controls as described in the configuration file
 * 
 * @author Herve Jouin
 *
 */
public class FormDialog extends Dialog {
	//TODO: add a "continueonerror" property
	private static final FormLogger logger = new FormLogger(FormDialog.class);

	private EObject selectedObject;

	private Color badValueColor= new Color(Display.getCurrent(), 255, 0, 0);
	private Color goodValueColor = new Color(Display.getCurrent(), 0, 100, 0);

	private Shell dialog = null;
	private TabFolder tabFolder;

	private HashSet<String> excelSheets = new HashSet<String>();

	public FormDialog(JSONObject json, EObject selectedObject) {
		super(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		String configFilename;
		this.selectedObject = selectedObject;


		if ( logger.isDebugEnabled() )
			logger.debug("Creating new FormDialog for "+selectedObject.getClass().getSimpleName()+" \""+((INameable)selectedObject).getName()+"\".");

		try {
			configFilename = Paths.get(FormPlugin.configFilePath).toRealPath().toString();
		} catch (IOException e1) {
			configFilename = FormPlugin.configFilePath;
		}

		try {
			createContents(json);
		} catch (IOException e) {
			popup(Level.ERROR, "I/O Error while reading configuration file \""+configFilename+"\"",e);
			if ( dialog != null ) dialog.dispose();
			return;
		} catch (ParseException e) {
			popup(Level.ERROR, "Parsing error while reading configuration file \""+configFilename+"\"",e);
			if ( dialog != null ) dialog.dispose();
			return;
		}  catch (ClassCastException e) {
			FormDialog.popup(Level.ERROR, "Wrong key type in the configuration files:\n"+configFilename,e);
		} catch (RuntimeException e) {
			popup(Level.ERROR, "Please check your configuration file.", e);
			if ( dialog != null ) dialog.dispose();
			return;
		}

		dialog.open();
		dialog.layout();
	}

	private final String[] whenEmptyValidStrings = new String[] { "ignore", "create", "delete"};

	private String dialogName;
	
	private final int defaultDialogWidth = 850;
	private final int defaultDialogHeight = 600;
	private final int defaultDialogSpacing = 4;
	private final String defaultDialogName = "Form plugin";
	private final String defaultDialogBackground = null;
	private final String defaultTabBackground = null;
	private final int defaultButtonWidth = 90;
	private final int defaultButtonHeight = 25;
	private final String defaultButtonOkText = "Ok";
	private final String defaultButtonCancelText = "Cancel";
	private final String defaultButtonExportText = "Export to Excel";
	private final String defaultTabName = "tab";
	private String globalWhenEmpty = null;

	/**
	 * Parses the configuration file and create the corresponding graphical controls
	 */
	private void createContents(JSONObject form) throws IOException, ParseException {
		dialogName = expand(getString(form, "name", defaultDialogName), selectedObject);
		int dialogWidth = getInt(form, "width", defaultDialogWidth);
		int dialogHeight = getInt(form, "height", defaultDialogHeight);
		int dialogSpacing = getInt(form, "spacing", defaultDialogSpacing);
		String dialogBackground = getString(form, "background", defaultDialogBackground);
		int buttonWidth = getInt(form, "buttonWidth", defaultButtonWidth);
		int buttonHeight = getInt(form, "buttonHeight", defaultButtonHeight);
		String buttonOkText = expand(getString(form, "buttonOk", defaultButtonOkText), selectedObject);
		String buttonCancelText = expand(getString(form, "buttonCancel", defaultButtonCancelText), selectedObject);
		String buttonExportText = expand(getString(form, "buttonExport", defaultButtonExportText), selectedObject);
		globalWhenEmpty = getString(form, "whenEmpty", null);

		if ( logger.isTraceEnabled() ) {
			logger.trace("   name = " + debugValue(dialogName, defaultDialogName));
			logger.trace("   width = " + debugValue(dialogWidth, defaultDialogWidth));
			logger.trace("   height = " + debugValue(dialogHeight, defaultDialogHeight));
			logger.trace("   spacing = " + debugValue(dialogSpacing, defaultDialogSpacing));
			logger.trace("   background = " + debugValue(dialogBackground, defaultDialogBackground));
			logger.trace("   width = " + debugValue(buttonWidth, defaultButtonWidth));
			logger.trace("   height = " + debugValue(buttonHeight, defaultButtonHeight));
			logger.trace("   ok = " + debugValue(buttonOkText, defaultButtonOkText));
			logger.trace("   cancel = " + debugValue(buttonCancelText, defaultButtonCancelText));
			logger.trace("   whenEmpty = " + debugValue(globalWhenEmpty, null));
		}

		if ( globalWhenEmpty != null ) {
			globalWhenEmpty = globalWhenEmpty.toLowerCase();
			if ( !inArray(whenEmptyValidStrings, globalWhenEmpty) )
				throw new RuntimeException("Invalid value \""+globalWhenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
		}

		dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText(expand(dialogName, selectedObject));
		dialog.setLayout(null);

		int tabFolderWidth  =  dialogWidth - dialogSpacing*2;
		int tabFolderHeight =  dialogHeight - dialogSpacing*3 - buttonHeight;

		dialog.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - dialogWidth) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogHeight) / 4, dialogWidth, dialogHeight);
		// we resize the dialog because we want the width and height to be the client's area width and height
		Rectangle area = dialog.getClientArea();
		dialog.setSize(dialogWidth*2 - area.width, dialogHeight*2 - area.height);

		if ( dialogBackground != null ) {
			String[] colorArray = dialogBackground.split(",");
			dialog.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		tabFolder = new TabFolder(dialog, SWT.BORDER);
		tabFolder.setBounds(dialogSpacing, dialogSpacing, tabFolderWidth, tabFolderHeight);

		Button cancelButton = new Button(dialog, SWT.NONE);
		cancelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth, tabFolderHeight + dialogSpacing*2, buttonWidth, buttonHeight);
		cancelButton.setText(buttonCancelText);
		cancelButton.setEnabled(true);
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
			public void widgetDefaultSelected(SelectionEvent e) { cancel(); }
		});

		Button okButton = new Button(dialog, SWT.NONE);
		okButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth*2 - dialogSpacing, tabFolderHeight + dialogSpacing*2, buttonWidth, buttonHeight);
		okButton.setText(buttonOkText);
		okButton.setEnabled(true);
		okButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
			public void widgetDefaultSelected(SelectionEvent e) { ok(); }
		});

		createTabs(form, tabFolder);

		// If there is at lease one Excel sheet specified, then we show up the "export to Excel" button
		if ( !excelSheets.isEmpty() ) {
			Button exportToExcelButton = new Button(dialog, SWT.NONE);
			exportToExcelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth*3 - dialogSpacing*2, tabFolderHeight + dialogSpacing*2, buttonWidth, buttonHeight);
			exportToExcelButton.setText(buttonExportText);
			exportToExcelButton.setEnabled(true);
			exportToExcelButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
				public void widgetDefaultSelected(SelectionEvent e) { exportToExcel(); }
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

			String tabName = expand(getString(tab, "name", defaultTabName), selectedObject);

			if ( logger.isDebugEnabled() )
				logger.debug("Creating tab " + debugValue(tabName, defaultTabName));

			String tabBackground = getString(tab, "background", defaultTabBackground);

			if ( logger.isTraceEnabled() )
				logger.trace("   tab background = " + debugValue(tabBackground, defaultTabBackground));

			TabItem tabItem = new TabItem(tabFolder, SWT.MULTI);
			tabItem.setText(tabName);
			Composite composite = new Composite(tabFolder, SWT.NONE);			
			tabItem.setControl(composite);

			if ( tabBackground != null ) {
				String[] colorArray = tabBackground.split(",");
				composite.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
			}

			createObjects(tab, composite);
			composite.layout();
		}
	}

	/**
	 * Creates the dialog controls. The following controls are currently managed :<br>
	 * <li>label
	 * <li>table
	 * <br><br>
	 * called by the createTabs() method
	 * @param tab The JSON object to parse
	 * @param composite The composite where the control will be created
	 */
	private void createObjects(JSONObject tab, Composite composite) {
		// we iterate over the "controls" entries
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> objectsIterator = getJSONArray(tab, "controls").iterator();	
		while (objectsIterator.hasNext()) {
			JSONObject jsonObject = objectsIterator.next();

			switch (getString(jsonObject, "class")) {
				case "label" : createLabel(jsonObject, composite); break;
				case "table" : createTable(jsonObject, composite); break;
				case "text" :  createText(jsonObject, composite); break;
				case "combo": createCombo(jsonObject, composite); break;
				case "check": createCheck(jsonObject, composite); break;
				default : throw new RuntimeException("Do not know how to create "+jsonObject.get("class"));
			}
		}
	}

	/**
	 * Create a Label control<br> 
	 * <br>
	 * called by the createObjects() method
	 * @param jsonObject the JSON object to parse
	 * @param composite the composite where the control will be created
	 */
	private Label createLabel(JSONObject jsonObject, Composite composite) {
		String labelText = expand(getString(jsonObject, "text", "label"), selectedObject);

		if ( logger.isDebugEnabled() )
			logger.debug("   Creating label control " + debugValue(labelText, "label"));

		Label label =  new Label(composite, SWT.NONE);		// we create the label at the very beginning because we need its default size wich is dependent on its content
		label.setText(labelText);
		label.pack();

		int x = getInt(jsonObject, "x", 0);
		int y = getInt(jsonObject, "y", 0);
		int width = getInt(jsonObject, "width", label.getSize().x);
		int height = getInt(jsonObject, "height", label.getSize().y);
		String background = getString(jsonObject, "background", null);
		String foreground = getString(jsonObject, "foreground", null);
		String tooltip = getString(jsonObject, "tooltip", null);
		String excelSheet = getString(jsonObject, "excelSheet", null);
		String excelCell = getString(jsonObject, "excelCell", null);

		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = " + debugValue(x, 0));
			logger.trace("      y = " + debugValue(y, 0));
			logger.trace("      width = " + debugValue(width, label.getSize().x));
			logger.trace("      height = " + debugValue(height, label.getSize().y));
			logger.trace("      background = " + debugValue(background, null));
			logger.trace("      foreground = " + debugValue(foreground, null));
			logger.trace("      tooltip = " + debugValue(tooltip, null));
			logger.trace("      excelSheet = " + debugValue(excelSheet, null));
			logger.trace("      excelCell = " + debugValue(excelCell, null));
		}

		label.setLocation(x, y);
		label.setSize(width, height);

		if ( background != null ) {
			String[] colorArray = background.split(",");
			label.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		} else {
			label.setBackground(composite.getBackground());
		}

		if ( foreground != null ) {
			String[] colorArray = foreground.split(",");
			label.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			label.setData("excelSheet", excelSheet);
			label.setData("excelCell", excelCell);
		}

		if ( tooltip == null ) {
			label.setToolTipText(tooltip);
		}

		return label;
	}

	/**
	 * Create a text control<br> 
	 * <br>
	 * called by the createObjects() method
	 * @param jsonObject the JSON object to parse
	 * @param composite the composite where the control will be created
	 */
	private StyledText createText(JSONObject jsonObject, Composite composite) {
		String variableName = getString(jsonObject, "variable");
		String variableValue = getVariable(variableName, selectedObject);					// can be null
		String defaultText = expand(getString(jsonObject, "default", ""), selectedObject);	// can be empty but 

		if ( variableValue == null )
			variableValue = defaultText;

		if ( logger.isDebugEnabled() ) logger.debug("   Creating Text control");

		StyledText text =  new StyledText(composite, SWT.BORDER);		// we create the label at the very beginning because we need its default size wich is dependent on its content
		text.setText(variableValue);
		text.pack();

		int x = getInt(jsonObject, "x", 0);
		int y = getInt(jsonObject, "y", 0);
		int width = getInt(jsonObject, "width", text.getSize().x);
		int height = getInt(jsonObject, "height", text.getSize().y);
		String background = getString(jsonObject, "background", null);
		String foreground = getString(jsonObject, "foreground", null);
		String regex = getString(jsonObject, "regexp", null);
		String tooltip = getString(jsonObject, "tooltip", null);
		String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
		String excelSheet = getString(jsonObject, "excelSheet", null);
		String excelCell = getString(jsonObject, "excelCell", null);

		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = " + debugValue(x, 0));
			logger.trace("      y = " + debugValue(y, 0));
			logger.trace("      width = " + debugValue(width, text.getSize().x));
			logger.trace("      height = " + debugValue(height, text.getSize().y));
			logger.trace("      variable = " + variableName);
			logger.trace("      default = " + debugValue(defaultText, ""));
			logger.trace("      background = " + debugValue(background, null));
			logger.trace("      foreground = " + debugValue(foreground, null));
			logger.trace("      regexp = " + debugValue(regex, null));
			logger.trace("      tooltip = " + debugValue(tooltip, null));
			logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
			logger.trace("      excelSheet = " + debugValue(excelSheet, null));
			logger.trace("      excelCell = " + debugValue(excelCell, null));
		}

		if ( whenEmpty != null ) {
			whenEmpty = whenEmpty.toLowerCase();
			if ( !inArray(whenEmptyValidStrings, whenEmpty) )
				throw new RuntimeException("Invalid value \""+whenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
		}

		text.setLocation(x, y);
		text.setSize(width, height);

		if ( background != null ) {
			String[] colorArray = background.split(",");
			text.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		if ( foreground != null ) {
			String[] colorArray = foreground.split(",");
			text.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		text.setData("variable", variableName);
		text.setData("eObject", selectedObject);
		text.setData("whenEmpty", whenEmpty);

		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			text.setData("excelSheet", excelSheet);
			text.setData("excelCell", excelCell);
		}

		if ( regex != null ) {
			text.setData("pattern", Pattern.compile(regex));
			if ( tooltip == null ) {
				text.setToolTipText("Your text should match the following regex :\n"+regex);
			} else {
				text.setToolTipText(tooltip);
			}
		} else {
			if ( tooltip == null ) {
				text.setToolTipText(tooltip);
			}
		}

		text.addModifyListener(textModifyListener);

		return text;
	}

	/**
	 * Create a Combo control<br> 
	 * <br>
	 * called by the createObjects() method
	 * @param jsonObject the JSON object to parse
	 * @param composite the composite where the control will be created
	 */
	private CCombo createCombo(JSONObject jsonObject, Composite composite) {
		CCombo combo = new CCombo(composite, SWT.NONE);
		combo.setEditable(false);

		@SuppressWarnings("unchecked")
		String[] values = (String[])(getJSONArray(jsonObject, "values")).toArray(new String[0]);
		combo.setItems(values);

		String variable = getString(jsonObject, "variable");
		String value = expand(variable, selectedObject);
		String defaultValue = getString(jsonObject, "default", "");
		if ( value != null && ! value.isEmpty() )
			combo.setText(value);
		else
			combo.setText(defaultValue);
		combo.pack();

		int x = getInt(jsonObject, "x", 0);
		int y = getInt(jsonObject, "y", 0);
		int width = getInt(jsonObject, "width", combo.getSize().x);
		int height = getInt(jsonObject, "height", combo.getSize().y);
		String background = getString(jsonObject, "background", null);
		String foreground = getString(jsonObject, "foreground", null);
		String tooltip = getString(jsonObject, "tooltip", null);
		String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
		String excelSheet = getString(jsonObject, "excelSheet", null);
		String excelCell = getString(jsonObject, "excelCell", null);


		if ( logger.isDebugEnabled() ) logger.debug("   Creating combo \""+variable+"\" ("+value+")");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+x);
			logger.trace("      y = "+y);
			logger.trace("      width = "+debugValue(width, combo.getSize().x));
			logger.trace("      height = "+debugValue(height, combo.getSize().y));
			logger.trace("      background = "+debugValue(background, null));
			logger.trace("      foreground = "+debugValue(foreground, null));
			logger.trace("      values = "+values);
			logger.trace("      default = "+debugValue(defaultValue, ""));
			logger.trace("      tooltip = "+debugValue(tooltip, null));
			logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
			logger.trace("      excelSheet = "+debugValue(excelSheet, null));
			logger.trace("      excelCell = "+debugValue(excelCell, null));
		}

		if ( whenEmpty != null ) {
			whenEmpty = whenEmpty.toLowerCase();
			if ( !inArray(whenEmptyValidStrings, whenEmpty) )
				throw new RuntimeException("Invalid value \""+whenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
		}

		combo.setLocation(x, y);
		combo.setSize(width, height);

		if ( background != null ) {
			String[] colorArray = background.split(",");
			combo.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		if ( foreground != null ) {
			String[] colorArray = foreground.split(",");
			combo.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		combo.setData("variable", getString(jsonObject, "variable"));
		combo.setData("eObject", selectedObject);
		combo.setData("whenEmpty", whenEmpty);

		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			combo.setData("excelSheet", excelSheet);
			combo.setData("excelCell", excelCell);
		}

		if ( tooltip != null ) {
			combo.setToolTipText(tooltip);
		}

		return combo;
	}

	/**
	 * Create a check button control<br> 
	 * <br>
	 * called by the createObjects() method
	 * @param jsonObject the JSON object to parse
	 * @param composite the composite where the control will be created
	 */
	private Button createCheck(JSONObject jsonObject, Composite composite) {
		Button check = new Button(composite, SWT.CHECK);

		String variable = getString(jsonObject, "variable");
		String value = expand(variable, selectedObject);
		@SuppressWarnings("unchecked")
		String[] values = (String[])(getJSONArray(jsonObject, "values")).toArray(new String[0]);
		String defaultValue = getString(jsonObject, "default", "");
		if ( values!=null && values.length!=0 ) {
			check.setData("values", values);
			if ( value.isEmpty() ) {
				// we set the checkbox depending on the "default"
				check.setSelection(values[0].equals(defaultValue));
			} else {
				check.setSelection(values[0].equals(value));
			}
		} else {
			check.setSelection(value!=null);
		}
		check.pack();


		int x = getInt(jsonObject, "x", 0);
		int y = getInt(jsonObject, "y", 0);
		int width = getInt(jsonObject, "width", check.getSize().x);
		int height = getInt(jsonObject, "height", check.getSize().y);
		String background = getString(jsonObject, "background", null);
		String foreground = getString(jsonObject, "foreground", null);
		String tooltip = getString(jsonObject, "tooltip", null);
		String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);
		String excelSheet = getString(jsonObject, "excelSheet", null);
		String excelCell = getString(jsonObject, "excelCell", null);

		//TODO : manage default value
		
		if ( logger.isDebugEnabled() ) logger.debug("   Creating combo \""+variable+"\" ("+value+")");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+debugValue(x, 0));
			logger.trace("      y = "+debugValue(y, 0));
			logger.trace("      width = "+debugValue(width, check.getSize().x));
			logger.trace("      height = "+debugValue(height, check.getSize().y));
			logger.trace("      background = "+debugValue(background, null));
			logger.trace("      foreground = "+debugValue(foreground, null));
			logger.trace("      values = "+values);
			logger.trace("      default = "+debugValue(defaultValue, ""));
			logger.trace("      tooltip = "+debugValue(tooltip, null));
			logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
			logger.trace("      excelSheet = "+debugValue(excelSheet, null));
			logger.trace("      excelCell = "+debugValue(excelCell, null));
		}

		if ( whenEmpty != null ) {
			whenEmpty = whenEmpty.toLowerCase();
			if ( !inArray(whenEmptyValidStrings, whenEmpty) )
				throw new RuntimeException("Invalid value \""+whenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
		}

		check.setLocation(x, y);
		check.setSize(width, height);

		if ( background != null ) {
			String[] colorArray = background.split(",");
			check.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		if ( foreground != null ) {
			String[] colorArray = foreground.split(",");
			check.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		check.setData("variable", getString(jsonObject, "variable"));
		check.setData("eObject", selectedObject);
		check.setData("whenEmpty", whenEmpty);

		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			check.setData("excelSheet", excelSheet);
			check.setData("excelCell", excelCell);
		}

		if ( tooltip != null ) {
			check.setToolTipText(tooltip);
		}

		check.addSelectionListener(checkButtonSelectionListener);

		return check;
	}

	/**
	 * Create a Table control<br> 
	 * <br>
	 * called by the createObjects() method
	 * @param jsonObject the JSON object to parse
	 * @param composite the composite where the control will be created
	 */
	@SuppressWarnings("unchecked")
	private Table createTable(JSONObject jsonObject, Composite composite) {
		int x = getInt(jsonObject, "x", 0);
		int y = getInt(jsonObject, "y", 0);
		int width = getInt(jsonObject, "width", 100);
		int height = getInt(jsonObject, "height", 50);
		String background = getString(jsonObject, "background", null);
		String foreground = getString(jsonObject, "foreground", null);
		String tooltip = getString(jsonObject, "tooltip", null);
		String excelSheet = getString(jsonObject, "excelSheet", null);
		int excelFirstLine = getInt(jsonObject, "excelFirstLine", 1);

		if ( logger.isDebugEnabled() ) logger.debug("   Creating table");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+debugValue(x, 0));
			logger.trace("      y = "+debugValue(y, 0));
			logger.trace("      width = "+debugValue(width, 100));
			logger.trace("      height = "+debugValue(height, 50));
			logger.trace("      background = "+debugValue(background, null));
			logger.trace("      foreground = "+debugValue(foreground, null));
			logger.trace("      tooltip = "+debugValue(tooltip, null));
			logger.trace("      excelSheet = "+debugValue(excelSheet, null));
			logger.trace("      excelFirstLine = "+debugValue(excelFirstLine, 1));
		}

		Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLocation(x, y);
		table.setSize(width, height);

		if ( background != null ) {
			String[] colorArray = background.split(",");
			table.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		if ( foreground != null ) {
			String[] colorArray = foreground.split(",");
			table.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0].trim()),Integer.parseInt(colorArray[1].trim()),Integer.parseInt(colorArray[2].trim())));
		}

		if ( tooltip == null ) {
			table.setToolTipText(tooltip);
		}

		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			table.setData("excelSheet", excelSheet);
			table.setData("excelFirstLine", excelFirstLine);
		}

		// we iterate over the "columns" entries
		Iterator<JSONObject> columnsIterator = (getJSONArray(jsonObject, "columns")).iterator();
		while (columnsIterator.hasNext()) {
			JSONObject column = columnsIterator.next();

			String columnName = getString(column, "name", "(no name)");
			String columnClass = getString(column, "class");
			String columnTooltip = getString(column, "tooltype", null);
			int columnWidth = getInt(column, "width", (10+columnName.length()*8));
			String excelColumn = getString(column, "excelColumn", null);
			String excelCellType = getString(column, "excelCellType", null);
			String excelDefault = getString(column, "excelDefault", null);

			if ( logger.isDebugEnabled() ) logger.debug("   Creating column \"" + columnName + "\" of class \"" + columnClass + "\"");
			if ( logger.isTraceEnabled() ) {
				logger.trace("      width = "+debugValue(columnWidth, (10+columnName.length()*8)));
				logger.trace("      tooltip = "+debugValue(columnTooltip, null));
				logger.trace("      excelColumn = "+debugValue(excelColumn, null));
				logger.trace("      excelCellType = "+debugValue(excelCellType, null));
				logger.trace("      excelDefault = "+debugValue(excelDefault, null));
			}

			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText(columnName);
			tableColumn.setAlignment(SWT.CENTER);
			tableColumn.setWidth(columnWidth);
			tableColumn.setResizable(columnWidth!=0);
			tableColumn.setData("class", columnClass);
			tableColumn.setData("excelColumn", excelColumn);
			tableColumn.setData("excelCellType", excelCellType == null ? "string" : excelCellType.toLowerCase());
			tableColumn.setData("excelDefault", excelDefault == null ? "nothing" : excelDefault.toLowerCase());
			tableColumn.addListener(SWT.Selection, sortListener);

			if ( columnTooltip == null ) {
				tableColumn.setToolTipText(columnTooltip);
			}

			switch ( columnClass.toLowerCase() ) {
				case "check":
					if ( JSONContainsKey(column, "values") ) {
						String[] values = (String[])(getJSONArray(column, "values")).toArray(new String[0]);
						String defaultValue = getString(column, "default", null);
						String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);

						if ( logger.isTraceEnabled() ) {
							logger.trace("      values = " + values);
							logger.trace("      default = " + debugValue(defaultValue, null));
							logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
						}

						if ( whenEmpty != null ) {
							whenEmpty = whenEmpty.toLowerCase();
							if ( !inArray(whenEmptyValidStrings, whenEmpty) )
								throw new RuntimeException("Invalid value \""+whenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
						}

						tableColumn.setData("values", values);
						tableColumn.setData("default", defaultValue);
						tableColumn.setData("whenEmpty", whenEmpty);
					}
					break;
				case "combo":
					if ( column.containsKey("values") ) {
						String[] values = (String[])(getJSONArray(column, "values")).toArray(new String[0]);
						String defaultValue = getString(column, "default", null);
						Boolean editable =  getBoolean(column, "editable", null);
						String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);

						if ( logger.isTraceEnabled() ) {
							logger.trace("      values = "+values);
							logger.trace("      default = "+debugValue(defaultValue, null));
							logger.trace("      editable = "+debugValue(editable, null));
							logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
						}

						if ( whenEmpty != null ) {
							whenEmpty = whenEmpty.toLowerCase();
							if ( !inArray(whenEmptyValidStrings, whenEmpty) )
								throw new RuntimeException("Invalid value \""+whenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
						}

						tableColumn.setData("values", values);
						tableColumn.setData("default", defaultValue);
						tableColumn.setData("editable", editable);
						tableColumn.setData("whenEmpty", whenEmpty);
					} else {
						throw new RuntimeException("Missing attribute \"values\" to table column type \"combo\".");
					}
					break;
				case "label":
					break;
				case "text":
					String regexp = getString(column, "regexp", null);
					String defaultValue = getString(column, "default", null);
					String whenEmpty = getString(jsonObject, "whenEmpty", globalWhenEmpty);

					if ( logger.isTraceEnabled() ) {
						logger.trace("      regexp = "+debugValue(regexp, null));
						logger.trace("      default = "+debugValue(defaultValue, null));
						logger.trace("      whenEmpty = " + debugValue(whenEmpty, globalWhenEmpty));
					}

					if ( whenEmpty != null ) {
						whenEmpty = whenEmpty.toLowerCase();
						if ( !inArray(whenEmptyValidStrings, whenEmpty) )
							throw new RuntimeException("Invalid value \""+whenEmpty+"\" for whenEmpty property.\n\nValid values are \"ignore\", \"create\" and \"delete\".");
					}

					tableColumn.setData("regexp", regexp);
					tableColumn.setData("default", defaultValue);
					tableColumn.setData("whenEmpty", whenEmpty);
					break;
				default : throw new RuntimeException("Unknown column class \""+getString(column, "class")+"\" in table.\n\nValid types are \"check\", \"combo\", \"label\" and \"text\".");
			}
		}

		table.setSortColumn(table.getColumn(0));
		table.setSortDirection(SWT.UP);

		// we iterate over the "lines" entries
		JSONArray lines = getJSONArray(jsonObject, "lines");
		if ( lines != null ) {
			Iterator<JSONObject> linesIterator = lines.iterator();
			while (linesIterator.hasNext()) {
				JSONObject line = linesIterator.next();

				if ( getJSON(line, "generate", null) == null ) {
					// static line
					if ( logger.isTraceEnabled() ) logger.trace("      Creating static line");
					addTableItem(table, selectedObject, getJSONArray(line, "cells"));
				} else {
					// we create one line per entry in getChildren()
					if ( logger.isTraceEnabled() ) logger.trace("      Generating dynamic lines");
					if ( selectedObject instanceof IArchimateDiagramModel ) {
						addTableItems(table, ((IArchimateDiagramModel)selectedObject).getChildren(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
					} else
						if ( selectedObject instanceof IDiagramModelContainer ) {
							addTableItems(table, ((IDiagramModelContainer)selectedObject).getChildren(), getJSONArray(line, "cells"), getJSONObject(line, "filter"));
						} else
							throw new RuntimeException("Cannot generate lines for selected component is it is not a container.");
				}
			}
		}
		return table;
	}



	/**
	 * Create TableItems in the Table control<br> 
	 * <br>
	 * called by the createTable() method
	 * @param table the table in which the tableItems will be creates
	 * @param list the list of objects corresponding to the tableItems to create
	 * @param values the JSON array representing the variables used to fill in the tableItem columns
	 * @param filter the JSONObject representing a filter if any
	 */
	@SuppressWarnings("unchecked")
	private void addTableItems(Table table, EList<?> list, JSONArray values, JSONObject filter) {
		if ( (list == null) || list.isEmpty() )
			return;

		if ( list.get(0) instanceof IDiagramModelObject ) {
			for ( IDiagramModelObject diagramObject: (EList<IDiagramModelObject>)list ) {
				if ( logger.isTraceEnabled() ) logger.trace("Found diagram object "+diagramObject.getName());
				if ( checkFilter((EObject)diagramObject, filter) ) {
					if (diagramObject instanceof IDiagramModelArchimateObject)
						addTableItem(table, (EObject)(((IDiagramModelArchimateObject)diagramObject).getArchimateElement()), values);
					else
						addTableItem(table, (EObject)diagramObject, values);
				}

				addTableItems(table, diagramObject.getSourceConnections(), values, filter);

				if ( diagramObject instanceof IDiagramModelArchimateObject ) {
					addTableItems(table, ((IDiagramModelArchimateObject)diagramObject).getChildren(), values, filter);
				}
			}
		} else if ( list.get(0) instanceof IDiagramModelArchimateConnection ) {
			for ( IDiagramModelArchimateConnection diagramConnection: (EList<IDiagramModelArchimateConnection>)list ) {
				if ( logger.isTraceEnabled() ) logger.trace("Found diagram connection "+diagramConnection.getName());
				if ( checkFilter((EObject)diagramConnection, filter) ) {
					addTableItem(table, (EObject)diagramConnection.getArchimateRelationship(), values);
				}
			}
		} else if ( list.get(0) instanceof IDiagramModelConnection ) {
			// do nothing
		} else {
			throw new RuntimeException("Unknown object class \""+list.get(0).getClass().getSimpleName()+"\"");
		}
	}

	/**
	 * Adds a line (TableItem) in the Table<br>
	 * @param table the table in which create the lines
	 * @param jsonArray the array of JSONObjects that contain the values to insert (one per column) 
	 */
	private void addTableItem(Table table, EObject eObject, JSONArray jsonArray) {
		TableItem tableItem = new TableItem(table, SWT.NONE);

		// we need to store the widgets to retreive them later on
		TableEditor[] editors= new TableEditor[jsonArray.size()];

		logger.trace("   adding line for object : "+((INameable)eObject).getName());

		for ( int columnNumber=0; columnNumber<jsonArray.size(); ++columnNumber) {
			String itemText = expand((String)jsonArray.get(columnNumber), eObject);

			logger.trace("      adding "+((String)table.getColumn(columnNumber).getData("class")).toLowerCase()+" column with value \""+itemText+"\"");

			TableEditor editor;
			switch ( ((String)table.getColumn(columnNumber).getData("class")).toLowerCase() ) {
				case "label" : 
					tableItem.setText(columnNumber, itemText);
					editors[columnNumber] = null;
					break;

				case "text" :
					editor = new TableEditor(table);
					StyledText text = new StyledText(table, SWT.NONE);
					if ( itemText.isEmpty() ) {
						String defaultValue = (String)table.getColumn(columnNumber).getData("default");
						if ( defaultValue != null )
							itemText = defaultValue;
					}
					text.setText(itemText);
					text.setToolTipText((String)table.getColumn(columnNumber).getData("tooltip"));
					text.setData("whenEmpty", table.getColumn(columnNumber).getData("whenEmpty"));
					text.setData("eObject", eObject);
					text.setData("variable", (String)jsonArray.get(columnNumber));

					if ( table.getColumn(columnNumber).getData("regexp") != null ) {
						String regex = (String)table.getColumn(columnNumber).getData("regexp");
						text.setData("pattern", Pattern.compile(regex));
						if ( table.getColumn(columnNumber).getData("tooltip") == null ) {
							text.setToolTipText("Your text shoud match the following regex :\n"+regex);
						}
						text.addModifyListener(textModifyListener);
					}

					editor.grabHorizontal = true;
					editor.setEditor(text, tableItem, columnNumber);
					editors[columnNumber] = editor;
					break;

				case "combo":
					editor = new TableEditor(table);
					CCombo combo = new CCombo(table, SWT.NONE);
					if ( itemText.isEmpty() ) {
						String defaultValue = (String)table.getColumn(columnNumber).getData("default");
						if ( defaultValue != null )
							itemText = defaultValue;
					}
					combo.setText(itemText);
					combo.setItems((String[])table.getColumn(columnNumber).getData("values"));
					combo.setToolTipText((String)table.getColumn(columnNumber).getData("tooltip"));
					combo.setData("whenEmpty", table.getColumn(columnNumber).getData("whenEmpty"));
					combo.setData("eObject", eObject);
					combo.setData("variable", (String)jsonArray.get(columnNumber));
					Boolean editable = (Boolean)table.getColumn(columnNumber).getData("editable");
					combo.setEditable(editable!=null && editable);

					editor.grabHorizontal = true;
					editor.setEditor(combo, tableItem, columnNumber);
					editors[columnNumber] = editor;
					break;

				case "check":
					editor = new TableEditor(table);
					Button check = new Button(table, SWT.CHECK);
					check.pack();
					editor.minimumWidth = check.getSize().x;
					editor.horizontalAlignment = SWT.CENTER;
					check.setData("whenEmpty", table.getColumn(columnNumber).getData("whenEmpty"));
					check.setData("eObject", eObject);
					check.setData("variable", (String)jsonArray.get(columnNumber));

					String[] values = (String[])table.getColumn(columnNumber).getData("values");
					if ( itemText.isEmpty() ) {
						String defaultValue = (String)table.getColumn(columnNumber).getData("default");
						if ( defaultValue != null )
							itemText = defaultValue;
					}
					if ( values!=null && values.length!=0 ) {
						check.setData("values", table.getColumn(columnNumber).getData("values"));
						check.setSelection(values[0].equals(itemText));
					} else {
						check.setSelection(!itemText.isEmpty());
					}

					check.addSelectionListener(checkButtonSelectionListener);

					editor.setEditor(check, tableItem, columnNumber);
					editors[columnNumber] = editor;
					break;

				default : throw new RuntimeException("Unknown object type \""+((String)table.getColumn(columnNumber).getData("class"))+"\".");
			}
		}
		tableItem.setData("editors", editors);
	}

	/**
	 * Shows up an on screen popup displaying the message and wait for the user to click on the "OK" button
	 */
	public static void popup(Level level, String msg) {
		popup(level,msg,null);
	}

	// the popupMessage is a class variable because it will be used in an asyncExec() method.
	private static String popupMessage;
	/**
	 * Shows up an on screen popup, displaying the message (and the exception message if any) and wait for the user to click on the "OK" button<br>
	 * The exception stacktrace is also printed on the standard error stream
	 */
	public static void popup(Level level, String msg, Exception e) {
		popupMessage = msg;
		logger.log(FormDialog.class, level, msg, e);

		if ( e != null ) {
			if ( (e.getMessage()!=null) && !e.getMessage().equals(msg)) {
				popupMessage += "\n\n" + e.getMessage();
			} else {
				popupMessage += "\n\n" + e.getClass().getName();
			}
		}

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				switch ( level.toInt() ) {
					case Level.FATAL_INT :
					case Level.ERROR_INT :
						MessageDialog.openError(Display.getDefault().getActiveShell(), FormPlugin.pluginTitle, popupMessage);
						break;
					case Level.WARN_INT :
						MessageDialog.openWarning(Display.getDefault().getActiveShell(), FormPlugin.pluginTitle, popupMessage);
						break;
					default :
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), FormPlugin.pluginTitle, popupMessage);
						break;
				}
			}
		});
	}

	/**
	 * Shows up an on screen popup displaying the question (and the exception message if any)  and wait for the user to click on the "YES" or "NO" button<br>
	 * The exception stacktrace is also printed on the standard error stream
	 * 
	 * @param msg
	 * @return true or false
	 */
	public static boolean question(String msg) {
		if ( logger.isDebugEnabled() ) logger.debug("question : "+msg);
		boolean result = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), FormPlugin.pluginTitle, msg);
		if ( logger.isDebugEnabled() ) logger.debug("answer : "+result);
		return result;
	}

	/**
	 * shows up an on screen popup displaying the message but does not wait for any user input<br>
	 * it is the responsibility of the caller to dismiss the popup 
	 */
	private static Shell dialogShell = null;
	private static Label dialogLabel = null;
	public static Shell popup(String msg) {
		if ( dialogShell == null ) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					dialogShell = new Shell(Display.getCurrent(), SWT.BORDER | SWT.APPLICATION_MODAL);
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

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				for ( Shell shell: Display.getCurrent().getShells() ) {
					shell.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
				}
				dialogShell.setText(msg);
				dialogLabel.setText(msg);
				dialogShell.open();
			}
		});
		return dialogShell;
	}

	public static void closePopup() {
		if ( dialogShell != null ) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					dialogShell.close();
					dialogShell = null;
					for ( Shell shell: Display.getCurrent().getShells() ) {
						shell.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
					}
				}
			});
		}
	}

	ModifyListener textModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			StyledText widget = (StyledText)e.widget;
			String text = widget.getText();

			// if a regex has been provided, we change the text color to show if it matches
			Pattern pattern = (Pattern)e.widget.getData("pattern");
			if ( pattern != null ) {
				widget.setStyleRange(new StyleRange(0, widget.getText().length(), pattern.matcher(text).matches() ? goodValueColor : badValueColor, null));
			}
		}
	};

	SelectionListener checkButtonSelectionListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			Button widget = (Button)e.widget;
			String[] values = (String[])widget.getData("values");
			if ( values==null || values.length==0)
				values=new String[]{"", null};
			if ( values.length == 1 )
				values=new String[]{values[0], null};

			String text = values[widget.getSelection()?0:1];

			logger.trace("check field modified for variable "+(String)widget.getData("variable")+". new value : \""+text+"\"");

			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// setAttribute((EObject)widget.getData("eObject"), (String)widget.getData("variable"), text);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
	};



	/**
	 * Set the value of a variable<br>
	 * <br>
	 * The variable name can be :<br>
	 *    - $documentation	sets the documentation of the eObject<br>
	 *    - $property:xxx 	deletes the property is value is null, else sets the property value (create the property if needed)<br>
	 * <br>
	 * This method does not throw exceptions as it is mainly called by SWT which won't know what to do with these exceptions.<br>
	 * Instead, it opens a popup to display the error message.
	 */
	private void setVariable(EObject eObject, String variable, String value) {
		if ( logger.isTraceEnabled() ) logger.trace("setting variable \""+variable+"\"");

		// we check that the variable provided is a string enclosed between "${" and "}"
		Pattern pattern = Pattern.compile("^\\$\\{[^}]+}$");
		Matcher matcher = pattern.matcher(variable);
		if ( !matcher.matches() )
			throw new RuntimeException("The expression \""+variable+"\" is not a variable. It should be enclosed between \"${\" and \"}\"");

		String variableName = variable.substring(2, variable.length()-1);
		Command cmd;

		switch ( variableName.toLowerCase() ) {
			case "class" :	// TODO: show error message
				break;		// we refuse to change the class of an eObject

			case "id" :		// TODO: show error message
				break;		// we refuse to change the ID of an eObject

			case "documentation" :
				if (eObject instanceof IDocumentable) {
					//((IDocumentable)eObject).setDocumentation(value == null ? "" : value);
		            cmd = new EObjectFeatureCommand(dialogName, eObject, IArchimatePackage.Literals.DOCUMENTABLE__DOCUMENTATION, value == null ? "" : value);
		            if(cmd.canExecute()) {
		                compoundCommand.add(cmd);
		            }
					return;
				}
				else {
					// TODO: show error message
				}
				break;

			case "name" :
				if (eObject instanceof INameable) {
					//((INameable)eObject).setName(value == null ? "" : value);
		            cmd = new EObjectFeatureCommand(dialogName, eObject, IArchimatePackage.Literals.NAMEABLE__NAME, value == null ? "" : value);
		            if(cmd.canExecute()) {
		                compoundCommand.add(cmd);
		            }
		            return;
				} else {
					// TODO: show error message
				}
				break;

			default :
				if ( variableName.startsWith("property:") ) {
					if ( eObject instanceof IDiagramModelArchimateObject )
						eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
					if ( eObject instanceof IDiagramModelArchimateConnection )
						eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
					
					if ( eObject instanceof IProperties ) {
						String propertyName = variableName.substring(9);

						IProperty propertyToUpdate = null;
						for ( IProperty property: ((IProperties)eObject).getProperties() ) {
							if ( property.getKey().equals(propertyName) ) {
								propertyToUpdate = property;
								break;
							}
						}
						// if the property does not (yet) exists
						if ( propertyToUpdate == null ) {
							// if value == null, we do not create a new property
							// if value != null, we create a new property
							if ( value != null ) {
								//IProperty newProperty = IArchimateFactory.eINSTANCE.createProperty();
								//newProperty.setKey(propertyName);
								//newProperty.setValue(value);
								//((IProperties)eObject).getProperties().add(newProperty);
					            cmd = new FormPropertyCommand(dialogName, (IProperties)eObject, IArchimateFactory.eINSTANCE.createProperty()) {
					                @Override
					                public void execute() {
					                    property.setKey(propertyName);
					                    property.setValue(value);
					                	eObject.getProperties().add(property);
					                }
					                
					                @Override
					                public void undo() {
					                	eObject.getProperties().remove(property);
					                }
					            };
					            
					            if(cmd.canExecute()) {
					                compoundCommand.add(cmd);
					            }
							}
						} else {
							// if the property already exists
							
							if ( value == null ) {
								// if value == null, we remove the property
								//((IProperties)eObject).getProperties().remove(propertyToUpdate);
					            cmd = new FormPropertyCommand(dialogName, (IProperties)eObject, propertyToUpdate) {
					                @Override
					                public void execute() {
					                	eObject.getProperties().remove(property);
					                }
					                
					                @Override
					                public void undo() {
					                	eObject.getProperties().add(property);				// TODO; store the property index to add it at the same place !!!
					                }
					            };
					            
					            if(cmd.canExecute()) {
					                compoundCommand.add(cmd);
					            }
							} else {
								// if value != null, we update the property value
								//propertyToUpdate.setKey(propertyName);
								//propertyToUpdate.setValue(value);
					            cmd = new FormPropertyCommand(dialogName, (IProperties)eObject, propertyToUpdate, propertyToUpdate.getValue(), value) {
					                @Override
					                public void execute() {
					                	property.setValue(newValue);
					                }
					                
					                @Override
					                public void undo() {
					                    property.setValue(oldValue);
					                }
					            };
					            
					            if(cmd.canExecute()) {
					                compoundCommand.add(cmd);
					            }
							}
						}
						return;
					} else {
						//TODO : show up an error
					}
				}

				else if ( variableName.startsWith("view:") ) {
					if ( eObject instanceof IDiagramModel ) {
						setVariable(eObject, "${"+variableName.substring(5)+"}", value);
						return;
					}
					else if ( eObject instanceof IDiagramModelArchimateObject ) {
						setVariable((EObject)((IDiagramModelArchimateObject)eObject).getDiagramModel(), "${"+variableName.substring(5)+"}", value);
						return;
					}
					throw new RuntimeException("Cannot set variable \""+variable+"\" as the object is not part of a DiagramModel.");
				}

				else if ( variableName.toLowerCase().startsWith("model:") ) {
					if ( eObject instanceof IArchimateDiagramModel ) {
						setVariable(eObject, "${"+variableName.substring(6)+"}", value);
						return;
					}
					else if ( eObject instanceof IDiagramModelArchimateObject ) {
						setVariable(((IDiagramModelArchimateObject)eObject).getDiagramModel().getArchimateModel(), "${"+variableName.substring(6)+"}", value); ;
						return;
					}
					throw new RuntimeException("Cannot set variable \""+variable+"\" as the object is not part of a model.");
				}
		}
		
		throw new RuntimeException("Do not know how to set variable \""+variableName+"\"");
	}


	private Listener sortListener =  new Listener() {
		public void handleEvent(Event e) {
			// Because of the graphical controls and the tableEditors, it is much more easier and quicker to create a new table
			// rather than add new tableItems and removing the old ones
			Table oldTable = ((TableColumn)e.widget).getParent();
			TableColumn sortedColumn = (TableColumn) e.widget;
			oldTable.setSortColumn(sortedColumn);
			Integer sortDirection = (Integer)sortedColumn.getData("sortDirection");
			if ( sortDirection == null || sortDirection == SWT.DOWN)
				sortDirection = SWT.UP;
			else
				sortDirection = SWT.DOWN;
			sortedColumn.setData("sortDirection", sortDirection);
			logger.trace("set sort direction "+sortDirection);
			oldTable.setSortDirection(sortDirection);

			TableItem[] oldTableItems = oldTable.getItems();

			if ((oldTableItems != null) && (oldTableItems.length > 0)) {

				Table newTable = new Table(oldTable.getParent(), oldTable.getStyle());
				newTable.setLinesVisible(oldTable.getLinesVisible());
				newTable.setHeaderVisible(oldTable.getHeaderVisible());
				newTable.setLocation(oldTable.getLocation());
				newTable.setSize(oldTable.getSize());
				newTable.setLayoutData(oldTable.getLayoutData());
				newTable.setLayout(oldTable.getLayout());

				for ( TableColumn oldTableColumn: oldTable.getColumns() ) {
					TableColumn newTableColumn = new TableColumn(newTable, SWT.NONE);
					newTableColumn.setText(oldTableColumn.getText());
					newTableColumn.setAlignment(oldTableColumn.getAlignment());
					newTableColumn.setWidth(oldTableColumn.getWidth());
					newTableColumn.setResizable(oldTableColumn.getResizable());
					newTableColumn.setData("class", oldTableColumn.getData("class"));
					newTableColumn.setData("tooltip", oldTableColumn.getData("tooltip"));
					newTableColumn.setData("values", oldTableColumn.getData("values"));
					newTableColumn.setData("regexp", oldTableColumn.getData("regexp"));
					newTableColumn.setData("excelColumn", oldTableColumn.getData("excelColumn"));
					newTableColumn.setData("excelCellType", oldTableColumn.getData("excelCellType"));
					newTableColumn.setData("excelDefault", oldTableColumn.getData("excelDefault"));
					newTableColumn.setData("sortDirection", oldTableColumn.getData("sortDirection"));
					newTableColumn.addListener(SWT.Selection, sortListener);
					newTableColumn.setImage(oldTableColumn.getImage());
					if ( oldTableColumn == oldTable.getSortColumn() ) {
						newTable.setSortColumn(newTableColumn);
						newTable.setSortDirection(oldTable.getSortDirection());
					}
				}

				Arrays.sort(oldTableItems, new TableItemComparator(oldTable.indexOf(sortedColumn), sortDirection));

				for ( TableItem oldTableItem: oldTableItems) {
					TableEditor[] oldEditors = (TableEditor[])oldTableItem.getData("editors");
					TableEditor[] newEditors = new TableEditor[oldEditors.length];

					TableItem newTableItem = new TableItem(newTable, SWT.NONE);
					for ( int column=0; column < oldTable.getColumnCount(); ++column ) {
						if ( oldEditors[column] == null ) {
							newTableItem.setText(column, oldTableItem.getText(column));
							newEditors[column]=null;
						} else {
							TableEditor newEditor = new TableEditor(newTable);
							switch ( oldEditors[column].getEditor().getClass().getSimpleName() ) {
								case "StyledText" :
									StyledText oldText = (StyledText)oldEditors[column].getEditor();
									StyledText newText = new StyledText(newTable, SWT.NONE);
									newText.setText(oldText.getText());
									newText.setToolTipText(oldText.getToolTipText());
									newText.setData("eObject", oldText.getData("eObject"));
									newText.setData("variable", oldText.getData("variable"));
									newText.setData("pattern", oldText.getData("pattern"));
									newText.addModifyListener(textModifyListener);
									newEditor.grabHorizontal = true;
									newEditor.setEditor(newText, newTableItem, column);
									break;

								case "CCombo":
									CCombo oldCombo = (CCombo)oldEditors[column].getEditor();
									CCombo newCombo = new CCombo(newTable, SWT.NONE);
									newCombo.setText(oldCombo.getText());
									newCombo.setItems(oldCombo.getItems());
									newCombo.setToolTipText(oldCombo.getToolTipText());
									newCombo.setData("eObject", oldCombo.getData("eObject"));
									newCombo.setData("variable", oldCombo.getData("variable"));
									newCombo.setEditable(false);
									newEditor.grabHorizontal = true;
									newEditor.setEditor(newCombo, newTableItem, column);
									break;

								case "Button":
									Button oldButton = (Button)oldEditors[column].getEditor();
									Button newButton = new Button(newTable, SWT.CHECK);
									newButton.pack();
									newEditor.minimumWidth = newButton.getSize().x;
									newEditor.horizontalAlignment = SWT.CENTER;
									newButton.setData("eObject", oldButton.getData("eObject"));
									newButton.setData("variable", oldButton.getData("variable"));
									newButton.setData("values", oldButton.getData("values"));
									newButton.setSelection(oldButton.getSelection());
									newButton.addSelectionListener(checkButtonSelectionListener);
									newEditor.setEditor(newButton, newTableItem, column);
							}
							newEditors[column] = newEditor;
						}
					}
					newTableItem.setData("editors", newEditors);
				}

				logger.debug("Replacing old table with new table"); 
				newTable.setVisible(true);
				oldTable.dispose();
			}

		}
	};

	private void cancel() {
		dialog.dispose();
	}
	
	CompoundCommand compoundCommand;

	private void ok() {
		compoundCommand = new NonNotifyingCompoundCommand();
		try {
			for ( Control control: dialog.getChildren() ) {
				save(control);
			}
		} catch ( Exception e) {
			popup(Level.ERROR, "Failed to save values.", e);
			return;
		}
		
		IArchimateModel model;
		
		if ( selectedObject instanceof IDiagramModel ) {
			model = ((IDiagramModel)selectedObject).getArchimateModel();
		}
		else if ( selectedObject instanceof IDiagramModelArchimateObject ) {
			model = ((IDiagramModelArchimateObject)selectedObject).getDiagramModel().getArchimateModel();
		}
		else {
			popup(Level.ERROR, "Failed to get the model.");
			return;
		}
		
        CommandStack stack = (CommandStack) model.getAdapter(CommandStack.class);
        stack.execute(compoundCommand);
        
		dialog.dispose();
	}

	private void save(Control control) {
		switch ( control.getClass().getSimpleName() ) {
			case "Label" :
				break;					// nothing to save here

			case "Button" :
			case "CCombo" :
			case "StyledText" :
				if ( control.getData("variable") != null ) do_save(control);
				break;

			case "TabFolder" :
				for ( Control child: ((TabFolder)control).getChildren() ) {
					save(child);
				}
				break;
			case "Table" :
				for ( TableItem item: ((Table)control).getItems() ) {
					for ( TableEditor editor: (TableEditor[])item.getData("editors") ) {
						save(editor.getEditor());
					}
				}
				break;
			case "Composite" :
				for ( Control child: ((Composite)control).getChildren() ) {
					save(child);
				}
				break;

			default : logger.debug("do not know how to save a "+control.getClass().getSimpleName());
		}
	}

	private void do_save(Control control) {
		String variable = (String)control.getData("variable");
		EObject eObject = (EObject)control.getData("eObject");
		String value;

		switch ( control.getClass().getSimpleName() ) {
			case "Button" : value=((Button)control).getText(); break;
			case "CCombo" : value=((CCombo)control).getText(); break;
			case "StyledText" : value=((StyledText)control).getText(); break;
			default : throw new RuntimeException("Do not know how to save a "+control.getClass().getSimpleName()+".");
		}

		if ( logger.isDebugEnabled() )
			logger.debug("do_save "+control.getClass().getSimpleName()+" : "+variable+" = \""+value+"\"");

		if ( value.isEmpty() ) {
			String whenEmpty = (String)control.getData("whenEmpty");

			if ( whenEmpty == null )
				whenEmpty = "ignore";

			switch ( whenEmpty ) {
				case "ignore" :
					if ( logger.isTraceEnabled() )
						logger.trace("   value is empty : ignored.");
					break;
				case "create" :
					if ( logger.isTraceEnabled() )
						logger.trace("   value is empty : creating property if doen't exist.");
					setVariable(eObject, variable, "");
					break;
				case "delete" :
					if ( logger.isTraceEnabled() )
						logger.trace("   value is empty : deleting property if it exists.");
					setVariable(eObject, variable, null);
					break;
			}
		} else {
			if ( logger.isTraceEnabled() )
				logger.trace("   value is not empty : setting property.");
			setVariable(eObject, variable, value);
		}
	}

	@SuppressWarnings("deprecation")
	private void exportToExcel() {
		FileDialog fsd = new FileDialog(dialog, SWT.SINGLE);
		fsd.setFilterExtensions(new String[] {"*.xls*"});
		fsd.setText("Select Excel File...");
		String excelFile = fsd.open();

		// we wait for the dialog disposal
		while ( dialog.getDisplay().readAndDispatch() );

		if ( excelFile != null ) {
			FileInputStream file;
			try {
				file = new FileInputStream(excelFile);
			} catch (FileNotFoundException e) {
				popup(Level.ERROR, "Cannot open the Excel file.", e);
				return;
			}

			popup("Please wait while exporting to Excel ...");

			Workbook workbook;
			Sheet sheet;

			if ( logger.isDebugEnabled() ) logger.debug("Openning file "+file);
			try {
				workbook = WorkbookFactory.create(file);
			} catch (Exception e) {
				closePopup();
				popup(Level.ERROR, "The file "+excelFile+" seems not to be an Excel file!", e);
				//TODO: add an option to create an empty Excel file
				return;
			}

			// we check that all the sheets already exist
			for ( String sheetName: excelSheets) {
				sheet = workbook.getSheet(sheetName);
				if ( sheet == null ) {
					closePopup();
					popup(Level.ERROR, "The file "+excelFile+" does not contain a sheet called \""+sheetName+"\"");
					//TODO : add a preference to create the sheet 
					try {
						workbook.close();
					} catch (IOException ign) { ign.printStackTrace(); }
					return;
				}
			}

			boolean exportOk = true;

			try {
				// we go through all the controls and export the corresponding excel cells
				for ( TabItem tabItem: tabFolder.getItems() ) {
					if ( logger.isDebugEnabled() ) logger.debug("Exporting tab "+tabItem.getText());

					Composite composite = (Composite)tabItem.getControl();
					for ( Control control: composite.getChildren() ) {
						String excelSheet = (String)control.getData("excelSheet");

						if ( excelSheet != null) {
							sheet = workbook.getSheet(excelSheet);		// cannot be null as it has been checked before

							if ( (control instanceof StyledText) || (control instanceof Label) ) {
								String excelCell = (String)control.getData("excelCell");

								CellReference ref = new CellReference(excelCell);
								Row row = sheet.getRow(ref.getRow());
								if ( row == null ) {
									row = sheet.createRow(ref.getRow());
								}
								Cell cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);

								String text;
								if ( control instanceof StyledText ) {
									text = ((StyledText)control).getText();
								} else {
									text = ((Label)control).getText();
								}
								cell.setCellValue(text);
								if ( logger.isTraceEnabled() ) logger.trace("   '"+excelSheet+"'!"+excelCell+" -> \""+text+"\"");
							} else if ( control instanceof Table ) {
								if ( logger.isDebugEnabled() ) logger.debug("Exporting table");
								Table table = (Table)control;
								int excelFirstLine = (int)table.getData("excelFirstLine") - 1;	// Excel lines begin at zero
								for (int line = 0; line < table.getItemCount(); ++line) {
									TableItem tableItem = table.getItem(line);
									Row row = sheet.getRow(excelFirstLine + line);
									if ( row == null ) row = sheet.createRow(excelFirstLine + line);

									for (int col = 0; col < table.getColumnCount(); ++col) {
										TableColumn tableColumn = table.getColumn(col);
										String excelColumn = (String)tableColumn.getData("excelColumn");

										if ( excelColumn != null ) {
											CellReference ref = new CellReference(excelColumn);
											Cell cell = row.getCell(ref.getCol(), MissingCellPolicy.CREATE_NULL_AS_BLANK);

											TableEditor editor = ((TableEditor[]) tableItem.getData("editors"))[col];

											String value;
											if ( editor == null )
												value = tableItem.getText(col);
											else switch ( editor.getEditor().getClass().getSimpleName() ) {
												case "StyledText" : value = ((StyledText)editor.getEditor()).getText(); break;
												case "Button" : value = ((Button)editor.getEditor()).getText(); break;
												case "CCombo" : value = ((CCombo)editor.getEditor()).getText(); break;
												default : throw new RuntimeException("Do not how to deal with columns of class "+editor.getClass().getSimpleName());
											}

											String excelCellType = (String)tableColumn.getData("excelCellType");
											String excelDefault = (String)tableColumn.getData("excelDefault");

											switch ( excelCellType ) {
												case "string" :
													if ( value.isEmpty() ) {
														switch ( excelDefault ) {
															case "blank" : cell.setCellType(CellType.BLANK); break;
															case "zero" : cell.setCellType(CellType.STRING); cell.setCellValue(""); break;
															default : ;
														}
													} else {
														cell.setCellType(CellType.STRING);
														cell.setCellValue(value);
													}
													break;

												case "numeric" :
													if ( value.isEmpty() ) {
														switch ( excelDefault ) {
															case "blank" : cell.setCellType(CellType.BLANK); break;
															case "zero" : cell.setCellType(CellType.BLANK); cell.setCellValue(0.0); break;
															default : ;
														}
													} else {
														cell.setCellType(CellType.NUMERIC);
														try  {
															cell.setCellValue(Double.parseDouble(value));
														} catch (Exception e) {
															throw new RuntimeException("Failed to convert value to numeric", e);
														}
													}
													break;

												case "boolean" :
													if ( value.isEmpty() ) {
														switch ( excelDefault ) {
															case "blank" : cell.setCellType(CellType.BLANK); break;
															case "zero" : cell.setCellType(CellType.BOOLEAN); cell.setCellValue(false); break;
															default : ;
														}
													} else {
														cell.setCellType(CellType.BOOLEAN);
														try  {
															cell.setCellValue(Boolean.parseBoolean(value));
														} catch (Exception e) {
															throw new RuntimeException("Failed to convert value to boolean", e);
														}
													}
													break;

												case "formula" :
													if ( value.isEmpty() ) {
														switch ( excelDefault ) {
															case "blank" : cell.setCellType(CellType.BLANK); break;
															case "zero" : cell.setCellType(CellType.FORMULA); cell.setCellValue(""); break;
															default : ;
														}
													} else {
														cell.setCellType(CellType.FORMULA);
														cell.setCellFormula(value);
													}
													break;

												case "blank" :
													cell.setCellType(CellType.BLANK);
													break;
												default : throw new RuntimeException("Don't know to deal with excell cell Type \""+excelCellType+"\".\n\nSupported values are blank, boolean, formula, numeric and string.");
											}

											if ( logger.isTraceEnabled() ) logger.trace("   '"+excelSheet+"'!"+excelColumn+(excelFirstLine + line +1)+" -> \""+value+"\" ("+cell.getCellTypeEnum().toString()+")");
										}
									}
								}
							} else
								if ( logger.isDebugEnabled() ) logger.debug("control is not a label, nor a text, nor a table !");
						}
					}
				}
			} catch (Exception e) {
				closePopup();
				popup(Level.ERROR, "Failed to update Excel file.", e);
				exportOk = false;
			}


			if ( exportOk ) {
				workbook.setForceFormulaRecalculation(true);
				if ( logger.isDebugEnabled() ) logger.debug("Saving Excel file");
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
			} catch (IOException ign) { ign.printStackTrace(); }

			closePopup();

			if ( exportOk && question("Export to Excel successful.\n\nDo you wish to open the Excel spreadsheet ?") ) {
				try {
					Desktop.getDesktop().open(new File(excelFile));
				} catch (IOException e) {
					popup(Level.ERROR, "Failed to launch Excel.", e);
				}
			}
		}
	}

	private class TableItemComparator implements Comparator<TableItem>
	{
		int columnIndex = 0;
		int sortDirection = SWT.UP;

		public  TableItemComparator(int columnIndex, int sortDirection)
		{
			this.columnIndex = columnIndex;
			this.sortDirection = sortDirection;
		}

		public  int compare(TableItem first, TableItem second)
		{
			TableEditor[] editorsFirst = (TableEditor[])first.getData("editors");

			if ( editorsFirst[columnIndex] != null ) {
				TableEditor[] editorsSecond = (TableEditor[])second.getData("editors");

				switch ( editorsFirst[columnIndex].getEditor().getClass().getSimpleName() ) {
					case "StyledText" : logger.trace("comparing \""+((StyledText)editorsFirst[columnIndex].getEditor()).getText()+"\" and \""+((StyledText)editorsSecond[columnIndex].getEditor()).getText()+"\"");
					return Collator.getInstance().compare(((StyledText)editorsFirst[columnIndex].getEditor()).getText(), ((StyledText)editorsSecond[columnIndex].getEditor()).getText()) * (sortDirection == SWT.UP ? 1 : -1);
					case "Button" : 	logger.trace("comparing \""+((Button)editorsFirst[columnIndex].getEditor()).getSelection()+"\" and \""+((Button)editorsSecond[columnIndex].getEditor()).getSelection()+"\"");
					return Collator.getInstance().compare(((Button)editorsFirst[columnIndex].getEditor()).getSelection(), ((Button)editorsSecond[columnIndex].getEditor()).getSelection()) * (sortDirection == SWT.UP ? 1 : -1);

					case "CCombo" : 	logger.trace("comparing \""+((CCombo)editorsFirst[columnIndex].getEditor()).getText()+"\" and \""+((CCombo)editorsSecond[columnIndex].getEditor()).getText()+"\"");
					return Collator.getInstance().compare(((CCombo)editorsFirst[columnIndex].getEditor()).getText(), ((CCombo)editorsSecond[columnIndex].getEditor()).getText()) * (sortDirection == SWT.UP ? 1 : -1);

					default :			throw new RuntimeException("Do not how to compare elements of class "+editorsFirst[columnIndex].getClass().getSimpleName());
				}
			}

			return Collator.getInstance().compare(first.getText(columnIndex), second.getText(columnIndex)) * (sortDirection == SWT.UP ? 1 : -1);
		}
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @return the value if found
	 * @throws RuntimeException if the key is not found
	 */
	public static Object getJSON(JSONObject obj, String key) throws RuntimeException {
		Object result = getJSON(obj, key, null);
		if ( result == null )
			throw new RuntimeException("key \""+key+"\" not found.");
		return result;
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @return the value if found, or the defaultValue provided if not found, ClassCastException if the object found is not a JSONObject
	 */
	public static JSONObject getJSONObject(JSONObject obj, String key) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key);

		if ( result instanceof JSONObject )
			return (JSONObject) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a JSONObject.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @return the value if found, ClassCastException if the object found is not a JSONArray
	 */
	public static JSONArray getJSONArray(JSONObject obj, String key) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key);

		if ( result instanceof JSONArray )
			return (JSONArray) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a JSONarray.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @return the value if found, ClassCastException if the object found is not a String
	 */
	public static String getString(JSONObject obj, String key) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key);

		if ( result instanceof String )
			return (String) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a String.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @return the value if found, ClassCastException if the object found is not an Integer
	 */
	public static int getInt(JSONObject obj, String key) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key);

		if ( result instanceof Long )
			return (int)(long) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be an Integer.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @param defaultValue
	 * @return the value if found, ClassCastException if the object found is not a boolean
	 */
	public static Boolean getBoolean(JSONObject obj, String key) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key);

		if ( result instanceof Boolean )
			return (Boolean)result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a Boolean.");
	}

	/**
	 * Checks if the key exists in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 */
	public static boolean JSONContainsKey(JSONObject obj, String key) {
		@SuppressWarnings("unchecked")
		Iterator<String> iter = obj.keySet().iterator();
		while (iter.hasNext()) {
			String key1 = iter.next();
			if (key1.equalsIgnoreCase(key)) return true;
		}
		return false;
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
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
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @param defaultValue
	 * @return the value if found, or the defaultValue provided if not found, ClassCastException if the object found is not a JSONObject
	 */
	public static JSONObject getJSONObject(JSONObject obj, String key, Object defaultValue) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key, defaultValue);

		if ( result == null || result instanceof JSONObject )
			return (JSONObject) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a JSONObject.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @param defaultValue
	 * @return the value if found, or the defaultValue provided if not found, ClassCastException if the object found is not a JSONArray
	 */
	public static JSONArray getJSONArray(JSONObject obj, String key, Object defaultValue) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key, defaultValue);

		if ( result == null || result instanceof JSONArray )
			return (JSONArray) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a JSONarray.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @param defaultValue
	 * @return the value if found, or the defaultValue provided if not found, ClassCastException if the object found is not a String
	 */
	public static String getString(JSONObject obj, String key, Object defaultValue) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key, defaultValue);

		if ( result == null || result instanceof String )
			return (String) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a String.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @param defaultValue
	 * @return the value if found, or the defaultValue provided if not found, ClassCastException if the object found is not an Integer
	 */
	public static int getInt(JSONObject obj, String key, int defaultValue) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key, Long.valueOf(defaultValue));

		if ( result instanceof Long )
			return (int)(long) result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be an Integer.");
	}

	/**
	 * Gets the value corresponding to the key in the JSONObject
	 * @param obj the JSONObject
	 * @param key the key (case insensitive)
	 * @param defaultValue
	 * @return the value if found, or the defaultValue provided if not found, ClassCastException if the object found is not a Boolean
	 */
	public static Boolean getBoolean(JSONObject obj, String key, Boolean defaultValue) throws RuntimeException, ClassCastException {
		Object result = getJSON(obj, key, defaultValue);

		if ( result == null || result instanceof Boolean )
			return (Boolean)result;

		throw new ClassCastException("Key \""+key+"\" is a "+result.getClass().getSimpleName()+" but should be a Boolean.");
	}

	/**
	 * Checks whether the eObject fits in the filter rules
	 */
	public static boolean checkFilter(EObject eObject, JSONObject filterObject) {
		if ( filterObject == null ) {
			return true;
		}

		String type = ((String)getJSON(filterObject, "genre", "AND")).toUpperCase();

		if ( !type.equals("AND") && !type.equals("OR") )
			throw new RuntimeException("Invalid filter genre. Supported genres are \"AND\" and \"OR\".");

		boolean result = true;

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> filterIterator = ((JSONArray)getJSON(filterObject, "tests")).iterator();
		while (filterIterator.hasNext()) {
			JSONObject filter = filterIterator.next();
			String attribute=(String)getJSON(filter, "attribute");			// raise an exception if not exist
			String operation=(String)getJSON(filter, "operation");			// raise an exception if not exist
			String value;

			String attributeValue = expand(attribute, eObject);

			switch ( operation.toLowerCase() ) {
				case "equals" :
					value=(String)getJSON(filter, "value");			// raise an exception if not exist

					result = (attributeValue != null) && attributeValue.equals(value);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") equals \""+value+"\" --> "+result);
					break;

				case "exists" :
					result = (attributeValue != null);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") exists --> "+result);
					break;

				case "iequals" :
					value=(String)getJSON(filter, "value");			// raise an exception if not exist

					result = (attributeValue != null) && attributeValue.equalsIgnoreCase(value);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") equals (ignore case) \""+value+"\" --> "+result);
					break;

				case "matches" :
					value=(String)getJSON(filter, "value");			// raise an exception if not exist

					result = (attributeValue != null) && attributeValue.matches(value);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") matches \""+value+"\" --> "+result);
					break;

				default :
					throw new RuntimeException("Unknown operation type \""+operation+"\" in filter.\n\nValid operations are \"equals\", \"exists\", \"iequals\" and \"matches\".");
			}

			// in AND mode, all the tests must return true, so if the current test is false, then the complete filter returns false
			if( result == false && type.equals("AND") )
				return false;

			// in OR mode, one test at lease must return true, so if the current test is true, then the complete filter returns true
			if( result == true && type.equals("OR") )
				return true;
		}
		// in AND mode, we're here if all the tests were true
		// in OR mode, we're here if all the tests were false 
		return type.equals("AND");
	}

	/**
	 * Expands an expression containing variables<br>
	 * It may return an empty string, but never a null value
	 */
	public static String expand(String expression, EObject eObject) {
		if ( expression == null )
			return null;

		StringBuffer sb = new StringBuffer(expression.length());

		Pattern pattern = Pattern.compile("(\\$\\{[^}]+})");
		Matcher matcher = pattern.matcher(expression);

		while (matcher.find()) {
			String variable = matcher.group(1);
			String variableValue = getVariable(variable, eObject);
			if ( variableValue == null )
				variableValue = "";
			matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Gets the value of the variable<br>
	 * car return a null value in case the property does not exist. This way it is possible to distinguish between empty value and null value
	 */
	public static String getVariable(String variable, EObject eObject) {
		if ( logger.isTraceEnabled() ) logger.trace("getting variable \""+variable+"\"");

		// we check that the variable provided is a string enclosed between "${" and "}"
		Pattern pattern = Pattern.compile("^\\$\\{[^}]+}$");
		Matcher matcher = pattern.matcher(variable);
		if ( !matcher.matches() )
			throw new RuntimeException("The expression \""+variable+"\" is not a variable. It should be enclosed between \"${\" and \"}\"");

		String variableName = variable.substring(2, variable.length()-1);

		//TODO : add a preference to choose between silently ignore or raise an error
		switch ( variableName.toLowerCase() ) {
			case "class" :
				if (eObject instanceof IDiagramModelArchimateObject)
					return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName();
				return eObject.getClass().getSimpleName();

			case "id" :
				if (eObject instanceof IDocumentable)
					return ((IIdentifier)eObject).getId();
				new RuntimeException("Cannot get variable \""+variable+"\" as the object is not an Identifier.");

			case "documentation" :
				if (eObject instanceof IDocumentable)
					return ((IDocumentable)eObject).getDocumentation();
				new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a Documentable.");

			case "name" :
				if (eObject instanceof INameable)
					return ((INameable)eObject).getName();
				new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a Nameable.");

			default :
				if ( variableName.toLowerCase().startsWith("property:") ) {
					if ( eObject instanceof IDiagramModelArchimateObject )
						eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
					if ( eObject instanceof IDiagramModelArchimateConnection )
						eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
					if ( eObject instanceof IProperties ) {
						String propertyName = variableName.substring(9);
						for ( IProperty property: ((IProperties)eObject).getProperties() ) {
							if ( property.getKey().equals(propertyName) ) {
								return property.getValue();
							}
						}
						return null;
					}
					throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not a Properties.");
				}

				else if ( variableName.toLowerCase().startsWith("view:") ) {
					if ( eObject instanceof IDiagramModel ) {
						return getVariable("${"+variableName.substring(5)+"}", eObject);
					}
					else if ( eObject instanceof IDiagramModelArchimateObject ) {
						return getVariable(variableName.substring(5), ((IDiagramModelArchimateObject)eObject).getDiagramModel());
					}
					throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not part of a DiagramModel.");
				}

				else if ( variableName.toLowerCase().startsWith("model:") ) {
					if ( eObject instanceof IArchimateDiagramModel ) {
						return getVariable("${"+variableName.substring(6)+"}", ((IArchimateDiagramModel)eObject).getArchimateModel());
					}
					else if ( eObject instanceof IDiagramModelArchimateObject ) {
						return getVariable("${"+variableName.substring(6)+"}", ((IDiagramModelArchimateObject)eObject).getDiagramModel().getArchimateModel());
					}
					throw new RuntimeException("Cannot get variable \""+variable+"\" as the object is not part of a model.");
				}
		}
		
		throw new RuntimeException("Unknown variable "+variable);
	}

	public String debugValue(String value, String defaultValue) {
		StringBuilder result = new StringBuilder("\""+value+"\"");
		if ( FormPlugin.areEqual(value, defaultValue) )
			result.append(" (default)");
		return result.toString();
	}

	public String debugValue(int value, int defaultValue) {
		StringBuilder result = new StringBuilder(value);
		if ( value == defaultValue )
			result.append(" (default)");
		return result.toString();
	}

	public String debugValue(Boolean value, Boolean defaultValue) {
		StringBuilder result = new StringBuilder();
		if ( value == null )
			result.append("null");
		else
			result.append(value ? "true" : "false");
		if ( value == defaultValue )
			result.append(" (default)");
		return result.toString();
	}
	
	public static boolean inArray(String[] stringArray, String string) {
		for(String s: stringArray){
			if ( FormPlugin.areEqual(s, string) )
				return true;
		}
		return false;
	}
}
