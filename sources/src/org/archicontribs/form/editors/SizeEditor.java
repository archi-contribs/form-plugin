package org.archicontribs.form.editors;

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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

public class SizeEditor {
    private Label      lblPosition;
	private Label      lblX;
	private StyledText txtX;
	private Label      lblY;
	private StyledText txtY;
	private Label      lblWidth;
	private StyledText txtWidth;
	private Label      lblHeight;
	private StyledText txtHeight;
	private Composite  parent;
	
	boolean isTableColumn;
	
	public SizeEditor(Composite parent) {
		this.parent = parent;
		
		isTableColumn = parent.getClass().getSimpleName().endsWith("ColumnComposite");
		
        lblPosition = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblPosition.setLayoutData(fd);
        lblPosition.setText(isTableColumn ? "Width" : "Position:");
		
		if ( !isTableColumn ) {
				// x
			lblX = new Label(parent, SWT.NONE);
			fd = new FormData();
	        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
			lblX.setLayoutData(fd);
			lblX.setText("X:");
	
			txtX = new StyledText(parent, SWT.BORDER);
			fd = new FormData();
			fd.top = new FormAttachment(lblX, 0, SWT.CENTER);
			fd.left = new FormAttachment(lblX, 0);
			fd.right = new FormAttachment(txtX, 40);
			txtX.setLayoutData(fd);
			txtX.setTextLimit(4);
			txtX.setLeftMargin(2);
			txtX.addVerifyListener(numericVerifyListener);
			txtX.addModifyListener(sizeModifyListener);
			txtX.setToolTipText("Horizontal position, in pixels, starting from the left border.");
	
			// y
			lblY = new Label(parent, SWT.NONE);
			fd = new FormData();
	        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(txtX, FormGraphicalEditor.editorBorderMargin);
			lblY.setLayoutData(fd);
			lblY.setText("Y:");
	
			txtY = new StyledText(parent, SWT.BORDER);
			fd = new FormData();
	        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(lblY, 0);
	        fd.right = new FormAttachment(txtY, 40);
			txtY.setLayoutData(fd);
			txtY.setTextLimit(4);
			txtY.setLeftMargin(2);
			txtY.addVerifyListener(numericVerifyListener);
			txtY.addModifyListener(sizeModifyListener);
			txtY.setToolTipText("Vertical position, in pixels, starting from the left border.");
		
			// width
			lblWidth = new Label(parent, SWT.NONE);
			fd = new FormData();
	        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(txtY, FormGraphicalEditor.editorBorderMargin);
			lblWidth.setLayoutData(fd);
			lblWidth.setText("Width:");
		}
		
		txtWidth = new StyledText(parent, SWT.BORDER);
		fd = new FormData();
        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
        if ( isTableColumn )
        	fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        else
        	fd.left = new FormAttachment(lblWidth, 0);
        fd.right = new FormAttachment(txtWidth, 40);
        txtWidth.setLayoutData(fd);
        txtWidth.setTextLimit(4);
        txtWidth.setLeftMargin(2);
        txtWidth.addVerifyListener(numericVerifyListener);
        txtWidth.addModifyListener(sizeModifyListener);
        txtWidth.setToolTipText("Width, in pixels.\n"+
        		"\n"+
        		"If set to zero, then the width will be automatically adapted to the control's text."
        		);
        
        if ( !isTableColumn ) {
	        // height
	        lblHeight = new Label(parent, SWT.NONE);
	        fd = new FormData();
	        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(txtWidth, FormGraphicalEditor.editorBorderMargin);
	        lblHeight.setLayoutData(fd);
	        lblHeight.setText("Height:");
	        
	        txtHeight = new StyledText(parent, SWT.BORDER);
	        fd = new FormData();
	        fd.top = new FormAttachment(lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(lblHeight, 0);
	        fd.right = new FormAttachment(txtHeight, 40);
	        txtHeight.setLayoutData(fd);
	        txtHeight.setTextLimit(4);
	        txtHeight.setLeftMargin(2);
	        txtHeight.addVerifyListener(numericVerifyListener);
	        txtHeight.addModifyListener(sizeModifyListener);
	        txtHeight.setToolTipText("Height, in pixels.\n"+
	        		"\n"+
	        		"If set to zero, then the height will be automatically adapted to the control's text."
	        		);
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
	
	private ModifyListener sizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Widget    widget = (Widget)parent.getData("control");
        	
        	int x=0, y=0, width=0, height=0;
        	
        	if ( !isTableColumn ) {
	        	x = getX();
	        	y = getY();
	        	height = getHeight();
        	}
        	width = getWidth();

        	
        	if ( widget != null ) {
            	if ( isTableColumn ) {
            		widget.setData("width", width);
            		((TableColumn)widget).setWidth(width);
            	} else {
            		widget.setData("x", x);
            		widget.setData("y", y);
            		widget.setData("width", width);
            		widget.setData("height", height);
            		
            		if ( width == 0 || height == 0 ) {
            			Point p = ((Control)widget).computeSize(SWT.DEFAULT, SWT.DEFAULT);
            			width = (width == 0) ? p.x : width;
            			height = (height == 0) ? p.y : height;
            		}
            		((Control)widget).setBounds(x, y, width, height);
            	}
		   	}
    	}
    };
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblPosition.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblPosition.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtWidth;				//this one always exists
	}
	
    public void setX(Integer x) {
    	txtX.removeModifyListener(sizeModifyListener);
		txtX.setText(x==null ? "" : String.valueOf(x));
		txtX.addModifyListener(sizeModifyListener);
    }
    
    public void setY(Integer y) {
    	txtY.removeModifyListener(sizeModifyListener);
    	txtY.setText(y==null ? "" : String.valueOf(y));
    	txtY.addModifyListener(sizeModifyListener);
    }
    
    public void setWidth(Integer width) {
		txtWidth.removeModifyListener(sizeModifyListener);
		txtWidth.setText(width==null ? "" : String.valueOf(width));
		txtWidth.addModifyListener(sizeModifyListener);
    }
    
    public void setHeight(Integer height) {
		txtHeight.removeModifyListener(sizeModifyListener);
		txtHeight.setText(height==null ? "" : String.valueOf(height));
		txtHeight.addModifyListener(sizeModifyListener);
    }
    
    public int getX() {
    	if ( txtX == null || FormPlugin.isEmpty(txtX.getText()) )
    		return 0;
    	return Integer.valueOf(txtX.getText());
    }
    
    public int getY() {
    	if ( txtY == null || FormPlugin.isEmpty(txtY.getText()) )
    		return 0;
    	return Integer.valueOf(txtY.getText());
    }
    
    public int getWidth() {
    	if ( txtWidth == null || FormPlugin.isEmpty(txtWidth.getText()) )
    		return 0;
		return Integer.valueOf(txtWidth.getText());
    }
    
    public int getHeight() {
    	if ( txtHeight == null || FormPlugin.isEmpty(txtHeight.getText()) )
    		return 0;
		return Integer.valueOf(txtHeight.getText());
    }
}
