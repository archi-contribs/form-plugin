package org.archicontribs.form;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

import com.archimatetool.editor.model.commands.EObjectFeatureCommand;
import com.archimatetool.editor.model.commands.NonNotifyingCompoundCommand;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
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
    
    /**
     * Expands an expression containing variables<br>
     * It may return an empty string, but never a null value
     */
    public static String expand(String expression, String separator, EObject eObject) {
        if ( expression == null )
            return "";

        StringBuffer sb = new StringBuffer(expression.length());

        Pattern pattern = Pattern.compile("(\\$\\{([^${}]|(?1))+\\})");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String variable = matcher.group(1);
            //if ( logger.isTraceEnabled() ) logger.trace("   matching "+variable);
            String variableValue = getVariable(variable, separator, eObject);
            if ( variableValue == null )
                variableValue = "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    /**
     * Gets the value of the variable<br>
     * can return a null value in case the property does not exist. This way it is possible to distinguish between empty value and null value
     */
    public static String getVariable(String variable, String separator, EObject eObject) {
        if ( logger.isTraceEnabled() ) logger.trace("getting variable \""+variable+"\"");

        // we check that the variable provided is a string enclosed between "${" and "}"
        if ( !variable.startsWith("${") || !variable.endsWith("}") )
            throw new RuntimeException(FormPosition.getPosition(null) + "\n\nThe expression \""+variable+"\" is not a variable (it should be enclosed between \"${\" and \"}\")");
        
        String variableName = expand(variable.substring(2, variable.length()-1), separator, eObject);

        //TODO : add a preference to choose between silently ignore or raise an error
        switch ( variableName.toLowerCase() ) {
            case "class" :
                if (eObject instanceof IDiagramModelArchimateObject)
                    return ((IDiagramModelArchimateObject)eObject).getArchimateElement().getClass().getSimpleName();
                if (eObject instanceof IDiagramModelArchimateConnection)
                    return ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship().getClass().getSimpleName();
                return eObject.getClass().getSimpleName();

            case "id" :
                if (eObject instanceof IIdentifier)
                    return ((IIdentifier)eObject).getId();
                new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object does not an ID ("+eObject.getClass().getSimpleName()+").");

            case "documentation" :
                if (eObject instanceof IDocumentable)
                    return ((IDocumentable)eObject).getDocumentation();
                new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object does not have a documentation ("+eObject.getClass().getSimpleName()+").");

            case "void":
                return "";
                
            case "name" :
                if (eObject instanceof INameable)
                    return ((INameable)eObject).getName();
                new RuntimeException(FormPosition.getPosition(null) + " : cannot get variable \""+variable+"\" as the object is not a does not have a name' ("+eObject.getClass().getSimpleName()+").");

            default :
                    // check for ${property:xxx}
                if ( variableName.toLowerCase().startsWith("property"+separator) ) {
                    if ( eObject instanceof IDiagramModelArchimateObject )
                        eObject = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    if ( eObject instanceof IDiagramModelArchimateConnection )
                        eObject = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    if ( eObject instanceof IProperties ) {
                        String propertyName = variableName.substring(9);
                        for ( IProperty property: ((IProperties)eObject).getProperties() ) {
                            if ( FormPlugin.areEqual(property.getKey(),propertyName) ) {
                                return property.getValue();
                            }
                        }
                        return null;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object does not have properties ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${view:xxx}
                else if ( variableName.toLowerCase().startsWith("view"+separator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                        return getVariable("${"+variableName.substring(5)+"}", separator, eObject);
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        return getVariable(variableName.substring(5), separator, ((IDiagramModelArchimateObject)eObject).getDiagramModel());
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as the object is not part of a DiagramModel ("+eObject.getClass().getSimpleName()+").");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+separator) ) {
                    if ( eObject instanceof IArchimateModelObject ) {
                        return getVariable("${"+variableName.substring(6)+"}", separator, ((IArchimateModelObject)eObject).getArchimateModel());
                    }
                    else if ( eObject instanceof IDiagramModelComponent ) {
                        return getVariable("${"+variableName.substring(6)+"}", separator, ((IDiagramModelComponent)eObject).getDiagramModel().getArchimateModel());
                    }
                    else if ( eObject instanceof IArchimateModel ) {
                        return getVariable("${"+variableName.substring(6)+"}", separator, eObject);
                    }
                    
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot get variable \""+variable+"\" as we failed to get the object's model ("+eObject.getClass().getSimpleName()+").");
                }
                
                    // check for ${source:xxx}
                else if ( variableName.toLowerCase().startsWith("source"+separator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }

                    if ( obj instanceof IArchimateRelationship ) {
                        return getVariable("${"+variableName.substring(7)+"}", separator, ((IArchimateRelationship)obj).getSource());
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot get variable \""+variable+"\" as the object is not a relationship.");
                }
                    
                    // check for ${target:xxx}
                else if ( variableName.toLowerCase().startsWith("target"+separator) ) {
                    EObject obj = eObject;
                    if ( eObject instanceof IDiagramModelArchimateObject) {
                        obj = ((IDiagramModelArchimateObject)eObject).getArchimateElement();
                    } else if (eObject instanceof IDiagramModelArchimateConnection) {
                        obj = ((IDiagramModelArchimateConnection)eObject).getArchimateRelationship();
                    } else {
                        obj = eObject;
                    }
                    
                    if ( obj instanceof IArchimateRelationship ) {
                        return getVariable("${"+variableName.substring(7)+"}", separator, ((IArchimateRelationship)obj).getTarget());
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
    public static void setVariable(String variable, String separator, String value, EObject eObject) throws RuntimeException {
        if ( logger.isTraceEnabled() ) logger.trace("setting variable \""+variable+"\"");
        
        CompoundCommand compoundCommand = new NonNotifyingCompoundCommand();

        // we check that the variable provided is a string enclosed between "${" and "}"
        Pattern pattern = Pattern.compile("^\\$\\{[^}]+}$");
        Matcher matcher = pattern.matcher(variable);
        if ( !matcher.matches() )
            throw new RuntimeException(FormPosition.getPosition(null) + "\n\nThe expression \""+variable+"\" is not a variable (it should be enclosed between \"${\" and \"}\")");

        String variableName = variable.substring(2, variable.length()-1);
        Command cmd;

        switch ( variableName.toLowerCase() ) {
            case "class" :  // TODO: show error message
                break;      // we refuse to change the class of an eObject

            case "id" :
                if (eObject instanceof IIdentifier) {
                    if ( value == null || value.length()==0 )
                        throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the value provided is null.");
                    cmd = new EObjectFeatureCommand(FormPosition.getFormName(), eObject, IArchimatePackage.Literals.IDENTIFIER__ID, value);
                    if(cmd.canExecute()) {
                        compoundCommand.add(cmd);
                    }
                    return;
                }
                break;

            case "documentation" :
                if (eObject instanceof IDocumentable) {
                    //((IDocumentable)eObject).setDocumentation(value == null ? "" : value);
                    cmd = new EObjectFeatureCommand(FormPosition.getFormName(), eObject, IArchimatePackage.Literals.DOCUMENTABLE__DOCUMENTATION, value == null ? "" : value);
                    if(cmd.canExecute()) {
                        compoundCommand.add(cmd);
                    }
                    return;
                }
                else {
                    // TODO: show error message
                }
                break;

            case "name" :
                if (eObject instanceof INameable) {
                    //((INameable)eObject).setName(value == null ? "" : value);
                    cmd = new EObjectFeatureCommand(FormPosition.getFormName(), eObject, IArchimatePackage.Literals.NAMEABLE__NAME, value == null ? "" : value);
                    if(cmd.canExecute()) {
                        compoundCommand.add(cmd);
                    }
                    return;
                } else {
                    // TODO: show error message
                }
                break;

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

                        IProperty propertyToUpdate = null;
                        for ( IProperty property: ((IProperties)eObject).getProperties() ) {
                            if ( FormPlugin.areEqual(property.getKey(), propertyName) ) {
                                propertyToUpdate = property;
                                break;
                            }
                        }
                        // if the property does not (yet) exists
                        if ( propertyToUpdate == null ) {
                            // if value == null, we do not create a new property
                            // if value != null, we create a new property
                            if ( value != null ) {
                                //IProperty newProperty = IArchimateFactory.eINSTANCE.createProperty();
                                //newProperty.setKey(propertyName);
                                //newProperty.setValue(value);
                                //((IProperties)eObject).getProperties().add(newProperty);
                                cmd = new FormPropertyCommand(FormPosition.getFormName(), (IProperties)eObject, IArchimateFactory.eINSTANCE.createProperty()) {
                                    @Override
                                    public void execute() {
                                        property.setKey(propertyName);
                                        property.setValue(value);
                                        eObject.getProperties().add(property);
                                    }
                                    
                                    @Override
                                    public void undo() {
                                        eObject.getProperties().remove(property);
                                    }
                                };
                                
                                if(cmd.canExecute()) {
                                    compoundCommand.add(cmd);
                                }
                            }
                        } else {
                            // if the property already exists
                            
                            if ( value == null ) {
                                // if value == null, we remove the property
                                //((IProperties)eObject).getProperties().remove(propertyToUpdate);
                                cmd = new FormPropertyCommand(FormPosition.getFormName(), (IProperties)eObject, propertyToUpdate) {
                                    @Override
                                    public void execute() {
                                        eObject.getProperties().remove(property);
                                    }
                                    
                                    @Override
                                    public void undo() {
                                        eObject.getProperties().add(property);              // TODO; store the property index to add it at the same place !!!
                                    }
                                };
                                
                                if(cmd.canExecute()) {
                                    compoundCommand.add(cmd);
                                }
                            } else {
                                // if value != null, we update the property value
                                //propertyToUpdate.setKey(propertyName);
                                //propertyToUpdate.setValue(value);
                                cmd = new FormPropertyCommand(FormPosition.getFormName(), (IProperties)eObject, propertyToUpdate, propertyToUpdate.getValue(), value) {
                                    @Override
                                    public void execute() {
                                        property.setValue(newValue);
                                    }
                                    
                                    @Override
                                    public void undo() {
                                        property.setValue(oldValue);
                                    }
                                };
                                
                                if(cmd.canExecute()) {
                                    compoundCommand.add(cmd);
                                }
                            }
                        }
                        return;
                    } else {
                        //TODO : show up an error
                    }
                }

                    // check for ${view:xxx}
                else if ( variableName.startsWith("view"+separator) ) {
                    if ( eObject instanceof IDiagramModel ) {
                        setVariable("${"+variableName.substring(5)+"}", separator, value, eObject);
                        return;
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        setVariable("${"+variableName.substring(5)+"}", separator, value, (EObject)((IDiagramModelArchimateObject)eObject).getDiagramModel());
                        return;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\n\nCannot set variable \""+variable+"\" as the object is not part of a DiagramModel.");
                }

                    // check for ${model:xxx}
                else if ( variableName.toLowerCase().startsWith("model"+separator) ) {
                    if ( eObject instanceof IArchimateDiagramModel ) {
                        setVariable("${"+variableName.substring(6)+"}", separator, value, eObject);
                        return;
                    }
                    else if ( eObject instanceof IDiagramModelArchimateObject ) {
                        setVariable("${"+variableName.substring(6)+"}", separator, value, ((IDiagramModelArchimateObject)eObject).getDiagramModel().getArchimateModel()); ;
                        return;
                    }
                    throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot set variable \""+variable+"\" as the object is not part of a model.");
                }
                
                // check for ${source:xxx}
            else if ( variableName.toLowerCase().startsWith("source"+separator) ) {
                if ( eObject instanceof IArchimateRelationship ) {
                    setVariable("${"+variableName.substring(7)+"}", separator, value, ((IArchimateRelationship)eObject).getSource());
                    return;
                }
                throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot set variable \""+variable+"\" as the object is not a relationship.");
            }
                
                // check for ${target:xxx}
            else if ( variableName.toLowerCase().startsWith("target"+separator) ) {
                if ( eObject instanceof IArchimateRelationship ) {
                    setVariable("${"+variableName.substring(7)+"}", separator, value, ((IArchimateRelationship)eObject).getTarget());
                    return;
                }
                throw new RuntimeException(FormPosition.getPosition(null) + "\nCannot set variable \""+variable+"\" as the object is not a relationship.");
            }
                
                // no need for a final else, because all the tests before are supposed to return the value if any, or throw an exception if none
        }
        
        throw new RuntimeException(FormPosition.getPosition(null) + "\nDo not know how to set variable \""+variableName+"\"");
    }
}