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
	private Label      lblCheck;
	private Button     check;
	private Composite  parent;
	private String     property = null;
	private Boolean    inverse = false;
	
	public CheckEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
		this.property = property;
		
		lblCheck = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblCheck.setLayoutData(fd);
        lblCheck.setText(labelText);
        
        check = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblCheck, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        check.setLayoutData(fd);
        check.addSelectionListener(checkSelectionListener);
	}
	
	public void setTooltipText(String tooltip) {
	    check.setToolTipText(tooltip);
	}
	
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}
	
	private SelectionListener checkSelectionListener = new SelectionListener() {
	    @Override
        public void widgetSelected(SelectionEvent e) {
	    	TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
        	
        	if ( treeItem != null && property != null ) {
        		treeItem.setData(property, getChecked());
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
        lblCheck.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblCheck.setLayoutData(fd);
	}
	
	public Button getControl() {
		return check;
	}
    
    public void setChecked(Boolean checked) {
        check.removeSelectionListener(checkSelectionListener);
        check.setSelection(checked!=null && (inverse ? !checked : checked));
        check.removeSelectionListener(checkSelectionListener);
    }
    
    public boolean getChecked() {
    	return inverse ? !check.getSelection() : check.getSelection();
    }
}
