## v1.9.2: 07/04/2023
* Fix save editor that failed after upgrade to Java 17
* Add ${specialization} variable

## v1.9.1: 23/05/2019
* Fix "Failed to get the model" error message
* Fix the version number printed in the update messages

* TODO list
  * Add an option in the preferences to continue in case of error in the configuration file (at the moment, if any error is found, the whole form is cancelled)
  * Add the sum or average of column values
  * Add a "if" option on all controls that would work like "filter" but for individual controls ...
    * if the condition is met, then the control is created, else it is not created

----------
## v1.9: 20/05/2019
* Add the "is selected" operation in the filter
* Fix the filter editor
  * Allow to generate lines without filter
  * Updates to filter fields are now better handled
* Fix the label column editor
  * Remove the text field that is unused
  
## v1.8.11: 30/04/2019
* Fix the Graphical editor:
  * The filter genre was not loaded correctly from the configuration file
  * The combo table column was not managing the foreground and background colors
* Fix the version comparison when a version component is greater than 10

## v1.8.10: 04/03/2019
* Fix "editable" property
* Fix "regexp" property
* Added a popup that indicates when the form is being created

## v1.8.9: 30/08/2018
* fix default font size

## v1.8.8: 29/08/2018
* Fix widgets location on Linux 4K display

## v1.8.7: 20/08/2018
* Allow to specify distinct width for the ok, cancel and export buttons (a width of 0 allows to hide the button)

## v1.8.6: 17/08/2018
* Fix export to Excel file

## v1.8.5: 14/04/2018
* Fix recursion in folders when a model is selected

## v1.8.4: 05/04/2018
* Fix folder recursion
* Fix graphical editor button activated even if no form file is selected
* Increase compiler severity and solve all of the warnings
* Add default and forceDefault fields in text and combo table columns
* Add "\*.xlt\*" and "\*.\*" as Excel file extension for MAC users
* Add RICHTEXT class to generate HTML text (experimental)

## v1.8.3: 22/11/2017
* Fix error messages on ${view:xxx} variables
	
## v1.8.2: 21/11/2017
* Add more default values to configuration's file keywords
* Update graphical generator : remove filter when empty 
* Remove error message when using ${screenshot} variable
* Fix "generate" keyword in graphical editor
* Fix epxort image to Excel spreadsheet

## v1.8.1: 18/11/2017
* Add ${screenshot} variable to get a view screenshot
* Add the "content" property to image class
* Add "comment" property this is not used by form but allow to keep some comment on every controls
* Update the inline help pages
* fix menus' icon

## v1.8: 16/11/2017
* Add class image
* Add ${username} variable
* Add ${date:format} variable

## v1.7: 12/11/2017
* Fix excelType for object outside of tables
* Fix table columns foreground and background
* Fix graphical editor that did not edit the filter on the whole form

## v1.6.7: 27/10/2017
* Fix table export to Excel after column sort
* Fix table color and font after column sort
* Fix default value for the "refers" property
  
## v1.6.6: 26/10/2017
* Fix documentation variable

## v1.6.5: 25/10/2017
* Fix Export to Excel button that was not shown when required
* Add "not" keyword in filter tests
* Fix table columns sorting that did not work since the graphical editor

## v1.6.4: 03/10/2017
* fix getting default font from parent's
* fix checkbox value

## v1.6.3:  30/09/2017
* Fix the "refers" field was not correctly set
* Fix the plugin auto update
* Add tooltips on the graphical editor fields

## v1.6.2:  28/09/2017
* Fix duplicate table lines whent the form is applied to the whole model
* Forcing layout of table widgets todisplay them quicker
* Adding a filter editor on the form

## v1.6.1:  26/09/2017
* fix missing relationships in table
  
## v1.6:	25/09/2017
* Add graphical interface to generate the configuration file
* Configuration files can now contain a single form only
* Change JSON file version to 3
* Fix check for update at startup
* Fix properties changes undo/redo

## v1.5.4:	05/08/2017
* Add "in" and "iin" tests
* Add "${source:xxx}" and "${target:xxx}" variables scope when selected object is a relationship
* Add "${void}" variable
* Bug corrections in export to Excel
* Add "delete" excelDefault behaviour
* Updating a control updates in real time all the other controls that refer to the same variable
* Add "foreground" and "background" keywords for table columns
* Labels are now in wrap mode
 
## v1.5.3:	30/06/2017
* Accept to change IDs
 
## v1.5.2:	21/06/2017
* Correct default value on combo controls
 
## v1.5.1:	19/06/2017
* Removed dependency to Eclipse library which invalidated the form plugin
 
## v1.5:	17/06/2017
* Finally, it is now possible to choose the configuration file(s) on the preference page
* Add first online help pages
* The selected object can now be a referenced view, a canvas or a sketch view
 
## v1.4.1:	06/06/2017
* Solve "failed to get the model" error message

## v1.4:	02/06/2017
* Allow to select components directly on the tree on the left side of Archi window
* Allow to select folders
* Allow to select the model itself
* Rewrite of error message to be more helpful in searching the error cause
* Set the WRAP bit of the text controls
* Solve bug which avoided the tooltip to showup on some controls

## v1.3:	27/05/2017
* Allow to nest variables
* Allow to change the char used to separate variable name from variable scope (default is ':')

## v1.2: 23/05/2017
 * The plugin is now able to automatically download updates from GitHub
 * Solve a bug which prevent the form to save the updated values if it was related to a relationship
 * Add the ability to change the font name, size and style of label, text and combo controls

## v1.1:	21/05/2017
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

## v1.0:	16/04/2017
* Add ability to change foreground and background color of all components
* Add ability to export to Excel files
* Update dynamic tables filter to add "AND" and "OR" genre

## v0.2:	04/03/2017
* Solve several bugs
* Add a preference page
* Add log4j support for logging
* Add filter in dynamic table items generation
* Change RCP methods to insert entries in menus in order to be more friendly with other plugins
* The keywords are now case insensitive
* Add ability to sort table columns

### v0.1: 28/11/2016
* Plug-in creation
