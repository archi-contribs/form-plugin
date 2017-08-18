package org.archicontribs.form.graphicalEditorComposites;

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
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.swt.widgets.TreeItem;

public class FormComposite extends Composite {
    private StyledText txtName              = null;
    private StyledText txtVariableSeparator = null;
    private StyledText txtWidth             = null;
    private StyledText txtHeight            = null;
    private StyledText txtSpacing           = null;
    private Label      lblBackgroundColor   = null;
    private CCombo     comboRefers          = null;
    private StyledText txtOk                = null;
    private StyledText txtCancel            = null;
    private StyledText txtExport            = null;
    private CCombo     comboWhenEmpty       = null;

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
        fd.top = new FormAttachment(0, 10);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtName.setLayoutData(fd);
        txtName.setToolTipText("Name of the form.\n\nCan be any arbitrary text and may include variables.");
        txtName.addModifyListener(nameModifyListener);
        
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
        fd.top = new FormAttachment(lblName, 10);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtVariableSeparator.setLayoutData(fd);
        txtVariableSeparator.setToolTipText("Character used to separate the different fields in a variable (must be a special character, not alphabetic nor a number).\n\nDefault: \":\".");
        txtVariableSeparator.setTextLimit(1);
        txtVariableSeparator.addModifyListener(variableSeparatorModifyListener);
        
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
        fd.top = new FormAttachment(lblVariableSeparator, 10);
        fd.left = new FormAttachment(lblName, 10);
        fd.right = new FormAttachment(100, -10);
        txtWidth.setLayoutData(fd);
        txtWidth.setToolTipText("Width of the form dialog.\n\nDefault: "+FormDialog.defaultDialogWidth+".");
        txtWidth.setTextLimit(4);
        txtWidth.addVerifyListener(numericVerifyListener);
        txtWidth.addModifyListener(widthModifyListener);
        
        // Height
        // Spacing
        
        // Background
        Label lblBackground = new Label(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblName, 10);
        fd.left = new FormAttachment(lblName, 0, SWT.LEFT);
        fd.right = new FormAttachment(lblName, 0, SWT.RIGHT);
        lblBackground.setLayoutData(fd);
        lblBackground.setText("Background color:");
        lblBackground.setToolTipText("Background color of the tab.");
        
        Button btnBackgroundColorEraser = new Button(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.CENTER);
        fd.right = new FormAttachment(txtName, 0, SWT.RIGHT);
        btnBackgroundColorEraser.setLayoutData(fd);
        btnBackgroundColorEraser.setImage(FormGraphicalEditor.binImage);
        btnBackgroundColorEraser.addSelectionListener(colorEraser);
        
        Button btnBackgroundColorChooser = new Button(this, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.CENTER);
        fd.right = new FormAttachment(btnBackgroundColorEraser, -5, SWT.LEFT);
        btnBackgroundColorChooser.setLayoutData(fd);
        btnBackgroundColorChooser.setText(" ... ");
        btnBackgroundColorChooser.addSelectionListener(colorChooser);
        
        lblBackgroundColor = new Label(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.TOP);
        fd.left = new FormAttachment(txtName, 0, SWT.LEFT);
        fd.right = new FormAttachment(btnBackgroundColorChooser, -5);
        lblBackgroundColor.setLayoutData(fd);
        
        // Refers
        // Ok
        // Cancel
        // Export
        // WhenEmpty
	}
	
	private ModifyListener nameModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell form = (Shell)getData("control");
        	if ( form != null )
        		form.setText(txtName.getText());
        	
        	TreeItem tabTreeItem = (TreeItem)getData("treeItem");
        	if ( tabTreeItem != null ) {
        		tabTreeItem.setText("Tab: "+txtName.getText());
        		tabTreeItem.setData("name", txtName.getText());
        	}
        }
    };
    
	private ModifyListener variableSeparatorModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem tabTreeItem = (TreeItem)getData("treeItem");
        	if ( tabTreeItem != null ) {
        		tabTreeItem.setData("variableSeparator", txtVariableSeparator.getText());
        	}
        }
    };
    
	private ModifyListener widthModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell form = (Shell)getData("control");
        	if ( form != null ) {
        		Point point = form.getSize();
        		if ( FormPlugin.isEmpty(txtWidth.getText()) )
        			point.x = FormDialog.defaultDialogWidth;
        		else
        			point.x = Integer.valueOf(txtWidth.getText());
        		
        		form.setSize(point);
        		// we want the width and height to be the client's area width and height
                Rectangle area = form.getClientArea();
                form.setSize(point.x * 2 - area.width, point.y * 2 - area.height);
                
                //TODO : resize also the tab folder
                //TODO : move the ok, cancel and export buttons
                //TODO : implement a min and a max value
        	}
        	
        	TreeItem tabTreeItem = (TreeItem)getData("treeItem");
        	if ( tabTreeItem != null ) {
        		tabTreeItem.setData("width", txtWidth.getText());
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

    		TreeItem tabTreeItem = (TreeItem)getData("treeItem");
    		if ( tabTreeItem != null ) {
    			tabTreeItem.setData("Background", "");
    			lblBackgroundColor.setText("");
    		}
    	}
    };
    
    private SelectionAdapter colorChooser = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
    		ColorDialog dlg = new ColorDialog(null);
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

    			TreeItem tabTreeItem = (TreeItem)getData("treeItem");
    			if ( tabTreeItem != null ) {
    				tabTreeItem.setData("Background", rgb.red+","+rgb.green+","+rgb.blue);
    				lblBackgroundColor.setText(rgb.red+","+rgb.green+","+rgb.blue);
    			}
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
    	switch ( key ) {
    		case "name" :
    			txtName.setText(value);
    			break;
    		case "variableSeparator" :
    			txtVariableSeparator.setText(value);
    			break;
    		case "width" :
    			txtWidth.setText(value);
    			break;
    		case "background" :
    			lblBackgroundColor.setText(value);
				if ( !FormPlugin.isEmpty(value) ) {
					String[] colorArray = value.split(",");
					lblBackgroundColor.setBackground(new Color(FormGraphicalEditor.display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
				}
    			break;
    		default:
    			throw new RuntimeException("does not know key "+key);
    	}
    	
    }
    
    public String get(String key) throws RuntimeException {
    	switch ( key ) {
    		case "name" :                    return txtName.getText();
    		case "VariableSeparator" :       return txtVariableSeparator.getText();
    		case "width" :                   return txtWidth.getText();
    		case "background" :              return lblBackgroundColor.getBackground().getRed()+","+lblBackgroundColor.getBackground().getGreen()+","+lblBackgroundColor.getBackground().getBlue();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
