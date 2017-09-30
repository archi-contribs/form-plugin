package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
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
	private Label      lblString;
	private StyledText txtString;
	private Composite  parent;
	private String     property = null;
	private boolean    mustSetTreeItemText = false;
	private boolean    mustSetControlText = false;
	private boolean    mustSetControlTolltip = false;
	
	private Widget     referencedWidget = null;
	
	private boolean    isArray = false;

	public StringEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
   		this.property = property;
   		
		lblString = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblString.setLayoutData(fd);
        lblString.setText(labelText);
        
        txtString = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(lblString, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        txtString.setLayoutData(fd);
        txtString.setLeftMargin(2);
        txtString.addModifyListener(stringModifyListener);
        

    	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
       	if ( treeItem != null && property != null) {
            setText((String)treeItem.getData(property));
        }
	}
	
	public void setTooltipText(String tooltip) {
		txtString.setToolTipText(tooltip);
	}
	
	public void mustSetControlText(boolean set) {
		mustSetControlText = set;
	}
	
	public void mustSetControlTolltip(boolean set) {
		mustSetControlTolltip = set;
	}
	
	public void mustSetTreeItemText(boolean set) {
		mustSetTreeItemText = set;
	}
	
	public void setTextLimit(int limit) {
		txtString.setTextLimit(limit);
	}
	
	public void setWidth(int width) {
		FormData fd = (FormData)txtString.getLayoutData();
		fd.right = new FormAttachment(txtString, width);
		txtString.layout();
	}
	
	public void setWidget(Widget widget) {
		referencedWidget = widget;
	}
	
	private ModifyListener stringModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	Widget widget = referencedWidget;
        	
        	if ( widget == null )
        		widget = (Widget)parent.getData("widget");
        	
        	if ( widget != null ) {
	    		switch ( widget.getClass().getSimpleName() ) {
	    			case "Composite":
	    				if ( mustSetControlText ) ((CTabItem)widget.getData("tabItem")).setText(getText());
	    				break;
	    				
	    			case "Label":
	    				if ( mustSetControlText ) {
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
	    				if ( mustSetControlTolltip ) ((Label)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "StyledText":
	    				if ( mustSetControlText ) ((StyledText)widget).setText(getText());
	    				if ( mustSetControlTolltip ) ((StyledText)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "CCombo":
	    				if ( mustSetControlText ) ((CCombo)widget).setText(getText());
	    				if ( mustSetControlTolltip ) ((CCombo)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "Button":
	    				if ( mustSetControlText ) ((Button)widget).setText(getText());
	    				if ( mustSetControlTolltip ) ((Button)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "Shell":
	    				if ( mustSetControlText ) ((Shell)widget).setText(getText());
	    				if ( mustSetControlTolltip ) ((Shell)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "Table":
	    				if ( mustSetControlTolltip ) ((Table)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "TableColumn":
	    				if ( mustSetControlText ) ((TableColumn)widget).setText(getText());
	    				if ( mustSetControlTolltip ) ((TableColumn)widget).setToolTipText(getText());
	    				break;
	    				
	    			case "TableItem":
	    				if ( mustSetControlText ) {
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
	    							}
	    						}
	    					}
	    				}
	    				break;
	    				
	    			default : throw new RuntimeException("Do not know "+widget.getClass().getSimpleName()+" controls");
	    		}
        	}
        	
        	if ( treeItem != null ) {
        		if ( property != null ) {
        			if ( isArray )
        				treeItem.setData(property, getText().split("\n"));
        			else
        				treeItem.setData(property, getText());
        		}
        		if ( mustSetTreeItemText )
        			treeItem.setText(getText());
        	}
        	
        	((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblString.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblString.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtString;
	}
    
    public void setText(String string) {
    	isArray = false;
    	
		txtString.removeModifyListener(stringModifyListener);
		txtString.setText(string==null ? "" : string);
		txtString.addModifyListener(stringModifyListener);
		
		((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setText(String[] array) {
    	isArray = true;
    	
		txtString.removeModifyListener(stringModifyListener);
		txtString.setText(array==null ? "" : FormPlugin.concat(array,  "", "\n"));
		txtString.addModifyListener(stringModifyListener);
		
		((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public String getText() {
    	return txtString.getText();
    }
}
