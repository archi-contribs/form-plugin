package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;

public class CheckEditor {
	Label      lblCheck;
	Button     check;
	Composite  parent;
	String     property = null;
	Boolean    inverse = false;
	
	public CheckEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
		this.property = property;
		
		this.lblCheck = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblCheck.setLayoutData(fd);
        this.lblCheck.setText(labelText);
        
        this.check = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblCheck, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        this.check.setLayoutData(fd);
        this.check.addSelectionListener(this.checkSelectionListener);
	}
	
	public void setTooltipText(String tooltip) {
	    this.check.setToolTipText(tooltip);
	}
	
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}
	
	private SelectionListener checkSelectionListener = new SelectionListener() {
	    @Override
        public void widgetSelected(SelectionEvent e) {
	    	TreeItem  treeItem = (TreeItem)CheckEditor.this.parent.getData("treeItem");
        	
        	if ( treeItem != null && CheckEditor.this.property != null ) {
        		treeItem.setData(CheckEditor.this.property, getChecked());
        	}
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
            
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblCheck.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblCheck.setLayoutData(fd);
	}
	
	public Button getControl() {
		return this.check;
	}
    
    public void setChecked(Boolean checked) {
        this.check.removeSelectionListener(this.checkSelectionListener);
        this.check.setSelection(checked!=null && (this.inverse ? !checked : checked));
        this.check.addSelectionListener(this.checkSelectionListener);
    }
    
    public boolean getChecked() {
    	return this.inverse ? !this.check.getSelection() : this.check.getSelection();
    }
}
