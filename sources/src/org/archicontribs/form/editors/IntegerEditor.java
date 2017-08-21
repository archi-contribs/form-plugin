package org.archicontribs.form.editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class IntegerEditor {
	private Label      lblInteger;
	private StyledText txtInteger;
	private Composite  parent;
	private String     property = null;
	private String     controlKey = "control";
	
	public IntegerEditor(Composite parent, String labelText) {
		this.parent = parent;
		
		lblInteger = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblInteger.setLayoutData(fd);
        lblInteger.setText(labelText);
        
        txtInteger = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblInteger, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        txtInteger.setLayoutData(fd);
        txtInteger.addVerifyListener(numericVerifyListener);
        txtInteger.addModifyListener(stringModifyListener);
	}
	
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
	
	public void setTooltipText(String tooltip) {
		txtInteger.setToolTipText(tooltip);
	}
	
	public void setControlKey(String controlKey) {
	    this.controlKey = controlKey;
	}
	
	public void setProperty(String property) {
		TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
		
		this.property = property;
		
    	if ( treeItem != null && property != null) {
    		setInteger((int)treeItem.getData(property));
    	}
	}
	
	private ModifyListener stringModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Widget     widget = (Widget)parent.getData(controlKey);
        	
	    	if ( widget != null && property != null ) {
		        widget.setData(property, getInteger());
        	}
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblInteger.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblInteger.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtInteger;
	}
    
    public void setInteger(int value) {
		txtInteger.removeModifyListener(stringModifyListener);
		txtInteger.setText(String.valueOf(value));
		txtInteger.addModifyListener(stringModifyListener);
    }
    
    public int getInteger() {
    	if ( FormPlugin.isEmpty(txtInteger.getText()) )
    		return 0;
    	return Integer.valueOf(txtInteger.getText());
    }
}
