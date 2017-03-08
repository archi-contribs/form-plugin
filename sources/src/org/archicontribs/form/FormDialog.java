package org.archicontribs.form;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
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

	public FormDialog(String formName, IArchimateDiagramModel diagramModel) {
		super(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		logger.debug("new formdialog("+formName+","+diagramModel.getName()+")");
		this.formName = formName;
		this.diagramModel = diagramModel;

		createContents();

		dialog.open();
		dialog.layout();
	}

	//TODO : transform to use FormLayout 

	/**
	 * Parses the configuration file and create the corresponding graphical controls
	 */
	private void createContents() {
		try {
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

					logger.trace("   dialog width = "+dialogWidth);
					logger.trace("   dialog height = "+dialogHeight);
					logger.trace("   dialog spacing = "+dialogSpacing);

					int buttonWidth = 70;
					int buttonHeight = 25;

					int tabFolderWidth  =  dialogWidth - dialogSpacing*2;
					int tabFolderHeight =  dialogHeight - dialogSpacing*3 - buttonHeight;

					dialog.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - dialogWidth) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogHeight) / 4, dialogWidth, dialogHeight);
					// we resize the dialog because we want the width and height to be the client's area width and height
					Rectangle area = dialog.getClientArea();
					dialog.setSize(dialogWidth*2 - area.width, dialogHeight*2 - area.height);

					TabFolder tabFolder = new TabFolder(dialog, SWT.BORDER);
					tabFolder.setBounds(dialogSpacing, dialogSpacing, tabFolderWidth, tabFolderHeight);

					//Button saveButton = new Button(dialog, SWT.NONE);
					//saveButton.setBounds(tabFolderWidth - buttonWidth*2, tabFolderHeight + spacing*2, buttonWidth, buttonHeight);
					//saveButton.setText("Save");
					//saveButton.setEnabled(true);
					//saveButton.addSelectionListener(new SelectionListener() {
					//	public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
					//	public void widgetDefaultSelected(SelectionEvent e) { dialog.dispose(); }
					//});

					Button cancelButton = new Button(dialog, SWT.NONE);
					cancelButton.setBounds(tabFolderWidth + dialogSpacing - buttonWidth, tabFolderHeight + dialogSpacing*2, buttonWidth, buttonHeight);
					cancelButton.setText("Close");
					cancelButton.setEnabled(true);
					cancelButton.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
						public void widgetDefaultSelected(SelectionEvent e) { dialog.dispose(); }
					});

					createTabs(form, tabFolder);
				}
			}

		} catch (FileNotFoundException e) {
			popup(Level.ERROR, "Configuration file \""+FormPlugin.configFilePath+"\" not found.");
		} catch (IOException e) {
			popup(Level.ERROR, "I/O Error while reading configuration file \""+FormPlugin.configFilePath+"\"",e);
		} catch (ParseException e) {
			popup(Level.ERROR, "Parsing error while reading configuration file \""+FormPlugin.configFilePath+"\"",e);
		} catch (Exception e) {
			popup(Level.ERROR, "Please check your configuration file.", e);
		}
	}

	/**
	 * Creates the dialog tabItems<br>
	 * <br>
	 * called by the createContents() method
	 */
	private void createTabs(JSONObject form, TabFolder tabFolder) {
		// we iterate over the "tabs" array attributes
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> tabsIterator = ((JSONArray)getIgnoreCase(form, "tabs")).iterator();
		while (tabsIterator.hasNext()) {
			JSONObject tab = tabsIterator.next();

			if ( logger.isDebugEnabled() ) logger.debug("Creating tab " + (String)getIgnoreCase(tab, "name"));

			// we create one TabItem per entry
			TabItem tabItem = new TabItem(tabFolder, SWT.MULTI);
			tabItem.setText((String)getIgnoreCase(tab, "name"));

			Composite composite = new Composite(tabFolder, SWT.NONE);			
			tabItem.setControl(composite);

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
		if ( logger.isDebugEnabled() ) logger.debug("Creating objects in tab");

		// we iterate over the "objects" entries
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> objectsIterator = ((JSONArray)getIgnoreCase(tab, "objects")).iterator();	
		while (objectsIterator.hasNext()) {
			JSONObject jsonObject = objectsIterator.next();

			switch ((String)getIgnoreCase(jsonObject, "type")) {
				case "label" : createLabel(jsonObject, composite); break;
				case "table" : createTable(jsonObject, composite); break;
				case "text" :  createText(jsonObject, composite); break;
				default : throw new RuntimeException("Do not know how to create "+jsonObject.get("type"));
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

		if ( logger.isDebugEnabled() ) logger.debug("   Creating label \""+(String)getIgnoreCase(jsonObject, "value", "")+"\"");
		String value = (String)getExpression(diagramModel, (String)getIgnoreCase(jsonObject, "value")); 
		if ( value != null ) label.setText(value); 

		label.pack();
		label.setLocation((int)(long)getIgnoreCase(jsonObject, "x", (long)label.getLocation().x),  (int)(long)getIgnoreCase(jsonObject, "y", (long)label.getLocation().y));
		label.setSize((int)(long)getIgnoreCase(jsonObject, "width", (long)label.getSize().x),  (int)(long)getIgnoreCase(jsonObject, "height", (long)label.getSize().y));

		String colorString = (String)getIgnoreCase(jsonObject, "background");
		if ( colorString != null ) {
			String[] colorArray = colorString.split(",");
			label.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2])));
		}

		colorString = (String)getIgnoreCase(jsonObject, "foreground");
		if ( colorString != null ) {
			String[] colorArray = colorString.split(",");
			label.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2])));
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
	private Text createText(JSONObject jsonObject, Composite composite) {
		Text text =  new Text(composite, SWT.BORDER);

		String value = (String)getExpression(diagramModel, (String)getIgnoreCase(jsonObject, "value")); 
		if ( value != null ) text.setText(value);
		if ( logger.isDebugEnabled() ) logger.debug("   Creating text \""+(String)getIgnoreCase(jsonObject, "value", "")+"\" ("+value+")");
		
		text.pack();
		text.setLocation((int)(long)getIgnoreCase(jsonObject, "x", (long)text.getLocation().x),  (int)(long)getIgnoreCase(jsonObject, "y", (long)text.getLocation().y));
		text.setSize((int)(long)getIgnoreCase(jsonObject, "width", (long)text.getSize().x),  (int)(long)getIgnoreCase(jsonObject, "height", (long)text.getSize().y));

		String colorString = (String)getIgnoreCase(jsonObject, "background");
		if ( colorString != null ) {
			String[] colorArray = colorString.split(",");
			text.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2])));
		}

		colorString = (String)getIgnoreCase(jsonObject, "foreground");
		if ( colorString != null ) {
			String[] colorArray = colorString.split(",");
			text.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2])));
		}

		text.setData("variable", (String)getIgnoreCase(jsonObject, "value"));

		text.setData("eObject", diagramModel);

		String regex = (String)getIgnoreCase(jsonObject, "regexp");
		String tooltip = (String)getIgnoreCase(jsonObject, "tooltip");
		if ( regex != null ) {
			text.setData("pattern", Pattern.compile(regex));
			if ( tooltip == null ) {
				text.setToolTipText("Your text should match the following regex :\n"+regex);
			} else {
				text.setToolTipText(tooltip);
			}
		}

		//TODO : manage default value

		text.addModifyListener(textModifyListener);

		return text;
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
		if ( logger.isDebugEnabled() ) logger.debug("   Creating table");

		Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		Object x = getIgnoreCase(jsonObject, "x");
		Object y = getIgnoreCase(jsonObject, "y");
		if ( x!=null || y!=null ) {
			table.setLocation(x!=null ? (int)(long)x : table.getLocation().x,  y!=null ? (int)(long)y : table.getLocation().y);
		}

		Object width = getIgnoreCase(jsonObject, "width");
		Object height = getIgnoreCase(jsonObject, "height");
		if ( width!=null || height!=null ) {
			table.setSize(width!=null ? (int)(long)width : table.getSize().x,  height!=null ? (int)(long)height : table.getSize().y);
		}

		// we iterate over the "columns" entries
		Iterator<JSONObject> columnsIterator = ((JSONArray)getIgnoreCase(jsonObject, "columns")).iterator();
		while (columnsIterator.hasNext()) {
			JSONObject column = columnsIterator.next();

			if ( logger.isTraceEnabled() ) logger.trace("      Creating column \"" + (String)getIgnoreCase(column, "name") + "\" of type \"" + (String)getIgnoreCase(column, "type") + "\"");

			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText((String)getIgnoreCase(column, "name"));
			tableColumn.setAlignment(SWT.CENTER);
			tableColumn.setWidth((int)(long)getIgnoreCase(column, "width", (long)(((String)getIgnoreCase(column, "name", " ")).length()*10)));
			tableColumn.setResizable(true);
			tableColumn.setData("type", (String)getIgnoreCase(column, "type"));
			tableColumn.setData("tooltip", (String)getIgnoreCase(column, "tooltip"));
			tableColumn.addListener(SWT.Selection, sortListener);

			switch ( ((String)getIgnoreCase(column, "type")).toLowerCase() ) {
				case "check":
					if ( column.containsKey("values") ) {
						tableColumn.setData("values", (String[])((JSONArray)getIgnoreCase(column, "values")).toArray(new String[0]));
					}
					break;
				case "combo":
					if ( column.containsKey("values") ) { 
						tableColumn.setData("values", (String[])((JSONArray)getIgnoreCase(column, "values")).toArray(new String[0]));
					} else {
						throw new RuntimeException("Missing attribute \"values\" to table column type \"combo\".");
					}
					break;
				case "label":
					break;
				case "text":
					tableColumn.setData("regexp", (String)getIgnoreCase(column, "regexp")); break;
				default : throw new RuntimeException("Unknown column type \""+(String)getIgnoreCase(column, "type")+"\" in table.\n\nValid types are \"check\", \"combo\", \"label\" and \"text\".");
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

				switch ( ((String)getIgnoreCase(line, "type")).toLowerCase() ) {
					case "dynamic":
						if ( logger.isTraceEnabled() ) logger.trace("      Generating dynamic lines");
						addTableItems(table, diagramModel.getChildren(), (JSONArray)getIgnoreCase(line, "values"), (JSONArray)getIgnoreCase(line, "filter"));
						break;
					case "static" :
						if ( logger.isTraceEnabled() ) logger.trace("      Creating static line");
						addTableItem(table, diagramModel, (JSONArray)getIgnoreCase(line, "values"));
						break;
					default : throw new RuntimeException("Unknown line type \""+(String)getIgnoreCase(line, "type")+"\" in table.\n\nValid types are \"dynamic\" and \"static\".");
				}
			}
		}
		return table;
	}

	/**
	 * Checks whether the eObject fits in the filter rules
	 */
	private boolean checkFilter(EObject eObject, JSONArray filterArray) {
		if ( filterArray == null ) {
			return true;
		}

		boolean result = true;

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> filterIterator = filterArray.iterator();
		while (filterIterator.hasNext()) {
			JSONObject filter = filterIterator.next();
			String attribute=(String)getIgnoreCase(filter,  "attribute");
			String operation=(String)getIgnoreCase(filter,  "operation");
			String attributeValue=(String)getIgnoreCase(filter,  "value");

			if ( attribute == null ) { 
				throw new RuntimeException("The property \"attribute\" is missing in filter.");
			}

			if ( operation == null ) {
				throw new RuntimeException("The property \"operation\" is missing in filter.");
			}

			switch (((String)getIgnoreCase(filter, "operation")).toLowerCase()) {
				case "equals" :  if ( attributeValue == null ) { throw new RuntimeException("The property \"value\" is missing in filter."); }
				attributeValue = getExpression(eObject, (String)getIgnoreCase(filter, "attribute"));
				if ( attributeValue == null || !attributeValue.equals((String)getIgnoreCase(filter, "value")) )
					result = false;
				break;
				case "exists" :  if ( attributeValue == null )
					result = false;
				break;
				case "iequals" : if ( attributeValue == null ) { throw new RuntimeException("The property \"value\" is missing in filter."); }
				attributeValue = getExpression(eObject, (String)getIgnoreCase(filter, "attribute"));
				if ( attributeValue == null || !attributeValue.equalsIgnoreCase((String)getIgnoreCase(filter, "value")) )
					result = false;
				break;
				case "matches" : if ( attributeValue == null ) { throw new RuntimeException("The property \"value\" is missing in filter."); }
				attributeValue = getExpression(eObject, (String)getIgnoreCase(filter, "attribute"));
				if ( attributeValue == null || !attributeValue.matches((String)getIgnoreCase(filter, "value")) )
					result = false;
				break;
				default :		throw new RuntimeException("Unknown operation type \""+(String)getIgnoreCase(filter, "operation")+"\" in filter.\n\nValid operations are \"equals\", \"exists\", \"iequals\" and \"matches\".");
			}

			if ( logger.isTraceEnabled() ) logger.trace("Applying filter ("+(String)getIgnoreCase(filter, "attribute")+" "+(String)getIgnoreCase(filter, "operation")+" "+(String)getIgnoreCase(filter, "value")+") --> "+String.valueOf(result));

			if( result == false)
				return false;
			// else we continue to test other filters
		}
		return true;
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
	private void addTableItems(Table table, EList<?> list, JSONArray values, JSONArray filter) {
		if ( (list == null) || list.isEmpty() )
			return;

		if ( list.get(0) instanceof IDiagramModelObject ) {
			for ( IDiagramModelObject diagramObject: (EList<IDiagramModelObject>)list ) {
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

		for ( int columnNumber=0; columnNumber<jsonArray.size(); ++columnNumber) {
			itemValue = getExpression(eObject, (String)jsonArray.get(columnNumber));

			TableEditor editor;
			switch ( ((String)table.getColumn(columnNumber).getData("type")).toLowerCase() ) {
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
					Button button = new Button(table, SWT.CHECK);
					button.pack();
					editor.minimumWidth = button.getSize().x;
					editor.horizontalAlignment = SWT.CENTER;
					button.setData("eObject", eObject);
					button.setData("variable", (String)jsonArray.get(columnNumber));

					String[] values = (String[])table.getColumn(columnNumber).getData("values");
					String value = getExpression(eObject, (String)jsonArray.get(columnNumber));
					if ( values!=null && values.length!=0 ) {
						button.setData("values", table.getColumn(columnNumber).getData("values"));
						button.setSelection(values[0].equals(value));
					} else {
						button.setSelection(value!=null);
					}

					//TODO : manage default value

					button.addSelectionListener(checkButtonSelectionListener);

					editor.setEditor(button, tableItem, columnNumber);
					editors[columnNumber] = editor;
					break;
					
				default : throw new RuntimeException("Unknown object type \""+((String)table.getColumn(columnNumber).getData("type"))+"\".");
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
		//TODO : in case a exception is provided : use multistatus instead
		/*
		        private static MultiStatus createMultiStatus(String msg, Throwable t) {

                List<Status> childStatuses = new ArrayList<>();
                StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

                 for (StackTraceElement stackTrace: stackTraces) {
                        Status status = new Status(IStatus.ERROR,
                                        "com.example.e4.rcp.todo", stackTrace.toString());
                        childStatuses.add(status);
                }

                MultiStatus ms = new MultiStatus("com.example.e4.rcp.todo",
                                IStatus.ERROR, childStatuses.toArray(new Status[] {}),
                                t.toString(), t);
                return ms;
        }
		 */

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
	private String getExpression(EObject eObject, String expression) {
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
												return getExpression((EObject)((IDiagramModelArchimateObject)eObject).getDiagramModel(), expression.substring(6));
											} else throw new RuntimeException("Cannot set view variable has object of class \""+eObject.getClass().getSimpleName()+"\" does not present a \"IDiagramModelArchimateObject\" interface.");
										} else if ( expression.startsWith("$model:") ) {
											if ( eObject instanceof IArchimateModel ) {
												return getExpression((EObject)((IArchimateModel)eObject).getArchimateModel(), expression.substring(7));
											} else throw new RuntimeException("Cannot set model variable has object of class \""+eObject.getClass().getSimpleName()+"\" does not present a \"IArchimateModel\" interface.");
										}
				//TODO : add a preference to choose between silently ignore or raise an error
			}
			throw new RuntimeException("Unknown variable "+expression);
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

	public Object getIgnoreCase(JSONObject obj, String key, Object defaultValue) {
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

	public Object getIgnoreCase(JSONObject obj, String key) {
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
					newTableColumn.setData("type", oldTableColumn.getData("type"));
					newTableColumn.setData("tooltip", oldTableColumn.getData("tooltip"));
					newTableColumn.setData("values", oldTableColumn.getData("values"));
					newTableColumn.setData("regexp", oldTableColumn.getData("regexp"));
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
