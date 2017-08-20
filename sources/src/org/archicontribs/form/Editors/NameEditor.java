package org.archicontribs.form.Editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class NameEditor {
	private Label      lblName;
	private StyledText txtName;
	private Composite  parent;
	
	public NameEditor(Composite parent) {
		this.parent = parent;
		
		lblName = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblName.setLayoutData(fd);
        lblName.setText("Name:");
        
        txtName = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblName, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        txtName.setLayoutData(fd);
        txtName.addModifyListener(nameModifyListener);
        txtName.setToolTipText("Name of the control.\n"+
        		"\n"+
        		"Can be any arbitrary text and may include variables."
        		);

	}
	
	private ModifyListener nameModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	String     treeItemTextPrefix = "";
        	Widget     widget = (Widget)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	
        	if ( widget != null ) {
	    		switch ( widget.getClass().getSimpleName() ) {
	    			case "Label":
	    				treeItemTextPrefix = "Label: ";
	    				break;
	    				
	    			case "StyledText":
	    				treeItemTextPrefix = "Text: ";
	    				break;
	    				
	    			case "TabItem":
	    				((TabItem)widget).setText(txtName.getText());
	    				treeItemTextPrefix = "Tab: ";
	    				break;
	    				
	    			case "Shell":
	    				((Shell)widget).setText(txtName.getText());
	    				treeItemTextPrefix = "Form: ";
	    				break;
	    				
	    			default : throw new RuntimeException("Do not know "+widget.getClass().getSimpleName()+" controls");
	    		}
	        	
	    		if ( treeItem != null ) {
		        	treeItem.setText(treeItemTextPrefix+txtName.getText());
		        	treeItem.setData("name", txtName.getText());
	    		}
        	}
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblName.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, 10);
        fd.left = new FormAttachment(0, 10);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblName.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtName;
	}
    
    public void setName(String name) {
		txtName.removeModifyListener(nameModifyListener);
		txtName.setText(name);
		txtName.addModifyListener(nameModifyListener);
    }
    
    public String getName() {
    	return txtName.getText();
    }
}
