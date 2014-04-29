package io.sporkpgm.map;

import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderFactory;
import io.sporkpgm.module.modules.info.InfoModule;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SporkLoader {

	private File folder;
	private Document document;

	private List<Module> modules;

	public SporkLoader(File folder) {
		this.folder = folder;

		File file = new File(folder, "map.xml");
		SAXBuilder sax = new SAXBuilder();
		try {
			this.document = sax.build(file);
		} catch(JDOMException | IOException e) {
			throw new IllegalStateException("Unable to parse Document for '" + folder.getName() + "'", e);
		}

		this.modules = new ArrayList<>();

		BuilderContext context = new BuilderContext(document);
		this.modules.addAll(BuilderFactory.build(InfoModule.class, context));
	}

}
