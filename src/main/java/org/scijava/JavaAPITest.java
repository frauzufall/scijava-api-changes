package org.scijava;

import apidiff.APIDiff;
import apidiff.Change;
import apidiff.Result;
import apidiff.enums.Classifier;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JavaAPITest {

//	static String projectName = "imglib/imglib2";
//	static String projectName = "scifio/scifio";
	static String projectName = "fiji/fiji";
//	static String projectName = "imagej/imagej-updater";
	static File dir = new File("/home/random/Development/imagej/logs/repos/");

	public static void main(String... args) throws Exception {
		dir.mkdirs();
		String url = "https://github.com/" + projectName + ".git";
		APIDiff diff = new APIDiff(projectName, url);
		diff.setPath(dir.getAbsolutePath());

		Map<String, Result> results = diff.detectChangeAllReleases("master", Collections.singletonList(Classifier.API));

		List<String> listChanges = new ArrayList<>();
		for(Map.Entry<String, Result> entry : results.entrySet()) {
			String res = "<div>";
			res += "<h2 class='name'>" + entry.getKey() + "</h2>";
			res += "<div class='changes'>" + getChanges(entry.getValue()) + "</div>";
			res += "</div>";
			listChanges.add(res);
		}
////		listChanges.add("Category;isDeprecated;containsJavadoc");
//		for(Change changeMethod : result.getChangeMethod()){
//			System.out.println(changeMethod);
////			String change = changeMethod.getCategory().getDisplayName() + ";" + changeMethod.isDeprecated()  + ";" + changeMethod.containsJavadoc() ;
//			listChanges.add(changeMethod.getDescription());
////			System.out.println(changeMethod.getDescription());
//		}
		Files.write(Paths.get("/home/random/Development/imagej/logs/" + projectName.replace("/", ":") + ".html"), listChanges);
//		UtilFile.writeFile("/home/random/Development/imagej/logs/csbdeep.csv", listChanges);
	}

	private static String getChanges(Result result) {
		String res = "";
		StringBuilder changedMethods = new StringBuilder();
		StringBuilder changedFields = new StringBuilder();
		StringBuilder changedTypes = new StringBuilder();
		result.getChangeMethod().stream().filter(Change::isBreakingChange).forEach(change -> {
			changedMethods.append("<p>" + change.getDescription().replace("<br>", " ") + "</p>");
		});
		result.getChangeType().stream().filter(Change::isBreakingChange).forEach(change -> {
			changedTypes.append("<p>" + change.getDescription().replace("<br>", " ") + "</p>");
		});
		result.getChangeField().stream().filter(Change::isBreakingChange).forEach(change -> {
			changedFields.append("<p>" + change.getDescription().replace("<br>", " ") + "</p>");
		});
		if(changedMethods.length() > 0) {
			res += "<h3>Methods</h3>" + changedMethods;
		}
		if(changedFields.length() > 0) {
			res += "<h3>Fields</h3>" + changedFields;
		}
		if(changedTypes.length() > 0) {
			res += "<h3>Types</h3>" + changedTypes;
		}
		return res;
	}

}
