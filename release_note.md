## v1.3 :	27/075/2017
* Allow to nest variables
* Allow to change the char used to separate variable name from variable scope (default is ':')
* TODO list
  * Allow to select elements or relationships from the model tree (left side of Archi window)
  * Allow to select the model itself
  * Add an option in the preferences to continue in case of error in the configuration file (at the moment, if any error is found, the whole form is cancelled)
  * Add an option in the preferences to choose the configuration file(s) instead of forcing it in the plugin's folder
  * Create a graphical interface to generate the form as the configuration file becomes quite complex to be managed manually)

## v1.2 : 23/05/2017
 * The plugin is now able to automatically download updates from GitHub
 * Solve a bug which prevent the form to save the updated values if it was related to a relationship
 * Add the ability to change the font name, size and style of label, text and combo controls

## v1.1 :	21/05/2017
* The plugin now uses Eclipse Commands to allow undo / redo
* Change the plugin behaviour to update variables only when the OK button is clicked rather than on every keystroke
* It is now possible to choose to which component the form refers to: the selected component in the view, the view itself, or the whole model
* Change the menu icon to make it clearer
* Updates in the configuration file:
  * Add "version" property to indicate that the other changes have been correctly applied
  * Add ability to change the Ok, Cancel and Export to Excel button labels, width and height
  * The "objects" array has been renamed to "controls" to fit to the SWT controls it allows to create
  * The "value" property has been renamed to "variable" to clearly indicates that it uniquely can contain a variable
  * The "values" array in the table lines has been renamed to "cells" as it can contain literal strings and variables, depending on the corresponding column class
* The "category : dynamic" property in the table lines has been renamed to "generate: yes" as it was confusing
  * Add the ability to use variables anywhere in literal strings (form name, tab name, labels, ...)
  * Add the ability to specify a default content to any variable
  * Add the ability to choose what to do when a variable is set to empty : ignore, create, or delete
  * Add the ability to set combo box as editable (it is possible to write any value) or not editable (only values listed in the combo can be selected)

## v1.0 :	16/04/2017
* Add ability to change foreground and background color of all components
* Add ability to export to Excel files
* Update dynamic tables filter to add "AND" and "OR" genre

## v0.2 :	04/03/2017
* Solve several bugs
* Add a preference page
* Add log4j support for logging
* Add filter in dynamic table items generation
* Change RCP methods to insert entries in menus in order to be more friendly with other plugins
* The keywords are now case insensitive
* Add ability to sort table columns

### v0.1 : 28/11/2016
* Plug-in creation
