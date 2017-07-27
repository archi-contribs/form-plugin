package org.archicontribs.form;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Control;

public class FormVarList {
    HashMap<EObject, HashMap<String, Set<Control>>> varList;
    
    public FormVarList() {
        varList = new HashMap<EObject, HashMap<String, Set<Control>>>();
    }
    
    public void set(EObject eObject, String variable, Control control) {
        if ( eObject == null )
            return;
        
        HashMap<String, Set<Control>> map = varList.get(eObject);
        Set<Control> controls = null;
        
        if ( map == null ) {
            map = new HashMap<String, Set<Control>>();
        } else {
            controls = map.get(variable);
        }
        
        if ( controls == null )
            controls = new HashSet<Control>();
        
        controls.add(control);
        
        map.put(variable, controls);
        
        varList.put(eObject, map);
    }
    
    public void replaceControl(Control oldControl, Control newControl) {
        if ( oldControl == null || newControl == null )
            return;
        
        for ( HashMap<String, Set<Control>> map: varList.values() ) {
            for ( Set<Control> controls: map.values() ) {
                if( controls.contains(oldControl) ) {
                    controls.remove(oldControl);
                    controls.add(newControl);
                }
            }
        }
    }
    
    public Set<Control> getControls(EObject eObject, String variable) {
        if ( eObject == null )
            return null;
        
        HashMap<String, Set<Control>> map = varList.get(eObject);
        
        if ( map == null )
            return null;
        
        Set<Control> controls = map.get(variable);
        
        if ( controls == null )
            return null;
        
        return controls;
    }
}
