package org.archicontribs.form.editors;

import org.archicontribs.form.FormGraphicalEditor;
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

public class FontEditor {
	private Label      lblFont;
	private Label      lblSample;
	private Button     btnSelectFont;
	private Button     btnResetToDefault;
	private Label      lblFontSize;
	private StyledText txtFontSize;
	private Button     btnBold;
	private Button     btnItalic;
	private Composite  parent;
	
	public FontEditor(Composite parent, String labelText) {
		this.parent = parent;
		
		lblFont = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblFont.setLayoutData(fd);
        lblFont.setText("labelText");
        
        btnResetToDefault = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblFont, 0, SWT.CENTER);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        btnResetToDefault.setLayoutData(fd);
        btnResetToDefault.setImage(FormGraphicalEditor.binImage);
        btnResetToDefault.addSelectionListener(fontReset);
        btnResetToDefault.setToolTipText("Reset to the default font.");
        
        btnSelectFont = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblFont, 0, SWT.CENTER);
        fd.right = new FormAttachment(btnResetToDefault, -5);
        btnSelectFont.setLayoutData(fd);
        btnSelectFont.setText("f");
        btnSelectFont.addSelectionListener(fontChooser);
        btnSelectFont.setToolTipText("Select the font.");
        
        lblSample = new Label(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFont, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(btnSelectFont, -5);
        lblSample.setLayoutData(fd);
        lblSample.setText("");
        
        lblFontSize = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblSample, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblFontSize.setLayoutData(fd);
        lblFontSize.setText("Font size: ");
        
        txtFontSize = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblSample, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(lblFontSize, 5);
        fd.right = new FormAttachment(txtFontSize, 25);
        txtFontSize.setTextLimit(2);
        txtFontSize.addVerifyListener(numericVerifyListener);
        txtFontSize.addModifyListener(refreshFontModifyListener);
        txtFontSize.setLayoutData(fd);
        
        btnBold = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblSample, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(txtFontSize, FormGraphicalEditor.editorBorderMargin*2);
        btnBold.setLayoutData(fd);
        btnBold.addSelectionListener(refreshFontSelectionListener);
        btnBold.setText("Bold");
        
        btnItalic = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblSample, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(btnBold, FormGraphicalEditor.editorBorderMargin*2);
        btnItalic.setLayoutData(fd);
        btnItalic.addSelectionListener(refreshFontSelectionListener);
        btnItalic.setText("Italic");
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
            FontDialog dlg = new FontDialog((Shell)parent.getData("shell"));
            dlg.setEffectsVisible(false);
            FontData fontData = dlg.open();
            if (fontData != null) {
            	lblSample.setText(fontData.getName());
            	txtFontSize.setText(String.valueOf(fontData.getHeight()));
            	btnBold.setSelection((fontData.getStyle()&SWT.BOLD) != 0);
            	btnItalic.setSelection((fontData.getStyle()&SWT.ITALIC) != 0);
            	
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
        	lblSample.setText("");
        	txtFontSize.setText("");
        	btnBold.setSelection(false);
        	btnItalic.setSelection(false);
        	
        	setFont(true);
        }
    };
    
    private void setFont(boolean updateControl) {
    	Font font;
    	
    	if ( FormPlugin.isEmpty(lblSample.getText()) && getFontSize()!=0 && !getBold() && !getItalic() )
    		font = null;
    	else {
    		lblSample.setFont(null);
    		
			int fontSize = getFontSize();
			if ( fontSize == 0 ) fontSize = 10;
    		
    		int style = SWT.NORMAL;
	    	if ( getBold() ) style |= SWT.BOLD;
	    	if ( getItalic() ) style |= SWT.ITALIC;
	    	
	    	font = new Font(FormGraphicalEditor.display, getFontName(), getFontSize(), style);
    	}
		lblSample.setFont(font);
		
		if ( updateControl) {
			Control    control = (Control)parent.getData("control");
			
			if ( control != null ) {
				control.setFont(font);

				control.setData("font", getFontName());
				control.setData("fontSize", getFontSize());
				control.setData("fontBold", getBold());
				control.setData("fontItalic", getItalic());
			}
		}
	}
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblFont.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblFont.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtFontSize;
	}
    
    public void setFontName(String fontName) {
    	lblSample.setText(fontName);
    	setFont(false);
    }
    
    public void setFontSize(int fontSize) {
    	txtFontSize.setText(String.valueOf(fontSize));
    	setFont(false);
    }
    
    public void setBold(boolean isBold) {
    	btnBold.setSelection(isBold);
    	setFont(false);
    }
    
    public void setItalic(boolean isItalic) {
    	btnItalic.setSelection(isItalic);
    	setFont(false);
    }
    
    public String getFontName() {
    	return lblSample.getText();
    }
    
    public int getFontSize() {
    	if ( FormPlugin.isEmpty(txtFontSize.getText()) )
    		return 0;
    	return Integer.valueOf(txtFontSize.getText());
    }
    
    public boolean getBold() {
    	return btnBold.getSelection();
    }
    
    public boolean getItalic() {
    	return btnItalic.getSelection();
    }
}
