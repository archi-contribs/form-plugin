package org.archicontribs.form.graphicalEditorComposites;

import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TreeItem;

public class TabComposite extends Composite {
    private StyledText       txtName             = null;			// name
    private Label            lblBackgroundColor  = null;			// background

	public TabComposite(Composite parent, int style) {
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
        txtName.setToolTipText("Name of the tab.\n\nCan be any arbitrary text and may include variables.");
        txtName.addModifyListener(nameModifyListener);
        
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
	}
	
	private ModifyListener nameModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
        	TreeItem treeItem = (TreeItem)getData("treeItem");
        	if ( treeItem != null ) {
        		treeItem.setText("Tab: "+txtName.getText());
        		treeItem.setData("name", txtName.getText());
        	}
        	
        	TabItem tabItem = (TabItem)getData("control");
        	if ( tabItem != null )
        		tabItem.setText(txtName.getText());
        }
    };
    
    private SelectionAdapter colorEraser = new SelectionAdapter() {
    	public void widgetSelected(SelectionEvent event) {
    		TreeItem treeItem = (TreeItem)getData("treeItem");
    		if ( treeItem != null ) {
    			treeItem.setData("Background", "");
    			lblBackgroundColor.setText("");
    		}
    		
			Color color = lblBackgroundColor.getBackground();
			if ( color != null )
				color.dispose();
			lblBackgroundColor.setBackground(null);
			
    		TabItem control = (TabItem)getData("control");
    		if ( control != null ) {
    			Control composite = control.getControl();
    			composite.setBackground(null);
    		}
    	}
    };
    
    private SelectionAdapter colorChooser = new SelectionAdapter() {
    	public void widgetSelected(SelectionEvent event) {
    		ColorDialog dlg = new ColorDialog((Shell)getData("shell"));
    		dlg.setRGB(lblBackgroundColor.getBackground().getRGB());
    		dlg.setText("Choose a Color");
    		RGB rgb = dlg.open();
    		
    		if (rgb != null) {
    			TreeItem treeItem = (TreeItem)getData("treeItem");
    			if ( treeItem != null ) {
    				treeItem.setData("Background", rgb.red+","+rgb.green+","+rgb.blue);
    				lblBackgroundColor.setText(rgb.red+","+rgb.green+","+rgb.blue);
    			}
    			
				Color color = lblBackgroundColor.getBackground();
				if ( color != null )
					color.dispose();
				color = new Color(FormGraphicalEditor.display, rgb);
				
    			lblBackgroundColor.setBackground(color);
    			lblBackgroundColor.setForeground( (rgb.red<=128 && rgb.green<=128 && rgb.blue<=128) ? FormGraphicalEditor.whiteColor : FormGraphicalEditor.blackColor);

    			TabItem tabItem = (TabItem)getData("control");
    			if ( tabItem != null ) {
    				Control control = tabItem.getControl();
    				control.setBackground(color);
    			}
    		}
    	}
    };
    
    public void set(String key, String value) throws RuntimeException {
    	switch ( key ) {
    		case "name" :
    			txtName.removeModifyListener(nameModifyListener);
    			txtName.setText(value);
    			txtName.addModifyListener(nameModifyListener);
    			return;
    			
    		case "background" :
    			lblBackgroundColor.setText(value);
				if ( !FormPlugin.isEmpty(value) ) {
					String[] colorArray = value.split(",");
					lblBackgroundColor.setBackground(new Color(FormGraphicalEditor.display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
				}
    			return;
    	}
    	throw new RuntimeException("does not know key "+key);
    }
    
    public String getString(String key) throws RuntimeException {
    	switch ( key ) {
    		case "name" :
    			return txtName.getText();
    			
    		case "background" :
    			return lblBackgroundColor.getBackground().getRed()+","+lblBackgroundColor.getBackground().getGreen()+","+lblBackgroundColor.getBackground().getBlue();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
