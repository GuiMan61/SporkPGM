package io.sporkpgm.module.modules.info;

import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.builder.BuilderResult;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.util.StringUtil;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo(result = BuilderResult.SINGLE)
public class InfoBuilder extends Builder {

	@Override
	public Module single(BuilderContext context) throws ModuleBuildException {
		if(!context.only("document")) {
			return null;
		}

		Document document = context.getDocument();
		Element root = document.getRootElement();

		String name;
		try {
			name = root.getChild("name").getText();
		} catch(NullPointerException ex) {
			throw new ModuleBuildException("Map names can't be null");
		}

		String version;
		try {
			version = root.getChild("version").getText();
		} catch(NullPointerException ex) {
			throw new ModuleBuildException("Map versions can't be null");
		}

		String objective;
		try {
			objective = root.getChild("objective").getText();
		} catch(NullPointerException ex) {
			throw new ModuleBuildException("Map objectives can't be null");
		}

		List<String> rules = new ArrayList<>();
		Element rulesElement = root.getChild("rules");
		if(rulesElement != null) {
			for(Element rule : rulesElement.getChildren("rule")) {
				rules.add(rule.getText());
			}
		}

		Element authorsElement = root.getChild("authors");
		List<Contributor> authors = contributors(authorsElement, "author");

		if(authors.size() == 0) {
			throw new ModuleBuildException("Maps must have at least 1 author");
		}

		Element contributorsElement = root.getChild("contributors");
		List<Contributor> contributors = contributors(contributorsElement, "contributor");

		int maxPlayers = 0;
		Element teamsElement = document.getRootElement().getChild("teams");
		if(teamsElement != null) {
			for(Element team : teamsElement.getChildren("team")) {
				try {
					maxPlayers += StringUtil.convertStringToInteger(team.getAttributeValue("max"));
				} catch(Exception e) { /* nothing */ }
			}
		}

		return new InfoModule(name, version, objective, rules, maxPlayers, authors, contributors);
	}

	private List<Contributor> contributors(Element element, String name) {
		List<Contributor> contributors = new ArrayList<>();

		if(element != null) {
			for(Element contributor : element.getChildren(name)) {
				if(contributor.getText() == null)
					continue;
				contributors.add(new Contributor(contributor.getText(), contributor.getAttributeValue("contribution")));
			}
		}

		return contributors;
	}

}
