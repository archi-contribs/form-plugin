package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.archicontribs.form.FormRichTextEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class StringEditor {
	Label      lblString;
	StyledText txtString;
	boolean    mustSetTreeItemText = false;
	boolean    mustSetControlText = false;
	boolean    mustSetControlTolltip = false;
    Composite  parent;
    String     property = null;	
	Widget     referencedWidget = null;
	boolean    isArray = false;

	public StringEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
   		this.property = property;
   		
		this.lblString = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblString.setLayoutData(fd);
        this.lblString.setText(labelText);
        
        this.txtString = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblString, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        this.txtString.setLayoutData(fd);
        this.txtString.setLeftMargin(2);
        this.txtString.addModifyListener(this.stringModifyListener);
        

    	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
       	if ( treeItem != null && property != null) {
            setText((String)treeItem.getData(property));
        }
	}
	
	public void setTooltipText(String tooltip) {
		this.txtString.setToolTipText(tooltip);
	}
	
	public void mustSetControlText(boolean set) {
		this.mustSetControlText = set;
	}
	
	public void mustSetControlTolltip(boolean set) {
		this.mustSetControlTolltip = set;
	}
	
	public void mustSetTreeItemText(boolean set) {
		this.mustSetTreeItemText = set;
	}
	
	public void setTextLimit(int limit) {
		this.txtString.setTextLimit(limit);
	}
	
	public void setWidth(int width) {
		FormData fd = (FormData)this.txtString.getLayoutData();
		fd.right = new FormAttachment(this.txtString, width);
		this.txtString.layout();
	}
	
	public void setWidget(Widget widget) {
		this.referencedWidget = widget;
	}
	
	private ModifyListener stringModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem   treeItem = (TreeItem)StringEditor.this.parent.getData("treeItem");
        	Widget widget = StringEditor.this.referencedWidget;
        	
        	if ( widget == null )
        		widget = (Widget)StringEditor.this.parent.getData("widget");
        	
        	if ( widget != null ) {
	    		switch ( widget.getClass().getSimpleName() ) {
	    			case "Composite":
	    				if ( StringEditor.this.mustSetControlText ) ((CTabItem)widget.getData("tabItem")).setText(getText());
	    				break;
	    				
	    			case "Label":
	    				if ( StringEditor.this.mustSetControlText ) {
	    				    ((Label)widget).setText(getText());
	    				    if ( treeItem.getData("width") == null || (int)treeItem.getData("width") == 0 ) {
	    				        Point p = ((Control)widget).computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    				        int x = treeItem.getData("x")==null ? 0 : (int)treeItem.getData("x");
	    		                int y = treeItem.getData("y")==null ? 0 : (int)treeItem.getData("y");
	    		                int width  = treeItem.getData("width")==null ? 0 : (int)treeItem.getData("width");
	    		                int height = treeItem.getData("height")==null ? 0 : (int)treeItem.getData("height");
	    		                if ( width == 0 ) width = p.x;
	    		                if ( height == 0 ) height = p.y;
	    		                ((Control)widget).setBounds(x, y, width, height);
	    				    }
	    				}
	    				if ( StringEditor.this.mustSetControlTolltip ) ((Label)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "StyledText":
	    				if ( StringEditor.this.mustSetControlText ) ((StyledText)widget).setText(getText());
	    				if ( StringEditor.this.mustSetControlTolltip ) ((StyledText)widget).setToolTipText(getText());
	    				break;
	    				
                    case "RichTextEditor":
                        if ( StringEditor.this.mustSetControlText ) ((FormRichTextEditor)widget).setText(getText());
                        if ( StringEditor.this.mustSetControlTolltip ) ((FormRichTextEditor)widget).setToolTipText(getText());
                        break;
	    				
	    			case "CCombo":
	    				if ( StringEditor.this.mustSetControlText ) ((CCombo)widget).setText(getText());
	    				if ( StringEditor.this.mustSetControlTolltip ) ((CCombo)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "Button":
	    				if ( StringEditor.this.mustSetControlText ) ((Button)widget).setText(getText());
	    				if ( StringEditor.this.mustSetControlTolltip ) ((Button)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "Shell":
	    				if ( StringEditor.this.mustSetControlText ) ((Shell)widget).setText(getText());
	    				if ( StringEditor.this.mustSetControlTolltip ) ((Shell)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "Table":
	    				if ( StringEditor.this.mustSetControlTolltip ) ((Table)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "TableColumn":
	    				if ( StringEditor.this.mustSetControlText ) ((TableColumn)widget).setText(getText());
	    				if ( StringEditor.this.mustSetControlTolltip ) ((TableColumn)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "TableItem":
	    				if ( StringEditor.this.mustSetControlText ) {
	    					TableEditor[] tableEditors = (TableEditor[])widget.getData("editors");
	    					String[] cells = getText().split("\n");
	    					if ( tableEditors != null ) {
	    						int maxIndex = (tableEditors.length < cells.length) ? tableEditors.length : cells.length;
	    						for ( int index=0; index < maxIndex; ++index ) {
	    							switch ( tableEditors[index].getEditor().getClass().getSimpleName() ) {
	    								case "Label": ((Label)tableEditors[index].getEditor()).setText(cells[index]); break;
	    								case "StyledText": ((StyledText)tableEditors[index].getEditor()).setText(cells[index]); break;
	    								case "CCombo": ((CCombo)tableEditors[index].getEditor()).setText(cells[index]); break;
	    								//Button do not show up text
                                        default:
                                            // unknown class
	    							}
	    						}
	    					}
	    				}
	    				break;
	    				
	    			default : throw new RuntimeException("Do not know "+widget.getClass().getSimpleName()+" controls");
	    		}
        	}
        	
        	if ( treeItem != null ) {
        		if ( StringEditor.this.property != null ) {
        			if ( StringEditor.this.isArray )
        				treeItem.setData(StringEditor.this.property, getText().split("\n"));
        			else
        				treeItem.setData(StringEditor.this.property, getText());
        		}
        		if ( StringEditor.this.mustSetTreeItemText )
        			treeItem.setText(getText());
        	}
        	
        	((ScrolledComposite)StringEditor.this.parent.getParent()).setMinSize(StringEditor.this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblString.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblString.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return this.txtString;
	}
    
    public void setText(String string) {
    	this.isArray = false;
    	
		this.txtString.removeModifyListener(this.stringModifyListener);
		this.txtString.setText(string==null ? "" : string);
		this.txtString.addModifyListener(this.stringModifyListener);
		
		((ScrolledComposite)this.parent.getParent()).setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setText(String[] array) {
    	this.isArray = true;
    	
		this.txtString.removeModifyListener(this.stringModifyListener);
		this.txtString.setText(array==null ? "" : FormPlugin.concat(array,  "", "\n"));
		this.txtString.addModifyListener(this.stringModifyListener);
		
		((ScrolledComposite)this.parent.getParent()).setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public String getText() {
    	return this.txtString.getText();
    }
}
