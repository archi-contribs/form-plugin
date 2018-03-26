package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class AlignmentEditor extends ComboEditor {
	public AlignmentEditor(Composite parent, String labelText) {
		super(parent, "alignment", labelText);
		
		this.parent = parent;
		
		this.combo=super.getControl();
		this.combo.addSelectionListener(this.alignmentSelectionListener);
		
        super.setTooltipText("Choose the alignment between \"left\", \"center\" and \"right\".\n\nDefault: left.");
        super.setItems(new String[] {"left", "center", "right"});
	}
	
	private SelectionListener alignmentSelectionListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TreeItem  treeItem = (TreeItem)AlignmentEditor.this.parent.getData("treeItem");
			Widget    widget = (Control)AlignmentEditor.this.parent.getData("widget");

			if ( widget != null ) {
				int alignment = 0;
				switch ( getText() ) {
					case "left": alignment=SWT.LEFT; break;
					case "center": alignment=SWT.CENTER; break;
					case "right": alignment=SWT.RIGHT; break;
                    default:
                        // unknown value, default to "left"
                        alignment=SWT.LEFT;
				}

				if ( alignment != 0 ) {
					switch ( widget.getClass().getSimpleName() ) {
						case "Label":
							((Label)widget).setAlignment(alignment);
							break;

						case "StyledText":
							((StyledText)widget).setAlignment(alignment);
							break;

						case "Button":
							((Button)widget).setAlignment(alignment);
							break;

						default : throw new RuntimeException("Do not know "+widget.getClass().getSimpleName()+" controls");
					}
				}
			}
			
			if ( treeItem != null )
				treeItem.setData("alignment", getText());
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);

		}
	};
    
    @Override
    public void setText(String text) {
        this.combo.removeSelectionListener(this.alignmentSelectionListener);
        super.setText(FormPlugin.isEmpty(text) ? FormDialog.validAlignment[0] : text);
        this.combo.addSelectionListener(this.alignmentSelectionListener);
    }   
}
