[![N|Solid](http://www.archimatetool.com/img/archi_logo.png)](http://www.archimatetool.com/)[![N|Solid](http://www.archimatetool.com/img/archi_text.png)](http://www.archimatetool.com/)
# Archimate Tool Form plugin
This is a plugin for Archi, the Archimate tool, that allows you to create forms to view
elements, relationships, and other properties in your Archi model. You can also edit or delete
information in your model directly from the generated form.

![description technology service](https://user-images.githubusercontent.com/9281982/32824895-1bee7b02-c9e3-11e7-8e66-9d22ae234f06.png)

## Archi versions compatibility
The plugin is compatible with version 4 of Archi.

## Installation Instructions
* **First Installation:**
  * Download the latest **org.archicontribs.form-xxxx.jar** file and copy it to your Archi **plugins** 
    folder. The plugins folder can be found by opening the package contents of your Archi application 
    and then navigating to "Contents>Eclipse>plugins". 
* **Install Updates:**
  * Open Archi, then open the "Preferences" option under "Archi" in the menu bar. 
    Select "form plugin" in the Preferences menu and click the "Check for update"
    button to check for and install new updates.
  
## Key functionalities
* The plugin uses configuration files to understand how to present the forms (e.g. if you want a different kind of form, you need to create a new configuration file)
* No programming experience is needed to create the forms, just edit the configuration file using the 
WYSIWYG editor in the plugin preferences page, or by editing the configuration file directly. 
* The configuration file is in JSON
* Each form is split into tabs to organize model data
* Forms are run by right-clicking on any Archi component and then selecting the name of the form you created in the context menu.

**Form Properties can be customizied using the following property types:**
* label : to show read-only values
* text : to show writable values 
* combo : to provide a list of valid values a user can choose from
* check : check if an object is equal to a defined value (yes/no, true/false, etc.)
* table : a table where the same properties apply to a series of objects 

## Wiki
For more information about customizing your configuration files take a look at the [Wiki](https://github.com/archi-contribs/form-plugin/wiki).
 

