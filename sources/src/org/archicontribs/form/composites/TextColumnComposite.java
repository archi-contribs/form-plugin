package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.CheckEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TextColumnComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
    private StringEditor            defaultTextEditor;   // defaultText
    private CheckEditor             forceDefaultEditor;  // forceDefault
    private CheckEditor             editableEditor;  	 // editable
	private SizeEditor              sizeEditor;          // width
	private StringEditor		    tooltipEditor;       // tooltip
	private StringEditor		    regexpEditor;        // regexp
	private ComboEditor             whenEmptyEditor;  	 // whenEmpty
	private AlignmentEditor         alignmentEditor;     // alignment
	private StringEditor            excelColumnEditor;   // excelColumn
	private ComboEditor             excelCellTypeEditor; // excelCellType
	private ComboEditor             excelDefaultEditor;  // excelDefault


	public TextColumnComposite(Composite parent, int style) {
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
		
	    // defaultText
        defaultTextEditor = new StringEditor(this, "default", "Default text:");
        defaultTextEditor.setPosition(nameEditor.getControl());
        
        // defaultText
        forceDefaultEditor = new CheckEditor(this, "forceDefault", "Force default:");
        
        // editable
        editableEditor = new CheckEditor(this, "editable", "Editable:");
        
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(editableEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "tooltip", "Tooltip:");
		tooltipEditor.setPosition(sizeEditor.getControl());
		tooltipEditor.mustSetControlTolltip(true);
		
		// regexp
		regexpEditor = new StringEditor(this, "regexp", "Regexp:");
		regexpEditor.setPosition(tooltipEditor.getControl());
		
		// whenempty
		whenEmptyEditor = new ComboEditor(this, "whenEmpty", "When empty:");
		whenEmptyEditor.setPosition(regexpEditor.getControl());
		whenEmptyEditor.setItems(new String[] {"", "ignore", "create", "delete"});
		
        // alignment
        alignmentEditor = new AlignmentEditor(this, "Alignment:");
        alignmentEditor.setPosition(whenEmptyEditor.getControl());
        alignmentEditor.setItems(new String[] {"", "left", "center", "right"});
        alignmentEditor.setTooltipText("Choose the alignment.\n\nDefault: left.");
        
        // excelColumn
        excelColumnEditor = new StringEditor(this, "excelColumn", "Excel column:");
        excelColumnEditor.setPosition(alignmentEditor.getControl());
        excelColumnEditor.setTooltipText("Adress of the Excel cell where the variable should be exported to (like A3 or D14).\n\nIf the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set.");
        
        // excelCellType
        excelCellTypeEditor = new ComboEditor(this, "excelType", "Excel type:");
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
            case "name":          nameEditor.setText((String)value); break;
            case "default":       defaultTextEditor.setText((String)value); break;
            case "forcedefault":  forceDefaultEditor.setChecked((Boolean)value); break;
            case "editable":      editableEditor.setChecked((Boolean)value); break;
    		case "x":			  sizeEditor.setX((Integer)value); break;
    		case "y":			  sizeEditor.setY((Integer)value); break;
    		case "width":		  sizeEditor.setWidth((Integer)value); break;
    		case "height":		  sizeEditor.setHeight((Integer)value); break;
            case "alignment":	  alignmentEditor.setText((String)value); break;
    		case "tooltip":    	  tooltipEditor.setText((String)value); break;
    		case "regexp":    	  regexpEditor.setText((String)value); break;
    		case "whenempty":     whenEmptyEditor.setText((String)value); break;
    		case "excelcolumn":	  excelColumnEditor.setText((String)value); break;
    		case "excelcelltype": excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  excelDefaultEditor.setText((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
