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
	private Label        lblGenerate;
	private Button       btnGenerate;
    private Label        lblFilter;
    private Button       btnFilter;
	private Label        lblGenre;
	private Button       btnAnd;
	private Button       btnOr;
	private Label        lblAttribute;
	private List<Text>   txtAttribute;
	private Label        lblOperation;
	private List<CCombo> comboOperation;
	private Label        lblValue;
	private List<Text>   txtValue;
	private List<Button> btnAdd;
	private List<Button> btnDelete;
	
	private boolean showGenerateCheckbox;
	
	private Composite    parent;
	
	public FilterEditor(Composite parent, boolean showGenerateCheckbox) {
		this.parent = parent;
		this.showGenerateCheckbox = showGenerateCheckbox;
		FormData fd;
		
		if ( showGenerateCheckbox) {
			lblGenerate = new Label(parent, SWT.NONE);
	        fd = new FormData();
	        fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
	        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
	        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
	        lblGenerate.setLayoutData(fd);
	        lblGenerate.setText("Generate lines:");
	        
	        btnGenerate = new Button(parent, SWT.CHECK);
	        fd = new FormData();
        	fd.top = new FormAttachment(lblGenerate, 0, SWT.TOP);
	        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
	        btnGenerate.setLayoutData(fd);
	        btnGenerate.addSelectionListener(FilterSelectionListener);
		}
        
		lblFilter = new Label(parent, SWT.NONE);
        fd = new FormData();
        if ( showGenerateCheckbox )
        	fd.top = new FormAttachment(lblGenerate, FormDialog.editorBorderMargin);
        else
        	fd.top = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblFilter.setLayoutData(fd);
        lblFilter.setText("Filter:");
        
        btnFilter = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblFilter, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        btnFilter.setLayoutData(fd);
        btnFilter.addSelectionListener(FilterSelectionListener);
        
		lblAttribute = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnFilter, 0);
        fd.right = new FormAttachment(50);
        lblAttribute.setLayoutData(fd);
        lblAttribute.setText("Attribute");
        
        Text attr = new Text(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblAttribute, (int)(FormDialog.editorVerticalMargin/2));
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        fd.right = new FormAttachment(lblAttribute, 0, SWT.RIGHT);
        attr.setLayoutData(fd);
        
        txtAttribute = new ArrayList<Text>();
        txtAttribute.add(attr);
        
		lblOperation = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(50, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(65, -FormDialog.editorBorderMargin);
        lblOperation.setLayoutData(fd);
        lblOperation.setText("Operation");
        
        CCombo cmb = new CCombo(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0 , SWT.CENTER);
        fd.left = new FormAttachment(lblOperation, 0, SWT.LEFT);
        fd.right = new FormAttachment(lblOperation, 0, SWT.RIGHT);
        cmb.setLayoutData(fd);
        cmb.setItems(new String[] {"exists", "not exists", "equals", "not equals", "iequals", "not iequals", "in", "not in", "iin", "not iin", "matches", "not matches"});
        cmb.addSelectionListener(operationSelectionListener);
        
        comboOperation = new ArrayList<CCombo>();
        comboOperation.add(cmb);
        
		lblValue = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(65);
        fd.right = new FormAttachment(100, -40);
        lblValue.setLayoutData(fd);
        lblValue.setText("Value");
        
        Text val = new Text(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(lblValue, 0, SWT.LEFT);
        fd.right = new FormAttachment(lblValue, 0, SWT.RIGHT);
        val.setLayoutData(fd);
        
        txtValue = new ArrayList<Text>();
        txtValue.add(val);
        
        Button add = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(100, -16);
        fd.right = new FormAttachment(100);
        add.setLayoutData(fd);
        add.setImage(FormDialog.PLUS_ICON);
        add.addSelectionListener(addSelectionListener);
        
        btnAdd = new ArrayList<Button>();
        btnAdd.add(add);
        
        Button del = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(100, -35);
        fd.right = new FormAttachment(100, -19);
        del.setLayoutData(fd);
        del.setImage(FormDialog.BIN_ICON);
        del.setVisible(false);
        
        btnDelete = new ArrayList<Button>();
        btnDelete.add(del);
        
		lblGenre = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        lblGenre.setLayoutData(fd);
        lblGenre.setText("Genre:");
		
        btnAnd = new Button(parent, SWT.RADIO);
		fd = new FormData();
        fd.top = new FormAttachment(lblGenre, FormDialog.editorVerticalMargin, SWT.CENTER);
        fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
        btnAnd.setLayoutData(fd);
        btnAnd.setSelection(true);
        btnAnd.setText("AND");
        btnAnd.addSelectionListener(genreSelectionListener);
        
        btnOr = new Button(parent, SWT.RADIO);
		fd = new FormData();
        fd.top = new FormAttachment(lblGenre, FormDialog.editorVerticalMargin, SWT.CENTER);
        fd.left = new FormAttachment(btnAnd, FormDialog.editorBorderMargin*2);
        btnOr.setLayoutData(fd);
        btnOr.setText("OR");
        btnOr.addSelectionListener(genreSelectionListener);
	}

	private SelectionListener FilterSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
	    	
	    	if ( treeItem != null ) {
	    		treeItem.setData("generate", showGenerateCheckbox ? getGenerate() : null );
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
	
	
	private SelectionListener addSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
	        
	    	// we get the index of the add button that has been selected
	    	int index = -1;
	    	
	    	for ( int i = 0; i < btnAdd.size(); ++i ) {
	    		if ( btnAdd.get(i) == e.getSource() ) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	// if we failed to get the index, then we add at the end
	    	if ( index == -1 )
	    		index = btnAdd.size()-1;
	    	
	    	// we insert new widgets after the corresponding index
	    	Text attr = new Text(parent, SWT.BORDER);
	        txtAttribute.add(index+1, attr);
	        
	        CCombo cmb = new CCombo(parent, SWT.BORDER);
	        cmb.setItems(new String[] {"exists", "not exists", "equals", "not equals", "iequals", "not iequals", "in", "not in", "iin", "not iin", "matches", "not matches"});
	        cmb.addSelectionListener(operationSelectionListener);
	        comboOperation.add(index+1, cmb);

	        Text val = new Text(parent, SWT.BORDER);
	        txtValue.add(index+1, val);
	        
	        Button add = new Button(parent, SWT.NONE);
	        add.setImage(FormDialog.PLUS_ICON);
	        add.addSelectionListener(addSelectionListener);
	        btnAdd.add(index+1, add);
	        
	        Button del = new Button(parent, SWT.NONE);
	        del.setImage(FormDialog.BIN_ICON);
	        del.addSelectionListener(delSelectionListener);
	        btnDelete.add(index+1, del);
	        
            if ( treeItem != null ) {
            	treeItem.setData("generate", showGenerateCheckbox ? getGenerate() : null );
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
	
	private SelectionListener operationSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	        TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
	        
	    	// we get the index of the add button that has been selected
	    	int index = -1;
	    	
	    	for ( int i = 0; i < comboOperation.size(); ++i ) {
	    		if ( comboOperation.get(i) == e.getSource() ) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	// if we failed to get the index, then we update the last one
	    	if ( index == -1 )
	    		index = comboOperation.size()-1;
	    	
	    	txtValue.get(index).setVisible(!FormPlugin.areEqualIgnoreCase(comboOperation.get(index).getText(), "exists"));
	    	
            if ( treeItem != null ) {
            	treeItem.setData("generate", showGenerateCheckbox ? getGenerate() : null );
            	treeItem.setData("genre", getFilter() ? getGenre() : null);
                treeItem.setData("tests", getFilter() ? getTests() : null);
            }
	    }
		
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	private SelectionListener delSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
            TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
            
	    	// we get the index of the add button that has been selected
	    	int index = -1;
	    	for ( int i = 0; i < btnDelete.size(); ++i ) {
	    		if ( btnDelete.get(i) == e.getSource() ) {
	    			index = i;
	    			break;
	    		}
	    	}
	    	
	    	// if we failed to get the index, then we delete the last one
	    	if ( index == -1 )
	    		index = btnDelete.size()-1;
	    	
	    	// we delete the widgets at the corresponding index
	        txtAttribute.remove(index).dispose();
	        comboOperation.remove(index).dispose();
	        txtValue.remove(index).dispose();
	        btnAdd.remove(index).dispose();
	        btnDelete.remove(index).dispose();
	        
            if ( treeItem != null ) {
            	treeItem.setData("generate", showGenerateCheckbox ? getGenerate() : null );
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
            TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
            
            if ( treeItem != null ) {
            	treeItem.setData("generate", showGenerateCheckbox ? getGenerate() : null );
            	treeItem.setData("genre", getFilter() ? getGenre() : null);
            }
        }
        
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
	};
	
	private void redraw() {
        // we reorganize the widgets
        FormData fd;
    	for ( int i = 0; i < btnAdd.size(); ++i ) {
            fd = new FormData();
            fd.top = new FormAttachment(i==0 ? lblAttribute : txtAttribute.get(i-1), (int)(FormDialog.editorVerticalMargin/2));
            fd.left = new FormAttachment(0, FormDialog.editorLeftposition);
            fd.right = new FormAttachment(lblAttribute, 0, SWT.RIGHT);
            txtAttribute.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(lblOperation, 0, SWT.LEFT);
            fd.right = new FormAttachment(lblOperation, 0, SWT.RIGHT);
            comboOperation.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(lblValue, 0, SWT.LEFT);
            fd.right = new FormAttachment(lblValue, 0, SWT.RIGHT);
            txtValue.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(100, -16);
            fd.right = new FormAttachment(100);
            btnAdd.get(i).setLayoutData(fd);
            
            fd = new FormData();
            fd.top = new FormAttachment(txtAttribute.get(i), 0, SWT.CENTER);
            fd.left = new FormAttachment(100, -35);
            fd.right = new FormAttachment(100, -19);
            btnDelete.get(i).setLayoutData(fd);
            if ( i == 0 ) btnDelete.get(i).setVisible(false);
        }
    	
        fd = new FormData();
        fd.top = new FormAttachment(txtAttribute.get(txtAttribute.size()-1), FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        lblGenre.setLayoutData(fd);
        
        boolean   isGenerated = getGenerate();
        boolean   showFilter = getFilter();
        
        lblFilter.setVisible(isGenerated);
        btnFilter.setVisible(isGenerated);
        
        lblGenre.setVisible(isGenerated && showFilter);
        btnAnd.setVisible(isGenerated && showFilter);
        btnOr.setVisible(isGenerated && showFilter);
        lblAttribute.setVisible(isGenerated && showFilter);
        lblOperation.setVisible(isGenerated && showFilter);
        lblValue.setVisible(isGenerated && showFilter);
        for ( Text txt:   txtAttribute   ) txt.setVisible(isGenerated && showFilter);
        for ( CCombo cmb: comboOperation ) cmb.setVisible(isGenerated && showFilter);
        for ( Text txt:   txtValue       ) txt.setVisible(isGenerated && showFilter);
        for ( Button add: btnAdd         ) add.setVisible(isGenerated && showFilter);
        for ( Button del: btnDelete      ) if ( (isGenerated == false && showFilter == false) || del!=btnDelete.get(0) ) del.setVisible(isGenerated && showFilter);
        
        parent.layout();
        ((ScrolledComposite)parent.getParent()).setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        lblGenerate.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormDialog.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormDialog.editorBorderMargin);
        fd.right = new FormAttachment(0, FormDialog.editorLeftposition);
        if ( showGenerateCheckbox )
        	lblGenerate.setLayoutData(fd);
        else
        	lblFilter.setLayoutData(fd);
	}
	
	public Label getControl() {
		return lblGenre;
	}
	
    public void setGenerate(Boolean checked) {
    	if ( showGenerateCheckbox )
    		btnGenerate.setSelection(checked!=null && checked);
    	redraw();
    }
	
    public boolean getGenerate() {
    	return showGenerateCheckbox ? btnGenerate.getSelection() : true;
    }
    
    public boolean getFilter() {
        return btnFilter.getSelection();
    }
    
    public void setGenre(String genre) {
   		btnAnd.setSelection(FormPlugin.areEqualIgnoreCase(genre, "and"));
   		btnOr.setSelection(!FormPlugin.areEqualIgnoreCase(genre, "and"));
    }
	
    public String getGenre() {
    	if ( getTests() == null )
    		return null;
    	return btnAnd.getSelection() ? "AND" : "OR";
    }
    
    public void setTests(List<Map<String, String>> tests) {
        if ( tests == null || tests.size() == 0 ) {
    	    btnFilter.setSelection(false);
            
            // we remove all the widgets
            for ( int i = txtAttribute.size(); i > 0; --i ) {
                btnDelete.get(i-1).notifyListeners(SWT.Selection, new Event());
            }
            txtAttribute.get(0).setText("");
            comboOperation.get(0).setText("");
            txtValue.get(0).setText("");
    	} else {
        	int nbTests = tests.size();
        	
        	// we add widgets if we miss some
        	for ( int i = txtAttribute.size(); i < nbTests; ++i ) {
        		btnAdd.get(i-1).notifyListeners(SWT.Selection, new Event());
        	}
        	
        	// we remove widgets if we've got too much of them
        	for ( int i = txtAttribute.size(); i > nbTests; --i ) {
        		btnDelete.get(i-1).notifyListeners(SWT.Selection, new Event());
        	}
        	
        	// we fill the widgets' text
        	for ( int i = 0; i < nbTests; ++i ) {
        		String value = tests.get(i).get("attribute");
        	    txtAttribute.get(i).setText(value==null ? "" : value);
        	    
        	    value = tests.get(i).get("operation");
        		comboOperation.get(i).setText(value==null ? "" : value);
        		
        		value = tests.get(i).get("value");
        		txtValue.get(i).setText(value==null ? "" : value);
        	}
        	
            btnFilter.setSelection(true);
            
            TreeItem  treeItem = (TreeItem)parent.getData("treeItem");
            
            if ( treeItem != null ) {
            	treeItem.setData("generate", showGenerateCheckbox ? getGenerate() : null );
            	treeItem.setData("genre", getFilter() ? getGenre() : null);
                treeItem.setData("tests", getFilter() ? getTests() : null);
            }
    	}
        
        redraw();
    }
	
    public List<Map<String, String>> getTests() {
    	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    	
    	for ( int i = 0; i < txtAttribute.size(); ++i ) {
    		if ( !FormPlugin.isEmpty(txtAttribute.get(i).getText()) || !FormPlugin.isEmpty(comboOperation.get(i).getText()) || !FormPlugin.isEmpty(txtValue.get(i).getText()) ) {
    			Map<String, String> map = new HashMap<String, String>();
    		
	    		map.put("attribute", txtAttribute.get(i).getText());
	    		map.put("operation", comboOperation.get(i).getText());
	    		if ( !FormPlugin.areEqualIgnoreCase(comboOperation.get(i).getText(), "exists") )
	    			map.put("value", txtValue.get(i).getText());
	    		
	    		list.add(map);
    		}
    	}
    	
    	if ( list.size() == 0 )
    		return null;
    	return list;
    }
}
