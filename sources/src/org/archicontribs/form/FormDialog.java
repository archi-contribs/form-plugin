package org.archicontribs.form;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
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
	private static final FormLogger logger = new FormLogger(FormDialog.class);

	private String formName;
	private IArchimateDiagramModel diagramModel;

	private Color badValueColor= new Color(Display.getCurrent(), 255, 0, 0);
	private Color goodValueColor = new Color(Display.getCurrent(), 0, 100, 0);

	private Shell dialog;
	private TabFolder tabFolder;
	
	private HashSet<String> excelSheets = new HashSet<String>();

	public FormDialog(String formName, IArchimateDiagramModel diagramModel) {
		super(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		logger.debug("new formdialog("+formName+","+diagramModel.getName()+")");
		this.formName = formName;
		this.diagramModel = diagramModel;
		
		boolean hasError = false;

		try {
			createContents();
		} catch (FileNotFoundException e) {
			popup(Level.ERROR, "Configuration file \""+FormPlugin.configFilePath+"\" not found.");
			hasError = true;
		} catch (IOException e) {
			popup(Level.ERROR, "I/O Error while reading configuration file \""+FormPlugin.configFilePath+"\"",e);
			hasError = true;
		} catch (ParseException e) {
			popup(Level.ERROR, "Parsing error while reading configuration file \""+FormPlugin.configFilePath+"\"",e);
			hasError = true;
		} catch (Exception e) {
			popup(Level.ERROR, "Please check your configuration file.", e);
			hasError = true;
		}
		
		if ( hasError ) {
			dialog.dispose();
		} else {
			dialog.open();
			dialog.layout();
		}
	}

	//TODO : transform to use FormLayout 

	/**
	 * Parses the configuration file and create the corresponding graphical controls
	 */
	private void createContents() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		logger.debug("Parsing JSON file ...");

		// if we get here, this means that the file has already been parsed to show-up the menu options
		// so we do not have to re-do all the checks
		JSONObject jsonFile = (JSONObject)parser.parse(new FileReader(FormPlugin.configFilePath));

		// we iterate over the "forms" array entries
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> formsIterator = ((JSONArray) jsonFile.get (FormPlugin.PLUGIN_ID)).iterator();
		while (formsIterator.hasNext()) {
			JSONObject form = formsIterator.next();

			// if the entry is related to the form selected in the menu
			if ( formName.equals((String)getIgnoreCase(form, "name")) ) {
				if ( logger.isDebugEnabled() ) logger.debug("Creating form "+formName);

				dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setText(diagramModel.getName() + " - " + formName);
				dialog.setLayout(null);

				int dialogWidth = (int)(long)getIgnoreCase(form, "width", 850L);
				int dialogHeight = (int)(long)getIgnoreCase(form, "height", 600L);
				int dialogSpacing = (int)(long)getIgnoreCase(form, "spacing", 4L);
				String dialogBackground = (String)getIgnoreCase(form, "background", null);
				
				if ( logger.isTraceEnabled() ) {
					logger.trace("   dialog width = "+dialogWidth);
					logger.trace("   dialog height = "+dialogHeight);
					logger.trace("   dialog spacing = "+dialogSpacing);
					logger.trace("   dialog background = "+dialogBackground);
				}

				int buttonWidth = 90;
				int buttonHeight = 25;

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
				cancelButton.setText("Close");
				cancelButton.setEnabled(true);
				cancelButton.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
					public void widgetDefaultSelected(SelectionEvent e) { dialog.dispose(); }
				});

				createTabs(form, tabFolder);

					// If there is at lease one Excel sheet specified, then we show up the "export to Excel" button
				if ( !excelSheets.isEmpty() ) {
					Button saveButton = new Button(dialog, SWT.NONE);
					saveButton.setBounds(tabFolderWidth - buttonWidth*2, tabFolderHeight + dialogSpacing*2, buttonWidth, buttonHeight);
					saveButton.setText("Export to Excel");
					saveButton.setEnabled(true);
					saveButton.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
						public void widgetDefaultSelected(SelectionEvent e) { save(); }
					});
					break;
				}
			}
		}
	}

	/**
	 * Creates the dialog tabItems<br>
	 * <br>
	 * called by the createContents() method
	 */
	private void createTabs(JSONObject form, TabFolder tabFolder) {
		// we iterate over the "tabs" array attributes
		JSONArray tabs = (JSONArray)getIgnoreCase(form, "tabs");
		if ( tabs != null ) {
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> tabsIterator = tabs.iterator();
			while (tabsIterator.hasNext()) {
				JSONObject tab = tabsIterator.next();
				
				String tabName = (String)getIgnoreCase(tab, "name", "(no name)");
				String tabBackground = (String)getIgnoreCase(tab, "background", null);
	
				if ( logger.isDebugEnabled() ) logger.debug("Creating tab " + tabName);
				if ( logger.isTraceEnabled() ) logger.trace("   tab background = "+tabBackground);
	
				// we create one TabItem per entry
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
		if ( logger.isDebugEnabled() ) logger.debug("Creating objects in tab");

		// we iterate over the "objects" entries
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> objectsIterator = ((JSONArray)getIgnoreCase(tab, "objects")).iterator();	
		while (objectsIterator.hasNext()) {
			JSONObject jsonObject = objectsIterator.next();

			switch ((String)getIgnoreCase(jsonObject, "class")) {
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
		Label label =  new Label(composite, SWT.NONE);
		
		String value = (String)getIgnoreCase(jsonObject, "value", "");
		String attributeValue = (String)getAttribute(diagramModel, value); 
		if ( attributeValue != null ) label.setText(attributeValue);
		label.pack();
		
		int x = (int)(long)getIgnoreCase(jsonObject, "x", 0L);
		int y = (int)(long)getIgnoreCase(jsonObject, "y", 0L);
		int width = (int)(long)getIgnoreCase(jsonObject, "width", (long)label.getSize().x);
		int height = (int)(long)getIgnoreCase(jsonObject, "height", (long)label.getSize().y);
		String background = (String)getIgnoreCase(jsonObject, "background");
		String foreground = (String)getIgnoreCase(jsonObject, "foreground");
		String tooltip = (String)getIgnoreCase(jsonObject, "tooltip");
		String excelSheet = (String)getIgnoreCase(jsonObject, "excelSheet");
		String excelCell = (String)getIgnoreCase(jsonObject, "excelCell");

		if ( logger.isDebugEnabled() ) logger.debug("   Creating label \""+value+"\" ("+attributeValue+")");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+x);
			logger.trace("      y = "+y);
			logger.trace("      width = "+width);
			logger.trace("      height = "+height);
			logger.trace("      background = "+background);
			logger.trace("      foreground = "+foreground);
			logger.trace("      tooltip = "+tooltip);
			logger.trace("      excelSheet = "+excelSheet);
			logger.trace("      excelCell = "+excelCell);
		}

		label.pack();
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
		StyledText text =  new StyledText(composite, SWT.BORDER);

		String value = (String)getIgnoreCase(jsonObject, "value", "");
		String attributeValue = (String)getAttribute(diagramModel, value); 
		if ( attributeValue != null ) text.setText(attributeValue);
		text.pack();
		
		int x = (int)(long)getIgnoreCase(jsonObject, "x", 0L);
		int y = (int)(long)getIgnoreCase(jsonObject, "y", 0L);
		int width = (int)(long)getIgnoreCase(jsonObject, "width", (long)text.getSize().x);
		int height = (int)(long)getIgnoreCase(jsonObject, "height", (long)text.getSize().y);
		String background = (String)getIgnoreCase(jsonObject, "background");
		String foreground = (String)getIgnoreCase(jsonObject, "foreground");
		String regex = (String)getIgnoreCase(jsonObject, "regexp");
		String tooltip = (String)getIgnoreCase(jsonObject, "tooltip");
		String excelSheet = (String)getIgnoreCase(jsonObject, "excelSheet");
		String excelCell = (String)getIgnoreCase(jsonObject, "excelCell");
		
		if ( logger.isDebugEnabled() ) logger.debug("   Creating text \""+value+"\" ("+attributeValue+")");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+x);
			logger.trace("      y = "+y);
			logger.trace("      width = "+width);
			logger.trace("      height = "+height);
			logger.trace("      background = "+background);
			logger.trace("      foreground = "+foreground);
			logger.trace("      regex = "+regex);
			logger.trace("      tooltip = "+tooltip);
			logger.trace("      excelSheet = "+excelSheet);
			logger.trace("      excelCell = "+excelCell);
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

		text.setData("variable", (String)getIgnoreCase(jsonObject, "value"));
		text.setData("eObject", diagramModel);
		
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

		//TODO : manage default value

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
		String[] values = (String[])((JSONArray)getIgnoreCase(jsonObject, "values")).toArray(new String[0]);
		if ( values == null ) {
			throw new RuntimeException("\"values\" property are mandatory for combo objects.");
		} else {
			combo.setItems(values);
		}
		
		String value = (String)getIgnoreCase(jsonObject, "value", "");
		String attributeValue = (String)getAttribute(diagramModel, value); 
		if ( attributeValue != null ) combo.setText(attributeValue);
		combo.pack();
		
		int x = (int)(long)getIgnoreCase(jsonObject, "x", 0L);
		int y = (int)(long)getIgnoreCase(jsonObject, "y", 0L);
		int width = (int)(long)getIgnoreCase(jsonObject, "width", (long)combo.getSize().x);
		int height = (int)(long)getIgnoreCase(jsonObject, "height", (long)combo.getSize().y);
		String background = (String)getIgnoreCase(jsonObject, "background");
		String foreground = (String)getIgnoreCase(jsonObject, "foreground");
		String tooltip = (String)getIgnoreCase(jsonObject, "tooltip");
		String excelSheet = (String)getIgnoreCase(jsonObject, "excelSheet");
		String excelCell = (String)getIgnoreCase(jsonObject, "excelCell");
		
		
		if ( logger.isDebugEnabled() ) logger.debug("   Creating combo \""+value+"\" ("+attributeValue+")");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+x);
			logger.trace("      y = "+y);
			logger.trace("      width = "+width);
			logger.trace("      height = "+height);
			logger.trace("      background = "+background);
			logger.trace("      foreground = "+foreground);
			logger.trace("      values = "+values);
			logger.trace("      tooltip = "+tooltip);
			logger.trace("      excelSheet = "+excelSheet);
			logger.trace("      excelCell = "+excelCell);
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

		combo.setData("variable", (String)getIgnoreCase(jsonObject, "value"));
		combo.setData("eObject", diagramModel);
		
		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			combo.setData("excelSheet", excelSheet);
			combo.setData("excelCell", excelCell);
		}

		if ( tooltip != null ) {
			combo.setToolTipText(tooltip);
		}

		//TODO : manage default value

		combo.addModifyListener(comboModifyListener);

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

		String value = (String)getIgnoreCase(jsonObject, "value", "");
		String attributeValue = (String)getAttribute(diagramModel, value);
		@SuppressWarnings("unchecked")
		String[] values = (String[])((JSONArray)getIgnoreCase(jsonObject, "values")).toArray(new String[0]);
		if ( values!=null && values.length!=0 ) {
			check.setData("values", values);
			check.setSelection(values[0].equals(value));
		} else {
			check.setSelection(value!=null);
		}
		check.pack();
		
		
		int x = (int)(long)getIgnoreCase(jsonObject, "x", 0L);
		int y = (int)(long)getIgnoreCase(jsonObject, "y", 0L);
		int width = (int)(long)getIgnoreCase(jsonObject, "width", (long)check.getSize().x);
		int height = (int)(long)getIgnoreCase(jsonObject, "height", (long)check.getSize().y);
		String background = (String)getIgnoreCase(jsonObject, "background");
		String foreground = (String)getIgnoreCase(jsonObject, "foreground");
		String tooltip = (String)getIgnoreCase(jsonObject, "tooltip");
		String excelSheet = (String)getIgnoreCase(jsonObject, "excelSheet");
		String excelCell = (String)getIgnoreCase(jsonObject, "excelCell");
		
		if ( logger.isDebugEnabled() ) logger.debug("   Creating combo \""+value+"\" ("+attributeValue+")");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+x);
			logger.trace("      y = "+y);
			logger.trace("      width = "+width);
			logger.trace("      height = "+height);
			logger.trace("      background = "+background);
			logger.trace("      foreground = "+foreground);
			logger.trace("      values = "+values);
			logger.trace("      tooltip = "+tooltip);
			logger.trace("      excelSheet = "+excelSheet);
			logger.trace("      excelCell = "+excelCell);
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

		check.setData("variable", (String)getIgnoreCase(jsonObject, "value"));
		check.setData("eObject", diagramModel);
		
		if ( excelSheet != null ) {
			excelSheets.add(excelSheet);
			check.setData("excelSheet", excelSheet);
			check.setData("excelCell", excelCell);
		}

		if ( tooltip != null ) {
			check.setToolTipText(tooltip);
		}

		//TODO : manage default value

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
		int x = (int)(long)getIgnoreCase(jsonObject, "x", 0L);
		int y = (int)(long)getIgnoreCase(jsonObject, "y", 0L);
		int width = (int)(long)getIgnoreCase(jsonObject, "width", 100L);
		int height = (int)(long)getIgnoreCase(jsonObject, "height", 50L);
		String background = (String)getIgnoreCase(jsonObject, "background");
		String foreground = (String)getIgnoreCase(jsonObject, "foreground");
		String tooltip = (String)getIgnoreCase(jsonObject, "tooltip");
		String excelSheet = (String)getIgnoreCase(jsonObject, "excelSheet");
		int excelFirstLine = (int)(long)getIgnoreCase(jsonObject, "excelFirstLine", 1L);
		
		if ( logger.isDebugEnabled() ) logger.debug("   Creating table");
		if ( logger.isTraceEnabled() ) {
			logger.trace("      x = "+x);
			logger.trace("      y = "+y);
			logger.trace("      width = "+width);
			logger.trace("      height = "+height);
			logger.trace("      background = "+background);
			logger.trace("      foreground = "+foreground);
			logger.trace("      tooltip = "+tooltip);
			logger.trace("      excelSheet = "+excelSheet);
			logger.trace("      excelFirstLine = "+excelFirstLine);
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
		Iterator<JSONObject> columnsIterator = ((JSONArray)getIgnoreCase(jsonObject, "columns")).iterator();
		while (columnsIterator.hasNext()) {
			JSONObject column = columnsIterator.next();

			String columnName = (String)getIgnoreCase(column, "name", "(no name)");
			String columnClass = (String)getIgnoreCase(column, "class");
			String columnTooltip = (String)getIgnoreCase(column, "tooltype");
			int columnWidth = (int)(long)getIgnoreCase(column, "width", (long)(10+columnName.length()*8));
			String excelColumn = (String)getIgnoreCase(column, "excelColumn");
			String excelCellType = (String)getIgnoreCase(column, "excelCellType");
			String excelDefault = (String)getIgnoreCase(column, "excelDefault");
			
			if ( logger.isDebugEnabled() ) logger.debug("   Creating column \"" + columnName + "\" of class \"" + columnClass + "\"");
			if ( logger.isTraceEnabled() ) {
				logger.trace("      width = "+columnWidth);
				logger.trace("      tooltip = "+columnTooltip);
				logger.trace("      excelColumn = "+excelColumn);
				logger.trace("      excelCellType = "+excelCellType);
				logger.trace("      excelDefault = "+excelDefault);
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
					if ( column.containsKey("values") ) {
						tableColumn.setData("values", (String[])((JSONArray)getIgnoreCase(column, "values")).toArray(new String[0]));
						if ( logger.isTraceEnabled() ) {
							logger.trace("      values = "+(String[])((JSONArray)getIgnoreCase(column, "values")).toArray(new String[0]));
						}
					}
					break;
				case "combo":
					if ( column.containsKey("values") ) { 
						tableColumn.setData("values", (String[])((JSONArray)getIgnoreCase(column, "values")).toArray(new String[0]));
						if ( logger.isTraceEnabled() ) {
							logger.trace("      values = "+(String[])((JSONArray)getIgnoreCase(column, "values")).toArray(new String[0]));
						}
					} else {
						throw new RuntimeException("Missing attribute \"values\" to table column type \"combo\".");
					}
					break;
				case "label":
					break;
				case "text":
					tableColumn.setData("regexp", (String)getIgnoreCase(column, "regexp"));
					if ( logger.isTraceEnabled() ) {
						logger.trace("      regexp = "+(String)getIgnoreCase(column, "regexp"));
					}
					break;
				default : throw new RuntimeException("Unknown column class \""+(String)getIgnoreCase(column, "class")+"\" in table.\n\nValid types are \"check\", \"combo\", \"label\" and \"text\".");
			}
		}

		table.setSortColumn(table.getColumn(0));
		table.setSortDirection(SWT.UP);

		// we iterate over the "lines" entries
		JSONArray lines = (JSONArray)getIgnoreCase(jsonObject, "lines");
		if ( lines != null ) {
			Iterator<JSONObject> linesIterator = lines.iterator();
			while (linesIterator.hasNext()) {
				JSONObject line = linesIterator.next();

				switch ( ((String)getIgnoreCase(line, "category")).toLowerCase() ) {
					case "dynamic":
						if ( logger.isTraceEnabled() ) logger.trace("      Generating dynamic lines");
						addTableItems(table, diagramModel.getChildren(), (JSONArray)getIgnoreCase(line, "values"), (JSONObject)getIgnoreCase(line, "filter"));
						break;
					case "static" :
						if ( logger.isTraceEnabled() ) logger.trace("      Creating static line");
						addTableItem(table, diagramModel, (JSONArray)getIgnoreCase(line, "values"));
						break;
					default : throw new RuntimeException("Unknown line category \""+(String)getIgnoreCase(line, "category")+"\" in table.\n\nValid categories are \"dynamic\" and \"static\".");
				}
			}
		}
		return table;
	}

	/**
	 * Checks whether the eObject fits in the filter rules
	 */
	private boolean checkFilter(EObject eObject, JSONObject filterObject) {
		if ( filterObject == null ) {
			return true;
		}
		
		String type = ((String)getIgnoreCase(filterObject, "genre", "AND")).toUpperCase();

		if ( !type.equals("AND") && !type.equals("OR") )
			throw new RuntimeException("Invalid filter genre. Supported genres are \"AND\" and \"OR\".");
		
		boolean result = true;

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> filterIterator = ((JSONArray)getIgnoreCase(filterObject, "tests")).iterator();
		while (filterIterator.hasNext()) {
			JSONObject filter = filterIterator.next();
			String attribute=(String)getIgnoreCase(filter, "attribute");
			String attributeValue = getAttribute(eObject, attribute);
			String operation=(String)getIgnoreCase(filter, "operation");
			String value=(String)getIgnoreCase(filter, "value");

			if ( attribute == null ) { 
				throw new RuntimeException("The property \"attribute\" is missing in filter.");
			}

			if ( operation == null ) {
				throw new RuntimeException("The property \"operation\" is missing in filter.");
			}

			switch (((String)getIgnoreCase(filter, "operation")).toLowerCase()) {
				case "equals" :
					if ( value == null )
						throw new RuntimeException("The property \"value\" is missing in filter.");
					result = (attributeValue != null) && attributeValue.equals(value);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") equals \""+value+"\" --> "+result);
					break;
				
				case "exists" :
					result = (attributeValue != null);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") exists --> "+result);
					break;
					
				case "iequals" :
					if ( value == null )
						throw new RuntimeException("The property \"value\" is missing in filter.");
					result = (attributeValue != null) && attributeValue.equalsIgnoreCase(value);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") equals (ignore case) \""+value+"\" --> "+result);
					break;
				
				case "matches" :
					if ( value == null )
						throw new RuntimeException("The property \"value\" is missing in filter.");
					result = (attributeValue != null) && attributeValue.matches(value);
					if ( logger.isTraceEnabled() ) logger.trace("   filter "+attribute+"(\""+attributeValue+"\") matches \""+value+"\" --> "+result);
					break;
					
				default :
					throw new RuntimeException("Unknown operation type \""+(String)getIgnoreCase(filter, "operation")+"\" in filter.\n\nValid operations are \"equals\", \"exists\", \"iequals\" and \"matches\".");
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
						addTableItem(table, ((IDiagramModelArchimateObject)diagramObject).getArchimateElement(), values);
					else
						addTableItem(table, diagramObject, values);
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
					addTableItem(table, diagramConnection.getArchimateRelationship(), values);
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

		String itemValue;

		// we need to store the widgets to retreive them later on
		TableEditor[] editors= new TableEditor[jsonArray.size()];
		
		logger.trace("   adding line for object : "+((INameable)eObject).getName());

		for ( int columnNumber=0; columnNumber<jsonArray.size(); ++columnNumber) {
			itemValue = getAttribute(eObject, (String)jsonArray.get(columnNumber));
			logger.trace("      adding "+((String)table.getColumn(columnNumber).getData("class")).toLowerCase()+" column with value \""+itemValue+"\"");

			TableEditor editor;
			switch ( ((String)table.getColumn(columnNumber).getData("class")).toLowerCase() ) {
				case "label" : 
					if ( itemValue != null ) tableItem.setText(columnNumber, itemValue);
					editors[columnNumber] = null;
					// TODO : manage calculated value (that can change depending on other table cells !!!)
					break;

				case "text" :
					editor = new TableEditor(table);
					StyledText text = new StyledText(table, SWT.NONE);
					if ( itemValue != null ) text.setText(itemValue);
					text.setToolTipText((String)table.getColumn(columnNumber).getData("tooltip"));
					text.setData("eObject", eObject);
					text.setData("variable", (String)jsonArray.get(columnNumber));

					if ( table.getColumn(columnNumber).getData("regexp") != null ) {
						String regex = (String)table.getColumn(columnNumber).getData("regexp");
						text.setData("pattern", Pattern.compile(regex));
						if ( table.getColumn(columnNumber).getData("tooltip") == null ) {
							text.setToolTipText("Your text shoud match the following regex :\n"+regex);
						}
					}

					//TODO : manage default value

					text.addModifyListener(textModifyListener);

					editor.grabHorizontal = true;
					editor.setEditor(text, tableItem, columnNumber);
					editors[columnNumber] = editor;
					break;

				case "combo":
					editor = new TableEditor(table);
					CCombo combo = new CCombo(table, SWT.NONE);
					if ( itemValue != null ) combo.setText(itemValue);
					combo.setItems((String[])table.getColumn(columnNumber).getData("values"));
					combo.setToolTipText((String)table.getColumn(columnNumber).getData("tooltip"));
					combo.setData("eObject", eObject);
					combo.setData("variable", (String)jsonArray.get(columnNumber));
					combo.setEditable(false);

					// TODO : manage default value

					combo.addModifyListener(comboModifyListener);

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
					check.setData("eObject", eObject);
					check.setData("variable", (String)jsonArray.get(columnNumber));

					String[] values = (String[])table.getColumn(columnNumber).getData("values");
					String value = getAttribute(eObject, (String)jsonArray.get(columnNumber));
					if ( values!=null && values.length!=0 ) {
						check.setData("values", table.getColumn(columnNumber).getData("values"));
						check.setSelection(values[0].equals(value));
					} else {
						check.setSelection(value!=null);
					}

					//TODO : manage default value

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

			logger.trace("text field modified for variable "+(String)widget.getData("variable")+". new value : \""+text+"\"");

			// if a regex has been provided, we change the text color to show if it matches
			Pattern pattern = (Pattern)e.widget.getData("pattern");
			if ( pattern != null ) {
				widget.setStyleRange(new StyleRange(0, widget.getText().length(), pattern.matcher(text).matches() ? goodValueColor : badValueColor, null));
			}

			setAttribute((EObject)widget.getData("eObject"), (String)widget.getData("variable"), text);
		}
	};

	ModifyListener comboModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			CCombo widget = (CCombo)e.widget;
			String text = widget.getText();

			logger.trace("combo field modified for variable "+(String)widget.getData("variable")+". new value : \""+text+"\"");

			setAttribute((EObject)widget.getData("eObject"), (String)widget.getData("variable"), text);
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

			setAttribute((EObject)widget.getData("eObject"), (String)widget.getData("variable"), text);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
	};

	/**
	 * Gets an attribute of the eObject<br>
	 * <br>
	 * The attribute name can be :<br>
	 *    - <b>$id</b>				returns the ID of the eObject<br>
	 *    - <b>$name</b>			returns the name of the eObject<br>
	 *    - <b>$documentation</b>	returns the documentation of the eObject<br>
	 *    - <b>$property:xxx</b>	returns the value of the property xxx<br>
	 * <br>
	 * Silently returns null if the attribute does not exist or is not set for the eObject
	 */
	private String getAttribute(EObject eObject, String expression) {
		// if the expression is a variable (ie. starts with a dollar sign, then we replace it with its value 
		if ( expression!=null && expression.startsWith("$") ) {
			switch ( expression.toLowerCase() ) {
				case "$class" :         if (eObject instanceof IDiagramModelArchimateObject)
					return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName();
				else
					return eObject.getClass().getSimpleName();
				case "$id" :            if (eObject instanceof IIdentifier) return ((IIdentifier)eObject).getId();
				case "$documentation" : if (eObject instanceof IDocumentable) return ((IDocumentable)eObject).getDocumentation();
				case "$name" :          if (eObject instanceof INameable) return ((INameable)eObject).getName();
				default : 				if ( expression.startsWith("$property:") ) {
											if ( eObject instanceof IProperties ) {
												String propertyName = expression.substring(10);
												for ( IProperty property: ((IProperties)eObject).getProperties() ) {
													if ( property.getKey().equals(propertyName) ) {
														return property.getValue();
													}
												}
												return null;
											} else throw new RuntimeException("Cannot set property has object of class \""+eObject.getClass().getSimpleName()+"\" does not present a \"IProperties\" interface.");
										} else if ( expression.startsWith("$view:") ) {
											if ( eObject instanceof IDiagramModelArchimateObject ) {
												return getAttribute((EObject)((IDiagramModelArchimateObject)eObject).getDiagramModel(), expression.substring(6));
											} else throw new RuntimeException("Cannot set view variable has object of class \""+eObject.getClass().getSimpleName()+"\" does not present a \"IDiagramModelArchimateObject\" interface.");
										} else if ( expression.startsWith("$model:") ) {
											if ( eObject instanceof IArchimateModel ) {
												return getAttribute((EObject)((IArchimateModel)eObject).getArchimateModel(), expression.substring(7));
											} else throw new RuntimeException("Cannot set model variable has object of class \""+eObject.getClass().getSimpleName()+"\" does not present a \"IArchimateModel\" interface.");
										}
				//TODO : add a preference to choose between silently ignore or raise an error
			}
			throw new RuntimeException("Unknown attribute "+expression);
		} else
			return expression;		// it is a litteral string
	}

	/**
	 * Sets an attribute of the eObject<br>
	 * <br>
	 * The attribute name can be :<br>
	 *    - $documentation	sets the documentation of the eObject<br>
	 *    - $property:xxx 	if the value is not null, sets the value of the Archi property (the property is created if it does not exists). If the value is null, the property is deleted.<br>
	 * <br>
	 * This method does not throw exceptions as it is mainly called by SWT which won't know what to do with these exceptions.<br>
	 * Instead, it opens a popup to display the error message.
	 */
	private void setAttribute(EObject eObject, String attributeName, String value) {
		logger.trace("setting property \""+attributeName+"\" to value \""+value+"\"");
		if ( attributeName!=null && attributeName.startsWith("$") ) {
			switch ( attributeName.toLowerCase() ) {
				case "$class" :         break;		// we refuse to change the class of an eObject
				case "$id" :            break;		// we refuse to change the ID of an eObject
				case "$documentation" : if (eObject instanceof IDocumentable)
											((IDocumentable)eObject).setDocumentation(value);
										break;
				case "$name" :          if (eObject instanceof INameable)
											((INameable)eObject).setName(value);
										break;
				default : 				if ( attributeName.startsWith("$property:") && (eObject instanceof IProperties) ) {
											String propertyName = attributeName.substring(10);
						
											IProperty propertyToUpdate = null;
											for ( IProperty property: ((IProperties)eObject).getProperties() ) {
												if ( property.getKey().equals(propertyName) ) {
													propertyToUpdate = property;
													logger.trace("   property already exists ...");
													break;
												}
											}
											if ( propertyToUpdate == null ) {
												logger.trace("   creating new property ...");
												IProperty newProperty = IArchimateFactory.eINSTANCE.createProperty();
												newProperty.setKey(propertyName);
												newProperty.setValue(value);
												((IProperties)eObject).getProperties().add(newProperty);
											} else {
												if ( value == null ) {
													logger.trace("   removing property ...");
													((IProperties)eObject).getProperties().remove(propertyToUpdate);
												} else {
													logger.trace("   updating property ...");
													propertyToUpdate.setKey(propertyName);
													propertyToUpdate.setValue(value);
												}
											}
										}  else if ( attributeName.startsWith("$view:") && (eObject instanceof IDiagramModelArchimateObject) ) {
											setAttribute((EObject)((IDiagramModelArchimateObject)eObject).getDiagramModel(), attributeName.substring(6), value);
										} else if ( attributeName.startsWith("$model:") && (eObject instanceof IArchimateModel) ) {
											setAttribute((EObject)((IArchimateModel)eObject).getArchimateModel(), attributeName.substring(7), value);
										} else {
											popup(Level.ERROR, "Please check your configuration file.\n\nDo not know how to set variable "+attributeName);
										}
			}
		} else {
			popup(Level.ERROR, "Please check your configuration file.\n\nCannot set attribute \""+attributeName+"\" as it is not a variable (does not start with a dollar sign).");
		}
	}

	public static Object getIgnoreCase(JSONObject obj, String key, Object defaultValue) {
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

	public static Object getIgnoreCase(JSONObject obj, String key) {
		return getIgnoreCase(obj, key, null);
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
									newCombo.addModifyListener(comboModifyListener);
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
	
	@SuppressWarnings("deprecation")
	private void save() {
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
					} catch (IOException ign) {
						// do nothing
					}
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
			} catch (IOException ign) {
				// do nothing
			}
			
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
}
