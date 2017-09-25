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
	protected String oldPropertyValue = null;
	protected int propertyIndex = 0;
	protected boolean propertyCreated;
		
	/*
	 * Creates a new property
	 */
	public FormPropertyCommand(String label, IProperties eObject, String propertyName, String propertyValue) {
	    setLabel(label);
	    this.eObject = eObject;
	    this.property = null;
	    this.propertyName = propertyName;
	    this.oldPropertyValue = null;
	    this.propertyValue = propertyValue;
	    this.propertyIndex = 0;
	    propertyCreated = true;
	}
	
	/*
	 * Updates an existing property
	 */
	public FormPropertyCommand(String label, IProperties eObject, IProperty property, String propertyValue) {
		setLabel(label);
	    this.eObject = eObject;
		this.property = property;
		this.propertyName = property.getKey();
		this.oldPropertyValue = property.getValue();
		this.propertyValue = propertyValue;
		this.propertyIndex = eObject.getProperties().indexOf(property);
		propertyCreated = false;
	}
	
    @Override
    public void execute() {
        if ( propertyCreated ) {
        	// We create a new property
            property = IArchimateFactory.eINSTANCE.createProperty();
            property.setKey(propertyName);
            property.setValue(propertyValue);
            eObject.getProperties().add(property);
        } else {
            // we update the existing property
            if ( propertyValue == null ) {
                eObject.getProperties().remove(property);		// if the value is null, then we remove the property
            } else {
                property.setValue(propertyValue);				// if the value is not null, we set it
            }
        }
    }
    
    @Override
    public void undo() {
        if ( propertyCreated ) {
            // if is was a new property, we remove it
            eObject.getProperties().remove(property);
        } else {
            if ( propertyValue == null ) {
                eObject.getProperties().add(propertyIndex, property);		// we restore the property
            } else {
            	property.setValue(oldPropertyValue);						// we restore the old value
            }
        }
    }
}
