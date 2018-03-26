package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

public class FormSizeEditor {
	Label      lblWidth;
	StyledText txtWidth;
	Label      lblHeight;
	StyledText txtHeight;
	Label      lblSpacing;
	StyledText txtSpacing;
	Label      lblButtonWidth;
	StyledText txtButtonWidth;
	Label      lblButtonHeight;
	StyledText txtButtonHeight;
	Composite  parent;
	
	public FormSizeEditor(Composite parent) {
		this.parent = parent;
		
		// width
		this.lblWidth = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblWidth.setLayoutData(fd);
        this.lblWidth.setText("Width:");
        
        this.txtWidth = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.txtWidth, 40);
        this.txtWidth.setLayoutData(fd);
        this.txtWidth.setTextLimit(4);
        this.txtWidth.setLeftMargin(2);
        this.txtWidth.addVerifyListener(this.numericVerifyListener);
        this.txtWidth.addModifyListener(this.sizeModifyListener);
        this.txtWidth.setToolTipText("Width of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogWidth+"."
        		);
        
        // height
        this.lblHeight = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.txtWidth, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblHeight.setLayoutData(fd);
        this.lblHeight.setText("Height:");
        
        this.txtHeight = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.txtHeight, 40);
        this.txtHeight.setLayoutData(fd);
        this.txtHeight.setTextLimit(4);
        this.txtHeight.setLeftMargin(2);
        this.txtHeight.addVerifyListener(this.numericVerifyListener);
        this.txtHeight.addModifyListener(this.sizeModifyListener);
        this.txtHeight.setToolTipText("Height of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogHeight+"."
        		);
        
        // spacing
        this.lblSpacing = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.txtHeight, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblSpacing.setLayoutData(fd);
        this.lblSpacing.setText("Spacing:");
        
        this.txtSpacing = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblSpacing, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.txtSpacing, 40);
        this.txtSpacing.setLayoutData(fd);
        this.txtSpacing.setTextLimit(2);
        this.txtSpacing.setLeftMargin(2);
        this.txtSpacing.addVerifyListener(this.numericVerifyListener);
        this.txtSpacing.addModifyListener(this.sizeModifyListener);
        this.txtSpacing.setToolTipText("Space to leave bewteen the form border and the tab in the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogSpacing+"."
        		);
        
        // buttonWidth
        this.lblButtonWidth = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.txtSpacing, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblButtonWidth.setLayoutData(fd);
        this.lblButtonWidth.setText("Buttons width:");
        
        this.txtButtonWidth = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblButtonWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.txtButtonWidth, 40);
        this.txtButtonWidth.setLayoutData(fd);
        this.txtButtonWidth.setTextLimit(3);
        this.txtButtonWidth.setLeftMargin(2);
        this.txtButtonWidth.addVerifyListener(this.numericVerifyListener);
        this.txtButtonWidth.addModifyListener(this.sizeModifyListener);
        this.txtButtonWidth.setToolTipText("Width of the Ok, Cancel and Export to Excel buttons\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultButtonWidth+"."
        		);
        
        // buttonHeight
        this.lblButtonHeight = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.txtButtonWidth, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblButtonHeight.setLayoutData(fd);
        this.lblButtonHeight.setText("Buttons height:");
        
        this.txtButtonHeight = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblButtonHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.txtButtonHeight, 40);
        this.txtButtonHeight.setLayoutData(fd);
        this.txtButtonHeight.setTextLimit(2);
        this.txtButtonHeight.setLeftMargin(2);
        this.txtButtonHeight.addVerifyListener(this.numericVerifyListener);
        this.txtButtonHeight.addModifyListener(this.sizeModifyListener);
        this.txtButtonHeight.setToolTipText("Height of the Ok, Cancel and Export to Excel buttons\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultButtonHeight+"."
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
        	Shell     form = (Shell)FormSizeEditor.this.parent.getData("widget");
        	TreeItem  treeItem = (TreeItem)FormSizeEditor.this.parent.getData("treeItem");
        	
        	int formWidth = getWidth();
        	int formHeight = getHeight();
        	int formSpacing = getSpacing();
        	int buttonWidth = getButtonWidth();
        	int buttonHeight = getButtonHeight();
        	
        	if ( treeItem != null ) {
        		treeItem.setData("width", formWidth);
        		treeItem.setData("height", formHeight);
        		treeItem.setData("spacing", formSpacing);
        		treeItem.setData("buttonWidth", buttonWidth);
        		treeItem.setData("buttonHeight", buttonHeight);
        	}
        	
        	if ( form != null ) {
		    	form.setSize(formWidth, formHeight);
				// we resize the form because we want the width and height to be the client's area width and height
		    	//TODO : size of the tab would be better ???
		        Rectangle area = form.getClientArea();
		        formWidth = formWidth * 2 - area.width;
		        formHeight = formHeight * 2 - area.height;
		        form.setSize(formWidth, formHeight);
		        
		        CTabFolder tabFolder = (CTabFolder)form.getData("tab folder");
		        if ( tabFolder != null ) {
		            
		            area = form.getClientArea();
		            int tabFolderWidth = area.width - formSpacing * 2;
		            int tabFolderHeight = area.height - formSpacing * 3 - buttonHeight;
		            
		            tabFolder.setBounds(formSpacing, formSpacing, tabFolderWidth, tabFolderHeight);
		            
		            Button buttonCancel = (Button)form.getData("cancel button");
		            if ( buttonCancel != null ) {
		            	buttonCancel.setBounds(tabFolderWidth+formSpacing-buttonWidth, tabFolderHeight+formSpacing*2, buttonWidth, buttonHeight);
		            }
		            
		            Button buttonOk = (Button)form.getData("ok button");
		            if ( buttonOk != null ) {
		            	buttonOk.setBounds(tabFolderWidth-(buttonWidth*2), tabFolderHeight+(formSpacing*2), buttonWidth, buttonHeight);
		            }
		            
		            Button buttonExport = (Button)form.getData("export button");
		            if ( buttonExport != null ) {
		            	buttonExport.setBounds(tabFolderWidth-(buttonWidth*3)-formSpacing, tabFolderHeight+(formSpacing*2), buttonWidth, buttonHeight);
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
        this.lblWidth.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblWidth.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return this.txtButtonHeight;
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
    
    public void setButtonWidth(Integer buttonWidth) {
		this.txtButtonWidth.removeModifyListener(this.sizeModifyListener);
		this.txtButtonWidth.setText(buttonWidth==null ? "" : String.valueOf(buttonWidth));
		this.txtButtonWidth.addModifyListener(this.sizeModifyListener);
    }
    
    public void setButtonHeight(Integer buttonHeight) {
		this.txtButtonHeight.removeModifyListener(this.sizeModifyListener);
		this.txtButtonHeight.setText(buttonHeight==null ? "" : String.valueOf(buttonHeight));
		this.txtButtonHeight.addModifyListener(this.sizeModifyListener);
    }
    
    public void setSpacing(Integer spacing) {
		this.txtSpacing.removeModifyListener(this.sizeModifyListener);
		this.txtSpacing.setText(spacing==null ? "" : String.valueOf(spacing));
		this.txtSpacing.addModifyListener(this.sizeModifyListener);
    }
    
    public int getWidth() {
		if ( FormPlugin.isEmpty(this.txtWidth.getText()) )
			return FormDialog.defaultDialogWidth;
		if ( Integer.valueOf(this.txtWidth.getText()) < 50 )
			return 50;
		return Integer.valueOf(this.txtWidth.getText());
    }
    
    public int getHeight() {
		if ( FormPlugin.isEmpty(this.txtHeight.getText()) )
			return FormDialog.defaultDialogHeight;
		if ( Integer.valueOf(this.txtHeight.getText()) < 50 )
			return 50;
		return Integer.valueOf(this.txtHeight.getText());
    }
    
    public int getSpacing() {
		if ( FormPlugin.isEmpty(this.txtSpacing.getText()) )
			return FormDialog.defaultDialogSpacing;
		return Integer.valueOf(this.txtSpacing.getText());
    }
    
    public int getButtonWidth() {
		if ( FormPlugin.isEmpty(this.txtButtonWidth.getText()) )
			return FormDialog.defaultButtonWidth;
		return Integer.valueOf(this.txtButtonWidth.getText());
    }
    
    public int getButtonHeight() {
		if ( FormPlugin.isEmpty(this.txtButtonHeight.getText()) )
			return FormDialog.defaultButtonHeight;
		return Integer.valueOf(this.txtButtonHeight.getText());
    }
}
