package org.archicontribs.form.preferences;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.log4j.Level;
import org.archicontribs.form.FormLogger;
import org.archicontribs.form.FormPlugin;
import org.archicontribs.form.FormUpdate;
import org.archicontribs.form.FormDialog;
import org.archicontribs.form.preferences.FormFileFieldEditor;
import org.archicontribs.form.preferences.FormTextFieldEditor;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbench;

/**
 * This class sets the preference page that will show up in Archi preference menu.
 * 
 * @author Herve Jouin
 */
public class FormPreferencePage extends FieldEditorPreferencePage	implements IWorkbenchPreferencePage {
	private static String[][] LOGGER_MODES = {{"Disabled", "disabled"}, {"Simple mode", "simple"}, {"Expert mode", "expert"}};
	private static String[][] LOGGER_LEVELS = {{"Fatal", "fatal"}, {"Error", "error"}, {"Warn", "warn"}, {"Info", "info"}, {"Debug", "debug"}, {"Trace", "trace"}};
	
	public static final Color COMPO_BACKGROUND_COLOR = new Color(null, 250, 250, 250);	// light grey
	public static final Color GROUP_BACKGROUND_COLOR = new Color(null, 235, 235, 235);	// light grey (a bit darker than compo background)
	public static final Color RED_COLOR = new Color(null, 240, 0, 0);				// red
	
	private static String HELP_ID = "com.archimatetool.help.FormPreferencePage";
	
	private Composite loggerComposite;
	
	private FormConfigFileTableEditor table;
	
	private RadioGroupFieldEditor loggerModeRadioGroupEditor;
	private FormFileFieldEditor filenameFileFieldEditor;
	private RadioGroupFieldEditor loggerLevelRadioGroupEditor;
	private FormTextFieldEditor expertTextFieldEditor;
	private Group simpleModeGroup;
	private Group expertModeGroup;
	
	private Button btnCheckForUpdateAtStartupButton;
	boolean mouseOverHelpButton = false;
	public static final Image HELP_ICON = new Image(Display.getDefault(), FormDialog.class.getResourceAsStream("/img/28x28/help.png"));
	
	FormLogger logger = new FormLogger(FormPreferencePage.class);
	
	private TabFolder tabFolder;
	
	public FormPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		if ( this.logger.isDebugEnabled() ) this.logger.debug("Setting preference store");
		setPreferenceStore(FormPlugin.INSTANCE.getPreferenceStore());
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
    protected void createFieldEditors() {
		if ( this.logger.isDebugEnabled() ) this.logger.debug("Creating field editors on preference page");
		
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getFieldEditorParent().getParent(), HELP_ID);
        
		this.tabFolder = new TabFolder(getFieldEditorParent(), SWT.NONE);
		this.tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.tabFolder.setBackground(GROUP_BACKGROUND_COLOR);
		
		// ********************************* */
		// * Behaviour tab  **************** */
		// ********************************* */
		
		Composite behaviourComposite = new Composite(this.tabFolder, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 8;
        behaviourComposite.setLayout(layout);
        
        RowLayout rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        rowLayout.pack = true;
        rowLayout.marginTop = 5;
        rowLayout.marginBottom = 5;
        rowLayout.justify = false;
        rowLayout.fill = false;
        behaviourComposite.setLayoutData(rowLayout);
        behaviourComposite.setBackground(GROUP_BACKGROUND_COLOR);
        
		TabItem behaviourTabItem = new TabItem(this.tabFolder, SWT.NONE);
        behaviourTabItem.setText("Behaviour");
        behaviourTabItem.setControl(behaviourComposite);
        		
        Group grpVersion = new Group(behaviourComposite, SWT.NONE);
		grpVersion.setBackground(COMPO_BACKGROUND_COLOR);
		grpVersion.setLayout(new FormLayout());
		grpVersion.setText("Version : ");
		
		Label versionLbl = new Label(grpVersion, SWT.NONE);
		versionLbl.setText("Actual version :");
		versionLbl.setBackground(COMPO_BACKGROUND_COLOR);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 10);
		versionLbl.setLayoutData(fd);
		
		Label versionValue = new Label(grpVersion, SWT.NONE);
		versionValue.setText(FormPlugin.pluginVersion);
		versionValue.setBackground(COMPO_BACKGROUND_COLOR);
		versionValue.setFont(FormDialog.BOLD_FONT);
		fd = new FormData();
		fd.top = new FormAttachment(versionLbl, 0, SWT.TOP);
		fd.left = new FormAttachment(versionLbl, 5);
		versionValue.setLayoutData(fd);
		
		Button checkUpdateButton = new Button(grpVersion, SWT.NONE);
		checkUpdateButton.setBackground(COMPO_BACKGROUND_COLOR);
		checkUpdateButton.setText("Check for update");
		fd = new FormData();
		fd.top = new FormAttachment(versionValue, 0, SWT.CENTER);
		fd.left = new FormAttachment(versionValue, 100);
		checkUpdateButton.setLayoutData(fd);
		checkUpdateButton.addSelectionListener(new SelectionListener() {
			@Override
            public void widgetSelected(SelectionEvent e) {
				new Thread("checkForUpdate") {
					@Override
					public void run() {
						FormUpdate formUpdate = new FormUpdate();
						formUpdate.checkUpdate(true);
					}
				}.start();
			}
			@Override
            public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		
		this.btnCheckForUpdateAtStartupButton = new Button(grpVersion, SWT.CHECK);
		this.btnCheckForUpdateAtStartupButton.setBackground(COMPO_BACKGROUND_COLOR);
		this.btnCheckForUpdateAtStartupButton.setText("Automatically check for update at startup");
		fd = new FormData();
		fd.top = new FormAttachment(versionLbl, 5);
		fd.left = new FormAttachment(0, 10);
		this.btnCheckForUpdateAtStartupButton.setLayoutData(fd);
		this.btnCheckForUpdateAtStartupButton.setSelection(FormPlugin.INSTANCE.getPreferenceStore().getBoolean("checkForUpdateAtStartup"));
		
		GridData gd = new GridData();
		//gd.heightHint = 45;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpVersion.setLayoutData(gd);
		
		this.table = new FormConfigFileTableEditor("ConfigFiles", "", behaviourComposite);
		addField(this.table);
		
		Group grpHelp = new Group(behaviourComposite, SWT.NONE);
		grpHelp.setBackground(COMPO_BACKGROUND_COLOR);
        grpHelp.setLayout(new FormLayout());
        grpHelp.setText("Online help : ");
        
        gd = new GridData();
        //gd.heightHint = 40;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        grpHelp.setLayoutData(gd);
        
        Label btnHelp = new Label(grpHelp, SWT.NONE);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { FormPreferencePage.this.mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { FormPreferencePage.this.mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( FormPreferencePage.this.mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( FormPreferencePage.this.logger.isDebugEnabled() ) FormPreferencePage.this.logger.debug("Showing help : /"+FormPlugin.PLUGIN_ID+"/help/html/configurePlugin.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+FormPlugin.PLUGIN_ID+"/help/html/configurePlugin.html"); } });
        fd = new FormData(30,30);
        fd.top = new FormAttachment(0, 11);
        fd.left = new FormAttachment(0, 10);
        btnHelp.setLayoutData(fd);
        
        Label helpLbl = new Label(grpHelp, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setBackground(COMPO_BACKGROUND_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 10);
        helpLbl.setLayoutData(fd);
		
		// ********************************* */
		// * Logging tab  ****************** */
		// ********************************* */
        this.loggerComposite = new Composite(this.tabFolder, SWT.NULL);
        rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        this.loggerComposite.setLayout(rowLayout);
        
        TabItem loggerTabItem = new TabItem(this.tabFolder, SWT.NONE);
        loggerTabItem.setText("Logger");
        loggerTabItem.setControl(this.loggerComposite);
        
        Label note = new Label(this.loggerComposite, SWT.NONE);
        note = new Label(this.loggerComposite, SWT.NONE);
        note.setText(" Please be aware that enabling debug or, even more, trace level has got important impact on performances!\n Activate only if required.");
        note.setForeground(RED_COLOR);
    	
    	this.loggerModeRadioGroupEditor = new RadioGroupFieldEditor("loggerMode", "", 1, LOGGER_MODES, this.loggerComposite, true);
    	addField(this.loggerModeRadioGroupEditor);
    	
    	this.simpleModeGroup = new Group(this.loggerComposite, SWT.NONE);
    	this.simpleModeGroup.setLayout(new GridLayout(3, false));
    	gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 500;
        this.simpleModeGroup.setLayoutData(gd);
        
        
        this.loggerLevelRadioGroupEditor = new RadioGroupFieldEditor("loggerLevel", "", 6, LOGGER_LEVELS, this.simpleModeGroup, false);
        addField(this.loggerLevelRadioGroupEditor);
        
        this.filenameFileFieldEditor = new FormFileFieldEditor("loggerFilename", "Log filename : ", false, StringFieldEditor.VALIDATE_ON_KEY_STROKE, this.simpleModeGroup);
        addField(this.filenameFileFieldEditor);    
        
    	this.expertModeGroup = new Group(this.loggerComposite, SWT.NONE);
    	this.expertModeGroup.setLayout(new GridLayout());
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.widthHint = 650;
    	this.expertModeGroup.setLayoutData(gd);
        
        this.expertTextFieldEditor = new FormTextFieldEditor("loggerExpert", "", this.expertModeGroup);
        this.expertTextFieldEditor.getTextControl().setLayoutData(gd);
        this.expertTextFieldEditor.getTextControl().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        addField(this.expertTextFieldEditor);

        showLogger();
	}
	
	@Override
    public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
		if ( event.getSource() == this.loggerModeRadioGroupEditor )
			showLogger();
		
		 if( event.getSource() == this.filenameFileFieldEditor )
			 setValid(true);
	}
	
	private void showLogger() {
		String mode = null;
		
		// If the user changes the value, we get it
		for ( Control control: this.loggerModeRadioGroupEditor.getRadioBoxControl(this.loggerComposite).getChildren() ) {
			if (((Button)control).getSelection())
				mode = (String)((Button)control).getData();
		}
		
		// when the preference page initialize, the radioButton selection is not (yet) made.
		// so we get the value from the preferenceStore
		if ( mode == null ) {
			mode = FormPlugin.INSTANCE.getPreferenceStore().getString("loggerMode");
    		if ( mode == null ) {
    			mode = FormPlugin.INSTANCE.getPreferenceStore().getDefaultString("loggerMode");
    		}
		}
		
		// Defining of the user's choice, we show up the simple or expert parameters or none of them
		switch ( mode ) {
		case "disabled" :
			this.expertModeGroup.setVisible(false);
			this.simpleModeGroup.setVisible(false);
			break;
		case "simple" :
			this.expertModeGroup.setVisible(false);
			this.simpleModeGroup.setVisible(true);
			break;
		case "expert" :
			this.expertModeGroup.setVisible(true);
			this.simpleModeGroup.setVisible(false);
			break;
		default : 
			this.expertModeGroup.setVisible(false);
			this.simpleModeGroup.setVisible(false);
			this.logger.error("Unknown value \""+mode+"\" in loggerModeRadioGroupEditor.");
		}
	}
	
    @Override
    public boolean performOk() {
    	this.table.close();
    	
    	if ( this.logger.isTraceEnabled() ) this.logger.trace("Saving preferences in preference store");
    	
    	FormPlugin.INSTANCE.getPreferenceStore().setValue("checkForUpdateAtStartup", this.btnCheckForUpdateAtStartupButton.getSelection());
    	
    	this.table.store();
    	
   	    	// the loggerMode is a private property, so we use reflection to access it
		try {
			Field field = RadioGroupFieldEditor.class.getDeclaredField("value");
			field.setAccessible(true);
			if ( this.logger.isTraceEnabled() ) this.logger.trace("loggerMode = "+(String)field.get(this.loggerModeRadioGroupEditor));
			field.setAccessible(false);
		} catch (Exception err) {
			this.logger.error("Failed to retrieve the \"loggerMode\" value from the preference page", err);
		}
		this.loggerModeRadioGroupEditor.store();
    	
    		// the loggerLevel is a private property, so we use reflection to access it
		try {
			Field field = RadioGroupFieldEditor.class.getDeclaredField("value");
			field.setAccessible(true);
			if ( this.logger.isTraceEnabled() ) this.logger.trace("loggerLevel = "+(String)field.get(this.loggerLevelRadioGroupEditor));
			field.setAccessible(false);
		} catch (Exception err) {
			this.logger.error("Failed to retrieve the \"loggerLevel\" value from the preference page", err);
		}
		this.loggerLevelRadioGroupEditor.store();
		
			//TODO : if we are in simple mode, check that is is a valid writable filename
		if ( this.logger.isTraceEnabled() ) this.logger.trace("loggerFilename = "+this.filenameFileFieldEditor.getStringValue());
		this.filenameFileFieldEditor.store();
		
		if ( this.logger.isTraceEnabled() ) this.logger.trace("loggerExpert = "+this.expertTextFieldEditor.getStringValue());
		this.expertTextFieldEditor.store();
		
        try {
        	if ( this.logger.isDebugEnabled() ) this.logger.debug("Saving the preference store to disk.");
            ((IPersistentPreferenceStore)FormPlugin.INSTANCE.getPreferenceStore()).save();
        } catch (IOException err) {
        	FormDialog.popup(Level.ERROR, "Failed to save the preference store to disk.", err);
        }
		
		try {
			this.logger.configure();
		} catch (Exception e) {
			FormDialog.popup(Level.ERROR, "Faied to configure logger", e);
		}
		
    	return true;
    }

	@Override
	public void init(IWorkbench workbench) {
	    // nothing to do
	}

}