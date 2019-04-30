package org.archicontribs.form.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

public class FilterEditor {
	Label        lblGenerate;
	Button       btnGenerate;
    Label        lblFilter;
    Button       btnFilter;
	Label        lblGenre;
	Button       btnAnd;
	Button       btnOr;
	Label        lblAttribute;
	List<Text>   txtAttribute;
	Label        lblOperation;
	List<CCombo> comboOperation;
	Label        lblValue;
	List<Text>   txtValue;
	List<Button> btnAdd;
	List<Button> btnDelete;
	
	boolean showGenerateCheckbox;
	
	Composite    parent;
	
	public FilterEditor(Composite parent, boolean showGenerateCheckbox) {
		this.parent = parent;
		this.showGenerateCheckbox = showGenerateCheckbox;
		FormData fd;
		
		if ( showGenerateCheckbox) {
			this.lblGenerate = new Label(parent, SWT.NONE);
	        fd = new FormData();
	        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
	        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
	        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
	        this.lblGenerate.setLayoutData(fd);
	        this.lblGenerate.setText("Generate lines:");
	        
	        this.btnGenerate = new Button(parent, SWT.CHECK);
	        fd = new FormData();
        	fd.top = new FormAttachment(this.lblGenerate, 0, SWT.TOP);
	        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
	        this.btnGenerate.setLayoutData(fd);
	        this.btnGenerate.addSelectionListener(this.FilterSelectionListener);
		}
        
		this.lblFilter = new Label(parent, SWT.NONE);
        fd = new FormData();
        if ( showGenerateCheckbox )
        	fd.top = new FormAttachment(this.lblGenerate, FormDialog.editorBorderMargin);
        else
        	fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblFilter.setLayoutData(fd);
        this.lblFilter.setText("Filter:");
        
        this.btnFilter = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFilter, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        this.btnFilter.setLayoutData(fd);
        this.btnFilter.addSelectionListener(this.FilterSelectionListener);
        
		this.lblAttribute = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.btnFilter, 0);
        fd.right = new FormAttachment(50);
        this.lblAttribute.setLayoutData(fd);
        this.lblAttribute.setText("Attribute");
        
        Text attr = new Text(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblAttribute, FormDialog.editorVerticalMargin/2);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(this.lblAttribute, 0, SWT.RIGHT);
        attr.setLayoutData(fd);
        
        this.txtAttribute = new ArrayList<Text>();
        this.txtAttribute.add(attr);
        
		this.lblOperation = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(50, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(65, -FormDialog.editorBorderMargin);
        this.lblOperation.setLayoutData(fd);
        this.lblOperation.setText("Operation");
        
        CCombo cmb = new CCombo(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0 , SWT.CENTER);
        fd.left = new FormAttachment(this.lblOperation, 0, SWT.LEFT);
        fd.right = new FormAttachment(this.lblOperation, 0, SWT.RIGHT);
        cmb.setLayoutData(fd);
        cmb.setItems(new String[] {"exists", "not exists", "equals", "not equals", "iequals", "not iequals", "in", "not in", "iin", "not iin", "matches", "not matches"});
        cmb.addSelectionListener(this.operationSelectionListener);
        
        this.comboOperation = new ArrayList<CCombo>();
        this.comboOperation.add(cmb);
        
		this.lblValue = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(this.lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(65);
        fd.right = new FormAttachment(100, -40);
        this.lblValue.setLayoutData(fd);
        this.lblValue.setText("Value");
        
        Text val = new Text(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(this.lblValue, 0, SWT.LEFT);
        fd.right = new FormAttachment(this.lblValue, 0, SWT.RIGHT);
        val.setLayoutData(fd);
        
        this.txtValue = new ArrayList<Text>();
        this.txtValue.add(val);
        
        Button add = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(100, -16);
        fd.right = new FormAttachment(100);
        add.setLayoutData(fd);
        add.setImage(FormDialog.PLUS_ICON);
        add.addSelectionListener(this.addSelectionListener);
        
        this.btnAdd = new ArrayList<Button>();
        this.btnAdd.add(add);
        
        Button del = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(100, -35);
        fd.right = new FormAttachment(100, -19);
        del.setLayoutData(fd);
        del.setImage(FormDialog.BIN_ICON);
        del.setVisible(false);
        
        this.btnDelete = new ArrayList<Button>();
        this.btnDelete.add(del);
        
		this.lblGenre = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        this.lblGenre.setLayoutData(fd);
        this.lblGenre.setText("Genre:");
		
        this.btnAnd = new Button(parent, SWT.RADIO);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblGenre, FormDialog.editorVerticalMargin, SWT.CENTER);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        this.btnAnd.setLayoutData(fd);
        this.btnAnd.setSelection(true);
        this.btnAnd.setText("AND");
        this.btnAnd.addSelectionListener(this.genreSelectionListener);
        
        this.btnOr = new Button(parent, SWT.RADIO);
		fd = new FormData();
        fd.top = new FormAttachment(this.lblGenre, FormDialog.editorVerticalMargin, SWT.CENTER);
        fd.left = new FormAttachment(this.btnAnd, FormDialog.editorBorderMargin*2);
        this.btnOr.setLayoutData(fd);
        this.btnOr.setText("OR");
        this.btnOr.addSelectionListener(this.genreSelectionListener);
	}

	private SelectionListener FilterSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)FilterEditor.this.parent.getData("treeItem");
	    	
	    	if ( treeItem != null ) {
	    		treeItem.setData("generate", getFilter() ? getGenerate() : null);
	    		treeItem.setData("genre", getFilter() ? getGenre() : null);
	    	    treeItem.setData("tests", getFilter() ? getTests() : null);
	    	}
	    	
	    	redraw();
	    }
	
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	
	SelectionListener addSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)FilterEditor.this.parent.getData("treeItem");
	        
	    	// we get the index of the add button that has been selected
	    	int index = -1;
	    	
	    	for ( int i = 0; i < FilterEditor.this.btnAdd.size(); ++i ) {
	    		if ( FilterEditor.this.btnAdd.get(i) == e.getSource() ) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	// if we failed to get the index, then we add at the end
	    	if ( index == -1 )
	    		index = FilterEditor.this.btnAdd.size()-1;
	    	
	    	// we insert new widgets after the corresponding index
	    	Text attr = new Text(FilterEditor.this.parent, SWT.BORDER);
	        FilterEditor.this.txtAttribute.add(index+1, attr);
	        
	        CCombo cmb = new CCombo(FilterEditor.this.parent, SWT.BORDER);
	        cmb.setItems(new String[] {"exists", "not exists", "equals", "not equals", "iequals", "not iequals", "in", "not in", "iin", "not iin", "matches", "not matches"});
	        cmb.addSelectionListener(FilterEditor.this.operationSelectionListener);
	        FilterEditor.this.comboOperation.add(index+1, cmb);

	        Text val = new Text(FilterEditor.this.parent, SWT.BORDER);
	        FilterEditor.this.txtValue.add(index+1, val);
	        
	        Button add = new Button(FilterEditor.this.parent, SWT.NONE);
	        add.setImage(FormDialog.PLUS_ICON);
	        add.addSelectionListener(FilterEditor.this.addSelectionListener);
	        FilterEditor.this.btnAdd.add(index+1, add);
	        
	        Button del = new Button(FilterEditor.this.parent, SWT.NONE);
	        del.setImage(FormDialog.BIN_ICON);
	        del.addSelectionListener(FilterEditor.this.delSelectionListener);
	        FilterEditor.this.btnDelete.add(index+1, del);
	        
            if ( treeItem != null ) {
            	treeItem.setData("genre", getFilter() ? getGenre() : null);
                treeItem.setData("tests", getFilter() ? getTests() : null);
            }

	        redraw();
	    }
	
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	SelectionListener operationSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)FilterEditor.this.parent.getData("treeItem");
	        
	    	// we get the index of the add button that has been selected
	    	int index = -1;
	    	
	    	for ( int i = 0; i < FilterEditor.this.comboOperation.size(); ++i ) {
	    		if ( FilterEditor.this.comboOperation.get(i) == e.getSource() ) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	// if we failed to get the index, then we update the last one
	    	if ( index == -1 )
	    		index = FilterEditor.this.comboOperation.size()-1;
	    	
	    	FilterEditor.this.txtValue.get(index).setVisible(!FormPlugin.areEqualIgnoreCase(FilterEditor.this.comboOperation.get(index).getText(), "exists"));
	    	
            if ( treeItem != null ) {
            	treeItem.setData("genre", getFilter() ? getGenre() : null);
                treeItem.setData("tests", getFilter() ? getTests() : null);
            }
	    }
		
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	SelectionListener delSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
            TreeItem  treeItem = (TreeItem)FilterEditor.this.parent.getData("treeItem");
            
	    	// we get the index of the add button that has been selected
	    	int index = -1;
	    	for ( int i = 0; i < FilterEditor.this.btnDelete.size(); ++i ) {
	    		if ( FilterEditor.this.btnDelete.get(i) == e.getSource() ) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	// if we failed to get the index, then we delete the last one
	    	if ( index == -1 )
	    		index = FilterEditor.this.btnDelete.size()-1;
	    	
	    	// we delete the widgets at the corresponding index
	        FilterEditor.this.txtAttribute.remove(index).dispose();
	        FilterEditor.this.comboOperation.remove(index).dispose();
	        FilterEditor.this.txtValue.remove(index).dispose();
	        FilterEditor.this.btnAdd.remove(index).dispose();
	        FilterEditor.this.btnDelete.remove(index).dispose();
	        
            if ( treeItem != null ) {
           		treeItem.setData("genre", getFilter() ? getGenre() : null);
                treeItem.setData("tests", getFilter() ? getTests() : null);
            }
	    	
	        redraw();
	    }
	
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	private SelectionListener genreSelectionListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            TreeItem  treeItem = (TreeItem)FilterEditor.this.parent.getData("treeItem");
            
            if ( treeItem != null ) {
            	treeItem.setData("genre", getFilter() ? getGenre() : null);
            }
        }
        
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
	};
	
	void redraw() {
        // we reorganize the widgets
        FormData fd;
    	for ( int i = 0; i < this.btnAdd.size(); ++i ) {
            fd = new FormData();
            fd.top = new FormAttachment(i==0 ? this.lblAttribute : this.txtAttribute.get(i-1), FormDialog.editorVerticalMargin/2);
            fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
            fd.right = new FormAttachment(this.lblAttribute, 0, SWT.RIGHT);
            this.txtAttribute.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(this.txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(this.lblOperation, 0, SWT.LEFT);
            fd.right = new FormAttachment(this.lblOperation, 0, SWT.RIGHT);
            this.comboOperation.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(this.txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(this.lblValue, 0, SWT.LEFT);
            fd.right = new FormAttachment(this.lblValue, 0, SWT.RIGHT);
            this.txtValue.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(this.txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(100, -16);
            fd.right = new FormAttachment(100);
            this.btnAdd.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(this.txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(100, -35);
            fd.right = new FormAttachment(100, -19);
            this.btnDelete.get(i).setLayoutData(fd);
            if ( i == 0 ) this.btnDelete.get(i).setVisible(false);
        }
    	
        fd = new FormData();
        fd.top = new FormAttachment(this.txtAttribute.get(this.txtAttribute.size()-1), FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        this.lblGenre.setLayoutData(fd);
        
        boolean   isGenerated = getGenerate();
        boolean   showFilter = getFilter();
        
        this.lblFilter.setVisible(isGenerated);
        this.btnFilter.setVisible(isGenerated);
        
        this.lblGenre.setVisible(isGenerated && showFilter);
        this.btnAnd.setVisible(isGenerated && showFilter);
        this.btnOr.setVisible(isGenerated && showFilter);
        this.lblAttribute.setVisible(isGenerated && showFilter);
        this.lblOperation.setVisible(isGenerated && showFilter);
        this.lblValue.setVisible(isGenerated && showFilter);
        for ( Text txt:   this.txtAttribute   ) txt.setVisible(isGenerated && showFilter);
        for ( CCombo cmb: this.comboOperation ) cmb.setVisible(isGenerated && showFilter);
        for ( Text txt:   this.txtValue       ) txt.setVisible(isGenerated && showFilter);
        for ( Button add: this.btnAdd         ) add.setVisible(isGenerated && showFilter);
        for ( Button del: this.btnDelete      ) if ( (isGenerated == false && showFilter == false) || del!=this.btnDelete.get(0) ) del.setVisible(isGenerated && showFilter);
        
        this.parent.layout();
        ((ScrolledComposite)this.parent.getParent()).setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        this.lblGenerate.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        if ( this.showGenerateCheckbox )
        	this.lblGenerate.setLayoutData(fd);
        else
        	this.lblFilter.setLayoutData(fd);
	}
	
	public Label getControl() {
		return this.lblGenre;
	}
	
    public void setGenerate(Boolean checked) {
    	if ( this.showGenerateCheckbox )
    		this.btnGenerate.setSelection(checked!=null && checked);
    	redraw();
    }
	
    public boolean getGenerate() {
    	return this.showGenerateCheckbox ? this.btnGenerate.getSelection() : true;
    }
    
    public boolean getFilter() {
        return this.btnFilter.getSelection();
    }
    
    public void setGenre(String genre) {
   		this.btnAnd.setSelection(FormPlugin.areEqualIgnoreCase(genre, "and"));
   		this.btnOr.setSelection(!FormPlugin.areEqualIgnoreCase(genre, "and"));
    }
	
    public String getGenre() {
    	if ( getTests() == null )
    		return null;
    	return this.btnAnd.getSelection() ? "AND" : "OR";
    }
    
    public void setTests(List<Map<String, String>> tests) {
        if ( tests == null || tests.size() == 0 ) {
    	    this.btnFilter.setSelection(false);
            
            // we remove all the widgets
            for ( int i = this.txtAttribute.size(); i > 0; --i ) {
                this.btnDelete.get(i-1).notifyListeners(SWT.Selection, new Event());
            }
            this.txtAttribute.get(0).setText("");
            this.comboOperation.get(0).setText("");
            this.txtValue.get(0).setText("");
    	} else {
    		this.btnFilter.setSelection(true);
        	int nbTests = tests.size();
        	
        	// we add widgets if we miss some
        	for ( int i = this.txtAttribute.size(); i < nbTests; ++i ) {
        		this.btnAdd.get(i-1).notifyListeners(SWT.Selection, new Event());
        	}
        	
        	// we remove widgets if we've got too much of them
        	for ( int i = this.txtAttribute.size(); i > nbTests; --i ) {
        		this.btnDelete.get(i-1).notifyListeners(SWT.Selection, new Event());
        	}
        	
        	// we fill the widgets' text
        	for ( int i = 0; i < nbTests; ++i ) {
        		String value = tests.get(i).get("attribute");
        	    this.txtAttribute.get(i).setText(value==null ? "" : value);
        	    
        	    value = tests.get(i).get("operation");
        		this.comboOperation.get(i).setText(value==null ? "" : value);
        		
        		value = tests.get(i).get("value");
        		this.txtValue.get(i).setText(value==null ? "" : value);
        	}
        	
            this.btnFilter.setSelection(true);
            
            TreeItem  treeItem = (TreeItem)this.parent.getData("treeItem");
            
            if ( treeItem != null ) {
            	//treeItem.setData("genre", getFilter() ? getGenre() : null);
                treeItem.setData("tests", getFilter() ? getTests() : null);
            }
    	}
        
        redraw();
    }
	
    public List<Map<String, String>> getTests() {
    	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    	
    	for ( int i = 0; i < this.txtAttribute.size(); ++i ) {
    		if ( !FormPlugin.isEmpty(this.txtAttribute.get(i).getText()) || !FormPlugin.isEmpty(this.comboOperation.get(i).getText()) || !FormPlugin.isEmpty(this.txtValue.get(i).getText()) ) {
    			Map<String, String> map = new HashMap<String, String>();
    		
	    		map.put("attribute", this.txtAttribute.get(i).getText());
	    		map.put("operation", this.comboOperation.get(i).getText());
	    		if ( !FormPlugin.areEqualIgnoreCase(this.comboOperation.get(i).getText(), "exists") )
	    			map.put("value", this.txtValue.get(i).getText());
	    		
	    		list.add(map);
    		}
    	}
    	
    	if ( list.size() == 0 )
    		return null;
    	return list;
    }
}
