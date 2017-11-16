package org.archicontribs.form;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CompoundCommand;

import com.archimatetool.editor.model.commands.EObjectFeatureCommand;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class FormVariable {
    private static final FormLogger logger = new FormLogger(FormVariable.class);
    
    private static String variableSeparator = ":";
    
    public static void setVariableSeparator(String separator) {
    	variableSeparator = separator;
    }
    
    /**
     * Expands an expression containing variables<br>
     * It may return an empty string, but never a null value
     */
    public static String expand(String expression, EObject eObject) {
        if ( expression == null )
            return "";

        StringBuffer sb = new StringBuffer(expression.length());

        Pattern pattern = Pattern.compile("(\\$\\{([^${}]|(?1))+\\})");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String variable = matcher.group(1);
            //if ( logger.isTraceEnabled() ) logger.trace("   matching "+variable);
            String variableValue = getVariable(variable, eObject);
            if ( variableValue == null )
                variableValue = "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    /**
     * Gets the real EObject that the variable refers to (as the variable can change the EObject using its scope)
     */
    public static EObject getReferedEObject(String variable, EObject eObject) {
        if ( logger.isTraceEnabled() ) logger.trace("         getting refered EObject from variable \""+variable+"\" (source object = "+FormPlugin.getDebugName(eObject)+")");

        if ( variable == null || eObject == null )
        	return null;
        
        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") ) {
        	if ( logger.isTraceEnabled() ) logger.trace("         --> does not start with \"${\"");
            return null;
        }
        if ( !variable.endsWith("}") ) {
            if ( logger.isTraceEnabled() ) logger.trace("         --> does not end with \"}\"");
            return null;
        }
        
        String variableName = expand(variable.substring(2, variable.length()-1), eObject);

        //TODO : add a preference to choose between silently ignore or raise an error
        switch ( variableName.toLowerCase() ) {
            case "class" :
            case "documentation" :
            case "id" :
            case "name" :
            	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                return eObject;

            case "void":
            case "username":
                return null;

            default :
                    // check for ${property:xxx}
                if ( variableName.toLowerCase().startsWith("property"+variableSeparator) ) {
                	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                    return eObject;
                }

                    // check for ${view:xxx}
                else if ( variableName.toLowerCase().startsWith("view"+variableSeparator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                        return eObject;
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("            --> "+FormPlugin.getDebugName(((IDiagramModelArchimateObject)eObject).getDiagramModel()));
                        return ((IDiagramModelArchimateObject)eObject).getDiagramModel();
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object is not part of a DiagramModel ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+variableSeparator) ) {
                    if ( eObject instanceof IArchimateModelObject ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+FormPlugin.getDebugName(((IArchimateModelObject)eObject).getArchimateModel()));
                        return ((IArchimateModelObject)eObject).getArchimateModel();
                    }
                    else if ( eObject instanceof IDiagramModelComponent ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+FormPlugin.getDebugName(((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel()));
                        return  ((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel();
                    }
                    else if ( eObject instanceof IArchimateModel ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> itself");
                        return eObject;
                    }
                    
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as we failed to get the object's model ("+FormPlugin.getDebugName(eObject)+").");
                }
                
                    // check for ${source:xxx}
                else if ( variableName.toLowerCase().startsWith("source"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }

                    if ( obj instanceof IArchimateRelationship ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+FormPlugin.getDebugName(((IArchimateRelationship)obj).getSource()));
                        return ((IArchimateRelationship)obj).getSource();
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot get variable \""+variable+"\" as the object is not a relationship.");
                }
                    
                    // check for ${target:xxx}
                else if ( variableName.toLowerCase().startsWith("target"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }
                    
                    if ( obj instanceof IArchimateRelationship ) {
                    	if ( logger.isTraceEnabled() ) logger.trace("         --> "+FormPlugin.getDebugName(((IArchimateRelationship)obj).getTarget()));
                        return ((IArchimateRelationship)obj).getTarget();
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot get variable \""+variable+"\" as the object is not a relationship.");
                }
        }
        throw new RuntimeException(FormPosition.getPosition(null) + "\n\nUnknown variable \""+variableName+"\" ("+variable+")");
    }
    
    /**
     * Gets the variable without its scope
     */
    public static String getUnscoppedVariable(String variable, EObject eObject) {
    	if ( variable == null || eObject == null )
    		return null;
    	
        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") || !variable.endsWith("}") )
            return null;
        
        String variableName = expand(variable.substring(2, variable.length()-1), eObject);

	        // check for ${property:xxx}
	    if ( variableName.toLowerCase().startsWith("view"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(5)+"}", eObject);
	    else if ( variableName.toLowerCase().startsWith("model"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(6)+"}", eObject);
	    else if ( variableName.toLowerCase().startsWith("source"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(7)+"}", eObject);
	    else if ( variableName.toLowerCase().startsWith("target"+variableSeparator) )
	    	return getUnscoppedVariable("${"+variableName.substring(7)+"}", eObject);
	    else return "${"+variableName+"}";
    }
    
    /**
     * Gets the value of the variable<br>
     * can return a null value in case the property does not exist. This way it is possible to distinguish between empty value and null value
     */
    public static String getVariable(String variable, EObject eObject) {
        if ( logger.isTraceEnabled() ) logger.trace("         getting variable \""+variable+"\"");

        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") || !variable.endsWith("}") )
            throw new RuntimeException(FormPosition.getPosition(null) + "\n\nThe expression \""+variable+"\" is not a variable (it should be enclosed between \"${\" and \"}\")");
        
        String variableName = expand(variable.substring(2, variable.length()-1), eObject);

        //TODO : add a preference to choose between silently ignore or raise an error
        switch ( variableName.toLowerCase() ) {
            case "class" :
                if (eObject instanceof IDiagramModelArchimateObject) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName() +"\"");
                    return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName();
                }
                if (eObject instanceof IDiagramModelArchimateConnection) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getClass().getSimpleName() +"\"");
                    return ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getClass().getSimpleName();
                }
                if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ eObject.getClass().getSimpleName() +"\"");
                return eObject.getClass().getSimpleName();

            case "id" :
                if (eObject instanceof IIdentifier) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IIdentifier)eObject).getId() +"\"");
                    return ((IIdentifier)eObject).getId();
                }
                new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object does not an ID ("+eObject.getClass().getSimpleName()+").");

            case "documentation" :
                if (eObject instanceof IDiagramModelArchimateObject) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateObject)eObject).getArchimateElement().getDocumentation() +"\"");
                    return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getDocumentation();
                }
                if (eObject instanceof IDiagramModelArchimateConnection) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getDocumentation() +"\"");
                    return ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getDocumentation();
                }
                if (eObject instanceof IDocumentable) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((IDocumentable)eObject).getDocumentation() +"\"");
                    return ((IDocumentable)eObject).getDocumentation();
                }
                new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object does not have a documentation ("+eObject.getClass().getSimpleName()+").");

            case "void":
                if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \"\"");
                return "";
                
            case "name" :
                if (eObject instanceof INameable) {
                    if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ ((INameable)eObject).getName() +"\"");
                    return ((INameable)eObject).getName();
                }
                new RuntimeException(FormPosition.getPosition(null) + " : cannot get variable \""+variable+"\" as the object is not a does not have a name' ("+eObject.getClass().getSimpleName()+").");

            case "username":
            	return System.getProperty("user.name");
            	
            default :
            		// check for ${date:format}
            	if ( variableName.toLowerCase().startsWith("date"+variableSeparator)) {
            		String format = variableName.substring(5);
            		DateFormat df = new SimpleDateFormat(format);
            		Date now = Calendar.getInstance().getTime();
            		return df.format(now);
            	}
            	
                    // check for ${property:xxx}
            	else if ( variableName.toLowerCase().startsWith("property"+variableSeparator) ) {
                    if ( eObject instanceof IDiagramModelArchimateObject )
                        eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    if ( eObject instanceof IDiagramModelArchimateConnection )
                        eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    if ( eObject instanceof IProperties ) {
                        String propertyName = variableName.substring(9);
                        for ( IProperty property: ((IProperties)eObject).getProperties() ) {
                            if ( FormPlugin.areEqual(property.getKey(),propertyName) ) {
                                if ( logger.isTraceEnabled() ) logger.trace("         ---> value is \""+ property.getValue() +"\"");
                                return property.getValue();
                            }
                        }
                        if ( logger.isTraceEnabled() ) logger.trace("         ---> value is null");
                        return null;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object does not have properties ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${view:xxx}
                else if ( variableName.toLowerCase().startsWith("view"+variableSeparator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                        return getVariable("${"+variableName.substring(5)+"}", eObject);
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        return getVariable(variableName.substring(5), ((IDiagramModelArchimateObject)eObject).getDiagramModel());
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object is not part of a DiagramModel ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+variableSeparator) ) {
                    if ( eObject instanceof IArchimateModelObject ) {
                        return getVariable("${"+variableName.substring(6)+"}", ((IArchimateModelObject)eObject).getArchimateModel());
                    }
                    else if ( eObject instanceof IDiagramModelComponent ) {
                        return getVariable("${"+variableName.substring(6)+"}", ((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel());
                    }
                    else if ( eObject instanceof IArchimateModel ) {
                        return getVariable("${"+variableName.substring(6)+"}", eObject);
                    }
                    
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as we failed to get the object's model ("+eObject.getClass().getSimpleName()+").");
                }
                
                    // check for ${source:xxx}
                else if ( variableName.toLowerCase().startsWith("source"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }

                    if ( obj instanceof IArchimateRelationship ) {
                        return getVariable("${"+variableName.substring(7)+"}", ((IArchimateRelationship)obj).getSource());
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot get variable \""+variable+"\" as the object is not a relationship.");
                }
                    
                    // check for ${target:xxx}
                else if ( variableName.toLowerCase().startsWith("target"+variableSeparator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }
                    
                    if ( obj instanceof IArchimateRelationship ) {
                        return getVariable("${"+variableName.substring(7)+"}", ((IArchimateRelationship)obj).getTarget());
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot get variable \""+variable+"\" as the object is not a relationship.");
                }
        }
        throw new RuntimeException(FormPosition.getPosition(null) + "\n\nUnknown variable \""+variableName+"\" ("+variable+")");
    }
    
    /**
     * Set the value of a variable<br>
     * <br>
     * The variable name can be :<br>
     *    - $documentation  sets the documentation of the eObject<br>
     *    - $property:xxx   deletes the property is value is null, else sets the property value (create the property if needed)<br>
     * <br>
     * This method does not throw exceptions as it is mainly called by SWT which won't know what to do with these exceptions.<br>
     * Instead, it opens a popup to display the error message.
     */
    public static void setVariable(CompoundCommand compoundCommand, String variable, String separator, String value, EObject eObject) throws RuntimeException {
        if ( logger.isTraceEnabled() ) logger.trace("   setting \""+value+"\" to "+variable+" of "+FormPlugin.getDebugName(eObject));
        
        EObjectFeatureCommand eCommand;
        FormPropertyCommand fCommand;

        // we check that the variable provided is a string enclosed between "${" and "}"
        Pattern pattern = Pattern.compile("^\\$\\{[^}]+}$");
        Matcher matcher = pattern.matcher(variable);
        if ( !matcher.matches() )
            throw new RuntimeException(FormPosition.getPosition(null) + "\n\nThe expression \""+variable+"\" is not a variable (it should be enclosed between \"${\" and \"}\")");

        String variableName = variable.substring(2, variable.length()-1);

        switch ( variableName.toLowerCase() ) {
            case "class" :  // we refuse to change the class of an eObject
                throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot change the class of an Archi object.");   

            case "id" :
                if (eObject instanceof IIdentifier) {
                    if ( value == null || value.length()==0 )
                        throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the value provided is null.");
                    eCommand = new EObjectFeatureCommand(FormPosition.getFormName(), eObject, IArchimatePackage.Literals.IDENTIFIER__ID, value);
                    if ( eCommand.canExecute() )
                    	compoundCommand.add(eCommand);
                    return;
                }
                break;

            case "documentation" :
                if (eObject instanceof IDiagramModelArchimateObject) {
                    eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                }
                if (eObject instanceof IDiagramModelArchimateConnection) {
                    eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                }
                if (eObject instanceof IDocumentable) {
                    //((IDocumentable)eObject).setDocumentation(value == null ? "" : value);
                    eCommand = new EObjectFeatureCommand(FormPosition.getFormName(), eObject, IArchimatePackage.Literals.DOCUMENTABLE__DOCUMENTATION, value == null ? "" : value);
                    if ( eCommand.canExecute() )
                    	compoundCommand.add(eCommand);
                    return;
                }
                throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the archi Object does not have a "+variable+" field.");

            case "name" :
                if (eObject instanceof INameable) {
                    //((INameable)eObject).setName(value == null ? "" : value);
                	eCommand = new EObjectFeatureCommand(FormPosition.getFormName(), eObject, IArchimatePackage.Literals.NAMEABLE__NAME, value == null ? "" : value);
                    if ( eCommand.canExecute() )
                    	compoundCommand.add(eCommand);
                    return;
                }
                throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the archi Object does not have a "+variable+" field.");

            case "void":
                return;
            
            default :
                    // check for ${property:xxx} 
                if ( variableName.startsWith("property"+separator) ) {
                    if ( eObject instanceof IDiagramModelArchimateObject )
                        eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    if ( eObject instanceof IDiagramModelArchimateConnection )
                        eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    
                    if ( eObject instanceof IProperties ) {
                        String propertyName = variableName.substring(9);
                        
                    	fCommand = new FormPropertyCommand(FormPosition.getFormName(), (IProperties)eObject, propertyName, value);
                        if ( fCommand.canExecute() )
                        	compoundCommand.add(fCommand);
                        return;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the archi Object does not have properties.");
                }

                    // check for ${view:xxx}
                else if ( variableName.startsWith("view"+separator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                        setVariable(compoundCommand, "${"+variableName.substring(5)+"}", separator, value, eObject);
                        return;
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        setVariable(compoundCommand, "${"+variableName.substring(5)+"}", separator, value, (EObject)((IDiagramModelArchimateObject)eObject).getDiagramModel());
                        return;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the object is not part of a DiagramModel.");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+separator) ) {
                    if ( eObject instanceof IArchimateDiagramModel ) {
                        setVariable(compoundCommand, "${"+variableName.substring(6)+"}", separator, value, eObject);
                        return;
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        setVariable(compoundCommand, "${"+variableName.substring(6)+"}", separator, value, ((IDiagramModelArchimateObject)eObject).getDiagramModel().getArchimateModel()); ;
                        return;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot set variable \""+variable+"\" as the object is not part of a model.");
                }
                
                // check for ${source:xxx}
            else if ( variableName.toLowerCase().startsWith("source"+separator) ) {
                if ( eObject instanceof IArchimateRelationship ) {
                    setVariable(compoundCommand, "${"+variableName.substring(7)+"}", separator, value, ((IArchimateRelationship)eObject).getSource());
                    return;
                }
                throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot set variable \""+variable+"\" as the object is not a relationship.");
            }
                
                // check for ${target:xxx}
            else if ( variableName.toLowerCase().startsWith("target"+separator) ) {
                if ( eObject instanceof IArchimateRelationship ) {
                    setVariable(compoundCommand, "${"+variableName.substring(7)+"}", separator, value, ((IArchimateRelationship)eObject).getTarget());
                    return;
                }
                throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot set variable \""+variable+"\" as the object is not a relationship.");
            }
                
                // no need for a final else, because all the tests before are supposed to return the value if any, or throw an exception if none
        }
        
        throw new RuntimeException(FormPosition.getPosition(null) + "\nDo not know how to set variable \""+variableName+"\"");
    }
}
