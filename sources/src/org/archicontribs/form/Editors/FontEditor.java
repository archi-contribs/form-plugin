package org.archicontribs.form.Editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	private Label      lblFont;
	private Label      lblSample;
	private Button     btnSelectFont;
	private Button     btnResetToDefault;
	private Label      lblFontSize;
	private StyledText txtFontSize;
	private Button     btnBold;
	private Button     btnItalic;
	private Composite  parent;
	
	public FontEditor(Composite parent) {
		this.parent = parent;
		
		lblFont = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblFont.setLayoutData(fd);
        lblFont.setText("Font:");
        
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
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
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
        lblSample.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        
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
        txtFontSize.setTextLimit(2);
        txtFontSize.addVerifyListener(numericVerifyListener);
        txtFontSize.setLayoutData(fd);
        
        btnBold = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblSample, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(txtFontSize, 5);
        btnBold.setLayoutData(fd);
        btnBold.setText("Bold");
        
        btnItalic = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblSample, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(btnBold, FormGraphicalEditor.editorVerticalMargin);
        btnItalic.setLayoutData(fd);
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
        	Control    control = (Control)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	
            FontDialog dlg = new FontDialog((Shell)parent.getData("shell"));
            FontData fontData = dlg.open();
            if (fontData != null) {
                Font font = new Font(((Shell)parent.getData("shell")).getDisplay(), fontData);
            	lblSample.setFont(font);
            	lblSample.setText(fontData.getName());
            	txtFontSize.setText(String.valueOf(fontData.getHeight()));
            	btnBold.setEnabled((fontData.getStyle()&SWT.BOLD) != 0);
            	btnItalic.setEnabled((fontData.getStyle()&SWT.ITALIC) != 0);
            	
				if ( control != null ) {
					control.setFont(font);
				}
            	
				if ( treeItem != null ) {
					treeItem.setData("font", fontData.getName());
					treeItem.setData("fontSize", fontData.getHeight());
					treeItem.setData("fontBold", (fontData.getStyle()&SWT.BOLD) != 0);
					treeItem.setData("fontItalic", (fontData.getStyle()&SWT.ITALIC) != 0);
				}
    		}
    	}
    };
    
    private SelectionAdapter fontReset = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
        	Control    control = (Control)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");

        	lblSample.setText("");
        	btnBold.setEnabled(false);
        	btnItalic.setEnabled(false);
        	
			lblSample.setFont(null);
			
			if ( control != null ) {
				control.setFont(null);
			}
			
			if ( treeItem != null ) {
				treeItem.setData("font", "");
				treeItem.setData("fontSize", 0);
				treeItem.setData("fontBold", false);
				treeItem.setData("fontItalic", false);
			}
        }
    };
    
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
	
	private void setFont() {
    	Font font = lblSample.getFont();
    	
    	if ( font != null )
    		font.dispose();
    	
    	int style = SWT.NORMAL;
    	if ( getBold() ) style |= SWT.BOLD;
    	if ( getItalic() ) style |= SWT.ITALIC;
    	
    	font = new Font(FormGraphicalEditor.display, getfont(), getFontSize(), style);
		lblSample.setFont(font);
	}
    
    public void setFont(String fontName) {
    	lblSample.setText(fontName);
    	setFont();
    }
    
    public void setFontSize(int fontSize) {
    	txtFontSize.setText(String.valueOf(fontSize));
    	setFont();
    }
    
    public void setBold(boolean isBold) {
    	btnBold.setEnabled(isBold);
    	setFont();
    }
    
    public void setItalic(boolean isItalic) {
    	btnItalic.setEnabled(isItalic);
    	setFont();
    }
    
    public String getfont() {
    	return lblSample.getText();
    }
    
    public int getFontSize() {
    	if ( FormPlugin.isEmpty(txtFontSize.getText()) )
    		return 0;
    	return Integer.valueOf(txtFontSize.getText());
    }
    
    public boolean getBold() {
    	return btnBold.getEnabled();
    }
    
    public boolean getItalic() {
    	return btnItalic.getEnabled();
    }
}
