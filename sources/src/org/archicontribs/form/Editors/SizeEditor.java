package org.archicontribs.form.Editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;

public class SizeEditor {
	private Label      lblX;
	private StyledText txtX;
	private Label      lblY;
	private StyledText txtY;
	private Label      lblWidth;
	private StyledText txtWidth;
	private Label      lblHeight;
	private StyledText txtHeight;
	private Composite  parent;
	
	public SizeEditor(Composite parent) {
		this.parent = parent;
		
		// x
		lblX = new Label(parent, SWT.NONE);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
		fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
		fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
		lblX.setLayoutData(fd);
		lblX.setText("X:");

		txtX = new StyledText(parent, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(lblX, 0, SWT.TOP);
		fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
		fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 40);
		txtX.setLayoutData(fd);
		txtX.setTextLimit(4);
		txtX.addVerifyListener(numericVerifyListener);
		txtX.addModifyListener(sizeModifyListener);
		txtX.setToolTipText("Horizontal position, in pixels, starting from the left border.");

		// y
		lblY = new Label(parent, SWT.NONE);
		fd = new FormData();
		fd.top = new FormAttachment(lblX, FormGraphicalEditor.editorVerticalMargin);
		fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
		fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
		lblY.setLayoutData(fd);
		lblY.setText("Y:");

		txtY = new StyledText(parent, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(lblY, 0, SWT.TOP);
		fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
		fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 40);
		txtY.setLayoutData(fd);
		txtY.setTextLimit(4);
		txtY.addVerifyListener(numericVerifyListener);
		txtY.addModifyListener(sizeModifyListener);
		txtY.setToolTipText("Vertical position, in pixels, starting from the left border.");

		// width
		lblWidth = new Label(parent, SWT.NONE);
		fd = new FormData();
		fd.top = new FormAttachment(lblY, FormGraphicalEditor.editorVerticalMargin);
		fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
		fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
		lblWidth.setLayoutData(fd);
		lblWidth.setText("Width:");

		txtWidth = new StyledText(parent, SWT.BORDER);
		fd = new FormData();
		fd.top = new FormAttachment(lblWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 40);
        txtWidth.setLayoutData(fd);
        txtWidth.setTextLimit(4);
        txtWidth.addVerifyListener(numericVerifyListener);
        txtWidth.addModifyListener(sizeModifyListener);
        txtWidth.setToolTipText("Width, in pixels.\n"+
        		"\n"+
        		"If set to zero, then the width will be automatically adapted to the control's text."
        		);
        
        // height
        lblHeight = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblWidth, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblHeight.setLayoutData(fd);
        lblHeight.setText("Height:");
        
        txtHeight = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 40);
        txtHeight.setLayoutData(fd);
        txtHeight.setTextLimit(4);
        txtHeight.addVerifyListener(numericVerifyListener);
        txtHeight.addModifyListener(sizeModifyListener);
        txtHeight.setToolTipText("Height, in pixels.\n"+
        		"\n"+
        		"If set to zero, then the height will be automatically adapted to the control's text."
        		);
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
	
	private ModifyListener sizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Control    control = (Control)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	
        	int x = getX();
        	int y = getY();
        	int width = getWidth();
        	int height = getHeight();
        	
        	if ( treeItem != null ) {
        		treeItem.setData("x", x);
        		treeItem.setData("y", y);
        		treeItem.setData("width", width);
        		treeItem.setData("height", height);
        	}
        	
        	if ( control != null ) { 
        		if ( width == 0 || height == 0 ) {
        			Point p = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        			width = (width == 0) ? p.x : width;
        			height = (height == 0) ? p.y : height;
        		}
        		control.setBounds(x, y, width, height);
		   	}
    	}
    };
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblX.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblX.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtHeight;
	}
	
    public void setX(int x) {
    	txtX.removeModifyListener(sizeModifyListener);
		txtX.setText(String.valueOf(x));
		txtX.addModifyListener(sizeModifyListener);
    }
    
    public void setY(int y) {
    	txtY.removeModifyListener(sizeModifyListener);
    	txtY.setText(String.valueOf(y));
    	txtY.addModifyListener(sizeModifyListener);
    }
    
    public void setWidth(int width) {
		txtWidth.removeModifyListener(sizeModifyListener);
		txtWidth.setText(String.valueOf(width));
		txtWidth.addModifyListener(sizeModifyListener);
    }
    
    public void setHeight(int height) {
		txtHeight.removeModifyListener(sizeModifyListener);
		txtHeight.setText(String.valueOf(height));
		txtHeight.addModifyListener(sizeModifyListener);
    }
    
    public int getX() {
    	if ( FormPlugin.isEmpty(txtX.getText()) )
    		return 0;
    	return Integer.valueOf(txtX.getText());
    }
    
    public int getY() {
    	if ( FormPlugin.isEmpty(txtY.getText()) )
    		return 0;
    	return Integer.valueOf(txtY.getText());
    }
    
    public int getWidth() {
    	if ( FormPlugin.isEmpty(txtWidth.getText()) )
    		return 0;
		return Integer.valueOf(txtWidth.getText());
    }
    
    public int getHeight() {
    	if ( FormPlugin.isEmpty(txtHeight.getText()) )
    		return 0;
		return Integer.valueOf(txtHeight.getText());
    }
}
