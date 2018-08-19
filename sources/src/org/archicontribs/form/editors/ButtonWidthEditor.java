package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;

public class ButtonWidthEditor {
	Label      lblButtonWidth;
	StyledText txtButtonWidth;
    Composite  parent;
    String     widthProperty = null;
    String     controlProperty = null;
	
	public ButtonWidthEditor(Composite parent, String controlProperty, String widthProperty, String labelText) {
		this.parent = parent;
		this.widthProperty = widthProperty;
		this.controlProperty = controlProperty;
		
		this.lblButtonWidth = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblButtonWidth.setLayoutData(fd);
        this.lblButtonWidth.setText(labelText);
        
        this.txtButtonWidth = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblButtonWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        this.txtButtonWidth.setLayoutData(fd);
        this.txtButtonWidth.setLeftMargin(2);
        this.txtButtonWidth.addVerifyListener(this.numericVerifyListener);
        this.txtButtonWidth.addModifyListener(this.buttonWidthModifyListener);
        
		TreeItem treeItem = (TreeItem)parent.getData("treeItem");
    	if ( treeItem != null && widthProperty != null) {
    		setButtonWidth((int)treeItem.getData(widthProperty));
    	}
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
		this.txtButtonWidth.setToolTipText(tooltip);
	}
	
	private ModifyListener stringModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem   treeItem = (TreeItem)ButtonWidthEditor.this.parent.getData("treeItem");
        	
        	if ( treeItem != null && ButtonWidthEditor.this.widthProperty != null ) {
        		treeItem.setData(ButtonWidthEditor.this.widthProperty, getButtonWidth());
        	}
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblButtonWidth.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblButtonWidth.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return this.txtButtonWidth;
	}
    
    public void setButtonWidth(Integer value) {
		this.txtButtonWidth.removeModifyListener(this.stringModifyListener);
		this.txtButtonWidth.setText(value==null ? "" : String.valueOf(value));
		this.txtButtonWidth.addModifyListener(this.stringModifyListener);
    }
    
    public int getButtonWidth() {
    	if ( FormPlugin.isEmpty(this.txtButtonWidth.getText()) )
    		return FormDialog.defaultButtonWidth;
    	return Integer.valueOf(this.txtButtonWidth.getText());
    }
    
    private ModifyListener buttonWidthModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
    		TreeItem treeItem = (TreeItem)ButtonWidthEditor.this.parent.getData("treeItem");
        	if ( treeItem != null && ButtonWidthEditor.this.controlProperty != null) {
        		Control referencedControl = (Control)treeItem.getData(ButtonWidthEditor.this.controlProperty);
        		if ( referencedControl != null ) {
	        		Rectangle bounds = referencedControl.getBounds();
	        		bounds.width = getButtonWidth();
	        		referencedControl.setBounds(bounds);
	        		referencedControl.setData(ButtonWidthEditor.this.widthProperty, getButtonWidth());
        		}
        	}
    	}
    };
}
