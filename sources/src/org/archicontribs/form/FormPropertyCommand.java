package org.archicontribs.form;

import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

public class FormPropertyCommand extends Command {
	protected IProperty property;
	protected IProperties eObject;
	protected String oldValue = "";
	protected String newValue = "";
	
	public FormPropertyCommand(String label, IProperties eObject, IProperty property) {
		setLabel(label);
		this.property = property;
		this.eObject = eObject;
	}
	
	public FormPropertyCommand(String label, IProperties eObject, IProperty property, String oldValue, String newValue) {
		setLabel(label);
		this.property = property;
		this.eObject = eObject;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
}
