package org.archicontribs.form.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archicontribs.form.FormGraphicalEditor;
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
import org.eclipse.swt.widgets.Widget;

public class FilterEditor {
	private Label        lblGenerate;
	private Button       btnGenerate;
    private Label        lblFilter;
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
	
	private Composite    parent;
	
	public FilterEditor(Composite parent) {
		this.parent = parent;
		
		lblGenerate = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblGenerate.setLayoutData(fd);
        lblGenerate.setText("Generate lines from content:");
        
        btnGenerate = new Button(parent, SWT.CHECK);
        fd = new FormData();
        fd.top = new FormAttachment(lblGenerate, 0, SWT.TOP);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        btnGenerate.setLayoutData(fd);
        btnGenerate.addSelectionListener(generateSelectionListener);
		
		lblFilter = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblGenerate, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblFilter.setLayoutData(fd);
        lblFilter.setText("Filter:");
        
		lblAttribute = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        fd.right = new FormAttachment(50);
        lblAttribute.setLayoutData(fd);
        lblAttribute.setText("Attribute");
        
        Text attr = new Text(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblAttribute, (int)(FormGraphicalEditor.editorVerticalMargin/2));
        fd.left = new FormAttachment(lblAttribute, 0, SWT.LEFT);
        fd.right = new FormAttachment(lblAttribute, 0, SWT.RIGHT);
        attr.setLayoutData(fd);
        
        txtAttribute = new ArrayList<Text>();
        txtAttribute.add(attr);
        
		lblOperation = new Label(parent, SWT.CENTER);
        fd = new FormData();
        fd.top = new FormAttachment(lblFilter, 0, SWT.CENTER);
        fd.left = new FormAttachment(50, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(65, -FormGraphicalEditor.editorBorderMargin);
        lblOperation.setLayoutData(fd);
        lblOperation.setText("Operation");
        
        CCombo cmb = new CCombo(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0 , SWT.CENTER);
        fd.left = new FormAttachment(lblOperation, 0, SWT.LEFT);
        fd.right = new FormAttachment(lblOperation, 0, SWT.RIGHT);
        cmb.setLayoutData(fd);
        cmb.setItems(new String[] {"exists", "equals", "iequals", "in", "iin", "matches"});
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
        add.setImage(FormGraphicalEditor.PLUS_ICON);
        add.addSelectionListener(addSelectionListener);
        
        btnAdd = new ArrayList<Button>();
        btnAdd.add(add);
        
        Button del = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, 0, SWT.CENTER);
        fd.left = new FormAttachment(100, -35);
        fd.right = new FormAttachment(100, -19);
        del.setLayoutData(fd);
        del.setImage(FormGraphicalEditor.BIN_ICON);
        del.setVisible(false);
        
        btnDelete = new ArrayList<Button>();
        btnDelete.add(del);
        
		lblGenre = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(attr, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        lblGenre.setLayoutData(fd);
        lblGenre.setText("Genre:");
		
        btnAnd = new Button(parent, SWT.RADIO);
		fd = new FormData();
        fd.top = new FormAttachment(lblGenre, FormGraphicalEditor.editorVerticalMargin, SWT.CENTER);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        btnAnd.setLayoutData(fd);
        btnAnd.setSelection(true);
        btnAnd.setText("AND");
        
        btnOr = new Button(parent, SWT.RADIO);
		fd = new FormData();
        fd.top = new FormAttachment(lblGenre, FormGraphicalEditor.editorVerticalMargin, SWT.CENTER);
        fd.left = new FormAttachment(btnAnd, FormGraphicalEditor.editorBorderMargin*2);
        btnOr.setLayoutData(fd);
        btnOr.setText("OR");
	}

	private SelectionListener generateSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
	    	Widget widget = (Widget)parent.getData("control");
	    	boolean generate = getGenerate();
	    	
	    	if ( widget != null ) {
	    		widget.setData("generate", generate);
	    	}
	    	
	    	lblFilter.setVisible(generate);
	    	lblGenre.setVisible(generate);
	    	btnAnd.setVisible(generate);
	    	btnOr.setVisible(generate);
	    	lblAttribute.setVisible(generate);
	    	lblOperation.setVisible(generate);
	    	lblValue.setVisible(generate);
	    	for ( Text txt:   txtAttribute   ) txt.setVisible(generate);
	    	for ( CCombo cmb: comboOperation ) cmb.setVisible(generate);
	    	for ( Text txt:   txtValue       ) txt.setVisible(generate);
	    	for ( Button add: btnAdd         ) add.setVisible(generate);
	    	for ( Button del: btnDelete      ) if ( generate == false || del!=btnDelete.get(0) ) del.setVisible(generate);
	    }
	
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	
	private SelectionListener addSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
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
	        cmb.setItems(new String[] {"exists", "equals", "iequals", "in", "iin", "matches"});
	        comboOperation.add(index+1, cmb);

	        Text val = new Text(parent, SWT.BORDER);
	        txtValue.add(index+1, val);
	        
	        Button add = new Button(parent, SWT.NONE);
	        add.setImage(FormGraphicalEditor.PLUS_ICON);
	        add.addSelectionListener(addSelectionListener);
	        btnAdd.add(index+1, add);
	        
	        Button del = new Button(parent, SWT.NONE);
	        del.setImage(FormGraphicalEditor.BIN_ICON);
	        del.addSelectionListener(delSelectionListener);
	        btnDelete.add(index+1, del);

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
	    }
		
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	private SelectionListener delSelectionListener = new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
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
	    	
	        redraw();
	    }
	
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	        widgetSelected(e);
	    }
	};
	
	private void redraw() {
        // we reorganize the widgets
        FormData fd;
    	for ( int i = 0; i < btnAdd.size(); ++i ) {
            fd = new FormData();
            fd.top = new FormAttachment(i==0 ? lblAttribute : txtAttribute.get(i-1), (int)(FormGraphicalEditor.editorVerticalMargin/2));
            fd.left = new FormAttachment(lblAttribute, 0, SWT.LEFT);
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
        fd.top = new FormAttachment(txtAttribute.get(txtAttribute.size()-1), FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        lblGenre.setLayoutData(fd);
        
        parent.layout();
        ((ScrolledComposite)parent.getParent()).setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblGenerate.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(0, FormGraphicalEditor.editorLeftposition);
        lblGenerate.setLayoutData(fd);
	}
	
	public Label getControl() {
		return lblGenre;
	}
	
    public void setGenerate(Boolean checked) {
    	btnGenerate.setSelection(checked!=null && checked);
    }
	
    public boolean getGenerate() {
    	return btnGenerate.getSelection();
    }
    
    public void setGenre(String genre) {
   		btnAnd.setSelection(FormPlugin.areEqualIgnoreCase(genre, "and"));
   		btnOr.setSelection(!FormPlugin.areEqualIgnoreCase(genre, "and"));
    }
	
    public String getGenre() {
    	return btnAnd.getSelection() ? "AND" : "OR";
    }
    
    public void setTests(List<Map<String, String>> tests) {
    	int nbTests;
    	if ( tests == null || tests.size() == 0 )
    		nbTests = 1;
    	else
    		nbTests = tests.size();
    	
    	// we add widgets if we miss some
    	for ( int i = txtAttribute.size(); i < nbTests; ++i ) {
    		btnAdd.get(i-1).notifyListeners(SWT.Selection, new Event());
    	}
    	
    	// we remove widgets if we've got too much of them
    	for ( int i = nbTests; i > txtAttribute.size(); --i ) {
    		btnDelete.get(i-1).notifyListeners(SWT.Selection, new Event());
    	}
    	
    	// we fill the widgets' text
    	for ( int i = 0; i < nbTests; ++i ) {
    		txtAttribute.get(i).setText(tests.get(i).get("attribute")==null ? "" : tests.get(i).get("attribute"));
    		comboOperation.get(i).setText(tests.get(i).get("operation")==null ? "" : tests.get(i).get("operation"));
    		txtValue.get(i).setText(tests.get(i).get("value")==null ? "" : tests.get(i).get("value"));
    	}
    }
	
    public List<Map<String, String>> getTests() {
    	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    	
    	for ( int i = 0; i < txtAttribute.size(); ++i ) {
    		Map<String, String> map = new HashMap<String, String>();
    		
    		map.put("attribute", txtAttribute.get(i).getText());
    		map.put("operation", comboOperation.get(i).getText());
    		if ( !FormPlugin.areEqualIgnoreCase(comboOperation.get(i).getText(), "exists") )
    			map.put("value", txtValue.get(i).getText());
    		
    		list.add(map);
    	}
    	
    	return list;
    }
}
