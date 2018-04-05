/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */

package org.archicontribs.form.preferences;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.archicontribs.form.FormDialog;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FormConfigFileTableEditor extends FieldEditor {
	private static final FormLogger logger = new FormLogger(FormConfigFileTableEditor.class);

	private Group grpConfigFiles;

	Table tblConfigFiles;
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
	@Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
		if ( logger.isTraceEnabled() ) logger.trace("doFillIntoGrid()");

		// we create a composite with layout as FormLayout
		this.grpConfigFiles = new Group(parent, SWT.NONE);
		this.grpConfigFiles.setFont(parent.getFont());
		this.grpConfigFiles.setLayout(new FormLayout());
		this.grpConfigFiles.setBackground(FormPreferencePage.COMPO_BACKGROUND_COLOR);
		this.grpConfigFiles.setText("Configuration files : ");

		this.btnUp = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnUp.setText("^");
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -40);
		this.btnUp.setLayoutData(fd);
		this.btnUp.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(-1); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnUp.setEnabled(false);

		this.btnDown = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnDown.setText("v");
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(100, -35);
		fd.right = new FormAttachment(100, -5);
		this.btnDown.setLayoutData(fd);
		this.btnDown.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { swapConfigFileEntries(1); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnDown.setEnabled(false);

		this.btnNew = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnNew.setText("New");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, 5);
		fd.left = new FormAttachment(100, -70);
		fd.right = new FormAttachment(100, -5);
		this.btnNew.setLayoutData(fd);
		this.btnNew.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { newCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});

		this.btnProperties = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnProperties.setText("Properties");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnNew, 5);
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		this.btnProperties.setLayoutData(fd);
		this.btnProperties.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { propertiesCallback(true); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnProperties.setEnabled(false);

		this.btnGraphicalEditor = new Button(this.grpConfigFiles, SWT.WRAP);
		this.btnGraphicalEditor.setText("Graphical editor");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnProperties, 5);
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		this.btnGraphicalEditor.setLayoutData(fd);
		this.btnGraphicalEditor.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { generateCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnGraphicalEditor.setEnabled(false);

		this.btnRemove = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnRemove.setText("Remove");
		fd = new FormData();
		fd.top = new FormAttachment(this.btnGraphicalEditor, 5);
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		this.btnRemove.setLayoutData(fd);
		this.btnRemove.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { removeCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnRemove.setEnabled(false);


		this.tblConfigFiles = new Table(this.grpConfigFiles, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		this.tblConfigFiles.setLinesVisible(true);
		fd = new FormData();
		fd.top = new FormAttachment(this.btnUp, 0, SWT.TOP);
		fd.left = new FormAttachment(0, 10);
		fd.right = new FormAttachment(this.btnNew, -10, SWT.LEFT);
		fd.bottom = new FormAttachment(this.btnRemove, 0, SWT.BOTTOM);
		this.tblConfigFiles.setLayoutData(fd);
		this.tblConfigFiles.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				FormConfigFileTableEditor.this.tblConfigFiles.getColumns()[0].setWidth(FormConfigFileTableEditor.this.tblConfigFiles.getClientArea().width);
			}
		});
		this.tblConfigFiles.addListener(SWT.Selection, new Listener() {
			@Override
            public void handleEvent(Event e) {
				propertiesCallback(false);
			}
		});
		
		@SuppressWarnings("unused")
        TableColumn tableColumn = new TableColumn(this.tblConfigFiles, SWT.NONE);

		this.lblFile = new Label(this.grpConfigFiles, SWT.NONE);
		this.lblFile.setText("File :");
		this.lblFile.setBackground(FormPreferencePage.COMPO_BACKGROUND_COLOR);
		fd = new FormData();
		fd.top = new FormAttachment(this.tblConfigFiles, 40);
		fd.left = new FormAttachment(this.tblConfigFiles, 0 , SWT.LEFT);
		this.lblFile.setLayoutData(fd);
		this.lblFile.setVisible(false);

		this.btnBrowse = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnBrowse.setText("Browse");
		fd = new FormData();
		fd.top = new FormAttachment(this.lblFile, 0, SWT.CENTER);
		fd.right = new FormAttachment(this.tblConfigFiles, -30, SWT.RIGHT);
		this.btnBrowse.setLayoutData(fd);
		this.btnBrowse.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { browseCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnBrowse.setVisible(false);

		this.txtFile = new Text(this.grpConfigFiles, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(this.lblFile, 0, SWT.CENTER);
		fd.left = new FormAttachment(this.lblFile, 5);
		fd.right = new FormAttachment(this.btnBrowse, -10);
		this.txtFile.setLayoutData(fd);
		this.txtFile.setVisible(false);

		this.btnSave = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnSave.setText("Save");
		fd = new FormData();
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(100, -7);
		this.btnSave.setLayoutData(fd);
		this.btnSave.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { saveCallback(); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnSave.setVisible(false);

		this.btnDiscard = new Button(this.grpConfigFiles, SWT.NONE);
		this.btnDiscard.setText("Discard");
		fd = new FormData();
		fd.left = new FormAttachment(this.btnNew, 0, SWT.LEFT);
		fd.right = new FormAttachment(this.btnNew, 0, SWT.RIGHT);
		fd.bottom = new FormAttachment(this.btnSave, -5, SWT.TOP);
		this.btnDiscard.setLayoutData(fd);
		this.btnDiscard.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) { propertiesCallback(false); }
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		this.btnDiscard.setVisible(false);


		this.grpConfigFiles.setTabList(new Control[] {this.txtFile, this.btnBrowse, this.btnDiscard, this.btnSave});

		this.grpConfigFiles.layout();

		GridData gd = new GridData();
		gd.heightHint = this.txtFile.getLocation().y + 10;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		this.grpConfigFiles.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void adjustForNumColumns(int numColumns) {
	    // nothing to do
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doLoadDefault() {
	    // nothing to do
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doLoad() {
		if ( logger.isTraceEnabled() ) logger.trace("doLoad()");

		this.tblConfigFiles.removeAll();
		
		int lines = store.getInt(FormPlugin.storeConfigFilesPrefix+"_#");
		
		for (int line = 0; line <lines; line++) {
			TableItem tableItem = new TableItem(this.tblConfigFiles, SWT.NONE);
			tableItem.setText(store.getString(FormPlugin.storeConfigFilesPrefix+"_"+String.valueOf(line)));
		}
			
		if ( this.tblConfigFiles.getItemCount() != 0 ) {
			this.tblConfigFiles.setSelection(0);
			this.tblConfigFiles.notifyListeners(SWT.Selection, new Event());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doStore() {
		if ( logger.isTraceEnabled() ) logger.trace("doStore()");
		
		int lines = this.tblConfigFiles.getItemCount();
		store.setValue(FormPlugin.storeConfigFilesPrefix+"_#", lines);

		for (int line = 0; line < lines; line++) {
			store.setValue(FormPlugin.storeConfigFilesPrefix+"_"+String.valueOf(line), this.tblConfigFiles.getItem(line).getText());
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public int getNumberOfControls() {
		return 1;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public void setFocus() {
		if ( this.tblConfigFiles != null )
			this.tblConfigFiles.setFocus();
	}

	/**
	 * Called when the "new" button has been pressed
	 */
	void newCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("newCallback()");

		// we unselect all the lines of the tblDatabases table
		this.tblConfigFiles.deselectAll();
		
		// we show up the edition widgets
		propertiesCallback(true);
	}

	/**
	 * Called when the "save" button has been pressed
	 */
	void saveCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("saveCallback()");

		if ( this.txtFile.getText().isEmpty() ) {
		    // TODO : disable the save button when the txtFile field is empty, and enable it when not empty ... may be activate only when valid file with tooltip with error message
		    return;		    
		}
		
		// We check if the configuration file already exists in the table
		for (int line = 0; line < this.tblConfigFiles.getItemCount(); ++line) {
		    if ( line != this.tblConfigFiles.getSelectionIndex() ) {
                if ( Paths.get(this.txtFile.getText()).equals(Paths.get(this.tblConfigFiles.getItem(line).getText())) ) {
                    FormDialog.popup(Level.ERROR, "The file is already selected, please choose another one.");
                    return;
                }
		    }
		}

		// if a tableItem is selected in the tblconfigFiles table, we replace it. Else, we add a new one
		TableItem tableItem;
		if ( this.tblConfigFiles.getSelectionIndex() >= 0 ) {
			tableItem = this.tblConfigFiles.getSelection()[0];
		} else {
			try {
			    tableItem = new TableItem(this.tblConfigFiles, SWT.NONE);
			} catch (Exception e) {
	            FormDialog.popup(Level.ERROR, "Cannot create new tableItem !", e);
	            return;
	        }
		}
		tableItem.setText(this.txtFile.getText());

		propertiesCallback(false);

		this.tblConfigFiles.setSelection(tableItem);
		this.tblConfigFiles.notifyListeners(SWT.Selection, new Event());
	}

	void propertiesCallback(boolean editMode) {
		String filename = "";

		if ( this.tblConfigFiles.getSelectionIndex() != -1 )
			filename = this.tblConfigFiles.getItem(this.tblConfigFiles.getSelectionIndex()).getText();

		this.lblFile.setVisible(editMode);
		this.txtFile.setVisible(editMode);					this.txtFile.setText(filename);
		this.btnBrowse.setVisible(editMode);
		this.btnSave.setVisible(editMode);
		this.btnDiscard.setVisible(editMode);

		this.btnNew.setEnabled(!editMode);
		this.btnProperties.setEnabled(!editMode && (this.tblConfigFiles.getSelection()!=null) && (this.tblConfigFiles.getSelection().length!=0));
		this.btnRemove.setEnabled(!editMode && (this.tblConfigFiles.getSelection()!=null) && (this.tblConfigFiles.getSelection().length!=0));
		this.btnGraphicalEditor.setEnabled(!editMode && (this.tblConfigFiles.getSelection()!=null) && (this.tblConfigFiles.getSelection().length!=0));
		this.btnUp.setEnabled(!editMode && (this.tblConfigFiles.getSelectionIndex() > 0));
		this.btnDown.setEnabled(!editMode && (this.tblConfigFiles.getSelectionIndex() < this.tblConfigFiles.getItemCount()-1));
		this.tblConfigFiles.setEnabled(!editMode);

		this.grpConfigFiles.layout();
	}

	/**
	 * Called when the "generate" button has been pressed
	 */
	@SuppressWarnings("unchecked") void generateCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("generateCallback()");
		
		if ( this.tblConfigFiles.getSelectionIndex() != -1 ) {
    		String configFilename = this.tblConfigFiles.getItem(this.tblConfigFiles.getSelectionIndex()).getText();
            
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
            
            JSONObject form = null;
            
            // if the file is empty, we insert the header
            if ( f.length() == 0 ) {
                String basename = new File(configFilename).getName();
                String[] basenameParts = basename.split("\\.");
                if ( basenameParts.length > 0 ) basename = basenameParts[0];
                
                JSONObject tab = new JSONObject();
                tab.put("name", "new tab");
                tab.put("controls", new JSONArray());
                
                JSONArray tabs = new JSONArray();
                tabs.add(tab);
                
                form = new JSONObject();
                form.put("name", basename);
                form.put("tabs", tabs);
                
            } else {
                JSONObject jsonFile;
                try ( FileReader reader = new FileReader(configFilename) ){
                    jsonFile = (JSONObject) new JSONParser().parse(reader);
                    Integer version = FormJsonParser.getInt(jsonFile, "version", 0, true);
                    if ( version != null && version != 3 ) {
                        FormDialog.popup(Level.ERROR, "Failed : not the right version (should be 3).");
                        return;
                    }
                    
                    form = FormJsonParser.getJSONObject(jsonFile, FormPlugin.PLUGIN_ID);
                    
                } catch (@SuppressWarnings("unused") ClassCastException ign) {
                    FormDialog.popup(Level.ERROR, "Failed : the version specified is not an integer (should be 3).");
                    return;
                } catch (@SuppressWarnings("unused") RuntimeException ign) {
                    FormDialog.popup(Level.ERROR, "Failed : the version is not specified (should be 3).");
                    return;
                } catch (IOException e) {
                    FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file \""+configFilename+"\"",e);
                } catch (ParseException e) {
                    if ( e.getMessage() !=null ) {
                        FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \""+configFilename+"\"",e);
                    } else {
                        FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file \""+configFilename+"\" : Unexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
                    }
                }
            }
            
            @SuppressWarnings("unused")
            FormDialog formDialog = new FormDialog(configFilename, form, null);
		}
	}

	/**
	 * Called when the "remove" button has been pressed
	 */
	void removeCallback() {
		if ( logger.isTraceEnabled() ) logger.trace("removeCallback()");
		// setPresentsDefaultValue(false);
		int index = this.tblConfigFiles.getSelectionIndex();

		this.tblConfigFiles.remove(index);

		if ( this.tblConfigFiles.getItemCount() > 0 ) {
			if ( index < this.tblConfigFiles.getItemCount() )
				this.tblConfigFiles.setSelection(index);
			else {
				if ( index > 0 )
					this.tblConfigFiles.setSelection(index-1);
			}
			propertiesCallback(false);
		} else {
			this.lblFile.setVisible(false);
			this.txtFile.setVisible(false);		
			this.btnBrowse.setVisible(false);

			this.btnSave.setVisible(false);
			this.btnDiscard.setVisible(false);

			this.btnNew.setEnabled(true);
			this.btnProperties.setEnabled(false);
			this.btnRemove.setEnabled(false);
			this.btnGraphicalEditor.setEnabled(false);
			this.btnUp.setEnabled(false);
			this.btnDown.setEnabled(false);
			this.tblConfigFiles.setEnabled(true);

			this.grpConfigFiles.layout();
		}
	}

	/**
	 * Called when the "browse" button has been pressed
	 */
	void browseCallback() {
		FileDialog dlg = new FileDialog(Display.getDefault().getActiveShell(), SWT.SINGLE);
		dlg.setFileName(this.txtFile.getText());
		dlg.setFilterExtensions(new String[]{"*.conf;*.config", "*.*"});
		if (dlg.open() != null) {
			StringBuffer buf = new StringBuffer(dlg.getFilterPath());
			if (buf.charAt(buf.length() - 1) != File.separatorChar)
				buf.append(File.separatorChar);
			buf.append(dlg.getFileName());
			this.txtFile.setText(buf.toString());
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 *
	 * @param direction :
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	void swapConfigFileEntries(int direction) {
		if ( logger.isTraceEnabled() ) logger.trace("swap("+direction+")");

		int source = this.tblConfigFiles.getSelectionIndex();
		int target = this.tblConfigFiles.getSelectionIndex()+direction;

		if ( logger.isTraceEnabled() ) logger.trace("swapping entrie "+source+" and "+target+".");
		TableItem sourceItem = this.tblConfigFiles.getItem(source);
		String sourceText = sourceItem.getText();

		TableItem targetItem = this.tblConfigFiles.getItem(target);
		String targetText = targetItem.getText();

		sourceItem.setText(targetText);
		targetItem.setText(sourceText);

		this.tblConfigFiles.setSelection(target);
		this.tblConfigFiles.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * If we are in edit mode, then ask the user is if wants to save or discard
	 */
	public void close() {
		if ( this.txtFile.isVisible() && this.txtFile.isEnabled() ) {
			if ( FormDialog.question("Do you wish to save or discard your currents updates ?", new String[] {"save", "discard"}) == 0 ) {
				saveCallback();
			}			
		}
	}
}
