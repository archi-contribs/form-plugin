package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;

public class ComboEditor {
	private Label      lblCombo;
	private CCombo     combo;
	private Composite  parent;
	private String     property = null;
	
	public ComboEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
		this.property = property;
		
		lblCombo = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblCombo.setLayoutData(fd);
        lblCombo.setText(labelText);
        
        combo = new CCombo(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblCombo, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        combo.setLayoutData(fd);
        combo.addSelectionListener(comboSelectionListener);
        
    	Control control = (Control)parent.getData("widget");
    	if ( control != null && property != null) {
    	    setText((String)control.getData(property));
        }
	}
	
	public void setTooltipText(String tooltip) {
		combo.setToolTipText(tooltip);
	}
	
	private SelectionListener comboSelectionListener = new SelectionListener() {
	    @Override
        public void widgetSelected(SelectionEvent e) {
	    	TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
        	
        	if ( treeItem != null && property != null ) {
        		treeItem.setData(property, getText());
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
        lblCombo.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblCombo.setLayoutData(fd);
	}
	
	public CCombo getControl() {
		return combo;
	}
    
    public void setItems(String[] items) {
		combo.setItems(items);
    }
    
    public void setText(String text) {
        combo.removeSelectionListener(comboSelectionListener);
        combo.setText(text==null ? "" : text);
        combo.addSelectionListener(comboSelectionListener);
    }
    
    public String getText() {
    	return combo.getText();
    }
}
