package org.archicontribs.form.composites;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.editors.CheckEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FontEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ComboComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
	private StringEditor            commentEditor;       // comment
    private StringEditor            variableEditor;	     // variable
    private StringEditor            defaultTextEditor;   // defaultText
    private CheckEditor             forceDefaultEditor;  // forceDefault
    private StringEditor			valuesEditor;        // values		// TODO: needs to be rewritten using a distinct list editor
    private CheckEditor             editableEditor;  	 // editable
	private SizeEditor              sizeEditor;          // x, y, width, height
	private ColorEditor             colorEditor;         // foreground, background
	private FontEditor				fontEditor;			 // font, fontBold, fontItalic
	private StringEditor		    tooltipEditor;       // tooltip
	private ComboEditor             whenEmptyEditor;  	 // whenEmpty
	private StringEditor            excelSheetEditor;    // excelSheet
	private StringEditor            excelCellEditor;     // excelCell
	private ComboEditor             excelCellTypeEditor; // excelCellType
	private ComboEditor             excelDefaultEditor;  // excelDefault


	public ComboComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "name", "Name:");
		nameEditor.setPosition(0);
		nameEditor.mustSetTreeItemText(true);
		nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		commentEditor = new StringEditor(this, "comment", "Comment:");
		commentEditor.setPosition(nameEditor.getControl());
		commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
		
		// variable
		variableEditor = new StringEditor(this, "variable", "Variable:");
		variableEditor.setPosition(commentEditor.getControl());
		variableEditor.setTooltipText("The variable field must start with '${' and ends with '}'.\n"+
				"\n"+
				"It allows to retreive and to set Archi components properties:\n"+
				"   ${class}   [RO] Archi's internal class\n"+
				"   ${id}   [RW] Archi's internal ID \n"+
				"   ${name}   [RW] name\n"+
				"   ${documentation}   [RW] documentation\n"+
				"   ${property:xxxx}   [RW] value of the property xxxx\n"+
				"   ${void}   [RO] does nothing\n"+
				"\n"+
				"It is possible to change the variable's scope:\n"+
				"   ${view:.....}   gets/sets the property of the view containing the Archi component rather than the component itself\n"+
				"   ${model:.....}   gets/sets the property of the model\n"+
				"   ${source:.....}   gets/sets the property of the source object (when the Archi component is a relationship)\n"+
				"   ${target:.....}   gets/sets the property of the target object (when the Archi component is a relationship)"
				);
		
	    // defaultText
        defaultTextEditor = new StringEditor(this, "default", "Default text:");
        defaultTextEditor.setPosition(variableEditor.getControl());
        defaultTextEditor.setTooltipText("Default value when the one corresponding to the variable value is empty.");
        
        // defaultText
        forceDefaultEditor = new CheckEditor(this, "forceDefault", "Force default:");
        forceDefaultEditor.setPosition(defaultTextEditor.getControl());
        forceDefaultEditor.setTooltipText("Force the default value even if the the variable value is not empty.");
        
        // values
        valuesEditor = new StringEditor(this, "values", "Values:");
        valuesEditor.setPosition(forceDefaultEditor.getControl());
        valuesEditor.setTooltipText("List of the valid values, one per line.");
        
        // editable
        editableEditor = new CheckEditor(this, "editable", "Read only:");
        editableEditor.setPosition(valuesEditor.getControl());
        editableEditor.setInverse(true);
        editableEditor.setTooltipText("Specifies if the variable value can be updated by the user.\n\nDefault: true.");
        
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
		tooltipEditor.setTooltipText("Specifies the tooltip to show when the mouse stands is over the control.\n\nDefault: none.");
		
		// whenempty
		whenEmptyEditor = new ComboEditor(this, "whenEmpty", "When empty:");
		whenEmptyEditor.setPosition(tooltipEditor.getControl());
		whenEmptyEditor.setItems(new String[] {"", "ignore", "create", "delete"});
        whenEmptyEditor.setTooltipText("Choose the plugin behaviour when a variable is left empty in the form:\n"+
                "   - ignore: do not change the property value:\n"+
                "                 - if the property does not already exist, it will not be created,\n"+
                "                 - if the propety does already exist, its value is left unmodified.\n"+
                "   - create: empty the property's value if it does already exist, or create a new one with an empty value,\n"+
                "   - delete: delete the property if it does already exist.\n"+
                "\n"+
                "Default: "+FormDialog.validWhenEmpty[0]+"."
                );
        
        // excelSheet
        excelSheetEditor = new StringEditor(this, "excelSheet", "Excel sheet:");
        excelSheetEditor.setPosition(whenEmptyEditor.getControl());
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
        excelDefaultEditor = new ComboEditor(this, "excelDefault", "Excel default:");
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
            case "comment":       commentEditor.setText((String)value); break;
            case "variable":      variableEditor.setText((String)value); break;
            case "default":       defaultTextEditor.setText((String)value); break;
            case "forcedefault":  forceDefaultEditor.setChecked((Boolean)value); break;
            case "editable":      editableEditor.setChecked((Boolean)value); break;
            case "values":        valuesEditor.setText((String[])value); break;
    		case "x":			  sizeEditor.setX((Integer)value); break;
    		case "y":			  sizeEditor.setY((Integer)value); break;
    		case "width":		  sizeEditor.setWidth((Integer)value); break;
    		case "height":		  sizeEditor.setHeight((Integer)value); break;
    		case "foreground":	  colorEditor.setForeground((String)value); break;
    		case "background":	  colorEditor.setBackground((String)value); break;
    		case "fontname":	  fontEditor.setFontName((String)value); break;
    		case "fontsize":	  fontEditor.setFontSize((Integer)value); break;
    		case "fontbold":	  fontEditor.setBold((Boolean)value); break;
    		case "fontitalic":	  fontEditor.setItalic((Boolean)value); break;
    		case "tooltip":    	  tooltipEditor.setText((String)value); break;
    		case "whenempty":     whenEmptyEditor.setText((String)value); break;
    		case "excelsheet":    excelSheetEditor.setText((String)value); break;
    		case "excelcell":	  excelCellEditor.setText((String)value); break;
    		case "excelcelltype": excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  excelDefaultEditor.setText((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
