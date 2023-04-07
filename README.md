[![Archimate tool](https://www.archimatetool.com/wp-content/uploads/2018/07/header.png)](http://www.archimatetool.com/)
# Archi Form Plugin
This plugin allows you to create forms inside Archi (the ArchiMate tool) to view, edit, or delete
elements, relationships, and other properties in your Archi model. 

![description technology service](screenshots/Example_Archi_Model.archimate.gif)
## Archi Version Compatibility
This plugin is compatible with versions 4 and 5 of Archi.

## Installation Instructions
Download the org.archicontribs.form_x.y.y.archiplugin file to your computer
If you manually installed a previous version (ie manually install the JAR file), please remove it from Archi's plugins folder first
Then launch Archi and go to the Help menu and launch "Manage Plugin ins ..."
Select the org.archicontribs.form_x.y.y.archiplugin you just downloaded. Archi will automatically restart
  
## Key Functionalities
* This plugin uses configuration files to understand how to present the forms (e.g. if you want a different form, you need to create a new configuration file)
* No programming experience is needed to create the forms, just edit the configuration file using the 
WYSIWYG editor in the plugin preferences page (select the graphical editor button) or by editing the configuration file (JSON).
* Forms are split into tabs to organize model data
* Forms are run by right-clicking any Archi component and then selecting the name of the form you created in the context menu.
* Forms can be exported to an Excel spreadsheet

**Form Properties can be customizied using the following property types:**
* label : to show read-only values
* text : to show writable values 
* combo : to provide a list of valid values a user can choose from
* check : check if an object is equal to a defined value (yes/no, true/false, etc.)
* table : a table where the same properties apply to a series of objects

## Exporting to an Excel Spreadsheet 
* An empty spreadsheet must already be created with three sheets named "elements", "relationships", and "model". 
* File extensions should be .xlsx, .xlt, or .xls. 
* Run a form and then select "Export to Excel" from the form window. Select which excel file you would like the form to be exported to.

## Wiki
For more information about customizing your configuration files take a look at the [Wiki](https://github.com/archi-contribs/form-plugin/wiki).
 

