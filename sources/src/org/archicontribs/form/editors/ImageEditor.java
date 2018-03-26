package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class ImageEditor {
	Label      lblImage;
	Label      lblContent;
	StyledText txtContent;
	Label      lblScale;
	Button     btnScale;
	Composite  parent;
    StyledText txtImage;
    String     property = null;
	Widget     referencedWidget = null;

	public ImageEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
   		this.property = property;
   		
		this.lblImage = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblImage.setLayoutData(fd);
        this.lblImage.setText(labelText);
        
        this.txtImage = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblImage, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        this.txtImage.setLayoutData(fd);
        this.txtImage.setLeftMargin(2);
        this.txtImage.addModifyListener(this.stringModifyListener);
        
		this.lblContent = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblImage, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblContent.setLayoutData(fd);
        this.lblContent.setText("Content:");
        
        this.txtContent = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblImage, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        this.txtContent.setLayoutData(fd);
        this.txtContent.setLeftMargin(2);
        this.txtContent.addModifyListener(this.stringModifyListener);
        
		this.lblScale = new Label(parent, SWT.NONE);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblContent, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        this.lblScale.setLayoutData(fd);
        this.lblScale.setText("Scale:");
        
        this.btnScale = new Button(parent, SWT.CHECK);
        fd = new FormData();
    	fd.top = new FormAttachment(this.lblContent, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        this.btnScale.setLayoutData(fd);
        this.btnScale.addSelectionListener(this.scaleSelectionListener);
        
        //TODO: add a browse icon
        

    	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
       	if ( treeItem != null && property != null) {
            setImage((String)treeItem.getData(property));
        }
	}
	
	public void setTooltipText(String tooltip) {
		this.txtImage.setToolTipText(tooltip);
		this.txtContent.setToolTipText(tooltip);
	}
	
	public void setWidth(int width) {
		FormData fd = (FormData)this.txtImage.getLayoutData();
		fd.right = new FormAttachment(this.txtImage, width);
		this.txtImage.layout();
	}
	
	public void setWidget(Widget widget) {
		this.referencedWidget = widget;
	}
	
	private ModifyListener stringModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	TreeItem   treeItem = (TreeItem)ImageEditor.this.parent.getData("treeItem");
        	Widget widget = ImageEditor.this.referencedWidget;
        	
        	if ( widget == null )
        		widget = (Widget)ImageEditor.this.parent.getData("widget");
        	
        	if ( widget != null ) {
        		// widget is Label
        		((Label)widget).setText("");
        				
        		// if the image already exist, we dispose it
        		Image image = ((Label)widget).getImage();
        		if ( image != null )
        			image.dispose();
        		
        		if ( !FormPlugin.isEmpty(getContent()) ) {
            		image = FormPlugin.stringToImage(getContent());
            	} else if ( !FormPlugin.isEmpty(getText()) ) {
                	image = new Image(Display.getCurrent(), getText());
            	}
            	
                if( image == null )
                	((Label)widget).setText(!FormPlugin.isEmpty(getContent()) ? getContent() : getText());
                else {
    	            try {
    	        		int width = ((Label)widget).getBounds().width > 0 ? ((Label)widget).getBounds().width : image.getBounds().width;
    	        		int height = ((Label)widget).getBounds().height > 0 ? ((Label)widget).getBounds().height : image.getBounds().height;
                    	if ( ImageEditor.this.btnScale.getSelection() ) {
                    		Image scaledImage = new Image(Display.getCurrent(), image.getImageData().scaledTo(width, height));
                    		((Label)widget).setImage(scaledImage);
                    		image.dispose();
                    	} else {
                    		((Label)widget).setImage(image);
                    	}
    	            } catch (@SuppressWarnings("unused") Exception ign) {
    	                // nothing to do
    	            }
            	}
        	}
        	
        	if ( treeItem != null && ImageEditor.this.property != null ) {
        		treeItem.setData(ImageEditor.this.property, getText());
        		treeItem.setData("content", getContent());
        	}
        	
        	((ScrolledComposite)ImageEditor.this.parent.getParent()).setMinSize(ImageEditor.this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    };
    
	private SelectionListener scaleSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)ImageEditor.this.parent.getData("treeItem");
	    	
	    	if ( treeItem != null ) {
	    	    treeItem.setData("scale", ImageEditor.this.btnScale.getSelection());
	    	}
	    	
	    	ImageEditor.this.txtImage.notifyListeners(SWT.Modify, new Event());		// calls the stringModifyListener() method
	    }
	
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblImage.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblImage.setLayoutData(fd);
	}
	
	public Button getControl() {
		return this.btnScale;
	}
    
    public void setImage(String string) {
		this.txtImage.removeModifyListener(this.stringModifyListener);
		this.txtImage.setText(string==null ? "" : string);
		this.txtImage.addModifyListener(this.stringModifyListener);
		
		((ScrolledComposite)this.parent.getParent()).setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setContent(String string) {
		this.txtContent.removeModifyListener(this.stringModifyListener);
		this.txtContent.setText(string==null ? "" : string);
		this.txtContent.addModifyListener(this.stringModifyListener);
		
		((ScrolledComposite)this.parent.getParent()).setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setScale(Boolean scale) {
    	this.btnScale.setSelection(scale);
    }
    
    public String getText() {
    	return this.txtImage.getText();
    }
    
    public String getContent() {
    	return this.txtContent.getText();
    }
}
