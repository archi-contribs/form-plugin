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
	protected enum actionType {Nothing, PropertyCreated, PropertyDeleted, PropertyUpdated}
	
    protected IProperties eObject;
	protected String      key;
	protected String      value;

	protected actionType  action;
	protected IProperty   property;
	protected String      oldValue;
	protected int         propertyIndex;
		
	/*
	 * Creates a new property
	 */
	public FormPropertyCommand(String label, IProperties eObject, String key, String value) {
	    setLabel(label);
	    this.eObject = eObject;
	    this.key = key;
	    this.value = value;
	}
	
    @Override
    public void execute() {
    	// we search for the property
    	this.property = null;
    	for ( IProperty prop: this.eObject.getProperties() ) {
    		if ( prop.getKey().equals(this.key)) {
    			this.property = prop;
    			break;
    		}
    	}
    	
    	if ( this.property == null ) {
    		// if the key does not exits yet, then we create it ... but only if the value is not null
    		if ( this.value == null ) {
    			this.action = actionType.Nothing;
    		} else {
	    		this.action = actionType.PropertyCreated;
	            this.property = IArchimateFactory.eINSTANCE.createProperty();
	            this.property.setKey(this.key);
	            this.property.setValue(this.value);
	            this.eObject.getProperties().add(this.property);
    		}
    	} else {
    		// else, we update the value ... but only if the value is not null
    		if ( this.value == null ) {
    			this.action = actionType.PropertyDeleted;
    			this.propertyIndex = this.eObject.getProperties().indexOf(this.property);
    			this.eObject.getProperties().remove(this.property);
    		} else {
    			this.action = actionType.PropertyUpdated;
    			this.oldValue = this.property.getValue();
    			this.property.setValue(this.value);
    		}
    	}
    }
    
    @Override
    public void undo() {
    	switch ( this.action ) {
    		case PropertyCreated:
    			// we remove the newly created property
    			this.eObject.getProperties().remove(this.property);
    			break;
    		case PropertyDeleted:
    			// we restore the deleted property
    			this.eObject.getProperties().add(this.propertyIndex, this.property);
    			break;
    		case PropertyUpdated:
    			// we restore the old value
    			this.property.setValue(this.oldValue);
    			break;
			case Nothing:
				// nothing to undo
				break;
            default:
                // unknown action
    	}
    }
}
