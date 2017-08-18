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
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TreeItem;

public class TabComposite extends Composite {
    private StyledText       txtName          = null;
    private Label            backgroundColor  = null;

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
        
        backgroundColor = new Label(this, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblBackground, 0, SWT.TOP);
        fd.left = new FormAttachment(txtName, 0, SWT.LEFT);
        fd.right = new FormAttachment(btnBackgroundColorChooser, -5);
        backgroundColor.setLayoutData(fd);
	}
	
	private ModifyListener nameModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
        	TabItem tabItem = (TabItem)getData("control");
        	if ( tabItem != null )
        		tabItem.setText(txtName.getText());
        	
        	TreeItem tabTreeItem = (TreeItem)getData("treeItem");
        	if ( tabTreeItem != null ) {
        		tabTreeItem.setText("Tab: "+txtName.getText());
        		tabTreeItem.setData("name", txtName.getText());
        	}
        }
    };
    
    private SelectionAdapter colorEraser = new SelectionAdapter() {
    	public void widgetSelected(SelectionEvent event) {
			Color color = backgroundColor.getBackground();
			if ( color != null )
				color.dispose();
			backgroundColor.setBackground(null);
			
    		TabItem tabItem = (TabItem)getData("control");
    		if ( tabItem != null ) {
    			Control control = tabItem.getControl();
    			control.setBackground(null);
    		}

    		TreeItem tabTreeItem = (TreeItem)getData("treeItem");
    		if ( tabTreeItem != null ) {
    			tabTreeItem.setData("Background", "");
    			backgroundColor.setText("");
    		}
    	}
    };
    
    private SelectionAdapter colorChooser = new SelectionAdapter() {
    	public void widgetSelected(SelectionEvent event) {
    		ColorDialog dlg = new ColorDialog(null);
    		dlg.setRGB(backgroundColor.getBackground().getRGB());
    		dlg.setText("Choose a Color");
    		RGB rgb = dlg.open();
    		if (rgb != null) {
				Color color = backgroundColor.getBackground();
				if ( color != null )
					color.dispose();
				color = new Color(FormGraphicalEditor.display, rgb);
				
    			backgroundColor.setBackground(color);
    			backgroundColor.setForeground( (rgb.red<=128 && rgb.green<=128 && rgb.blue<=128) ? FormGraphicalEditor.whiteColor : FormGraphicalEditor.blackColor);

    			TabItem tabItem = (TabItem)getData("control");
    			if ( tabItem != null ) {
    				Control control = tabItem.getControl();
    				control.setBackground(color);
    			}

    			TreeItem tabTreeItem = (TreeItem)getData("treeItem");
    			if ( tabTreeItem != null ) {
    				tabTreeItem.setData("Background", rgb.red+","+rgb.green+","+rgb.blue);
    				backgroundColor.setText(rgb.red+","+rgb.green+","+rgb.blue);
    			}
    		}
    	}
    };
    
    public void set(String key, String value) throws RuntimeException {
    	switch ( key ) {
    		case "name" :
    			txtName.setText(value);
    			break;
    		case "background" :
    			backgroundColor.setText(value);
				if ( !FormPlugin.isEmpty(value) ) {
					String[] colorArray = value.split(",");
					backgroundColor.setBackground(new Color(FormGraphicalEditor.display, Integer.parseInt(colorArray[0].trim()), Integer.parseInt(colorArray[1].trim()), Integer.parseInt(colorArray[2].trim())));
				}
    			break;
    		default:
    			throw new RuntimeException("does not know key "+key);
    	}
    	
    }
    
    public String get(String key) throws RuntimeException {
    	switch ( key ) {
    		case "name" :       return txtName.getText();
    		case "background" : return backgroundColor.getBackground().getRed()+","+backgroundColor.getBackground().getGreen()+","+backgroundColor.getBackground().getBlue();
    	}
    	throw new RuntimeException("does not know key "+key);
    }
}
