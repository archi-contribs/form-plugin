package org.archicontribs.form.composites;

import org.archicontribs.form.editors.CheckEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ComboColumnComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
    private StringEditor            variableEditor;	     // variable
    private StringEditor            defaultTextEditor;   // defaultText
    private CheckEditor             forceDefaultEditor;  // forceDefault
    private StringEditor			valuesEditor;        // values		// TODO: needs to be rewritten using a distinct list editor
    private CheckEditor             editableEditor;  	 // editable
	private SizeEditor              sizeEditor;          // width
	private StringEditor		    tooltipEditor;       // tooltip
	private ComboEditor             whenEmptyEditor;  	 // whenEmpty
	private StringEditor            excelColumnEditor;   // excelColumn
	private ComboEditor             excelCellTypeEditor; // excelCellType
	private ComboEditor             excelDefaultEditor;  // excelDefault


	public ComboColumnComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "Name:");
		nameEditor.setPosition(0);
		nameEditor.setProperty("name");
		nameEditor.mustSetTreeItemText(true);
		
		// variable
		variableEditor = new StringEditor(this, "Variable:");
		variableEditor.setPosition(nameEditor.getControl());
		variableEditor.setProperty("variable");
		
	    // defaultText
        defaultTextEditor = new StringEditor(this, "Default text:");
        defaultTextEditor.setPosition(variableEditor.getControl());
        defaultTextEditor.setProperty("default");
        
        // defaultText
        forceDefaultEditor = new CheckEditor(this, "Force default:");
        forceDefaultEditor.setPosition(defaultTextEditor.getControl());
        forceDefaultEditor.setProperty("forceDefault");
        
        // values
        valuesEditor = new StringEditor(this, "Values:");
        valuesEditor.setPosition(forceDefaultEditor.getControl());
        valuesEditor.setProperty("values");
        
        // editable
        editableEditor = new CheckEditor(this, "Editable:");
        editableEditor.setPosition(valuesEditor.getControl());
        editableEditor.setProperty("editable");
        
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(editableEditor.getControl());
		
		// tooltip
		tooltipEditor = new StringEditor(this, "Tooltip:");
		tooltipEditor.setPosition(sizeEditor.getControl());
		tooltipEditor.setProperty("tooltip");
		tooltipEditor.mustSetControlTolltip(true);
		
		// whenempty
		whenEmptyEditor = new ComboEditor(this, "When empty:");
		whenEmptyEditor.setPosition(tooltipEditor.getControl());
		whenEmptyEditor.setItems(new String[] {"", "ignore", "create", "delete"});
		whenEmptyEditor.setProperty("whenEmpty");
        
        // excelColumn
        excelColumnEditor = new StringEditor(this, "Excel cell:");
        excelColumnEditor.setPosition(whenEmptyEditor.getControl());
        excelColumnEditor.setProperty("excelColumn");
        excelColumnEditor.setTooltipText("Adress of the Excel cell where the variable should be exported to (like A3 or D14).\n\nIf the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set.");
        
        // excelCellType
        excelCellTypeEditor = new ComboEditor(this, "Excel type:");
        excelCellTypeEditor.setPosition(excelColumnEditor.getControl());
        excelCellTypeEditor.setProperty("excelType");
        excelCellTypeEditor.setItems(new String[] {"", "string", "boolean", "numeric", "formula"});
        excelCellTypeEditor.setTooltipText("Type of the Excel cell.\n\nDefault: string");
        
        // excelDefault
        excelDefaultEditor = new ComboEditor(this, "Excel default:");
        excelDefaultEditor.setPosition(excelCellTypeEditor.getControl());
        excelDefaultEditor.setProperty("exceldefault");
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
            case "values":        valuesEditor.setText((String[])value);
    		case "x":			  sizeEditor.setX((Integer)value); break;
    		case "y":			  sizeEditor.setY((Integer)value); break;
    		case "width":		  sizeEditor.setWidth((Integer)value); break;
    		case "height":		  sizeEditor.setHeight((Integer)value); break;
    		case "tooltip":    	  tooltipEditor.setText((String)value); break;
    		case "whenempty":     whenEmptyEditor.setText((String)value); break;
    		case "excelcolumn":	  excelColumnEditor.setText((String)value); break;
    		case "excelcelltype": excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  excelDefaultEditor.setText((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
