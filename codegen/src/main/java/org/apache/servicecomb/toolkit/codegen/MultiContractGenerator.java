package org.apache.servicecomb.toolkit.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.Generator;

public class MultiContractGenerator extends DefaultGenerator {

  private List<ClientOptInput> optsList = new ArrayList<>();

  public Generator addOpts(ClientOptInput opts) {
    optsList.add(opts);
    return this;
  }

  public void generateParentProject(List<File> files, List<Map<String, Object>> modules) {

    this.config = optsList.get(0).getConfig();
    String outputFilename = opts.getConfig().outputFolder() + File.separator + "pom.xml";

    if (!config.shouldOverwrite(outputFilename)) {
      LOGGER.info("Skipped overwriting " + outputFilename);
    }
    Map<String, Object> templateData = this.config.additionalProperties();
    templateData.put("modules", modules);
    try {
      files.add(processTemplateToFile(templateData, "project/pom.mustache", outputFilename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<File> generate() {

    if (optsList == null || optsList.size() == 0) {
      return null;
    }

    List<File> fileList = new ArrayList<>();
    List<Map<String, Object>> modules = new ArrayList<>();

    Set<Object> moduleSet = new HashSet<>();
    for (ClientOptInput opts : optsList) {
      opts.getConfig().additionalProperties().put("isMultipleModule", false);
      moduleSet.add(opts.getConfig().additionalProperties().get(GeneratorExternalConfigConstant.PROVIDER_PROJECT_NAME));
      moduleSet.add(opts.getConfig().additionalProperties().get(GeneratorExternalConfigConstant.CONSUMER_PROJECT_NAME));
      moduleSet.add(opts.getConfig().additionalProperties().get(GeneratorExternalConfigConstant.MODEL_PROJECT_NAME));
      this.opts(opts);
      fileList.addAll(super.generate());
    }

    moduleSet.forEach(module -> {
      modules.add(Collections.singletonMap("module", module));
    });

    generateParentProject(fileList, modules);

    return fileList;
  }
}
