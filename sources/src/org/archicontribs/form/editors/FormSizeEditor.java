package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormGraphicalEditor;
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
	private Label      lblWidth;
	private StyledText txtWidth;
	private Label      lblHeight;
	private StyledText txtHeight;
	private Label      lblSpacing;
	private StyledText txtSpacing;
	private Label      lblButtonWidth;
	private StyledText txtButtonWidth;
	private Label      lblButtonHeight;
	private StyledText txtButtonHeight;
	private Composite  parent;
	
	public FormSizeEditor(Composite parent) {
		this.parent = parent;
		
		// width
		lblWidth = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblWidth.setLayoutData(fd);
        lblWidth.setText("Width:");
        
        txtWidth = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(txtWidth, 40);
        txtWidth.setLayoutData(fd);
        txtWidth.setTextLimit(4);
        txtWidth.setLeftMargin(2);
        txtWidth.addVerifyListener(numericVerifyListener);
        txtWidth.addModifyListener(sizeModifyListener);
        txtWidth.setToolTipText("Width of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogWidth+"."
        		);
        
        // height
        lblHeight = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(txtWidth, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblHeight.setLayoutData(fd);
        lblHeight.setText("Height:");
        
        txtHeight = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(txtHeight, 40);
        txtHeight.setLayoutData(fd);
        txtHeight.setTextLimit(4);
        txtHeight.setLeftMargin(2);
        txtHeight.addVerifyListener(numericVerifyListener);
        txtHeight.addModifyListener(sizeModifyListener);
        txtHeight.setToolTipText("Height of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogHeight+"."
        		);
        
        // spacing
        lblSpacing = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(txtHeight, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblSpacing.setLayoutData(fd);
        lblSpacing.setText("Spacing:");
        
        txtSpacing = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpacing, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(txtSpacing, 40);
        txtSpacing.setLayoutData(fd);
        txtSpacing.setTextLimit(2);
        txtSpacing.setLeftMargin(2);
        txtSpacing.addVerifyListener(numericVerifyListener);
        txtSpacing.addModifyListener(sizeModifyListener);
        txtSpacing.setToolTipText("Space to leave bewteen the form border and the tab in the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogSpacing+"."
        		);
        
        // buttonWidth
        lblButtonWidth = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(txtSpacing, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblButtonWidth.setLayoutData(fd);
        lblButtonWidth.setText("Buttons width:");
        
        txtButtonWidth = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblButtonWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(txtButtonWidth, 40);
        txtButtonWidth.setLayoutData(fd);
        txtButtonWidth.setTextLimit(3);
        txtButtonWidth.setLeftMargin(2);
        txtButtonWidth.addVerifyListener(numericVerifyListener);
        txtButtonWidth.addModifyListener(sizeModifyListener);
        txtButtonWidth.setToolTipText("Width of the Ok, Cancel and Export to Excel buttons\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultButtonWidth+"."
        		);
        
        // buttonHeight
        lblButtonHeight = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(txtButtonWidth, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblButtonHeight.setLayoutData(fd);
        lblButtonHeight.setText("Buttons height:");
        
        txtButtonHeight = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblButtonHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(txtButtonHeight, 40);
        txtButtonHeight.setLayoutData(fd);
        txtButtonHeight.setTextLimit(2);
        txtButtonHeight.setLeftMargin(2);
        txtButtonHeight.addVerifyListener(numericVerifyListener);
        txtButtonHeight.addModifyListener(sizeModifyListener);
        txtButtonHeight.setToolTipText("Height of the Ok, Cancel and Export to Excel buttons\n"+
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
        	Shell     form = (Shell)parent.getData("widget");
        	TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
        	
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
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblWidth.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblWidth.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtButtonHeight;
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
    
    public void setButtonWidth(Integer buttonWidth) {
		txtButtonWidth.removeModifyListener(sizeModifyListener);
		txtButtonWidth.setText(buttonWidth==null ? "" : String.valueOf(buttonWidth));
		txtButtonWidth.addModifyListener(sizeModifyListener);
    }
    
    public void setButtonHeight(Integer buttonHeight) {
		txtButtonHeight.removeModifyListener(sizeModifyListener);
		txtButtonHeight.setText(buttonHeight==null ? "" : String.valueOf(buttonHeight));
		txtButtonHeight.addModifyListener(sizeModifyListener);
    }
    
    public void setSpacing(Integer spacing) {
		txtSpacing.removeModifyListener(sizeModifyListener);
		txtSpacing.setText(spacing==null ? "" : String.valueOf(spacing));
		txtSpacing.addModifyListener(sizeModifyListener);
    }
    
    public int getWidth() {
		if ( FormPlugin.isEmpty(txtWidth.getText()) )
			return FormDialog.defaultDialogWidth;
		if ( Integer.valueOf(txtWidth.getText()) < 50 )
			return 50;
		return Integer.valueOf(txtWidth.getText());
    }
    
    public int getHeight() {
		if ( FormPlugin.isEmpty(txtHeight.getText()) )
			return FormDialog.defaultDialogHeight;
		if ( Integer.valueOf(txtHeight.getText()) < 50 )
			return 50;
		return Integer.valueOf(txtHeight.getText());
    }
    
    public int getSpacing() {
		if ( FormPlugin.isEmpty(txtSpacing.getText()) )
			return FormDialog.defaultDialogSpacing;
		return Integer.valueOf(txtSpacing.getText());
    }
    
    public int getButtonWidth() {
		if ( FormPlugin.isEmpty(txtButtonWidth.getText()) )
			return FormDialog.defaultButtonWidth;
		return Integer.valueOf(txtButtonWidth.getText());
    }
    
    public int getButtonHeight() {
		if ( FormPlugin.isEmpty(txtButtonHeight.getText()) )
			return FormDialog.defaultButtonHeight;
		return Integer.valueOf(txtButtonHeight.getText());
    }
}
