package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FontEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class LabelComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;         // name
    private StringEditor            textEditor;			// text
	private SizeEditor              sizeEditor;         // x, y, width, height
	private ColorEditor             colorEditor;        // foreground, background
	private FontEditor				fontEditor;			// fontName, fontSize, fontBold, fontItalic
	private StringEditor            tooltipEditor;      // tooltip
	private AlignmentEditor         alignmentEditor;    // alignment
    private StringEditor            excelSheetEditor;    // excelSheet
    private StringEditor            excelCellEditor;     // excelCell
    private ComboEditor             excelCellTypeEditor; // excelCellType
    private ComboEditor             excelDefaultEditor;  // excelDefault

	public LabelComposite(Composite parent, int style) {
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
		
		// text
		textEditor = new StringEditor(this, "text", "Text:");
		textEditor.setPosition(nameEditor.getControl());
		textEditor.mustSetControlText(true);
						
		// x, y, width, height
		sizeEditor = new SizeEditor(this);
		sizeEditor.setPosition(textEditor.getControl());
		        
		// foreground, background
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
		
	      // alignement
        alignmentEditor = new AlignmentEditor(this, "Alignment:");
        alignmentEditor.setPosition(tooltipEditor.getControl());
        
        // excelSheet
        excelSheetEditor = new StringEditor(this, "excelSheet", "Excel sheet:");
        excelSheetEditor.setPosition(alignmentEditor.getControl());
        excelSheetEditor.setTooltipText("Name of the Excel sheet where the text should be exported to.\n\nIf this field is left blank, then the variable will not be exported to Excel, even if the others Excel related field are set.");
        
        // excelCell
        excelCellEditor = new StringEditor(this, "excelCell", "Excel cell:");
        excelCellEditor.setPosition(excelSheetEditor.getControl());
        excelCellEditor.setTooltipText("Adress of the Excel cell where the text should be exported to (like A3 or D14).\n\nIf the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set.");
        
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
    		case "x":    		  sizeEditor.setX((Integer)value); break;
    		case "y":    		  sizeEditor.setY((Integer)value); break;
    		case "width":    	  sizeEditor.setWidth((Integer)value); break;
    		case "height":    	  sizeEditor.setHeight((Integer)value); break;
    	    case "alignment":     alignmentEditor.setText((String)value); break;
    		case "name":    	  nameEditor.setText((String)value); break;
    		case "foreground":	  colorEditor.setForeground((String)value); break;
    		case "background":	  colorEditor.setBackround((String)value); break;
    		case "text":    	  textEditor.setText((String)value); break;
    		case "tooltip":    	  tooltipEditor.setText((String)value); break;
    		case "fontname":	  fontEditor.setFontName((String)value); break;
    		case "fontsize":	  fontEditor.setFontSize((Integer)value); break;
    		case "fontbold":	  fontEditor.setBold((Boolean)value); break;
    		case "fontitalic":	  fontEditor.setItalic((Boolean)value); break;
    		case "excelsheet":    excelSheetEditor.setText((String)value); break;
    		case "excelcell":	  excelCellEditor.setText((String)value); break;
    		case "excelcelltype": excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  excelDefaultEditor.setText((String)value); break;
    		default:			throw new RuntimeException("does not know key "+key);
    	}
    	
    }
}
