package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class ColorEditor {
	Label      lblColor;
	Label      lblSample;
	Button     btnSelectBackground;
	Button     btnSelectForeground;
	Button     btnResetToDefault;
	Composite  parent;
	
	public ColorEditor(Composite parent, String labelText) {
		this.parent = parent;
		
		this.lblColor = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblColor.setLayoutData(fd);
        this.lblColor.setText(labelText);
        
        this.btnResetToDefault = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblColor, 0, SWT.CENTER);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        this.btnResetToDefault.setLayoutData(fd);
        this.btnResetToDefault.setImage(FormDialog.binImage);
        this.btnResetToDefault.addSelectionListener(this.colorReset);
        this.btnResetToDefault.setToolTipText("Reset to the default color.");
        
        this.btnSelectBackground = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblColor, 0, SWT.CENTER);
        fd.right = new FormAttachment(this.btnResetToDefault, -5);
        this.btnSelectBackground.setLayoutData(fd);
        this.btnSelectBackground.setText("B");
        this.btnSelectBackground.addSelectionListener(this.colorChooser);
        this.btnSelectBackground.setToolTipText("Select the background color.");
        
        this.btnSelectForeground = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblColor, 0, SWT.CENTER);
        fd.right = new FormAttachment(this.btnSelectBackground, -5);
        this.btnSelectForeground.setLayoutData(fd);
        this.btnSelectForeground.setText("F");
        this.btnSelectForeground.addSelectionListener(this.colorChooser);
        this.btnSelectForeground.setToolTipText("Select the foreground color.");
        
        this.lblSample = new Label(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblColor, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.btnSelectForeground, -5);
        this.lblSample.setLayoutData(fd);
        this.lblSample.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
	}
	
    private SelectionAdapter colorChooser = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
        	TreeItem  treeItem = (TreeItem)ColorEditor.this.parent.getData("treeItem");
        	Widget    widget   = (Widget)ColorEditor.this.parent.getData("widget");
        	Color     color;
        	
    		ColorDialog dlg = new ColorDialog((Shell)ColorEditor.this.parent.getData("shell"));
    		if ( event.getSource() == ColorEditor.this.btnSelectForeground ) {
    			dlg.setRGB(ColorEditor.this.lblSample.getForeground().getRGB());
    			dlg.setText("Choose the foreground color");
    		} else {
    			dlg.setRGB(ColorEditor.this.lblSample.getBackground().getRGB());
    			dlg.setText("Choose the background color");
    		}
    		
    		RGB rgb = dlg.open();
    		if (rgb != null) {
   				color = (event.getSource() == ColorEditor.this.btnSelectForeground) ? ColorEditor.this.lblSample.getForeground() : ColorEditor.this.lblSample.getBackground();
				if ( color != null )
					color.dispose();
				color = new Color(FormDialog.display, rgb);
				
				if ( event.getSource() == ColorEditor.this.btnSelectForeground ) {
					setForeground(color);
					widget.setData("foreground", getForeground());
					if ( treeItem != null ) {
	    				treeItem.setData("foreground", getForeground());
	    				
	    				// we update all the embeded controls that do not have a font specified
	    				for( TreeItem childTreeItem: treeItem.getItems() )
	    					setColor(childTreeItem, color, SWT.FOREGROUND);
					}
				} else {
					setBackground(color);
					widget.setData("background", getBackground());
					if ( treeItem != null ) {
						treeItem.setData("background", getBackground());
	    				
	    				// we update all the embeded controls that do not have a font specified
	    				for( TreeItem childTreeItem: treeItem.getItems() )
	    					setColor(childTreeItem, color, SWT.FOREGROUND);
					}
				}
    		}
    	}
    };
    
    void setColor(TreeItem treeItem, Color color, int colorType) {
    	if ( treeItem != null ) {
    		Control control = (Control)treeItem.getData("widget");
    		if ( control != null ) {
    			if ( colorType == SWT.FOREGROUND ) {
    				control.setForeground(color);
    				control.setData("foreground", getForeground());
    			} else {
    				control.setBackground(color);
    				control.setData("background", getBackground());
    			}
    		}
    		
			for( TreeItem childTreeItem: treeItem.getItems() ) {
				setColor(childTreeItem, color, colorType);
			}
    	}
    }
    
    private SelectionAdapter colorReset = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
        	Widget     widget = (Widget)ColorEditor.this.parent.getData("widget");
        	Color color;
        	
			color = ColorEditor.this.lblSample.getForeground();
			if ( color != null )
				color.dispose();
			
			color = ColorEditor.this.lblSample.getBackground();
			if ( color != null )
				color.dispose();
			
			color = null;
			
			
			ColorEditor.this.lblSample.setBackground(null);
			ColorEditor.this.lblSample.setForeground(null);
			
			if ( widget != null ) {
				if ( widget instanceof Shell ) {
					((Shell)widget).setBackground(((Shell)widget).getParent().getBackground());
					((Shell)widget).setForeground(((Shell)widget).getParent().getForeground());
				} else if ( ! (widget instanceof TableColumn) ) {
					((Control)widget).setBackground(((Control)widget).getParent().getBackground());
					((Control)widget).setForeground(((Control)widget).getParent().getForeground());
				}

				widget.setData("background", "");
				widget.setData("foreground", "");
			}
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblColor.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblColor.setLayoutData(fd);
	}
	
	public Label getControl() {
		return this.lblSample;
	}
    
	public void setBackground(Color color) {
    	if ( color != null )
    		this.lblSample.setBackground(color);
    	else
    		this.lblSample.setBackground(this.lblSample.getParent().getBackground());
    	
    	Widget widget = (Widget)this.parent.getData("widget");
    	if ( widget != null ) {
    		if ( widget instanceof Shell ) {
    	    	if ( color != null )
    	    		((Shell)widget).setBackground(color);
    	    	else
    	    		((Shell)widget).setBackground(((Shell)widget).getParent().getBackground());
	    	} else if ( widget instanceof TableColumn ) {
	        	widget.setData("background color", color);
	        	// we change the color of all elements in this column
	        	Table table = ((TableColumn)widget).getParent();
	        	int columnIndex = table.indexOf((TableColumn)widget);
	        	for ( TableItem tableItem: table.getItems() ) {
	        		TableEditor[] editors = (TableEditor[])tableItem.getData("editors");
	        		if ( editors[columnIndex] != null ) {
	        			if ( color == null )
	        				editors[columnIndex].getEditor().setBackground(table.getBackground());
	        			else
	        				editors[columnIndex].getEditor().setBackground(color);
	        		}
	        	}
	    	} else {
	    		if ( color == null )
	    			((Control)widget).setBackground(((Control)widget).getParent().getBackground());
				else
					((Control)widget).setBackground(color);
	    	}
    	}
    }
    
    public void setBackground(String rgbString) {
    	Color color = null;
    	
    	if ( !FormPlugin.isEmpty(rgbString) ) {
	    	String rgb[] = rgbString.split(",");
	    	if ( rgb.length == 3 ) {
	    		color = this.lblSample.getBackground();
				if ( color != null )
					color.dispose();
				
				color = new Color(FormDialog.display, Integer.valueOf(rgb[0].trim()),Integer.valueOf(rgb[1].trim()),Integer.valueOf(rgb[2].trim()));
	    	}
    	}
    	
    	setBackground(color);
    }
    
    public void setForeground(Color color) {
    	if ( color != null )
    		this.lblSample.setForeground(color);
    	else
    		this.lblSample.setForeground(this.lblSample.getParent().getForeground());
    	
    	Widget widget = (Widget)this.parent.getData("widget");
    	if ( widget != null ) {
    		if ( widget instanceof Shell ) {
    	    	if ( color != null )
    	    		((Shell)widget).setForeground(color);
    	    	else
    	    		((Shell)widget).setForeground(((Shell)widget).getParent().getForeground());
	    	} else if ( widget instanceof TableColumn ) {
	        	widget.setData("foreground color", color);
	        	// we change the color of all elements in this column
	        	Table table = ((TableColumn)widget).getParent();
	        	int columnIndex = table.indexOf((TableColumn)widget);
	        	for ( TableItem tableItem: table.getItems() ) {
	        		TableEditor[] editors = (TableEditor[])tableItem.getData("editors");
	        		if ( editors[columnIndex] != null ) {
	        			if ( color == null )
	        				editors[columnIndex].getEditor().setForeground(table.getForeground());
	        			else
	        				editors[columnIndex].getEditor().setForeground(color);
	        		}
	        	}
	    	} else {
	    		if ( color == null )
	    			((Control)widget).setForeground(((Control)widget).getParent().getForeground());
				else
					((Control)widget).setForeground(color);
	    	}
    	}
    }
    
    public void setForeground(String rgbString) {
    	Color color = null;
    	
    	if ( !FormPlugin.isEmpty(rgbString) ) {
	    	String rgb[] = rgbString.split(",");
	    	if ( rgb.length == 3 ) {
	    		color = this.lblSample.getForeground();
				if ( color != null )
					color.dispose();
				
				color = new Color(FormDialog.display, Integer.valueOf(rgb[0].trim()),Integer.valueOf(rgb[1].trim()),Integer.valueOf(rgb[2].trim()));
	    	}
    	}
    	
    	setForeground(color);
    }
    
    public String getBackground() {
    	Color color = this.lblSample.getBackground();
    	return color.getRed()+","+color.getGreen()+","+color.getBlue();
    }
    
    public String getForeground() {
    	Color color = this.lblSample.getForeground();
    	return color.getRed()+","+color.getGreen()+","+color.getBlue();
    }
}
