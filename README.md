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
* It uses configuration files to know how to present the forms
* No need to know programming to create the forms, just to edit the configuration files (JSON)
* Each form is split in tabs to organise data
* **Properties can be shown in the following controls:**
    * label : to show read-only values
    * text : to allow values edition
    * combo : to provide a list of valid values
    * check : when binary valid versions (yes/no, true/false, ...)
    * table : when the same properties apply to a serie of objects The forms are available by right-clicking (context menu) on any Archi component.

Please consult the other pages of the wiki for more information.

## Accessing forms
Forms can be run through new context menu entries (right click) on elements, relationships, or views, depending on the forms you created.

## Wiki
For more information about customizing your configuration files take a look at the [Wiki](https://github.com/archi-contribs/form-plugin/wiki).
 

