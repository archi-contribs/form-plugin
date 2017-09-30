package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class LabelColumnComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
    private StringEditor            textEditor;			 // text
	private SizeEditor              sizeEditor;          // width
	private StringEditor            tooltipEditor;       // tooltip
	private AlignmentEditor         alignmentEditor;    // alignment
    private StringEditor            excelColumnEditor;   // excelColumn
    private ComboEditor             excelCellTypeEditor; // excelCellType
    private ComboEditor             excelDefaultEditor;  // excelDefault

	public LabelColumnComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "name", "Name:");
		nameEditor.setPosition(0);
		nameEditor.mustSetTreeItemText(true);
		nameEditor.mustSetControlText(true);
		nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// text
		textEditor = new StringEditor(this, "text", "Text:");
		textEditor.setPosition(nameEditor.getControl());
		textEditor.mustSetControlText(true);
						
		// width
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(textEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "tooltip", "Tooltip:");
		tooltipEditor.setPosition(sizeEditor.getControl());
		tooltipEditor.mustSetControlTolltip(true);
		tooltipEditor.setTooltipText("Specifies the tooltip to show when the mouse stands is over the control.\n\nDefault: none.");
		
	      // alignement
        alignmentEditor = new AlignmentEditor(this, "Alignment:");
        alignmentEditor.setPosition(tooltipEditor.getControl());
        
        // excelCell
        excelColumnEditor = new StringEditor(this, "excelColumn", "Excel column:");
        excelColumnEditor.setPosition(alignmentEditor.getControl());
        excelColumnEditor.setTooltipText("Adress of the Excel cell where the variable should be exported to (like A3 or D14).\n"+
        		"\n"+
        		"If the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set."
        		);
        
        // excelCellType
        excelCellTypeEditor = new ComboEditor(this, "exceltype", "Excel type:");
        excelCellTypeEditor.setPosition(excelColumnEditor.getControl());
        excelCellTypeEditor.setItems(new String[] {"", "string", "boolean", "numeric", "formula"});
        excelCellTypeEditor.setTooltipText("Type of the Excel cell.\n\nDefault: string");
        
        // excelDefault
        excelDefaultEditor = new ComboEditor(this, "exceldefault", "Excel default:");
        excelDefaultEditor.setPosition(excelCellTypeEditor.getControl());
        excelDefaultEditor.setItems(new String[] {"", "blank", "zero", "delete"});
        excelDefaultEditor.setTooltipText("Behaviour of the plugin when exporting an empty value:\n"+
                "   - blank : a blank cell will be created (ie a cell with no content)\n"+
                "   - zero : a cell with a zero value in it:\n"+
                "                - 0 for numeric cells\n"+
                "                - empty string for string and formula cells\n"+
                "                - false for boolean cells\n"+
                "   - delete : the cell will be deleted.\n"+
                "\n"+
                "Default: blank");
	}
	
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "x":    		  sizeEditor.setX((Integer)value); break;
    		case "y":    		  sizeEditor.setY((Integer)value); break;
    		case "width":    	  sizeEditor.setWidth((Integer)value); break;
    		case "height":    	  sizeEditor.setHeight((Integer)value); break;
    		case "name":    	  nameEditor.setText((String)value); break;
    		case "text":    	  textEditor.setText((String)value); break;
    		case "tooltip":    	  tooltipEditor.setText((String)value); break;
    	    case "alignment":     alignmentEditor.setText((String)value); break;
    		case "excelcolumn":	  excelColumnEditor.setText((String)value); break;
    		case "excelcelltype": excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  excelDefaultEditor.setText((String)value); break;
    		default:			throw new RuntimeException("does not know key "+key);
    	}
    	
    }
}
