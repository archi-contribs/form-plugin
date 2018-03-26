package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

public class FontEditor {
	Label      lblFont;
	Button     btnSelectFont;
	Button     btnResetToDefault;
	Label      lblFontSize;
    Label      lblSample;
	StyledText txtFontSize;
	Button     btnBold;
	Button     btnItalic;
	Composite  parent;
	
	public FontEditor(Composite parent, String labelText) {
		this.parent = parent;
		
		this.lblFont = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblFont.setLayoutData(fd);
        this.lblFont.setText(labelText);
        
        this.btnResetToDefault = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFont, 0, SWT.CENTER);
        fd.right = new FormAttachment(100, -FormDialog.editorBorderMargin);
        this.btnResetToDefault.setLayoutData(fd);
        this.btnResetToDefault.setImage(FormDialog.binImage);
        this.btnResetToDefault.addSelectionListener(this.fontReset);
        this.btnResetToDefault.setToolTipText("Reset to the default font.");
        
        this.btnSelectFont = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFont, 0, SWT.CENTER);
        fd.right = new FormAttachment(this.btnResetToDefault, -5);
        this.btnSelectFont.setLayoutData(fd);
        this.btnSelectFont.setText("f");
        this.btnSelectFont.addSelectionListener(this.fontChooser);
        this.btnSelectFont.setToolTipText("Select the font.");
        
        this.lblSample = new Label(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFont, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.btnSelectFont, -5);
        this.lblSample.setLayoutData(fd);
        this.lblSample.setText("");
        
        this.lblFontSize = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblSample, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblFontSize.setLayoutData(fd);
        this.lblFontSize.setText("Font size: ");
        
        this.txtFontSize = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblSample, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(this.lblFontSize, 5);
        fd.right = new FormAttachment(this.txtFontSize, 25);
        this.txtFontSize.setTextLimit(2);
        this.txtFontSize.setLeftMargin(2);
        this.txtFontSize.addVerifyListener(this.numericVerifyListener);
        this.txtFontSize.addModifyListener(this.refreshFontModifyListener);
        this.txtFontSize.setLayoutData(fd);
        
        this.btnBold = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblSample, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(this.txtFontSize, FormDialog.editorBorderMargin*2);
        this.btnBold.setLayoutData(fd);
        this.btnBold.addSelectionListener(this.refreshFontSelectionListener);
        this.btnBold.setText("Bold");
        
        this.btnItalic = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblSample, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(this.btnBold, FormDialog.editorBorderMargin*2);
        this.btnItalic.setLayoutData(fd);
        this.btnItalic.addSelectionListener(this.refreshFontSelectionListener);
        this.btnItalic.setText("Italic");
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
	
    private SelectionAdapter fontChooser = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
            FontDialog dlg = new FontDialog((Shell)FontEditor.this.parent.getData("shell"));
            dlg.setEffectsVisible(false);
            FontData fontData = dlg.open();
            if (fontData != null) {
            	FontEditor.this.lblSample.setText(fontData.getName());
            	FontEditor.this.txtFontSize.setText(String.valueOf(fontData.getHeight()));
            	FontEditor.this.btnBold.setSelection((fontData.getStyle()&SWT.BOLD) != 0);
            	FontEditor.this.btnItalic.setSelection((fontData.getStyle()&SWT.ITALIC) != 0);
            	
				setFont(true);
    		}
    	}
    };
    
    private SelectionListener refreshFontSelectionListener = new SelectionListener() {
    	@Override
    	public void widgetSelected(SelectionEvent event) {
        	setFont(true);
    	}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
    };
    
    private ModifyListener refreshFontModifyListener = new ModifyListener() {
    	@Override
    	public void modifyText(ModifyEvent event) {
        	setFont(true);
    	}
    };
    
    private SelectionAdapter fontReset = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
        	FontEditor.this.lblSample.setText("");
        	FontEditor.this.txtFontSize.setText("");
        	FontEditor.this.btnBold.setSelection(false);
        	FontEditor.this.btnItalic.setSelection(false);
        	
        	setFont(true);
        }
    };
    
    void setFont(boolean updateControl) {
    	TreeItem  treeItem = (TreeItem)this.parent.getData("treeItem");
    	Control   control  = (Control)this.parent.getData("widget");
    	Font      font;
    	
    	if ( FormPlugin.isEmpty(this.lblSample.getText()) && getFontSize()==0 && !getBold() && !getItalic() )
    		font = null;
    	else {
    		this.lblSample.setFont(null);
    		
			int fontSize = getFontSize();
			if ( fontSize == 0 ) fontSize = 10;
    		
    		int style = SWT.NORMAL;
	    	if ( getBold() ) style |= SWT.BOLD;
	    	if ( getItalic() ) style |= SWT.ITALIC;
	    	
	    	font = new Font(FormDialog.display, getFontName(), getFontSize(), style);
    	}
		this.lblSample.setFont(font);
		
		if ( treeItem != null ) {
			treeItem.setData("fontName", getFontName());
			treeItem.setData("fontSize", getFontSize()==0 ? null : getFontSize());
			treeItem.setData("fontBold", getBold() ? true : null);
			treeItem.setData("fontItalic", getItalic() ? true : null);
			
			// we update all the embedded controls that do not have a font specified
			for( TreeItem childTreeItem: treeItem.getItems() )
				setFont(childTreeItem, font);
		}
		
		if ( updateControl && control != null ) {
			control.setFont(font);
		}
	}
    
    private void setFont(TreeItem treeItem, Font font) {
    	if ( treeItem != null ) {
    		Control control = (Control)treeItem.getData("widget");
    		if ( control != null ) {
    			control.setFont(font);
    		}
    		
			for( TreeItem childTreeItem: treeItem.getItems() ) {
				setFont(childTreeItem, font);
			}
    	}
    }
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblFont.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblFont.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return this.txtFontSize;
	}
    
    public void setFontName(String fontName) {
    	this.lblSample.setText(fontName==null ? "" : fontName);
    	setFont(false);
    }
    
    public void setFontSize(Integer fontSize) {
  	    this.txtFontSize.setText(fontSize==null ? "" : String.valueOf(fontSize));
    	setFont(false);
    }
    
    public void setBold(Boolean isBold) {
    	this.btnBold.setSelection(isBold==null ? false : isBold);
    	setFont(false);
    }
    
    public void setItalic(Boolean isItalic) {
    	this.btnItalic.setSelection(isItalic==null ? false : isItalic);
    	setFont(false);
    }
    
    public String getFontName() {
    	return this.lblSample.getText();
    }
    
    public int getFontSize() {
    	if ( FormPlugin.isEmpty(this.txtFontSize.getText()) )
    		return 0;
    	return Integer.valueOf(this.txtFontSize.getText());
    }
    
    public boolean getBold() {
    	return this.btnBold.getSelection();
    }
    
    public boolean getItalic() {
    	return this.btnItalic.getSelection();
    }
}
