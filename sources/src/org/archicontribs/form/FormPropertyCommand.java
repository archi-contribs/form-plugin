package org.archicontribs.form;

import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * This class allows to store updated properties on EObjects. It is used by the Eclipse commands mechanism to allow undo / redo.
 * 
 * @author Herve Jouin
 *
 */
public class FormPropertyCommand extends Command {
    protected IProperties eObject = null;
	protected IProperty property = null;
	protected String propertyName = null;
	protected String propertyValue = null;
	protected String oldValue = null;
	protected int oldIndex = -1;
		
	/*
	 * Creates a new property
	 */
	public FormPropertyCommand(String label, IProperties eObject, String propertyName, String propertyValue) {
	    setLabel(label);
	    this.eObject = eObject;
	    this.propertyName = propertyName;
	    this.propertyValue = propertyValue;
	}
	
	/*
	 * Updates an existing property
	 */
	public FormPropertyCommand(String label, IProperties eObject, IProperty property, String propertyValue) {
		setLabel(label);
	    this.eObject = eObject;
		this.property = property;
		this.propertyValue = propertyValue;
		this.oldValue = property.getValue();
	}
	
    @Override
    public void execute() {
        if ( property == null ) {
            // if the property is null, this means that the property does not exist yet.
            // Therefore, a new property needs to be created
            property = IArchimateFactory.eINSTANCE.createProperty();
            property.setKey(propertyName);
            property.setValue(propertyValue);
            eObject.getProperties().add(property);
        } else {
            // Else, we just have to update the existing property
            if ( propertyValue == null ) {
                // if the value is null, then we remove the property
                oldIndex = eObject.getProperties().indexOf(property);
                eObject.getProperties().remove(property);
            } else {
                property.setValue(propertyValue);
            }
        }
    }
    
    @Override
    public void undo() {
        if (propertyName == null ) {
            // if is was a new property, we remove it
            eObject.getProperties().remove(property);
        } else {
            property.setValue(oldValue);
            // else the property had been deleted, then we need to create a new one at the same index
            if ( oldIndex != -1 ) {
                eObject.getProperties().add(oldIndex, property);
            }
        }
    }
}
