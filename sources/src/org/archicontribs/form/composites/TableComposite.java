package org.archicontribs.form.composites;

import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.IntegerEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TableComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;             // name
	private StringEditor            commentEditor;          // comment
	private SizeEditor              sizeEditor;             // x, y, width, height
	private ColorEditor             colorEditor;            // foreground, background
	private StringEditor            tooltipEditor;          // tooltip
    private StringEditor            excelSheetEditor;       // excelSheet
    private IntegerEditor           excelFirstLineEditor;   // excelFirstLine
    private IntegerEditor           excelLastLineEditor;    // excelLastLine

	public TableComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		this.nameEditor = new StringEditor(this, "name", "Name:");
		this.nameEditor.setPosition(0);
		this.nameEditor.mustSetTreeItemText(true);
		this.nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		this.commentEditor = new StringEditor(this, "comment", "Comment:");
		this.commentEditor.setPosition(this.nameEditor.getControl());
		this.commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
		
		// x, y, width, height
		this.sizeEditor = new SizeEditor(this);
		this.sizeEditor.setPosition(this.commentEditor.getControl());
		
		// foreground, background
		this.colorEditor = new ColorEditor(this, "Color:");
		this.colorEditor.setPosition(this.sizeEditor.getControl());
		
		// tooltip
		this.tooltipEditor = new StringEditor(this, "tooltip", "Tooltip:");
		this.tooltipEditor.setPosition(this.colorEditor.getControl());
		this.tooltipEditor.mustSetControlTolltip(true);
		this.tooltipEditor.setTooltipText("Specifies the tooltip to show when the mouse stands is over the control.\n\nDefault: none.");
		
        // excelSheet
        this.excelSheetEditor = new StringEditor(this, "excelSheet", "Excel sheet:");
        this.excelSheetEditor.setPosition(this.tooltipEditor.getControl());
        this.excelSheetEditor.setTooltipText("Name of the Excel sheet where the text should be exported to.\n\nIf this field is left blank, then the variable will not be exported to Excel, even if the others Excel related field are set.");
        
		// name
        this.excelFirstLineEditor = new IntegerEditor(this, "excelFirstLine", "Excel first line:");
        this.excelFirstLineEditor.setPosition(this.excelSheetEditor.getControl());
		
		// name
        this.excelLastLineEditor = new IntegerEditor(this, "excelLastLine", "Excel last line:");
        this.excelLastLineEditor.setPosition(this.excelFirstLineEditor.getControl());
        
	}
	
    @Override
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
            case "name":		  this.nameEditor.setText((String)value); break;
    		case "comment":       this.commentEditor.setText((String)value); break;
    		case "x":			  this.sizeEditor.setX((Integer)value); break;
    		case "y":			  this.sizeEditor.setY((Integer)value); break;
    		case "width":		  this.sizeEditor.setWidth((Integer)value); break;
    		case "height":		  this.sizeEditor.setHeight((Integer)value); break;
            case "foreground":	  this.colorEditor.setForeground((String)value); break;
    		case "background":	  this.colorEditor.setBackground((String)value); break;
    		case "tooltip":    	  this.tooltipEditor.setText((String)value); break;
    		case "excelsheet":    this.excelSheetEditor.setText((String)value); break;
    		case "excelfirstline":this.excelFirstLineEditor.setInteger((Integer)value); break;
    		case "excellastline": this.excelLastLineEditor.setInteger((Integer)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
