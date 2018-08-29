package com.cognifide.aet.executor.wrappers;

public class MetadataRunDecorator extends RunDecorator {

  private String correlationId;

  private String company;

  private String project;

  private String name;

  public MetadataRunDecorator(Run decoratedRun, String correlationId, String company,
      String project) {
    super(decoratedRun);
    this.correlationId = correlationId;
    this.company = company;
    this.project = project;
  }

  @Override
  public String getCorrelationId() {
    return correlationId;
  }

  @Override
  public String getCompany() {
    return company;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSuiteIdentifier() {
    return company + "-" + project + "-" + name;
  }

  @Override
  public Object getObjectToRun() {
    return decoratedRun.getObjectToRun();
  }

  @Override
  public String getProject() {
    return project;
  }

}
