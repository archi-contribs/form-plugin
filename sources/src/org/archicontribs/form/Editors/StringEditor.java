package org.archicontribs.form.Editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class StringEditor {
	private Label      lblString;
	private StyledText txtString;
	private Composite  parent;
	private String     property = null;
	private boolean    mustSetTreeItemText = false;
	private String     treeItemTextPrefix = "";
	private boolean    mustSetControlText = false;
	private boolean    mustSetControlTolltip = false;
	
	public StringEditor(Composite parent) {
		this(parent, 1);
	}
	
	public StringEditor(Composite parent, int nbLines) {
		this.parent = parent;
		
		lblString = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblString.setLayoutData(fd);
        lblString.setText("String:");
        
        if ( nbLines <= 0 )
        	nbLines = 1;
        if ( nbLines == 1 )
        	txtString = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        else
        	txtString = new StyledText(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(lblString, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        fd.bottom = new FormAttachment(txtString, txtString.getLineHeight()*nbLines, SWT.TOP);
        txtString.setLayoutData(fd);
        txtString.addModifyListener(stringModifyListener);
	}
	
	public void setTooltipText(String tooltip) {
		txtString.setToolTipText(tooltip);
	}
	
	public void mustSetControlText(boolean set) {
		mustSetControlText = set;
	}
	
	public void mustSetControlTolltip(boolean set) {
		mustSetControlTolltip = set;
	}
	
	public void mustSetTreeItemText(boolean set) {
		mustSetTreeItemText = set;
	}
	
	public void treeItemTextPrefix(String prefix) {
		if ( prefix == null )
			prefix = "";
		
		treeItemTextPrefix = prefix;
	}
	
	public void setProperty(String property) {
		TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
		
		this.property = property;
		lblString.setText(property.substring(0, 1).toUpperCase()+property.substring(1)+":");
		
    	if ( treeItem != null && property != null) {
	        setString((String)treeItem.getData(property));
    	}
	}
	
	private ModifyListener stringModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Widget     widget = (Widget)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	
        	if ( widget != null ) {
	    		switch ( widget.getClass().getSimpleName() ) {
	    			case "Label":
	    				if ( mustSetControlText ) ((Label)widget).setText(getString());
	    				if ( mustSetControlTolltip ) ((Label)widget).setToolTipText(getString());
	    				break;
	    				
	    			case "StyledText":
	    				if ( mustSetControlText ) ((StyledText)widget).setText(getString());
	    				if ( mustSetControlTolltip ) ((StyledText)widget).setToolTipText(getString());
	    				break;
	    				
	    			case "CCombo":
	    				if ( mustSetControlText ) ((CCombo)widget).setText(getString());
	    				if ( mustSetControlTolltip ) ((CCombo)widget).setToolTipText(getString());
	    				break;
	    				
	    			case "Button":
	    				if ( mustSetControlText ) ((Button)widget).setText(getString());
	    				if ( mustSetControlTolltip ) ((Button)widget).setToolTipText(getString());
	    				break;
	    				
	    			case "Shell":
	    				if ( mustSetControlText ) ((Shell)widget).setText(getString());
	    				if ( mustSetControlTolltip ) ((Shell)widget).setToolTipText(getString());
	    				break;
	    				
	    			case "Table":
	    				if ( mustSetControlTolltip ) ((Shell)widget).setToolTipText(getString());
	    				break;
	    				
	    			default : throw new RuntimeException("Do not know "+widget.getClass().getSimpleName()+" controls");
	    		}
        	}
        	
	    	if ( treeItem != null ) {
		        if ( property != null ) treeItem.setData(property, getString());
		        if ( mustSetTreeItemText ) treeItem.setText(treeItemTextPrefix+getString());
        	}
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblString.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblString.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtString;
	}
    
    public void setString(String string) {
		txtString.removeModifyListener(stringModifyListener);
		txtString.setText(string);
		txtString.addModifyListener(stringModifyListener);
    }
    
    public String getString() {
    	return txtString.getText();
    }
}
