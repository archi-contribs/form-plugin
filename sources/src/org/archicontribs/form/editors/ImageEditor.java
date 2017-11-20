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
	private Label      lblImage;
	private StyledText txtImage;
	private Label      lblContent;
	private StyledText txtContent;
	private Label      lblScale;
	private Button     btnScale;
	private Composite  parent;
	private String     property = null;
		
	private Widget     referencedWidget = null;

	public ImageEditor(Composite parent, String property, String labelText) {
		this.parent = parent;
   		this.property = property;
   		
		lblImage = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblImage.setLayoutData(fd);
        lblImage.setText(labelText);
        
        txtImage = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(lblImage, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        txtImage.setLayoutData(fd);
        txtImage.setLeftMargin(2);
        txtImage.addModifyListener(stringModifyListener);
        
		lblContent = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblImage, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblContent.setLayoutData(fd);
        lblContent.setText("Content:");
        
        txtContent = new StyledText(parent, SWT.BORDER | SWT.NO_SCROLL);
        fd = new FormData();
        fd.top = new FormAttachment(lblImage, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        txtContent.setLayoutData(fd);
        txtContent.setLeftMargin(2);
        txtContent.addModifyListener(stringModifyListener);
        
		lblScale = new Label(parent, SWT.NONE);
		fd = new FormData();
        fd.top = new FormAttachment(lblContent, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        lblScale.setLayoutData(fd);
        lblScale.setText("Scale:");
        
        btnScale = new Button(parent, SWT.CHECK);
        fd = new FormData();
    	fd.top = new FormAttachment(lblContent, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        btnScale.setLayoutData(fd);
        btnScale.addSelectionListener(scaleSelectionListener);
        
        //TODO: add a browse icon
        

    	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
       	if ( treeItem != null && property != null) {
            setImage((String)treeItem.getData(property));
        }
	}
	
	public void setTooltipText(String tooltip) {
		txtImage.setToolTipText(tooltip);
		txtContent.setToolTipText(tooltip);
	}
	
	public void setWidth(int width) {
		FormData fd = (FormData)txtImage.getLayoutData();
		fd.right = new FormAttachment(txtImage, width);
		txtImage.layout();
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
                    	if ( btnScale.getSelection() ) {
                    		Image scaledImage = new Image(Display.getCurrent(), image.getImageData().scaledTo(width, height));
                    		((Label)widget).setImage(scaledImage);
                    		image.dispose();
                    	} else {
                    		((Label)widget).setImage(image);
                    	}
    	            } catch (Exception ign) {}
            	}
        	}
        	
        	if ( treeItem != null && property != null ) {
        		treeItem.setData(property, getText());
        		treeItem.setData("content", getContent());
        	}
        	
        	((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    };
    
	private SelectionListener scaleSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
	    	
	    	if ( treeItem != null ) {
	    	    treeItem.setData("scale", btnScale.getSelection());
	    	}
	    	
	    	txtImage.notifyListeners(SWT.Modify, new Event());		// calls the stringModifyListener() method
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
        lblImage.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblImage.setLayoutData(fd);
	}
	
	public Button getControl() {
		return btnScale;
	}
    
    public void setImage(String string) {
		txtImage.removeModifyListener(stringModifyListener);
		txtImage.setText(string==null ? "" : string);
		txtImage.addModifyListener(stringModifyListener);
		
		((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setContent(String string) {
		txtContent.removeModifyListener(stringModifyListener);
		txtContent.setText(string==null ? "" : string);
		txtContent.addModifyListener(stringModifyListener);
		
		((ScrolledComposite)parent.getParent()).setMinSize(((Composite)parent).computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
    
    public void setScale(Boolean scale) {
    	btnScale.setSelection(scale);
    }
    
    public String getText() {
    	return txtImage.getText();
    }
    
    public String getContent() {
    	return txtContent.getText();
    }
}
