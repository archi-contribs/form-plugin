package org.archicontribs.form.editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
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
	
	public ComboEditor(Composite parent) {
		this(parent, 1);
	}
	
	public ComboEditor(Composite parent, int nbLines) {
		this.parent = parent;
		
		lblCombo = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblCombo.setLayoutData(fd);
        
        combo = new CCombo(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblCombo, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        combo.setLayoutData(fd);
        combo.addSelectionListener(comboSelectionListener);
	}
	
	public void setTooltipText(String tooltip) {
		combo.setToolTipText(tooltip);
	}
	
	public void setProperty(String property) {
		TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
		
		this.property = property;
		
    	if ( treeItem != null && property != null) {
	        setText((String)treeItem.getData(property));
    	}
    	
    	if ( FormPlugin.isEmpty(lblCombo.getText()) )
    	    setLabel(property.substring(0, 1).toUpperCase()+property.substring(1)+":");
	}
	
	public void setLabel(String labelText) {
	    lblCombo.setText(labelText);
	}
	
	private SelectionListener comboSelectionListener = new SelectionListener() {
	    @Override
        public void widgetSelected(SelectionEvent e) {
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	
        	if ( treeItem != null ) {
		        if ( property != null ) treeItem.setData(property, getText());
        	}
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
            
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblCombo.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
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
        combo.setText(text);
        combo.addSelectionListener(comboSelectionListener);
    }
    
    public String getText() {
    	return combo.getText();
    }
}
