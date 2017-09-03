package org.archicontribs.form.editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
	
	public StringEditor(Composite parent, String labelText) {
		this(parent, labelText, 1);
	}
	
	public StringEditor(Composite parent, String labelText, int nbLines) {
		this.parent = parent;
		
		lblString = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblString.setLayoutData(fd);
        lblString.setText(labelText);
        
        if ( nbLines <= 1 )
        	txtString = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        else
        	txtString = new StyledText(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(lblString, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        if ( nbLines > 1)
            fd.bottom = new FormAttachment(txtString, (txtString.getLineHeight()+2)*(nbLines+1), SWT.TOP);     // we add the inter-lines height plus the scrollbar height
        txtString.setLayoutData(fd);
        txtString.setLeftMargin(2);
        txtString.addModifyListener(stringModifyListener);
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
	
	public void setProperty(String property) {
		TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
		
		this.property = property;
		
    	if ( treeItem != null && property != null) {
    	    setText((String)treeItem.getData(property));
    	}
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
        		widget = (Widget)parent.getData("control");
        	
        	if ( widget != null ) {
	    		switch ( widget.getClass().getSimpleName() ) {
	    			case "Label":
	    				if ( mustSetControlText ) ((Label)widget).setText(getText());
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
	    				if ( mustSetControlTolltip ) ((Shell)widget).setToolTipText(getText());
	    				break;
	    				
	    			default : throw new RuntimeException("Do not know "+widget.getClass().getSimpleName()+" controls");
	    		}
        	}
        	
        	if ( treeItem != null ) {
        		if ( property != null )
        			treeItem.setData(property, getText());
        		if ( mustSetTreeItemText )
        			treeItem.setText(getText());
        	}
        	
        	((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblString.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblString.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtString;
	}
    
    public void setText(String string) {
		txtString.removeModifyListener(stringModifyListener);
		txtString.setText(string==null ? "" : string);
		txtString.addModifyListener(stringModifyListener);
    }
    
    public String getText() {
    	return txtString.getText();
    }
}
