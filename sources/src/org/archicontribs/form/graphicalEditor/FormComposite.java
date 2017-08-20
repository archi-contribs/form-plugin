package org.archicontribs.form.graphicalEditor;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TreeItem;

public class FormComposite extends Composite {
    private StyledText txtName              = null;		// name
    private StyledText txtVariableSeparator = null;		// variableSeparator
    private StyledText txtWidth             = null;		// width
    private StyledText txtHeight            = null;		// height
    private StyledText txtSpacing           = null;		// spacing
    private Label      lblBackgroundColor   = null;		// background
    private CCombo     comboRefers          = null;		// refers
    private StyledText txtOk                = null;		// okButton
    private StyledText txtCancel            = null;		// cancelButton
    private StyledText txtExport            = null;		// exportToExcelButton
    private CCombo     comboWhenEmpty       = null;		// whenEmpty

	public FormComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// Name
        Label lblName = new Label(this, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblName.setLayoutData(fd);
        lblName.setText("Name:");
        
        txtName = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblName, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtName.setLayoutData(fd);
        txtName.addModifyListener(nameModifyListener);
        txtName.setToolTipText("Name of the form.\n"+
        		"\n"+
        		"Can be any arbitrary text and may include variables."
        		);
        
        // VariableSeparator
        Label lblVariableSeparator = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblName, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblVariableSeparator.setLayoutData(fd);
        lblVariableSeparator.setText("Variable separator:");
        
        txtVariableSeparator = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblVariableSeparator, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtVariableSeparator.setLayoutData(fd);
        txtVariableSeparator.setTextLimit(1);
        txtVariableSeparator.addModifyListener(variableSeparatorModifyListener);
        txtVariableSeparator.setToolTipText("Character used to separate the different fields in a variable (please use a special character, not alphabetic nor a number).\n"+
        		"\n"+
        		"Default: colon (':')."
        		);
        
        // Width
        Label lblWidth = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblVariableSeparator, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblWidth.setLayoutData(fd);
        lblWidth.setText("Width:");
        
        txtWidth = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtWidth.setLayoutData(fd);
        txtWidth.setTextLimit(4);
        txtWidth.addVerifyListener(numericVerifyListener);
        txtWidth.addModifyListener(sizeModifyListener);
        txtWidth.setToolTipText("Width of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogWidth+"."
        		);
        
        // Height
        Label lblHeight = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblWidth, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblHeight.setLayoutData(fd);
        lblHeight.setText("Height:");
        
        txtHeight = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtHeight.setLayoutData(fd);
        txtHeight.setTextLimit(4);
        txtHeight.addVerifyListener(numericVerifyListener);
        txtHeight.addModifyListener(sizeModifyListener);
        txtHeight.setToolTipText("Height of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogHeight+"."
        		);
        
        // Spacing
        Label lblSpacing = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblHeight, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblSpacing.setLayoutData(fd);
        lblSpacing.setText("Spacing:");
        
        txtSpacing = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpacing, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtSpacing.setLayoutData(fd);
        txtSpacing.setTextLimit(2);
        txtSpacing.addVerifyListener(numericVerifyListener);
        txtSpacing.addModifyListener(sizeModifyListener);
        txtSpacing.setToolTipText("Space to leave bewteen the form border and the tab in the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogSpacing+"."
        		);
        
        // Background
        Label lblBackground = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpacing, 10);
        fd.left = new FormAttachment(lblName, 0, SWT.LEFT);
        fd.right = new FormAttachment(lblName, 0, SWT.RIGHT);
        lblBackground.setLayoutData(fd);
        lblBackground.setText("Background color:");
        
        Button btnBackgroundColorEraser = new Button(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.CENTER);
        fd.right = new FormAttachment(txtName, 0, SWT.RIGHT);
        btnBackgroundColorEraser.setLayoutData(fd);
        btnBackgroundColorEraser.setImage(FormGraphicalEditor.binImage);
        btnBackgroundColorEraser.addSelectionListener(colorEraser);
        btnBackgroundColorEraser.setToolTipText("Background color of the form.\n"+
	        "\n"+
	        "Please select a color, or leave empty to keed the default color."
	        );
        
        Button btnBackgroundColorChooser = new Button(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.CENTER);
        fd.right = new FormAttachment(btnBackgroundColorEraser, -5, SWT.LEFT);
        btnBackgroundColorChooser.setLayoutData(fd);
        btnBackgroundColorChooser.setText(" ... ");
        btnBackgroundColorChooser.addSelectionListener(colorChooser);
        btnBackgroundColorChooser.setToolTipText("Background color of the form.\n"+
        		"\n"+
        		"Please select a color, or leave empty to keed the default color."
        		);
        
        lblBackgroundColor = new Label(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.TOP);
        fd.left = new FormAttachment(txtName, 0, SWT.LEFT);
        fd.right = new FormAttachment(btnBackgroundColorChooser, -5);
        lblBackgroundColor.setLayoutData(fd);
        lblBackgroundColor.setToolTipText("Background color of the form.\n"+
        		"\n"+
        		"Please select a color, or leave empty to keed the default color."
        		);
        
        // Refers
        Label lblRefers = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblRefers.setLayoutData(fd);
        lblRefers.setText("Refers:");
        
        comboRefers = new CCombo(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblRefers, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        comboRefers.setLayoutData(fd);
        comboRefers.setItems(FormDialog.validRefers);
        comboRefers.setText(FormDialog.defaultRefers);
        comboRefers.addModifyListener(refersModifyListener);
        comboRefers.setToolTipText("Choose which component the form will apply to:\n"+
        		"   - Selected: the component(s) that will be selected when the form will be ran,\n"+
        		"   - Folder: the folder in which the selected components are,\n"+
        		"   - View: the view in which the selected components are,\n"+
        		"   - Model: the whole model.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultRefers+"."
        		);
        
        // OkButton
        Label lblOk = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblRefers, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblOk.setLayoutData(fd);
        lblOk.setText("Ok Button:");
        
        txtOk = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblOk, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtOk.setLayoutData(fd);
        txtOk.setText(FormDialog.defaultButtonOkText);
        txtOk.addModifyListener(okModifyListener);
        txtOk.setToolTipText("Text to show in the OK button.\n"+
        		"\n"+
        		"Default : "+FormDialog.defaultButtonOkText+"."
        		);
        
        // CancelButton
        Label lblCancel = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblOk, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblCancel.setLayoutData(fd);
        lblCancel.setText("Cancel button:");
        
        txtCancel = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblCancel, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtCancel.setLayoutData(fd);
        txtCancel.setText(FormDialog.defaultButtonCancelText);
        txtCancel.addModifyListener(cancelModifyListener);
        txtCancel.setToolTipText("Text to show in the Cancel button.\n"+
        		"\n"+
        		"Default : "+FormDialog.defaultButtonCancelText+"."
        		);
        
        // ExportToExcelButton
        Label lblExport = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblCancel, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblExport.setLayoutData(fd);
        lblExport.setText("Export to Excel button:");
        
        txtExport = new StyledText(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblExport, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtExport.setLayoutData(fd);
        txtExport.setText(FormDialog.defaultButtonExportText);
        txtExport.addModifyListener(exportModifyListener);
        txtExport.setToolTipText("Text to show in the Export to Excel button.\n"+
        		"\n"+
        		"Default : "+FormDialog.defaultButtonExportText+"."
        		);
        
        // WhenEmpty
        Label lblWhenEmpty = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblExport, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblWhenEmpty.setLayoutData(fd);
        lblWhenEmpty.setText("When empty");
        
        comboWhenEmpty = new CCombo(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblWhenEmpty, 0, SWT.TOP);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        comboWhenEmpty.setLayoutData(fd);
        comboWhenEmpty.setItems(FormDialog.validWhenEmpty);
        comboWhenEmpty.setText(FormDialog.defaultWhenEmpty);
        comboWhenEmpty.addModifyListener(whenEmptyModifyListener);
        comboWhenEmpty.setToolTipText("Choose the plugin behaviour when a variable is left empty in the form:\n"+
        		"   - ignore: do not change the property value:\n"+
        		"                 - if the property does not already exist, it will not be created,\n"+
        		"                 - if the propety does already exist, its value is left unmodified.\n"+
        		"   - create: empty the property's value if it does already exist, or create a new one with an empty value,\n"+
        		"   - delete: delete the property if it does already exist.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultWhenEmpty+"."
        		);
	}
	
	private ModifyListener nameModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell form = (Shell)getData("control");
        	if ( form != null )
        		form.setText(txtName.getText());
        	
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setText("Tab: "+txtName.getText());
        		treeItem.setData("name", txtName.getText());
        	}
        }
    };
    
	private ModifyListener variableSeparatorModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem formTreeItem = (TreeItem)getData("treeItem");
        	if ( formTreeItem != null ) {
        		formTreeItem.setData("variableSeparator", txtVariableSeparator.getText());
        	}
        }
    };
    
	private ModifyListener sizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	int formWidth = getInt("width");
        	int formHeight = getInt("height");
        	int formSpacing = getInt("spacing");
        	
        	TreeItem formTreeItem = (TreeItem)getData("treeItem");
        	if ( formTreeItem != null ) {
        		formTreeItem.setData("width", formWidth);
        		formTreeItem.setData("height", formHeight);
        		formTreeItem.setData("spacing", formSpacing);
        	}
        	
        	Shell form = (Shell)getData("control");
        	if ( form != null ) {    	
		    	form.setSize(getInt("width"), getInt("height"));
				// we resize the form because we want the width and height to be the client's area width and height
		        Rectangle area = form.getClientArea();
		        formWidth = formWidth * 2 - area.width;
		        formHeight = formHeight * 2 - area.height;
		        form.setSize(formWidth, formHeight);
		        
		        TabFolder tabFolder = (TabFolder)form.getData("tabFolder");
		        if ( tabFolder != null ) {
		            int buttonWidth = (int)formTreeItem.getData("buttonWidth");
		            int buttonHeight = (int)formTreeItem.getData("buttonHeight");
		            
		            area = form.getClientArea();
		            int tabFolderWidth = area.width - formSpacing * 2;
		            int tabFolderHeight = area.height - formSpacing * 3 - buttonHeight;
		            
		            tabFolder.setBounds(formSpacing, formSpacing, tabFolderWidth, tabFolderHeight);
		            
		            Button buttonCancel = (Button)form.getData("buttonCancel");
		            if ( buttonCancel != null ) {
		            	buttonCancel.setBounds(tabFolderWidth+formSpacing-buttonWidth, tabFolderHeight+formSpacing*2, buttonWidth, buttonHeight);
		            }
		            
		            Button buttonOk = (Button)form.getData("buttonOk");
		            if ( buttonOk != null ) {
		            	buttonOk.setBounds(tabFolderWidth-(buttonWidth*2), tabFolderHeight+(formSpacing*2), buttonWidth, buttonHeight);
		            }
		            
		            Button buttonExport = (Button)form.getData("buttonExport");
		            if ( buttonExport != null ) {
		            	buttonExport.setBounds(tabFolderWidth-(buttonWidth*3)-formSpacing, tabFolderHeight+(formSpacing*2), buttonWidth, buttonHeight);
			        }
		        }
        	}
    	}
    };
    
    private SelectionAdapter colorEraser = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
			Color color = lblBackgroundColor.getBackground();
			if ( color != null )
				color.dispose();
			lblBackgroundColor.setBackground(null);
			
    		Shell form = (Shell)getData("control");
    		if ( form != null ) {
    			form.setBackground(null);
    		}

    		TreeItem treeItem = (TreeItem)getData("treeItem");
    		if ( treeItem != null ) {
    			treeItem.setData("Background", "");
    			lblBackgroundColor.setText("");
    		}
    	}
    };
    
    private SelectionAdapter colorChooser = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
    		ColorDialog dlg = new ColorDialog((Shell)getData("shell"));
    		dlg.setRGB(lblBackgroundColor.getBackground().getRGB());
    		dlg.setText("Choose a Color");
    		RGB rgb = dlg.open();
    		if (rgb != null) {
				Color color = lblBackgroundColor.getBackground();
				if ( color != null )
					color.dispose();
				color = new Color(FormGraphicalEditor.display, rgb);
				
    			lblBackgroundColor.setBackground(color);
    			lblBackgroundColor.setForeground( (rgb.red<=128 && rgb.green<=128 && rgb.blue<=128) ? FormGraphicalEditor.whiteColor : FormGraphicalEditor.blackColor);

    			Shell form = (Shell)getData("control");
    			if ( form != null ) {
    				form.setBackground(color);
    			}

    			TreeItem treeItem = (TreeItem)getData("treeItem");
    			if ( treeItem != null ) {
    				treeItem.setData("Background", rgb.red+","+rgb.green+","+rgb.blue);
    				lblBackgroundColor.setText(rgb.red+","+rgb.green+","+rgb.blue);
    			}
    		}
    	}
    };
    
	private ModifyListener okModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell form = (Shell)getData("control");
        	if ( form != null ) {
        		Button okButton = (Button)form.getData("buttonOk");
	        	if ( okButton != null )
	        		okButton.setText(getString("buttonOk"));
        	}
        	
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setData("buttonOk", getString("buttonOk"));
        	}
        }
    };
    
	private ModifyListener cancelModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell form = (Shell)getData("control");
        	if ( form != null ) {
	        	Button cancelButton = (Button)form.getData("buttonCancel");
	        	if ( cancelButton != null )
	        		cancelButton.setText(getString("buttonCancel"));
        	}
        	
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setData("buttonCancel", getString("buttonCancel"));
        	}
        }
    };
    
	private ModifyListener exportModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell form = (Shell)getData("control");
        	if ( form != null ) {
        		Button buttonExport = (Button)form.getData("buttonExport");
        		if ( buttonExport != null )
        			buttonExport.setText(getString("buttonExport"));
        	}
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setData("buttonExport", getString("buttonExport"));
        	}
        }
    };
    
	private ModifyListener refersModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setData("refers", getString("refers"));
        	}
        }
    };
    
	private ModifyListener whenEmptyModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setData("whenEmpty", getString("whenEmpty"));
        	}
        }
    };
    
    VerifyListener numericVerifyListener = new VerifyListener() {
        @Override
    	public void verifyText(VerifyEvent e) {
          String string = e.text;
          char[] chars = new char[string.length()];
          string.getChars(0, chars.length, chars, 0);
          for (int i = 0; i < chars.length; i++) {
            if (!('0' <= chars[i] && chars[i] <= '9')) {
              e.doit = false;
              return;
            }
          }
        }
    };
    
    public void set(String key, String value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":
    			txtName.removeModifyListener(nameModifyListener);
    			txtName.setText(value);
    			txtName.addModifyListener(nameModifyListener);
    			return;
    			
    		case "variableseparator":
    			txtVariableSeparator.removeModifyListener(variableSeparatorModifyListener);
    			txtVariableSeparator.setText(value);
    			txtVariableSeparator.addModifyListener(variableSeparatorModifyListener);
    			return;
    			
    		case "background":
    			lblBackgroundColor.setText(value);
				if ( !FormPlugin.isEmpty(value) ) {
					String[] colorArray = value.split(",");
					lblBackgroundColor.setBackground(new Color(FormGraphicalEditor.display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
				}
				return;
				
    		case "refers":
    			comboRefers.removeModifyListener(refersModifyListener);
    			comboRefers.setText(FormPlugin.isEmpty(value) ? FormDialog.defaultRefers : value);
    			comboRefers.addModifyListener(refersModifyListener);
    			return;
    			
    		case "buttonok":
    			txtOk.removeModifyListener(okModifyListener);
    			txtOk.setText(FormPlugin.isEmpty(value) ? FormDialog.defaultButtonOkText : value);
    			txtOk.addModifyListener(okModifyListener);
    			return;
    			
    		case "buttoncancel":
    			txtCancel.removeModifyListener(cancelModifyListener);
    			txtCancel.setText(FormPlugin.isEmpty(value) ? FormDialog.defaultButtonCancelText : value);
    			txtCancel.addModifyListener(cancelModifyListener);
    			return;
    			
    		case "buttonexport":
    			txtExport.removeModifyListener(exportModifyListener);
    			txtExport.setText(FormPlugin.isEmpty(value) ? FormDialog.defaultButtonExportText : value);
    			txtExport.addModifyListener(exportModifyListener);
    			return;
    			
    		case "whenempty":
    			comboWhenEmpty.removeModifyListener(whenEmptyModifyListener);
    			comboWhenEmpty.setText(FormPlugin.isEmpty(value) ? FormDialog.defaultWhenEmpty : value);
    			comboWhenEmpty.addModifyListener(whenEmptyModifyListener);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public void set(String key, int value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "width":
    			txtWidth.removeModifyListener(sizeModifyListener);
    			txtWidth.setText(String.valueOf(value));
    			txtWidth.addModifyListener(sizeModifyListener);
    			return;
    			
    		case "height":
    			txtHeight.removeModifyListener(sizeModifyListener);
    			txtHeight.setText(String.valueOf(value));
    			txtHeight.addModifyListener(sizeModifyListener);
    			return;
    			
    		case "spacing":
    			txtSpacing.removeModifyListener(sizeModifyListener);
    			txtSpacing.setText(String.valueOf(value));
    			txtSpacing.addModifyListener(sizeModifyListener);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public String getString(String key) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":              return txtName.getText();
    		case "variableseparator": return FormPlugin.isEmpty(txtVariableSeparator.getText()) ? FormDialog.defaultVariableSeparator : txtVariableSeparator.getText();
    		case "background":        return lblBackgroundColor.getBackground().getRed()+","+lblBackgroundColor.getBackground().getGreen()+","+lblBackgroundColor.getBackground().getBlue();
    		case "refers":            return FormPlugin.isEmpty(comboRefers.getText()) ? FormDialog.defaultRefers : comboRefers.getText();
    		case "buttonok":          return FormPlugin.isEmpty(txtOk.getText()) ? FormDialog.defaultButtonOkText : txtOk.getText();
    		case "buttoncancel":      return FormPlugin.isEmpty(txtCancel.getText()) ? FormDialog.defaultButtonCancelText : txtCancel.getText();
    		case "buttonexport":      return FormPlugin.isEmpty(txtExport.getText()) ? FormDialog.defaultButtonExportText : txtExport.getText();
    		case "whenempty":         return FormPlugin.isEmpty(comboWhenEmpty.getText()) ? FormDialog.defaultWhenEmpty : comboWhenEmpty.getText();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public int getInt(String key) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "width":
    			if ( FormPlugin.isEmpty(txtWidth.getText()) )
    				return FormDialog.defaultDialogWidth;
    			if ( Integer.valueOf(txtWidth.getText()) < 50 )
    				return 50;
    			return Integer.valueOf(txtWidth.getText());

    		case "height":
    			if ( FormPlugin.isEmpty(txtHeight.getText()) )
    				return FormDialog.defaultDialogHeight;
    			if ( Integer.valueOf(txtHeight.getText()) < 50 )
    				return 50;
    			return Integer.valueOf(txtHeight.getText());
    			
    		case "spacing":
    			if ( FormPlugin.isEmpty(txtSpacing.getText()) )
    				return FormDialog.defaultDialogSpacing;
    			return Integer.valueOf(txtSpacing.getText());
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
