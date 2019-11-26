package com.cognifide.aet.models;

import java.util.Set;

public class CookieResult extends Error {
  private Set<String> notFoundCookies;
  private Set<String> additionalCookies;
  private Set<String> foundCookies;

  public Set<String> getNotFoundCookies() {
    return notFoundCookies;
  }

  public Set<String> getAdditionalCookies() {
    return additionalCookies;
  }

  public Set<String> getFoundCookies() {
    return foundCookies;
  }
}
