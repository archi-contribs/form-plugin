package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class SizeEditor {
    Label      lblPosition;
	Label      lblX;
	StyledText txtX;
	Label      lblY;
	StyledText txtY;
	Label      lblWidth;
	StyledText txtWidth;
	Label      lblHeight;
	StyledText txtHeight;
	Composite  parent;
	
	boolean isTableColumn;
	
	public SizeEditor(Composite parent) {
		this.parent = parent;
		
		this.isTableColumn = parent.getClass().getSimpleName().endsWith("ColumnComposite");
	
        this.lblPosition = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblPosition.setLayoutData(fd);
        this.lblPosition.setText(this.isTableColumn ? "Width" : "Position:");
		
		if ( !this.isTableColumn ) {
				// x
			this.lblX = new Label(parent, SWT.NONE);
			fd = new FormData();
	        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
			this.lblX.setLayoutData(fd);
			this.lblX.setText("X:");
	
			this.txtX = new StyledText(parent, SWT.BORDER);
			fd = new FormData();
			fd.top = new FormAttachment(this.lblX, 0, SWT.CENTER);
			fd.left = new FormAttachment(this.lblX, 0);
			fd.right = new FormAttachment(this.txtX, 40);
			this.txtX.setLayoutData(fd);
			this.txtX.setTextLimit(4);
			this.txtX.setLeftMargin(2);
			this.txtX.addVerifyListener(this.numericVerifyListener);
			this.txtX.addModifyListener(this.sizeModifyListener);
			this.txtX.setToolTipText("Horizontal position, in pixels, starting from the left border.");
	
			// y
			this.lblY = new Label(parent, SWT.NONE);
			fd = new FormData();
	        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(this.txtX, FormDialog.editorBorderMargin);
			this.lblY.setLayoutData(fd);
			this.lblY.setText("Y:");
	
			this.txtY = new StyledText(parent, SWT.BORDER);
			fd = new FormData();
	        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(this.lblY, 0);
	        fd.right = new FormAttachment(this.txtY, 40);
			this.txtY.setLayoutData(fd);
			this.txtY.setTextLimit(4);
			this.txtY.setLeftMargin(2);
			this.txtY.addVerifyListener(this.numericVerifyListener);
			this.txtY.addModifyListener(this.sizeModifyListener);
			this.txtY.setToolTipText("Vertical position, in pixels, starting from the left border.");
		
			// width
			this.lblWidth = new Label(parent, SWT.NONE);
			fd = new FormData();
	        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(this.txtY, FormDialog.editorBorderMargin);
			this.lblWidth.setLayoutData(fd);
			this.lblWidth.setText("Width:");
		}
		
		this.txtWidth = new StyledText(parent, SWT.BORDER);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
        if ( this.isTableColumn )
        	fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        else
        	fd.left = new FormAttachment(this.lblWidth, 0);
        fd.right = new FormAttachment(this.txtWidth, 40);
        this.txtWidth.setLayoutData(fd);
        this.txtWidth.setTextLimit(4);
        this.txtWidth.setLeftMargin(2);
        this.txtWidth.addVerifyListener(this.numericVerifyListener);
        this.txtWidth.addModifyListener(this.sizeModifyListener);
        this.txtWidth.setToolTipText("Width, in pixels.\n"+
        		"\n"+
        		"If set to zero, then the width will be automatically adapted to the control's text."
        		);
        
        if ( !this.isTableColumn ) {
	        // height
	        this.lblHeight = new Label(parent, SWT.NONE);
	        fd = new FormData();
	        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(this.txtWidth, FormDialog.editorBorderMargin);
	        this.lblHeight.setLayoutData(fd);
	        this.lblHeight.setText("Height:");
	        
	        this.txtHeight = new StyledText(parent, SWT.BORDER);
	        fd = new FormData();
	        fd.top = new FormAttachment(this.lblPosition, 0, SWT.CENTER);
	        fd.left = new FormAttachment(this.lblHeight, 0);
	        fd.right = new FormAttachment(this.txtHeight, 40);
	        this.txtHeight.setLayoutData(fd);
	        this.txtHeight.setTextLimit(4);
	        this.txtHeight.setLeftMargin(2);
	        this.txtHeight.addVerifyListener(this.numericVerifyListener);
	        this.txtHeight.addModifyListener(this.sizeModifyListener);
	        this.txtHeight.setToolTipText("Height, in pixels.\n"+
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
        	Widget    widget = (Widget)SizeEditor.this.parent.getData("widget");
        	TreeItem  treeItem = (TreeItem)SizeEditor.this.parent.getData("treeItem");
        	
        	int x=0, y=0, width=0, height=0;
        	
        	if ( !SizeEditor.this.isTableColumn ) {
	        	x = getX();
	        	y = getY();
	        	width = getWidth();
	        	height = getHeight();
        		if ( treeItem != null ) {
        			treeItem.setData("x", x);
        			treeItem.setData("y", y);
        			treeItem.setData("width", width);
        			treeItem.setData("height", height);
        		}
        	} else {
        		width = getWidth();
        		if ( treeItem != null )
        			treeItem.setData("width", width);
        	}

        	
        	if ( widget != null ) {
            	if ( SizeEditor.this.isTableColumn )
            		((TableColumn)widget).setWidth(width);
            	else {
            		if ( width == 0 || height == 0 ) {
            			Point p = ((Control)widget).computeSize(SWT.DEFAULT, SWT.DEFAULT);
            			width = (width == 0) ? p.x : width;
            			height = (height == 0) ? p.y : height;
            		}
            		((Control)widget).setBounds(x, y, width, height);
            		
            		if ( treeItem!=null && treeItem.getData("image")!=null && treeItem.getData("scale")!=null && (Boolean)treeItem.getData("scale") && ((Label)(Control)widget).getImage() != null ) {
            			Image image = new Image(widget.getDisplay(), (String)treeItem.getData("image"));
                		Image scaledImage = new Image(widget.getDisplay(), image.getImageData().scaledTo(width, height));
                		((Label)widget).setImage(scaledImage);
                		image.dispose();
            		}
            	}
		   	}
    	}
    };
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblPosition.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblPosition.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return this.txtWidth;				//this one always exists
	}
	
    public void setX(Integer x) {
    	this.txtX.removeModifyListener(this.sizeModifyListener);
		this.txtX.setText(x==null ? "" : String.valueOf(x));
		this.txtX.addModifyListener(this.sizeModifyListener);
    }
    
    public void setY(Integer y) {
    	this.txtY.removeModifyListener(this.sizeModifyListener);
    	this.txtY.setText(y==null ? "" : String.valueOf(y));
    	this.txtY.addModifyListener(this.sizeModifyListener);
    }
    
    public void setWidth(Integer width) {
		this.txtWidth.removeModifyListener(this.sizeModifyListener);
		this.txtWidth.setText(width==null ? "" : String.valueOf(width));
		this.txtWidth.addModifyListener(this.sizeModifyListener);
    }
    
    public void setHeight(Integer height) {
		this.txtHeight.removeModifyListener(this.sizeModifyListener);
		this.txtHeight.setText(height==null ? "" : String.valueOf(height));
		this.txtHeight.addModifyListener(this.sizeModifyListener);
    }
    
    public int getX() {
    	if ( this.txtX == null || FormPlugin.isEmpty(this.txtX.getText()) )
    		return 0;
    	return Integer.valueOf(this.txtX.getText());
    }
    
    public int getY() {
    	if ( this.txtY == null || FormPlugin.isEmpty(this.txtY.getText()) )
    		return 0;
    	return Integer.valueOf(this.txtY.getText());
    }
    
    public int getWidth() {
    	if ( this.txtWidth == null || FormPlugin.isEmpty(this.txtWidth.getText()) )
    		return 0;
		return Integer.valueOf(this.txtWidth.getText());
    }
    
    public int getHeight() {
    	if ( this.txtHeight == null || FormPlugin.isEmpty(this.txtHeight.getText()) )
    		return 0;
		return Integer.valueOf(this.txtHeight.getText());
    }
}
