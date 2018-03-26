package org.archicontribs.form.composites;

import org.archicontribs.form.editors.AlignmentEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.ImageEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ImageComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
	private StringEditor            commentEditor;       // comment
    private ImageEditor             imageEditor;		 // image
	private SizeEditor              sizeEditor;          // x, y, width, height
	private ColorEditor             colorEditor;         // foreground, background
	private StringEditor            tooltipEditor;       // tooltip
	private AlignmentEditor         alignmentEditor;     // alignment
    private StringEditor            excelSheetEditor;    // excelSheet
    private StringEditor            excelCellEditor;     // excelCell
    private ComboEditor             excelCellTypeEditor; // excelCellType
    private ComboEditor             excelDefaultEditor;  // excelDefault

	public ImageComposite(Composite parent, int style) {
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
		
		// text
		this.imageEditor = new ImageEditor(this, "image", "Image:");
		this.imageEditor.setPosition(this.commentEditor.getControl());
		this.imageEditor.setTooltipText("You may enter an image filename or its content (but not both). If you specify both, the content will take precedence over the filename.");
						
		// x, y, width, height
		this.sizeEditor = new SizeEditor(this);
		this.sizeEditor.setPosition(this.imageEditor.getControl());
		        
		// foreground, background
		this.colorEditor = new ColorEditor(this, "Color:");
		this.colorEditor.setPosition(this.sizeEditor.getControl());
		
		// tooltip
		this.tooltipEditor = new StringEditor(this, "tooltip", "Tooltip:");
		this.tooltipEditor.setPosition(this.colorEditor.getControl());
		this.tooltipEditor.mustSetControlTolltip(true);
		this.tooltipEditor.setTooltipText("Specifies the tooltip to show when the mouse stands is over the control.\n\nDefault: none.");
		
	      // alignement
        this.alignmentEditor = new AlignmentEditor(this, "Alignment:");
        this.alignmentEditor.setPosition(this.tooltipEditor.getControl());
        
        // excelSheet
        this.excelSheetEditor = new StringEditor(this, "excelSheet", "Excel sheet:");
        this.excelSheetEditor.setPosition(this.alignmentEditor.getControl());
        this.excelSheetEditor.setTooltipText("Name of the Excel sheet where the text should be exported to.\n\nIf this field is left blank, then the variable will not be exported to Excel, even if the others Excel related field are set.");
        
        // excelCell
        this.excelCellEditor = new StringEditor(this, "excelCell", "Excel cell:");
        this.excelCellEditor.setPosition(this.excelSheetEditor.getControl());
        this.excelCellEditor.setTooltipText("Adress of the Excel cell where the text should be exported to (like A3 or D14).\n\nIf the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set.");
        
        // excelCellType
        this.excelCellTypeEditor = new ComboEditor(this, "excelType", "Excel type:");
        this.excelCellTypeEditor.setPosition(this.excelCellEditor.getControl());
        this.excelCellTypeEditor.setItems(new String[] {"", "string", "boolean", "numeric", "formula"});
        this.excelCellTypeEditor.setTooltipText("Type of the Excel cell.\n\nDefault: string");
        
        // excelDefault
        this.excelDefaultEditor = new ComboEditor(this, "excelDefault", "Excel default:");
        this.excelDefaultEditor.setPosition(this.excelCellTypeEditor.getControl());
        this.excelDefaultEditor.setItems(new String[] {"", "blank", "zero", "delete"});
        this.excelDefaultEditor.setTooltipText("Behaviour of the plugin when exporting an empty value:\n"+
                "   - blank : a blank cell will be created (ie a cell with no content)\n"+
                "   - zero : a cell with a zero value in it:\n"+
                "                - 0 for numeric cells\n"+
                "                - empty string for string and formula cells\n"+
                "                - false for boolean cells\n"+
                "   - delete : the cell will be deleted.\n"+
                "\n"+
                "Default: blank");
	}
	
    @Override
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":    	  this.nameEditor.setText((String)value); break;
            case "comment":       this.commentEditor.setText((String)value); break;
    		case "x":    		  this.sizeEditor.setX((Integer)value); break;
    		case "y":    		  this.sizeEditor.setY((Integer)value); break;
    		case "width":    	  this.sizeEditor.setWidth((Integer)value); break;
    		case "height":    	  this.sizeEditor.setHeight((Integer)value); break;
    	    case "alignment":     this.alignmentEditor.setText((String)value); break;
    		case "foreground":	  this.colorEditor.setForeground((String)value); break;
    		case "background":	  this.colorEditor.setBackground((String)value); break;
    		case "image":    	  this.imageEditor.setImage((String)value); break;
    		case "content":    	  this.imageEditor.setContent((String)value); break;
    		case "scale":         this.imageEditor.setScale((Boolean)value); break;
    		case "tooltip":    	  this.tooltipEditor.setText((String)value); break;
    		case "excelsheet":    this.excelSheetEditor.setText((String)value); break;
    		case "excelcell":	  this.excelCellEditor.setText((String)value); break;
    		case "excelcelltype": this.excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  this.excelDefaultEditor.setText((String)value); break;
    		default:			throw new RuntimeException("does not know key "+key);
    	}
    	
    }
}
