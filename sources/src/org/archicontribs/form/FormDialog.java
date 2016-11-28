package org.archicontribs.form;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.archicontribs.form.FormStaticMethod.Level;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
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
import org.eclipse.swt.widgets.Label;
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
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

public class FormDialog extends Dialog {
	private String formName;
	private IArchimateDiagramModel diagramModel;
	
	private Color badValueColor= new Color(Display.getCurrent(), 255, 0, 0);
	private Color goodValueColor = new Color(Display.getCurrent(), 0, 100, 0);
	
	private Shell dialog;
	
	public FormDialog(String formName, IArchimateDiagramModel diagramModel) {
		super(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.formName = formName;
		this.diagramModel = diagramModel;
		
		createContents();

		dialog.open();
		dialog.layout();
	}
	
	private void createContents() {
		try {
			JSONParser parser = new JSONParser();
			
			System.out.println("createContents()");
			
			
				// if we get here, this means that the file has already been parsed to show-up the menu options
				// so we do not have to re-do all the checks
			JSONObject jsonFile = (JSONObject)parser.parse(new FileReader(FormStaticMethod.configFilePath));
			
				// we iterate over the "forms" array entries
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> formsIterator = ((JSONArray) jsonFile.get (FormStaticMethod.PLUGIN_ID)).iterator();
            while (formsIterator.hasNext()) {
            	JSONObject form = formsIterator.next();
            	System.out.println("   found form = " + (String)form.get("name"));
            	
            		// if the entry is related to the form selected in the menu
    			if ( formName.equals((String)form.get("name")) ) {
    				dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    				dialog.setText(diagramModel.getName() + " - " + formName);
    				dialog.setLayout(null);
    				
    				int dialogWidth  = (form.containsKey("width")   ? (int)(long)form.get("width")   : 850);
      				int dialogHeight = (form.containsKey("height")  ? (int)(long)form.get("height")  : 300);
      				
      				int spacing = (form.containsKey("spacing") ? (int)(long)form.get("spacing") : 4);
      				
      				int buttonWidth = 70;
      				int buttonHeight = 25;
      				
    				int tabFolderWidth  =  dialogWidth - spacing*2;
      				int tabFolderHeight =  dialogHeight - spacing*3 - buttonHeight;

    				dialog.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - dialogWidth) / 4, (Toolkit.getDefaultToolkit().getScreenSize().height - dialogHeight) / 4, dialogWidth, dialogHeight);
    					// we resize the dialog because we want the width and height to be the client's area width and height
    				Rectangle area = dialog.getClientArea();
    				dialog.setSize(dialogWidth*2 - area.width, dialogHeight*2 - area.height);
    				
    				TabFolder tabFolder = new TabFolder(dialog, SWT.BORDER);
    				tabFolder.setBounds(spacing, spacing, tabFolderWidth, tabFolderHeight);
    				
    				//Button saveButton = new Button(dialog, SWT.NONE);
    				//saveButton.setBounds(tabFolderWidth - buttonWidth*2, tabFolderHeight + spacing*2, buttonWidth, buttonHeight);
    				//saveButton.setText("Save");
    				//saveButton.setEnabled(true);
    				//saveButton.addSelectionListener(new SelectionListener() {
    				//	public void widgetSelected(SelectionEvent e) { this.widgetDefaultSelected(e); }
    				//	public void widgetDefaultSelected(SelectionEvent e) { dialog.dispose(); }
    				//});
    				
    				Button cancelButton = new Button(dialog, SWT.NONE);
    				cancelButton.setBounds(tabFolderWidth + spacing - buttonWidth, tabFolderHeight + spacing*2, buttonWidth, buttonHeight);
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
			FormStaticMethod.popup(Level.Error, "Configuration file \""+FormStaticMethod.configFilePath+"\" not found.");
		} catch (IOException e) {
			FormStaticMethod.popup(Level.Error, "I/O Error while reading configuration file \""+FormStaticMethod.configFilePath+"\"",e);
		} catch (ParseException e) {
			FormStaticMethod.popup(Level.Error, "Parsing error while reading configuration file \""+FormStaticMethod.configFilePath+"\"",e);
		} catch (Exception e) {
			FormStaticMethod.popup(Level.Error, e.getMessage(), e);
		}
		
		// manage generated exception : attribute not found ...
	}
	
	private void createTabs(JSONObject form, TabFolder tabFolder) throws Exception {
			// we iterate over the "tabs" array attributes
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> tabsIterator = ((JSONArray) form.get("tabs")).iterator();
		while (tabsIterator.hasNext()) {
			JSONObject tab = tabsIterator.next();
			
			System.out.println("      found tab = " + (String)tab.get("name"));
			
				// we create one TabItem per entry
			TabItem tabItem = new TabItem(tabFolder, SWT.MULTI);
			tabItem.setText((String)tab.get("name"));
			
			Composite composite = new Composite(tabFolder, SWT.NONE);			
		 	tabItem.setControl(composite);
			
			createObjects(tab, composite);
		}
	}
	
	private void createObjects(JSONObject tab, Composite composite) throws Exception {
				// we iterate over the "objects" entries
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> objectsIterator = ((JSONArray) tab.get("objects")).iterator();	
			while (objectsIterator.hasNext()) {
				JSONObject object = objectsIterator.next();
				
				switch ((String) object.get("type")) {
				case "label" : createLabel(object, composite); break;
				case "table" : createTable(object, composite); break;
				}
		}
	}
	
	
	private Label createLabel(JSONObject object, Composite composite) {
		Label label =  new Label(composite, SWT.NONE);
		
		if ( object.containsKey("text") ) {
			label.setText((String)object.get("text"));
		}
		
	 	if ( object.containsKey("x") && object.containsKey("y") ) {
	 		label.setLocation((int)(long)object.get("x"), (int)(long)object.get("y"));
	 	}
	 	if ( object.containsKey("width") && object.containsKey("height") ) {
	 		label.setSize((int)(long)object.get("width"), (int)(long)object.get("height"));
	 	}
		
		if ( object.containsKey("background") ) {
			String colorString = (String) object.get("background");
			String[] colorArray = colorString.split(",");
			label.setBackground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2])));
		}
		
		if ( object.containsKey("foreground") ) {
			String colorString = (String) object.get("foreground");
			String[] colorArray = colorString.split(",");
			label.setForeground(new Color(dialog.getDisplay(), Integer.parseInt(colorArray[0]),Integer.parseInt(colorArray[1]),Integer.parseInt(colorArray[2])));
		}
		return label;
	}
	
	@SuppressWarnings("unchecked")
	private Table createTable(JSONObject object, Composite composite) throws Exception {
		Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	
			// we iterate over the "columns" entries
		Iterator<JSONObject> columnsIterator = ((JSONArray) object.get("columns")).iterator();
	 	while (columnsIterator.hasNext()) {
	    	JSONObject column = columnsIterator.next();
	    	
	    	System.out.println("         found column = \"" + (String)column.get("name") + "\" of type \"" + (String)column.get("type") + "\"");
	    	
	    	TableColumn tableColumn = new TableColumn(table, SWT.NONE);
	    	tableColumn.setText((String) column.get("name"));
	    	tableColumn.setAlignment(SWT.CENTER);
	    	tableColumn.setWidth(((String) column.get("name")).length()*10);
	    	tableColumn.setData("type", (String)column.get("type"));
	    	tableColumn.setData("tooltip", (String)column.get("tooltip"));
	    	switch ( (String)column.get("type") ) {
	    		case "label":
	    			break;
	    		
	    		case "text":
	    			tableColumn.setData("regexp", (String)column.get("regexp"));
	    			break;
	    			
	    		case "combo":
	    			tableColumn.setData("values", (String[])((JSONArray)column.get("values")).toArray(new String[0]));
	    			break;
	    			
	    		case "check":
	    			tableColumn.setData("values", (String[])((JSONArray)column.get("values")).toArray(new String[0]));
	    			break;
	    			
	    		default : //TODO : set error	    			
	    	}
		}
	 	
			// we iterate over the "lines" entries
	 	if ( object.containsKey("lines") ) {
			Iterator<JSONObject> linesIterator = ((JSONArray) object.get("lines")).iterator();
		 	while (linesIterator.hasNext()) {
		 		JSONObject line = linesIterator.next();
		 		
		 		switch ( (String)line.get("type") ) {
			 		case "static" :
			 			System.out.println("         found static line");
				 		addTableItem(table, diagramModel, (JSONArray)line.get("values"));
				 		break;
			 		case "dynamic":
			 			System.out.println("         found dynamic line");
			 			
			 			addTableItems(table, diagramModel.getChildren(), (JSONArray)line.get("values"));
			 			break;
			 		
			 		default : //TODO: add error
		 		}
		 	}
		}
	 	
	 	if ( object.containsKey("x") && object.containsKey("y") ) {
	 		table.setLocation((int)(long)object.get("x"), (int)(long)object.get("y"));
	 	}
	 	if ( object.containsKey("width") && object.containsKey("height") ) {
	 		table.setSize((int)(long)object.get("width"), (int)(long)object.get("height"));
	 	}
	 	
	 	return table;
	}
	
	@SuppressWarnings("unchecked")
	private void addTableItems(Table table, EList<?> list, JSONArray jsonArray) throws Exception {
		if ( (list == null) || list.isEmpty() )
			return;
		
		if ( list.get(0) instanceof IDiagramModelObject ) {
			for ( IDiagramModelObject diagramObject: (EList<IDiagramModelObject>)list ) {
				if ( diagramObject.eClass().getName().equals("DiagramModelArchimateObject") ) {
					addTableItem(table, ((IDiagramModelArchimateObject)diagramObject).getArchimateElement(), jsonArray);
				} else {
					addTableItem(table, diagramObject, jsonArray);
				}
				
				addTableItems(table, diagramObject.getSourceConnections(), jsonArray);
				
				if ( diagramObject.eClass().getName().equals("DiagramModelArchimateObject") ) {
					addTableItems(table, ((IDiagramModelArchimateObject)diagramObject).getChildren(), jsonArray);
				}
			}
		} else if ( list.get(0) instanceof IDiagramModelArchimateConnection ) {
			for ( IDiagramModelArchimateConnection diagramConnection: (EList<IDiagramModelArchimateConnection>)list ) {
				addTableItem(table, diagramConnection.getArchimateRelationship(), jsonArray);
				// comment gérer le cas d'une connection sur une connection
			}
		} else {
			throw new Exception("addTableItems : unknown list type (\""+list.get(0).getClass().getSimpleName()+"\"");
		}
	}
	
	private void addTableItem(Table table, EObject eObject, JSONArray jsonArray) {
		TableItem tableItem = new TableItem(table, SWT.NONE);
		
		String itemValue;
		
 		for ( int i=0; i<jsonArray.size(); ++i) {
 			itemValue = null;
 			switch ((String)jsonArray.get(i)) {
	 			case "$name" :
	 				if ( eObject instanceof INameable) {
	 					itemValue = ((INameable)eObject).getName();
	 				}
	 				break;
	 				
	 			case "$id" :
	 				if ( eObject instanceof IIdentifier) {
	 					itemValue = ((IIdentifier)eObject).getId();
	 				}
	 				break;
	 				
	 			case "$type" :
	 				itemValue = eObject.getClass().getSimpleName();
	 				break;
	 				
	 			default :
		 			if ( ((String)jsonArray.get(i)).startsWith("$property:") ) {
		 				if ( eObject instanceof IProperties ) {
			 				String key = ((String)jsonArray.get(i)).substring(10);
			 				for ( IProperty property: ((IProperties)eObject).getProperties() ) {
			 					if ( property.getKey().equals(key) ) {
			 						itemValue = property.getValue();
			 						break;
			 					}		 					
			 				}
		 				}
		 			} else {
		 				itemValue = (String)jsonArray.get(i);
		 			}
 			}
 			
 			TableEditor editor;
 			switch ( (String)table.getColumn(i).getData("type") ) {
 				case "label" : 
 					if ( itemValue != null ) tableItem.setText(i, itemValue);
 					break;
 					
 				case "text" :
 					editor = new TableEditor(table);
 					StyledText text = new StyledText(table, SWT.NONE);
 					if ( itemValue != null ) text.setText(itemValue);
 					text.setToolTipText((String)table.getColumn(i).getData("tooltip"));
 					text.setData("eObject", eObject);
 					text.setData("variable", (String)jsonArray.get(i));
 					
 					if ( table.getColumn(i).getData("regexp") != null ) {
 						String regex = (String)table.getColumn(i).getData("regexp");
 						text.setData("pattern", Pattern.compile(regex));
 						if ( table.getColumn(i).getData("tooltip") == null ) {
 							text.setToolTipText("Your text shoud match the following regex :\n"+regex);
 						}
 					}
 						
	 				text.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							StyledText widget = (StyledText)e.widget;
 				        	String text = widget.getText();
			        		
 				        		// if a regex has been provided, we change the text color to show if it matches
 				        	Pattern pattern = (Pattern)e.widget.getData("pattern");
 				        	if ( pattern != null ) {
 				        		widget.setStyleRange(new StyleRange(0, widget.getText().length(), pattern.matcher(text).matches() ? goodValueColor : badValueColor, null));
 				        	}
			        		 
			        		switch ( (String)widget.getData("variable") ) {
			 	 				case "$name" :
			 	 					((INameable)widget.getData("eObject")).setName(text);
			 	 					break;
			 	 				case "$id" :
			 	 						// cannot update object ID yet (too dangerous)
			 	 					break;
			 	 				case "$type" :
			 	 						// cannot update object type (too dangerous)
			 	 					break;
			 	 				default :
				 	 				if ( ((String)widget.getData("variable")).startsWith("$property:") ) {
				 	 					EObject eObject = (EObject)widget.getData("eObject");
				 	 					if ( eObject instanceof IProperties ) {
							 				String key = ((String)widget.getData("variable")).substring(10);
							 				boolean updated = false;
							 				for ( IProperty property: ((IProperties)eObject).getProperties() ) {
							 					if ( property.getKey().equals(key) ) {
							 						property.setValue(text);
							 						updated = true;
							 						break;
							 					}		 					
							 				}
							 				if ( !updated ) {
							 					// if the property has not been updated, then it must be created
							 					IProperty newProperty = IArchimateFactory.eINSTANCE.createProperty();
							 					newProperty.setKey(key);
							 					newProperty.setValue(text);
							 					((IProperties)eObject).getProperties().add(newProperty);
							 				}
						 				}
				 	 				} else {
				 	 					// shouldn't be here as only variables can be updated
				 	 				}
			        		}
						}
 				    });
	 				
 				    editor.grabHorizontal = true;
 				    editor.setEditor(text, tableItem, i);
 				    break;
 				    
 				case "combo":
 					editor = new TableEditor(table);
 					CCombo combo = new CCombo(table, SWT.NONE);
 					if ( itemValue != null ) combo.setText(itemValue);
 				    combo.setItems((String[])table.getColumn(i).getData("values"));
 				    combo.setToolTipText((String)table.getColumn(i).getData("tooltip"));
 				    combo.setData("object", (String)jsonArray.get(i));
 				    combo.setEditable(false);
 				    editor.grabHorizontal = true;
 				    editor.setEditor(combo, tableItem, i);
				    break;
				    
 				case "check":
 					editor = new TableEditor(table);
 					Button button = new Button(table, SWT.CHECK);
 				    button.pack();
 				    editor.minimumWidth = button.getSize().x;
 				    editor.horizontalAlignment = SWT.CENTER;
 				    button.setSelection((table.getColumn(i).getData("values") != null) && ((String[])table.getColumn(i).getData("values"))[0].equals(itemValue));
 				    button.setData("object", (String)jsonArray.get(i));
 				    editor.setEditor(button, tableItem, i);
 				    
 				default : //TODO: set error
 			}
 		}
	}
	
}
