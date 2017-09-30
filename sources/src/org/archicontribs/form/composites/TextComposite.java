package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.CheckEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FontEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class TextComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
    private StringEditor            variableEditor;	     // variable
    private StringEditor            defaultTextEditor;   // defaultText
    private CheckEditor             forceDefaultEditor;  // forceDefault
    private CheckEditor             editableEditor;  	 // editable
	private SizeEditor              sizeEditor;          // x, y, width, height
	private ColorEditor             colorEditor;         // foreground, background
	private FontEditor				fontEditor;			 // font, fontBold, fontItalic
	private StringEditor		    tooltipEditor;       // tooltip
	private StringEditor		    regexpEditor;        // regexp
	private ComboEditor             whenEmptyEditor;  	 // whenEmpty
	private AlignmentEditor         alignmentEditor;     // alignment
	private StringEditor            excelSheetEditor;    // excelSheet
	private StringEditor            excelCellEditor;     // excelCell
	private ComboEditor             excelCellTypeEditor; // excelCellType
	private ComboEditor             excelDefaultEditor;  // excelDefault


	public TextComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "name", "Name:");
		nameEditor.setPosition(0);
		nameEditor.mustSetTreeItemText(true);
		
		// variable
		variableEditor = new StringEditor(this, "variable", "Variable:");
		variableEditor.setPosition(nameEditor.getControl());
		
	    // defaultText
        defaultTextEditor = new StringEditor(this, "default", "Default text:");
        defaultTextEditor.setPosition(variableEditor.getControl());
        
        // forceDefault
        forceDefaultEditor = new CheckEditor(this, "forceDefault", "Force default:");
        forceDefaultEditor.setPosition(defaultTextEditor.getControl());
        
        // editable
        editableEditor = new CheckEditor(this, "editable", "Read only:");
        editableEditor.setPosition(forceDefaultEditor.getControl());
        editableEditor.setInverse(true);
        
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(editableEditor.getControl());
		        
		// Background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(sizeEditor.getControl());
		
		// font, fontBold, fontItalic
		fontEditor = new FontEditor(this, "Font:");
		fontEditor.setPosition(colorEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "tooltip", "Tooltip:");
		tooltipEditor.setPosition(fontEditor.getControl());
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
        
        // excelSheet
        excelSheetEditor = new StringEditor(this, "excelSheet", "Excel sheet:");
        excelSheetEditor.setPosition(alignmentEditor.getControl());
        excelSheetEditor.setTooltipText("Name of the Excel sheet where the variable should be exported to.\n\nIf this field is left blank, then the variable will not be exported to Excel, even if the others Excel related field are set.");
        
        // excelCell
        excelCellEditor = new StringEditor(this, "excelCell", "Excel cell:");
        excelCellEditor.setPosition(excelSheetEditor.getControl());
        excelCellEditor.setTooltipText("Adress of the Excel cell where the variable should be exported to (like A3 or D14).\n\nIf the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set.");
        
        // excelCellType
        excelCellTypeEditor = new ComboEditor(this, "excelType", "Excel type:");
        excelCellTypeEditor.setPosition(excelCellEditor.getControl());
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
            case "variable":      variableEditor.setText((String)value); break;
            case "default":       defaultTextEditor.setText((String)value); break;
            case "forcedefault":  forceDefaultEditor.setChecked((Boolean)value); break;
            case "editable":      editableEditor.setChecked((Boolean)value); break;
    		case "x":			  sizeEditor.setX((Integer)value); break;
    		case "y":			  sizeEditor.setY((Integer)value); break;
    		case "width":		  sizeEditor.setWidth((Integer)value); break;
    		case "height":		  sizeEditor.setHeight((Integer)value); break;
            case "alignment":	  alignmentEditor.setText((String)value); break;
    		case "foreground":	  colorEditor.setForeground((String)value); break;
    		case "background":	  colorEditor.setBackround((String)value); break;
    		case "fontname":	  fontEditor.setFontName((String)value); break;
    		case "fontsize":	  fontEditor.setFontSize((Integer)value); break;
    		case "fontbold":	  fontEditor.setBold((Boolean)value); break;
    		case "fontitalic":	  fontEditor.setItalic((Boolean)value); break;
    		case "tooltip":    	  tooltipEditor.setText((String)value); break;
    		case "regexp":    	  regexpEditor.setText((String)value); break;
    		case "whenempty":     whenEmptyEditor.setText((String)value); break;
    		case "excelsheet":    excelSheetEditor.setText((String)value); break;
    		case "excelcell":	  excelCellEditor.setText((String)value); break;
    		case "excelcelltype": excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  excelDefaultEditor.setText((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
