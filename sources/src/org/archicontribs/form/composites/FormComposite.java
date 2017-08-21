package org.archicontribs.form.composites;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.FormSizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

public class FormComposite extends Composite {
	private StringEditor            nameEditor;         // name
	private FormSizeEditor          formSizeEditor;     // width, height, spacing						//TODO : rename spacing to margin
	private ColorEditor             colorEditor;        // foreground, background
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
		// name
		nameEditor = new StringEditor(this, "Name");
		nameEditor.setPosition(0);
		nameEditor.setProperty("name");
		nameEditor.mustSetControlText(true);
				
        // width, height, spacing
		formSizeEditor = new FormSizeEditor(this);
		formSizeEditor.setPosition(nameEditor.getControl());
        
        // Background
		colorEditor = new ColorEditor(this, "Color :");
		colorEditor.setPosition(formSizeEditor.getControl());
		
        
        
        // Refers
        Label lblRefers = new Label(this, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(colorEditor.getControl(), 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(35, 0);
        lblRefers.setLayoutData(fd);
        lblRefers.setText("Refers:");
        
        comboRefers = new CCombo(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblRefers, 0, SWT.TOP);
        fd.left = new FormAttachment(0, 10);
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
        fd.left = new FormAttachment(0, 10);
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
        fd.left = new FormAttachment(0, 10);
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
        fd.left = new FormAttachment(0, 10);
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
        fd.left = new FormAttachment(0, 10);
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
    			nameEditor.setString(value);
    			return;
    			
    		case "foreground":
    			colorEditor.setBackround(value);
    			
    		case "background":
    			colorEditor.setBackround(value);
				
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
    			formSizeEditor.setWidth(value);
    			return;
    			
    		case "height":
    			formSizeEditor.setHeight(value);
    			return;
    			
    		case "spacing":
    			formSizeEditor.setSpacing(value);
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public String getString(String key) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":              return nameEditor.getString();
    		case "background":        return colorEditor.getBackground();
    		case "foreground":        return colorEditor.getForeground();
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
    			return formSizeEditor.getWidth();

    		case "height":
    			return formSizeEditor.getHeight();
    			
    		case "spacing":
    			return formSizeEditor.getSpacing();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
