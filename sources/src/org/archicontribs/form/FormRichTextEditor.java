package org.archicontribs.form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Level;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.swt.widgets.Composite;

public class FormRichTextEditor extends RichTextEditor {
	private static final FormLogger logger            = new FormLogger(FormRichTextEditor.class);

	public FormRichTextEditor(Composite parent, int style) {
		super(parent, style);
	}
	
	static {
		initializeTemplateURL();
	}

	private static void initializeTemplateURL() {
		logger.debug("Initializing RichTextEditor javascript files ...");
		
		FormDialog.popup("Please wait while initializing RichTextEditor environment ...");
		URL jarURL = RichTextEditor.class.getProtectionDomain().getCodeSource().getLocation();
		File jarFileReference = null;
		try {
			String decodedPath = URLDecoder.decode(jarURL.getPath(), "UTF-8");
			jarFileReference = new File(decodedPath);
		} catch (UnsupportedEncodingException e) {
			FormDialog.closePopup();
			FormDialog.popup(Level.ERROR, "Failed to initialize RichTextEditor environment.", e);
			return;
		}
		
		try (JarFile jarFile = new JarFile(jarFileReference)) {
			// create the directory to unzip to
			final java.nio.file.Path unpackDir = Files.createDirectories(Paths.get(URLDecoder.decode(System.getProperty("java.io.tmpdir") , "UTF-8"), "FormRichTextEditor"));
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith("org/eclipse/nebula/widgets/richtext/resources")) {
					File file = new File(unpackDir.toAbsolutePath() + File.separator + name);
					if ( !file.exists() ) {
						if ( entry.isDirectory() )
							file.mkdirs();
						else {
							File parentDir = new File(Paths.get(unpackDir.toString(), name , "..").toAbsolutePath().toString());
							if ( !parentDir.exists() )
								parentDir.mkdirs();
							try (InputStream is = jarFile.getInputStream(entry);
								OutputStream os = new FileOutputStream(file)) {
								while (is.available() > 0)
									os.write(is.read());
							}
						}
					}

					// found the template.html in the jar entries, so remember the URL for further usage
					if (name.endsWith("template.html")) {
			   	    	// the templateURL is a private property, so we use reflection to access it
						try {
							Field field = RichTextEditor.class.getDeclaredField("templateURL");
							field.setAccessible(true);
							if ( logger.isDebugEnabled() ) logger.trace("Setting templateURL to "+file.toURI().toURL());
							field.set(null,  file.toURI().toURL());
							field.setAccessible(false);
						} catch (Exception e) {
							FormDialog.closePopup();
							FormDialog.popup(Level.ERROR, "Failed to initialize RichTextEditor environment.", e);
							return;
						}
					}
				}
			}
		} catch (IOException e) {
			FormDialog.closePopup();
			FormDialog.popup(Level.ERROR, "Failed to initialize RichTextEditor environment.", e);
			return;
		}
		
		FormDialog.closePopup();
	}
}
