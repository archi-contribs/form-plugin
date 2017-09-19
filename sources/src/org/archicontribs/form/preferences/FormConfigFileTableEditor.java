/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */

package org.archicontribs.form.preferences;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormJsonParser;
import org.archicontribs.form.FormLogger;
import org.archicontribs.form.FormPlugin;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FormConfigFileTableEditor extends FieldEditor {
	private static final FormLogger logger = new FormLogger(FormConfigFileTableEditor.class);

	private Group grpConfigFiles;

	private Table tblConfigFiles;
	private Label lblFile;
	private Button btnBrowse;
	private Text txtFile;
	
	private Button btnUp;
	private Button btnNew;
	private Button btnRemove;
	private Button btnProperties;
	private Button btnDown;

	private Button btnGraphicalEditor;
	private Button btnDiscard;
	private Button btnSave;
	
	private static final IPreferenceStore store = FormPlugin.INSTANCE.getPreferenceStore();
	
	private static final FormJsonParser jsonParser = new FormJsonParser();

	/**
	 * Creates a table field editor.
	 */
	public FormConfigFileTableEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		if ( logger.isTraceEnabled() ) logger.trace("new FormConfigFileTableEditor(\""+name+"\",\""+labelText+"\")");
		createControl(parent);		// calls doFillIntoGrid
	}

	/*
	 * (non-Javadoc) Method declared in FieldEditor.
	 * 
	 * called by createControl(parent)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		if ( logger.isTraceEnabled() ) logger.trace("doFillIntoGrid()");

		// we create a composite with layout as FormLayout
		grpConfigFiles = new Group(parent, SWT.NONE);
		grpConfigFiles.setFont(parent.getFont());
		grpConfigFiles.setLayout(new FormLayout());
		grpConfigFiles.setBackground(FormPreferencePage.COMPO_BACKGROUND_COLOR);
		grpConfigFiles.setText("Configuration files : ");

		btnUp = new Button(grpConfigFiles, SWT.NONE);
		btnUp.setText("^");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -40);
		btnUp.setLayoutData(fd);
		btnUp.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(-1); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnUp.setEnabled(false);

		btnDown = new Button(grpConfigFiles, SWT.NONE);
		btnDown.setText("v");
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -35);
		fd.right = new FormAttachment(100, -5);
		btnDown.setLayoutData(fd);
		btnDown.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(1); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnDown.setEnabled(false);

		btnNew = new Button(grpConfigFiles, SWT.NONE);
		btnNew.setText("New");
		fd = new FormData();
		fd.top = new FormAttachment(btnUp, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -5);
		btnNew.setLayoutData(fd);
		btnNew.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { newCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});

		btnProperties = new Button(grpConfigFiles, SWT.NONE);
		btnProperties.setText("Properties");
		fd = new FormData();
		fd.top = new FormAttachment(btnNew, 5);
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		btnProperties.setLayoutData(fd);
		btnProperties.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { propertiesCallback(true); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnProperties.setEnabled(false);

		btnGraphicalEditor = new Button(grpConfigFiles, SWT.WRAP);
		btnGraphicalEditor.setText("Graphical editor");
		fd = new FormData();
		fd.top = new FormAttachment(btnProperties, 5);
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		btnGraphicalEditor.setLayoutData(fd);
		btnGraphicalEditor.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { generateCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnGraphicalEditor.setEnabled(false);

		btnRemove = new Button(grpConfigFiles, SWT.NONE);
		btnRemove.setText("Remove");
		fd = new FormData();
		fd.top = new FormAttachment(btnGraphicalEditor, 5);
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		btnRemove.setLayoutData(fd);
		btnRemove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { removeCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnRemove.setEnabled(false);


		tblConfigFiles = new Table(grpConfigFiles, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		tblConfigFiles.setLinesVisible(true);
		fd = new FormData();
		fd.top = new FormAttachment(btnUp, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 10);
		fd.right = new FormAttachment(btnNew, -10, SWT.LEFT);
		fd.bottom = new FormAttachment(btnRemove, 0, SWT.BOTTOM);
		tblConfigFiles.setLayoutData(fd);
		tblConfigFiles.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				tblConfigFiles.getColumns()[0].setWidth(tblConfigFiles.getClientArea().width);
			}
		});
		tblConfigFiles.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				propertiesCallback(false);
			}
		});
		new TableColumn(tblConfigFiles, SWT.NONE);

		lblFile = new Label(grpConfigFiles, SWT.NONE);
		lblFile.setText("File :");
		lblFile.setBackground(FormPreferencePage.COMPO_BACKGROUND_COLOR);
		fd = new FormData();
		fd.top = new FormAttachment(tblConfigFiles, 40);
		fd.left = new FormAttachment(tblConfigFiles, 0 , SWT.LEFT);
		lblFile.setLayoutData(fd);
		lblFile.setVisible(false);

		btnBrowse = new Button(grpConfigFiles, SWT.NONE);
		btnBrowse.setText("Browse");
		fd = new FormData();
		fd.top = new FormAttachment(lblFile, 0, SWT.CENTER);
		fd.right = new FormAttachment(tblConfigFiles, -30, SWT.RIGHT);
		btnBrowse.setLayoutData(fd);
		btnBrowse.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { browseCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnBrowse.setVisible(false);

		txtFile = new Text(grpConfigFiles, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(lblFile, 0, SWT.CENTER);
		fd.left = new FormAttachment(lblFile, 5);
		fd.right = new FormAttachment(btnBrowse, -10);
		txtFile.setLayoutData(fd);
		txtFile.setVisible(false);

		btnSave = new Button(grpConfigFiles, SWT.NONE);
		btnSave.setText("Save");
		fd = new FormData();
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(100, -7);
		btnSave.setLayoutData(fd);
		btnSave.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { saveCallback(); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnSave.setVisible(false);

		btnDiscard = new Button(grpConfigFiles, SWT.NONE);
		btnDiscard.setText("Discard");
		fd = new FormData();
		fd.left = new FormAttachment(btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(btnSave, -5, SWT.TOP);
		btnDiscard.setLayoutData(fd);
		btnDiscard.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { propertiesCallback(false); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		btnDiscard.setVisible(false);


		grpConfigFiles.setTabList(new Control[] {txtFile, btnBrowse, btnDiscard, btnSave});

		grpConfigFiles.layout();

		GridData gd = new GridData();
		gd.heightHint = txtFile.getLocation().y + 10;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpConfigFiles.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns) {
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if ( logger.isTraceEnabled() ) logger.trace("doLoad()");

		tblConfigFiles.removeAll();
		
		int lines = store.getInt(FormPlugin.storeConfigFilesPrefix+"_#");
		
		for (int line = 0; line <lines; line++) {
			TableItem tableItem = new TableItem(tblConfigFiles, SWT.NONE);
			tableItem.setText(store.getString(FormPlugin.storeConfigFilesPrefix+"_"+String.valueOf(line)));
		}
			
		if ( tblConfigFiles.getItemCount() != 0 ) {
			tblConfigFiles.setSelection(0);
			tblConfigFiles.notifyListeners(SWT.Selection, new Event());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore() {
		if ( logger.isTraceEnabled() ) logger.trace("doStore()");
		
		int lines = tblConfigFiles.getItemCount();
		store.setValue(FormPlugin.storeConfigFilesPrefix+"_#", lines);

		for (int line = 0; line < lines; line++) {
			store.setValue(FormPlugin.storeConfigFilesPrefix+"_"+String.valueOf(line), tblConfigFiles.getItem(line).getText());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		return 1;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if ( tblConfigFiles != null )
			tblConfigFiles.setFocus();
	}

	/**
	 * Called when the "new" button has been pressed
	 */
	private void newCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("newCallback()");

		// we unselect all the lines of the tblDatabases table
		tblConfigFiles.deselectAll();
		
		// we show up the edition widgets
		propertiesCallback(true);
	}

	/**
	 * Called when the "save" button has been pressed
	 */
	private void saveCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("saveCallback()");

		if ( txtFile.getText().isEmpty() ) {
		    // TODO : disable the save button when the txtFile field is empty, and enable it when not empty ... may be activate only when valid file with tooltip with error message
		    return;		    
		}
		
		// We check if the configuration file already exists in the table
		for (int line = 0; line < tblConfigFiles.getItemCount(); ++line) {
		    if ( line != tblConfigFiles.getSelectionIndex() ) {
                if ( Paths.get(txtFile.getText()).equals(Paths.get(tblConfigFiles.getItem(line).getText())) ) {
                    FormDialog.popup(Level.ERROR, "The file is already selected, please choose another one.");
                    return;
                }
		    }
		}

		// if a tableItem is selected in the tblconfigFiles table, we replace it. Else, we add a new one
		TableItem tableItem;
		if ( tblConfigFiles.getSelectionIndex() >= 0 ) {
			tableItem = tblConfigFiles.getSelection()[0];
		} else {
			try {
			    tableItem = new TableItem(tblConfigFiles, SWT.NONE);
			} catch (Exception e) {
	            FormDialog.popup(Level.ERROR, "Cannot create new tableItem !", e);
	            return;
	        }
		}
		tableItem.setText(txtFile.getText());

		propertiesCallback(false);

		tblConfigFiles.setSelection(tableItem);
		tblConfigFiles.notifyListeners(SWT.Selection, new Event());
	}

	private void propertiesCallback(boolean editMode) {
		String filename = "";

		if ( tblConfigFiles.getSelectionIndex() != -1 )
			filename = tblConfigFiles.getItem(tblConfigFiles.getSelectionIndex()).getText();

		lblFile.setVisible(editMode);
		txtFile.setVisible(editMode);					txtFile.setText(filename);
		btnBrowse.setVisible(editMode);
		btnSave.setVisible(editMode);
		btnDiscard.setVisible(editMode);

		btnNew.setEnabled(!editMode);
		btnProperties.setEnabled(!editMode && (tblConfigFiles.getSelection()!=null) && (tblConfigFiles.getSelection().length!=0));
		btnRemove.setEnabled(!editMode && (tblConfigFiles.getSelection()!=null) && (tblConfigFiles.getSelection().length!=0));
		btnGraphicalEditor.setEnabled(editMode || ((tblConfigFiles.getSelection()!=null) && (tblConfigFiles.getSelection().length!=0)));
		btnUp.setEnabled(!editMode && (tblConfigFiles.getSelectionIndex() > 0));
		btnDown.setEnabled(!editMode && (tblConfigFiles.getSelectionIndex() < tblConfigFiles.getItemCount()-1));
		tblConfigFiles.setEnabled(!editMode);

		grpConfigFiles.layout();
	}

	/**
	 * Called when the "generate" button has been pressed
	 */
	private void generateCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("generateCallback()");
		
		if ( tblConfigFiles.getSelectionIndex() != -1 ) {
    		String configFilename = tblConfigFiles.getItem(tblConfigFiles.getSelectionIndex()).getText();
            
    		if ( logger.isDebugEnabled() ) logger.debug("Opening configuration file \""+configFilename+"\" ...");
    
            File f = new File(configFilename);
            
            if ( !f.exists() ) {
                try {
                    if ( !f.createNewFile() ) {
                        FormDialog.popup(Level.ERROR, "Failed : cannot create");
                       return;
                    }
                } catch (IOException | SecurityException e) {
                    FormDialog.popup(Level.ERROR, "Failed : cannot create", e);
                    return;
                }
             }
    
            if( f.isDirectory() ) {
                FormDialog.popup(Level.ERROR, "Failed : is a directory");
                return;
            }
    
            if ( !f.canRead() | !f.canWrite() ) {
                FormDialog.popup(Level.ERROR, "Failed : permission denied");
                return;
            }
            
            // if the file is empty, we insert the header
            if ( f.length() == 0 ) {
                try {
                    FileWriter fw = new FileWriter(configFilename);
                    String basename = new File(configFilename).getName().split(".")[0];
                    fw.write("{\n    \"version\": 3,\n    \"org.archicontribs.form\": { \"name\": \""+basename+"\" }\n}");
                    fw.close();
                } catch (IOException e) {
                    FormDialog.popup(Level.ERROR, "Failed : cannot write header", e);
                    return;
                }
            }

            try {
                JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(configFilename));
                Integer version;
                try {
                    version = jsonParser.getInt(json, "version");
                    if ( version != null && version != 3 ) {
                        FormDialog.popup(Level.ERROR, "Failed : not the right version (should be 3).");
                        return;
                    }
                } catch (ClassCastException e) {
                    FormDialog.popup(Level.ERROR, "Failed : the version specified is not an integer (should be 3).");
                    return;
                } catch (RuntimeException e) {
                    FormDialog.popup(Level.ERROR, "Failed : the version is not specified (should be 3).");
                    return;
                }
                
                JSONObject form = jsonParser.getJSONObject(json, FormPlugin.PLUGIN_ID);
            
                new FormGraphicalEditor(configFilename, form);
            } catch (IOException e) {
                FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file \""+configFilename+"\"",e);
            } catch (ParseException e) {
                if ( e.getMessage() !=null )
                    FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \""+configFilename+"\"",e);
                else
                    FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \""+configFilename+"\" : Unexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
            }  catch (ClassCastException e) {
                FormDialog.popup(Level.ERROR, "Wrong key type in the configuration files \""+configFilename+"\"",e);
            }
		}
	}

	/**
	 * Called when the "remove" button has been pressed
	 */
	private void removeCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("removeCallback()");
		// setPresentsDefaultValue(false);
		int index = tblConfigFiles.getSelectionIndex();

		tblConfigFiles.remove(index);

		if ( tblConfigFiles.getItemCount() > 0 ) {
			if ( index < tblConfigFiles.getItemCount() )
				tblConfigFiles.setSelection(index);
			else {
				if ( index > 0 )
					tblConfigFiles.setSelection(index-1);
			}
			propertiesCallback(false);
		} else {
			lblFile.setVisible(false);
			txtFile.setVisible(false);		
			btnBrowse.setVisible(false);

			btnSave.setVisible(false);
			btnDiscard.setVisible(false);

			btnNew.setEnabled(true);
			btnProperties.setEnabled(false);
			btnRemove.setEnabled(false);
			btnGraphicalEditor.setEnabled(false);
			btnUp.setEnabled(false);
			btnDown.setEnabled(false);
			tblConfigFiles.setEnabled(true);

			grpConfigFiles.layout();
		}
	}

	/**
	 * Called when the "browse" button has been pressed
	 */
	private void browseCallback() {
		FileDialog dlg = new FileDialog(Display.getDefault().getActiveShell(), SWT.SINGLE);
		dlg.setFileName(txtFile.getText());
		dlg.setFilterExtensions(new String[]{"*.conf;*.config", "*.*"});
		if (dlg.open() != null) {
			StringBuffer buf = new StringBuffer(dlg.getFilterPath());
			if (buf.charAt(buf.length() - 1) != File.separatorChar)
				buf.append(File.separatorChar);
			buf.append(dlg.getFileName());
			txtFile.setText(buf.toString());
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 *
	 * @param direction :
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	private void swapConfigFileEntries(int direction) {
		if ( logger.isTraceEnabled() ) logger.trace("swap("+direction+")");

		int source = tblConfigFiles.getSelectionIndex();
		int target = tblConfigFiles.getSelectionIndex()+direction;

		if ( logger.isTraceEnabled() ) logger.trace("swapping entrie "+source+" and "+target+".");
		TableItem sourceItem = tblConfigFiles.getItem(source);
		String sourceText = sourceItem.getText();

		TableItem targetItem = tblConfigFiles.getItem(target);
		String targetText = targetItem.getText();

		sourceItem.setText(targetText);
		targetItem.setText(sourceText);

		tblConfigFiles.setSelection(target);
		tblConfigFiles.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * If we are in edit mode, then ask the user is if wants to save or discard
	 */
	public void close() {
		if ( txtFile.isVisible() && txtFile.isEnabled() ) {
			if ( FormDialog.question("Do you wish to save or discard your currents updates ?", new String[] {"save", "discard"}) == 0 ) {
				saveCallback();
			}			
		}
	}
}
